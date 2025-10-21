package com.neb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.neb.dto.CareerApplicationResponseDto;
/**
 * Global exception handler for the application.
 * 
 * -- Handles specific custom exceptions as well as general exceptions,
 * returning a structured error response with appropriate HTTP status codes.--
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	/**
     * Handles InvalidFileFormatException when an uploaded file has an unsupported format.
     */
    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<CareerApplicationResponseDto> handleInvalidFile(InvalidFileFormatException ex) {
    	
    
        CareerApplicationResponseDto resp = new CareerApplicationResponseDto(null, "error", ex.getMessage());
        return ResponseEntity.badRequest().body(resp);
    	
    }
    
    /**
     * Handles FileStorageException when file storage fails (e.g., writing to disk).
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<CareerApplicationResponseDto> handleFileStorage(FileStorageException ex) {
        CareerApplicationResponseDto resp = new CareerApplicationResponseDto(null, "error", "File upload failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
    
    /**
     * Handles all other exceptions that are not explicitly caught.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CareerApplicationResponseDto> handleGeneral(Exception ex) {
        CareerApplicationResponseDto resp = new CareerApplicationResponseDto(null, "error", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
