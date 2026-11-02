package com.pengugarage.controller.component;

import com.pengugarage.util.DieuHuongUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

/**
 * Controller cho Sidebar của nhóm Quản Lý.
 * Hỗ trợ ĐÓNG / MỞ (Collapsible) thanh menu và highlight nút chức năng đang active
 * theo đúng BẢNG MÀU THƯƠNG HIỆU trích từ Logo Pengu Garage:
 * - Brand Navy: #102A46 / #173B61
 * - Action Orange: #F47B31
 * - Surface Light: #FFFFFF / #F4F7FA
 */
public class SidebarQuanLyController extends BaseSidebarController {

    private static boolean isSidebarCollapsed = false;

    @FXML
    private VBox rootSidebar;

    @FXML
    private Label lblTenHangMuc;

    @FXML
    private Button btnToggleSidebar;

    @FXML
    private VBox boxChucNang;

    @FXML
    private Label lblSectionCungCap;

    @FXML
    private Label lblSectionVanHanh;

    @FXML
    private Label lblSectionQuanTri;

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

    @FXML
    private Button btnHuongDanSuDung;

    private Button currentActiveButton;

    @Override
    public void initialize() {
        super.initialize();
        khoiTaoTooltip();
        capNhatTrangThaiCollapse();
        capNhatKhungChucNangHienTai();
        Platform.runLater(this::capNhatKhungChucNangHienTai);
    }

    private void khoiTaoTooltip() {
        if (btnDashboard != null) btnDashboard.setTooltip(new Tooltip("Dashboard tổng quan"));
        if (btnQuanLySanPham != null) btnQuanLySanPham.setTooltip(new Tooltip("Quản lý sản phẩm & phụ tùng"));
        if (btnQuanLyDichVu != null) btnQuanLyDichVu.setTooltip(new Tooltip("Quản lý dịch vụ kỹ thuật"));
        if (btnQuanLyMaKhuyenMai != null) btnQuanLyMaKhuyenMai.setTooltip(new Tooltip("Quản lý mã khuyến mãi"));
        if (btnQuanLyPhuongTien != null) btnQuanLyPhuongTien.setTooltip(new Tooltip("Quản lý phương tiện"));
        if (btnQuanLyHoaDon != null) btnQuanLyHoaDon.setTooltip(new Tooltip("Quản lý hóa đơn"));
        if (btnQuanLyKhachHang != null) btnQuanLyKhachHang.setTooltip(new Tooltip("Quản lý khách hàng"));
        if (btnQuanLyNhanVien != null) btnQuanLyNhanVien.setTooltip(new Tooltip("Quản lý nhân viên & tài khoản"));
        if (btnBaoCaoThongKe != null) btnBaoCaoThongKe.setTooltip(new Tooltip("Thống kê & báo cáo"));
        if (btnHuongDanSuDung != null) btnHuongDanSuDung.setTooltip(new Tooltip("Hướng dẫn sử dụng"));
    }

    @FXML
    public void onToggleSidebarClick() {
        isSidebarCollapsed = !isSidebarCollapsed;
        capNhatTrangThaiCollapse();
    }

