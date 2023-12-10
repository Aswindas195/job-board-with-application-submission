package com.aswinayyappadas.usingDatastrutures.apis.jobseeker.get;

import com.aswinayyappadas.usingDatastrutures.services.GetServices;
import com.aswinayyappadas.usingDatastrutures.services.KeyServices;
import com.aswinayyappadas.usingDatastrutures.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/ds/view-all-jobs/jobSeeker/*")
public class ViewAllJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final KeyServices keyServices;


    public ViewAllJobsServlet() {
        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.keyServices = new KeyServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job seeker ID from the request parameters
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 2 || !pathInfo[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int jobSeekerId = Integer.parseInt(pathInfo[1]);

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
//            String authToken = request.getHeader("Authorization");
//            if (authToken == null || !jwtTokenVerifier.verifyToken(authToken, email, jwtSecretKey)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
//                return;
//            }

            // Retrieve all jobs from job listings
            JSONArray allJobsArray = getServices.getAllJobsFromListings();

            // Send the list of all jobs as a JSON response
            out.println(allJobsArray.toString());
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
