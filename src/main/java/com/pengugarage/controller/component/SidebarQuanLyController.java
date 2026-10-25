package com.pengugarage.controller.component;

import com.pengugarage.util.DieuHuongUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

/**
 * Controller cho Sidebar của nhóm Quản Lý.
 * Tự động xác định màn hình đang hiển thị và kích hoạt khung (Active highlight)
 * cho chức năng tương ứng trên sidebar.
 */
public class SidebarQuanLyController extends BaseSidebarController {

    @FXML
    private VBox rootSidebar;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnQuanLySanPham;

    @FXML
    private Button btnQuanLyDichVu;

    @FXML
    private Button btnQuanLyMaKhuyenMai;

    @FXML
    private Button btnQuanLyPhuongTien;

    @FXML
    private Button btnQuanLyHoaDon;

    @FXML
    private Button btnQuanLyKhachHang;

    @FXML
    private Button btnQuanLyNhanVien;

    @FXML
    private Button btnBaoCaoThongKe;

    private Button currentActiveButton;

    private static final String STYLE_ACTIVE =
            "-fx-background-color: #1a385c; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: 800; " +
            "-fx-font-size: 14.5px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: #2b5588; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 10px 14px; " +
            "-fx-cursor: hand;";

    private static final String STYLE_DASHBOARD_INACTIVE =
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #cbd5e1; " +
            "-fx-font-weight: 800; " +
            "-fx-font-size: 14.5px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: transparent; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 10px 14px; " +
            "-fx-cursor: hand;";

    private static final String STYLE_INACTIVE =
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #cbd5e1; " +
            "-fx-font-weight: 750; " +
            "-fx-font-size: 14.5px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: transparent; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 10px 14px; " +
            "-fx-cursor: hand;";

    private static final String STYLE_HOVER =
            "-fx-background-color: #142438; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: 800; " +
            "-fx-font-size: 14.5px; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: #1c3554; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 10px 14px; " +
            "-fx-cursor: hand;";

    @Override
    public void initialize() {
        super.initialize();
        khoiTaoSuKienHover();

        // Cập nhật trạng thái active ngay lập tức
        capNhatKhungChucNangHienTai();

        // Và chạy lại sau khi Scene được gắn hoàn tất để đảm bảo chính xác 100%
        Platform.runLater(this::capNhatKhungChucNangHienTai);
    }

    private List<Button> layDanhSachTatCaNut() {
        return Arrays.asList(
                btnDashboard,
                btnQuanLySanPham,
                btnQuanLyDichVu,
                btnQuanLyMaKhuyenMai,
                btnQuanLyPhuongTien,
                btnQuanLyHoaDon,
                btnQuanLyKhachHang,
                btnQuanLyNhanVien,
                btnBaoCaoThongKe
        );
    }

    private void khoiTaoSuKienHover() {
        for (Button btn : layDanhSachTatCaNut()) {
            if (btn == null) continue;
            btn.setOnMouseEntered(e -> {
                if (btn != currentActiveButton) {
                    btn.setStyle(STYLE_HOVER);
                }
            });
            btn.setOnMouseExited(e -> {
                if (btn != currentActiveButton) {
                    btn.setStyle(btn == btnDashboard ? STYLE_DASHBOARD_INACTIVE : STYLE_INACTIVE);
                }
            });
        }
    }

