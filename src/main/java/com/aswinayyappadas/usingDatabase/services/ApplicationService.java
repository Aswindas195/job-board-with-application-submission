/**
 * This class represents the service layer for job applications in the application.
 * It provides methods for applying for jobs, checking if a user has already applied,
 * updating cover letters and resume file paths, and deleting job applications.
 * The class uses a database connection for executing SQL queries related to job applications.
 */
package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;
import com.aswinayyappadas.usingDatabase.util.application.ApplicationUtils;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicationService {
    private final LogExceptions logExceptions;

    /**
     * Constructs an {@code ApplicationService} instance and initializes the {@code LogExceptions} object.
     */
    public ApplicationService() {
        this.logExceptions = new LogExceptions();
    }

    /**
     * Applies for a job by inserting a new job application into the database.
     *
     * @param jobSeekerId The ID of the job seeker applying for the job.
     * @param jobId       The ID of the job being applied for.
     * @return A JSON object containing details of the applied job, or null if the application is not successful.
     * @throws ExceptionHandler If an error occurs during the application process.
     */
    public JSONObject applyForJob(int jobSeekerId, int jobId) throws ExceptionHandler {
        // Check if the user has already applied for the job
        if (hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ExceptionHandler("Error applying for the job. User has already applied.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            // Generate random cloud file location path
            String resumeFilePath = ApplicationUtils.generateRandomFilePath();

            // Generate cover letter text
            String coverLetter = ApplicationUtils.generateCoverLetter();

            // Your existing code for applying for a job
            String sql = "INSERT INTO tbl_job_application (job_seeker_id, job_id, resume_file_path, cover_letter, date) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING *";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);
                preparedStatement.setInt(2, jobId);
                preparedStatement.setString(3, resumeFilePath);
                preparedStatement.setString(4, coverLetter);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Application successful, create a JSON object with all fields
                    JSONObject applicationDetails = new JSONObject();
                    applicationDetails.put("applicationId", resultSet.getInt("id"));
                    applicationDetails.put("jobSeekerId", resultSet.getInt("job_seeker_id"));
                    applicationDetails.put("jobId", resultSet.getInt("job_id"));
                    applicationDetails.put("resumeFilePath", resultSet.getString("resume_file_path"));
                    applicationDetails.put("coverLetter", resultSet.getString("cover_letter"));
                    applicationDetails.put("submissionDate", resultSet.getTimestamp("date").toString());
                    // Add other fields as needed

                    return applicationDetails;
                }

                // Return null if application is not successful
                return null;
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error applying for the job.", e);
        }
    }

    /**
     * Checks if a user has already applied for a specific job.
     *
     * @param jobSeekerId The ID of the job seeker.
     * @param jobId       The ID of the job.
     * @return True if the user has already applied, false otherwise.
     */
    public boolean hasUserAppliedForJob(int jobSeekerId, int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM tbl_job_application WHERE job_seeker_id = ? AND job_id = ?";

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

    /**
     * Deletes all job applications for a specific job.
     *
     * @param connection The database connection.
     * @param jobId      The ID of the job for which applications should be deleted.
     * @throws SQLException If an error occurs during the deletion process.
     */
    public void deleteApplicationsForJob(Connection connection, int jobId) throws SQLException {
        String deleteApplicationsSql = "DELETE FROM tbl_job_application WHERE job_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteApplicationsSql)) {
            preparedStatement.setInt(1, jobId);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Updates the cover letter for a specific job application.
     *
     * @param jobSeekerId     The ID of the job seeker.
     * @param jobId           The ID of the job.
     * @param newCoverLetter  The new cover letter text.
     * @return The updated cover letter.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
    public String updateCoverLetter(int jobSeekerId, int jobId, String newCoverLetter) throws ExceptionHandler {
        // Check if the application exists before attempting to update
        if (!hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ExceptionHandler("Error updating cover letter. Application not found.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_application SET cover_letter = ? WHERE job_seeker_id = ? AND job_id = ? RETURNING cover_letter";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newCoverLetter);
                preparedStatement.setInt(2, jobSeekerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("cover_letter");
                    } else {
                        throw new ExceptionHandler("Application not found or not authorized to update the cover letter.");
                    }
                }
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating cover letter.", e);
        }
    }

    /**
     * Updates the resume file path for a specific job application.
     *
     * @param jobSeekerId        The ID of the job seeker.
     * @param jobId              The ID of the job.
     * @param newResumeFilePath  The new resume file path.
     * @return The updated resume file path.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
    public String updateResumeFilePath(int jobSeekerId, int jobId, String newResumeFilePath) throws  ExceptionHandler{
        // Check if the application exists before attempting to update
        if (!hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ExceptionHandler("Error updating resume file path. Application not found.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_application SET resume_file_path = ? WHERE job_seeker_id = ? AND job_id = ? RETURNING resume_file_path";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newResumeFilePath);
                preparedStatement.setInt(2, jobSeekerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("resume_file_path");
                    } else {
                        throw new ExceptionHandler("Application not found or not authorized to update the resume file path.");
                    }
                }
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating resume file path.", e);
        }
    }

    /**
     * Deletes a job application based on the job seeker ID and job ID.
     *
     * @param jobSeekerId The ID of the job seeker.
     * @param jobId       The ID of the job.
     * @throws ExceptionHandler If an error occurs during the deletion process.
     */
    public void deleteJobApplicationByJobSeekerId(int jobSeekerId, int jobId) throws ExceptionHandler {
        // Check if the application exists before attempting to delete
        if (!hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ExceptionHandler("Error deleting job application. Application not found.");
        }

        try (Connection connection = DbConnector.getConnection()) {
            // Your SQL query to delete the job application
            String sql = "DELETE FROM tbl_job_application WHERE job_seeker_id = ? AND job_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);
                preparedStatement.setInt(2, jobId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 0) {
                    throw new ExceptionHandler("Job application not found or not authorized to delete the application.");
                }

                // Job application deleted successfully
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error deleting job application.", e);
        }
    }
}