    private void capNhatTrangThaiCollapse() {
        if (rootSidebar == null) return;

        if (isSidebarCollapsed) {
            rootSidebar.setPrefWidth(62.0);
            rootSidebar.setMinWidth(62.0);
            rootSidebar.setMaxWidth(62.0);

            if (lblTenHangMuc != null) {
                lblTenHangMuc.setVisible(false);
                lblTenHangMuc.setManaged(false);
            }
            if (lblSectionCungCap != null) {
                lblSectionCungCap.setVisible(false);
                lblSectionCungCap.setManaged(false);
            }
            if (lblSectionVanHanh != null) {
                lblSectionVanHanh.setVisible(false);
                lblSectionVanHanh.setManaged(false);
            }
            if (lblSectionQuanTri != null) {
                lblSectionQuanTri.setVisible(false);
                lblSectionQuanTri.setManaged(false);
            }

            if (btnToggleSidebar != null) btnToggleSidebar.setText(">");

            // Đặt text dạng icon thu nhỏ
            if (btnDashboard != null) btnDashboard.setText("📊");
            if (btnQuanLySanPham != null) btnQuanLySanPham.setText("📦");
            if (btnQuanLyDichVu != null) btnQuanLyDichVu.setText("🔧");
            if (btnQuanLyMaKhuyenMai != null) btnQuanLyMaKhuyenMai.setText("🏷️");
            if (btnQuanLyPhuongTien != null) btnQuanLyPhuongTien.setText("🚗");
            if (btnQuanLyHoaDon != null) btnQuanLyHoaDon.setText("📄");
            if (btnQuanLyKhachHang != null) btnQuanLyKhachHang.setText("👥");
            if (btnQuanLyNhanVien != null) btnQuanLyNhanVien.setText("👨‍🔧");
            if (btnBaoCaoThongKe != null) btnBaoCaoThongKe.setText("📈");
            if (btnHuongDanSuDung != null) {
                btnHuongDanSuDung.setText("❗");
                btnHuongDanSuDung.setStyle("-fx-alignment: CENTER;");
            }

        } else {
            rootSidebar.setPrefWidth(230.0);
            rootSidebar.setMinWidth(230.0);
            rootSidebar.setMaxWidth(230.0);

            if (lblTenHangMuc != null) {
                lblTenHangMuc.setVisible(true);
                lblTenHangMuc.setManaged(true);
            }
            if (lblSectionCungCap != null) {
                lblSectionCungCap.setVisible(true);
                lblSectionCungCap.setManaged(true);
            }
            if (lblSectionVanHanh != null) {
                lblSectionVanHanh.setVisible(true);
                lblSectionVanHanh.setManaged(true);
            }
            if (lblSectionQuanTri != null) {
                lblSectionQuanTri.setVisible(true);
                lblSectionQuanTri.setManaged(true);
            }

            if (btnToggleSidebar != null) btnToggleSidebar.setText("<");

            // Khôi phục text đầy đủ
            if (btnDashboard != null) btnDashboard.setText("📊 Dashboard");
            if (btnQuanLySanPham != null) btnQuanLySanPham.setText("📦 Sản phẩm & Phụ tùng");
            if (btnQuanLyDichVu != null) btnQuanLyDichVu.setText("🔧 Dịch vụ kỹ thuật");
            if (btnQuanLyMaKhuyenMai != null) btnQuanLyMaKhuyenMai.setText("🏷️ Mã khuyến mãi");
            if (btnQuanLyPhuongTien != null) btnQuanLyPhuongTien.setText("🚗 Quản lý phương tiện");
            if (btnQuanLyHoaDon != null) btnQuanLyHoaDon.setText("📄 Quản lý hóa đơn");
            if (btnQuanLyKhachHang != null) btnQuanLyKhachHang.setText("👥 Quản lý khách hàng");
            if (btnQuanLyNhanVien != null) btnQuanLyNhanVien.setText("👨‍🔧 Quản lý nhân viên");
            if (btnBaoCaoThongKe != null) btnBaoCaoThongKe.setText("📊 Báo cáo & Thống kê");
            if (btnHuongDanSuDung != null) {
                btnHuongDanSuDung.setText("❗ Hướng dẫn sử dụng");
                btnHuongDanSuDung.setStyle("-fx-alignment: CENTER_LEFT;");
            }
        }
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

    /**
     * Cập nhật nút active và đề mục hạng mục theo trang hiện tại
     */
    public void capNhatKhungChucNangHienTai() {
        String path = DieuHuongUtil.getCurrentFxmlPath();

        if ((path == null || path.isEmpty()) && rootSidebar != null && rootSidebar.getScene() != null) {
            Parent root = rootSidebar.getScene().getRoot();
            if (root != null && root.getProperties().containsKey("fxmlPath")) {
                path = String.valueOf(root.getProperties().get("fxmlPath"));
            }
        }

        if (path == null) {
            path = "dashboard";
        }

        if (path.contains("san-pham")) {
            datTieuDeHangMuc("HẠNG MỤC CUNG CẤP");
            datTrangThaiActive(btnQuanLySanPham);
        } else if (path.contains("dich-vu")) {
            datTieuDeHangMuc("HẠNG MỤC CUNG CẤP");
            datTrangThaiActive(btnQuanLyDichVu);
        } else if (path.contains("ma-khuyen-mai")) {
            datTieuDeHangMuc("HẠNG MỤC CUNG CẤP");
            datTrangThaiActive(btnQuanLyMaKhuyenMai);
        } else if (path.contains("nhan-vien")) {
            datTieuDeHangMuc("QUẢN TRỊ CỬA HÀNG");
            datTrangThaiActive(btnQuanLyNhanVien);
        } else if (path.contains("phuong-tien")) {
            datTieuDeHangMuc("VẬN HÀNH GARA");
            datTrangThaiActive(btnQuanLyPhuongTien);
        } else if (path.contains("hoa-don")) {
            datTieuDeHangMuc("VẬN HÀNH GARA");
            datTrangThaiActive(btnQuanLyHoaDon);
        } else if (path.contains("khach-hang")) {
            datTieuDeHangMuc("VẬN HÀNH GARA");
            datTrangThaiActive(btnQuanLyKhachHang);
        } else if (path.contains("thong-ke") || path.contains("bao-cao")) {
            datTieuDeHangMuc("QUẢN TRỊ CỬA HÀNG");
            datTrangThaiActive(btnBaoCaoThongKe);
        } else {
            datTieuDeHangMuc("BẢNG ĐIỀU KHIỂN");
            datTrangThaiActive(btnDashboard);
        }
    }

    private void datTieuDeHangMuc(String tieuDe) {
        if (lblTenHangMuc != null) {
            lblTenHangMuc.setText(tieuDe);
        }
    }

    private void datTrangThaiActive(Button targetBtn) {
        if (targetBtn == null) return;

        currentActiveButton = targetBtn;

        for (Button btn : layDanhSachTatCaNut()) {
            if (btn == null) continue;
            btn.getStyleClass().remove("sidebar-nav-btn-active");
            if (btn == targetBtn) {
                btn.getStyleClass().add("sidebar-nav-btn-active");
            }
        }
    }

    // =========================================================================
    // SỰ KIỆN ĐIỀU HƯỚNG TỪ SIDEBAR
    // =========================================================================

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard");
    }

