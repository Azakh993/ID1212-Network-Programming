package se.kth.id1212.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionLogger {
    public static void log(Exception exception_to_log) {
        try {
            Logger logger = Logger.getLogger("ExceptionLogger");

            logger.log(Level.SEVERE, exception_to_log.fillInStackTrace() + "\n", exception_to_log);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
