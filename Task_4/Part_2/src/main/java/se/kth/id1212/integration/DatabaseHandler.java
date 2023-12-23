package se.kth.id1212.integration;

import se.kth.id1212.util.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static Connection connection = null;

    public static Connection connect() {
        Path filePath = Paths.get("src", "main", "database", "Quiz.db");
        File databaseFile = new File(String.valueOf(filePath.toAbsolutePath()));

        if (connection != null) {
            return connection;
        }

        if (databaseFile.exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
            } catch (ClassNotFoundException | SQLException exception) {
                ExceptionLogger.log(exception);
            }
        } else {
            connection = createNewDatabase(databaseFile);
        }

        return connection;
    }

    private static Connection createNewDatabase(File databaseFile) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

            Path scriptPath = Paths.get("src", "main", "resources", "PopulateDB.sql").toAbsolutePath();

            String scriptContent = new String(Files.readAllBytes(scriptPath));
            executeSqlScript(connection, scriptContent);

        } catch (ClassNotFoundException | SQLException exception) {
            ExceptionLogger.log(exception);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return connection;
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