/**
 * PWAPrompt.js — Component hiển thị thông báo PWA
 * - Banner cài đặt app (Install Prompt)
 * - Banner cập nhật app (Update Available)
 * - Banner offline
 */
import React, { useEffect } from 'react';
import { notification, Button, Space, Tag } from 'antd';
import {
  DownloadOutlined,
  SyncOutlined,
  WifiOutlined,
  DisconnectOutlined,
} from '@ant-design/icons';
import { usePWA } from '../hooks/usePWA';
import { register } from '../serviceWorkerRegistration';

const PWAPrompt = () => {
  const {
    installPrompt,
    isInstallable,
    isOffline,
    hasUpdate,
    setHasUpdate,
    setSwRegistration,
    updateApp,
  } = usePWA();

  const [api, contextHolder] = notification.useNotification();

  // Đăng ký Service Worker
  useEffect(() => {
    register({
      onSuccess: () => {
        api.success({
          message: 'App sẵn sàng offline',
          description: 'TMDT Fashion đã được cache. Bạn có thể dùng khi không có mạng.',
          placement: 'bottomRight',
          duration: 5,
          icon: <WifiOutlined style={{ color: '#7c3aed' }} />,
        });
      },
      onUpdate: (reg) => {
        setSwRegistration(reg);
        setHasUpdate(true);
      },
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Banner cài đặt PWA
  useEffect(() => {
    if (!isInstallable) return;

    const key = 'pwa-install';
    api.info({
      key,
      message: 'Cài đặt TMDT Fashion',
      description: 'Thêm ứng dụng vào màn hình chính để trải nghiệm tốt hơn — nhanh hơn, offline được!',
      placement: 'bottomRight',
      duration: 0,
      icon: <DownloadOutlined style={{ color: '#7c3aed' }} />,
      btn: (
        <Space>
          <Button size="small" onClick={() => api.destroy(key)}>
            Để sau
          </Button>
          <Button
            type="primary"
            size="small"
            style={{ background: '#7c3aed', borderColor: '#7c3aed' }}
            onClick={() => {
              installPrompt();
              api.destroy(key);
            }}
          >
            Cài đặt ngay
          </Button>
        </Space>
      ),
    });
  }, [isInstallable]); // eslint-disable-line

  // Banner cập nhật
  useEffect(() => {
    if (!hasUpdate) return;

    const key = 'pwa-update';
    api.warning({
      key,
      message: 'Có bản cập nhật mới',
      description: 'Phiên bản mới của TMDT Fashion đã sẵn sàng. Tải lại để áp dụng.',
      placement: 'bottomRight',
      duration: 0,
      icon: <SyncOutlined spin style={{ color: '#d97706' }} />,
      btn: (
        <Space>
          <Button size="small" onClick={() => api.destroy(key)}>
            Để sau
          </Button>
          <Button
            type="primary"
            size="small"
            style={{ background: '#d97706', borderColor: '#d97706' }}
            onClick={() => {
              updateApp();
              api.destroy(key);
            }}
          >
            Cập nhật ngay
          </Button>
        </Space>
      ),
    });
  }, [hasUpdate]); // eslint-disable-line

  // Banner offline/online
  useEffect(() => {
    const key = 'pwa-network';
    if (isOffline) {
      api.error({
        key,
        message: 'Mất kết nối mạng',
        description: 'Bạn đang offline. Một số tính năng có thể không khả dụng.',
        placement: 'bottomRight',
        duration: 0,
        icon: <DisconnectOutlined style={{ color: '#dc2626' }} />,
      });
    } else {
      api.destroy(key);
    }
  }, [isOffline]); // eslint-disable-line

  return (
    <>
      {contextHolder}
      {/* Badge hiển thị trạng thái mạng ở góc dưới trái */}
      {isOffline && (
        <div style={{
          position: 'fixed',
          bottom: '1rem',
          left: '1rem',
          zIndex: 9999,
        }}>
          <Tag
            icon={<DisconnectOutlined />}
            color="error"
            style={{
              fontSize: '0.85rem',
              padding: '4px 10px',
              borderRadius: '9999px',
              boxShadow: '0 2px 8px rgba(0,0,0,0.3)',
            }}
          >
            Offline
          </Tag>
        </div>
      )}
    </>
  );
};

export default PWAPrompt;
