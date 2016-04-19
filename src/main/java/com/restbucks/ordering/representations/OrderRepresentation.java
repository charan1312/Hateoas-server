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
import com.restbucks.ordering.model.Item;
import com.restbucks.ordering.model.Location;
import com.restbucks.ordering.model.Order;
import com.restbucks.ordering.model.OrderStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "order", namespace = Representation.RESTBUCKS_NAMESPACE)
public class OrderRepresentation extends Representation {
    
    private static final Logger LOG = LoggerFactory.getLogger(OrderRepresentation.class);

    @XmlElement(name = "item", namespace = Representation.RESTBUCKS_NAMESPACE)
    private List<Item> items;
    @XmlElement(name = "location", namespace = Representation.RESTBUCKS_NAMESPACE)
    private Location location;
    @XmlElement(name = "cost", namespace = Representation.RESTBUCKS_NAMESPACE)
    private double cost;
    @XmlElement(name = "status", namespace = Representation.RESTBUCKS_NAMESPACE)
    private OrderStatus status;

    /**
     * For JAXB :-(
     */
    OrderRepresentation() {
        LOG.debug("In OrderRepresentation Constructor");
    }

    public static OrderRepresentation fromXmlString(String xmlRepresentation) {
        LOG.info("Creating an Order object from the XML = {}", xmlRepresentation);
                
        OrderRepresentation orderRepresentation = null;     
        try {
            JAXBContext context = JAXBContext.newInstance(OrderRepresentation.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            orderRepresentation = (OrderRepresentation) unmarshaller.unmarshal(new ByteArrayInputStream(xmlRepresentation.getBytes()));
        } catch (Exception e) {
            throw new InvalidOrderException(e);
        }
        
        LOG.debug("Generated the object {}", orderRepresentation);
        return orderRepresentation;
    }
    
    public static OrderRepresentation createResponseOrderRepresentation(Order order, RestbucksUri orderUri) {
        LOG.info("Creating a Response Order for order = {} and order URI", order.toString(), orderUri.toString());
        
        OrderRepresentation orderRepresentation;     
        
        RestbucksUri paymentUri = new RestbucksUri(orderUri.getBaseUri() + "/payment/" + orderUri.getId().toString());
        LOG.debug("Payment URI = {}", paymentUri);
        
        if(order.getStatus() == OrderStatus.UNPAID) {
            LOG.debug("The order status is {}", OrderStatus.UNPAID);
            orderRepresentation = new OrderRepresentation(order, 
                    new Link(RELATIONS_URI + "cancel", orderUri), 
                    new Link(RELATIONS_URI + "payment", paymentUri), 
                    new Link(RELATIONS_URI + "update", orderUri),
                    new Link(Representation.SELF_REL_VALUE, orderUri));
        } else if(order.getStatus() == OrderStatus.PREPARING) {
            LOG.debug("The order status is {}", OrderStatus.PREPARING);
            orderRepresentation = new OrderRepresentation(order, new Link(Representation.SELF_REL_VALUE, orderUri));
        } else if(order.getStatus() == OrderStatus.READY) {
            LOG.debug("The order status is {}", OrderStatus.READY);
            orderRepresentation = new OrderRepresentation(order, new Link(Representation.RELATIONS_URI + "reciept", UriExchange.receiptForPayment(paymentUri)));
        } else if(order.getStatus() == OrderStatus.TAKEN) {
            LOG.debug("The order status is {}", OrderStatus.TAKEN);
            orderRepresentation = new OrderRepresentation(order);            
        } else {
            LOG.debug("The order status is in an unknown status");
            throw new RuntimeException("Unknown Order Status");
        }
        
        LOG.debug("The order representation created for the Create Response Order Representation is {}", orderRepresentation);
        
        return orderRepresentation;
    }

    public OrderRepresentation(Order order, Link... links) {
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
            JAXBContext context = JAXBContext.newInstance(OrderRepresentation.class);
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
}
