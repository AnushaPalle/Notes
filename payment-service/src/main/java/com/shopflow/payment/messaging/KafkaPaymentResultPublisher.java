package com.shopflow.payment.messaging;

import com.shopflow.contracts.PaymentResultV1;
import com.shopflow.contracts.Topology;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "payments.messaging.enabled", havingValue = "true")
class KafkaPaymentResultPublisher implements PaymentResultPublisher {

    private final KafkaTemplate<String, Object> kafka;

    KafkaPaymentResultPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    @Override
    public void publish(PaymentResultV1 result) {
        kafka.send(Topology.PAYMENT_RESULT_TOPIC, String.valueOf(result.orderId()), result);
    }
}
