package com.restbucks.ordering.representations;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restbucks.ordering.activities.InvalidAppealException;
import com.restbucks.ordering.activities.InvalidOrderException;
import com.restbucks.ordering.model.Appeal;
import com.restbucks.ordering.model.AppealStatus;
import com.restbucks.ordering.model.Location;

@XmlRootElement(name = "appeal", namespace = Representation1.APPEALS_NAMESPACE)
public class AppealRepresentation extends Representation1 {

    private static final Logger LOG = LoggerFactory.getLogger(AppealRepresentation.class);

    @XmlElement(name = "studentId", namespace = Representation1.APPEALS_NAMESPACE)
    private int studentId;

    @XmlElement(name = "gradeId", namespace = Representation1.APPEALS_NAMESPACE)
    private int gradeId;

    @XmlElement(name = "comment", namespace = Representation1.APPEALS_NAMESPACE)
    private List<String> comments;

    @XmlElement(name = "title", namespace = Representation1.APPEALS_NAMESPACE)
    private String title;

    @XmlElement(name = "status", namespace = Representation1.APPEALS_NAMESPACE)
    private AppealStatus appealStatus;

    /**
     * For JAXB :-(
     */
    AppealRepresentation() {
        LOG.debug("In AppealRepresentation Constructor");
    }

    public static AppealRepresentation fromXmlString(String xmlRepresentation) {
        LOG.info("Creating an Appeal object from the XML = {}", xmlRepresentation);

        AppealRepresentation appealRepresentation = null;     
        try {
            JAXBContext context = JAXBContext.newInstance(AppealRepresentation.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            appealRepresentation = (AppealRepresentation) unmarshaller.unmarshal(new ByteArrayInputStream(xmlRepresentation.getBytes()));
        } catch (Exception e) {
            throw new InvalidOrderException(e);
        }

        LOG.debug("Generated the object {}", appealRepresentation);
        return appealRepresentation;
    }


    public static AppealRepresentation createResponseAppealRepresentation(Appeal appeal, AppealsUri appealUri) {
        LOG.info("Creating a Response Appeal for appeal = {} and Appeal URI", appeal.toString(), appealUri.toString());

        AppealRepresentation appealRepresentation;     

        //AppealsUri gradeUri = new AppealsUri(appealUri.getBaseUri() + "/grade/" + appealUri.getId().toString());
        AppealsUri gradeUri = new AppealsUri(appealUri.getBaseUri() + "/grade/" + appeal.getGradeId());
        LOG.debug("Grade URI = {}", gradeUri);

        if(appeal.getStatus() == AppealStatus.CREATED) {
            LOG.debug("The order status is {}", AppealStatus.CREATED);
            appealRepresentation = new AppealRepresentation(appeal, 
                    new Link1(RELATIONS_URI + "reject", appealUri), 
                    new Link1(RELATIONS_URI + "grade", gradeUri), 
                    new Link1(RELATIONS_URI + "process", appealUri),
                    new Link1(RELATIONS_URI + "approve", appealUri),
                    new Link1(RELATIONS_URI + "submit", appealUri),
                    new Link1(Representation1.SELF_REL_VALUE, appealUri));
        } else if(order.getStatus() == OrderStatus.PREPARING) {
            LOG.debug("The order status is {}", OrderStatus.PREPARING);
            orderRepresentation = new AppealRepresentation(order, new Link(Representation.SELF_REL_VALUE, orderUri));
        } else if(order.getStatus() == OrderStatus.READY) {
            LOG.debug("The order status is {}", OrderStatus.READY);
            orderRepresentation = new AppealRepresentation(order, new Link(Representation.RELATIONS_URI + "reciept", UriExchange.receiptForPayment(paymentUri)));
        } else if(order.getStatus() == OrderStatus.TAKEN) {
            LOG.debug("The order status is {}", OrderStatus.TAKEN);
            orderRepresentation = new AppealRepresentation(order);            
        } else {
            LOG.debug("The order status is in an unknown status");
            throw new RuntimeException("Unknown Order Status");
        }

        LOG.debug("The order representation created for the Create Response Order Representation is {}", orderRepresentation);

        return appealRepresentation;
    }

    public AppealRepresentation(Appeal appeal, Link1... links) {
        LOG.info("Creating an Appeal Representation for order = {} and links = {}", appeal.toString(), links.toString());

        try {
            this.studentId = appeal.getStudentID();
            this.comments = appeal.getComments();
            this.title = appeal.getTitle();
            this.appealStatus = appeal.getStatus();
            this.links = java.util.Arrays.asList(links);
        } catch (Exception ex) {
            throw new InvalidAppealException(ex);
        }

        LOG.debug("Created the AppealRepresentation {}", this);
    }

    @Override
    public String toString() {
        LOG.info("Converting Order Representation object to string");
        try {
            JAXBContext context = JAXBContext.newInstance(AppealRepresentation.class);
            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Appeal getAppeal() {
        LOG.info("Retrieving the Appeal Representation");
//        LOG.debug("Location = {}", location);
//        LOG.debug("Location = {}", items);
        if (title.isEmpty() || gradeId == 0 || studentId ==0 ) {
            throw new InvalidAppealException();
        }
/*        for (Item i : items) {
            if (i == null) {
                throw new InvalidOrderException();
            }
        }
*/
        Appeal appeal = new Appeal(studentId, gradeId, title);

        LOG.debug("Retrieving the Appeal Representation {}", appeal);

        return appeal;
    }

    /*
    public Link getCancelLink() {
        LOG.info("Retrieving the Cancel link ");
        return getLinkByName(RELATIONS_URI + "cancel");
    }

    public Link getPaymentLink() {
        LOG.info("Retrieving the Payment link ");
        return getLinkByName(RELATIONS_URI + "payment");
    }

    public Link getUpdateLink() {
        LOG.info("Retrieving the Update link ");
        return getLinkByName(RELATIONS_URI + "update");
    }

    public Link getSelfLink() {
        LOG.info("Retrieving the Self link ");
        return getLinkByName("self");
    }

    public OrderStatus getStatus() {
        LOG.info("Retrieving the order status {}", status);
        return status;
    }

    public double getCost() {
        LOG.info("Retrieving the order cost {}", cost);
        return cost;
    }
    */
}
