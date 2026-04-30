package com.shopflow.monolith.billing.application;

import com.shopflow.monolith.billing.contract.PaymentCompleted;
import com.shopflow.monolith.billing.contract.PaymentFailed;
import com.shopflow.monolith.billing.domain.Payment;
import com.shopflow.monolith.billing.domain.PaymentRepository;
import java.math.BigDecimal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

    static final BigDecimal LIMIT = new BigDecimal("1000");

    private final PaymentRepository payments;
    private final ApplicationEventPublisher events;

    BillingService(PaymentRepository payments, ApplicationEventPublisher events) {
        this.payments = payments;
        this.events = events;
    }

    @Transactional
    public void charge(Long orderId, BigDecimal amount) {
        boolean ok = amount.compareTo(LIMIT) <= 0;
        String reason = ok ? null : "amount exceeds limit";
        payments.save(new Payment(orderId, amount, ok, reason));
        events.publishEvent(ok ? new PaymentCompleted(orderId) : new PaymentFailed(orderId, reason));
    }
}
