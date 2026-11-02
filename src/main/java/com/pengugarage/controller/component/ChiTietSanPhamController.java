package com.pengugarage.controller.component;

import com.pengugarage.controller.SanPhamController;
import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.model.enums.DonViTinh;
import com.pengugarage.service.DanhMucService;
import com.pengugarage.service.SanPhamService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller cho Component Xem & Chỉnh Sửa Chi Tiết Sản Phẩm (chi-tiet-san-pham.fxml).
 * Hỗ trợ các trường TextField, ComboBox danh mục, chọn lại ảnh, thay đổi trạng thái và nút Lưu.
 */
public class ChiTietSanPhamController {

    @FXML private VBox rootPane;
    @FXML private Label detailMaSPLabel;
    @FXML private Label thongBaoLuuLabel;

    // Khung ảnh & Chọn ảnh
    @FXML private ImageView previewImageView;
    @FXML private Label previewPlaceholderLabel;

    // Form chỉnh sửa dạng TextField & ComboBox
    @FXML private TextField tenSanPhamField;
    @FXML private ComboBox<DanhMucSanPham> danhMucCombo;
    @FXML private ComboBox<DonViTinh> donViTinhCombo;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private TextField donGiaBanField;
    @FXML private TextField giaNhapField;
    @FXML private TextField soLuongTonField;
    @FXML private TextField soLuongTonToiThieuField;
    @FXML private TextField thueVATField;
    @FXML private Button btnLuuThayDoi;

    // Các nhãn hiển thị lỗi chữ đỏ ngay dưới từng ô input
    @FXML private Label errTenSP;
    @FXML private Label errDanhMuc;
    @FXML private Label errDonViTinh;
    @FXML private Label errDonGiaBan;
    @FXML private Label errGiaNhap;
    @FXML private Label errSoLuongTon;
    @FXML private Label errSoLuongTonToiThieu;
    @FXML private Label errThueVAT;

    private final DanhMucService danhMucService = new DanhMucService();
    private final SanPhamService sanPhamService = new SanPhamService();
    private SanPham sanPhamHienTai;
    private String duongDanAnhDaChon;

    private Runnable onDongCallback;
    private Consumer<SanPham> onLuuCallback;

    @FXML
    public void initialize() {
        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang kinh doanh", "Ngừng kinh doanh"));
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
        napDanhSachDanhMuc();
        anChiTiet();
    }

    private void napDanhSachDanhMuc() {
        try {
            List<DanhMucSanPham> dsDM = danhMucService.layTatCa();
            if (dsDM != null) {
                danhMucCombo.setItems(FXCollections.observableArrayList(dsDM));
            }
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
        } catch (Exception e) {
            System.err.println("Chưa thể tải danh mục trong component chi tiết: " + e.getMessage());
        }
    }

    public void setOnDongCallback(Runnable callback) {
        this.onDongCallback = callback;
    }

    public void setOnLuuCallback(Consumer<SanPham> callback) {
        this.onLuuCallback = callback;
    }

