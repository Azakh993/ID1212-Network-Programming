package se.kth.id1212.springquiz.dao.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.dao.UserDAO;
import se.kth.id1212.springquiz.model.User;
import se.kth.id1212.springquiz.util.ExceptionLogger;

@Repository
public class UserDAOImpl implements UserDAO< User > {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User getUserByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        return getUser(jpql, "username", username);
    }

    private User getUser(String jpql, String parameterName, String parameterValue) {
        Query query = entityManager.createQuery(jpql, User.class);

        if (parameterName.equals("userID")) {
            query.setParameter(parameterName, Integer.parseInt(parameterValue));
        } else {
            query.setParameter(parameterName, parameterValue);
        }

        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
        return user;

    }

    @Override
    @Transactional
    public User getUserByUserID(String userID) {
        String jpql = "SELECT u FROM User u WHERE u.id = :userID";
        return getUser(jpql, "userID", userID);
    }
}