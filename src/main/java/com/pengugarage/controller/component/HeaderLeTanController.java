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
 * Controller cho Thanh Header Tiếp Nhận & Bán Hàng (header-letan.fxml).
 * Thiết kế giao diện Light Modern, Brand Navy #102A46, Action Orange #F47B31, Surface White #FFFFFF.
 */
public class HeaderLeTanController extends BaseSidebarController {

    @FXML
    private HBox rootHeader;

    @FXML
    private Button btnHeaderDashboard;

    @FXML
    private MenuButton menuHeaderTiepNhan;

    @FXML
    private MenuButton menuHeaderBanHang;

    @FXML
    private MenuButton menuHeaderHoaDon;

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

        setControlActive(btnHeaderDashboard, false);
        setControlActive(menuHeaderTiepNhan, false);
        setControlActive(menuHeaderBanHang, false);
        setControlActive(menuHeaderHoaDon, false);

        if (path.contains("tiep-nhan") || path.contains("hang-doi") || path.contains("ho-so")) {
            setControlActive(menuHeaderTiepNhan, true);
        } else if (path.contains("ban-hang") || path.contains("don-hang") || path.contains("tra-cuu")) {
            setControlActive(menuHeaderBanHang, true);
        } else if (path.contains("hoa-don") || path.contains("ban-giao") || path.contains("nhat-ky")) {
            setControlActive(menuHeaderHoaDon, true);
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
    // SỰ KIỆN ĐIỀU HƯỚNG TỪ HEADER TIẾP NHẬN & BÁN HÀNG
    // =========================================================================

    @FXML
    public void onDashboardClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard");
    }

    @FXML
    public void onTiepNhanXeClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/tiep-nhan-xe-view.fxml",
                "Pengu Garage Service ERP - Tiếp Nhận Xe");
    }

    @FXML
    public void onHangDoiXeClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/hang-doi-xe-view.fxml",
                "Pengu Garage Service ERP - Hàng Đợi Xe Tại Xưởng");
    }

    @FXML
    public void onHoSoKhachHangClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/ho-so-khach-hang-view.fxml",
                "Pengu Garage Service ERP - Hồ Sơ Khách Hàng");
    }

    // Các sự kiện Bán hàng
    @FXML
    public void onBanHangClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/ban-hang-view.fxml",
                "Pengu Garage Service ERP - Bán Hàng & Phụ Tùng");
    }

    @FXML
    public void onTraCuuGiaClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/tra-cuu-san-pham-view.fxml",
                "Pengu Garage Service ERP - Tra Cứu Sản Phẩm");
    }

    // Các sự kiện Quản lý hóa đơn
    @FXML
    public void onLapHoaDonClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/lap-hoa-don-view.fxml",
                "Pengu Garage Service ERP - Lập Hóa Đơn & Thu Ngân");
    }

    @FXML
    public void onDanhSachHoaDonClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/letan/danh-sach-hoa-don-view.fxml",
                "Pengu Garage Service ERP - Danh Sách Hóa Đơn");
    }

    @FXML
    public void onNhatKyThuNganClick(ActionEvent event) {
        onDanhSachHoaDonClick(event);
    }

    private void thongBaoPhatTrien(String tenChucNang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng \"" + tenChucNang + "\" đang được hoàn thiện!");
        alert.showAndWait();
    }
}
