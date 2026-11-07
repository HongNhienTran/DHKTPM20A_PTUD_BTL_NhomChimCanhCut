package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    public static Connection ketNoi() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/pengugarage", "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException("Kết nối MariaDB thất bại", e);
        }
    }
}

