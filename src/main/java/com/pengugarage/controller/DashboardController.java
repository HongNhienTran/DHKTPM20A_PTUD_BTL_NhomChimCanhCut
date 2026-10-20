package com.pengugarage.controller;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.util.PhienDangNhapUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

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
    private ComboBox<String> demoRoleCombo;

    @FXML
    private ComboBox<String> timeRangeCombo;

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
        // Cập nhật thông tin nhân viên đăng nhập từ PhienDangNhapUtil
        loadCurrentUser();

        // Khởi tạo các ComboBox
        demoRoleCombo.setItems(FXCollections.observableArrayList("Quản lý", "Kỹ thuật viên", "Lễ tân"));
        demoRoleCombo.setValue("Quản lý");

        timeRangeCombo.setItems(FXCollections.observableArrayList("7 ngày gần nhất", "30 ngày gần nhất", "Tháng này", "Quý này"));
        timeRangeCombo.setValue("7 ngày gần nhất");

        demoRoleCombo.setOnAction(e -> {
            String selectedRole = demoRoleCombo.getValue();
            if (userRoleLabel != null) {
                userRoleLabel.setText(selectedRole);
            }
            capNhatGiaoDienTheoQuyen(selectedRole);
        });
    }

    private void capNhatGiaoDienTheoQuyen(String role) {
        boolean isQuanLy = "Quản lý".equalsIgnoreCase(role);
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
                userNameLabel.setText(hoTen);
                
                // Avatar lấy chữ cái đầu
                if (hoTen != null && !hoTen.trim().isEmpty()) {
                    String[] parts = hoTen.trim().split("\\s+");
                    String lastWord = parts[parts.length - 1];
                    userAvatarLabel.setText(lastWord.substring(0, 1).toUpperCase());
                }

                if (nv.getChucVu() != null) {
                    if (nv.getChucVu() == ChucVu.QUAN_LY) {
                        userRoleLabel.setText("Quản lý");
                        demoRoleCombo.setValue("Quản lý");
                    } else if (nv.getChucVu() == ChucVu.KY_THUAT_VIEN) {
                        userRoleLabel.setText("Kỹ thuật viên");
                        demoRoleCombo.setValue("Kỹ thuật viên");
                    } else if (nv.getChucVu() == ChucVu.LE_TAN) {
                        userRoleLabel.setText("Lễ tân");
                        demoRoleCombo.setValue("Lễ tân");
                    }
                }
            }
        }
    }

    /**
     * Mở màn hình Quản lý nhân viên (M01-T07 & M01-T08)
     */
    @FXML
    private void onQuanLyNhanVienClick() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengugarage/view/quanly/nhan-vien-view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) userNameLabel.getScene().getWindow();
                Scene scene = new Scene(root, 1280, 760);
                stage.setScene(scene);
                stage.setTitle("Pengu Garage Service ERP - Quản Lý Nhân Sự & Tài Khoản");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Đăng xuất và quay về màn hình đăng nhập
     */
    @FXML
    private void onLogoutClick() {
        PhienDangNhapUtil.getInstance().logout();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengugarage/view/chung/dang-nhap-view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) userNameLabel.getScene().getWindow();
                Scene scene = new Scene(root, 1080, 680);
                stage.setScene(scene);
                stage.setTitle("Pengu Garage Service ERP - Đăng Nhập Hệ Thống");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
