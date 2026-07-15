package com.fashion.app.security.xss;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return cleanXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXSS(value);
    }

    // Hàm cốt lõi sử dụng Jsoup để làm sạch chuỗi
    private String cleanXSS(String value) {
        if (value != null) {
            // Safelist.none() sẽ loại bỏ TOÀN BỘ các thẻ HTML (bao gồm <script>, <img>, <a>,...)
            // Trả về chuỗi văn bản thuần túy (plain text)
            return Jsoup.clean(value, Safelist.none());
        }
        return null;
    }
}