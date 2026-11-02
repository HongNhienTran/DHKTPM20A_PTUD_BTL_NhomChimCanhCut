package com.pengugarage.controller.component;

import com.pengugarage.model.entity.DichVu;
import com.pengugarage.service.DichVuService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Controller cho Component Xem & Chỉnh Sửa Chi Tiết Dịch Vụ (chi-tiet-dich-vu.fxml)
 */
public class ChiTietDichVuController {

    @FXML private VBox rootPane;
    @FXML private Label detailMaDVLabel;
    @FXML private Label thongBaoLuuLabel;

    @FXML private TextField tenDichVuField;
    @FXML private TextField donGiaField;
    @FXML private TextField thueVATField;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private TextArea moTaArea;
    @FXML private Button btnLuuThayDoi;

    // Các nhãn báo lỗi chữ đỏ ngay dưới từng ô input
    @FXML private Label errTenDichVu;
    @FXML private Label errDonGia;
    @FXML private Label errThueVAT;

    private DichVu dichVuHienTai;
    private Runnable onDongCallback;
    private Consumer<DichVu> onLuuCallback;
    private final DichVuService dichVuService = new DichVuService();

    @FXML
    public void initialize() {
        if (trangThaiCombo != null) {
            trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
        }
        anChiTiet();

        // Nhấn Enter để lưu (bỏ qua nếu đang nhập trong TextArea để cho phép xuống dòng)
        Platform.runLater(() -> {
            if (rootPane != null) {
                rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ENTER && !(event.getTarget() instanceof TextArea)) {
                        onLuuThayDoiClick();
                        event.consume();
                    }
                });
            }
        });
    }

    public void setOnDongCallback(Runnable callback) {
        this.onDongCallback = callback;
    }

    public void setOnLuuCallback(Consumer<DichVu> callback) {
        this.onLuuCallback = callback;
    }

    public void hienThiChiTiet(DichVu dv) {
        if (dv == null) {
            anChiTiet();
            return;
        }

        this.dichVuHienTai = dv;
        setHienThi(true);
        xoaTatCaLoi();
        xoaThongBao();

        detailMaDVLabel.setText(dv.getMaDichVu() != null ? dv.getMaDichVu() : "---");
        tenDichVuField.setText(dv.getTenDichVu() != null ? dv.getTenDichVu() : "");
        donGiaField.setText(String.format("%.0f", dv.getDonGia()));
        thueVATField.setText(String.valueOf(dv.getThueVAT()));
        moTaArea.setText(dv.getMoTa() != null ? dv.getMoTa() : "");
        trangThaiCombo.setValue(dv.isTrangThai() ? "Đang áp dụng" : "Ngừng áp dụng");
    }

    private void xoaTatCaLoi() {
        datLoiInput(tenDichVuField, errTenDichVu, null, false);
        datLoiInput(donGiaField, errDonGia, null, true);
        datLoiInput(thueVATField, errThueVAT, null, false);
        if (thongBaoLuuLabel != null) {
            thongBaoLuuLabel.setText("");
        }
    }

    private void datLoiInput(Control control, Label errLabel, String loiMsg, boolean isDonGia) {
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
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    @FXML
    public void onLuuThayDoiClick() {
        if (dichVuHienTai == null) {
            hienThongBao("Vui lòng chọn dịch vụ cần cập nhật.", false);
            return;
        }

        xoaTatCaLoi();

        try {
            // 1. Kiểm tra Tên dịch vụ
            String tenDichVu = tenDichVuField.getText();
            if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
                datLoiInput(tenDichVuField, errTenDichVu, "Tên dịch vụ không được để trống.", false);
                tenDichVuField.requestFocus();
                return;
            }
            tenDichVu = tenDichVu.trim().replaceAll("\\s+", " ");
            if (!tenDichVu.isEmpty()) {
                tenDichVu = Character.toUpperCase(tenDichVu.charAt(0)) + tenDichVu.substring(1);
            }

            // 2. Kiểm tra Đơn giá
            String donGiaText = donGiaField.getText();
            if (donGiaText == null || donGiaText.trim().isEmpty()) {
                datLoiInput(donGiaField, errDonGia, "Đơn giá không được để trống.", true);
                donGiaField.requestFocus();
                return;
            }

            double donGia;
            try {
                donGia = Double.parseDouble(donGiaText.trim().replace(",", ""));
            } catch (NumberFormatException e) {
                datLoiInput(donGiaField, errDonGia, "Đơn giá phải là số hợp lệ.", true);
                donGiaField.requestFocus();
                return;
            }

            if (!Double.isFinite(donGia) || donGia < 0) {
                datLoiInput(donGiaField, errDonGia, "Đơn giá phải là số không âm.", true);
                donGiaField.requestFocus();
                return;
            }

            // 3. Kiểm tra Thuế VAT
            String thueVATText = thueVATField.getText();
            if (thueVATText == null || thueVATText.trim().isEmpty()) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT không được để trống.", false);
                thueVATField.requestFocus();
                return;
            }

            double thueVAT;
            try {
                thueVAT = Double.parseDouble(thueVATText.trim());
            } catch (NumberFormatException e) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải là số hợp lệ.", false);
                thueVATField.requestFocus();
                return;
            }

            if (!Double.isFinite(thueVAT) || thueVAT < 0 || thueVAT > 100) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải từ 0% đến 100%.", false);
                thueVATField.requestFocus();
                return;
            }

            // 4. Kiểm tra Trạng thái
            String trangThai = trangThaiCombo.getValue();
            if (trangThai == null) {
                hienThongBao("Vui lòng chọn trạng thái dịch vụ.", false);
                trangThaiCombo.requestFocus();
                return;
            }

            // Cập nhật thông tin dịch vụ hiện tại
            dichVuHienTai.setTenDichVu(tenDichVu);
            dichVuHienTai.setDonGia(donGia);
            dichVuHienTai.setThueVAT(thueVAT);
            dichVuHienTai.setMoTa(moTaArea.getText() != null ? moTaArea.getText().trim() : "");
            dichVuHienTai.setTrangThai("Đang áp dụng".equals(trangThai));

            // Lưu thay đổi xuống database
            boolean thanhCong = dichVuService.capNhat(dichVuHienTai);
            if (thanhCong) {
                hienThongBao("Cập nhật dịch vụ thành công!", true);

                if (onLuuCallback != null) {
                    onLuuCallback.accept(dichVuHienTai);
                }
            } else {
                hienThongBao("Cập nhật dịch vụ thất bại.", false);
            }

        } catch (IllegalArgumentException e) {
            hienThongBao(e.getMessage(), false);
        } catch (Exception e) {
            hienThongBao("Lỗi khi cập nhật dịch vụ: " + e.getMessage(), false);
        }
    }

    public void anChiTiet() {
        setHienThi(false);
    }

    public void setHienThi(boolean hienThi) {
        if (rootPane != null) {
            rootPane.setVisible(hienThi);
            rootPane.setManaged(hienThi);
        }
    }

    @FXML
    public void onDongClick() {
        anChiTiet();
        if (onDongCallback != null) {
            onDongCallback.run();
        }
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        if (thongBaoLuuLabel != null) {
            thongBaoLuuLabel.setText(msg);
            thongBaoLuuLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }

    private void xoaThongBao() {
        if (thongBaoLuuLabel != null) {
            thongBaoLuuLabel.setText("");
        }
    }

    public DichVu getDichVuHienTai() {
        return dichVuHienTai;
    }
}
