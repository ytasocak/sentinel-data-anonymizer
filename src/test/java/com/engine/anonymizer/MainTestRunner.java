package com.engine.anonymizer;

import com.engine.anonymizer.detector.TypeDetectors;
import com.engine.anonymizer.strategy.*;

/**
 * MainTestRunner runs our custom unit tests using standard assertions.
 * This lets us run tests without needing external JUnit libraries.
 */
public class MainTestRunner {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   Running Custom Anonymizer Unit Tests   ");
        System.out.println("==========================================");

        int passed = 0;
        int failed = 0;

        // Test 1: CryptographicHash
        try {
            testCryptographicHash();
            System.out.println("[PASS] CryptographicHash Test");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] CryptographicHash Test: " + e.getMessage());
            failed++;
        }

        // Test 2: CharScrambler
        try {
            testCharScrambler();
            System.out.println("[PASS] CharScrambler Test");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] CharScrambler Test: " + e.getMessage());
            failed++;
        }

        // Test 3: RegExBlur
        try {
            testRegExBlur();
            System.out.println("[PASS] RegExBlur Test");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] RegExBlur Test: " + e.getMessage());
            failed++;
        }

        // Test 4: TypeDetectors
        try {
            testTypeDetectors();
            System.out.println("[PASS] TypeDetectors Test");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] TypeDetectors Test: " + e.getMessage());
            failed++;
        }

        System.out.println("------------------------------------------");
        System.out.println("Result: Passed " + passed + ", Failed " + failed);
        System.out.println("==========================================");

        // Exit with error code if any test failed
        if (failed > 0) {
            System.exit(1);
        }
    }

    // Helper Assert Methods
    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected [" + expected + "] but got [" + actual + "]");
        }
    }

    private static void assertNotEquals(Object val1, Object val2) {
        if (val1.equals(val2)) {
            throw new AssertionError("Expected different values, but both were [" + val1 + "]");
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be TRUE");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected condition to be FALSE");
        }
    }

    private static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected object NOT to be null");
        }
    }

    // Test Implementations
    private static void testCryptographicHash() {
        CryptographicHash hash = new CryptographicHash();
        String name = "John Doe";
        String hashed1 = hash.anonymize(name);
        String hashed2 = hash.anonymize(name);

        assertNotNull(hashed1);
        assertEquals(hashed1, hashed2); // Deterministic
        assertEquals(64, hashed1.length()); // SHA-256 has 64 chars
        assertNotEquals(name, hashed1); // Muted
    }

    private static void testCharScrambler() {
        CharScrambler scrambler = new CharScrambler();
        String cc = "1234-5678-ABCD";
        String scrambled = scrambler.anonymize(cc);
        assertEquals("9999-9999-XXXX", scrambled);
    }

    private static void testRegExBlur() {
        RegExBlur emailBlur = new RegExBlur();
        String email = "john.doe@example.com";
        String blurred = emailBlur.anonymize(email);
        assertEquals("jo******@example.com", blurred);

        // Custom regex for phone format test
        RegExBlur phoneBlur = new RegExBlur("\\d(?=(?:\\D*\\d){4})", "*");
        assertEquals("+*-***-0199", phoneBlur.anonymize("+1-555-0199"));
        assertEquals("******4444", phoneBlur.anonymize("5553334444"));
    }

    private static void testTypeDetectors() {
        assertTrue(TypeDetectors.isEmail("john.doe@example.com"));
        assertFalse(TypeDetectors.isEmail("john.doe.com"));

        assertTrue(TypeDetectors.isCreditCard("1234-5678-9012-3456"));
        assertTrue(TypeDetectors.isCreditCard("1234 5678 9012 3456"));
        assertFalse(TypeDetectors.isCreditCard("1234-abc"));

        assertTrue(TypeDetectors.isPhoneNumber("+1-555-0199"));
        assertTrue(TypeDetectors.isPhoneNumber("5553334444"));
        assertFalse(TypeDetectors.isPhoneNumber("text"));
    }
}
