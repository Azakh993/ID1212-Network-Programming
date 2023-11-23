package se.kth.id1212.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides a static method for logging exceptions
 * at the SEVERE level, including the stack trace and additional information.
 */
public class ExceptionLogger {

    /**
     * Logs the provided exception at the SEVERE level, including the stack trace and additional information.
     *
     * @param exception_to_log The exception to be logged.
     */
    public static void log(Exception exception_to_log) {
        try {
            Logger logger = Logger.getLogger("ExceptionLogger");

            logger.log(Level.SEVERE, exception_to_log.fillInStackTrace() + "\n", exception_to_log);

        } catch (Exception exception) {
            ExceptionLogger.log(exception);

        }
    }
}
