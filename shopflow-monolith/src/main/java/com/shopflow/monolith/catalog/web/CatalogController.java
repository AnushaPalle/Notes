package com.shopflow.monolith.catalog.web;

import com.shopflow.monolith.catalog.application.CatalogService;
import com.shopflow.monolith.catalog.domain.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
class CatalogController {

    record CreateProductRequest(@NotBlank String sku, @NotBlank String name,
                                @NotNull @DecimalMin("0.01") BigDecimal price) {
    }

    record ProductResponse(String sku, String name, BigDecimal price) {
        static ProductResponse from(Product p) {
            return new ProductResponse(p.getSku(), p.getName(), p.getPrice());
        }
    }

    private final CatalogService service;

    CatalogController(CatalogService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return ProductResponse.from(service.create(request.sku(), request.name(), request.price()));
    }

    @GetMapping("/{sku}")
    ProductResponse get(@PathVariable String sku) {
        return ProductResponse.from(service.getBySku(sku));
    }

    @GetMapping
    List<ProductResponse> list() {
        return service.list().stream().map(ProductResponse::from).toList();
    }
}
