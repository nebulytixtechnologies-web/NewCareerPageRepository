package com.neb.util;

import org.springframework.stereotype.Component;

import com.neb.entity.CloudDeveloper;

import java.util.HashMap;
import java.util.Map;

@Component
public class CloudDeveloperVerificationManager {

    private final Map<String, CloudDeveloper> pendingApps = new HashMap<>();
    private final Map<String, VerificationEntry> codes = new HashMap<>();

    private static final long EXPIRY = 10 * 60 * 1000; // 10 minutes

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

    // Verify cloud developer code
    public CloudDeveloper verifyCloudDeveloperCode(String email, String code) {
        VerificationEntry entry = codes.get(email);

        if (entry == null || System.currentTimeMillis() - entry.timestamp > EXPIRY) {
            clear(email);
            return null;
        }

        if (!entry.code.equals(code)) {
            return null;
        }

        return pendingApps.get(email);
    }

    // Clear pending application and code
    public void clear(String email) {
        pendingApps.remove(email);
        codes.remove(email);
    }

	public Map<String, CloudDeveloper> getPendingApps() {
		return pendingApps;
	}

	public Map<String, VerificationEntry> getCodes() {
		return codes;
	}

	public static long getExpiry() {
		return EXPIRY;
	}

	

	
}
