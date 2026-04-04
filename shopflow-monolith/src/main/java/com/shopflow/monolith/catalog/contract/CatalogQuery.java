package com.shopflow.monolith.catalog.contract;

import java.math.BigDecimal;
import java.util.Optional;

/** Public contract of the catalog module. */
public interface CatalogQuery {

    record ProductView(String sku, String name, BigDecimal price) {
    }

    Optional<ProductView> findBySku(String sku);
}
