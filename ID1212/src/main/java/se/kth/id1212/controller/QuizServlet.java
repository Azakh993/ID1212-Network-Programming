package se.kth.id1212.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.id1212.integration.QuestionDAOImpl;
import se.kth.id1212.integration.QuizDAOImpl;
import se.kth.id1212.integration.ResultDAOImpl;
import se.kth.id1212.model.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "QuizServlet", urlPatterns = {"/quiz"})
public class QuizServlet extends HttpServlet {
    private QuizDAO< Quiz > quizDAO;
    private QuestionDAO< Question > questionDAO;
    private ResultDAO< Result > resultDAO;
    private HttpServletRequest request;
    private Integer acquiredPoints;

    @Override
    public void init(ServletConfig config) {
        this.quizDAO = new QuizDAOImpl();
        this.questionDAO = new QuestionDAOImpl();
        this.resultDAO = new ResultDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userID = ControllerUtil.validate_login_state(request, response);

        if (userID != null) {
            this.request = request;
            getQuizPageData();
            ControllerUtil.forward_request(request, response, "/quiz.jsp");
        }
    }

    private void getQuizPageData() {
        HttpSession session = this.request.getSession();
        String quizID_string = (String) session.getAttribute("quizID");
        Integer quizID = Integer.valueOf(quizID_string);

        setQuizSubject(quizID);
        setQuizQuestions(quizID);
    }


    private void setQuizSubject(Integer quizID) {
        Quiz quiz = this.quizDAO.getQuiz(quizID);
        String quizSubject = quiz.subject();
        request.setAttribute("quizSubject", quizSubject);
    }

    private void setQuizQuestions(Integer quizID) {
        Question[] questions = this.questionDAO.getAllQuestions(quizID);
        request.setAttribute("questionsAndOptions", questions);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String userID = ControllerUtil.validate_login_state(request, response);

        if (userID != null) {
            this.request = request;
            if (requestContainsQuestionIDs()) {
                correctQuizAttempt();
                updateResultsInDB(userID);
                getQuizPageData();
                ControllerUtil.forward_request(request, response, "/quiz.jsp");
            } else if (requestContainsExit()) {
                ControllerUtil.redirect_request(request, response, "/dashboard");
            } else {
                ControllerUtil.redirect_request(request, response, "/quiz");
            }
        }
    }

    private boolean requestContainsQuestionIDs() {
        Enumeration< String > parameterNames = this.request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith("question")) {
                return true;
            }
        }
        return false;
    }

    private void correctQuizAttempt() {
        int response_content_length = request.getContentLength();
        if (response_content_length == -1 || !requestContainsQuestionIDs()) {
            return;
        }

        Map< String, String[] > parameter_map = request.getParameterMap();
        HashMap< Integer, Integer > questionIDs_to_points = new HashMap<>();
        this.acquiredPoints = 0;

        for (Map.Entry< String, String[] > entry : parameter_map.entrySet()) {
            String parameterName = entry.getKey();

            if (parameterName != null && parameterName.startsWith("question")) {
                String questionID_string = parameterName.substring(8);
                Integer questionID = Integer.valueOf(questionID_string);

                String selected_option = entry.getValue()[0];
                String correct_answer = questionDAO.getQuestion(questionID).answer();
                int points = 0;

                if (selected_option.equals(correct_answer)) {
                    this.acquiredPoints++;
                    points = 1;
                }
                questionIDs_to_points.put(questionID, points);
            }
        }

        this.request.setAttribute("acquiredPoints", this.acquiredPoints.toString());
        this.request.setAttribute("questionIDs_to_points", questionIDs_to_points);
    }

    private void updateResultsInDB(String userID_string) {
        HttpSession session = this.request.getSession();
        String quizID_string = (String) session.getAttribute("quizID");

        Integer userID = Integer.valueOf(userID_string);
        Integer quizID = Integer.valueOf(quizID_string);

        this.resultDAO.addResult(userID, quizID, this.acquiredPoints);
    }

    private boolean requestContainsExit() {
        Enumeration< String > parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.equals("exit")) {
                return true;
            }
        }
        return false;
    }
}

