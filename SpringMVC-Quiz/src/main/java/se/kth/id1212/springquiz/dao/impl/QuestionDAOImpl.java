package se.kth.id1212.springquiz.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.config.DataSourceConfig;
import se.kth.id1212.springquiz.model.Question;
import se.kth.id1212.springquiz.dao.QuestionDAO;
import se.kth.id1212.springquiz.util.ExceptionLogger;

import java.util.List;

import static se.kth.id1212.springquiz.config.DataSourceConfig.getEntityManager;

@Repository
public class QuestionDAOImpl implements QuestionDAO< Question > {
    private EntityManager entityManager;

    @Override
    public Question getQuestion(Integer questionID) {
        this.entityManager = DataSourceConfig.getEntityManager();
        try {
            return this.entityManager.find(Question.class, questionID);
        } catch (Exception e) {
            ExceptionLogger.log(e);
            return null;
        }
    }

    @Override
    public Question[] getAllQuestions(Integer quizID) {
        this.entityManager = DataSourceConfig.getEntityManager();

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