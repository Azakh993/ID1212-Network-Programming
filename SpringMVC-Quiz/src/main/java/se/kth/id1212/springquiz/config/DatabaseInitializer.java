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

public class DatabaseInitializer {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
            "QuizPersistenceUnit");

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void initializeDatabase() throws ClassNotFoundException {
        Class.forName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
        Path filePath = Paths.get("SpringMVC-Quiz", "src", "main", "database", "db.lck");
        File databaseFile = new File(String.valueOf(filePath.toAbsolutePath()));

        Path dbDirectoryPath = Paths.get("SpringMVC-Quiz", "src", "main", "database");
        File dbDirectory = new File(String.valueOf(dbDirectoryPath.toAbsolutePath()));

        if (databaseFile.exists()) {
            try {
                Class.forName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
                DriverManager.getConnection("jdbc:derby:" + dbDirectory);
            } catch (ClassNotFoundException | SQLException exception) {
                ExceptionLogger.log(exception);
            }
        } else {
            createNewDatabase(dbDirectory);
        }
    }

    private static void createNewDatabase(File dbDirectory) {

        try {
            Class.forName("org.apache.derby.iapi.jdbc.AutoloadedDriver");

            Connection connection = DriverManager.getConnection("jdbc:derby:" + dbDirectory.getAbsolutePath()
                    + ";create=true");

            Path scriptPath =
                    Paths.get("SpringMVC-Quiz", "src", "main", "resources", "sql", "PopulateDB.sql").toAbsolutePath();

            String scriptContent = new String(Files.readAllBytes(scriptPath));
            executeSqlScript(connection, scriptContent);

        } catch (SQLException exception) {
            ExceptionLogger.log(exception);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
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
