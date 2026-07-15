
import React, { createContext, useState, useCallback } from 'react';
import customerAuthService from '../services/customerAuthService';

export const CustomerAuthContext = createContext(null);

const USER_KEY = 'fashion_customer_user';
const TOKEN_KEY = 'fashion_customer_token';

const _loadUser = () => {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY)) || null;
  } catch {
    return null;
  }
};

const _loadToken = () => localStorage.getItem(TOKEN_KEY) || null;

export const CustomerAuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(_loadUser);
  const [token, setToken]             = useState(_loadToken);

  const isAuthenticated = !!currentUser && !!token;

  const login = useCallback(async (email, password) => {
    const res = await customerAuthService.login(email, password);
    const userData = {
      id: res.userId,
      full_name: res.fullName,
      email: res.email,
      role: res.role,
    };
    setCurrentUser(userData);
    setToken(res.accessToken);
    localStorage.setItem(TOKEN_KEY, res.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
    // CartContext sẽ tự phát hiện sự kiện đăng nhập qua useRef và merge giỏ hàng
  }, []);

  const logout = useCallback(async () => {
    if (customerAuthService.logout) {
      await customerAuthService.logout();
    }
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
    setCurrentUser(null);
  }, []);

  const updateProfile = useCallback((updated) => {
    setCurrentUser((prev) => {
      const merged = { ...prev, ...updated };
      localStorage.setItem(USER_KEY, JSON.stringify(merged));
      return merged;
    });
  }, []);

  return (
    <CustomerAuthContext.Provider
      value={{ currentUser, token, isAuthenticated, login, logout, updateProfile, loading: false }}
    >
      {children}
    </CustomerAuthContext.Provider>
  );
};