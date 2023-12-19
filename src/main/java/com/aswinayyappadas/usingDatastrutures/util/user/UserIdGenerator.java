package com.aswinayyappadas.usingDatastrutures.util.user;

import java.util.Random;
/**
 * A utility class for generating random user IDs.
 */
public class UserIdGenerator {
    /**
     * Generates a random user ID between 1 and 999 (inclusive).
     *
     * @return A randomly generated user ID.
     */
    public int generateRandomUserId() {
        Random random = new Random();
        // Generate a random user ID between 1 and 999
        return random.nextInt(999) + 1;
    }
}
