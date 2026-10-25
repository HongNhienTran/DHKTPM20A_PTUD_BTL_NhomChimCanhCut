package com.pengugarage.controller.component;

import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.service.DanhMucService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    @FXML private TextField donViTinhField;
    @FXML private ComboBox<String> trangThaiCombo;
    @FXML private TextField donGiaBanField;
    @FXML private TextField giaNhapField;
    @FXML private TextField soLuongTonField;
    @FXML private TextField soLuongTonToiThieuField;
    @FXML private TextField thueVATField;
    @FXML private Button btnLuuThayDoi;

    private final DanhMucService danhMucService = new DanhMucService();
    private final com.pengugarage.service.SanPhamService sanPhamService = new com.pengugarage.service.SanPhamService();
    private SanPham sanPhamHienTai;
    private String duongDanAnhDaChon;

    private Runnable onDongCallback;
    private Consumer<SanPham> onLuuCallback;

    @FXML
    public void initialize() {
        // 1. Nạp danh sách trạng thái kinh doanh
        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang kinh doanh", "Ngừng kinh doanh"));

        // 2. Nạp danh sách danh mục từ Database
        napDanhSachDanhMuc();

        // 3. Mặc định ẩn khung chi tiết khi chưa click chọn sản phẩm
        anChiTiet();
    }

    /**
     * Nạp danh mục từ CSDL vào ComboBox
     */
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

    /**
     * Sổ khung chi tiết ra và đổ dữ liệu vào các TextField/ComboBox
     */
    public void hienThiChiTiet(SanPham sp) {
        if (sp == null) {
            anChiTiet();
            return;
        }

        this.sanPhamHienTai = sp;
        this.duongDanAnhDaChon = sp.getAnhSanPham();
        setHienThi(true);
        xoaThongBao();

        detailMaSPLabel.setText(sp.getMaSanPham() != null ? sp.getMaSanPham() : "---");
        tenSanPhamField.setText(sp.getTenSanPham() != null ? sp.getTenSanPham() : "");
        donViTinhField.setText(sp.getDonViTinh() != null ? sp.getDonViTinh() : "");
        donGiaBanField.setText(String.format("%.0f", sp.getDonGiaBan()));
        giaNhapField.setText(String.format("%.0f", sp.getGiaNhap()));
        soLuongTonField.setText(String.valueOf(sp.getSoLuongTon()));
        soLuongTonToiThieuField.setText(String.valueOf(sp.getSoLuongTonToiThieu()));
        thueVATField.setText(String.valueOf(sp.getThueVAT()));

        // Chọn danh mục tương ứng
        if (danhMucCombo.getItems() != null && !danhMucCombo.getItems().isEmpty()) {
            for (DanhMucSanPham dm : danhMucCombo.getItems()) {
                if (dm.getId() == sp.getDanhMucId() || 
                    (sp.getDanhMuc() != null && dm.getId() == sp.getDanhMuc().getId())) {
                    danhMucCombo.setValue(dm);
                    break;
                }
            }
        }

        // Chọn trạng thái kinh doanh
        trangThaiCombo.setValue(sp.isTrangThai() ? "Đang kinh doanh" : "Ngừng kinh doanh");

        // Tải ảnh xem trước
        taiAnhSanPham(duongDanAnhDaChon);
    }

    /**
     * Tải và hiển thị ảnh lên khung preview
     */
    public void taiAnhSanPham(String duongDanAnh) {
        if (duongDanAnh != null && !duongDanAnh.isBlank()) {
            try {
                File file = new File(duongDanAnh);
                if (file.exists() && file.isFile()) {
                    previewImageView.setImage(new Image(file.toURI().toString()));
                    previewImageView.setVisible(true);
                    previewPlaceholderLabel.setVisible(false);
                    return;
                } else if (getClass().getResource(duongDanAnh) != null) {
                    previewImageView.setImage(new Image(getClass().getResource(duongDanAnh).toExternalForm()));
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

    /**
     * Sự kiện nút Chọn / Đổi hình ảnh
     */
    @FXML
    public void onChonAnhClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn Hình Ảnh Cho Sản Phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tệp hình ảnh (*.png, *.jpg, *.jpeg, *.webp)", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        Stage stage = (Stage) rootPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            duongDanAnhDaChon = file.getAbsolutePath();
            taiAnhSanPham(duongDanAnhDaChon);
            hienThongBao("Đã chọn ảnh mới. Hãy bấm Lưu thay đổi!", true);
        }
    }

    // =========================================================================
    // TASK M02-T04: Hiển thị thông tin sản phẩm và cho phép chỉnh sửa
    // Người thực hiện: Tuấn
    // =========================================================================
    @FXML
    public void onLuuThayDoiClick() {
        // TODO: Tuấn thực hiện chức năng chỉnh sửa và lưu cập nhật thông tin sản phẩm (Task M02-T04)
        hienThongBao("Chức năng chỉnh sửa sản phẩm do Tuấn thực hiện (Task M02-T04)", false);
    }

    /**
     * Thu gọn khung chi tiết
     */
    public void anChiTiet() {
        setHienThi(false);
    }

    /**
     * Bật / tắt hiển thị khung
     */
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

    public SanPham getSanPhamHienTai() {
        return sanPhamHienTai;
    }

    public String getDuongDanAnhDaChon() {
        return duongDanAnhDaChon;
    }
}
