package com.fashion.app.repository;

import com.fashion.app.model.IndustryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndustryReportRepository extends JpaRepository<IndustryReport, Long> {
    List<IndustryReport> findBySourceOrderByYearAsc(String source);
    List<IndustryReport> findBySourceAndMetricTypeOrderByValueDesc(String source, String metricType);
}
