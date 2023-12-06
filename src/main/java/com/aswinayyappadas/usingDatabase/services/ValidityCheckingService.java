package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidityCheckingService {
    private final LogExceptions logExceptions;

    public ValidityCheckingService() {
        this.logExceptions = new LogExceptions();
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
            logExceptions.logSQLExceptionDetails(e);
            return false; // Error during validation
        }
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
            logExceptions.logSQLExceptionDetails(e);
            return false; // Error during validation
        }
    }

    public boolean isValidUserId(int userId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE userId = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the user ID is valid
                    }
                }
            }

            // If the resultSet is empty or there is an issue with the query
            return false;
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            return false; // Default to false in case of an exception
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
            logExceptions.logSQLExceptionDetails(e);
            return false; // Default to false in case of an exception
        }
    }
}
