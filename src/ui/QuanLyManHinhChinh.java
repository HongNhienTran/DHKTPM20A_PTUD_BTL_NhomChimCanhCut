package ui;

import entity.NhanVien;
import entity.TaiKhoan;
import util.IconUtil;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuanLyManHinhChinh extends JFrame {
    // Mã định danh các màn hình con trong CardLayout
    public static final String MAN_DASHBOARD = "DASHBOARD";
    public static final String MAN_SAN_PHAM = "SAN_PHAM_PHU_TUNG";
    public static final String MAN_DICH_VU = "DICH_VU_KY_THUAT";
    public static final String MAN_KHUYEN_MAI = "MA_KHUYEN_MAI";
    public static final String MAN_PHUONG_TIEN = "QUAN_LY_PHUONG_TIEN";
    public static final String MAN_HOA_DON = "QUAN_LY_HOA_DON";
    public static final String MAN_KHACH_HANG = "QUAN_LY_KHACH_HANG";
    public static final String MAN_NHAN_VIEN = "QUAN_LY_NHAN_VIEN";
    public static final String MAN_BAO_CAO = "BAO_CAO_THONG_KE";

    // Bảng màu theo thiết kế mẫu
    private static final Color MAU_NEN_HEADER = Color.WHITE;
    private static final Color MAU_VIEN_DUOI_HEADER = new Color(226, 232, 240);
    private static final Color MAU_NEN_NOI_DUNG = new Color(252, 253, 255);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_MENU = new Color(30, 41, 59);
    private static final Color MAU_CHU_NHOM_MENU = new Color(148, 163, 184);
    private static final Color MAU_NEN_MENU_ACTIVE = new Color(237, 244, 255);
    private static final Color MAU_VIEN_MENU_ACTIVE = new Color(212, 228, 255);
    private static final Color MAU_NEN_MENU_HOVER = new Color(244, 247, 252);
    private static final Color MAU_CAM_CHUC_VU = new Color(234, 106, 31);
    private static final Color MAU_NEN_DANG_XUAT = new Color(255, 241, 242);
    private static final Color MAU_NEN_DANG_XUAT_HOVER = new Color(255, 228, 230);
    private static final Color MAU_VIEN_DANG_XUAT = new Color(254, 205, 211);
    private static final Color MAU_CHU_DANG_XUAT = new Color(225, 29, 72);
    private static final Color MAU_NEN_HDSD = new Color(241, 245, 249);
    private static final Color MAU_NEN_HDSD_HOVER = new Color(226, 232, 240);

    private final TaiKhoan taiKhoanDangNhap;
    private BufferedImage logoHeader;

    private final CardLayout boCucManHinhCon = new CardLayout();
    private final JPanel vungNoiDungChinh = new JPanel(boCucManHinhCon);

    private NutDieuHuongHeader btnDashboard;
    private NutDieuHuongHeader btnHangMucCungCap;
    private NutDieuHuongHeader btnHangMucVanHanh;
    private NutDieuHuongHeader btnQuanTriCuaHang;

    private final Map<String, NutDieuHuongHeader> mapManHinhThuocNhom = new LinkedHashMap<>();
    private final List<MucMenuThaXuong> danhSachMucMenu = new ArrayList<>();
    private String maManHinhHienTai = MAN_DASHBOARD;

    private QuanLyManHinhQuanLySanPham manHinhSanPham;
    private QuanLyManHinhQuanLyDichVu manHinhDichVu;
    private QuanLyManHinhQuanLyMaKhuyenMai manHinhKhuyenMai;
    private QuanLyManHinhQuanLyPhuongTien manHinhPhuongTien;
    private QuanLyManHinhQuanLyHoaDon manHinhHoaDon;
    private QuanLyManHinhQuanLyKhachHang manHinhKhachHang;
    private QuanLyManHinhQuanLyNhanVien manHinhNhanVien;

    public QuanLyManHinhChinh() {
        this(null);
    }

    public QuanLyManHinhChinh(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        taiLogoThuongHieu();
        khoiTaoGiaoDien();
        chuyenManHinh(MAN_DASHBOARD);
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    private void khoiTaoGiaoDien() {
        setTitle("Pengu Garage - Quản lý hệ thống");
        setSize(new Dimension(1366, 768));
        setMinimumSize(new Dimension(1120, 650));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gocPanel = new JPanel(new BorderLayout());
        gocPanel.setBackground(MAU_NEN_NOI_DUNG);

        JPanel thanhHeader = taoThanhHeader();
        khoiTaoCacManHinhConMacDinh();

        gocPanel.add(thanhHeader, BorderLayout.NORTH);
        gocPanel.add(vungNoiDungChinh, BorderLayout.CENTER);
        setContentPane(gocPanel);
    }

    private JPanel taoThanhHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MAU_NEN_HEADER);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN_DUOI_HEADER),
                new EmptyBorder(0, 20, 0, 18)
        ));

        // 1. Cụm bên trái: Logo ứng dụng + Tên thương hiệu + Thanh điều hướng danh mục
        JPanel cumTraiVaMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 11));
        cumTraiVaMenu.setOpaque(false);

        JPanel cumThuongHieu = taoCumThuongHieuTrai();
        cumTraiVaMenu.add(cumThuongHieu);
        cumTraiVaMenu.add(Box.createHorizontalStrut(28));

        // Nút Dashboard trực tiếp
        btnDashboard = new NutDieuHuongHeader("Dashboard", LoaiBieuTuong.DASHBOARD, false);
        btnDashboard.addActionListener(e -> chuyenManHinh(MAN_DASHBOARD));
        cumTraiVaMenu.add(btnDashboard);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 1: Hạng mục cung cấp
        btnHangMucCungCap = new NutDieuHuongHeader("Hạng mục cung cấp", LoaiBieuTuong.CUNG_CAP, true);
        JPopupMenu popupCungCap = taoPopupDanhMuc(
                "HẠNG MỤC CUNG CẤP",
                new ThongTinMucCon(MAN_SAN_PHAM, "Sản phẩm & Phụ tùng", LoaiBieuTuong.SAN_PHAM),
                new ThongTinMucCon(MAN_DICH_VU, "Dịch vụ kỹ thuật", LoaiBieuTuong.DICH_VU),
                new ThongTinMucCon(MAN_KHUYEN_MAI, "Mã khuyến mãi", LoaiBieuTuong.KHUYEN_MAI)
        );
        ganSuKienMoMenu(btnHangMucCungCap, popupCungCap);
        cumTraiVaMenu.add(btnHangMucCungCap);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 2: Hạng mục vận hành
        btnHangMucVanHanh = new NutDieuHuongHeader("Hạng mục vận hành", LoaiBieuTuong.PHUONG_TIEN, true);
        JPopupMenu popupVanHanh = taoPopupDanhMuc(
                "VẬN HÀNH GARA",
                new ThongTinMucCon(MAN_PHUONG_TIEN, "Quản lý phương tiện", LoaiBieuTuong.PHUONG_TIEN),
                new ThongTinMucCon(MAN_HOA_DON, "Quản lý hóa đơn", LoaiBieuTuong.HOA_DON),
                new ThongTinMucCon(MAN_KHACH_HANG, "Quản lý khách hàng", LoaiBieuTuong.KHACH_HANG)
        );
        ganSuKienMoMenu(btnHangMucVanHanh, popupVanHanh);
        cumTraiVaMenu.add(btnHangMucVanHanh);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 3: Quản trị cửa hàng
        btnQuanTriCuaHang = new NutDieuHuongHeader("Quản trị cửa hàng", LoaiBieuTuong.QUAN_TRI, true);
        JPopupMenu popupQuanTri = taoPopupDanhMuc(
                "QUẢN TRỊ CỬA HÀNG",
                new ThongTinMucCon(MAN_NHAN_VIEN, "Quản lý nhân viên", LoaiBieuTuong.NHAN_VIEN),
                new ThongTinMucCon(MAN_BAO_CAO, "Báo cáo & Thống kê", LoaiBieuTuong.BAO_CAO)
        );
        ganSuKienMoMenu(btnQuanTriCuaHang, popupQuanTri);
        cumTraiVaMenu.add(btnQuanTriCuaHang);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Nút dấu ! (HDSD) đặt kế bên mục Quản trị cửa hàng
        NutHuongDanSuDung btnHDSD = new NutHuongDanSuDung();
        btnHDSD.addActionListener(e -> moHopThoaiHuongDanSuDung());
        cumTraiVaMenu.add(btnHDSD);

        // Ánh xạ màn hình con về nút nhóm cha trên Header để tô sáng đúng danh mục đang mở
        mapManHinhThuocNhom.put(MAN_DASHBOARD, btnDashboard);
        mapManHinhThuocNhom.put(MAN_SAN_PHAM, btnHangMucCungCap);
        mapManHinhThuocNhom.put(MAN_DICH_VU, btnHangMucCungCap);
        mapManHinhThuocNhom.put(MAN_KHUYEN_MAI, btnHangMucCungCap);
        mapManHinhThuocNhom.put(MAN_PHUONG_TIEN, btnHangMucVanHanh);
        mapManHinhThuocNhom.put(MAN_HOA_DON, btnHangMucVanHanh);
        mapManHinhThuocNhom.put(MAN_KHACH_HANG, btnHangMucVanHanh);
        mapManHinhThuocNhom.put(MAN_NHAN_VIEN, btnQuanTriCuaHang);
        mapManHinhThuocNhom.put(MAN_BAO_CAO, btnQuanTriCuaHang);

        // 2. Cụm bên phải: Thông tin người dùng + Nút Đăng xuất
        JPanel cumPhai = taoCumNguoiDungVaCongCuPhai();

        header.add(cumTraiVaMenu, BorderLayout.WEST);
        header.add(cumPhai, BorderLayout.EAST);
        return header;
    }

    private JPanel taoCumThuongHieuTrai() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel khungLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f));
                if (logoHeader != null) {
                    int x = (getWidth() - logoHeader.getWidth()) / 2;
                    int y = (getHeight() - logoHeader.getHeight()) / 2;
                    g2.drawImage(logoHeader, x, y, null);
                }
                g2.dispose();
            }
        };
        khungLogo.setOpaque(false);
        khungLogo.setPreferredSize(new Dimension(40, 40));

        JLabel lblTenApp = new JLabel("PENGU GARAGE");
        lblTenApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTenApp.setForeground(MAU_XANH_DAM);

        panel.add(khungLogo);
        panel.add(lblTenApp);

        MouseAdapter veDashboard = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chuyenManHinh(MAN_DASHBOARD);
            }
        };
        panel.addMouseListener(veDashboard);
        return panel;
    }

    private JPanel taoCumNguoiDungVaCongCuPhai() {
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        cumPhai.setOpaque(false);

        NhanVien nv = taiKhoanDangNhap != null ? taiKhoanDangNhap.getMaNhanVien() : null;
        String hoTen = (nv != null && nv.getHoTen() != null) ? nv.getHoTen().trim() : "";
        String chucVu = (nv != null && nv.getChucVu() != null) ? nv.getChucVu().getTenHienThi() : "";
        if (hoTen.isEmpty() && taiKhoanDangNhap != null && taiKhoanDangNhap.getTenDangNhap() != null) {
            hoTen = taiKhoanDangNhap.getTenDangNhap();
        }

        // Khối Họ tên & Vai trò của tài khoản đăng nhập
        JPanel khoiChu = new JPanel();
        khoiChu.setOpaque(false);
        khoiChu.setLayout(new BoxLayout(khoiChu, BoxLayout.Y_AXIS));

        if (!hoTen.isEmpty()) {
            JLabel lblHoTen = new JLabel(hoTen);
            lblHoTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblHoTen.setForeground(MAU_CHU_MENU);
            khoiChu.add(lblHoTen);
        }

        if (!chucVu.isEmpty()) {
            JLabel lblVaiTro = new JLabel(chucVu);
            lblVaiTro.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblVaiTro.setForeground(MAU_CAM_CHUC_VU);
            khoiChu.add(lblVaiTro);
        }

        // Nút Đăng xuất
        NutDangXuat btnDangXuat = new NutDangXuat("Đăng xuất");
        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        if (khoiChu.getComponentCount() > 0) {
            cumPhai.add(khoiChu);
        }
        cumPhai.add(btnDangXuat);
        return cumPhai;
    }

    private JPopupMenu taoPopupDanhMuc(String tieuDeNhom, ThongTinMucCon... cacMucCon) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);
        popup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 238), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel tieuDePanel = new JPanel(new BorderLayout());
        tieuDePanel.setOpaque(false);
        tieuDePanel.setBorder(new EmptyBorder(4, 12, 6, 12));
        JLabel lblNhom = new JLabel(tieuDeNhom);
        lblNhom.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNhom.setForeground(MAU_CHU_NHOM_MENU);
        tieuDePanel.add(lblNhom, BorderLayout.WEST);
        popup.add(tieuDePanel);

        for (ThongTinMucCon muc : cacMucCon) {
            MucMenuThaXuong item = new MucMenuThaXuong(muc.maManHinh, muc.tenHienThi, muc.loaiBieuTuong);
            item.addActionListener(e -> {
                popup.setVisible(false);
                chuyenManHinh(muc.maManHinh);
            });
            danhSachMucMenu.add(item);
            popup.add(item);
        }
        return popup;
    }

    private void ganSuKienMoMenu(NutDieuHuongHeader nut, JPopupMenu popup) {
        nut.addActionListener(e -> popup.show(nut, 0, nut.getHeight() + 6));
    }

    private void khoiTaoCacManHinhConMacDinh() {
        vungNoiDungChinh.setBackground(MAU_NEN_NOI_DUNG);
        manHinhSanPham = new QuanLyManHinhQuanLySanPham();
        manHinhDichVu = new QuanLyManHinhQuanLyDichVu();
        manHinhKhuyenMai = new QuanLyManHinhQuanLyMaKhuyenMai();
        manHinhPhuongTien = new QuanLyManHinhQuanLyPhuongTien();
        manHinhHoaDon = new QuanLyManHinhQuanLyHoaDon();
        manHinhKhachHang = new QuanLyManHinhQuanLyKhachHang();
        manHinhNhanVien = new QuanLyManHinhQuanLyNhanVien();

        vungNoiDungChinh.add(taoManHinhTrongDashboard(), MAN_DASHBOARD);
        vungNoiDungChinh.add(manHinhSanPham, MAN_SAN_PHAM);
        vungNoiDungChinh.add(manHinhDichVu, MAN_DICH_VU);
        vungNoiDungChinh.add(manHinhKhuyenMai, MAN_KHUYEN_MAI);
        vungNoiDungChinh.add(manHinhPhuongTien, MAN_PHUONG_TIEN);
        vungNoiDungChinh.add(manHinhHoaDon, MAN_HOA_DON);
        vungNoiDungChinh.add(manHinhKhachHang, MAN_KHACH_HANG);
        vungNoiDungChinh.add(manHinhNhanVien, MAN_NHAN_VIEN);
        vungNoiDungChinh.add(taoKhungChucNang("Quản trị cửa hàng", "Báo cáo & Thống kê"), MAN_BAO_CAO);
    }

    private JPanel taoManHinhTrongDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAU_NEN_NOI_DUNG);
        return panel;
    }

    private JPanel taoKhungChucNang(String tenNhom, String tenChucNang) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MAU_NEN_NOI_DUNG);

        JPanel the = new JPanel();
        the.setOpaque(false);
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));

        JLabel lblNhom = new JLabel(tenNhom.toUpperCase(), SwingConstants.CENTER);
        lblNhom.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNhom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNhom.setForeground(MAU_CAM_CHUC_VU);

        JLabel lblTieuDe = new JLabel(tenChucNang, SwingConstants.CENTER);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        lblTieuDe.setBorder(new EmptyBorder(8, 0, 6, 0));

        JLabel lblPhu = new JLabel("Phân hệ đang sẵn sàng để tích hợp giao diện chi tiết.", SwingConstants.CENTER);
        lblPhu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPhu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPhu.setForeground(new Color(100, 116, 139));

        the.add(lblNhom);
        the.add(lblTieuDe);
        the.add(lblPhu);
        panel.add(the);
        return panel;
    }

    /**
     * Cho phép thay thế bất kỳ màn hình chức năng con nào bằng JPanel nghiệp vụ chi tiết.
     */
    public void datManHinhCon(String maManHinh, JPanel panelMoi) {
        vungNoiDungChinh.add(panelMoi, maManHinh);
        if (maManHinh.equals(maManHinhHienTai)) {
            boCucManHinhCon.show(vungNoiDungChinh, maManHinh);
        }
    }

    public void chuyenManHinh(String maManHinh) {
        this.maManHinhHienTai = maManHinh;
        if (MAN_SAN_PHAM.equals(maManHinh) && manHinhSanPham != null) {
            manHinhSanPham.lamMoiDuLieu();
        } else if (MAN_DICH_VU.equals(maManHinh) && manHinhDichVu != null) {
            manHinhDichVu.lamMoiDuLieu();
        } else if (MAN_KHUYEN_MAI.equals(maManHinh) && manHinhKhuyenMai != null) {
            manHinhKhuyenMai.lamMoiDuLieu();
        } else if (MAN_PHUONG_TIEN.equals(maManHinh) && manHinhPhuongTien != null) {
            manHinhPhuongTien.lamMoiDuLieu();
        } else if (MAN_HOA_DON.equals(maManHinh) && manHinhHoaDon != null) {
            manHinhHoaDon.lamMoiDuLieu();
        } else if (MAN_KHACH_HANG.equals(maManHinh) && manHinhKhachHang != null) {
            manHinhKhachHang.lamMoiDuLieu();
        } else if (MAN_NHAN_VIEN.equals(maManHinh) && manHinhNhanVien != null) {
            manHinhNhanVien.lamMoiDuLieu();
        }
        boCucManHinhCon.show(vungNoiDungChinh, maManHinh);

        NutDieuHuongHeader nutChaActive = mapManHinhThuocNhom.get(maManHinh);
        btnDashboard.setDangChon(nutChaActive == btnDashboard);
        btnHangMucCungCap.setDangChon(nutChaActive == btnHangMucCungCap);
        btnHangMucVanHanh.setDangChon(nutChaActive == btnHangMucVanHanh);
        btnQuanTriCuaHang.setDangChon(nutChaActive == btnQuanTriCuaHang);

        for (MucMenuThaXuong item : danhSachMucMenu) {
            item.setDangChon(maManHinh.equals(item.getMaManHinh()));
        }
    }

    private void xuLyDangXuat() {
        dispose();
        new ManHinhDangNhap().setVisible(true);
    }

    private void moHopThoaiHuongDanSuDung() {
        JDialog dialog = new JDialog(this, "Hướng dẫn sử dụng (HDSD) - Phân hệ Quản lý", true);
        dialog.setSize(620, 470);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel dauDialog = new JPanel(new BorderLayout());
        dauDialog.setBackground(MAU_XANH_DAM);
        dauDialog.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblTieuDe = new JLabel("HƯỚNG DẪN SỬ DỤNG PHÂN HỆ QUẢN LÝ");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(Color.WHITE);
        dauDialog.add(lblTieuDe, BorderLayout.WEST);

        String noiDungHtml = """
                <html>
                <body style='font-family: Segoe UI; font-size: 11px; color: #1E293B; padding: 14px 18px; line-height: 1.55;'>
                    <p style='margin-top:0;'>Chào mừng bạn đến với <b>Pengu Garage - Service ERP</b>. Thanh điều hướng phía trên được chia thành các nhóm nghiệp vụ:</p>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>1. Dashboard</h4>
                    <p style='margin-top:0;'>Theo dõi nhanh tổng quan hoạt động trong ngày, doanh thu, số lượng xe đang sửa chữa và cảnh báo tồn kho.</p>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>2. Hạng mục cung cấp</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Sản phẩm &amp; Phụ tùng:</b> Quản lý danh mục phụ tùng, đơn giá bán, đơn vị tính và số lượng tồn kho.</li>
                        <li><b>Dịch vụ kỹ thuật:</b> Thiết lập bảng giá tiền công và các gói bảo dưỡng/sửa chữa tại gara.</li>
                        <li><b>Mã khuyến mãi:</b> Tạo chương trình giảm giá theo phần trăm hoặc số tiền cố định kèm hạn dùng.</li>
                    </ul>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>3. Hạng mục vận hành (Vận hành gara)</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Quản lý phương tiện:</b> Tra cứu hồ sơ xe, biển số, hãng xe và lịch sử tiếp nhận bảo dưỡng.</li>
                        <li><b>Quản lý hóa đơn:</b> Kiểm tra chứng từ thanh toán dịch vụ sửa chữa và hóa đơn bán lẻ phụ tùng.</li>
                        <li><b>Quản lý khách hàng:</b> Quản lý thông tin khách hàng đăng ký và khách vãng lai.</li>
                    </ul>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>4. Quản trị cửa hàng</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Quản lý nhân viên:</b> Thêm mới nhân viên (tự động cấp tài khoản đăng nhập), phân quyền và đặt lại mật khẩu.</li>
                        <li><b>Báo cáo &amp; Thống kê:</b> Thống kê doanh thu, hiệu suất kỹ thuật viên và biến động phụ tùng theo kỳ.</li>
                    </ul>
                </body>
                </html>
                """;

        JLabel lblNoiDung = new JLabel(noiDungHtml);
        lblNoiDung.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane cuon = new JScrollPane(lblNoiDung);
        cuon.setBorder(null);
        cuon.getViewport().setBackground(Color.WHITE);

        JPanel chanDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        chanDialog.setBackground(new Color(248, 250, 252));
        chanDialog.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN_DUOI_HEADER));

        JButton btnDong = new JButton("Đã hiểu");
        btnDong.setFocusPainted(false);
        btnDong.setBackground(MAU_XANH_DAM);
        btnDong.setForeground(Color.WHITE);
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.setPreferredSize(new Dimension(96, 34));
        btnDong.addActionListener(e -> dialog.dispose());
        chanDialog.add(btnDong);

        dialog.add(dauDialog, BorderLayout.NORTH);
        dialog.add(cuon, BorderLayout.CENTER);
        dialog.add(chanDialog, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void taiLogoThuongHieu() {
        BufferedImage goc = docAnhLogo();
        if (goc != null) {
            setIconImage(goc);
            this.logoHeader = thuNhoAnhChatLuongCao(goc, 34, 34);
        }
    }

    private BufferedImage docAnhLogo() {
        File[] cacDuongDan = new File[]{
                new File("img/logo_pengu-garage.png"),
                new File(System.getProperty("user.dir"), "img/logo_pengu-garage.png")
        };
        for (File f : cacDuongDan) {
            if (f.exists() && f.isFile()) {
                try {
                    return ImageIO.read(f);
                } catch (IOException ignored) {
                }
            }
        }
        URL url = QuanLyManHinhChinh.class.getResource("/img/logo_pengu-garage.png");
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
            g2.drawImage(hienTai, 0, 0, w, h, null);
            g2.dispose();
            hienTai = buoc;
        }

        BufferedImage ketQua = new BufferedImage(rongDich, caoDich, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = ketQua.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(hienTai, 0, 0, rongDich, caoDich, null);
        g2.dispose();
        return ketQua;
    }

    private record ThongTinMucCon(String maManHinh, String tenHienThi, LoaiBieuTuong loaiBieuTuong) {
    }

    private enum LoaiBieuTuong {
        DASHBOARD,
        CUNG_CAP,
        SAN_PHAM,
        DICH_VU,
        KHUYEN_MAI,
        PHUONG_TIEN,
        HOA_DON,
        KHACH_HANG,
        QUAN_TRI,
        NHAN_VIEN,
        BAO_CAO
    }

    /**
     * Nút điều hướng trên thanh Header hỗ trợ trạng thái đang chọn (viền xanh nhạt như ảnh mẫu) và mũi tên mở rộng.
     */
    private static class NutDieuHuongHeader extends JButton {
        private final boolean coMuiTen;
        private boolean dangChon = false;
        private boolean dangTroChuot = false;

        NutDieuHuongHeader(String vanBan, LoaiBieuTuong loaiBieuTuong, boolean coMuiTen) {
            super(vanBan, new BieuTuongVector(loaiBieuTuong, 16, MAU_XANH_DAM));
            this.coMuiTen = coMuiTen;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(MAU_CHU_MENU);
            setIconTextGap(8);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 14, 8, coMuiTen ? 26 : 14));

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
            });
        }

        void setDangChon(boolean dangChon) {
            this.dangChon = dangChon;
            setForeground(dangChon ? MAU_XANH_DAM : MAU_CHU_MENU);
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 40);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D nen = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 10f, 10f);

            if (dangChon) {
                g2.setColor(MAU_NEN_MENU_ACTIVE);
                g2.fill(nen);
                g2.setColor(MAU_VIEN_MENU_ACTIVE);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(nen);
            } else if (dangTroChuot) {
                g2.setColor(MAU_NEN_MENU_HOVER);
                g2.fill(nen);
            }

            super.paintComponent(g);

            if (coMuiTen) {
                float mx = w - 15f;
                float my = h / 2f - 1f;
                g2.setColor(dangChon ? MAU_XANH_DAM : new Color(100, 116, 139));
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D muiTen = new Path2D.Float();
                muiTen.moveTo(mx - 3.5f, my - 1.5f);
                muiTen.lineTo(mx, my + 2.2f);
                muiTen.lineTo(mx + 3.5f, my - 1.5f);
                g2.draw(muiTen);
            }

            g2.dispose();
        }
    }

    /**
     * Mục chức năng bên trong menu thả xuống (tô nền xanh đậm chữ trắng khi đang chọn hoặc xanh nhạt khi di chuột).
     */
    private static class MucMenuThaXuong extends JButton {
        private final String maManHinh;
        private final LoaiBieuTuong loaiBieuTuong;
        private boolean dangChon = false;
        private boolean dangTroChuot = false;

        MucMenuThaXuong(String maManHinh, String tenHienThi, LoaiBieuTuong loaiBieuTuong) {
            super(tenHienThi);
            this.maManHinh = maManHinh;
            this.loaiBieuTuong = loaiBieuTuong;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(MAU_CHU_MENU);
            setIconTextGap(12);
            setBorder(new EmptyBorder(9, 14, 9, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            capNhatIcon();

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
            });
        }

        String getMaManHinh() {
            return maManHinh;
        }

        void setDangChon(boolean dangChon) {
            this.dangChon = dangChon;
            setFont(new Font("Segoe UI", dangChon ? Font.BOLD : Font.PLAIN, 14));
            setForeground(dangChon ? Color.WHITE : MAU_CHU_MENU);
            capNhatIcon();
            repaint();
        }

        private void capNhatIcon() {
            setIcon(new BieuTuongVector(loaiBieuTuong, 17, dangChon ? Color.WHITE : new Color(43, 62, 85)));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(Math.max(235, d.width), 40);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension d = getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, d.height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            RoundRectangle2D nen = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 9f, 9f);
            if (dangChon) {
                g2.setColor(MAU_XANH_DAM);
                g2.fill(nen);
            } else if (dangTroChuot) {
                g2.setColor(MAU_NEN_MENU_ACTIVE);
                g2.fill(nen);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nút Đăng xuất nền hồng nhạt chữ đỏ theo đúng ảnh thiết kế mẫu.
     */
    private static class NutDangXuat extends JButton {
        private boolean dangTroChuot = false;

        NutDangXuat(String chuoi) {
            super(chuoi);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_DANG_XUAT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));

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
            });
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 36);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            RoundRectangle2D hinh = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 9f, 9f);
            g2.setColor(dangTroChuot ? MAU_NEN_DANG_XUAT_HOVER : MAU_NEN_DANG_XUAT);
            g2.fill(hinh);
            g2.setColor(MAU_VIEN_DANG_XUAT);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(hinh);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /**
     * Nút tròn dấu chấm than (!) nằm ngoài cùng bên phải Header mở Hướng dẫn sử dụng (HDSD).
     */
    private static class NutHuongDanSuDung extends JButton {
        private boolean dangTroChuot = false;

        NutHuongDanSuDung() {
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Hướng dẫn sử dụng (HDSD)");
            setPreferredSize(new Dimension(36, 36));

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
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float s = Math.min(getWidth(), getHeight()) - 1.5f;
            Ellipse2D vongTron = new Ellipse2D.Float(0.75f, 0.75f, s, s);
            g2.setColor(dangTroChuot ? MAU_NEN_HDSD_HOVER : MAU_NEN_HDSD);
            g2.fill(vongTron);
            g2.setColor(dangTroChuot ? MAU_CAM_CHUC_VU : new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(vongTron);
            g2.dispose();

            Color mau = dangTroChuot ? MAU_CAM_CHUC_VU : MAU_XANH_DAM;
            ImageIcon icon = IconUtil.layIcon("help.png", 16, 16, mau);
            if (icon != null) {
                int x = (getWidth() - icon.getIconWidth()) / 2;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g, x, y);
            }
        }
    }

    /**
     * Tải icon từ thư mục img/ theo chuẩn cho toàn bộ nút trên thanh Header và menu thả xuống.
     */
    private static class BieuTuongVector implements Icon {
        private final LoaiBieuTuong loai;
        private final int kichThuoc;
        private final Color mau;

        BieuTuongVector(LoaiBieuTuong loai, int kichThuoc, Color mau) {
            this.loai = loai;
            this.kichThuoc = kichThuoc;
            this.mau = mau;
        }

        private String layTenFileIcon(LoaiBieuTuong loai) {
            return switch (loai) {
                case DASHBOARD -> "dashboard.png";
                case CUNG_CAP, SAN_PHAM -> "part.png";
                case DICH_VU -> "service.png";
                case KHUYEN_MAI -> "voucher.png";
                case PHUONG_TIEN -> "car.png";
                case HOA_DON -> "invoice.png";
                case KHACH_HANG -> "user.png";
                case QUAN_TRI, NHAN_VIEN -> "employee.png";
                case BAO_CAO -> "chart.png";
            };
        }

        @Override
        public int getIconWidth() {
            return kichThuoc;
        }

        @Override
        public int getIconHeight() {
            return kichThuoc;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Icon iconImg = IconUtil.layIcon(layTenFileIcon(loai), kichThuoc, kichThuoc, mau);
            if (iconImg != null) {
                iconImg.paintIcon(c, g, x, y);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyManHinhChinh frm = new QuanLyManHinhChinh();
            frm.setVisible(true);
        });
    }
}
