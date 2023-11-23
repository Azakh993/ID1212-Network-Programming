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

/**
 * This class extends HttpServlet and is responsible for managing
 * user requests related to quizzes, including displaying quiz questions,
 * handling quiz attempts, and updating quiz results in the database.
 */
@WebServlet(name = "QuizServlet", urlPatterns = {"/quiz"})
public class QuizServlet extends HttpServlet {
    private QuizDAO<Quiz> quizDAO;
    private QuestionDAO<Question> questionDAO;
    private ResultDAO<Result> resultDAO;
    private HttpServletRequest request;
    private Integer acquiredPoints;

    /**
     * Initializes the servlet by creating instances of QuizDAO, QuestionDAO, and ResultDAO.
     *
     * @param config The ServletConfig object containing configuration information.
     */
    @Override
    public void init(ServletConfig config) {
        this.quizDAO = new QuizDAOImpl();
        this.questionDAO = new QuestionDAOImpl();
        this.resultDAO = new ResultDAOImpl();
    }

    /**
     * Handles GET requests to the quiz page, validates user login state,
     * retrieves quiz data, and forwards the request to the quiz view.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userID = ControllerUtil.validate_login_state(request, response);

        if (userID != null) {
            this.request = request;
            getQuizPageData();
            ControllerUtil.forward_request(request, response, "/quiz.jsp");
        }
    }

    /**
     * Retrieves quiz data for the quiz page based on the selected quiz ID.
     */
    private void getQuizPageData() {
        HttpSession session = this.request.getSession();
        String quizID_string = (String) session.getAttribute("quizID");
        Integer quizID = Integer.valueOf(quizID_string);

        setQuizSubject(quizID);
        setQuizQuestions(quizID);
    }

    /**
     * Sets the subject of the quiz in the request attributes.
     *
     * @param quizID The unique identifier of the quiz.
     */
    private void setQuizSubject(Integer quizID) {
        Quiz quiz = this.quizDAO.getQuiz(quizID);
        String quizSubject = quiz.subject();
        request.setAttribute("quizSubject", quizSubject);
    }

    /**
     * Sets the quiz questions and options in the request attributes.
     *
     * @param quizID The unique identifier of the quiz.
     */
    private void setQuizQuestions(Integer quizID) {
        Question[] questions = this.questionDAO.getAllQuestions(quizID);
        request.setAttribute("questionsAndOptions", questions);
    }

    /**
     * Handles POST requests for quiz attempts; validates user login state,
     * processes user responses, updates quiz results in the database, and forwards
     * the request to the quiz page or redirects to the dashboard based on user actions.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     */
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

    /**
     * Checks if the request contains user responses to quiz questions.
     *
     * @return True if the request contains question IDs, false otherwise.
     */
    private boolean requestContainsQuestionIDs() {
        Enumeration<String> parameterNames = this.request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith("question")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The method evaluates the user's selected options against the correct answers, determines the points earned for each
     * question, and accumulates the total acquired points for the entire quiz attempt.
     */
    private void correctQuizAttempt() {
        if (irrelevantRequest()) {
            return;
        }

        Map<String, String[]> parameter_map = request.getParameterMap();
        HashMap<Integer, Integer> questionIDs_to_points = new HashMap<>();
        this.acquiredPoints = 0;

        for (Map.Entry<String, String[]> entry : parameter_map.entrySet()) {
            String parameterName = entry.getKey();
            String selected_option = entry.getValue()[0];
            mapQuestionIDToPoints(questionIDs_to_points, parameterName, selected_option);
        }

        this.request.setAttribute("acquiredPoints", this.acquiredPoints.toString());
        this.request.setAttribute("questionIDs_to_points", questionIDs_to_points);
    }

    /**
     * Checks if the request is irrelevant, such as having no content or not containing question IDs.
     *
     * @return True if the request is irrelevant, false otherwise.
     */
    private boolean irrelevantRequest() {
        int response_content_length = request.getContentLength();
        return response_content_length == -1 || !requestContainsQuestionIDs();
    }

    /**
     * Maps question IDs to points based on the selected options and correct answers.
     *
     * @param questionIDs_to_points A HashMap to store question IDs and corresponding points.
     * @param parameterName         The parameter name representing a question.
     * @param selected_option       The selected option for the question.
     */
    private void mapQuestionIDToPoints(HashMap<Integer, Integer> questionIDs_to_points, String parameterName, String selected_option) {
        if (parameterName != null && parameterName.startsWith("question")) {
            String questionID_string = parameterName.substring(8);
            Integer questionID = Integer.valueOf(questionID_string);

            String correct_answer = questionDAO.getQuestion(questionID).answer();
            int points = 0;

            if (selected_option.equals(correct_answer)) {
                this.acquiredPoints++;
                points = 1;
            }
            questionIDs_to_points.put(questionID, points);
        }
    }

    /**
     * Updates user quiz results in the database.
     *
     * @param userID_string The unique identifier of the user.
     */
    private void updateResultsInDB(String userID_string) {
        HttpSession session = this.request.getSession();
        String quizID_string = (String) session.getAttribute("quizID");

        Integer userID = Integer.valueOf(userID_string);
        Integer quizID = Integer.valueOf(quizID_string);

        Result latestResult = new Result(userID, quizID, this.acquiredPoints);

        this.resultDAO.addResult(latestResult);
    }

    /**
     * Checks if the request contains an exit parameter, indicating the user wants to exit the quiz.
     *
     * @return True if the request contains an exit parameter, false otherwise.
     */
    private boolean requestContainsExit() {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.equals("exit")) {
                return true;
            }
        }
        return false;
    }
}

