import React, { useState, useEffect } from 'react';
import { Space, Typography } from 'antd';
import { ClockCircleOutlined, ThunderboltFilled } from '@ant-design/icons';

const { Text } = Typography;

const FlashSaleTimer = ({ targetDate }) => {
  const [timeLeft, setTimeLeft] = useState({ hours: 0, minutes: 0, seconds: 0 });

  useEffect(() => {
    const interval = setInterval(() => {
      const now = new Date().getTime();
      const distance = new Date(targetDate).getTime() - now;

      if (distance <= 0) {
        clearInterval(interval);
        setTimeLeft({ hours: 0, minutes: 0, seconds: 0 });
      } else {
        setTimeLeft({
          hours: Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
          minutes: Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60)),
          seconds: Math.floor((distance % (1000 * 60)) / 1000)
        });
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [targetDate]);

  const blockStyle = {
    background: '#ff4d4f', 
    color: '#fff', 
    padding: '2px 6px', 
    borderRadius: '4px', 
    fontWeight: 'bold',
    fontSize: '15px',
    display: 'inline-block',
    minWidth: '28px',
    textAlign: 'center'
  };

  return (
    <div style={{ background: 'linear-gradient(90deg, #fff1f0 0%, #fff 100%)', padding: '12px 16px', borderRadius: 8, marginBottom: 16, borderLeft: '4px solid #ff4d4f' }}>
      <Space align="center" style={{ width: '100%', justifyContent: 'space-between', flexWrap: 'wrap' }}>
        <Space>
          <ThunderboltFilled style={{ color: '#ff4d4f', fontSize: 20 }} />
          <Text strong style={{ color: '#ff4d4f', fontSize: 16 }}>ĐANG FLASH SALE</Text>
        </Space>
        <Space size={4} align="center">
          <ClockCircleOutlined style={{ color: '#888', marginRight: 4 }} />
          <span style={{ fontSize: 13, color: '#666', marginRight: 4 }}>Kết thúc trong:</span>
          <div style={blockStyle}>{String(timeLeft.hours).padStart(2, '0')}</div>
          <span style={{ color: '#ff4d4f', fontWeight: 'bold' }}>:</span>
          <div style={blockStyle}>{String(timeLeft.minutes).padStart(2, '0')}</div>
          <span style={{ color: '#ff4d4f', fontWeight: 'bold' }}>:</span>
          <div style={blockStyle}>{String(timeLeft.seconds).padStart(2, '0')}</div>
        </Space>
      </Space>
    </div>
  );
};

export default FlashSaleTimer;
