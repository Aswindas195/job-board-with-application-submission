package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.User;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
import com.aswinayyappadas.usingDatastrutures.util.user.CheckUserIdValidity;
import com.aswinayyappadas.usingDatastrutures.util.user.UserIdGenerator;
import org.mindrot.jbcrypt.BCrypt;

public class UserManager implements UserData {
    private CheckUserIdValidity checkUserIdValidity;
    private UserIdGenerator userIdGenerator;

    public UserManager(){
        this.checkUserIdValidity = new CheckUserIdValidity();
        this.userIdGenerator = new UserIdGenerator();
    }

    // Authenticates the user with email and password.
    public int authenticateUserAndGetId(String email, String password) {
        if(userEmailData.containsKey(email)) {
           String passwordHash =  userEmailData.get(email).getPassword();
           String obtainedPasswordHash = BCrypt.hashpw(password, userEmailData.get(email).getSalt());
           if(passwordHash.equals(obtainedPasswordHash)) {
               if(userEmailData.get(email) instanceof Employer) {
                   return ((Employer) userEmailData.get(email)).getEmployerId();
               }
               else if(userEmailData.get(email) instanceof Jobseeker) {
                   return ((Jobseeker) userEmailData.get(email)).getJobseekerId();
               }
           }
        }
        return -1;
    }
    // Checks if email already exist in the map.
    public boolean isEmailExist(String email) {
        return userEmailData.containsKey(email);
    }
    // Returns a valid user id for new registrations.
    public int getValidUserId() {
        int userId = -1;
        while(true) {
            userId = userIdGenerator.generateRandomUserId();
            if(!checkUserIdValidity.isUserIdValid(userId, userData)) break;
        }
        return userId;
    }
    // Add user to the userData map
    public void addUser(int userId, User user) {
        userData.put(userId, user);
    }
    // Add user to the userEmailData map
    public void addUserEmail(String email, User user) {
        userEmailData.put(email, user);
    }
    public boolean logoutUser(int userId) {
       if(userData.containsKey(userId) && userData.get(userId).getJwt_secret_key() != null) {
           userData.get(userId).setJwt_secret_key(null);
           return true;
       }
       return false;
    }
    public Jobseeker createJobseeker(int userId, String username, String email, String passwordHash, String salt) {
        Jobseeker jobseeker = new Jobseeker();
        jobseeker.setJobseekerId(userId);
        jobseeker.setName(username);
        jobseeker.setEmail(email);
        jobseeker.setSalt(salt);
        jobseeker.setPassword(passwordHash);
        return jobseeker;
    }
    public Employer createEmployer(int userId, String username, String email, String passwordHash, String salt) {
        Employer employer = new Employer();
        employer.setEmployerId(userId);
        employer.setName(username);
        employer.setEmail(email);
        employer.setSalt(salt);
        employer.setPassword(passwordHash);
        return employer;
    }
}
