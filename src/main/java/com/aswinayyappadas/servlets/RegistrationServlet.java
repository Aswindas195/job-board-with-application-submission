package com.aswinayyappadas.servlets;
import com.aswinayyappadas.exceptions.RegistrationException;
import com.aswinayyappadas.services.UserService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegistrationServlet/cust/8273")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService;  // Assuming UserService handles user-related operations
    public RegistrationServlet() {
        this.userService = new UserService();  // Inject dependencies properly in a real application
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("pass");
        String role = "1".equals(request.getParameter("role")) ? "JobSeeker" : "Employer";

        try {
            Class.forName("org.postgresql.Driver"); // Ensure driver is loaded

            int rowsAffected = userService.registerUser(name, email, password, role);

            if (rowsAffected > 0) {
                response.sendRedirect("registration/registration_success.jsp");
            } else {
                setErrorAttributes(request, "Error: Database operation failed.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("registration/registration_error.jsp");
                dispatcher.forward(request, response);
            }
        } catch (RegistrationException e) {
            setErrorAttributes(request, e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("registration/registration_error.jsp");
            dispatcher.forward(request, response);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void setErrorAttributes(HttpServletRequest request, String errorMessage) {
        request.setAttribute("error", true);
        request.setAttribute("errorMessage", errorMessage);
    }
}

