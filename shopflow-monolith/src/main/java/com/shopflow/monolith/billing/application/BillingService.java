package com.shopflow.monolith.billing.application;

import com.shopflow.monolith.billing.contract.PaymentGateway;
import com.shopflow.monolith.billing.contract.PaymentResult;
import com.shopflow.monolith.billing.domain.Payment;
import com.shopflow.monolith.billing.domain.PaymentRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
class BillingService implements PaymentGateway {

    static final BigDecimal LIMIT = new BigDecimal("1000");

    private final PaymentRepository payments;

    BillingService(PaymentRepository payments) {
        this.payments = payments;
    }

    @Override
    public PaymentResult charge(Long orderId, BigDecimal amount) {
        PaymentResult result = amount.compareTo(LIMIT) > 0
                ? PaymentResult.declined("amount exceeds limit")
                : PaymentResult.ok();
        payments.save(new Payment(orderId, amount, result.success(), result.reason()));
        return result;
    }
}
