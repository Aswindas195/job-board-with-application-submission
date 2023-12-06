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
import java.util.Iterator;

@WebServlet("/api/edit-job/employer/*")
public class JobEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final MapperService mapperService;
    private final JobListingService jobListingService;
    private final KeyServices keyServices;
    public JobEditServlet() {
        this.jobListingService = new JobListingService();
        this.mapperService = new MapperService();
        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.keyServices = new KeyServices();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract employer details, job ID, and new job details from the request parameters
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 5 || !pathInfo[1].matches("\\d+") || !pathInfo[4].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int employerId = Integer.parseInt(pathInfo[1]);
            int jobId = Integer.parseInt(pathInfo[4]);
            String detailType = pathInfo[2];

            // Parse employerId from the API path
            if (!validityCheckingService.isValidEmployerId(employerId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid employer ID.\"}");
                return;
            }

            // Extract other details from the path
            String email = getServices.getEmailByUserId(employerId);
            String jwtSecretKey = keyServices.getJwtSecretKeyByEmail(email);

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

            // Check if the job is mapped to the employer
            if (!mapperService.isJobMappedToEmployer(jobId, employerId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"status\": \"error\", \"message\": \"Job not mapped to the employer.\"}");
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
            Iterator<String> keys = jsonBody.keys();
            String keyDetailType = "";
            while (keys.hasNext()) {
                 keyDetailType = keys.next();
            }
            // Validate 'detailType' against allowed values
            if (!isValidDetailType(detailType)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid 'detailType' value in the request URL.\"}");
                return;
            }
            // Validate 'detailType' against allowed values
            if (!isValidDetailType(keyDetailType)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid 'detailType' in request body.\"}");
                return;
            }
            if(!detailType.equals(keyDetailType)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid request and 'dataType'.\"}");
                return;
            }
            String updatedData = "";  // Updated data will be assigned based on the detail type
            String message = "Job details updated successfully.";

            // Perform the job details update based on the detail type
            switch (detailType) {
                case "description":
                    String newJobDescription = jsonBody.optString("description");
                    updatedData = jobListingService.updateJobDescription(employerId, jobId, newJobDescription);
                    break;
                case "location":
                    String newLocation = jsonBody.optString("location");
                    updatedData = jobListingService.updateJobLocation(employerId, jobId, newLocation);
                    break;
                case "requirements":
                    String newRequirements = jsonBody.optString("requirements");
                    updatedData = jobListingService.updateJobRequirements(employerId, jobId, newRequirements);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"status\": \"error\", \"message\": \"Invalid detail type.\"}");
                    return;
            }

            // Prepare the JSON response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", message);
            jsonResponse.put("updatedData", updatedData);

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

    private boolean isValidDetailType(String detailType) {
        return "description".equals(detailType) || "location".equals(detailType) || "requirements".equals(detailType);
    }
}
