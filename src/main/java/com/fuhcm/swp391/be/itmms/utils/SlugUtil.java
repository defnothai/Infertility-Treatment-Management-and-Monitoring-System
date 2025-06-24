package com.fuhcm.swp391.be.itmms.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtil {
    public static String toSlug(String input) {
        String nonWhitespace = input.trim().replaceAll("\\s+", "-"); // thay khoảng trắng thành -
        String normalized = Normalizer.normalize(nonWhitespace, Normalizer.Form.NFD); // chuẩn hóa Unicode
        String slug = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""); // bỏ dấu tiếng Việt
        return slug.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]", "")  // bỏ ký tự không hợp lệ
                .replaceAll("-{2,}", "-")      // thay nhiều dấu '-' liên tiếp thành 1
                .replaceAll("^-|-$", "");      // bỏ dấu '-' đầu/cuối
    }
}
