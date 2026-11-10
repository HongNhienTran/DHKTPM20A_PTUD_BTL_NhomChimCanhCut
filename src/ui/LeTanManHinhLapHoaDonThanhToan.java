package ui;

import bus.HoaDonBUS;
import bus.KhachHangBUS;
import bus.PhieuTiepNhanBUS;
import bus.SanPhamBUS;
import dao.NhanVienDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.TaiKhoan;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiThanhToan;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Màn hình: LẬP HÓA ĐƠN & THU NGÂN THANH TOÁN
 * <p>
 * Cho phép thu ngân / lễ tân:
 * - Tiếp nhận và theo dõi các hóa đơn đang ở trạng thái CHƯA_THANH_TOAN (bao gồm đơn in tạm tính từ bán lẻ và dịch vụ sửa chữa).
 * - Tra cứu, lọc theo nguồn đơn và từ khóa.
 * - Kiểm tra chi tiết đơn hàng, tổng tiền, thuế VAT, giảm giá.
 * - Chọn phương thức thanh toán (Tiền mặt, Chuyển khoản QR), tính tiền thừa.
 * - In phiếu tạm, Hủy đơn hoặc Xác nhận hoàn tất thanh toán.
 */
public class LeTanManHinhLapHoaDonThanhToan extends JPanel {

