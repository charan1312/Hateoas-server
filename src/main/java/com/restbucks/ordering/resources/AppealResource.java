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

import com.restbucks.ordering.activities.AppealDeletionException;
import com.restbucks.ordering.activities.CreateAppealActivity;
import com.restbucks.ordering.activities.InvalidAppealException;
import com.restbucks.ordering.activities.NoSuchAppealException;
import com.restbucks.ordering.activities.ReadAppealActivity;
import com.restbucks.ordering.activities.RemoveAppealActivity;
import com.restbucks.ordering.activities.RemoveOrderActivity;
import com.restbucks.ordering.activities.CreateOrderActivity;
import com.restbucks.ordering.activities.InvalidOrderException;
import com.restbucks.ordering.activities.NoSuchOrderException;
import com.restbucks.ordering.activities.OrderDeletionException;
import com.restbucks.ordering.activities.ReadOrderActivity;
import com.restbucks.ordering.activities.UpdateAppealActivity;
import com.restbucks.ordering.activities.UpdateException;
import com.restbucks.ordering.activities.UpdateOrderActivity;
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
    public Response createOrder(String appealRepresentation) {
        LOG.info("Creating an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new CreateAppealActivity().create(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new AppealsUri(uriInfo.getRequestUri()));
            response = Response.created(responseRepresentation.getSubmitLink().getUri()).entity(responseRepresentation).build();
        } catch (InvalidAppealException iae) {
            LOG.debug("Invalid Appeal - Problem with the appealrepresentation {}", appealRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } catch (Exception ex) {
            LOG.debug("Someting went wrong creating the appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Resulting response for creating the appeal resource is {}", response);
        
        return response;
    }

    @DELETE
    @Path("/{appealId}")
    @Produces("application/vnd.restbucks+xml")
    public Response removeOrder() {
        LOG.info("Removing an Order Reource");
        
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
        
        LOG.debug("Resulting response for deleting the order resource is {}", response);
        
        return response;
    }

    @POST
    @Path("/{orderId}")
    @Consumes("application/vnd.restbucks+xml")
    @Produces("application/vnd.restbucks+xml")
    public Response updateOrder(String orderRepresentation) {
        LOG.info("Updating an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new UpdateAppealActivity().update(AppealRepresentation.fromXmlString(orderRepresentation).getAppeal(), new AppealsUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch (InvalidAppealException ioe) {
            LOG.debug("Invalid order in the XML representation {}", orderRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } catch (NoSuchAppealException nsoe) {
            LOG.debug("No such order resource to update");
            response = Response.status(Status.NOT_FOUND).build();
        } catch(UpdateException ue) {
            LOG.debug("Problem updating the order resource");
            response = Response.status(Status.CONFLICT).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the order resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        
        LOG.debug("Resulting response for updating the order resource is {}", response);
        
        return response;
     }
}
