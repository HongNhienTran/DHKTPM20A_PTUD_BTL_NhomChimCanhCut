package ui;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ManHinhSplash extends JFrame {
    private static final int CHIEU_RONG = 1200;
    private static final int CHIEU_CAO = 675;

    private static final Color MAU_NEN_TRUNG_TAM = new Color(20, 32, 48);
    private static final Color MAU_NEN_RIA = new Color(10, 16, 26);
    private static final Color MAU_KHUNG_LOGO = new Color(28, 38, 54);
    private static final Color MAU_VIEN_LOGO = new Color(115, 78, 58);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_SANG = new Color(249, 115, 22);
    private static final Color MAU_CHU_TRANG = new Color(248, 250, 252);
    private static final Color MAU_CHU_PHU = new Color(139, 155, 176);
    private static final Color MAU_VIEN_THANH_TIEN_TRINH = new Color(195, 206, 220, 210);
    private static final Color MAU_NEN_THANH_TIEN_TRINH = new Color(19, 28, 42);
    private static final Color MAU_CHAM_TRANG_THAI = new Color(20, 184, 166);

    private int phanTram = 0;
    private String thongDiepTrangThai = "Đang khởi tạo cấu hình hệ thống...";
    private float phaNhipSang = 0f;
    private BufferedImage anhLogoDaScale;
    private Timer boDemTienTrinh;
    private final Runnable khiHoanTat;

    public ManHinhSplash() {
        this(null);
    }

    public ManHinhSplash(Runnable khiHoanTat) {
        this.khiHoanTat = khiHoanTat;
        taiLogoThuongHieu();
        khoiTaoCuaSo();
    }

    private void khoiTaoCuaSo() {
        setTitle("Pengu Garage - Service ERP");
        setUndecorated(true);
        setSize(new Dimension(CHIEU_RONG, CHIEU_CAO));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        SplashCanvas canvas = new SplashCanvas();
        setContentPane(canvas);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dungVaDong();
                }
            }
        });
    }

    private void taiLogoThuongHieu() {
        BufferedImage logoGoc = docAnhLogo();
        if (logoGoc != null) {
            setIconImage(logoGoc);
            this.anhLogoDaScale = thuNhoAnhChatLuongCao(logoGoc, 128, 128);
        }
    }

    private BufferedImage docAnhLogo() {
        File[] cacDuongDan = new File[]{
                new File("img/logo_pengu-garage.png"),
                new File(System.getProperty("user.dir"), "img/logo_pengu-garage.png")
        };
        for (File file : cacDuongDan) {
            if (file.exists() && file.isFile()) {
                try {
                    return ImageIO.read(file);
                } catch (IOException ignored) {
                }
            }
        }
        URL url = ManHinhSplash.class.getResource("/img/logo_pengu-garage.png");
        if (url != null) {
            try {
                return ImageIO.read(url);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private BufferedImage thuNhoAnhChatLuongCao(BufferedImage anhNguon, int rongDich, int caoDich) {
        int w = anhNguon.getWidth();
        int h = anhNguon.getHeight();
        BufferedImage hienTai = anhNguon;

        while (w > rongDich * 2 || h > caoDich * 2) {
            if (w > rongDich * 2) {
                w /= 2;
            }
            if (h > caoDich * 2) {
                h /= 2;
            }
            BufferedImage buoc = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buoc.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(hienTai, 0, 0, w, h, null);
            g2.dispose();
            hienTai = buoc;
        }

        BufferedImage ketQua = new BufferedImage(rongDich, caoDich, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = ketQua.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(hienTai, 0, 0, rongDich, caoDich, null);
        g2.dispose();
        return ketQua;
    }

    public void hienThiVaChay() {
        setVisible(true);
        boDemTienTrinh = new Timer(32, e -> {
            phaNhipSang += 0.12f;
            if (phanTram < 100) {
                phanTram++;
                capNhatThongDiepTheoPhanTram(phanTram);
                repaint();
            } else {
                boDemTienTrinh.stop();
                Timer treChuyenManHinh = new Timer(350, ev -> {
                    ((Timer) ev.getSource()).stop();
                    if (khiHoanTat != null) {
                        dispose();
                        khiHoanTat.run();
                    }
                });
                treChuyenManHinh.setRepeats(false);
                treChuyenManHinh.start();
            }
        });
        boDemTienTrinh.start();
    }

    public void capNhatTienTrinh(int giaTriPhanTram, String thongDiep) {
        this.phanTram = Math.max(0, Math.min(100, giaTriPhanTram));
        if (thongDiep != null && !thongDiep.trim().isEmpty()) {
            this.thongDiepTrangThai = thongDiep.trim();
        } else {
            capNhatThongDiepTheoPhanTram(this.phanTram);
        }
        repaint();
    }

    private void capNhatThongDiepTheoPhanTram(int pt) {
        if (pt < 25) {
            thongDiepTrangThai = "Đang khởi tạo cấu hình hệ thống...";
        } else if (pt < 70) {
            thongDiepTrangThai = "Đang kết nối cơ sở dữ liệu Pengu Garage...";
        } else if (pt < 92) {
            thongDiepTrangThai = "Đang nạp danh mục dịch vụ & phụ tùng...";
        } else {
            thongDiepTrangThai = "Sẵn sàng khởi chạy giao diện làm việc...";
        }
    }

    private void dungVaDong() {
        if (boDemTienTrinh != null && boDemTienTrinh.isRunning()) {
            boDemTienTrinh.stop();
        }
        dispose();
    }

    private Font taoFontCoDanChu(String tenFont, int kieu, float coChu, float khoangCachChu) {
        Font coBan = new Font(tenFont, kieu, Math.round(coChu)).deriveFont(coChu);
        Map<TextAttribute, Object> thuocTinh = new HashMap<>();
        thuocTinh.put(TextAttribute.TRACKING, khoangCachChu);
        return coBan.deriveFont(thuocTinh);
    }

    private class SplashCanvas extends JPanel {
        SplashCanvas() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();
            int tamX = w / 2;

            // 1. Nền tối chuyển sắc tỏa tròn từ tâm
            RadialGradientPaint nenToaTron = new RadialGradientPaint(
                    new Point2D.Float(tamX, h * 0.42f),
                    Math.max(w, h) * 0.65f,
                    new float[]{0f, 0.55f, 1f},
                    new Color[]{MAU_NEN_TRUNG_TAM, new Color(14, 22, 35), MAU_NEN_RIA}
            );
            g2.setPaint(nenToaTron);
            g2.fillRect(0, 0, w, h);

            // 2. Khung chứa Logo bo góc kèm hiệu ứng quầng sáng cam dịu
            int kichThuocKhung = 160;
            int khungX = tamX - kichThuocKhung / 2;
            int khungY = (int) (h * 0.255f);
            int boGocKhung = 32;

            for (int i = 10; i >= 1; i--) {
                int moRong = i * 2;
                int doMo = Math.max(2, 18 - i);
                g2.setColor(new Color(234, 106, 31, doMo));
                g2.setStroke(new BasicStroke(1.6f));
                g2.draw(new RoundRectangle2D.Float(
                        khungX - moRong / 2f,
                        khungY - moRong / 2f,
                        kichThuocKhung + moRong,
                        kichThuocKhung + moRong,
                        boGocKhung + moRong,
                        boGocKhung + moRong
                ));
            }

            RoundRectangle2D khungLogo = new RoundRectangle2D.Float(
                    khungX, khungY, kichThuocKhung, kichThuocKhung, boGocKhung, boGocKhung
            );
            g2.setColor(MAU_KHUNG_LOGO);
            g2.fill(khungLogo);

            g2.setColor(MAU_VIEN_LOGO);
            g2.setStroke(new BasicStroke(1.25f));
            g2.draw(khungLogo);

            // Lõi trắng hiển thị logo thương hiệu Pengu Garage
            int kichThuocLoiLogo = 128;
            int loiX = tamX - kichThuocLoiLogo / 2;
            int loiY = khungY + (kichThuocKhung - kichThuocLoiLogo) / 2;
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(loiX, loiY, kichThuocLoiLogo, kichThuocLoiLogo, 4, 4));

            if (anhLogoDaScale != null) {
                g2.drawImage(anhLogoDaScale, loiX, loiY, null);
            }

            // 3. Cụm tiêu đề thương hiệu
            int tieuDeY = khungY + kichThuocKhung + 48;
            Font fontTieuDe = taoFontCoDanChu("Segoe UI", Font.BOLD, 30f, 0.03f);
            g2.setFont(fontTieuDe);
            g2.setColor(MAU_CHU_TRANG);
            veChuCanGiua(g2, "PENGU GARAGE", tamX, tieuDeY);

            int sloganY = tieuDeY + 28;
            Font fontSlogan = taoFontCoDanChu("Segoe UI", Font.BOLD, 11.5f, 0.08f);
            g2.setFont(fontSlogan);
            g2.setColor(MAU_CAM_CHU_DAO);
            veChuCanGiua(g2, "SERVICE ERP  •  YOUR CAR, OUR CARE", tamX, sloganY);

            int moTaY = sloganY + 24;
            Font fontMoTa = new Font("Segoe UI", Font.PLAIN, 13);
            g2.setFont(fontMoTa);
            g2.setColor(MAU_CHU_PHU);
            veChuCanGiua(g2, "Hệ Thống Quản Lý Dịch Vụ Gara Ô Tô & Xe Máy Chuyên Nghiệp", tamX, moTaY);

            // 4. Thanh tiến trình mảnh viền sáng + dòng trạng thái
            int rongThanh = 340;
            int caoThanh = 5;
            int thanhX = tamX - rongThanh / 2;
            int thanhY = moTaY + 38;

            RoundRectangle2D ranhTienTrinh = new RoundRectangle2D.Float(
                    thanhX, thanhY, rongThanh, caoThanh, caoThanh, caoThanh
            );
            g2.setColor(MAU_NEN_THANH_TIEN_TRINH);
            g2.fill(ranhTienTrinh);

            if (phanTram > 0) {
                int rongTienDo = Math.max(caoThanh, Math.round(rongThanh * (phanTram / 100f)));
                LinearGradientPaint mauTienDo = new LinearGradientPaint(
                        thanhX, thanhY, thanhX + rongTienDo, thanhY,
                        new float[]{0f, 1f},
                        new Color[]{new Color(145, 78, 42), MAU_CAM_SANG}
                );
                g2.setPaint(mauTienDo);
                g2.fill(new RoundRectangle2D.Float(
                        thanhX, thanhY, rongTienDo, caoThanh, caoThanh, caoThanh
                ));
            }

            g2.setColor(MAU_VIEN_THANH_TIEN_TRINH);
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(ranhTienTrinh);

            int dongTrangThaiY = thanhY + 21;
            Font fontTrangThai = new Font("Segoe UI", Font.PLAIN, 11);
            g2.setFont(fontTrangThai);
            g2.setColor(MAU_CHU_PHU);
            g2.drawString(thongDiepTrangThai, thanhX, dongTrangThaiY);

            Font fontPhanTram = new Font("Segoe UI", Font.BOLD, 11);
            g2.setFont(fontPhanTram);
            g2.setColor(MAU_CAM_SANG);
            String chuoiPhanTram = phanTram + "%";
            FontMetrics fmPT = g2.getFontMetrics();
            g2.drawString(chuoiPhanTram, thanhX + rongThanh - fmPT.stringWidth(chuoiPhanTram), dongTrangThaiY);

            // 5. Chấm tròn tín hiệu xanh ngọc phía dưới
            float nhip = (float) (0.55 + 0.45 * Math.sin(phaNhipSang));
            int alphaQuang = Math.round(65 * nhip);
            float duongKinhCham = 4.2f;
            float chamY = dongTrangThaiY + 38f;

            g2.setColor(new Color(20, 184, 166, alphaQuang));
            g2.fill(new Ellipse2D.Float(tamX - 5f, chamY - 5f, 10f, 10f));
            g2.setColor(new Color(
                    MAU_CHAM_TRANG_THAI.getRed(),
                    MAU_CHAM_TRANG_THAI.getGreen(),
                    MAU_CHAM_TRANG_THAI.getBlue(),
                    Math.min(255, Math.round(160 + 95 * nhip))
            ));
            g2.fill(new Ellipse2D.Float(
                    tamX - duongKinhCham / 2f,
                    chamY - duongKinhCham / 2f,
                    duongKinhCham,
                    duongKinhCham
            ));

            g2.dispose();
        }

        private void veChuCanGiua(Graphics2D g2, String chuoi, int tamX, int y) {
            FontMetrics fm = g2.getFontMetrics();
            int x = tamX - fm.stringWidth(chuoi) / 2;
            g2.drawString(chuoi, x, y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManHinhSplash splash = new ManHinhSplash();
            splash.hienThiVaChay();
        });
    }
}
