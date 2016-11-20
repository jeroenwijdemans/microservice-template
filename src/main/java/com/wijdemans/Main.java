package com.wijdemans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wijdemans.cqrs.*;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.Immediate;
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

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule valueTyepModule = new SimpleModule("ValueTypeSerializer");
        valueTyepModule.addDeserializer(ValueType.class, new ValueTypeDeserializer());
        valueTyepModule.addSerializer(new ValueTypeSerializer());
        mapper.registerModule(valueTyepModule);

        logger.info("Registering providers and resources.  ");
        final ResourceConfig rc = new ResourceConfig(ImmediateFeature.class, TemplateResource.class, CqrsResource.class);

        String[] packages = {
                "com.wijdemans"
        };

        rc.packages(packages);


        // enable swagger
        rc.register(SwaggerSerializers.class);
        rc.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind("test").to(String.class).named("CQRS_TOPIC");

                bind(Config.class).to(Config.class).in(Immediate.class);

                bind(KafkaProvider.class).to(KafkaProvider.class).in(Singleton.class);
                bind(TemplateConsumer.class).to(TemplateConsumer.class).in(Immediate.class);

                bind(TemplateResource.class).to(TemplateResource.class).in(Singleton.class);
                bind(HealthResource.class).to(HealthResource.class).in(Singleton.class);
                bind(ApiListingResource.class).to(ApiListingResource.class).in(Singleton.class);

                bind(TemplateService.class).to(TemplateService.class).in(Singleton.class);
                bind(KafkaPostService.class).to(KafkaPostService.class).in(Singleton.class);

                bind(CORSFilter.class).to(CORSFilter.class).in(Singleton.class);
                bind(PrometheusFilter.class).to(PrometheusFilter.class).in(Singleton.class);
            }
        });

        // user Jetty Servlet container (not Grizzly) to allow httprequest injection
        ServletContainer servletContainer = new ServletContainer(rc);
        ServletHolder sh = new ServletHolder(servletContainer);
        Server server = new Server(7777);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");

        DefaultExports.initialize();

        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

        server.setHandler(context);

        return server;
    }

    public static void main(String[] args) throws IOException {
        final Server server = startServer();
        try {
            server.start();
            logger.info("... server ready to serve!");

//            printCurrentRegisteredServiceLocators();

        } catch (Exception e) {
            logger.error("Error starting server:  ", e);
        }
    }

}
