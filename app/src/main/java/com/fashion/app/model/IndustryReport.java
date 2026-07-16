package com.fashion.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "industry_report")
@Data
@NoArgsConstructor
public class IndustryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source; // VECOM, YOUNET_ECI

    private Integer year;
    
    private Integer quarter; // 0 if it's a yearly report

    @Column(name = "metric_type")
    private String metricType; // B2C_MARKET_SIZE, B2C_GROWTH_RATE, MARKET_SHARE, CATEGORY_GROWTH

    private String category; // e.g., ALL, Thời trang & phụ kiện

    private Double value;
}
