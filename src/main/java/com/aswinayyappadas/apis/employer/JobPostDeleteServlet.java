package com.aswinayyappadas.apis.employer;

import com.aswinayyappadas.exceptions.JobDeleteException;
import com.aswinayyappadas.services.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/job-delete/employer/*")
public class JobPostDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public JobPostDeleteServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract employerId and jobId from the URL
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 4 || !pathInfo[2].equals("job") || !pathInfo[3].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int employerId = Integer.parseInt(pathInfo[1]);
            int jobId = Integer.parseInt(pathInfo[3]);

            // Validate employerId
            if (!userService.isValidEmployerId(employerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid employer ID.\"}");
                return;
            }

            try {
                // Attempt to delete the job
                userService.deleteJobPost(employerId, jobId);
                // You can add more information in the response if needed
                out.println("{\"status\": \"success\", \"message\": \"Job deleted successfully.\"}");
            } catch (JobDeleteException e) {
                // Job delete exception
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
