package ui;

import bus.ChiTietDungPhuTungBUS;
import bus.PhieuPhanCongBUS;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DichVu;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.SanPham;
import entity.TaiKhoan;
import enums.TrangThaiSua;
import util.ChonNgayLichUtil;
import util.IconUtil;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KyThuatVienManHinhChinh extends JFrame {
    // Mã định danh các màn hình con trong CardLayout của Kỹ thuật viên
    public static final String MAN_PHIEU_PHAN_CONG = "PHIEU_PHAN_CONG";
    public static final String MAN_KIEM_TRA = "KIEM_TRA";
    public static final String MAN_TRA_CUU_PHU_TUNG = "TRA_CUU_PHU_TUNG";
    public static final String MAN_LICH_SU_CA_SUA = "LICH_SU_CA_SUA_HOAN_TAT";

    private static final DateTimeFormatter DINH_DANG_THOI_GIAN = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter DINH_DANG_NGAY_LOC = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Bảng màu đồng bộ hệ thống Pengu Garage
    private static final Color MAU_NEN_HEADER = Color.WHITE;
    private static final Color MAU_VIEN_DUOI_HEADER = new Color(226, 232, 240);
    private static final Color MAU_NEN_TRANG = new Color(246, 249, 253);
    private static final Color MAU_VIEN_THE = new Color(226, 233, 243);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(24, 58, 92);
    private static final Color MAU_CHU_MENU = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_NEN_MENU_ACTIVE = new Color(237, 244, 255);
    private static final Color MAU_VIEN_MENU_ACTIVE = new Color(212, 228, 255);
    private static final Color MAU_NEN_MENU_HOVER = new Color(244, 247, 252);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(245, 124, 48);
    private static final Color MAU_XANH_LAM = new Color(2, 132, 199);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_XANH_LA_MO = new Color(134, 207, 175);
    private static final Color MAU_NEN_DONG_CHON = new Color(255, 242, 230);
    private static final Color MAU_NEN_DANG_XUAT = new Color(255, 241, 242);
    private static final Color MAU_NEN_DANG_XUAT_HOVER = new Color(255, 228, 230);
    private static final Color MAU_VIEN_DANG_XUAT = new Color(254, 205, 211);
    private static final Color MAU_CHU_DANG_XUAT = new Color(225, 29, 72);
    private static final Color MAU_NEN_HDSD = new Color(241, 245, 249);
    private static final Color MAU_NEN_HDSD_HOVER = new Color(226, 232, 240);

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();
    private final ChiTietDungPhuTungBUS chiTietDungPhuTungBUS = new ChiTietDungPhuTungBUS();

    private BufferedImage logoHeader;

    private final CardLayout boCucManHinhCon = new CardLayout();
    private final JPanel vungNoiDungChinh = new JPanel(boCucManHinhCon);

    // Các nút danh mục trên thanh Header
    private NutDieuHuongHeader btnCaSuaPhanCong;
    private NutDieuHuongHeader btnVatTuPhuTung;
    private NutDieuHuongHeader btnNhatKyCongViec;

    private final Map<String, NutDieuHuongHeader> mapManHinhThuocNhom = new LinkedHashMap<>();
    private final List<MucMenuThaXuong> danhSachMucMenu = new ArrayList<>();
    private String maManHinhHienTai = MAN_PHIEU_PHAN_CONG;

    // Dữ liệu thực của Kỹ thuật viên (tuyệt đối không chèn dữ liệu giả)
    private List<PhieuPhanCong> danhSachPhanCongGoc = new ArrayList<>();
    private final List<PhieuPhanCong> danhSachPhanCongDangLoc = new ArrayList<>();
    private PhieuPhanCong phieuDangChon = null;

    // Thành phần giao diện màn hình Phiếu phân công
    private JLabel lblTongSoCa;
    private JLabel lblChoKiemTra;
    private JLabel lblDangThiCong;
    private JLabel lblDaHoanThanhHomNay;
    private JTextField txtTimKiem;
    private JComboBox<String> cboTrangThai;
    private String cheDoLocNgay = "TAT_CA";
    private LocalDate ngayLocCuThe = null;
    private JButton btnChonNgay;
    private JLabel lblHienThiSoPhieu;

    private DefaultTableModel modelBangPhanCong;
    private JTable tblPhanCong;

    // Thành phần bảng chi tiết & cập nhật tiến độ bên phải
    private JLabel lblMaPhieuChiTiet;
    private NhanVienThuocBadge badgeTrangThaiChiTiet;
    private JLabel lblBienSoChiTiet;
    private JLabel lblMauXeChiTiet;
    private JTextArea txtTrieuChungBanDau;
    private JTextArea txtKetLuanChanDoan;
    private NutHanhDongTienDo btnBatDauSua;
    private NutHanhDongTienDo btnHoanThanhSua;
    private JTextArea txtGhiChuThiCong;
    private DefaultTableModel modelBangPhuTung;
    private JTable tblPhuTungSuDung;

    public KyThuatVienManHinhChinh() {
        this(null);
    }

    public KyThuatVienManHinhChinh(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        taiLogoThuongHieu();
        khoiTaoGiaoDien();
        chuyenManHinh(MAN_PHIEU_PHAN_CONG);
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    private void khoiTaoGiaoDien() {
        setTitle("Pengu Garage - Phân hệ Kỹ thuật viên");
        setSize(new Dimension(1380, 800));
        setMinimumSize(new Dimension(1160, 680));
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

        // 1. Cụm bên trái: Logo + Tên thương hiệu + Danh mục chức năng Kỹ thuật viên + Nút ! (HDSD)
        JPanel cumTraiVaMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 11));
        cumTraiVaMenu.setOpaque(false);

        cumTraiVaMenu.add(taoCumThuongHieuTrai());
        cumTraiVaMenu.add(Box.createHorizontalStrut(28));

        // Danh mục 1: Ca sửa & Phân công
        btnCaSuaPhanCong = new NutDieuHuongHeader("Ca sửa & Phân công", LoaiBieuTuong.CAP_CONG_VIEC, true);
        JPopupMenu popupCaSua = taoPopupDanhMuc(
                "CA SỬA & PHÂN CÔNG",
                new ThongTinMucCon(MAN_PHIEU_PHAN_CONG, "Phiếu phân công", LoaiBieuTuong.CAP_CONG_VIEC),
                new ThongTinMucCon(MAN_KIEM_TRA, "Kiểm tra", LoaiBieuTuong.TIM_KIEM)
        );
        ganSuKienMoMenu(btnCaSuaPhanCong, popupCaSua);
        cumTraiVaMenu.add(btnCaSuaPhanCong);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 2: Vật tư & Phụ tùng
        btnVatTuPhuTung = new NutDieuHuongHeader("Vật tư & Phụ tùng", LoaiBieuTuong.THE_PHU_TUNG, true);
        JPopupMenu popupVatTu = taoPopupDanhMuc(
                "VẬT TƯ & PHỤ TÙNG",
                new ThongTinMucCon(MAN_TRA_CUU_PHU_TUNG, "Tra cứu phụ tùng", LoaiBieuTuong.THE_PHU_TUNG)
        );
        ganSuKienMoMenu(btnVatTuPhuTung, popupVatTu);
        cumTraiVaMenu.add(btnVatTuPhuTung);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Danh mục 3: Nhật ký công việc
        btnNhatKyCongViec = new NutDieuHuongHeader("Nhật ký công việc", LoaiBieuTuong.NHAT_KY, true);
        JPopupMenu popupNhatKy = taoPopupDanhMuc(
                "NHẬT KÝ CÔNG VIỆC",
                new ThongTinMucCon(MAN_LICH_SU_CA_SUA, "Lịch sử ca sửa hoàn tất", LoaiBieuTuong.NHAT_KY)
        );
        ganSuKienMoMenu(btnNhatKyCongViec, popupNhatKy);
        cumTraiVaMenu.add(btnNhatKyCongViec);
        cumTraiVaMenu.add(Box.createHorizontalStrut(10));

        // Nút ! (HDSD) đặt ngay kế bên danh mục cuối cùng trên Header
        NutHuongDanSuDung btnHDSD = new NutHuongDanSuDung();
        btnHDSD.addActionListener(e -> moHopThoaiHuongDanSuDung());
        cumTraiVaMenu.add(btnHDSD);

        mapManHinhThuocNhom.put(MAN_PHIEU_PHAN_CONG, btnCaSuaPhanCong);
        mapManHinhThuocNhom.put(MAN_KIEM_TRA, btnCaSuaPhanCong);
        mapManHinhThuocNhom.put(MAN_TRA_CUU_PHU_TUNG, btnVatTuPhuTung);
        mapManHinhThuocNhom.put(MAN_LICH_SU_CA_SUA, btnNhatKyCongViec);

        // 2. Cụm bên phải: Avatar + Tên tài khoản Kỹ thuật viên + Nút Đăng xuất
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
                chuyenManHinh(MAN_PHIEU_PHAN_CONG);
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
        lblNhom.setForeground(MAU_CHU_MO);
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
        vungNoiDungChinh.add(taoManHinhPhieuPhanCong(), MAN_PHIEU_PHAN_CONG);
        vungNoiDungChinh.add(taoKhungChucNang("Ca sửa & Phân công", "Kiểm tra"), MAN_KIEM_TRA);
        vungNoiDungChinh.add(taoKhungChucNang("Vật tư & Phụ tùng", "Tra cứu phụ tùng"), MAN_TRA_CUU_PHU_TUNG);
        vungNoiDungChinh.add(taoKhungChucNang("Nhật ký công việc", "Lịch sử ca sửa hoàn tất"), MAN_LICH_SU_CA_SUA);
    }

    /**
     * Xây dựng màn hình chính CA SỬA CHỮA & PHÂN CÔNG KỸ THUẬT theo đúng ảnh mẫu (không hiển thị dữ liệu giả).
     */
    private JPanel taoManHinhPhieuPhanCong() {
        JPanel trangChinh = new JPanel(new BorderLayout(0, 14));
        trangChinh.setBackground(MAU_NEN_TRANG);
        trangChinh.setBorder(new EmptyBorder(16, 22, 18, 22));

        // Khối trên cùng: Tiêu đề + 4 thẻ thống kê + Thanh tìm kiếm & bộ lọc
        JPanel khoiDauTrang = new JPanel();
        khoiDauTrang.setOpaque(false);
        khoiDauTrang.setLayout(new BoxLayout(khoiDauTrang, BoxLayout.Y_AXIS));

        // 1. Dòng tiêu đề trang + nút Làm mới
        JPanel dongTieuDe = new JPanel(new BorderLayout());
        dongTieuDe.setOpaque(false);

        JLabel lblTieuDeTrang = new JLabel("CA SỬA CHỮA & PHÂN CÔNG KỸ THUẬT");
        lblTieuDeTrang.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTieuDeTrang.setForeground(MAU_XANH_DAM);

        NutVienTrang btnLamMoi = new NutVienTrang("Làm mới", LoaiBieuTuong.LAM_MOI);
        btnLamMoi.addActionListener(e -> lamMoiDuLieuPhanCong());

        dongTieuDe.add(lblTieuDeTrang, BorderLayout.WEST);
        dongTieuDe.add(btnLamMoi, BorderLayout.EAST);

        // 2. Hàng 4 thẻ chỉ số ca sửa
        JPanel hangChiSo = new JPanel(new GridLayout(1, 4, 16, 0));
        hangChiSo.setOpaque(false);

        lblTongSoCa = new JLabel("0 ca");
        lblChoKiemTra = new JLabel("0 ca");
        lblDangThiCong = new JLabel("0 ca");
        lblDaHoanThanhHomNay = new JLabel("0 ca");

        hangChiSo.add(taoTheChiSoCa(
                "Tổng số ca phân công",
                lblTongSoCa,
                MAU_XANH_DAM,
                "Toàn bộ danh sách phân bổ",
                MAU_CHU_MO
        ));
        hangChiSo.add(taoTheChiSoCa(
                "Chờ kiểm tra / chẩn đoán",
                lblChoKiemTra,
                MAU_XANH_LAM,
                "Sẵn sàng nhận xe vào ca",
                new Color(56, 189, 248)
        ));
        hangChiSo.add(taoTheChiSoCa(
                "Đang thi công trên cầu",
                lblDangThiCong,
                MAU_CAM_CHU_DAO,
                "Cần tập trung hoàn thành",
                new Color(251, 146, 60)
        ));
        hangChiSo.add(taoTheChiSoCa(
                "Đã hoàn thành hôm nay",
                lblDaHoanThanhHomNay,
                MAU_XANH_LA,
                "Đã sẵn sàng rửa & giao xe",
                new Color(52, 211, 153)
        ));

        // 3. Thanh tìm kiếm và lọc
        TheBoGocTrang thanhBoLoc = taoThanhTimKiemVaLoc();

        khoiDauTrang.add(dongTieuDe);
        khoiDauTrang.add(Box.createVerticalStrut(12));
        khoiDauTrang.add(hangChiSo);
        khoiDauTrang.add(Box.createVerticalStrut(12));
        khoiDauTrang.add(thanhBoLoc);

        // Khối dưới: Bảng PHIẾU PHÂN CÔNG CỦA TÔI (trái) + Khung Chi tiết & Cập nhật tiến độ (phải)
        JPanel vungChiaDoi = new JPanel(new BoCucHaiCotTyLe(0.675, 16, 360));
        vungChiaDoi.setOpaque(false);

        vungChiaDoi.add(taoBangPhieuPhanCongCuaToi(), BorderLayout.CENTER);
        vungChiaDoi.add(taoKhungChiTietVaCapNhatTienDo(), BorderLayout.EAST);

        trangChinh.add(khoiDauTrang, BorderLayout.NORTH);
        trangChinh.add(vungChiaDoi, BorderLayout.CENTER);
        return trangChinh;
    }

    private TheBoGocTrang taoTheChiSoCa(String tieuDe, JLabel lblGiaTri, Color mauGiaTri,
                                        String phuDe, Color mauPhuDe) {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 4));
        the.setPreferredSize(new Dimension(0, 102));
        the.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(MAU_CHU_PHU);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblGiaTri.setForeground(mauGiaTri);

        JLabel lblPhuDe = new JLabel(phuDe);
        lblPhuDe.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPhuDe.setForeground(mauPhuDe);

        the.add(lblTieuDe, BorderLayout.NORTH);
        the.add(lblGiaTri, BorderLayout.CENTER);
        the.add(lblPhuDe, BorderLayout.SOUTH);
        return the;
    }

    private TheBoGocTrang taoThanhTimKiemVaLoc() {
        TheBoGocTrang thanh = new TheBoGocTrang(new BorderLayout());
        thanh.setPreferredSize(new Dimension(0, 56));
        thanh.setBorder(new EmptyBorder(9, 14, 9, 18));

        JPanel cumLocTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cumLocTrai.setOpaque(false);

        // Ô tìm kiếm có biểu tượng kính lúp và placeholder
        JPanel khungTim = new JPanel(new BorderLayout(6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D hinh = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
                g2.setColor(new Color(250, 251, 253));
                g2.fill(hinh);
                g2.setColor(new Color(218, 226, 238));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(hinh);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        khungTim.setOpaque(false);
        khungTim.setPreferredSize(new Dimension(250, 36));
        khungTim.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel iconTim = new JLabel(new BieuTuongVector(LoaiBieuTuong.TIM_KIEM, 14, MAU_CHU_MO));
        txtTimKiem = taoOTimKiemPlaceholder("Tìm theo biển số, mã phiếu, tên xe, chủ...");
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                apDungBoLocDanhSach();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                apDungBoLocDanhSach();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                apDungBoLocDanhSach();
            }
        });
        khungTim.add(iconTim, BorderLayout.WEST);
        khungTim.add(txtTimKiem, BorderLayout.CENTER);

        JLabel lblNhanTrangThai = new JLabel("Trạng thái:");
        lblNhanTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNhanTrangThai.setForeground(MAU_CHU_MENU);

        cboTrangThai = new JComboBox<>(new String[]{
                "Trạng thái",
                TrangThaiSua.CHO_THUC_HIEN.getTenHienThi(),
                TrangThaiSua.DANG_SUA.getTenHienThi(),
                TrangThaiSua.HOAN_THANH.getTenHienThi()
        });
        cboTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboTrangThai.setBackground(Color.WHITE);
        cboTrangThai.setPreferredSize(new Dimension(138, 36));
        cboTrangThai.addActionListener(e -> apDungBoLocDanhSach());

        JLabel lblNhanNgay = new JLabel("Ngày:");
        lblNhanNgay.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNhanNgay.setForeground(MAU_CHU_MENU);

        btnChonNgay = new JButton("Chọn ngày   ▼");
        btnChonNgay.setFocusPainted(false);
        btnChonNgay.setBackground(Color.WHITE);
        btnChonNgay.setForeground(MAU_CHU_PHU);
        btnChonNgay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChonNgay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 226, 238), 1, true),
                new EmptyBorder(0, 12, 0, 10)
        ));
        btnChonNgay.setPreferredSize(new Dimension(136, 36));
        btnChonNgay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popupLocNgay = taoMenuChonNgay();
        btnChonNgay.addActionListener(e -> popupLocNgay.show(btnChonNgay, 0, btnChonNgay.getHeight() + 4));

        cumLocTrai.add(khungTim);
        cumLocTrai.add(lblNhanTrangThai);
        cumLocTrai.add(cboTrangThai);
        cumLocTrai.add(lblNhanNgay);
        cumLocTrai.add(btnChonNgay);

        lblHienThiSoPhieu = new JLabel("Hiển thị 0 phiếu việc");
        lblHienThiSoPhieu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHienThiSoPhieu.setForeground(MAU_CHU_MENU);

        thanh.add(cumLocTrai, BorderLayout.WEST);
        thanh.add(lblHienThiSoPhieu, BorderLayout.EAST);
        return thanh;
    }

    private JPopupMenu taoMenuChonNgay() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem mucTatCa = new JMenuItem("Chọn ngày (Tất cả)");
        mucTatCa.addActionListener(e -> {
            cheDoLocNgay = "TAT_CA";
            ngayLocCuThe = null;
            btnChonNgay.setText("Chọn ngày   ▼");
            apDungBoLocDanhSach();
        });

        JMenuItem mucHomNay = new JMenuItem("Hôm nay (" + LocalDate.now().format(DINH_DANG_NGAY_LOC) + ")");
        mucHomNay.addActionListener(e -> {
            cheDoLocNgay = "HOM_NAY";
            ngayLocCuThe = null;
            btnChonNgay.setText(LocalDate.now().format(DINH_DANG_NGAY_LOC) + " ▼");
            apDungBoLocDanhSach();
        });

        JMenuItem muc7Ngay = new JMenuItem("7 ngày gần đây");
        muc7Ngay.addActionListener(e -> {
            cheDoLocNgay = "7_NGAY";
            ngayLocCuThe = null;
            btnChonNgay.setText("7 ngày qua ▼");
            apDungBoLocDanhSach();
        });

        JMenuItem mucChonLich = new JMenuItem("📅 Chọn ngày trên lịch...");
        mucChonLich.addActionListener(e -> {
            ChonNgayLichUtil.KetQuaChonNgay kq = ChonNgayLichUtil.hienThiLichChonNgay(
                    this, "Chọn Ngày Lọc Phiếu Phân Công", ngayLocCuThe != null ? ngayLocCuThe : LocalDate.now(), true
            );
            if (!kq.daXacNhan()) {
                return;
            }
            if (kq.xoaLoc()) {
                cheDoLocNgay = "TAT_CA";
                ngayLocCuThe = null;
                btnChonNgay.setText("Chọn ngày   ▼");
            } else if (kq.ngay() != null) {
                cheDoLocNgay = "NGAY_CU_THE";
                ngayLocCuThe = kq.ngay();
                btnChonNgay.setText(ngayLocCuThe.format(DINH_DANG_NGAY_LOC) + " ▼");
            }
            apDungBoLocDanhSach();
        });

        popup.add(mucTatCa);
        popup.add(mucHomNay);
        popup.add(muc7Ngay);
        popup.addSeparator();
        popup.add(mucChonLich);
        return popup;
    }

    private TheBoGocTrang taoBangPhieuPhanCongCuaToi() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 12));
        the.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTieuDe = new JLabel(
                "PHIẾU PHÂN CÔNG CỦA TÔI",
                new BieuTuongVector(LoaiBieuTuong.BANG_PHAN_CONG, 15, MAU_XANH_DAM),
                SwingConstants.LEFT
        );
        lblTieuDe.setIconTextGap(8);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        String[] cot = {
                "STT", "Mã Phân Công", "Biển Số Xe", "Mẫu Xe",
                "Hạng Mục Sửa Chữa", "Thời Gian", "Trạng Thái"
        };
        modelBangPhanCong = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhanCong = new JTable(modelBangPhanCong);
        tblPhanCong.setRowHeight(38);
        tblPhanCong.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPhanCong.setShowVerticalLines(true);
        tblPhanCong.setShowHorizontalLines(true);
        tblPhanCong.setGridColor(new Color(238, 242, 248));
        tblPhanCong.setIntercellSpacing(new Dimension(1, 1));
        tblPhanCong.setFillsViewportHeight(true);
        tblPhanCong.setBackground(Color.WHITE);
        tblPhanCong.setSelectionBackground(MAU_NEN_DONG_CHON);
        tblPhanCong.setSelectionForeground(MAU_XANH_DAM);

        cauhinhDauBang(tblPhanCong);
        tblPhanCong.getColumnModel().getColumn(0).setMaxWidth(48);
        tblPhanCong.getColumnModel().getColumn(0).setPreferredWidth(44);
        tblPhanCong.getColumnModel().getColumn(1).setPreferredWidth(155);
        tblPhanCong.getColumnModel().getColumn(2).setPreferredWidth(105);
        tblPhanCong.getColumnModel().getColumn(3).setPreferredWidth(115);
        tblPhanCong.getColumnModel().getColumn(4).setPreferredWidth(210);
        tblPhanCong.getColumnModel().getColumn(5).setPreferredWidth(105);
        tblPhanCong.getColumnModel().getColumn(6).setPreferredWidth(115);

        DefaultTableCellRenderer cellThuong = new RendererDongPhanCong(false);
        DefaultTableCellRenderer cellCanGiua = new RendererDongPhanCong(true);
        tblPhanCong.getColumnModel().getColumn(0).setCellRenderer(cellCanGiua);
        for (int i = 1; i <= 5; i++) {
            tblPhanCong.getColumnModel().getColumn(i).setCellRenderer(cellThuong);
        }
        tblPhanCong.getColumnModel().getColumn(6).setCellRenderer(new RendererTrangThaiPhanCong());

        tblPhanCong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int dong = tblPhanCong.getSelectedRow();
                if (dong >= 0 && dong < danhSachPhanCongDangLoc.size()) {
                    hienThiChiTietPhieu(danhSachPhanCongDangLoc.get(dong));
                } else {
                    hienThiChiTietPhieu(null);
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(tblPhanCong);
        cuonBang.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(lblTieuDe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    private TheBoGocTrang taoKhungChiTietVaCapNhatTienDo() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout());
        the.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel noiDung = new JPanel();
        noiDung.setOpaque(false);
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));

        // 1. Tiêu đề mã phiếu & nhãn trạng thái
        JPanel dauChiTiet = new JPanel(new BorderLayout(8, 2));
        dauChiTiet.setOpaque(false);
        dauChiTiet.setAlignmentX(Component.LEFT_ALIGNMENT);
        dauChiTiet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel cumTieuDePhieu = new JPanel();
        cumTieuDePhieu.setOpaque(false);
        cumTieuDePhieu.setLayout(new BoxLayout(cumTieuDePhieu, BoxLayout.Y_AXIS));

        lblMaPhieuChiTiet = new JLabel("PHIẾU: —");
        lblMaPhieuChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMaPhieuChiTiet.setForeground(MAU_XANH_DAM);

        JLabel lblMoTaPhu = new JLabel("Thông tin phương tiện & bảng điều khiển tiến độ");
        lblMoTaPhu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMoTaPhu.setForeground(MAU_CHU_MO);

        cumTieuDePhieu.add(lblMaPhieuChiTiet);
        cumTieuDePhieu.add(lblMoTaPhu);

        badgeTrangThaiChiTiet = new NhanVienThuocBadge(
                "CHƯA CHỌN",
                new Color(241, 245, 249),
                new Color(226, 232, 240),
                MAU_CHU_PHU
        );
        JPanel bocBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        bocBadge.setOpaque(false);
        bocBadge.add(badgeTrangThaiChiTiet);

        dauChiTiet.add(cumTieuDePhieu, BorderLayout.WEST);
        dauChiTiet.add(bocBadge, BorderLayout.EAST);

        // 2. Dải tóm tắt Biển số & Mẫu xe
        JPanel khungXe = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D r = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
                g2.setColor(new Color(248, 250, 253));
                g2.fill(r);
                g2.setColor(new Color(226, 232, 242));
                g2.draw(r);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        khungXe.setOpaque(false);
        khungXe.setBorder(new EmptyBorder(8, 12, 8, 12));
        khungXe.setAlignmentX(Component.LEFT_ALIGNMENT);
        khungXe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel traiXe = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        traiXe.setOpaque(false);
        JLabel lblNhanBS = new JLabel("Biển số:");
        lblNhanBS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNhanBS.setForeground(MAU_CHU_PHU);
        lblBienSoChiTiet = new JLabel("—");
        lblBienSoChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBienSoChiTiet.setForeground(MAU_XANH_DAM);
        traiXe.add(lblNhanBS);
        traiXe.add(lblBienSoChiTiet);

        JPanel phaiXe = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        phaiXe.setOpaque(false);
        JLabel lblNhanMX = new JLabel("Mẫu xe:");
        lblNhanMX.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNhanMX.setForeground(MAU_CHU_PHU);
        lblMauXeChiTiet = new JLabel("—");
        lblMauXeChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMauXeChiTiet.setForeground(MAU_XANH_DAM);
        phaiXe.add(lblNhanMX);
        phaiXe.add(lblMauXeChiTiet);

        khungXe.add(traiXe, BorderLayout.WEST);
        khungXe.add(phaiXe, BorderLayout.EAST);

        // 3. Khối KIỂM TRA & CHẨN ĐOÁN LỖI XE
        JLabel lblMucChanDoan = taoNhanMucNho("KIỂM TRA & CHẨN ĐOÁN LỖI XE", LoaiBieuTuong.TIM_KIEM);

        JLabel lblNhanTrieuChung = taoNhanTruongPhu("Triệu chứng ban đầu khi tiếp nhận:");
        txtTrieuChungBanDau = taoVungVanBan("", false, new Color(248, 250, 253), new Color(226, 232, 240));
        JScrollPane cuonTrieuChung = bocScrollVungVanBan(txtTrieuChungBanDau, 46, new Color(226, 232, 240));

        JLabel lblNhanKetLuan = taoNhanTruongPhu("Kết luận chẩn đoán thực tế của kỹ thuật viên:");
        txtKetLuanChanDoan = taoVungVanBan("Nhập kết luận kiểm tra thực tế...", true, Color.WHITE, new Color(100, 116, 139));
        JScrollPane cuonKetLuan = bocScrollVungVanBan(txtKetLuanChanDoan, 56, new Color(120, 134, 152));

        // 4. Khối CẬP NHẬT TIẾN ĐỘ CA SỬA
        JLabel lblMucTienDo = taoNhanMucNho("CẬP NHẬT TIẾN ĐỘ CA SỬA", LoaiBieuTuong.BANH_RANG);

        JPanel hangNutTienDo = new JPanel(new GridLayout(1, 2, 10, 0));
        hangNutTienDo.setOpaque(false);
        hangNutTienDo.setAlignmentX(Component.LEFT_ALIGNMENT);
        hangNutTienDo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnBatDauSua = new NutHanhDongTienDo("▶  Bắt đầu sửa", MAU_CAM_CHU_DAO, MAU_CAM_HOVER, new Color(245, 180, 140));
        btnBatDauSua.addActionListener(e -> xuLyBatDauSua());

        btnHoanThanhSua = new NutHanhDongTienDo("✓  Hoàn thành", MAU_XANH_LA, new Color(5, 150, 105), MAU_XANH_LA_MO);
        btnHoanThanhSua.addActionListener(e -> xuLyHoanThanhSua());

        hangNutTienDo.add(btnBatDauSua);
        hangNutTienDo.add(btnHoanThanhSua);

        JLabel lblNhanGhiChu = taoNhanTruongPhu("Ghi chú thi công / Biên bản bàn giao:");

        JPanel dongGhiChu = new JPanel(new BorderLayout(8, 0));
        dongGhiChu.setOpaque(false);
        dongGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);
        dongGhiChu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        txtGhiChuThiCong = taoVungVanBan("Ghi nhận lưu ý hoặc tiến độ thực hiện...", true, Color.WHITE, new Color(120, 134, 152));
        JScrollPane cuonGhiChu = bocScrollVungVanBan(txtGhiChuThiCong, 50, new Color(120, 134, 152));

        JButton btnTuyChonGhiChu = new JButton("...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_XANH_DAM_HOVER : MAU_XANH_DAM);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8f, 8f));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnTuyChonGhiChu.setFocusPainted(false);
        btnTuyChonGhiChu.setBorderPainted(false);
        btnTuyChonGhiChu.setContentAreaFilled(false);
        btnTuyChonGhiChu.setForeground(Color.WHITE);
        btnTuyChonGhiChu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTuyChonGhiChu.setPreferredSize(new Dimension(40, 38));
        btnTuyChonGhiChu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTuyChonGhiChu.setToolTipText(" Tùy chọn ghi chú nhanh");
        ganMenuGhiChuNhanh(btnTuyChonGhiChu);

        JPanel bocNutBaCham = new JPanel(new BorderLayout());
        bocNutBaCham.setOpaque(false);
        bocNutBaCham.add(btnTuyChonGhiChu, BorderLayout.NORTH);

        dongGhiChu.add(cuonGhiChu, BorderLayout.CENTER);
        dongGhiChu.add(bocNutBaCham, BorderLayout.EAST);

        // 5. Khối VẬT TƯ & PHỤ TÙNG SỬ DỤNG
        JPanel dongTieuDeVatTu = new JPanel(new BorderLayout());
        dongTieuDeVatTu.setOpaque(false);
        dongTieuDeVatTu.setAlignmentX(Component.LEFT_ALIGNMENT);
        dongTieuDeVatTu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lblMucVatTu = taoNhanMucNho("VẬT TƯ & PHỤ TÙNG SỬ DỤNG", LoaiBieuTuong.CAP_CONG_VIEC);
        JLabel lblCoDinhBaoGia = new JLabel("(Cố định theo báo giá đã duyệt)");
        lblCoDinhBaoGia.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCoDinhBaoGia.setForeground(MAU_CHU_MO);

        dongTieuDeVatTu.add(lblMucVatTu, BorderLayout.WEST);
        dongTieuDeVatTu.add(lblCoDinhBaoGia, BorderLayout.EAST);

        String[] cotPhuTung = {"#", "Tên Phụ Tùng", "Mã SP", "SL", "Trạng Thái"};
        modelBangPhuTung = new DefaultTableModel(cotPhuTung, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPhuTungSuDung = new JTable(modelBangPhuTung);
        tblPhuTungSuDung.setRowHeight(30);
        tblPhuTungSuDung.setGridColor(new Color(238, 242, 248));
        tblPhuTungSuDung.setFillsViewportHeight(true);
        tblPhuTungSuDung.setBackground(Color.WHITE);
        cauhinhDauBang(tblPhuTungSuDung);
        tblPhuTungSuDung.getColumnModel().getColumn(0).setMaxWidth(34);
        tblPhuTungSuDung.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblPhuTungSuDung.getColumnModel().getColumn(3).setMaxWidth(42);
        tblPhuTungSuDung.getColumnModel().getColumn(4).setPreferredWidth(78);

        JScrollPane cuonPhuTung = new JScrollPane(tblPhuTungSuDung);
        cuonPhuTung.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuonPhuTung.setPreferredSize(new Dimension(0, 125));
        cuonPhuTung.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        cuonPhuTung.getViewport().setBackground(Color.WHITE);

        // Ghép toàn bộ thành phần vào khung phải
        noiDung.add(dauChiTiet);
        noiDung.add(Box.createVerticalStrut(10));
        noiDung.add(khungXe);
        noiDung.add(Box.createVerticalStrut(14));
        noiDung.add(lblMucChanDoan);
        noiDung.add(Box.createVerticalStrut(6));
        noiDung.add(lblNhanTrieuChung);
        noiDung.add(Box.createVerticalStrut(4));
        noiDung.add(cuonTrieuChung);
        noiDung.add(Box.createVerticalStrut(8));
        noiDung.add(lblNhanKetLuan);
        noiDung.add(Box.createVerticalStrut(4));
        noiDung.add(cuonKetLuan);
        noiDung.add(Box.createVerticalStrut(14));
        noiDung.add(lblMucTienDo);
        noiDung.add(Box.createVerticalStrut(8));
        noiDung.add(hangNutTienDo);
        noiDung.add(Box.createVerticalStrut(8));
        noiDung.add(lblNhanGhiChu);
        noiDung.add(Box.createVerticalStrut(4));
        noiDung.add(dongGhiChu);
        noiDung.add(Box.createVerticalStrut(14));
        noiDung.add(dongTieuDeVatTu);
        noiDung.add(Box.createVerticalStrut(6));

        JPanel bocTong = new JPanel(new BorderLayout());
        bocTong.setOpaque(false);
        bocTong.add(noiDung, BorderLayout.NORTH);
        bocTong.add(cuonPhuTung, BorderLayout.CENTER);

        the.add(bocTong, BorderLayout.CENTER);
        hienThiChiTietPhieu(null);
        return the;
    }

    private void ganMenuGhiChuNhanh(JButton nut) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem xoaTrang = new JMenuItem("Xóa trắng ô ghi chú thi công");
        xoaTrang.addActionListener(e -> txtGhiChuThiCong.setText(""));

        JMenuItem saoChepChanDoan = new JMenuItem("Chèn nội dung từ kết luận chẩn đoán");
        saoChepChanDoan.addActionListener(e -> {
            String kl = txtKetLuanChanDoan.getText().trim();
            if (!kl.isEmpty()) {
                txtGhiChuThiCong.setText(kl);
            }
        });
        menu.add(saoChepChanDoan);
        menu.add(xoaTrang);
        nut.addActionListener(e -> menu.show(nut, 0, nut.getHeight() + 4));
    }

    /**
     * Nạp dữ liệu thực tế chỉ thuộc về Kỹ thuật viên đang đăng nhập (tuyệt đối không hiển thị dữ liệu giả hoặc của người khác).
     */
    public void lamMoiDuLieuPhanCong() {
        try {
            NhanVien ktv = taiKhoanDangNhap != null ? taiKhoanDangNhap.getMaNhanVien() : null;
            if (ktv != null && ktv.getMaNhanVien() != null) {
                danhSachPhanCongGoc = phieuPhanCongBUS.layTheoNhanVien(ktv.getMaNhanVien());
            } else {
                danhSachPhanCongGoc = Collections.emptyList();
            }
        } catch (RuntimeException ex) {
            danhSachPhanCongGoc = Collections.emptyList();
        }

        capNhatChiSoThongKe();
        apDungBoLocDanhSach();
    }

    private void capNhatChiSoThongKe() {
        LocalDate homNay = LocalDate.now();
        int tongCa = danhSachPhanCongGoc.size();
        int choKiemTra = 0;
        int dangThiCong = 0;
        int hoanThanhHomNay = 0;

        for (PhieuPhanCong ppc : danhSachPhanCongGoc) {
            TrangThaiSua tt = ppc.getTrangThaiSua();
            if (tt == TrangThaiSua.CHO_THUC_HIEN) {
                choKiemTra++;
            } else if (tt == TrangThaiSua.DANG_SUA) {
                dangThiCong++;
            } else if (tt == TrangThaiSua.HOAN_THANH) {
                LocalDateTime moc = ppc.getThoiGianHoanThanh() != null
                        ? ppc.getThoiGianHoanThanh()
                        : ppc.getThoiGianPhanCong();
                if (moc != null && moc.toLocalDate().equals(homNay)) {
                    hoanThanhHomNay++;
                }
            }
        }

        lblTongSoCa.setText(tongCa + " ca");
        lblChoKiemTra.setText(choKiemTra + " ca");
        lblDangThiCong.setText(dangThiCong + " ca");
        lblDaHoanThanhHomNay.setText(hoanThanhHomNay + " ca");
    }

    private void apDungBoLocDanhSach() {
        String tuKhoa = txtTimKiem != null && txtTimKiem.getText() != null
                ? txtTimKiem.getText().trim().toLowerCase(Locale.ROOT)
                : "";
        int chiMucTrangThai = cboTrangThai != null ? cboTrangThai.getSelectedIndex() : 0;
        LocalDate homNay = LocalDate.now();

        String maCuDangChon = phieuDangChon != null ? phieuDangChon.getMaPhanCong() : null;
        danhSachPhanCongDangLoc.clear();

        for (PhieuPhanCong ppc : danhSachPhanCongGoc) {
            // 1. Lọc theo trạng thái
            if (chiMucTrangThai == 1 && ppc.getTrangThaiSua() != TrangThaiSua.CHO_THUC_HIEN) {
                continue;
            }
            if (chiMucTrangThai == 2 && ppc.getTrangThaiSua() != TrangThaiSua.DANG_SUA) {
                continue;
            }
            if (chiMucTrangThai == 3 && ppc.getTrangThaiSua() != TrangThaiSua.HOAN_THANH) {
                continue;
            }

            // 2. Lọc theo ngày
            LocalDateTime mocThoiGian = layMocThoiGianHienThi(ppc);
            if ("HOM_NAY".equals(cheDoLocNgay)) {
                if (mocThoiGian == null || !mocThoiGian.toLocalDate().equals(homNay)) {
                    continue;
                }
            } else if ("7_NGAY".equals(cheDoLocNgay)) {
                if (mocThoiGian == null || mocThoiGian.toLocalDate().isBefore(homNay.minusDays(6))) {
                    continue;
                }
            } else if ("NGAY_CU_THE".equals(cheDoLocNgay)) {
                if (mocThoiGian == null || ngayLocCuThe == null || !mocThoiGian.toLocalDate().equals(ngayLocCuThe)) {
                    continue;
                }
            }

            // 3. Lọc theo từ khóa tìm kiếm (biển số, mã phiếu, mẫu xe, tên khách hàng, hạng mục)
            if (!tuKhoa.isEmpty() && !khopTuKhoa(ppc, tuKhoa)) {
                continue;
            }

            danhSachPhanCongDangLoc.add(ppc);
        }

        lblHienThiSoPhieu.setText("Hiển thị " + danhSachPhanCongDangLoc.size() + " phiếu việc");

        modelBangPhanCong.setRowCount(0);
        int viTriChonLai = -1;

        for (int i = 0; i < danhSachPhanCongDangLoc.size(); i++) {
            PhieuPhanCong ppc = danhSachPhanCongDangLoc.get(i);
            PhieuTiepNhan ptn = ppc.layPhieuTiepNhan();
            PhuongTien pt = ptn != null ? ptn.getPhuongTien() : null;
            ChiTietBaoGia ctbg = ppc.getChiTietBaoGia();
            DichVu dv = ctbg != null ? ctbg.getDichVu() : null;
            LocalDateTime tg = layMocThoiGianHienThi(ppc);

            modelBangPhanCong.addRow(new Object[]{
                    i + 1,
                    ppc.getMaPhanCong(),
                    pt != null ? pt.getBienSoXe() : "—",
                    pt != null ? pt.getMauXe() : "—",
                    dv != null ? dv.getTenDichVu() : "—",
                    tg != null ? tg.format(DINH_DANG_THOI_GIAN) : "—",
                    ppc.getTrangThaiSua() != null ? ppc.getTrangThaiSua().getTenHienThi() : "—"
            });

            if (maCuDangChon != null && maCuDangChon.equals(ppc.getMaPhanCong())) {
                viTriChonLai = i;
            }
        }

        if (viTriChonLai >= 0) {
            tblPhanCong.setRowSelectionInterval(viTriChonLai, viTriChonLai);
        } else if (!danhSachPhanCongDangLoc.isEmpty()) {
            tblPhanCong.setRowSelectionInterval(0, 0);
        } else {
            hienThiChiTietPhieu(null);
        }
    }

    private boolean khopTuKhoa(PhieuPhanCong ppc, String tuKhoa) {
        PhieuTiepNhan ptn = ppc.layPhieuTiepNhan();
        PhuongTien pt = ptn != null ? ptn.getPhuongTien() : null;
        KhachHang kh = pt != null ? pt.getKhachHang() : null;
        ChiTietBaoGia ctbg = ppc.getChiTietBaoGia();
        DichVu dv = ctbg != null ? ctbg.getDichVu() : null;

        String gop = (ppc.getMaPhanCong() + " "
                + (pt != null ? pt.getBienSoXe() + " " + pt.getMauXe() : "") + " "
                + (kh != null ? kh.getHoTen() : "") + " "
                + (dv != null ? dv.getTenDichVu() : "")).toLowerCase(Locale.ROOT);
        return gop.contains(tuKhoa);
    }

    private LocalDateTime layMocThoiGianHienThi(PhieuPhanCong ppc) {
        if (ppc.getThoiGianHoanThanh() != null) {
            return ppc.getThoiGianHoanThanh();
        }
        if (ppc.getThoiGianBatDau() != null) {
            return ppc.getThoiGianBatDau();
        }
        return ppc.getThoiGianPhanCong();
    }

    private void hienThiChiTietPhieu(PhieuPhanCong ppc) {
        this.phieuDangChon = ppc;
        modelBangPhuTung.setRowCount(0);

        if (ppc == null) {
            lblMaPhieuChiTiet.setText("PHIẾU: —");
            badgeTrangThaiChiTiet.capNhat(
                    "CHƯA CHỌN",
                    new Color(241, 245, 249),
                    new Color(226, 232, 240),
                    MAU_CHU_PHU
            );
            lblBienSoChiTiet.setText("—");
            lblMauXeChiTiet.setText("—");
            txtTrieuChungBanDau.setText("");
            txtKetLuanChanDoan.setText("");
            txtKetLuanChanDoan.setEditable(false);
            txtGhiChuThiCong.setText("");
            txtGhiChuThiCong.setEditable(false);
            btnBatDauSua.setEnabled(false);
            btnHoanThanhSua.setEnabled(false);
            return;
        }

        PhieuTiepNhan ptn = ppc.layPhieuTiepNhan();
        PhuongTien pt = ptn != null ? ptn.getPhuongTien() : null;
        ChiTietBaoGia ctbg = ppc.getChiTietBaoGia();
        TrangThaiSua tt = ppc.getTrangThaiSua();

        lblMaPhieuChiTiet.setText("PHIẾU: " + ppc.getMaPhanCong());
        capNhatBadgeTrangThaiChiTiet(tt);

        lblBienSoChiTiet.setText(pt != null ? pt.getBienSoXe() : "—");
        lblMauXeChiTiet.setText(pt != null ? pt.getMauXe() : "—");
        txtTrieuChungBanDau.setText(ptn != null && ptn.getYeuCauKhachHang() != null ? ptn.getYeuCauKhachHang() : "");
        txtKetLuanChanDoan.setText(ctbg != null && ctbg.getGhiChu() != null ? ctbg.getGhiChu() : "");
        txtGhiChuThiCong.setText(ppc.getGhiChu() != null ? ppc.getGhiChu() : "");

        boolean choPhepCapNhat = tt != TrangThaiSua.HOAN_THANH;
        txtKetLuanChanDoan.setEditable(choPhepCapNhat);
        txtGhiChuThiCong.setEditable(choPhepCapNhat);

        btnBatDauSua.setEnabled(tt == TrangThaiSua.CHO_THUC_HIEN);
        btnHoanThanhSua.setEnabled(tt == TrangThaiSua.CHO_THUC_HIEN || tt == TrangThaiSua.DANG_SUA);

        // Nạp danh sách vật tư & phụ tùng sử dụng thực tế gắn với hạng mục báo giá
        if (ctbg != null) {
            List<ChiTietDungPhuTung> dsPhuTung = ctbg.getDanhSachPhuTung();
            if ((dsPhuTung == null || dsPhuTung.isEmpty())
                    && ctbg.getPhieuBaoGia() != null && ctbg.getDichVu() != null) {
                try {
                    dsPhuTung = chiTietDungPhuTungBUS.layTheoChiTietBaoGia(ctbg);
                } catch (RuntimeException ignored) {
                    dsPhuTung = Collections.emptyList();
                }
            }
            if (dsPhuTung != null) {
                for (int i = 0; i < dsPhuTung.size(); i++) {
                    ChiTietDungPhuTung dpt = dsPhuTung.get(i);
                    SanPham sp = dpt.getSanPham();
                    modelBangPhuTung.addRow(new Object[]{
                            i + 1,
                            sp != null ? sp.getTenSanPham() : "—",
                            sp != null ? sp.getMaSanPham() : "—",
                            dpt.getSoLuong(),
                            dpt.isDaTruKho() ? "Đã xuất kho" : "Dự kiến"
                    });
                }
            }
        }
    }

    private void capNhatBadgeTrangThaiChiTiet(TrangThaiSua tt) {
        if (tt == TrangThaiSua.DANG_SUA) {
            badgeTrangThaiChiTiet.capNhat(
                    "ĐANG SỬA",
                    new Color(255, 237, 213),
                    new Color(254, 215, 170),
                    MAU_CAM_CHU_DAO
            );
        } else if (tt == TrangThaiSua.HOAN_THANH) {
            badgeTrangThaiChiTiet.capNhat(
                    "HOÀN THÀNH",
                    new Color(209, 250, 229),
                    new Color(167, 243, 208),
                    new Color(4, 120, 87)
            );
        } else {
            badgeTrangThaiChiTiet.capNhat(
                    "CHỜ THỰC HIỆN",
                    new Color(235, 242, 250),
                    new Color(215, 226, 240),
                    MAU_XANH_DAM
            );
        }
    }

    private void xuLyBatDauSua() {
        if (phieuDangChon == null) {
            return;
        }
        try {
            boolean ok = phieuPhanCongBUS.batDauSua(phieuDangChon.getMaPhanCong());
            if (ok) {
                lamMoiDuLieuPhanCong();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Không thể bắt đầu ca sửa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void xuLyHoanThanhSua() {
        if (phieuDangChon == null) {
            return;
        }
        String ghiChuThiCong = txtGhiChuThiCong.getText() != null ? txtGhiChuThiCong.getText().trim() : "";
        String ketLuan = txtKetLuanChanDoan.getText() != null ? txtKetLuanChanDoan.getText().trim() : "";
        String ghiChuLuu = !ghiChuThiCong.isEmpty() ? ghiChuThiCong : ketLuan;

        try {
            boolean ok = phieuPhanCongBUS.hoanThanhSua(phieuDangChon.getMaPhanCong(), ghiChuLuu);
            if (ok) {
                lamMoiDuLieuPhanCong();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Không thể hoàn thành ca sửa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void cauhinhDauBang(JTable bang) {
        JTableHeader header = bang.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 34));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(224, 231, 241));
                lbl.setForeground(MAU_XANH_DAM);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(210, 219, 232)),
                        new EmptyBorder(0, 10, 0, 8)
                ));
                lbl.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    private JLabel taoNhanMucNho(String tieuDe, LoaiBieuTuong icon) {
        JLabel lbl = new JLabel(tieuDe, new BieuTuongVector(icon, 13, MAU_XANH_DAM), SwingConstants.LEFT);
        lbl.setIconTextGap(6);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MAU_XANH_DAM);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel taoNhanTruongPhu(String vanBan) {
        JLabel lbl = new JLabel(vanBan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(MAU_CHU_PHU);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextArea taoVungVanBan(String placeholder, boolean choChinhSua, Color mauNen, Color mauChuPlaceholder) {
        JTextArea area = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g2.setColor(MAU_CHU_MO);
                    g2.setFont(getFont());
                    g2.drawString(placeholder, getInsets().left, getInsets().top + g2.getFontMetrics().getAscent());
                    g2.dispose();
                }
            }
        };
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(choChinhSua);
        area.setBackground(mauNen);
        area.setForeground(MAU_CHU_MENU);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBorder(new EmptyBorder(6, 8, 6, 8));
        return area;
    }

    private JScrollPane bocScrollVungVanBan(JTextArea area, int chieuCao, Color mauVien) {
        JScrollPane sp = new JScrollPane(area);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setPreferredSize(new Dimension(0, chieuCao));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, chieuCao));
        sp.setBorder(BorderFactory.createLineBorder(mauVien, 1, true));
        return sp;
    }

    private JTextField taoOTimKiemPlaceholder(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g2.setColor(MAU_CHU_MO);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, 2, y);
                    g2.dispose();
                }
            }
        };
        tf.setOpaque(false);
        tf.setBorder(null);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(MAU_CHU_MENU);
        return tf;
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

    public void datManHinhCon(String maManHinh, JPanel panelMoi) {
        vungNoiDungChinh.add(panelMoi, maManHinh);
        if (maManHinh.equals(maManHinhHienTai)) {
            boCucManHinhCon.show(vungNoiDungChinh, maManHinh);
        }
    }

    public void chuyenManHinh(String maManHinh) {
        this.maManHinhHienTai = maManHinh;
        if (MAN_PHIEU_PHAN_CONG.equals(maManHinh)) {
            lamMoiDuLieuPhanCong();
        }
        boCucManHinhCon.show(vungNoiDungChinh, maManHinh);

        NutDieuHuongHeader nutChaActive = mapManHinhThuocNhom.get(maManHinh);
        btnCaSuaPhanCong.setDangChon(nutChaActive == btnCaSuaPhanCong);
        btnVatTuPhuTung.setDangChon(nutChaActive == btnVatTuPhuTung);
        btnNhatKyCongViec.setDangChon(nutChaActive == btnNhatKyCongViec);

        for (MucMenuThaXuong item : danhSachMucMenu) {
            item.setDangChon(maManHinh.equals(item.getMaManHinh()));
        }
    }

    private void xuLyDangXuat() {
        dispose();
        new ManHinhDangNhap().setVisible(true);
    }

    private void moHopThoaiHuongDanSuDung() {
        JDialog dialog = new JDialog(this, "Hướng dẫn sử dụng (HDSD) - Phân hệ Kỹ thuật viên", true);
        dialog.setSize(620, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel dauDialog = new JPanel(new BorderLayout());
        dauDialog.setBackground(MAU_XANH_DAM);
        dauDialog.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel lblTieuDe = new JLabel("HƯỚNG DẪN SỬ DỤNG PHÂN HỆ KỸ THUẬT VIÊN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(Color.WHITE);
        dauDialog.add(lblTieuDe, BorderLayout.WEST);

        String noiDungHtml = """
                <html>
                <body style='font-family: Segoe UI; font-size: 11px; color: #1E293B; padding: 14px 18px; line-height: 1.55;'>
                    <p style='margin-top:0;'>Chào mừng bạn đến với <b>Pengu Garage - Phân hệ Kỹ thuật viên</b>:</p>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>1. Ca sửa &amp; Phân công</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Phiếu phân công:</b> Xem danh sách các hạng mục sửa chữa được phân công cho bạn, lọc theo trạng thái/ngày, xem triệu chứng tiếp nhận, bấm <b>Bắt đầu sửa</b> khi đưa xe lên cầu và <b>Hoàn thành</b> khi thi công xong.</li>
                        <li><b>Kiểm tra:</b> Ghi nhận chẩn đoán lỗi xe và đề xuất hạng mục dịch vụ/phụ tùng cho báo giá.</li>
                    </ul>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>2. Vật tư &amp; Phụ tùng</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Tra cứu phụ tùng:</b> Kiểm tra nhanh mã sản phẩm, thông số và số lượng tồn kho phụ tùng tại xưởng.</li>
                    </ul>
                    <h4 style='color:#0F2942; margin-bottom:4px;'>3. Nhật ký công việc</h4>
                    <ul style='margin-top:2px;'>
                        <li><b>Lịch sử ca sửa hoàn tất:</b> Tra cứu các phiếu phân công đã thực hiện hoàn thành theo thời gian.</li>
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
        URL url = KyThuatVienManHinhChinh.class.getResource("/img/logo_pengu-garage.png");
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
        CAP_CONG_VIEC,
        TIM_KIEM,
        THE_PHU_TUNG,
        NHAT_KY,
        LAM_MOI,
        BANG_PHAN_CONG,
        BANH_RANG
    }

    private static class BoCucHaiCotTyLe implements LayoutManager {
        private final double tyLeTrai;
        private final int khoangCach;
        private final int rongToiThieuPhai;
        private Component trai;
        private Component phai;

        BoCucHaiCotTyLe(double tyLeTrai, int khoangCach, int rongToiThieuPhai) {
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
            return new Dimension(1200, 480);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(900, 340);
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
            RoundRectangle2D khung = new RoundRectangle2D.Float(0.75f, 0.75f, w, h, 14f, 14f);

            g2.setColor(new Color(15, 23, 42, 6));
            g2.fill(new RoundRectangle2D.Float(1.5f, 2.5f, w - 1f, h - 1f, 14f, 14f));

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
     * Renderer dòng bảng Phiếu phân công: tự động in đậm trên nền cam đào nhạt khi đang được chọn giống ảnh mẫu.
     */
    private static class RendererDongPhanCong extends DefaultTableCellRenderer {
        private final boolean canGiua;

        RendererDongPhanCong(boolean canGiua) {
            this.canGiua = canGiua;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            lbl.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 12));
            lbl.setForeground(isSelected ? MAU_XANH_DAM : MAU_CHU_MENU);
            lbl.setBackground(isSelected ? MAU_NEN_DONG_CHON : Color.WHITE);
            lbl.setHorizontalAlignment(canGiua ? SwingConstants.CENTER : SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(0, canGiua ? 0 : 10, 0, 8));
            return lbl;
        }
    }

    /**
     * Renderer cột Trạng Thái dưới dạng nhãn viên thuốc bo tròn màu sắc theo đúng ảnh mẫu.
     */
    private static class RendererTrangThaiPhanCong extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JPanel boc = new JPanel(new GridBagLayout());
            boc.setBackground(isSelected ? MAU_NEN_DONG_CHON : Color.WHITE);

            String chuoi = value != null ? value.toString() : "";
            Color nenBadge;
            Color vienBadge;
            Color chuBadge;

            if (TrangThaiSua.HOAN_THANH.getTenHienThi().equalsIgnoreCase(chuoi)) {
                nenBadge = new Color(209, 250, 229);
                vienBadge = new Color(167, 243, 208);
                chuBadge = new Color(4, 120, 87);
            } else if (TrangThaiSua.DANG_SUA.getTenHienThi().equalsIgnoreCase(chuoi)) {
                nenBadge = new Color(255, 237, 213);
                vienBadge = new Color(254, 215, 170);
                chuBadge = MAU_CAM_CHU_DAO;
            } else {
                nenBadge = new Color(235, 242, 250);
                vienBadge = new Color(215, 226, 240);
                chuBadge = MAU_XANH_DAM;
            }

            boc.add(new NhanVienThuocBadge(chuoi, nenBadge, vienBadge, chuBadge));
            return boc;
        }
    }

    private static class NutVienTrang extends JButton {
        private boolean dangTroChuot = false;

        NutVienTrang(String chuoi, LoaiBieuTuong icon) {
            super(chuoi, new BieuTuongVector(icon, 14, MAU_XANH_DAM));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_XANH_DAM);
            setIconTextGap(6);
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
            return new Dimension(d.width, 34);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D r = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
            g2.setColor(dangTroChuot ? MAU_NEN_MENU_HOVER : Color.WHITE);
            g2.fill(r);
            g2.setColor(new Color(215, 224, 236));
            g2.draw(r);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutHanhDongTienDo extends JButton {
        private final Color mauNen;
        private final Color mauHover;
        private final Color mauVoHieu;
        private boolean dangTroChuot = false;

        NutHanhDongTienDo(String chuoi, Color mauNen, Color mauHover, Color mauVoHieu) {
            super(chuoi);
            this.mauNen = mauNen;
            this.mauHover = mauHover;
            this.mauVoHieu = mauVoHieu;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
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
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            Color mauVe = !isEnabled() ? mauVoHieu : (dangTroChuot ? mauHover : mauNen);
            g2.setColor(mauVe);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8f, 8f));

            g2.setFont(getFont());
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    private static class NhanVienThuocBadge extends JComponent {
        private String vanBan;
        private Color mauNen;
        private Color mauVien;
        private Color mauChu;

        NhanVienThuocBadge(String vanBan, Color mauNen, Color mauVien, Color mauChu) {
            this.vanBan = vanBan;
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            this.mauChu = mauChu;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
        }

        void capNhat(String vanBanMoi, Color mauNenMoi, Color mauVienMoi, Color mauChuMoi) {
            this.vanBan = vanBanMoi;
            this.mauNen = mauNenMoi;
            this.mauVien = mauVienMoi;
            this.mauChu = mauChuMoi;
            revalidate();
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(vanBan) + 18, 23);
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
     * Bộ vẽ vector sắc nét cho icon menu và màn hình Kỹ thuật viên.
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
                case CAP_CONG_VIEC -> "service.png";
                case TIM_KIEM -> "search.png";
                case THE_PHU_TUNG -> "part.png";
                case NHAT_KY -> "history.png";
                case LAM_MOI -> "refresh.png";
                case BANG_PHAN_CONG -> "invoice.png";
                case BANH_RANG -> "service.png";
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
            KyThuatVienManHinhChinh frm = new KyThuatVienManHinhChinh();
            frm.setVisible(true);
        });
    }
}
