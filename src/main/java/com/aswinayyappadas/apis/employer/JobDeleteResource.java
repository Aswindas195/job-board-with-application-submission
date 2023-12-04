package com.aswinayyappadas.apis.employer;

import com.aswinayyappadas.exceptions.JobDeleteException;
import com.aswinayyappadas.services.UserService;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/employer/job-delete")
public class JobDeleteResource {

    private final UserService userService;

    public JobDeleteResource() {
        this.userService = new UserService();
    }

    @DELETE
    @Path("/{employerId}/{jobId}")
    public Response deleteJob(
            @PathParam("employerId") int employerId,
            @PathParam("jobId") int jobId) {

        try {
            // Validate employerId
            if (!userService.isValidEmployerId(employerId)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\": \"error\", \"message\": \"Invalid employer ID.\"}")
                        .build();
            }

            // Attempt to delete the job
            userService.deleteJob(employerId, jobId);

            // Return success response
            return Response.status(Response.Status.OK)
                    .entity("{\"status\": \"success\", \"message\": \"Job deleted successfully.\"}")
                    .build();
        } catch (JobDeleteException e) {
            // Return error response for JobDeleteException
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            // Return internal server error response for other exceptions
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"status\": \"error\", \"message\": \"Internal Server Error.\"}")
                    .build();
        }
    }
}
