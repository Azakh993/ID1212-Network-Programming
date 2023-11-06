package se.kth.id1212.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.id1212.util.ExceptionLogger;

public class ControllerUtil {
    public static String validate_login_state(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String userID = (String) session.getAttribute("USERID");

        if (userID == null) {
            redirect_request(request, response, "/login");
            return null;
        } else {
            return userID;
        }
    }

    public static void forward_request(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
    }

    public static void redirect_request(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            response.sendRedirect(request.getContextPath() + path);
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
    }
}
