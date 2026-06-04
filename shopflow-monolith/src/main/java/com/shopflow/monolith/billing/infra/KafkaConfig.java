package com.shopflow.monolith.billing.infra;

import com.shopflow.contracts.Topology;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "shopflow.payments.mode", havingValue = "kafka")
class KafkaConfig {

    @Bean
    NewTopic orderPlacedTopic() {
        return TopicBuilder.name(Topology.ORDER_PLACED_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic paymentResultTopic() {
        return TopicBuilder.name(Topology.PAYMENT_RESULT_TOPIC).partitions(3).replicas(1).build();
    }
}
