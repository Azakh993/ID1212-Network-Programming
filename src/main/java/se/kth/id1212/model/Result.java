package se.kth.id1212.model;

/**
 * Represents the result of a user's performance in a quiz.
 *
 * @param userID The unique identifier of the user associated with the result.
 * @param quizID The unique identifier of the quiz associated with the result.
 * @param score  The score achieved by the user in the quiz.
 */
public record Result(Integer userID, Integer quizID, Integer score) {
}
