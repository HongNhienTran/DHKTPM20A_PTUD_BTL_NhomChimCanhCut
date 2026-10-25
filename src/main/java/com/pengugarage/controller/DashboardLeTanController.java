package com.pengugarage.controller;

import com.pengugarage.util.CoSoDuLieuUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

/**
 * Controller cho Dashboard dành riêng cho Lễ tân & Thu ngân (Actor: LE_TAN).
 * Tải trực tiếp các chỉ số tiếp nhận, chờ thanh toán và doanh thu thực tế từ CSDL pengu_garage.
 */
public class DashboardLeTanController {

    @FXML private Label kpiTiepNhanHomNayLabel;
    @FXML private Label kpiChoPhanCongLabel;
    @FXML private Label kpiChoThanhToanLabel;
    @FXML private Label kpiDoanhThuNgayLabel;

    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");

    @FXML
    public void initialize() {
        taiDuLieuThucTe();
    }

    private void taiDuLieuThucTe() {
        new Thread(() -> {
            int tiepNhan = 0;
            int choPhanCong = 0;
            int choThanhToan = 0;
            double doanhThu = 0;

            try (Connection conn = CoSoDuLieuUtil.getConnection()) {
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan`")) {
                    if (rs.next()) tiepNhan = rs.getInt(1);
                }
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'CHO_SUA_CHUA'")) {
                    if (rs.next()) choPhanCong = rs.getInt(1);
                }
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `hoa_don` WHERE `trang_thai_thanh_toan` != 'DA_THANH_TOAN'")) {
                    if (rs.next()) choThanhToan = rs.getInt(1);
                }
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COALESCE(SUM(tong_thanh_toan), 0) FROM `hoa_don` WHERE `trang_thai_thanh_toan` = 'DA_THANH_TOAN'")) {
                    if (rs.next()) doanhThu = rs.getDouble(1);
                }

                final int fTiepNhan = tiepNhan;
                final int fChoPhanCong = choPhanCong;
                final int fChoThanhToan = choThanhToan;
                final double fDoanhThu = doanhThu;

                Platform.runLater(() -> {
                    if (kpiTiepNhanHomNayLabel != null) kpiTiepNhanHomNayLabel.setText(String.valueOf(fTiepNhan));
                    if (kpiChoPhanCongLabel != null) kpiChoPhanCongLabel.setText(String.valueOf(fChoPhanCong));
                    if (kpiChoThanhToanLabel != null) kpiChoThanhToanLabel.setText(String.valueOf(fChoThanhToan));
                    if (kpiDoanhThuNgayLabel != null) kpiDoanhThuNgayLabel.setText(tienFormat.format(fDoanhThu));
                });
            } catch (Exception e) {
                System.err.println("Lỗi tải KPI lễ tân: " + e.getMessage());
            }
        }).start();
    }
}
