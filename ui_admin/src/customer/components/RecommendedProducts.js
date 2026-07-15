import React, { useEffect, useState, useContext } from 'react';
import { Row, Col, Spin, Typography } from 'antd';
import ProductCard from './ProductCard';
import { shopProductService } from '../services/shopProductService';
import { CustomerAuthContext } from '../context/CustomerAuthContext';

const { Title } = Typography;

const RecommendedProducts = () => {
  const { currentUser } = useContext(CustomerAuthContext);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRecommendations = async () => {
      try {
        const userId = currentUser ? currentUser.id : null;
        const data = await shopProductService.getRecommendations(userId);
        setProducts(data?.data || data || []);
      } catch (error) {
        console.error('Lỗi lấy gợi ý sản phẩm:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendations();
  }, [currentUser]);

  if (loading) return <div style={{ textAlign: 'center', padding: '40px' }}><Spin size="large" /></div>;
  if (!products || products.length === 0) return null;

  return (
    <div style={{ marginTop: 48, marginBottom: 48 }}>
      <Title level={2} style={{ textAlign: 'center', marginBottom: 32, fontWeight: 700, color: '#e53935' }}>
        SẢN PHẨM GỢI Ý DÀNH RIÊNG CHO BẠN
      </Title>
      <Row gutter={[24, 24]}>
        {products.map(product => (
          <Col xs={12} sm={12} md={8} lg={6} key={product.id || product.productId}>
            <ProductCard product={product} />
          </Col>
        ))}
      </Row>
    </div>
  );
};

export default RecommendedProducts;
