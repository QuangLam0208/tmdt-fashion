/**
 * usePWA.js — Custom Hook quản lý PWA
 *
 * Trả về:
 *   - installPrompt: Gọi hàm này để hiện hộp thoại cài đặt app
 *   - isInstallable: true nếu có thể cài đặt (chưa cài)
 *   - isInstalled: true nếu đã cài đặt (chạy standalone)
 *   - isOffline: true nếu đang offline
 *   - hasUpdate: true nếu có bản cập nhật mới
 *   - updateApp: Hàm tải lại để áp dụng bản cập nhật
 */
import { useState, useEffect, useCallback } from 'react';

export function usePWA() {
  const [deferredPrompt, setDeferredPrompt] = useState(null);
  const [isInstallable, setIsInstallable] = useState(false);
  const [isInstalled, setIsInstalled] = useState(false);
  const [isOffline, setIsOffline] = useState(!navigator.onLine);
  const [hasUpdate, setHasUpdate] = useState(false);
  const [swRegistration, setSwRegistration] = useState(null);

  useEffect(() => {
    // Kiểm tra đã cài đặt chưa
    const standalone =
      window.matchMedia('(display-mode: standalone)').matches ||
      window.navigator.standalone === true;
    setIsInstalled(standalone);

    // Bắt sự kiện beforeinstallprompt
    const handleBeforeInstall = (e) => {
      e.preventDefault();
      setDeferredPrompt(e);
      setIsInstallable(true);
    };

    // Khi app được cài đặt
    const handleAppInstalled = () => {
      setIsInstallable(false);
      setDeferredPrompt(null);
      setIsInstalled(true);
    };

    // Online/offline
    const handleOnline = () => setIsOffline(false);
    const handleOffline = () => setIsOffline(true);

    window.addEventListener('beforeinstallprompt', handleBeforeInstall);
    window.addEventListener('appinstalled', handleAppInstalled);
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('beforeinstallprompt', handleBeforeInstall);
      window.removeEventListener('appinstalled', handleAppInstalled);
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  /** Kích hoạt hộp thoại cài đặt PWA */
  const installPrompt = useCallback(async () => {
    if (!deferredPrompt) return;
    deferredPrompt.prompt();
    const { outcome } = await deferredPrompt.userChoice;
    if (outcome === 'accepted') {
      setIsInstallable(false);
      setDeferredPrompt(null);
    }
  }, [deferredPrompt]);

  /** Tải lại trang để áp dụng bản cập nhật */
  const updateApp = useCallback(() => {
    if (swRegistration?.waiting) {
      swRegistration.waiting.postMessage({ type: 'SKIP_WAITING' });
    }
    window.location.reload();
  }, [swRegistration]);

  return {
    installPrompt,
    isInstallable,
    isInstalled,
    isOffline,
    hasUpdate,
    setHasUpdate,
    setSwRegistration,
    updateApp,
  };
}
