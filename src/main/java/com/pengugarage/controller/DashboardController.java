package com.pengugarage.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller cho Dashboard chính của Quản Lý.
 * Quản lý các thẻ chỉ số KPI, thống kê doanh thu và tiến độ gara.
 * Phần Menu Sidebar đã được tách thành component SidebarQuanLyController độc lập.
 */
public class DashboardController {

    @FXML
    private Label kpiRevenueLabel;

    @FXML
    private Label kpiInvoiceLabel;

    @FXML
    private Label kpiIntakeLabel;

    @FXML
    private Label kpiRepairLabel;

    @FXML
    private Label kpiCustomerLabel;

    @FXML
    private Label kpiLowStockLabel;

    @FXML
    public void initialize() {
        // Khởi tạo các số liệu thống kê Dashboard nếu cần
    }
}
