package com.pengugarage.controller.component;

import com.pengugarage.util.DieuHuongUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller cho Sidebar của nhóm Quản Lý
 */
public class SidebarQuanLyController extends BaseSidebarController {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnQuanLyNhanVien;

    @FXML
    private Button btnQuanLySanPham;

    @FXML
    private Button btnQuanLyDichVu;

    @FXML
    private Button btnBaoCaoThongKe;

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Quản Lý");
    }

    @FXML
    public void onQuanLyNhanVienClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/quanly/nhan-vien-view.fxml",
                "Pengu Garage Service ERP - Quản Lý Nhân Sự & Tài Khoản");
    }

    @FXML
    public void onQuanLySanPhamClick() {
        if (getClass().getResource("/com/pengugarage/view/quanly/san-pham-view.fxml") != null) {
            DieuHuongUtil.chuyenManHinh(userNameLabel,
                    "/com/pengugarage/view/quanly/san-pham-view.fxml",
                    "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
        }
    }
}
