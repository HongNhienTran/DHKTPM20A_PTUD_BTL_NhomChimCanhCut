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
 * Controller cho Sidebar của nhóm Tiếp Nhận & Bán Hàng.
 * Hỗ trợ ĐÓNG / MỞ (Collapsible) thanh menu và highlight nút chức năng đang active qua CSS.
 */
public class SidebarLeTanController extends BaseSidebarController {

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
    private Label lblSectionTiepNhan;

    @FXML
    private Label lblSectionBanHang;

    @FXML
    private Label lblSectionHoaDon;

    @FXML
    private Label lblSectionLichHen;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnTiepNhanXe;

    @FXML
    private Button btnHangDoiXe;

    @FXML
    private Button btnHoSoKhachHang;

    @FXML
    private Button btnTaoDonBanHang;

    @FXML
    private Button btnTraCuuGia;

    @FXML
    private Button btnLapHoaDon;

    @FXML
    private Button btnDanhSachHoaDon;

    @FXML
    private Button btnBanGiaoXe;

    @FXML
    private Button btnDatLichHen;

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
        if (btnDashboard != null) btnDashboard.setTooltip(new Tooltip("Dashboard Tiếp Nhận & Bán Hàng"));
        if (btnTiepNhanXe != null) btnTiepNhanXe.setTooltip(new Tooltip("Tiếp nhận xe mới (Check-in)"));
        if (btnHangDoiXe != null) btnHangDoiXe.setTooltip(new Tooltip("Hàng đợi xe tại xưởng"));
        if (btnHoSoKhachHang != null) btnHoSoKhachHang.setTooltip(new Tooltip("Hồ sơ xe & Khách hàng"));
        if (btnTaoDonBanHang != null) btnTaoDonBanHang.setTooltip(new Tooltip("Tạo đơn bán hàng & Phụ tùng"));
        if (btnTraCuuGia != null) btnTraCuuGia.setTooltip(new Tooltip("Tra cứu sản phẩm & Giá"));
        if (btnLapHoaDon != null) btnLapHoaDon.setTooltip(new Tooltip("Lập hóa đơn thanh toán"));
        if (btnDanhSachHoaDon != null) btnDanhSachHoaDon.setTooltip(new Tooltip("Danh sách & Nhật ký hóa đơn"));
        if (btnBanGiaoXe != null) btnBanGiaoXe.setTooltip(new Tooltip("Bàn giao xe cho khách"));
        if (btnDatLichHen != null) btnDatLichHen.setTooltip(new Tooltip("Đặt lịch hẹn khách hàng"));
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
            if (lblSectionTiepNhan != null) {
                lblSectionTiepNhan.setVisible(false);
                lblSectionTiepNhan.setManaged(false);
            }
            if (lblSectionBanHang != null) {
                lblSectionBanHang.setVisible(false);
                lblSectionBanHang.setManaged(false);
            }
            if (lblSectionHoaDon != null) {
                lblSectionHoaDon.setVisible(false);
                lblSectionHoaDon.setManaged(false);
            }
            if (lblSectionLichHen != null) {
                lblSectionLichHen.setVisible(false);
                lblSectionLichHen.setManaged(false);
            }

            if (btnToggleSidebar != null) btnToggleSidebar.setText(">");

            // Đặt text dạng icon thu nhỏ
            if (btnDashboard != null) btnDashboard.setText("📊");
            if (btnTiepNhanXe != null) btnTiepNhanXe.setText("🚗");
            if (btnHangDoiXe != null) btnHangDoiXe.setText("⏳");
            if (btnHoSoKhachHang != null) btnHoSoKhachHang.setText("👥");
            if (btnTaoDonBanHang != null) btnTaoDonBanHang.setText("🛒");
            if (btnTraCuuGia != null) btnTraCuuGia.setText("🔍");
            if (btnLapHoaDon != null) btnLapHoaDon.setText("🧾");
            if (btnDanhSachHoaDon != null) btnDanhSachHoaDon.setText("📑");
            if (btnBanGiaoXe != null) btnBanGiaoXe.setText("🔑");
            if (btnDatLichHen != null) btnDatLichHen.setText("📅");
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
            if (lblSectionTiepNhan != null) {
                lblSectionTiepNhan.setVisible(true);
                lblSectionTiepNhan.setManaged(true);
            }
            if (lblSectionBanHang != null) {
                lblSectionBanHang.setVisible(true);
                lblSectionBanHang.setManaged(true);
            }
            if (lblSectionHoaDon != null) {
                lblSectionHoaDon.setVisible(true);
                lblSectionHoaDon.setManaged(true);
            }
            if (lblSectionLichHen != null) {
                lblSectionLichHen.setVisible(true);
                lblSectionLichHen.setManaged(true);
            }

            if (btnToggleSidebar != null) btnToggleSidebar.setText("<");

