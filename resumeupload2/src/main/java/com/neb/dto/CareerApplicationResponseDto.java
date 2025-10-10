package com.neb.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@Data
public class CareerApplicationResponseDto {
    public CareerApplicationResponseDto(Object object, String string, String message2) {
		
	}
	private Long id;
    private String status;
    private String message;
}
