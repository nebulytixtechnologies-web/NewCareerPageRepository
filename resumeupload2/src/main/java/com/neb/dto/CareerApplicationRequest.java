package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareerApplicationRequest {
	
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private int passoutYear;
    private String domain;
    private String gender;
	    
	
}
