package com.aswinayyappadas.apis.employer;

import com.aswinayyappadas.services.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/view-job-posts/employer/*")
public class ViewJobPostsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public ViewJobPostsServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract employerId from the URL
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 2 || !pathInfo[1].matches("\\d+")) {
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
                // Fetch job posts for the given employerId
                JSONArray jobPosts = userService.getJobPostsByEmployer(employerId);

                // Return the job posts as JSON
                out.println(jobPosts.toString());
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
