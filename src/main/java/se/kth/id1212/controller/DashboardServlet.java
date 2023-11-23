package se.kth.id1212.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.id1212.integration.QuizDAOImpl;
import se.kth.id1212.integration.ResultDAOImpl;
import se.kth.id1212.model.Quiz;
import se.kth.id1212.model.QuizDAO;
import se.kth.id1212.model.Result;
import se.kth.id1212.model.ResultDAO;

import java.util.HashMap;

/**
 * This class is responsible for managing user requests related to the dashboard,
 * including displaying quiz results and redirecting users to the quiz page.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private QuizDAO<Quiz> quizDAO;
    private ResultDAO<Result> resultDAO;

    /**
     * Initializes the servlet during which it creates instances of QuizDAO and ResultDAO.
     *
     * @param config The ServletConfig object containing configuration information.
     */
    public void init(ServletConfig config) {
        this.quizDAO = new QuizDAOImpl();
        this.resultDAO = new ResultDAOImpl();
    }

    /**
     * Handles GET requests to the dashboard, validates user login state,
     * retrieves quiz and result data, and forwards the request to the dashboard view.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userID = ControllerUtil.validate_login_state(request, response);

        if (userID != null) {
            HashMap<Quiz, Integer> quizResultMap = getDashboardData(Integer.valueOf(userID));
            request.setAttribute("quizResultMap", quizResultMap);
            ControllerUtil.forward_request(request, response, "/dashboard.jsp");
        }
    }


    /**
     * Retrieves quiz and result data for the user's dashboard.
     *
     * @param userID The unique identifier of the user.
     * @return A HashMap mapping Quiz objects to their corresponding scores.
     */
    private HashMap<Quiz, Integer> getDashboardData(Integer userID) {
        Quiz[] quizzes = this.quizDAO.getAllQuizzes();
        HashMap<Integer, Result> results = this.resultDAO.getAllResults(userID);
        HashMap<Quiz, Integer> quizResultMap = new HashMap<>();

        for (Quiz quiz : quizzes) {
            Integer quizID = quiz.id();

            if (results != null && results.containsKey(quizID)) {
                Result result = results.get(quizID);
                Integer score = result.getScore();
                quizResultMap.put(quiz, score);
            } else {
                quizResultMap.put(quiz, null);
            }
        }
        return quizResultMap;
    }

    /**
     * Handles POST requests to the dashboard, sets the selected quiz ID in the session,
     * and redirects the user to the quiz page.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute("quizID", request.getParameter("quizID"));
        ControllerUtil.redirect_request(request, response, "/quiz");
    }
}
