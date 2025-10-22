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
/**
 * Service class to handle career application submissions and verification.
 * --
 * Responsible for processing incoming applications, saving resumes temporarily,
 * sending verification emails, verifying applicants, and moving applications
 * to persistent storage.
 * --
 */
@Service
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    // Stores applications pending email verification, key by email
    private final Map<String, CareerApplication> pendingApplications = new HashMap<>();
    
    /**
     * Handles submission of career application requests.
     * Validates and temporarily saves the resume, stores pending application,
     * generates and sends a verification code via email.
     * -- Param 
     * resumeFile the resume file uploaded by the applicant (must be PDF) 
     * req the career application request DTO containing applicant details --
     * 
     */
    public ResponseEntity<?> submitApplication(CareerApplicationRequest req, MultipartFile resumeFile) {
        Map<String, Object> response = new HashMap<>();

        try {
        	// Validate that a resume file is provided
            if (resumeFile == null || resumeFile.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Resume file is required.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validate resume file type: only PDFs allowed
            String originalFileName = resumeFile.getOriginalFilename();
            if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
                response.put("status", "error");
                response.put("message", "Only PDF resumes are allowed.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Create temporary directory if not exists
            String tempDirPath = System.getProperty("user.dir") + "/uploads_temp";
            File tempDir = new File(tempDirPath);
            if (!tempDir.exists()) tempDir.mkdirs();
           
            
            // Sanitize and generate a unique filename for saving
            String safeFileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            File tempFile = new File(tempDir, safeFileName);
            
            // Save the uploaded file temporarily
            resumeFile.transferTo(tempFile);

            // Save the uploaded file temporarily
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
      
            // Store in pendingApplications for later verification
            pendingApplications.put(req.getEmail(), app);

            // Generate verification code and send via email
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
    
    /**
     * Verifies the application by checking the email and verification code.
     * If valid, saves the application permanently, moves resume file,
     * sends confirmation and assessment emails based on role and domain.
     */

    public ResponseEntity<?> verifyApplication(String email, String code) {
        Map<String, Object> response = new HashMap<>();
            
        // Check if there is a pending application for the email
        if (!pendingApplications.containsKey(email)) {
            response.put("status", "error");
            response.put("message", "No pending application found for this email.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
       
        // Verify the provided code
        if (!verificationService.verifyCode(email, code)) {
            response.put("status", "error");
            response.put("message", "Invalid or expired verification code.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        CareerApplication app = pendingApplications.get(email);
        
        // Prevent duplicate applications for the same email
        if (careerRepository.existsByEmail(app.getEmail())) {
            response.put("status", "error");
            response.put("message", "This email has already been used to submit an application.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

         // Move resume file from temporary to final uploads directory
        File tempFile = new File(System.getProperty("user.dir") + "/uploads_temp/" + app.getResumeFileName());
        File finalDir = new File(System.getProperty("user.dir") + "/uploads");
        if (!finalDir.exists()) finalDir.mkdirs();
        tempFile.renameTo(new File(finalDir, app.getResumeFileName()));

        // Set application submission time and save to database
        app.setAppliedAt(Instant.now());
        CareerApplication savedApp = careerRepository.save(app);
        
        // Cleanup
        pendingApplications.remove(email);
        verificationService.removeCode(email);

        // Send confirmation + assessment emails
        String subject = "NEBULYTIX | Java Intern Assessment";
        String text = "Hello " + app.getFirstName() + ",\n\n" +
                "Your application has been successfully submitted.\n\nBest regards,\nHR Team";
        emailService.sendApplicationMail(email, subject, text);
        
       // If role is intern, send domain-specific assessment email
         if(app.getRole().equalsIgnoreCase("intern"))
        {
       
             //checking the domain for intern
        	switch(app.getDomain())
        	{
        	    case "Java":
        	    { 
        	    	System.out.println(" ===>" + app.getDomain());
        	    	String assessmentSubject = "NEBULYTIX | Assessment for " + app.getRole() + " Position";
        	    	String assessmentText = "Hello " + app.getFirstName() + ",\n" +
        	    	        "Domain " + app.getDomain() + ",\n" +
        	    	        "Role " + app.getRole() + ",\n\n" +
        	    	        "Thank you for your interest in Software Development Engineering Internship opportunities at Nebulytix technologies pvt ltd! We are excited to move you forward to the next step of the application process, the Online Assessment.\n\n" +
        	    	        "Please complete the assessment:\n" +
        	    	        "https://www.hackerrank.com/test/dce0c7o3ks8/96b0bccd128199aa249a5d878252c7c8?try_test=true&email=hr%40nebulytixtechnologies.com\n\n" +
        	    	        "Once you are ready, set aside a block 1:45 hour of uninterrupted time to complete it in its entirety.\n\n" +
        	    	        "For technical and online assessment support, please review the Preparation Guide for resources or contact your recruiting team for any questions.\n\n" +
        	    	        "Thank you,\nNebulytix technologies pvt ltd";

        	    	emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
        	    	break;

                 }
        	     	
        	    case "Python":
        	    {
        	    	System.out.println(" ===>" + app.getDomain());
        	    	String assessmentSubject = "NEBULYTIX |Assessment for " + app.getRole() + " Position";

        	    	String assessmentText = "Hello " + app.getFirstName() + ",\n" +
        	    	        "Domain " + app.getDomain() + ",\n" +
        	    	        "Role " + app.getRole() + ",\n\n" +
        	    	        "Thank you for your interest in Software Development Engineering Internship opportunities at Nebulytix technologies pvt ltd! We are excited to move you forward to the next step of the application process, the Online Assessment.\n\n" +
        	    	        "Please complete the assessment:\n" +
        	    	        "https://www.hackerrank.com/test/dce0c7o3ks8/96b0bccd128199aa249a5d878252c7c8?try_test=true&email=hr%40nebulytixtechnologies.com\n\n" +
        	    	        "Once you are ready, set aside a block of 1:45 hour of uninterrupted time to complete it in its entirety.\n\n" +
        	    	        "For technical and online assessment support, please review the Preparation Guide for resources or contact your recruiting team for any questions.\n\n" +
        	    	        "Thank you,\nNebulytix technologies pvt ltd Student Programs";

        	    	emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
        	    	break;

        	    }
        	    
        	    case ".NET":
        	    {
        	    	System.out.println(" ===>" + app.getDomain());
        	    	String assessmentSubject = "NEBULYTIX |Assessment for " + app.getRole() + " Position";
        	    	String assessmentText = "Hello " + app.getFirstName() + ",\n" +
        	    	        "Domain " + app.getDomain() + ",\n" +
        	    	        "Role " + app.getRole() + ",\n\n" +
        	    	        "Thank you for your interest in Software Development Engineering Internship opportunities at Nebulytix technologies pvt ltd! We are excited to move you forward to the next step of the application process, the Online Assessment.\n\n" +
        	    	        "Please complete the assessment:\n" +
        	    	        "https://www.hackerrank.com/test/dce0c7o3ks8/96b0bccd128199aa249a5d878252c7c8?try_test=true&email=hr%40nebulytixtechnologies.com\n\n" +
        	    	        "Once you are ready, set aside a block of 1 :45 hour of uninterrupted time to complete it in its entirety.\n\n" +
        	    	        "For technical and online assessment support, please review the Preparation Guide for resources or contact your recruiting team for any questions.\n\n" +
        	    	        "Thank you,\nNebulytix technologies pvt ltd Student Programs";

        	    	emailService.sendApplicationMail(app.getEmail(), assessmentSubject, assessmentText);
        	    	break;
        	    }
        	    
        	    default:
                  // No assessment email for other domains
                   break;
        		
        	}// end of switch case for domain
        	
        	
        }// end of if condition
 
         response.put("status", "success");
         response.put("message", "Your Application Submitted Successfully. Check your email to start the assessment.");
         return ResponseEntity.ok(response);
    }
}
