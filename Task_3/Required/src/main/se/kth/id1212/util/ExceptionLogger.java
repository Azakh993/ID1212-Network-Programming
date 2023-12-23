package main.se.kth.id1212.util;

import java.io.FileWriter;
import java.io.PrintWriter;

public class ExceptionLogger {
    public static void logExceptionToFile(String source, Exception exception) {
        String filePath = "Task_3/src/main/se/kth/id1212/log.txt";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filePath, true));
            writer.println("Exception in " + source + ":");
            exception.printStackTrace(writer);
            writer.println();
            writer.flush();
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
    }
}
