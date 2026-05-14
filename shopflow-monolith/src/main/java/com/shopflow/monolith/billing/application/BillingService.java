package com.shopflow.monolith.billing.application;

import com.shopflow.monolith.billing.contract.PaymentCompleted;
import com.shopflow.monolith.billing.contract.PaymentFailed;
import com.shopflow.monolith.billing.application.PaymentProcessor.PaymentOutcome;
import java.math.BigDecimal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

    private final PaymentProcessor processor;
    private final ApplicationEventPublisher events;

    BillingService(PaymentProcessor processor, ApplicationEventPublisher events) {
        this.processor = processor;
        this.events = events;
    }

    @Transactional
    public void charge(Long orderId, BigDecimal amount) {
        PaymentOutcome outcome = processor.process(orderId, amount);
        events.publishEvent(outcome.success()
                ? new PaymentCompleted(orderId)
                : new PaymentFailed(orderId, outcome.reason()));
    }
}
