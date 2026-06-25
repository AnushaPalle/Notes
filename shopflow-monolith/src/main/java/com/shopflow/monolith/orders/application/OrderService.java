package com.shopflow.monolith.orders.application;

import com.shopflow.monolith.catalog.contract.CatalogQuery;
import com.shopflow.monolith.orders.contract.OrderPlaced;
import com.shopflow.monolith.orders.domain.Order;
import com.shopflow.monolith.orders.domain.OrderRepository;
import com.shopflow.monolith.shared.NotFoundException;
import com.shopflow.monolith.users.contract.UserQuery;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final UserQuery users;
    private final CatalogQuery catalog;
    private final ApplicationEventPublisher events;
    private final Counter placed;
    private final Counter cancelled;

    public OrderService(OrderRepository orders, UserQuery users, CatalogQuery catalog,
                        ApplicationEventPublisher events, MeterRegistry metrics) {
        this.orders = orders;
        this.users = users;
        this.catalog = catalog;
        this.events = events;
        this.placed = Counter.builder("shopflow.orders.placed").description("Orders placed").register(metrics);
        this.cancelled = Counter.builder("shopflow.orders.cancelled")
                .description("Orders cancelled by the payment saga").register(metrics);
    }

    @Transactional
    public Order place(Long userId, String sku, int quantity) {
        if (!users.exists(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
        var product = catalog.findBySku(sku).orElseThrow(() -> new NotFoundException("Product " + sku + " not found"));
        var total = product.price().multiply(BigDecimal.valueOf(quantity));
        var order = orders.save(new Order(userId, sku, quantity, total));
        placed.increment();
        events.publishEvent(new OrderPlaced(order.getId(), total));
        return order;
    }

    @Transactional
    public void markPaid(Long orderId) {
        get(orderId).markPaid();
    }

    /** Saga compensation: payment failed, so the order is cancelled. */
    @Transactional
    public void cancel(Long orderId) {
        get(orderId).cancel();
        cancelled.increment();
    }

    @Transactional(readOnly = true)
    public Order get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }
}
