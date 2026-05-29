package com.shopflow.monolith.billing.application;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Strangler route: billing traffic is sent to the extracted payment-service, with fault tolerance. */
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

    /**
     * Retry is the outer aspect, the circuit breaker the inner one. Retries are safe because the call carries an
     * idempotency key. When retries are exhausted (or the circuit is open) we degrade gracefully: the payment is
     * reported as failed and the order saga cancels the order instead of returning a 500.
     */
    @Override
    @Retry(name = "payments", fallbackMethod = "unavailable")
    @CircuitBreaker(name = "payments")
    public PaymentOutcome process(Long orderId, BigDecimal amount) {
        Response response = client.post().uri("/payments")
                .header("Idempotency-Key", "order-" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Request(orderId, amount))
                .retrieve()
                .body(Response.class);
        return new PaymentOutcome("APPROVED".equals(response.status()), response.reason());
    }

    PaymentOutcome unavailable(Long orderId, BigDecimal amount, Throwable cause) {
        return new PaymentOutcome(false, "payment service unavailable");
    }
}
