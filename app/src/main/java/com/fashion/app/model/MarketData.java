package com.fashion.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "market_data")
@Data
@NoArgsConstructor
public class MarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String platform; // e.g., Shopee, Lazada

    @Column(name = "product_name", nullable = false)
    private String productName;

    private String category; // e.g., Áo, Quần, Giày

    private Double price;

    @Column(name = "crawled_date")
    private LocalDate crawledDate;
}
