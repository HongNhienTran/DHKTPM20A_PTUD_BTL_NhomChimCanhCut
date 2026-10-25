package com.pengugarage.controller;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.entity.TaiKhoan;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.service.DangNhapService;
import com.pengugarage.util.DieuHuongUtil;
import com.pengugarage.util.PhienDangNhapUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

/**
 * Controller điều khiển màn hình Đăng Nhập Hệ Thống Pengu Garage.
 */
public class DangNhapController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button togglePasswordBtn;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private VBox alertBox;

    @FXML
    private Label alertLabel;

    private final DangNhapService dangNhapService = new DangNhapService();
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // Đồng bộ dữ liệu giữa PasswordField và TextField khi người dùng nhập
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isPasswordVisible) {
                passwordTextField.setText(newVal);
            }
        });

        passwordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isPasswordVisible) {
                passwordField.setText(newVal);
            }
        });

        // Hỗ trợ phím Enter để đăng nhập nhanh
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onLoginClick();
            }
        });

        passwordTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onLoginClick();
            }
        });
    }

    /**
     * Bật / tắt chế độ xem mật khẩu
     */
    @FXML
    private void onTogglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setText("🙈");
            passwordTextField.requestFocus();
            passwordTextField.positionCaret(passwordTextField.getText().length());
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordBtn.setText("👁");
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    /**
     * Xử lý sự kiện nhấn nút Đăng Nhập
     */
    @FXML
    private void onLoginClick() {
        String username = usernameField.getText();
        String password = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();

        if (username == null || username.trim().isEmpty()) {
            showAlert("Vui lòng nhập tên đăng nhập hoặc mã nhân viên!", true);
            usernameField.requestFocus();
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showAlert("Vui lòng nhập mật khẩu tài khoản!", true);
            passwordField.requestFocus();
            return;
        }

        setLoading(true);
        hideAlert();

        // Xử lý xác thực bất đồng bộ trong background thread
        new Thread(() -> {
            DangNhapService.KetQuaDangNhap result = dangNhapService.dangNhap(username, password);

            Platform.runLater(() -> {
                setLoading(false);

                if (result.isThanhCong()) {
                    TaiKhoan tk = result.getTaiKhoan();
                    NhanVien nv = tk.getNhanVien();
                    String chucVuDesc = (nv != null && nv.getChucVu() != null) ? nv.getChucVu().name() : "";
                    String hoTen = nv != null ? nv.getHoTen() : tk.getTenDangNhap();

                    showAlert("✓ Đăng nhập thành công! Xin chào " + hoTen + " (" + chucVuDesc + ")", false);

                    // Điều hướng sang màn hình Dashboard
                    navigateToDashboard();
                } else {
                    showAlert(result.getThongBao(), true);
                }
            });
        }).start();
    }

    /**
     * Điền nhanh thông tin tài khoản Quản lý (Demo)
     */
    @FXML
    private void onQuickAdminClick() {
        fillCredentials("NV_QL_001", "Tuan@Admin2026!");
    }

    /**
     * Điền nhanh thông tin tài khoản Kỹ thuật viên (Demo)
     */
    @FXML
    private void onQuickTechClick() {
        fillCredentials("NV_KTV_002", "Bao@Tech2026!");
    }

    /**
     * Điền nhanh thông tin tài khoản Lễ tân (Demo)
     */
    @FXML
    private void onQuickFrontClick() {
        fillCredentials("NV_LT_006", "Anh@Front2026!");
    }

    private void fillCredentials(String username, String password) {
        usernameField.setText(username);
        passwordField.setText(password);
        passwordTextField.setText(password);
        hideAlert();
    }

    /**
     * Quên mật khẩu - Hướng dẫn liên hệ Admin
     */
    @FXML
    private void onForgotPasswordClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Khôi phục mật khẩu");
        alert.setHeaderText("Chính sách khôi phục mật khẩu gara");
        alert.setContentText("Vì lý do bảo mật dữ liệu khách hàng và gara, vui lòng liên hệ trực tiếp Quản lý hệ thống (Admin) hoặc gửi yêu cầu đến ban điều hành gara để được cấp lại mật khẩu!");
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo trên form
     */
    private void showAlert(String message, boolean isError) {
        alertLabel.setText(message);
        if (isError) {
            alertBox.setStyle("-fx-background-color: rgba(220, 38, 38, 0.15); -fx-border-color: #ef4444; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px 12px;");
            alertLabel.setStyle("-fx-text-fill: #fca5a5; -fx-font-weight: bold; -fx-font-size: 12px;");
        } else {
            alertBox.setStyle("-fx-background-color: rgba(16, 185, 129, 0.15); -fx-border-color: #10b981; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px 12px;");
            alertLabel.setStyle("-fx-text-fill: #86efac; -fx-font-weight: bold; -fx-font-size: 12px;");
        }
        alertBox.setVisible(true);
        alertBox.setManaged(true);
    }

    private void hideAlert() {
        alertBox.setVisible(false);
        alertBox.setManaged(false);
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        if (loading) {
            loginButton.setText("ĐANG XÁC THỰC TÀI KHOẢN...");
            loginButton.setStyle("-fx-opacity: 0.75;");
        } else {
            loginButton.setText("ĐĂNG NHẬP VÀO HỆ THỐNG");
            loginButton.setStyle("");
        }
    }

    /**
     * Chuyển sang màn hình Dashboard riêng cho từng actor sau khi đăng nhập thành công
     */
    private void navigateToDashboard() {
        PhienDangNhapUtil session = PhienDangNhapUtil.getInstance();
        ChucVu chucVu = session.getChucVu();

        String fxmlPath;
        String tieuDe;

        if (chucVu == ChucVu.KY_THUAT_VIEN) {
            fxmlPath = "/com/pengugarage/view/kythuatvien/dashboard-view.fxml";
            tieuDe = "Pengu Garage Service ERP - Dashboard Kỹ Thuật Viên";
        } else if (chucVu == ChucVu.LE_TAN) {
            fxmlPath = "/com/pengugarage/view/letan/dashboard-view.fxml";
            tieuDe = "Pengu Garage Service ERP - Dashboard Lễ Tân & Thu Ngân";
        } else {
            // Mặc định hoặc Quản lý
            fxmlPath = "/com/pengugarage/view/quanly/dashboard-view.fxml";
            tieuDe = "Pengu Garage Service ERP - Dashboard Quản Lý";
        }

        if (getClass().getResource(fxmlPath) == null) {
            fxmlPath = "/com/pengugarage/view/quanly/dashboard-view.fxml";
            tieuDe = "Pengu Garage Service ERP - Dashboard Quản Lý";
        }
        DieuHuongUtil.chuyenManHinh(loginButton, fxmlPath, tieuDe);
    }
}
