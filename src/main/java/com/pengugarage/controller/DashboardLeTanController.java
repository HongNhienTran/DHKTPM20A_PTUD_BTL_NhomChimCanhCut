package com.pengugarage.controller;

import com.pengugarage.util.CoSoDuLieuUtil;
import com.pengugarage.util.DieuHuongUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

/**
 * Controller cho Dashboard dành riêng cho Lễ tân & Thu ngân (Actor: LE_TAN).
 * Tải trực tiếp các chỉ số tiếp nhận, hàng đợi xe, chờ thanh toán và doanh thu thực tế từ CSDL pengu_garage.
 */
public class DashboardLeTanController {

    @FXML private VBox rootView;

    // 4 KPI Cards
    @FXML private Label kpiTiepNhanHomNayLabel;
    @FXML private Label kpiChoPhanCongLabel;
    @FXML private Label kpiChoThanhToanLabel;
    @FXML private Label kpiDoanhThuNgayLabel;

    // Widget Hàng Đợi Xe Tại Xưởng
    @FXML private VBox cardHangDoiXe;
    @FXML private Label lblTongXeHangDoi;
    @FXML private Label lblSoLuongChoKiemTra;
    @FXML private Label lblSoLuongKhamBaoGia;
    @FXML private Label lblSoLuongDangSuaChua;
    @FXML private Label lblSoLuongChoBanGiao;

    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");

    @FXML
    public void initialize() {
        taiDuLieuThucTe();
    }

    @FXML
    public void onTiepNhanXeClick() {
        DieuHuongUtil.chuyenManHinh(rootView,
                "/com/pengugarage/view/letan/tiep-nhan-xe-view.fxml",
                "Pengu Garage Service ERP - Tiếp Nhận Xe");
    }

    @FXML
    public void onHangDoiXeClick() {
        DieuHuongUtil.chuyenManHinh(rootView,
                "/com/pengugarage/view/letan/hang-doi-xe-view.fxml",
                "Pengu Garage Service ERP - Hàng Đợi Xe Tại Xưởng");
    }

    @FXML
    public void onTaoDonBanHangClick() {
        DieuHuongUtil.chuyenManHinh(rootView,
                "/com/pengugarage/view/letan/ban-hang-view.fxml",
                "Pengu Garage Service ERP - Bán Hàng & Phụ Tùng");
    }

    @FXML
    public void onLapHoaDonClick() {
        DieuHuongUtil.chuyenManHinh(rootView,
                "/com/pengugarage/view/letan/lap-hoa-don-view.fxml",
                "Pengu Garage Service ERP - Lập Hóa Đơn & Thu Ngân");
    }

    private void taiDuLieuThucTe() {
        new Thread(() -> {
            int tiepNhan = 0;
            int choKiemTra = 0;
            int khamBaoGia = 0;
            int dangSuaChua = 0;
            int choBanGiao = 0;
            int choThanhToan = 0;
            double doanhThu = 0;

            try (Connection conn = CoSoDuLieuUtil.getConnection()) {
                // Tổng tiếp nhận
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan`")) {
                    if (rs.next()) tiepNhan = rs.getInt(1);
                }

                // 1. Chờ kiểm tra (CHO_TIEP_NHAN, DA_TIEP_NHAN)
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` IN ('CHO_TIEP_NHAN', 'DA_TIEP_NHAN')")) {
                    if (rs.next()) choKiemTra = rs.getInt(1);
                }

                // 2. Kiểm tra & Báo giá (DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA)
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` IN ('DANG_KIEM_TRA', 'CHO_XAC_NHAN_BAO_GIA')")) {
                    if (rs.next()) khamBaoGia = rs.getInt(1);
                }

                // 3. Đang sửa chữa (DANG_SUA_CHUA)
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'DANG_SUA_CHUA'")) {
                    if (rs.next()) dangSuaChua = rs.getInt(1);
                }

                // 4. Chờ bàn giao (HOAN_THANH)
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'HOAN_THANH'")) {
                    if (rs.next()) choBanGiao = rs.getInt(1);
                }

                // Hóa đơn chờ thanh toán
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `hoa_don` WHERE `trang_thai_thanh_toan` != 'DA_THANH_TOAN'")) {
                    if (rs.next()) choThanhToan = rs.getInt(1);
                }

                // Doanh thu hôm nay
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COALESCE(SUM(tong_thanh_toan), 0) FROM `hoa_don` WHERE `trang_thai_thanh_toan` = 'DA_THANH_TOAN'")) {
                    if (rs.next()) doanhThu = rs.getDouble(1);
                }

                final int fTiepNhan = tiepNhan;
                final int fChoKiemTra = choKiemTra;
                final int fKhamBaoGia = khamBaoGia;
                final int fDangSuaChua = dangSuaChua;
                final int fChoBanGiao = choBanGiao;
                final int fTongXeHangDoi = fChoKiemTra + fKhamBaoGia + fDangSuaChua + fChoBanGiao;
                final int fChoThanhToan = choThanhToan;
                final double fDoanhThu = doanhThu;

                Platform.runLater(() -> {
                    // Cập nhật 4 KPI Cards
                    if (kpiTiepNhanHomNayLabel != null) kpiTiepNhanHomNayLabel.setText(String.valueOf(fTiepNhan));
                    if (kpiChoPhanCongLabel != null) kpiChoPhanCongLabel.setText(String.valueOf(fKhamBaoGia) + " xe");
                    if (kpiChoThanhToanLabel != null) kpiChoThanhToanLabel.setText(String.valueOf(fChoThanhToan) + " xe");
                    if (kpiDoanhThuNgayLabel != null) kpiDoanhThuNgayLabel.setText(tienFormat.format(fDoanhThu));

                    // Cập nhật Widget Hàng Đợi Xe Tại Xưởng
                    if (lblTongXeHangDoi != null) lblTongXeHangDoi.setText("● " + fTongXeHangDoi + " xe tại xưởng");
                    if (lblSoLuongChoKiemTra != null) lblSoLuongChoKiemTra.setText(fChoKiemTra + " xe");
                    if (lblSoLuongKhamBaoGia != null) lblSoLuongKhamBaoGia.setText(fKhamBaoGia + " xe");
                    if (lblSoLuongDangSuaChua != null) lblSoLuongDangSuaChua.setText(fDangSuaChua + " xe");
                    if (lblSoLuongChoBanGiao != null) lblSoLuongChoBanGiao.setText(fChoBanGiao + " xe");
                });
            } catch (Exception e) {
                System.err.println("Lỗi tải KPI lễ tân: " + e.getMessage());
            }
        }).start();
    }
}
