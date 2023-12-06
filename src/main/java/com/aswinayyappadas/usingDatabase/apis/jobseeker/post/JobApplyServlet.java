package com.aswinayyappadas.usingDatabase.apis.jobseeker.post;

import com.aswinayyappadas.usingDatabase.services.ApplicationService;
import com.aswinayyappadas.usingDatabase.services.GetServices;
import com.aswinayyappadas.usingDatabase.services.KeyServices;
import com.aswinayyappadas.usingDatabase.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/job-apply/jobSeeker/*")
public class JobApplyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final ApplicationService applicationService;
    private final KeyServices keyServices;

    public JobApplyServlet() {
        this.applicationService = new ApplicationService();
        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.keyServices = new KeyServices();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job seeker details and job ID from the request parameters
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 4 || !pathInfo[1].matches("\\d+") || !pathInfo[3].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int jobSeekerId = Integer.parseInt(pathInfo[1]);
            int jobId = Integer.parseInt(pathInfo[3]);

            // Check if the job seeker ID is valid
            if (!validityCheckingService.isValidJobSeekerId(jobSeekerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job seeker ID.\"}");
                return;
            }

            // Extract other details from the path
            String email = getServices.getEmailByUserId(jobSeekerId);
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

            // Check if the job ID is valid
            if (!validityCheckingService.isValidJobId(jobId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID.\"}");
                return;
            }

            // Perform the job application
            boolean applicationSuccessful = applicationService.applyForJob(jobSeekerId, jobId);

            if (applicationSuccessful) {
                out.println("{\"status\": \"success\", \"message\": \"Job application successful.\"}");
            } else {
                out.println("{\"status\": \"error\", \"message\": \"Error applying for the job.\"}");
            }
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for jobSeekerId or jobId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
