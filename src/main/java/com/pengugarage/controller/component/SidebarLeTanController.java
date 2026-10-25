package com.pengugarage.controller.component;

import com.pengugarage.util.DieuHuongUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller cho Sidebar của nhóm Lễ Tân & Thu Ngân
 */
public class SidebarLeTanController extends BaseSidebarController {

    @FXML
    private Button btnDashboard;

    @FXML
    public void onDashboardClick() {
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/letan/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Lễ Tân & Thu Ngân");
    }
}
