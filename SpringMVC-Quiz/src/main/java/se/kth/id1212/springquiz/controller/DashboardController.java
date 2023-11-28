package se.kth.id1212.springquiz.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.kth.id1212.springquiz.integration.QuizDAOImpl;
import se.kth.id1212.springquiz.integration.ResultDAOImpl;
import se.kth.id1212.springquiz.model.Quiz;
import se.kth.id1212.springquiz.model.QuizDAO;
import se.kth.id1212.springquiz.model.Result;
import se.kth.id1212.springquiz.model.ResultDAO;

import java.util.HashMap;

@Controller
@RequestMapping("/dashboard")
@SessionAttributes({"USER_ID", "QUIZ_ID"})
public class DashboardController {
    private final QuizDAO<Quiz> quizDAO;
    private final ResultDAO<Result> resultDAO;

    public DashboardController() {
        this.quizDAO = new QuizDAOImpl();
        this.resultDAO = new ResultDAOImpl();
    }

    @GetMapping
    protected String showDashboard(Model model) {
        String userID = (String) model.getAttribute("USER_ID");

        if (userID == null) {
            return "redirect:/login";
        } else {
            HashMap<Quiz, Integer> quizResultMap = getDashboardData(Integer.valueOf(userID));
            model.addAttribute("quizResultMap", quizResultMap);
            return "dashboard";
        }
    }

    private HashMap<Quiz, Integer> getDashboardData(Integer userID) {
        Quiz[] quizzes = this.quizDAO.getAllQuizzes();
        HashMap<Integer, Result> results = this.resultDAO.getAllResults(userID);
        HashMap<Quiz, Integer> quizResultMap = new HashMap<>();

        for (Quiz quiz : quizzes) {
            Integer quizID = quiz.getId();

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

    @PostMapping
    public String selectQuiz(@RequestParam("quizID") String quizID, Model model) {
        model.addAttribute("QUIZ_ID", quizID);
        return "redirect:/quiz";
    }
}