package se.kth.id1212.springquiz.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.dao.QuestionDAO;
import se.kth.id1212.springquiz.model.Question;
import se.kth.id1212.springquiz.util.ExceptionLogger;

import java.util.List;

@Repository
public class QuestionDAOImpl implements QuestionDAO< Question > {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Question getQuestion(Integer questionID) {
        try {
            return this.entityManager.find(Question.class, questionID);
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    @Override
    @Transactional
    public Question[] getAllQuestions(Integer quizID) {
        String jpql = "SELECT q FROM Question q JOIN q.selectors s WHERE s.quiz.id = :quizId";
        try {
            TypedQuery< Question > query = entityManager.createQuery(jpql, Question.class);
            query.setParameter("quizId", quizID);

            List< Question > questions = query.getResultList();
            return questions.toArray(new Question[0]);
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return new Question[0];
        }
    }
}