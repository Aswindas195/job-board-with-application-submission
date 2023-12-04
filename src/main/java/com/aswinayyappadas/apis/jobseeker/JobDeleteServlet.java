package com.aswinayyappadas.apis.jobseeker;

import com.aswinayyappadas.exceptions.JobDeleteException;
import com.aswinayyappadas.services.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/job-delete/jobSeeker/*")
public class JobDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public JobDeleteServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract job seeker ID and job ID from the request path
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 4 || !pathInfo[1].matches("\\d+") || !pathInfo[3].matches("\\d+") || !pathInfo[2].equals("job") ) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }

            int jobSeekerId = Integer.parseInt(pathInfo[1]);
            int jobId = Integer.parseInt(pathInfo[3]);

            // Check if the job seeker ID is valid
            if (!userService.isValidJobSeekerId(jobSeekerId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job seeker ID.\"}");
                return;
            }

            // Check if the job ID is valid
            if (!userService.isValidJobId(jobId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid job ID.\"}");
                return;
            }
            // Perform the job deletion
            userService.deleteJobApplication(jobSeekerId, jobId);

            out.println("{\"status\": \"success\", \"message\": \"Job deleted successfully.\"}");
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer values for jobSeekerId or jobId)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid input format.\"}");
        } catch (JobDeleteException e) {
            // Handle custom exception for job deletion
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}");
        }
    }
}
