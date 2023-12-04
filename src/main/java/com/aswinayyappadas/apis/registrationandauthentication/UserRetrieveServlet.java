package com.aswinayyappadas.apis.registrationandauthentication;

import com.aswinayyappadas.exceptions.UserRetrievalException;
import com.aswinayyappadas.services.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/api/users/*")
public class UserRetrieveServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public UserRetrieveServlet() {
        this.userService = new UserService();
    }

    // Handle GET requests to retrieve user information by ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Extract user ID from the request URI
        String[] pathInfo = request.getPathInfo().split("/");
        if (pathInfo.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Error: User ID is required in the request path.");
            return;
        }

        String userId = pathInfo[1];

        try {
            JSONObject userData = userService.getUserById(Integer.parseInt(userId));

            if (userData != null) {
                // Set Content-Type header to application/json
                response.setContentType("application/json");

                // Write the JSON string to the response
                response.getWriter().println(userData.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Error: User not found.");
            }
        } catch (UserRetrievalException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
