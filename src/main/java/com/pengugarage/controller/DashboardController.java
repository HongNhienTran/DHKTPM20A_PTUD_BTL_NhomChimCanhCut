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
 * Controller cho Dashboard chính của Quản Lý.
 * Tải trực tiếp các chỉ số KPI thống kê thực tế từ CSDL pengu_garage.
 */
public class DashboardController {

    @FXML private Label kpiRevenueLabel;
    @FXML private Label kpiInvoiceLabel;
    @FXML private Label kpiIntakeLabel;
    @FXML private Label kpiRepairLabel;
    @FXML private Label kpiCustomerLabel;
    @FXML private Label kpiLowStockLabel;

    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");

    @FXML
    public void initialize() {
        taiDuLieuThucTe();
    }

    private void taiDuLieuThucTe() {
        new Thread(() -> {
            double revenue = 0;
            int invoices = 0;
            int intake = 0;
            int repairs = 0;
            int customers = 0;
            int lowStock = 0;

            try (Connection conn = CoSoDuLieuUtil.getConnection()) {
                // 1. Doanh thu thực thu từ hóa đơn đã thanh toán
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COALESCE(SUM(tong_thanh_toan), 0) FROM `hoa_don` WHERE `trang_thai_thanh_toan` = 'DA_THANH_TOAN'")) {
                    if (rs.next()) revenue = rs.getDouble(1);
                }

                // 2. Hóa đơn đã thanh toán
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `hoa_don` WHERE `trang_thai_thanh_toan` = 'DA_THANH_TOAN'")) {
                    if (rs.next()) invoices = rs.getInt(1);
                }

                // 3. Lượt tiếp nhận dịch vụ
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan`")) {
                    if (rs.next()) intake = rs.getInt(1);
                }

                // 4. Phiếu sửa chữa đang xử lý
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'DANG_SUA_CHUA'")) {
                    if (rs.next()) repairs = rs.getInt(1);
                }

                // 5. Khách hàng
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `khach_hang`")) {
                    if (rs.next()) customers = rs.getInt(1);
                }

                // 6. Sản phẩm sắp hết hàng / tồn ít
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `san_pham` WHERE `so_luong_ton` <= `so_luong_ton_toi_thieu`")) {
                    if (rs.next()) lowStock = rs.getInt(1);
                }

                final double finalRevenue = revenue;
                final int finalInvoices = invoices;
                final int finalIntake = intake;
                final int finalRepairs = repairs;
                final int finalCustomers = customers;
                final int finalLowStock = lowStock;

                Platform.runLater(() -> {
                    if (kpiRevenueLabel != null) kpiRevenueLabel.setText(tienFormat.format(finalRevenue));
                    if (kpiInvoiceLabel != null) kpiInvoiceLabel.setText(String.valueOf(finalInvoices));
                    if (kpiIntakeLabel != null) kpiIntakeLabel.setText(String.valueOf(finalIntake));
                    if (kpiRepairLabel != null) kpiRepairLabel.setText(String.valueOf(finalRepairs));
                    if (kpiCustomerLabel != null) kpiCustomerLabel.setText(String.valueOf(finalCustomers));
                    if (kpiLowStockLabel != null) kpiLowStockLabel.setText(String.valueOf(finalLowStock));
                });
            } catch (Exception e) {
                System.err.println("Lỗi khi tải số liệu thống kê Dashboard: " + e.getMessage());
            }
        }).start();
    }
}
