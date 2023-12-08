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
    public JSONObject getUserById(int userId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT user_name, email, user_type FROM tbl_user WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Assuming you have a User class; adjust accordingly
                        String username = resultSet.getString("user_name");
                        String email = resultSet.getString("email");
                        String userType = resultSet.getString("user_type");

                        // Construct a JSON object

                        return new JSONObject()
                                .put("username", username)
                                .put("email", email)
                                .put("usertype", userType);
                    } else {
                        throw new ExceptionHandler("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving user by ID.", e);
        }
    }

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

    public String getEmailByUserId(int userId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT email FROM tbl_user WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("email");
                    } else {
                        throw new ExceptionHandler("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving email by user ID.", e);
        }
    }

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
    public JSONArray getJobsByLocation(String location) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, industry, job_type, title, description, requirements, location FROM tbl_job_post WHERE location = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, location);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("jobid");
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
    public JSONArray getJobsByTitle(String title) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT id, title, job_type, industry, description, requirements, location FROM tbl_job_post WHERE title = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("id");
                        String jobTitle = resultSet.getString("title");
                        String industry = resultSet.getString("industry");
                        String jobType = resultSet.getString("job_type");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String location = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("title", jobTitle);
                        jobListingObject.put("industry", industry);
                        jobListingObject.put("jobType", jobType);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", location);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new ExceptionHandler("Error retrieving job listings by title.", e);
        }
    }


    public JSONArray getApplicationsByJob(int employerId, int jobId) throws ExceptionHandler {
        try (Connection connection = DbConnector.getConnection()) {
            // Assuming you have a table named applications, adjust the SQL query accordingly
            String sql = "SELECT a.id, a.job_seeker_id, a.date, a.cover_letter, a.resume_file_path, u.user_name, u.email " +
                    "FROM tbl_job_application a " +
                    "JOIN users u ON a.job_seeker_id = u.user_id " +
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
}
