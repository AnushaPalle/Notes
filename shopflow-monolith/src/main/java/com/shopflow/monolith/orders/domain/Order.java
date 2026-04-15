package com.shopflow.monolith.orders.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String sku;
    private int quantity;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    protected Order() {
    }

    public Order(Long userId, String sku, int quantity, BigDecimal total) {
        this.userId = userId;
        this.sku = sku;
        this.quantity = quantity;
        this.total = total;
    }

    public void markPaid() {
        if (status == OrderStatus.PENDING) {
            status = OrderStatus.PAID;
        }
    }

    /** Compensating action: an order that cannot be paid is cancelled. */
    public void cancel() {
        if (status == OrderStatus.PENDING) {
            status = OrderStatus.CANCELLED;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
