package com.pengugarage.controller.component;

import com.pengugarage.model.entity.DichVu;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;
import com.pengugarage.service.DichVuService;
import javafx.scene.control.Alert;

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

    private DichVu dichVuHienTai;
    private Runnable onDongCallback;
    private Consumer<DichVu> onLuuCallback;
    private final DichVuService dichVuService = new DichVuService();
    @FXML
    public void initialize() {
        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
        anChiTiet();
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
        xoaThongBao();

        detailMaDVLabel.setText(dv.getMaDichVu() != null ? dv.getMaDichVu() : "---");
        tenDichVuField.setText(dv.getTenDichVu() != null ? dv.getTenDichVu() : "");
        donGiaField.setText(String.format("%.0f", dv.getDonGia()));
        thueVATField.setText(String.valueOf(dv.getThueVAT()));
        moTaArea.setText(dv.getMoTa() != null ? dv.getMoTa() : "");
        trangThaiCombo.setValue(dv.isTrangThai() ? "Đang áp dụng" : "Ngừng áp dụng");
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    @FXML
    public void onLuuThayDoiClick() {
        if (dichVuHienTai == null) {
            hienThongBao("Vui lòng chọn dịch vụ cần cập nhật.", false);
            return;}
        try {
            // Lấy và kiểm tra tên dịch vụ
            String tenDichVu = tenDichVuField.getText();
            if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
                hienThongBao("Tên dịch vụ không được để trống.", false);
                tenDichVuField.requestFocus();
                return;}

            //Lấy và kiểm tra đơn giá
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

            if (!Double.isFinite(donGia) || donGia < 0) {
                hienThongBao("Đơn giá phải là số không âm.", false);
                donGiaField.requestFocus();
                return;}

            // Lấy và kiểm tra thuế VAT
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

            if (!Double.isFinite(thueVAT) || thueVAT < 0 || thueVAT > 100) {
                hienThongBao("Thuế VAT phải từ 0 đến 100%.", false);
                thueVATField.requestFocus();
                return;}

            // Lấy và kiểm tra trạng thái
            String trangThai = trangThaiCombo.getValue();

            if (trangThai == null) {
                hienThongBao("Vui lòng chọn trạng thái dịch vụ.", false);
                trangThaiCombo.requestFocus();
                return;}

            // Cập nhật 5 thuộc tính được phép chỉnh sửa
            dichVuHienTai.setTenDichVu(tenDichVu.trim());
            dichVuHienTai.setDonGia(donGia);
            dichVuHienTai.setThueVAT(thueVAT);
            dichVuHienTai.setMoTa(moTaArea.getText());
            dichVuHienTai.setTrangThai(
                    trangThai.equals("Đang áp dụng"));

            // Lưu thay đổi xuống database
            boolean thanhCong = dichVuService.capNhat(dichVuHienTai);

            if (thanhCong) {
                hienThongBao("Cập nhật dịch vụ thành công!", true);

                // Làm mới danh sách sau khi cập nhật thành công
                if (onLuuCallback != null) {
                    onLuuCallback.accept(dichVuHienTai);
                }
            } else {
                hienThongBao("Cập nhật dịch vụ thất bại.", false);
            }

        } catch (IllegalArgumentException e) {
            hienThongBao(e.getMessage(), false);

        } catch (Exception e) {
            hienThongBao(
                    "Lỗi khi cập nhật dịch vụ: " + e.getMessage(),
                    false
            );
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
        thongBaoLuuLabel.setText(msg);
        thongBaoLuuLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #ef4444;");
    }

    private void xoaThongBao() {
        thongBaoLuuLabel.setText("");
    }

    public DichVu getDichVuHienTai() {
        return dichVuHienTai;
    }
}
