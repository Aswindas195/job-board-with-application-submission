package com.aswinayyappadas.usingDatastrutures.util.job;

import java.util.Random;
/**
 * A utility class for generating random job IDs.
 */
public class JobIdGenerator {
    /**
     * Generates a random job ID.
     *
     * @return A random job ID between 1 and 999.
     */
    public int generateRandomUserId() {
        Random random = new Random();
        // Generate a random user ID between 1 and 999
        return random.nextInt(999) + 1;
    }
}
