package com.fashion.app.repository;

import com.fashion.app.model.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    List<MarketData> findByCategory(String category);

    @org.springframework.data.jpa.repository.Query("SELECT m.category, m.platform, AVG(m.price) FROM MarketData m GROUP BY m.category, m.platform")
    List<Object[]> getAveragePricesByCategory();
}
