/**
 * This class provides various methods to retrieve information related to job posts and applications.
 * It includes methods to get job posts by employer, user type by user ID, applied jobs by job seeker,
 * all jobs from listings, jobs by location, applications by job, and jobs by different combinations of
 * location, industry, and job type.
 */
package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetServices {
    /**
     * Retrieves a JSON array of job posts for a specific employer.
     *
     * @param employerId The ID of the employer.
     * @return A JSON array containing job posts for the specified employer.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobPostsByEmployer(int employerId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, title, industry, job_type, description, requirements, location FROM tbl_job_post WHERE employer_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobPosts = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        String location = resultSet.getString("location");
                        String industry = resultSet.getString("industry");
                        String jobType = resultSet.getString("job_type");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");

                        // Construct a JSON object for each job post
                        JSONObject jobPost = new JSONObject()
                                .put("jobId", jobId)
                                .put("title", title)
                                .put("industry", industry)
                                .put("jobType", jobType)
                                .put("description", description)
                                .put("requirements", requirements)
                                .put("location", location);

                        jobPosts.put(jobPost);
                    }

                    return jobPosts; // Return the array, even if it's empty
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job posts by employer ID.", e);
        }
    }
    /**
     * Retrieves the user type based on the given user ID.
     *
     * @param userId The ID of the user.
     * @return The user type associated with the given user ID.
     */
    public String getUserTypeByUserId(int userId) {
        // This assumes you have a database connection. Replace "yourDatabaseConnection" with your actual database connection.
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT user_type FROM tbl_user WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("user_type");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }

        return null; // Return null if user type is not found
    }
    /**
     * Retrieves a JSON array of jobs that a job seeker has applied for.
     *
     * @param jobSeekerId The ID of the job seeker.
     * @return A JSON array containing job information for the applied jobs.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getAppliedJobsByJobSeeker(int jobSeekerId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT j.id, j.title, j.description, j.requirements, j.location, j.industry, j.job_type, a.date, a.cover_letter, a.resume_file_path " +
                    "FROM tbl_job_post j " +
                    "JOIN tbl_job_application a ON j.id = a.job_id " +
                    "WHERE a.job_seeker_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray appliedJobs = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String location = resultSet.getString("location");
                        String industry = resultSet.getString("industry");
                        String jobType = resultSet.getString("job_type");
                        String submissionDate = resultSet.getString("date");
                        String coverLetter = resultSet.getString("cover_letter");
                        String resumeFilePath = resultSet.getString("resume_file_path");

                        // Construct a JSON object for each applied job
                        JSONObject appliedJob = new JSONObject()
                                .put("jobId", jobId)
                                .put("industry", industry)
                                .put("jobType", jobType)
                                .put("title", title)
                                .put("description", description)
                                .put("requirements", requirements)
                                .put("location", location)
                                .put("submissionDate", submissionDate)
                                .put("coverLetter", coverLetter)
                                .put("resumeFilePath", resumeFilePath);

                        appliedJobs.put(appliedJob);
                    }

                    return appliedJobs; // Return the array, even if it's empty
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving applied jobs by job seeker ID.", e);
        }
    }
    /**
     * Retrieves a JSON array of all jobs from listings.
     *
     * @return A JSON array containing information about all job listings.
     * @throws SQLException If an SQL error occurs during the retrieval process.
     */
    public JSONArray getAllJobsFromListings() throws SQLException {
        JSONArray jobListingsArray = new JSONArray();

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int jobId = resultSet.getInt("id");
                    String jobType = resultSet.getString("job_type");
                    String industry = resultSet.getString("industry");
                    String title = resultSet.getString("title");
                    String requirements = resultSet.getString("requirements");
                    String description = resultSet.getString("description");
                    String location = resultSet.getString("location");

                    JSONObject jobListingObject = new JSONObject();
                    jobListingObject.put("jobId", jobId);
                    jobListingObject.put("industry", industry);
                    jobListingObject.put("jobType", jobType);
                    jobListingObject.put("title", title);
                    jobListingObject.put("description", description);
                    jobListingObject.put("requirements", requirements);
                    jobListingObject.put("location", location);

                    jobListingsArray.put(jobListingObject);
                }
            }
        }

        return jobListingsArray;
    }

    /**
     * Retrieves a JSON array of jobs based on the specified location.
     *
     * @param location The location to filter the jobs by.
     * @return A JSON array containing job information for the specified location.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByLocation(String location) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post WHERE location = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, location);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String industry = resultSet.getString("industry");
                        location = resultSet.getString("location");
                        String jobType = resultSet.getString("job_type");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industry);
                        jobListingObject.put("jobType", jobType);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", location);
                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by location.", e);
        }
    }
    /**
     * Retrieves a JSON array of applications for a specific job and employer.
     *
     * @param employerId The ID of the employer.
     * @param jobId      The ID of the job.
     * @return A JSON array containing information about applications for the specified job and employer.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getApplicationsByJob(int employerId, int jobId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            // Assuming you have a table named applications, adjust the SQL query accordingly
            String sql = "SELECT a.id, a.job_seeker_id, a.date, a.cover_letter, a.resume_file_path, u.user_name, u.email " +
                    "FROM tbl_job_application a " +
                    "JOIN tbl_user u ON a.job_seeker_id = u.id " +
                    "WHERE a.job_id = ? AND EXISTS (SELECT 1 FROM tbl_job_post j WHERE j.id = ? AND j.employer_id = ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobId);
                preparedStatement.setInt(2, jobId);
                preparedStatement.setInt(3, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray applicationsArray = new JSONArray();

                    while (resultSet.next()) {
                        int applicationId = resultSet.getInt("id");
                        int jobSeekerId = resultSet.getInt("job_seeker_id");
                        String submissionDate = resultSet.getString("date");
                        String coverLetter = resultSet.getString("cover_letter");
                        String resumeFilePath = resultSet.getString("resume_file_path");
                        String jobSeekerUsername = resultSet.getString("user_name");
                        String jobSeekerEmail = resultSet.getString("email");

                        JSONObject applicationObject = new JSONObject();
                        applicationObject.put("applicationId", applicationId);
                        applicationObject.put("jobSeekerId", jobSeekerId);
                        applicationObject.put("submissionDate", submissionDate);
                        applicationObject.put("coverLetter", coverLetter);
                        applicationObject.put("resumeFilePath", resumeFilePath);
                        applicationObject.put("jobSeekerUsername", jobSeekerUsername);
                        applicationObject.put("jobSeekerEmail", jobSeekerEmail);

                        applicationsArray.put(applicationObject);
                    }

                    return applicationsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving applications by job ID and employer ID.", e);
        }
    }
    /**
     * Retrieves a JSON array of jobs based on location, industry, and job type.
     *
     * @param location The location to filter the jobs by.
     * @param industry The industry to filter the jobs by.
     * @param jobType  The job type to filter the jobs by.
     * @return A JSON array containing job information for the specified location, industry, and job type.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByLocationIndustryType(String location, String industry, String jobType) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post " +
                    "WHERE location = ? AND industry = ? AND job_type = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, location);
                preparedStatement.setString(2, industry);
                preparedStatement.setString(3, jobType);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobTypeResult = resultSet.getString("job_type");
                        String industryResult = resultSet.getString("industry");
                        String title = resultSet.getString("title");
                        String requirements = resultSet.getString("requirements");
                        String description = resultSet.getString("description");
                        String locationResult = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industryResult);
                        jobListingObject.put("jobType", jobTypeResult);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", locationResult);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by location, industry, and job type.", e);
        }
    }
    /**
     * Retrieves a JSON array of jobs based on the specified location and industry.
     *
     * @param location The location to filter the jobs by.
     * @param industry The industry to filter the jobs by.
     * @return A JSON array containing job information for the specified location and industry.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByLocationIndustry(String location, String industry) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post " +
                    "WHERE location = ? AND industry = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, location);
                preparedStatement.setString(2, industry);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobType = resultSet.getString("job_type");
                        String industryResult = resultSet.getString("industry");
                        String title = resultSet.getString("title");
                        String requirements = resultSet.getString("requirements");
                        String description = resultSet.getString("description");
                        String locationResult = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industryResult);
                        jobListingObject.put("jobType", jobType);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", locationResult);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by location and industry.", e);
        }
    }
    /**
     * Retrieves a JSON array of jobs based on the specified location and job type.
     *
     * @param location The location to filter the jobs by.
     * @param jobType  The job type to filter the jobs by.
     * @return A JSON array containing job information for the specified location and job type.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByLocationType(String location, String jobType) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post " +
                    "WHERE location = ? AND job_type = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, location);
                preparedStatement.setString(2, jobType);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobTypeResult = resultSet.getString("job_type");
                        String industry = resultSet.getString("industry");
                        String title = resultSet.getString("title");
                        String requirements = resultSet.getString("requirements");
                        String description = resultSet.getString("description");
                        String locationResult = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industry);
                        jobListingObject.put("jobType", jobTypeResult);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", locationResult);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by location and job type.", e);
        }
    }
    /**
     * Retrieves a JSON array of jobs based on the specified industry and job type.
     *
     * @param industry The industry to filter the jobs by.
     * @param jobType  The job type to filter the jobs by.
     * @return A JSON array containing job information for the specified industry and job type.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByIndustryType(String industry, String jobType) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post " +
                    "WHERE industry = ? AND job_type = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, industry);
                preparedStatement.setString(2, jobType);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobTypeResult = resultSet.getString("job_type");
                        String industryResult = resultSet.getString("industry");
                        String title = resultSet.getString("title");
                        String requirements = resultSet.getString("requirements");
                        String description = resultSet.getString("description");
                        String location = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industryResult);
                        jobListingObject.put("jobType", jobTypeResult);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", location);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by industry and job type.", e);
        }
    }
    /**
     * Retrieves a JSON array of jobs based on the specified industry.
     *
     * @param industry The industry to filter the jobs by.
     * @return A JSON array containing job information for the specified industry.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByIndustry(String industry) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post " +
                    "WHERE industry = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, industry);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobType = resultSet.getString("job_type");
                        String industryResult = resultSet.getString("industry");
                        String title = resultSet.getString("title");
                        String requirements = resultSet.getString("requirements");
                        String description = resultSet.getString("description");
                        String location = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industryResult);
                        jobListingObject.put("jobType", jobType);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", location);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by industry.", e);
        }
    }
    /**
     * Retrieves a JSON array of jobs based on the specified job type.
     *
     * @param jobType The job type to filter the jobs by.
     * @return A JSON array containing job information for the specified job type.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONArray getJobsByType(String jobType) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post " +
                    "WHERE job_type = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, jobType);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobTypeResult = resultSet.getString("job_type");
                        String industry = resultSet.getString("industry");
                        String title = resultSet.getString("title");
                        String requirements = resultSet.getString("requirements");
                        String description = resultSet.getString("description");
                        String location = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("industry", industry);
                        jobListingObject.put("jobType", jobTypeResult);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", location);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by job type.", e);
        }
    }
    /**
     * Retrieves a JSON array of job posts for a specific employer and job ID.
     *
     * @param employerId The ID of the employer.
     * @param jobId      The ID of the job.
     * @return A JSON object containing information about the specified job post.
     * @throws ExceptionHandler If an error occurs during the retrieval process.
     */
    public JSONObject getJobPostsByEmployer(int employerId, int jobId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, title, industry, job_type, description, requirements, location FROM tbl_job_post WHERE employer_id = ? AND id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);
                preparedStatement.setInt(2, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        String location = resultSet.getString("location");
                        String industry = resultSet.getString("industry");
                        String jobType = resultSet.getString("job_type");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");

                        // Construct a JSON object for the job post
                        JSONObject jobPost = new JSONObject()
                                .put("jobId", id)
                                .put("title", title)
                                .put("industry", industry)
                                .put("jobType", jobType)
                                .put("description", description)
                                .put("requirements", requirements)
                                .put("location", location);

                        return jobPost;
                    } else {
                        // Return an empty JSONObject if no job post is found
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job post by employer ID and job ID.", e);
        }
    }
}
