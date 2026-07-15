import React, { createContext, useState, useEffect, useCallback, useRef } from 'react';
import { cartService } from '../services/cartService';
import useCustomerAuth from '../hooks/useCustomerAuth';
import { message } from 'antd';

export const CartContext = createContext(null);

// ─── Key localStorage cho khách vãng lai ───────────────────────────────────
const GUEST_CART_KEY = 'fashion_guest_cart';

const loadGuestCart = () => {
  try {
    return JSON.parse(localStorage.getItem(GUEST_CART_KEY)) || [];
  } catch {
    return [];
  }
};

const saveGuestCart = (items) =>
  localStorage.setItem(GUEST_CART_KEY, JSON.stringify(items));

const clearGuestCart = () =>
  localStorage.removeItem(GUEST_CART_KEY);

// ─── Helper tính tổng tiền ────────────────────────────────────────────────
const calcTotal = (list) =>
  list.reduce((s, i) => s + (i.price || 0) * (i.quantity || 0), 0);

export const CartProvider = ({ children }) => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [totalPrice, setTotalPrice] = useState(0);
  const { isAuthenticated } = useCustomerAuth() || {};

  // Theo dõi giá trị isAuthenticated trước đó để phát hiện lúc "vừa đăng nhập"
  // Khởi tạo = undefined để phân biệt được lần render đầu tiên và lúc thực sự login
  const prevAuthRef = useRef(undefined);

  // ─── 1. TẢI GIỎ HÀNG ─────────────────────────────────────────────────────
  const loadCart = useCallback(async () => {
    if (!isAuthenticated) {
      const guestItems = loadGuestCart();
      setItems(guestItems);
      setTotalPrice(calcTotal(guestItems));
      return;
    }
    setLoading(true);
    try {
      const data = await cartService.getCart();
      const serverItems = Array.isArray(data)
        ? data
        : data?.content || data?.items || [];
      setItems(serverItems);
      setTotalPrice(
        data?.totalAmount !== undefined ? data.totalAmount : calcTotal(serverItems)
      );
    } catch (error) {
      message.error(
        'Lỗi tải giỏ hàng: ' + (error?.response?.data?.message || 'Vui lòng thử lại!')
      );
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  // ─── 2. ĐỒNG BỘ giỏ hàng khách lên Server khi vừa đăng nhập ─────────────
  useEffect(() => {
    const justLoggedIn = prevAuthRef.current === false && isAuthenticated === true;
    prevAuthRef.current = isAuthenticated ?? false;

    if (justLoggedIn) {
      // Merge giỏ hàng guest lên server rồi reload
      const guestItems = loadGuestCart();
      if (guestItems.length > 0) {
        Promise.allSettled(
          guestItems.map((item) =>
            cartService.addItem({ variantId: item.variantId, quantity: item.quantity })
          )
        ).finally(() => {
          clearGuestCart();
          loadCart();
        });
      } else {
        loadCart();
      }
    } else {
      loadCart();
    }
  }, [isAuthenticated, loadCart]);

  // ─── Tổng số lượng ────────────────────────────────────────────────────────
  const totalItems = items.reduce((s, i) => s + (i.quantity || 0), 0);

  // ─── 3. THÊM VÀO GIỎ ─────────────────────────────────────────────────────
  const addItem = useCallback(
    async (productProps) => {
      const variantId = productProps.variantId ?? productProps.variant_id;
      const quantity = productProps.quantity ?? 1;

      if (!variantId) {
        message.warning('Vui lòng chọn phân loại (Size/Màu) trước khi thêm!');
        return;
      }

      if (!isAuthenticated) {
        // Khách vãng lai: lưu vào localStorage
        const guestItems = loadGuestCart();
        const existing = guestItems.find((i) => i.variantId === variantId);
        const updated = existing
          ? guestItems.map((i) =>
              i.variantId === variantId ? { ...i, quantity: i.quantity + quantity } : i
            )
          : [
              ...guestItems,
              {
                cartItemId: `guest_${variantId}_${Date.now()}`,
                variantId,
                quantity,
                price: productProps.price || 0,
                name: productProps.name || productProps.productName || 'Sản phẩm',
                primaryImageUrl: productProps.primaryImageUrl || productProps.image || null,
                color: productProps.color || '',
                size: productProps.size || '',
              },
            ];
        saveGuestCart(updated);
        setItems(updated);
        setTotalPrice(calcTotal(updated));
        message.success('Đã thêm sản phẩm vào giỏ hàng 🛒');
        return;
      }

      try {
        await cartService.addItem({ variantId, quantity });
        message.success('Đã thêm sản phẩm vào giỏ hàng thành công 🛒');
        await loadCart();
      } catch (error) {
        const errorMsg =
          error?.response?.data?.errors?.variantId ||
          error?.response?.data?.message ||
          'Không thể thêm sản phẩm vào giỏ hàng';
        message.error(`Lỗi: ${errorMsg}`);
      }
    },
    [isAuthenticated, loadCart]
  );

  // ─── 4. CẬP NHẬT SỐ LƯỢNG ────────────────────────────────────────────────
  const updateQuantity = useCallback(
    async (cartItemId, quantity) => {
      if (quantity < 1) return;
      if (!isAuthenticated) {
        const guestItems = loadGuestCart();
        const updated = guestItems.map((i) =>
          i.cartItemId === cartItemId ? { ...i, quantity } : i
        );
        saveGuestCart(updated);
        setItems(updated);
        setTotalPrice(calcTotal(updated));
        return;
      }
      try {
        await cartService.updateQuantity(cartItemId, quantity);
        await loadCart();
      } catch (error) {
        message.error(error?.response?.data?.message || 'Cập nhật số lượng thất bại');
      }
    },
    [isAuthenticated, loadCart]
  );

  // ─── 5. XÓA SẢN PHẨM ─────────────────────────────────────────────────────
  const removeItem = useCallback(
    async (itemId) => {
      if (!isAuthenticated) {
        const guestItems = loadGuestCart();
        const updated = guestItems.filter((i) => i.cartItemId !== itemId);
        saveGuestCart(updated);
        setItems(updated);
        setTotalPrice(calcTotal(updated));
        message.success('Đã xóa sản phẩm khỏi giỏ hàng');
        return;
      }
      try {
        await cartService.removeItem(itemId);
        message.success('Đã xóa sản phẩm khỏi giỏ hàng');
        await loadCart();
      } catch {
        message.error('Không thể xóa sản phẩm này');
      }
    },
    [isAuthenticated, loadCart]
  );

  // ─── 6. XÓA TOÀN BỘ ─────────────────────────────────────────────────────
  const clearCart = useCallback(async () => {
    if (!isAuthenticated) {
      clearGuestCart();
      setItems([]);
      setTotalPrice(0);
      return;
    }
    try {
      if (cartService.clearCart) await cartService.clearCart();
    } catch {
      console.warn('API clear giỏ hàng gặp sự cố hoặc chưa hoàn thiện');
    } finally {
      setItems([]);
      setTotalPrice(0);
    }
  }, [isAuthenticated]);

  return (
    <CartContext.Provider
      value={{ items, totalItems, totalPrice, loading, loadCart, addItem, updateQuantity, removeItem, clearCart }}
    >
      {children}
    </CartContext.Provider>
  );
};