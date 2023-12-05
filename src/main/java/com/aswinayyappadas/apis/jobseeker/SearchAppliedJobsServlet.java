package com.aswinayyappadas.apis.jobseeker;
package com.aswinayyappadas.apis.jobseeker;

import com.aswinayyappadas.exceptions.ApplicationUpdateException;
import com.aswinayyappadas.services.*;
import com.aswinayyappadas.util.jwt.JwtTokenVerifier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;
@WebServlet("/api/search-job-by/jobSeeker/*")
public class SearchAppliedJobsServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final ValidityCheckingService validityCheckingService;
    private final GetServices getServices;
    private final MapperService mapperService;
    private final ApplicationService applicationService;

    public SearchAppliedJobsServlet() {
        this.applicationService = new ApplicationService();
        this.mapperService = new MapperService();
        this.getServices = new GetServices();
        this.validityCheckingService = new ValidityCheckingService();
        this.jwtTokenVerifier = new JwtTokenVerifier();
    }

    @Override
    protected doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Extract jobseeker id and search type from the path info
            String[] pathInfo = request.getPathInfo().split("/");
            if (pathInfo.length != 2 || !pathInfo[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"status\": \"error\", \"message\": \"Invalid URL format.\"}");
                return;
            }
        }
    }
}
