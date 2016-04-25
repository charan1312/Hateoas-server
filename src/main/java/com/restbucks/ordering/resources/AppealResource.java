package com.restbucks.ordering.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.hateoas.appeals.activities.AppealDeletionException;
import com.hateoas.appeals.activities.CreateAppealActivity;
import com.hateoas.appeals.activities.CreateOrderActivity;
import com.hateoas.appeals.activities.InvalidAppealException;
import com.hateoas.appeals.activities.InvalidOrderException;
import com.hateoas.appeals.activities.NoSuchAppealException;
import com.hateoas.appeals.activities.NoSuchOrderException;
import com.hateoas.appeals.activities.OrderDeletionException;
import com.hateoas.appeals.activities.ReadAppealActivity;
import com.hateoas.appeals.activities.ReadOrderActivity;
import com.hateoas.appeals.activities.RemoveAppealActivity;
import com.hateoas.appeals.activities.RemoveOrderActivity;
import com.hateoas.appeals.activities.UpdateAppealActivity;
import com.hateoas.appeals.activities.UpdateException;
import com.hateoas.appeals.activities.UpdateOrderActivity;
import com.restbucks.ordering.representations.AppealRepresentation;
import com.restbucks.ordering.representations.AppealsUri;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/appeal")
public class AppealResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(AppealResource.class);

    private @Context UriInfo uriInfo;

    public AppealResource() {
        LOG.info("AppealResource constructor");
    }

    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * 
     * @param uriInfo
     */
    public AppealResource(UriInfo uriInfo) {
        LOG.info("AppealResource constructor with mock uriInfo {}", uriInfo);
        this.uriInfo = uriInfo;  
    }
    
    @GET
    @Path("/{appealId}")
    @Produces("application/vnd-cse564-appeals+xml")
    public Response getAppeal() {
        LOG.info("Retrieving an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new ReadAppealActivity().retrieveByUri(new AppealsUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(NoSuchAppealException nsae) {
            LOG.debug("No such appeal");
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong retriveing the Appeal");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Retrieved the appeal resource", response);
        
        return response;
    }
    
    @POST
    @Consumes("application/vnd-cse564-appeals+xml")
    @Produces("application/vnd-cse564-appeals+xml")
    public Response createAppeal(String appealRepresentation) {
        LOG.info("Creating an Appeal Resource");
        
        Response response;
        AppealRepresentation responseRepresentation;
        try {
            responseRepresentation = new CreateAppealActivity().create(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new AppealsUri(uriInfo.getRequestUri()));
            response = Response
                    .created(responseRepresentation.getUpdateLink().getUri())
                    .entity(responseRepresentation)
                    .build();
        } catch (InvalidAppealException iae) {
            LOG.debug("Invalid Appeal - Problem with the appealrepresentation {}", appealRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } catch (Exception ex) {
            LOG.debug("Someting went wrong creating the appeal resource");
            LOG.info(ex.getMessage());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Resulting response for creating the appeal resource is {}", response);
        
        return response;
    }

    @DELETE
    @Path("/{appealId}")
    @Produces("application/vnd-cse564-appeals+xml")
    public Response removeAppeal() {
        LOG.info("Removing an Appeal Reource");
        
        Response response;
        
        try {
            AppealRepresentation removedAppeal = new RemoveAppealActivity().delete(new AppealsUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(removedAppeal).build();
        } catch (NoSuchAppealException nsae) {
            LOG.debug("No such appeal resource to delete");
            response = Response.status(Status.NOT_FOUND).build();
        } catch(AppealDeletionException ade) {
            LOG.debug("Problem deleting appeal resource");
            response = Response.status(Status.METHOD_NOT_ALLOWED).header("Allow", "GET").build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong deleting the appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Resulting response for deleting the Appeal resource is {}", response);
        
        return response;
    }

    @POST
    @Path("/{appealId}")
    @Consumes("application/vnd-cse564-appeals+xml")
    @Produces("application/vnd-cse564-appeals+xml")
    public Response updateAppeal(String appealRepresentation) {
        LOG.info("Updating an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new UpdateAppealActivity().update(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new AppealsUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch (InvalidAppealException ioe) {
            LOG.debug("Invalid appeal in the XML representation {}", appealRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } catch (NoSuchAppealException nsoe) {
            LOG.debug("No such appeal resource to update");
            response = Response.status(Status.NOT_FOUND).build();
        } catch(UpdateException ue) {
            LOG.debug("Problem updating the appeal resource");
            response = Response.status(Status.CONFLICT).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        
        LOG.debug("Resulting response for updating the appeal resource is {}", response);
        
        return response;
     }
}