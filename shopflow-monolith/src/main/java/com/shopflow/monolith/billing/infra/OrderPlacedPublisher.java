package com.shopflow.monolith.billing.infra;

import com.shopflow.contracts.OrderPlacedV1;
import com.shopflow.contracts.Topology;
import com.shopflow.monolith.orders.contract.OrderPlaced;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Saga step 1: once the order is committed, announce it to the broker. */
@Component
@ConditionalOnProperty(name = "shopflow.payments.mode", havingValue = "kafka")
class OrderPlacedPublisher {

    private final KafkaTemplate<String, Object> kafka;

    OrderPlacedPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    // AFTER_COMMIT: the payment result must never arrive before the order row exists.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void on(OrderPlaced event) {
        kafka.send(Topology.ORDER_PLACED_TOPIC, String.valueOf(event.orderId()),
                new OrderPlacedV1(event.orderId(), event.amount()));
    }
}