            // Khôi phục text đầy đủ
            if (btnDashboard != null) btnDashboard.setText("📊 Dashboard Tiếp nhận");
            if (btnTiepNhanXe != null) btnTiepNhanXe.setText("🚗 Tiếp nhận xe (Check-in)");
            if (btnHangDoiXe != null) btnHangDoiXe.setText("⏳ Hàng đợi xe tại xưởng");
            if (btnHoSoKhachHang != null) btnHoSoKhachHang.setText("👥 Hồ sơ xe & Khách hàng");
            if (btnTaoDonBanHang != null) btnTaoDonBanHang.setText("🛒 Tạo đơn bán hàng");
            if (btnTraCuuGia != null) btnTraCuuGia.setText("🔍 Tra cứu sản phẩm & Giá");
            if (btnLapHoaDon != null) btnLapHoaDon.setText("🧾 Lập hóa đơn thanh toán");
            if (btnDanhSachHoaDon != null) btnDanhSachHoaDon.setText("📑 Danh sách hóa đơn");
            if (btnBanGiaoXe != null) btnBanGiaoXe.setText("🔑 Bàn giao xe cho khách");
            if (btnDatLichHen != null) btnDatLichHen.setText("📅 Đặt lịch hẹn khách hàng");
            if (btnHuongDanSuDung != null) {
                btnHuongDanSuDung.setText("❗ Hướng dẫn sử dụng");
                btnHuongDanSuDung.setStyle("-fx-alignment: CENTER_LEFT;");
            }
        }
    }

    private List<Button> layDanhSachTatCaNut() {
        return Arrays.asList(
                btnDashboard,
                btnTiepNhanXe,
                btnHangDoiXe,
                btnHoSoKhachHang,
                btnTaoDonBanHang,
                btnTraCuuGia,
                btnLapHoaDon,
                btnDanhSachHoaDon,
                btnBanGiaoXe,
                btnDatLichHen
        );
    }

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

        if (path.contains("tiep-nhan")) {
            datTieuDeHangMuc("TIẾP NHẬN & ĐIỀU PHỐI");
            datTrangThaiActive(btnTiepNhanXe);
        } else if (path.contains("hang-doi")) {
            datTieuDeHangMuc("TIẾP NHẬN & ĐIỀU PHỐI");
            datTrangThaiActive(btnHangDoiXe);
        } else if (path.contains("ho-so")) {
            datTieuDeHangMuc("TIẾP NHẬN & ĐIỀU PHỐI");
            datTrangThaiActive(btnHoSoKhachHang);
        } else if (path.contains("ban-hang")) {
            datTieuDeHangMuc("BÁN HÀNG & PHỤ TÙNG");
            datTrangThaiActive(btnTaoDonBanHang);
        } else if (path.contains("tra-cuu-gia")) {
            datTieuDeHangMuc("BÁN HÀNG & PHỤ TÙNG");
            datTrangThaiActive(btnTraCuuGia);
        } else if (path.contains("hoa-don")) {
            datTieuDeHangMuc("QUẢN LÝ HÓA ĐƠN");
            datTrangThaiActive(btnLapHoaDon);
        } else if (path.contains("danh-sach-hoa-don")) {
            datTieuDeHangMuc("QUẢN LÝ HÓA ĐƠN");
            datTrangThaiActive(btnDanhSachHoaDon);
        } else if (path.contains("ban-giao")) {
            datTieuDeHangMuc("QUẢN LÝ HÓA ĐƠN");
            datTrangThaiActive(btnBanGiaoXe);
        } else if (path.contains("lich-hen")) {
            datTieuDeHangMuc("LỊCH HẸN BẢO DƯỠNG");
            datTrangThaiActive(btnDatLichHen);
        } else {
            datTieuDeHangMuc("TIẾP NHẬN & BÁN HÀNG");
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
    // SỰ KIỆN ĐIỀU HƯỚNG TỪ SIDEBAR TIẾP NHẬN & BÁN HÀNG
    // =========================================================================

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/letan/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Tiếp Nhận & Bán Hàng");
    }

    @FXML
    public void onTiepNhanXeClick() {
        thongBaoPhatTrien("Tiếp nhận xe mới (Check-in)");
    }

    @FXML
    public void onHangDoiXeClick() {
        thongBaoPhatTrien("Hàng đợi xe tại xưởng");
    }

    @FXML
    public void onHoSoKhachHangClick() {
        thongBaoPhatTrien("Hồ sơ xe & Khách hàng");
    }

    @FXML
    public void onTaoDonBanHangClick() {
        thongBaoPhatTrien("Tạo đơn bán hàng & Phụ tùng");
    }

    @FXML
    public void onTraCuuGiaClick() {
        thongBaoPhatTrien("Tra cứu sản phẩm & Giá");
    }

    @FXML
    public void onLapHoaDonClick() {
        thongBaoPhatTrien("Lập hóa đơn thanh toán");
    }

    @FXML
    public void onDanhSachHoaDonClick() {
        thongBaoPhatTrien("Danh sách & Nhật ký hóa đơn");
    }

    @FXML
    public void onBanGiaoXeClick() {
        thongBaoPhatTrien("Bàn giao xe cho khách");
    }

    @FXML
    public void onDatLichHenClick() {
        thongBaoPhatTrien("Đặt lịch hẹn khách hàng");
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
