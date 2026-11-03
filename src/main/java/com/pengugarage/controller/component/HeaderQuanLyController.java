package com.pengugarage.controller.component;

import com.pengugarage.util.DieuHuongUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;

/**
 * Controller cho Thanh Header Quản Lý (header-quanly.fxml).
 * Phối màu theo chuẩn nhận diện thương hiệu Logo Pengu Garage:
 * - Brand Deep Navy: #102A46 / #173B61
 * - Action Orange: #F47B31
 * - Surface Light: #FFFFFF / #EBF3FA
 */
public class HeaderQuanLyController extends BaseSidebarController {

    @FXML
    private HBox rootHeader;

    @FXML
    private Button btnHeaderDashboard;

    @FXML
    private MenuButton menuHeaderCungCap;

    @FXML
    private MenuButton menuHeaderVanHanh;

    @FXML
    private MenuButton menuHeaderQuanTri;

    @Override
    public void initialize() {
        super.initialize();
        capNhatHeaderActive();
        Platform.runLater(this::capNhatHeaderActive);
    }

    public void capNhatHeaderActive() {
        String path = DieuHuongUtil.getCurrentFxmlPath();
        if (path == null) {
            path = "dashboard";
        }

        // Đặt trạng thái active thông qua CSS class header-nav-btn-active
        setControlActive(btnHeaderDashboard, false);
        setControlActive(menuHeaderCungCap, false);
        setControlActive(menuHeaderVanHanh, false);
        setControlActive(menuHeaderQuanTri, false);

        if (path.contains("san-pham") || path.contains("dich-vu") || path.contains("ma-khuyen-mai")) {
            setControlActive(menuHeaderCungCap, true);
        } else if (path.contains("phuong-tien") || path.contains("hoa-don") || path.contains("khach-hang")) {
            setControlActive(menuHeaderVanHanh, true);
        } else if (path.contains("nhan-vien") || path.contains("thong-ke") || path.contains("bao-cao")) {
            setControlActive(menuHeaderQuanTri, true);
        } else {
            setControlActive(btnHeaderDashboard, true);
        }
    }

    private void setControlActive(Control control, boolean active) {
        if (control == null) return;
        control.getStyleClass().remove("header-nav-btn-active");
        if (active) {
            control.getStyleClass().add("header-nav-btn-active");
        }
    }

    // =========================================================================
    // SỰ KIỆN ĐIỀU HƯỚNG TỪ HEADER
    // =========================================================================

    @FXML
    public void onDashboardClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard");
    }

    @FXML
    public void onQuanLySanPhamClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/quanly/san-pham-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
    }

    @FXML
    public void onQuanLyDichVuClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/quanly/dich-vu-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Dịch Vụ Sửa Chữa");
    }

    @FXML
    public void onQuanLyMaKhuyenMaiClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/quanly/ma-khuyen-mai-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Mã Khuyến Mãi");
    }

    @FXML
    public void onQuanLyPhuongTienClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/quanly/phuong-tien-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Phương Tiện");
    }

    @FXML
    public void onQuanLyHoaDonClick(ActionEvent event) {
        thongBaoPhatTrien("Quản lý hóa đơn");
    }

    @FXML
    public void onQuanLyKhachHangClick(ActionEvent event) {
        thongBaoPhatTrien("Quản lý khách hàng");
    }

    @FXML
    public void onQuanLyNhanVienClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/quanly/nhan-vien-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Nhân Sự & Tài Khoản");
    }

    @FXML
    public void onBaoCaoThongKeClick(ActionEvent event) {
        thongBaoPhatTrien("Thống kê & báo cáo");
    }

    private void thongBaoPhatTrien(String tenChucNang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng \"" + tenChucNang + "\" đang được hoàn thiện!");
        alert.showAndWait();
    }
}
