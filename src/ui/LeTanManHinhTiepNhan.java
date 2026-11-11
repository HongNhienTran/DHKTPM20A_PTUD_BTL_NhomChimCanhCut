package ui;

import bus.PhieuTiepNhanBUS;
import entity.KhachHang;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.TaiKhoan;
import enums.TrangThaiPhieuTiepNhan;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Tiếp Nhận Xe dành cho Lễ tân (Pengu Garage).
 * Thiết kế giao diện hiện đại theo mẫu chuẩn:
 * - 4 Thẻ chỉ số tổng quan ở hàng trên: Chờ kiểm tra, Đang kiểm tra & báo giá, Đang sửa chữa, Hoàn thành.
 * - Nút "+ Tạo Tiếp Nhận" bật hộp thoại modal LeTanManHinhThemTiepNhan.
 * - Bảng danh sách phiếu tiếp nhận với bộ lọc tìm kiếm tức thời theo biển số, mã phiếu, SĐT, trạng thái.
 */
public class LeTanManHinhTiepNhan extends JPanel {

    // Bảng màu giao diện chuẩn Pengu Garage
    private static final Color MAU_NEN_TRANG = new Color(246, 249, 253);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 124, 48);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(24, 58, 92);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_HEADER_BANG = new Color(241, 245, 249);
    private static final Color MAU_CHON_DONG = new Color(254, 243, 199);

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();

    // Dữ liệu bộ nhớ tạm cho bảng
    private List<PhieuTiepNhan> danhSachGoc = new ArrayList<>();

    // Các thành phần giao diện
    private JLabel lblTiepNhanHomNayPill;
    private JLabel lblSoChoKiemTra;
    private JLabel lblSoDangKiemTra;
    private JLabel lblSoDangSuaChua;
    private JLabel lblSoHoanThanh;

    private JTextField txtTimKiem;
    private JComboBox<String> cboLocTrangThai;
    private DefaultTableModel moHinhBang;
    private JTable bangTiepNhan;
    private JLabel lblTongSoPhieu;

    public LeTanManHinhTiepNhan() {
        this(null);
    }

    public LeTanManHinhTiepNhan(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        khoiTaoGiaoDien();
        taiLaiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 16));
        setBackground(MAU_NEN_TRANG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // Cụm phía trên: Header tiêu đề + 4 Thẻ chỉ số
        JPanel pnlTren = new JPanel(new BorderLayout(0, 16));
        pnlTren.setOpaque(false);
        pnlTren.add(taoThanhTieuDe(), BorderLayout.NORTH);
        pnlTren.add(taoHangTheChiSo(), BorderLayout.CENTER);
        add(pnlTren, BorderLayout.NORTH);

        // Cụm ở giữa: Thẻ bảng dữ liệu danh sách phiếu tiếp nhận
        add(taoTheBangDuLieu(), BorderLayout.CENTER);
    }

    private JPanel taoThanhTieuDe() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Tiêu đề lớn bên trái
        JLabel lblTieuDe = new JLabel("Tiếp Nhận Xe");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_CHU_CHINH);
        header.add(lblTieuDe, BorderLayout.WEST);

        // Cụm nút thao tác bên phải
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumPhai.setOpaque(false);

        // Badge pill đếm xe hôm nay
        lblTiepNhanHomNayPill = new JLabel("Tiếp nhận hôm nay: 0 xe", IconUtil.layIcon("car.png", 14, 14, new Color(15, 41, 66)), SwingConstants.LEFT);
        lblTiepNhanHomNayPill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTiepNhanHomNayPill.setForeground(new Color(15, 41, 66));
        lblTiepNhanHomNayPill.setOpaque(true);
        lblTiepNhanHomNayPill.setBackground(new Color(237, 244, 255));
        lblTiepNhanHomNayPill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 228, 255), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        // Nút + Tạo tiếp nhận
        JButton btnTaoTiepNhan = new JButton("+ Tạo Tiếp Nhận", IconUtil.layIcon("plus.png", 14, 14, Color.WHITE)) {
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
        btnTaoTiepNhan.setIconTextGap(6);
        btnTaoTiepNhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTaoTiepNhan.setForeground(Color.WHITE);
        btnTaoTiepNhan.setFocusPainted(false);
        btnTaoTiepNhan.setBorderPainted(false);
        btnTaoTiepNhan.setContentAreaFilled(false);
        btnTaoTiepNhan.setOpaque(false);
        btnTaoTiepNhan.setPreferredSize(new Dimension(160, 36));
        btnTaoTiepNhan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTaoTiepNhan.addActionListener(e -> moModalThemTiepNhan());

        // Nút Làm Mới
        JButton btnLamMoi = new JButton("Làm Mới", IconUtil.layIcon("refresh.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_XANH_DAM_HOVER : MAU_XANH_DAM);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLamMoi.setIconTextGap(6);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setBorderPainted(false);
        btnLamMoi.setContentAreaFilled(false);
        btnLamMoi.setOpaque(false);
        btnLamMoi.setPreferredSize(new Dimension(115, 36));
        btnLamMoi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLamMoi.addActionListener(e -> taiLaiDuLieu());

        cumPhai.add(lblTiepNhanHomNayPill);
        cumPhai.add(btnTaoTiepNhan);
        cumPhai.add(btnLamMoi);

        header.add(cumPhai, BorderLayout.EAST);
        return header;
    }

    private JPanel taoHangTheChiSo() {
        JPanel hang = new JPanel(new GridLayout(1, 4, 16, 0));
        hang.setOpaque(false);

        lblSoChoKiemTra = new JLabel("0 xe");
        lblSoDangKiemTra = new JLabel("0 xe");
        lblSoDangSuaChua = new JLabel("0 xe");
        lblSoHoanThanh = new JLabel("0 xe");

        hang.add(taoTheChiSo("history.png", "Chờ Kiểm Tra Chẩn Đoán", lblSoChoKiemTra, new Color(37, 99, 235), new Color(239, 246, 255)));
        hang.add(taoTheChiSo("search.png", "Đang Kiểm Tra & Báo Giá", lblSoDangKiemTra, new Color(217, 119, 6), new Color(254, 243, 199)));
        hang.add(taoTheChiSo("service.png", "Đang Sửa Chữa Tại Xưởng", lblSoDangSuaChua, new Color(234, 88, 12), new Color(255, 237, 213)));
        hang.add(taoTheChiSo("save.png", "Hoàn Thành Chờ Bàn Giao", lblSoHoanThanh, new Color(16, 185, 129), new Color(236, 253, 245)));

        return hang;
    }

    private TheBoGoc taoTheChiSo(String tenIcon, String tieuDe, JLabel lblGiaTri, Color mauChuDao, Color mauNenIcon) {
        TheBoGoc the = new TheBoGoc(12, Color.WHITE, MAU_VIEN);
        the.setLayout(new BorderLayout(14, 0));
        the.setPreferredSize(new Dimension(0, 84));
        the.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Khối tròn icon bên trái
        JPanel pnlIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mauNenIcon);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pnlIcon.setOpaque(false);
        pnlIcon.setPreferredSize(new Dimension(46, 46));
        pnlIcon.setLayout(new GridBagLayout());
        JLabel lblIcon = new JLabel(IconUtil.layIcon(tenIcon, 20, 20, mauChuDao));
        pnlIcon.add(lblIcon);

        // Nội dung bên phải
        JPanel pnlChu = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlChu.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(MAU_CHU_PHU);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblGiaTri.setForeground(mauChuDao);

        pnlChu.add(lblTieuDe);
        pnlChu.add(lblGiaTri);

        the.add(pnlIcon, BorderLayout.WEST);
        the.add(pnlChu, BorderLayout.CENTER);
        return the;
    }

    private TheBoGoc taoTheBangDuLieu() {
        TheBoGoc the = new TheBoGoc(12, Color.WHITE, MAU_VIEN);
        the.setLayout(new BorderLayout(0, 12));
        the.setBorder(new EmptyBorder(16, 18, 16, 18));

        // 1. Thanh tìm kiếm & lọc trạng thái
        the.add(taoThanhLocVaTimKiem(), BorderLayout.NORTH);

        // 2. Bảng dữ liệu phiếu tiếp nhận
        String[] cot = {"STT", "Mã Tiếp Nhận", "Biển Số Xe", "Hãng / Mẫu Xe", "Thời Gian Vào", "Trạng Thái"};
        moHinhBang = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangTiepNhan = new JTable(moHinhBang);
        bangTiepNhan.setRowHeight(42);
        bangTiepNhan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangTiepNhan.setSelectionBackground(MAU_CHON_DONG);
        bangTiepNhan.setSelectionForeground(MAU_CHU_CHINH);
        bangTiepNhan.setGridColor(new Color(241, 245, 249));
        bangTiepNhan.setShowHorizontalLines(true);
        bangTiepNhan.setShowVerticalLines(false);
        bangTiepNhan.setFillsViewportHeight(true);

        // Căn chỉnh độ rộng cột
        bangTiepNhan.getColumnModel().getColumn(0).setPreferredWidth(50);
        bangTiepNhan.getColumnModel().getColumn(0).setMaxWidth(60);
        bangTiepNhan.getColumnModel().getColumn(1).setPreferredWidth(170);
        bangTiepNhan.getColumnModel().getColumn(2).setPreferredWidth(130);
        bangTiepNhan.getColumnModel().getColumn(3).setPreferredWidth(220);
        bangTiepNhan.getColumnModel().getColumn(4).setPreferredWidth(160);
        bangTiepNhan.getColumnModel().getColumn(5).setPreferredWidth(180);

        // Render header
        JTableHeader header = bangTiepNhan.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MAU_HEADER_BANG);
        header.setForeground(MAU_XANH_DAM);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DinhDangHeaderBang(header.getDefaultRenderer()));

        // Render dữ liệu từng cột
        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);

        bangTiepNhan.getColumnModel().getColumn(0).setCellRenderer(canGiua);
        bangTiepNhan.getColumnModel().getColumn(1).setCellRenderer(new DinhDangMaTiepNhan());
        bangTiepNhan.getColumnModel().getColumn(2).setCellRenderer(new DinhDangBienSoXe());
        bangTiepNhan.getColumnModel().getColumn(4).setCellRenderer(canGiua);
        bangTiepNhan.getColumnModel().getColumn(5).setCellRenderer(new DinhDangBadgeTrangThai());

        // Sự kiện click bảng: nhấp đúp hoặc chuột phải để thao tác
        bangTiepNhan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = bangTiepNhan.getSelectedRow();
                    if (row >= 0) {
                        xemChiTietPhieu(row);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    moMenuChuotPhai(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    moMenuChuotPhai(e);
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangTiepNhan);
        cuonBang.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cuonBang.getViewport().setBackground(Color.WHITE);
        the.add(cuonBang, BorderLayout.CENTER);

        // 3. Footer đếm số phiếu
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false);
        lblTongSoPhieu = new JLabel("Hiển thị 0 phiếu tiếp nhận");
        lblTongSoPhieu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTongSoPhieu.setForeground(MAU_CHU_PHU);
        pnlFooter.add(lblTongSoPhieu, BorderLayout.WEST);

        the.add(pnlFooter, BorderLayout.SOUTH);
        return the;
    }

    private JPanel taoThanhLocVaTimKiem() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        // Ô tìm kiếm bên trái
        txtTimKiem = new JTextField();
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimKiem.setPreferredSize(new Dimension(340, 36));
        datGoiYNhap(txtTimKiem, "Tìm kiếm biển số xe, mã phiếu, tên/SĐT khách...");
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                locVaHienThiDuLieu();
            }
        });

        // Cụm lọc bên phải
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cumPhai.setOpaque(false);

        cboLocTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái",
                "Chờ kiểm tra",
                "Đang kiểm tra",
                "Chờ xác nhận báo giá",
                "Đang sửa chữa",
                "Hoàn thành",
                "Đã thanh toán",
                "Đã hủy"
        });
        cboLocTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboLocTrangThai.setPreferredSize(new Dimension(175, 36));
        cboLocTrangThai.setBackground(Color.WHITE);
        cboLocTrangThai.setFocusable(false);
        cboLocTrangThai.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cboLocTrangThai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        cboLocTrangThai.addActionListener(e -> locVaHienThiDuLieu());

        JButton btnDatLai = new JButton("Đặt lại") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDatLai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDatLai.setForeground(MAU_CHU_PHU);
        btnDatLai.setFocusPainted(false);
        btnDatLai.setBorderPainted(false);
        btnDatLai.setContentAreaFilled(false);
        btnDatLai.setPreferredSize(new Dimension(80, 36));
        btnDatLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDatLai.addActionListener(e -> {
            txtTimKiem.setText("Tìm kiếm biển số xe, mã phiếu, tên/SĐT khách...");
            txtTimKiem.setForeground(MAU_CHU_PHU);
            cboLocTrangThai.setSelectedIndex(0);
            locVaHienThiDuLieu();
        });

        cumPhai.add(cboLocTrangThai);
        cumPhai.add(btnDatLai);

        bar.add(txtTimKiem, BorderLayout.WEST);
        bar.add(cumPhai, BorderLayout.EAST);
        return bar;
    }

    public void taiLaiDuLieu() {
        try {
            danhSachGoc = phieuTiepNhanBUS.layTatCa();
            capNhatCacChiSoTongQuan();
            locVaHienThiDuLieu();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải dữ liệu phiếu tiếp nhận: " + ex.getMessage());
        }
    }

    private void capNhatCacChiSoTongQuan() {
        int demChoKiemTra = 0;
        int demDangKiemTra = 0;
        int demDangSuaChua = 0;
        int demHoanThanh = 0;
        int demHomNay = 0;

        LocalDate homNay = LocalDate.now();

        for (PhieuTiepNhan ptn : danhSachGoc) {
            if (ptn.getThoiGianTiepNhan() != null
                    && ptn.getThoiGianTiepNhan().toLocalDate().isEqual(homNay)) {
                demHomNay++;
            }

            TrangThaiPhieuTiepNhan tt = ptn.getTrangThai();
            if (tt == null) continue;

            switch (tt) {
                case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA -> demDangKiemTra++;
                case DANG_SUA_CHUA -> demDangSuaChua++;
                case HOAN_THANH, THANH_TOAN -> demHoanThanh++;
                default -> {
                }
            }
        }

        lblTiepNhanHomNayPill.setText("Tiếp nhận hôm nay: " + demHomNay + " xe");
        lblSoChoKiemTra.setText(demChoKiemTra + " xe");
        lblSoDangKiemTra.setText(demDangKiemTra + " xe");
        lblSoDangSuaChua.setText(demDangSuaChua + " xe");
        lblSoHoanThanh.setText(demHoanThanh + " xe");
    }

    private void locVaHienThiDuLieu() {
        moHinhBang.setRowCount(0);

        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        if (tuKhoa.startsWith("tìm kiếm")) {
            tuKhoa = "";
        }

        String locTT = (String) cboLocTrangThai.getSelectedItem();
        boolean locTatCa = (locTT == null || locTT.equals("Tất cả trạng thái"));

        int stt = 1;
        for (PhieuTiepNhan ptn : danhSachGoc) {
            // Lọc trạng thái
            if (!locTatCa) {
                String tenTT = ptn.getTrangThai() != null ? ptn.getTrangThai().getTenHienThi() : "";
                if (!tenTT.equalsIgnoreCase(locTT)) {
                    continue;
                }
            }

            // Lọc từ khóa
            if (!tuKhoa.isEmpty()) {
                String ma = ptn.getMaTiepNhan() != null ? ptn.getMaTiepNhan().toLowerCase() : "";
                String bienSo = ptn.getPhuongTien() != null && ptn.getPhuongTien().getBienSoXe() != null
                        ? ptn.getPhuongTien().getBienSoXe().toLowerCase() : "";
                String tenXe = ptn.getPhuongTien() != null && ptn.getPhuongTien().getMauXe() != null
                        ? ptn.getPhuongTien().getMauXe().toLowerCase() : "";

                KhachHang kh = ptn.getPhuongTien() != null ? ptn.getPhuongTien().getKhachHang() : null;
                String tenKH = kh != null && kh.getHoTen() != null ? kh.getHoTen().toLowerCase() : "";
                String sdt = kh != null && kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "";

                if (!ma.contains(tuKhoa) && !bienSo.contains(tuKhoa) && !tenXe.contains(tuKhoa)
                        && !tenKH.contains(tuKhoa) && !sdt.contains(tuKhoa)) {
                    continue;
                }
            }

            // Thêm vào bảng
            PhuongTien pt = ptn.getPhuongTien();
            String bienSoXe = pt != null ? pt.getBienSoXe() : "---";
            String hangMauXe = pt != null ? (pt.getHangXe() != null ? pt.getHangXe().getTenHang() + " " : "") + pt.getMauXe() : "---";
            String thoiGian = ptn.getThoiGianTiepNhan() != null ? ptn.getThoiGianTiepNhan().format(DINH_DANG_NGAY_GIO) : "---";
            String trangThai = ptn.getTrangThai() != null ? ptn.getTrangThai().getTenHienThi() : "---";

            moHinhBang.addRow(new Object[]{
                    stt++,
                    ptn.getMaTiepNhan(),
                    bienSoXe,
                    hangMauXe,
                    thoiGian,
                    trangThai
            });
        }

        lblTongSoPhieu.setText("Hiển thị " + moHinhBang.getRowCount() + " phiếu tiếp nhận");
    }

    private void moModalThemTiepNhan() {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhThemTiepNhan modal = new LeTanManHinhThemTiepNhan(cuaSo, taiKhoanDangNhap, this::taiLaiDuLieu);
        modal.setVisible(true);
    }

    private void xemChiTietPhieu(int row) {
        String maTN = (String) moHinhBang.getValueAt(row, 1);
        PhieuTiepNhan ptn = phieuTiepNhanBUS.timTheoMa(maTN);
        if (ptn == null) return;

        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhTiepNhanXemChiTietTiepNhan modal = new LeTanManHinhTiepNhanXemChiTietTiepNhan(cuaSo, ptn);
        modal.setVisible(true);
    }

    private void moModalChiTietSuaChua(PhieuTiepNhan ptn) {
        if (ptn == null) return;
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhHangDoiPhieuChiTietDangSua modal = new LeTanManHinhHangDoiPhieuChiTietDangSua(
                cuaSo, taiKhoanDangNhap, ptn, this::taiLaiDuLieu);
        modal.setVisible(true);
    }

    private void moMenuChuotPhai(MouseEvent e) {
        Point p = e.getPoint();
        int row = bangTiepNhan.rowAtPoint(p);
        if (row < 0) return;

        bangTiepNhan.setRowSelectionInterval(row, row);
        String maTN = (String) moHinhBang.getValueAt(row, 1);
        PhieuTiepNhan ptn = phieuTiepNhanBUS.timTheoMa(maTN);
        if (ptn == null) return;

        JPopupMenu menu = new JPopupMenu();

        JMenuItem mnuXem = new JMenuItem("Xem chi tiết phiếu tiếp nhận", IconUtil.layIcon("view.png", 14, 14, MAU_XANH_DAM));
        mnuXem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mnuXem.addActionListener(ev -> xemChiTietPhieu(row));
        menu.add(mnuXem);

        TrangThaiPhieuTiepNhan tt = ptn.getTrangThai();
        if (tt == TrangThaiPhieuTiepNhan.DANG_KIEM_TRA || tt == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA) {
            JMenuItem mnuChuyenSuaChua = new JMenuItem("Chuyển sang: Đang sửa chữa", IconUtil.layIcon("service.png", 14, 14, MAU_CAM_CHU_DAO));
            mnuChuyenSuaChua.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            mnuChuyenSuaChua.addActionListener(ev -> chuyenTrangThaiPhieu(maTN, TrangThaiPhieuTiepNhan.DANG_SUA_CHUA));
            menu.add(mnuChuyenSuaChua);
        } else if (tt == TrangThaiPhieuTiepNhan.DANG_SUA_CHUA) {
            JMenuItem mnuTienDo = new JMenuItem("Xem chi tiết tiến độ sửa chữa", IconUtil.layIcon("history.png", 14, 14, MAU_XANH_DAM));
            mnuTienDo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            mnuTienDo.addActionListener(ev -> moModalChiTietSuaChua(ptn));
            menu.add(mnuTienDo);
        }

        if (tt != TrangThaiPhieuTiepNhan.HOAN_THANH && tt != TrangThaiPhieuTiepNhan.DA_HUY && tt != TrangThaiPhieuTiepNhan.THANH_TOAN) {
            menu.addSeparator();
            JMenuItem mnuHuy = new JMenuItem("Hủy phiếu tiếp nhận", IconUtil.layIcon("close.png", 14, 14, new Color(225, 29, 72)));
            mnuHuy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            mnuHuy.setForeground(new Color(225, 29, 72));
            mnuHuy.addActionListener(ev -> huyPhieuTiepNhan(maTN));
            menu.add(mnuHuy);
        }

        menu.show(bangTiepNhan, e.getX(), e.getY());
    }

    private void chuyenTrangThaiPhieu(String maTN, TrangThaiPhieuTiepNhan ttMoi) {
        try {
            boolean ok = phieuTiepNhanBUS.chuyenTrangThai(maTN, ttMoi);
            if (ok) {
                taiLaiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể chuyển trạng thái phiếu!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi chuyển trạng thái: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void huyPhieuTiepNhan(String maTN) {
        int hoi = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy phiếu tiếp nhận [" + maTN + "] không?",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (hoi != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = phieuTiepNhanBUS.huyPhieuTiepNhan(maTN);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã hủy phiếu tiếp nhận!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiLaiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể hủy phiếu tiếp nhận này!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi hủy phiếu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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

    // Custom Cell Renderers
    private static class DinhDangHeaderBang extends DefaultTableCellRenderer {
        private final javax.swing.table.TableCellRenderer goc;

        DinhDangHeaderBang(javax.swing.table.TableCellRenderer goc) {
            this.goc = goc;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = goc.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(new Font("Segoe UI", Font.BOLD, 12));
            c.setForeground(MAU_XANH_DAM);
            c.setBackground(MAU_HEADER_BANG);
            if (c instanceof JLabel lbl) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
            }
            return c;
        }
    }

    private static class DinhDangMaTiepNhan extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(new Color(15, 41, 66));
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    private static class DinhDangBienSoXe extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(MAU_CHU_CHINH);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    private static class DinhDangBadgeTrangThai extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            JLabel badge = new JLabel(text, SwingConstants.CENTER);
            badge.setOpaque(true);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));

            Color bg = new Color(241, 245, 249);
            Color fg = MAU_CHU_PHU;

            if (text.contains("Chờ kiểm tra")) {
                bg = new Color(239, 246, 255);
                fg = new Color(37, 99, 235);
            } else if (text.contains("Đang kiểm tra") || text.contains("báo giá")) {
                bg = new Color(254, 243, 199);
                fg = new Color(217, 119, 6);
            } else if (text.contains("Đang sửa chữa")) {
                bg = new Color(255, 237, 213);
                fg = new Color(234, 88, 12);
            } else if (text.contains("Hoàn thành")) {
                bg = new Color(236, 253, 245);
                fg = new Color(16, 185, 129);
            } else if (text.contains("Đã thanh toán")) {
                bg = new Color(238, 242, 255);
                fg = new Color(79, 70, 229);
            } else if (text.contains("Đã hủy")) {
                bg = new Color(254, 242, 242);
                fg = new Color(225, 29, 72);
            }

            badge.setBackground(isSelected ? MAU_CHON_DONG : bg);
            badge.setForeground(fg);
            badge.setBorder(new EmptyBorder(3, 8, 3, 8));
            return badge;
        }
    }

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
            JFrame f = new JFrame("Pengu Garage - Tiếp Nhận Xe");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1280, 768);
            f.setLocationRelativeTo(null);
            f.setContentPane(new LeTanManHinhTiepNhan(null));
            f.setVisible(true);
        });
    }
}
