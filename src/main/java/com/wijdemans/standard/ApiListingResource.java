package com.wijdemans.standard;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.listing.BaseApiListingResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
    public Response staticSwaggerPages(@PathParam("path") String path) {
        String resource = "/api-docs/" + path;
        logger.trace("Serving resource : [{}]", resource);
        InputStream is = ApiListingResource.class.getResourceAsStream(resource);
        if (is == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // TODO add mediatype per file @Produces(MediaType.TEXT_HTML)
        StreamingOutput stream = os -> {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = is.read(buffer)) > 0) {
                os.write(buffer, 0, n);
            }
            os.close();
        };
        return Response.ok(stream).build();
    }
}

