import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Typography, Upload, Button, message, Table, Spin } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { 
  ComposedChart, Line, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, 
  BarChart, Cell 
} from 'recharts';
import axiosInstance from '../../../shared/config/axiosInstance';
import { ADMIN_TOKEN_KEY } from '../../../shared/config/axiosInstance';

const { Title, Text } = Typography;

const MarketAnalyticsDashboard = () => {
  const [loading, setLoading] = useState(false);
  const [vecomData, setVecomData] = useState([]);
  const [younetData, setYounetData] = useState({ marketShare: [], categoryGrowth: [] });
  const [marketPrices, setMarketPrices] = useState([]);
  const [avgPrices, setAvgPrices] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [vecomRes, younetRes, pricesRes, avgRes] = await Promise.all([
        axiosInstance.get('/api/admin/analytics/vecom-growth'),
        axiosInstance.get('/api/admin/analytics/younet-share'),
        axiosInstance.get('/api/admin/analytics/market-comparison'),
        axiosInstance.get('/api/admin/analytics/price-avg-comparison')
      ]);

      // Format VECOM data
      const vecomRaw = vecomRes.data;
      const vecomFormatted = [];
      const years = [...new Set(vecomRaw.map(item => item.year))];
      years.forEach(year => {
        const sizeItem = vecomRaw.find(i => i.year === year && i.metricType === 'B2C_MARKET_SIZE');
        const growthItem = vecomRaw.find(i => i.year === year && i.metricType === 'B2C_GROWTH_RATE');
        vecomFormatted.push({
          year: year.toString(),
          marketSize: sizeItem ? sizeItem.value : 0,
          growthRate: growthItem ? growthItem.value : 0,
          internalGrowthRate: growthItem ? growthItem.internalGrowthRate : 0
        });
      });
      setVecomData(vecomFormatted);

      // Format YouNet data
      setYounetData(younetRes.data);

      // Format Market Prices
      setMarketPrices(pricesRes.data);

      // Format Avg Prices
      setAvgPrices(avgRes.data);

    } catch (error) {
      console.error('Error fetching analytics data:', error);
      message.error('Lỗi khi tải dữ liệu thị trường');
    } finally {
      setLoading(false);
    }
  };

  const uploadProps = {
    name: 'file',
    action: 'http://localhost:8080/api/admin/analytics/upload-market-data',
    accept: '.csv',
    headers: {
      Authorization: `Bearer ${localStorage.getItem(ADMIN_TOKEN_KEY)}`,
    },
    onChange(info) {
      if (info.file.status === 'done') {
        message.success(`${info.file.name} tải lên thành công.`);
        fetchData(); // reload data
      } else if (info.file.status === 'error') {
        message.error(`${info.file.name} tải lên thất bại.`);
      }
    },
  };

  const priceColumns = [
    { title: 'Sàn', dataIndex: 'platform', key: 'platform' },
    { title: 'Tên Sản Phẩm', dataIndex: 'productName', key: 'productName' },
    { title: 'Danh mục', dataIndex: 'category', key: 'category' },
    { title: 'Giá bán (VNĐ)', dataIndex: 'price', key: 'price', render: (val) => val.toLocaleString('vi-VN') },
    { title: 'Ngày lấy dữ liệu', dataIndex: 'crawledDate', key: 'crawledDate' },
  ];

  // Colors for YouNet Chart
  const COLORS = ['#6366f1', '#ec4899', '#3b82f6', '#ef4444', '#f59e0b', '#10b981', '#9ca3af'];

  if (loading) return <div style={{ textAlign: 'center', padding: '50px' }}><Spin size="large" /></div>;

  return (
    <div style={{ padding: '24px' }}>
      <Title level={3}>Market Analytics Dashboard</Title>

      <Row gutter={[24, 24]}>
        {/* VECOM Chart */}
        <Col span={24}>
          <Card title="Tốc độ tăng trưởng thương mại điện tử B2C (2014-2024) - Nguồn: VECOM" bordered={false}>
            <div style={{ height: 400 }}>
              <ResponsiveContainer width="100%" height="100%">
                <ComposedChart data={vecomData} margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
                  <CartesianGrid stroke="#f5f5f5" />
                  <XAxis dataKey="year" />
                  <YAxis yAxisId="left" orientation="left" stroke="#82ca9d" label={{ value: 'Quy mô (tỷ USD)', angle: -90, position: 'insideLeft' }} />
                  <YAxis yAxisId="right" orientation="right" stroke="#ff7300" label={{ value: 'Tăng trưởng (%)', angle: 90, position: 'insideRight' }} />
                  <Tooltip />
                  <Legend />
                  <Bar yAxisId="left" dataKey="marketSize" barSize={30} fill="#82ca9d" name="Quy mô TMĐT (tỷ USD)" />
                  <Line yAxisId="right" type="monotone" dataKey="growthRate" stroke="#ff7300" strokeWidth={3} name="Tốc độ tăng trưởng thị trường (%)" dot={{ r: 5 }} />
                  <Line yAxisId="right" type="monotone" dataKey="internalGrowthRate" stroke="#6366f1" strokeWidth={3} name="Tốc độ tăng trưởng doanh thu nội bộ (%)" strokeDasharray="5 5" dot={{ r: 5 }} />
                </ComposedChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Col>

        {/* YouNet ECI Chart */}
        <Col span={12}>
          <Card title="Thị phần Ngành hàng Q4/2024 - Nguồn: YouNet ECI" bordered={false}>
            <div style={{ height: 350 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={younetData.marketShare} margin={{ top: 20, right: 30, left: 20, bottom: 5 }} layout="vertical">
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis type="number" />
                  <YAxis dataKey="category" type="category" width={150} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="value" name="Thị phần chung (%)" barSize={15} fill="#f59e0b" />
                  <Bar dataKey="internalShare" name="Thị phần nội bộ (%)" barSize={15} fill="#6366f1" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Col>
        
        {/* YouNet ECI Growth Chart */}
        <Col span={12}>
          <Card title="Tốc độ tăng trưởng Ngành hàng YoY Q4/2024 - Nguồn: YouNet ECI" bordered={false}>
            <div style={{ height: 350 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={younetData.categoryGrowth} margin={{ top: 20, right: 30, left: 20, bottom: 5 }} layout="vertical">
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis type="number" />
                  <YAxis dataKey="category" type="category" width={150} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="value" name="Tăng trưởng (%)" barSize={20}>
                    {younetData.categoryGrowth.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.value >= 0 ? '#10b981' : '#ef4444'} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Col>

        {/* Price Comparison */}
        <Col span={24}>
          <Card title="So sánh Giá Bán Trung Bình (Nội bộ vs Đối thủ)" bordered={false}>
            <div style={{ height: 400 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={avgPrices} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="category" />
                  <YAxis label={{ value: 'Giá bán (VNĐ)', angle: -90, position: 'insideLeft' }} />
                  <Tooltip formatter={(value) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)} />
                  <Legend />
                  <Bar dataKey="internalPrice" name="Giá Nội bộ" fill="#6366f1" />
                  <Bar dataKey="shopeePrice" name="Giá Shopee" fill="#f97316" />
                  <Bar dataKey="lazadaPrice" name="Giá Lazada" fill="#0ea5e9" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Col>

        {/* Competitor Pricing Upload */}
        <Col span={24}>
          <Card 
            title="Dữ liệu Giá Thị Trường (Đối thủ)" 
            bordered={false}
            extra={
              <Upload {...uploadProps} showUploadList={false}>
                <Button icon={<UploadOutlined />} type="primary">Tải lên CSV</Button>
              </Upload>
            }
          >
            <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
              Tải lên file CSV chứa dữ liệu cào được từ Shopee/Lazada (cột: platform, product_name, category, price, crawled_date)
            </Text>
            <Table 
              dataSource={marketPrices} 
              columns={priceColumns} 
              rowKey="id" 
              pagination={{ pageSize: 5 }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default MarketAnalyticsDashboard;
