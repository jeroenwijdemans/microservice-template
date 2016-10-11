package com.wijdemans;

import io.prometheus.client.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO once happy move to own lib
@Provider
public class PrometheusFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(PrometheusFilter.class);
    private final Map<String, Counter> counters = new HashMap<>();


    public PrometheusFilter() {
        logger.info("Registered [{}]", this.getClass().getSimpleName());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty("start", System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        long wasted = System.nanoTime() - (Long) requestContext.getProperty("start");
        logger.debug("Wasted [{}] on [{}] was a [{}]", wasted, requestContext.getMethod(), requestContext.getUriInfo().getPath());

        // TODO Add: total calls / failed calls / 404 / 500 / time spend
        String key = "name_" + sanitizePath(requestContext) + "_" + requestContext.getMethod();
        if (!counters.containsKey(key)) {
            Counter requests = Counter.build()
                    .name(key)
                    .help("Times called!").register();
            counters.put(key, requests);
        }
        counters.get(key).inc();

    }

    private String sanitizePath(ContainerRequestContext requestContext) {
        // TODO replace with regex
        return requestContext.getUriInfo().getPath()
                .replace("/", "_")
                .replace(".", "_")
                .replace("-", "_");
    }


}