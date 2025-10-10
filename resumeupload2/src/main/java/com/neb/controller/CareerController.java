package com.neb.controller;

import com.neb.dto.CareerApplicationRequest;
import com.neb.service.CareerService;
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
}
