package com.shopflow.payment.messaging;

import com.shopflow.contracts.PaymentResultV1;

/** Outbound port so the saga step can be tested without a broker. */
public interface PaymentResultPublisher {
    void publish(PaymentResultV1 result);
}
