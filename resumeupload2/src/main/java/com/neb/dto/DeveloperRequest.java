package com.neb.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class DeveloperRequest {
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private Integer passoutYear;
    private String internship;
    private String domain;
    private String companyName;
    private Double salary;
    private Double duration;
    private MultipartFile resume;
    private String code;
}
