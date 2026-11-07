package ui;

import bus.TaiKhoanBUS;
import entity.TaiKhoan;
import enums.ChucVu;
import util.IconUtil;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class ManHinhDangNhap extends JFrame {
    private static final int CHIEU_RONG_MAC_DINH = 1200;
    private static final int CHIEU_CAO_MAC_DINH = 675;

    // Bảng màu theo thiết kế Pengu Garage
    private static final Color MAU_NEN_TRAI = new Color(13, 25, 43);
    private static final Color MAU_NEN_PHAI = new Color(244, 246, 249);
    private static final Color MAU_THE_DANG_NHAP = new Color(15, 35, 60);
    private static final Color MAU_NEN_O_NHAP = new Color(10, 21, 37);
    private static final Color MAU_VIEN_O_NHAP = new Color(34, 54, 81);
    private static final Color MAU_VIEN_FOCUS_CAM = new Color(232, 119, 46);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_SANG = new Color(244, 132, 52);
    private static final Color MAU_CAM_HOVER = new Color(251, 146, 60);
    private static final Color MAU_CAM_NHAN = new Color(212, 88, 19);
    private static final Color MAU_CHU_TRANG = new Color(248, 250, 252);
    private static final Color MAU_NHAN_TRUONG = new Color(203, 213, 225);
    private static final Color MAU_CHU_MO_TA = new Color(126, 147, 174);
    private static final Color MAU_PLACEHOLDER = new Color(93, 114, 141);
    private static final Color MAU_BAO_LOI = new Color(248, 113, 113);

    private static final String PREF_GHI_NHO = "pengu_ghi_nho_dang_nhap";
    private static final String PREF_TEN_DANG_NHAP = "pengu_ten_dang_nhap_da_luu";

    private final TaiKhoanBUS taiKhoanBUS = new TaiKhoanBUS();
    private final Preferences tuyChonLuu = Preferences.userNodeForPackage(ManHinhDangNhap.class);
    private final Consumer<TaiKhoan> khiDangNhapThanhCong;

    private BufferedImage anhLogoDaScale;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private HopKiemGhiNho chkGhiNho;
    private JLabel lblThongBaoLoi;
    private boolean hienMatKhau = false;

    public ManHinhDangNhap() {
        this(null);
    }

    public ManHinhDangNhap(Consumer<TaiKhoan> khiDangNhapThanhCong) {
        this.khiDangNhapThanhCong = khiDangNhapThanhCong;
        taiLogoThuongHieu();
        khoiTaoGiaoDien();
        napThongTinGhiNho();
    }

    private void khoiTaoGiaoDien() {
        setTitle("Pengu Garage - Đăng nhập hệ thống");
        setSize(new Dimension(CHIEU_RONG_MAC_DINH, CHIEU_CAO_MAC_DINH));
        setMinimumSize(new Dimension(980, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gocPanel = new JPanel(new BoCucChiaCotTyLe(0.318, 340));
        BrandSidebarPanel cotThuongHieu = new BrandSidebarPanel();
        JPanel vungBenPhai = taoVungDangNhapBenPhai();

        gocPanel.add(cotThuongHieu, BorderLayout.WEST);
        gocPanel.add(vungBenPhai, BorderLayout.CENTER);
        setContentPane(gocPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (txtTenDangNhap.getText().trim().isEmpty()) {
                    txtTenDangNhap.requestFocusInWindow();
                } else {
                    txtMatKhau.requestFocusInWindow();
                }
            }
        });
    }

    private JPanel taoVungDangNhapBenPhai() {
        JPanel vungPhai = new JPanel(new GridBagLayout());
        vungPhai.setBackground(MAU_NEN_PHAI);

        TheDangNhapPanel theDangNhap = new TheDangNhapPanel();
        vungPhai.add(theDangNhap, new GridBagConstraints());
        return vungPhai;
    }

    private void napThongTinGhiNho() {
        boolean ghiNho = tuyChonLuu.getBoolean(PREF_GHI_NHO, false);
        chkGhiNho.setDaChon(ghiNho);
        if (ghiNho) {
            String tenDaLuu = tuyChonLuu.get(PREF_TEN_DANG_NHAP, "");
            txtTenDangNhap.setText(tenDaLuu);
        }
    }

    private void luuThongTinGhiNho(String tenDangNhap) {
        if (chkGhiNho.isDaChon()) {
            tuyChonLuu.putBoolean(PREF_GHI_NHO, true);
            tuyChonLuu.put(PREF_TEN_DANG_NHAP, tenDangNhap);
        } else {
            tuyChonLuu.putBoolean(PREF_GHI_NHO, false);
            tuyChonLuu.remove(PREF_TEN_DANG_NHAP);
        }
    }

    private void xuLyDangNhap() {
        lblThongBaoLoi.setText(" ");
        String tenDangNhap = txtTenDangNhap.getText() != null ? txtTenDangNhap.getText().trim().toUpperCase() : "";
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty()) {
            hienLoiTaiForm("Vui lòng nhập tên đăng nhập / mã nhân viên.");
            txtTenDangNhap.requestFocusInWindow();
            return;
        }
        if (matKhau.trim().isEmpty()) {
            hienLoiTaiForm("Vui lòng nhập mật khẩu của bạn.");
            txtMatKhau.requestFocusInWindow();
            return;
        }

        try {
            TaiKhoan taiKhoan = taiKhoanBUS.dangNhap(tenDangNhap, matKhau);
            if (taiKhoan == null) {
                hienLoiTaiForm("Tên đăng nhập hoặc mật khẩu không chính xác.");
                txtMatKhau.selectAll();
                txtMatKhau.requestFocusInWindow();
                return;
            }

            luuThongTinGhiNho(taiKhoan.getTenDangNhap());

            if (khiDangNhapThanhCong != null) {
                dispose();
                khiDangNhapThanhCong.accept(taiKhoan);
            } else {
                dieuHuongTheoQuyen(taiKhoan);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            hienLoiTaiForm(ex.getMessage());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể kết nối cơ sở dữ liệu MariaDB (pengugarage).\nChi tiết: " + ex.getMessage(),
                    "Lỗi kết nối hệ thống",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void dieuHuongTheoQuyen(TaiKhoan taiKhoan) {
        ChucVu quyen = taiKhoan.getQuyen();
        if (quyen == null) {
            hienLoiTaiForm("Tài khoản chưa được gán chức vụ hợp lệ.");
            return;
        }

        dispose();
        switch (quyen) {
            case QUAN_LY -> new QuanLyManHinhChinh(taiKhoan).setVisible(true);
            case LE_TAN -> new LeTanManHinhChinh(taiKhoan).setVisible(true);
            case KY_THUAT_VIEN -> new KyThuatVienManHinhChinh(taiKhoan).setVisible(true);
        }
    }

    private void xuLyQuenMatKhau() {
        JOptionPane.showMessageDialog(
                this,
                "Vui lòng liên hệ Quản lý Gara (mã NV000) để được cấp lại mật khẩu cho tài khoản nhân viên.",
                "Hỗ trợ khôi phục mật khẩu",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void hienLoiTaiForm(String thongBao) {
        lblThongBaoLoi.setText(thongBao);
    }

    private void taiLogoThuongHieu() {
        BufferedImage logoGoc = docAnhLogo();
        if (logoGoc != null) {
            setIconImage(logoGoc);
            this.anhLogoDaScale = thuNhoAnhChatLuongCao(logoGoc, 174, 174);
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
        URL url = ManHinhDangNhap.class.getResource("/img/logo_pengu-garage.png");
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

    private Font taoFontCoDanChu(String tenFont, int kieu, float coChu, float khoangCachChu) {
        Font coBan = new Font(tenFont, kieu, Math.round(coChu)).deriveFont(coChu);
        Map<TextAttribute, Object> thuocTinh = new HashMap<>();
        thuocTinh.put(TextAttribute.TRACKING, khoangCachChu);
        return coBan.deriveFont(thuocTinh);
    }

    /**
     * Bố cục giữ cột thương hiệu bên trái chiếm đúng tỷ lệ (~31.8%) khi phóng to / thu nhỏ cửa sổ.
     */
    private static class BoCucChiaCotTyLe implements LayoutManager {
        private final double tyLeCotTrai;
        private final int chieuRongToiThieuTrai;
        private Component cotTrai;
        private Component vungPhai;

        BoCucChiaCotTyLe(double tyLeCotTrai, int chieuRongToiThieuTrai) {
            this.tyLeCotTrai = tyLeCotTrai;
            this.chieuRongToiThieuTrai = chieuRongToiThieuTrai;
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
            if (BorderLayout.WEST.equals(name)) {
                cotTrai = comp;
            } else {
                vungPhai = comp;
            }
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            if (comp == cotTrai) {
                cotTrai = null;
            } else if (comp == vungPhai) {
                vungPhai = null;
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(CHIEU_RONG_MAC_DINH, CHIEU_CAO_MAC_DINH);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(980, 600);
        }

        @Override
        public void layoutContainer(Container parent) {
            int w = parent.getWidth();
            int h = parent.getHeight();
            int rongTrai = Math.max(chieuRongToiThieuTrai, (int) Math.round(w * tyLeCotTrai));
            if (cotTrai != null) {
                cotTrai.setBounds(0, 0, rongTrai, h);
            }
            if (vungPhai != null) {
                vungPhai.setBounds(rongTrai, 0, Math.max(0, w - rongTrai), h);
            }
        }
    }

    /**
     * Cột thương hiệu tối màu bên trái chứa thẻ Logo trắng, tên thương hiệu và slogan.
     */
    private class BrandSidebarPanel extends JPanel {
        BrandSidebarPanel() {
            setOpaque(true);
            setBackground(MAU_NEN_TRAI);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();
            int tamX = w / 2;

            int kichThuocTheLogo = 206;
            int tongChieuCaoCum = kichThuocTheLogo + 42 + 26 + 24;
            int theX = tamX - kichThuocTheLogo / 2;
            int theY = Math.max(40, (h - tongChieuCaoCum) / 2 - 12);
            int boGoc = 30;

            // Đổ bóng mềm bên dưới thẻ trắng chứa logo
            for (int i = 8; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 10 + (8 - i) * 4));
                g2.fill(new RoundRectangle2D.Float(
                        theX - i + 1,
                        theY + i * 1.5f,
                        kichThuocTheLogo + i * 2 - 2,
                        kichThuocTheLogo + i * 2 - 2,
                        boGoc + i,
                        boGoc + i
                ));
            }

            // Nền trắng bo góc hiển thị logo
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(theX, theY, kichThuocTheLogo, kichThuocTheLogo, boGoc, boGoc));

            if (anhLogoDaScale != null) {
                int logoX = tamX - anhLogoDaScale.getWidth() / 2;
                int logoY = theY + (kichThuocTheLogo - anhLogoDaScale.getHeight()) / 2;
                g2.drawImage(anhLogoDaScale, logoX, logoY, null);
            }

            // Tiêu đề thương hiệu
            int tieuDeY = theY + kichThuocTheLogo + 44;
            g2.setFont(taoFontCoDanChu("Segoe UI", Font.BOLD, 27f, 0.02f));
            g2.setColor(Color.WHITE);
            veChuCanGiua(g2, "PENGU GARAGE", tamX, tieuDeY);

            // Slogan cam
            int sloganY = tieuDeY + 26;
            g2.setFont(taoFontCoDanChu("Segoe UI", Font.BOLD, 11f, 0.06f));
            g2.setColor(MAU_CAM_CHU_DAO);
            veChuCanGiua(g2, "SERVICE ERP • YOUR CAR, OUR CARE", tamX, sloganY);

            // Dòng mô tả hệ thống
            int moTaY = sloganY + 23;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(MAU_CHU_MO_TA);
            veChuCanGiua(g2, "Hệ Thống Quản Lý Dịch Vụ Gara Chuyên Nghiệp", tamX, moTaY);

            g2.dispose();
        }

        private void veChuCanGiua(Graphics2D g2, String chuoi, int tamX, int y) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(chuoi, tamX - fm.stringWidth(chuoi) / 2, y);
        }
    }

    /**
     * Thẻ đăng nhập trung tâm màu xanh đen bo góc kèm hiệu ứng bóng đổ mềm trên nền sáng.
     */
    private class TheDangNhapPanel extends JPanel {
        private static final int LE_BONG = 14;
        private static final int RONG_THE = 450;
        private static final int CAO_THE = 406;

        TheDangNhapPanel() {
            setOpaque(false);
            setLayout(null);
            Dimension kichThuoc = new Dimension(RONG_THE + LE_BONG * 2, CAO_THE + LE_BONG * 2);
            setPreferredSize(kichThuoc);
            setMinimumSize(kichThuoc);
            khoiTaoThanhPhanBenTrong();
        }

        private void khoiTaoThanhPhanBenTrong() {
            int gocX = LE_BONG + 36;
            int gocY = LE_BONG;
            int rongNoiDung = RONG_THE - 72;

            JLabel lblTieuDe = new JLabel("Đăng Nhập Hệ Thống");
            lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblTieuDe.setForeground(Color.WHITE);
            lblTieuDe.setBounds(gocX, gocY + 32, rongNoiDung, 30);
            add(lblTieuDe);

            JLabel lblPhuDe = new JLabel("Chào mừng trở lại! Vui lòng nhập thông tin để truy cập gara.");
            lblPhuDe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblPhuDe.setForeground(MAU_CHU_MO_TA);
            lblPhuDe.setBounds(gocX, gocY + 64, rongNoiDung, 20);
            add(lblPhuDe);

            JLabel lblTenDangNhap = new JLabel("TÊN ĐĂNG NHẬP / MÃ NHÂN VIÊN");
            lblTenDangNhap.setFont(taoFontCoDanChu("Segoe UI", Font.BOLD, 11f, 0.03f));
            lblTenDangNhap.setForeground(MAU_NHAN_TRUONG);
            lblTenDangNhap.setBounds(gocX, gocY + 104, rongNoiDung, 18);
            add(lblTenDangNhap);

            txtTenDangNhap = taoOChuaVanBan("Ví dụ: NV000, NV001, NV002...");
            KhungONhap khungTenDangNhap = new KhungONhap(txtTenDangNhap, null);
            khungTenDangNhap.setBounds(gocX, gocY + 126, rongNoiDung, 44);
            add(khungTenDangNhap);

            JLabel lblMatKhau = new JLabel("MẬT KHẨU");
            lblMatKhau.setFont(taoFontCoDanChu("Segoe UI", Font.BOLD, 11f, 0.03f));
            lblMatKhau.setForeground(MAU_NHAN_TRUONG);
            lblMatKhau.setBounds(gocX, gocY + 188, rongNoiDung, 18);
            add(lblMatKhau);

            txtMatKhau = taoOMatKhau("Nhập mật khẩu của bạn");
            NutConMat nutConMat = new NutConMat();
            KhungONhap khungMatKhau = new KhungONhap(txtMatKhau, nutConMat);
            khungMatKhau.setBounds(gocX, gocY + 210, rongNoiDung, 44);
            add(khungMatKhau);

            chkGhiNho = new HopKiemGhiNho("Ghi nhớ đăng nhập");
            chkGhiNho.setBounds(gocX, gocY + 269, 175, 22);
            add(chkGhiNho);

            JLabel lblQuenMatKhau = new JLabel("Quên mật khẩu?", SwingConstants.RIGHT);
            lblQuenMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblQuenMatKhau.setForeground(MAU_VIEN_FOCUS_CAM);
            lblQuenMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblQuenMatKhau.setBounds(gocX + rongNoiDung - 130, gocY + 269, 130, 22);
            lblQuenMatKhau.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    lblQuenMatKhau.setForeground(MAU_CAM_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lblQuenMatKhau.setForeground(MAU_VIEN_FOCUS_CAM);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    xuLyQuenMatKhau();
                }
            });
            add(lblQuenMatKhau);

            NutDangNhapCam btnDangNhap = new NutDangNhapCam("ĐĂNG NHẬP VÀO HỆ THỐNG");
            btnDangNhap.setBounds(gocX, gocY + 308, rongNoiDung, 46);
            btnDangNhap.addActionListener(e -> xuLyDangNhap());
            add(btnDangNhap);

            lblThongBaoLoi = new JLabel(" ", SwingConstants.CENTER);
            lblThongBaoLoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblThongBaoLoi.setForeground(MAU_BAO_LOI);
            lblThongBaoLoi.setBounds(gocX, gocY + 362, rongNoiDung, 22);
            add(lblThongBaoLoi);

            txtTenDangNhap.addActionListener(e -> txtMatKhau.requestFocusInWindow());
            txtMatKhau.addActionListener(e -> xuLyDangNhap());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int boGoc = 20;
            // Đổ bóng mềm quanh thẻ đăng nhập
            for (int i = LE_BONG; i >= 1; i--) {
                int alpha = Math.max(2, (LE_BONG - i + 1) * 3);
                g2.setColor(new Color(15, 23, 42, alpha));
                g2.fill(new RoundRectangle2D.Float(
                        LE_BONG - i,
                        LE_BONG - i + 4,
                        RONG_THE + i * 2,
                        CAO_THE + i * 2,
                        boGoc + i,
                        boGoc + i
                ));
            }

            g2.setColor(MAU_THE_DANG_NHAP);
            g2.fill(new RoundRectangle2D.Float(LE_BONG, LE_BONG, RONG_THE, CAO_THE, boGoc, boGoc));
            g2.dispose();
        }
    }

    private JTextField taoOChuaVanBan(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g2.setColor(MAU_PLACEHOLDER);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(0, 14, 0, 12));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(MAU_CHU_TRANG);
        tf.setCaretColor(MAU_CAM_HOVER);
        return tf;
    }

    private JPasswordField taoOMatKhau(String placeholder) {
        JPasswordField pf = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g2.setColor(MAU_PLACEHOLDER);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, getInsets().left, y);
                    g2.dispose();
                }
            }
        };
        pf.setOpaque(false);
        pf.setBorder(new EmptyBorder(0, 14, 0, 6));
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setForeground(MAU_CHU_TRANG);
        pf.setCaretColor(MAU_CAM_HOVER);
        pf.setEchoChar('•');
        return pf;
    }

    /**
     * Khung bo góc chứa JTextField / JPasswordField, tự động sáng viền cam khi nhận focus.
     */
    private static class KhungONhap extends JPanel {
        private boolean dangFocus = false;

        KhungONhap(JComponent truongNhap, JComponent phuKienPhai) {
            super(new BorderLayout());
            setOpaque(false);
            add(truongNhap, BorderLayout.CENTER);
            if (phuKienPhai != null) {
                add(phuKienPhai, BorderLayout.EAST);
            }
            truongNhap.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    dangFocus = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    dangFocus = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float w = getWidth() - 1.5f;
            float h = getHeight() - 1.5f;
            RoundRectangle2D hinh = new RoundRectangle2D.Float(0.75f, 0.75f, w, h, 9f, 9f);

            g2.setColor(MAU_NEN_O_NHAP);
            g2.fill(hinh);

            if (dangFocus) {
                g2.setColor(MAU_VIEN_FOCUS_CAM);
                g2.setStroke(new BasicStroke(1.4f));
            } else {
                g2.setColor(MAU_VIEN_O_NHAP);
                g2.setStroke(new BasicStroke(1.1f));
            }
            g2.draw(hinh);
            g2.dispose();
        }
    }

    /**
     * Nút biểu tượng con mắt bật/tắt hiển thị mật khẩu bên phải ô mật khẩu.
     */
    private class NutConMat extends JComponent {
        private boolean dangTroChuot = false;

        NutConMat() {
            setPreferredSize(new Dimension(38, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Hiện / ẩn mật khẩu");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    dangTroChuot = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dangTroChuot = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    hienMatKhau = !hienMatKhau;
                    txtMatKhau.setEchoChar(hienMatKhau ? (char) 0 : '•');
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color mau = dangTroChuot ? MAU_NHAN_TRUONG : MAU_CHU_MO_TA;
            String tenAnh = hienMatKhau ? "view_off.png" : "view.png";
            ImageIcon icon = IconUtil.layIcon(tenAnh, 18, 18, mau);
            if (icon != null) {
                int x = (getWidth() - icon.getIconWidth()) / 2;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g, x, y);
            }
        }
    }

    /**
     * Hộp kiểm "Ghi nhớ đăng nhập" tối màu đồng bộ giao diện thẻ đăng nhập.
     */
    private static class HopKiemGhiNho extends JComponent {
        private final String nhan;
        private boolean daChon = false;
        private boolean dangTroChuot = false;

        HopKiemGhiNho(String nhan) {
            this.nhan = nhan;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    dangTroChuot = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dangTroChuot = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    daChon = !daChon;
                    repaint();
                }
            });
        }

        boolean isDaChon() {
            return daChon;
        }

        void setDaChon(boolean daChon) {
            this.daChon = daChon;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int oVuong = 15;
            int yVuong = (getHeight() - oVuong) / 2;
            RoundRectangle2D khung = new RoundRectangle2D.Float(0.5f, yVuong, oVuong, oVuong, 4f, 4f);

            if (daChon) {
                g2.setColor(MAU_CAM_CHU_DAO);
                g2.fill(khung);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D dauTich = new Path2D.Float();
                dauTich.moveTo(3.8f, yVuong + 7.8f);
                dauTich.lineTo(6.6f, yVuong + 10.8f);
                dauTich.lineTo(11.8f, yVuong + 4.8f);
                g2.draw(dauTich);
            } else {
                g2.setColor(MAU_NEN_O_NHAP);
                g2.fill(khung);
                g2.setColor(dangTroChuot ? MAU_CHU_MO_TA : MAU_VIEN_O_NHAP);
                g2.setStroke(new BasicStroke(1.1f));
                g2.draw(khung);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(dangTroChuot ? MAU_NHAN_TRUONG : MAU_CHU_MO_TA);
            FontMetrics fm = g2.getFontMetrics();
            int yChu = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(nhan, oVuong + 8, yChu);

            g2.dispose();
        }
    }

    /**
     * Nút bấm chính màu cam chuyển sắc kèm hiệu ứng hover / nhấn.
     */
    private class NutDangNhapCam extends JButton {
        private boolean dangTroChuot = false;
        private boolean dangNhan = false;

        NutDangNhapCam(String chuoi) {
            super(chuoi);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(taoFontCoDanChu("Segoe UI", Font.BOLD, 13f, 0.03f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    dangTroChuot = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dangTroChuot = false;
                    dangNhan = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        dangNhan = true;
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    dangNhan = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D hinhNut = new RoundRectangle2D.Float(0, 0, w, h - 2, 9f, 9f);

            // Bóng cam ấm nhẹ phía dưới nút
            g2.setColor(new Color(234, 106, 31, dangTroChuot ? 75 : 45));
            g2.fill(new RoundRectangle2D.Float(2, 3, w - 4, h - 3, 10f, 10f));

            Color mauDau;
            Color mauCuoi;
            if (dangNhan) {
                mauDau = MAU_CAM_NHAN;
                mauCuoi = MAU_CAM_NHAN;
            } else if (dangTroChuot) {
                mauDau = MAU_CAM_HOVER;
                mauCuoi = MAU_CAM_SANG;
            } else {
                mauDau = MAU_CAM_SANG;
                mauCuoi = MAU_CAM_CHU_DAO;
            }

            g2.setPaint(new LinearGradientPaint(
                    0, 0, w, 0,
                    new float[]{0f, 1f},
                    new Color[]{mauDau, mauCuoi}
            ));
            g2.fill(hinhNut);

            g2.setFont(getFont());
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int xChu = (w - fm.stringWidth(getText())) / 2;
            int yChu = (h - 2 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), xChu, yChu);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManHinhDangNhap manHinhDangNhap = new ManHinhDangNhap();
            manHinhDangNhap.setVisible(true);
        });
    }
}
