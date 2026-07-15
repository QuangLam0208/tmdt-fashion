/**
 * serviceWorkerRegistration.js
 * Đăng ký Service Worker cho PWA.
 * Gọi register() trong index.js để kích hoạt PWA.
 */

const isLocalhost = Boolean(
  window.location.hostname === 'localhost' ||
    window.location.hostname === '[::1]' ||
    window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4]\d|[01]?\d\d?)){3}$/)
);

/**
 * Đăng ký Service Worker.
 * @param {Object} config - { onSuccess, onUpdate, onOffline, onOnline }
 */
export function register(config = {}) {
  if ('serviceWorker' in navigator) {
    const swUrl = `${process.env.PUBLIC_URL}/sw.js`;

    window.addEventListener('load', () => {
      if (isLocalhost) {
        // Trên localhost: kiểm tra SW có hợp lệ không
        checkValidServiceWorker(swUrl, config);
        navigator.serviceWorker.ready.then(() => {
          console.log('[PWA] App đang chạy với Service Worker (localhost).');
        });
      } else {
        registerValidSW(swUrl, config);
      }
    });

    // Lắng nghe trạng thái online/offline
    window.addEventListener('online', () => {
      config.onOnline?.();
    });
    window.addEventListener('offline', () => {
      config.onOffline?.();
    });
  }
}

function registerValidSW(swUrl, config) {
  navigator.serviceWorker
    .register(swUrl)
    .then((registration) => {
      // Phát hiện cập nhật SW mới
      registration.onupdatefound = () => {
        const installingWorker = registration.installing;
        if (!installingWorker) return;

        installingWorker.onstatechange = () => {
          if (installingWorker.state === 'installed') {
            if (navigator.serviceWorker.controller) {
              // Có phiên bản mới — thông báo người dùng
              console.log('[PWA] Nội dung mới sẵn sàng. Tải lại để cập nhật.');
              config.onUpdate?.(registration);
            } else {
              // Cache lần đầu hoàn tất
              console.log('[PWA] Nội dung đã được cache để dùng offline.');
              config.onSuccess?.(registration);
            }
          }
        };
      };
    })
    .catch((error) => {
      console.error('[PWA] Lỗi khi đăng ký Service Worker:', error);
    });
}

function checkValidServiceWorker(swUrl, config) {
  fetch(swUrl, { headers: { 'Service-Worker': 'script' } })
    .then((response) => {
      const contentType = response.headers.get('content-type');
      if (
        response.status === 404 ||
        (contentType != null && !contentType.includes('javascript'))
      ) {
        // SW không tìm thấy → reload
        navigator.serviceWorker.ready.then((registration) => {
          registration.unregister().then(() => {
            window.location.reload();
          });
        });
      } else {
        registerValidSW(swUrl, config);
      }
    })
    .catch(() => {
      console.log('[PWA] Không có kết nối internet. App đang chạy ở chế độ offline.');
    });
}

/**
 * Hủy đăng ký Service Worker.
 */
export function unregister() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.ready
      .then((registration) => registration.unregister())
      .catch((error) => console.error(error.message));
  }
}
