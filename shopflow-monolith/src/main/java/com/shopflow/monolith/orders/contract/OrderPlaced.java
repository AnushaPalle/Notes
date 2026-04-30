package com.shopflow.monolith.orders.contract;

import java.math.BigDecimal;

/** Domain event published when an order has been placed and awaits payment. */
public record OrderPlaced(Long orderId, BigDecimal amount) {
}
