package se.kth.id1212.springquiz.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.dao.ResultDAO;
import se.kth.id1212.springquiz.model.Quiz;
import se.kth.id1212.springquiz.model.Result;
import se.kth.id1212.springquiz.model.User;

import java.util.HashMap;
import java.util.List;

@Repository
public class ResultDAOImpl implements ResultDAO< Result > {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public HashMap< Integer, Result > getAllResults(Integer userID) {
        HashMap< Integer, Result > results = new HashMap<>();

        String jpql = "SELECT r FROM Result r WHERE r.user.id = :userId";
        Query query = entityManager.createQuery(jpql, Result.class);
        query.setParameter("userId", userID);

        List< Result > resultList = query.getResultList();

        for (Result result : resultList) {
            results.put(result.getQuiz().getId(), result);
        }

        return results;
    }

    @Transactional
    public void addResult(Integer userID, Integer quizID, Integer points) {
        String query = "SELECT r FROM Result r WHERE r.user.id = :userId AND r.quiz.id = :quizId";
        Query checkQuery = entityManager.createQuery(query, Result.class);
        checkQuery.setParameter("userId", userID);
        checkQuery.setParameter("quizId", quizID);


        Result existingResult;
        try {
            existingResult = (Result) checkQuery.getSingleResult();
        } catch (NoResultException e) {
            existingResult = null;
        }

        if (existingResult != null) {
            updateResult(existingResult, points);
        } else {
            insertResult(userID, quizID, points);
        }
    }

    @Transactional
    public void updateResult(Result existingResult, Integer points) {
        existingResult.setScore(points);
        this.entityManager.merge(existingResult);
    }

    @Transactional
    public void insertResult(Integer userID, Integer quizID, Integer points) {
        Result newResult = new Result();
        newResult.setUser(entityManager.find(User.class, userID));
        newResult.setQuiz(entityManager.find(Quiz.class, quizID));
        newResult.setScore(points);

        entityManager.persist(newResult);
    }
}
