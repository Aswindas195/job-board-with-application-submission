package com.aswinayyappadas.usingDatastrutures.util.user;

import java.util.Random;

public class UserIdGenerator {
    public int generateRandomUserId() {
        Random random = new Random();
        // Generate a random user ID between 1 and 999
        return random.nextInt(999) + 1;
    }
}
