package com.shopflow.contracts;

/** Kafka topics shared by producers and consumers. Messages are keyed by orderId so one order stays in order. */
public final class Topology {
    public static final String ORDER_PLACED_TOPIC = "shopflow.order-placed.v1";
    public static final String PAYMENT_RESULT_TOPIC = "shopflow.payment-result.v1";

    public static final String PAYMENT_SERVICE_GROUP = "payment-service";
    public static final String MONOLITH_GROUP = "shopflow-monolith";
    private Topology() {
    }
}
