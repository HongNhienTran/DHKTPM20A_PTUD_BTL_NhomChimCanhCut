package com.pengugarage.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller cho Dashboard dành riêng cho Lễ tân & Thu ngân (Actor: LE_TAN).
 * Quản lý hàng đợi xe tại xưởng, tiếp nhận khách hàng và hóa đơn thanh toán.
 * Phần Menu Sidebar đã được tách thành component SidebarLeTanController độc lập.
 */
public class DashboardLeTanController {

    @FXML
    private Label kpiTiepNhanHomNayLabel;

    @FXML
    private Label kpiChoPhanCongLabel;

    @FXML
    private Label kpiChoThanhToanLabel;

    @FXML
    private Label kpiDoanhThuNgayLabel;

    @FXML
    public void initialize() {
        // Khởi tạo các số liệu thống kê lượt xe và hóa đơn lễ tân
    }
}
