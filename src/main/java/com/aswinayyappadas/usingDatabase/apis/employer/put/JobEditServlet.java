package com.aswinayyappadas.usingDatabase.apis.employer.put;

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

@WebServlet("/api/employer/edit-job")
public class JobEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;

    private final MapperService mapperService;
    private final JobListingService jobListingService;

    public JobEditServlet() {
        this.jobListingService = new JobListingService();
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
            // Extreact user id from jwt
            String authToken = request.getHeader("Authorization");
            if (authToken != null) {
                userId = jwtTokenVerifier.extractUserId(authToken);
            }
            else if(userId == -1){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
                return;
            }

            // Parse employerId from the API path
            if (!validityCheckingService.isValidEmployerId(userId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid employer ID.\"}");
                return;
            }

            // Check if the job is mapped to the employer
            if (!mapperService.isJobMappedToEmployer(jobId, userId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"status\": \"error\", \"message\": \"Job not mapped to the employer.\"}");
                return;
            }

            // Perform the job details update based on the keys in the JSON body
            Map<String, Object> updatedDataMap = new HashMap<>();
            Iterator<String> keys = jsonBody.keys();
            while (keys.hasNext()) {
                String detailType = keys.next();
                if(detailType.equals("jobId")) continue;
                switch (detailType) {
                    case "description":
                        String newJobDescription = jsonBody.optString("description");
                        updatedDataMap.put("description", jobListingService.updateJobDescription(userId, jobId, newJobDescription));
                        break;
                    case "location":
                        int newLocation = jsonBody.optInt("location");
                        updatedDataMap.put("location", jobListingService.updateJobLocation(userId, jobId, newLocation));
                        break;
                    case "requirements":
                        String newRequirements = jsonBody.optString("requirements");
                        updatedDataMap.put("requirements", jobListingService.updateJobRequirements(userId, jobId, newRequirements));
                        break;
                    case "jobType":
                        int newJobType = jsonBody.optInt("jobType");
                        updatedDataMap.put("jobType", jobListingService.updateJobType(userId, jobId, newJobType));
                        break;
                    case "industry":
                        int newIndustry = jsonBody.optInt("industry");
                        updatedDataMap.put("industry", jobListingService.updateIndustry(userId, jobId, newIndustry));
                        break;
                    // Add more cases for other keys if needed
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println("{\"status\": \"error\", \"message\": \"Invalid detail type.\"}");
                        return;
                }
            }

// Prepare the JSON response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Job details updated successfully.");
            jsonResponse.put("updatedData", new JSONObject(updatedDataMap));

            out.println(jsonResponse.toString());
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for employerId or jobId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (ExceptionHandler e) {
            // Handle job update exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Handle any unexpected exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }

}
