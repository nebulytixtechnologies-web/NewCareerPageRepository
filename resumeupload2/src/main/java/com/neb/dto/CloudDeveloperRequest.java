package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;


/**
 * Data Transfer Object(DTO) for handling cloud developer application requests.
 * 
 * --This class captures candidate information submitted through a form on API,
 * including personal details, qualifications, experience, and uploaded resume.--
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudDeveloperRequest {
	/**
	 * these are the form submission detail for cloud engineer  
	 */
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private Integer experienceYears;
    private String domain;
    private String companyName;
    private Double currentSalary;
    private Double expectedSalary;
    private Double durationAtCompany;
    private MultipartFile resume;
    private String linkedinProfile;
    private String githubProfile;
    private String code;
    private String gender;


   
   
    
   }
