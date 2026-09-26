package com.engine.anonymizer.detector;

/**
 * SemanticColumnAnalyzers looks at the column header name (like "email_address" or "card_number")
 * and returns the suspected category string (EMAIL, CREDIT_CARD, PHONE, NAME, or TEXT).
 */
public class SemanticColumnAnalyzers {

    public static String analyzeColumn(String headerName) {
        if (headerName == null) {
            return "TEXT";
        }

        // Convert to lowercase to make checking easier
        String lower = headerName.toLowerCase().trim();

        if (lower.contains("mail") || lower.contains("email")) {
            return "EMAIL";
        } else if (lower.contains("card") || lower.contains("cc") || lower.contains("credit")) {
            return "CREDIT_CARD";
        } else if (lower.contains("phone") || lower.contains("tel") || lower.contains("mobile")) {
            return "PHONE";
        } else if (lower.contains("name") || lower.contains("username") || lower.contains("surname")) {
            return "NAME";
        }

        // Default type if nothing matches
        return "TEXT";
    }
}
