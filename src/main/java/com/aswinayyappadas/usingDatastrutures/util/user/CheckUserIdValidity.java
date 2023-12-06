package com.aswinayyappadas.usingDatastrutures.util.user;

import com.aswinayyappadas.usingDatastrutures.user.User;

import java.util.HashMap;
import java.util.Set;

public class CheckUserIdValidity {
    public boolean isUserIdValid(int generatedUserId, HashMap<Integer, User> userList) {
        return userList.containsKey(generatedUserId);
    }
}
