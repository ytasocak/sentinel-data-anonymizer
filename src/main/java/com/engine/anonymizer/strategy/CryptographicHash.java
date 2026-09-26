package com.engine.anonymizer.strategy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CryptographicHash converts text into a fixed-length SHA-256 hex digest.
 * This is useful for anonymizing fields like Names or user IDs where we still need matching values.
 */
public class CryptographicHash implements MaskingStrategy {

    @Override
    public String anonymize(String data) {
        // If data is null or empty, return as is
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            // Use standard Java security to get SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Calculate hash bytes using UTF-8 charset
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            
            // Convert bytes to hex string format
            StringBuilder hexString = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Could not find SHA-256 algorithm: " + e.getMessage());
            return data;
        }
    }
}
