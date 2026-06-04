package com.shopflow.monolith.billing.infra;

import com.shopflow.monolith.billing.application.BillingService;
import com.shopflow.monolith.orders.contract.OrderPlaced;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${shopflow.payments.mode:local}' != 'kafka'")
class OrderPlacedListener {

    private final BillingService billing;

    OrderPlacedListener(BillingService billing) {
        this.billing = billing;
    }

    @EventListener
    void on(OrderPlaced event) {
        billing.charge(event.orderId(), event.amount());
    }
}
