package com.neb.util;


import com.neb.entity.DeveloperApplication;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class VerificationManager {

    private final Map<String, DeveloperApplication> pendingApps = new HashMap<>();
    private final Map<String, VerificationEntry> codes = new HashMap<>();

    private static final long EXPIRY = 10 * 60 * 1000; // 10 min

    private static class VerificationEntry {
        String code;
        long timestamp;
        VerificationEntry(String c, long t) { code = c; timestamp = t; }
    }

    public void addPendingApplication(DeveloperApplication app) {
        pendingApps.put(app.getEmail(), app);
    }

    public String generateCode(String email) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        codes.put(email, new VerificationEntry(code, System.currentTimeMillis()));
        return code;
    }

    public DeveloperApplication verifyCode(String email, String code) {
        VerificationEntry entry = codes.get(email);
        if (entry == null || System.currentTimeMillis() - entry.timestamp > EXPIRY) {
            clear(email);
            return null;
        }
        if (!entry.code.equals(code)) return null;
        return pendingApps.get(email);
    }

    public void clear(String email) {
        pendingApps.remove(email);
        codes.remove(email);
    }
}

