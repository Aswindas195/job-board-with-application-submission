/**
 * Service class for managing job listings, including posting, updating, and deleting jobs.
 */
package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;

import java.sql.*;

public class JobListingService {
    private final LogExceptions logExceptions;
    private final ApplicationService applicationService;
    private final MapperService mapperService;
    /**
     * Constructor for the JobListingService class.
     * Initializes the required services.
     */
    public JobListingService() {
        this.logExceptions = new LogExceptions();
        this.applicationService = new ApplicationService();
        this.mapperService = new MapperService();
    }
    /**
     * Posts a new job listing to the database.
     *
     * @param employerId      The ID of the employer posting the job.
     * @param industry        The industry ID associated with the job.
     * @param jobType         The job type ID associated with the job.
     * @param jobTitle        The title of the job.
     * @param jobDescription  The description of the job.
     * @param requirements    The requirements for the job.
     * @param location        The location ID associated with the job.
     * @return The ID of the newly posted job.
     * @throws ExceptionHandler If an error occurs during the posting process.
     */
    public int postJob(int employerId, int industry, int jobType ,String jobTitle, String jobDescription, String requirements, int location)
            throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "INSERT INTO tbl_job_post (employer_id, industry, job_type, title, description, requirements, location) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setString(2, String.valueOf(industry));
                preparedStatement.setString(3, String.valueOf(jobType));
                preparedStatement.setString(4, jobTitle);
                preparedStatement.setString(5, jobDescription);
                preparedStatement.setString(6, requirements);
                preparedStatement.setString(7, String.valueOf(location));

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
    /**
     * Deletes a job post from the database.
     *
     * @param employerId The ID of the employer deleting the job.
     * @param jobId      The ID of the job to be deleted.
     * @throws ExceptionHandler If an error occurs during the deletion process.
     */
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
    /**
     * Updates the requirements of a job listing.
     *
     * @param employerId      The ID of the employer updating the job.
     * @param jobId           The ID of the job to be updated.
     * @param newRequirements The new requirements for the job.
     * @return The updated requirements of the job.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
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
    /**
     * Updates the location of a job listing.
     *
     * @param employerId The ID of the employer updating the job.
     * @param jobId      The ID of the job to be updated.
     * @param newLocation The new location ID for the job.
     * @return The updated location ID of the job.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
    public String updateJobLocation(int employerId, int jobId, int newLocation) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_post SET location = ? WHERE employer_id = ? AND id = ? RETURNING location";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, String.valueOf(newLocation));
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
    /**
     * Updates the description of a job listing.
     *
     * @param employerId         The ID of the employer updating the job.
     * @param jobId              The ID of the job to be updated.
     * @param newJobDescription  The new description for the job.
     * @return The updated description of the job.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
    public String updateJobDescription(int employerId, int jobId, String newJobDescription) throws ExceptionHandler {

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
    /**
     * Updates the job type of a job listing.
     *
     * @param employerId The ID of the employer updating the job.
     * @param jobId      The ID of the job to be updated.
     * @param newJobType The new job type ID for the job.
     * @return The updated job type ID of the job.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
    public String updateJobType(int employerId, int jobId, int newJobType) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_post SET job_type = ? WHERE employer_id = ? AND id = ? RETURNING job_type";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, String.valueOf(newJobType));
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("job_type");
                    } else {
                        throw new ExceptionHandler("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating job type.", e);
        }
    }
    /**
     * Updates the industry of a job listing.
     *
     * @param employerId  The ID of the employer updating the job.
     * @param jobId       The ID of the job to be updated.
     * @param newIndustry The new industry ID for the job.
     * @return The updated industry ID of the job.
     * @throws ExceptionHandler If an error occurs during the update process.
     */
    public String updateIndustry(int employerId, int jobId, int newIndustry) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE tbl_job_post SET industry = ? WHERE employer_id = ? AND id = ? RETURNING industry";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, String.valueOf(newIndustry));
                preparedStatement.setInt(2, employerId);
                preparedStatement.setInt(3, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("industry");
                    } else {
                        throw new ExceptionHandler("Job not found or not authorized to update the job.");
                    }
                }
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            throw new ExceptionHandler("Error updating industry.", e);
        }
    }
}
