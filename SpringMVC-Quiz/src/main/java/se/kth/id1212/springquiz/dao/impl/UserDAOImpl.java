package se.kth.id1212.springquiz.dao.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import se.kth.id1212.springquiz.config.DataSourceConfig;
import se.kth.id1212.springquiz.model.User;
import se.kth.id1212.springquiz.dao.UserDAO;

@Repository
public class UserDAOImpl implements UserDAO {

    @Override
    public User getUser(String username, String password) {
        EntityManager entityManager = DataSourceConfig.getEntityManager();

        String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password";
        Query query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);

        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (Exception ignored) {
            // Handle NoResultException or other exceptions if needed
        }
        return user;
    }
}