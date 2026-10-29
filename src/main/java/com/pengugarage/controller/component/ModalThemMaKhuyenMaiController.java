package com.pengugarage.controller.component;

import com.pengugarage.controller.MaKhuyenMaiController;
import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;
import com.pengugarage.service.MaKhuyenMaiService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Controller cho Modal Thêm Mã Khuyến Mãi Mới.
 * =============================================================================
 * PHÂN CÔNG TASK:
 * Task ID: M03-T02 - Nhập thông tin mã khuyến mãi và thêm
 * Người thực hiện: Tuấn
 * =============================================================================
 */
public class ModalThemMaKhuyenMaiController extends Component {

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
        if(!maKMField.getText().matches("KM_[a-bA-Z0-9]{3,}")) {
            JOptionPane.showMessageDialog(this,"Mã khuyến mãi phải là KM_....","Lỗi Nhập liệu",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(loaiGiamCombo.getValue().toString().equals(LoaiGiamGia.PHAN_TRAM.toString())) {
            try {
                double val= Double.parseDouble(giaTriGiamField.getText());
                if(val >= 100 || val <= 0){
                    JOptionPane.showMessageDialog(this,"Giá trị khuyến mãi phần trăm phải là 1 số lớn hơn 0 và nhỏ hơn 100","Lỗi nhập liệu",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Giá trị khuyến mãi phải là số", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if(loaiGiamCombo.getValue().toString().equals(LoaiGiamGia.SO_TIEN.toString())) {
            try {
                int val= Integer.parseInt(giaTriGiamField.getText());
                if(val <= 0){
                    JOptionPane.showMessageDialog(this,"Giá trị khuyến mãi phần trăm số phải là 1 số lớn hơn 0","Lỗi nhập liệu",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Giá trị khuyến mãi phải là số", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        try {
            int val3=Integer.parseInt(giaTriToiThieuField.getText());
            if(val3 <0){
                JOptionPane.showMessageDialog(this, "Giá trị tối thiểu phải lớn hơn 0", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Giá trị khuyến mãi phải là số", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(loaiGiamCombo.getValue().toString().equals(LoaiGiamGia.SO_TIEN.toString())) {
            try {
                double val= Double.parseDouble(giaTriToiThieuField.getText());
                double val1=Double.parseDouble(giaTriGiamField.getText());
                if(val1 >= val){
                    JOptionPane.showMessageDialog(this,"Giá trị tối thiểu phải lớn hơn giá trị được giảm","Lỗi nhập liệu",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Giá trị khuyến mãi tối thiểu phải là số", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        LocalDate first=ngayBatDauPicker.getValue();
        LocalDate last=ngayHetHanPicker.getValue();
        if(first.isAfter(last) || first.equals(last)){
            JOptionPane.showMessageDialog(this,"Ngày bắt đầu phải trước ngày kết thúc","Lỗi nhập liệu",JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int sl=Integer.parseInt(soLuongTonField.getText());
            if(sl <0){
                JOptionPane.showMessageDialog(this, "Giá trị số lượng phải là số lớn hơn 0", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Giá trị số lượng phải là số", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 2. Khởi tạo đối tượng MaKhuyenMai từ form
        MaKhuyenMai km = new MaKhuyenMai();
        km.setMaKhuyenMai(maKMField.getText());
        km.setLoaiGiam(loaiGiamCombo.getValue());
        km.setGiaTriGiam(Double.parseDouble(giaTriGiamField.getText()));
        km.setGiaTriToiThieuApDung(Double.parseDouble(giaTriToiThieuField.getText()));
        km.setNgayBatDauApDung(ngayBatDauPicker.getValue().atStartOfDay());
        km.setNgayHetHan(ngayHetHanPicker.getValue().atStartOfDay().plusHours(23).plusMinutes(59));
        km.setSoLuongTon(Integer.parseInt(soLuongTonField.getText()));
        km.setTrangThai(trangThaiCombo.getValue().equals("Đang áp dụng"));
        // 3. Gọi MaKhuyenMaiService.them(km) để lưu vào database

        // 4. Hiển thị thông báo kết quả và đóng modal nếu thêm thành công
        int n = JOptionPane.showConfirmDialog(this,"Xác nhận thêm?","Xác nhận",JOptionPane.YES_NO_OPTION);
        if((new MaKhuyenMaiService().them(km)== true) &&n ==JOptionPane.YES_OPTION){
        hienThongBao("Thêm thành công", true);
        }else {hienThongBao("Thêm không thành công", false);}
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
