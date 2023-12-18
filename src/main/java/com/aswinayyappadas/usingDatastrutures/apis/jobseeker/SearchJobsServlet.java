package com.aswinayyappadas.usingDatastrutures.apis.jobseeker;



import com.aswinayyappadas.usingDatastrutures.services.GetServices;
import com.aswinayyappadas.usingDatastrutures.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/ds/job-seeker/search-job")
public class SearchJobsServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;


    public SearchJobsServlet() {

        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract parameters from the query
            String location = request.getParameter("location");
            String industry = request.getParameter("industry");
            String jobType = request.getParameter("jobType");

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

            // Check if jobseeker ID is valid
            if (!validityCheckingService.isValidJobSeekerId(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid jobseeker ID.\"}");
                return;
            }

            JSONArray jsonArray;

            // Perform job search based on different combinations of parameters
            if (location != null && industry != null && jobType != null) {
                // Search by all parameters
                jsonArray = getServices.getJobsByLocationIndustryType(location, industry, jobType);
            } else if (location != null && industry != null) {
                // Search by location and industry
                jsonArray = getServices.getJobsByLocationIndustry(location, industry);
            } else if (location != null && jobType != null) {
                // Search by location and job type
                jsonArray = getServices.getJobsByLocationType(location, jobType);
            } else if (industry != null && jobType != null) {
                // Search by industry and job type
                jsonArray = getServices.getJobsByIndustryType(industry, jobType);
            } else if (location != null) {
                // Search by location only
                jsonArray = getServices.getJobsByLocation(location);
            } else if (industry != null) {
                // Search by industry only
                jsonArray = getServices.getJobsByIndustry(industry);
            } else if (jobType != null) {
                // Search by job type only
                jsonArray = getServices.getJobsByType(jobType);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Missing search parameters.\"}");
                return;
            }

            // Send the JSON array as the response
            out.println(jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal server error.\"}");
        }
    }
}

