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
 * Controller cho Sidebar của nhóm Kỹ Thuật Viên.
 * Hỗ trợ ĐÓNG / MỞ (Collapsible) thanh menu và highlight nút chức năng đang active qua CSS.
 */
public class SidebarKyThuatVienController extends BaseSidebarController {

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
    private Label lblSectionCaSua;

    @FXML
    private Label lblSectionVatTu;

    @FXML
    private Label lblSectionNhatKy;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnPhieuPhanCong;

    @FXML
    private Button btnKiemTraChanDoan;

    @FXML
    private Button btnCapNhatTienDo;

    @FXML
    private Button btnDeXuatVatTu;

    @FXML
    private Button btnTraCuuTonKho;

    @FXML
    private Button btnLichSuCaSua;

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
        if (btnDashboard != null) btnDashboard.setTooltip(new Tooltip("Dashboard Kỹ Thuật"));
        if (btnPhieuPhanCong != null) btnPhieuPhanCong.setTooltip(new Tooltip("Phiếu phân công của tôi"));
        if (btnKiemTraChanDoan != null) btnKiemTraChanDoan.setTooltip(new Tooltip("Kiểm tra & Chẩn đoán xe"));
        if (btnCapNhatTienDo != null) btnCapNhatTienDo.setTooltip(new Tooltip("Cập nhật tiến độ ca sửa"));
        if (btnDeXuatVatTu != null) btnDeXuatVatTu.setTooltip(new Tooltip("Đề xuất xuất kho phụ tùng"));
        if (btnTraCuuTonKho != null) btnTraCuuTonKho.setTooltip(new Tooltip("Tra cứu tồn kho phụ tùng"));
        if (btnLichSuCaSua != null) btnLichSuCaSua.setTooltip(new Tooltip("Lịch sử ca sửa hoàn tất"));
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
            if (lblSectionCaSua != null) {
                lblSectionCaSua.setVisible(false);
                lblSectionCaSua.setManaged(false);
            }
            if (lblSectionVatTu != null) {
                lblSectionVatTu.setVisible(false);
                lblSectionVatTu.setManaged(false);
            }
            if (lblSectionNhatKy != null) {
                lblSectionNhatKy.setVisible(false);
                lblSectionNhatKy.setManaged(false);
            }

            if (btnToggleSidebar != null) btnToggleSidebar.setText(">");

            // Đặt text dạng icon thu nhỏ
            if (btnDashboard != null) btnDashboard.setText("📊");
            if (btnPhieuPhanCong != null) btnPhieuPhanCong.setText("📋");
            if (btnKiemTraChanDoan != null) btnKiemTraChanDoan.setText("🔍");
            if (btnCapNhatTienDo != null) btnCapNhatTienDo.setText("⚙️");
            if (btnDeXuatVatTu != null) btnDeXuatVatTu.setText("📦");
            if (btnTraCuuTonKho != null) btnTraCuuTonKho.setText("🏷️");
            if (btnLichSuCaSua != null) btnLichSuCaSua.setText("📜");
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
            if (lblSectionCaSua != null) {
                lblSectionCaSua.setVisible(true);
                lblSectionCaSua.setManaged(true);
            }
            if (lblSectionVatTu != null) {
                lblSectionVatTu.setVisible(true);
                lblSectionVatTu.setManaged(true);
            }
            if (lblSectionNhatKy != null) {
                lblSectionNhatKy.setVisible(true);
                lblSectionNhatKy.setManaged(true);
            }

            if (btnToggleSidebar != null) btnToggleSidebar.setText("<");

            // Khôi phục text đầy đủ
            if (btnDashboard != null) btnDashboard.setText("📊 Dashboard Kỹ thuật");
            if (btnPhieuPhanCong != null) btnPhieuPhanCong.setText("📋 Phiếu phân công của tôi");
            if (btnKiemTraChanDoan != null) btnKiemTraChanDoan.setText("🔍 Kiểm tra & Chẩn đoán xe");
            if (btnCapNhatTienDo != null) btnCapNhatTienDo.setText("⚙️ Cập nhật tiến độ ca sửa");
            if (btnDeXuatVatTu != null) btnDeXuatVatTu.setText("📦 Đề xuất xuất kho phụ tùng");
            if (btnTraCuuTonKho != null) btnTraCuuTonKho.setText("🏷️ Tra cứu tồn kho phụ tùng");
            if (btnLichSuCaSua != null) btnLichSuCaSua.setText("📜 Lịch sử ca sửa hoàn tất");
            if (btnHuongDanSuDung != null) {
                btnHuongDanSuDung.setText("❗ Hướng dẫn sử dụng");
                btnHuongDanSuDung.setStyle("-fx-alignment: CENTER_LEFT;");
            }
        }
    }

    private List<Button> layDanhSachTatCaNut() {
        return Arrays.asList(
                btnDashboard,
                btnPhieuPhanCong,
                btnKiemTraChanDoan,
                btnCapNhatTienDo,
                btnDeXuatVatTu,
                btnTraCuuTonKho,
                btnLichSuCaSua
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

        if (path.contains("phan-cong")) {
            datTieuDeHangMuc("CA SỬA & PHÂN CÔNG");
            datTrangThaiActive(btnPhieuPhanCong);
        } else if (path.contains("kiem-tra")) {
            datTieuDeHangMuc("CA SỬA & PHÂN CÔNG");
            datTrangThaiActive(btnKiemTraChanDoan);
        } else if (path.contains("tien-do")) {
            datTieuDeHangMuc("CA SỬA & PHÂN CÔNG");
            datTrangThaiActive(btnCapNhatTienDo);
        } else if (path.contains("vat-tu") || path.contains("de-xuat")) {
            datTieuDeHangMuc("VẬT TƯ & PHỤ TÙNG");
            datTrangThaiActive(btnDeXuatVatTu);
        } else if (path.contains("ton-kho")) {
            datTieuDeHangMuc("VẬT TƯ & PHỤ TÙNG");
            datTrangThaiActive(btnTraCuuTonKho);
        } else if (path.contains("lich-su") || path.contains("nhat-ky")) {
            datTieuDeHangMuc("NHẬT KÝ CÔNG VIỆC");
            datTrangThaiActive(btnLichSuCaSua);
        } else {
            datTieuDeHangMuc("KHÔNG GIAN KỸ THUẬT");
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
    // SỰ KIỆN ĐIỀU HƯỚNG TỪ SIDEBAR KỸ THUẬT VIÊN
    // =========================================================================

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(rootSidebar,
                "/com/pengugarage/view/kythuatvien/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Kỹ Thuật Viên");
    }

    @FXML
    public void onPhieuPhanCongClick() {
        thongBaoPhatTrien("Phiếu phân công của tôi");
    }

    @FXML
    public void onKiemTraChanDoanClick() {
        thongBaoPhatTrien("Kiểm tra & Chẩn đoán xe");
    }

    @FXML
    public void onCapNhatTienDoClick() {
        thongBaoPhatTrien("Cập nhật tiến độ ca sửa");
    }

    @FXML
    public void onDeXuatVatTuClick() {
        thongBaoPhatTrien("Đề xuất xuất kho phụ tùng");
    }

    @FXML
    public void onTraCuuTonKhoClick() {
        thongBaoPhatTrien("Tra cứu tồn kho phụ tùng");
    }

    @FXML
    public void onLichSuCaSuaClick() {
        thongBaoPhatTrien("Lịch sử ca sửa hoàn tất");
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
