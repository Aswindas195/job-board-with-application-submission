package com.aswinayyappadas.usingDatastrutures.user;

import java.util.HashMap;
/**
 * A data interface representing user-related data, including user information stored by user ID and email.
 */
public interface UserData {
    /**
     * A map containing user information mapped by user ID.
     * The key is the user ID, and the value is the corresponding user object.
     */
    HashMap<Integer, User> userData = new HashMap<>();
    /**
     * A map containing user information mapped by email.
     * The key is the email address, and the value is the corresponding user object.
     */
    HashMap<String, User> userEmailData = new HashMap<>();
}
