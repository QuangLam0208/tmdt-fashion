package com.fashion.app.controller;

import com.fashion.app.model.IndustryReport;
import com.fashion.app.model.MarketData;
import com.fashion.app.service.MarketAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin("*")
public class MarketAnalyticsController {

    @Autowired
    private MarketAnalyticsService marketAnalyticsService;

    @PostMapping("/upload-market-data")
    public ResponseEntity<?> uploadMarketData(@RequestParam("file") MultipartFile file) {
        try {
            marketAnalyticsService.processMarketDataCsv(file);
            return ResponseEntity.ok(Map.of("message", "Tải lên dữ liệu thị trường thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi xử lý file: " + e.getMessage()));
        }
    }

    @GetMapping("/market-comparison")
    public ResponseEntity<List<MarketData>> getMarketComparison() {
        return ResponseEntity.ok(marketAnalyticsService.getAllMarketData());
    }

    @GetMapping("/vecom-growth")
    public ResponseEntity<List<Map<String, Object>>> getVecomGrowth() {
        return ResponseEntity.ok(marketAnalyticsService.getVecomGrowthData());
    }

    @GetMapping("/younet-share")
    public ResponseEntity<Map<String, Object>> getYouNetEciData() {
        Map<String, Object> data = new HashMap<>();
        data.put("marketShare", marketAnalyticsService.getYouNetEciShareData());
        data.put("categoryGrowth", marketAnalyticsService.getYouNetEciGrowthData());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/price-avg-comparison")
    public ResponseEntity<List<Map<String, Object>>> getPriceAvgComparison() {
        return ResponseEntity.ok(marketAnalyticsService.getPriceComparison());
    }
}
