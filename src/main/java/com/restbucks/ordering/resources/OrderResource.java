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

import com.restbucks.ordering.activities.RemoveOrderActivity;
import com.restbucks.ordering.activities.CreateOrderActivity;
import com.restbucks.ordering.activities.InvalidOrderException;
import com.restbucks.ordering.activities.NoSuchOrderException;
import com.restbucks.ordering.activities.OrderDeletionException;
import com.restbucks.ordering.activities.ReadOrderActivity;
import com.restbucks.ordering.activities.UpdateException;
import com.restbucks.ordering.activities.UpdateOrderActivity;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/order")
public class OrderResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(OrderResource.class);

    private @Context UriInfo uriInfo;

    public OrderResource() {
        LOG.info("OrderResource constructor");
    }

    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * 
     * @param uriInfo
     */
    public OrderResource(UriInfo uriInfo) {
        LOG.info("OrderResource constructor with mock uriInfo {}", uriInfo);
        this.uriInfo = uriInfo;  
    }
    
    @GET
    @Path("/{orderId}")
    @Produces("application/vnd.restbucks+xml")
    public Response getOrder() {
        LOG.info("Retrieving an Order Resource");
        
        Response response;
        
        try {
            OrderRepresentation responseRepresentation = new ReadOrderActivity().retrieveByUri(new RestbucksUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(NoSuchOrderException nsoe) {
            LOG.debug("No such order");
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong retriveing the Order");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Retrieved the order resource", response);
        
        return response;
    }
    
    @POST
    @Consumes("application/vnd.restbucks+xml")
    @Produces("application/vnd.restbucks+xml")
    public Response createOrder(String orderRepresentation) {
        LOG.info("Creating an Order Resource");
        
        Response response;
        
        try {
            OrderRepresentation responseRepresentation = new CreateOrderActivity().create(OrderRepresentation.fromXmlString(orderRepresentation).getOrder(), new RestbucksUri(uriInfo.getRequestUri()));
            response = Response.created(responseRepresentation.getUpdateLink().getUri()).entity(responseRepresentation).build();
        } catch (InvalidOrderException ioe) {
            LOG.debug("Invalid Order - Problem with the orderrepresentation {}", orderRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } catch (Exception ex) {
            LOG.debug("Someting went wrong creating the order resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Resulting response for creating the order resource is {}", response);
        
        return response;
    }

    @DELETE
    @Path("/{orderId}")
    @Produces("application/vnd.restbucks+xml")
    public Response removeOrder() {
        LOG.info("Removing an Order Reource");
        
        Response response;
        
        try {
            OrderRepresentation removedOrder = new RemoveOrderActivity().delete(new RestbucksUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(removedOrder).build();
        } catch (NoSuchOrderException nsoe) {
            LOG.debug("No such order resource to delete");
            response = Response.status(Status.NOT_FOUND).build();
        } catch(OrderDeletionException ode) {
            LOG.debug("Problem deleting order resource");
            response = Response.status(Status.METHOD_NOT_ALLOWED).header("Allow", "GET").build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong deleting the order resource");
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
        LOG.info("Updating an Order Resource");
        
        Response response;
        
        try {
            OrderRepresentation responseRepresentation = new UpdateOrderActivity().update(OrderRepresentation.fromXmlString(orderRepresentation).getOrder(), new RestbucksUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch (InvalidOrderException ioe) {
            LOG.debug("Invalid order in the XML representation {}", orderRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } catch (NoSuchOrderException nsoe) {
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
