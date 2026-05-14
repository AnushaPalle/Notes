package com.shopflow.payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    static final BigDecimal LIMIT = new BigDecimal("1000");

    public record PaymentResponse(String paymentId, String status, String reason) {
    }

    private final Map<String, PaymentResponse> byIdempotencyKey = new ConcurrentHashMap<>();

    /** Same idempotency key => same response, no matter how many times a client retries. */
    public PaymentResponse process(String idempotencyKey, long orderId, BigDecimal amount) {
        return byIdempotencyKey.computeIfAbsent(idempotencyKey, k -> amount.compareTo(LIMIT) > 0
                ? new PaymentResponse(UUID.randomUUID().toString(), "DECLINED", "amount exceeds limit")
                : new PaymentResponse(UUID.randomUUID().toString(), "APPROVED", null));
    }
}
