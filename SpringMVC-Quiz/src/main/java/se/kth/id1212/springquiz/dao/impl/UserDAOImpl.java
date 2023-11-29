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
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User getUser(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        Query query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);

        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (Exception exception) {
            ExceptionLogger.log(exception);
        }
        return user;
    }
}