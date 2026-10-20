package com.pengugarage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Lớp khởi chạy ứng dụng duy nhất của Pengu Garage Service ERP.
 */
public class App {

    public static class GarageApplication extends Application {
        @Override
        public void start(Stage primaryStage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/pengugarage/view/chung/splash-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 760);

            primaryStage.setTitle("Pengu Garage Service ERP - Khởi Động Hệ Thống");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(960);
            primaryStage.setMinHeight(620);
            primaryStage.centerOnScreen();
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(GarageApplication.class, args);
    }
}
