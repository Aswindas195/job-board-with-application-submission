package com.aswinayyappadas.usingDatabase.apis.employer;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.services.GetServices;
import com.aswinayyappadas.usingDatabase.services.JobListingService;
import com.aswinayyappadas.usingDatabase.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/api/employer/job-post")
public class JobPostAndViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final JobListingService jobListingService;
    private final GetServices getServices;


    public JobPostAndViewServlet() {
        this.validityCheckingService = new ValidityCheckingService();
        this.jobListingService = new JobListingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.getServices = new GetServices();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract user ID from JWT
            int userId = -1;
            String authToken = request.getHeader("Authorization");
            if (authToken != null) {
                // Check if authentication fails
                try {
                    userId = jwtTokenVerifier.extractUserId(authToken);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or expired token.\"}");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
                return;
            }

            // Validate employerId
            if (!validityCheckingService.isValidEmployerId(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID.\"}");
                return;
            }

            try {
                // Fetch job posts for the given employerId
                JSONArray jobPosts = getServices.getJobPostsByEmployer(userId);

                // Return the job posts as JSON
                out.println(jobPosts.toString());
            } catch (Exception e) {
                // Handle other exceptions
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read JSON data from the request body
            BufferedReader reader = request.getReader();
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }

            // Parse JSON data
            JSONObject jsonData = new JSONObject(jsonBody.toString());

            // Extract job post data from JSON
            int industry = jsonData.getInt("industry");
            int jobType = jsonData.getInt("jobType");
            String jobTitle = jsonData.getString("jobTitle");
            String jobDescription = jsonData.getString("jobDescription");
            String requirements = jsonData.getString("requirements");
            int location = jsonData.getInt("location");

            // Extract user ID from JWT
            int userId = -1;
            String authToken = request.getHeader("Authorization");
            if (authToken != null) {
                // Check if authentication fails
                try {
                    userId = jwtTokenVerifier.extractUserId(authToken);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or expired token.\"}");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
                return;
            }

            // Validate employerId
            if (!validityCheckingService.isValidEmployerId(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID.\"}");
                return;
            }

            try {
                // Attempt to post the job
                int jobPostId = jobListingService.postJob(userId, industry, jobType, jobTitle, jobDescription, requirements, location);
                // You can add more information in the response if needed
                out.println("{\"status\": \"success\", \"message\": \"Job posted successfully.\", \"jobPostId\": " + jobPostId + "}");
            } catch (ExceptionHandler e) {
                // Job post exception
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            // Handle any unexpected exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
