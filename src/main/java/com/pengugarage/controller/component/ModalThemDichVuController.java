package com.pengugarage.controller.component;

import com.pengugarage.model.entity.DichVu;
import com.pengugarage.service.DichVuService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    // Các nhãn báo lỗi chữ đỏ ngay dưới từng ô input
    @FXML private Label errMaDichVu;
    @FXML private Label errTenDichVu;
    @FXML private Label errDonGia;
    @FXML private Label errThueVAT;

    private final DichVuService dichVuService = new DichVuService();

    @FXML
    public void initialize() {
        if (trangThaiCombo != null) {
            trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
            trangThaiCombo.setValue("Đang áp dụng");
        }
        if (thueVATField != null && (thueVATField.getText() == null || thueVATField.getText().trim().isEmpty())) {
            thueVATField.setText("10");
        }

        // Nhấn Enter trên các trường để thực hiện lưu
        if (maDichVuField != null) {
            maDichVuField.setOnAction(event -> onLuuClick());
        }
        if (tenDichVuField != null) {
            tenDichVuField.setOnAction(event -> onLuuClick());
        }
        if (donGiaField != null) {
            donGiaField.setOnAction(event -> onLuuClick());
        }
        if (thueVATField != null) {
            thueVATField.setOnAction(event -> onLuuClick());
        }
    }

    private void xoaTatCaLoi() {
        datLoiInput(maDichVuField, errMaDichVu, null);
        datLoiInput(tenDichVuField, errTenDichVu, null);
        datLoiInput(donGiaField, errDonGia, null);
        datLoiInput(thueVATField, errThueVAT, null);
        if (thongBaoLabel != null) {
            thongBaoLabel.setText("");
        }
    }

    private void datLoiInput(Control control, Label errLabel, String loiMsg) {
        if (errLabel != null) {
            if (loiMsg != null && !loiMsg.isEmpty()) {
                errLabel.setText("⚠ " + loiMsg);
                errLabel.setVisible(true);
                errLabel.setManaged(true);
            } else {
                errLabel.setText("");
                errLabel.setVisible(false);
                errLabel.setManaged(false);
            }
        }
        if (control != null) {
            control.getStyleClass().remove("input-error");
            if (loiMsg != null && !loiMsg.isEmpty()) {
                control.getStyleClass().add("input-error");
            }
        }
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    @FXML
    public void onLuuClick() {
        xoaTatCaLoi();

        try {
            // 1. Kiểm tra Mã dịch vụ
            String maDichVu = maDichVuField.getText();
            if (maDichVu == null || maDichVu.trim().isEmpty()) {
                datLoiInput(maDichVuField, errMaDichVu, "Mã dịch vụ không được để trống.");
                maDichVuField.requestFocus();
                return;
            }
            maDichVu = maDichVu.trim().toUpperCase();

            if (!maDichVu.matches("^[A-Z0-9_\\-]{3,30}$")) {
                datLoiInput(maDichVuField, errMaDichVu, "Mã từ 3-30 ký tự (chữ cái, chữ số, gạch ngang/dưới).");
                maDichVuField.requestFocus();
                return;
            }

            if (dichVuService.kiemTraTonTaiMa(maDichVu)) {
                datLoiInput(maDichVuField, errMaDichVu, "Mã dịch vụ '" + maDichVu + "' đã tồn tại!");
                maDichVuField.requestFocus();
                return;
            }

            // 2. Kiểm tra Tên dịch vụ
            String tenDichVu = tenDichVuField.getText();
            if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
                datLoiInput(tenDichVuField, errTenDichVu, "Tên dịch vụ không được để trống.");
                tenDichVuField.requestFocus();
                return;
            }
            tenDichVu = tenDichVu.trim().replaceAll("\\s+", " ");
            if (!tenDichVu.isEmpty()) {
                tenDichVu = Character.toUpperCase(tenDichVu.charAt(0)) + tenDichVu.substring(1);
            }

            // 3. Kiểm tra Đơn giá
            String donGiaText = donGiaField.getText();
            if (donGiaText == null || donGiaText.trim().isEmpty()) {
                datLoiInput(donGiaField, errDonGia, "Đơn giá không được để trống.");
                donGiaField.requestFocus();
                return;
            }
            double donGia;
            try {
                donGia = Double.parseDouble(donGiaText.trim().replace(",", ""));
            } catch (NumberFormatException e) {
                datLoiInput(donGiaField, errDonGia, "Đơn giá phải là số hợp lệ.");
                donGiaField.requestFocus();
                return;
            }
            if (!Double.isFinite(donGia) || donGia <= 0) {
                datLoiInput(donGiaField, errDonGia, "Đơn giá phải lớn hơn 0.");
                donGiaField.requestFocus();
                return;
            }

            // 4. Kiểm tra Thuế VAT
            String thueVATText = thueVATField.getText();
            if (thueVATText == null || thueVATText.trim().isEmpty()) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT không được để trống.");
                thueVATField.requestFocus();
                return;
            }
            double thueVAT;
            try {
                thueVAT = Double.parseDouble(thueVATText.trim());
            } catch (NumberFormatException e) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải là số hợp lệ.");
                thueVATField.requestFocus();
                return;
            }
            if (!Double.isFinite(thueVAT) || thueVAT < 0 || thueVAT > 100) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải từ 0% đến 100%.");
                thueVATField.requestFocus();
                return;
            }

            // 5. Kiểm tra Trạng thái
            String trangThai = trangThaiCombo.getValue();
            if (trangThai == null) {
                hienThongBao("Vui lòng chọn trạng thái dịch vụ.", false);
                trangThaiCombo.requestFocus();
                return;
            }

            // Tạo entity DichVu
            DichVu dichVu = new DichVu();
            dichVu.setMaDichVu(maDichVu);
            dichVu.setTenDichVu(tenDichVu);
            dichVu.setDonGia(donGia);
            dichVu.setThueVAT(thueVAT);

            String moTa = moTaArea.getText();
            dichVu.setMoTa(moTa != null ? moTa.trim() : "");
            dichVu.setTrangThai("Đang áp dụng".equals(trangThai));

            // Lưu vào database
            boolean thanhCong = dichVuService.them(dichVu);
            if (thanhCong) {
                hienThongBao("Thêm dịch vụ thành công!", true);
                onDongClick();
            } else {
                hienThongBao("Thêm dịch vụ thất bại. Vui lòng thử lại.", false);
            }
        } catch (IllegalArgumentException e) {
            hienThongBao(e.getMessage(), false);
        } catch (Exception e) {
            hienThongBao("Lỗi khi thêm dịch vụ: " + e.getMessage(), false);
        }
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
            thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }
}
