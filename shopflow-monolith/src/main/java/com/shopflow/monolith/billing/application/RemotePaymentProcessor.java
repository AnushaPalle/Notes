package com.shopflow.monolith.billing.application;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Strangler route: billing traffic is sent to the extracted payment-service. */
@Component
@ConditionalOnProperty(name = "shopflow.payments.mode", havingValue = "rest")
class RemotePaymentProcessor implements PaymentProcessor {

    record Request(Long orderId, BigDecimal amount) {
    }

    record Response(String paymentId, String status, String reason) {
    }

    private final RestClient client;

    RemotePaymentProcessor(RestClient.Builder builder,
                           @Value("${shopflow.payments.url:http://localhost:8081}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    @Override
    public PaymentOutcome process(Long orderId, BigDecimal amount) {
        Response response = client.post().uri("/payments")
                .header("Idempotency-Key", "order-" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Request(orderId, amount))
                .retrieve()
                .body(Response.class);
        return new PaymentOutcome("APPROVED".equals(response.status()), response.reason());
    }
}
