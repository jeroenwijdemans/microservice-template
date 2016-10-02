package com.wijdemans;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.listing.BaseApiListingResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Path("/")
public class ApiListingResource extends BaseApiListingResource {

    private static final Logger logger = LoggerFactory.getLogger(ApiListingResource.class);

    @Context
    ServletContext context;

    // root path == no path
    @GET
    @ApiOperation(value = "redirect to api-docs", hidden = true)
    public Response home() throws URISyntaxException {
        return Response.status(301).location(new URI("/api-docs/index.html")).build();
    }

    @GET
    @Path("/api-docs")
    @ApiOperation(value = "redirect to api-docs", hidden = true)
    public Response apidocs() throws URISyntaxException {
        return Response.status(301).location(new URI("/api-docs/index.html")).build();
    }

    @GET
    @Path("/api/swagger.{type:json|yaml}")
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    @ApiOperation(value = "The swagger definition in either JSON or YAML", hidden = true)
    public Response getListing(
            @Context Application app,
            @Context ServletConfig sc,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo,
            @PathParam("type") String type) {
        if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
            return getListingYamlResponse(app, context, sc, headers, uriInfo);
        } else {
            return getListingJsonResponse(app, context, sc, headers, uriInfo);
        }
    }

    @GET
    @Path("/api-docs/{path:.*}")
    @ApiOperation(value = "The swagger api itself", hidden = true)
    public Response staticSwaggerPages(@PathParam("path") String path) throws URISyntaxException {
        String resource = "api-docs/" + path;
        logger.trace("Serving resource : [{}]", resource);
        File r = Paths.get(ClassLoader.getSystemResource(resource).toURI()).toFile();

        // TODO add mediatype per file @Produces(MediaType.TEXT_HTML)

        return Response.ok(r).build();
    }
}

