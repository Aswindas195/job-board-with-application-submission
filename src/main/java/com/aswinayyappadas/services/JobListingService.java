package com.aswinayyappadas.services;

import com.aswinayyappadas.dbconnection.DbConnector;
import com.aswinayyappadas.exceptions.*;

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

    public int postJob(int employerId, String jobTitle, String jobDescription, String requirements, String location)
            throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "INSERT INTO joblistings (employerid, title, description, requirements, location) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setString(2, jobTitle);
                preparedStatement.setString(3, jobDescription);
                preparedStatement.setString(4, requirements);
                preparedStatement.setString(5, location);

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
            String sql = "DELETE FROM joblistings WHERE employerid = ? AND jobid = ?";

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
            String sql = "UPDATE joblistings SET requirements = ? WHERE employerid = ? AND jobid = ? RETURNING requirements";

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
            String sql = "UPDATE joblistings SET location = ? WHERE employerid = ? AND jobid = ? RETURNING location";

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
        // Check if the job is mapped to the employer
        if (!mapperService.isJobMappedToEmployer(jobId, employerId)) {
            throw new ExceptionHandler("Job not mapped to the employer.");
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
