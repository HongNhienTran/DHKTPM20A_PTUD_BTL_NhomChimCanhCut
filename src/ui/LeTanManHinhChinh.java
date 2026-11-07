package ui;

import bus.HoaDonBUS;
import bus.PhieuTiepNhanBUS;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.TaiKhoan;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiThanhToan;
import util.IconUtil;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeTanManHinhChinh extends JFrame {
    // Mã định danh các màn hình con trong CardLayout của Lễ tân
    public static final String MAN_DASHBOARD = "DASHBOARD";
    public static final String MAN_TIEP_NHAN_XE = "TIEP_NHAN_XE";
    public static final String MAN_HANG_DOI_XUONG = "HANG_DOI_XE_TAI_XUONG";
    public static final String MAN_HO_SO_KHACH_HANG = "HO_SO_KHACH_HANG";
    public static final String MAN_TAO_DON_BAN_HANG = "TAO_DON_BAN_HANG";
    public static final String MAN_TRA_CUU_SAN_PHAM = "TRA_CUU_SAN_PHAM";
    public static final String MAN_LAP_HOA_DON = "LAP_HOA_DON_THANH_TOAN";
    public static final String MAN_DANH_SACH_HOA_DON = "DANH_SACH_HOA_DON";

    // Bảng màu đồng bộ thiết kế Pengu Garage
    private static final Color MAU_NEN_HEADER = Color.WHITE;
    private static final Color MAU_VIEN_DUOI_HEADER = new Color(226, 232, 240);
    private static final Color MAU_NEN_TRANG = new Color(246, 249, 253);
    private static final Color MAU_VIEN_THE = new Color(228, 235, 245);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(24, 58, 92);
    private static final Color MAU_CHU_MENU = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_CHU_NHOM_MENU = new Color(148, 163, 184);
    private static final Color MAU_NEN_MENU_ACTIVE = new Color(237, 244, 255);
    private static final Color MAU_VIEN_MENU_ACTIVE = new Color(212, 228, 255);
    private static final Color MAU_NEN_MENU_HOVER = new Color(244, 247, 252);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 124, 48);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_NEN_DANG_XUAT = new Color(255, 241, 242);
    private static final Color MAU_NEN_DANG_XUAT_HOVER = new Color(255, 228, 230);
    private static final Color MAU_VIEN_DANG_XUAT = new Color(254, 205, 211);
    private static final Color MAU_CHU_DANG_XUAT = new Color(225, 29, 72);
    private static final Color MAU_NEN_HDSD = new Color(241, 245, 249);
    private static final Color MAU_NEN_HDSD_HOVER = new Color(226, 232, 240);

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();
    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();

    private BufferedImage logoHeader;

    private final CardLayout boCucManHinhCon = new CardLayout();
    private final JPanel vungNoiDungChinh = new JPanel(boCucManHinhCon);

    // Các nút trên thanh điều hướng Header
    private NutDieuHuongHeader btnDashboard;
    private NutDieuHuongHeader btnTiepNhan;
    private NutDieuHuongHeader btnBanHangPhuTung;
    private NutDieuHuongHeader btnQuanLyHoaDon;

    private final Map<String, NutDieuHuongHeader> mapManHinhThuocNhom = new LinkedHashMap<>();
    private final List<MucMenuThaXuong> danhSachMucMenu = new ArrayList<>();
    private String maManHinhHienTai = MAN_DASHBOARD;
    private LeTanManHinhLapHoaDonThanhToan manHinhLapHoaDon;

    // Các thành phần hiển thị dữ liệu thực trên Dashboard (không dùng dữ liệu giả)
    private JLabel lblLuotTiepNhanHomNay;
    private JLabel lblXeChoPhanCong;
    private JLabel lblXeChoGiaoKhach;
    private JLabel lblSoXeDangThaoTac;
    private NhanVienThuocBadge lblTongXeTaiXuongBadge;
    private JPanel danhSachHangDoiPanel;
    private TheBuocXuong buocChoKiemTra;
    private TheBuocXuong buocBaoGia;
    private TheBuocXuong buocDangSuaChua;
    private TheBuocXuong buocChoBanGiao;

    public LeTanManHinhChinh() {
        this(null);
    }

    public LeTanManHinhChinh(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        taiLogoThuongHieu();
        khoiTaoGiaoDien();
        chuyenManHinh(MAN_DASHBOARD);
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    private void khoiTaoGiaoDien() {
        setTitle("Pengu Garage - Phân hệ Lễ tân");
        setSize(new Dimension(1366, 768));
        setMinimumSize(new Dimension(1120, 660));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gocPanel = new JPanel(new BorderLayout());
        gocPanel.setBackground(MAU_NEN_TRANG);

        JPanel thanhHeader = taoThanhHeader();
        khoiTaoCacManHinhCon();

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

        // 1. Cụm bên trái: Logo + Tên thương hiệu + Các danh mục chức năng Lễ tân + Nút ! (HDSD)
        JPanel cumTraiVaMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 11));
        cumTraiVaMenu.setOpaque(false);

        cumTraiVaMenu.add(taoCumThuongHieuTrai());
        cumTraiVaMenu.add(Box.createHorizontalStrut(28));

        // Nút Dashboard
        btnDashboard = new NutDieuHuongHeader("Dashboard", LoaiBieuTuong.DASHBOARD, false);
        btnDashboard.addActionListener(e -> chuyenManHinh(MAN_DASHBOARD));
        cumTraiVaMenu.add(btnDashboard);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 1: Tiếp nhận
        btnTiepNhan = new NutDieuHuongHeader("Tiếp nhận", LoaiBieuTuong.XE_HOI, true);
        JPopupMenu popupTiepNhan = taoPopupDanhMuc(
                "TIẾP NHẬN",
                new ThongTinMucCon(MAN_TIEP_NHAN_XE, "Tiếp nhận xe", LoaiBieuTuong.XE_HOI),
                new ThongTinMucCon(MAN_HANG_DOI_XUONG, "Hàng đợi xe tại xưởng", LoaiBieuTuong.DONG_HO_CAT),
                new ThongTinMucCon(MAN_HO_SO_KHACH_HANG, "Hồ sơ khách hàng", LoaiBieuTuong.KHACH_HANG)
        );
        ganSuKienMoMenu(btnTiepNhan, popupTiepNhan);
        cumTraiVaMenu.add(btnTiepNhan);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 2: Bán hàng & Phụ tùng
        btnBanHangPhuTung = new NutDieuHuongHeader("Bán hàng & Phụ tùng", LoaiBieuTuong.GIO_HANG, true);
        JPopupMenu popupBanHang = taoPopupDanhMuc(
                "BÁN HÀNG & PHỤ TÙNG",
                new ThongTinMucCon(MAN_TAO_DON_BAN_HANG, "Tạo đơn bán hàng", LoaiBieuTuong.GIO_HANG),
                new ThongTinMucCon(MAN_TRA_CUU_SAN_PHAM, "Tra cứu sản phẩm", LoaiBieuTuong.TIM_KIEM)
        );
        ganSuKienMoMenu(btnBanHangPhuTung, popupBanHang);
        cumTraiVaMenu.add(btnBanHangPhuTung);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 3: Quản lý hóa đơn
        btnQuanLyHoaDon = new NutDieuHuongHeader("Quản lý hóa đơn", LoaiBieuTuong.TAI_LIEU, true);
        JPopupMenu popupHoaDon = taoPopupDanhMuc(
                "QUẢN LÝ HÓA ĐƠN",
                new ThongTinMucCon(MAN_LAP_HOA_DON, "Lập hóa đơn thanh toán", LoaiBieuTuong.TAI_LIEU),
                new ThongTinMucCon(MAN_DANH_SACH_HOA_DON, "Danh sách hóa đơn", LoaiBieuTuong.SO_HOA_DON)
        );
        ganSuKienMoMenu(btnQuanLyHoaDon, popupHoaDon);
        cumTraiVaMenu.add(btnQuanLyHoaDon);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Nút ! (Hướng dẫn sử dụng) đặt ngay kế bên danh mục cuối giống màn hình Quản lý
        NutHuongDanSuDung btnHDSD = new NutHuongDanSuDung();
        btnHDSD.addActionListener(e -> moHopThoaiHuongDanSuDung());
        cumTraiVaMenu.add(btnHDSD);

        // Ánh xạ màn hình con về nút nhóm cha
        mapManHinhThuocNhom.put(MAN_DASHBOARD, btnDashboard);
        mapManHinhThuocNhom.put(MAN_TIEP_NHAN_XE, btnTiepNhan);
        mapManHinhThuocNhom.put(MAN_HANG_DOI_XUONG, btnTiepNhan);
        mapManHinhThuocNhom.put(MAN_HO_SO_KHACH_HANG, btnTiepNhan);
        mapManHinhThuocNhom.put(MAN_TAO_DON_BAN_HANG, btnBanHangPhuTung);
        mapManHinhThuocNhom.put(MAN_TRA_CUU_SAN_PHAM, btnBanHangPhuTung);
        mapManHinhThuocNhom.put(MAN_LAP_HOA_DON, btnQuanLyHoaDon);
        mapManHinhThuocNhom.put(MAN_DANH_SACH_HOA_DON, btnQuanLyHoaDon);

        // 2. Cụm bên phải: Thông tin tài khoản Lễ tân + Nút Đăng xuất
        JPanel cumPhai = taoCumNguoiDungVaDangXuat();

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
                g2.setColor(MAU_VIEN_DUOI_HEADER);
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
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chuyenManHinh(MAN_DASHBOARD);
            }
        });
        return panel;
    }

    private JPanel taoCumNguoiDungVaDangXuat() {
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 11));
        cumPhai.setOpaque(false);

        NhanVien nv = taiKhoanDangNhap != null ? taiKhoanDangNhap.getMaNhanVien() : null;
        String hoTen = (nv != null && nv.getHoTen() != null) ? nv.getHoTen().trim() : "";
        String chucVu = (nv != null && nv.getChucVu() != null) ? nv.getChucVu().getTenHienThi() : "";
        if (hoTen.isEmpty() && taiKhoanDangNhap != null && taiKhoanDangNhap.getTenDangNhap() != null) {
            hoTen = taiKhoanDangNhap.getTenDangNhap();
        }

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
            lblVaiTro.setForeground(MAU_CAM_CHU_DAO);
            khoiChu.add(lblVaiTro);
        }

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

    private void khoiTaoCacManHinhCon() {
        vungNoiDungChinh.setBackground(MAU_NEN_TRANG);
        vungNoiDungChinh.add(taoManHinhDashboard(), MAN_DASHBOARD);
        vungNoiDungChinh.add(taoKhungChucNang("Tiếp nhận", "Tiếp nhận xe"), MAN_TIEP_NHAN_XE);
        vungNoiDungChinh.add(taoKhungChucNang("Tiếp nhận", "Hàng đợi xe tại xưởng"), MAN_HANG_DOI_XUONG);
        vungNoiDungChinh.add(new QuanLyManHinhQuanLyKhachHang(), MAN_HO_SO_KHACH_HANG);
        vungNoiDungChinh.add(new LeTanManHinhBanHang(taiKhoanDangNhap), MAN_TAO_DON_BAN_HANG);
        vungNoiDungChinh.add(new LeTanManHinhTraCuuSanPham(), MAN_TRA_CUU_SAN_PHAM);
        manHinhLapHoaDon = new LeTanManHinhLapHoaDonThanhToan(taiKhoanDangNhap);
        vungNoiDungChinh.add(manHinhLapHoaDon, MAN_LAP_HOA_DON);
        vungNoiDungChinh.add(new QuanLyManHinhQuanLyHoaDon(), MAN_DANH_SACH_HOA_DON);
    }

    /**
     * Xây dựng giao diện Dashboard Lễ tân đúng bố cục ảnh mẫu (3 thẻ chỉ số trên + Hàng đợi phương tiện + Thao tác nhanh + Hàng đợi xe tại xưởng).
     */
    private JPanel taoManHinhDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 20));
        dashboard.setBackground(MAU_NEN_TRANG);
        dashboard.setBorder(new EmptyBorder(22, 24, 24, 24));

        // Hàng trên: 3 thẻ chỉ số tổng quan
        JPanel hangChiSoTren = new JPanel(new GridLayout(1, 3, 20, 0));
        hangChiSoTren.setOpaque(false);

        lblLuotTiepNhanHomNay = new JLabel("0");
        lblXeChoPhanCong = new JLabel("0 xe");
        lblXeChoGiaoKhach = new JLabel("0 xe");

        hangChiSoTren.add(taoTheChiSo("Lượt tiếp nhận hôm nay", lblLuotTiepNhanHomNay, MAU_XANH_LA));
        hangChiSoTren.add(taoTheChiSo("Xe mới chờ phân công KTV", lblXeChoPhanCong, MAU_CAM_CHU_DAO));
        hangChiSoTren.add(taoTheChiSo("Xe đã xong chờ giao khách", lblXeChoGiaoKhach, MAU_XANH_DAM));

        // Hàng dưới: Chia 2 cột tỷ lệ (~72% trái: Hàng Đợi Phương Tiện | ~28% phải: Thao Tác Nhanh + Hàng Đợi Xe Tại Xưởng)
        JPanel vungChinhDuoi = new JPanel(new BoCucHaiCotDashboard(0.715, 20, 310));
        vungChinhDuoi.setOpaque(false);

        vungChinhDuoi.add(taoTheHangDoiPhuongTien(), BorderLayout.CENTER);
        vungChinhDuoi.add(taoCotBenPhaiDashboard(), BorderLayout.EAST);

        dashboard.add(hangChiSoTren, BorderLayout.NORTH);
        dashboard.add(vungChinhDuoi, BorderLayout.CENTER);
        return dashboard;
    }

    private TheBoGocTrang taoTheChiSo(String tieuDe, JLabel lblGiaTri, Color mauGiaTri) {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 10));
        the.setPreferredSize(new Dimension(0, 108));
        the.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblNhan = new JLabel(tieuDe);
        lblNhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNhan.setForeground(MAU_CHU_PHU);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGiaTri.setForeground(mauGiaTri);

        the.add(lblNhan, BorderLayout.NORTH);
        the.add(lblGiaTri, BorderLayout.CENTER);
        return the;
    }

    private TheBoGocTrang taoTheHangDoiPhuongTien() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 16));
        the.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel thanhTieuDe = new JPanel(new BorderLayout());
        thanhTieuDe.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Hàng Đợi Phương Tiện");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        lblSoXeDangThaoTac = new JLabel("• 0 xe đang thao tác");
        lblSoXeDangThaoTac.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSoXeDangThaoTac.setForeground(MAU_XANH_LA);

        thanhTieuDe.add(lblTieuDe, BorderLayout.WEST);
        thanhTieuDe.add(lblSoXeDangThaoTac, BorderLayout.EAST);

        danhSachHangDoiPanel = new JPanel();
        danhSachHangDoiPanel.setOpaque(false);
        danhSachHangDoiPanel.setLayout(new BoxLayout(danhSachHangDoiPanel, BoxLayout.Y_AXIS));

        JPanel bocDanhSach = new JPanel(new BorderLayout());
        bocDanhSach.setOpaque(false);
        bocDanhSach.add(danhSachHangDoiPanel, BorderLayout.NORTH);

        JScrollPane cuon = new JScrollPane(bocDanhSach);
        cuon.setBorder(null);
        cuon.setOpaque(false);
        cuon.getViewport().setOpaque(false);
        cuon.getVerticalScrollBar().setUnitIncrement(16);

        the.add(thanhTieuDe, BorderLayout.NORTH);
        the.add(cuon, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoCotBenPhaiDashboard() {
        JPanel cotPhai = new JPanel(new BorderLayout(0, 20));
        cotPhai.setOpaque(false);

        cotPhai.add(taoTheThaoTacNhanh(), BorderLayout.NORTH);
        cotPhai.add(taoTheHangDoiXeTaiXuong(), BorderLayout.CENTER);
        return cotPhai;
    }

    private TheBoGocTrang taoTheThaoTacNhanh() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 14));
        the.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTieuDe = new JLabel("Thao Tác Nhanh Nghiệp Vụ");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel danhSachNut = new JPanel(new GridLayout(4, 1, 0, 10));
        danhSachNut.setOpaque(false);

        NutThaoTacNhanh btnTiepNhanXe = new NutThaoTacNhanh(
                "Tiếp Nhận Xe",
                LoaiBieuTuong.XE_HOI,
                MAU_CAM_CHU_DAO,
                MAU_CAM_HOVER,
                Color.WHITE,
                null
        );
        btnTiepNhanXe.addActionListener(e -> chuyenManHinh(MAN_TIEP_NHAN_XE));

        NutThaoTacNhanh btnHangDoiXuong = new NutThaoTacNhanh(
                "Hàng Đợi Xe Tại Xưởng",
                LoaiBieuTuong.DONG_HO_CAT,
                MAU_XANH_DAM,
                MAU_XANH_DAM_HOVER,
                Color.WHITE,
                null
        );
        btnHangDoiXuong.addActionListener(e -> chuyenManHinh(MAN_HANG_DOI_XUONG));

        NutThaoTacNhanh btnTaoDonBanHang = new NutThaoTacNhanh(
                "Tạo Đơn Bán Hàng Phụ Tùng",
                LoaiBieuTuong.GIO_HANG,
                Color.WHITE,
                MAU_NEN_MENU_HOVER,
                MAU_XANH_DAM,
                new Color(218, 228, 240)
        );
        btnTaoDonBanHang.addActionListener(e -> chuyenManHinh(MAN_TAO_DON_BAN_HANG));

        NutThaoTacNhanh btnQuanLyLapHoaDon = new NutThaoTacNhanh(
                "Quản Lý & Lập Hóa Đơn",
                LoaiBieuTuong.TAI_LIEU,
                Color.WHITE,
                MAU_NEN_MENU_HOVER,
                MAU_XANH_DAM,
                new Color(218, 228, 240)
        );
        btnQuanLyLapHoaDon.addActionListener(e -> chuyenManHinh(MAN_LAP_HOA_DON));

        danhSachNut.add(btnTiepNhanXe);
        danhSachNut.add(btnHangDoiXuong);
        danhSachNut.add(btnTaoDonBanHang);
        danhSachNut.add(btnQuanLyLapHoaDon);

        the.add(lblTieuDe, BorderLayout.NORTH);
        the.add(danhSachNut, BorderLayout.CENTER);
        return the;
    }

    private TheBoGocTrang taoTheHangDoiXeTaiXuong() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 14));
        the.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel thanhDau = new JPanel(new BorderLayout());
        thanhDau.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Hàng Đợi Xe Tại Xưởng");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        lblTongXeTaiXuongBadge = new NhanVienThuocBadge(
                "• 0 xe tại xưởng",
                new Color(238, 244, 255),
                new Color(214, 228, 255),
                new Color(30, 58, 138)
        );

        thanhDau.add(lblTieuDe, BorderLayout.WEST);
        thanhDau.add(lblTongXeTaiXuongBadge, BorderLayout.EAST);

        JPanel cacBuocPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        cacBuocPanel.setOpaque(false);

        buocChoKiemTra = new TheBuocXuong(
                "1. Chờ kiểm tra",
                LoaiBieuTuong.DONG_HO_CAT,
                new Color(239, 246, 255),
                new Color(219, 234, 254),
                new Color(29, 78, 216),
                new Color(219, 234, 254),
                new Color(30, 64, 175)
        );
        buocBaoGia = new TheBuocXuong(
                "2. Báo giá",
                LoaiBieuTuong.TIM_KIEM,
                new Color(255, 247, 237),
                new Color(255, 237, 213),
                new Color(194, 65, 12),
                new Color(255, 237, 213),
                new Color(154, 52, 18)
        );
        buocDangSuaChua = new TheBuocXuong(
                "3. Đang sửa chữa",
                LoaiBieuTuong.CO_LE,
                new Color(255, 244, 234),
                new Color(254, 215, 170),
                new Color(194, 65, 12),
                new Color(254, 215, 170),
                new Color(154, 52, 18)
        );
        buocChoBanGiao = new TheBuocXuong(
                "4. Chờ bàn giao",
                LoaiBieuTuong.HOAN_THANH,
                new Color(236, 253, 245),
                new Color(209, 250, 229),
                new Color(4, 120, 87),
                new Color(209, 250, 229),
                new Color(6, 95, 70)
        );

        cacBuocPanel.add(buocChoKiemTra);
        cacBuocPanel.add(buocBaoGia);
        cacBuocPanel.add(buocDangSuaChua);
        cacBuocPanel.add(buocChoBanGiao);

        JPanel bocTren = new JPanel(new BorderLayout());
        bocTren.setOpaque(false);
        bocTren.add(cacBuocPanel, BorderLayout.NORTH);

        the.add(thanhDau, BorderLayout.NORTH);
        the.add(bocTren, BorderLayout.CENTER);
        return the;
    }

    /**
     * Nạp số liệu thực tế từ CSDL thông qua tầng BUS (tuyệt đối không hiển thị dữ liệu giả).
     */
    public void lamMoiDuLieuDashboard() {
        List<PhieuTiepNhan> tatCaPhieu;
        try {
            tatCaPhieu = phieuTiepNhanBUS.layTatCa();
        } catch (RuntimeException ex) {
            tatCaPhieu = Collections.emptyList();
        }

        LocalDate homNay = LocalDate.now();
        int luotHomNay = 0;
        int choPhanCong = 0;
        int demChoKiemTra = 0;
        int demBaoGia = 0;
        int demDangSuaChua = 0;
        int demChoBanGiao = 0;
        List<PhieuTiepNhan> dsPhuongTienDangThaoTac = new ArrayList<>();

        for (PhieuTiepNhan ptn : tatCaPhieu) {
            TrangThaiPhieuTiepNhan tt = ptn.getTrangThai();
            if (tt == null || tt == TrangThaiPhieuTiepNhan.DA_HUY) {
                continue;
            }
            if (ptn.getThoiGianTiepNhan() != null
                    && ptn.getThoiGianTiepNhan().toLocalDate().equals(homNay)) {
                luotHomNay++;
            }

            switch (tt) {
                case CHO_KIEM_TRA -> {
                    choPhanCong++;
                    demChoKiemTra++;
                    dsPhuongTienDangThaoTac.add(ptn);
                }
                case DANG_KIEM_TRA -> {
                    demChoKiemTra++;
                    dsPhuongTienDangThaoTac.add(ptn);
                }
                case CHO_XAC_NHAN_BAO_GIA -> {
                    demBaoGia++;
                    dsPhuongTienDangThaoTac.add(ptn);
                }
                case DANG_SUA_CHUA -> {
                    demDangSuaChua++;
                    dsPhuongTienDangThaoTac.add(ptn);
                }
                case HOAN_THANH -> {
                    if (!daThanhToanXong(ptn.getMaTiepNhan())) {
                        demChoBanGiao++;
                        dsPhuongTienDangThaoTac.add(ptn);
                    }
                }
                default -> {
                }
            }
        }

        int tongTaiXuong = demChoKiemTra + demBaoGia + demDangSuaChua + demChoBanGiao;

        lblLuotTiepNhanHomNay.setText(String.valueOf(luotHomNay));
        lblXeChoPhanCong.setText(choPhanCong + " xe");
        lblXeChoGiaoKhach.setText(demChoBanGiao + " xe");
        lblSoXeDangThaoTac.setText("• " + dsPhuongTienDangThaoTac.size() + " xe đang thao tác");
        lblTongXeTaiXuongBadge.setVanBan("• " + tongTaiXuong + " xe tại xưởng");

        buocChoKiemTra.setSoLuongXe(demChoKiemTra);
        buocBaoGia.setSoLuongXe(demBaoGia);
        buocDangSuaChua.setSoLuongXe(demDangSuaChua);
        buocChoBanGiao.setSoLuongXe(demChoBanGiao);

        danhSachHangDoiPanel.removeAll();
        for (int i = 0; i < dsPhuongTienDangThaoTac.size(); i++) {
            if (i > 0) {
                danhSachHangDoiPanel.add(Box.createVerticalStrut(12));
            }
            danhSachHangDoiPanel.add(new DongPhuongTienHangDoi(dsPhuongTienDangThaoTac.get(i)));
        }
        danhSachHangDoiPanel.revalidate();
        danhSachHangDoiPanel.repaint();
    }

    private boolean daThanhToanXong(String maTiepNhan) {
        try {
            HoaDon hd = hoaDonBUS.timTheoPhieuTiepNhan(maTiepNhan);
            return hd != null && hd.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private JPanel taoKhungChucNang(String tenNhom, String tenChucNang) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(MAU_NEN_TRANG);

        JPanel the = new JPanel();
        the.setOpaque(false);
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));

        JLabel lblNhom = new JLabel(tenNhom.toUpperCase(), SwingConstants.CENTER);
        lblNhom.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNhom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNhom.setForeground(MAU_CAM_CHU_DAO);

        JLabel lblTieuDe = new JLabel(tenChucNang, SwingConstants.CENTER);
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        lblTieuDe.setBorder(new EmptyBorder(8, 0, 6, 0));

        JLabel lblPhu = new JLabel("Phân hệ đang sẵn sàng để tích hợp giao diện chi tiết.", SwingConstants.CENTER);
        lblPhu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPhu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPhu.setForeground(MAU_CHU_PHU);

        the.add(lblNhom);
        the.add(lblTieuDe);
        the.add(lblPhu);
        panel.add(the);
        return panel;
    }

    /**
     * Cho phép lắp các màn hình chức năng con của Lễ tân vào CardLayout.
     */
    public void datManHinhCon(String maManHinh, JPanel panelMoi) {
        vungNoiDungChinh.add(panelMoi, maManHinh);
        if (maManHinh.equals(maManHinhHienTai)) {
            boCucManHinhCon.show(vungNoiDungChinh, maManHinh);
        }
    }

    public void chuyenManHinh(String maManHinh) {
        this.maManHinhHienTai = maManHinh;
        if (MAN_DASHBOARD.equals(maManHinh)) {
            lamMoiDuLieuDashboard();
        } else if (MAN_LAP_HOA_DON.equals(maManHinh) && manHinhLapHoaDon != null) {
            manHinhLapHoaDon.taiDanhSachHoaDonCho();
        }
        boCucManHinhCon.show(vungNoiDungChinh, maManHinh);

        NutDieuHuongHeader nutChaActive = mapManHinhThuocNhom.get(maManHinh);
        btnDashboard.setDangChon(nutChaActive == btnDashboard);
        btnTiepNhan.setDangChon(nutChaActive == btnTiepNhan);
        btnBanHangPhuTung.setDangChon(nutChaActive == btnBanHangPhuTung);
        btnQuanLyHoaDon.setDangChon(nutChaActive == btnQuanLyHoaDon);

        for (MucMenuThaXuong item : danhSachMucMenu) {
            item.setDangChon(maManHinh.equals(item.getMaManHinh()));
        }
    }

    private void xuLyDangXuat() {
        dispose();
        new ManHinhDangNhap().setVisible(true);
    }

    private void moHopThoaiHuongDanSuDung() {
        JDialog dialog = new JDialog(this, "Hướng dẫn sử dụng (HDSD) - Phân hệ Lễ tân", true);
        dialog.setSize(620, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel dauDialog = new JPanel(new BorderLayout());
        dauDialog.setBackground(MAU_XANH_DAM);
        dauDialog.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblTieuDe = new JLabel("HƯỚNG DẪN SỬ DỤNG PHÂN HỆ LỄ TÂN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(Color.WHITE);
        dauDialog.add(lblTieuDe, BorderLayout.WEST);

        String noiDungHtml = """
                <html>
                <body style='font-family: Segoe UI; font-size: 11px; color: #1E293B; padding: 14px 18px; line-height: 1.55;'>
                    <p style='margin-top:0;'>Chào mừng bạn đến với <b>Pengu Garage - Phân hệ Lễ tân</b>. Các nhóm nghiệp vụ trên thanh điều hướng bao gồm:</p>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>1. Dashboard</h4>
                    <p style='margin-top:0;'>Theo dõi lượt tiếp nhận trong ngày, hàng đợi phương tiện thời gian thực, trạng thái xe tại xưởng và các nút thao tác nhanh.</p>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>2. Tiếp nhận</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Tiếp nhận xe:</b> Lập phiếu tiếp nhận phương tiện vào xưởng, ghi nhận số KM, chìa khóa và yêu cầu của khách.</li>
                        <li><b>Hàng đợi xe tại xưởng:</b> Theo dõi tiến độ kiểm tra, báo giá, sửa chữa và bàn giao xe.</li>
                        <li><b>Hồ sơ khách hàng:</b> Tra cứu, thêm mới khách hàng và danh sách phương tiện thuộc sở hữu khách hàng.</li>
                    </ul>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>3. Bán hàng &amp; Phụ tùng</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Tạo đơn bán hàng:</b> Bán lẻ phụ tùng trực tiếp cho khách hàng đăng ký hoặc khách vãng lai (KH000000000).</li>
                        <li><b>Tra cứu sản phẩm:</b> Kiểm tra nhanh giá bán, danh mục và số lượng tồn kho phụ tùng.</li>
                    </ul>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>4. Quản lý hóa đơn</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Lập hóa đơn thanh toán:</b> Tính tiền dịch vụ &amp; phụ tùng, áp dụng mã khuyến mãi và xác nhận thanh toán.</li>
                        <li><b>Danh sách hóa đơn:</b> Tra cứu lịch sử hóa đơn dịch vụ và hóa đơn bán hàng.</li>
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
        URL url = LeTanManHinhChinh.class.getResource("/img/logo_pengu-garage.png");
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
        XE_HOI,
        DONG_HO_CAT,
        KHACH_HANG,
        GIO_HANG,
        TIM_KIEM,
        TAI_LIEU,
        SO_HOA_DON,
        CO_LE,
        HOAN_THANH
    }

    /**
     * Bố cục chia 2 cột tỷ lệ cho vùng dưới Dashboard.
     */
    private static class BoCucHaiCotDashboard implements LayoutManager {
        private final double tyLeTrai;
        private final int khoangCach;
        private final int rongToiThieuPhai;
        private Component trai;
        private Component phai;

        BoCucHaiCotDashboard(double tyLeTrai, int khoangCach, int rongToiThieuPhai) {
            this.tyLeTrai = tyLeTrai;
            this.khoangCach = khoangCach;
            this.rongToiThieuPhai = rongToiThieuPhai;
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
            if (BorderLayout.EAST.equals(name)) {
                phai = comp;
            } else {
                trai = comp;
            }
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            if (comp == trai) {
                trai = null;
            } else if (comp == phai) {
                phai = null;
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(1200, 500);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(800, 360);
        }

        @Override
        public void layoutContainer(Container parent) {
            int w = parent.getWidth();
            int h = parent.getHeight();
            int khaDung = Math.max(0, w - khoangCach);
            int rongPhai = Math.max(rongToiThieuPhai, (int) Math.round(khaDung * (1.0 - tyLeTrai)));
            int rongTrai = Math.max(0, khaDung - rongPhai);
            if (trai != null) {
                trai.setBounds(0, 0, rongTrai, h);
            }
            if (phai != null) {
                phai.setBounds(rongTrai + khoangCach, 0, rongPhai, h);
            }
        }
    }

    /**
     * Thẻ nền trắng bo góc viền mảnh đồng bộ các khối trên Dashboard.
     */
    private static class TheBoGocTrang extends JPanel {
        TheBoGocTrang(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float w = getWidth() - 1.5f;
            float h = getHeight() - 1.5f;
            RoundRectangle2D khung = new RoundRectangle2D.Float(0.75f, 0.75f, w, h, 16f, 16f);

            g2.setColor(new Color(15, 23, 42, 7));
            g2.fill(new RoundRectangle2D.Float(1.5f, 2.5f, w - 1f, h - 1f, 16f, 16f));

            g2.setColor(Color.WHITE);
            g2.fill(khung);
            g2.setColor(MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(khung);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Một dòng phương tiện thực tế trong bảng Hàng Đợi Phương Tiện khi có phiếu tiếp nhận.
     */
    private static class DongPhuongTienHangDoi extends JPanel {
        DongPhuongTienHangDoi(PhieuTiepNhan ptn) {
            super(new BorderLayout(20, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(14, 18, 14, 18));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

            PhuongTien pt = ptn.getPhuongTien();
            KhachHang kh = pt != null ? pt.getKhachHang() : null;

            String bienSo = pt != null ? pt.getBienSoXe() : "-";
            String mauXe = pt != null ? pt.getMauXe() : "-";
            String yeuCau = ptn.getYeuCauKhachHang() != null ? ptn.getYeuCauKhachHang() : "";
            String tenKhach = kh != null ? kh.getHoTen() : "Khách hàng";

            JPanel cumXe = new JPanel();
            cumXe.setOpaque(false);
            cumXe.setPreferredSize(new Dimension(140, 40));
            cumXe.setLayout(new BoxLayout(cumXe, BoxLayout.Y_AXIS));

            JLabel lblBienSo = new JLabel(bienSo);
            lblBienSo.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblBienSo.setForeground(MAU_XANH_DAM);

            JLabel lblMauXe = new JLabel(mauXe);
            lblMauXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblMauXe.setForeground(MAU_CHU_PHU);

            cumXe.add(lblBienSo);
            cumXe.add(lblMauXe);

            JPanel cumThongTin = new JPanel();
            cumThongTin.setOpaque(false);
            cumThongTin.setLayout(new BoxLayout(cumThongTin, BoxLayout.Y_AXIS));

            JLabel lblYeuCau = new JLabel(yeuCau);
            lblYeuCau.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblYeuCau.setForeground(MAU_CHU_MENU);

            JLabel lblKhachVaMa = new JLabel("Khách: " + tenKhach + " • Mã: " + ptn.getMaTiepNhan());
            lblKhachVaMa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblKhachVaMa.setForeground(MAU_CHU_PHU);

            cumThongTin.add(lblYeuCau);
            cumThongTin.add(lblKhachVaMa);

            String chuoiTrangThai = layNhanTrangThaiHangDoi(ptn.getTrangThai());
            NhanVienThuocBadge badge = new NhanVienThuocBadge(
                    chuoiTrangThai,
                    new Color(235, 242, 250),
                    new Color(215, 226, 240),
                    MAU_XANH_DAM
            );

            JPanel bocBadge = new JPanel(new GridBagLayout());
            bocBadge.setOpaque(false);
            bocBadge.add(badge);

            add(cumXe, BorderLayout.WEST);
            add(cumThongTin, BorderLayout.CENTER);
            add(bocBadge, BorderLayout.EAST);
        }

        private static String layNhanTrangThaiHangDoi(TrangThaiPhieuTiepNhan tt) {
            if (tt == null) {
                return "CHỜ KIỂM TRA";
            }
            return switch (tt) {
                case CHO_KIEM_TRA, DANG_KIEM_TRA -> "CHỜ KIỂM TRA";
                case CHO_XAC_NHAN_BAO_GIA -> "BÁO GIÁ";
                case DANG_SUA_CHUA -> "ĐANG SỬA CHỮA";
                case HOAN_THANH -> "CHỜ BÀN GIAO";
                case DA_HUY -> "ĐÃ HỦY";
            };
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D khung = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12f, 12f);
            g2.setColor(new Color(248, 250, 253));
            g2.fill(khung);
            g2.setColor(new Color(226, 232, 242));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(khung);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nút bấm bo góc trong thẻ Thao Tác Nhanh Nghiệp Vụ.
     */
    private static class NutThaoTacNhanh extends JButton {
        private final Color mauNen;
        private final Color mauHover;
        private final Color mauVien;
        private boolean dangTroChuot = false;

        NutThaoTacNhanh(String vanBan, LoaiBieuTuong iconLoai, Color mauNen, Color mauHover,
                        Color mauChu, Color mauVien) {
            super(vanBan, new BieuTuongVector(iconLoai, 15, mauChu));
            this.mauNen = mauNen;
            this.mauHover = mauHover;
            this.mauVien = mauVien;

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(mauChu);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setIconTextGap(8);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(0, 42));

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

            RoundRectangle2D hinh = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f);
            g2.setColor(dangTroChuot ? mauHover : mauNen);
            g2.fill(hinh);

            if (mauVien != null) {
                g2.setColor(mauVien);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(hinh);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Một bước trạng thái màu pastel trong thẻ Hàng Đợi Xe Tại Xưởng.
     */
    private static class TheBuocXuong extends JPanel {
        private final Color mauNen;
        private final Color mauVien;
        private final NhanVienThuocBadge badgeSoXe;

        TheBuocXuong(String tenBuoc, LoaiBieuTuong loaiIcon, Color mauNen, Color mauVien,
                     Color mauChu, Color mauNenBadge, Color mauChuBadge) {
            super(new BorderLayout(8, 0));
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 44));
            setBorder(new EmptyBorder(0, 12, 0, 12));

            Color mauIconNhat = new Color(mauChu.getRed(), mauChu.getGreen(), mauChu.getBlue(), 110);
            JLabel lblTen = new JLabel(tenBuoc, new BieuTuongVector(loaiIcon, 14, mauIconNhat), SwingConstants.LEFT);
            lblTen.setIconTextGap(8);
            lblTen.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTen.setForeground(mauChu);

            badgeSoXe = new NhanVienThuocBadge("0 xe", mauNenBadge, mauVien, mauChuBadge);
            JPanel bocBadge = new JPanel(new GridBagLayout());
            bocBadge.setOpaque(false);
            bocBadge.add(badgeSoXe);

            add(lblTen, BorderLayout.WEST);
            add(bocBadge, BorderLayout.EAST);
        }

        void setSoLuongXe(int soLuong) {
            badgeSoXe.setVanBan(soLuong + " xe");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D hinh = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f);
            g2.setColor(mauNen);
            g2.fill(hinh);
            g2.setColor(mauVien);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(hinh);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nhãn viên thuốc (pill badge) bo tròn hiển thị số lượng xe hoặc trạng thái.
     */
    private static class NhanVienThuocBadge extends JComponent {
        private String vanBan;
        private final Color mauNen;
        private final Color mauVien;
        private final Color mauChu;

        NhanVienThuocBadge(String vanBan, Color mauNen, Color mauVien, Color mauChu) {
            this.vanBan = vanBan;
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            this.mauChu = mauChu;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
        }

        void setVanBan(String vanBanMoi) {
            this.vanBan = vanBanMoi;
            revalidate();
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(vanBan) + 20, 24);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            float w = getWidth() - 1f;
            float h = getHeight() - 1f;
            RoundRectangle2D vienThuoc = new RoundRectangle2D.Float(0.5f, 0.5f, w, h, h, h);
            g2.setColor(mauNen);
            g2.fill(vienThuoc);
            if (mauVien != null) {
                g2.setColor(mauVien);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(vienThuoc);
            }

            g2.setFont(getFont());
            g2.setColor(mauChu);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(vanBan)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(vanBan, x, y);
            g2.dispose();
        }
    }

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
                g2.setColor(dangChon ? MAU_XANH_DAM : MAU_CHU_PHU);
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
            return new Dimension(Math.max(240, d.width), 40);
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            float s = Math.min(getWidth(), getHeight()) - 1.5f;
            Ellipse2D vongTron = new Ellipse2D.Float(0.75f, 0.75f, s, s);
            g2.setColor(dangTroChuot ? MAU_NEN_HDSD_HOVER : MAU_NEN_HDSD);
            g2.fill(vongTron);
            g2.setColor(dangTroChuot ? MAU_CAM_CHU_DAO : new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(vongTron);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(dangTroChuot ? MAU_CAM_CHU_DAO : MAU_XANH_DAM);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth("!")) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 1;
            g2.drawString("!", x, y);

            g2.dispose();
        }
    }

    /**
     * Bộ vẽ vector sắc nét cho icon menu và Dashboard Lễ tân.
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
            String tenAnh = switch (loai) {
                case DASHBOARD -> "dashboard.png";
                case XE_HOI -> "car.png";
                case DONG_HO_CAT -> "history.png";
                case KHACH_HANG -> "user.png";
                case GIO_HANG -> "part.png";
                case TIM_KIEM -> "search.png";
                case TAI_LIEU -> "invoice.png";
                case SO_HOA_DON -> "invoice.png";
                case CO_LE -> "service.png";
                case HOAN_THANH -> "save.png";
                default -> null;
            };

            if (tenAnh != null) {
                ImageIcon iconImg = IconUtil.layIcon(tenAnh, kichThuoc, kichThuoc, mau);
                if (iconImg != null) {
                    iconImg.paintIcon(c, g, x, y);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LeTanManHinhChinh frm = new LeTanManHinhChinh();
            frm.setVisible(true);
        });
    }
}
