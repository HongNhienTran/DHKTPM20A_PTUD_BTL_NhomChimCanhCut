package ui;

import bus.PhieuPhanCongBUS;
import entity.PhieuPhanCong;
import entity.TaiKhoan;
import enums.TrangThaiSua;
import util.ChonNgayLichUtil;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Nhật Ký Công Việc (Lịch Sử Ca Sửa Hoàn Tất) dành cho Kỹ thuật viên.
 * Thiết kế giao diện đúng chuẩn theo hình ảnh mẫu Pengu Garage:
 * - Header: "LỊCH SỬ CA SỬA HOÀN TẤT", Nút "↻ Làm mới", Nút "📥 Xuất báo cáo"
 * - 3 Thẻ chỉ số tổng quan ở hàng trên:
 *   + Tổng ca
 *   + Số ca hoàn thành
 *   + Số ca hủy
 * - Thanh tìm kiếm nhanh có placeholder & Lọc tức thì trên bộ nhớ
 * - Bộ lọc khoảng ngày (Từ ngày -> Đến ngày), nút "Hôm nay" (mặc định) & nút "Tất cả"
 * - Bảng danh sách các ca sửa hoàn tất (toàn màn hình, double click để xem modal chi tiết nghiệm thu)
 */
public class KyThuatVienManHinhNhatKyCongViec extends JPanel {

