package com.pengugarage.controller;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.util.DieuHuongUtil;
import com.pengugarage.util.PhienDangNhapUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller cho Dashboard chính của hệ thống Pengu Garage.
 */
public class DashboardController {

    @FXML
    private Label userAvatarLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label kpiRevenueLabel;

    @FXML
    private Label kpiInvoiceLabel;

    @FXML
    private Label kpiIntakeLabel;

    @FXML
    private Label kpiRepairLabel;

    @FXML
    private Label kpiCustomerLabel;

    @FXML
    private Label kpiLowStockLabel;

    @FXML
    private javafx.scene.layout.VBox menuQuanTriCuahang;

    @FXML
    private Button btnQuanLyNhanVien;

    @FXML
    public void initialize() {
        // Cập nhật thông tin và phân quyền nhân viên đăng nhập thực tế
        loadCurrentUser();
    }

    private void capNhatGiaoDienTheoQuyen(boolean isQuanLy) {
        if (menuQuanTriCuahang != null) {
            menuQuanTriCuahang.setVisible(isQuanLy);
            menuQuanTriCuahang.setManaged(isQuanLy);
        }
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
                
                // Avatar lấy chữ cái đầu
                if (userAvatarLabel != null && hoTen != null && !hoTen.trim().isEmpty()) {
                    String[] parts = hoTen.trim().split("\\s+");
                    String lastWord = parts[parts.length - 1];
                    userAvatarLabel.setText(lastWord.substring(0, 1).toUpperCase());
                }

                if (nv.getChucVu() != null) {
                    boolean isQuanLy = (nv.getChucVu() == ChucVu.QUAN_LY);
                    if (userRoleLabel != null) {
                        if (isQuanLy) {
                            userRoleLabel.setText("Quản lý");
                        } else if (nv.getChucVu() == ChucVu.KY_THUAT_VIEN) {
                            userRoleLabel.setText("Kỹ thuật viên");
                        } else if (nv.getChucVu() == ChucVu.LE_TAN) {
                            userRoleLabel.setText("Lễ tân");
                        }
                    }
                    capNhatGiaoDienTheoQuyen(isQuanLy);
                }
            }
        }
    }

    /**
     * Mở màn hình Quản lý nhân viên (M01-T07 & M01-T08)
     */
    @FXML
    private void onQuanLyNhanVienClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/quanly/nhan-vien-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Nhân Sự & Tài Khoản");
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
