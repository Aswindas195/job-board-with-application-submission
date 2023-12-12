package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.job.Job;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.User;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {

    private UserManager userManager;

    @BeforeEach
    void setUp() {
        // Initialize the UserManager instance before each test
        userManager = new UserManager();
    }

    @Test
    void testAuthenticateUserAndGetId() {
        // Add a sample user to userData
        Jobseeker user = new Jobseeker();
        String salt = BCrypt.gensalt();
        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw("password", salt);
        user.setEmail("user@example.com");
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        UserData.userData.put(1, user);
        UserData.userEmailData.put("user@example.com", user);

        // Test authentication with correct email and password
        assertEquals(0, userManager.authenticateUserAndGetId("user@example.com", "password"));

        // Test authentication with incorrect password
        assertEquals(-1, userManager.authenticateUserAndGetId("user@example.com", "wrongPassword"));

        // Test authentication with non-existent email
        assertEquals(-1, userManager.authenticateUserAndGetId("nonexistent@example.com", "password"));
    }

    @Test
    void testIsEmailExist() {
        // Add a sample user to userEmailData
        User user = new User();
        user.setEmail("user@example.com");
        UserData.userEmailData.put("user@example.com", user);

        // Test checking the existence of an existing email
        assertTrue(userManager.isEmailExist("user@example.com"));

        // Test checking the existence of a non-existent email
        assertFalse(userManager.isEmailExist("nonexistent@example.com"));
    }

    @Test
    void testAddUser() {
        // Test adding a user to userData
        User user = new User();
//        user.setUserId(1);
        userManager.addUser(1, user);

        // Check if the user is added to userData
        assertTrue(UserData.userData.containsKey(1));
        assertEquals(user, UserData.userData.get(1));
    }

    @Test
    void testAddUserEmail() {
        // Test adding a user to userEmailData
        User user = new User();
        user.setEmail("user@example.com");
        userManager.addUserEmail("user@example.com", user);

        // Check if the user is added to userEmailData
        assertTrue(UserData.userEmailData.containsKey("user@example.com"));
        assertEquals(user, UserData.userEmailData.get("user@example.com"));
    }

    @Test
    void testCreateJobseeker() {
        // Test creating a Jobseeker instance
        Jobseeker jobseeker = userManager.createJobseeker(1, "Jobseeker1", "jobseeker@example.com", "hashedPassword", "salt");

        // Check if the Jobseeker instance is created correctly
        assertNotNull(jobseeker);
        assertEquals(1, jobseeker.getJobseekerId());
        assertEquals("Jobseeker1", jobseeker.getName());
        assertEquals("jobseeker@example.com", jobseeker.getEmail());
        assertEquals("hashedPassword", jobseeker.getPassword());
        assertEquals("salt", jobseeker.getSalt());
    }

    @Test
    void testCreateEmployer() {
        // Test creating an Employer instance
        Employer employer = userManager.createEmployer(1, "Employer1", "employer@example.com", "hashedPassword", "salt");

        // Check if the Employer instance is created correctly
        assertNotNull(employer);
        assertEquals(1, employer.getEmployerId());
        assertEquals("Employer1", employer.getName());
        assertEquals("employer@example.com", employer.getEmail());
        assertEquals("hashedPassword", employer.getPassword());
        assertEquals("salt", employer.getSalt());
    }
}
