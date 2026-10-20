package com.pengugarage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Lớp khởi chạy chính của ứng dụng Pengu Garage Service ERP.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/pengugarage/view/chung/dang-nhap-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 680);
        
        primaryStage.setTitle("Pengu Garage Service ERP - Đăng Nhập Hệ Thống");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(620);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
