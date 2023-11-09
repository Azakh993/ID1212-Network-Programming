package se.kth.id1212.model;


/**
 * Represents a quiz question with its associated details.
 * @param id          The unique identifier for the question.
 * @param questionText The text of the question.
 * @param answer      The correct answer to the question.
 * @param options     An array of multiple-choice options for the question.
 */
public record Question( Integer id, String questionText, String answer, String[] options ) {
}