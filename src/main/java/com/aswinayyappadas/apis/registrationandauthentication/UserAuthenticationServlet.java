package com.aswinayyappadas.apis.registrationandauthentication;

import com.aswinayyappadas.services.UserService;
import com.aswinayyappadas.util.TokenGenerator;
import com.aswinayyappadas.util.UserInputValidator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

@WebServlet("/api/login")
public class UserAuthenticationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService;
    private final UserInputValidator userInputValidator;
    private final TokenGenerator tokenGenerator;

    public UserAuthenticationServlet() {
        this.userService = new UserService();
        this.userInputValidator = new UserInputValidator();
        this.tokenGenerator = new TokenGenerator();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read the JSON data from the request body
        StringBuilder jsonBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }

        // Parse JSON data
        JSONObject jsonData = new JSONObject(jsonBody.toString());

        // Validate input
        JSONObject validationErrors = validateInput(jsonData);
        if (validationErrors.length() > 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println(validationErrors.toString());
            return;
        }

        // Retrieve email and password from the JSON data
        String email = jsonData.getString("email");
        String password = jsonData.getString("password");

        // Validate the username and password by checking against the database
        boolean isAuthenticated = userService.authenticateUser(email, password);

        // Send the response based on authentication result
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (isAuthenticated) {
            // Generate a token or session identifier and send it in the response
            String authToken = tokenGenerator.generateToken(email);
            JSONObject successResponse = new JSONObject();
            successResponse.put("status", "success");
            successResponse.put("token", authToken);
            out.println(successResponse.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid username or password");
            out.println(errorResponse.toString());
        }
    }


    private JSONObject validateInput(JSONObject jsonData) {
        JSONObject validationErrors = new JSONObject();

        // Retrieve email and password from the JSON data
        String email = jsonData.optString("email");
        String password = jsonData.optString("password");

        if (!userInputValidator.isValidEmail(email)) {
            validationErrors.put("email", "Invalid or missing email address.");
        }

        return validationErrors;
    }
}
