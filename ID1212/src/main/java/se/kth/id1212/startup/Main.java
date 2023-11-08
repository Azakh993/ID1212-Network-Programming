package se.kth.id1212.startup;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import se.kth.id1212.util.ExceptionLogger;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        start_tomcat();
    }

    private static void start_tomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);

        String absolutePathToWebApp = Paths.get("ID1212", "src", "main", "webapp").toFile().getAbsolutePath();
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
