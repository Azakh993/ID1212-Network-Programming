package se.kth.id1212.springquiz.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.dao.QuizDAO;
import se.kth.id1212.springquiz.model.Quiz;

import java.util.List;

@Repository
public class QuizDAOImpl implements QuizDAO< Quiz > {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Quiz getQuiz(Integer quizID) {
        return this.entityManager.find(Quiz.class, quizID);
    }

    @Override
    @Transactional
    public Quiz[] getAllQuizzes() {
        String jpql = "SELECT q FROM Quiz q";
        Query query = this.entityManager.createQuery(jpql, Quiz.class);
        List< Quiz > quizList = query.getResultList();

        return quizList.toArray(new Quiz[0]);
    }
}