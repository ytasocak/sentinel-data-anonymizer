package com.engine.anonymizer.config;

import com.engine.anonymizer.strategy.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ProfileConfigurationRegistry maps data categories (like EMAIL or NAME)
 * to their respective MaskingStrategy objects.
 */
public class ProfileConfigurationRegistry {
    private final Map<String, MaskingStrategy> registry = new HashMap<>();

    // Constructor sets up standard default strategies for each category
    public ProfileConfigurationRegistry() {
        registry.put("EMAIL", new RegExBlur());
        registry.put("CREDIT_CARD", new CharScrambler());
        
        // Custom RegExBlur for phone: masks all digits except the last 4 digits (even with formatting)
        registry.put("PHONE", new RegExBlur("\\d(?=(?:\\D*\\d){4})", "*"));
        
        registry.put("NAME", new CryptographicHash());
        
        // Default strategy keeps the data unchanged (identity lambda function)
        registry.put("TEXT", data -> data);
    }

    // Allows registering new custom strategies at runtime
    public void register(String category, MaskingStrategy strategy) {
        if (category != null && strategy != null) {
            registry.put(category.toUpperCase(), strategy);
        }
    }

    // Fetches the masking strategy for a category, falls back to TEXT if not found
    public MaskingStrategy getStrategy(String category) {
        if (category == null) {
            return registry.get("TEXT");
        }
        return registry.getOrDefault(category.toUpperCase(), registry.get("TEXT"));
    }
}
