package com.aswinayyappadas.apis.registrationandauthentication;
import com.aswinayyappadas.services.*;
import com.aswinayyappadas.util.UserInputValidator;

import com.aswinayyappadas.util.jwt.JwtTokenVerifier;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@WebServlet("/api/users/login")
public class UserAuthenticationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserManager userManager;
    private final GetServices getServices;
    private final UserInputValidator userInputValidator;
    private final KeyServices keyServices;
    public UserAuthenticationServlet() {
        this.userManager = new UserManager();
        this.getServices = new GetServices();
        this.userInputValidator = new UserInputValidator();
        this.keyServices = new KeyServices();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder jsonBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }

        JSONObject jsonData = new JSONObject(jsonBody.toString());
        JSONObject validationErrors = validateInput(jsonData);

        if (validationErrors.length() > 0) {
            sendErrorResponse(response, validationErrors, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String email = jsonData.getString("email");
        String password = jsonData.getString("password");

       int userId = userManager.authenticateUserAndGetId(email, password);
       String userType = getServices.getUserTypeByUserId(userId);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (userId != -1) {
            try {
                String authToken = generateJwtToken(email);
                JSONObject successResponse = new JSONObject();
                successResponse.put("status", "Authentication success");
                successResponse.put("user id", userId);
                successResponse.put("user type", userType);
                successResponse.put("token", authToken);
                out.println(successResponse.toString());
            } catch (Exception e) {
                sendErrorResponse(response, "Failed to generate JWT token", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            sendErrorResponse(response, "Invalid username or password", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, Object message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        response.getWriter().println(errorResponse.toString());
    }

    private JSONObject validateInput(JSONObject jsonData) {
        JSONObject validationErrors = new JSONObject();

        String email = jsonData.optString("email");
        String password = jsonData.optString("password");

        if (!userInputValidator.isValidEmail(email)) {
            validationErrors.put("email", "Invalid or missing email address.");
        }

        return validationErrors;
    }

    private String generateJwtToken(String email) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretKeyString = Base64.getUrlEncoder().withoutPadding().encodeToString(key.getEncoded());

        try {
           keyServices.storeSecretKeyByEmail(email, secretKeyString);
        } catch (SQLException e) {
            // Handle the exception appropriately (e.g., log it)
            e.printStackTrace();
        }

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 days
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
