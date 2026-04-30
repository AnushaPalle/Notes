package com.shopflow.monolith.orders.infra;

import com.shopflow.monolith.billing.contract.PaymentCompleted;
import com.shopflow.monolith.billing.contract.PaymentFailed;
import com.shopflow.monolith.orders.application.OrderService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Reacts to billing events; orders never calls billing directly. */
@Component
class PaymentResultListener {

    private final OrderService orders;

    PaymentResultListener(OrderService orders) {
        this.orders = orders;
    }

    @EventListener
    void on(PaymentCompleted event) {
        orders.markPaid(event.orderId());
    }

    @EventListener
    void on(PaymentFailed event) {
        orders.cancel(event.orderId());
    }
}
