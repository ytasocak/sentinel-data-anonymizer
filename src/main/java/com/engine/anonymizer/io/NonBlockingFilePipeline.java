package com.engine.anonymizer.io;

import com.engine.anonymizer.config.ProfileConfigurationRegistry;
import com.engine.anonymizer.detector.SemanticColumnAnalyzers;
import com.engine.anonymizer.detector.TypeDetectors;
import com.engine.anonymizer.strategy.MaskingStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * NonBlockingFilePipeline coordinates the masking pipeline.
 * It reads the first CSV line for column headers, configures the masking strategy 
 * for each column, and then processes all rows via ChunkedStreamBroker.
 * It also uses dynamic heuristic fallback detectors on cells.
 */
public class NonBlockingFilePipeline {
    private final ProfileConfigurationRegistry registry;

    public NonBlockingFilePipeline(ProfileConfigurationRegistry registry) {
        this.registry = registry;
    }

    /**
     * Executes the streaming transformation from input file path to output file path.
     */
    public void execute(String inputPath, String outputPath) {
        System.out.println("Pipeline started. Source: " + inputPath + " -> Destination: " + outputPath);
        
        ChunkedStreamBroker broker = new ChunkedStreamBroker(inputPath, outputPath);
        
        // Lists to store headers and the mapped strategy for each column index
        final List<String> headers = new ArrayList<>();
        final List<MaskingStrategy> columnStrategies = new ArrayList<>();

        try {
            broker.process(line -> {
                // Return empty lines unchanged
                if (line.trim().isEmpty()) {
                    return line;
                }

                // Split cells by comma (simple CSV split)
                String[] cells = line.split(",", -1);

                // Step 1: Detect Headers on first line
                if (headers.isEmpty()) {
                    for (String cell : cells) {
                        String header = cell.trim();
                        headers.add(header);
                        
                        // Map the column header to a semantic category (EMAIL, PHONE, name, etc.)
                        String category = SemanticColumnAnalyzers.analyzeColumn(header);
                        MaskingStrategy strategy = registry.getStrategy(category);
                        columnStrategies.add(strategy);
                        
                        System.out.println("  Column Mapping: [" + header + "] is analyzed as " + category);
                    }
                    return line; // Output headers unchanged
                }

                // Step 2: Mask cell data in data rows
                StringBuilder processedLine = new StringBuilder();
                for (int i = 0; i < cells.length; i++) {
                    String value = cells[i];
                    MaskingStrategy strategy = null;

                    // Fetch preset column strategy
                    if (i < columnStrategies.size()) {
                        strategy = columnStrategies.get(i);
                    }

                    // Heuristic fallback if column is default "TEXT" but contents look like sensitive data
                    if (strategy == null || strategy == registry.getStrategy("TEXT")) {
                        if (TypeDetectors.isEmail(value)) {
                            strategy = registry.getStrategy("EMAIL");
                        } else if (TypeDetectors.isCreditCard(value)) {
                            strategy = registry.getStrategy("CREDIT_CARD");
                        } else if (TypeDetectors.isPhoneNumber(value)) {
                            strategy = registry.getStrategy("PHONE");
                        }
                    }

                    // Apply strategy if found
                    if (strategy != null) {
                        value = strategy.anonymize(value);
                    }

                    processedLine.append(value);
                    if (i < cells.length - 1) {
                        processedLine.append(",");
                    }
                }
                
                return processedLine.toString();
            });
            
            System.out.println("Pipeline completed successfully!");
            
        } catch (IOException e) {
            System.err.println("Pipeline failed due to IO Error: " + e.getMessage());
        }
    }
}
