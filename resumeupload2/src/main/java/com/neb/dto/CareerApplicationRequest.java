package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for receiving general intern application details
 * from the client.
 *
 * This class captures basic applicant information for non-developer roles.
 * It is typically used in form submissions and passed from the controller to the service layer.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareerApplicationRequest {
	/**
	 * these are the form submission detail for intern  
	 */
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
