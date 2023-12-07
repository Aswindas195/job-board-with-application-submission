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

@WebServlet("/api/ds/search-job/jobSeeker/*")
public class SearchJobsServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final KeyServices keyServices;

    public SearchJobsServlet() {

        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.keyServices = new KeyServices();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract jobseeker id and search type from the path info
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 4 || !pathInfo[1].matches("\\d+") || (!pathInfo[2].equals("location") && !pathInfo[2].equals("title"))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int jobSeekerId = Integer.parseInt(pathInfo[1]);
            String searchType = pathInfo[2];
            String searchValue = pathInfo[3];

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
            // Check if jobseeker ID is valid
            if (!validityCheckingService.isValidJobSeekerId(jobSeekerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid jobseeker ID.\"}");
                return;
            }
            JSONArray jsonArray;
            if (validityCheckingService.isValidJobSeekerId(jobSeekerId)) {
                switch (searchType) {
                    case "location":
                        String location = searchValue;
                        if (location != null) {
                            jsonArray = getServices.getJobsByLocation(location);
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.println("{\"status\": \"error\", \"message\": \"Missing 'location' parameter.\"}");
                            return;
                        }
                        break;
                    case "title":
                        String title = searchValue;
                        if (title != null) {
                            jsonArray = getServices.getJobsByTitle(title);
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.println("{\"status\": \"error\", \"message\": \"Missing 'title' parameter.\"}");
                            return;
                        }
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println("{\"status\": \"error\", \"message\": \"Invalid search type.\"}");
                        return;
                }

                // Send the JSON array as the response
                out.println(jsonArray.toString());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception according to your application's error handling strategy
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal server error.\"}");
        }
    }
}

