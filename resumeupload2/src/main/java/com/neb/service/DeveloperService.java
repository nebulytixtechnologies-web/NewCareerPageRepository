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

/**
 * Service to handle developer job applications.
 * Supports application submission, resume upload, email verification,
 * and dispatch of role/domain-specific assessments.
 */
@Service
public class DeveloperService {

    @Autowired
    private DeveloperRepository repo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationManager verificationManager;
    
    /**
     * Handles both phases of the developer application process:
     * - Application submission
     * - Code verification
     *
     * @param req DeveloperRequest DTO from client
     * @return ResponseEntity with result status and message
     */
    public ResponseEntity<?> handleDeveloperApplication(DeveloperRequest req) {
        Map<String, Object> response = new HashMap<>();

        try {
            // STEP 1: VERIFY CODE PHASE
            if (req.getCode() != null && req.getEmail() != null) {
                return verifyDeveloper(req.getEmail(), req.getCode(), response);
            }

            // STEP 2: APPLY PHASE
            if (req.getEmail() == null || req.getResume() == null) {
                response.put("status", "error");
                response.put("message", "Email and resume are required.");
                return ResponseEntity.badRequest().body(response);
            }

            // Save resume to temp folder
            String safeFileName = saveTempResume(req.getResume());
            
            // Map to entity and save as pending
            DeveloperApplication app = mapToEntity(req, safeFileName);
            verificationManager.addPendingApplication(app);

            // Generate and send email verification code
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
    
    /**
     * Verifies email/code and sends assessment link if successful.
     *
     * @param email    Applicant's email
     * @param code     Verification code
     * @param response Output response map
     * @return ResponseEntity with result status and message
     */
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

        // Move resume from temp to uploads folder
        moveResumeToFinal(app.getResumePath());
        
        // Finalize and persist application
        app.setCreatedAt(Instant.now());
        repo.save(app);
        verificationManager.clear(email);

        // Send confirmation email
        emailService.sendApplicationMail(email,
                "Application Received - " + app.getRole(),
                "Hello " + app.getFirstName() + ",\n\nYour developer application has been successfully submitted.\n\nHR Team");
        // checking of role
        if(app.getRole().equalsIgnoreCase("developer"))
        {
           //checking the domain for developer
    	switch(app.getDevdomain())
    	{
    	    case "Java":
    	    { 
    	    	
    	    	System.out.println(" ===>"+app.getDevdomain());
            	 String assessmentSubject = "NEBULYTIX | Java Developer Assessment";
            	 String assessmentText = "Hi " + app.getFirstName() + ",\n\n" +
            			    "Thanks for showing interest in the " + app.getRole() + " role at Nebulytix!\n" +
            			    "We’re excited to move you to the next step — the Online Assessment.\n\n" +
            			    "To move forward in the process, please complete the assessment within 1 days using the link below:\n\n" +
            			    "https://www.hackerrank.com/test/9eht0gma42h/666cfbe6ce1da32d0c3cf7a5a4d22af1?try_test=true&email=careers@nebulytix.com\n\n" +
            			    "Before you start, make sure you have 2 hours of quiet, uninterrupted time.\n" +
            			    "Once started, you won’t be able to pause or restart the test — so get ready before you begin.\n\n" +
            			    "If you face any technical issues, feel free to reach out to us at careers@nebulytix.com.\n\n" +
            			    "Good luck — we can’t wait to see what you build!\n\n" +
            			    "Cheers,\n" +
            			    "Team Nebulytix\n" +
            			    "www.nebulytixtechnologies.com";

                 emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
                 break;
             }
    	     	
    	    case "Python":
    	    {
    	    	System.out.println(" ===>"+app.getDomain());
    	    	System.out.println(" ===>"+app.getDevdomain());
            	 String assessmentSubject = "NEBULYTIX | Python Developer Assessment";
            	 String assessmentText = "Hi " + app.getFirstName() + ",\n\n" +
            			    "Thanks for showing interest in the " + app.getRole() + " role at Nebulytix!\n" +
            			    "We’re excited to move you to the next step — the Online Assessment.\n\n" +
            			    "To move forward in the process, please complete the assessment within 1 day using the link below:\n\n" +
            			    "https://www.hackerrank.com/test/9eht0gma42h/666cfbe6ce1da32d0c3cf7a5a4d22af1?try_test=true&email=careers@nebulytix.com\n\n" +
            			    "Before you start, make sure you have 2 hours of quiet, uninterrupted time.\n" +
            			    "Once started, you won’t be able to pause or restart the test — so get ready before you begin.\n\n" +
            			    "If you face any technical issues, feel free to reach out to us at careers@nebulytix.com.\n\n" +
            			    "Good luck — we can’t wait to see what you build!\n\n" +
            			    "Cheers,\n" +
            			    "Team Nebulytix\n" +
            			    "www.nebulytixtechnologies.com";

                 emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
                 break;
    	    }
    	    
    	    case ".NET":
    	    {
    	    	System.out.println(" ===>"+app.getDomain());
    	    	System.out.println(" ===>"+app.getDevdomain());
            	 String assessmentSubject = "NEBULYTIX | .NET Developer Assessment";
            	 String assessmentText = "Hi " + app.getFirstName() + ",\n\n" +
            			    "Thanks for showing interest in the " + app.getRole() + " role at Nebulytix!\n" +
            			    "We’re excited to move you to the next step — the Online Assessment.\n\n" +
            			    "To move forward in the process, please complete the assessment within 1 day  using the link below:\n\n" +
            			    "https://www.hackerrank.com/test/9eht0gma42h/666cfbe6ce1da32d0c3cf7a5a4d22af1?try_test=true&email=careers@nebulytix.com\n\n" +
            			    "Before you start, make sure you have 2 hours of quiet, uninterrupted time.\n" +
            			    "Once started, you won’t be able to pause or restart the test — so get ready before you begin.\n\n" +
            			    "If you face any technical issues, feel free to reach out to us at careers@nebulytix.com.\n\n" +
            			    "Good luck — we can’t wait to see what you build!\n\n" +
            			    "Cheers,\n" +
            			    "Team Nebulytix\n" +
            			    "www.nebulytixtechnologies.com";

                 emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
                 break;
    	    }
    		
    	 }// end of switch case for domain
        }// end of if condition
        response.put("status", "success");
        response.put("message", "Application submitted successfully! check your mail for the assessment");
        return ResponseEntity.ok(response);
        
    }
    
    /**
     * Saves the resume file to a temporary folder with a safe name.
     *
     * @param file Multipart resume file
     * @return Safe file name for the saved resume
     * @throws IOException if saving fails
     */
    private String saveTempResume(MultipartFile file) throws IOException {
        String tempDir = System.getProperty("user.dir") + "/uploads_temp";
        File dir = new File(tempDir);
        if (!dir.exists()) dir.mkdirs();

        String safeName = UUID.randomUUID() + "_" +
                file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        file.transferTo(new File(dir, safeName));
        return safeName;
    }

    /**
     * Moves resume from temporary to final uploads folder.
     *
     * @param resumeName The name of the resume file
     */
    private void moveResumeToFinal(String resumeName) {
        File tempFile = new File(System.getProperty("user.dir") + "/uploads_temp/" + resumeName);
        File finalDir = new File(System.getProperty("user.dir") + "/uploads");
        if (!finalDir.exists()) finalDir.mkdirs();
        tempFile.renameTo(new File(finalDir, resumeName));
    }
    
    /**
     * Maps a DeveloperRequest DTO to DeveloperApplication entity.
     *
     * @param req      Developer request
     * @param fileName Resume file name
     * @return DeveloperApplication entity
     */
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
        app.setDevdomain(req.getDevdomain());
        
        return app;
    }
}
