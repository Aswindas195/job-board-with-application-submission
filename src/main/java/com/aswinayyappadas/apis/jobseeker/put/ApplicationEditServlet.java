package com.aswinayyappadas.apis.jobseeker;

import com.aswinayyappadas.exceptions.ExceptionHandler;
import com.aswinayyappadas.services.*;
import com.aswinayyappadas.util.jwt.JwtTokenVerifier;
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


@WebServlet("/api/edit-application/jobSeeker/*")
public class ApplicationEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final MapperService mapperService;
    private final ApplicationService applicationService;

    public ApplicationEditServlet() {
        this.applicationService = new ApplicationService();
        this.mapperService = new MapperService();
        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job seeker details, application ID, and new application details from the request parameters
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 5 || !pathInfo[1].matches("\\d+") || !pathInfo[4].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int jobSeekerId = Integer.parseInt(pathInfo[1]);
            int jobId = Integer.parseInt(pathInfo[4]);
            String detailType = pathInfo[2];

            // Parse jobSeekerId from the API path
            if (!validityCheckingService.isValidJobSeekerId(jobSeekerId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid job seeker ID.\"}");
                return;
            }

            // Extract other details from the path
            String email = getServices.getEmailByUserId(jobSeekerId);
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

            // Check if the application is mapped to the job seeker
            if (!mapperService.isApplicationMappedToJobSeeker(jobSeekerId, jobId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"status\": \"error\", \"message\": \"Application not mapped to the job seeker.\"}");
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

            // Convert the JSON body to a JSONObject
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
            String message = "Application details updated successfully.";

            // Perform the application details update based on the detail type
            switch (detailType) {
                case "resumefilepath":
                    String newResumeFilePath = jsonBody.optString("resumefilepath");
                    updatedData = applicationService.updateResumeFilePath(jobSeekerId, jobId, newResumeFilePath);
                    break;
                case "coverletter":
                    String newCoverLetter = jsonBody.optString("coverletter");
                    updatedData = applicationService.updateCoverLetter(jobSeekerId, jobId, newCoverLetter);
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
            // Handle invalid input (non-integer values for jobSeekerId or applicationId)
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
    private boolean isValidDetailType(String detailType) {
        return "resumefilepath".equals(detailType) || "coverletter".equals(detailType);
    }
}
