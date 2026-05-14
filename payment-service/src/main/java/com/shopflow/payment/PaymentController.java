package com.shopflow.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PaymentController {

    record PaymentRequest(@NotNull Long orderId, @NotNull @DecimalMin("0.01") BigDecimal amount) {
    }

    private final PaymentService service;

    PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/payments")
    PaymentService.PaymentResponse pay(@RequestHeader("Idempotency-Key") String key,
                                       @Valid @RequestBody PaymentRequest request) {
        return service.process(key, request.orderId(), request.amount());
    }
}
