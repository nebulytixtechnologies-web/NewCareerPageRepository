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

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
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

    // Static inner class
    private static class VerificationEntry {
        String code;
        long timestamp;

        VerificationEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    // ✅ Single endpoint for both steps
    @PostMapping("/apply")
    public ResponseEntity<?> applyOrVerifyCareer(
            @RequestParam("role") String role,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("qualification") String qualification,
            @RequestParam("passoutYear") int passoutYear,
            @RequestParam(value = "resume", required = false) MultipartFile resumeFile,
            @RequestParam(value = "code", required = false) String code
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ STEP 2: If code exists, verify and finalize
            if (code != null && !code.isEmpty()) {
                if (!verificationCodes.containsKey(email) || !pendingApplications.containsKey(email)) {
                    response.put("status", "error");
                    response.put("message", "No pending application found for this email.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                VerificationEntry entry = verificationCodes.get(email);

                // Expired
                if (System.currentTimeMillis() - entry.timestamp > CODE_EXPIRY_MILLIS) {
                    verificationCodes.remove(email);
                    pendingApplications.remove(email);
                    response.put("status", "error");
                    response.put("message", "Verification code expired. Please submit your application again.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                // Mismatch
                if (!entry.code.equals(code)) {
                    response.put("status", "error");
                    response.put("message", "Invalid verification code.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                CareerApplication app = pendingApplications.get(email);

                if (careerRepository.existsByEmail(app.getEmail())) {
                    response.put("status", "error");
                    response.put("message", "This email has already been used to submit an application.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                // Move resume to final folder
                String finalDirPath = System.getProperty("user.dir") + "/uploads";
                File finalDir = new File(finalDirPath);
                if (!finalDir.exists()) finalDir.mkdirs();

                File tempFile = new File(System.getProperty("user.dir") + "/uploads_temp/" + app.getResumeFileName());
                File finalFile = new File(finalDir, app.getResumeFileName());
                tempFile.renameTo(finalFile);

                app.setAppliedAt(Instant.now());
                CareerApplication savedApp = careerRepository.save(app);

                pendingApplications.remove(email);
                verificationCodes.remove(email);

                // Confirmation + assessment emails
                String subject = "Application Received - " + app.getRole();
                String text = "Hello " + app.getFirstName() + ",\n\n" +
                        "Your application has been successfully submitted.\n\nBest regards,\nHR Team";
                emailService.sendApplicationMail(email, subject, text);

                String assessmentSubject = "Assessment for " + app.getRole() + " Position";
                String assessmentText = "Hello " + app.getFirstName() + ",\n\n" +
                        "Please complete the assessment to proceed:\n" +
                        "https://yourcompany.com/assessment?email=" + app.getEmail();
                emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);

                response.put("status", "success");
                response.put("message", "Your Application Submitted Successfully. Check your email to start the assessment.");
                response.put("id", savedApp.getId());
                return ResponseEntity.ok(response);
            }

            // ✅ STEP 1: Apply & send verification email
            String originalFileName = resumeFile.getOriginalFilename();
            String contentType = resumeFile.getContentType();
            if (originalFileName == null ||
                (!originalFileName.toLowerCase().endsWith(".pdf")) ||
                (contentType != null && !contentType.equalsIgnoreCase("application/pdf"))) {
                response.put("status", "error");
                response.put("message", "Only PDF resumes allowed.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Save temp resume
            String tempDirPath = System.getProperty("user.dir") + "/uploads_temp";
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()) tempDir.mkdirs();

            String safeFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            File tempFile = new File(tempDir, safeFileName);
            resumeFile.transferTo(tempFile);

            CareerApplication tempApp = new CareerApplication();
            tempApp.setRole(role);
            tempApp.setFirstName(firstName);
            tempApp.setLastName(lastName);
            tempApp.setEmail(email);
            tempApp.setPhone(phone);
            tempApp.setQualification(qualification);
            tempApp.setPassoutYear(passoutYear);
            tempApp.setResumeFileName(safeFileName);

            String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
            pendingApplications.put(email, tempApp);
            verificationCodes.put(email, new VerificationEntry(verificationCode, System.currentTimeMillis()));

            String subject = "Verify your email for career application";
            String text = "Hello " + firstName + ",\n\n" +
                    "Please verify your email by entering this code:\n\n" +
                    verificationCode + "\n\nThis code is valid for 10 minutes.\n\nHR Team";
            emailService.sendApplicationMail(email, subject, text);

            response.put("status", "pending");
            response.put("message", "Verification code sent to your email. Please verify to submit application.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
