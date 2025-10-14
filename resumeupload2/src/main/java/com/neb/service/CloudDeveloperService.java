package com.neb.service;

import com.neb.dto.CloudDeveloperRequest;
import com.neb.entity.CloudDeveloper;
import com.neb.repo.CloudDeveloperRepository;
import com.neb.util.CloudDeveloperVerificationManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudDeveloperService {

    @Autowired
    private CloudDeveloperRepository repo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CloudDeveloperVerificationManager cloudDeveloperVerificationManager;
 
    //Response Entity req
    public ResponseEntity<?> handleCloudDeveloperApplication(CloudDeveloperRequest req) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ VERIFY CODE PHASE
            if (req.getCode() != null && req.getEmail() != null) {
                return verifyCloudDeveloper(req.getEmail(), req.getCode(), response);
            }

            // ✅ APPLY PHASE
            if (req.getEmail() == null || req.getResume() == null) {
                response.put("status", "error");
                response.put("message", "Email and resume are required.");
                return ResponseEntity.badRequest().body(response);
            }

            // Save resume temporarily
            String safeFileName = saveTempResume(req.getResume());
            CloudDeveloper app = mapToEntity(req, safeFileName);
            cloudDeveloperVerificationManager.addPendingApplication(app);

            // Send verification code
            String code = cloudDeveloperVerificationManager.generateCode(req.getEmail());
            emailService.sendApplicationMail(req.getEmail(),
                    "Verify Your Cloud Developer Application",
                    "Hello " + req.getFirstName() + ",\n\nPlease verify using this code:\n\n" +
                            code + "\n\nValid for 10 minutes.\n\nBest Regards,\nHR Team");

            response.put("status", "pending");
            response.put("message", "Verification code sent to your email.");
            
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // responseEntity for verify mail
    private ResponseEntity<?> verifyCloudDeveloper(String email, String code, Map<String, Object> response) {
    	
      //  CloudDeveloper app = cloudDeveloperVerificationManager.verifyCloudDeveloperCode(email, code);
    	
    	
    	CloudDeveloper app = cloudDeveloperVerificationManager.verifyCloudDeveloperCode(email, code);

        if (app == null) {
            response.put("status", "error");
            response.put("message", "Invalid or expired verification code.");
            return ResponseEntity.badRequest().body(response);
        }

        if (repo.existsByEmail(email)) {
            response.put("status", "error");
            response.put("message", "You have already submitted your application.");
            cloudDeveloperVerificationManager.clear(email);
            return ResponseEntity.badRequest().body(response);
        }

        // Move file from temp → uploads
        moveResumeToFinal(app.getResumePath());

        app.setCreatedAt(Instant.now());
        repo.save(app);
        cloudDeveloperVerificationManager.clear(email);

        // Send final confirmation
        emailService.sendApplicationMail(email,
                "Application Received - " + app.getRole(),
                "Hello " + app.getFirstName() + ",\n\nYour cloud developer application has been successfully submitted.\n\nHR Team");

        emailService.sendApplicationMail(email,
                "Assessment for " + app.getRole() + " Cloud Developer",
                "Hello " + app.getFirstName() + ",\n\nPlease complete your assessment:\nhttps://yourcompany.com/assessment?email=" + app.getEmail());

        response.put("status", "success");
        response.put("message", "Application submitted successfully!");
        return ResponseEntity.ok(response);
    }

    private String saveTempResume(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("user.dir") + "/uploads_temp";
        File dir = new File(tempDir);
        if (!dir.exists()) dir.mkdirs();

        String safeName = UUID.randomUUID() + "_" +
                file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        file.transferTo(new File(dir, safeName));
        return safeName;
    }

    private void moveResumeToFinal(String resumeName) {
        File tempFile = new File(System.getProperty("user.dir") + "/uploads_temp/" + resumeName);
        File finalDir = new File(System.getProperty("user.dir") + "/uploads");
        if (!finalDir.exists()) finalDir.mkdirs();
        tempFile.renameTo(new File(finalDir, resumeName));
    }

    private CloudDeveloper mapToEntity(CloudDeveloperRequest req, String fileName) {
        CloudDeveloper app = new CloudDeveloper();
        app.setRole(req.getRole());
        app.setFirstName(req.getFirstName());
        app.setLastName(req.getLastName());
        app.setEmail(req.getEmail());
        app.setPhone(req.getPhone());
        app.setQualification(req.getQualification());
        app.setExperienceYears(req.getExperienceYears() != null ? req.getExperienceYears() : 0);
        app.setDomain(req.getDomain());
        app.setCompanyName(req.getCompanyName());
        app.setCurrentSalary(req.getCurrentSalary() != null ? req.getCurrentSalary() : 0);
        app.setExpectedSalary(req.getExpectedSalary() != null ? req.getExpectedSalary() : 0);
        app.setDurationAtCompany(req.getDurationAtCompany() != null ? req.getDurationAtCompany() : 0);
        app.setResumePath(fileName);
        app.setLinkedinProfile(req.getLinkedinProfile());
        app.setGithubProfile(req.getGithubProfile());
        app.setGender(req.getGender());
        return app;
    }
}
