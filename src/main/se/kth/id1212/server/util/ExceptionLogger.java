package main.se.kth.id1212.server.util;

import java.io.FileWriter;
import java.io.PrintWriter;

public class ExceptionLogger {
    public static void logExceptionToFile(Exception exception) {
        String filePath = "Task_2\\src\\main\\se\\kth\\id1212\\server\\exception_log.txt";
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
