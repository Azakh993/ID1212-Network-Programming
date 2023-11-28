package se.kth.id1212.springquiz.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.util.Properties;

public class DataSourceConfig {
    private static final EntityManagerFactory entityManagerFactory;

    static {
        try {
            Properties properties = new Properties();
            properties.load(DataSourceConfig.class.getClassLoader().getResourceAsStream("database.properties"));

            String persistenceUnitName = properties.getProperty("persistence.unit.name");
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
