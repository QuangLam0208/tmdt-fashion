package com.fashion.app.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * Làm sạch HTML do người dùng (admin) nhập vào các field rich-text (VD: mô tả sản phẩm)
 * trước khi lưu xuống DB, để chống Stored XSS khi field này được render lại
 * bằng dangerouslySetInnerHTML ở frontend.
 *
 * Chỉ cho phép các thẻ định dạng cơ bản (bold, italic, list, link, đoạn văn...),
 * loại bỏ hoàn toàn <script>, <img onerror>, onclick, javascript:, style, iframe, v.v.
 */
@Component
public class HtmlSanitizer {

    // basic() : cho phép b, em, i, strong, a, p, br, ul, ol, li, blockquote, code...
    // KHÔNG cho phép <img>, <script>, <iframe>, hay bất kỳ attribute on* (onerror, onclick...)
    private static final Safelist DESCRIPTION_SAFELIST = Safelist.basic()
            .addTags("h1", "h2", "h3", "h4", "u")
            .removeProtocols("a", "href", "ftp", "mailto"); // chỉ giữ http/https cho link

    public String sanitizeDescription(String rawHtml) {
        if (rawHtml == null) return null;
        return Jsoup.clean(rawHtml, DESCRIPTION_SAFELIST);
    }
}