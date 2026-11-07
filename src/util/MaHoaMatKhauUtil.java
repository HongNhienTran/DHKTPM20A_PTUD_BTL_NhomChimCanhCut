package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MaHoaMatKhauUtil {
    private static final String THUAT_TOAN = "SHA-256";
    private static final String REGEX_SHA256_HEX = "^[0-9a-f]{64}$";

    private MaHoaMatKhauUtil() {
    }

    public static String maHoa(String matKhauGoc) {
        if (matKhauGoc == null || matKhauGoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu cần mã hóa không được để trống");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(THUAT_TOAN);
            byte[] digest = md.digest(matKhauGoc.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Môi trường Java không hỗ trợ thuật toán " + THUAT_TOAN, e);
        }
    }

    public static boolean laChuoiDaMaHoa(String chuoi) {
        return chuoi != null && chuoi.matches(REGEX_SHA256_HEX);
    }

    public static boolean khopMatKhau(String matKhauGoc, String matKhauMaHoa) {
        if (matKhauGoc == null || matKhauMaHoa == null) {
            return false;
        }
        String bam = maHoa(matKhauGoc);
        return MessageDigest.isEqual(
                bam.getBytes(StandardCharsets.UTF_8),
                matKhauMaHoa.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
        );
    }
}

