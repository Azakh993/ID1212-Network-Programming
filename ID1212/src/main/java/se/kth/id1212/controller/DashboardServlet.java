package se.kth.id1212.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.kth.id1212.integration.QuizDAOImpl;
import se.kth.id1212.integration.ResultDAOImpl;
import se.kth.id1212.model.Quiz;
import se.kth.id1212.model.QuizDAO;
import se.kth.id1212.model.Result;
import se.kth.id1212.model.ResultDAO;

import java.util.HashMap;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private QuizDAO<Quiz> quizDAO;
    private ResultDAO<Result> resultDAO;

    public void init(ServletConfig config) {
        this.quizDAO = new QuizDAOImpl();
        this.resultDAO = new ResultDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userID = ControllerUtil.validate_login_state(request, response);

        if(userID != null) {
            HashMap<Quiz, Integer> quizResultMap = getDashboardData(userID);
            request.setAttribute("quizResultMap", quizResultMap);
            ControllerUtil.forward_request(request, response, "/dashboard.jsp");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String selected_quiz = request.getParameter("quizID");
        ControllerUtil.redirect_request(request, response, "/quiz?quizID=" + selected_quiz);
    }

    private HashMap<Quiz, Integer> getDashboardData(String userID) {
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
}
