package com.aswinayyappadas.usingDatastrutures.apis.employer.get;

import com.aswinayyappadas.usingDatastrutures.services.GetServices;
import com.aswinayyappadas.usingDatastrutures.services.KeyServices;
import com.aswinayyappadas.usingDatastrutures.services.MapperServices;
import com.aswinayyappadas.usingDatastrutures.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/ds/view-applications/employer/*")
public class ViewApplicationsForJobPostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final KeyServices keyServices;
    private final MapperServices mapperService;

    public ViewApplicationsForJobPostServlet() {
        this.validityCheckingService = new ValidityCheckingService();
        this.getServices = new GetServices();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.keyServices = new KeyServices();
        this.mapperService = new MapperServices();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract employerId and jobId from the URL
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 4 || !pathInfo[1].matches("\\d+") || !pathInfo[3].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int employerId = Integer.parseInt(pathInfo[1]);
            int jobId = Integer.parseInt(pathInfo[3]);

            // Validate employerId and jobId
            if (!validityCheckingService.isValidEmployerId(employerId) || !validityCheckingService.isValidJobId(jobId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID or job ID.\"}");
                return;
            }

            // Check if employer is mapped to the job
            if (!mapperService.isEmployerMappedToJob(employerId, jobId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Employer is not mapped to the job.\"}");
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
//            String authToken = request.getHeader("Authorization");
//            if (authToken == null || !jwtTokenVerifier.verifyToken(authToken, email, jwtSecretKey)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
//                return;
//            }

            try {
                // Fetch applications for the given employerId and jobId
                JSONArray applications = getServices.getApplicationsByJob(employerId, jobId);

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
