/**
 * Service class for managing user-related operations, such as user registration and authentication.
 */
package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserManager {
    private final LogExceptions logExceptions;
    /**
     * Constructor for the UserManager class.
     * Initializes the LogExceptions instance for logging.
     */
    public UserManager() {
        this.logExceptions = new LogExceptions();
    }
    /**
     * Registers a new user in the system.
     *
     * @param username The username of the user.
     * @param email    The email address of the user.
     * @param password The password of the user.
     * @param usertype The type of the user (e.g., role or privilege level).
     * @return The generated user ID if registration is successful; otherwise, 0.
     * @throws ExceptionHandler If an error occurs during user registration.
     */
    public int registerUser(String username, String email, String password, int usertype) throws ExceptionHandler {
        // Check if the email already exists in the database
        if (isEmailExists(email)) {
            throw new ExceptionHandler("Error registering user. Email already exists.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            // Generate a secure salt for password hashing
            String salt = BCrypt.gensalt();

            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(password, salt);

            String sql = "INSERT INTO tbl_user (user_name, email, password_hash, user_type, salt) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.setString(4, String.valueOf(usertype));
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
    /**
     * Checks if a given email address already exists in the user database.
     *
     * @param email The email address to check.
     * @return True if the email already exists; otherwise, false.
     */
    private boolean isEmailExists(String email) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM tbl_user WHERE email = ?";

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

    /**
     * Authenticates a user by checking the entered email and password against the stored credentials.
     *
     * @param email    The email address of the user attempting to authenticate.
     * @param password The password entered by the user.
     * @return The user ID if authentication is successful; -1 if authentication fails or user not found.
     */
    public int authenticateUserAndGetId(String email, String password) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, password_hash, salt FROM tbl_user WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("id");
                        String storedPasswordHash = resultSet.getString("password_hash");
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
    /**
     * Hashes a password using BCrypt with the provided salt.
     *
     * @param password The password to be hashed.
     * @param salt     The salt used in the password hashing process.
     * @return The hashed password.
     */
    private String hashPasswordWithSalt(String password, String salt) {
        // Assuming you are using BCrypt for password hashing
        return BCrypt.hashpw(password, salt);
    }
}
