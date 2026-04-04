package com.shopflow.monolith.catalog.application;

import com.shopflow.monolith.catalog.domain.Product;
import com.shopflow.monolith.catalog.domain.ProductRepository;
import com.shopflow.monolith.shared.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final ProductRepository products;

    public CatalogService(ProductRepository products) {
        this.products = products;
    }

    public Product create(String sku, String name, BigDecimal price) {
        return products.save(new Product(sku, name, price));
    }

    public Product getBySku(String sku) {
        return products.findBySku(sku).orElseThrow(() -> new NotFoundException("Product " + sku + " not found"));
    }

    public List<Product> list() {
        return products.findAll();
    }
}
