// src/customer/layouts/Navbar.js
import React, { useEffect, useState } from 'react';
import {
  Badge, Button, Drawer, Avatar, Dropdown, message,
  Typography, Spin, List, Popover,
} from 'antd';
import {
  ShoppingCartOutlined, UserOutlined, LogoutOutlined,
  HeartOutlined, HistoryOutlined, WalletOutlined,
  StarOutlined, CheckOutlined, BellOutlined,
  MenuOutlined, CloseOutlined,
} from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import useCart         from '../hooks/useCart';
import useCustomerAuth from '../hooks/useCustomerAuth';
import CartDrawer      from '../components/CartDrawer';
import { notificationService } from '../services/notificationService';
import { formatDateTime } from '../../shared/utils/formatters';

const { Text } = Typography;

const NAV_LINKS = [
  ['/', 'Danh mục'],
  ['/shop', 'Sản phẩm'],
  ['/shop?sale=true', 'Sale'],
  ['/about', 'Về chúng tôi'],
];

const Navbar = () => {
  const { totalItems }  = useCart();
  const { isAuthenticated, currentUser, logout } = useCustomerAuth();
  const [cartOpen, setCartOpen]   = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const navigate = useNavigate();

  // ─── Notification state ───────────────────────────────────────────────────
  const [unreadCount, setUnreadCount]   = useState(0);
  const [notifications, setNotifications] = useState([]);
  const [loadingNotifs, setLoadingNotifs] = useState(false);
  const [markingAll, setMarkingAll]     = useState(false);
  const [popoverOpen, setPopoverOpen]   = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      notificationService
        .getUnreadCount()
        .then((res) => {
          const count = typeof res === 'number' ? res : res?.count || res?.data || 0;
          setUnreadCount(count);
        })
        .catch(() => {});
    }
  }, [isAuthenticated]);

  const fetchNotifications = async () => {
    setLoadingNotifs(true);
    try {
      const res = await notificationService.getNotifications({
        page: 0, size: 5, sort: 'createdAt,desc',
      });
      setNotifications(res?.content || res?.data?.content || res || []);
    } catch {
      message.error('Không thể tải danh sách thông báo');
    } finally {
      setLoadingNotifs(false);
    }
  };

  const handlePopoverChange = (open) => {
    setPopoverOpen(open);
    if (open) fetchNotifications();
  };

  const handleMarkAsRead = async (id, isRead) => {
    if (isRead) return;
    try {
      await notificationService.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) =>
          n.id === id || n.notificationId === id
            ? { ...n, isRead: true, read: true }
            : n
        )
      );
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch {}
  };

  const handleMarkAllAsRead = async () => {
    setMarkingAll(true);
    try {
      await notificationService.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true, read: true })));
      setUnreadCount(0);
    } catch {
      message.error('Có lỗi xảy ra khi cập nhật');
    } finally {
      setMarkingAll(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
    setMobileOpen(false);
  };

  const userMenu = [
    { key: 'profile',   label: 'Tài khoản của tôi',    icon: <UserOutlined />,      onClick: () => navigate('/account/profile') },
    { key: 'orders',    label: 'Đơn mua',               icon: <ShoppingCartOutlined />, onClick: () => navigate('/account/orders') },
    { key: 'returns',   label: 'Yêu cầu trả hàng',      icon: <HistoryOutlined />,   onClick: () => navigate('/account/returns') },
    { key: 'wallet',    label: 'Ví Voucher',             icon: <WalletOutlined />,    onClick: () => navigate('/account/wallet') },
    { key: 'reviews',   label: 'Đánh giá của tôi',      icon: <StarOutlined />,      onClick: () => navigate('/account/my-reviews') },
    { key: 'wishlist',  label: 'Yêu thích',             icon: <HeartOutlined />,     onClick: () => navigate('/wishlist') },
    { type: 'divider' },
    { key: 'logout',    label: 'Đăng xuất',             icon: <LogoutOutlined />,    danger: true, onClick: handleLogout },
  ];

  // ─── Notification Popover Content ─────────────────────────────────────────
  const notificationContent = (
    <div style={{ width: 340, padding: '0 8px' }}>
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', paddingBottom: 12, borderBottom:'1px solid #f0f0f0', marginBottom: 8 }}>
        <Text strong style={{ fontSize: 16 }}>Thông báo mới</Text>
        {unreadCount > 0 && (
          <Button type="link" size="small" onClick={handleMarkAllAsRead} loading={markingAll} style={{ fontSize: 13, padding: 0 }}>
            <CheckOutlined /> Đánh dấu tất cả đã đọc
          </Button>
        )}
      </div>

      {loadingNotifs && notifications.length === 0 ? (
        <div style={{ textAlign:'center', padding:'30px 0' }}><Spin /></div>
      ) : notifications.length === 0 ? (
        <div style={{ textAlign:'center', padding:'40px 0', color:'#999' }}>
          <BellOutlined style={{ fontSize: 32, color:'#e2e8f0', display:'block', marginBottom: 8 }} />
          Bạn chưa có thông báo nào
        </div>
      ) : (
        <div style={{ maxHeight: 400, overflowY:'auto', margin:'0 -8px' }}>
          <List
            dataSource={notifications}
            renderItem={(item) => {
              const isItemRead = item.isRead || item.read;
              const notifId    = item.id || item.notificationId;
              return (
                <List.Item
                  style={{ padding:'12px 16px', cursor:'pointer', backgroundColor: isItemRead ? '#fff' : '#f0f5ff', borderBottom:'1px solid #f0f0f0', transition:'background-color 0.3s' }}
                  onClick={() => handleMarkAsRead(notifId, isItemRead)}
                >
                  <List.Item.Meta
                    avatar={<div style={{ width:40, height:40, borderRadius:'50%', background:'#e6f4ff', display:'flex', alignItems:'center', justifyContent:'center' }}><BellOutlined style={{ color:'#1677ff', fontSize:18 }} /></div>}
                    title={
                      <div style={{ display:'flex', justifyContent:'space-between' }}>
                        <Text style={{ fontSize:14, fontWeight: isItemRead ? 500 : 700, color: isItemRead ? '#333' : '#000' }}>{item.title}</Text>
                        {!isItemRead && <div style={{ width:8, height:8, borderRadius:'50%', background:'#ff4d4f', marginTop:6 }} />}
                      </div>
                    }
                    description={
                      <div>
                        <div style={{ fontSize:13, color:'#666', marginBottom:6, lineHeight:1.4 }}>{item.message || item.content}</div>
                        <div style={{ fontSize:11, color:'#94a3b8' }}>{formatDateTime(item.createdAt)}</div>
                      </div>
                    }
                  />
                </List.Item>
              );
            }}
          />
        </div>
      )}
    </div>
  );

  // ─── Mobile Menu Drawer ───────────────────────────────────────────────────
  const mobileMenuContent = (
    <div style={{ padding:'8px 0' }}>
      {NAV_LINKS.map(([href, label]) => (
        <Link
          key={href}
          to={href}
          onClick={() => setMobileOpen(false)}
          style={{ display:'block', padding:'14px 24px', fontSize:16, fontWeight:600, color:'#1a1a1a', textDecoration:'none', borderBottom:'1px solid #f5f0ea' }}
        >
          {label}
        </Link>
      ))}

      {isAuthenticated ? (
        <>
          <div style={{ padding:'16px 24px 8px', color:'#999', fontSize:12, fontWeight:700, textTransform:'uppercase', letterSpacing:1 }}>Tài khoản</div>
          {[
            ['/account/profile', 'Tài khoản của tôi'],
            ['/account/orders', 'Đơn mua'],
            ['/account/returns', 'Yêu cầu trả hàng'],
            ['/account/wallet', 'Ví Voucher'],
            ['/wishlist', 'Yêu thích'],
          ].map(([href, label]) => (
            <Link key={href} to={href} onClick={() => setMobileOpen(false)} style={{ display:'block', padding:'12px 24px', fontSize:15, color:'#333', textDecoration:'none', borderBottom:'1px solid #faf8f5' }}>
              {label}
            </Link>
          ))}
          <div style={{ padding:'16px 24px' }}>
            <Button danger block onClick={handleLogout} style={{ borderRadius:8, fontWeight:600 }}>
              <LogoutOutlined /> Đăng xuất
            </Button>
          </div>
        </>
      ) : (
        <div style={{ padding:'16px 24px' }}>
          <Button type="primary" block onClick={() => { navigate('/login'); setMobileOpen(false); }} style={{ borderRadius:8, fontWeight:600, background:'#1a1a1a', borderColor:'#1a1a1a', height:44 }}>
            Đăng nhập
          </Button>
        </div>
      )}
    </div>
  );

  return (
    <>
      <header style={{ position:'sticky', top:0, zIndex:100, background:'#fff', borderBottom:'1px solid #f0ede8', height:64, display:'flex', alignItems:'center', justifyContent:'space-between', padding:'0 16px' }}>

        {/* ── Hamburger (chỉ mobile) ──────────────────────────────────────── */}
        <Button
          type="text"
          icon={<MenuOutlined style={{ fontSize: 20 }} />}
          onClick={() => setMobileOpen(true)}
          className="navbar-hamburger"
          aria-label="Mở menu"
        />

        {/* ── Desktop Nav (ẩn trên mobile) ────────────────────────────────── */}
        <nav className="navbar-desktop-nav">
          {NAV_LINKS.map(([href, label]) => (
            <Link key={href} to={href} style={{ color:'#333', textDecoration:'none', fontSize:14, fontWeight:500 }}>
              {label}
            </Link>
          ))}
        </nav>

        {/* ── Logo ────────────────────────────────────────────────────────── */}
        <Link to="/" style={{ fontFamily:'serif', fontSize:20, fontWeight:700, letterSpacing:3, color:'#1a1a1a', textDecoration:'none', position:'absolute', left:'50%', transform:'translateX(-50%)', whiteSpace:'nowrap' }}>
          ✦ FASHION ✦
        </Link>

        {/* ── Actions ─────────────────────────────────────────────────────── */}
        <div style={{ display:'flex', alignItems:'center', gap:8 }}>
          {/* Giỏ hàng */}
          <Badge count={totalItems} size="small" offset={[-4, 4]}>
            <Button type="text" icon={<ShoppingCartOutlined style={{ fontSize:20 }} />} onClick={() => setCartOpen(true)} />
          </Badge>

          {isAuthenticated ? (
            <div style={{ display:'flex', alignItems:'center', gap:8 }}>
              {/* Chuông thông báo */}
              <Popover
                content={notificationContent}
                trigger="click"
                placement="bottomRight"
                open={popoverOpen}
                onOpenChange={handlePopoverChange}
                overlayInnerStyle={{ padding:'12px 0 0 0', borderRadius:12, overflow:'hidden' }}
              >
                <Badge count={unreadCount} size="small" offset={[-4, 4]}>
                  <Button type="text" icon={<BellOutlined style={{ fontSize:20 }} />} />
                </Badge>
              </Popover>

              {/* Avatar + tên (ẩn tên trên mobile) */}
              <Dropdown menu={{ items: userMenu }} placement="bottomRight" arrow>
                <div style={{ display:'flex', alignItems:'center', cursor:'pointer', gap:6 }}>
                  <Avatar style={{ backgroundColor:'#1a1a1a' }} icon={<UserOutlined />} src={currentUser?.avatar} size={32} />
                  <span className="navbar-username" style={{ fontWeight:500, fontSize:14 }}>
                    {currentUser?.full_name || 'Khách hàng'}
                  </span>
                </div>
              </Dropdown>
            </div>
          ) : (
            <button
              className="login-btn"
              onClick={() => navigate('/login')}
              style={{ background:'#1a1a1a', color:'#fff', border:'none', borderRadius:8, padding:'8px 16px', fontWeight:600, cursor:'pointer', fontSize:14 }}
            >
              Đăng nhập
            </button>
          )}
        </div>
      </header>

      {/* ── Mobile Side Drawer ─────────────────────────────────────────────── */}
      <Drawer
        title={
          <Link to="/" onClick={() => setMobileOpen(false)} style={{ fontFamily:'serif', fontSize:18, fontWeight:700, letterSpacing:2, color:'#1a1a1a', textDecoration:'none' }}>
            ✦ FASHION ✦
          </Link>
        }
        placement="left"
        onClose={() => setMobileOpen(false)}
        open={mobileOpen}
        width={280}
        bodyStyle={{ padding:0 }}
        closeIcon={<CloseOutlined />}
      >
        {mobileMenuContent}
      </Drawer>

      <CartDrawer open={cartOpen} onClose={() => setCartOpen(false)} />
    </>
  );
};

export default Navbar;