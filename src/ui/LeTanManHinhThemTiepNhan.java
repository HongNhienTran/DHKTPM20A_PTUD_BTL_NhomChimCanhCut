package ui;

import bus.DichVuBUS;
import bus.HangXeBUS;
import bus.KhachHangBUS;
import bus.NhanVienBUS;
import bus.PhieuBaoGiaBUS;
import bus.PhieuPhanCongBUS;
import bus.PhieuTiepNhanBUS;
import bus.PhuongTienBUS;
import entity.ChiTietBaoGia;
import entity.DichVu;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.ChuyenMon;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiSua;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Modal Tạo Phiếu Tiếp Nhận Xe dành cho Lễ tân.
 * Thiết kế giao diện nổi (JDialog) theo chuẩn Pengu Garage:
 * - Tra cứu phương tiện theo biển số & thêm mới nhanh xe nếu chưa có.
 * - Nhập số ODO, số chìa khóa, phân công Kỹ thuật viên kiểm tra.
 * - Ghi nhận yêu cầu, tình trạng ngoại thất và nội thất xe.
 */
public class LeTanManHinhThemTiepNhan extends JDialog {

    // Bảng màu chuẩn Pengu Garage
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 124, 48);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_FORM = new Color(248, 250, 252);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);

    private final Window cuaSoCha;
    private final TaiKhoan taiKhoanDangNhap;
    private final Runnable callbackSauKhiThem;

    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();
    private final PhieuBaoGiaBUS phieuBaoGiaBUS = new PhieuBaoGiaBUS();
    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private final PhuongTienBUS phuongTienBUS = new PhuongTienBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final HangXeBUS hangXeBUS = new HangXeBUS();

    // Các thành phần giao diện
    private JLabel lblMaTiepNhanPill;
    private JTextField txtBienSoXe;
    private JButton btnTimKiemXe;
    private JButton btnThemXeNhanh;

    private JLabel lblThongTinPhuongTien;
    private JLabel lblThongTinChuXe;
    private PhuongTien phuongTienDaChon = null;

    private JTextField txtSoKM;
    private JTextField txtSoChiaKhoa;
    private JTextArea txtYeuCauHienTuong;
    private JTextArea txtTinhTrangNgoaiThat;
    private JTextArea txtTinhTrangNoiThat;

    // Quản lý hạng mục dịch vụ khách yêu cầu
    private final List<DichVu> danhSachDichVuChon = new ArrayList<>();
    private JComboBox<DichVu> cboDichVu;
    private JButton btnThemDichVu;
    private DefaultTableModel modelBangDichVu;
    private JTable bangDichVu;
    private JLabel lblHuyHieuPhanLuong;
    private JLabel lblTongTienCongDuKien;

    // Kỹ thuật viên phụ trách kiểm tra xe
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();
    private JPanel pnlChonKTVKiemTra;
    private JComboBox<NhanVien> cboKTVKiemTra;

    private JButton btnTaoTiepNhan;

    public LeTanManHinhThemTiepNhan(Window parent, TaiKhoan taiKhoanDangNhap, Runnable callbackSauKhiThem) {
        super(parent, "Tạo Phiếu Tiếp Nhận Xe", ModalityType.APPLICATION_MODAL);
        this.cuaSoCha = parent;
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        this.callbackSauKhiThem = callbackSauKhiThem;

        khoiTaoGiaoDien();
        napDanhSachDichVu();
        napDanhSachKTVKiemTra();
    }

    public LeTanManHinhThemTiepNhan(Window parent) {
        this(parent, null, null);
    }

    private void khoiTaoGiaoDien() {
        Dimension manHinh = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int rong = Math.min(1080, manHinh.width - 60);
        int cao = Math.min(840, manHinh.height - 60);
        setSize(rong, cao);
        setMinimumSize(new Dimension(920, 700));
        setLocationRelativeTo(cuaSoCha);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Header Dialog
        root.add(taoHeaderDialog(), BorderLayout.NORTH);

        // Body cuộn nội dung form
        JPanel pnlThan = new JPanel();
        pnlThan.setLayout(new BoxLayout(pnlThan, BoxLayout.Y_AXIS));
        pnlThan.setBackground(Color.WHITE);
        pnlThan.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel p1 = taoPhanThongTinXeVaKhach();
        p1.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlThan.add(p1);
        pnlThan.add(taoKhoangCach(16));

        JPanel p2 = taoPhanThongSoBanGiao();
        p2.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlThan.add(p2);
        pnlThan.add(taoKhoangCach(16));

        JPanel p3 = taoPhanDichVuYeuCau();
        p3.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlThan.add(p3);

        JScrollPane cuonThan = new JScrollPane(pnlThan);
        cuonThan.setBorder(null);
        cuonThan.getVerticalScrollBar().setUnitIncrement(16);
        cuonThan.getViewport().setBackground(Color.WHITE);
        root.add(cuonThan, BorderLayout.CENTER);

        // Footer nút tạo tiếp nhận
        root.add(taoFooterDialog(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel taoHeaderDialog() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN),
                new EmptyBorder(14, 20, 14, 20)
        ));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("Phiếu Tiếp Nhận Xe");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        // Cụm góc phải: Mã phiếu dự kiến + Nút đóng
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumPhai.setOpaque(false);

        lblMaTiepNhanPill = new JLabel("TN-TỰ ĐỘNG-01");
        lblMaTiepNhanPill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMaTiepNhanPill.setForeground(new Color(2, 132, 199));
        lblMaTiepNhanPill.setOpaque(true);
        lblMaTiepNhanPill.setBackground(new Color(224, 242, 254));
        lblMaTiepNhanPill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JButton btnDong = new JButton(IconUtil.layIcon("close.png", 12, 12, MAU_CHU_PHU));
        btnDong.setFocusPainted(false);
        btnDong.setBorderPainted(false);
        btnDong.setContentAreaFilled(false);
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.setPreferredSize(new Dimension(24, 24));
        btnDong.addActionListener(e -> dispose());

        cumPhai.add(lblMaTiepNhanPill);
        cumPhai.add(btnDong);

        header.add(lblTieuDe, BorderLayout.WEST);
        header.add(cumPhai, BorderLayout.EAST);
        return header;
    }

    private Component taoKhoangCach(int chieuCao) {
        Component strut = Box.createVerticalStrut(chieuCao);
        if (strut instanceof JComponent jComp) {
            jComp.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return strut;
    }

    private JPanel taoPhanThongTinXeVaKhach() {
        TheBoGoc the = new TheBoGoc(12, Color.WHITE, MAU_VIEN);
        the.setLayout(new BorderLayout(0, 10));
        the.setBorder(new EmptyBorder(14, 18, 14, 18));
        the.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tiêu đề mục
        JLabel lblMuc = new JLabel(" 1. Thông Tin Xe & Khách Hàng", IconUtil.layIcon("car.png", 16, 16, MAU_XANH_DAM), SwingConstants.LEFT);
        lblMuc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMuc.setForeground(MAU_XANH_DAM);
        the.add(lblMuc, BorderLayout.NORTH);

        // Khối 2 cột rộng rãi: Cột trái nhập biển số & tìm kiếm | Cột phải thẻ thông tin tóm tắt xe & chủ xe
        JPanel hangNoiDung = new JPanel(new GridLayout(1, 2, 16, 0));
        hangNoiDung.setOpaque(false);

        // Cột trái: Nhập biển số + 2 nút
        JPanel cotTrai = new JPanel(new BorderLayout(0, 6));
        cotTrai.setOpaque(false);
        JLabel lblNhapBS = new JLabel("Biển Số Phương Tiện (*)");
        lblNhapBS.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNhapBS.setForeground(MAU_CHU_PHU);
        cotTrai.add(lblNhapBS, BorderLayout.NORTH);

        JPanel pnlNhapBienSo = new JPanel(new BorderLayout(8, 0));
        pnlNhapBienSo.setOpaque(false);

        txtBienSoXe = new JTextField();
        txtBienSoXe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBienSoXe.setPreferredSize(new Dimension(0, 36));
        datGoiYNhap(txtBienSoXe, "Nhập biển số xe (VD: 51H-12345)...");
        txtBienSoXe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    xuLyTimKiemXe();
                }
            }
        });

        JPanel cumNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cumNut.setOpaque(false);

        btnTimKiemXe = new JButton(IconUtil.layIcon("search.png", 15, 15, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(24, 58, 92) : MAU_XANH_DAM);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnTimKiemXe.setPreferredSize(new Dimension(42, 36));
        btnTimKiemXe.setFocusPainted(false);
        btnTimKiemXe.setBorderPainted(false);
        btnTimKiemXe.setContentAreaFilled(false);
        btnTimKiemXe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTimKiemXe.setToolTipText("Tra cứu phương tiện theo biển số");
        btnTimKiemXe.addActionListener(e -> xuLyTimKiemXe());

        btnThemXeNhanh = new JButton(IconUtil.layIcon("plus.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(5, 150, 105) : MAU_XANH_LA);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnThemXeNhanh.setPreferredSize(new Dimension(42, 36));
        btnThemXeNhanh.setFocusPainted(false);
        btnThemXeNhanh.setBorderPainted(false);
        btnThemXeNhanh.setContentAreaFilled(false);
        btnThemXeNhanh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemXeNhanh.setToolTipText("Thêm mới phương tiện vào hệ thống");
        btnThemXeNhanh.addActionListener(e -> xuLyThemPhuongTienMoi());

        cumNut.add(btnTimKiemXe);
        cumNut.add(btnThemXeNhanh);

        pnlNhapBienSo.add(txtBienSoXe, BorderLayout.CENTER);
        pnlNhapBienSo.add(cumNut, BorderLayout.EAST);
        cotTrai.add(pnlNhapBienSo, BorderLayout.CENTER);

        // Cột phải: Thẻ tóm tắt thông tin xe và chủ xe sau khi tra cứu
        TheBoGoc theTomTat = new TheBoGoc(8, MAU_NEN_FORM, MAU_VIEN);
        theTomTat.setLayout(new GridLayout(2, 1, 0, 4));
        theTomTat.setBorder(new EmptyBorder(10, 14, 10, 14));

        lblThongTinPhuongTien = new JLabel("Phương tiện: Chưa chọn xe");
        lblThongTinPhuongTien.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblThongTinPhuongTien.setForeground(MAU_CHU_CHINH);

        lblThongTinChuXe = new JLabel("Chủ xe: -");
        lblThongTinChuXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongTinChuXe.setForeground(MAU_CHU_PHU);

        theTomTat.add(lblThongTinPhuongTien);
        theTomTat.add(lblThongTinChuXe);

        hangNoiDung.add(cotTrai);
        hangNoiDung.add(theTomTat);

        the.add(hangNoiDung, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoPhanThongSoBanGiao() {
        TheBoGoc the = new TheBoGoc(12, Color.WHITE, MAU_VIEN);
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));
        the.setBorder(new EmptyBorder(14, 18, 14, 18));
        the.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMuc = new JLabel(" 2. Thông Số Bàn Giao Xe & Hiện Trạng", IconUtil.layIcon("settings.png", 16, 16, MAU_XANH_DAM), SwingConstants.LEFT);
        lblMuc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMuc.setForeground(MAU_XANH_DAM);
        lblMuc.setAlignmentX(Component.LEFT_ALIGNMENT);

        the.add(lblMuc);
        the.add(Box.createVerticalStrut(12));

        // Hàng 1: 2 cột song song (Số ODO, Số chìa khóa) - KTV đã được chuyển xuống dưới mục 3
        JPanel hang1 = new JPanel(new GridLayout(1, 2, 16, 0));
        hang1.setOpaque(false);
        hang1.setAlignmentX(Component.LEFT_ALIGNMENT);
        hang1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        // Cột 1: ODO
        JPanel cotODO = new JPanel(new BorderLayout(0, 4));
        cotODO.setOpaque(false);
        JLabel lblODO = new JLabel("Số ODO / KM Hiện Tại (*)");
        lblODO.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblODO.setForeground(MAU_CHU_PHU);
        txtSoKM = new JTextField();
        txtSoKM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSoKM.setPreferredSize(new Dimension(0, 36));
        datGoiYNhap(txtSoKM, "VD: 45000");
        cotODO.add(lblODO, BorderLayout.NORTH);
        cotODO.add(txtSoKM, BorderLayout.CENTER);
        hang1.add(cotODO);

        // Cột 2: Số chìa khóa
        JPanel cotChiaKhoa = new JPanel(new BorderLayout(0, 4));
        cotChiaKhoa.setOpaque(false);
        JLabel lblChiaKhoa = new JLabel("Số Chìa Khóa (*)");
        lblChiaKhoa.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblChiaKhoa.setForeground(MAU_CHU_PHU);
        txtSoChiaKhoa = new JTextField("1");
        txtSoChiaKhoa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSoChiaKhoa.setPreferredSize(new Dimension(0, 36));
        cotChiaKhoa.add(lblChiaKhoa, BorderLayout.NORTH);
        cotChiaKhoa.add(txtSoChiaKhoa, BorderLayout.CENTER);
        hang1.add(cotChiaKhoa);

        the.add(hang1);
        the.add(Box.createVerticalStrut(14));

        // Hàng 2: Yêu cầu / Hiện tượng hỏng hóc (Toàn bộ chiều rộng, mở rộng ô nội dung)
        JPanel cotYeuCau = new JPanel(new BorderLayout(0, 4));
        cotYeuCau.setOpaque(false);
        cotYeuCau.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblYeuCau = new JLabel("Yêu Cầu / Hiện Tượng Hỏng Hóc Của Khách (*)");
        lblYeuCau.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblYeuCau.setForeground(MAU_CHU_PHU);

        txtYeuCauHienTuong = new JTextArea(4, 20);
        txtYeuCauHienTuong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtYeuCauHienTuong.setLineWrap(true);
        txtYeuCauHienTuong.setWrapStyleWord(true);
        txtYeuCauHienTuong.setBorder(new EmptyBorder(8, 10, 8, 10));
        datGoiYNhapArea(txtYeuCauHienTuong, "VD: Bảo dưỡng định kỳ 40.000 km, kiểm tra tiếng kêu lục cục dưới gầm khi qua gờ giảm tốc...");

        JScrollPane cuonYeuCau = new JScrollPane(txtYeuCauHienTuong);
        cuonYeuCau.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));

        cotYeuCau.add(lblYeuCau, BorderLayout.NORTH);
        cotYeuCau.add(cuonYeuCau, BorderLayout.CENTER);

        the.add(cotYeuCau);
        the.add(Box.createVerticalStrut(14));

        // Hàng 3: Hai ô tình trạng ngoại thất và nội thất song song mở rộng
        JPanel hangTinhTrang = new JPanel(new GridLayout(1, 2, 16, 0));
        hangTinhTrang.setOpaque(false);
        hangTinhTrang.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ngoại thất
        JPanel cotNgoaiThat = new JPanel(new BorderLayout(0, 4));
        cotNgoaiThat.setOpaque(false);
        JLabel lblNgoaiThat = new JLabel("Tình Trạng Ngoại Thất (Trầy xước, đèn, gương, kính...)");
        lblNgoaiThat.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNgoaiThat.setForeground(MAU_CHU_PHU);

        txtTinhTrangNgoaiThat = new JTextArea(3, 20);
        txtTinhTrangNgoaiThat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTinhTrangNgoaiThat.setLineWrap(true);
        txtTinhTrangNgoaiThat.setWrapStyleWord(true);
        txtTinhTrangNgoaiThat.setBorder(new EmptyBorder(8, 10, 8, 10));
        datGoiYNhapArea(txtTinhTrangNgoaiThat, "VD: Cản trước trầy nhẹ bên phụ, đèn hậu nguyên vẹn...");

        JScrollPane cuonNgoaiThat = new JScrollPane(txtTinhTrangNgoaiThat);
        cuonNgoaiThat.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));

        cotNgoaiThat.add(lblNgoaiThat, BorderLayout.NORTH);
        cotNgoaiThat.add(cuonNgoaiThat, BorderLayout.CENTER);

        // Nội thất & đồ đạc
        JPanel cotNoiThat = new JPanel(new BorderLayout(0, 4));
        cotNoiThat.setOpaque(false);
        JLabel lblNoiThat = new JLabel("Tình Trạng Nội Thất & Đồ Đạc Trong Xe");
        lblNoiThat.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNoiThat.setForeground(MAU_CHU_PHU);

        txtTinhTrangNoiThat = new JTextArea(3, 20);
        txtTinhTrangNoiThat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTinhTrangNoiThat.setLineWrap(true);
        txtTinhTrangNoiThat.setWrapStyleWord(true);
        txtTinhTrangNoiThat.setBorder(new EmptyBorder(8, 10, 8, 10));
        datGoiYNhapArea(txtTinhTrangNoiThat, "VD: Đồ đạc cá nhân khách đã lấy, màn hình android, ghế da...");

        JScrollPane cuonNoiThat = new JScrollPane(txtTinhTrangNoiThat);
        cuonNoiThat.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));

        cotNoiThat.add(lblNoiThat, BorderLayout.NORTH);
        cotNoiThat.add(cuonNoiThat, BorderLayout.CENTER);

        hangTinhTrang.add(cotNgoaiThat);
        hangTinhTrang.add(cotNoiThat);

        the.add(hangTinhTrang);
        return the;
    }

    private JPanel taoPhanDichVuYeuCau() {
        TheBoGoc the = new TheBoGoc(12, Color.WHITE, MAU_VIEN);
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));
        the.setBorder(new EmptyBorder(14, 18, 14, 18));
        the.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tiêu đề mục + Badge luồng tiếp nhận
        JPanel hangTieuDe = new JPanel(new BorderLayout(10, 0));
        hangTieuDe.setOpaque(false);
        hangTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        hangTieuDe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblMuc = new JLabel(" 3. Hạng Mục Dịch Vụ Khách Hàng Yêu Cầu (*)", IconUtil.layIcon("wrench.png", 16, 16, MAU_XANH_DAM), SwingConstants.LEFT);
        lblMuc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMuc.setForeground(MAU_XANH_DAM);

        lblHuyHieuPhanLuong = new JLabel("Chưa chọn dịch vụ");
        lblHuyHieuPhanLuong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHuyHieuPhanLuong.setOpaque(true);
        lblHuyHieuPhanLuong.setBackground(new Color(241, 245, 249));
        lblHuyHieuPhanLuong.setForeground(MAU_CHU_PHU);
        lblHuyHieuPhanLuong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        hangTieuDe.add(lblMuc, BorderLayout.WEST);
        hangTieuDe.add(lblHuyHieuPhanLuong, BorderLayout.EAST);
        the.add(hangTieuDe);
        the.add(Box.createVerticalStrut(12));

        // Thanh chọn dịch vụ để thêm
        JPanel hangChonDV = new JPanel(new BorderLayout(10, 0));
        hangChonDV.setOpaque(false);
        hangChonDV.setAlignmentX(Component.LEFT_ALIGNMENT);
        hangChonDV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        cboDichVu = new JComboBox<>();
        cboDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDichVu.setPreferredSize(new Dimension(0, 36));
        cboDichVu.setBackground(Color.WHITE);
        cboDichVu.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DichVu dv) {
                    String loai = dv.isCoPhuTung() ? "[Có phụ tùng]" : "[Chỉ công thợ]";
                    String cm = dv.getChuyenMon() != null ? " - " + dv.getChuyenMon().getTenHienThi() : "";
                    setText(dv.getTenDichVu() + " (" + dinhDangTien(dv.getDonGia()) + ") " + loai + cm);
                } else if (value == null) {
                    setText("-- Chọn dịch vụ muốn thêm cho xe --");
                }
                return this;
            }
        });

        btnThemDichVu = new JButton("+ Thêm Dịch Vụ", IconUtil.layIcon("plus.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(29, 78, 216) : new Color(37, 99, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnThemDichVu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemDichVu.setForeground(Color.WHITE);
        btnThemDichVu.setFocusPainted(false);
        btnThemDichVu.setBorderPainted(false);
        btnThemDichVu.setContentAreaFilled(false);
        btnThemDichVu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemDichVu.setPreferredSize(new Dimension(140, 36));
        btnThemDichVu.addActionListener(e -> xuLyThemDichVu());

        hangChonDV.add(cboDichVu, BorderLayout.CENTER);
        hangChonDV.add(btnThemDichVu, BorderLayout.EAST);
        the.add(hangChonDV);
        the.add(Box.createVerticalStrut(10));

        // Bảng danh sách dịch vụ đã chọn
        String[] tieuDeCot = {"STT", "Mã DV", "Tên Dịch Vụ", "Nhóm Chuyên Môn", "Đơn Giá Công", "Loại Dịch Vụ", "Xóa"};
        modelBangDichVu = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangDichVu = new JTable(modelBangDichVu);
        bangDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangDichVu.setRowHeight(32);
        bangDichVu.setShowGrid(true);
        bangDichVu.setGridColor(new Color(241, 245, 249));
        bangDichVu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bangDichVu.getTableHeader().setBackground(new Color(248, 250, 252));
        bangDichVu.getTableHeader().setForeground(MAU_CHU_CHINH);

        // Định dạng cột
        TableColumnModel colModel = bangDichVu.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(45);
        colModel.getColumn(1).setPreferredWidth(75);
        colModel.getColumn(2).setPreferredWidth(260);
        colModel.getColumn(3).setPreferredWidth(190);
        colModel.getColumn(4).setPreferredWidth(110);
        colModel.getColumn(5).setPreferredWidth(120);
        colModel.getColumn(6).setPreferredWidth(50);

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        colModel.getColumn(0).setCellRenderer(canGiua);
        colModel.getColumn(1).setCellRenderer(canGiua);
        colModel.getColumn(5).setCellRenderer(canGiua);

        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);
        colModel.getColumn(4).setCellRenderer(canPhai);

        DefaultTableCellRenderer nutXoa = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(new Color(239, 68, 68));
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return this;
            }
        };
        colModel.getColumn(6).setCellRenderer(nutXoa);

        bangDichVu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bangDichVu.rowAtPoint(e.getPoint());
                int col = bangDichVu.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 6) {
                    xuLyXoaDichVu(row);
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangDichVu);
        cuonBang.setPreferredSize(new Dimension(0, 140));
        cuonBang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        cuonBang.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cuonBang.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.add(cuonBang);
        the.add(Box.createVerticalStrut(10));

        // Thanh tổng kết & chú thích
        JPanel hangTongKet = new JPanel(new BorderLayout(10, 0));
        hangTongKet.setOpaque(false);
        hangTongKet.setAlignmentX(Component.LEFT_ALIGNMENT);
        hangTongKet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lblGhiChuPhuTung = new JLabel("*(Giá công niêm yết, chưa gồm phụ tùng phát sinh nếu có khi KTV khám xe)*");
        lblGhiChuPhuTung.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblGhiChuPhuTung.setForeground(MAU_CHU_PHU);

        lblTongTienCongDuKien = new JLabel("Tổng tiền công dịch vụ dự kiến: 0 đ");
        lblTongTienCongDuKien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongTienCongDuKien.setForeground(MAU_CAM_CHU_DAO);

        hangTongKet.add(lblGhiChuPhuTung, BorderLayout.WEST);
        hangTongKet.add(lblTongTienCongDuKien, BorderLayout.EAST);
        the.add(hangTongKet);

        the.add(Box.createVerticalStrut(12));

        // Khung chọn Kỹ Thuật Viên Phụ Trách Kiểm Tra Xe (Chuyên Môn Bảo Dưỡng)
        pnlChonKTVKiemTra = new JPanel(new BorderLayout(8, 4));
        pnlChonKTVKiemTra.setOpaque(false);
        pnlChonKTVKiemTra.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlChonKTVKiemTra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

        JPanel hangNhanKTV = new JPanel(new BorderLayout());
        hangNhanKTV.setOpaque(false);
        JLabel lblTieuDeKTV = new JLabel("Kỹ Thuật Viên Phụ Trách Kiểm Tra Xe (*):");
        lblTieuDeKTV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDeKTV.setForeground(MAU_CHU_CHINH);

        JLabel lblGhiChuKTV = new JLabel("*(Ưu tiên kỹ thuật viên chuyên môn Bảo dưỡng & đang rảnh việc nhất)*");
        lblGhiChuKTV.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblGhiChuKTV.setForeground(MAU_CHU_PHU);

        hangNhanKTV.add(lblTieuDeKTV, BorderLayout.WEST);
        hangNhanKTV.add(lblGhiChuKTV, BorderLayout.EAST);
        pnlChonKTVKiemTra.add(hangNhanKTV, BorderLayout.NORTH);

        cboKTVKiemTra = new JComboBox<>();
        cboKTVKiemTra.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboKTVKiemTra.setPreferredSize(new Dimension(0, 36));
        cboKTVKiemTra.setBackground(Color.WHITE);
        cboKTVKiemTra.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhanVien nv) {
                    int soViec = phieuPhanCongBUS.demSoViecDangXuLy(nv.getMaNhanVien());
                    String trangThaiTai = (soViec == 0) ? " • (Rảnh tay)" : " • (Đang xử lý " + soViec + " việc)";
                    String cm = (nv.getChuyenMon() != null) ? nv.getChuyenMon().getTenHienThi() : "Kỹ thuật viên";
                    setText(nv.getHoTen() + " (" + nv.getMaNhanVien() + ") - " + cm + trangThaiTai);
                    setIcon(IconUtil.layIcon("wrench.png", 14, 14, isSelected ? Color.WHITE : MAU_CAM_CHU_DAO));
                } else if (value == null) {
                    setText("-- Chọn KTV chuyên môn Bảo Dưỡng phụ trách kiểm tra xe --");
                    setIcon(null);
                }
                return this;
            }
        });
        pnlChonKTVKiemTra.add(cboKTVKiemTra, BorderLayout.CENTER);
        the.add(pnlChonKTVKiemTra);

        return the;
    }

    private void napDanhSachKTVKiemTra() {
        try {
            cboKTVKiemTra.removeAllItems();
            // Lọc KTV chuyên môn Bảo Dưỡng để kiểm tra xe
            List<NhanVien> dsKTV = nhanVienBUS.layKyThuatVienTheoChuyenMon(ChuyenMon.CHUYEN_BAO_DUONG);
            if (dsKTV == null || dsKTV.isEmpty()) {
                dsKTV = nhanVienBUS.layKyThuatVienDangLamViec();
            }

            java.util.Map<String, Integer> mapTai = phieuPhanCongBUS.laySoLuongViecDangXuLyCuaTatCaKTV();

            // Sắp xếp ưu tiên người đang rảnh việc nhất (ít phiếu đang xử lý nhất)
            dsKTV.sort(java.util.Comparator.comparingInt(nv -> mapTai.getOrDefault(nv.getMaNhanVien(), 0)));

            for (NhanVien nv : dsKTV) {
                cboKTVKiemTra.addItem(nv);
            }
        } catch (Exception ex) {
            System.err.println("Lỗi nạp danh sách KTV kiểm tra: " + ex.getMessage());
        }
    }

    private static String dinhDangTien(double tien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0 đ", symbols);
        return df.format(tien);
    }

    private void napDanhSachDichVu() {
        try {
            List<DichVu> ds = dichVuBUS.layDanhSachDangKinhDoanh();
            cboDichVu.removeAllItems();
            for (DichVu dv : ds) {
                cboDichVu.addItem(dv);
            }
            // Mặc định chọn DV000 Kiểm tra & chẩn đoán nếu có
            for (DichVu dv : ds) {
                if ("DV000".equalsIgnoreCase(dv.getMaDichVu())) {
                    cboDichVu.setSelectedItem(dv);
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Lỗi nạp danh sách dịch vụ: " + ex.getMessage());
        }
    }

    private void xuLyThemDichVu() {
        DichVu dv = (DichVu) cboDichVu.getSelectedItem();
        if (dv == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dịch vụ từ danh sách!", "Chưa chọn dịch vụ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (DichVu d : danhSachDichVuChon) {
            if (d.getMaDichVu().equalsIgnoreCase(dv.getMaDichVu())) {
                JOptionPane.showMessageDialog(this, "Dịch vụ [" + dv.getTenDichVu() + "] đã có trong danh sách yêu cầu!", "Đã tồn tại", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        danhSachDichVuChon.add(dv);
        capNhatBangDichVu();
    }

    private void xuLyXoaDichVu(int row) {
        if (row >= 0 && row < danhSachDichVuChon.size()) {
            danhSachDichVuChon.remove(row);
            capNhatBangDichVu();
        }
    }

    private void capNhatBangDichVu() {
        modelBangDichVu.setRowCount(0);
        double tongTien = 0;
        int stt = 1;
        for (DichVu dv : danhSachDichVuChon) {
            String loai = dv.isCoPhuTung() ? "Có phụ tùng" : "Chỉ công thợ";
            String cm = dv.getChuyenMon() != null ? dv.getChuyenMon().getTenHienThi() : "-";
            modelBangDichVu.addRow(new Object[]{
                    stt++,
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    cm,
                    dinhDangTien(dv.getDonGia()),
                    loai,
                    "✕"
            });
            tongTien += dv.getDonGia();
        }
        lblTongTienCongDuKien.setText("Tổng tiền công dịch vụ dự kiến: " + dinhDangTien(tongTien));
        capNhatTrangThaiPhanLuong();
    }

    private void capNhatTrangThaiPhanLuong() {
        if (danhSachDichVuChon.isEmpty()) {
            lblHuyHieuPhanLuong.setText("Chưa chọn dịch vụ");
            lblHuyHieuPhanLuong.setBackground(new Color(241, 245, 249));
            lblHuyHieuPhanLuong.setForeground(MAU_CHU_PHU);
            if (pnlChonKTVKiemTra != null) {
                pnlChonKTVKiemTra.setVisible(false);
            }
            return;
        }
        boolean canKTV = dichVuBUS.canKiemTraKTV(danhSachDichVuChon);
        if (!canKTV) {
            lblHuyHieuPhanLuong.setText("⚡ Tiếp Nhận Nhanh: 100% không dùng phụ tùng (Bỏ qua bước khám xe -> Chờ báo giá/phân công)");
            lblHuyHieuPhanLuong.setBackground(new Color(220, 252, 231));
            lblHuyHieuPhanLuong.setForeground(new Color(21, 128, 61));
            if (pnlChonKTVKiemTra != null) {
                pnlChonKTVKiemTra.setVisible(false);
            }
        } else {
            lblHuyHieuPhanLuong.setText("🛠️ Luồng Tiêu Chuẩn: Có phụ tùng / chẩn đoán (Xe vào xưởng chờ phân công KTV kiểm tra)");
            lblHuyHieuPhanLuong.setBackground(new Color(254, 243, 199));
            lblHuyHieuPhanLuong.setForeground(new Color(180, 83, 9));
            if (pnlChonKTVKiemTra != null) {
                pnlChonKTVKiemTra.setVisible(true);
            }
        }
    }

    private JPanel taoFooterDialog() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN),
                new EmptyBorder(12, 20, 14, 20)
        ));

        btnTaoTiepNhan = new JButton("Tạo Tiếp Nhận", IconUtil.layIcon("plus.png", 16, 16, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_CAM_HOVER : MAU_CAM_CHU_DAO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnTaoTiepNhan.setIconTextGap(8);
        btnTaoTiepNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTaoTiepNhan.setForeground(Color.WHITE);
        btnTaoTiepNhan.setFocusPainted(false);
        btnTaoTiepNhan.setBorderPainted(false);
        btnTaoTiepNhan.setContentAreaFilled(false);
        btnTaoTiepNhan.setOpaque(false);
        btnTaoTiepNhan.setPreferredSize(new Dimension(0, 44));
        btnTaoTiepNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTaoTiepNhan.addActionListener(e -> xuLyTaoTiepNhan());

        footer.add(btnTaoTiepNhan, BorderLayout.CENTER);
        return footer;
    }

    private void xuLyTimKiemXe() {
        String bienSo = txtBienSoXe.getText().trim();
        if (bienSo.isEmpty() || bienSo.startsWith("Nhập biển số xe")) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập biển số xe cần tra cứu!",
                    "Chưa nhập biển số", JOptionPane.WARNING_MESSAGE);
            txtBienSoXe.requestFocus();
            return;
        }

        try {
            PhuongTien pt = phuongTienBUS.timTheoBienSo(bienSo);
            if (pt != null) {
                capNhatThongTinXeDaChon(pt);
            } else {
                int luaChon = JOptionPane.showConfirmDialog(this,
                        "Không tìm thấy phương tiện biển số [" + bienSo + "] trong hệ thống.\n" +
                                "Bạn có muốn thêm mới hồ sơ xe này ngay không?",
                        "Không tìm thấy xe", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (luaChon == JOptionPane.YES_OPTION) {
                    xuLyThemPhuongTienMoi(bienSo);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tra cứu phương tiện: " + ex.getMessage(),
                    "Lỗi tra cứu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatThongTinXeDaChon(PhuongTien pt) {
        this.phuongTienDaChon = pt;
        txtBienSoXe.setText(pt.getBienSoXe());
        txtBienSoXe.setForeground(MAU_CHU_CHINH);

        String hang = pt.getHangXe() != null ? pt.getHangXe().getTenHang() : "";
        String mauXe = pt.getMauXe() != null ? pt.getMauXe() : "";
        String nhienLieu = pt.getNhienLieu() != null ? pt.getNhienLieu().getTenHienThi() : "";
        lblThongTinPhuongTien.setText("Phương tiện: " + mauXe + " - " + hang + " (" + pt.getNamSanXuat() + ", " + nhienLieu + ")");

        KhachHang kh = pt.getKhachHang();
        if (kh != null) {
            lblThongTinChuXe.setText("Chủ xe: " + kh.getHoTen() + " - " + kh.getSoDienThoai());
        } else {
            lblThongTinChuXe.setText("Chủ xe: Chưa rõ");
        }

        try {
            String maGoiY = phieuTiepNhanBUS.phatSinhMaTiepNhan(pt.getMaPhuongTien(), LocalDateTime.now());
            lblMaTiepNhanPill.setText(maGoiY);
        } catch (Exception ignored) {
        }
    }

    private void xuLyThemPhuongTienMoi() {
        String bienSoHienTai = txtBienSoXe.getText().trim();
        if (bienSoHienTai.startsWith("Nhập biển số xe")) {
            bienSoHienTai = "";
        }
        xuLyThemPhuongTienMoi(bienSoHienTai);
    }

    private void xuLyThemPhuongTienMoi(String bienSoGoiY) {
        QuanLyManHinhQuanLyPhuongTienXemChiTiet modal = new QuanLyManHinhQuanLyPhuongTienXemChiTiet(
                this,
                null,
                null,
                phuongTienBUS,
                phieuTiepNhanBUS,
                hangXeBUS,
                khachHangBUS
        );
        modal.setVisible(true);

        // Sau khi đóng modal, thử kiểm tra xem có xe mới tra cứu được không
        if (bienSoGoiY != null && !bienSoGoiY.isEmpty()) {
            PhuongTien ptMoi = phuongTienBUS.timTheoBienSo(bienSoGoiY);
            if (ptMoi != null) {
                capNhatThongTinXeDaChon(ptMoi);
            }
        }
    }

    private void xuLyTaoTiepNhan() {
        if (phuongTienDaChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng tra cứu hoặc chọn một phương tiện để tiếp nhận!",
                    "Chưa chọn xe", JOptionPane.WARNING_MESSAGE);
            txtBienSoXe.requestFocus();
            return;
        }

        String strODO = txtSoKM.getText().trim();
        if (strODO.isEmpty() || strODO.startsWith("VD:")) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số ODO / KM hiện tại của xe!",
                    "Thiếu số KM", JOptionPane.WARNING_MESSAGE);
            txtSoKM.requestFocus();
            return;
        }

        double soKM;
        try {
            soKM = Double.parseDouble(strODO.replace(",", "."));
            if (soKM < 0) {
                JOptionPane.showMessageDialog(this, "Số KM hiện tại không được là số âm!", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số KM hiện tại phải là một con số hợp lệ!", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int soChiaKhoa;
        try {
            soChiaKhoa = Integer.parseInt(txtSoChiaKhoa.getText().trim());
            if (soChiaKhoa < 1) {
                JOptionPane.showMessageDialog(this, "Số chìa khóa phải từ 1 trở lên!", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số chìa khóa phải là số nguyên hợp lệ!", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (danhSachDichVuChon.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng thêm ít nhất 1 dịch vụ khách yêu cầu thực hiện\n(hoặc chọn 'Kiểm tra & chẩn đoán tổng quát' nếu chưa rõ bệnh xe)!",
                    "Chưa chọn dịch vụ", JOptionPane.WARNING_MESSAGE);
            cboDichVu.requestFocus();
            return;
        }

        boolean canKTV = dichVuBUS.canKiemTraKTV(danhSachDichVuChon);
        NhanVien ktvKiemTra = null;
        if (canKTV) {
            ktvKiemTra = (NhanVien) cboKTVKiemTra.getSelectedItem();
            if (ktvKiemTra == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn Kỹ thuật viên (chuyên môn Bảo Dưỡng) phụ trách kiểm tra xe!",
                        "Chưa chọn KTV kiểm tra", JOptionPane.WARNING_MESSAGE);
                cboKTVKiemTra.requestFocus();
                return;
            }
        }

        String yeuCau = txtYeuCauHienTuong.getText().trim();
        if (yeuCau.isEmpty() || yeuCau.startsWith("VD:")) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập Yêu cầu / Hiện tượng hỏng hóc của khách hàng!",
                    "Thiếu yêu cầu hỏng hóc", JOptionPane.WARNING_MESSAGE);
            txtYeuCauHienTuong.requestFocus();
            return;
        }

        String ngoaiThat = txtTinhTrangNgoaiThat.getText().trim();
        if (ngoaiThat.startsWith("VD:")) ngoaiThat = "";

        String noiThat = txtTinhTrangNoiThat.getText().trim();
        if (noiThat.startsWith("VD:")) noiThat = "";

        // Tìm nhân viên Lễ tân đang lập phiếu
        NhanVien nvLeTan = layNhanVienLeTanHienTai();
        if (nvLeTan == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin nhân viên Lễ tân đang thực hiện thao tác!",
                    "Lỗi tài khoản", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime thoiGian = LocalDateTime.now();
        String maTiepNhan = phieuTiepNhanBUS.phatSinhMaTiepNhan(phuongTienDaChon.getMaPhuongTien(), thoiGian);
        String ghiChuTiepNhan = canKTV
                ? "[KTV kiểm tra: " + ktvKiemTra.getMaNhanVien() + " - " + ktvKiemTra.getHoTen() + "] "
                  + "Xe vào xưởng chờ KTV kiểm tra chẩn đoán toàn diện."
                : "⚡ Tiếp nhận nhanh: Bỏ qua bước KTV kiểm tra xe (100% không dùng phụ tùng)";

        TrangThaiPhieuTiepNhan trangThaiBanDau = canKTV
                ? TrangThaiPhieuTiepNhan.DANG_KIEM_TRA
                : TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA;

        PhieuTiepNhan ptn = new PhieuTiepNhan(
                maTiepNhan,
                thoiGian,
                trangThaiBanDau,
                soKM,
                yeuCau,
                noiThat,
                ngoaiThat,
                soChiaKhoa,
                ghiChuTiepNhan,
                phuongTienDaChon,
                nvLeTan
        );

        try {
            boolean thanhCong = phieuTiepNhanBUS.lapPhieuTiepNhan(ptn);
            if (thanhCong) {
                // Tự động khởi tạo phiếu báo giá ban đầu chứa các dịch vụ khách đã chọn
                PhieuBaoGia pbgBanDau = null;
                try {
                    pbgBanDau = phieuBaoGiaBUS.khoiTaoBaoGiaBanDau(ptn, canKTV ? ktvKiemTra : nvLeTan, danhSachDichVuChon);
                } catch (Exception exBG) {
                    System.err.println("Lỗi khởi tạo báo giá ban đầu: " + exBG.getMessage());
                }

                // Nếu cần KTV kiểm tra: Phân công nhiệm vụ kiểm tra cho KTV phụ trách với trạng thái sửa KIEM_TRA
                if (canKTV && ktvKiemTra != null && pbgBanDau != null && pbgBanDau.getDanhSachChiTiet() != null && !pbgBanDau.getDanhSachChiTiet().isEmpty()) {
                    ChiTietBaoGia ctKiemTra = pbgBanDau.getDanhSachChiTiet().get(0);
                    for (ChiTietBaoGia ct : pbgBanDau.getDanhSachChiTiet()) {
                        if ("DV000".equalsIgnoreCase(ct.getDichVu().getMaDichVu())) {
                            ctKiemTra = ct;
                            break;
                        }
                    }
                    try {
                        PhieuPhanCong ppcKiemTra = new PhieuPhanCong();
                        ppcKiemTra.setMaPhanCong(phieuPhanCongBUS.phatSinhMaPhanCong(ptn.getMaTiepNhan(), ktvKiemTra.getMaNhanVien()));
                        ppcKiemTra.setNhanVien(ktvKiemTra);
                        ppcKiemTra.setChiTietBaoGia(ctKiemTra);
                        ppcKiemTra.setTrangThaiSua(TrangThaiSua.KIEM_TRA);
                        ppcKiemTra.setThoiGianPhanCong(LocalDateTime.now());
                        ppcKiemTra.setThoiGianBatDau(LocalDateTime.now());
                        ppcKiemTra.setGhiChu("Nhiệm vụ kiểm tra chẩn đoán toàn diện xe tại xưởng");
                        phieuPhanCongBUS.phanCongHangMuc(ppcKiemTra);
                    } catch (Exception exPC) {
                        System.err.println("Lỗi phân công KTV kiểm tra: " + exPC.getMessage());
                    }
                }

                if (canKTV) {
                    JOptionPane.showMessageDialog(this,
                            "Tạo phiếu tiếp nhận xe thành công!\n"
                                    + "Mã phiếu: " + maTiepNhan + "\n"
                                    + "Biển số xe: " + phuongTienDaChon.getBienSoXe() + "\n"
                                    + "KTV phụ trách kiểm tra: " + ktvKiemTra.getHoTen() + " (" + ktvKiemTra.getMaNhanVien() + ")\n"
                                    + "Quy trình: [Luồng tiêu chuẩn] - Xe đã vào xưởng và chuyển sang trạng thái [ĐANG KIỂM TRA].",
                            "Tiếp nhận thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "⚡ TIẾP NHẬN NHANH THÀNH CÔNG!\n"
                                    + "Mã phiếu: " + maTiepNhan + "\n"
                                    + "Biển số xe: " + phuongTienDaChon.getBienSoXe() + "\n"
                                    + "Tất cả dịch vụ đều không cần phụ tùng => Bỏ qua bước KTV khám xe.\n"
                                    + "Xe đã chuyển thẳng sang trạng thái [CHỜ XÁC NHẬN BÁO GIÁ].",
                            "Tiếp nhận nhanh thành công", JOptionPane.INFORMATION_MESSAGE);
                }

                if (callbackSauKhiThem != null) {
                    callbackSauKhiThem.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu phiếu tiếp nhận vào hệ thống!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lập phiếu tiếp nhận: " + ex.getMessage(),
                    "Lỗi lưu trữ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private NhanVien layNhanVienLeTanHienTai() {
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNhanVien() != null) {
            return taiKhoanDangNhap.getMaNhanVien();
        }
        // Fallback: Tìm nhân viên Lễ tân đầu tiên trong hệ thống
        try {
            List<NhanVien> dsLT = nhanVienBUS.layTheoChucVu(ChucVu.LE_TAN);
            for (NhanVien nv : dsLT) {
                if (nv.isTrangThai()) return nv;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void datGoiYNhap(JTextField field, String prompt) {
        field.setText(prompt);
        field.setForeground(MAU_CHU_PHU);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(prompt)) {
                    field.setText("");
                    field.setForeground(MAU_CHU_CHINH);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(prompt);
                    field.setForeground(MAU_CHU_PHU);
                }
            }
        });
    }

    private void datGoiYNhapArea(JTextArea area, String prompt) {
        area.setText(prompt);
        area.setForeground(MAU_CHU_PHU);
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(prompt)) {
                    area.setText("");
                    area.setForeground(MAU_CHU_CHINH);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().trim().isEmpty()) {
                    area.setText(prompt);
                    area.setForeground(MAU_CHU_PHU);
                }
            }
        });
    }

    // Panel bo góc
    private static class TheBoGoc extends JPanel {
        private final int banKinh;
        private final Color mauNen;
        private final Color mauVien;

        TheBoGoc(int banKinh, Color mauNen, Color mauVien) {
            this.banKinh = banKinh;
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D rect = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, banKinh, banKinh);
            g2.setColor(mauNen);
            g2.fill(rect);
            if (mauVien != null) {
                g2.setColor(mauVien);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(400, 300);
            f.setLocationRelativeTo(null);
            f.setVisible(true);

            new LeTanManHinhThemTiepNhan(f, null, () -> System.out.println("Callback invoked")).setVisible(true);
        });
    }
}
