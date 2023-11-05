package se.kth.id1212.model;

public class Result {
    private Integer id;
    private Integer userID;
    private Integer quizID;
    private Integer score;

    public Result(Integer userID, Integer quizID, Integer score) {
        this.userID = userID;
        this.quizID = quizID;
        this.score = score;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getQuizID() {
        return quizID;
    }

    public void setQuizID(Integer quizId) {
        this.quizID = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
