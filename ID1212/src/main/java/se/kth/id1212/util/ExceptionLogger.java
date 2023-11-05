package se.kth.id1212.util;

import java.util.logging.Logger;

public class ExceptionLogger {
    public static void log(Exception exception_to_log) {
        try {
            Logger logger = Logger.getLogger("ExceptionLogger");

            logger.info(exception_to_log.toString() + "\n");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
