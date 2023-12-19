package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.User;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
import com.aswinayyappadas.usingDatastrutures.util.user.CheckUserIdValidity;
import com.aswinayyappadas.usingDatastrutures.util.user.UserIdGenerator;
import org.mindrot.jbcrypt.BCrypt;
/**
 * Manages user-related operations such as authentication, registration, and user creation.
 */
public class UserManager implements UserData {
    private CheckUserIdValidity checkUserIdValidity;
    private UserIdGenerator userIdGenerator;
    /**
     * Constructor to initialize CheckUserIdValidity and UserIdGenerator.
     */
    public UserManager(){
        this.checkUserIdValidity = new CheckUserIdValidity();
        this.userIdGenerator = new UserIdGenerator();
    }

    /**
     * Authenticates the user with email and password.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     * @return The user ID if authentication is successful, -1 otherwise.
     */
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

    /**
     * Checks if an email already exists in the map.
     *
     * @param email The email to check.
     * @return {@code true} if the email exists, {@code false} otherwise.
     */
    public boolean isEmailExist(String email) {
        return userEmailData.containsKey(email);
    }
    /**
     * Returns a valid user ID for new registrations.
     *
     * @return A valid user ID.
     */
    public int getValidUserId() {
        int userId = -1;
        while(true) {
            userId = userIdGenerator.generateRandomUserId();
            if(!checkUserIdValidity.isUserIdValid(userId, userData)) break;
        }
        return userId;
    }

    /**
     * Adds a user to the userData map.
     *
     * @param userId The ID of the user.
     * @param user   The user object.
     */
    public void addUser(int userId, User user) {
        userData.put(userId, user);
    }
    /**
     * Adds a user to the userEmailData map.
     *
     * @param email The email of the user.
     * @param user  The user object.
     */
    public void addUserEmail(String email, User user) {
        userEmailData.put(email, user);
    }
    /**
     * Creates a Jobseeker object with the provided details.
     *
     * @param userId      The ID of the jobseeker.
     * @param username    The username of the jobseeker.
     * @param email       The email of the jobseeker.
     * @param passwordHash The hashed password of the jobseeker.
     * @param salt        The salt used in password hashing.
     * @return A Jobseeker object.
     */
    public Jobseeker createJobseeker(int userId, String username, String email, String passwordHash, String salt) {
        Jobseeker jobseeker = new Jobseeker();
        jobseeker.setJobseekerId(userId);
        jobseeker.setName(username);
        jobseeker.setEmail(email);
        jobseeker.setSalt(salt);
        jobseeker.setPassword(passwordHash);
        return jobseeker;
    }
    /**
     * Creates an Employer object with the provided details.
     *
     * @param userId      The ID of the employer.
     * @param username    The username of the employer.
     * @param email       The email of the employer.
     * @param passwordHash The hashed password of the employer.
     * @param salt        The salt used in password hashing.
     * @return An Employer object.
     */
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
