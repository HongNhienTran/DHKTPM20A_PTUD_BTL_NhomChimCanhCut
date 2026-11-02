package com.pengugarage.controller;

import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.model.enums.DonViTinh;
import com.pengugarage.service.DanhMucService;
import com.pengugarage.service.SanPhamService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

/**
 * Controller cho màn hình Thêm Sản Phẩm Mới (san-pham-them.fxml)
 */
public class SanPhamThemController {

    @FXML private TextField maSanPhamField;
    @FXML private TextField tenSanPhamField;
    @FXML private ComboBox<DanhMucSanPham> danhMucCombo;
    @FXML private ComboBox<DonViTinh> donViTinhCombo;
    @FXML private TextField giaNhapField;
    @FXML private TextField donGiaBanField;
    @FXML private TextField thueVATField;
    @FXML private TextField soLuongTonField;
    @FXML private TextField soLuongTonToiThieuField;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private TextArea moTaArea;
    @FXML private ImageView anhPreviewView;
    @FXML private VBox placeholderBox;
    @FXML private Label duongDanAnhLabel;
    @FXML private Label thongBaoLabel;

    // Các nhãn hiển thị lỗi chữ đỏ ngay dưới từng ô input
    @FXML private Label errMaSP;
    @FXML private Label errTenSP;
    @FXML private Label errDanhMuc;
    @FXML private Label errDonViTinh;
    @FXML private Label errGiaNhap;
    @FXML private Label errDonGiaBan;
    @FXML private Label errThueVAT;
    @FXML private Label errSoLuongTon;
    @FXML private Label errSoLuongTonToiThieu;

    private final DanhMucService danhMucService;
    private final SanPhamService sanPhamService;
    private String duongDanAnhDaChon = null;

    public SanPhamThemController() {
        this.danhMucService = new DanhMucService();
        this.sanPhamService = new SanPhamService();
    }

    @FXML
    public void initialize() {
        khoiTaoDuLieu();
    }

    /**
     * Nạp dữ liệu danh mục, đơn vị tính mẫu và trạng thái vào các ComboBox
     */
    private void khoiTaoDuLieu() {
        // 1. Danh mục sản phẩm
        try {
            List<DanhMucSanPham> dsDanhMuc = danhMucService.layTatCa();
            danhMucCombo.setItems(FXCollections.observableArrayList(dsDanhMuc));
            danhMucCombo.setConverter(new StringConverter<>() {
                @Override
                public String toString(DanhMucSanPham dm) {
                    return dm != null ? dm.getTenDanhMuc() : "";
                }

                @Override
                public DanhMucSanPham fromString(String string) {
                    return null;
                }
            });
            if (!dsDanhMuc.isEmpty()) {
                danhMucCombo.setValue(dsDanhMuc.get(0));
            }
        } catch (Exception e) {
            hienThongBao("Không thể tải danh mục sản phẩm từ CSDL: " + e.getMessage(), false);
        }

        // 2. Đơn vị tính chuẩn hóa theo Enum DonViTinh
        donViTinhCombo.setItems(FXCollections.observableArrayList(DonViTinh.values()));
        donViTinhCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DonViTinh dvt) {
                return SanPhamController.layTenHienThiDVT(dvt);
            }

