package com.pengugarage.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller cho Dashboard dành riêng cho Kỹ thuật viên (Actor: KY_THUAT_VIEN).
 * Quản lý tiến độ phân công sửa chữa, đề xuất phụ tùng và lịch sử ca sửa chữa.
 * Phần Menu Sidebar đã được tách thành component SidebarKyThuatVienController độc lập.
 */
public class DashboardKyThuatVienController {

    @FXML
    private Label kpiDangSuaLabel;

    @FXML
    private Label kpiChoTiepNhanLabel;

    @FXML
    private Label kpiHoanThanhLabel;

    @FXML
    private Label kpiVatTuChoDuyetLabel;

    @FXML
    public void initialize() {
        // Khởi tạo các số liệu thống kê ca sửa chữa của kỹ thuật viên
    }
}
