package com.aswinayyappadas.usingDatastrutures.util.application;

import java.util.Random;
/**
 * A utility class for generating random application IDs.
 */
public class ApplicationIdGenerator {
    /**
     * Generates a random application ID.
     *
     * @return A randomly generated application ID between 1 and 999 (inclusive).
     */
    public int generateRandomUserId() {
        Random random = new Random();
        // Generate a random user ID between 1 and 999
        return random.nextInt(999) + 1;
    }
}
