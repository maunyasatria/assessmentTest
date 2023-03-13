package org.mandiri.gateway.Controller;

import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.mandiri.gateway.Exception.ValidationApiException;
import org.mandiri.gateway.Service.ListData;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DataController {

    @Inject
    ListData listData;


    @Path("/v1/data-list")
    @POST
    @Operation(summary = "Get data List", description = "Get data List from  Database")
   public Response getTransactionList(@Context UriInfo uriInfo, JsonObject payload) throws ValidationApiException {
        JsonObject result = listData.getDataList(uriInfo, payload);
        return Response.ok().entity(result).build();
    }

    @GET
    @Path("v1/get-data-status")
    @Operation(summary = "get detail data status", description = "detail data status")
    public Response getPaymentStatus(@Context UriInfo context) {
        JsonObject result = listData.dataStatus();
        return Response.ok().entity(result).build();
    }
}

