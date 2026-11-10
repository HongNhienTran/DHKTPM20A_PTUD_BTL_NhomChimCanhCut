package ui;

import bus.DichVuBUS;
import bus.PhieuBaoGiaBUS;
import bus.PhieuPhanCongBUS;
import bus.PhieuTiepNhanBUS;
import bus.SanPhamBUS;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DichVu;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuTiepNhan;
import entity.SanPham;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.TrangThaiBaoGia;
import enums.TrangThaiPhieuTiepNhan;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * Màn hình Nhiệm Vụ Kiểm Tra & Chẩn Đoán Xe dành cho Kỹ Thuật Viên.
 * Thiết kế giao diện đúng chuẩn theo hình ảnh mẫu Pengu Garage:
 * - Header thanh chỉ số: "🔍 NHIỆM VỤ KIỂM TRA & CHẨN ĐOÁN XE", badge "🕒 Đang kiểm tra", badge "📄 Đã gửi báo giá"
 * - Cột bên trái: Ô tìm kiếm xe + Danh sách xe cần kiểm tra / đã gửi báo giá
 * - Cột bên phải:
 *   + Thông tin xe (Biển số, mã tiếp nhận, dòng xe, trạng thái, Odo)
 *   + Yêu cầu ban đầu của khách & Kết luận kiểm tra (chẩn đoán của KTV)
 *   + Thanh chọn hạng mục dịch vụ & phụ tùng kèm số lượng để thêm vào báo giá
 *   + Bảng danh sách dịch vụ / vật tư đề xuất (Loại, Mã hạng mục, Tên, SL, Đơn giá, Thành tiền, Xóa)
 *   + Tổng chi phí dự kiến + Nút "Gửi kết quả kiểm tra cho Lễ tân"
 */
public class KyThuatVienManHinhKiemTraXe extends JPanel {

