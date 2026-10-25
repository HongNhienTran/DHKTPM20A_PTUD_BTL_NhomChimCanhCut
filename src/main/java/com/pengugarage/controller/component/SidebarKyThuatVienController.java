package com.pengugarage.controller.component;

import com.pengugarage.util.DieuHuongUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller cho Sidebar của nhóm Kỹ Thuật Viên
 */
public class SidebarKyThuatVienController extends BaseSidebarController {

    @FXML
    private Button btnDashboard;

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/kythuatvien/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Kỹ Thuật Viên");
    }
}
