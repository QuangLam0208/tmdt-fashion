package com.fashion.app.service;

import com.fashion.app.model.IndustryReport;
import com.fashion.app.model.MarketData;
import com.fashion.app.repository.IndustryReportRepository;
import com.fashion.app.repository.MarketDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketAnalyticsService {

    @Autowired
    private MarketDataRepository marketDataRepository;

    @Autowired
    private IndustryReportRepository industryReportRepository;

    @Autowired
    private com.fashion.app.repository.OrderRepository orderRepository;

    @Autowired
    private com.fashion.app.repository.OrderItemRepository orderItemRepository;

    @Autowired
    private com.fashion.app.repository.ProductVariantRepository productVariantRepository;

    public void processMarketDataCsv(MultipartFile file) throws Exception {
        List<MarketData> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Bỏ qua header
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5) {
                    MarketData data = new MarketData();
                    data.setPlatform(values[0].trim());
                    data.setProductName(values[1].trim());
                    data.setCategory(values[2].trim());
                    try {
                        data.setPrice(Double.parseDouble(values[3].trim()));
                    } catch (NumberFormatException e) {
                        data.setPrice(0.0);
                    }
                    try {
                        data.setCrawledDate(LocalDate.parse(values[4].trim()));
                    } catch (Exception e) {
                        data.setCrawledDate(LocalDate.now());
                    }
                    dataList.add(data);
                }
            }
        }
        marketDataRepository.saveAll(dataList);
    }

    public List<MarketData> getAllMarketData() {
        return marketDataRepository.findAll();
    }

    public List<Map<String, Object>> getVecomGrowthData() {
        List<IndustryReport> reports = industryReportRepository.findBySourceOrderByYearAsc("VECOM");
        List<Object[]> yearlyRevenue = orderRepository.findYearlyRevenue();
        
        List<Map<String, Object>> result = new ArrayList<>();
        double previousRevenue = 0;
        
        for (IndustryReport r : reports) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", r.getYear());
            map.put("metricType", r.getMetricType());
            map.put("value", r.getValue());
            
            if (r.getMetricType().equals("B2C_GROWTH_RATE")) {
                double currentRevenue = 0;
                for (Object[] row : yearlyRevenue) {
                    if (row[0] != null && (Integer) row[0] == r.getYear()) {
                        currentRevenue = ((Number) row[1]).doubleValue();
                        break;
                    }
                }
                
                double internalGrowth = 0;
                if (previousRevenue > 0) {
                    internalGrowth = ((currentRevenue - previousRevenue) / previousRevenue) * 100;
                }
                map.put("internalGrowthRate", Math.round(internalGrowth * 100.0) / 100.0);
                previousRevenue = currentRevenue;
            }
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getYouNetEciShareData() {
        List<IndustryReport> reports = industryReportRepository.findBySourceAndMetricTypeOrderByValueDesc("YOUNET_ECI", "MARKET_SHARE");
        List<Object[]> categoryRevenue = orderItemRepository.findCategoryRevenue();
        
        double totalInternalRevenue = 0;
        for (Object[] row : categoryRevenue) {
            totalInternalRevenue += ((Number) row[1]).doubleValue();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        // 1. Thêm dữ liệu YouNet ECI (đã map vào "Thời trang & phụ kiện" hoặc hiển thị chung)
        for (IndustryReport r : reports) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", r.getCategory());
            map.put("value", r.getValue());
            map.put("internalShare", 0.0); // Mặc định là 0
            
            // Nếu bạn muốn dồn tất cả doanh thu nội bộ vào "Thời trang & phụ kiện"
            if (r.getCategory().equals("Thời trang & phụ kiện") && totalInternalRevenue > 0) {
                map.put("internalShare", 100.0);
            }
            result.add(map);
        }
        
        // 2. Thêm các danh mục nội bộ để so sánh (Áo, Quần, Giày...)
        for (Object[] row : categoryRevenue) {
            if (row[0] == null) continue;
            String catName = row[0].toString();
            double rev = ((Number) row[1]).doubleValue();
            double share = totalInternalRevenue > 0 ? (rev / totalInternalRevenue) * 100 : 0;
            
            Map<String, Object> map = new HashMap<>();
            map.put("category", "Nội bộ: " + catName);
            map.put("value", 0.0); // Không có thị phần chung
            map.put("internalShare", Math.round(share * 100.0) / 100.0);
            result.add(map);
        }
        
        return result;
    }
    
    public List<IndustryReport> getYouNetEciGrowthData() {
        return industryReportRepository.findBySourceAndMetricTypeOrderByValueDesc("YOUNET_ECI", "CATEGORY_GROWTH");
    }

    public List<Map<String, Object>> getPriceComparison() {
        List<Object[]> internalPrices = productVariantRepository.getAveragePricesByCategory();
        List<Object[]> marketPrices = marketDataRepository.getAveragePricesByCategory();
        
        Map<String, Map<String, Object>> categoryMap = new HashMap<>();
        
        for (Object[] row : internalPrices) {
            if (row[0] == null) continue;
            String cat = row[0].toString();
            double avgPrice = ((Number) row[1]).doubleValue();
            
            categoryMap.putIfAbsent(cat, new HashMap<>());
            categoryMap.get(cat).put("category", cat);
            categoryMap.get(cat).put("internalPrice", Math.round(avgPrice));
            categoryMap.get(cat).put("shopeePrice", 0);
            categoryMap.get(cat).put("lazadaPrice", 0);
        }
        
        for (Object[] row : marketPrices) {
            if (row[0] == null) continue;
            String cat = row[0].toString();
            String platform = row[1].toString().toLowerCase();
            double avgPrice = ((Number) row[2]).doubleValue();
            
            categoryMap.putIfAbsent(cat, new HashMap<>());
            categoryMap.get(cat).put("category", cat);
            categoryMap.get(cat).putIfAbsent("internalPrice", 0);
            categoryMap.get(cat).putIfAbsent("shopeePrice", 0);
            categoryMap.get(cat).putIfAbsent("lazadaPrice", 0);
            
            if (platform.contains("shopee")) {
                categoryMap.get(cat).put("shopeePrice", Math.round(avgPrice));
            } else if (platform.contains("lazada")) {
                categoryMap.get(cat).put("lazadaPrice", Math.round(avgPrice));
            }
        }
        
        return new ArrayList<>(categoryMap.values());
    }
}
