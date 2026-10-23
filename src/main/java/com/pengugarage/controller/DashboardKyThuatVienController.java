package com.pengugarage.controller;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.util.DieuHuongUtil;
import com.pengugarage.util.PhienDangNhapUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller cho Dashboard dành riêng cho Kỹ thuật viên (Actor: KY_THUAT_VIEN).
 * Quản lý tiến độ phân công sửa chữa, đề xuất phụ tùng và lịch sử ca sửa chữa.
 */
public class DashboardKyThuatVienController {

    @FXML
    private Label userAvatarLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label kpiDangSuaLabel;

    @FXML
    private Label kpiChoTiepNhanLabel;

    @FXML
    private Label kpiHoanThanhLabel;

    @FXML
    private Label kpiVatTuChoDuyetLabel;

    @FXML
    public void initialize() {
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        PhienDangNhapUtil session = PhienDangNhapUtil.getInstance();
        if (session.isLoggedIn()) {
            NhanVien nv = session.getNhanVien();
            if (nv != null) {
                String hoTen = nv.getHoTen();
                if (userNameLabel != null) {
                    userNameLabel.setText(hoTen);
                }
                if (userAvatarLabel != null && hoTen != null && !hoTen.trim().isEmpty()) {
                    String[] parts = hoTen.trim().split("\\s+");
                    String lastWord = parts[parts.length - 1];
                    userAvatarLabel.setText(lastWord.substring(0, 1).toUpperCase());
                }
                if (userRoleLabel != null) {
                    userRoleLabel.setText("Kỹ thuật viên");
                }
            }
        }
    }

    /**
     * Đăng xuất và quay về màn hình đăng nhập
     */
    @FXML
    private void onLogoutClick() {
        PhienDangNhapUtil.getInstance().logout();
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/chung/dang-nhap-view.fxml",
                "Pengu Garage Service ERP - Đăng Nhập Hệ Thống");
    }
}
