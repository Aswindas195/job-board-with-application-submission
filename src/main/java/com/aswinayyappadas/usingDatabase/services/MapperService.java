/**
 * Service class for mapping relationships between entities such as jobs and employers, and job applications and job seekers.
 */
package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapperService {
    private final LogExceptions logExceptions;
    /**
     * Constructor for the MapperService class.
     * Initializes the LogExceptions instance for logging.
     */
    public MapperService() {
        this.logExceptions = new LogExceptions();
    }

    /**
     * Checks if a job is mapped to a specific employer.
     *
     * @param jobId      The ID of the job to be checked for mapping.
     * @param employerId The ID of the employer to check for mapping.
     * @return True if the job is mapped to the employer; otherwise, false.
     */
    public boolean isJobMappedToEmployer(int jobId, int employerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM tbl_job_post WHERE id = ? AND employer_id = ?";

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
           logExceptions.logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    /**
     * Checks if a job application is mapped to a specific job seeker.
     *
     * @param jobSeekerId The ID of the job seeker to be checked for mapping.
     * @param jobId       The ID of the job to check for mapping with the job seeker.
     * @return True if the application is mapped to the job seeker; otherwise, false.
     */
    public boolean isApplicationMappedToJobSeeker(int jobSeekerId, int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM tbl_job_application WHERE job_seeker_id = ? AND job_id = ?";

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
            logExceptions.logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
}
