package com.shopflow.monolith.catalog.application;

import com.shopflow.monolith.catalog.contract.CatalogQuery;
import com.shopflow.monolith.catalog.domain.ProductRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class CatalogQueryImpl implements CatalogQuery {

    private final ProductRepository products;

    CatalogQueryImpl(ProductRepository products) {
        this.products = products;
    }

    @Override
    public Optional<ProductView> findBySku(String sku) {
        return products.findBySku(sku).map(p -> new ProductView(p.getSku(), p.getName(), p.getPrice()));
    }
}
