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

/**
 * Handles the connection to the database and provides functionality for database-related operations.
 */
public class DatabaseHandler {
    /**
     * The singleton instance of the database connection.
     */
    private static Connection connection = null;

    /**
     * Establishes a connection to the database. If the database file exists, it connects to the existing database.
     * Otherwise, it creates a new database and initializes it with the contents of a SQL script.
     *
     * @return The established database connection.
     */
    public static Connection connect() {
        Path filePath = Paths.get("src", "main", "database", "db.lck");
        File databaseFile = new File(String.valueOf(filePath.toAbsolutePath()));

        Path dbDirectoryPath = Paths.get("src", "main", "database");
        File dbDirectory = new File(String.valueOf(dbDirectoryPath.toAbsolutePath()));

        if (connection != null) {
            return connection;
        }

        if (databaseFile.exists()) {
            try {
                Class.forName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
                connection = DriverManager.getConnection("jdbc:derby:" + dbDirectory);
            } catch (ClassNotFoundException | SQLException exception) {
                ExceptionLogger.log(exception);
            }
        } else {
            connection = createNewDatabase(dbDirectory);
        }

        return connection;
    }

    /**
     * Creates a new database and initializes it with the contents of a SQL script.
     *
     * @param dbDirectory The directory where the new database will be created.
     * @return The established connection to the newly created database.
     */
    private static Connection createNewDatabase(File dbDirectory) {
        Connection connection = null;
        try {
            Class.forName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
            connection = DriverManager.getConnection("jdbc:derby:" + dbDirectory.getAbsolutePath() + ";create=true");

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

    /**
     * Executes a SQL script on the given database connection.
     *
     * @param connection The database connection on which the SQL script will be executed.
     * @param sqlScript  The SQL script to be executed.
     * @throws SQLException If an SQL exception occurs during script execution.
     */
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
