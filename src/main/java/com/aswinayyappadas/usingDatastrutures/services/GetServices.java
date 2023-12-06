package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.UserData;

public class GetServices implements UserData {
    public String getUserTypeByUserId(int userId) {
        if(userData.get(userId) instanceof Employer) {
            return "Employer";
        }
        return "Jobseeker";
    }
    public String getEmailByUserId(int userId) {
        return userData.get(userId).getEmail();
    }
}
