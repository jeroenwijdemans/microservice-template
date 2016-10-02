package com.wijdemans;

import com.wijdemans.kafka.KafkaProvider;
import com.wijdemans.kafka.TemplateConsumer;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static Server startServer() throws IOException {

        logger.info("Registering providers and resources.  ");
        final ResourceConfig rc = new ResourceConfig(TemplateResource.class);

        String[] packages = {
                "com.wijdemans"
        };
        rc.packages(packages);

        // enable swagger
        rc.register(SwaggerSerializers.class);
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(KafkaProvider.class).to(KafkaProvider.class).in(Singleton.class);
                bind(TemplateConsumer.class).to(TemplateConsumer.class).in(Singleton.class);

                bind(TemplateResource.class).to(TemplateResource.class).in(Singleton.class);
                bind(ApiListingResource.class).to(ApiListingResource.class).in(Singleton.class);

                bind(TemplateService.class).to(TemplateService.class).in(Singleton.class);

            }
        });

//        rc.register(RolesAllowedDynamicFeature.class); // this enables the RolesAllowed

        // user Jetty Servlet container (not Grizzly) to allow httprequest injection
        ServletContainer servletContainer = new ServletContainer(rc);
        ServletHolder sh = new ServletHolder(servletContainer);
        Server server = new Server(7777);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");

        DefaultExports.initialize();

        context.addServlet(new ServletHolder(
                new MetricsServlet()), "/metrics");

        server.setHandler(context);

        return server;
    }

    private static URI getBaseUri() {
        return URI.create(String.format("http://%s:%s/api/", "0.0.0.0", 7070));
    }

    public static void main(String[] args) throws IOException {
        final Server server = startServer();
        try {
            server.start();
            logger.info("... server ready to serve!");

            printCurrentRegisteredServiceLocators();

        } catch (Exception e) {
            logger.error("Error starting server:  ", e);
        }
    }

    private static void printCurrentRegisteredServiceLocators() {
        ServiceLocatorFactory sf = ServiceLocatorFactory.getInstance();
        Class c = sf.getClass();
        try {
            java.lang.reflect.Field field = c.getDeclaredField("serviceLocators");
            field.setAccessible(true);
            HashMap<String, ServiceLocator> map = (HashMap<String, ServiceLocator>) field.get(sf);
            if (map.entrySet().isEmpty()) {
                logger.warn("Nothing registered yet...");
            }
            map.values().stream().forEach(it -> logger.debug(it.getName()));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            logger.debug("Error reading ServiceLocatorFactory's serviceLocators: {}", e.getMessage());
        }
    }

}
