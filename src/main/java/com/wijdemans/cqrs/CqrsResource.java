package com.wijdemans.cqrs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(
        value = "cqrs",
        description = "Commands can be posted to this resource. Commands are offloaded to Kafka.",
        tags = {"cqrs"}
)
@Path("api/v1")
public class CqrsResource {

    private static final Logger logger = LoggerFactory.getLogger(CqrsResource.class);

    @Inject
    private KafkaPostService service;

    @POST
    @Path("/command")
    @ApiOperation(value = "The command that the user wants to execute")
    @ApiResponses({
            @ApiResponse(code = 202, message = "when the command has been accepted and committed to the kafka log"),
            @ApiResponse(code = 400, message = "when the command was rejected. One or more reasons are given.")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCommand(
            Command command
    ) {
        try {
            String linkId = service.publish(command);
            return Response.status(202).entity(linkId).build();
        } catch (InvalidCommandException e) {
            logger.debug("Incorrect command: [{}]", e.getMessage());
            return Response.status(400).entity(e.getErrors()).build();
        }
    }

    @GET
    @Path("/actions")
    @ApiOperation(value = "The actions that are supported by the server")
    @ApiResponses({
            @ApiResponse(code = 200, message = "given a set of actions that can be used in the post command"),
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response supportedActions() {
        return Response.ok().entity(service.getSupportedActions()).build();
    }
}
