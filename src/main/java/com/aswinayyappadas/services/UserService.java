package com.aswinayyappadas.services;


import com.aswinayyappadas.exceptions.*;
import com.aswinayyappadas.util.application.ApplicationUtils;
import org.json.JSONArray;
import org.mindrot.jbcrypt.BCrypt;

import com.aswinayyappadas.dbconnection.DbConnector;

import java.sql.*;

import org.json.JSONObject;
public class UserService {
    private void logSQLExceptionDetails(SQLException e) {
        System.err.println("SQL Exception Details:");
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("Message: " + e.getMessage());
    }
    public int registerUser(String username, String email, String password, String usertype) throws UserRegistrationException {
        // Check if the email already exists in the database
        if (isEmailExists(email)) {
            throw new UserRegistrationException("Error registering user. Email already exists.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            // Generate a secure salt for password hashing
            String salt = BCrypt.gensalt();

            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(password, salt);

            String sql = "INSERT INTO users (username, email, passwordhash, usertype, salt) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.setString(4, usertype);
                preparedStatement.setString(5, salt);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Retrieve the generated keys
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // This is the generated user ID
                        } else {
                            // Handle the case where no key was generated
                            throw new SQLException("Error generating user ID.");
                        }
                    }
                } else {
                    // Handle the case where no rows were affected
                    return 0;
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new UserRegistrationException("Error registering user.", e);
        }
    }


    private boolean isEmailExists(String email) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the email already exists
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    public void postJob(int employerId, String jobTitle, String jobDescription, String requirements, String location)
            throws JobPostException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "INSERT INTO joblistings (employerid, title, description, requirements, location) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setString(2, jobTitle);
                preparedStatement.setString(3, jobDescription);
                preparedStatement.setString(4, requirements);
                preparedStatement.setString(5, location);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected <= 0) {
                    throw new JobPostException("Error posting job. Please try again.");
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new JobPostException("Error posting job.", e);
        }
    }
    public String getJwtSecretKeyByEmail(String email) throws SQLException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jwt_secret_key FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("jwt_secret_key");
                    }
                }
            }
        }

        // Return null or handle accordingly based on your requirements
        return null;
    }
    // Example method to delete jwt_secret_key by email
    public void deleteJwtSecretKey(String email) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE users SET jwt_secret_key = NULL WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            // Handle the exception appropriately (e.g., log it)
        }
    }
    public boolean isValidEmployerId(int employerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT usertype FROM users WHERE userId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String userType = resultSet.getString("usertype");
                        return "employer".equalsIgnoreCase(userType);
                    } else {
                        return false; // User not found
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            return false; // Error during validation
        }
    }
    public void storeSecretKeyByEmail(String email, String secretKey) throws SQLException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE users SET jwt_secret_key = ? WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, secretKey);
                preparedStatement.setString(2, email);

                preparedStatement.executeUpdate();
            }
        }
    }
    public int getEmployerIdByEmail(String email) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT userid FROM users WHERE email = ? AND usertype = 'Employer'";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("userid");
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
        }

        // Return a sentinel value or throw an exception based on your error handling strategy
        return -1;
    }

    public JSONObject getUserById(int userId) throws UserRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT username, email, usertype FROM users WHERE userid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Assuming you have a User class; adjust accordingly
                        String username = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String userType = resultSet.getString("usertype");

                        // Construct a JSON object

                        return new JSONObject()
                                .put("username", username)
                                .put("email", email)
                                .put("usertype", userType);
                    } else {
                        throw new UserRetrievalException("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new UserRetrievalException("Error retrieving user by ID.", e);
        }
    }
    public void deleteJobPost(int employerId, int jobId) throws JobDeleteException {
        try (Connection connection = DbConnector.getConnection()) {
            // Delete corresponding entries from the applications table
            deleteApplicationsForJob(connection, jobId);

            // Delete the job from the joblistings table
            String sql = "DELETE FROM joblistings WHERE employerid = ? AND jobid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setInt(2, jobId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new JobDeleteException("Job not found or not authorized to delete the job.");
                }

                // Job deleted successfully
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new JobDeleteException("Error deleting job.", e);
        }
    }

    private void deleteApplicationsForJob(Connection connection, int jobId) throws SQLException {
        String deleteApplicationsSql = "DELETE FROM applications WHERE jobid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteApplicationsSql)) {
            preparedStatement.setInt(1, jobId);
            preparedStatement.executeUpdate();
        }
    }
    // New method to retrieve all job posts by a specific employer
    public JSONArray getJobPostsByEmployer(int employerId) throws JobRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jobid, title, description, requirements, location FROM joblistings WHERE employerid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobPosts = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("jobid");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String location = resultSet.getString("location");

                        // Construct a JSON object for each job post
                        JSONObject jobPost = new JSONObject()
                                .put("jobId", jobId)
                                .put("title", title)
                                .put("description", description)
                                .put("requirements", requirements)
                                .put("location", location);

                        jobPosts.put(jobPost);
                    }

                    return jobPosts; // Return the array, even if it's empty
                }
            }
        } catch (SQLException e) {
            throw new JobRetrievalException("Error retrieving job posts by employer ID.", e);
        }
    }
    public int authenticateUserAndGetId(String email, String password) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT userid, passwordhash, salt FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("userid");
                        String storedPasswordHash = resultSet.getString("passwordhash");
                        String salt = resultSet.getString("salt");

                        // Validate the password by rehashing the entered password with the stored salt
                        String enteredPasswordHash = hashPasswordWithSalt(password, salt);

                        if (storedPasswordHash.equals(enteredPasswordHash)) {
                            return userId; // Authentication successful, return user ID
                        } else {
                            return -1; // Authentication failed
                        }
                    } else {
                        return -1; // User not found
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            return -1; // Error during authentication
        }
    }

    public String getEmailByUserId(int userId) throws UserRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT email FROM users WHERE userid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("email");
                    } else {
                        throw new UserRetrievalException("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new UserRetrievalException("Error retrieving email by user ID.", e);
        }
    }
    public boolean isJobMappedToEmployer(int jobId, int employerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM joblistings WHERE jobid = ? AND employerid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobId);
                preparedStatement.setInt(2, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the job is mapped to the employer
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    public String updateJobDescription(int employerId, int jobId, String newJobDescription) throws JobUpdateException {
        // Check if the job is mapped to the employer
        if (!isJobMappedToEmployer(jobId, employerId)) {
            throw new JobUpdateException("Job not mapped to the employer.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE joblistings SET description = ? WHERE employerid = ? AND jobid = ? RETURNING description";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newJobDescription);
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("description");
                    } else {
                        throw new JobUpdateException("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new JobUpdateException("Error updating job description.", e);
        }
    }
    public String updateJobLocation(int employerId, int jobId, String newLocation) throws JobUpdateException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE joblistings SET location = ? WHERE employerid = ? AND jobid = ? RETURNING location";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newLocation);
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("location");
                    } else {
                        throw new JobUpdateException("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new JobUpdateException("Error updating job location.", e);
        }
    }

    public String updateJobRequirements(int employerId, int jobId, String newRequirements) throws JobUpdateException {
        // Check if the job is mapped to the employer
        if (!isJobMappedToEmployer(jobId, employerId)) {
            throw new JobUpdateException("Job not mapped to the employer.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE joblistings SET requirements = ? WHERE employerid = ? AND jobid = ? RETURNING requirements";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newRequirements);
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("requirements");
                    } else {
                        throw new JobUpdateException("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new JobUpdateException("Error updating job requirements.", e);
        }
    }
    //////////////////////////////////
    public String updateResumeFilePath(int jobSeekerId, int jobId, String newResumeFilePath) throws  ApplicationUpdateException{
        // Check if the application exists before attempting to update
        if (!hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ApplicationUpdateException("Error updating resume file path. Application not found.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE applications SET resumefilepath = ? WHERE jobseekerid = ? AND jobid = ? RETURNING resumefilepath";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newResumeFilePath);
                preparedStatement.setInt(2, jobSeekerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("resumefilepath");
                    } else {
                        throw new ApplicationUpdateException("Application not found or not authorized to update the resume file path.");
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new ApplicationUpdateException("Error updating resume file path.", e);
        }
    }
    public boolean isApplicationMappedToJobSeeker(int jobSeekerId, int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM applications WHERE jobseekerid = ? AND jobid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);
                preparedStatement.setInt(2, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the application is mapped to the job seeker
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    // Add this method to get userType from userId
    public String getUserTypeByUserId(int userId) {
        // This assumes you have a database connection. Replace "yourDatabaseConnection" with your actual database connection.
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT usertype FROM users WHERE userid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("usertype");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }

        return null; // Return null if user type is not found
    }

    public String updateCoverLetter(int jobSeekerId, int jobId, String newCoverLetter) throws ApplicationUpdateException {
        // Check if the application exists before attempting to update
        if (!hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ApplicationUpdateException("Error updating cover letter. Application not found.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE applications SET coverletter = ? WHERE jobseekerid = ? AND jobid = ? RETURNING coverletter";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newCoverLetter);
                preparedStatement.setInt(2, jobSeekerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("coverletter");
                    } else {
                        throw new ApplicationUpdateException("Application not found or not authorized to update the cover letter.");
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            throw new ApplicationUpdateException("Error updating cover letter.", e);
        }
    }
public JSONArray getAppliedJobsByJobSeeker(int jobSeekerId) throws JobRetrievalException {
    try (Connection connection = DbConnector.getConnection()) {
        String sql = "SELECT j.jobid, j.title, j.description, j.requirements, j.location, a.submissiondate, a.coverletter, a.resumefilepath " +
                "FROM joblistings j " +
                "JOIN applications a ON j.jobid = a.jobid " +
                "WHERE a.jobseekerid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, jobSeekerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                JSONArray appliedJobs = new JSONArray();

                while (resultSet.next()) {
                    int jobId = resultSet.getInt("jobid");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String requirements = resultSet.getString("requirements");
                    String location = resultSet.getString("location");
                    String submissionDate = resultSet.getString("submissiondate");
                    String coverLetter = resultSet.getString("coverletter");
                    String resumeFilePath = resultSet.getString("resumefilepath");

                    // Construct a JSON object for each applied job
                    JSONObject appliedJob = new JSONObject()
                            .put("jobId", jobId)
                            .put("title", title)
                            .put("description", description)
                            .put("requirements", requirements)
                            .put("location", location)
                            .put("submissionDate", submissionDate)
                            .put("coverLetter", coverLetter)
                            .put("resumeFilePath", resumeFilePath);

                    appliedJobs.put(appliedJob);
                }

                return appliedJobs; // Return the array, even if it's empty
            }
        }
    } catch (SQLException e) {
        throw new JobRetrievalException("Error retrieving applied jobs by job seeker ID.", e);
    }
}


    public void deleteJobApplication(int jobSeekerId, int jobId) throws JobDeleteException {
    // Check if the application exists before attempting to delete
    if (!hasUserAppliedForJob(jobSeekerId, jobId)) {
        throw new JobDeleteException("Error deleting job application. Application not found.");
    }

    try (Connection connection = DbConnector.getConnection()) {
        // Your SQL query to delete the job application
        String sql = "DELETE FROM applications WHERE jobseekerid = ? AND jobid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, jobSeekerId);
            preparedStatement.setInt(2, jobId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new JobDeleteException("Job application not found or not authorized to delete the application.");
            }

            // Job application deleted successfully
        }
    } catch (SQLException e) {
        logSQLExceptionDetails(e);
        throw new JobDeleteException("Error deleting job application.", e);
    }
}
public boolean applyForJob(int jobSeekerId, int jobId) throws JobApplicationException {
    // Check if the user has already applied for the job
    if (hasUserAppliedForJob(jobSeekerId, jobId)) {
        throw new JobApplicationException("Error applying for the job. User has already applied.");
    }

    try (Connection connection = DbConnector.getConnection()) {
        // Generate random cloud file location path
        String resumeFilePath = ApplicationUtils.generateRandomFilePath();

        // Generate cover letter text
        String coverLetter = ApplicationUtils.generateCoverLetter();

        // Your existing code for applying for a job
        String sql = "INSERT INTO applications (jobseekerid, jobid, resumefilepath, coverletter, submissiondate) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, jobSeekerId);
            preparedStatement.setInt(2, jobId);
            preparedStatement.setString(3, resumeFilePath);
            preparedStatement.setString(4, coverLetter);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Return true if application is successful
        }
    } catch (SQLException e) {
        logSQLExceptionDetails(e);
        throw new JobApplicationException("Error applying for the job.", e);
    }
}
    public boolean hasUserAppliedForJob(int jobSeekerId, int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM applications WHERE jobseekerid = ? AND jobid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);
                preparedStatement.setInt(2, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the user has already applied for the job
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    public JSONArray getAllJobsFromListings() throws SQLException {
        JSONArray jobListingsArray = new JSONArray();

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jobid, title, description, location FROM joblistings";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int jobId = resultSet.getInt("jobid");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String location = resultSet.getString("location");

                    JSONObject jobListingObject = new JSONObject();
                    jobListingObject.put("jobId", jobId);
                    jobListingObject.put("title", title);
                    jobListingObject.put("description", description);
                    jobListingObject.put("location", location);

                    jobListingsArray.put(jobListingObject);
                }
            }
        }

        return jobListingsArray;
    }
    public boolean isValidJobSeekerId(int jobSeekerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT usertype FROM users WHERE userid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String userType = resultSet.getString("usertype");
                        return "job_seeker".equalsIgnoreCase(userType);
                    } else {
                        return false; // User not found
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            return false; // Error during validation
        }
    }

    public boolean isValidJobId(int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM joblistings WHERE jobid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the job ID is valid
                    }
                }
            }

            // If the resultSet is empty or there is an issue with the query
            return false;
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            return false; // Default to false in case of an exception
        }
    }

    private String hashPasswordWithSalt(String password, String salt) {
        // Assuming you are using BCrypt for password hashing
        return BCrypt.hashpw(password, salt);
    }
}