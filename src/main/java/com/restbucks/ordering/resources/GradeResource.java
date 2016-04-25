package com.restbucks.ordering.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.hateoas.appeals.activities.GradeActivity;
import com.hateoas.appeals.activities.InvalidGradeException;
import com.hateoas.appeals.activities.InvalidPaymentException;
import com.hateoas.appeals.activities.NoSuchAppealException;
import com.hateoas.appeals.activities.NoSuchGradeException;
import com.hateoas.appeals.activities.NoSuchOrderException;
import com.hateoas.appeals.activities.PaymentActivity;
import com.hateoas.appeals.activities.ReadAppealActivity;
import com.hateoas.appeals.activities.ReadGradeActivity;
import com.hateoas.appeals.activities.UpdateException;
import com.restbucks.ordering.model.Identifier;
import com.restbucks.ordering.representations.AppealRepresentation;
import com.restbucks.ordering.representations.AppealsUri;
import com.restbucks.ordering.representations.GradeRepresentation;
import com.restbucks.ordering.representations.Link;
import com.restbucks.ordering.representations.Link1;
import com.restbucks.ordering.representations.PaymentRepresentation;
import com.restbucks.ordering.representations.Representation;
import com.restbucks.ordering.representations.Representation1;
import com.restbucks.ordering.representations.RestbucksUri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/grade/{gid}")
public class GradeResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradeResource.class);
    
    private @Context UriInfo uriInfo;
    
    public GradeResource(){
        LOG.info("Grade Resource Constructor");
    }
    
    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * @param uriInfo
     */
    public GradeResource(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @GET
    @Produces("application/vnd-cse564-appeals+xml")
    public Response getAppeal() {
        LOG.info("Retrieving a Grade Resource");
        
        Response response;
        
        try {
            GradeRepresentation responseRepresentation = new ReadGradeActivity().retrieveByUri(new AppealsUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(NoSuchAppealException nsae) {
            LOG.debug("No such appeal");
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong retriveing the Appeal");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Retrieved the grade resource", response);
        
        return response;
    }
    
    
    @PUT
    @Consumes("application/vnd-cse564-appeals+xml")
    @Produces("application/vnd-cse564-appeals+xml")
    public Response updateGrade(String gradeRepresentation) {
        LOG.info("Updating the grade");
        
        Response response;
        
        try {
            response = Response.ok(uriInfo.getRequestUri())    //AppealRepresentation.fromXmlString(appealRepresentation).getAppeal()
                       .entity(new GradeActivity().updategrade(GradeRepresentation.fromXmlString(gradeRepresentation).getGrade(), new AppealsUri(uriInfo.getRequestUri())))
                       .build();
        } catch(NoSuchGradeException nsae) {
            LOG.debug("No grade {}", gradeRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(UpdateException ue) {
            LOG.debug("Invalid update to grade {}", gradeRepresentation);
            Link1 link = new Link1(Representation1.SELF_REL_VALUE, new AppealsUri(uriInfo.getRequestUri().toString()));
            //new Link1(Representation1.SELF_REL_VALUE, new AppealsUri(uriInfo.getBaseUri().toString() + "appeal/" + identifier));
            response = Response.status(Status.FORBIDDEN).entity(link).build();
        } catch(InvalidGradeException ige) {
            LOG.debug("Invalid Grade for Appeal");
            response = Response.status(Status.BAD_REQUEST).build();
        } catch(Exception e) {
            LOG.debug("Someting when wrong with processing grade");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Updated the Grade activity {}", response);
        
        return response;
    }
}
