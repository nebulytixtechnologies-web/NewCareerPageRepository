package com.neb.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to manage email verification codes for user authentication and validation.
 */
@Service
public class VerificationService {
    
	// Stores verification codes mapped by email
    private final Map<String, VerificationEntry> verificationCodes = new HashMap<>();
    
    // Code validity duration: 10 minutes in milliseconds
    private static final long CODE_EXPIRY_MILLIS = 10 * 60 * 1000;

    /**
     * Inner class to hold a verification code and its creation timestamp.
     */
    private static class VerificationEntry {
        String code;
        long timestamp;

        VerificationEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    /**
     * Generates a 6-digit verification code and stores it for the provided email.
     *
     * @param email The user's email address
     * @return The generated verification code
     */
    public String generateCode(String email) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        verificationCodes.put(email, new VerificationEntry(code, System.currentTimeMillis()));
        return code;
    }
    
    /**
     * Verifies whether the provided code matches the stored one and is not expired.
     *
     * @param email The user's email address
     * @param code  The code entered by the user
     * @return true if the code is correct and not expired; false otherwise
     */
    public boolean verifyCode(String email, String code) {
        if (!verificationCodes.containsKey(email)) return false;
        VerificationEntry entry = verificationCodes.get(email);
        boolean valid = entry.code.equals(code) && (System.currentTimeMillis() - entry.timestamp < CODE_EXPIRY_MILLIS);
        if (!valid) verificationCodes.remove(email);
        return valid;
    }

    /**
     * Removes the stored code for the given email.
     *
     * @param email The user's email address
     */
    public void removeCode(String email) {
        verificationCodes.remove(email);
    }
}
