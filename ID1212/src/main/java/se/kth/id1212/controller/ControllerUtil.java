package se.kth.id1212.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.id1212.util.ExceptionLogger;

/**
 * The class contains static methods for validating login state,
 * redirecting requests, and forwarding requests to specific paths.
 */
public class ControllerUtil {


    /**
     * Validates the user's login state by checking the presence of a user ID in the session.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @return The user ID if the user is logged in, or null if not logged in.
     */
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


    /**
     * Redirects the request to the specified path.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @param path     The path to redirect the request to.
     */
    public static void redirect_request(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            response.sendRedirect(request.getContextPath() + path);
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
    }

    /**
     * Forwards the request to the specified path.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @param path     The path to forward the request to.
     */
    public static void forward_request(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
    }
}
