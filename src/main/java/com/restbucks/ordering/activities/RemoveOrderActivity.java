package com.restbucks.ordering.activities;

import com.hateoas.appeals.activities.NoSuchOrderException;
import com.hateoas.appeals.activities.OrderDeletionException;
import com.restbucks.ordering.model.Identifier;
import com.restbucks.ordering.model.Order;
import com.restbucks.ordering.model.OrderStatus;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.representations.OrderRepresentation;
import com.restbucks.ordering.representations.RestbucksUri;

public class RemoveOrderActivity {
    public OrderRepresentation delete(RestbucksUri orderUri) {
        // Discover the URI of the order that has been cancelled
        
        Identifier identifier = orderUri.getId();

        OrderRepository orderRepository = OrderRepository.current();

        if (orderRepository.orderNotPlaced(identifier)) {
            throw new NoSuchOrderException();
        }

        Order order = orderRepository.get(identifier);

        // Can't delete a ready or preparing order
        if (order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.READY) {
            throw new OrderDeletionException();
        }

        if(order.getStatus() == OrderStatus.UNPAID) { // An unpaid order is being cancelled 
            orderRepository.remove(identifier);
        }

        return new OrderRepresentation(order);
    }

}