            @Override
            public DonViTinh fromString(String string) {
                return SanPham.parseDonViTinh(string);
            }
        });
        donViTinhCombo.setValue(DonViTinh.CAI);

        // 3. Trạng thái kinh doanh
        trangThaiCombo.setItems(FXCollections.observableArrayList(
                "Đang kinh doanh", "Ngừng kinh doanh"
        ));
        trangThaiCombo.setValue("Đang kinh doanh");

        // 4. Giá trị mặc định
        thueVATField.setText("10.0");
        soLuongTonField.setText("0");
        soLuongTonToiThieuField.setText("5");
    }

    private void xoaTatCaLoi() {
        datLoiInput(maSanPhamField, errMaSP, null, false);
        datLoiInput(tenSanPhamField, errTenSP, null, false);
        datLoiInput(danhMucCombo, errDanhMuc, null, false);
        datLoiInput(donViTinhCombo, errDonViTinh, null, false);
        datLoiInput(giaNhapField, errGiaNhap, null, false);
        datLoiInput(donGiaBanField, errDonGiaBan, null, true);
        datLoiInput(thueVATField, errThueVAT, null, false);
        datLoiInput(soLuongTonField, errSoLuongTon, null, false);
        datLoiInput(soLuongTonToiThieuField, errSoLuongTonToiThieu, null, false);
        if (thongBaoLabel != null) {
            thongBaoLabel.setText("");
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

    /**
     * Mở hộp thoại chọn ảnh đại diện sản phẩm từ máy tính
     */
    @FXML
    public void onChonAnhClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tệp hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        Stage stage = (Stage) tenSanPhamField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.duongDanAnhDaChon = selectedFile.getAbsolutePath();
            try {
                Image img = new Image(selectedFile.toURI().toString());
                anhPreviewView.setImage(img);
                anhPreviewView.setVisible(true);
                placeholderBox.setVisible(false);
                duongDanAnhLabel.setText(selectedFile.getName());
            } catch (Exception e) {
                hienThongBao("Không thể tải hình ảnh đã chọn!", false);
            }
        }
    }

    /**
     * Xóa ảnh đã chọn và trả về trạng thái mặc định
     */
    @FXML
    public void onXoaAnhClick() {
        this.duongDanAnhDaChon = null;
        anhPreviewView.setImage(null);
        anhPreviewView.setVisible(false);
        placeholderBox.setVisible(true);
        duongDanAnhLabel.setText("Chưa chọn tệp ảnh");
    }

    /**
     * Sự kiện khi nhấn nút "Lưu Sản Phẩm"
     */
    @FXML
    public void onLuuClick() {
        xoaTatCaLoi();

        // 1. Kiểm tra Tên sản phẩm
        String tenSP = tenSanPhamField.getText() != null ? tenSanPhamField.getText().trim() : "";
        if (tenSP.isEmpty()) {
            datLoiInput(tenSanPhamField, errTenSP, "Tên sản phẩm không được để trống!", false);
            tenSanPhamField.requestFocus();
            return;
        }

        // 2. Kiểm tra Danh mục
        DanhMucSanPham danhMuc = danhMucCombo.getValue();
        if (danhMuc == null) {
            datLoiInput(danhMucCombo, errDanhMuc, "Vui lòng chọn danh mục cho sản phẩm!", false);
            danhMucCombo.requestFocus();
            return;
        }

        // 3. Kiểm tra Đơn vị tính
        DonViTinh donViTinh = donViTinhCombo.getValue();
        if (donViTinh == null) {
            datLoiInput(donViTinhCombo, errDonViTinh, "Vui lòng chọn đơn vị tính!", false);
            donViTinhCombo.requestFocus();
            return;
        }

        // 4. Kiểm tra Mã sản phẩm (tự sinh nếu để trống)
        String maSP = maSanPhamField.getText() != null ? maSanPhamField.getText().trim() : "";
        if (maSP.isEmpty()) {
            maSP = "SP" + (System.currentTimeMillis() % 1000000);
            maSanPhamField.setText(maSP);
        } else if (sanPhamService.kiemTraMaSanPhamTonTai(maSP)) {
            datLoiInput(maSanPhamField, errMaSP, "Mã sản phẩm '" + maSP + "' đã tồn tại!", false);
            maSanPhamField.requestFocus();
            return;
        }

        // 5. Kiểm tra Giá nhập
        double giaNhap = 0;
        String giaNhapTxt = giaNhapField.getText() != null ? giaNhapField.getText().trim().replace(",", "") : "";
        if (!giaNhapTxt.isEmpty()) {
            try {
                giaNhap = Double.parseDouble(giaNhapTxt);
                if (giaNhap < 0) {
                    datLoiInput(giaNhapField, errGiaNhap, "Giá nhập không được nhỏ hơn 0!", false);
                    giaNhapField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(giaNhapField, errGiaNhap, "Giá nhập phải là số hợp lệ!", false);
                giaNhapField.requestFocus();
                return;
            }
        }

        // 6. Kiểm tra Đơn giá bán
        double giaBan = 0;
        String giaBanTxt = donGiaBanField.getText() != null ? donGiaBanField.getText().trim().replace(",", "") : "";
        if (giaBanTxt.isEmpty()) {
            datLoiInput(donGiaBanField, errDonGiaBan, "Vui lòng nhập giá bán niêm yết!", true);
            donGiaBanField.requestFocus();
            return;
        }
        try {
            giaBan = Double.parseDouble(giaBanTxt);
            if (giaBan < 0) {
                datLoiInput(donGiaBanField, errDonGiaBan, "Giá bán niêm yết không được nhỏ hơn 0!", true);
                donGiaBanField.requestFocus();
                return;
            }
            if (giaBan < giaNhap) {
                datLoiInput(donGiaBanField, errDonGiaBan, "Giá bán (" + giaBan + ") phải ≥ giá nhập (" + giaNhap + ")!", true);
                donGiaBanField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            datLoiInput(donGiaBanField, errDonGiaBan, "Giá bán phải là số hợp lệ!", true);
            donGiaBanField.requestFocus();
            return;
        }

        // 7. Kiểm tra Thuế VAT
        double thueVAT = 10.0;
        String thueTxt = thueVATField.getText() != null ? thueVATField.getText().trim() : "";
        if (!thueTxt.isEmpty()) {
            try {
                thueVAT = Double.parseDouble(thueTxt);
                if (thueVAT < 0 || thueVAT > 100) {
                    datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải từ 0% đến 100%!", false);
                    thueVATField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải là số hợp lệ!", false);
                thueVATField.requestFocus();
                return;
            }
        }

        // 8. Kiểm tra Số lượng tồn ban đầu
        int slTon = 0;
        String slTonTxt = soLuongTonField.getText() != null ? soLuongTonField.getText().trim() : "";
        if (!slTonTxt.isEmpty()) {
            try {
                slTon = Integer.parseInt(slTonTxt);
                if (slTon < 0) {
                    datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng tồn không được nhỏ hơn 0!", false);
                    soLuongTonField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng tồn phải là số nguyên!", false);
                soLuongTonField.requestFocus();
                return;
            }
        }

        // 9. Kiểm tra Số lượng tồn tối thiểu
        int slTonMin = 5;
        String slTonMinTxt = soLuongTonToiThieuField.getText() != null ? soLuongTonToiThieuField.getText().trim() : "";
        if (!slTonMinTxt.isEmpty()) {
            try {
                slTonMin = Integer.parseInt(slTonMinTxt);
                if (slTonMin < 0) {
                    datLoiInput(soLuongTonToiThieuField, errSoLuongTonToiThieu, "Tồn tối thiểu không được nhỏ hơn 0!", false);
                    soLuongTonToiThieuField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(soLuongTonToiThieuField, errSoLuongTonToiThieu, "Tồn tối thiểu phải là số nguyên!", false);
                soLuongTonToiThieuField.requestFocus();
                return;
            }
        }

        boolean trangThai = "Đang kinh doanh".equals(trangThaiCombo.getValue());
        String moTa = moTaArea != null ? moTaArea.getText() : "";

        // Tạo đối tượng SanPham
        SanPham sp = new SanPham();
        sp.setMaSanPham(maSP);
        sp.setTenSanPham(tenSP);
        sp.setDanhMucId(danhMuc.getId());
        sp.setDonViTinh(donViTinh);
        sp.setGiaNhap(giaNhap);
        sp.setDonGiaBan(giaBan);
        sp.setThueVAT(thueVAT);
        sp.setSoLuongTon(slTon);
        sp.setSoLuongTonToiThieu(slTonMin);
        sp.setTrangThai(trangThai);
        sp.setDuongDanAnh(this.duongDanAnhDaChon);

        // Lưu vào cơ sở dữ liệu
        try {
            boolean thanhCong = sanPhamService.them(sp);
            if (thanhCong) {
                hienThongBao("Thêm sản phẩm mới thành công! Mã SP: " + sp.getMaSanPham(), true);
                xoaTrangForm();
            } else {
                hienThongBao("Thêm sản phẩm thất bại. Vui lòng kiểm tra lại kết nối cơ sở dữ liệu!", false);
            }
        } catch (IllegalArgumentException ex) {
            hienThongBao(ex.getMessage(), false);
        } catch (Exception ex) {
            hienThongBao("Lỗi khi thêm sản phẩm: " + ex.getMessage(), false);
        }
    }

    /**
     * Sự kiện quay lại trang danh sách sản phẩm
     */
    @FXML
    public void onQuayLaiClick() {
        DieuHuongUtil.chuyenManHinh(tenSanPhamField,
                "/com/pengugarage/view/quanly/san-pham-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
    }

    @FXML
    public void onHuyClick() {
        onQuayLaiClick();
    }

    /**
     * Xóa trắng các ô nhập liệu sau khi thêm thành công để sẵn sàng nhập tiếp
     */
    private void xoaTrangForm() {
        maSanPhamField.clear();
        tenSanPhamField.clear();
        giaNhapField.clear();
        donGiaBanField.clear();
        if (moTaArea != null) {
            moTaArea.clear();
        }
        thueVATField.setText("10.0");
        soLuongTonField.setText("0");
        soLuongTonToiThieuField.setText("5");
        donViTinhCombo.setValue(DonViTinh.CAI);
        onXoaAnhClick();
        xoaTatCaLoi();
    }

    @FXML
    public void onThemBangFileExcelClick() {
        hienThongBao("Chức năng thêm sản phẩm bằng file Excel do Phong thực hiện (Task M02-T03)", false);
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        if (thongBaoLabel != null) {
            thongBaoLabel.setText(msg);
            thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }
}
