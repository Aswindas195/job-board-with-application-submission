package com.aswinayyappadas.services;

import com.aswinayyappadas.dbconnection.DbConnector;
import com.aswinayyappadas.exceptions.ApplicationUpdateException;
import com.aswinayyappadas.exceptions.JobApplicationException;
import com.aswinayyappadas.exceptions.JobDeleteException;
import com.aswinayyappadas.util.application.ApplicationUtils;
import com.aswinayyappadas.exceptions.LogExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicationService {
    private final LogExceptions logExceptions;

    public ApplicationService() {
        this.logExceptions = new LogExceptions();
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
           logExceptions.logSQLExceptionDetails(e);
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
           logExceptions.logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    public void deleteApplicationsForJob(Connection connection, int jobId) throws SQLException {
        String deleteApplicationsSql = "DELETE FROM applications WHERE jobid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteApplicationsSql)) {
            preparedStatement.setInt(1, jobId);
            preparedStatement.executeUpdate();
        }
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
           logExceptions.logSQLExceptionDetails(e);
            throw new ApplicationUpdateException("Error updating cover letter.", e);
        }
    }
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
           logExceptions.logSQLExceptionDetails(e);
            throw new ApplicationUpdateException("Error updating resume file path.", e);
        }
    }
    public void deleteJobApplicationByJobSeekerId(int jobSeekerId, int jobId) throws JobDeleteException {
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
           logExceptions.logSQLExceptionDetails(e);
            throw new JobDeleteException("Error deleting job application.", e);
        }
    }
}
