package com.shopflow.payment.messaging;

import com.shopflow.contracts.OrderPlacedV1;
import com.shopflow.contracts.PaymentResultV1;
import com.shopflow.contracts.Topology;
import com.shopflow.payment.PaymentService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Saga step 2: reacts to OrderPlaced, charges idempotently, reports the outcome as an event. */
@Component
@ConditionalOnProperty(name = "payments.messaging.enabled", havingValue = "true")
public class OrderPlacedConsumer {

    private final PaymentService payments;
    private final PaymentResultPublisher publisher;

    OrderPlacedConsumer(PaymentService payments, PaymentResultPublisher publisher) {
        this.payments = payments;
        this.publisher = publisher;
    }

    @KafkaListener(topics = Topology.ORDER_PLACED_TOPIC, groupId = Topology.PAYMENT_SERVICE_GROUP)
    public void handle(OrderPlacedV1 event) {
        var response = payments.process("order-" + event.orderId(), event.orderId(), event.amount());
        publisher.publish(new PaymentResultV1(event.orderId(), "APPROVED".equals(response.status()), response.reason()));
    }
}
