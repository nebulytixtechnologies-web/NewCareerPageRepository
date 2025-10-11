package com.neb.controller;

import com.neb.dto.CareerApplicationRequest;
import com.neb.service.CareerService;
import com.neb.dto.DeveloperRequest;
import com.neb.service.DeveloperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/careers")
public class CareerController {

    @Autowired
    private CareerService careerService;

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
    
    @Autowired
    private DeveloperService developerService;

    // Unified endpoint (apply or verify)
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
            @RequestParam(value = "salary", required = false) Double salary,
            @RequestParam(value = "duration", required = false) Double duration,
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "code", required = false) String code
    ) {
        DeveloperRequest request = new DeveloperRequest(role, firstName, lastName, email, phone,
                qualification, passoutYear, internship, domain, companyName, salary, duration, resume, code);
        return developerService.handleDeveloperApplication(request);
    }
}
