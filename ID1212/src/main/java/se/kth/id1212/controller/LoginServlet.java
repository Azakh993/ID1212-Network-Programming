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

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init(ServletConfig config) {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ControllerUtil.forward_request(request, response, "/login.jsp");
    }

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
