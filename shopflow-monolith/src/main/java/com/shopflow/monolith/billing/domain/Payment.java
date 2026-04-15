package com.shopflow.monolith.billing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private BigDecimal amount;
    private boolean successful;
    private String reason;

    protected Payment() {
    }

    public Payment(Long orderId, BigDecimal amount, boolean successful, String reason) {
        this.orderId = orderId;
        this.amount = amount;
        this.successful = successful;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getReason() {
        return reason;
    }
}
