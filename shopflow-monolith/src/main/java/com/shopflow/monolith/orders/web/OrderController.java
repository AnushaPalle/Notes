package com.shopflow.monolith.orders.web;

import com.shopflow.monolith.orders.application.OrderService;
import com.shopflow.monolith.orders.domain.Order;
import com.shopflow.monolith.orders.domain.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
class OrderController {

    record PlaceOrderRequest(@NotNull Long userId, @NotBlank String sku, @Min(1) int quantity) {
    }

    record OrderResponse(Long id, Long userId, String sku, int quantity, BigDecimal total, OrderStatus status) {
        static OrderResponse from(Order o) {
            return new OrderResponse(o.getId(), o.getUserId(), o.getSku(), o.getQuantity(), o.getTotal(), o.getStatus());
        }
    }

    private final OrderService service;

    OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse place(@Valid @RequestBody PlaceOrderRequest request) {
        return OrderResponse.from(service.place(request.userId(), request.sku(), request.quantity()));
    }

    @GetMapping("/{id}")
    OrderResponse get(@PathVariable Long id) {
        return OrderResponse.from(service.get(id));
    }
}
