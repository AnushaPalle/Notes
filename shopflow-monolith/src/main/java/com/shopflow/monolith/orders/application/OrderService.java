package com.shopflow.monolith.orders.application;

import com.shopflow.monolith.billing.contract.PaymentGateway;
import com.shopflow.monolith.billing.contract.PaymentResult;
import com.shopflow.monolith.catalog.contract.CatalogQuery;
import com.shopflow.monolith.orders.domain.Order;
import com.shopflow.monolith.orders.domain.OrderRepository;
import com.shopflow.monolith.shared.NotFoundException;
import com.shopflow.monolith.users.contract.UserQuery;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final UserQuery users;
    private final CatalogQuery catalog;
    private final PaymentGateway payments;

    public OrderService(OrderRepository orders, UserQuery users, CatalogQuery catalog, PaymentGateway payments) {
        this.orders = orders;
        this.users = users;
        this.catalog = catalog;
        this.payments = payments;
    }

    @Transactional
    public Order place(Long userId, String sku, int quantity) {
        if (!users.exists(userId)) {
            throw new NotFoundException("User " + userId + " not found");
        }
        var product = catalog.findBySku(sku).orElseThrow(() -> new NotFoundException("Product " + sku + " not found"));
        var total = product.price().multiply(BigDecimal.valueOf(quantity));
        var order = orders.save(new Order(userId, sku, quantity, total));

        PaymentResult result = payments.charge(order.getId(), total);
        if (result.success()) {
            order.markPaid();
        } else {
            order.cancel();
        }
        return order;
    }

    @Transactional(readOnly = true)
    public Order get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }
}
