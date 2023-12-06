package com.aswinayyappadas.apis.employer.post;

import com.aswinayyappadas.exceptions.ExceptionHandler;
import com.aswinayyappadas.services.GetServices;
import com.aswinayyappadas.services.JobListingService;
import com.aswinayyappadas.services.ValidityCheckingService;
import com.aswinayyappadas.util.jwt.JwtTokenVerifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/job-post/employer/*")
public class JobPostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final JobListingService jobListingService;

    public JobPostServlet() {
        this.validityCheckingService = new ValidityCheckingService();
        this.getServices = new GetServices();
        this.jobListingService = new JobListingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract employerId from the URL
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 2 || !pathInfo[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }
            int employerId = Integer.parseInt(pathInfo[1]);

            // Validate employerId
            if (!validityCheckingService.isValidEmployerId(employerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID.\"}");
                return;
            }

            // Extract other details from the path
            String email = getServices.getEmailByUserId(employerId);
            String jwtSecretKey = getServices.getJwtSecretKeyByEmail(email);

            // Check if jwtSecretKey is null
            if (jwtSecretKey == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. JWT secret key not found.\"}");
                return;
            }

            // Read JWT token from the Authorization header
            String authToken = request.getHeader("Authorization");
            if (authToken == null || !jwtTokenVerifier.verifyToken(authToken, email, jwtSecretKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
                return;
            }

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
            String jobTitle = jsonData.getString("jobTitle");
            String jobDescription = jsonData.getString("jobDescription");
            String requirements = jsonData.getString("requirements");
            String location = jsonData.getString("location");

            try {
                // Attempt to post the job
                int jobPostId = jobListingService.postJob(employerId, jobTitle, jobDescription, requirements, location);
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
