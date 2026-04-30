package com.shopflow.monolith.billing.contract;

public record PaymentFailed(Long orderId, String reason) {
}
