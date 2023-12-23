package se.kth.id1212.model;

import jakarta.persistence.*;

/**
 * Represents the result of a quiz for a specific user.
 * This entity is used to store information about quiz results in a database.
 */
@Entity
@Table(name = "RESULTS")
public class Result {
    /**
     * The unique identifier for a result.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The identifier of the user who took the quiz.
     */
    @Column(name = "USER_ID")
    private Integer userID;

    /**
     * The identifier of the quiz for which the result is recorded.
     */
    @Column(name = "QUIZ_ID")
    private Integer quizID;

    /**
     * The score achieved by the user in the quiz.
     */
    @Column(name = "SCORE")
    private Integer score;

    /**
     * Constructs a new Result object with the specified user ID, quiz ID, and score.
     *
     * @param userID The identifier of the user who took the quiz.
     * @param quizID The identifier of the quiz for which the result is recorded.
     * @param score  The score achieved by the user in the quiz.
     */
    public Result(Integer userID, Integer quizID, Integer score) {
        this.userID = userID;
        this.quizID = quizID;
        this.score = score;
    }

    /**
     * Default constructor required by JPA.
     */
    public Result() {

    }

    /**
     * Retrieves the unique identifier for this result.
     *
     * @return The unique identifier for this result.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this result.
     *
     * @param id The unique identifier for this result.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retrieves the identifier of the user who took the quiz.
     *
     * @return The identifier of the user who took the quiz.
     */
    public Integer getUserID() {
        return userID;
    }

    /**
     * Sets the identifier of the user who took the quiz.
     *
     * @param userID The identifier of the user who took the quiz.
     */
    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    /**
     * Retrieves the identifier of the quiz for which the result is recorded.
     *
     * @return The identifier of the quiz for which the result is recorded.
     */
    public Integer getQuizID() {
        return quizID;
    }

    /**
     * Sets the identifier of the quiz for which the result is recorded.
     *
     * @param quizID The identifier of the quiz for which the result is recorded.
     */
    public void setQuizID(Integer quizID) {
        this.quizID = quizID;
    }

    /**
     * Retrieves the score achieved by the user in the quiz.
     *
     * @return The score achieved by the user in the quiz.
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Sets the score achieved by the user in the quiz.
     *
     * @param score The score achieved by the user in the quiz.
     */
    public void setScore(Integer score) {
        this.score = score;
    }
}
