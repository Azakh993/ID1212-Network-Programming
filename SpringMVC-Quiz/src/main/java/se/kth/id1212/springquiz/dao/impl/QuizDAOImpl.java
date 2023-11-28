package se.kth.id1212.springquiz.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.config.DataSourceConfig;
import se.kth.id1212.springquiz.model.Quiz;
import se.kth.id1212.springquiz.dao.QuizDAO;

import java.util.List;

@Repository
public class QuizDAOImpl implements QuizDAO< Quiz > {
    private EntityManager entityManager;

    @Override
    public Quiz getQuiz(Integer quizID) {
        this.entityManager = DataSourceConfig.getEntityManager();
        return entityManager.find(Quiz.class, quizID);
    }

    @Override
    public Quiz[] getAllQuizzes() {
        this.entityManager = DataSourceConfig.getEntityManager();
        String jpql = "SELECT q FROM Quiz q";
        Query query = entityManager.createQuery(jpql, Quiz.class);
        List< Quiz > quizList = query.getResultList();

        return quizList.toArray(new Quiz[0]);
    }
}