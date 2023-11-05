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

import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init(ServletConfig config) {
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean logged_in = false;

        if(username != null || password != null) {
            logged_in = login(session, username, password);
        }

        try {
            if (logged_in) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                response.sendError(401, "Unauthorized");
            }
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
    }

    private boolean login(HttpSession session, String username, String password) {
        Optional<User> user = userDAO.getUser(username, password);

        if (user.isPresent()) {
            String userID = user.get().getId().toString();
            session.setAttribute("USERID", userID);
            return true;
        }
        return false;
    }
}
