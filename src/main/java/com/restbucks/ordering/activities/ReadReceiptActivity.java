package com.restbucks.ordering.activities;

import com.hateoas.appeals.activities.OrderAlreadyCompletedException;
import com.hateoas.appeals.activities.OrderNotPaidException;
import com.hateoas.appeals.activities.UriExchange;
import com.restbucks.ordering.model.Identifier;
import com.restbucks.ordering.model.OrderStatus;
import com.restbucks.ordering.model.Payment;
import com.restbucks.ordering.repositories.OrderRepository;
import com.restbucks.ordering.repositories.PaymentRepository;
import com.restbucks.ordering.representations.Link;
import com.restbucks.ordering.representations.ReceiptRepresentation;
import com.restbucks.ordering.representations.Representation;
import com.restbucks.ordering.representations.RestbucksUri;

public class ReadReceiptActivity {

    public ReceiptRepresentation read(RestbucksUri receiptUri) {
        Identifier identifier = receiptUri.getId();
        if(!orderHasBeenPaid(identifier)) {
            throw new OrderNotPaidException();
        } else if (OrderRepository.current().has(identifier) && OrderRepository.current().get(identifier).getStatus() == OrderStatus.TAKEN) {
            throw new OrderAlreadyCompletedException();
        }
        
        Payment payment = PaymentRepository.current().get(identifier);
        
        return new ReceiptRepresentation(payment, new Link(Representation.RELATIONS_URI + "order", UriExchange.orderForReceipt(receiptUri)));
    }

    private boolean orderHasBeenPaid(Identifier id) {
        return PaymentRepository.current().has(id);
    }

}
