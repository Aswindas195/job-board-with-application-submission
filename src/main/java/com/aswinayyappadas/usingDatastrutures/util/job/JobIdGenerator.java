package com.aswinayyappadas.usingDatastrutures.util.job;

import java.util.Random;

public class JobIdGenerator {
    public int generateRandomUserId() {
        Random random = new Random();
        // Generate a random user ID between 1 and 999
        return random.nextInt(999) + 1;
    }
}
