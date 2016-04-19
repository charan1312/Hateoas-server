package com.restbucks.ordering.repositories;

import java.util.HashMap;
import java.util.Map.Entry;

import com.restbucks.ordering.model.Identifier;
import com.restbucks.ordering.model.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(OrderRepository.class);

    private static final OrderRepository theRepository = new OrderRepository();
    private HashMap<String, Order> backingStore = new HashMap<>(); // Default implementation, not suitable for production!

    public static OrderRepository current() {
        return theRepository;
    }
    
    private OrderRepository(){
        LOG.debug("OrderRepository Constructor");
    }
    
    public Order get(Identifier identifier) {
        LOG.debug("Retrieving Order object for identifier {}", identifier);
        return backingStore.get(identifier.toString());
     }
    
    public Order take(Identifier identifier) {
        LOG.debug("Removing the Order object for identifier {}", identifier);
        Order order = backingStore.get(identifier.toString());
        remove(identifier);
        return order;
    }

    public Identifier store(Order order) {
        LOG.debug("Storing a new Order object");
                
        Identifier id = new Identifier();
        LOG.debug("New order object id is {}", id);
                
        backingStore.put(id.toString(), order);
        return id;
    }
    
    public void store(Identifier identifier, Order order) {
        LOG.debug("Storing again the Order object with id", identifier);
        backingStore.put(identifier.toString(), order);
    }

    public boolean has(Identifier identifier) {
        LOG.debug("Checking to see if there is an Order object associated with the id {} in the Order store", identifier);
        
        boolean result =  backingStore.containsKey(identifier.toString());
        LOG.debug("The result of the search is {}", result);
        
        return result;
    }

    public void remove(Identifier identifier) {
        LOG.debug("Removing from storage the Order object with id", identifier);
        backingStore.remove(identifier.toString());
    }
    
    public boolean orderPlaced(Identifier identifier) {
        LOG.debug("Checking to see if the order with id = {} has been place", identifier);
        return OrderRepository.current().has(identifier);
    }
    
    public boolean orderNotPlaced(Identifier identifier) {
        LOG.debug("Checking to see if the order with id = {} has not been place", identifier);
        return !orderPlaced(identifier);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Entry<String, Order> entry : backingStore.entrySet()) {
            sb.append(entry.getKey());
            sb.append("\t:\t");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    public synchronized void clear() {
        backingStore = new HashMap<>();
    }

    public int size() {
        return backingStore.size();
    }
}
