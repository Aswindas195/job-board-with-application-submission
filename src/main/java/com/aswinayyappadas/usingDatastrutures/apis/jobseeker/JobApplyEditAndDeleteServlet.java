package com.aswinayyappadas.usingDatastrutures.apis.jobseeker;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatastrutures.services.*;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@WebServlet("/api/ds/job-seeker/application/*")
public class JobApplyEditAndDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final ApplicationService applicationService;
    private final MapperServices mapperServices;

    public JobApplyEditAndDeleteServlet() {
        this.applicationService = new ApplicationService();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.mapperServices = new MapperServices();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job ID from the request URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() < 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL. Missing job ID in the path.\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            int jobId;
            try {
                jobId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID in the path.\"}");
                return;
            }

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

            // Check if the job seeker ID is valid
            if (!validityCheckingService.isValidJobSeekerId(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job seeker ID.\"}");
                return;
            }

            // Check if the job ID is valid
            if (!validityCheckingService.isValidJobId(jobId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID.\"}");
                return;
            }

            // Perform the job application
            JSONObject applicationDetails = applicationService.applyForJob(userId, jobId);

            if (applicationDetails != null) {
                // Include application details in the success response
                JSONObject successResponse = new JSONObject();
                successResponse.put("status", "success");
                successResponse.put("message", "Job application successful");
                successResponse.put("applicationDetails", applicationDetails);

                // Convert the success response to a JSON string and print it
                out.println(successResponse.toString());
            } else {
                out.println("{\"status\": \"error\", \"message\": \"Error applying for the job.\"}");
            }
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job ID from the request URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() < 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL. Missing job ID in the path.\"}");
                return;
            }

            String[] pathParts = pathInfo.split("/");
            int jobId;
            try {
                jobId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID in the path.\"}");
                return;
            }
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

            // Check if the job seeker ID is valid
            if (!validityCheckingService.isValidJobSeekerId(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job seeker ID.\"}");
                return;
            }

            // Check if the job ID is valid
            if (!validityCheckingService.isValidJobId(jobId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID.\"}");
                return;
            }
            // Perform the job deletion
            applicationService.deleteJobApplicationByJobSeekerId(userId, jobId);

            out.println("{\"status\": \"success\", \"message\": \"Job deleted successfully.\"}");
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for jobSeekerId or jobId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job ID from the path
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() < 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL. Missing job ID in the path.\"}");
                return;
            }

            // Remove the leading slash
            String jobIdString = pathInfo.substring(1);

            // Parse job ID from the path
            int jobId;
            try {
                jobId = Integer.parseInt(jobIdString);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID format in the path.\"}");
                return;
            }
            // Read the JSON body from the request
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            JSONObject jsonBody = new JSONObject(requestBody.toString());

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

            // Check if the application is mapped to the job seeker
            if (!mapperServices.isApplicationMappedToJobSeeker(userId, jobId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"status\": \"error\", \"message\": \"Application not mapped to the job seeker.\"}");
                return;
            }

            // Perform the application details update based on the keys in the JSON body
            Map<String, Object> updatedApplicationDataMap = new HashMap<>();
            Iterator<String> applicationKeys = jsonBody.keys();
            while (applicationKeys.hasNext()) {
                String detailType = applicationKeys.next();
                if (detailType.equals("jobId")) continue;
                switch (detailType) {
                    case "resumeFilePath":
                        String newResumeFilePath = jsonBody.optString("resumeFilePath");
                        if(newResumeFilePath == null || newResumeFilePath.isEmpty()) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.println("{\"status\": \"error\", \"message\": \"Missing, null, or empty values in the request body.\"}");
                            return;
                        }
                        applicationService.updateResumeFilePath(userId, jobId, newResumeFilePath);
                        updatedApplicationDataMap.put("resumeFilepath", newResumeFilePath);
                        break;
                    case "coverLetter":
                        String newCoverLetter = jsonBody.optString("coverLetter");
                        if(newCoverLetter == null || newCoverLetter.isEmpty()) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.println("{\"status\": \"error\", \"message\": \"Missing, null, or empty values in the request body.\"}");
                            return;
                        }
                        applicationService.updateCoverLetter(userId, jobId, newCoverLetter);
                        updatedApplicationDataMap.put("coverLetter", newCoverLetter);
                        break;
                    // Add more cases for other keys if needed
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println("{\"status\": \"error\", \"message\": \"Invalid detail type.\"}");
                        return;
                }
            }

            // Prepare the JSON response for application update
            JSONObject applicationUpdateResponse = new JSONObject();
            applicationUpdateResponse.put("status", "success");
            applicationUpdateResponse.put("message", "Job application details updated successfully.");
            applicationUpdateResponse.put("updatedApplicationData", new JSONObject(updatedApplicationDataMap));

            out.println(applicationUpdateResponse.toString());

        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for userId or jobId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (Exception e) {
            // Handle any unexpected exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