    /**
     * Xác định trang quản lý hiện tại để kích hoạt khung tương ứng
     */
    public void capNhatKhungChucNangHienTai() {
        String path = DieuHuongUtil.getCurrentFxmlPath();

        // Kiểm tra qua thuộc tính root của Scene nếu path chưa được set
        if ((path == null || path.isEmpty()) && rootSidebar != null && rootSidebar.getScene() != null) {
            Parent root = rootSidebar.getScene().getRoot();
            if (root != null && root.getProperties().containsKey("fxmlPath")) {
                path = String.valueOf(root.getProperties().get("fxmlPath"));
            }
        }

        // Fallback nhận diện đặc trưng UI của Scene
        if ((path == null || path.isEmpty()) && rootSidebar != null && rootSidebar.getScene() != null) {
            Scene scene = rootSidebar.getScene();
            if (scene.lookup("#tableSanPham") != null || scene.lookup("#btnThemSanPham") != null || scene.lookup("#tenSanPhamField") != null) {
                path = "san-pham";
            } else if (scene.lookup("#tableDichVu") != null || scene.lookup("#btnThemDichVu") != null || scene.lookup("#tenDichVuField") != null) {
                path = "dich-vu";
            } else if (scene.lookup("#tableKhuyenMai") != null || scene.lookup("#btnThemKhuyenMai") != null || scene.lookup("#maKhuyenMaiField") != null) {
                path = "ma-khuyen-mai";
            } else if (scene.lookup("#tableNhanVien") != null || scene.lookup("#btnThemNhanVien") != null || scene.lookup("#hoTenField") != null) {
                path = "nhan-vien";
            } else {
                path = "dashboard";
            }
        }

        if (path == null) {
            path = "dashboard";
        }

        // Đặt khung nổi bật cho nút tương ứng
        if (path.contains("san-pham")) {
            datTrangThaiActive(btnQuanLySanPham);
        } else if (path.contains("dich-vu")) {
            datTrangThaiActive(btnQuanLyDichVu);
        } else if (path.contains("ma-khuyen-mai")) {
            datTrangThaiActive(btnQuanLyMaKhuyenMai);
        } else if (path.contains("nhan-vien")) {
            datTrangThaiActive(btnQuanLyNhanVien);
        } else if (path.contains("phuong-tien")) {
            datTrangThaiActive(btnQuanLyPhuongTien);
        } else if (path.contains("hoa-don")) {
            datTrangThaiActive(btnQuanLyHoaDon);
        } else if (path.contains("khach-hang")) {
            datTrangThaiActive(btnQuanLyKhachHang);
        } else if (path.contains("thong-ke") || path.contains("bao-cao")) {
            datTrangThaiActive(btnBaoCaoThongKe);
        } else {
            datTrangThaiActive(btnDashboard);
        }
    }

    private void datTrangThaiActive(Button targetBtn) {
        currentActiveButton = targetBtn;
        for (Button btn : layDanhSachTatCaNut()) {
            if (btn == null) continue;
            if (btn == targetBtn) {
                btn.setStyle(STYLE_ACTIVE);
                btn.getStyleClass().remove("sidebar-nav-btn");
                if (!btn.getStyleClass().contains("sidebar-nav-btn-active")) {
                    btn.getStyleClass().add("sidebar-nav-btn-active");
                }
            } else {
                btn.setStyle(btn == btnDashboard ? STYLE_DASHBOARD_INACTIVE : STYLE_INACTIVE);
                btn.getStyleClass().remove("sidebar-nav-btn-active");
                if (!btn.getStyleClass().contains("sidebar-nav-btn")) {
                    btn.getStyleClass().add("sidebar-nav-btn");
                }
            }
        }
    }

    // =========================================================================
    // SỰ KIỆN ĐIỀU HƯỚNG CÁC TRANG QUẢN LÝ
    // =========================================================================

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Quản Lý");
    }

    @FXML
    public void onQuanLySanPhamClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/san-pham-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/san-pham-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
        }
    }

    @FXML
    public void onQuanLyDichVuClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/dich-vu-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/dich-vu-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Dịch Vụ Gara");
        }
    }

    @FXML
    public void onQuanLyMaKhuyenMaiClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/ma-khuyen-mai-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/ma-khuyen-mai-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Mã Khuyến Mãi");
        }
    }

    @FXML
    public void onQuanLyNhanVienClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/quanly/nhan-vien-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Nhân Sự & Tài Khoản");
    }

    @FXML
    public void onQuanLyPhuongTienClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/phuong-tien-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/phuong-tien-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Phương Tiện");
        } else {
            hienThongBaoChuaPhatTrien("Quản lý phương tiện");
        }
    }

    @FXML
    public void onQuanLyHoaDonClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/hoa-don-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/hoa-don-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Hóa Đơn");
        } else {
            hienThongBaoChuaPhatTrien("Quản lý hóa đơn");
        }
    }

    @FXML
    public void onQuanLyKhachHangClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/khach-hang-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/khach-hang-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Khách Hàng");
        } else {
            hienThongBaoChuaPhatTrien("Quản lý khách hàng");
        }
    }

    @FXML
    public void onBaoCaoThongKeClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/bao-cao-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/bao-cao-view.fxml",
                    "Pengu Garage Service ERP - Báo Cáo & Thống Kê");
        } else {
            hienThongBaoChuaPhatTrien("Thống kê & báo cáo");
        }
    }

    private void hienThongBaoChuaPhatTrien(String tenChucNang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo hệ thống");
        alert.setHeaderText("Chức năng: " + tenChucNang);
        alert.setContentText("Chức năng " + tenChucNang + " đang được hoàn thiện và sẽ sẵn sàng trong bản cập nhật tiếp theo.");
        alert.showAndWait();
    }
}
