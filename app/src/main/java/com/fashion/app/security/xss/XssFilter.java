package com.fashion.app.security.xss;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ưu tiên chạy Filter này đầu tiên
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Bọc request gốc bằng XssRequestWrapper của chúng ta
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(httpRequest);

        // Cho phép request (đã được bọc) đi tiếp tới các Filter khác hoặc Controller
        chain.doFilter(wrappedRequest, response);
    }
}