    private void xoaTatCaLoi() {
        datLoiInput(tenSanPhamField, errTenSP, null, false);
        datLoiInput(danhMucCombo, errDanhMuc, null, false);
        datLoiInput(donViTinhCombo, errDonViTinh, null, false);
        datLoiInput(donGiaBanField, errDonGiaBan, null, true);
        datLoiInput(giaNhapField, errGiaNhap, null, false);
        datLoiInput(soLuongTonField, errSoLuongTon, null, false);
        datLoiInput(soLuongTonToiThieuField, errSoLuongTonToiThieu, null, false);
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

    public void hienThiChiTiet(SanPham sp) {
        if (sp == null) {
            anChiTiet();
            return;
        }

        this.sanPhamHienTai = sp;
        this.duongDanAnhDaChon = sp.getDuongDanAnh();

        setHienThi(true);
        xoaTatCaLoi();

        detailMaSPLabel.setText(sp.getMaSanPham() != null ? sp.getMaSanPham() : "---");
        tenSanPhamField.setText(sp.getTenSanPham() != null ? sp.getTenSanPham() : "");
        donViTinhCombo.setValue(sp.getDonViTinh());
        donGiaBanField.setText(String.format("%.0f", sp.getDonGiaBan()));
        giaNhapField.setText(String.format("%.0f", sp.getGiaNhap()));
        soLuongTonField.setText(String.valueOf(sp.getSoLuongTon()));
        soLuongTonToiThieuField.setText(String.valueOf(sp.getSoLuongTonToiThieu()));
        thueVATField.setText(String.valueOf(sp.getThueVAT()));
        trangThaiCombo.setValue(sp.isTrangThai() ? "Đang kinh doanh" : "Ngừng kinh doanh");

        // Đồng bộ danh mục tương ứng
        if (sp.getDanhMuc() != null && danhMucCombo.getItems() != null) {
            for (DanhMucSanPham dm : danhMucCombo.getItems()) {
                if (dm.getId() == sp.getDanhMuc().getId()) {
                    danhMucCombo.setValue(dm);
                    break;
                }
            }
        }

        hienThiHinhAnh(this.duongDanAnhDaChon);
    }

    private void hienThiHinhAnh(String duongDan) {
        if (duongDan != null && !duongDan.trim().isEmpty()) {
            try {
                File fileAnh = new File(duongDan);
                Image img;
                if (fileAnh.exists()) {
                    img = new Image(fileAnh.toURI().toString());
                } else if (duongDan.startsWith("http") || duongDan.startsWith("file:")) {
                    img = new Image(duongDan);
                } else {
                    img = new Image(getClass().getResourceAsStream(duongDan));
                }

                if (img != null && !img.isError()) {
                    previewImageView.setImage(img);
                    previewImageView.setVisible(true);
                    previewPlaceholderLabel.setVisible(false);
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        previewImageView.setImage(null);
        previewImageView.setVisible(false);
        previewPlaceholderLabel.setVisible(true);
    }

    @FXML
    public void onChonAnhClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh phụ tùng / sản phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tệp hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        Stage stage = (Stage) rootPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.duongDanAnhDaChon = selectedFile.getAbsolutePath();
            hienThiHinhAnh(this.duongDanAnhDaChon);
            hienThongBao("Đã chọn ảnh mới. Hãy bấm 'Lưu Thay Đổi' để cập nhật!", true);
        }
    }

    @FXML
    public void onLuuThayDoiClick() {
        if (sanPhamHienTai == null) {
            hienThongBao("Vui lòng chọn sản phẩm cần cập nhật!", false);
            return;
        }

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
        DonViTinh dvt = donViTinhCombo.getValue();
        if (dvt == null) {
            datLoiInput(donViTinhCombo, errDonViTinh, "Vui lòng chọn đơn vị tính!", false);
            donViTinhCombo.requestFocus();
            return;
        }

        // 4. Kiểm tra Giá nhập
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

        // 5. Kiểm tra Đơn giá bán
        double giaBan = 0;
        String giaBanTxt = donGiaBanField.getText() != null ? donGiaBanField.getText().trim().replace(",", "") : "";
        if (giaBanTxt.isEmpty()) {
            datLoiInput(donGiaBanField, errDonGiaBan, "Đơn giá bán không được để trống!", true);
            donGiaBanField.requestFocus();
            return;
        }
        try {
            giaBan = Double.parseDouble(giaBanTxt);
            if (giaBan < 0) {
                datLoiInput(donGiaBanField, errDonGiaBan, "Đơn giá bán không được nhỏ hơn 0!", true);
                donGiaBanField.requestFocus();
                return;
            }
            if (giaBan < giaNhap) {
                datLoiInput(donGiaBanField, errDonGiaBan, "Giá bán phải lớn hơn hoặc bằng giá nhập!", true);
                donGiaBanField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            datLoiInput(donGiaBanField, errDonGiaBan, "Đơn giá bán phải là số hợp lệ!", true);
            donGiaBanField.requestFocus();
            return;
        }

        // 6. Kiểm tra Số lượng tồn
        int slTon = 0;
        String slTonTxt = soLuongTonField.getText() != null ? soLuongTonField.getText().trim() : "";
        if (!slTonTxt.isEmpty()) {
            try {
                slTon = Integer.parseInt(slTonTxt);
                if (slTon < 0) {
                    datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng tồn phải từ 0 trở lên!", false);
                    soLuongTonField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng tồn phải là số nguyên!", false);
                soLuongTonField.requestFocus();
                return;
            }
        }

        // 7. Kiểm tra Số lượng tồn tối thiểu
        int slTonToiThieu = 0;
        String slTonMinTxt = soLuongTonToiThieuField.getText() != null ? soLuongTonToiThieuField.getText().trim() : "";
        if (!slTonMinTxt.isEmpty()) {
            try {
                slTonToiThieu = Integer.parseInt(slTonMinTxt);
                if (slTonToiThieu < 0) {
                    datLoiInput(soLuongTonToiThieuField, errSoLuongTonToiThieu, "Tồn tối thiểu phải từ 0 trở lên!", false);
                    soLuongTonToiThieuField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(soLuongTonToiThieuField, errSoLuongTonToiThieu, "Số lượng tồn tối thiểu phải là số nguyên!", false);
                soLuongTonToiThieuField.requestFocus();
                return;
            }
        }

        // 8. Kiểm tra Thuế VAT
        double vat = 10.0;
        String vatTxt = thueVATField.getText() != null ? thueVATField.getText().trim() : "";
        if (!vatTxt.isEmpty()) {
            try {
                vat = Double.parseDouble(vatTxt);
                if (vat < 0 || vat > 100) {
                    datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải từ 0 đến 100%!", false);
                    thueVATField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                datLoiInput(thueVATField, errThueVAT, "Thuế VAT phải là số hợp lệ!", false);
                thueVATField.requestFocus();
                return;
            }
        }

        // Gán các giá trị mới vào entity
        sanPhamHienTai.setTenSanPham(tenSP);
        sanPhamHienTai.setDanhMucId(danhMuc.getId());
        sanPhamHienTai.setDanhMuc(danhMuc);
        sanPhamHienTai.setDonViTinh(dvt);
        sanPhamHienTai.setGiaNhap(giaNhap);
        sanPhamHienTai.setDonGiaBan(giaBan);
        sanPhamHienTai.setSoLuongTon(slTon);
        sanPhamHienTai.setSoLuongTonToiThieu(slTonToiThieu);
        sanPhamHienTai.setThueVAT(vat);
        sanPhamHienTai.setTrangThai("Đang kinh doanh".equals(trangThaiCombo.getValue()));
        sanPhamHienTai.setDuongDanAnh(this.duongDanAnhDaChon);

        // Lưu xuống database qua Service
        try {
            boolean kq = sanPhamService.capNhat(sanPhamHienTai);
            if (kq) {
                hienThongBao("Cập nhật sản phẩm '" + sanPhamHienTai.getMaSanPham() + "' thành công!", true);
                if (onLuuCallback != null) {
                    onLuuCallback.accept(sanPhamHienTai);
                }
            } else {
                hienThongBao("Cập nhật thất bại. Vui lòng kiểm tra lại!", false);
            }
        } catch (IllegalArgumentException ex) {
            hienThongBao(ex.getMessage(), false);
        } catch (Exception ex) {
            hienThongBao("Lỗi khi cập nhật sản phẩm: " + ex.getMessage(), false);
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
        thongBaoLuuLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }

    public SanPham getSanPhamHienTai() {
        return sanPhamHienTai;
    }
}
