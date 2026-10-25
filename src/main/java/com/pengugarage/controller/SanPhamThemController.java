package com.pengugarage.controller;

import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.service.DanhMucService;
import com.pengugarage.service.SanPhamService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    @FXML
    private TextField maSanPhamField;

    @FXML
    private TextField tenSanPhamField;

    @FXML
    private ComboBox<DanhMucSanPham> danhMucCombo;

    @FXML
    private ComboBox<String> donViTinhCombo;

    @FXML
    private TextField giaNhapField;

    @FXML
    private TextField donGiaBanField;

    @FXML
    private TextField thueVATField;

    @FXML
    private TextField soLuongTonField;

    @FXML
    private TextField soLuongTonToiThieuField;

    @FXML
    private ComboBox<String> trangThaiCombo;

    @FXML
    private TextArea moTaArea;

    @FXML
    private ImageView anhPreviewView;

    @FXML
    private VBox placeholderBox;

    @FXML
    private Label duongDanAnhLabel;

    @FXML
    private Label thongBaoLabel;

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
            danhMucCombo.setConverter(new StringConverter<DanhMucSanPham>() {
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
                danhMucCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.err.println("Chưa thể tải danh mục: " + e.getMessage());
        }

        // 2. Đơn vị tính: Lấy từ các đơn vị tính thực tế hiện có trong CSDL
        try {
            List<String> dsDVT = sanPhamService.layDanhSachDonViTinh();
            if (dsDVT != null && !dsDVT.isEmpty()) {
                donViTinhCombo.setItems(FXCollections.observableArrayList(dsDVT));
            } else {
                donViTinhCombo.setItems(FXCollections.observableArrayList("Cái", "Bộ", "Chai", "Can (4L)", "Lít", "Bình", "Cặp", "Cuộn", "Tấm"));
            }
        } catch (Exception e) {
            donViTinhCombo.setItems(FXCollections.observableArrayList("Cái", "Bộ", "Chai", "Can (4L)", "Lít", "Bình", "Cặp", "Cuộn", "Tấm"));
        }
        donViTinhCombo.getSelectionModel().selectFirst();

        // 3. Trạng thái kinh doanh
        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang bán", "Ngừng kinh doanh"));
        trangThaiCombo.getSelectionModel().selectFirst();

        // 4. Giá trị mặc định
        thueVATField.setText("10.0");
        soLuongTonField.setText("0");
        soLuongTonToiThieuField.setText("5");
    }

    /**
     * Sự kiện nút ← Quay lại: trở về trang danh sách sản phẩm
     */
    @FXML
    public void onQuayLaiClick() {
        DieuHuongUtil.chuyenManHinh(tenSanPhamField,
                "/com/pengugarage/view/quanly/san-pham-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
    }

    /**
     * Sự kiện nút Hủy: xác nhận và quay lại
     */
    @FXML
    public void onHuyClick() {
        onQuayLaiClick();
    }

    /**
     * Sự kiện Chọn Ảnh: mở FileChooser để tải ảnh lên và xem trước
     */
    @FXML
    public void onChonAnhClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn Hình Ảnh Sản Phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tệp hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        Stage stage = (Stage) tenSanPhamField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            duongDanAnhDaChon = file.getAbsolutePath();
            try {
                Image image = new Image(file.toURI().toString());
                anhPreviewView.setImage(image);
                placeholderBox.setVisible(false);
                duongDanAnhLabel.setText(file.getName());
            } catch (Exception e) {
                hienThongBao("Lỗi tải hình ảnh xem trước: " + e.getMessage(), true);
            }
        }
    }

    /**
     * Sự kiện Xóa Ảnh: bỏ ảnh đã chọn và hiện lại placeholder
     */
    @FXML
    public void onXoaAnhClick() {
        duongDanAnhDaChon = null;
        anhPreviewView.setImage(null);
        placeholderBox.setVisible(true);
        duongDanAnhLabel.setText("Chưa chọn hình ảnh");
    }

    // =========================================================================
    // TASK M02-T03: Nhập 1 thông tin sản phẩm và thêm, thêm bằng file import (exel)
    // Người thực hiện: Phong
    // =========================================================================
    @FXML
    public void onLuuClick() {
        // TODO: Phong thực hiện chức năng nhập 1 thông tin sản phẩm và thêm (Task M02-T03)
        hienThongBao("Chức năng thêm sản phẩm do Phong thực hiện (Task M02-T03)", false);
    }

    @FXML
    public void onThemBangFileExcelClick() {
        // TODO: Phong thực hiện chức năng thêm bằng file import (exel) (Task M02-T03)
        hienThongBao("Chức năng thêm sản phẩm bằng file Excel do Phong thực hiện (Task M02-T03)", false);
    }

    private void hienThongBao(String msg, boolean isError) {
        if (thongBaoLabel != null) {
            thongBaoLabel.setText(msg);
            thongBaoLabel.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #10b981;");
        }
    }
}
