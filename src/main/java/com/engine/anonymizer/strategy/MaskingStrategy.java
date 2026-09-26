package com.engine.anonymizer.strategy;

/**
 * MaskingStrategy is a simple functional interface.
 * It takes a single string value (like an email, credit card, etc.) 
 * and returns the anonymized/masked version of it.
 */
@FunctionalInterface
public interface MaskingStrategy {
    String anonymize(String data);
}