    @FXML
    public void onQuanLySanPhamClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/quanly/san-pham-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
    }

    @FXML
    public void onQuanLyDichVuClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/quanly/dich-vu-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Dịch Vụ Sửa Chữa");
    }

    @FXML
    public void onQuanLyMaKhuyenMaiClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/quanly/ma-khuyen-mai-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Mã Khuyến Mãi");
    }

    @FXML
    public void onQuanLyPhuongTienClick() {
        thongBaoPhatTrien("Quản lý phương tiện");
    }

    @FXML
    public void onQuanLyHoaDonClick() {
        thongBaoPhatTrien("Quản lý hóa đơn");
    }

    @FXML
    public void onQuanLyKhachHangClick() {
        thongBaoPhatTrien("Quản lý khách hàng");
    }

    @FXML
    public void onQuanLyNhanVienClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/quanly/nhan-vien-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Nhân Sự & Tài Khoản");
    }

    @FXML
    public void onBaoCaoThongKeClick() {
        thongBaoPhatTrien("Thống kê & báo cáo");
    }

    @FXML
    public void onHuongDanSuDungClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hướng dẫn sử dụng");
        alert.setHeaderText("Hệ thống Trợ giúp & Hướng dẫn sử dụng");
        alert.setContentText("Chức năng Hướng dẫn sử dụng hệ thống đang được hoàn thiện!");
        alert.showAndWait();
    }

    private void thongBaoPhatTrien(String tenChucNang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng \"" + tenChucNang + "\" đang được hoàn thiện!");
        alert.showAndWait();
    }
}
