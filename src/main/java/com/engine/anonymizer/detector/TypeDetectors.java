package com.engine.anonymizer.detector;

import java.util.regex.Pattern;

/**
 * TypeDetectors provides simple heuristic functions using regular expressions.
 * They help detect what kind of sensitive information a data value might be.
 */
public class TypeDetectors {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern CREDIT_CARD_STRIP_PATTERN = Pattern.compile("[\\s-]");
    private static final Pattern CREDIT_CARD_DIGITS_PATTERN = Pattern.compile("^\\d{13,19}$");
    private static final Pattern PHONE_STRIP_PATTERN = Pattern.compile("[\\s-()\\+]");
    private static final Pattern PHONE_DIGITS_PATTERN = Pattern.compile("^\\d{7,15}$");

    // Simple heuristic pattern for emails
    public static boolean isEmail(String value) {
        if (value == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    // Simple check for Credit Cards: strips spaces/dashes and checks length (13-19 digits)
    public static boolean isCreditCard(String value) {
        if (value == null) {
            return false;
        }
        String clean = CREDIT_CARD_STRIP_PATTERN.matcher(value).replaceAll("");
        return CREDIT_CARD_DIGITS_PATTERN.matcher(clean).matches();
    }

    // Simple check for Phone Numbers: strips spaces, dashes, brackets, and checks length (7-15 digits)
    public static boolean isPhoneNumber(String value) {
        if (value == null) {
            return false;
        }
        String clean = PHONE_STRIP_PATTERN.matcher(value).replaceAll("");
        return PHONE_DIGITS_PATTERN.matcher(clean).matches();
    }
}
