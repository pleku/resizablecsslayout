package com.vaadin.pekka.resizablecsslayout;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import com.vaadin.server.VaadinServlet;

public class TServer {

    /**
     *
     * Test server for the addon.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        startServer(9998);
    }

    public static Server startServer(int port) throws Exception {
        Server server = new Server();

        final Connector connector = new SelectChannelConnector();

        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });

        WebAppContext context = new WebAppContext();
        VaadinServlet vaadinServlet = new VaadinServlet();
        ServletHolder servletHolder = new ServletHolder(vaadinServlet);
        servletHolder.setInitParameter("widgetset",
                "com.vaadin.pekka.resizablecsslayout.Widgetset");
        servletHolder.setInitParameter("UIProvider",
                TestUIProviderImpl.class.getName());

        File file = new File("target");
        context.setWar(file.getPath());
        context.setContextPath("/");

        context.addServlet(servletHolder, "/*");
        server.setHandler(context);
        server.start();
        return server;
    }
}
