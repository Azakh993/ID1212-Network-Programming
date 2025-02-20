package se.kth.id1212.startup;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import se.kth.id1212.util.ExceptionLogger;

import java.nio.file.Paths;

/**
 * This class contains a method to configure and start the Tomcat server, setting up
 * the necessary connectors and deploying the web application.
 */
public class Main {

    /**
     * The main entry point for the application. Calls the method to start the Tomcat server.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        start_tomcat();
    }

    /**
     * Configures and starts the Tomcat server, setting up connectors and deploying the web application.
     */
    private static void start_tomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);

        String absolutePathToWebApp = Paths.get("src", "main", "webapp").toFile().getAbsolutePath();
        Context context = tomcat.addWebapp("", absolutePathToWebApp);
        context.setPath("");

        try {
            tomcat.start();
        } catch (LifecycleException exception) {
            ExceptionLogger.log(exception);
        }

        tomcat.getServer().await();
    }
}
