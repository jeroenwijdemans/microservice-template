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
        value = "health",
        description = "health",
        tags = {"health"}
)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Path("api/v1")
public class HealthResource {

    @GET
    @Path("/health")
    public Response healthCheck() {
        return Response.ok().build();
    }

}
