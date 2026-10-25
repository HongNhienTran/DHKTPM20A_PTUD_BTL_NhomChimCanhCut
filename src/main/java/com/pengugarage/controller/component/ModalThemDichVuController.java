package com.pengugarage.controller.component;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller cho Modal Thêm Dịch Vụ Mới.
 * 
 * =============================================================================
 * PHÂN CÔNG TASK:
 * Task ID: M03-T02 - Nhập thông tin dịch vụ và thêm
 * Người thực hiện: Tân
 * =============================================================================
 */
public class ModalThemDichVuController {

    @FXML private VBox rootPane;
    @FXML private TextField maDichVuField;
    @FXML private TextField tenDichVuField;
    @FXML private TextField donGiaField;
    @FXML private TextField thueVATField;
    @FXML private TextArea moTaArea;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private Label thongBaoLabel;

    @FXML
    public void initialize() {
        if (trangThaiCombo != null) {
            trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
            trangThaiCombo.setValue("Đang áp dụng");
        }
        if (thueVATField != null && thueVATField.getText().isEmpty()) {
            thueVATField.setText("10");
        }
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    @FXML
    public void onLuuClick() {
        // TODO: Tân thực hiện code chức năng tại đây:
        // 1. Kiểm tra validation các trường nhập (Mã DV, Tên DV, Đơn giá > 0, Thuế VAT...)
        // 2. Khởi tạo đối tượng DichVu từ form
        // 3. Gọi DichVuService.them(dichVu) để lưu vào database
        // 4. Hiển thị thông báo kết quả và đóng modal nếu thêm thành công
        hienThongBao("Chức năng lưu thêm mới dịch vụ do Tân thực hiện (Task M03-T02)", false);
    }

    @FXML
    public void onDongClick() {
        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            ((Stage) rootPane.getScene().getWindow()).close();
        }
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        if (thongBaoLabel != null) {
            thongBaoLabel.setText(msg);
            thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #f59e0b;");
        }
    }
}
