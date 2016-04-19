package com.restbucks.ordering.repositories;

import java.util.HashMap;
import java.util.Map.Entry;

import com.restbucks.ordering.model.Identifier;
import com.restbucks.ordering.model.Payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(PaymentRepository.class);

    private static final PaymentRepository theRepository = new PaymentRepository();
    private HashMap<String, Payment> backingStore = new HashMap<>(); // Default implementation, not suitable for production!

    public static PaymentRepository current() {
        return theRepository;
    }
    
    private PaymentRepository(){
        LOG.debug("PaymentRepository Constructor");
    }
    
    public Payment get(Identifier identifier) {
        LOG.debug("Retrieving Payment object for identifier {}", identifier);
        return backingStore.get(identifier.toString());
    }
    
    public Payment take(Identifier identifier) {
        LOG.debug("Removing the Payment object for identifier {}", identifier);
        Payment payment = backingStore.get(identifier.toString());
        remove(identifier);
        return payment;
    }

    public Identifier store(Payment payment) {
        LOG.debug("Storing a new Payment object");
        
        Identifier id = new Identifier();
        LOG.debug("New payment object's id is {}", id);
        
        backingStore.put(id.toString(), payment);
        return id;
    }
    
    public void store(Identifier identifier, Payment payment) {
        LOG.debug("Storing again the Order object with id", identifier);
        backingStore.put(identifier.toString(), payment);
    }

    public boolean has(Identifier identifier) {
        LOG.debug("Checking to see if there is a payment object associated with the id {} in the Payment store", identifier);
        
        boolean result =  backingStore.containsKey(identifier.toString());
        return result;
    }

    public void remove(Identifier identifier) {
        LOG.debug("Removing from storage the Payment object with id", identifier);
        backingStore.remove(identifier.toString());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Entry<String, Payment> entry : backingStore.entrySet()) {
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
}
