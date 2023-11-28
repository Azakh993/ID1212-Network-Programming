package se.kth.id1212.springquiz.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import se.kth.id1212.springquiz.util.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseInitializer {
    private static final EntityManagerFactory entityManagerFactory;
    private static final String DATABASE_DRIVER;
    private static final String DATABASE_DIRECTORY;
    private static final String DATABASE_FILE;
    private static final String DATABASE_CONNECTION_PREFIX;
    private static final String SQL_SCRIPT;


    static {
        try {
            Properties properties = new Properties();
            properties.load(DatabaseInitializer.class.getClassLoader().getResourceAsStream("database.properties"));

            String persistenceUnitName = properties.getProperty("persistence.unit.name");
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

            DATABASE_DRIVER = properties.getProperty("db.driver");
            DATABASE_DIRECTORY = properties.getProperty("db.directory");
            DATABASE_FILE = properties.getProperty("db.file");
            DATABASE_CONNECTION_PREFIX = properties.getProperty("db.connection.prefix");
            SQL_SCRIPT = properties.getProperty("sql.script.path");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void initializeDatabase() {
        Path filePath = Paths.get(DATABASE_FILE).toAbsolutePath();
        File databaseFile = new File(String.valueOf(filePath));

        Path dbDirectoryPath = Paths.get(DATABASE_DIRECTORY).toAbsolutePath();
        File dbDirectory = new File(String.valueOf(dbDirectoryPath));

        if (databaseFile.exists()) {
            try {
                Class.forName(DATABASE_DRIVER);
                DriverManager.getConnection(DATABASE_CONNECTION_PREFIX + dbDirectory);
            } catch (ClassNotFoundException | SQLException exception) {
                ExceptionLogger.log(exception);
            }
        } else {
            createNewDatabase(dbDirectory);
        }
    }

    private static void createNewDatabase(File dbDirectory) {
        try {
            Class.forName(DATABASE_DRIVER);
            Connection connection = DriverManager.getConnection(DATABASE_CONNECTION_PREFIX + dbDirectory
                    + ";create=true");

            Path scriptPath = Paths.get(SQL_SCRIPT).toAbsolutePath();

            String scriptContent = new String(Files.readAllBytes(scriptPath));
            executeSqlScript(connection, scriptContent);

        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeSqlScript(Connection connection, String sqlScript) throws SQLException {
        String[] sqlStatements = sqlScript.split(";");
        try (Statement statement = connection.createStatement()) {
            for (String sqlStatement : sqlStatements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    statement.execute(sqlStatement);
                }
            }
        }
    }
}
