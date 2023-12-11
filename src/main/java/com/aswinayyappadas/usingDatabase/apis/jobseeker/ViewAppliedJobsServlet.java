package com.aswinayyappadas.usingDatabase.apis.jobseeker;


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

@WebServlet("/api/job-seeker/application")
public class ViewAppliedJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final MapperService mapperService;
    private final ApplicationService applicationService;
    public ViewAppliedJobsServlet() {
        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.mapperService = new MapperService();
        this.applicationService = new ApplicationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            int userId = -1;
            // Extract user id from jwt
            String authToken = request.getHeader("Authorization");
            if (authToken != null) {
                userId = jwtTokenVerifier.extractUserId(authToken);
            }
            else if(userId == -1){
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

            // Retrieve the applied jobs for the job seeker
            String appliedJobs = getServices.getAppliedJobsByJobSeeker(userId).toString();

            // Send the list of applied jobs as a JSON response
            out.println(appliedJobs);
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for jobSeekerId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
