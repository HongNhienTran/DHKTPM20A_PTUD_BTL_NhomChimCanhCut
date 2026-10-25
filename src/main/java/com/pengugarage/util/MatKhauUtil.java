package com.pengugarage.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích mã hóa và đối soát mật khẩu người dùng.
 * Áp dụng duy nhất 1 chuẩn thuật toán bảo mật an toàn: SHA-256.
 */
public class MatKhauUtil {

    private MatKhauUtil() {
    }

    /**
     * Băm chuỗi văn bản bằng thuật toán chuẩn SHA-256 (64 ký tự hex)
     */
    public static String maHoa(String rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán SHA-256 không khả dụng trong môi trường Java này!", e);
        }
    }

    /**
     * Alias tương thích cho phương thức băm SHA-256
     */
    public static String hashSHA256(String rawPassword) {
        return maHoa(rawPassword);
    }

    /**
     * Đối soát tính hợp lệ của mật khẩu người dùng nhập so với mã băm SHA-256 lưu trong CSDL.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        String hash = maHoa(rawPassword);
        return hash.equalsIgnoreCase(storedHash);
    }
}
