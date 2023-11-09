package se.kth.id1212.model;

/**
 * An interface that provides methods to retrieve an individual quiz
 * and a collection of all available quizzes.
 *
 * @param <Quiz> The type representing a quiz.
 */
public interface QuizDAO<Quiz> {

    /**
     * Retrieves a specific quiz based on its unique identifier.
     *
     * @param id The unique identifier of the quiz to retrieve.
     * @return The Quiz object associated with the given identifier.
     */
    Quiz getQuiz(Integer id);

    /**
     * Retrieves all available quizzes.
     *
     * @return An array of Quiz objects representing all available quizzes.
     */
    Quiz[] getAllQuizzes();
}
