package com.neb.controller;

import com.neb.entity.CareerApplication;
import com.neb.repo.CareerRepository;
import com.neb.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/careers")
public class CareerController {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private EmailService emailService;

    private final Map<String, CareerApplication> pendingApplications = new HashMap<>();
    private final Map<String, VerificationEntry> verificationCodes = new HashMap<>();
    private static final long CODE_EXPIRY_MILLIS = 10 * 60 * 1000; // 10 minutes

    private static class VerificationEntry {
        String code;
        long timestamp;

        VerificationEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    // Step 1: Submit application for email verification
    @PostMapping("/apply")
    public ResponseEntity<?> applyForCareer(
            @RequestParam("role") String role,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("qualification") String qualification,
            @RequestParam("passoutYear") int passoutYear,
            @RequestParam("resume") MultipartFile resumeFile
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validate PDF
            String originalFileName = resumeFile.getOriginalFilename();
            String contentType = resumeFile.getContentType();
            if (originalFileName == null ||
                (!originalFileName.toLowerCase().endsWith(".pdf")) ||
                (contentType != null && !contentType.equalsIgnoreCase("application/pdf"))) {
                response.put("status", "error");
                response.put("message", "Only PDF resumes allowed.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Save resume temporarily
            String tempDirPath = System.getProperty("user.dir") + "/uploads_temp";
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()) tempDir.mkdirs();

            String safeFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            File tempFile = new File(tempDir, safeFileName);
            resumeFile.transferTo(tempFile);

            // Create temporary application
            CareerApplication tempApp = new CareerApplication();
            tempApp.setRole(role);
            tempApp.setFirstName(firstName);
            tempApp.setLastName(lastName);
            tempApp.setEmail(email);
            tempApp.setPhone(phone);
            tempApp.setQualification(qualification);
            tempApp.setPassoutYear(passoutYear);
            tempApp.setResumeFileName(safeFileName);

            // Generate 6-digit verification code
            String code = String.valueOf((int) (Math.random() * 900000) + 100000);
            pendingApplications.put(email, tempApp);
            verificationCodes.put(email, new VerificationEntry(code, System.currentTimeMillis()));

            // Send verification email
            String subject = "Verify your email for career application";
            String text = "Hello " + firstName + ",\n\n" +
                    "Please verify your email by entering the following code:\n" +
                    code + "\n\nThis code is valid for 10 minutes.\n\nHR Team";
            emailService.sendApplicationMail(email, subject, text);

            response.put("status", "pending");
            response.put("message", "Verification code sent to your email. Please verify to submit application.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Step 2: Verify code and finalize application
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code
    ) {
        Map<String, Object> response = new HashMap<>();

        if (!verificationCodes.containsKey(email) || !pendingApplications.containsKey(email)) {
            response.put("status", "error");
            response.put("message", "No pending application found for this email.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        VerificationEntry entry = verificationCodes.get(email);

        // Check expiry
        if (System.currentTimeMillis() - entry.timestamp > CODE_EXPIRY_MILLIS) {
            verificationCodes.remove(email);
            pendingApplications.remove(email);
            response.put("status", "error");
            response.put("message", "Verification code expired. Please submit your application again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Check code match
        if (!entry.code.equals(code)) {
            response.put("status", "error");
            response.put("message", "Invalid verification code.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        CareerApplication app = pendingApplications.get(email);

        // Check if email already exists in DB
        if (careerRepository.existsByEmail(app.getEmail())) {
            response.put("status", "error");
            response.put("message", "This email has already been used to submit an application.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Move resume from temp to final folder
            String finalDirPath = System.getProperty("user.dir") + "/uploads";
            File finalDir = new File(finalDirPath);
            if (!finalDir.exists()) finalDir.mkdirs();

            File tempFile = new File(System.getProperty("user.dir") + "/uploads_temp/" + app.getResumeFileName());
            File finalFile = new File(finalDir, app.getResumeFileName());
            tempFile.renameTo(finalFile);

            // Save to DB
            app.setAppliedAt(Instant.now());
            CareerApplication savedApp = careerRepository.save(app);

            // Cleanup pending maps
            pendingApplications.remove(email);
            verificationCodes.remove(email);

            // Send confirmation email
            String subject = "Application Received - " + app.getRole();
            String text = "Hello " + app.getFirstName() + ",\n\n" +
                    "Your application has been successfully submitted.\n\nBest regards,\nHR Team";
            emailService.sendApplicationMail(email, subject, text);

            // Send assessment email
            String assessmentSubject = "Assessment for " + app.getRole() + " Position";
            String assessmentText = "Hello " + app.getFirstName() + ",\n\n" +
                    "Congratulations! Please complete the assessment to proceed:\n" +
                    "Assessment Link: https://yourcompany.com/assessment?email=" + app.getEmail() + "\n\nBest regards,\nHR Team";
            emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);

            // ✅ Modified message here 
            
            response.put("status", "success");
            response.put("message", "Your Application Submitted Successfully. Check your email to start the assessment.");
            response.put("id", savedApp.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error submitting application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}//end code

