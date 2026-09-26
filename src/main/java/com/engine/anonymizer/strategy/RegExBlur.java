package com.engine.anonymizer.strategy;

/**
 * RegExBlur uses regular expressions to find patterns in a string and mask them.
 * The default setup masks characters in emails before the '@' symbol.
 */
public class RegExBlur implements MaskingStrategy {
    private final String regex;
    private final String replacement;

    // Default constructor uses the user's specific email masking regex
    public RegExBlur() {
        this.regex = "(?<=.{2}).(?=.*@)";
        this.replacement = "*";
    }

    // Constructor to support custom regex rules
    public RegExBlur(String regex, String replacement) {
        this.regex = regex;
        this.replacement = replacement;
    }

    @Override
    public String anonymize(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        return data.replaceAll(regex, replacement);
    }
}