    // Bảng màu giao diện chuẩn Pengu Garage
    private static final Color MAU_NEN_CHINH = new Color(246, 249, 253);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 124, 48);
    private static final Color MAU_XANH_DUONG = new Color(37, 99, 235);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_DO_XOA = new Color(239, 68, 68);

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();
    private final PhieuBaoGiaBUS phieuBaoGiaBUS = new PhieuBaoGiaBUS();
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();
    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();

    // Dữ liệu danh sách xe bên trái
    private final List<PhieuTiepNhan> danhSachXeGoc = new ArrayList<>();
    private final List<PhieuTiepNhan> danhSachXeHienThi = new ArrayList<>();
    private PhieuTiepNhan xeDangChon = null;

    // Danh sách dịch vụ & sản phẩm nạp từ CSDL
    private List<DichVu> danhSachDichVu = new ArrayList<>();
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    // Danh sách các hạng mục kiểm tra đề xuất cho xe đang chọn
    private final List<HangMucDeXuatItem> danhSachHangMucDeXuat = new ArrayList<>();

    // Các thành phần UI Header & Thống kê
    private JLabel lblBadgeDangKiemTra;
    private JLabel lblBadgeDaGuiBaoGia;

    // Cột bên trái
    private JTextField txtTimKiemXe;
    private JButton btnLamMoiXe;
    private DefaultTableModel modelBangXe;
    private JTable bangXe;
    private JLabel lblTongSoXe;

    // Cột bên phải
    private JLabel lblBienSoXe;
    private JLabel lblMaTiepNhanPill;
    private JLabel lblDongXe;
    private JLabel lblKTVPhuTrach;
    private JLabel lblTrangThaiXe;
    private JLabel lblOdoXe;

    private JTextArea txtYeuCauKhach;
    private JTextArea txtKetLuanKiemTra;

    private JComboBox<DichVuComboItem> cbDichVu;
    private JButton btnThemDichVu;
    private JComboBox<DichVuComboItem> cbDichVuGanPhuTung;
    private JComboBox<SanPhamComboItem> cbSanPham;
    private JSpinner spSoLuongPhuTung;
    private JButton btnThemSanPham;

    private DefaultTableModel modelBangHangMuc;
    private JTable bangHangMuc;

    private JLabel lblTongChiPhiDuKien;
    private JButton btnGuiKetQuaChoLeTan;

    /**
     * DTO lưu trữ từng hạng mục đề xuất (Dịch vụ hoặc Phụ tùng)
     */
    public static class HangMucDeXuatItem {
        private final boolean laDichVu;
        private final String ma;
        private final String ten;
        private int soLuong;
        private final double donGia;
        private final double thueVAT;
        private final DichVu dichVuGoc;
        private final SanPham sanPhamGoc;
        private DichVu dichVuCha;

        public HangMucDeXuatItem(boolean laDichVu, String ma, String ten, int soLuong, double donGia, double thueVAT,
                                 DichVu dichVuGoc, SanPham sanPhamGoc) {
            this(laDichVu, ma, ten, soLuong, donGia, thueVAT, dichVuGoc, sanPhamGoc, null);
        }

        public HangMucDeXuatItem(boolean laDichVu, String ma, String ten, int soLuong, double donGia, double thueVAT,
                                 DichVu dichVuGoc, SanPham sanPhamGoc, DichVu dichVuCha) {
            this.laDichVu = laDichVu;
            this.ma = ma;
            this.ten = ten;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thueVAT = thueVAT;
            this.dichVuGoc = dichVuGoc;
            this.sanPhamGoc = sanPhamGoc;
            this.dichVuCha = dichVuCha;
        }

        public double tinhThanhTien() {
            return soLuong * donGia;
        }

        public boolean isLaDichVu() { return laDichVu; }
        public String getMa() { return ma; }
        public String getTen() { return ten; }
        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
        public double getDonGia() { return donGia; }
        public double getThueVAT() { return thueVAT; }
        public DichVu getDichVuGoc() { return dichVuGoc; }
        public SanPham getSanPhamGoc() { return sanPhamGoc; }
        public DichVu getDichVuCha() { return dichVuCha; }
        public void setDichVuCha(DichVu dichVuCha) { this.dichVuCha = dichVuCha; }
    }

    public static class DichVuComboItem {
        private final DichVu dv;
        public DichVuComboItem(DichVu dv) { this.dv = dv; }
        public DichVu getDv() { return dv; }
        @Override
        public String toString() {
            if (dv == null) return "-- Chọn dịch vụ kỹ thuật --";
            String loai = dv.isCoPhuTung() ? "[Có phụ tùng]" : "[Chỉ công thợ]";
            return dv.getTenDichVu() + " (" + dinhDangTien(dv.getDonGia()) + ") " + loai;
        }
    }

    public static class SanPhamComboItem {
        private final SanPham sp;
        public SanPhamComboItem(SanPham sp) { this.sp = sp; }
        public SanPham getSp() { return sp; }
        @Override
        public String toString() {
            if (sp == null) return "-- Chọn phụ tùng / vật tư --";
            return sp.getTenSanPham() + " (" + dinhDangTien(sp.getDonGiaBan()) + ")";
        }
    }

    public KyThuatVienManHinhKiemTraXe() {
        this(null);
    }

    public KyThuatVienManHinhKiemTraXe(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        napDuLieuDanhMucDichVuVaVatTu();

        khoiTaoGiaoDien();
        taiLaiDanhSachXe();
    }

    private void napDuLieuDanhMucDichVuVaVatTu() {
        danhSachDichVu = dichVuBUS.layDanhSachDangKinhDoanh();
        danhSachSanPham = sanPhamBUS.layTatCa();
        if (danhSachDichVu == null) {
            throw new IllegalStateException("Không thể tải danh sách dịch vụ từ cơ sở dữ liệu");
        }
        if (danhSachSanPham == null) {
            throw new IllegalStateException("Không thể tải danh sách sản phẩm từ cơ sở dữ liệu");
        }
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN_CHINH);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Header trên cùng
        add(taoHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính: Chia 2 cột (Trái: Danh sách xe | Phải: Bảng kiểm tra chẩn đoán)
        JPanel pnlNoiDung = new JPanel(new BorderLayout(16, 0));
        pnlNoiDung.setOpaque(false);

        pnlNoiDung.add(taoCotTraiDanhSachXe(), BorderLayout.WEST);
        pnlNoiDung.add(taoCotPhaiKiemTra(), BorderLayout.CENTER);

        add(pnlNoiDung, BorderLayout.CENTER);
    }

    /**
     * Header hiển thị: "🔍 NHIỆM VỤ KIỂM TRA & CHẨN ĐOÁN XE" + 2 badge thống kê bên phải
     */
    private JPanel taoHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Tiêu đề
        JPanel pnlTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTieuDe.setOpaque(false);
        JLabel lblIcon = new JLabel(IconUtil.layIcon("search.png", 20, 20, MAU_XANH_DAM));
        JLabel lblTieuDe = new JLabel("NHIỆM VỤ KIỂM TRA & CHẨN ĐOÁN XE");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        pnlTieuDe.add(lblIcon);
        pnlTieuDe.add(lblTieuDe);

        // Các badge thống kê
        JPanel pnlBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBadge.setOpaque(false);

        lblBadgeDangKiemTra = taoBadgePill(" Đang kiểm tra: 0 xe", IconUtil.layIcon("history.png", 14, 14, MAU_XANH_DUONG), new Color(239, 246, 255), new Color(191, 219, 254), MAU_XANH_DUONG);
        lblBadgeDaGuiBaoGia = taoBadgePill(" Đã gửi báo giá: 0 xe", IconUtil.layIcon("invoice.png", 14, 14, new Color(180, 83, 9)), new Color(254, 243, 199), new Color(253, 230, 138), new Color(180, 83, 9));

        pnlBadge.add(lblBadgeDangKiemTra);
        pnlBadge.add(lblBadgeDaGuiBaoGia);

        header.add(pnlTieuDe, BorderLayout.WEST);
        header.add(pnlBadge, BorderLayout.EAST);
        return header;
    }

    private JLabel taoBadgePill(String text, Icon icon, Color mauNen, Color mauVien, Color mauChu) {
        JLabel badge = new JLabel(text, icon, SwingConstants.LEFT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 16, 16);
                g2.setColor(mauNen);
                g2.fill(rect);
                g2.setColor(mauVien);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(mauChu);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        badge.setOpaque(false);
        return badge;
    }

    /**
     * Cột bên trái: Ô tìm kiếm + Bảng danh sách xe
     */
    private JPanel taoCotTraiDanhSachXe() {
        TheBoGocTrang cardTrai = new TheBoGocTrang(new BorderLayout(0, 10));
        cardTrai.setPreferredSize(new Dimension(380, 0));
        cardTrai.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Thanh tìm kiếm + nút Làm mới
        JPanel pnlTimKiem = new JPanel(new BorderLayout(8, 0));
        pnlTimKiem.setOpaque(false);

        txtTimKiemXe = new JTextField();
        txtTimKiemXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTimKiemXe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txtTimKiemXe.putClientProperty("JTextField.placeholderText", "Tìm theo biển số, mã TN...");
        txtTimKiemXe.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locDanhSachXe(); }
            public void removeUpdate(DocumentEvent e) { locDanhSachXe(); }
            public void changedUpdate(DocumentEvent e) { locDanhSachXe(); }
        });

        btnLamMoiXe = new JButton(IconUtil.layIcon("refresh.png", 14, 14, MAU_CHU_CHINH));
        btnLamMoiXe.setPreferredSize(new Dimension(34, 32));
        btnLamMoiXe.setBackground(Color.WHITE);
        btnLamMoiXe.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        btnLamMoiXe.setFocusPainted(false);
        btnLamMoiXe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLamMoiXe.setToolTipText("Làm mới danh sách xe");
        btnLamMoiXe.addActionListener(e -> taiLaiDanhSachXe());

        pnlTimKiem.add(txtTimKiemXe, BorderLayout.CENTER);
        pnlTimKiem.add(btnLamMoiXe, BorderLayout.EAST);
        cardTrai.add(pnlTimKiem, BorderLayout.NORTH);

        // Bảng xe: Biển số, Dòng xe, KTV Khám, Trạng thái
        String[] tieuDeCot = {"Biển số", "Dòng xe", "KTV Khám", "Trạng thái"};
        modelBangXe = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangXe = new JTable(modelBangXe);
        bangXe.setRowHeight(38);
        bangXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangXe.setForeground(MAU_CHU_CHINH);
        bangXe.setGridColor(new Color(235, 238, 242));
        bangXe.setShowVerticalLines(true);
        bangXe.setShowHorizontalLines(true);
        bangXe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangXe.setSelectionBackground(new Color(254, 243, 199)); // Màu highlight cam nhạt như ảnh
        bangXe.setSelectionForeground(MAU_CHU_CHINH);
        bangXe.getTableHeader().setReorderingAllowed(false);
        bangXe.getTableHeader().setPreferredSize(new Dimension(0, 34));
        bangXe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bangXe.getTableHeader().setBackground(new Color(241, 245, 249));
        bangXe.getTableHeader().setForeground(new Color(51, 65, 85));

        TableColumnModel colModel = bangXe.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(95);
        colModel.getColumn(1).setPreferredWidth(110);
        colModel.getColumn(2).setPreferredWidth(130);
        colModel.getColumn(3).setPreferredWidth(105);

        // Renderer in đậm cho biển số
        colModel.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        });

        // Renderer badge trạng thái (cột 3)
        colModel.getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String tt = (value != null) ? value.toString() : "";
                if ("Đã gửi báo giá".equals(tt)) {
                    lbl.setForeground(new Color(180, 83, 9));
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("Đang kiểm tra".equals(tt)) {
                    lbl.setForeground(new Color(234, 106, 31));
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("Chờ kiểm tra".equals(tt)) {
                    lbl.setForeground(MAU_XANH_DUONG);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    lbl.setForeground(MAU_CHU_CHINH);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                }
                return lbl;
            }
        });

        bangXe.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = bangXe.getSelectedRow();
                if (row >= 0 && row < danhSachXeHienThi.size()) {
                    chonXe(danhSachXeHienThi.get(row));
                }
            }
        });

        JScrollPane cuonBangXe = new JScrollPane(bangXe);
        cuonBangXe.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuonBangXe.getViewport().setBackground(Color.WHITE);
        cardTrai.add(cuonBangXe, BorderLayout.CENTER);

        // Footer dưới chân cột trái
        lblTongSoXe = new JLabel("Tổng: 0 xe trong danh sách");
        lblTongSoXe.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTongSoXe.setForeground(MAU_CHU_PHU);
        lblTongSoXe.setBorder(new EmptyBorder(4, 4, 0, 0));
        cardTrai.add(lblTongSoXe, BorderLayout.SOUTH);

        return cardTrai;
    }

    /**
     * Cột bên phải: Toàn bộ bảng kiểm tra, kết luận, chọn phụ tùng & báo giá
     */
    private JPanel taoCotPhaiKiemTra() {
        TheBoGocTrang cardPhai = new TheBoGocTrang(new BorderLayout(0, 12));
        cardPhai.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Phía trên: Thông tin xe + 2 ô Yêu cầu & Kết luận
        JPanel pnlDauPhai = new JPanel(new BorderLayout(0, 10));
        pnlDauPhai.setOpaque(false);

        // 1. Dòng thông số xe (Biển số, Dòng xe, Trạng thái, Odo)
        JPanel pnlThongTinXe = new JPanel(new BorderLayout());
        pnlThongTinXe.setOpaque(false);

        JPanel pnlTraiXe = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTraiXe.setOpaque(false);

        lblBienSoXe = new JLabel("-");
        lblBienSoXe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBienSoXe.setForeground(MAU_XANH_DAM);

        lblMaTiepNhanPill = new JLabel("[-]");
        lblMaTiepNhanPill.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMaTiepNhanPill.setForeground(MAU_CHU_PHU);

        lblDongXe = new JLabel("-", IconUtil.layIcon("car.png", 14, 14, MAU_CHU_CHINH), SwingConstants.LEFT);
        lblDongXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDongXe.setForeground(MAU_CHU_CHINH);

        lblKTVPhuTrach = new JLabel("• KTV Khám: -");
        lblKTVPhuTrach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKTVPhuTrach.setForeground(MAU_CAM_CHU_DAO);

        pnlTraiXe.add(lblBienSoXe);
        pnlTraiXe.add(lblMaTiepNhanPill);
        pnlTraiXe.add(Box.createHorizontalStrut(6));
        pnlTraiXe.add(lblDongXe);
        pnlTraiXe.add(Box.createHorizontalStrut(8));
        pnlTraiXe.add(lblKTVPhuTrach);

        JPanel pnlPhaiXe = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlPhaiXe.setOpaque(false);
        lblTrangThaiXe = new JLabel("Chờ xác nhận báo giá", SwingConstants.RIGHT);
        lblTrangThaiXe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTrangThaiXe.setForeground(MAU_CHU_CHINH);

        lblOdoXe = new JLabel("Odo: 50000 km", SwingConstants.RIGHT);
        lblOdoXe.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblOdoXe.setForeground(MAU_CHU_PHU);

        pnlPhaiXe.add(lblTrangThaiXe);
        pnlPhaiXe.add(lblOdoXe);

        pnlThongTinXe.add(pnlTraiXe, BorderLayout.WEST);
        pnlThongTinXe.add(pnlPhaiXe, BorderLayout.EAST);
        pnlDauPhai.add(pnlThongTinXe, BorderLayout.NORTH);

        // 2. Hai ô văn bản ngang nhau: Yêu cầu của khách & Kết luận kiểm tra
        JPanel pnlHaiOVanBan = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlHaiOVanBan.setOpaque(false);

        // Ô Yêu cầu của khách
        JPanel oYeuCau = new JPanel(new BorderLayout(0, 4));
        oYeuCau.setOpaque(false);
        JLabel lblTieuDeYeuCau = new JLabel("Yêu cầu ban đầu của khách:", IconUtil.layIcon("edit.png", 12, 12, MAU_CHU_PHU), SwingConstants.LEFT);
        lblTieuDeYeuCau.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTieuDeYeuCau.setForeground(MAU_CHU_PHU);
        txtYeuCauKhach = new JTextArea("thay lốp");
        txtYeuCauKhach.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtYeuCauKhach.setLineWrap(true);
        txtYeuCauKhach.setWrapStyleWord(true);
        txtYeuCauKhach.setEditable(false);
        txtYeuCauKhach.setBackground(new Color(248, 250, 252));
        txtYeuCauKhach.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane cuonYeuCau = new JScrollPane(txtYeuCauKhach);
        cuonYeuCau.setPreferredSize(new Dimension(0, 60));
        cuonYeuCau.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        oYeuCau.add(lblTieuDeYeuCau, BorderLayout.NORTH);
        oYeuCau.add(cuonYeuCau, BorderLayout.CENTER);

        // Ô Kết luận kiểm tra (chẩn đoán của KTV)
        JPanel oKetLuan = new JPanel(new BorderLayout(0, 4));
        oKetLuan.setOpaque(false);
        JLabel lblTieuDeKetLuan = new JLabel("Kết luận kiểm tra", IconUtil.layIcon("search.png", 12, 12, MAU_XANH_DAM), SwingConstants.LEFT);
        lblTieuDeKetLuan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTieuDeKetLuan.setForeground(MAU_XANH_DAM);
        txtKetLuanKiemTra = new JTextArea("Đã kiểm tra xe tổng thể. Đề xuất các hạng mục sửa chữa và vật tư thay thế đi kèm.");
        txtKetLuanKiemTra.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtKetLuanKiemTra.setLineWrap(true);
        txtKetLuanKiemTra.setWrapStyleWord(true);
        txtKetLuanKiemTra.setBackground(Color.WHITE);
        txtKetLuanKiemTra.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane cuonKetLuan = new JScrollPane(txtKetLuanKiemTra);
        cuonKetLuan.setPreferredSize(new Dimension(0, 60));
        cuonKetLuan.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        oKetLuan.add(lblTieuDeKetLuan, BorderLayout.NORTH);
        oKetLuan.add(cuonKetLuan, BorderLayout.CENTER);

        pnlHaiOVanBan.add(oYeuCau);
        pnlHaiOVanBan.add(oKetLuan);
        pnlDauPhai.add(pnlHaiOVanBan, BorderLayout.CENTER);

        cardPhai.add(pnlDauPhai, BorderLayout.NORTH);

        // Phần giữa: Khung chọn Hạng mục & Bảng danh sách
        JPanel pnlGiuaPhai = new JPanel(new BorderLayout(0, 8));
        pnlGiuaPhai.setOpaque(false);

        // Thanh chọn dịch vụ & phụ tùng
        pnlGiuaPhai.add(taoThanhChonHangMuc(), BorderLayout.NORTH);

        // Bảng danh sách hạng mục đã chọn
        pnlGiuaPhai.add(taoBangHangMuc(), BorderLayout.CENTER);

        cardPhai.add(pnlGiuaPhai, BorderLayout.CENTER);

        // Phần dưới chân: Tổng chi phí dự kiến + Nút Gửi kết quả
        cardPhai.add(taoChanPhaiThaoTac(), BorderLayout.SOUTH);

        return cardPhai;
    }

    /**
     * Thanh chọn Hạng mục Dịch vụ & Phụ tùng cần thay thế
     */
    private JPanel taoThanhChonHangMuc() {
        JPanel wrap = new JPanel(new BorderLayout(0, 6));
        wrap.setOpaque(false);

        JLabel lblTieuDe = new JLabel("+ CHỌN HẠNG MỤC DỊCH VỤ & PHỤ TÙNG CẦN THAY THẾ CHO XE:");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        wrap.add(lblTieuDe, BorderLayout.NORTH);

        JPanel pnlChon = new JPanel(new GridLayout(1, 2, 14, 0));
        pnlChon.setOpaque(false);

        // Cụm trái: Chọn dịch vụ + Nút Thêm xanh dương
        JPanel cumDichVu = new JPanel(new BorderLayout(6, 0));
        cumDichVu.setOpaque(false);

        cbDichVu = new JComboBox<>();
        cbDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbDichVu.setBackground(Color.WHITE);
        for (DichVu dv : danhSachDichVu) {
            cbDichVu.addItem(new DichVuComboItem(dv));
        }

        btnThemDichVu = new JButton("Thêm");
        btnThemDichVu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemDichVu.setForeground(Color.WHITE);
        btnThemDichVu.setBackground(MAU_XANH_DUONG);
        btnThemDichVu.setFocusPainted(false);
        btnThemDichVu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemDichVu.setPreferredSize(new Dimension(75, 32));
        btnThemDichVu.addActionListener(e -> themDichVuVaoDanhSach());

        cumDichVu.add(cbDichVu, BorderLayout.CENTER);
        cumDichVu.add(btnThemDichVu, BorderLayout.EAST);

        // Cụm phải: Chọn dịch vụ gắn phụ tùng + Chọn phụ tùng + Spinner SL + Nút Thêm xanh lá
        JPanel cumPhuTung = new JPanel(new BorderLayout(6, 4));
        cumPhuTung.setOpaque(false);

        JPanel pnlChonDVCha = new JPanel(new BorderLayout(4, 0));
        pnlChonDVCha.setOpaque(false);
        JLabel lblGanVao = new JLabel("Gắn cho DV:");
        lblGanVao.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblGanVao.setForeground(MAU_CHU_PHU);

        cbDichVuGanPhuTung = new JComboBox<>();
        cbDichVuGanPhuTung.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbDichVuGanPhuTung.setBackground(Color.WHITE);
        cbDichVuGanPhuTung.setPreferredSize(new Dimension(0, 26));
        pnlChonDVCha.add(lblGanVao, BorderLayout.WEST);
        pnlChonDVCha.add(cbDichVuGanPhuTung, BorderLayout.CENTER);

        JPanel pnlHangSanPham = new JPanel(new BorderLayout(6, 0));
        pnlHangSanPham.setOpaque(false);

        cbSanPham = new JComboBox<>();
        cbSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSanPham.setBackground(Color.WHITE);
        for (SanPham sp : danhSachSanPham) {
            cbSanPham.addItem(new SanPhamComboItem(sp));
        }

        spSoLuongPhuTung = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spSoLuongPhuTung.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spSoLuongPhuTung.setPreferredSize(new Dimension(50, 32));

        btnThemSanPham = new JButton("Thêm");
        btnThemSanPham.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemSanPham.setForeground(Color.WHITE);
        btnThemSanPham.setBackground(new Color(5, 150, 105)); // Xanh lá đậm như hình
        btnThemSanPham.setFocusPainted(false);
        btnThemSanPham.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemSanPham.setPreferredSize(new Dimension(75, 32));
        btnThemSanPham.addActionListener(e -> themSanPhamVaoDanhSach());

        JPanel subPhuTung = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        subPhuTung.setOpaque(false);
        subPhuTung.add(spSoLuongPhuTung);
        subPhuTung.add(btnThemSanPham);

        pnlHangSanPham.add(cbSanPham, BorderLayout.CENTER);
        pnlHangSanPham.add(subPhuTung, BorderLayout.EAST);

        cumPhuTung.add(pnlChonDVCha, BorderLayout.NORTH);
        cumPhuTung.add(pnlHangSanPham, BorderLayout.CENTER);

        pnlChon.add(cumDichVu);
        pnlChon.add(cumPhuTung);
        wrap.add(pnlChon, BorderLayout.CENTER);

        return wrap;
    }

    /**
     * Bảng danh sách hạng mục đề xuất (Loại, Mã hạng mục, Tên, SL, Đơn giá, Thành tiền, Xóa)
     */
    private JScrollPane taoBangHangMuc() {
        String[] tieuDeCot = {"Loại", "Mã hạng mục", "Tên dịch vụ / Phụ tùng", "SL", "Đơn giá", "Thành tiền", "Xóa"};
        modelBangHangMuc = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangHangMuc = new JTable(modelBangHangMuc);
        bangHangMuc.setRowHeight(38);
        bangHangMuc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangHangMuc.setForeground(MAU_CHU_CHINH);
        bangHangMuc.setGridColor(new Color(235, 238, 242));
        bangHangMuc.setShowVerticalLines(true);
        bangHangMuc.setShowHorizontalLines(true);
        bangHangMuc.setSelectionBackground(new Color(239, 246, 255));
        bangHangMuc.setSelectionForeground(MAU_CHU_CHINH);
        bangHangMuc.getTableHeader().setReorderingAllowed(false);
        bangHangMuc.getTableHeader().setPreferredSize(new Dimension(0, 34));
        bangHangMuc.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bangHangMuc.getTableHeader().setBackground(new Color(241, 245, 249));
        bangHangMuc.getTableHeader().setForeground(new Color(51, 65, 85));

        TableColumnModel colModel = bangHangMuc.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(80);  // Loại
        colModel.getColumn(1).setPreferredWidth(100); // Mã hạng mục
        colModel.getColumn(2).setPreferredWidth(230); // Tên
        colModel.getColumn(3).setPreferredWidth(45);  // SL
        colModel.getColumn(4).setPreferredWidth(95);  // Đơn giá
        colModel.getColumn(5).setPreferredWidth(110); // Thành tiền
        colModel.getColumn(6).setPreferredWidth(45);  // Xóa

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer canPhaiDam = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        };
        canPhaiDam.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer rendererLoai = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row >= 0 && row < danhSachHangMucDeXuat.size()) {
                    boolean laDV = danhSachHangMucDeXuat.get(row).isLaDichVu();
                    lbl.setIcon(IconUtil.layIcon(laDV ? "service.png" : "part.png", 14, 14, laDV ? MAU_XANH_DAM : new Color(180, 83, 9)));
                    lbl.setText(laDV ? " Dịch vụ" : " Phụ tùng");
                }
                return lbl;
            }
        };

        // Renderer nút xóa icon thùng rác
        DefaultTableCellRenderer rendererXoa = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setText("");
                lbl.setIcon(IconUtil.layIcon("trash.png", 15, 15, MAU_DO_XOA));
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return lbl;
            }
        };

        colModel.getColumn(0).setCellRenderer(rendererLoai);
        colModel.getColumn(1).setCellRenderer(canGiua);
        colModel.getColumn(3).setCellRenderer(canGiua);
        colModel.getColumn(4).setCellRenderer(canPhai);
        colModel.getColumn(5).setCellRenderer(canPhaiDam);
        colModel.getColumn(6).setCellRenderer(rendererXoa);

        // Bắt sự kiện click vào cột Xóa (cột index 6)
        bangHangMuc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = bangHangMuc.columnAtPoint(e.getPoint());
                int row = bangHangMuc.rowAtPoint(e.getPoint());
                if (col == 6 && row >= 0 && row < danhSachHangMucDeXuat.size()) {
                    xoaHangMuc(row);
                }
            }
        });

        JScrollPane cuon = new JScrollPane(bangHangMuc);
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.setPreferredSize(new Dimension(0, 220));
        return cuon;
    }

    /**
     * Dưới chân cột phải: Tổng chi phí dự kiến + Nút Gửi kết quả kiểm tra cho Lễ tân
     */
    private JPanel taoChanPhaiThaoTac() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Bên trái: Tổng chi phí dự kiến
        JPanel pnlTong = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlTong.setOpaque(false);
        JLabel lblTieuDeTong = new JLabel("Tổng chi phí dự kiến:");
        lblTieuDeTong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDeTong.setForeground(MAU_XANH_DAM);
        lblTongChiPhiDuKien = new JLabel("950.000 đ");
        lblTongChiPhiDuKien.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTongChiPhiDuKien.setForeground(MAU_CAM_CHU_DAO);
        pnlTong.add(lblTieuDeTong);
        pnlTong.add(lblTongChiPhiDuKien);

        // Bên phải: Nút Gửi kết quả kiểm tra cho Lễ tân (màu Cam đặc trưng)
        btnGuiKetQuaChoLeTan = new JButton("Gửi kết quả kiểm tra cho Lễ tân") {
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
        btnGuiKetQuaChoLeTan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuiKetQuaChoLeTan.setForeground(Color.WHITE);
        btnGuiKetQuaChoLeTan.setFocusPainted(false);
        btnGuiKetQuaChoLeTan.setBorderPainted(false);
        btnGuiKetQuaChoLeTan.setContentAreaFilled(false);
        btnGuiKetQuaChoLeTan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuiKetQuaChoLeTan.setPreferredSize(new Dimension(240, 36));
        btnGuiKetQuaChoLeTan.addActionListener(e -> xuLyGuiKetQuaChoLeTan());

        footer.add(pnlTong, BorderLayout.WEST);
        footer.add(btnGuiKetQuaChoLeTan, BorderLayout.EAST);
        return footer;
    }

    /**
     * Tải lại danh sách xe cần kiểm tra từ CSDL
     */
    public void taiLaiDanhSachXe() {
        danhSachXeGoc.clear();
        try {
            List<PhieuTiepNhan> ds = phieuTiepNhanBUS.layTatCa();
            if (ds != null) {
                for (PhieuTiepNhan ptn : ds) {
                    if (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.DANG_KIEM_TRA
                            || ptn.getTrangThai() == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA) {
                        if (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA) {
                            try {
                                PhieuBaoGia pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(ptn.getMaTiepNhan());
                                if (pbg != null && pbg.getTrangThai() == TrangThaiBaoGia.TU_CHOI) {
                                    continue;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        danhSachXeGoc.add(ptn);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        locDanhSachXe();
    }

    private void locDanhSachXe() {
        String tuKhoa = (txtTimKiemXe != null) ? txtTimKiemXe.getText().trim().toLowerCase() : "";
        danhSachXeHienThi.clear();

        int demDangKiemTra = 0;
        int demDaGuiBaoGia = 0;

        for (PhieuTiepNhan ptn : danhSachXeGoc) {
            if (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA) {
                demDaGuiBaoGia++;
            } else {
                demDangKiemTra++;
            }

            boolean hopLe = true;
            if (!tuKhoa.isEmpty()) {
                String bienSo = (ptn.getPhuongTien() != null) ? ptn.getPhuongTien().getBienSoXe().toLowerCase() : "";
                String maTN = ptn.getMaTiepNhan().toLowerCase();
                String mauXe = (ptn.getPhuongTien() != null && ptn.getPhuongTien().getMauXe() != null)
                        ? ptn.getPhuongTien().getMauXe().toLowerCase() : "";
                hopLe = bienSo.contains(tuKhoa) || maTN.contains(tuKhoa) || mauXe.contains(tuKhoa);
            }
            if (hopLe) {
                danhSachXeHienThi.add(ptn);
            }
        }

        // Cập nhật badge
        lblBadgeDangKiemTra.setText(" Đang kiểm tra: " + demDangKiemTra + " xe");
        lblBadgeDaGuiBaoGia.setText(" Đã gửi báo giá: " + demDaGuiBaoGia + " xe");
        lblTongSoXe.setText("Tổng: " + danhSachXeHienThi.size() + " xe trong danh sách");

        // Cập nhật bảng trái: Biển số, Dòng xe, KTV Khám, Trạng thái
        modelBangXe.setRowCount(0);
        for (PhieuTiepNhan ptn : danhSachXeHienThi) {
            String bienSo = (ptn.getPhuongTien() != null) ? ptn.getPhuongTien().getBienSoXe() : "-";
            String dongXe = (ptn.getPhuongTien() != null) ? ptn.getPhuongTien().getMauXe() : "-";
            String ktvKham = layTenKTVKiemTra(ptn);
            String trangThaiHienThi = (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA)
                    ? "Đã gửi báo giá"
                    : (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.DANG_KIEM_TRA ? "Đang kiểm tra" : "Chờ kiểm tra");

            modelBangXe.addRow(new Object[]{bienSo, dongXe, ktvKham, trangThaiHienThi});
        }

        // Chọn dòng đầu tiên mặc định nếu có
        if (!danhSachXeHienThi.isEmpty()) {
            bangXe.setRowSelectionInterval(0, 0);
        } else {
            xoaTrangCotPhai();
        }
    }

    private String layTenKTVKiemTra(PhieuTiepNhan ptn) {
        if (ptn == null) return "-";
        try {
            PhieuBaoGia pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(ptn.getMaTiepNhan());
            if (pbg != null && pbg.getNhanVien() != null && pbg.getNhanVien().getChucVu() == ChucVu.KY_THUAT_VIEN) {
                return pbg.getNhanVien().getHoTen() + " (" + pbg.getNhanVien().getMaNhanVien() + ")";
            }
        } catch (Exception ignored) {
        }
        String gc = ptn.getGhiChu();
        if (gc != null && gc.contains("[KTV kiểm tra:")) {
            int start = gc.indexOf("[KTV kiểm tra:") + "[KTV kiểm tra:".length();
            int end = gc.indexOf("]", start);
            if (end > start) {
                return gc.substring(start, end).trim();
            }
        }
        return "Chưa gán";
    }

    /**
     * Nạp dữ liệu xe được chọn vào cột bên phải
     */
    private void chonXe(PhieuTiepNhan ptn) {
        this.xeDangChon = ptn;
        if (ptn == null) {
            xoaTrangCotPhai();
            return;
        }

        String bienSo = (ptn.getPhuongTien() != null) ? ptn.getPhuongTien().getBienSoXe() : "-";
        String mauXe = (ptn.getPhuongTien() != null) ? ptn.getPhuongTien().getMauXe() : "-";
        lblBienSoXe.setText(bienSo);
        lblMaTiepNhanPill.setText("[" + ptn.getMaTiepNhan() + "]");
        lblDongXe.setText(mauXe);
        lblKTVPhuTrach.setText("• KTV Khám: " + layTenKTVKiemTra(ptn));

        lblTrangThaiXe.setText(ptn.getTrangThai() != null ? ptn.getTrangThai().getTenHienThi() : "Đang kiểm tra");
        lblOdoXe.setText(String.format("Odo: %,.0f km", ptn.getSoKMHienTai()));
        txtYeuCauKhach.setText(ptn.getYeuCauKhachHang() != null ? ptn.getYeuCauKhachHang() : "-");

        // Nạp các hạng mục đề xuất đã có hoặc tạo mẫu đúng hình
        danhSachHangMucDeXuat.clear();
        napHangMucDeXuatTheoXe(ptn);
        capNhatBangHangMucVaTongTien();
    }

    private void napHangMucDeXuatTheoXe(PhieuTiepNhan ptn) {
        PhieuBaoGia pbg = null;
        try {
            pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(ptn.getMaTiepNhan());
            if (pbg == null) {
                pbg = phieuBaoGiaBUS.khoiTaoBaoGiaBanDau(ptn, ptn.getNhanVien(), new ArrayList<>());
            }
        } catch (Exception ignored) {
        }

        if (pbg != null && pbg.getDanhSachChiTiet() != null && !pbg.getDanhSachChiTiet().isEmpty()) {
            txtKetLuanKiemTra.setText(pbg.getGhiChu() != null && !pbg.getGhiChu().isBlank()
                    ? pbg.getGhiChu()
                    : "Đã kiểm tra xe tổng thể. Đề xuất các hạng mục sửa chữa và vật tư thay thế đi kèm.");

            for (ChiTietBaoGia ct : pbg.getDanhSachChiTiet()) {
                DichVu dv = ct.getDichVu();
                if (dv != null) {
                    danhSachHangMucDeXuat.add(new HangMucDeXuatItem(
                            true, dv.getMaDichVu(), dv.getTenDichVu(), 1, ct.getDonGiaDichVu(), dv.getThueVAT(), dv, null, null
                    ));
                    if (ct.getDanhSachPhuTung() != null) {
                        for (ChiTietDungPhuTung pt : ct.getDanhSachPhuTung()) {
                            SanPham sp = pt.getSanPham();
                            if (sp != null) {
                                danhSachHangMucDeXuat.add(new HangMucDeXuatItem(
                                        false, sp.getMaSanPham(), sp.getTenSanPham(), pt.getSoLuong(), pt.getDonGia(), sp.getThueVAT(), null, sp, dv
                                ));
                            }
                        }
                    }
                }
            }
        } else {
            txtKetLuanKiemTra.setText("Đã kiểm tra xe tổng thể. Đề xuất các hạng mục sửa chữa và vật tư thay thế đi kèm.");
        }
    }

    private void themDichVuVaoDanhSach() {
        DichVuComboItem item = (DichVuComboItem) cbDichVu.getSelectedItem();
        if (item == null || item.getDv() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dịch vụ kỹ thuật!", "Chưa chọn dịch vụ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DichVu dv = item.getDv();
        // Kiểm tra xem dịch vụ đã có trong danh sách chưa
        for (HangMucDeXuatItem hm : danhSachHangMucDeXuat) {
            if (hm.isLaDichVu() && hm.getMa().equals(dv.getMaDichVu())) {
                JOptionPane.showMessageDialog(this, "Dịch vụ '" + dv.getTenDichVu() + "' đã có trong danh sách đề xuất!", "Đã tồn tại", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        danhSachHangMucDeXuat.add(new HangMucDeXuatItem(
                true, dv.getMaDichVu(), dv.getTenDichVu(), 1, dv.getDonGia(), dv.getThueVAT(), dv, null, null
        ));
        capNhatBangHangMucVaTongTien();
    }

    private void themSanPhamVaoDanhSach() {
        DichVuComboItem dvGanItem = (DichVuComboItem) cbDichVuGanPhuTung.getSelectedItem();
        if (dvGanItem == null || dvGanItem.getDv() == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng thêm hoặc chọn dịch vụ có yêu cầu phụ tùng để gắn phụ tùng vào!",
                    "Chưa chọn dịch vụ tương ứng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DichVu dvGan = dvGanItem.getDv();

        SanPhamComboItem item = (SanPhamComboItem) cbSanPham.getSelectedItem();
        if (item == null || item.getSp() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm / phụ tùng!", "Chưa chọn phụ tùng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SanPham sp = item.getSp();
        int soLuong = (int) spSoLuongPhuTung.getValue();
        if (soLuong <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phụ tùng phải lớn hơn 0!", "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nếu đã có phụ tùng này gắn vào dịch vụ này thì cộng dồn số lượng
        for (HangMucDeXuatItem hm : danhSachHangMucDeXuat) {
            if (!hm.isLaDichVu() && hm.getMa().equals(sp.getMaSanPham())
                    && hm.getDichVuCha() != null && hm.getDichVuCha().getMaDichVu().equals(dvGan.getMaDichVu())) {
                hm.setSoLuong(hm.getSoLuong() + soLuong);
                capNhatBangHangMucVaTongTien();
                return;
            }
        }

        if (sp.getMaSanPham() == null || sp.getMaSanPham().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sản phẩm được chọn không có mã hợp lệ trong cơ sở dữ liệu.",
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        danhSachHangMucDeXuat.add(new HangMucDeXuatItem(
                false, sp.getMaSanPham(), sp.getTenSanPham(), soLuong, sp.getDonGiaBan(), sp.getThueVAT(), null, sp, dvGan
        ));
        capNhatBangHangMucVaTongTien();
    }

    private void xoaHangMuc(int index) {
        if (index >= 0 && index < danhSachHangMucDeXuat.size()) {
            HangMucDeXuatItem itemBiXoa = danhSachHangMucDeXuat.get(index);
            if (itemBiXoa.isLaDichVu()) {
                // Nếu xóa dịch vụ thì xóa luôn các phụ tùng trực thuộc dịch vụ đó
                String maDV = itemBiXoa.getMa();
                danhSachHangMucDeXuat.removeIf(hm -> hm == itemBiXoa
                        || (!hm.isLaDichVu() && hm.getDichVuCha() != null && hm.getDichVuCha().getMaDichVu().equals(maDV)));
            } else {
                danhSachHangMucDeXuat.remove(index);
            }
            capNhatBangHangMucVaTongTien();
        }
    }

    private void capNhatComboDichVuGanPhuTung() {
        if (cbDichVuGanPhuTung == null) return;
        DichVuComboItem daChon = (DichVuComboItem) cbDichVuGanPhuTung.getSelectedItem();
        cbDichVuGanPhuTung.removeAllItems();
        boolean coDichVuCoPhuTung = false;
        for (HangMucDeXuatItem hm : danhSachHangMucDeXuat) {
            if (hm.isLaDichVu() && hm.getDichVuGoc() != null && hm.getDichVuGoc().isCoPhuTung()) {
                cbDichVuGanPhuTung.addItem(new DichVuComboItem(hm.getDichVuGoc()));
                coDichVuCoPhuTung = true;
            }
        }
        if (!coDichVuCoPhuTung) {
            cbDichVuGanPhuTung.addItem(new DichVuComboItem(null) {
                @Override
                public String toString() {
                    return "-- Chưa có dịch vụ cần phụ tùng --";
                }
            });
            cbSanPham.setEnabled(false);
            btnThemSanPham.setEnabled(false);
        } else {
            cbSanPham.setEnabled(true);
            btnThemSanPham.setEnabled(true);
            if (daChon != null && daChon.getDv() != null) {
                for (int i = 0; i < cbDichVuGanPhuTung.getItemCount(); i++) {
                    DichVuComboItem it = cbDichVuGanPhuTung.getItemAt(i);
                    if (it.getDv() != null && it.getDv().getMaDichVu().equals(daChon.getDv().getMaDichVu())) {
                        cbDichVuGanPhuTung.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void capNhatBangHangMucVaTongTien() {
        modelBangHangMuc.setRowCount(0);
        double tongChiPhi = 0;

        for (HangMucDeXuatItem hm : danhSachHangMucDeXuat) {
            tongChiPhi += hm.tinhThanhTien();
            String loaiText = hm.isLaDichVu() ? "Dịch vụ" : "Phụ tùng";
            String tenHienThi = hm.isLaDichVu()
                    ? hm.getTen() + (hm.getDichVuGoc() != null && hm.getDichVuGoc().isCoPhuTung() ? " [Cần phụ tùng]" : " [Không dùng phụ tùng]")
                    : hm.getTen() + " (gắn cho: " + (hm.getDichVuCha() != null ? hm.getDichVuCha().getTenDichVu() : "Dịch vụ") + ")";

            modelBangHangMuc.addRow(new Object[]{
                    loaiText,
                    hm.getMa(),
                    tenHienThi,
                    hm.getSoLuong(),
                    dinhDangTien(hm.getDonGia()),
                    dinhDangTien(hm.tinhThanhTien()),
                    ""
            });
        }

        lblTongChiPhiDuKien.setText(dinhDangTien(tongChiPhi));
        capNhatComboDichVuGanPhuTung();
    }

    /**
     * Xử lý gửi kết quả kiểm tra & báo giá đề xuất cho Lễ tân
     */
    private void xuLyGuiKetQuaChoLeTan() {
        if (xeDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một xe cần gửi kết quả kiểm tra!", "Chưa chọn xe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (danhSachHangMucDeXuat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng thêm ít nhất 1 hạng mục dịch vụ trước khi gửi kết quả!",
                    "Chưa có hạng mục đề xuất", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ketLuan = txtKetLuanKiemTra.getText().trim();
        if (ketLuan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập kết luận kiểm tra & chẩn đoán xe!",
                    "Thiếu kết luận", JOptionPane.WARNING_MESSAGE);
            txtKetLuanKiemTra.requestFocus();
            return;
        }

        String bienSo = (xeDangChon.getPhuongTien() != null) ? xeDangChon.getPhuongTien().getBienSoXe() : "";
        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Xác nhận hoàn tất kiểm tra và gửi kết quả chẩn đoán & bảng giá đề xuất cho Lễ tân?\n" +
                        "Phương tiện [" + bienSo + "] sẽ chuyển sang trạng thái [CHỜ XÁC NHẬN BÁO GIÁ].",
                "Xác nhận gửi kết quả kiểm tra",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }

        // Cập nhật CSDL
        try {
            // Lấy hoặc tạo phiếu báo giá
            PhieuBaoGia pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(xeDangChon.getMaTiepNhan());
            if (pbg == null) {
                pbg = new PhieuBaoGia();
                pbg.setPhieuTiepNhan(xeDangChon);
                NhanVien ktv = (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNhanVien() != null)
                        ? taiKhoanDangNhap.getMaNhanVien()
                        : xeDangChon.getNhanVien();
                pbg.setNhanVien(ktv);
                pbg.setThoiGianLap(LocalDateTime.now());
                pbg.setTrangThai(TrangThaiBaoGia.CHO_XAC_NHAN);
                pbg.setMaBaoGia(phieuBaoGiaBUS.phatSinhMaBaoGia(xeDangChon.getMaTiepNhan()));
            }

            pbg.setGhiChu(ketLuan);
            pbg.setTrangThai(TrangThaiBaoGia.CHO_XAC_NHAN);

            // Gom nhóm các dịch vụ và phụ tùng theo đúng cấu trúc cây OOP
            List<ChiTietBaoGia> dsCT = new ArrayList<>();
            java.util.Map<String, ChiTietBaoGia> mapDichVu = new java.util.LinkedHashMap<>();

            for (HangMucDeXuatItem hm : danhSachHangMucDeXuat) {
                if (hm.isLaDichVu()) {
                    DichVu dv = hm.getDichVuGoc();
                    if (dv == null) {
                        throw new IllegalStateException("Hạng mục dịch vụ không còn tồn tại trong cơ sở dữ liệu: " + hm.getMa());
                    }
                    ChiTietBaoGia ct = new ChiTietBaoGia(hm.getDonGia(), false, "", pbg, dv);
                    mapDichVu.put(dv.getMaDichVu(), ct);
                    dsCT.add(ct);
                }
            }

            for (HangMucDeXuatItem hm : danhSachHangMucDeXuat) {
                if (!hm.isLaDichVu()) {
                    SanPham sp = hm.getSanPhamGoc();
                    if (sp == null) {
                        throw new IllegalStateException("Hạng mục phụ tùng không còn tồn tại trong cơ sở dữ liệu: " + hm.getMa());
                    }
                    ChiTietBaoGia ctCha = null;
                    if (hm.getDichVuCha() != null) {
                        ctCha = mapDichVu.get(hm.getDichVuCha().getMaDichVu());
                    }
                    if (ctCha == null && !dsCT.isEmpty()) {
                        ctCha = dsCT.get(0);
                    }
                    if (ctCha != null) {
                        ChiTietDungPhuTung pt = new ChiTietDungPhuTung(hm.getSoLuong(), hm.getDonGia(), false, "", ctCha, sp);
                        ctCha.getDanhSachPhuTung().add(pt);
                    }
                }
            }

            pbg.setDanhSachChiTiet(dsCT);
            boolean ok = phieuBaoGiaBUS.guiKetQuaKiemTraVaBaoGia(pbg, ketLuan);
            if (!ok) {
                throw new IllegalStateException("Không thể lưu kết quả kiểm tra & báo giá vào hệ thống.");
            }
            xeDangChon.setTrangThai(TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi gửi kết quả kiểm tra: " + ex.getMessage(),
                    "Lỗi thực hiện", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "✓ Đã hoàn tất kiểm tra và gửi kết quả chẩn đoán thành công!\n" +
                        "Phương tiện [" + bienSo + "] đã chuyển sang [CHỜ XÁC NHẬN BÁO GIÁ] cho Lễ tân.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        taiLaiDanhSachXe();
    }

    private void xoaTrangCotPhai() {
        lblBienSoXe.setText("-");
        lblMaTiepNhanPill.setText("[-]");
        lblDongXe.setText("-");
        lblKTVPhuTrach.setText("• KTV Khám: -");
        lblTrangThaiXe.setText("-");
        lblOdoXe.setText("-");
        txtYeuCauKhach.setText("");
        txtKetLuanKiemTra.setText("");
        danhSachHangMucDeXuat.clear();
        modelBangHangMuc.setRowCount(0);
        lblTongChiPhiDuKien.setText("0 đ");
    }

    private static String dinhDangTien(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien) + " đ";
    }

    /**
     * Thẻ nền trắng bo góc nhẹ hỗ trợ đổ bóng viền
     */
    private static class TheBoGocTrang extends JPanel {
        public TheBoGocTrang(BorderLayout layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D rect = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12, 12);
            g2.setColor(Color.WHITE);
            g2.fill(rect);
            g2.setColor(MAU_VIEN);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(rect);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Hàm main hỗ trợ chạy thử độc lập giao diện
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pengu Garage - Nhiệm Vụ Kiểm Tra & Chẩn Đoán Xe (Kỹ Thuật Viên)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KyThuatVienManHinhKiemTraXe(null));
            frame.setVisible(true);
        });
    }
}
