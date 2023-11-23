package se.kth.id1212.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.kth.id1212.model.GameSession;

import java.util.HashMap;

@WebServlet(name = "GameServlet", urlPatterns = "/guess")
public class GameServlet extends jakarta.servlet.http.HttpServlet {
    private HashMap<String, GameSession > gameSessions;

    @Override
    public void init(ServletConfig config) {
        gameSessions = new HashMap<>();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String session_id = session.getId();
        GameSession gameSession = getGameSession(session_id);
        sendResponse(session, response, gameSession);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String session_id = session.getId();
        GameSession gameSession = getGameSession(session_id);

        String restart = request.getParameter("restart");
        String guess = request.getParameter("guess");

        if(restart != null && restart.equals("true")) {
            gameSession.restartGame();
        } else if(guess != null) {

            try {
                gameSession.guess(Integer.parseInt(guess));
            } catch (Exception e) {
                gameSession.setGuessOutcome("INVALID");
            }
        }

        sendResponse(session, response, gameSession);
    }

    private GameSession getGameSession(String session_id) {
        GameSession gameSession = gameSessions.get(session_id);
        if(gameSession == null) {
            gameSession = new GameSession();
            gameSessions.put(session_id, gameSession);
        }
        return gameSession;
    }

    private void sendResponse(HttpSession session, HttpServletResponse response, GameSession gameSession) {
        String guess_outcome = gameSession.getGuessOutcome();
        String number_of_guesses = Integer.toString(gameSession.getNumberOfGuesses());

        session.setAttribute("number_of_guesses", number_of_guesses);
        session.setAttribute("guess_outcome", guess_outcome);

        response.setContentType("text/html; charset=UTF-8");

        try {
            response.sendRedirect("index.jsp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}