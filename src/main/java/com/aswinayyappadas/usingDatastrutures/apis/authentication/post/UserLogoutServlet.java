package com.aswinayyappadas.usingDatastrutures.apis.authentication.post;

import com.aswinayyappadas.usingDatastrutures.services.GetServices;
import com.aswinayyappadas.usingDatastrutures.services.KeyServices;
import com.aswinayyappadas.usingDatastrutures.services.UserManager;
import com.aswinayyappadas.usingDatastrutures.services.ValidityCheckingService;
import com.aswinayyappadas.usingDatabase.util.jwt.JwtTokenVerifier;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/ds/users/logout/*")
public class UserLogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserManager userManager;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final GetServices getServices;
    private final ValidityCheckingService validityCheckingService;
    private final KeyServices keyServices;

    public UserLogoutServlet() {
        this.userManager = new UserManager();
        this.getServices = new GetServices();
        this.jwtTokenVerifier = new JwtTokenVerifier();
        this.validityCheckingService = new ValidityCheckingService();
        this.keyServices = new KeyServices();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract userId from the URL
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 2 || !pathInfo[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }
            int userId = Integer.parseInt(pathInfo[1]);

            // Validate userId
            if (!validityCheckingService.isValidUserId(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid User ID.\"}");
                return;
            }

            // Extract other details from the path
            String email = getServices.getEmailByUserId(userId);
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

            try {
                // Attempt to logout the user
                boolean logoutSuccessful = userManager.logoutUser(userId);

                if (logoutSuccessful) {
                    // Logout successful
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "User logged out successfully.");
                    out.println(jsonResponse.toString());
                } else {
                    // Database operation failed
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("{\"status\": \"error\", \"message\": \"Database operation failed.\"}");
                }
            } catch (Exception e) {
                // Handle other exceptions if necessary
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"status\": \"error\", \"message\": \"Internal server error.\"}");
            }
        } catch (Exception e) {
            // Handle other exceptions if necessary
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid request.\"}");
        }
    }
}
