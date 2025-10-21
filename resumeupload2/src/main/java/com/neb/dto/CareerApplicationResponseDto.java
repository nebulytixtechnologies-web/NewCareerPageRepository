package com.neb.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A simple DTO (Data Transfer Object) for sending responses 
 * related to career applications.
 */

@Setter
@Getter
@NoArgsConstructor
@Data
public class CareerApplicationResponseDto {
	 /**
     * Constructor to initialize the response with specific values.
     */
    public CareerApplicationResponseDto(Object object, String string, String message2) 
    {
		
	}
	private Long id;
    private String status;
    private String message;
}
