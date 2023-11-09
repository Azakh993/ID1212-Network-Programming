package se.kth.id1212.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.id1212.integration.UserDAOImpl;
import se.kth.id1212.model.User;
import se.kth.id1212.model.UserDAO;
import se.kth.id1212.util.ExceptionLogger;

/**
 * This class extends HttpServlet and is responsible for managing
 * user login requests, validating user credentials, and handling session management.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    /**
     * Initializes the servlet during which it creates an instance of UserDAO.
     *
     * @param config The ServletConfig object containing configuration information.
     */
    @Override
    public void init(ServletConfig config) {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Handles GET requests to the login page and forwards the request to the login view.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ControllerUtil.forward_request(request, response, "/login.jsp");
    }

    /**
     * Handles POST requests for user login, validates user credentials,
     * sets the user ID in the session, and redirects to the dashboard on success.
     * Sends an error response if the login attempt fails.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        login(request);
        String userID = ControllerUtil.validate_login_state(request, response);

        if (userID != null) {
            ControllerUtil.redirect_request(request, response, "/dashboard");
        } else {
            try {
                response.sendError(401, "Unauthorized");
            } catch (Exception exception) {
                ExceptionLogger.log(exception);
            }
        }
    }

    /**
     * Performs user authentication by retrieving user credentials from the request,
     * querying the UserDAO for user information, and setting the user ID in the session if valid.
     *
     * @param request The HttpServletRequest object containing user credentials.
     */
    private void login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username != null || password != null) {
            User user = this.userDAO.getUser(username, password);

            if (user != null) {
                String userID = user.id().toString();
                HttpSession session = request.getSession();
                session.setAttribute("USERID", userID);
            }
        }
    }
}
