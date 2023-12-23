package se.kth.id1212.integration;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import se.kth.id1212.model.Result;
import se.kth.id1212.model.ResultDAO;

import java.util.HashMap;
import java.util.List;

/**
 * This class provides methods to retrieve all quiz results for a user
 * and to add a new quiz result to the database.
 */
public class ResultDAOImpl implements ResultDAO<Result> {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ResultsPU");
    EntityManager entityManager;


    /**
     * Retrieves all quiz results for a specific user from the database.
     *
     * @param userID The unique identifier of the user for whom to retrieve quiz results.
     * @return A HashMap containing quiz IDs as keys and corresponding Result objects as values.
     */
    @Override
    public HashMap<Integer, Result> getAllResults(Integer userID) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        HashMap<Integer, Result> results = new HashMap<>();

        try {
            String jpql = "SELECT r FROM Result r WHERE r.userID = :userId";
            Query query = entityManager.createQuery(jpql, Result.class);
            query.setParameter("userId", userID);

            List<Result> resultList = query.getResultList();

            for (Result result : resultList) {
                results.put(result.getQuizID(), result);
            }
        } finally {
            entityManager.close();
        }

        return results;
    }

    @Transactional
    public void addResult(Result result) {
        this.entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        Integer userId = result.getUserID();
        Integer quizId = result.getQuizID();
        Integer points = result.getScore();

        String query = "SELECT r FROM Result r WHERE r.userID = :userId AND r.quizID = :quizId";
        Query checkQuery = entityManager.createQuery(query, Result.class);
        checkQuery.setParameter("userId", userId);
        checkQuery.setParameter("quizId", quizId);


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
            insertResult(userId, quizId, points);
        }
        transaction.commit();
        entityManager.close();
    }

    @Transactional
    private void updateResult(Result existingResult, Integer points) {
        existingResult.setScore(points);
        this.entityManager.merge(existingResult);
    }

    @Transactional
    private void insertResult(Integer userID, Integer quizID, Integer points) {
        Result newResult = new Result();
        newResult.setUserID(userID);
        newResult.setQuizID(quizID);
        newResult.setScore(points);
        this.entityManager.persist(newResult);
    }
}