    private static final Color MAU_NEN_CHINH = new Color(246, 249, 253);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 124, 48);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_XANH_LA_CHU = new Color(21, 128, 61);
    private static final Color MAU_DO = new Color(225, 29, 72);

    private static final DateTimeFormatter DINH_DANG_GIO_NGAY = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();

    // Dữ liệu danh sách
    private final List<PhieuPhanCong> danhSachPhanCongGoc = new ArrayList<>();
    private final List<PhieuPhanCong> danhSachPhanCongHienThi = new ArrayList<>();

    // Các thành phần UI Thống kê
    private JLabel lblTongCa;
    private JLabel lblSoCaHoanThanh;
    private JLabel lblSoCaHuy;

    // Bộ lọc
    private JTextField txtTimKiem;
    private LocalDate ngayTu = LocalDate.now();   // Mặc định là ngày hôm nay
    private LocalDate ngayDen = LocalDate.now();  // Mặc định là ngày hôm nay
    private JButton btnHomNay;
    private JButton btnTuNgay;
    private JButton btnDenNgay;
    private JButton btnTatCa;
    private JLabel lblSoLuongHienThi;

    // Bảng dữ liệu
    private DefaultTableModel modelBang;
    private JTable bangLichSu;

    public KyThuatVienManHinhNhatKyCongViec() {
        this(null);
    }

    public KyThuatVienManHinhNhatKyCongViec(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;

        khoiTaoGiaoDien();
        taiLaiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN_CHINH);
        setBorder(new EmptyBorder(16, 22, 16, 22));

        // 1. Header trên cùng
        add(taoHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính: 3 Thẻ chỉ số + Thanh lọc + Bảng toàn màn hình
        JPanel pnlTrungTam = new JPanel(new BorderLayout(0, 12));
        pnlTrungTam.setOpaque(false);

        // 3 Thẻ chỉ số tổng quan
        pnlTrungTam.add(tao3TheChiSo(), BorderLayout.NORTH);

        // Thẻ trắng lớn chứa Thanh lọc + Bảng
        TheBoGocTrang cardBang = new TheBoGocTrang(new BorderLayout(0, 10));
        cardBang.setBorder(new EmptyBorder(14, 16, 14, 16));

        cardBang.add(taoThanhLoc(), BorderLayout.NORTH);
        cardBang.add(taoBangDuLieu(), BorderLayout.CENTER);
        cardBang.add(taoFooterHuongDan(), BorderLayout.SOUTH);

        pnlTrungTam.add(cardBang, BorderLayout.CENTER);

        add(pnlTrungTam, BorderLayout.CENTER);
    }

    /**
     * Header hiển thị: "🕒 LỊCH SỬ CA SỬA HOÀN TẤT" + Nút Làm mới + Nút Xuất báo cáo
     */
    private JPanel taoHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Tiêu đề bên trái
        JPanel pnlTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTieuDe.setOpaque(false);
        JLabel lblIcon = new JLabel(IconUtil.layIcon("history.png", 20, 20, MAU_XANH_DAM));
        JLabel lblTieuDe = new JLabel("LỊCH SỬ CA SỬA HOÀN TẤT");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        pnlTieuDe.add(lblIcon);
        pnlTieuDe.add(lblTieuDe);

        // Các nút bên phải
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlNut.setOpaque(false);

        // Nút Làm mới
        JButton btnLamMoi = new JButton("Làm mới", IconUtil.layIcon("refresh.png", 14, 14, new Color(51, 65, 85))) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLamMoi.setForeground(new Color(51, 65, 85));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setBorderPainted(false);
        btnLamMoi.setContentAreaFilled(false);
        btnLamMoi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLamMoi.setPreferredSize(new Dimension(100, 34));
        btnLamMoi.addActionListener(e -> taiLaiDuLieu());

        // Nút Xuất báo cáo (Màu cam)
        JButton btnXuatBaoCao = new JButton("Xuất báo cáo", IconUtil.layIcon("excel.png", 14, 14, Color.WHITE)) {
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
        btnXuatBaoCao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXuatBaoCao.setForeground(Color.WHITE);
        btnXuatBaoCao.setFocusPainted(false);
        btnXuatBaoCao.setBorderPainted(false);
        btnXuatBaoCao.setContentAreaFilled(false);
        btnXuatBaoCao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXuatBaoCao.setPreferredSize(new Dimension(135, 34));
        btnXuatBaoCao.addActionListener(e -> xuLyXuatBaoCao());

        pnlNut.add(btnLamMoi);
        pnlNut.add(btnXuatBaoCao);

        header.add(pnlTieuDe, BorderLayout.WEST);
        header.add(pnlNut, BorderLayout.EAST);
        return header;
    }

    /**
     * Hàng 3 thẻ chỉ số: Tổng ca | Số ca hoàn thành | Số ca hủy
     */
    private JPanel tao3TheChiSo() {
        JPanel hangThe = new JPanel(new GridLayout(1, 3, 16, 0));
        hangThe.setOpaque(false);
        hangThe.setPreferredSize(new Dimension(0, 96));

        lblTongCa = new JLabel("0 ca");
        lblSoCaHoanThanh = new JLabel("0 ca");
        lblSoCaHuy = new JLabel("0 ca");

        hangThe.add(taoTheChiSo("Tổng ca", lblTongCa, MAU_CHU_CHINH));
        hangThe.add(taoTheChiSo("Số ca hoàn thành", lblSoCaHoanThanh, MAU_XANH_LA));
        hangThe.add(taoTheChiSo("Số ca hủy", lblSoCaHuy, MAU_DO));

        return hangThe;
    }

    private TheBoGocTrang taoTheChiSo(String tieuDe, JLabel lblGiaTri, Color mauGiaTri) {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTieuDe.setForeground(MAU_CHU_PHU);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGiaTri.setForeground(mauGiaTri);

        the.add(lblTieuDe, BorderLayout.NORTH);
        the.add(lblGiaTri, BorderLayout.CENTER);
        return the;
    }

    /**
     * Thanh tìm kiếm có placeholder + Lọc khoảng ngày (Từ ngày -> Đến ngày) + Nút "Hôm nay" + Nút "Tất cả"
     */
    private JPanel taoThanhLoc() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(4, 0, 6, 0));

        // Bên trái: Ô tìm kiếm + Bộ chọn khoảng ngày
        JPanel pnlTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTrai.setOpaque(false);

        // Ô tìm kiếm có placeholder vẽ mượt mà
        txtTimKiem = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !(getGraphics() == null)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(148, 163, 184));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("🔍 Tìm theo Biển số xe, Mã phiếu, Hạng mục...", insets.left + 4, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTimKiem.setPreferredSize(new Dimension(280, 34));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locDuLieu(); }
            public void removeUpdate(DocumentEvent e) { locDuLieu(); }
            public void changedUpdate(DocumentEvent e) { locDuLieu(); }
        });

        // Nút lọc nhanh "Hôm nay"
        btnHomNay = taoNutBoLoc("Hôm nay", 82);
        btnHomNay.addActionListener(e -> {
            ngayTu = LocalDate.now();
            ngayDen = LocalDate.now();
            capNhatGiaoDienNutNgay();
            locDuLieu();
        });

        // Nhãn Từ ngày
        JLabel lblTu = new JLabel("Từ:");
        lblTu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTu.setForeground(MAU_CHU_CHINH);

        btnTuNgay = taoNutBoLoc(ngayTu != null ? ngayTu.format(DINH_DANG_NGAY) : "Từ ngày", 105);
        btnTuNgay.addActionListener(e -> xuLyChonTuNgay());

        // Nhãn Đến ngày
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDen.setForeground(MAU_CHU_CHINH);

        btnDenNgay = taoNutBoLoc(ngayDen != null ? ngayDen.format(DINH_DANG_NGAY) : "Đến ngày", 105);
        btnDenNgay.addActionListener(e -> xuLyChonDenNgay());

        // Nút "Tất cả"
        btnTatCa = taoNutBoLoc("Tất cả", 70);
        btnTatCa.addActionListener(e -> {
            ngayTu = null;
            ngayDen = null;
            capNhatGiaoDienNutNgay();
            locDuLieu();
        });

        pnlTrai.add(txtTimKiem);
        pnlTrai.add(Box.createHorizontalStrut(6));
        pnlTrai.add(btnHomNay);
        pnlTrai.add(lblTu);
        pnlTrai.add(btnTuNgay);
        pnlTrai.add(lblDen);
        pnlTrai.add(btnDenNgay);
        pnlTrai.add(btnTatCa);

        // Bên phải: Label số lượng hiển thị
        lblSoLuongHienThi = new JLabel("Hiển thị 0 ca sửa hoàn tất");
        lblSoLuongHienThi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSoLuongHienThi.setForeground(new Color(16, 185, 129));

        bar.add(pnlTrai, BorderLayout.WEST);
        bar.add(lblSoLuongHienThi, BorderLayout.EAST);

        capNhatGiaoDienNutNgay();
        return bar;
    }

    private JButton taoNutBoLoc(String text, int width) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(MAU_CHU_CHINH);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 34));
        return btn;
    }

    private void xuLyChonTuNgay() {
        ChonNgayLichUtil.KetQuaChonNgay kq = ChonNgayLichUtil.hienThiLichChonNgay(
                this, "Chọn Ngày Bắt Đầu", ngayTu != null ? ngayTu : LocalDate.now(), false
        );
        if (kq.daXacNhan() && kq.ngay() != null) {
            ngayTu = kq.ngay();
            if (ngayDen != null && ngayTu.isAfter(ngayDen)) {
                ngayDen = ngayTu;
            }
            capNhatGiaoDienNutNgay();
            locDuLieu();
        }
    }

    private void xuLyChonDenNgay() {
        ChonNgayLichUtil.KetQuaChonNgay kq = ChonNgayLichUtil.hienThiLichChonNgay(
                this, "Chọn Ngày Kết Thúc", ngayDen != null ? ngayDen : (ngayTu != null ? ngayTu : LocalDate.now()), false
        );
        if (kq.daXacNhan() && kq.ngay() != null) {
            ngayDen = kq.ngay();
            if (ngayTu != null && ngayDen.isBefore(ngayTu)) {
                ngayTu = ngayDen;
            }
            capNhatGiaoDienNutNgay();
            locDuLieu();
        }
    }

    private void capNhatGiaoDienNutNgay() {
        LocalDate homNay = LocalDate.now();
        boolean laHomNay = (ngayTu != null && ngayDen != null && ngayTu.isEqual(homNay) && ngayDen.isEqual(homNay));
        boolean laTatCa = (ngayTu == null && ngayDen == null);

        // Nút Hôm nay
        if (btnHomNay != null) {
            btnHomNay.setBackground(laHomNay ? new Color(239, 246, 255) : Color.WHITE);
            btnHomNay.setForeground(laHomNay ? new Color(37, 99, 235) : MAU_CHU_CHINH);
            btnHomNay.setFont(new Font("Segoe UI", laHomNay ? Font.BOLD : Font.PLAIN, 12));
            btnHomNay.setBorder(BorderFactory.createLineBorder(laHomNay ? new Color(96, 165, 250) : MAU_VIEN));
        }

        // Nút Tất cả
        if (btnTatCa != null) {
            btnTatCa.setBackground(laTatCa ? new Color(239, 246, 255) : Color.WHITE);
            btnTatCa.setForeground(laTatCa ? new Color(37, 99, 235) : MAU_CHU_CHINH);
            btnTatCa.setFont(new Font("Segoe UI", laTatCa ? Font.BOLD : Font.PLAIN, 12));
            btnTatCa.setBorder(BorderFactory.createLineBorder(laTatCa ? new Color(96, 165, 250) : MAU_VIEN));
        }

        // Nút Từ ngày & Đến ngày
        if (btnTuNgay != null) {
            btnTuNgay.setText(ngayTu != null ? ngayTu.format(DINH_DANG_NGAY) : "--/--/----");
            btnTuNgay.setFont(new Font("Segoe UI", ngayTu != null ? Font.BOLD : Font.PLAIN, 12));
        }
        if (btnDenNgay != null) {
            btnDenNgay.setText(ngayDen != null ? ngayDen.format(DINH_DANG_NGAY) : "--/--/----");
            btnDenNgay.setFont(new Font("Segoe UI", ngayDen != null ? Font.BOLD : Font.PLAIN, 12));
        }
    }

    /**
     * Bảng hiển thị danh sách các ca sửa hoàn tất (full màn hình)
     */
    private JScrollPane taoBangDuLieu() {
        String[] tieuDeCot = {
                "STT", "Mã phân công", "Biển số", "Dòng xe", "Hạng mục thi công", "Thời gian xong", "Trạng thái"
        };

        modelBang = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangLichSu = new JTable(modelBang);
        bangLichSu.setRowHeight(38);
        bangLichSu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangLichSu.setForeground(MAU_CHU_CHINH);
        bangLichSu.setGridColor(new Color(235, 238, 242));
        bangLichSu.setShowVerticalLines(true);
        bangLichSu.setShowHorizontalLines(true);
        bangLichSu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangLichSu.setSelectionBackground(new Color(254, 243, 199)); // Highlight màu cam nhẹ
        bangLichSu.setSelectionForeground(MAU_CHU_CHINH);
        bangLichSu.getTableHeader().setReorderingAllowed(false);
        bangLichSu.getTableHeader().setPreferredSize(new Dimension(0, 36));
        bangLichSu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bangLichSu.getTableHeader().setBackground(new Color(241, 245, 249));
        bangLichSu.getTableHeader().setForeground(new Color(51, 65, 85));

        TableColumnModel colModel = bangLichSu.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(50);  // STT
        colModel.getColumn(1).setPreferredWidth(140); // Mã phân công
        colModel.getColumn(2).setPreferredWidth(110); // Biển số
        colModel.getColumn(3).setPreferredWidth(130); // Dòng xe
        colModel.getColumn(4).setPreferredWidth(270); // Hạng mục thi công
        colModel.getColumn(5).setPreferredWidth(120); // Thời gian xong
        colModel.getColumn(6).setPreferredWidth(110); // Trạng thái

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer canGiuaDam = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        };
        canGiuaDam.setHorizontalAlignment(SwingConstants.CENTER);

        colModel.getColumn(0).setCellRenderer(canGiua);
        colModel.getColumn(1).setCellRenderer(canGiua);
        colModel.getColumn(2).setCellRenderer(canGiuaDam);
        colModel.getColumn(5).setCellRenderer(canGiua);

        // Renderer badge trạng thái xanh lá Hoàn thành
        colModel.getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setForeground(MAU_XANH_LA_CHU);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                return lbl;
            }
        });

        // Bắt sự kiện double-click vào dòng để mở modal xem chi tiết
        bangLichSu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = bangLichSu.getSelectedRow();
                    if (row >= 0 && row < danhSachPhanCongHienThi.size()) {
                        moModalXemChiTiet(danhSachPhanCongHienThi.get(row));
                    }
                }
            }
        });

        JScrollPane cuon = new JScrollPane(bangLichSu);
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        return cuon;
    }

    private JPanel taoFooterHuongDan() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        footer.setOpaque(false);
        JLabel lblGoiY = new JLabel(" Gợi ý: Bấm đúp (Double-click) vào bất kỳ dòng nào trong bảng để xem chi tiết nghiệm thu ca sửa và vật tư đã sử dụng.", IconUtil.layIcon("help.png", 14, 14, MAU_CHU_PHU), SwingConstants.LEFT);
        lblGoiY.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblGoiY.setForeground(MAU_CHU_PHU);
        footer.add(lblGoiY);
        return footer;
    }

    private void moModalXemChiTiet(PhieuPhanCong ppc) {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        KyThuatVienManHinhNhatKyCongViecXemChiTiet modal = new KyThuatVienManHinhNhatKyCongViecXemChiTiet(cuaSo, ppc);
        modal.setVisible(true);
    }

    public void taiLaiDuLieu() {
        danhSachPhanCongGoc.clear();

        try {
            List<PhieuPhanCong> ds = null;
            if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNhanVien() != null) {
                ds = phieuPhanCongBUS.layTheoNhanVien(taiKhoanDangNhap.getMaNhanVien().getMaNhanVien());
            } else {
                ds = phieuPhanCongBUS.layTatCa();
            }

            if (ds != null) {
                for (PhieuPhanCong ppc : ds) {
                    if (ppc.getTrangThaiSua() == TrangThaiSua.HOAN_THANH) {
                        danhSachPhanCongGoc.add(ppc);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        locDuLieu();
    }

    /**
     * Lọc dữ liệu tức thì trên giao diện (in-memory filtering)
     */
    private void locDuLieu() {
        String tuKhoa = (txtTimKiem != null) ? txtTimKiem.getText().trim().toLowerCase() : "";
        danhSachPhanCongHienThi.clear();

        for (PhieuPhanCong ppc : danhSachPhanCongGoc) {
            boolean hopLe = true;

            // 1. Lọc theo từ khóa (Biển số xe, Mã phiếu, Dòng xe, Hạng mục)
            if (!tuKhoa.isEmpty()) {
                String maPC = ppc.getMaPhanCong() != null ? ppc.getMaPhanCong().toLowerCase() : "";
                String bienSo = "";
                String dongXe = "";
                if (ppc.layPhieuTiepNhan() != null && ppc.layPhieuTiepNhan().getPhuongTien() != null) {
                    bienSo = ppc.layPhieuTiepNhan().getPhuongTien().getBienSoXe().toLowerCase();
                    if (ppc.layPhieuTiepNhan().getPhuongTien().getMauXe() != null) {
                        dongXe = ppc.layPhieuTiepNhan().getPhuongTien().getMauXe().toLowerCase();
                    }
                }
                String hangMuc = "";
                if (ppc.getChiTietBaoGia() != null && ppc.getChiTietBaoGia().getDichVu() != null) {
                    hangMuc = ppc.getChiTietBaoGia().getDichVu().getTenDichVu().toLowerCase();
                }

                hopLe = maPC.contains(tuKhoa) || bienSo.contains(tuKhoa) || dongXe.contains(tuKhoa) || hangMuc.contains(tuKhoa);
            }

            // 2. Lọc theo khoảng ngày (Từ ngày -> Đến ngày)
            if (hopLe) {
                LocalDateTime tg = ppc.getThoiGianHoanThanh() != null ? ppc.getThoiGianHoanThanh() : ppc.getThoiGianPhanCong();
                if (tg != null) {
                    LocalDate ngayCa = tg.toLocalDate();
                    if (ngayTu != null && ngayCa.isBefore(ngayTu)) {
                        hopLe = false;
                    }
                    if (ngayDen != null && ngayCa.isAfter(ngayDen)) {
                        hopLe = false;
                    }
                }
            }

            if (hopLe) {
                danhSachPhanCongHienThi.add(ppc);
            }
        }

        // Cập nhật số liệu trên 3 thẻ chỉ số
        int tongTatCa = danhSachPhanCongGoc.size();
        int soHienThi = danhSachPhanCongHienThi.size();

        lblTongCa.setText(tongTatCa + " ca");
        lblSoCaHoanThanh.setText(soHienThi + " ca");
        lblSoCaHuy.setText("0 ca");

        lblSoLuongHienThi.setText("Hiển thị " + soHienThi + " ca sửa hoàn tất");

        // Đổ dữ liệu vào JTable
        modelBang.setRowCount(0);
        int stt = 1;
        for (PhieuPhanCong ppc : danhSachPhanCongHienThi) {
            String bienSo = "-";
            String dongXe = "-";
            if (ppc.layPhieuTiepNhan() != null && ppc.layPhieuTiepNhan().getPhuongTien() != null) {
                bienSo = ppc.layPhieuTiepNhan().getPhuongTien().getBienSoXe();
                if (ppc.layPhieuTiepNhan().getPhuongTien().getMauXe() != null) {
                    dongXe = ppc.layPhieuTiepNhan().getPhuongTien().getMauXe();
                }
            }

            String hangMuc = "Sửa chữa kỹ thuật";
            if (ppc.getChiTietBaoGia() != null && ppc.getChiTietBaoGia().getDichVu() != null) {
                hangMuc = ppc.getChiTietBaoGia().getDichVu().getTenDichVu();
            }

            String thoiGian = (ppc.getThoiGianHoanThanh() != null)
                    ? ppc.getThoiGianHoanThanh().format(DINH_DANG_GIO_NGAY)
                    : (ppc.getThoiGianPhanCong() != null ? ppc.getThoiGianPhanCong().format(DINH_DANG_GIO_NGAY) : "-");

            modelBang.addRow(new Object[]{
                    stt++,
                    ppc.getMaPhanCong(),
                    bienSo,
                    dongXe,
                    hangMuc,
                    thoiGian,
                    "Hoàn thành"
            });
        }
    }

    /**
     * Xuất báo cáo danh sách ca sửa hoàn tất ra file CSV
     */
    private void xuLyXuatBaoCao() {
        if (danhSachPhanCongHienThi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu ca sửa nào để xuất báo cáo!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File thuMuc = new File("exports");
            if (!thuMuc.exists()) {
                thuMuc.mkdirs();
            }
            String tenFile = "exports/BaoCao_LichSuCaSua_" + LocalDate.now().toString() + ".csv";
            try (PrintWriter pw = new PrintWriter(tenFile, StandardCharsets.UTF_8)) {
                pw.println("\uFEFFSTT,Mã phân công,Biển số xe,Dòng xe,Hạng mục thi công,Thời gian hoàn thành,Trạng thái");
                int stt = 1;
                for (PhieuPhanCong ppc : danhSachPhanCongHienThi) {
                    String bienSo = (ppc.layPhieuTiepNhan() != null && ppc.layPhieuTiepNhan().getPhuongTien() != null)
                            ? ppc.layPhieuTiepNhan().getPhuongTien().getBienSoXe() : "-";
                    String dongXe = (ppc.layPhieuTiepNhan() != null && ppc.layPhieuTiepNhan().getPhuongTien() != null && ppc.layPhieuTiepNhan().getPhuongTien().getMauXe() != null)
                            ? ppc.layPhieuTiepNhan().getPhuongTien().getMauXe() : "-";
                    String hangMuc = (ppc.getChiTietBaoGia() != null && ppc.getChiTietBaoGia().getDichVu() != null)
                            ? ppc.getChiTietBaoGia().getDichVu().getTenDichVu() : "-";
                    String thoiGian = (ppc.getThoiGianHoanThanh() != null)
                            ? ppc.getThoiGianHoanThanh().format(DINH_DANG_GIO_NGAY) : "-";

                    pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            stt++, ppc.getMaPhanCong(), bienSo, dongXe, hangMuc, thoiGian, "Hoàn thành");
                }
            }

            JOptionPane.showMessageDialog(this,
                    "✓ Xuất báo cáo lịch sử ca sửa hoàn tất thành công!\nĐường dẫn file: " + tenFile,
                    "Xuất báo cáo thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pengu Garage - Lịch Sử Ca Sửa Hoàn Tất (Kỹ Thuật Viên)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KyThuatVienManHinhNhatKyCongViec(null));
            frame.setVisible(true);
        });
    }
}
