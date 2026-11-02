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
 * Controller cho Thanh Header Kỹ Thuật Viên (header-kythuatvien.fxml).
 * Thiết kế giao diện Light Modern, Brand Navy #102A46, Action Orange #F47B31, Surface White #FFFFFF.
 */
public class HeaderKyThuatVienController extends BaseSidebarController {

    @FXML
    private HBox rootHeader;

    @FXML
    private Button btnHeaderDashboard;

    @FXML
    private MenuButton menuHeaderCaSua;

    @FXML
    private MenuButton menuHeaderVatTu;

    @FXML
    private MenuButton menuHeaderNhatKy;

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
        setControlActive(menuHeaderCaSua, false);
        setControlActive(menuHeaderVatTu, false);
        setControlActive(menuHeaderNhatKy, false);

        if (path.contains("phan-cong") || path.contains("kiem-tra") || path.contains("tien-do")) {
            setControlActive(menuHeaderCaSua, true);
        } else if (path.contains("vat-tu") || path.contains("phu-tung") || path.contains("ton-kho")) {
            setControlActive(menuHeaderVatTu, true);
        } else if (path.contains("nhat-ky") || path.contains("lich-su")) {
            setControlActive(menuHeaderNhatKy, true);
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
    // SỰ KIỆN ĐIỀU HƯỚNG TỪ HEADER KỸ THUẬT VIÊN
    // =========================================================================

    @FXML
    public void onDashboardClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(rootHeader,
                "/com/pengugarage/view/kythuatvien/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Kỹ Thuật Viên");
    }

    @FXML
    public void onPhieuPhanCongClick(ActionEvent event) {
        thongBaoPhatTrien("Phiếu phân công của tôi");
    }

    @FXML
    public void onKiemTraChanDoanClick(ActionEvent event) {
        thongBaoPhatTrien("Kiểm tra & Chẩn đoán xe");
    }

    @FXML
    public void onCapNhatTienDoClick(ActionEvent event) {
        thongBaoPhatTrien("Cập nhật tiến độ ca sửa");
    }

    @FXML
    public void onDeXuatVatTuClick(ActionEvent event) {
        thongBaoPhatTrien("Đề xuất xuất kho phụ tùng");
    }

    @FXML
    public void onTraCuuTonKhoClick(ActionEvent event) {
        thongBaoPhatTrien("Tra cứu tồn kho phụ tùng");
    }

    @FXML
    public void onLichSuCaSuaClick(ActionEvent event) {
        thongBaoPhatTrien("Lịch sử ca sửa hoàn tất");
    }

    private void thongBaoPhatTrien(String tenChucNang) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng \"" + tenChucNang + "\" đang được hoàn thiện!");
        alert.showAndWait();
    }
}

