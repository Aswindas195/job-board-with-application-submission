package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.user.UserData;

public class ValidityCheckingService implements UserData {
    public boolean isValidUserId(int userId) {
        return userData.containsKey(userId);
    }
    public boolean isValidEmployerId(int employerId) {
        return userData.containsKey(employerId) && userData.get(employerId) instanceof Employer;
    }
}
