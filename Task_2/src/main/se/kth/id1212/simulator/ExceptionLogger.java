package main.se.kth.id1212.simulator;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Logs exceptions to a file.
 */
public class ExceptionLogger {

    /**
     * Logs an exception to a file.
     * @param exception The exception to log.
     */
    public static void logExceptionToFile(Exception exception) {
        String filePath = "Task_2/src/main/se/kth/id1212/simulator/log.txt";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filePath, true));
            exception.printStackTrace(writer);
            writer.println();
            writer.flush();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
}
