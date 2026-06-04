package com.shopflow.monolith.billing.infra;

import com.shopflow.contracts.PaymentResultV1;
import com.shopflow.contracts.Topology;
import com.shopflow.monolith.billing.contract.PaymentCompleted;
import com.shopflow.monolith.billing.contract.PaymentFailed;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Saga step 3: translate the broker event back into the module's own domain events.
 * Failure => PaymentFailed => the order module cancels the order (compensation).
 */
@Component
@ConditionalOnProperty(name = "shopflow.payments.mode", havingValue = "kafka")
public class PaymentResultConsumer {

    private final ApplicationEventPublisher events;

    PaymentResultConsumer(ApplicationEventPublisher events) {
        this.events = events;
    }

    @KafkaListener(topics = Topology.PAYMENT_RESULT_TOPIC, groupId = Topology.MONOLITH_GROUP)
    public void on(PaymentResultV1 result) {
        events.publishEvent(result.success()
                ? new PaymentCompleted(result.orderId())
                : new PaymentFailed(result.orderId(), result.reason()));
    }
}
