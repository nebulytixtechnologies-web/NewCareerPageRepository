package com.neb.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;
/**
 * Data Transfer Object (DTO) for handling developer job application submissions.
 * 
 * --This class captures applicant details such as personal info, education,
 * internship, experience, and resume file for the developer position.--
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperRequest {
	/**
	 * these are the form submission detail for developer 
	 */
	private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private Integer passoutYear;
    private String internship;
    private String domain;
    private String devdomain;
    private String companyName;
    private Double salary;
    private Double duration;
    private MultipartFile resume;
    private String code;
    private String gender;
 }
