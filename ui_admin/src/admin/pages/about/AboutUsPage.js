import React from 'react';
import { Card, Col, Row, Space, Typography, Button } from 'antd';
import { ArrowLeftOutlined, InfoCircleOutlined, ShopOutlined, TeamOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import PageHeader from '../../components/PageHeader';

const { Paragraph, Text, Title } = Typography;

const highlights = [
  {
    icon: <InfoCircleOutlined style={{ fontSize: 24, color: '#1677ff' }} />,
    title: 'Câu chuyện thương hiệu',
    description: 'Tóm tắt hành trình xây dựng thương hiệu, định vị phong cách và giá trị cốt lõi của cửa hàng.',
  },
  {
    icon: <ShopOutlined style={{ fontSize: 24, color: '#13c2c2' }} />,
    title: 'Thông tin vận hành',
    description: 'Nơi bạn có thể cập nhật nhanh nội dung giới thiệu, hình ảnh và thông điệp hiển thị tới khách hàng.',
  },
  {
    icon: <TeamOutlined style={{ fontSize: 24, color: '#fa8c16' }} />,
    title: 'Đội ngũ & giá trị',
    description: 'Khu vực dành cho các điểm mạnh của đội ngũ, cam kết dịch vụ và định hướng phát triển dài hạn.',
  },
];

const AboutUsPage = () => {
  const navigate = useNavigate();

  return (
    <div>
      <PageHeader title="Về chúng tôi" />

      <Card style={{ borderRadius: 12, marginBottom: 24 }}>
        <Title level={4} style={{ marginTop: 0 }}>Trang giới thiệu thương hiệu</Title>
        <Paragraph style={{ marginBottom: 0, color: '#4b5563' }}>
          Đây là khu vực quản lý nội dung giới thiệu của cửa hàng. Bạn có thể dùng trang này để
          theo dõi, cập nhật hoặc mở rộng phần giới thiệu thương hiệu trong hệ thống quản trị.
        </Paragraph>
      </Card>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        {highlights.map((item) => (
          <Col xs={24} md={8} key={item.title}>
            <Card style={{ height: '100%', borderRadius: 12 }}>
              <Space align="start" size={16}>
                <div style={{ width: 44, height: 44, borderRadius: 12, background: '#f8fafc', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  {item.icon}
                </div>
                <div>
                  <Title level={5} style={{ marginTop: 0, marginBottom: 8 }}>{item.title}</Title>
                  <Text type="secondary">{item.description}</Text>
                </div>
              </Space>
            </Card>
          </Col>
        ))}
      </Row>

      <Card style={{ borderRadius: 12 }}>
        <Space direction="vertical" size={12}>
          <Text strong>Gợi ý tiếp theo</Text>
          <Text type="secondary">
            Nếu bạn muốn trang này hiển thị nội dung chi tiết như ở website khách hàng, có thể nối
            thêm dữ liệu từ backend hoặc tách thành màn hình chỉnh sửa riêng.
          </Text>
          <Button type="primary" icon={<ArrowLeftOutlined />} onClick={() => navigate('/admin/dashboard')}>
            Về Dashboard
          </Button>
        </Space>
      </Card>
    </div>
  );
};

export default AboutUsPage;