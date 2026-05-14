package com.shopflow.monolith.billing.application;

import java.math.BigDecimal;

/** Port: how billing actually takes a payment. Swapped per `shopflow.payments.mode`. */
public interface PaymentProcessor {

    record PaymentOutcome(boolean success, String reason) {
    }

    PaymentOutcome process(Long orderId, BigDecimal amount);
}
