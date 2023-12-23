package se.kth.id1212.model;

/**
 * Represents a quiz with its associated details.
 *
 * @param id      The unique identifier for the quiz.
 * @param subject The subject/topic of the quiz.
 */
public record Quiz(Integer id, String subject) {
}
