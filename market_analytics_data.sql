-- Dữ liệu hạt giống (Seed data) cho bảng IndustryReport

CREATE TABLE IF NOT EXISTS `industry_report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `source` varchar(255) NOT NULL,
  `year` int DEFAULT NULL,
  `quarter` int DEFAULT NULL,
  `metric_type` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `value` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `market_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `platform` varchar(255) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `crawled_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dữ liệu từ VECOM: Tốc độ tăng trưởng B2C (2014-2024)
INSERT INTO industry_report (source, year, quarter, metric_type, category, value) VALUES
('VECOM', 2014, 0, 'B2C_MARKET_SIZE', 'ALL', 2.97),
('VECOM', 2014, 0, 'B2C_GROWTH_RATE', 'ALL', 0.0),
('VECOM', 2015, 0, 'B2C_MARKET_SIZE', 'ALL', 4.07),
('VECOM', 2015, 0, 'B2C_GROWTH_RATE', 'ALL', 37.0),
('VECOM', 2016, 0, 'B2C_MARKET_SIZE', 'ALL', 5.0),
('VECOM', 2016, 0, 'B2C_GROWTH_RATE', 'ALL', 23.0),
('VECOM', 2017, 0, 'B2C_MARKET_SIZE', 'ALL', 6.2),
('VECOM', 2017, 0, 'B2C_GROWTH_RATE', 'ALL', 24.0),
('VECOM', 2018, 0, 'B2C_MARKET_SIZE', 'ALL', 8.06),
('VECOM', 2018, 0, 'B2C_GROWTH_RATE', 'ALL', 30.0),
('VECOM', 2019, 0, 'B2C_MARKET_SIZE', 'ALL', 10.08),
('VECOM', 2019, 0, 'B2C_GROWTH_RATE', 'ALL', 25.0),
('VECOM', 2020, 0, 'B2C_MARKET_SIZE', 'ALL', 11.8),
('VECOM', 2020, 0, 'B2C_GROWTH_RATE', 'ALL', 18.0),
('VECOM', 2021, 0, 'B2C_MARKET_SIZE', 'ALL', 13.7),
('VECOM', 2021, 0, 'B2C_GROWTH_RATE', 'ALL', 16.0),
('VECOM', 2022, 0, 'B2C_MARKET_SIZE', 'ALL', 16.4),
('VECOM', 2022, 0, 'B2C_GROWTH_RATE', 'ALL', 20.0),
('VECOM', 2023, 0, 'B2C_MARKET_SIZE', 'ALL', 20.5),
('VECOM', 2023, 0, 'B2C_GROWTH_RATE', 'ALL', 25.0),
('VECOM', 2024, 0, 'B2C_MARKET_SIZE', 'ALL', 25.0),
('VECOM', 2024, 0, 'B2C_GROWTH_RATE', 'ALL', 30.0);

-- Dữ liệu từ YouNet ECI: Tỷ trọng ngành hàng Q4/2024 (Market Share %)
INSERT INTO industry_report (source, year, quarter, metric_type, category, value) VALUES
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'Thời trang & phụ kiện', 28.1),
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'Sắc đẹp', 12.8),
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'FMCG', 18.5),
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'Điện gia dụng & công nghệ', 13.4),
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'Nhà cửa & đời sống', 11.0),
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'Sức khỏe', 3.8),
('YOUNET_ECI', 2024, 4, 'MARKET_SHARE', 'Khác', 12.4);

-- Dữ liệu từ YouNet ECI: Tốc độ tăng trưởng ngành hàng Q4/2024 (YoY Growth %)
INSERT INTO industry_report (source, year, quarter, metric_type, category, value) VALUES
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'Thời trang & phụ kiện', 11.0),
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'Sắc đẹp', 9.0),
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'FMCG', 47.0),
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'Điện gia dụng & công nghệ', -19.0),
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'Nhà cửa & đời sống', -2.0),
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'Sức khỏe', -3.0),
('YOUNET_ECI', 2024, 4, 'CATEGORY_GROWTH', 'Khác', 4.0);
