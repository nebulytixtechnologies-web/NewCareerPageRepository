package com.neb.util;

import org.springframework.stereotype.Component;

import com.neb.entity.CloudDeveloper;

import java.util.HashMap;
import java.util.Map;
/**
 * Manages verification process for Cloud Developer applications.
 * Stores pending applications and verification codes, and handles verification logic.
 */
@Component
public class CloudDeveloperVerificationManager {
	
	/** Stores pending applications waiting for verification */
    private final Map<String, CloudDeveloper> pendingApps = new HashMap<>();
    
    /** Stores verification codes and timestamps associated with each email */
    private final Map<String, VerificationEntry> codes = new HashMap<>();
    
    /** Code expiry time: 10 minutes (in milliseconds) */
    private static final long EXPIRY = 10 * 60 * 1000; // 10 minutes
    
    /** Inner class to store verification code and timestamp */
    private static class VerificationEntry {
        String code;
        long timestamp;

        VerificationEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    // Add a pending cloud developer application
    public void addPendingApplication(CloudDeveloper app) {
        pendingApps.put(app.getEmail(), app);
    }

    // Generate verification code for email
    public String generateCode(String email) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit code
        codes.put(email, new VerificationEntry(code, System.currentTimeMillis()));
        return code;
    }

     // Verify the given code for the given email
    public CloudDeveloper verifyCloudDeveloperCode(String email, String code) {
        VerificationEntry entry = codes.get(email);
          /** If no entry found or code expired, clear data and return null */
        if (entry == null || System.currentTimeMillis() - entry.timestamp > EXPIRY) {
            clear(email);
            return null;
        }
          /** If code doesn't match, return null */
        if (!entry.code.equals(code)) {
            return null;
        }
         /** Return the pending application if code is valid */
        return pendingApps.get(email);
    }

    // Remove pending application and verification code
    public void clear(String email) {
        pendingApps.remove(email);
        codes.remove(email);
    }
    // Getter for pending applications (used for inspection/testing)
	public Map<String, CloudDeveloper> getPendingApps() {
		return pendingApps;
	}
	// Getter for verification codes (used for inspection/testing)
	public Map<String, VerificationEntry> getCodes() {
		return codes;
	}
    // Getter for expiry duration
	public static long getExpiry() {
		return EXPIRY;
	}

	

	
}
