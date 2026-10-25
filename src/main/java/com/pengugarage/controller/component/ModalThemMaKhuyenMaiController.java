package com.pengugarage.controller.component;

import com.pengugarage.model.enums.LoaiGiamGia;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller cho Modal Thêm Mã Khuyến Mãi Mới.
 * 
 * =============================================================================
 * PHÂN CÔNG TASK:
 * Task ID: M03-T02 - Nhập thông tin mã khuyến mãi và thêm
 * Người thực hiện: Tuấn
 * =============================================================================
 */
public class ModalThemMaKhuyenMaiController {

    @FXML private VBox rootPane;
    @FXML private TextField maKMField;
    @FXML private ComboBox<LoaiGiamGia> loaiGiamCombo;
    @FXML private TextField giaTriGiamField;
    @FXML private TextField giaTriToiThieuField;
    @FXML private DatePicker ngayBatDauPicker;
    @FXML private DatePicker ngayHetHanPicker;
    @FXML private TextField soLuongTonField;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private Label thongBaoLabel;

    @FXML
    public void initialize() {
        if (loaiGiamCombo != null) {
            loaiGiamCombo.setItems(FXCollections.observableArrayList(LoaiGiamGia.values()));
            loaiGiamCombo.setValue(LoaiGiamGia.PHAN_TRAM);
        }
        if (trangThaiCombo != null) {
            trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
            trangThaiCombo.setValue("Đang áp dụng");
        }
        
        // Cấu hình lịch DatePicker chuẩn tiếng Việt dd/MM/yyyy
        if (ngayBatDauPicker != null) {
            com.pengugarage.util.DatePickerUtil.cauHinhDinhDang(ngayBatDauPicker);
            ngayBatDauPicker.setValue(java.time.LocalDate.now());
        }
        if (ngayHetHanPicker != null) {
            com.pengugarage.util.DatePickerUtil.cauHinhDinhDang(ngayHetHanPicker);
            ngayHetHanPicker.setValue(java.time.LocalDate.now().plusMonths(1));
        }
    }


    // =========================================================================
    // TASK M03-T02: Nhập thông tin mã khuyến mãi và thêm
    // Người thực hiện: Tuấn
    // =========================================================================
    @FXML
    public void onLuuClick() {
        // TODO: Tuấn thực hiện code chức năng tại đây:
        // 1. Kiểm tra validation (Mã KM không trống, Giá trị giảm > 0, Ngày hết hạn > Ngày bắt đầu, Số lượng tồn >= 0)
        // 2. Khởi tạo đối tượng MaKhuyenMai từ form
        // 3. Gọi MaKhuyenMaiService.them(km) để lưu vào database
        // 4. Hiển thị thông báo kết quả và đóng modal nếu thêm thành công
        hienThongBao("Chức năng lưu thêm mới mã khuyến mãi do Tuấn thực hiện (Task M03-T02)", false);
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
