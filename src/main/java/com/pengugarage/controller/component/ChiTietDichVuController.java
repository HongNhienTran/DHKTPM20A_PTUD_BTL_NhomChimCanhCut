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
        // TODO: Tân thực hiện chức năng lưu cập nhật thông tin dịch vụ (Task M02-T03)
        hienThongBao("Chức năng cập nhật dịch vụ do Tân thực hiện (Task M02-T03)", false);
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
