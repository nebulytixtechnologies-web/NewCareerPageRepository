package com.neb.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cloud_developers1")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudDeveloper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String resumePath; // Store resume file path or URL
    private String linkedinProfile;
    private String githubProfile;
    private Instant createdAt;
    private String gender;


    
   
	
}
