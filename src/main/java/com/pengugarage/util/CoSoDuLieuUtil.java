package com.pengugarage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Tiện ích quản lý kết nối cơ sở dữ liệu MariaDB.
 * Đọc trực tiếp cấu hình từ file .env tại thư mục gốc dự án.
 */
public class CoSoDuLieuUtil {

    private static String jdbcUrl;
    private static String user;
    private static String password;

    static {
        napCauHinhTuEnv();
    }

    /**
     * Đọc các biến cấu hình từ file .env, ném ngoại lệ nếu file không tồn tại hoặc lỗi đọc
     */
    private static void napCauHinhTuEnv() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            throw new RuntimeException("Lỗi: Không tìm thấy file .env tại thư mục gốc dự án!");
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(envFile)) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file .env: " + e.getMessage(), e);
        }

        String host = props.getProperty("DB_HOST", "localhost").trim();
        String port = props.getProperty("DB_PORT", "3306").trim();
        String dbName = props.getProperty("DB_NAME", "pengu_garage").trim();
        user = props.getProperty("DB_USER", "root").trim();
        password = props.getProperty("DB_PASSWORD", "").trim();

        // restrictedAuth giúp ngăn chặn lỗi GSS-API / Kerberos trên Windows
        jdbcUrl = String.format(
                "jdbc:mariadb://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&restrictedAuth=mysql_native_password,client_ed25519",
                host, port, dbName);
    }

    /**
     * Mở một kết nối mới tới CSDL MariaDB.
     * Ném SQLException ra ngoài để nơi gọi tự xử lý.
     */
    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            napCauHinhTuEnv();
        }
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    /**
     * Đóng kết nối CSDL an toàn.
     */
    public static void dongKetNoi(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối CSDL: " + e.getMessage());
            }
        }
    }

    /**
     * Kiểm tra trạng thái kết nối tới MariaDB.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra kết nối CSDL: " + e.getMessage());
            return false;
        }
    }
}
