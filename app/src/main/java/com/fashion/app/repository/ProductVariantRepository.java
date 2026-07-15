package com.fashion.app.repository;

import com.fashion.app.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    @org.springframework.data.jpa.repository.Query("SELECT p.category.name, AVG(pv.price) FROM ProductVariant pv JOIN pv.product p GROUP BY p.category.name")
    java.util.List<Object[]> getAveragePricesByCategory();
}
