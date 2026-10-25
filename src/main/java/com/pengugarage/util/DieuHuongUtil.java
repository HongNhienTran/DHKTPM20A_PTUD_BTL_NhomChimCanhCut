package com.pengugarage.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Tiện ích điều hướng giao diện trung tâm của hệ thống Pengu Garage.
 * Đảm bảo ứng dụng luôn giữ trạng thái full màn hình (Maximized) mượt mà,
 * không bị chớp giật hay co nhỏ cửa sổ khi chuyển giữa các màn hình.
 */
public class DieuHuongUtil {

    private static String currentFxmlPath = "/com/pengugarage/view/quanly/dashboard-view.fxml";

    public static String getCurrentFxmlPath() {
        return currentFxmlPath;
    }

    public static void setCurrentFxmlPath(String currentFxmlPath) {
        DieuHuongUtil.currentFxmlPath = currentFxmlPath;
    }

    /**
     * Chuyển màn hình thông qua đối tượng Stage.
     * Tận dụng việc thay đổi root của Scene hiện tại để giữ nguyên trạng thái Maximized trên Windows.
     */
    public static void chuyenManHinh(Stage stage, String fxmlPath, String tieuDe) {
        if (stage == null || fxmlPath == null) {
            return;
        }

        currentFxmlPath = fxmlPath;

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(DieuHuongUtil.class.getResource(fxmlPath));
                Parent root = loader.load();
                root.getProperties().put("fxmlPath", fxmlPath);

                Scene scene = stage.getScene();
                if (scene == null) {
                    scene = new Scene(root);
                    stage.setScene(scene);
                } else {
                    scene.setRoot(root);
                }

                if (tieuDe != null && !tieuDe.trim().isEmpty()) {
                    stage.setTitle(tieuDe);
                }

                // Luôn thiết lập và duy trì trạng thái full màn hình
                stage.setMaximized(true);
                if (!stage.isShowing()) {
                    stage.show();
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi điều hướng sang màn hình: " + fxmlPath);
                e.printStackTrace();
            }
        });
    }

    /**
     * Chuyển màn hình tiện lợi thông qua bất kỳ thành phần UI nào trên giao diện hiện tại.
     */
    public static void chuyenManHinh(Node batKyNode, String fxmlPath, String tieuDe) {
        if (batKyNode == null || batKyNode.getScene() == null || batKyNode.getScene().getWindow() == null) {
            return;
        }
        Stage stage = (Stage) batKyNode.getScene().getWindow();
        chuyenManHinh(stage, fxmlPath, tieuDe);
    }

    /**
     * Mở cửa sổ Modal (Dialog) đè lên giao diện hiện tại với kiểu Modality.APPLICATION_MODAL.
     */
    public static Stage moModal(Stage ownerStage, String fxmlPath, String tieuDe) {
        try {
            FXMLLoader loader = new FXMLLoader(DieuHuongUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle(tieuDe != null ? tieuDe : "Pengu Garage");
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            if (ownerStage != null) {
                modalStage.initOwner(ownerStage);
            }
            modalStage.setResizable(false);
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            return modalStage;
        } catch (IOException e) {
            System.err.println("Lỗi khi mở modal: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    public static Stage moModal(Node batKyNode, String fxmlPath, String tieuDe) {
        Stage ownerStage = (batKyNode != null && batKyNode.getScene() != null)
                ? (Stage) batKyNode.getScene().getWindow() : null;
        return moModal(ownerStage, fxmlPath, tieuDe);
    }
}

