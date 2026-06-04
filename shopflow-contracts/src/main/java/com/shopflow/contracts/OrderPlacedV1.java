package com.shopflow.contracts;

import java.math.BigDecimal;

/** Event contract v1. Breaking changes must ship as a new V2 type and routing key. */
public record OrderPlacedV1(Long orderId, BigDecimal amount) {
}
