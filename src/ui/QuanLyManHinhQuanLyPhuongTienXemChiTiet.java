package ui;

import bus.HangXeBUS;
import bus.KhachHangBUS;
import bus.PhieuTiepNhanBUS;
import bus.PhuongTienBUS;
import entity.HangXe;
import entity.KhachHang;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import enums.NhienLieu;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
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
import java.awt.geom.RoundRectangle2D;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Modal hiển thị toàn bộ hồ sơ chi tiết phương tiện, thông tin chủ sở hữu
 * và lịch sử tiếp nhận bảo dưỡng / sửa chữa tại gara.
 * Cho phép xem chi tiết và cập nhật trực tiếp thông số xe một cách trực quan.
 */
public class QuanLyManHinhQuanLyPhuongTienXemChiTiet extends JDialog {

    // Bảng màu đồng bộ thương hiệu Pengu Garage
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_NHAT = new Color(27, 63, 98);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 132, 52);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_FORM = new Color(248, 250, 252);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);
    private static final Color MAU_CHON_DONG = new Color(255, 247, 237);

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private PhuongTien phuongTien;
    private final PhuongTienBUS phuongTienBUS;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS;
    private final HangXeBUS hangXeBUS;
    private final KhachHangBUS khachHangBUS;

    private KhachHang khachHangMacDinh;
    private boolean daCapNhat = false;
    private List<PhieuTiepNhan> danhSachLichSu = new ArrayList<>();

    // Các thành phần form thông tin xe
    private JTextField txtMaPT;
    private JTextField txtBienSo;
    private JComboBox<HangXe> cboHangXe;
    private JTextField txtMauXe;
    private JTextField txtMauSac;
    private JTextField txtNamSX;
    private JComboBox<NhienLieu> cboNhienLieu;

    // Các trường thông tin chủ sở hữu
    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSoDienThoai;
    private JTextField txtDiaChiKH;
    private JComboBox<String> cboGioiTinhKH;
    private JLabel lblLoaiKH;
    private JButton btnTimKH;
    private JButton btnKhachMoi;
    private JButton btnDoiKH;

    private JLabel lblPhu;
    private JLabel lblBienSoBadge;

    private KhachHang khachHangDaChon = null;
    private boolean laKhachHangMoi = false;

    // Bảng lịch sử tiếp nhận
    private DefaultTableModel moHinhBangLichSu;
    private JTable bangLichSu;
    private JLabel lblSoLuotSuaChua;

    public QuanLyManHinhQuanLyPhuongTienXemChiTiet(Window cuaSoCha, PhuongTien phuongTien) {
        this(cuaSoCha, phuongTien, null, new PhuongTienBUS(), new PhieuTiepNhanBUS(), new HangXeBUS(), new KhachHangBUS());
    }

    public QuanLyManHinhQuanLyPhuongTienXemChiTiet(Window cuaSoCha, PhuongTien phuongTien, KhachHang khachHangMacDinh) {
        this(cuaSoCha, phuongTien, khachHangMacDinh, new PhuongTienBUS(), new PhieuTiepNhanBUS(), new HangXeBUS(), new KhachHangBUS());
    }

    public QuanLyManHinhQuanLyPhuongTienXemChiTiet(Window cuaSoCha, PhuongTien phuongTien,
                                                   PhuongTienBUS phuongTienBUS, PhieuTiepNhanBUS phieuTiepNhanBUS,
                                                   HangXeBUS hangXeBUS, KhachHangBUS khachHangBUS) {
        this(cuaSoCha, phuongTien, null, phuongTienBUS, phieuTiepNhanBUS, hangXeBUS, khachHangBUS);
    }

    public QuanLyManHinhQuanLyPhuongTienXemChiTiet(Window cuaSoCha, PhuongTien phuongTien, KhachHang khachHangMacDinh,
                                                   PhuongTienBUS phuongTienBUS, PhieuTiepNhanBUS phieuTiepNhanBUS,
                                                   HangXeBUS hangXeBUS, KhachHangBUS khachHangBUS) {
        super(cuaSoCha, "Hồ Sơ Chi Tiết Phương Tiện - " + (phuongTien != null ? phuongTien.getBienSoXe() : "Thêm Xe Mới"),
                ModalityType.APPLICATION_MODAL);
        this.phuongTien = phuongTien;
        this.khachHangMacDinh = khachHangMacDinh;
        this.phuongTienBUS = phuongTienBUS != null ? phuongTienBUS : new PhuongTienBUS();
        this.phieuTiepNhanBUS = phieuTiepNhanBUS != null ? phieuTiepNhanBUS : new PhieuTiepNhanBUS();
        this.hangXeBUS = hangXeBUS != null ? hangXeBUS : new HangXeBUS();
        this.khachHangBUS = khachHangBUS != null ? khachHangBUS : new KhachHangBUS();

        taiLichSuTiepNhan();
        khoiTaoGiaoDien();
        napDuLieuVaoGiaoDien();
    }

    public boolean isDaCapNhat() {
        return daCapNhat;
    }

    public PhuongTien getPhuongTien() {
        return phuongTien;
    }

    private void taiLichSuTiepNhan() {
        if (phuongTien == null) {
            danhSachLichSu = new ArrayList<>();
            return;
        }
        try {
            danhSachLichSu = phieuTiepNhanBUS.layTheoPhuongTien(phuongTien.getMaPhuongTien());
            if (danhSachLichSu == null) {
                danhSachLichSu = new ArrayList<>();
            }
        } catch (Exception ex) {
            danhSachLichSu = new ArrayList<>();
        }
    }

    private void khoiTaoGiaoDien() {
        // Tăng kích thước modal rộng rãi để xem trọn vẹn thông tin kỹ thuật & nhật ký tiếp nhận mà không cần cuộn
        setSize(1140, 840);
        setMinimumSize(new Dimension(1000, 760));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 1. Header sắc xanh navy thương hiệu
        add(taoHeaderModal(), BorderLayout.NORTH);

        // 2. Nội dung chính dạng thẻ cuộn
        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setBackground(MAU_NEN_FORM);
        noiDung.setBorder(new EmptyBorder(16, 20, 16, 20));

        noiDung.add(taoTheThongTinXeVaChuSoHuu());
        noiDung.add(Box.createVerticalStrut(14));
        noiDung.add(taoTheLichSuTiepNhan());

        JScrollPane cuon = new JScrollPane(noiDung);
        cuon.setBorder(null);
        cuon.getVerticalScrollBar().setUnitIncrement(14);
        cuon.getViewport().setBackground(MAU_NEN_FORM);
        add(cuon, BorderLayout.CENTER);

        // 3. Footer các nút lưu / đóng
        add(taoFooterModal(), BorderLayout.SOUTH);
    }

    private JPanel taoHeaderModal() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, MAU_XANH_DAM, getWidth(), getHeight(), MAU_XANH_NHAT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Cụm trái: Tiêu đề + Thông tin xe tóm tắt
        JPanel cumTrai = new JPanel();
        cumTrai.setOpaque(false);
        cumTrai.setLayout(new BoxLayout(cumTrai, BoxLayout.Y_AXIS));

        JLabel lblTieuDe = new JLabel(phuongTien != null ? "HỒ SƠ CHI TIẾT PHƯƠNG TIỆN" : "ĐĂNG KÝ PHƯƠNG TIỆN MỚI");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTieuDe.setForeground(Color.WHITE);

        String hangTen = phuongTien != null && phuongTien.getHangXe() != null ? phuongTien.getHangXe().getTenHang() : "Chưa rõ";
        String mauXe = phuongTien != null && phuongTien.getMauXe() != null ? phuongTien.getMauXe() : "";
        String maPT = phuongTien != null && phuongTien.getMaPhuongTien() != null ? phuongTien.getMaPhuongTien() : "Tự động sinh";

        lblPhu = new JLabel("Mã PT: " + maPT + "  |  Hãng & Dòng xe: " + hangTen + " " + mauXe);
        lblPhu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhu.setForeground(new Color(203, 213, 225));
        lblPhu.setBorder(new EmptyBorder(4, 0, 0, 0));

        cumTrai.add(lblTieuDe);
        cumTrai.add(lblPhu);

        // Cụm phải: Biển số xe dạng badge sang trọng
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cumPhai.setOpaque(false);

        String bienSoHienThi = phuongTien != null && phuongTien.getBienSoXe() != null
                ? phuongTien.getBienSoXe()
                : "CHƯA CÓ BIỂN SỐ";

        lblBienSoBadge = new JLabel(bienSoHienThi) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fill(rect);
                g2.setColor(new Color(254, 215, 170));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBienSoBadge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBienSoBadge.setForeground(new Color(254, 215, 170));
        lblBienSoBadge.setBorder(new EmptyBorder(6, 16, 6, 16));

        cumPhai.add(lblBienSoBadge);

        header.add(cumTrai, BorderLayout.WEST);
        header.add(cumPhai, BorderLayout.EAST);
        return header;
    }

    /**
     * Thẻ 1: Thông tin kỹ thuật xe (bên trái) + Thông tin chủ xe (bên phải)
     */
    private JPanel taoTheThongTinXeVaChuSoHuu() {
        JPanel the = taoTheKhungBoGoc("1. THÔNG TIN HỒ SƠ PHƯƠNG TIỆN & CHỦ SỞ HỮU");
        JPanel noiDung = new JPanel(new GridLayout(1, 2, 20, 0));
        noiDung.setOpaque(false);

        // --- Cột 1: Thông số kỹ thuật phương tiện ---
        JPanel cotXe = new JPanel(new GridBagLayout());
        cotXe.setOpaque(false);
        GridBagConstraints gbcXe = new GridBagConstraints();
        gbcXe.fill = GridBagConstraints.HORIZONTAL;
        gbcXe.insets = new Insets(5, 5, 5, 5);

        txtMaPT = taoOVanBan(false);
        txtMaPT.setBackground(new Color(241, 245, 249));
        txtMaPT.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtMaPT.setForeground(MAU_XANH_DAM);

        txtBienSo = taoOVanBan(true);
        txtBienSo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtBienSo.getDocument().addDocumentListener(new DocumentListener() {
            private void capNhat() {
                String bs = txtBienSo.getText() != null ? txtBienSo.getText().trim() : "";
                if (lblBienSoBadge != null) {
                    lblBienSoBadge.setText(bs.isEmpty() ? "CHƯA CÓ BIỂN SỐ" : bs.toUpperCase());
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { capNhat(); }
            @Override public void removeUpdate(DocumentEvent e) { capNhat(); }
            @Override public void changedUpdate(DocumentEvent e) { capNhat(); }
        });

        cboHangXe = new JComboBox<>();
        cboHangXe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboHangXe.setBackground(Color.WHITE);
        cboHangXe.setPreferredSize(new Dimension(0, 36));
        cboHangXe.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof HangXe hx) {
                    setText(hx.getTenHang() + " (" + hx.getQuocGia() + ")");
                }
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        napHangXeVaoCombo();
        cboHangXe.addActionListener(e -> capNhatThongTinHeader());

        txtMauXe = taoOVanBan(true);
        txtMauXe.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { capNhatThongTinHeader(); }
            @Override public void removeUpdate(DocumentEvent e) { capNhatThongTinHeader(); }
            @Override public void changedUpdate(DocumentEvent e) { capNhatThongTinHeader(); }
        });

        txtMauSac = taoOVanBan(true);
        txtNamSX = taoOVanBan(true);

        cboNhienLieu = new JComboBox<>(NhienLieu.values());
        cboNhienLieu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboNhienLieu.setBackground(Color.WHITE);
        cboNhienLieu.setPreferredSize(new Dimension(0, 36));

        themTruongForm(cotXe, gbcXe, 0, 0, "Mã phương tiện:", txtMaPT);
        themTruongForm(cotXe, gbcXe, 0, 1, "Biển số xe (*):", txtBienSo);
        themTruongForm(cotXe, gbcXe, 0, 2, "Hãng sản xuất (*):", cboHangXe);
        themTruongForm(cotXe, gbcXe, 0, 3, "Dòng / Mẫu xe (*):", txtMauXe);

        // Hàng ghép Màu sắc & Năm sản xuất
        JPanel hangMauVaNam = new JPanel(new GridLayout(1, 2, 10, 0));
        hangMauVaNam.setOpaque(false);
        JPanel pnlMau = taoCumCotNho("Màu sắc:", txtMauSac);
        JPanel pnlNam = taoCumCotNho("Năm SX:", txtNamSX);
        hangMauVaNam.add(pnlMau);
        hangMauVaNam.add(pnlNam);

        gbcXe.gridx = 0;
        gbcXe.gridy = 4;
        cotXe.add(hangMauVaNam, gbcXe);

        themTruongForm(cotXe, gbcXe, 0, 5, "Loại nhiên liệu:", cboNhienLieu);

        // --- Cột 2: Thông tin chủ sở hữu (Khách hàng) ---
        JPanel cotKhach = new JPanel(new GridBagLayout());
        cotKhach.setOpaque(false);
        GridBagConstraints gbcKhach = new GridBagConstraints();
        gbcKhach.fill = GridBagConstraints.HORIZONTAL;
        gbcKhach.insets = new Insets(5, 5, 5, 5);

        // Hàng SĐT kèm cụm nút Tìm kiếm / Tạo mới / Đổi khách hàng
        JPanel hangSDT = new JPanel(new BorderLayout(6, 0));
        hangSDT.setOpaque(false);

        txtSoDienThoai = taoOVanBan(true);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSoDienThoai.addActionListener(e -> xuLyTimKiemKhachHangTheoSDT());

        JPanel pnlNutSDT = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnlNutSDT.setOpaque(false);

        btnTimKH = new JButton("Tìm", IconUtil.layIcon("search.png", 14, 14, Color.WHITE));
        btnTimKH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTimKH.setBackground(MAU_XANH_DAM);
        btnTimKH.setForeground(Color.WHITE);
        btnTimKH.setFocusPainted(false);
        btnTimKH.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTimKH.setPreferredSize(new Dimension(80, 36));
        btnTimKH.addActionListener(e -> xuLyTimKiemKhachHangTheoSDT());

        btnKhachMoi = new JButton("+ Mới");
        btnKhachMoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKhachMoi.setBackground(MAU_CAM_CHU_DAO);
        btnKhachMoi.setForeground(Color.WHITE);
        btnKhachMoi.setFocusPainted(false);
        btnKhachMoi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKhachMoi.setPreferredSize(new Dimension(75, 36));
        btnKhachMoi.addActionListener(e -> chuyenSangTaoKhachHangMoi(txtSoDienThoai.getText()));

        btnDoiKH = new JButton("↻ Đổi");
        btnDoiKH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDoiKH.setBackground(new Color(241, 245, 249));
        btnDoiKH.setForeground(MAU_CHU_CHINH);
        btnDoiKH.setFocusPainted(false);
        btnDoiKH.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDoiKH.setPreferredSize(new Dimension(75, 36));
        btnDoiKH.setVisible(false);
        btnDoiKH.addActionListener(e -> datLaiTraCuuKhachHang());

        pnlNutSDT.add(btnTimKH);
        pnlNutSDT.add(btnKhachMoi);
        pnlNutSDT.add(btnDoiKH);

        hangSDT.add(txtSoDienThoai, BorderLayout.CENTER);
        hangSDT.add(pnlNutSDT, BorderLayout.EAST);

        txtMaKH = taoOVanBan(false);
        txtMaKH.setBackground(new Color(241, 245, 249));
        txtMaKH.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtMaKH.setForeground(MAU_XANH_DAM);

        txtTenKH = taoOVanBan(false);
        txtTenKH.setBackground(new Color(241, 245, 249));
        txtTenKH.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Hàng Giới tính & Địa chỉ
        JPanel hangGioiTinhVaDiaChi = new JPanel(new BorderLayout(8, 0));
        hangGioiTinhVaDiaChi.setOpaque(false);

        cboGioiTinhKH = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinhKH.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboGioiTinhKH.setBackground(Color.WHITE);
        cboGioiTinhKH.setPreferredSize(new Dimension(90, 36));
        cboGioiTinhKH.setEnabled(false);

        txtDiaChiKH = taoOVanBan(false);
        txtDiaChiKH.setBackground(new Color(241, 245, 249));

        JPanel pnlGioiTinh = taoCumCotNho("Giới tính:", cboGioiTinhKH);
        pnlGioiTinh.setPreferredSize(new Dimension(95, 60));
        JPanel pnlDiaChi = taoCumCotNho("Địa chỉ liên hệ:", txtDiaChiKH);

        hangGioiTinhVaDiaChi.add(pnlGioiTinh, BorderLayout.WEST);
        hangGioiTinhVaDiaChi.add(pnlDiaChi, BorderLayout.CENTER);

        // Huy hiệu loại khách hàng / trạng thái liên kết
        lblLoaiKH = new JLabel("✓ Khách hàng đăng ký hồ sơ gara");
        lblLoaiKH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLoaiKH.setForeground(new Color(22, 101, 52));
        lblLoaiKH.setOpaque(true);
        lblLoaiKH.setBackground(new Color(220, 252, 231));
        lblLoaiKH.setBorder(new EmptyBorder(8, 12, 8, 12));

        themTruongForm(cotKhach, gbcKhach, 0, 0, "Số điện thoại (*):", hangSDT);
        themTruongForm(cotKhach, gbcKhach, 0, 1, "Mã khách hàng:", txtMaKH);
        themTruongForm(cotKhach, gbcKhach, 0, 2, "Họ và tên chủ xe (*):", txtTenKH);

        gbcKhach.gridx = 0;
        gbcKhach.gridy = 3;
        cotKhach.add(hangGioiTinhVaDiaChi, gbcKhach);

        gbcKhach.gridx = 0;
        gbcKhach.gridy = 4;
        cotKhach.add(lblLoaiKH, gbcKhach);

        noiDung.add(cotXe);
        noiDung.add(cotKhach);

        the.add(noiDung, BorderLayout.CENTER);
        return the;
    }

    /**
     * Thẻ 2: Bảng lịch sử tiếp nhận bảo dưỡng & sửa chữa tại gara
     */
    private JPanel taoTheLichSuTiepNhan() {
        JPanel the = taoTheKhungBoGoc("2. NHẬT KÝ TIẾP NHẬN & SỬA CHỮA TẠI GARA");
        JPanel noiDung = new JPanel(new BorderLayout(0, 10));
        noiDung.setOpaque(false);

        // Hàng thông báo số lượt tiếp nhận
        JPanel hangTieuDe = new JPanel(new BorderLayout());
        hangTieuDe.setOpaque(false);

        lblSoLuotSuaChua = new JLabel("Tổng số: " + danhSachLichSu.size() + " lượt tiếp nhận");
        lblSoLuotSuaChua.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSoLuotSuaChua.setForeground(MAU_CAM_CHU_DAO);
        hangTieuDe.add(lblSoLuotSuaChua, BorderLayout.EAST);

        String[] cot = {
                "#",
                "Mã Tiếp Nhận",
                "Thời Gian",
                "Số ODO (Km)",
                "Yêu Cầu / Triệu Chứng",
                "Trạng Thái"
        };

        moHinhBangLichSu = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangLichSu = new JTable(moHinhBangLichSu);
        bangLichSu.setRowHeight(36);
        bangLichSu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangLichSu.setForeground(MAU_CHU_CHINH);
        bangLichSu.setSelectionBackground(MAU_CHON_DONG);
        bangLichSu.setSelectionForeground(MAU_XANH_DAM);
        bangLichSu.setShowVerticalLines(false);
        bangLichSu.setShowHorizontalLines(true);
        bangLichSu.setGridColor(MAU_KE_BANG);
        bangLichSu.setFillsViewportHeight(true);

        JTableHeader header = bangLichSu.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(241, 245, 249));
                setForeground(MAU_CHU_CHINH);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        TableColumnModel colModel = bangLichSu.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(45);
        colModel.getColumn(1).setPreferredWidth(170);
        colModel.getColumn(2).setPreferredWidth(140);
        colModel.getColumn(3).setPreferredWidth(100);
        colModel.getColumn(4).setPreferredWidth(270);
        colModel.getColumn(5).setPreferredWidth(140);

        BoVeBangLichSu renderer = new BoVeBangLichSu();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(renderer);
        }

        napDuLieuVaoBangLichSu();

        JScrollPane cuonBang = new JScrollPane(bangLichSu);
        cuonBang.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cuonBang.getViewport().setBackground(Color.WHITE);
        cuonBang.setPreferredSize(new Dimension(0, 240));

        noiDung.add(hangTieuDe, BorderLayout.NORTH);
        noiDung.add(cuonBang, BorderLayout.CENTER);

        the.add(noiDung, BorderLayout.CENTER);
        return the;
    }

    private void napDuLieuVaoBangLichSu() {
        moHinhBangLichSu.setRowCount(0);
        int stt = 1;
        for (PhieuTiepNhan ptn : danhSachLichSu) {
            moHinhBangLichSu.addRow(new Object[]{
                    stt++,
                    ptn.getMaTiepNhan(),
                    ptn.getThoiGianTiepNhan() != null ? ptn.getThoiGianTiepNhan().format(DINH_DANG_NGAY_GIO) : "",
                    String.format(Locale.US, "%,.0f km", ptn.getSoKMHienTai()),
                    ptn.getYeuCauKhachHang() != null ? ptn.getYeuCauKhachHang() : "",
                    ptn.getTrangThai() != null ? ptn.getTrangThai().getTenHienThi() : ""
            });
        }
        if (lblSoLuotSuaChua != null) {
            lblSoLuotSuaChua.setText("Tổng số: " + danhSachLichSu.size() + " lượt tiếp nhận");
        }
    }

    private JPanel taoFooterModal() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDong.setFocusPainted(false);
        btnDong.setBackground(Color.WHITE);
        btnDong.setForeground(MAU_CHU_CHINH);
        btnDong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 20, 8, 20)
        ));
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> dispose());

        JButton btnLuu = new JButton(phuongTien != null ? "Lưu Thay Đổi" : "Đăng Ký Phương Tiện", IconUtil.layIcon("save.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_CAM_HOVER : MAU_CAM_CHU_DAO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.setBorderPainted(false);
        btnLuu.setContentAreaFilled(false);
        btnLuu.setOpaque(false);
        btnLuu.setBorder(new EmptyBorder(9, 22, 9, 22));
        btnLuu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLuu.addActionListener(e -> xuLyLuuCapNhat());

        footer.add(btnDong);
        footer.add(btnLuu);
        return footer;
    }

    private void napHangXeVaoCombo() {
        DefaultComboBoxModel<HangXe> model = new DefaultComboBoxModel<>();
        try {
            List<HangXe> ds = hangXeBUS.layTatCa();
            for (HangXe hx : ds) {
                model.addElement(hx);
            }
        } catch (Exception ignored) {
        }
        cboHangXe.setModel(model);
    }

    private void capNhatThongTinHeader() {
        if (lblPhu == null) return;
        String maPT = txtMaPT != null && !txtMaPT.getText().trim().isEmpty() ? txtMaPT.getText().trim() : "Tự động sinh";
        HangXe hx = cboHangXe != null ? (HangXe) cboHangXe.getSelectedItem() : null;
        String hangTen = hx != null ? hx.getTenHang() : "Chưa rõ";
        String mauXe = txtMauXe != null ? txtMauXe.getText().trim() : "";
        lblPhu.setText("Mã PT: " + maPT + "  |  Hãng & Dòng xe: " + hangTen + (mauXe.isEmpty() ? "" : " " + mauXe));
    }

    private void xuLyTimKiemKhachHangTheoSDT() {
        String sdt = txtSoDienThoai.getText() != null ? txtSoDienThoai.getText().trim() : "";
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại để tìm kiếm khách hàng!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtSoDienThoai.requestFocus();
            return;
        }
        if (!sdt.matches("0\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0!",
                    "Số điện thoại không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtSoDienThoai.requestFocus();
            return;
        }

        try {
            KhachHang kh = khachHangBUS.timTheoSoDienThoai(sdt);
            if (kh != null) {
                // Đã tìm thấy khách hàng trong DB
                khachHangDaChon = kh;
                laKhachHangMoi = false;
                txtMaKH.setText(kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "");
                txtTenKH.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
                txtDiaChiKH.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
                if (kh.getGioiTinh() != null) {
                    cboGioiTinhKH.setSelectedItem(kh.getGioiTinh());
                }

                txtSoDienThoai.setEditable(false);
                txtSoDienThoai.setBackground(new Color(241, 245, 249));
                txtTenKH.setEditable(false);
                txtTenKH.setBackground(new Color(241, 245, 249));
                txtDiaChiKH.setEditable(false);
                txtDiaChiKH.setBackground(new Color(241, 245, 249));
                cboGioiTinhKH.setEnabled(false);

                btnTimKH.setVisible(false);
                btnKhachMoi.setVisible(false);
                btnDoiKH.setVisible(true);

                lblLoaiKH.setText("✓ Khách hàng đã có: " + kh.getHoTen());
                lblLoaiKH.setBackground(new Color(220, 252, 231));
                lblLoaiKH.setForeground(new Color(22, 101, 52));

                String maPT = phuongTienBUS.phatSinhMaPhuongTien(kh.getMaKhachHang());
                txtMaPT.setText(maPT);
                capNhatThongTinHeader();

                txtBienSo.requestFocus();
            } else {
                int luaChon = JOptionPane.showConfirmDialog(this,
                        "Không tìm thấy khách hàng với số điện thoại [" + sdt + "] trong hệ thống.\n"
                                + "Bạn có muốn tạo hồ sơ khách hàng mới này không?",
                        "Khách hàng mới",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (luaChon == JOptionPane.YES_OPTION) {
                    chuyenSangTaoKhachHangMoi(sdt);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tra cứu khách hàng: " + ex.getMessage(),
                    "Lỗi tra cứu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chuyenSangTaoKhachHangMoi(String sdt) {
        laKhachHangMoi = true;
        khachHangDaChon = null;

        String maKHMoi = khachHangBUS.phatSinhMaKhachHang();
        txtMaKH.setText(maKHMoi);
        txtSoDienThoai.setText(sdt != null ? sdt.trim() : "");
        txtSoDienThoai.setEditable(true);
        txtSoDienThoai.setBackground(Color.WHITE);

        txtTenKH.setText("");
        txtTenKH.setEditable(true);
        txtTenKH.setBackground(Color.WHITE);

        txtDiaChiKH.setText("");
        txtDiaChiKH.setEditable(true);
        txtDiaChiKH.setBackground(Color.WHITE);

        cboGioiTinhKH.setEnabled(true);
        cboGioiTinhKH.setSelectedItem("Nam");

        btnTimKH.setVisible(false);
        btnKhachMoi.setVisible(false);
        btnDoiKH.setVisible(true);

        lblLoaiKH.setText("★ Khách hàng mới - Vui lòng điền thông tin và lưu xe");
        lblLoaiKH.setBackground(new Color(254, 243, 199));
        lblLoaiKH.setForeground(new Color(180, 83, 9));

        txtMaPT.setText("PT-" + maKHMoi + "-001");
        capNhatThongTinHeader();

        txtTenKH.requestFocus();
    }

    private void datLaiTraCuuKhachHang() {
        khachHangDaChon = null;
        laKhachHangMoi = false;

        txtMaKH.setText("");
        txtTenKH.setText("");
        txtTenKH.setEditable(false);
        txtTenKH.setBackground(new Color(241, 245, 249));

        txtDiaChiKH.setText("");
        txtDiaChiKH.setEditable(false);
        txtDiaChiKH.setBackground(new Color(241, 245, 249));

        cboGioiTinhKH.setEnabled(false);

        txtSoDienThoai.setText("");
        txtSoDienThoai.setEditable(true);
        txtSoDienThoai.setBackground(Color.WHITE);

        btnTimKH.setVisible(true);
        btnKhachMoi.setVisible(true);
        btnDoiKH.setVisible(false);

        lblLoaiKH.setText("ℹ Nhập SĐT và bấm [Tìm] để liên kết khách hàng");
        lblLoaiKH.setBackground(new Color(241, 245, 249));
        lblLoaiKH.setForeground(MAU_CHU_PHU);

        txtMaPT.setText("Tự động sinh");
        capNhatThongTinHeader();

        txtSoDienThoai.requestFocus();
    }

    private void napDuLieuVaoGiaoDien() {
        if (phuongTien == null) {
            if (khachHangMacDinh != null) {
                khachHangDaChon = khachHangMacDinh;
                laKhachHangMoi = false;

                txtMaKH.setText(khachHangMacDinh.getMaKhachHang() != null ? khachHangMacDinh.getMaKhachHang() : "");
                txtTenKH.setText(khachHangMacDinh.getHoTen() != null ? khachHangMacDinh.getHoTen() : "");
                txtSoDienThoai.setText(khachHangMacDinh.getSoDienThoai() != null ? khachHangMacDinh.getSoDienThoai() : "");
                txtDiaChiKH.setText(khachHangMacDinh.getDiaChi() != null ? khachHangMacDinh.getDiaChi() : "");
                if (khachHangMacDinh.getGioiTinh() != null) {
                    cboGioiTinhKH.setSelectedItem(khachHangMacDinh.getGioiTinh());
                }

                txtTenKH.setEditable(false);
                txtSoDienThoai.setEditable(false);
                txtDiaChiKH.setEditable(false);
                cboGioiTinhKH.setEnabled(false);

                btnTimKH.setVisible(false);
                btnKhachMoi.setVisible(false);
                btnDoiKH.setVisible(false);

                lblLoaiKH.setText("✓ Chủ xe: " + khachHangMacDinh.getHoTen());
                lblLoaiKH.setBackground(new Color(220, 252, 231));
                lblLoaiKH.setForeground(new Color(22, 101, 52));

                txtMaPT.setText(phuongTienBUS.phatSinhMaPhuongTien(khachHangMacDinh.getMaKhachHang()));
                capNhatThongTinHeader();
            } else {
                datLaiTraCuuKhachHang();
            }
            return;
        }

        // Trường hợp xem / cập nhật phương tiện đã tồn tại
        btnTimKH.setVisible(false);
        btnKhachMoi.setVisible(false);
        btnDoiKH.setVisible(false);

        txtSoDienThoai.setEditable(false);
        txtSoDienThoai.setBackground(new Color(241, 245, 249));
        txtTenKH.setEditable(false);
        txtTenKH.setBackground(new Color(241, 245, 249));
        txtDiaChiKH.setEditable(false);
        txtDiaChiKH.setBackground(new Color(241, 245, 249));
        cboGioiTinhKH.setEnabled(false);

        txtMaPT.setText(phuongTien.getMaPhuongTien() != null ? phuongTien.getMaPhuongTien() : "");
        txtBienSo.setText(phuongTien.getBienSoXe() != null ? phuongTien.getBienSoXe() : "");
        txtMauXe.setText(phuongTien.getMauXe() != null ? phuongTien.getMauXe() : "");
        txtMauSac.setText(phuongTien.getMauSacXe() != null ? phuongTien.getMauSacXe() : "");
        txtNamSX.setText(String.valueOf(phuongTien.getNamSanXuat()));

        if (phuongTien.getNhienLieu() != null) {
            cboNhienLieu.setSelectedItem(phuongTien.getNhienLieu());
        }

        if (phuongTien.getHangXe() != null) {
            for (int i = 0; i < cboHangXe.getItemCount(); i++) {
                HangXe hx = cboHangXe.getItemAt(i);
                if (hx != null && hx.getMaHangXe() != null
                        && hx.getMaHangXe().equals(phuongTien.getHangXe().getMaHangXe())) {
                    cboHangXe.setSelectedIndex(i);
                    break;
                }
            }
        }

        KhachHang kh = phuongTien.getKhachHang();
        if (kh != null) {
            txtMaKH.setText(kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "");
            txtTenKH.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
            txtSoDienThoai.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
            txtDiaChiKH.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
            if (kh.getGioiTinh() != null) {
                cboGioiTinhKH.setSelectedItem(kh.getGioiTinh());
            }
        }
        lblLoaiKH.setText("✓ Khách hàng đăng ký hồ sơ gara");
        lblLoaiKH.setBackground(new Color(220, 252, 231));
        lblLoaiKH.setForeground(new Color(22, 101, 52));
        capNhatThongTinHeader();
    }

    private void xuLyLuuCapNhat() {
        String bienSo = txtBienSo.getText() != null ? txtBienSo.getText().trim() : "";
        if (bienSo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Biển số xe không được để trống!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtBienSo.requestFocus();
            return;
        }

        String mauXe = txtMauXe.getText() != null ? txtMauXe.getText().trim() : "";
        if (mauXe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dòng / Mẫu xe không được để trống!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtMauXe.requestFocus();
            return;
        }

        String mauSac = txtMauSac.getText() != null ? txtMauSac.getText().trim() : "";
        if (mauSac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Màu sắc xe không được để trống!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtMauSac.requestFocus();
            return;
        }

        int namSX;
        try {
            namSX = Integer.parseInt(txtNamSX.getText().trim());
            int namHienTai = Year.now().getValue();
            if (namSX < 1900 || namSX > namHienTai + 1) {
                JOptionPane.showMessageDialog(this, "Năm sản xuất phải từ 1900 đến " + (namHienTai + 1) + "!",
                        "Năm sản xuất không hợp lệ", JOptionPane.WARNING_MESSAGE);
                txtNamSX.requestFocus();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập năm sản xuất hợp lệ (dạng số)!",
                    "Sai định dạng", JOptionPane.WARNING_MESSAGE);
            txtNamSX.requestFocus();
            return;
        }

        HangXe hx = (HangXe) cboHangXe.getSelectedItem();
        if (hx == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hãng xe!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhienLieu nhienLieu = (NhienLieu) cboNhienLieu.getSelectedItem();

        try {
            if (phuongTien == null) {
                KhachHang chuXe;
                if (khachHangMacDinh != null) {
                    chuXe = khachHangMacDinh;
                } else if (khachHangDaChon != null) {
                    chuXe = khachHangDaChon;
                } else if (laKhachHangMoi) {
                    String hoTenKH = txtTenKH.getText() != null ? txtTenKH.getText().trim() : "";
                    String sdtKH = txtSoDienThoai.getText() != null ? txtSoDienThoai.getText().trim() : "";
                    String diaChiKH = txtDiaChiKH.getText() != null ? txtDiaChiKH.getText().trim() : "";
                    String gioiTinhKH = (String) cboGioiTinhKH.getSelectedItem();

                    if (hoTenKH.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Họ tên chủ xe mới không được để trống!",
                                "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                        txtTenKH.requestFocus();
                        return;
                    }
                    if (sdtKH.isEmpty() || !sdtKH.matches("0\\d{9}")) {
                        JOptionPane.showMessageDialog(this, "Số điện thoại khách hàng phải gồm 10 chữ số và bắt đầu bằng số 0!",
                                "Số điện thoại không hợp lệ", JOptionPane.WARNING_MESSAGE);
                        txtSoDienThoai.requestFocus();
                        return;
                    }

                    chuXe = new KhachHang();
                    chuXe.setMaKhachHang(txtMaKH.getText().trim());
                    chuXe.setHoTen(hoTenKH);
                    chuXe.setSoDienThoai(sdtKH);
                    chuXe.setDiaChi(diaChiKH);
                    chuXe.setGioiTinh(gioiTinhKH);

                    boolean taoKHOk = khachHangBUS.themKhachHang(chuXe);
                    if (!taoKHOk) {
                        JOptionPane.showMessageDialog(this, "Không thể lưu thông tin khách hàng mới. Vui lòng kiểm tra lại!",
                                "Lỗi tạo khách hàng", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Vui lòng tìm kiếm khách hàng bằng số điện thoại hoặc chọn tạo mới hồ sơ khách hàng trước khi lưu xe!",
                            "Chưa có thông tin chủ xe", JOptionPane.WARNING_MESSAGE);
                    txtSoDienThoai.requestFocus();
                    return;
                }

                PhuongTien ptMoi = new PhuongTien();
                ptMoi.setKhachHang(chuXe);
                ptMoi.setHangXe(hx);
                ptMoi.setBienSoXe(bienSo);
                ptMoi.setMauXe(mauXe);
                ptMoi.setMauSacXe(mauSac);
                ptMoi.setNamSanXuat(namSX);
                if (nhienLieu != null) {
                    ptMoi.setNhienLieu(nhienLieu);
                }
                String maPTSinh = txtMaPT.getText() != null ? txtMaPT.getText().trim() : "";
                if (!maPTSinh.isEmpty() && !maPTSinh.equals("Tự động sinh")) {
                    ptMoi.setMaPhuongTien(maPTSinh);
                }

                if (phuongTienBUS.themPhuongTien(ptMoi)) {
                    this.phuongTien = ptMoi;
                    daCapNhat = true;
                    JOptionPane.showMessageDialog(this,
                            "Đã đăng ký phương tiện mới [" + ptMoi.getBienSoXe() + "] cho khách hàng [" + chuXe.getHoTen() + "] thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
                return;
            }

            phuongTien.setBienSoXe(bienSo);
            phuongTien.setHangXe(hx);
            phuongTien.setMauXe(mauXe);
            phuongTien.setMauSacXe(mauSac);
            phuongTien.setNamSanXuat(namSX);
            if (nhienLieu != null) {
                phuongTien.setNhienLieu(nhienLieu);
            }

            if (phuongTienBUS.capNhatPhuongTien(phuongTien)) {
                daCapNhat = true;
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin phương tiện [" + phuongTien.getBienSoXe() + "] thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể cập nhật phương tiện. Vui lòng thử lại!",
                        "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi lưu phương tiện", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Hỗ trợ tạo giao diện & Định dạng ---

    private JPanel taoTheKhungBoGoc(String tieuDe) {
        JPanel the = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setColor(MAU_VIEN);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        the.setOpaque(false);
        the.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        the.add(lblTieuDe, BorderLayout.NORTH);
        return the;
    }

    private void themTruongForm(JPanel pnl, GridBagConstraints gbc, int x, int y,
                                String nhan, JComponent comp) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;

        JPanel cot = new JPanel(new BorderLayout(0, 4));
        cot.setOpaque(false);

        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MAU_CHU_CHINH);

        cot.add(lbl, BorderLayout.NORTH);
        cot.add(comp, BorderLayout.CENTER);
        pnl.add(cot, gbc);
    }

    private JPanel taoCumCotNho(String nhan, JComponent comp) {
        JPanel cot = new JPanel(new BorderLayout(0, 4));
        cot.setOpaque(false);
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MAU_CHU_CHINH);
        cot.add(lbl, BorderLayout.NORTH);
        cot.add(comp, BorderLayout.CENTER);
        return cot;
    }

    private JTextField taoOVanBan(boolean editable) {
        JTextField txt = new JTextField();
        txt.setEditable(editable);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(MAU_CHU_CHINH);
        txt.setPreferredSize(new Dimension(0, 36));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return txt;
    }

    private static class BoVeBangLichSu extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 10, 0, 10));

            if (isSelected) {
                setBackground(MAU_CHON_DONG);
                setForeground(MAU_XANH_DAM);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                setBackground(Color.WHITE);
                setForeground(MAU_CHU_CHINH);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }

            switch (column) {
                case 0 -> setHorizontalAlignment(SwingConstants.CENTER);
                case 1 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                case 3 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setForeground(new Color(234, 106, 31));
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                case 5 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    String s = value != null ? value.toString() : "";
                    if (s.contains("Hoàn thành")) {
                        setForeground(MAU_XANH_LA);
                    } else if (s.contains("Đang sửa")) {
                        setForeground(new Color(217, 119, 6));
                    } else {
                        setForeground(MAU_CHU_PHU);
                    }
                }
                default -> setHorizontalAlignment(SwingConstants.LEFT);
            }
            return this;
        }
    }
}
