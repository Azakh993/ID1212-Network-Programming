package se.kth.id1212.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.kth.id1212.integration.QuestionDAOImpl;
import se.kth.id1212.integration.QuizDAOImpl;
import se.kth.id1212.model.*;

@WebServlet(name = "QuizServlet", urlPatterns = {"/quiz"})
public class QuizServlet extends HttpServlet {
    private QuizDAO<Quiz> quizDAO;
    private QuestionDAO<Question> questionDAO;

    @Override
    public void init(ServletConfig config) {
        this.quizDAO = new QuizDAOImpl();
        this.questionDAO = new QuestionDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userID = ControllerUtil.validate_login_state(request, response);

        if (userID != null) {
            setQuizSubject(request);
            setQuizQuestions(request);
            ControllerUtil.forward_request(request, response, "/quiz.jsp");
        }
    }

    private void setQuizSubject(HttpServletRequest request) {
        Integer quizID = Integer.valueOf(request.getParameter("quizID"));
        Quiz quiz = this.quizDAO.getQuiz(quizID);
        String quizSubject = quiz.subject();
        request.setAttribute("quizSubject", quizSubject);
    }

    private void setQuizQuestions(HttpServletRequest request) {
        Integer quizID = Integer.valueOf(request.getParameter("quizID"));
        Question[] questions = this.questionDAO.getAllQuestions(quizID);
        request.setAttribute("questionsAndOptions", questions);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}