    // Bảng màu giao diện chuẩn hệ thống Pengu Garage
    private static final Color MAU_NEN_CHINH = new Color(241, 245, 249);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_VIEN_INPUT = new Color(203, 213, 225);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(30, 58, 95);
    private static final Color MAU_CHU_CHINH = new Color(15, 23, 42);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 88, 12);
    private static final Color MAU_XANH_LA = new Color(5, 150, 105);
    private static final Color MAU_XANH_LA_HOVER = new Color(4, 120, 87);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);
    private static final Color MAU_CHON_DONG = new Color(255, 241, 230);

    private static final DateTimeFormatter DINH_DANG_GIO_TAM = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    // Các tầng nghiệp vụ
    private final HoaDonBUS hoaDonBUS;
    private final SanPhamBUS sanPhamBUS;
    private final KhachHangBUS khachHangBUS;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();
    private final NhanVienDAO nhanVienDAO;
    private TaiKhoan taiKhoanDangNhap;

    // Danh sách dữ liệu
    private List<HoaDon> dsHoaDonCho = new ArrayList<>();
    private HoaDon hoaDonDangChon = null;
    private boolean dangCapNhatBang = false;

    // Component Header
    private BadgeVang lblBadgeSoLuongCho;
    private BadgeXanh lblBadgeTongTienCho;

    // Component Thẻ Trái (Danh sách chờ)
    private JTextField txtTimKiem;
    private JComboBox<String> cboNguonDon;
    private JTable bangHoaDonCho;
    private DefaultTableModel moHinhBangCho;

    // Component Thẻ Phải (Chi tiết & Thanh toán)
    private JPanel cardNoiDungChiTiet;
    private CardLayout boCucCardChiTiet;
    private static final String CARD_CHI_TIET = "CHI_TIET";
    private static final String CARD_TRONG = "TRONG";

    private JLabel lblMaHoaDonTieuDe;
    private JLabel lblTenKhachHang;
    private JLabel lblSoDienThoai;
    private JLabel lblBienSoXe;
    private JLabel lblNhanVienLap;

    private JTable bangChiTiet;
    private DefaultTableModel moHinhBangChiTiet;

    private JLabel lblTongTienHang;
    private JLabel lblGiamGia;
    private JLabel lblThueVAT;
    private JLabel lblTongPhaiThu;

    private JRadioButton radTienMat;
    private JRadioButton radChuyenKhoan;
    private JTextField txtTienKhachDua;
    private JLabel lblTienThua;
    private JTextField txtGhiChuThanhToan;

    private JButton btnHuyDon;
    private JButton btnInPhieuTam;
    private JButton btnXacNhanThanhToan;

    public LeTanManHinhLapHoaDonThanhToan() {
        this(null);
    }

    public LeTanManHinhLapHoaDonThanhToan(TaiKhoan taiKhoan) {
        this(taiKhoan, new HoaDonBUS(), new SanPhamBUS(), new KhachHangBUS());
    }

    public LeTanManHinhLapHoaDonThanhToan(TaiKhoan taiKhoan, HoaDonBUS hoaDonBUS,
                                         SanPhamBUS sanPhamBUS, KhachHangBUS khachHangBUS) {
        this.taiKhoanDangNhap = taiKhoan;
        this.hoaDonBUS = hoaDonBUS != null ? hoaDonBUS : new HoaDonBUS();
        this.sanPhamBUS = sanPhamBUS != null ? sanPhamBUS : new SanPhamBUS();
        this.khachHangBUS = khachHangBUS != null ? khachHangBUS : new KhachHangBUS();
        this.nhanVienDAO = new NhanVienDAO();

        khoiTaoGiaoDien();
        taiDanhSachHoaDonCho();
    }

    public void setTaiKhoanDangNhap(TaiKhoan taiKhoan) {
        this.taiKhoanDangNhap = taiKhoan;
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 12));
        setBackground(MAU_NEN_CHINH);
        setBorder(new EmptyBorder(14, 18, 14, 18));

        // 1. Thanh tiêu đề phía trên
        add(taoThanhTieuDe(), BorderLayout.NORTH);

        // 2. Khu vực chia đôi: Cột trái (Danh sách chờ) - Cột phải (Chi tiết & Thanh toán)
        JPanel khuVucNoiDung = new JPanel(new GridBagLayout());
        khuVucNoiDung.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Thẻ trái chiếm ~42% chiều rộng
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        gbc.insets = new Insets(0, 0, 0, 12);
        khuVucNoiDung.add(taoTheDanhSachCho(), gbc);

        // Thẻ phải chiếm ~58% chiều rộng
        gbc.gridx = 1;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 0);
        khuVucNoiDung.add(taoTheChiTietVaThanhToan(), gbc);

        add(khuVucNoiDung, BorderLayout.CENTER);
    }

    // =========================================================================
    // 1. THANH TIÊU ĐỀ
    // =========================================================================
    private JPanel taoThanhTieuDe() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Lập Hóa Đơn & Thu Ngân Thanh Toán");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel cumBadges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumBadges.setOpaque(false);

        lblBadgeSoLuongCho = new BadgeVang("⏳ 0 đơn chờ thanh toán");
        lblBadgeTongTienCho = new BadgeXanh("0 đ");

        cumBadges.add(lblBadgeSoLuongCho);
        cumBadges.add(lblBadgeTongTienCho);

        pnl.add(lblTieuDe, BorderLayout.WEST);
        pnl.add(cumBadges, BorderLayout.EAST);
        return pnl;
    }

    // =========================================================================
    // 2. THẺ BÊN TRÁI: DANH SÁCH HÓA ĐƠN CHỜ THU TIỀN
    // =========================================================================
    private JPanel taoTheDanhSachCho() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout(0, 10));
        the.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Tiêu đề thẻ & Thanh công cụ lọc
        JPanel dauThe = new JPanel();
        dauThe.setLayout(new BoxLayout(dauThe, BoxLayout.Y_AXIS));
        dauThe.setOpaque(false);

        JLabel lblTenThe = new JLabel("Danh Sách Hóa Đơn Chờ Thu Tiền");
        lblTenThe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTenThe.setForeground(MAU_XANH_DAM);
        lblTenThe.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hàng thanh công cụ tìm kiếm
        JPanel hangCongCu = new JPanel(new BorderLayout(8, 0));
        hangCongCu.setOpaque(false);
        hangCongCu.setAlignmentX(Component.LEFT_ALIGNMENT);
        hangCongCu.setBorder(new EmptyBorder(8, 0, 4, 0));

        // Ô tìm kiếm
        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(160, 34));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 8)
        ));
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Mã HĐ, Tên KH, SĐT...");
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { locVaHienThiDanhSach(); }
            @Override
            public void removeUpdate(DocumentEvent e) { locVaHienThiDanhSach(); }
            @Override
            public void changedUpdate(DocumentEvent e) { locVaHienThiDanhSach(); }
        });

        // Dropdown lọc nguồn đơn
        cboNguonDon = new JComboBox<>(new String[]{"Nguồn đơn", "Tất cả nguồn", "Bán lẻ", "Sửa chữa"});
        cboNguonDon.setPreferredSize(new Dimension(115, 34));
        cboNguonDon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboNguonDon.setBackground(Color.WHITE);
        cboNguonDon.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true));
        cboNguonDon.addActionListener(e -> locVaHienThiDanhSach());

        // Nút làm mới
        JButton btnLamMoi = new JButton(IconUtil.layIcon("refresh.png", 14, 14, MAU_XANH_DAM));
        btnLamMoi.setPreferredSize(new Dimension(34, 34));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setContentAreaFilled(false);
        btnLamMoi.setOpaque(false);
        btnLamMoi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLamMoi.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true));
        btnLamMoi.setToolTipText("Làm mới danh sách đơn chờ");
        btnLamMoi.addActionListener(e -> taiDanhSachHoaDonCho());

        JPanel cumPhaiCongCu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cumPhaiCongCu.setOpaque(false);
        cumPhaiCongCu.add(cboNguonDon);
        cumPhaiCongCu.add(btnLamMoi);

        hangCongCu.add(txtTimKiem, BorderLayout.CENTER);
        hangCongCu.add(cumPhaiCongCu, BorderLayout.EAST);

        dauThe.add(lblTenThe);
        dauThe.add(hangCongCu);

        // Bảng danh sách hóa đơn chờ
        String[] cot = {"#", "Mã HĐ", "Nguồn", "Khách Hàng", "Giờ In Tạm", "Cần Thu"};
        moHinhBangCho = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangHoaDonCho = new JTable(moHinhBangCho);
        bangHoaDonCho.setRowHeight(36);
        bangHoaDonCho.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangHoaDonCho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangHoaDonCho.setShowGrid(false);
        bangHoaDonCho.setShowHorizontalLines(true);
        bangHoaDonCho.setGridColor(new Color(241, 245, 249));

        JTableHeader header = bangHoaDonCho.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(MAU_CHU_CHINH);
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel colModel = bangHoaDonCho.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(32);  // #
        colModel.getColumn(1).setPreferredWidth(125); // Mã HĐ
        colModel.getColumn(2).setPreferredWidth(65);  // Nguồn
        colModel.getColumn(3).setPreferredWidth(100); // Khách Hàng
        colModel.getColumn(4).setPreferredWidth(85);  // Giờ In Tạm
        colModel.getColumn(5).setPreferredWidth(95);  // Cần Thu

        BoVeOBangCho renderer = new BoVeOBangCho();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(renderer);
        }

        bangHoaDonCho.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !dangCapNhatBang) {
                int hang = bangHoaDonCho.getSelectedRow();
                if (hang >= 0 && hang < dsHoaDonCho.size()) {
                    chonHoaDon(dsHoaDonCho.get(hang));
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangHoaDonCho);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    // =========================================================================
    // 3. THẺ BÊN PHẢI: CHI TIẾT HÓA ĐƠN & THU NGÂN THANH TOÁN
    // =========================================================================
    private JPanel taoTheChiTietVaThanhToan() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());
        the.setBorder(new EmptyBorder(14, 18, 14, 18));

        boCucCardChiTiet = new CardLayout();
        cardNoiDungChiTiet = new JPanel(boCucCardChiTiet);
        cardNoiDungChiTiet.setOpaque(false);

        // Card 1: Có dữ liệu hóa đơn được chọn
        JPanel pnlCoDuLieu = new JPanel(new BorderLayout(0, 10));
        pnlCoDuLieu.setOpaque(false);

        pnlCoDuLieu.add(taoDauTheChiTiet(), BorderLayout.NORTH);
        pnlCoDuLieu.add(taoBangChiTietHangMuc(), BorderLayout.CENTER);
        pnlCoDuLieu.add(taoChanTheThanhToan(), BorderLayout.SOUTH);

        // Card 2: Trạng thái trống (chưa chọn hóa đơn)
        JPanel pnlTrong = new JPanel(new GridBagLayout());
        pnlTrong.setOpaque(false);
        JLabel lblTrong = new JLabel("Vui lòng chọn một hóa đơn từ danh sách chờ bên trái để thanh toán");
        lblTrong.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTrong.setForeground(MAU_CHU_MO);
        pnlTrong.add(lblTrong);

        cardNoiDungChiTiet.add(pnlCoDuLieu, CARD_CHI_TIET);
        cardNoiDungChiTiet.add(pnlTrong, CARD_TRONG);

        the.add(cardNoiDungChiTiet, BorderLayout.CENTER);
        return the;
    }

    /**
     * Phần đầu thẻ chi tiết: Tiêu đề Hóa đơn + Badge trạng thái + Khung thông tin khách & nhân viên
     */
    private JPanel taoDauTheChiTiet() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);

        // Hàng 1: HÓA ĐƠN: [Mã] (trái) + [• CHƯA THANH TOÁN] (phải)
        JPanel hangTieuDe = new JPanel(new BorderLayout());
        hangTieuDe.setOpaque(false);

        lblMaHoaDonTieuDe = new JLabel("HÓA ĐƠN: ---");
        lblMaHoaDonTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMaHoaDonTieuDe.setForeground(MAU_XANH_DAM);

        BadgeCam lblBadgeTrangThai = new BadgeCam("• CHƯA THANH TOÁN");

        hangTieuDe.add(lblMaHoaDonTieuDe, BorderLayout.WEST);
        hangTieuDe.add(lblBadgeTrangThai, BorderLayout.EAST);

        // Hàng 2: Hộp tóm tắt thông tin khách hàng & xe & nhân viên
        TheBoGoc pnlThongTin = new TheBoGoc(10, new Color(248, 250, 252), MAU_VIEN_THE);
        pnlThongTin.setLayout(new GridLayout(2, 2, 16, 6));
        pnlThongTin.setBorder(new EmptyBorder(8, 14, 8, 14));

        lblTenKhachHang = taoLabelGiaTri("Khách hàng: -");
        lblSoDienThoai = taoLabelGiaTri("Số ĐT: -");
        lblBienSoXe = taoLabelGiaTri("Biển số xe: -");
        lblNhanVienLap = taoLabelGiaTri("Nhân viên lập: -");

        pnlThongTin.add(lblTenKhachHang);
        pnlThongTin.add(lblSoDienThoai);
        pnlThongTin.add(lblBienSoXe);
        pnlThongTin.add(lblNhanVienLap);

        pnl.add(hangTieuDe);
        pnl.add(Box.createVerticalStrut(10));
        pnl.add(pnlThongTin);
        return pnl;
    }

    private JLabel taoLabelGiaTri(String macDinh) {
        JLabel lbl = new JLabel(macDinh);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(MAU_CHU_CHINH);
        return lbl;
    }

    /**
     * Bảng danh sách mặt hàng / dịch vụ
     */
    private JPanel taoBangChiTietHangMuc() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        String[] cot = {"#", "Mặt Hàng / Dịch Vụ", "SL", "Đơn Giá", "Thành Tiền"};
        moHinhBangChiTiet = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangChiTiet = new JTable(moHinhBangChiTiet);
        bangChiTiet.setRowHeight(32);
        bangChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangChiTiet.setShowGrid(false);
        bangChiTiet.setShowHorizontalLines(true);
        bangChiTiet.setGridColor(new Color(241, 245, 249));

        JTableHeader header = bangChiTiet.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(MAU_CHU_CHINH);
        header.setPreferredSize(new Dimension(0, 32));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel colModel = bangChiTiet.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(32);  // #
        colModel.getColumn(1).setPreferredWidth(260); // Mặt Hàng / Dịch Vụ
        colModel.getColumn(2).setPreferredWidth(45);  // SL
        colModel.getColumn(3).setPreferredWidth(95);  // Đơn Giá
        colModel.getColumn(4).setPreferredWidth(105); // Thành Tiền

        BoVeOBangChiTiet renderer = new BoVeOBangChiTiet();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane cuonBang = new JScrollPane(bangChiTiet);
        cuonBang.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true));
        cuonBang.getViewport().setBackground(Color.WHITE);
        cuonBang.setPreferredSize(new Dimension(0, 160));

        pnl.add(cuonBang, BorderLayout.CENTER);
        return pnl;
    }

    /**
     * Chân thẻ: Tổng tiền tính toán, Nhập phương thức thanh toán & Cụm nút hành động
     */
    private JPanel taoChanTheThanhToan() {
        JPanel chan = new JPanel();
        chan.setLayout(new BoxLayout(chan, BoxLayout.Y_AXIS));
        chan.setOpaque(false);

        // 1. Khung tổng kết tài chính (Tổng tiền hàng, Giảm giá, VAT, Tổng tiền phải thu)
        TheBoGoc pnlTaiChinh = new TheBoGoc(10, new Color(248, 250, 252), MAU_VIEN_THE);
        pnlTaiChinh.setLayout(new GridBagLayout());
        pnlTaiChinh.setBorder(new EmptyBorder(10, 14, 10, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        int d = 0;

        lblTongTienHang = taoLabelTienPhu("0 đ");
        lblGiamGia = taoLabelTienPhu("- 0 đ");
        lblGiamGia.setForeground(MAU_DO_CANH_BAO);
        lblThueVAT = taoLabelTienPhu("0 đ");

        themDongTongKet(pnlTaiChinh, gbc, d++, "Tổng tiền hàng/dịch vụ:", lblTongTienHang);
        themDongTongKet(pnlTaiChinh, gbc, d++, "Giảm giá / Voucher:", lblGiamGia);
        themDongTongKet(pnlTaiChinh, gbc, d++, "Thuế VAT (10%):", lblThueVAT);

        // Dòng kẻ phân cách
        gbc.gridy = d++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 0, 8, 0);
        JPanel duongKe = new JPanel();
        duongKe.setBackground(MAU_VIEN_THE);
        duongKe.setPreferredSize(new Dimension(0, 1));
        pnlTaiChinh.add(duongKe, gbc);

        // TỔNG TIỀN PHẢI THU (nổi bật cam đậm)
        gbc.gridy = d;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblTieuDePhaiThu = new JLabel("TỔNG TIỀN PHẢI THU:");
        lblTieuDePhaiThu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTieuDePhaiThu.setForeground(MAU_XANH_DAM);
        pnlTaiChinh.add(lblTieuDePhaiThu, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        lblTongPhaiThu = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTongPhaiThu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongPhaiThu.setForeground(MAU_CAM_CHU_DAO);
        pnlTaiChinh.add(lblTongPhaiThu, gbc);

        // 2. Khung thao tác thanh toán của thu ngân
        JPanel pnlThaoTacTT = new JPanel();
        pnlThaoTacTT.setLayout(new BoxLayout(pnlThaoTacTT, BoxLayout.Y_AXIS));
        pnlThaoTacTT.setOpaque(false);
        pnlThaoTacTT.setBorder(new EmptyBorder(8, 0, 8, 0));

        // Hàng chọn Phương thức thanh toán
        JPanel hangPT = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        hangPT.setOpaque(false);
        JLabel lblNhanPT = new JLabel("Phương thức thanh toán:");
        lblNhanPT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNhanPT.setForeground(MAU_CHU_CHINH);

        radTienMat = new JRadioButton("Tiền mặt", true);
        radTienMat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radTienMat.setOpaque(false);

        radChuyenKhoan = new JRadioButton("Chuyển khoản QR", false);
        radChuyenKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radChuyenKhoan.setOpaque(false);

        ButtonGroup grp = new ButtonGroup();
        grp.add(radTienMat);
        grp.add(radChuyenKhoan);

        hangPT.add(lblNhanPT);
        hangPT.add(radTienMat);
        hangPT.add(radChuyenKhoan);

        // Hàng nhập Tiền khách đưa & Tiền thừa
        JPanel hangTienTra = new JPanel(new GridBagLayout());
        hangTienTra.setOpaque(false);
        hangTienTra.setBorder(new EmptyBorder(6, 0, 4, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        // Tiền khách đưa
        c.gridx = 0;
        c.weightx = 0.5;
        c.insets = new Insets(0, 0, 0, 10);
        JPanel pnlKhachDua = new JPanel(new BorderLayout(0, 3));
        pnlKhachDua.setOpaque(false);
        JLabel lblNhanKD = new JLabel("Tiền khách đưa (VNĐ):");
        lblNhanKD.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNhanKD.setForeground(MAU_CHU_PHU);

        txtTienKhachDua = new JTextField();
        txtTienKhachDua.setPreferredSize(new Dimension(0, 34));
        txtTienKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtTienKhachDua.setForeground(MAU_CHU_CHINH);
        txtTienKhachDua.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { tinhTienThua(); }
            @Override
            public void removeUpdate(DocumentEvent e) { tinhTienThua(); }
            @Override
            public void changedUpdate(DocumentEvent e) { tinhTienThua(); }
        });

        pnlKhachDua.add(lblNhanKD, BorderLayout.NORTH);
        pnlKhachDua.add(txtTienKhachDua, BorderLayout.CENTER);
        hangTienTra.add(pnlKhachDua, c);

        // Tiền thừa trả khách
        c.gridx = 1;
        c.weightx = 0.5;
        c.insets = new Insets(0, 0, 0, 0);
        JPanel pnlTienThua = new JPanel(new BorderLayout(0, 3));
        pnlTienThua.setOpaque(false);
        JLabel lblNhanTT = new JLabel("Tiền thừa trả khách:");
        lblNhanTT.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNhanTT.setForeground(MAU_CHU_PHU);

        lblTienThua = new JLabel("0 đ");
        lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTienThua.setForeground(MAU_XANH_LA);

        pnlTienThua.add(lblNhanTT, BorderLayout.NORTH);
        pnlTienThua.add(lblTienThua, BorderLayout.CENTER);
        hangTienTra.add(pnlTienThua, c);

        // Sự kiện chuyển radio: nếu chọn Chuyển khoản QR -> tự động điền đúng số tiền
        radChuyenKhoan.addActionListener(e -> {
            if (hoaDonDangChon != null) {
                double phaiThu = hoaDonDangChon.tinhTongThanhToan();
                txtTienKhachDua.setText(dinhDangSo(phaiThu));
                txtGhiChuThanhToan.setText("Chuyển khoản QR - HĐ " + hoaDonDangChon.getMaHoaDon());
            }
        });
        radTienMat.addActionListener(e -> {
            tinhTienThua();
        });

        // Ghi chú thanh toán
        txtGhiChuThanhToan = new JTextField();
        txtGhiChuThanhToan.setPreferredSize(new Dimension(0, 32));
        txtGhiChuThanhToan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGhiChuThanhToan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(0, 10, 0, 8)
        ));
        txtGhiChuThanhToan.putClientProperty("JTextField.placeholderText", "Ghi chú thanh toán / Mã giao dịch chuyển khoản...");

        pnlThaoTacTT.add(hangPT);
        pnlThaoTacTT.add(hangTienTra);
        pnlThaoTacTT.add(txtGhiChuThanhToan);

        // 3. Hàng nút hành động: [✕ Hủy Đơn] (trái) - [In Phiếu Tạm] [✓ XÁC NHẬN THANH TOÁN] (phải)
        JPanel hangNut = new JPanel(new BorderLayout());
        hangNut.setOpaque(false);
        hangNut.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnHuyDon = new NutVienDo("Hủy Đơn", IconUtil.layIcon("close.png", 12, 12, MAU_DO_CANH_BAO));
        btnHuyDon.setPreferredSize(new Dimension(105, 36));
        btnHuyDon.addActionListener(e -> xuLyHuyDonDangChon());

        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumPhai.setOpaque(false);

        btnInPhieuTam = new NutVienTrang("In Phiếu Tạm");
        btnInPhieuTam.setPreferredSize(new Dimension(115, 36));
        btnInPhieuTam.addActionListener(e -> xuLyInPhieuTam());

        btnXacNhanThanhToan = new NutXanhXacNhan("XÁC NHẬN THANH TOÁN", IconUtil.layIcon("check.png", 14, 14, Color.WHITE));
        btnXacNhanThanhToan.setPreferredSize(new Dimension(200, 36));
        btnXacNhanThanhToan.addActionListener(e -> xuLyXacNhanThanhToan());

        cumPhai.add(btnInPhieuTam);
        cumPhai.add(btnXacNhanThanhToan);

        hangNut.add(btnHuyDon, BorderLayout.WEST);
        hangNut.add(cumPhai, BorderLayout.EAST);

        chan.add(pnlTaiChinh);
        chan.add(pnlThaoTacTT);
        chan.add(hangNut);
        return chan;
    }

    private JLabel taoLabelTienPhu(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(MAU_CHU_CHINH);
        return lbl;
    }

    private void themDongTongKet(JPanel p, GridBagConstraints gbc, int dong, String nhan, JLabel lblGiaTri) {
        gbc.gridy = dong;
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(2, 0, 2, 0);
        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNhan.setForeground(MAU_CHU_PHU);
        p.add(lblNhan, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        p.add(lblGiaTri, gbc);
    }

    // =========================================================================
    // XỬ LÝ NGHIỆP VỤ & TẢI DỮ LIỆU
    // =========================================================================

    /**
     * Tải toàn bộ danh sách hóa đơn CHƯA_THANH_TOAN từ database,
     * tự động lập hóa đơn dịch vụ cho các xe đã hoàn thành sửa chữa (HOAN_THANH) nếu chưa có.
     */
    public void taiDanhSachHoaDonCho() {
        try {
            // Tự động kiểm tra và tạo hóa đơn dịch vụ cho các xe đã hoàn thành sửa chữa (HOAN_THANH) nếu chưa có hóa đơn
            try {
                List<PhieuTiepNhan> dsHoanThanh = phieuTiepNhanBUS.layTheoTrangThai(TrangThaiPhieuTiepNhan.HOAN_THANH);
                if (dsHoanThanh != null) {
                    NhanVien nvLeTan = (taiKhoanDangNhap != null) ? taiKhoanDangNhap.getNhanVien() : null;
                    for (PhieuTiepNhan ptn : dsHoanThanh) {
                        try {
                            if (hoaDonBUS.timTheoPhieuTiepNhan(ptn.getMaTiepNhan()) == null) {
                                String bienSo = (ptn.getPhuongTien() != null) ? ptn.getPhuongTien().getBienSoXe() : "";
                                hoaDonBUS.taoHoaDonDichVuTuPhieuTiepNhan(
                                        ptn.getMaTiepNhan(),
                                        nvLeTan != null ? nvLeTan : ptn.getNhanVien(),
                                        null,
                                        "Thanh toán dịch vụ xe " + bienSo
                                );
                            }
                        } catch (Exception ignored) {
                            // Bỏ qua nếu phiếu tiếp nhận chưa có báo giá phù hợp
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            List<HoaDon> tatCa = hoaDonBUS.layTatCa();
            dsHoaDonCho.clear();
            for (HoaDon hd : tatCa) {
                if (hd.getTrangThaiThanhToan() == TrangThaiThanhToan.CHUA_THANH_TOAN) {
                    dsHoaDonCho.add(hd);
                }
            }
            locVaHienThiDanhSach();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách hóa đơn chờ: " + ex.getMessage(),
                    "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tự động tìm và chọn một hóa đơn cụ thể theo mã hóa đơn
     */
    public void chonHoaDonTheoMa(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return;

        if (txtTimKiem != null) txtTimKiem.setText("");
        if (cboNguonDon != null) cboNguonDon.setSelectedIndex(0);

        taiDanhSachHoaDonCho();

        for (int i = 0; i < dsHoaDonCho.size(); i++) {
            HoaDon hd = dsHoaDonCho.get(i);
            if (maHoaDon.equalsIgnoreCase(hd.getMaHoaDon())) {
                if (i < bangHoaDonCho.getRowCount()) {
                    bangHoaDonCho.setRowSelectionInterval(i, i);
                    bangHoaDonCho.scrollRectToVisible(bangHoaDonCho.getCellRect(i, 0, true));
                }
                chonHoaDon(hd);
                return;
            }
        }
    }

    /**
     * Tự động tìm và chọn hóa đơn gắn với phiếu tiếp nhận
     */
    public void chonHoaDonTheoPhieuTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) return;

        if (txtTimKiem != null) txtTimKiem.setText("");
        if (cboNguonDon != null) cboNguonDon.setSelectedIndex(0);

        taiDanhSachHoaDonCho();

        for (int i = 0; i < dsHoaDonCho.size(); i++) {
            HoaDon hd = dsHoaDonCho.get(i);
            if (hd.getPhieuTiepNhan() != null && maTiepNhan.equalsIgnoreCase(hd.getPhieuTiepNhan().getMaTiepNhan())) {
                if (i < bangHoaDonCho.getRowCount()) {
                    bangHoaDonCho.setRowSelectionInterval(i, i);
                    bangHoaDonCho.scrollRectToVisible(bangHoaDonCho.getCellRect(i, 0, true));
                }
                chonHoaDon(hd);
                return;
            }
        }
    }

    /**
     * Lọc danh sách theo từ khóa tìm kiếm & nguồn đơn (Bán lẻ / Sửa chữa)
     */
    private void locVaHienThiDanhSach() {
        dangCapNhatBang = true;
        try {
            moHinhBangCho.setRowCount(0);

            String tuKhoa = txtTimKiem != null && txtTimKiem.getText() != null
                    ? txtTimKiem.getText().trim().toLowerCase(Locale.ROOT) : "";
            int indexNguon = cboNguonDon != null ? cboNguonDon.getSelectedIndex() : 0;
            // 0: Nguồn đơn (tất cả), 1: Tất cả nguồn, 2: Bán lẻ, 3: Sửa chữa

            int stt = 1;
            double tongTienCho = 0.0;
            int soLuongCho = 0;
            HoaDon hdCanChonLai = null;

            for (HoaDon hd : dsHoaDonCho) {
                // Lọc nguồn
                if (indexNguon == 2 && hd.getLoaiHoaDon() != LoaiHoaDon.BAN_HANG) {
                    continue;
                }
                if (indexNguon == 3 && hd.getLoaiHoaDon() != LoaiHoaDon.DICH_VU) {
                    continue;
                }

                // Lọc từ khóa
                String maHD = hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "";
                String tenKH = hd.getKhachHang() != null && hd.getKhachHang().getHoTen() != null
                        ? hd.getKhachHang().getHoTen() : "Khách lẻ";
                String sdt = hd.getKhachHang() != null && hd.getKhachHang().getSoDienThoai() != null
                        ? hd.getKhachHang().getSoDienThoai() : "";

                if (!tuKhoa.isEmpty()) {
                    boolean khop = maHD.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || tenKH.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || sdt.toLowerCase(Locale.ROOT).contains(tuKhoa);
                    if (!khop) {
                        continue;
                    }
                }

                double canThu = hd.tinhTongThanhToan();
                tongTienCho += canThu;
                soLuongCho++;

                String nguonStr = hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG ? "Bán lẻ" : "Sửa chữa";
                String gioLap = hd.getThoiGianLap() != null ? hd.getThoiGianLap().format(DINH_DANG_GIO_TAM) : "-";

                moHinhBangCho.addRow(new Object[]{
                        stt++,
                        maHD,
                        nguonStr,
                        tenKH,
                        gioLap,
                        dinhDangTien(canThu)
                });

                if (hoaDonDangChon != null && hd.getMaHoaDon().equals(hoaDonDangChon.getMaHoaDon())) {
                    hdCanChonLai = hd;
                }
            }

            // Cập nhật badges
            lblBadgeSoLuongCho.setText("⏳ " + soLuongCho + " đơn chờ thanh toán");
            lblBadgeTongTienCho.setText(dinhDangTien(tongTienCho));

            // Khôi phục chọn dòng hoặc chọn dòng đầu tiên
            if (hdCanChonLai != null) {
                chonHoaDon(hdCanChonLai);
            } else if (bangHoaDonCho.getRowCount() > 0) {
                bangHoaDonCho.setRowSelectionInterval(0, 0);
                chonHoaDon(dsHoaDonCho.get(0));
            } else {
                boChonHoaDon();
            }
        } finally {
            dangCapNhatBang = false;
        }
    }

    /**
     * Nạp dữ liệu hóa đơn được chọn lên thẻ bên phải
     */
    private void chonHoaDon(HoaDon hd) {
        this.hoaDonDangChon = hd;
        if (hd == null) {
            boChonHoaDon();
            return;
        }

        boCucCardChiTiet.show(cardNoiDungChiTiet, CARD_CHI_TIET);

        // Header & Thông tin
        lblMaHoaDonTieuDe.setText("HÓA ĐƠN: " + hd.getMaHoaDon());

        KhachHang kh = hd.getKhachHang();
        lblTenKhachHang.setText("Khách hàng: " + (kh != null && kh.getHoTen() != null ? kh.getHoTen() : "Khách lẻ"));
        lblSoDienThoai.setText("Số ĐT: " + (kh != null && kh.getSoDienThoai() != null && !kh.getSoDienThoai().isBlank()
                ? kh.getSoDienThoai() : "-"));

        String bienSo = "-";
        if (hd.getPhieuTiepNhan() != null && hd.getPhieuTiepNhan().getPhuongTien() != null) {
            PhuongTien pt = hd.getPhieuTiepNhan().getPhuongTien();
            bienSo = pt.getBienSoXe() != null ? pt.getBienSoXe() : "-";
        }
        lblBienSoXe.setText("Biển số xe: " + bienSo);

        NhanVien nv = hd.getNhanVien();
        lblNhanVienLap.setText("Nhân viên lập: " + (nv != null && nv.getHoTen() != null ? nv.getHoTen() : "-"));

        // Nạp bảng chi tiết
        moHinhBangChiTiet.setRowCount(0);
        int stt = 1;
        if (hd.getDanhSachChiTiet() != null) {
            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                String tenHangMuc = "";
                if (ct.getSanPham() != null && ct.getSanPham().getTenSanPham() != null) {
                    tenHangMuc = ct.getSanPham().getTenSanPham();
                } else if (ct.getDichVu() != null && ct.getDichVu().getTenDichVu() != null) {
                    tenHangMuc = ct.getDichVu().getTenDichVu();
                }

                moHinhBangChiTiet.addRow(new Object[]{
                        stt++,
                        tenHangMuc,
                        ct.getSoLuong(),
                        dinhDangTien(ct.getDonGia()),
                        dinhDangTien(ct.tinhThanhTien())
                });
            }
        }

        // Tài chính
        double tongHang = hd.tinhTongTienHang();
        double giamGia = hd.tinhTienKhuyenMai();
        double thueVAT = hd.tinhThue();
        double tongPhaiThu = hd.tinhTongThanhToan();

        lblTongTienHang.setText(dinhDangTien(tongHang));
        lblGiamGia.setText(giamGia > 0 ? "- " + dinhDangTien(giamGia) : "- 0 đ");
        lblThueVAT.setText(dinhDangTien(thueVAT));
        lblTongPhaiThu.setText(dinhDangTien(tongPhaiThu));

        // Phương thức thanh toán mặc định
        if (radChuyenKhoan.isSelected()) {
            txtTienKhachDua.setText(dinhDangSo(tongPhaiThu));
            txtGhiChuThanhToan.setText("Chuyển khoản QR - HĐ " + hd.getMaHoaDon());
        } else {
            txtTienKhachDua.setText("");
            lblTienThua.setText("0 đ");
            txtGhiChuThanhToan.setText(hd.getGhiChu() != null ? hd.getGhiChu() : "");
        }
    }

    private void boChonHoaDon() {
        this.hoaDonDangChon = null;
        boCucCardChiTiet.show(cardNoiDungChiTiet, CARD_TRONG);
    }

    /**
     * Tự động tính tiền thừa trả khách khi thu ngân nhập tiền khách đưa
     */
    private void tinhTienThua() {
        if (hoaDonDangChon == null) {
            lblTienThua.setText("0 đ");
            return;
        }

        String raw = txtTienKhachDua.getText() != null ? txtTienKhachDua.getText().trim() : "";
        if (raw.isEmpty()) {
            lblTienThua.setText("0 đ");
            lblTienThua.setForeground(MAU_XANH_LA);
            return;
        }

        try {
            double khachDua = Double.parseDouble(raw.replace(".", "").replace(",", "").replace("đ", "").trim());
            double phaiThu = hoaDonDangChon.tinhTongThanhToan();
            double tienThua = khachDua - phaiThu;

            if (tienThua >= 0) {
                lblTienThua.setText(dinhDangTien(tienThua));
                lblTienThua.setForeground(MAU_XANH_LA);
            } else {
                lblTienThua.setText("Thiếu: " + dinhDangTien(Math.abs(tienThua)));
                lblTienThua.setForeground(MAU_DO_CANH_BAO);
            }
        } catch (NumberFormatException ignored) {
            lblTienThua.setText("Số tiền không hợp lệ");
            lblTienThua.setForeground(MAU_DO_CANH_BAO);
        }
    }

    /**
     * Xử lý HỦY ĐƠN: Hoàn trả tồn kho (nếu là đơn bán lẻ) và xóa hóa đơn chờ
     */
    private void xuLyHuyDonDangChon() {
        if (hoaDonDangChon == null) return;

        int hoi = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn HỦY hóa đơn chờ [" + hoaDonDangChon.getMaHoaDon() + "] không?\n"
                        + "Thao tác này sẽ hoàn lại số lượng tồn kho sản phẩm (nếu có) và xóa đơn khỏi danh sách.",
                "Xác nhận hủy đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (hoi != JOptionPane.YES_OPTION) return;

        try {
            // Nếu là hóa đơn bán lẻ -> hoàn trả số lượng tồn kho cho các sản phẩm
            if (hoaDonDangChon.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG && hoaDonDangChon.getDanhSachChiTiet() != null) {
                for (ChiTietHoaDon ct : hoaDonDangChon.getDanhSachChiTiet()) {
                    if (ct.getSanPham() != null && ct.getSanPham().getMaSanPham() != null) {
                        sanPhamBUS.nhapThemHang(ct.getSanPham().getMaSanPham(), ct.getSoLuong());
                    }
                }
            }

            // Xóa hóa đơn khỏi CSDL
            if (hoaDonBUS.xoaHoaDon(hoaDonDangChon.getMaHoaDon())) {
                JOptionPane.showMessageDialog(this,
                        "Đã hủy và xóa hóa đơn [" + hoaDonDangChon.getMaHoaDon() + "] thành công!",
                        "Đã hủy đơn", JOptionPane.INFORMATION_MESSAGE);
                hoaDonDangChon = null;
                taiDanhSachHoaDonCho();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể hủy hóa đơn: " + ex.getMessage(),
                    "Lỗi hủy đơn", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý IN PHIẾU TẠM: Mở modal xem trước và in phiếu tính tiền tạm tính cho khách
     */
    private void xuLyInPhieuTam() {
        if (hoaDonDangChon == null) return;
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        QuanLyManHinhQuanLyHoaDonXemChiTiet modal =
                new QuanLyManHinhQuanLyHoaDonXemChiTiet(cuaSoCha, hoaDonDangChon, false);
        modal.setVisible(true);
    }

    /**
     * Xử lý XÁC NHẬN THANH TOÁN: Cập nhật hóa đơn sang ĐÃ THANH TOÁN
     */
    private void xuLyXacNhanThanhToan() {
        if (hoaDonDangChon == null) return;

        PhuongThucThanhToan pt = radChuyenKhoan.isSelected()
                ? PhuongThucThanhToan.CHUYEN_KHOAN : PhuongThucThanhToan.TIEN_MAT;

        double phaiThu = hoaDonDangChon.tinhTongThanhToan();

        // Kiểm tra tiền khách đưa nếu thanh toán tiền mặt
        if (pt == PhuongThucThanhToan.TIEN_MAT) {
            String raw = txtTienKhachDua.getText() != null ? txtTienKhachDua.getText().trim() : "";
            if (raw.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập số tiền khách đưa!",
                        "Chưa nhập tiền", JOptionPane.WARNING_MESSAGE);
                txtTienKhachDua.requestFocus();
                return;
            }
            try {
                double khachDua = Double.parseDouble(raw.replace(".", "").replace(",", "").replace("đ", "").trim());
                if (khachDua < phaiThu) {
                    JOptionPane.showMessageDialog(this,
                            "Số tiền khách đưa (" + dinhDangTien(khachDua) + ") chưa đủ để thanh toán (" + dinhDangTien(phaiThu) + ")!",
                            "Chưa đủ tiền", JOptionPane.WARNING_MESSAGE);
                    txtTienKhachDua.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền khách đưa không đúng định dạng!",
                        "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                txtTienKhachDua.requestFocus();
                return;
            }
        }

        String ghiChu = txtGhiChuThanhToan.getText() != null ? txtGhiChuThanhToan.getText().trim() : "";

        try {
            boolean thanhCong = hoaDonBUS.thanhToanHoaDon(hoaDonDangChon.getMaHoaDon(), pt, ghiChu);
            if (thanhCong) {
                String thongBao = "Thanh toán hóa đơn [" + hoaDonDangChon.getMaHoaDon() + "] thành công!\n"
                        + "Phương thức: " + pt.getTenHienThi() + "\n"
                        + "Tổng tiền: " + dinhDangTien(phaiThu);
                if (pt == PhuongThucThanhToan.TIEN_MAT) {
                    thongBao += "\nTiền thừa trả khách: " + lblTienThua.getText();
                }

                JOptionPane.showMessageDialog(this, thongBao,
                        "Thanh toán thành công", JOptionPane.INFORMATION_MESSAGE);

                // Mở cửa sổ in hóa đơn chính thức cho khách
                Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
                QuanLyManHinhQuanLyHoaDonXemChiTiet modal =
                        new QuanLyManHinhQuanLyHoaDonXemChiTiet(cuaSoCha, hoaDonDangChon, true);
                modal.setVisible(true);

                // Làm mới lại danh sách chờ
                hoaDonDangChon = null;
                taiDanhSachHoaDonCho();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể hoàn tất thanh toán: " + ex.getMessage(),
                    "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // ĐỊNH DẠNG & TIỆN ÍCH
    // =========================================================================
    private String dinhDangTien(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien) + " đ";
    }

    private String dinhDangSo(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien);
    }

    // =========================================================================
    // CÁC COMPONENT ĐỒ HOẠ TUỲ CHỈNH
    // =========================================================================

    private static class TheBoGoc extends JPanel {
        private final int gocBo;
        private final Color mauNen;
        private final Color mauVien;

        TheBoGoc(int gocBo, Color mauNen, Color mauVien) {
            this.gocBo = gocBo;
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, gocBo, gocBo);
            g2.setColor(mauNen);
            g2.fill(nen);
            g2.setColor(mauVien);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BadgeVang extends JLabel {
        BadgeVang(String vanBan) {
            super(vanBan, SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(new Color(146, 64, 14)); // #92400E
            setBorder(new EmptyBorder(5, 12, 5, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 14f, 14f);
            g2.setColor(new Color(254, 243, 199)); // #FEF3C7
            g2.fill(pill);
            g2.setColor(new Color(253, 230, 138)); // #FDE68A
            g2.setStroke(new BasicStroke(1f));
            g2.draw(pill);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BadgeCam extends JLabel {
        BadgeCam(String vanBan) {
            super(vanBan, SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(new Color(217, 119, 6)); // #D97706
            setBorder(new EmptyBorder(4, 10, 4, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 14f, 14f);
            g2.setColor(new Color(254, 243, 199)); // #FEF3C7
            g2.fill(pill);
            g2.setColor(new Color(253, 230, 138));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(pill);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BadgeXanh extends JLabel {
        BadgeXanh(String vanBan) {
            super(vanBan, SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(new Color(29, 78, 216)); // #1D4ED8
            setBorder(new EmptyBorder(5, 12, 5, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 14f, 14f);
            g2.setColor(new Color(239, 246, 255)); // #EFF6FF
            g2.fill(pill);
            g2.setColor(new Color(219, 234, 254)); // #DBEAFE
            g2.setStroke(new BasicStroke(1f));
            g2.draw(pill);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutVienDo extends JButton {
        private boolean hover = false;

        NutVienDo(String chu) {
            this(chu, null);
        }

        NutVienDo(String chu, Icon icon) {
            super(chu, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_DO_CANH_BAO);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
            g2.setColor(hover ? new Color(254, 226, 226) : new Color(254, 242, 242));
            g2.fill(nen);
            g2.setColor(new Color(254, 202, 202));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutVienTrang extends JButton {
        private boolean hover = false;

        NutVienTrang(String chu) {
            super(chu);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_CHINH);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
            g2.setColor(hover ? new Color(241, 245, 249) : Color.WHITE);
            g2.fill(nen);
            g2.setColor(MAU_VIEN_INPUT);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutXanhXacNhan extends JButton {
        private boolean hover = false;

        NutXanhXacNhan(String chu) {
            this(chu, null);
        }

        NutXanhXacNhan(String chu, Icon icon) {
            super(chu, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
            g2.setColor(hover ? MAU_XANH_LA_HOVER : MAU_XANH_LA);
            g2.fill(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BoVeTieuDeBang extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = new JLabel(value != null ? value.toString() : "") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(new GradientPaint(
                            0, 0, new Color(248, 250, 252),
                            0, getHeight(), new Color(226, 232, 240)
                    ));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            lbl.setOpaque(false);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(MAU_CHU_CHINH);
            lbl.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER
                    : (column == table.getColumnCount() - 1 ? SwingConstants.RIGHT : SwingConstants.LEFT));
            lbl.setBorder(new EmptyBorder(0, column == 0 ? 4 : 8, 0, column == table.getColumnCount() - 1 ? 8 : 4));
            return lbl;
        }
    }

    private static class BoVeOBangCho extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setFont(new Font("Segoe UI", (column == 1 || isSelected) ? Font.BOLD : Font.PLAIN, 12));

            if (column == 5) { // Cần thu
                setForeground(MAU_CAM_CHU_DAO);
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (column == 0) { // #
                setForeground(MAU_CHU_MO);
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setForeground(isSelected ? MAU_XANH_DAM : MAU_CHU_CHINH);
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            setBorder(new EmptyBorder(0, column == 0 ? 4 : 8, 0, column == 5 ? 8 : 4));
            return this;
        }
    }

    private static class BoVeOBangChiTiet extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(MAU_CHU_CHINH);

            if (column == 0 || column == 2) { // # hoặc SL
                setHorizontalAlignment(SwingConstants.CENTER);
            } else if (column == 3 || column == 4) { // Đơn giá, Thành tiền
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            setBorder(new EmptyBorder(0, 6, 0, 6));
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lập Hóa Đơn & Thu Ngân Thanh Toán");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 750);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new LeTanManHinhLapHoaDonThanhToan());
            frame.setVisible(true);
        });
    }
}
