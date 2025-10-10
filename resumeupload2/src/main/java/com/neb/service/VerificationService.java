package com.neb.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VerificationService {

    private final Map<String, VerificationEntry> verificationCodes = new HashMap<>();
    private static final long CODE_EXPIRY_MILLIS = 10 * 60 * 1000;

    private static class VerificationEntry {
        String code;
        long timestamp;

        VerificationEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    public String generateCode(String email) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        verificationCodes.put(email, new VerificationEntry(code, System.currentTimeMillis()));
        return code;
    }

    public boolean verifyCode(String email, String code) {
        if (!verificationCodes.containsKey(email)) return false;
        VerificationEntry entry = verificationCodes.get(email);
        boolean valid = entry.code.equals(code) && (System.currentTimeMillis() - entry.timestamp < CODE_EXPIRY_MILLIS);
        if (!valid) verificationCodes.remove(email);
        return valid;
    }

    public void removeCode(String email) {
        verificationCodes.remove(email);
    }
}
