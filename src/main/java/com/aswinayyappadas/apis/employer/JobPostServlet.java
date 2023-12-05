package com.aswinayyappadas.apis.employer;// JobPostServlet.java

import com.aswinayyappadas.exceptions.JobPostException;
import com.aswinayyappadas.services.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.sql.SQLException;
import java.util.Base64;

@WebServlet("/api/job-post/employer/*")
public class JobPostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public JobPostServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Verify JWT token

            // Parse employerId from the API path
            int employerId = extractEmployerIdFromPath(request.getPathInfo());
            String email = userService.getEmailByUserId(employerId);
            String authToken = request.getHeader("Authorization");
            if (authToken == null || !verifyJwtToken(authToken, email)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"status\": \"error\", \"message\": \"Unauthorized. Invalid or missing token.\"}");
                return;
            }



            // Read JSON data from the request body
            BufferedReader reader = request.getReader();
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }

            // Parse JSON data
            JSONObject jsonData = new JSONObject(jsonBody.toString());

            // Extract job post data from JSON
            String jobTitle = jsonData.getString("jobTitle");
            String jobDescription = jsonData.getString("jobDescription");
            String requirements = jsonData.getString("requirements");
            String location = jsonData.getString("location");

            try {
                // Attempt to post the job
                userService.postJob(employerId, jobTitle, jobDescription, requirements, location);
                // You can add more information in the response if needed
                out.println("{\"status\": \"success\", \"message\": \"Job posted successfully.\"}");
            } catch (JobPostException e) {
                // Job post exception
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }

    private boolean verifyJwtToken(String token, String email) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String secretKey = userService.getJwtSecretKeyByEmail(email);

            if (secretKey == null) {
                // Handle the case when the secret key is not found for the user
                return false;
            }

            // Decode the Base64 URL encoded secret key
            byte[] decodedSecretKey = Base64.getUrlDecoder().decode(secretKey);

            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(decodedSecretKey)).build()
                    .parseClaimsJws(token).getBody();

            // Additional checks if needed
            // e.g., check claims.get("iss"), claims.get("aud"), claims.get("nbf"), claims.getExpiration(), etc.

            return true;
        } catch (JwtException | SQLException e) {
            // Token verification failed
            e.printStackTrace(); // Log the exception for debugging
            return false;
        }
    }


    private int extractEmployerIdFromPath(String pathInfo) {
        // Extract employerId from the API path
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length == 2 && pathParts[1].matches("\\d+")) {
            return Integer.parseInt(pathParts[1]);
        } else {
            // Handle invalid path or return a sentinel value
            return -1;
        }
    }
}
