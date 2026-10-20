package com.pengugarage.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích mã hóa và đối soát mật khẩu người dùng.
 * Hỗ trợ băm MD5 và SHA-256.
 */
public class MatKhauUtil {

    private MatKhauUtil() {
    }

    /**
     * Băm chuỗi văn bản bằng thuật toán MD5
     */
    public static String hashMD5(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán MD5 không khả dụng", e);
        }
    }

    /**
     * Băm chuỗi văn bản bằng thuật toán SHA-256
     */
    public static String hashSHA256(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán SHA-256 không khả dụng", e);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của mật khẩu người dùng nhập so với chuỗi lưu trong CSDL.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        // 1. So khớp MD5
        String md5 = hashMD5(rawPassword);
        if (md5.equalsIgnoreCase(storedHash)) {
            return true;
        }

        // 2. So khớp SHA-256
        String sha256 = hashSHA256(rawPassword);
        if (sha256.equalsIgnoreCase(storedHash)) {
            return true;
        }

        // 3. So khớp trực tiếp (hỗ trợ kiểm thử)
        return rawPassword.equals(storedHash);
    }
}
