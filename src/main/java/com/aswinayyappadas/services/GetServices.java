package com.aswinayyappadas.services;

import com.aswinayyappadas.dbconnection.DbConnector;
import com.aswinayyappadas.exceptions.JobRetrievalException;
import com.aswinayyappadas.exceptions.UserRetrievalException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetServices {
    public String getJwtSecretKeyByEmail(String email) throws SQLException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jwt_secret_key FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("jwt_secret_key");
                    }
                }
            }
        }

        // Return null or handle accordingly based on your requirements
        return null;
    }

    public JSONObject getUserById(int userId) throws UserRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT username, email, usertype FROM users WHERE userid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Assuming you have a User class; adjust accordingly
                        String username = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String userType = resultSet.getString("usertype");

                        // Construct a JSON object

                        return new JSONObject()
                                .put("username", username)
                                .put("email", email)
                                .put("usertype", userType);
                    } else {
                        throw new UserRetrievalException("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new UserRetrievalException("Error retrieving user by ID.", e);
        }
    }

    public JSONArray getJobPostsByEmployer(int employerId) throws JobRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jobid, title, description, requirements, location FROM joblistings WHERE employerid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobPosts = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("jobid");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String location = resultSet.getString("location");

                        // Construct a JSON object for each job post
                        JSONObject jobPost = new JSONObject()
                                .put("jobId", jobId)
                                .put("title", title)
                                .put("description", description)
                                .put("requirements", requirements)
                                .put("location", location);

                        jobPosts.put(jobPost);
                    }

                    return jobPosts; // Return the array, even if it's empty
                }
            }
        } catch (SQLException e) {
            throw new JobRetrievalException("Error retrieving job posts by employer ID.", e);
        }
    }

    public String getEmailByUserId(int userId) throws UserRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT email FROM users WHERE userid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("email");
                    } else {
                        throw new UserRetrievalException("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new UserRetrievalException("Error retrieving email by user ID.", e);
        }
    }

    public String getUserTypeByUserId(int userId) {
        // This assumes you have a database connection. Replace "yourDatabaseConnection" with your actual database connection.
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT usertype FROM users WHERE userid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("usertype");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
        }

        return null; // Return null if user type is not found
    }
    public JSONArray getAppliedJobsByJobSeeker(int jobSeekerId) throws JobRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT j.jobid, j.title, j.description, j.requirements, j.location, a.submissiondate, a.coverletter, a.resumefilepath " +
                    "FROM joblistings j " +
                    "JOIN applications a ON j.jobid = a.jobid " +
                    "WHERE a.jobseekerid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray appliedJobs = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("jobid");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String location = resultSet.getString("location");
                        String submissionDate = resultSet.getString("submissiondate");
                        String coverLetter = resultSet.getString("coverletter");
                        String resumeFilePath = resultSet.getString("resumefilepath");

                        // Construct a JSON object for each applied job
                        JSONObject appliedJob = new JSONObject()
                                .put("jobId", jobId)
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
            throw new JobRetrievalException("Error retrieving applied jobs by job seeker ID.", e);
        }
    }

    public JSONArray getAllJobsFromListings() throws SQLException {
        JSONArray jobListingsArray = new JSONArray();

        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jobid, title, description, location FROM joblistings";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int jobId = resultSet.getInt("jobid");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    String location = resultSet.getString("location");

                    JSONObject jobListingObject = new JSONObject();
                    jobListingObject.put("jobId", jobId);
                    jobListingObject.put("title", title);
                    jobListingObject.put("description", description);
                    jobListingObject.put("location", location);

                    jobListingsArray.put(jobListingObject);
                }
            }
        }

        return jobListingsArray;
    }
    public JSONArray getJobsByLocation(String location) throws JobRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jobid, title, description, requirements FROM joblistings WHERE location = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, location);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("jobid");
                        String title = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("title", title);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new JobRetrievalException("Error retrieving job listings by location.", e);
        }
    }
    public JSONArray getJobsByTitle(String title) throws JobRetrievalException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jobid, title, description, requirements, location FROM joblistings WHERE title = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    JSONArray jobListingsArray = new JSONArray();

                    while (resultSet.next()) {
                        int jobId = resultSet.getInt("jobid");
                        String jobTitle = resultSet.getString("title");
                        String description = resultSet.getString("description");
                        String requirements = resultSet.getString("requirements");
                        String location = resultSet.getString("location");

                        JSONObject jobListingObject = new JSONObject();
                        jobListingObject.put("jobId", jobId);
                        jobListingObject.put("title", jobTitle);
                        jobListingObject.put("description", description);
                        jobListingObject.put("requirements", requirements);
                        jobListingObject.put("location", location);

                        jobListingsArray.put(jobListingObject);
                    }

                    return jobListingsArray;
                }
            }
        } catch (SQLException e) {
            throw new JobRetrievalException("Error retrieving job listings by title.", e);
        }
    }


}
