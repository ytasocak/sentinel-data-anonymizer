package com.engine.anonymizer;

import com.engine.anonymizer.detector.TypeDetectors;
import com.engine.anonymizer.strategy.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AnonymizerTest provides unit tests to verify the correctness of
 * strategies and type detection heuristics.
 */
public class AnonymizerTest {

    @Test
    public void testCryptographicHash() {
        CryptographicHash hash = new CryptographicHash();
        String input = "John Doe";
        String hash1 = hash.anonymize(input);
        String hash2 = hash.anonymize(input);

        assertNotNull(hash1);
        assertEquals(hash1, hash2); // Must be deterministic (same input produces same hash)
        assertEquals(64, hash1.length()); // SHA-256 hash length is 64 hex characters
        assertNotEquals(input, hash1); // Result must be anonymized
    }

    @Test
    public void testCharScrambler() {
        CharScrambler scrambler = new CharScrambler();
        
        // Alphanumerics should be scrambled, other formatting characters kept
        String creditCard = "1234-5678-ABCD";
        String scrambled = scrambler.anonymize(creditCard);
        
        assertEquals("9999-9999-XXXX", scrambled);
    }

    @Test
    public void testRegExBlur() {
        // Default RegExBlur targets emails (obscuring all but the first two chars of prefix)
        RegExBlur emailBlur = new RegExBlur();
        String email = "john.doe@example.com";
        String blurred = emailBlur.anonymize(email);
        
        assertEquals("jo******@example.com", blurred);
    }

    @Test
    public void testTypeDetectors() {
        // Check email detection
        assertTrue(TypeDetectors.isEmail("john.doe@example.com"));
        assertFalse(TypeDetectors.isEmail("john.doe.com"));

        // Check credit card detection
        assertTrue(TypeDetectors.isCreditCard("1234-5678-9012-3456"));
        assertTrue(TypeDetectors.isCreditCard("1234 5678 9012 3456"));
        assertFalse(TypeDetectors.isCreditCard("1234-abc-5678"));

        // Check phone number detection
        assertTrue(TypeDetectors.isPhoneNumber("+1-555-0199"));
        assertTrue(TypeDetectors.isPhoneNumber("5553334444"));
        assertFalse(TypeDetectors.isPhoneNumber("not-a-phone-number"));
    }
}
