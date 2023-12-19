/**
 * Service class for performing validity checks related to employers, job seekers, and job IDs.
 */
package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidityCheckingService {
    private final LogExceptions logExceptions;
    /**
     * Constructor for the ValidityCheckingService class.
     * Initializes the LogExceptions instance for logging.
     */
    public ValidityCheckingService() {
        this.logExceptions = new LogExceptions();
    }
    /**
     * Checks if the provided employer ID corresponds to a valid employer user.
     *
     * @param employerId The ID of the employer to be validated.
     * @return True if the employer ID is valid; otherwise, false.
     */
    public boolean isValidEmployerId(int employerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT user_type FROM tbl_user WHERE Id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String userType = resultSet.getString("user_type");
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

    /**
     * Checks if the provided job seeker ID corresponds to a valid job seeker user.
     *
     * @param jobSeekerId The ID of the job seeker to be validated.
     * @return True if the job seeker ID is valid; otherwise, false.
     */
    public boolean isValidJobSeekerId(int jobSeekerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT user_type FROM tbl_user WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String userType = resultSet.getString("user_type");
                        return "Job Seeker".equalsIgnoreCase(userType);
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
    /**
     * Checks if the provided job ID corresponds to a valid job posting.
     *
     * @param jobId The ID of the job to be validated.
     * @return True if the job ID is valid; otherwise, false.
     */
    public boolean isValidJobId(int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM tbl_job_post WHERE id = ?";

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
