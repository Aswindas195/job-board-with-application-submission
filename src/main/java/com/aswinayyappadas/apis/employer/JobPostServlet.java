package com.aswinayyappadas.apis.employer;

import com.aswinayyappadas.exceptions.JobPostException;
import com.aswinayyappadas.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

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

            // Extract employerId from the URL
            String[] pathInfo = request.getPathInfo().split("/");
            System.out.println("pathInfo: " + Arrays.toString(pathInfo));
            if (pathInfo.length > 2 || !pathInfo[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int employerId = Integer.parseInt(pathInfo[1]);

            // Validate employerId
            if (!userService.isValidEmployerId(employerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID.\"}");
                return;
            }

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
}
