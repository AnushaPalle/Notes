package com.shopflow.monolith.billing.contract;

import java.math.BigDecimal;

/** Public contract of the billing module (synchronous, replaced by events in the next step). */
public interface PaymentGateway {
    PaymentResult charge(Long orderId, BigDecimal amount);
}
