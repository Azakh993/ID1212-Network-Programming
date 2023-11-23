package se.kth.id1212.model;

/**
 * An interface defining operations for accessing and retrieving quiz questions.
 * An interface that provides methods to retrieve individual questions
 * and a collection of questions associated with a specific quiz.
 *
 * @param <Questions> The type representing a collection of questions.
 */
public interface QuestionDAO<Questions> {

    /**
     * Retrieves a specific question based on its unique identifier.
     *
     * @param questionID The unique identifier of the question to retrieve.
     * @return The Question object associated with the given identifier.
     */
    Question getQuestion(Integer questionID);

    /**
     * Retrieves all questions associated with a specific quiz.
     *
     * @param quizID The unique identifier of the quiz to retrieve questions for.
     * @return An array of Question objects associated with the given quiz identifier.
     */

    Questions[] getAllQuestions(Integer quizID);
}
