package com.neb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.CareerApplicationRequest;
import com.neb.dto.DeveloperRequest;
import com.neb.service.CareerService;
import com.neb.service.DeveloperService;
import com.neb.dto.CloudDeveloperRequest;
import com.neb.service.CloudDeveloperService;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/careers")
public class CareerController {

    @Autowired
    private CareerService careerService;
    @Autowired
    private DeveloperService developerService;
    @Autowired
    private CloudDeveloperService cloudDeveloperService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyOrVerifyCareer(
            @ModelAttribute CareerApplicationRequest request,
            @RequestParam(value = "resume", required = false) MultipartFile resumeFile,
            @RequestParam(value = "code", required = false) String code) {

        if (code != null && !code.isEmpty()) {
            return careerService.verifyApplication(request.getEmail(), code);
        } else {
            return careerService.submitApplication(request, resumeFile);
        }
    }
    
    @PostMapping("/applydeveloper")
    public ResponseEntity<?> applyOrVerifyDeveloper(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "qualification", required = false) String qualification,
            @RequestParam(value = "passoutYear", required = false) Integer passoutYear,
            @RequestParam(value = "internship", required = false) String internship,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value ="devdomain",required = false)String devdomain,
            @RequestParam(value = "salary", required = false) Double salary,
            @RequestParam(value = "duration", required = false) Double duration,
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "gender",required = false) String gender
    ) {
        DeveloperRequest request = new DeveloperRequest(role, firstName, lastName, email, phone,
                qualification, passoutYear, internship, domain,devdomain, companyName, salary, duration, resume, code,gender);
        return developerService.handleDeveloperApplication(request);
    }
    

 // Unified endpoint for experienced Cloud Developers
    @PostMapping("/applyclouddeveloper")
    public ResponseEntity<?> applyOrVerifyCloudDeveloper(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "qualification", required = false) String qualification,
            @RequestParam(value = "passoutYear", required = false) Integer passoutYear,
            @RequestParam(value = "experienceYears", required = false) Integer experienceYears,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "currentSalary", required = false) Double currentSalary,
            @RequestParam(value = "expectedSalary", required = false) Double expectedSalary,
            @RequestParam(value = "durationAtCompany", required = false) Double durationAtCompany,
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "linkedinProfile", required = false) String linkedinProfile,
            @RequestParam(value = "githubProfile", required = false) String githubProfile,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "gender", required = false) String gender
            
    ) {
        CloudDeveloperRequest request = new CloudDeveloperRequest(
                role, firstName, lastName, email, phone,
                qualification, experienceYears, domain,
                companyName, currentSalary, expectedSalary, durationAtCompany, resume,
                linkedinProfile, githubProfile, code ,gender
        );
        return cloudDeveloperService.handleCloudDeveloperApplication(request);
    }
    
   
}