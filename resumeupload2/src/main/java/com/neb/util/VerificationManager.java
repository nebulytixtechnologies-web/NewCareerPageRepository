package com.neb.util;


import com.neb.entity.DeveloperApplication;
import org.springframework.stereotype.Component;
import java.util.*;
/**
 * Manages the email verification process for DeveloperApplication submissions.
 * Stores pending applications and temporary verification codes.
 */
@Component
public class VerificationManager {
	
	// Stores applications waiting to be verified, mapped by email
    private final Map<String, DeveloperApplication> pendingApps = new HashMap<>();
    
    // Stores verification codes and their creation timestamps, mapped by email
    private final Map<String, VerificationEntry> codes = new HashMap<>();
     
    // Code expiry time: 10 minutes in milliseconds
    private static final long EXPIRY = 10 * 60 * 1000; // 10 min
  
    // Internal class to hold verification code and timestamp
    private static class VerificationEntry {
        String code;
        long timestamp;
        VerificationEntry(String c, long t) { code = c; timestamp = t; }
    }

    // Add a new application to the pending list
    public void addPendingApplication(DeveloperApplication app) {
        pendingApps.put(app.getEmail(), app);
    }

    // Generate a 6-digit verification code for the given email
    public String generateCode(String email) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        codes.put(email, new VerificationEntry(code, System.currentTimeMillis()));
        return code;
    }
    
    // Verify the code for the given email and return the application if valid
    public DeveloperApplication verifyCode(String email, String code) {
        VerificationEntry entry = codes.get(email);
        
        // If no entry or code expired, clear data and return null
        if (entry == null || System.currentTimeMillis() - entry.timestamp > EXPIRY) {
            clear(email);
            return null;
        }
        
        // If code doesn't match, return null
        if (!entry.code.equals(code)) return null;
        
        // Return the verified application
        return pendingApps.get(email);
    }
    
    // Remove the application and verification code after use or expiry
    public void clear(String email) {
        pendingApps.remove(email);
        codes.remove(email);
    }
}

