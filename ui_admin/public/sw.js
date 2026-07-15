/* ============================================================
   Service Worker — TMDT Fashion PWA
   Chiến lược:
     - Static assets (JS/CSS/img/fonts): Cache First
     - API calls (/api/*):               Network First → fallback cache
     - HTML navigation:                  Network First → fallback /offline.html
   ============================================================ */

const CACHE_NAME = 'tmdt-fashion-v1';
const OFFLINE_URL = '/offline.html';

// Các file được pre-cache khi SW được cài đặt
const PRECACHE_URLS = [
  '/',
  '/offline.html',
  '/manifest.json',
];

// ─── Install ──────────────────────────────────────────────
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(PRECACHE_URLS);
    })
  );
  // Kích hoạt ngay lập tức, không chờ tab cũ đóng
  self.skipWaiting();
});

// ─── Activate ─────────────────────────────────────────────
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames
          .filter((name) => name !== CACHE_NAME)
          .map((name) => caches.delete(name))
      );
    })
  );
  // Nhận quyền kiểm soát toàn bộ tab ngay lập tức
  self.clients.claim();
});

// ─── Fetch ────────────────────────────────────────────────
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // Bỏ qua các request không phải HTTP(S)
  if (!request.url.startsWith('http')) return;

  // Bỏ qua Chrome extension requests
  if (url.protocol === 'chrome-extension:') return;

  // API calls → Network First
  if (url.pathname.startsWith('/api/')) {
    event.respondWith(networkFirst(request));
    return;
  }

  // HTML navigation → Network First với offline fallback
  if (request.mode === 'navigate') {
    event.respondWith(
      fetch(request).catch(() => caches.match(OFFLINE_URL))
    );
    return;
  }

  // Static assets → Cache First
  event.respondWith(cacheFirst(request));
});

// ─── Strategies ───────────────────────────────────────────

/** Cache First: trả về từ cache nếu có, nếu không fetch rồi cache lại */
async function cacheFirst(request) {
  const cached = await caches.match(request);
  if (cached) return cached;

  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      const cache = await caches.open(CACHE_NAME);
      cache.put(request, networkResponse.clone());
    }
    return networkResponse;
  } catch {
    return new Response('Resource not available offline', { status: 503 });
  }
}

/** Network First: fetch trước, nếu lỗi thì fallback về cache */
async function networkFirst(request) {
  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      const cache = await caches.open(CACHE_NAME);
      cache.put(request, networkResponse.clone());
    }
    return networkResponse;
  } catch {
    const cached = await caches.match(request);
    return cached || new Response(JSON.stringify({ error: 'Offline' }), {
      status: 503,
      headers: { 'Content-Type': 'application/json' },
    });
  }
}

// ─── Push Notifications ───────────────────────────────────
self.addEventListener('push', (event) => {
  const data = event.data?.json() ?? {};
  const title = data.title || 'TMDT Fashion';
  const options = {
    body: data.body || 'Bạn có thông báo mới!',
    icon: '/logo192.png',
    badge: '/logo192.png',
    vibrate: [100, 50, 100],
    data: { url: data.url || '/' },
  };
  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const targetUrl = event.notification.data?.url || '/';
  event.waitUntil(
    clients.matchAll({ type: 'window' }).then((windowClients) => {
      for (const client of windowClients) {
        if (client.url === targetUrl && 'focus' in client) {
          return client.focus();
        }
      }
      if (clients.openWindow) return clients.openWindow(targetUrl);
    })
  );
});
