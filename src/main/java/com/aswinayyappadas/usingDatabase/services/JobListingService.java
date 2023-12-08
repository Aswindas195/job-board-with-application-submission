package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;

import java.sql.*;

public class JobListingService {
    private final LogExceptions logExceptions;
    private final ApplicationService applicationService;
    private final MapperService mapperService;
    public JobListingService() {
        this.logExceptions = new LogExceptions();
        this.applicationService = new ApplicationService();
        this.mapperService = new MapperService();
    }

    public int postJob(int employerId, String industry, String jobType ,String jobTitle, String jobDescription, String requirements, String location)
            throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "INSERT INTO tbl_job_post (employer_id, industry, job_type, title, description, requirements, location) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setString(2, industry);
                preparedStatement.setString(3, jobType);
                preparedStatement.setString(4, jobTitle);
                preparedStatement.setString(5, jobDescription);
                preparedStatement.setString(6, requirements);
                preparedStatement.setString(7, location);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected <= 0) {
                    throw new ExceptionHandler("Error posting job. Please try again.");
                }

                // Retrieve the generated job post ID
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new ExceptionHandler("Failed to retrieve the job post ID.");
                }
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error posting job.", e);
        }
    }

    public void deleteJobPost(int employerId, int jobId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            // Delete corresponding entries from the applications table
            applicationService.deleteApplicationsForJob(connection, jobId);

            // Delete the job from the joblistings table
            String sql = "DELETE FROM tbl_job_post WHERE employer_id = ? AND id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setInt(2, jobId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new ExceptionHandler("Job not found or not authorized to delete the job.");
                }

                // Job deleted successfully
            }
        } catch (SQLException e) {
           logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error deleting job.", e);
        }
    }
    public String updateJobRequirements(int employerId, int jobId, String newRequirements) throws ExceptionHandler {
        // Check if the job is mapped to the employer
        if (!mapperService.isJobMappedToEmployer(jobId, employerId)) {
            throw new ExceptionHandler("Job not mapped to the employer.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_post SET requirements = ? WHERE employer_id = ? AND id = ? RETURNING requirements";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newRequirements);
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("requirements");
                    } else {
                        throw new ExceptionHandler("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
           logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating job requirements.", e);
        }
    }
    public String updateJobLocation(int employerId, int jobId, String newLocation) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_post SET location = ? WHERE employer_id = ? AND id = ? RETURNING location";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newLocation);
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("location");
                    } else {
                        throw new ExceptionHandler("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
           logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating job location.", e);
        }
    }
    public String updateJobDescription(int employerId, int jobId, String newJobDescription) throws ExceptionHandler {
//        // Check if the job is mapped to the employer
//        if (!mapperService.isJobMappedToEmployer(jobId, employerId)) {
//            throw new ExceptionHandler("Job not mapped to the employer.");
//        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_post SET description = ? WHERE employer_id = ? AND id = ? RETURNING description";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newJobDescription);
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("description");
                    } else {
                        throw new ExceptionHandler("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
           logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating job description.", e);
        }
    }

}
