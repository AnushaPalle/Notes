package com.shopflow.contracts;

public record PaymentResultV1(Long orderId, boolean success, String reason) {
}
