package com.neb.service;

import com.neb.dto.CareerApplicationRequest;
import com.neb.entity.CareerApplication;
import com.neb.repo.CareerRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    private final Map<String, CareerApplication> pendingApplications = new HashMap<>();

    public ResponseEntity<?> submitApplication(CareerApplicationRequest req, MultipartFile resumeFile) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (resumeFile == null || resumeFile.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Resume file is required.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validate PDF
            String originalFileName = resumeFile.getOriginalFilename();
            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
                response.put("status", "error");
                response.put("message", "Only PDF resumes are allowed.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Save temp resume
            String tempDirPath = System.getProperty("user.dir") + "/uploads_temp";
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()) tempDir.mkdirs();

            String safeFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            File tempFile = new File(tempDir, safeFileName);
            resumeFile.transferTo(tempFile);

            CareerApplication app = new CareerApplication();
            app.setRole(req.getRole());
            app.setFirstName(req.getFirstName());
            app.setLastName(req.getLastName());
            app.setEmail(req.getEmail());
            app.setPhone(req.getPhone());
            app.setQualification(req.getQualification());
            app.setPassoutYear(req.getPassoutYear());
            app.setResumeFileName(safeFileName);
            app.setDomain(req.getDomain());
            app.setGender(req.getGender());

            pendingApplications.put(req.getEmail(), app);

            // Generate code
            String verificationCode = verificationService.generateCode(req.getEmail());

            // Send verification mail
            String subject = "Verify your email for career application";
            String text = "Hello " + req.getFirstName() + ",\n\n" +
                    "Please verify your email by entering this code:\n\n" +
                    verificationCode + "\n\nThis code is valid for 10 minutes.\n\nHR Team";
            emailService.sendApplicationMail(req.getEmail(), subject, text);

            response.put("status", "pending");
            response.put("message", "Verification code sent to your email.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<?> verifyApplication(String email, String code) {
        Map<String, Object> response = new HashMap<>();

        if (!pendingApplications.containsKey(email)) {
            response.put("status", "error");
            response.put("message", "No pending application found for this email.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!verificationService.verifyCode(email, code)) {
            response.put("status", "error");
            response.put("message", "Invalid or expired verification code.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        CareerApplication app = pendingApplications.get(email);
        if (careerRepository.existsByEmail(app.getEmail())) {
            response.put("status", "error");
            response.put("message", "This email has already been used to submit an application.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Move file from temp to final folder
        File tempFile = new File(System.getProperty("user.dir") + "/uploads_temp/" + app.getResumeFileName());
        File finalDir = new File(System.getProperty("user.dir") + "/uploads");
        if (!finalDir.exists()) finalDir.mkdirs();
        tempFile.renameTo(new File(finalDir, app.getResumeFileName()));

        app.setAppliedAt(Instant.now());
        CareerApplication savedApp = careerRepository.save(app);
        pendingApplications.remove(email);
        verificationService.removeCode(email);

        // Send confirmation + assessment emails
        String subject = "Application Received - " + app.getRole();
        String text = "Hello " + app.getFirstName() + ",\n\n" +
                "Your application has been successfully submitted.\n\nBest regards,\nHR Team";
        emailService.sendApplicationMail(email, subject, text);
          // Checking the role
         if(app.getRole().equalsIgnoreCase("intern"))
        {
       
             //checking the domain for intern
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
                             "https://www.hackerrank.com/test/dce0c7o3ks8/96b0bccd128199aa249a5d878252c7c8?try_test=true&email=hr%40nebulytixtechnologies.com"+
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
                             "https://www.hackerrank.com/test/dce0c7o3ks8/96b0bccd128199aa249a5d878252c7c8?try_test=true&email=hr%40nebulytixtechnologies.com"+
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
                             "https://www.hackerrank.com/test/dce0c7o3ks8/96b0bccd128199aa249a5d878252c7c8?try_test=true&email=hr%40nebulytixtechnologies.com"+
                             "\n\nBest regards,\nHR Team";
                     emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
        	    }
        		
        	}// end of switch case for domain
        	
        	
        }// end of if condition
 
         response.put("status", "success");
         response.put("message", "Your Application Submitted Successfully. Check your email to start the assessment.");
         return ResponseEntity.ok(response);
    }
}
