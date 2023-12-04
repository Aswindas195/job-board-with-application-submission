package com.aswinayyappadas.services;

import com.aswinayyappadas.exceptions.JobDeleteException;
import com.aswinayyappadas.exceptions.JobPostException;
import org.mindrot.jbcrypt.BCrypt;

import com.aswinayyappadas.dbconnection.DbConnector;
import com.aswinayyappadas.exceptions.UserRegistrationException;
import com.aswinayyappadas.exceptions.UserRetrievalException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.setString(4, usertype);
                preparedStatement.setString(5, salt);

                return preparedStatement.executeUpdate();
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
    public void deleteJob(int employerId, int jobId) throws JobDeleteException {
        // Implement your job deletion logic here
        try (Connection connection = DbConnector.getConnection()) {
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
    public boolean authenticateUser(String email, String password) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT passwordhash, salt FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Assuming you have a User class; adjust accordingly
                        String storedPasswordHash = resultSet.getString("passwordhash");
                        String salt = resultSet.getString("salt");

                        // Validate the password by rehashing the entered password with the stored salt
                        String enteredPasswordHash = hashPasswordWithSalt(password, salt);

                        return storedPasswordHash.equals(enteredPasswordHash);
                    } else {
                        return false; // User not found
                    }
                }
            }
        } catch (SQLException e) {
            logSQLExceptionDetails(e);
            return false; // Error during authentication
        }
    }

    private String hashPasswordWithSalt(String password, String salt) {
        // Assuming you are using BCrypt for password hashing
        return BCrypt.hashpw(password, salt);
    }
}