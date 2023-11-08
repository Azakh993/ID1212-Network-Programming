package se.kth.id1212.startup;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);

        String absolutePathToWebApp = Paths.get("ID1212","src","main","webapp").toFile().getAbsolutePath();
        Context context = tomcat.addWebapp("", absolutePathToWebApp);
        context.setPath("");

        tomcat.start();
        tomcat.getServer().await();
    }

}
