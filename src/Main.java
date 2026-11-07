import ui.ManHinhDangNhap;
import ui.ManHinhSplash;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManHinhSplash splash = new ManHinhSplash(() -> {
                ManHinhDangNhap manHinhDangNhap = new ManHinhDangNhap();
                manHinhDangNhap.setVisible(true);
            });
            splash.hienThiVaChay();
        });
    }
}
