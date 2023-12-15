package com.aswinayyappadas.usingDatabase.apis.employer;

import com.aswinayyappadas.usingDatabase.services.GetServices;
import com.aswinayyappadas.usingDatabase.services.MapperService;
import com.aswinayyappadas.usingDatabase.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/employer/job-applications")
public class ViewApplicationsForJobPostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;

    private final MapperService mapperService;

    public ViewApplicationsForJobPostServlet() {
        this.validityCheckingService = new ValidityCheckingService();
        this.getServices = new GetServices();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.mapperService = new MapperService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract employerId and jobId from the query parameter
            int jobId = Integer.parseInt(request.getParameter("jobId"));

            // Get query parameter for employer id
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
            // Validate employerId and jobId
            if (!validityCheckingService.isValidEmployerId(userId) || !validityCheckingService.isValidJobId(jobId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID or job ID.\"}");
                return;
            }

            // Check if employer is mapped to the job
            if (!mapperService.isJobMappedToEmployer(jobId, userId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Employer is not mapped to the job.\"}");
                return;
            }


            try {
                // Fetch applications for the given employerId and jobId
                JSONArray applications = getServices.getApplicationsByJob(userId, jobId);

                // Return the applications as JSON
                out.println(applications.toString());
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
}
