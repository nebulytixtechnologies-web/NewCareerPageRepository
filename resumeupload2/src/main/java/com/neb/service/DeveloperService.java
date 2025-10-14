package com.neb.service;



import com.neb.dto.DeveloperRequest;
import com.neb.entity.DeveloperApplication;
import com.neb.repo.DeveloperRepository;
import com.neb.util.VerificationManager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class DeveloperService {

    @Autowired
    private DeveloperRepository repo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationManager verificationManager;

    public ResponseEntity<?> handleDeveloperApplication(DeveloperRequest req) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ VERIFY CODE PHASE
            if (req.getCode() != null && req.getEmail() != null) {
                return verifyDeveloper(req.getEmail(), req.getCode(), response);
            }

            // ✅ APPLY PHASE
            if (req.getEmail() == null || req.getResume() == null) {
                response.put("status", "error");
                response.put("message", "Email and resume are required.");
                return ResponseEntity.badRequest().body(response);
            }

            // Save resume temporarily
            String safeFileName = saveTempResume(req.getResume());
            DeveloperApplication app = mapToEntity(req, safeFileName);
            verificationManager.addPendingApplication(app);

            // Send verification code
            String code = verificationManager.generateCode(req.getEmail());
            emailService.sendApplicationMail(req.getEmail(),
                    "Verify Your Developer Application",
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

    private ResponseEntity<?> verifyDeveloper(String email, String code, Map<String, Object> response) {
        DeveloperApplication app = verificationManager.verifyCode(email, code);

        if (app == null) {
            response.put("status", "error");
            response.put("message", "Invalid or expired verification code.");
            return ResponseEntity.badRequest().body(response);
        }

        if (repo.existsByEmail(email)) {
            response.put("status", "error");
            response.put("message", "You have already submitted your application.");
            verificationManager.clear(email);
            return ResponseEntity.badRequest().body(response);
        }

        // Move file from temp → uploads
        moveResumeToFinal(app.getResumePath());

        app.setCreatedAt(Instant.now());
        repo.save(app);
        verificationManager.clear(email);

        // Send final confirmation
        emailService.sendApplicationMail(email,
                "Application Received - " + app.getRole(),
                "Hello " + app.getFirstName() + ",\n\nYour developer application has been successfully submitted.\n\nHR Team");
        // checking of role
        if(app.getRole().equalsIgnoreCase("developer"))
        {
           //checking the domain for developer
    	switch(app.getDomain())
    	{
    	    case "Java":
    	    { 
    	    	System.out.println(" ===>"+app.getDomain());
            	 String assessmentSubject = "Assessment for " + app.getRole() + " Position";
                 String assessmentText = "Hello " + app.getFirstName() + ",\n" +
                         "Domain "+ app.getDomain() + ",\n"+
                         "Role "+ app.getRole() + ",\n\n"+
                         "Please complete the assessment:\n" +
                         "https://yourcompany.com/assessment?email=" + app.getEmail() +app.getRole()+
                         "\n\nBest regards,\nHR Team";
                 emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
             }
    	     	
    	    case "Python":
    	    {
    	    	 System.out.println(" ===>"+app.getDomain());
            	 String assessmentSubject = "Assessment for " + app.getRole() + " Position";
                 String assessmentText = "Hello " + app.getFirstName() + ",\n" +
                         "Domain "+ app.getDomain() + ",\n"+
                         "Role "+ app.getRole() + ",\n\n"+
                         "Please complete the assessment:\n" +
                         "https://yourcompany.com/assessment?email=" + app.getEmail() +app.getRole()+
                         "\n\nBest regards,\nHR Team";
                 emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
    	    }
    	    
    	    case ".NET":
    	    {
    	    	 System.out.println(" ===>"+app.getDomain());
            	 String assessmentSubject = "Assessment for " + app.getRole() + " Position";
                 String assessmentText = "Hello " + app.getFirstName() + ",\n" +
                         "Domain "+ app.getDomain() + ",\n"+
                         "Role "+ app.getRole() + ",\n\n"+
                         "Please complete the assessment:\n" +
                         "https://yourcompany.com/assessment?email=" + app.getEmail() +app.getRole()+
                         "\n\nBest regards,\nHR Team";
                 emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
    	    }
    		
    	 }// end of switch case for domain
        }// end of if condition
        response.put("status", "success");
        response.put("message", "Application submitted successfully! check your mail for the assessment");
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

    private DeveloperApplication mapToEntity(DeveloperRequest req, String fileName) {
        DeveloperApplication app = new DeveloperApplication();
        app.setRole(req.getRole());
        app.setFirstName(req.getFirstName());
        app.setLastName(req.getLastName());
        app.setEmail(req.getEmail());
        app.setPhone(req.getPhone());
        app.setQualification(req.getQualification());
        app.setPassoutYear(req.getPassoutYear() != null ? req.getPassoutYear() : 0);

        boolean isIntern = "on".equalsIgnoreCase(req.getInternship()) || "true".equalsIgnoreCase(req.getInternship());
        app.setInternship(isIntern);
        app.setDomain(isIntern ? req.getDomain() : "N/A");
        app.setCompanyName(isIntern ? req.getCompanyName() : "N/A");
        app.setSalary(isIntern ? (req.getSalary() != null ? req.getSalary() : 0) : 0);
        app.setDuration(isIntern ? (req.getDuration() != null ? req.getDuration() : 0) : 0);
        app.setResumePath(fileName);
        app.setGender(req.getGender());
        
        return app;
    }
}
