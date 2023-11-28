package se.kth.id1212.springquiz.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import se.kth.id1212.springquiz.model.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
@SessionAttributes({"USER_ID", "QUIZ_ID"})
public class QuizController {
    private final QuizDAO<Quiz> quizDAO;
    private final QuestionDAO<Question> questionDAO;
    private final ResultDAO<Result> resultDAO;
    private Model model;
    private HttpServletRequest request;
    private Integer acquiredPoints;

    @Autowired
    public QuizController(QuizDAO<Quiz> quizDAO, QuestionDAO<Question> questionDAO, ResultDAO<Result> resultDAO) {
        this.quizDAO = quizDAO;
        this.questionDAO = questionDAO;
        this.resultDAO = resultDAO;
    }

    @GetMapping
    public String showQuizPage(Model model) {
        this.model = model;
        String userID = (String) this.model.getAttribute("USER_ID");

        if (userID == null) {
            return "redirect:/login";
        } else {
            getQuizPageData();
            return "quiz";
        }
    }

    private void getQuizPageData() {
        String quizID_String = (String) this.model.getAttribute("QUIZ_ID");
        Integer quizID = Integer.valueOf(quizID_String);
        setQuizSubject(quizID);
        setQuizQuestions(quizID);
    }

    private void setQuizSubject(Integer quizID) {
        Quiz quiz = this.quizDAO.getQuiz(quizID);
        String quizSubject = quiz.getSubject();
        this.model.addAttribute("quizSubject", quizSubject);
    }

    private void setQuizQuestions(Integer quizID) {
        Question[] questions = this.questionDAO.getAllQuestions(quizID);
        this.model.addAttribute("questionsAndOptions", questions);
    }

    @PostMapping
    public String processQuizForm(HttpServletRequest request, Model model) {
        this.request = request;
        this.model = model;

        String userID = (String) model.getAttribute("USER_ID");

        if (userID != null) {
            if (requestContainsQuestionIDs()) {
                correctQuizAttempt();
                updateResultsInDB(userID);
                getQuizPageData();
                return "quiz";
            } else if (requestContainsExit()) {
                return "redirect:/dashboard";
            } else {
                return "redirect:/quiz";
            }
        } else {
            return "redirect:/login";
        }
    }

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

        this.model.addAttribute("acquiredPoints", this.acquiredPoints.toString());
        this.model.addAttribute("questionIDs_to_points", questionIDs_to_points);
    }

    private boolean irrelevantRequest() {
        int response_content_length = this.request.getContentLength();
        return response_content_length == -1 || !requestContainsQuestionIDs();
    }

    private void mapQuestionIDToPoints(HashMap<Integer, Integer> questionIDs_to_points, String parameterName, String selected_option) {
        if (parameterName != null && parameterName.startsWith("question")) {
            String questionID_string = parameterName.substring(8);
            Integer questionID = Integer.valueOf(questionID_string);

            String correct_answer = this.questionDAO.getQuestion(questionID).getAnswer();
            int points = 0;

            if (selected_option.equals(correct_answer)) {
                this.acquiredPoints++;
                points = 1;
            }
            questionIDs_to_points.put(questionID, points);
        }
    }

    private void updateResultsInDB(String userID_string) {
        String quizID_string = (String) model.getAttribute("QUIZ_ID");

        Integer userID = Integer.valueOf(userID_string);
        Integer quizID = Integer.valueOf(quizID_string);

        this.resultDAO.addResult(userID, quizID, this.acquiredPoints);
    }

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

