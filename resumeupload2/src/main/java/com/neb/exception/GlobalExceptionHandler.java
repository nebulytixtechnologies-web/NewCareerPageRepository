package com.neb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.neb.dto.CareerApplicationResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<CareerApplicationResponseDto> handleInvalidFile(InvalidFileFormatException ex) {
    	
    
        CareerApplicationResponseDto resp = new CareerApplicationResponseDto(null, "error", ex.getMessage());
        return ResponseEntity.badRequest().body(resp);
    	
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<CareerApplicationResponseDto> handleFileStorage(FileStorageException ex) {
        CareerApplicationResponseDto resp = new CareerApplicationResponseDto(null, "error", "File upload failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CareerApplicationResponseDto> handleGeneral(Exception ex) {
        CareerApplicationResponseDto resp = new CareerApplicationResponseDto(null, "error", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
