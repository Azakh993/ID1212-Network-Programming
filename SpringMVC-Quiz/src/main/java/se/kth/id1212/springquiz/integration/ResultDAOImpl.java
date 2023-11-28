package se.kth.id1212.springquiz.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.config.DatabaseInitializer;
import se.kth.id1212.springquiz.model.Quiz;
import se.kth.id1212.springquiz.model.Result;
import se.kth.id1212.springquiz.model.ResultDAO;
import se.kth.id1212.springquiz.model.User;

import java.util.HashMap;
import java.util.List;

@Repository
public class ResultDAOImpl implements ResultDAO<Result> {
    EntityManager entityManager;

    @Override
    public HashMap<Integer, Result> getAllResults(Integer userID) {
        this.entityManager = DatabaseInitializer.getEntityManager();
        HashMap<Integer, Result> results = new HashMap<>();

        try {
            String jpql = "SELECT r FROM Result r WHERE r.user.id = :userId";
            Query query = entityManager.createQuery(jpql, Result.class);
            query.setParameter("userId", userID);

            List<Result> resultList = query.getResultList();

            for (Result result : resultList) {
                results.put(result.getQuiz().getId(), result);
            }
        } finally {
            entityManager.close();
        }

        return results;
    }

    @Transactional
    public void addResult(Integer userID, Integer quizID, Integer points) {
        this.entityManager = DatabaseInitializer.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        String query = "SELECT r FROM Result r WHERE r.user.id = :userId AND r.quiz.id = :quizId";
        Query checkQuery = entityManager.createQuery(query, Result.class);
        checkQuery.setParameter("userId", userID);
        checkQuery.setParameter("quizId", quizID);


        Result existingResult;
        try {
            transaction.begin();
            existingResult = (Result) checkQuery.getSingleResult();
        } catch (NoResultException e) {
            existingResult = null;
        }

        if (existingResult != null) {
            updateResult(existingResult, points);
        } else {
            insertResult(userID, quizID, points);
        }
        transaction.commit();
        this.entityManager.close();
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