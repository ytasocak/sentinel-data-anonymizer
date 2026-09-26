package com.engine.anonymizer;

import com.engine.anonymizer.config.ProfileConfigurationRegistry;
import com.engine.anonymizer.config.YamlSchemaInterpreter;
import com.engine.anonymizer.io.NonBlockingFilePipeline;
import com.engine.anonymizer.strategy.CharScrambler;
import com.engine.anonymizer.strategy.CryptographicHash;
import com.engine.anonymizer.strategy.MaskingStrategy;
import com.engine.anonymizer.strategy.RegExBlur;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Main class is the driver application for our data masking utility.
 * It can accept CLI parameters (inputPath, outputPath, [schemaConfigPath]) or
 * run in a self-demonstration mode by creating its own demo input data.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Sentinel Anonymous Smart Data Masking Utility ");
        System.out.println("=================================================");

        if (args.length == 1 && ("--help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printUsage();
            return;
        }

        String inputPath;
        String outputPath;
        String schemaPath = null;

        // If command-line arguments are provided
        if (args.length >= 2) {
            inputPath = args[0];
            outputPath = args[1];
            if (args.length >= 3) {
                schemaPath = args[2];
            }
        } else {
            // Auto self-demonstration mode
            System.out.println("No command line parameters found. Running self-demonstration...");
            inputPath = "sample_input.csv";
            outputPath = "sample_output_anonymized.csv";
            schemaPath = "schema_config.yaml";
            
            // Create dummy files to demonstrate the functionality
            generateDemoFiles(inputPath, schemaPath);
        }

        // 1. Initialize Profile Strategy Registry
        ProfileConfigurationRegistry registry = new ProfileConfigurationRegistry();

        // 2. Read and map custom configurations if config file path exists
        if (schemaPath != null) {
            System.out.println("Interpreting configuration schema: " + schemaPath);
            Map<String, String> parsedSchema = YamlSchemaInterpreter.interpret(schemaPath);
            
            for (Map.Entry<String, String> entry : parsedSchema.entrySet()) {
                String category = entry.getKey().toUpperCase();
                String strategyName = entry.getValue().toUpperCase();
                
                MaskingStrategy strategy;
                switch (strategyName) {
                    case "HASH":
                        strategy = new CryptographicHash();
                        break;
                    case "SCRAMBLE":
                        strategy = new CharScrambler();
                        break;
                    case "REGEX":
                        if ("PHONE".equals(category)) {
                            strategy = new RegExBlur("\\d(?=(?:\\D*\\d){4})", "*");
                        } else {
                            strategy = new RegExBlur();
                        }
                        break;
                    default:
                        strategy = data -> data; // fallback to plaintext
                        break;
                }
                
                registry.register(category, strategy);
                System.out.println("  Registered Strategy: " + category + " -> " + strategyName);
            }
        }

        // 3. Run Pipeline
        NonBlockingFilePipeline pipeline = new NonBlockingFilePipeline(registry);
        pipeline.execute(inputPath, outputPath);

        System.out.println("=================================================");
        System.out.println("Masking Operation Finished!");
        System.out.println("=================================================");
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -jar sentinel-data-anonymizer-1.0-SNAPSHOT.jar <input_csv> <output_csv> [schema_config.yaml]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help       Show this help message");
        System.out.println("  (no arguments)   Runs in self-demonstration mode with sample files");
    }

    /**
     * Helper to write initial test files so the project runs out-of-the-box.
     */
    private static void generateDemoFiles(String csvPath, String yamlPath) {
        // Generate YAML schema configurations
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(yamlPath), StandardCharsets.UTF_8)) {
            writer.write("# Sentinel Masking Config YAML\n");
            writer.write("EMAIL: REGEX\n");
            writer.write("CREDIT_CARD: SCRAMBLE\n");
            writer.write("NAME: HASH\n");
            writer.write("PHONE: REGEX\n");
            System.out.println("Generated demo schema configuration: " + yamlPath);
        } catch (IOException e) {
            System.err.println("Could not create demo yaml config file: " + e.getMessage());
        }

        // Generate sample input CSV
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvPath), StandardCharsets.UTF_8)) {
            writer.write("id,customer_name,email_address,credit_card_no,phone_no,comments\n");
            writer.write("1,John Doe,john.doe@example.com,1234-5678-9012-3456,+1-555-0199,Active VIP customer\n");
            writer.write("2,Alice Smith,alice.s@domain.org,9876 5432 1098 7654,+44 20 7946 0192,Interested in promotions\n");
            writer.write("3,Bob Johnson,bob_j@coolservice.net,1111222233334444,5553334444,Standard subscription\n");
            System.out.println("Generated demo source CSV dataset: " + csvPath);
        } catch (IOException e) {
            System.err.println("Could not create demo CSV file: " + e.getMessage());
        }
    }
}
