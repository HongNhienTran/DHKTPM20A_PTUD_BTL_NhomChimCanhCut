package com.pengugarage.controller;

import com.pengugarage.util.CoSoDuLieuUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Controller cho Dashboard dành riêng cho Kỹ thuật viên (Actor: KY_THUAT_VIEN).
 * Tải trực tiếp các chỉ số tiến độ ca sửa chữa từ CSDL pengu_garage.
 */
public class DashboardKyThuatVienController {

    @FXML private Label kpiDangSuaLabel;
    @FXML private Label kpiChoTiepNhanLabel;
    @FXML private Label kpiHoanThanhLabel;
    @FXML private Label kpiVatTuChoDuyetLabel;

    @FXML
    public void initialize() {
        taiDuLieuThucTe();
    }

    private void taiDuLieuThucTe() {
        new Thread(() -> {
            int dangSua = 0;
            int choTiepNhan = 0;
            int hoanThanh = 0;
            int vatTu = 0;

            try (Connection conn = CoSoDuLieuUtil.getConnection()) {
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'DANG_SUA_CHUA'")) {
                    if (rs.next()) dangSua = rs.getInt(1);
                }
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'CHO_SUA_CHUA'")) {
                    if (rs.next()) choTiepNhan = rs.getInt(1);
                }
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `phieu_tiep_nhan` WHERE `trang_thai` = 'HOAN_THANH'")) {
                    if (rs.next()) hoanThanh = rs.getInt(1);
                }
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM `chi_tiet_dung_phu_tung`")) {
                    if (rs.next()) vatTu = rs.getInt(1);
                }

                final int fDangSua = dangSua;
                final int fCho = choTiepNhan;
                final int fHoanThanh = hoanThanh;
                final int fVatTu = vatTu;

                Platform.runLater(() -> {
                    if (kpiDangSuaLabel != null) kpiDangSuaLabel.setText(String.valueOf(fDangSua));
                    if (kpiChoTiepNhanLabel != null) kpiChoTiepNhanLabel.setText(String.valueOf(fCho));
                    if (kpiHoanThanhLabel != null) kpiHoanThanhLabel.setText(String.valueOf(fHoanThanh));
                    if (kpiVatTuChoDuyetLabel != null) kpiVatTuChoDuyetLabel.setText(String.valueOf(fVatTu));
                });
            } catch (Exception e) {
                System.err.println("Lỗi tải KPI kỹ thuật viên: " + e.getMessage());
            }
        }).start();
    }
}
