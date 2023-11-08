package se.kth.id1212.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static Connection connection = null;

    public static Connection connect() {
        File databaseFile = new File("/Users/khz/Library/CloudStorage/OneDrive-Personal/Dokument/1. Personal/1. Education/TIDAB - HT21/ID1212/Task_4-P2/ID1212/src/main/database/Guess.db");

        if (connection != null) {
            return connection;
        }

        if (databaseFile.exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
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

            String scriptPath = "/Users/khz/Library/CloudStorage/OneDrive-Personal/Dokument/1. Personal/1. Education/TIDAB - HT21/ID1212/Task_4-P2/ID1212/src/main/resources/PopulateDB.sql";
            String scriptContent = new String(Files.readAllBytes(Paths.get(scriptPath)));
            executeSqlScript(connection, scriptContent);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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