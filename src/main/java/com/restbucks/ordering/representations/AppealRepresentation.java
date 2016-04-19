package com.restbucks.ordering.representations;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.restbucks.ordering.activities.InvalidOrderException;
import com.restbucks.ordering.activities.UriExchange;
import com.restbucks.ordering.model.Appeal;
import com.restbucks.ordering.model.AppealStatus;
import com.restbucks.ordering.model.Item;
import com.restbucks.ordering.model.Location;
import com.restbucks.ordering.model.Order;
import com.restbucks.ordering.model.OrderStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "appeal", namespace = Representation1.APPEALS_NAMESPACE)
public class AppealRepresentation extends Representation1 {
    
    private static final Logger LOG = LoggerFactory.getLogger(AppealRepresentation.class);

    @XmlElement(name = "id", namespace = Representation1.APPEALS_NAMESPACE)
    //private List<Appeal> appeals;
    private int id;
    
    @XmlElement(name = "description", namespace = Representation1.APPEALS_NAMESPACE)
    private Location description;
    
    @XmlElement(name = "byStudent", namespace = Representation1.APPEALS_NAMESPACE)
    private String byStudent;
    
    @XmlElement(name = "status", namespace = Representation1.APPEALS_NAMESPACE)
    private AppealStatus appealStatus;

    /**
     * For JAXB :-(
     */
    AppealRepresentation() {
        LOG.debug("In AppealRepresentation Constructor");
    }

    public static AppealRepresentation fromXmlString(String xmlRepresentation) {
        LOG.info("Creating an Order object from the XML = {}", xmlRepresentation);
                
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

/*    
    public static AppealRepresentation createResponseAppealRepresentation(Appeal appeal, AppealsUri appealsUri) {
        LOG.info("Creating a Response Appeal for appeal = {} and Appeal URI", appeal.toString(), appealsUri.toString());
        
        AppealRepresentation appealRepresentation;     
        
        AppealsUri appealUri = new AppealsUri(Uri.getBaseUri() + "/payment/" + orderUri.getId().toString());
        LOG.debug("Payment URI = {}", paymentUri);
        
        if(order.getStatus() == OrderStatus.UNPAID) {
            LOG.debug("The order status is {}", OrderStatus.UNPAID);
            orderRepresentation = new AppealRepresentation(order, 
                    new Link(RELATIONS_URI + "cancel", orderUri), 
                    new Link(RELATIONS_URI + "payment", paymentUri), 
                    new Link(RELATIONS_URI + "update", orderUri),
                    new Link(Representation.SELF_REL_VALUE, orderUri));
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
        
        return orderRepresentation;
    }

    public AppealRepresentation(Order order, Link... links) {
        LOG.info("Creating an Order Representation for order = {} and links = {}", order.toString(), links.toString());
        
        try {
            this.location = order.getLocation();
            this.items = order.getItems();
            this.cost = order.calculateCost();
            this.status = order.getStatus();
            this.links = java.util.Arrays.asList(links);
        } catch (Exception ex) {
            throw new InvalidOrderException(ex);
        }
        
        LOG.debug("Created the OrderRepresentation {}", this);
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

    public Order getOrder() {
        LOG.info("Retrieving the Order Representation");
        LOG.debug("Location = {}", location);
        LOG.debug("Location = {}", items);
        if (location == null || items == null) {
            throw new InvalidOrderException();
        }
        for (Item i : items) {
            if (i == null) {
                throw new InvalidOrderException();
            }
        }
        
        Order order = new Order(location, status, items);
        
        LOG.debug("Retrieving the Order Representation {}", order);

        return order;
    }

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
