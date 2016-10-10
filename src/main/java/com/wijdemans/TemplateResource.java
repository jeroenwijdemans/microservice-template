package com.wijdemans;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(
        value = "template",
        description = "template",
        tags = {"template"}
)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path("api/v1")
public class TemplateResource {

    private static final Logger logger = LoggerFactory.getLogger(TemplateResource.class);

    @Inject
    private TemplateService templateService;

    @GET
    @Path("/template")
    public Response all() {
        return Response.ok().build();
    }


    @GET
    @Path("/template/{name}")
    public Response findByName(
            @PathParam("name") String name
    ) {
        return Response.ok().build();
    }

}
