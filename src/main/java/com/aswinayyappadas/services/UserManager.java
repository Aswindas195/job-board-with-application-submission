package com.aswinayyappadas.services;

import com.aswinayyappadas.dbconnection.DbConnector;
import com.aswinayyappadas.exceptions.ExceptionHandler;
import org.mindrot.jbcrypt.BCrypt;
import com.aswinayyappadas.exceptions.LogExceptions;

import java.sql.*;

public class UserManager {
    private final LogExceptions logExceptions;

    public UserManager() {
        this.logExceptions = new LogExceptions();
    }

    private String hashPasswordWithSalt(String password, String salt) {
        // Assuming you are using BCrypt for password hashing
        return BCrypt.hashpw(password, salt);
    }
    public int registerUser(String username, String email, String password, String usertype) throws ExceptionHandler {
        // Check if the email already exists in the database
        if (isEmailExists(email)) {
            throw new ExceptionHandler("Error registering user. Email already exists.");
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
           logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error registering user.", e);
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
           logExceptions.logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
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
           logExceptions.logSQLExceptionDetails(e);
            return -1; // Error during authentication
        }
    }
    public boolean logoutUser(int userId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE users SET jwt_secret_key = NULL WHERE userid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                int rowsAffected = preparedStatement.executeUpdate();

                // Check if the update was successful
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            return false; // Error during logout
        }
    }
}
