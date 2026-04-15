package com.shopflow.monolith.billing.contract;

public record PaymentResult(boolean success, String reason) {
    public static PaymentResult ok() {
        return new PaymentResult(true, null);
    }

    public static PaymentResult declined(String reason) {
        return new PaymentResult(false, reason);
    }
}
