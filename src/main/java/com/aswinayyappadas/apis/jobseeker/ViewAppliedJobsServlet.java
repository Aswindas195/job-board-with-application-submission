package com.aswinayyappadas.apis.jobseeker;

import com.aswinayyappadas.services.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/view-applied-jobs/jobSeeker/*")
public class ViewAppliedJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public ViewAppliedJobsServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job seeker ID from the request parameters
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 2 || !pathInfo[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int jobSeekerId = Integer.parseInt(pathInfo[1]);

            // Check if the job seeker ID is valid
            if (!userService.isValidJobSeekerId(jobSeekerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job seeker ID.\"}");
                return;
            }

            // Retrieve the applied jobs for the job seeker
            String appliedJobs = userService.getAppliedJobsByJobSeeker(jobSeekerId).toString();

            // Send the list of applied jobs as a JSON response
            out.println(appliedJobs);
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for jobSeekerId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
