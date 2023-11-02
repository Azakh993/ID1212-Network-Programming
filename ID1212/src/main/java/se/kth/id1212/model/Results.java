package se.kth.id1212.model;

public class Results {
    private Integer id;
    private Integer userId;
    private Integer quizId;
    private Integer score;

    public Results(Integer userId, Integer quizId, Integer score) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
