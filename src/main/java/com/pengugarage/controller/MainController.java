package com.pengugarage.controller;

import com.pengugarage.util.CoSoDuLieuUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller kiểm tra khung giao diện JavaFX và kiểm tra kết nối Database.
 */
public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    private void onCheckConnectionClick() {
        statusLabel.setText("Đang kiểm tra kết nối MariaDB...");
        statusLabel.setStyle("-fx-text-fill: #0284c7;");

        // Chạy kiểm tra kết nối bất đồng bộ để không treo UI
        new Thread(() -> {
            boolean success = CoSoDuLieuUtil.testConnection();
            javafx.application.Platform.runLater(() -> {
                if (success) {
                    statusLabel.setText("✓ Kết nối MariaDB thành công!");
                    statusLabel.setStyle("-fx-text-fill: #0D6832; -fx-font-weight: bold;");
                } else {
                    statusLabel.setText("✗ Kết nối MariaDB thất bại. Vui lòng kiểm tra file .env");
                    statusLabel.setStyle("-fx-text-fill: #A81B34; -fx-font-weight: bold;");
                }
            });
        }).start();
    }
}
