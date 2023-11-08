package se.kth.id1212.startup;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);

        Context context = tomcat.addWebapp("",
                "/Users/khz/Library/CloudStorage/OneDrive-Personal/Dokument/1. Personal/1. Education/TIDAB - HT21/ID1212/Task_4-P2/ID1212/src/main/webapp");
        context.setPath("");

        tomcat.start();
        tomcat.getServer().await();
    }

}
