package com.aswinayyappadas.usingDatastrutures.util.user;

import com.aswinayyappadas.usingDatastrutures.user.User;

import java.util.HashMap;
import java.util.Set;
/**
 * A utility class for checking the validity of user IDs.
 */
public class CheckUserIdValidity {

    /**
     * Checks if a generated user ID is valid within the given user list.
     *
     * @param generatedUserId The generated user ID to check for validity.
     * @param userList        The HashMap containing user IDs and corresponding User objects.
     * @return {@code true} if the user ID is valid; {@code false} otherwise.
     */
    public boolean isUserIdValid(int generatedUserId, HashMap<Integer, User> userList) {
        return userList.containsKey(generatedUserId);
    }
}
