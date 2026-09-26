package com.engine.anonymizer.strategy;

/**
 * CharScrambler masks alphanumeric characters.
 * It changes letters to 'X' and digits to '9', leaving symbols (like dashes, spaces) alone.
 * This helps keep structural layouts like Credit Cards and Phone numbers.
 */
public class CharScrambler implements MaskingStrategy {

    @Override
    public String anonymize(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (Character.isLetter(ch)) {
                result.append('X');
            } else if (Character.isDigit(ch)) {
                result.append('9');
            } else {
                // Keep spaces, hyphens, parentheses, etc.
                result.append(ch);
            }
        }
        return result.toString();
    }
}
