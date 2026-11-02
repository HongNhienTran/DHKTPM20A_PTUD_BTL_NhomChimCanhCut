package com.pengugarage.controller.component;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.pengugarage.model.entity.DichVu;
import com.pengugarage.service.DichVuService;
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
    private final DichVuService dichVuService = new DichVuService();
    @FXML
    public void initialize() {
        if (trangThaiCombo != null) {
            trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
            trangThaiCombo.setValue("Đang áp dụng");
        }
        if (thueVATField != null && thueVATField.getText().isEmpty()) {
            thueVATField.setText("10");
        }
          // nhấn enter
        if (maDichVuField != null) {
            maDichVuField.setOnAction(event -> onLuuClick());}

        if (tenDichVuField != null) {
            tenDichVuField.setOnAction(event -> onLuuClick());}

        if (donGiaField != null) {
            donGiaField.setOnAction(event -> onLuuClick());}

        if (thueVATField != null) {
            thueVATField.setOnAction(event -> onLuuClick());}
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    @FXML
    public void onLuuClick() {
        try {

            //mã dịch vụ
            String maDichVu = maDichVuField.getText();

            if (maDichVu == null || maDichVu.trim().isEmpty()) {
                hienThongBao("Mã dịch vụ không được để trống.", false);
                maDichVuField.requestFocus();
                return;
            }
            if (!maDichVu.matches("^DV\\d{3}$")) {
                hienThongBao(
                        "Mã dịch vụ phải có định dạng DV001, DV002...", false
                );maDichVuField.requestFocus();
                return;}


            maDichVu = maDichVu.trim();
            //Tên dịch vụ
            String tenDichVu = tenDichVuField.getText();

            if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
                hienThongBao("Tên dịch vụ không được để trống.", false);
                tenDichVuField.requestFocus();
                return;}

            tenDichVu = tenDichVu.trim();

            String[] words = tenDichVu.toLowerCase(
                    java.util.Locale.forLanguageTag("vi")
            ).split("\\s+");
            StringBuilder tenChuanHoa = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    if (tenChuanHoa.length() > 0) {
                        tenChuanHoa.append(" ");
                    }
                    tenChuanHoa.append(
                            word.substring(0, 1).toUpperCase(
                                    java.util.Locale.forLanguageTag("vi"))
                    );tenChuanHoa.append(word.substring(1));
                }
            }

            tenDichVu = tenChuanHoa.toString();

            //Đơn giá

            String donGiaText = donGiaField.getText();
            if (donGiaText == null || donGiaText.trim().isEmpty()) {
                hienThongBao("Đơn giá không được để trống.", false);
                donGiaField.requestFocus();
                return;}
            double donGia;
            try {
                donGia = Double.parseDouble(donGiaText.trim());
            } catch (NumberFormatException e) {
                hienThongBao("Đơn giá phải là số hợp lệ.", false);
                donGiaField.requestFocus();
                return;}
            if (!Double.isFinite(donGia) || donGia <= 0) {
                hienThongBao("Đơn giá phải lớn hơn 0.", false);
                donGiaField.requestFocus();
                return;}

              //Thuế VAT
            String thueVATText = thueVATField.getText();
            if (thueVATText == null || thueVATText.trim().isEmpty()) {
                hienThongBao("Thuế VAT không được để trống.", false);
                thueVATField.requestFocus();
                return;}

            double thueVAT;

            try {
                thueVAT = Double.parseDouble(thueVATText.trim());
            } catch (NumberFormatException e) {
                hienThongBao("Thuế VAT phải là số hợp lệ.", false);
                thueVATField.requestFocus();
                return;}
            if (!Double.isFinite(thueVAT)
                    || thueVAT < 0
                    || thueVAT > 100) {
                hienThongBao("Thuế VAT phải từ 0 đến 100%.", false);
                thueVATField.requestFocus();
                return;}

            //Trạng Thái
            String trangThai = trangThaiCombo.getValue();
            if (trangThai == null) {
                hienThongBao("Vui lòng chọn trạng thái dịch vụ.", false);
                trangThaiCombo.requestFocus();
                return;}

           DichVu dichVu = new DichVu();
            dichVu.setMaDichVu(maDichVu);
            dichVu.setTenDichVu(tenDichVu);
            dichVu.setDonGia(donGia);
            dichVu.setThueVAT(thueVAT);

            String moTa = moTaArea.getText();
            dichVu.setMoTa(moTa == null ? "" : moTa.trim());

            dichVu.setTrangThai(
                    trangThai.equals("Đang áp dụng")
            );
            boolean thanhCong = dichVuService.them(dichVu);

            if (thanhCong) {
                hienThongBao("Thêm dịch vụ thành công!", true);

                onDongClick();
            } else {
                hienThongBao(
                        "Thêm dịch vụ thất bại. Vui lòng thử lại.",
                        false
                );}
        } catch (IllegalArgumentException e) {
            hienThongBao(e.getMessage(), false);

        } catch (Exception e) {
            hienThongBao(
                    "Lỗi khi thêm dịch vụ: " + e.getMessage(),
                    false
            );
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
            thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #f59e0b;");
        }
    }
}
