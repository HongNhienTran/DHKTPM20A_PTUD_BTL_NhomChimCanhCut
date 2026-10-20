package com.pengugarage.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Tiện ích quản lý kết nối cơ sở dữ liệu MariaDB thông qua Connection Pool (HikariCP).
 * Cấu hình được nạp ưu tiên theo thứ tự:
 * 1. Biến môi trường hệ thống (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)
 * 2. File .env hoặc config.properties tại thư mục gốc ứng dụng
 * 3. File config.properties trong Classpath (resources)
 */
public class CoSoDuLieuUtil {

    private static HikariDataSource dataSource;

    static {
        try {
            initDataSource();
        } catch (Exception e) {
            System.err.println("WARN: Chưa khởi tạo được DataSource tự động: " + e.getMessage());
        }
    }

    private static synchronized void initDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        Properties props = loadConfiguration();

        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String dbName = props.getProperty("db.name", "pengu_garage");
        String user = props.getProperty("db.user", "root");
        String password = props.getProperty("db.password", "");

        String jdbcUrl = String.format("jdbc:mariadb://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8",
                host, port, dbName);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        // Cấu hình Pool tối ưu
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setMaxLifetime(1800000);

        // Tham số hiệu năng MariaDB
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    private static Properties loadConfiguration() {
        Properties props = new Properties();

        // 1. Đọc từ file .env tại thư mục gốc dự án nếu có
        File envFile = new File(".env");
        if (envFile.exists()) {
            try (FileInputStream fis = new FileInputStream(envFile)) {
                Properties envProps = new Properties();
                envProps.load(fis);
                if (envProps.containsKey("DB_HOST")) props.setProperty("db.host", envProps.getProperty("DB_HOST").trim());
                if (envProps.containsKey("DB_PORT")) props.setProperty("db.port", envProps.getProperty("DB_PORT").trim());
                if (envProps.containsKey("DB_NAME")) props.setProperty("db.name", envProps.getProperty("DB_NAME").trim());
                if (envProps.containsKey("DB_USER")) props.setProperty("db.user", envProps.getProperty("DB_USER").trim());
                if (envProps.containsKey("DB_PASSWORD")) props.setProperty("db.password", envProps.getProperty("DB_PASSWORD").trim());
            } catch (Exception e) {
                System.err.println("WARN: Lỗi đọc file .env: " + e.getMessage());
            }
        }

        // 2. Đọc từ file config.properties tại thư mục làm việc hiện tại
        File externalConfig = new File("config.properties");
        if (externalConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(externalConfig)) {
                props.load(fis);
            } catch (Exception e) {
                System.err.println("WARN: Lỗi đọc file config.properties ngoài: " + e.getMessage());
            }
        }

        // 3. Đọc từ file config.properties trong resources nếu chưa có cấu hình
        if (!props.containsKey("db.host")) {
            try (InputStream is = CoSoDuLieuUtil.class.getResourceAsStream("/config.properties")) {
                if (is != null) {
                    props.load(is);
                }
            } catch (Exception e) {
                System.err.println("WARN: Không tìm thấy /config.properties trong resources");
            }
        }

        // 4. Biến môi trường hệ thống ghi đè ưu tiên cao nhất
        String envHost = System.getenv("DB_HOST");
        if (envHost != null && !envHost.isBlank()) props.setProperty("db.host", envHost);

        String envPort = System.getenv("DB_PORT");
        if (envPort != null && !envPort.isBlank()) props.setProperty("db.port", envPort);

        String envName = System.getenv("DB_NAME");
        if (envName != null && !envName.isBlank()) props.setProperty("db.name", envName);

        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.isBlank()) props.setProperty("db.user", envUser);

        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null) props.setProperty("db.password", envPass);

        return props;
    }

    /**
     * Lấy kết nối cơ sở dữ liệu từ Pool.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * Kiểm tra trạng thái kết nối tới MariaDB.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra kết nối CSDL: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đóng Connection Pool khi tắt ứng dụng.
     */
    public static synchronized void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
