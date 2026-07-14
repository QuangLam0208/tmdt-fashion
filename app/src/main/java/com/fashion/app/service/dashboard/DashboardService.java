package com.fashion.app.service.dashboard;

import com.fashion.app.dto.response.DashboardResponseDTO;
import com.fashion.app.dto.response.RevenueReturnChartDTO;

import java.util.Date;
import java.util.List;

public interface DashboardService {
    DashboardResponseDTO getDashboardData();
    List<RevenueReturnChartDTO> getRevenueAndReturnRateChart(Date startDate, Date endDate);
}
