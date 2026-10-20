package com.pengugarage.controller;

import com.pengugarage.util.CoSoDuLieuUtil;
import com.pengugarage.util.DieuHuongUtil;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

import java.sql.Connection;

/**
 * Controller cho màn hình khởi động (Splash / Flash Screen).
 * Hiển thị logo, thanh tiến trình loading và kiểm tra môi trường trước khi vào hệ thống.
 */
public class ManHinhChaoController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label percentLabel;

    @FXML
    public void initialize() {
        batDauKhoiDong();
    }

    private void batDauKhoiDong() {
        // Tạo hiệu ứng nạp tiến trình mượt mà kéo dài ~1.8 giây
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        e -> capNhatTienDo(0.05, "Đang khởi tạo môi trường ứng dụng...")),
                new KeyFrame(Duration.millis(350),
                        e -> capNhatTienDo(0.25, "Đang nạp cấu hình hệ thống & bảng màu chuẩn...")),
                new KeyFrame(Duration.millis(750),
                        e -> {
                            capNhatTienDo(0.55, "Đang kiểm tra kết nối cơ sở dữ liệu MariaDB...");
                            // Kiểm tra kết nối nhanh trong background thread để không chặn UI
                            kiemTraKetNoiNen();
                        }),
                new KeyFrame(Duration.millis(1250),
                        e -> capNhatTienDo(0.85, "Đang chuẩn bị màn hình làm việc & phân quyền...")),
                new KeyFrame(Duration.millis(1700),
                        e -> capNhatTienDo(1.00, "Hệ thống sẵn sàng! Chào mừng đến với Pengu Garage...")),
                new KeyFrame(Duration.millis(2000),
                        e -> chuyenSangDangNhap())
        );

        timeline.play();
    }

    private void capNhatTienDo(double progress, String status) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
        if (percentLabel != null) {
            percentLabel.setText((int) (progress * 100) + "%");
        }
    }

    private void kiemTraKetNoiNen() {
        new Thread(() -> {
            try (Connection conn = CoSoDuLieuUtil.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    Platform.runLater(() -> {
                        if (statusLabel != null && progressBar.getProgress() < 0.8) {
                            statusLabel.setText("Kết nối cơ sở dữ liệu pengu_garage thành công!");
                        }
                    });
                }
            } catch (Exception ex) {
                // Ghi nhận lỗi nhưng không chặn app vào form đăng nhập
                System.err.println("Cảnh báo kết nối ban đầu từ màn hình Splash: " + ex.getMessage());
            }
        }).start();
    }

    private void chuyenSangDangNhap() {
        DieuHuongUtil.chuyenManHinh(progressBar,
                "/com/pengugarage/view/chung/dang-nhap-view.fxml",
                "Pengu Garage Service ERP - Đăng Nhập Hệ Thống");
    }
}
