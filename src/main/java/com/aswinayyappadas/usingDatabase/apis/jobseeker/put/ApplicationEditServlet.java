package com.aswinayyappadas.usingDatabase.apis.jobseeker.put;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.services.*;
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

@WebServlet("/api/jobSeeker/edit-application")
public class ApplicationEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final MapperService mapperService;
    private final ApplicationService applicationService;

    public ApplicationEditServlet() {
        this.applicationService = new ApplicationService();
        this.mapperService = new MapperService();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read the JSON body from the request
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            JSONObject jsonBody = new JSONObject(requestBody.toString());
            int jobId = jsonBody.getInt("jobId");
            int userId = -1;

            // Extract user id from jwt
            String authToken = request.getHeader("Authorization");
            if (authToken != null) {
                userId = jwtTokenVerifier.extractUserId(authToken);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
                return;
            }

            // Parse jobSeekerId from the API path
            if (!validityCheckingService.isValidJobSeekerId(userId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid job seeker ID.\"}");
                return;
            }

            // Check if the application is mapped to the job seeker
            if (!mapperService.isApplicationMappedToJobSeeker(userId, jobId)) {
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
                        applicationService.updateResumeFilePath(userId, jobId, newResumeFilePath);
                        updatedApplicationDataMap.put("resumeFilepath", newResumeFilePath);
                        break;
                    case "coverLetter":
                        String newCoverLetter = jsonBody.optString("coverLetter");
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
        } catch (ExceptionHandler e) {
            // Handle application update exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Handle any unexpected exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
