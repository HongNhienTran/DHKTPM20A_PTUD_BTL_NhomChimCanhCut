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

    @FXML
    private MenuButton menuHeaderLichHen;

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
        setControlActive(menuHeaderLichHen, false);

        if (path.contains("tiep-nhan") || path.contains("hang-doi") || path.contains("ho-so")) {
            setControlActive(menuHeaderTiepNhan, true);
        } else if (path.contains("ban-hang") || path.contains("don-hang") || path.contains("tra-cuu-gia")) {
            setControlActive(menuHeaderBanHang, true);
        } else if (path.contains("hoa-don") || path.contains("ban-giao") || path.contains("nhat-ky")) {
            setControlActive(menuHeaderHoaDon, true);
        } else if (path.contains("lich-hen")) {
            setControlActive(menuHeaderLichHen, true);
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
                "Pengu Garage Service ERP - Dashboard Tiếp Nhận & Bán Hàng");
    }

    @FXML
    public void onTiepNhanXeClick(ActionEvent event) {
        thongBaoPhatTrien("Tiếp nhận xe mới (Check-in)");
    }

    @FXML
    public void onHangDoiXeClick(ActionEvent event) {
        thongBaoPhatTrien("Hàng đợi xe tại xưởng");
    }

    @FXML
    public void onHoSoKhachHangClick(ActionEvent event) {
        thongBaoPhatTrien("Hồ sơ xe & Khách hàng");
    }

    // Các sự kiện Bán hàng
    @FXML
    public void onBanHangClick(ActionEvent event) {
        thongBaoPhatTrien("Tạo đơn bán hàng & Phụ tùng");
    }

    @FXML
    public void onTraCuuGiaClick(ActionEvent event) {
        thongBaoPhatTrien("Tra cứu sản phẩm & Bảng giá");
    }

    @FXML
    public void onDonHangBanLeClick(ActionEvent event) {
        thongBaoPhatTrien("Danh sách đơn bán lẻ");
    }

    // Các sự kiện Quản lý hóa đơn
    @FXML
    public void onLapHoaDonClick(ActionEvent event) {
        thongBaoPhatTrien("Lập hóa đơn thanh toán");
    }

    @FXML
    public void onBanGiaoXeClick(ActionEvent event) {
        thongBaoPhatTrien("Bàn giao xe cho khách");
    }

    @FXML
    public void onNhatKyThuNganClick(ActionEvent event) {
        thongBaoPhatTrien("Danh sách & Nhật ký hóa đơn");
    }

    @FXML
    public void onDatLichHenClick(ActionEvent event) {
        thongBaoPhatTrien("Đặt lịch hẹn khách hàng");
    }

    private void thongBaoPhatTrien(String tenChucNang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng \"" + tenChucNang + "\" đang được hoàn thiện!");
        alert.showAndWait();
    }
}
