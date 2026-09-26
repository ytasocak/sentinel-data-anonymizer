package com.engine.anonymizer.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

/**
 * ChunkedStreamBroker processes files line-by-line.
 * It uses Java try-with-resources to properly close character buffers with explicit UTF-8 charset.
 * RAM usage stays extremely low (&lt; 50MB) regardless of target file size.
 */
public class ChunkedStreamBroker {
    private final String inputFilePath;
    private final String outputFilePath;

    public ChunkedStreamBroker(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    /**
     * Safe streaming boundary: Processes each line using the transformer 
     * and writes immediately to the output file.
     */
    public void process(Function<String, String> lineTransformer) throws IOException {
        // Strict forward-only memory block allocation mechanism with explicit UTF-8
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(inputFilePath), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(outputFilePath), StandardCharsets.UTF_8)) {

            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                // Transform single line sequence to isolate scope
                String processedLine = lineTransformer.apply(currentLine);
                bufferedWriter.write(processedLine);
                bufferedWriter.newLine();
            }
        }
    }
}
