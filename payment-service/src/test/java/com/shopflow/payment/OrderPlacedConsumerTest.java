package com.shopflow.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.shopflow.contracts.OrderPlacedV1;
import com.shopflow.contracts.PaymentResultV1;
import com.shopflow.payment.messaging.OrderPlacedConsumer;
import com.shopflow.payment.messaging.PaymentResultPublisher;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Primary;

@SpringBootTest(properties = {
        "payments.messaging.enabled=true",
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.admin.auto-create=false"
})
class OrderPlacedConsumerTest {

    @TestConfiguration
    static class Capture {
        static final List<PaymentResultV1> published = new ArrayList<>();

        @Bean
        @Primary
        PaymentResultPublisher publisher() {
            return published::add;
        }
    }

    @Autowired
    OrderPlacedConsumer consumer;

    @Test
    void approvedAndDeclinedOrdersProduceResultEvents() {
        consumer.handle(new OrderPlacedV1(1L, new BigDecimal("50")));
        consumer.handle(new OrderPlacedV1(2L, new BigDecimal("5000")));

        assertThat(Capture.published).extracting(PaymentResultV1::success).containsExactly(true, false);
        assertThat(Capture.published.get(1).reason()).isEqualTo("amount exceeds limit");
    }

    @Test
    void redeliveredMessageGivesTheSameOutcome() {
        consumer.handle(new OrderPlacedV1(3L, new BigDecimal("20")));
        consumer.handle(new OrderPlacedV1(3L, new BigDecimal("20")));

        var results = Capture.published.stream().filter(r -> r.orderId() == 3L).toList();
        assertThat(results).hasSize(2).allMatch(PaymentResultV1::success);
    }
}
