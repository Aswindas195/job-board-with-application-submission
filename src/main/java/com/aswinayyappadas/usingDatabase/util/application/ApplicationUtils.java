package com.aswinayyappadas.usingDatabase.util.application;

import java.util.UUID;

public class ApplicationUtils {

    // Method to generate a random cloud file location path
    public static String generateRandomFilePath() {
        String randomUUID = UUID.randomUUID().toString();
        return "cloud-storage/" + randomUUID;
    }

    // Method to generate cover letter text
    public static String generateCoverLetter() {
        // Implement your logic to generate cover letter text here
        // For simplicity, returning a placeholder text
        return "Dear Hiring Manager, ...";
    }
}
