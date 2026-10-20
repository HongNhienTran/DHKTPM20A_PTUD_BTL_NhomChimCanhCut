package com.pengugarage;

/**
 * Lớp Launcher phụ trợ để khởi chạy ứng dụng JavaFX trong IntelliJ IDEA / Eclipse.
 * 
 * LƯU Ý KỸ THUẬT:
 * Từ Java 11+, nếu chạy trực tiếp class kế thừa từ Application (như Main.java) trên Classpath,
 * JVM sẽ báo lỗi: 'Error: JavaFX runtime components are missing, and are required to run this application'.
 * Lớp App này không kế thừa Application, giúp vượt qua kiểm tra module-path nghiêm ngặt của JVM
 * và khởi động giao diện JavaFX mượt mà trực tiếp bằng nút Run trong IDE mà không cần cấu hình VM Options.
 */
public class App {

    public static void main(String[] args) {
        Main.main(args);
    }
}
