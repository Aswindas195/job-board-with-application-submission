package com.aswinayyappadas.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateToken(String email) {
        // Combine the email and a random component
        String combinedString = email + generateRandomComponent();

        // Encode the combined string into a secure token
        byte[] combinedBytes = combinedString.getBytes();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(combinedBytes);
    }

    private String generateRandomComponent() {
        byte[] randomBytes = new byte[8]; // Adjust the size as needed
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

//    public static void main(String[] args) {
//        String email = "example@example.com";
//        String authToken = generateToken(email);
//        System.out.println(authToken);
//    }
}
