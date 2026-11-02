package com.pengugarage.controller.component;

import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;
import com.pengugarage.service.MaKhuyenMaiService;
import com.pengugarage.util.DatePickerUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Controller cho Modal Thêm Mã Khuyến Mãi Mới (modal-them-ma-khuyen-mai.fxml)
 */
public class ModalThemMaKhuyenMaiController {

    @FXML private VBox rootPane;
    @FXML private TextField maKMField;
    @FXML private ComboBox<String> loaiGiamCombo;
    @FXML private TextField giaTriGiamField;
    @FXML private TextField giaTriToiThieuField;
    @FXML private DatePicker ngayBatDauPicker;
    @FXML private DatePicker ngayHetHanPicker;
    @FXML private TextField soLuongTonField;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private Label thongBaoLabel;

    // Các nhãn hiển thị lỗi chữ đỏ ngay dưới từng ô input
    @FXML private Label errMaKM;
    @FXML private Label errGiaTriGiam;
    @FXML private Label errGiaTriToiThieu;
    @FXML private Label errNgayBatDau;
    @FXML private Label errNgayHetHan;
    @FXML private Label errSoLuongTon;

    private final MaKhuyenMaiService maKhuyenMaiService = new MaKhuyenMaiService();

    @FXML
    public void initialize() {
        loaiGiamCombo.setItems(FXCollections.observableArrayList("Theo Phần Trăm (%)", "Theo Tiền Mặt (VNĐ)"));
        loaiGiamCombo.setValue("Theo Phần Trăm (%)");

        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));
        trangThaiCombo.setValue("Đang áp dụng");

        DatePickerUtil.cauHinhDinhDang(ngayBatDauPicker);
        DatePickerUtil.cauHinhDinhDang(ngayHetHanPicker);
        ngayBatDauPicker.setValue(LocalDate.now());
        ngayHetHanPicker.setValue(LocalDate.now().plusMonths(1));
    }

    private void xoaTatCaLoi() {
        datLoiInput(maKMField, errMaKM, null);
        datLoiInput(giaTriGiamField, errGiaTriGiam, null);
        datLoiInput(giaTriToiThieuField, errGiaTriToiThieu, null);
        datLoiInput(ngayBatDauPicker, errNgayBatDau, null);
        datLoiInput(ngayHetHanPicker, errNgayHetHan, null);
        datLoiInput(soLuongTonField, errSoLuongTon, null);
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

    @FXML
    public void onLuuClick() {
        xoaTatCaLoi();

        try {
            // 1. Kiểm tra Mã khuyến mãi
            String maKM = maKMField.getText() != null ? maKMField.getText().trim().toUpperCase() : "";
            if (maKM.isEmpty()) {
                datLoiInput(maKMField, errMaKM, "Mã khuyến mãi không được để trống!");
                maKMField.requestFocus();
                return;
            }

            if (!maKM.matches("^[A-Za-z0-9_\\-]{3,30}$")) {
                datLoiInput(maKMField, errMaKM, "Mã khuyến mãi từ 3-30 ký tự (chữ cái, chữ số, gạch ngang/dưới)!");
                maKMField.requestFocus();
                return;
            }

            if (maKhuyenMaiService.kiemTraTrungMa(maKM, 0)) {
                datLoiInput(maKMField, errMaKM, "Mã khuyến mãi '" + maKM + "' đã tồn tại!");
                maKMField.requestFocus();
                return;
            }

            // 2. Kiểm tra Loại giảm và Giá trị giảm
            String loaiGiamStr = loaiGiamCombo.getValue();
            LoaiGiamGia loaiGiam = (loaiGiamStr != null && loaiGiamStr.contains("Tiền"))
                    ? LoaiGiamGia.SO_TIEN : LoaiGiamGia.PHAN_TRAM;

            String giaTriGiamTxt = giaTriGiamField.getText() != null ? giaTriGiamField.getText().trim().replace(",", "") : "";
            if (giaTriGiamTxt.isEmpty()) {
                datLoiInput(giaTriGiamField, errGiaTriGiam, "Vui lòng nhập giá trị giảm!");
                giaTriGiamField.requestFocus();
                return;
            }

            double giaTriGiam;
            try {
                giaTriGiam = Double.parseDouble(giaTriGiamTxt);
            } catch (NumberFormatException e) {
                datLoiInput(giaTriGiamField, errGiaTriGiam, "Giá trị giảm phải là số hợp lệ!");
                giaTriGiamField.requestFocus();
                return;
            }

            if (loaiGiam == LoaiGiamGia.PHAN_TRAM) {
                if (giaTriGiam <= 0 || giaTriGiam > 100) {
                    datLoiInput(giaTriGiamField, errGiaTriGiam, "Giảm theo % phải lớn hơn 0 và nhỏ hơn hoặc bằng 100%!");
                    giaTriGiamField.requestFocus();
                    return;
                }
            } else {
                if (giaTriGiam <= 0) {
                    datLoiInput(giaTriGiamField, errGiaTriGiam, "Số tiền giảm phải lớn hơn 0!");
                    giaTriGiamField.requestFocus();
                    return;
                }
            }

            // 3. Kiểm tra Hóa đơn tối thiểu
            double giaTriToiThieu = 0;
            String toiThieuTxt = giaTriToiThieuField.getText() != null ? giaTriToiThieuField.getText().trim().replace(",", "") : "";
            if (!toiThieuTxt.isEmpty()) {
                try {
                    giaTriToiThieu = Double.parseDouble(toiThieuTxt);
                    if (giaTriToiThieu < 0) {
                        datLoiInput(giaTriToiThieuField, errGiaTriToiThieu, "Giá trị đơn hàng tối thiểu không được âm!");
                        giaTriToiThieuField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    datLoiInput(giaTriToiThieuField, errGiaTriToiThieu, "Giá trị tối thiểu phải là số!");
                    giaTriToiThieuField.requestFocus();
                    return;
                }
            }

            if (loaiGiam == LoaiGiamGia.SO_TIEN && giaTriToiThieu > 0 && giaTriGiam > giaTriToiThieu) {
                datLoiInput(giaTriGiamField, errGiaTriGiam, "Số tiền giảm không được vượt quá đơn hàng tối thiểu!");
                giaTriGiamField.requestFocus();
                return;
            }

            // 4. Kiểm tra Ngày áp dụng
            LocalDate startLocalDate = ngayBatDauPicker.getValue();
            LocalDate endLocalDate = ngayHetHanPicker.getValue();
            if (startLocalDate == null) {
                datLoiInput(ngayBatDauPicker, errNgayBatDau, "Vui lòng chọn ngày bắt đầu áp dụng!");
                ngayBatDauPicker.requestFocus();
                return;
            }
            if (endLocalDate == null) {
                datLoiInput(ngayHetHanPicker, errNgayHetHan, "Vui lòng chọn ngày hết hạn!");
                ngayHetHanPicker.requestFocus();
                return;
            }
            if (endLocalDate.isBefore(startLocalDate)) {
                datLoiInput(ngayHetHanPicker, errNgayHetHan, "Ngày hết hạn không thể trước ngày bắt đầu!");
                ngayHetHanPicker.requestFocus();
                return;
            }

            LocalDateTime ngayBatDau = startLocalDate.atStartOfDay();
            LocalDateTime ngayHetHan = endLocalDate.atTime(23, 59, 59);

            // 5. Kiểm tra Số lượng phát hành
            int soLuong = 100;
            String soLuongTxt = soLuongTonField.getText() != null ? soLuongTonField.getText().trim() : "";
            if (!soLuongTxt.isEmpty()) {
                try {
                    soLuong = Integer.parseInt(soLuongTxt);
                    if (soLuong < 0) {
                        datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng không được nhỏ hơn 0!");
                        soLuongTonField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng phát hành phải là số nguyên!");
                    soLuongTonField.requestFocus();
                    return;
                }
            }

            boolean trangThai = "Đang áp dụng".equals(trangThaiCombo.getValue());

            // 6. Tạo đối tượng và lưu vào Database
            MaKhuyenMai km = new MaKhuyenMai();
            km.setMaKhuyenMai(maKM);
            km.setLoaiGiam(loaiGiam);
            km.setGiaTriGiam(giaTriGiam);
            km.setGiaTriToiThieuApDung(giaTriToiThieu);
            km.setNgayBatDauApDung(ngayBatDau);
            km.setNgayHetHan(ngayHetHan);
            km.setSoLuongTon(soLuong);
            km.setTrangThai(trangThai);

            boolean ketQua = maKhuyenMaiService.them(km);
            if (ketQua) {
                hienThongBao("Thêm mã khuyến mãi thành công!", true);
                onDongClick();
            } else {
                hienThongBao("Thêm mã khuyến mãi thất bại. Vui lòng thử lại!", false);
            }

        } catch (IllegalArgumentException e) {
            hienThongBao(e.getMessage(), false);
        } catch (Exception e) {
            hienThongBao("Lỗi khi thêm mã khuyến mãi: " + e.getMessage(), false);
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
            thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;" );
        }
    }
}
