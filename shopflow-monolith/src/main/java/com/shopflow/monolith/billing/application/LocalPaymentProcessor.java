package com.shopflow.monolith.billing.application;

import com.shopflow.monolith.billing.domain.Payment;
import com.shopflow.monolith.billing.domain.PaymentRepository;
import java.math.BigDecimal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** The original in-process billing logic, kept as the default while the new service is proven. */
@Component
@ConditionalOnProperty(name = "shopflow.payments.mode", havingValue = "local", matchIfMissing = true)
class LocalPaymentProcessor implements PaymentProcessor {

    static final BigDecimal LIMIT = new BigDecimal("1000");

    private final PaymentRepository payments;

    LocalPaymentProcessor(PaymentRepository payments) {
        this.payments = payments;
    }

    @Override
    public PaymentOutcome process(Long orderId, BigDecimal amount) {
        boolean ok = amount.compareTo(LIMIT) <= 0;
        String reason = ok ? null : "amount exceeds limit";
        payments.save(new Payment(orderId, amount, ok, reason));
        return new PaymentOutcome(ok, reason);
    }
}
