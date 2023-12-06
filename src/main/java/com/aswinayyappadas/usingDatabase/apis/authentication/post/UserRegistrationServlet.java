package com.aswinayyappadas.usingDatabase.apis.authentication.post;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.services.UserManager;
import com.aswinayyappadas.usingDatabase.util.user.UserInputValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/api/users/register")
public class UserRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserManager userManager;

    private final UserInputValidator userInputValidator;


    // Default constructor for servlet container
    public UserRegistrationServlet() {
        this.userManager = new UserManager();
        this.userInputValidator = new UserInputValidator();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Set content type for JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read JSON data from the request body
            BufferedReader reader = request.getReader();
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }

            // Parse JSON data
            JSONObject jsonData = new JSONObject(jsonBody.toString());

            // Extract user data from JSON
            String username = jsonData.getString("username");
            String email = jsonData.getString("email");
            String password = jsonData.getString("password");
            String usertype = jsonData.getString("usertype");

            // Validate user input
            JSONObject validationErrors = new JSONObject();

            if (!userInputValidator.isValidName(username)) {
                validationErrors.put("username", "Invalid or missing username.");
            }
            if (!userInputValidator.isValidEmail(email)) {
                validationErrors.put("email", "Invalid or missing email address.");
            }
            if (!userInputValidator.isValidPassword(password)) {
                validationErrors.put("password", "Invalid or missing password. Password must be at least 8 characters.");
            }
            if (!userInputValidator.isValidUserType(usertype)) {
                validationErrors.put("usertype", "Invalid or missing user type. Accepted values are 'Job_Seeker' or 'Employer'.");
            }

            if (!validationErrors.isEmpty()) {
                handleInvalidInput(response, validationErrors);
                return;
            }

            try {
                // Attempt to register the user
                int userId = userManager.registerUser(username, email, password, usertype);

                if (userId > 0) {
                    // Registration successful
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "User registered successfully.");
                    jsonResponse.put("userId", userId);
                    out.println(jsonResponse.toString());
                } else {
                    // Database operation failed
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("{\"status\": \"error\", \"message\": \"Database operation failed.\"}");
                }
            } catch (ExceptionHandler e) {
                // User registration exception
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (JSONException e) {
            // JSON parsing exception
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid JSON format.\"}");
        }
    }

    // Helper method to handle invalid input with specific error message
    private void handleInvalidInput(HttpServletResponse response, JSONObject validationErrors)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter out = response.getWriter();
        out.println(validationErrors.toString());
    }
}
