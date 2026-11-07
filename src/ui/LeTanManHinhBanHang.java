package ui;

import bus.DanhMucSanPhamBUS;
import bus.HoaDonBUS;
import bus.KhachHangBUS;
import bus.MaKhuyenMaiBUS;
import bus.SanPhamBUS;
import dao.NhanVienDAO;
import entity.ChiTietHoaDon;
import entity.DanhMucSanPham;
import entity.HoaDon;
import entity.KhachHang;
import entity.MaKhuyenMai;
import entity.NhanVien;
import entity.SanPham;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.LoaiGiamGia;
import enums.LoaiHangMuc;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiThanhToan;
import util.IconUtil;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
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
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Màn hình tạo đơn bán hàng lẻ & phụ tùng dành riêng cho Lễ tân (POS Bán Hàng).
 * Thiết kế giao diện hiện đại, chuyên nghiệp, hỗ trợ:
 * - Đa tab hóa đơn (quản lý nhiều giỏ hàng chờ cùng lúc).
 * - Quét mã vạch / Tìm kiếm sản phẩm thông minh (F3).
 * - Khay menu phụ tùng dạng thẻ trực quan (có thể Mở rộng / Thu nhỏ).
 * - Tìm kiếm & tạo nhanh hồ sơ khách hàng.
 * - Áp dụng mã khuyến mãi tự động / thủ công.
 * - Tính toán thuế VAT, tiền thừa, hỗ trợ thanh toán tiền mặt và chuyển khoản QR.
 */
public class LeTanManHinhBanHang extends JPanel {

    // Bảng màu thiết kế đồng bộ theo hệ thống Pengu Garage
    private static final Color MAU_NEN_CHINH = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_VIEN_HOVER = new Color(203, 213, 225);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 132, 52);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_DO = new Color(220, 38, 38);
    private static final Color MAU_CHON_DONG = new Color(255, 247, 237);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);

    private static final String PLACEHOLDER_TIM_SP = "Nhập tên hoặc quét mã vạch sản phẩm (F3)...";
    private static final String PLACEHOLDER_SDT = "Nhập số điện thoại khách hàng...";

    // BUS & Services
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private final DanhMucSanPhamBUS danhMucBUS = new DanhMucSanPhamBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final MaKhuyenMaiBUS maKhuyenMaiBUS = new MaKhuyenMaiBUS();
    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final DecimalFormat dinhDangTien;

    private final TaiKhoan taiKhoanDangNhap;
    private int demThuTuTab = 1;

    // Danh sách tab đơn hàng chờ
    private final List<DonBanHangTam> danhSachDonTam = new ArrayList<>();
    private int chiSoDonHienTai = 0;

    // Component vùng bên trái (Tìm kiếm, Giỏ hàng, Menu sản phẩm)
    private JTextField txtTimKiemSP;
    private JPopupMenu popupGoiYSP;
    private JPanel pnlThanhTabs;
    private CardLayout layoutVungGioHang;
    private JPanel pnlVungGioHang;
    private DefaultTableModel moHinhBangGioHang;
    private JTable bangGioHang;

    // Khay menu sản phẩm bên dưới
    private JPanel pnlNganKeo;
    private JPanel pnlLuoiSanPham;
    private JComboBox<LuaChonDanhMuc> cboDanhMucSanPham;
    private NutTabNganKeo btnTabBanChay;
    private NutTabNganKeo btnTabSanPhamMoi;
    private enum CheDoNganKeo { DANH_MUC, BAN_CHAY, SAN_PHAM_MOI }
    private CheDoNganKeo cheDoNganKeoHienTai = CheDoNganKeo.DANH_MUC;
    private JButton btnMoRongThuNho;
    private boolean dangMoRongNganKeo = false;

    // Component vùng bên phải (Thông tin thanh toán)
    private JTextField txtTimSDTKhach;
    private JButton btnTraCuuSDT;
    private JTextField txtTenKhachHang;
    private JButton btnHuyChonKhach;

    private JLabel lblTongTienHang;
    private JLabel lblTienKhuyenMai;
    private JComboBox<MaKhuyenMai> cboMaKhuyenMai;
    private JLabel lblThueVAT;
    private JLabel lblKhachCanTra;
    private JTextField txtTienKhachDua;
    private JLabel lblTienThuaTraKhach;

    private JRadioButton radTienMat;
    private JRadioButton radChuyenKhoan;
    private JTextArea txtGhiChu;

    private boolean dangCapNhatDuLieu = false;

    public LeTanManHinhBanHang() {
        this(null);
    }

    public LeTanManHinhBanHang(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;

        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        kyHieu.setGroupingSeparator('.');
        kyHieu.setDecimalSeparator(',');
        this.dinhDangTien = new DecimalFormat("#,##0", kyHieu);

        khoiTaoDuLieuBanDau();
        khoiTaoGiaoDien();
        caiDatPhimTat();
        chuyenSangDonTam(0);
    }

    private void khoiTaoDuLieuBanDau() {
        // Mặc định tạo đơn hàng số 1 đầu tiên
        DonBanHangTam donDau = new DonBanHangTam("Hóa đơn 1");
        danhSachDonTam.add(donDau);
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(14, 0));
        setBackground(MAU_NEN_CHINH);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // 1. Phân vùng bên trái: Tìm kiếm + Tab đơn + Giỏ hàng + Khay menu sản phẩm
        JPanel pnlTrai = taoPhanVungTrai();
        add(pnlTrai, BorderLayout.CENTER);

        // 2. Phân vùng bên phải: Thẻ Thông tin thanh toán (rộng 380px)
        JPanel pnlPhai = taoPhanVungPhai();
        pnlPhai.setPreferredSize(new Dimension(380, 0));
        add(pnlPhai, BorderLayout.EAST);
    }

    // =========================================================================
    // XÂY DỰNG PHÂN VÙNG TRÁI: TÌM KIẾM, TAB, GIỎ HÀNG, MENU PHỤ TÙNG
    // =========================================================================

    private JPanel taoPhanVungTrai() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        // Hàng trên: Thanh tìm kiếm sản phẩm bên trái + Hàng Tab hóa đơn bên phải
        JPanel hangTren = new JPanel(new BorderLayout(10, 0));
        hangTren.setOpaque(false);

        JPanel pnlTimKiem = taoKhungTimKiemSanPham();
        pnlThanhTabs = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pnlThanhTabs.setOpaque(false);
        capNhatGiaoDienTabs();

        hangTren.add(pnlTimKiem, BorderLayout.CENTER);
        hangTren.add(pnlThanhTabs, BorderLayout.EAST);

        // Vùng trung tâm: Thẻ chứa bảng giỏ hàng (kèm chế độ rỗng)
        JPanel theGioHang = taoTheGioHang();

        // Vùng dưới: Khay menu sản phẩm phụ tùng dạng lưới
        JPanel theMenuDuoi = taoTheMenuPhuTung();

        pnl.add(hangTren, BorderLayout.NORTH);
        pnl.add(theGioHang, BorderLayout.CENTER);
        pnl.add(theMenuDuoi, BorderLayout.SOUTH);

        return pnl;
    }

    private JPanel taoKhungTimKiemSanPham() {
        TheBoGoc the = new TheBoGoc(12, MAU_THE_TRANG, MAU_VIEN);
        the.setLayout(new BorderLayout(8, 0));
        the.setPreferredSize(new Dimension(0, 42));
        the.setBorder(new EmptyBorder(0, 14, 0, 14));

        JLabel lblIcon = new JLabel();
        Icon iconSearch = IconUtil.layIcon("search.png", 16, 16, MAU_CHU_MO);
        if (iconSearch != null) {
            lblIcon.setIcon(iconSearch);
        }

        txtTimKiemSP = new JTextField(PLACEHOLDER_TIM_SP);
        txtTimKiemSP.setBorder(null);
        txtTimKiemSP.setOpaque(false);
        txtTimKiemSP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimKiemSP.setForeground(MAU_CHU_MO);

        popupGoiYSP = new JPopupMenu();

        txtTimKiemSP.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (PLACEHOLDER_TIM_SP.equals(txtTimKiemSP.getText())) {
                    txtTimKiemSP.setText("");
                    txtTimKiemSP.setForeground(MAU_CHU_CHINH);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiemSP.getText().trim().isEmpty()) {
                    txtTimKiemSP.setText(PLACEHOLDER_TIM_SP);
                    txtTimKiemSP.setForeground(MAU_CHU_MO);
                }
            }
        });

        txtTimKiemSP.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { xuLyTimKiemSanPhamLive(); }
            @Override public void removeUpdate(DocumentEvent e) { xuLyTimKiemSanPhamLive(); }
            @Override public void changedUpdate(DocumentEvent e) { xuLyTimKiemSanPhamLive(); }
        });

        txtTimKiemSP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    xuLyChonSanPhamBangEnter();
                }
            }
        });

        the.add(lblIcon, BorderLayout.WEST);
        the.add(txtTimKiemSP, BorderLayout.CENTER);
        return the;
    }

    private void capNhatGiaoDienTabs() {
        pnlThanhTabs.removeAll();

        for (int i = 0; i < danhSachDonTam.size(); i++) {
            DonBanHangTam don = danhSachDonTam.get(i);
            final int index = i;
            boolean laActive = (i == chiSoDonHienTai);

            JPanel tabItem = new JPanel(new BorderLayout(6, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    RoundRectangle2D rect = new RoundRectangle2D.Float(
                            0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
                    g2.setColor(laActive ? Color.WHITE : new Color(241, 245, 249));
                    g2.fill(rect);
                    g2.setColor(laActive ? MAU_CAM_CHU_DAO : MAU_VIEN);
                    g2.setStroke(new BasicStroke(laActive ? 1.5f : 1f));
                    g2.draw(rect);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            tabItem.setOpaque(false);
            tabItem.setPreferredSize(new Dimension(125, 36));
            tabItem.setBorder(new EmptyBorder(0, 10, 0, 6));
            tabItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tabItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chuyenSangDonTam(index);
                }
            });

            JLabel lblTenTab = new JLabel(don.getTenDon());
            lblTenTab.setFont(new Font("Segoe UI", laActive ? Font.BOLD : Font.PLAIN, 12));
            lblTenTab.setForeground(laActive ? MAU_CAM_CHU_DAO : MAU_CHU_CHINH);
            lblTenTab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JButton btnDongTab = new JButton() {
                private boolean hover = false;
                {
                    addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) {
                            hover = true;
                            setIcon(IconUtil.layIcon("close.png", 10, 10, MAU_DO));
                            repaint();
                        }
                        @Override public void mouseExited(MouseEvent e) {
                            hover = false;
                            setIcon(IconUtil.layIcon("close.png", 10, 10, laActive ? MAU_CAM_CHU_DAO : MAU_CHU_MO));
                            repaint();
                        }
                    });
                }
                @Override
                protected void paintComponent(Graphics g) {
                    if (hover) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(254, 226, 226));
                        g2.fillOval(0, 0, getWidth(), getHeight());
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };
            btnDongTab.setIcon(IconUtil.layIcon("close.png", 10, 10, laActive ? MAU_CAM_CHU_DAO : MAU_CHU_MO));
            btnDongTab.setMargin(new java.awt.Insets(0, 0, 0, 0));
            btnDongTab.setBorder(null);
            btnDongTab.setFocusPainted(false);
            btnDongTab.setBorderPainted(false);
            btnDongTab.setContentAreaFilled(false);
            btnDongTab.setOpaque(false);
            btnDongTab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnDongTab.setPreferredSize(new Dimension(18, 18));
            btnDongTab.setToolTipText("Đóng hóa đơn này");
            btnDongTab.addActionListener(e -> xuLyDongTab(index));

            tabItem.add(lblTenTab, BorderLayout.CENTER);
            tabItem.add(btnDongTab, BorderLayout.EAST);

            pnlThanhTabs.add(tabItem);
        }

        // Nút thêm tab mới [+] chuẩn icon plus.png
        JButton btnThemTab = new JButton() {
            private boolean hover = false;
            {
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
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
                g2.setColor(hover ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(rect);
                g2.setColor(hover ? MAU_CAM_CHU_DAO : MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnThemTab.setIcon(IconUtil.layIcon("plus.png", 14, 14, MAU_CHU_PHU));
        btnThemTab.setRolloverIcon(IconUtil.layIcon("plus.png", 14, 14, MAU_CAM_CHU_DAO));
        btnThemTab.setFocusPainted(false);
        btnThemTab.setBorderPainted(false);
        btnThemTab.setContentAreaFilled(false);
        btnThemTab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemTab.setPreferredSize(new Dimension(36, 36));
        btnThemTab.setToolTipText("Thêm hóa đơn bán hàng mới");
        btnThemTab.addActionListener(e -> xuLyThemTabMoi());

        pnlThanhTabs.add(btnThemTab);

        pnlThanhTabs.revalidate();
        pnlThanhTabs.repaint();
    }

    private JPanel taoTheGioHang() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN);
        the.setLayout(new BorderLayout());

        layoutVungGioHang = new CardLayout();
        pnlVungGioHang = new JPanel(layoutVungGioHang);
        pnlVungGioHang.setOpaque(false);

        // 1. Giao diện trạng thái giỏ hàng rỗng (Empty state)
        JPanel pnlRong = new JPanel(new GridBagLayout());
        pnlRong.setOpaque(false);

        JPanel cumRong = new JPanel();
        cumRong.setOpaque(false);
        cumRong.setLayout(new BoxLayout(cumRong, BoxLayout.Y_AXIS));

        JLabel lblIconCart = new JLabel();
        lblIconCart.setAlignmentX(Component.CENTER_ALIGNMENT);
        Icon iconCart = IconUtil.layIcon("cart.png", 48, 48, MAU_CHU_MO);
        if (iconCart != null) {
            lblIconCart.setIcon(iconCart);
        }

        JLabel lblRong1 = new JLabel("Chưa có sản phẩm trong đơn hàng");
        lblRong1.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRong1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRong1.setForeground(MAU_CHU_PHU);

        JLabel lblRong2 = new JLabel("Chọn từ menu sản phẩm bên dưới hoặc quét mã vạch để thêm nhanh");
        lblRong2.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRong2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRong2.setForeground(MAU_CHU_MO);

        cumRong.add(lblIconCart);
        cumRong.add(Box.createVerticalStrut(10));
        cumRong.add(lblRong1);
        cumRong.add(Box.createVerticalStrut(4));
        cumRong.add(lblRong2);

        pnlRong.add(cumRong);

        // 2. Giao diện bảng danh sách sản phẩm đã chọn
        String[] tenCot = {
                "#", "Mã", "Tên sản phẩm", "ĐVT", "Đơn giá", "Số lượng", "Thành tiền", "Xóa"
        };
        moHinhBangGioHang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangGioHang = new JTable(moHinhBangGioHang);
        bangGioHang.setRowHeight(40);
        bangGioHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangGioHang.setForeground(MAU_CHU_CHINH);
        bangGioHang.setBackground(Color.WHITE);
        bangGioHang.setSelectionBackground(MAU_CHON_DONG);
        bangGioHang.setSelectionForeground(MAU_CHU_CHINH);
        bangGioHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangGioHang.setShowVerticalLines(false);
        bangGioHang.setShowHorizontalLines(true);
        bangGioHang.setGridColor(MAU_KE_BANG);
        bangGioHang.setIntercellSpacing(new Dimension(0, 1));
        bangGioHang.setFillsViewportHeight(true);

        JTableHeader header = bangGioHang.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new BoVeTieuDeBangPOS());

        TableColumnModel colModel = bangGioHang.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(40);
        colModel.getColumn(1).setPreferredWidth(105);
        colModel.getColumn(2).setPreferredWidth(260);
        colModel.getColumn(3).setPreferredWidth(65);
        colModel.getColumn(4).setPreferredWidth(110);
        colModel.getColumn(5).setPreferredWidth(115); // Cột số lượng [+ / -]
        colModel.getColumn(6).setPreferredWidth(125);
        colModel.getColumn(7).setPreferredWidth(50);  // Cột xóa icon thùng rác

        BoVeOGioHang rendererO = new BoVeOGioHang();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            if (i != 5 && i != 7) {
                colModel.getColumn(i).setCellRenderer(rendererO);
            }
        }
        colModel.getColumn(5).setCellRenderer(new BoVeSoLuongEditor());
        colModel.getColumn(7).setCellRenderer(new BoVeNutXoa());

        // Lắng nghe click xóa dòng, tăng giảm số lượng (+ / -), nhấp đúp xem chi tiết
        bangGioHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cot = bangGioHang.columnAtPoint(e.getPoint());
                int dong = bangGioHang.rowAtPoint(e.getPoint());
                if (dong < 0) return;

                if (cot == 7) {
                    xuLyXoaKhoiGioHang(dong);
                } else if (cot == 5) {
                    java.awt.Rectangle cellRect = bangGioHang.getCellRect(dong, cot, false);
                    int xInCell = e.getX() - cellRect.x;
                    if (xInCell <= 36) {
                        xuLyGiamSoLuong(dong);
                    } else if (xInCell >= cellRect.width - 36) {
                        xuLyTangSoLuong(dong);
                    }
                } else if (e.getClickCount() == 2) {
                    DonBanHangTam don = layDonHienTai();
                    if (don != null && dong < don.getDanhSachHang().size()) {
                        SanPham sp = don.getDanhSachHang().get(dong).getSanPham();
                        Window cuaSoCha = SwingUtilities.getWindowAncestor(LeTanManHinhBanHang.this);
                        new QuanLyManHinhQuanLySanPhamXemChiTiet(cuaSoCha, sp, true, sanPhamBUS, danhMucBUS).setVisible(true);
                    }
                }
            }
        });

        moHinhBangGioHang.addTableModelListener(e -> {
            if (dangCapNhatDuLieu) return;
            int dong = e.getFirstRow();
            int cot = e.getColumn();
            if (dong >= 0 && cot == 5) {
                xuLyThayDoiSoLuong(dong);
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangGioHang);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cuonBang.getViewport().setBackground(Color.WHITE);

        pnlVungGioHang.add(pnlRong, "RONG");
        pnlVungGioHang.add(cuonBang, "CO_HANG");

        the.add(pnlVungGioHang, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoTheMenuPhuTung() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN);
        the.setLayout(new BorderLayout());

        // 1. Thanh tiêu đề ngăn kéo: DANH MỤC + TAB BÁN CHẠY + TAB SẢN PHẨM MỚI + Nút Mở rộng / Thu nhỏ
        JPanel thanhNganKeo = new JPanel(new BorderLayout(10, 0));
        thanhNganKeo.setOpaque(false);
        thanhNganKeo.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel cumTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cumTrai.setOpaque(false);

        JLabel lblDanhMuc = new JLabel("DANH MỤC:");
        lblDanhMuc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDanhMuc.setForeground(MAU_CHU_PHU);

        cboDanhMucSanPham = new JComboBox<>();
        cboDanhMucSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDanhMucSanPham.setPreferredSize(new Dimension(175, 34));
        cboDanhMucSanPham.setBackground(Color.WHITE);
        cboDanhMucSanPham.setFocusable(false);
        cboDanhMucSanPham.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cboDanhMucSanPham.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        napDanhMucVaoCombo();
        cboDanhMucSanPham.addActionListener(e -> {
            cheDoNganKeoHienTai = CheDoNganKeo.DANH_MUC;
            if (btnTabBanChay != null) btnTabBanChay.setDangChon(false);
            if (btnTabSanPhamMoi != null) btnTabSanPhamMoi.setDangChon(false);
            if (!dangMoRongNganKeo) {
                chuyenDoiTrangThaiNganKeo();
            }
            napDanhSachPhuTungVaoNganKeo();
        });

        // Tab "Bán chạy"
        btnTabBanChay = new NutTabNganKeo("Bán chạy", "chart.png");
        btnTabBanChay.addActionListener(e -> {
            cheDoNganKeoHienTai = CheDoNganKeo.BAN_CHAY;
            btnTabBanChay.setDangChon(true);
            btnTabSanPhamMoi.setDangChon(false);
            if (!dangMoRongNganKeo) {
                chuyenDoiTrangThaiNganKeo();
            }
            napDanhSachPhuTungVaoNganKeo();
        });

        // Tab "Sản phẩm mới"
        btnTabSanPhamMoi = new NutTabNganKeo("Sản phẩm mới", "plus.png");
        btnTabSanPhamMoi.addActionListener(e -> {
            cheDoNganKeoHienTai = CheDoNganKeo.SAN_PHAM_MOI;
            btnTabSanPhamMoi.setDangChon(true);
            btnTabBanChay.setDangChon(false);
            if (!dangMoRongNganKeo) {
                chuyenDoiTrangThaiNganKeo();
            }
            napDanhSachPhuTungVaoNganKeo();
        });

        cumTrai.add(lblDanhMuc);
        cumTrai.add(cboDanhMucSanPham);
        cumTrai.add(Box.createHorizontalStrut(6));
        cumTrai.add(btnTabBanChay);
        cumTrai.add(btnTabSanPhamMoi);

        btnMoRongThuNho = new JButton("▲ Mở rộng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
                g2.setColor(new Color(241, 245, 249));
                g2.fill(rect);
                g2.setColor(MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnMoRongThuNho.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMoRongThuNho.setForeground(MAU_CHU_CHINH);
        btnMoRongThuNho.setFocusPainted(false);
        btnMoRongThuNho.setBorderPainted(false);
        btnMoRongThuNho.setContentAreaFilled(false);
        btnMoRongThuNho.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMoRongThuNho.setPreferredSize(new Dimension(105, 34));
        btnMoRongThuNho.addActionListener(e -> chuyenDoiTrangThaiNganKeo());

        thanhNganKeo.add(cumTrai, BorderLayout.WEST);
        thanhNganKeo.add(btnMoRongThuNho, BorderLayout.EAST);

        // 2. Nội dung lưới sản phẩm phụ tùng - Chiều cao mở rộng đủ cho 2 hàng sản phẩm
        pnlLuoiSanPham = new JPanel();
        pnlLuoiSanPham.setOpaque(false);
        pnlLuoiSanPham.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane cuonLuoi = new JScrollPane(pnlLuoiSanPham);
        cuonLuoi.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));
        cuonLuoi.getViewport().setBackground(Color.WHITE);
        cuonLuoi.getHorizontalScrollBar().setUnitIncrement(25);
        cuonLuoi.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cuonLuoi.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        cuonLuoi.setPreferredSize(new Dimension(0, 365)); // Tăng chiều cao để hiển thị đủ 2 hàng

        pnlNganKeo = new JPanel(new BorderLayout());
        pnlNganKeo.setOpaque(false);
        pnlNganKeo.setPreferredSize(new Dimension(0, 365));
        pnlNganKeo.add(cuonLuoi, BorderLayout.CENTER);
        pnlNganKeo.setVisible(false); // Mặc định thu nhỏ, bấm mở rộng sẽ hiển thị 2 hàng

        the.add(thanhNganKeo, BorderLayout.NORTH);
        the.add(pnlNganKeo, BorderLayout.CENTER);

        return the;
    }

    private void chuyenDoiTrangThaiNganKeo() {
        dangMoRongNganKeo = !dangMoRongNganKeo;
        pnlNganKeo.setVisible(dangMoRongNganKeo);
        btnMoRongThuNho.setText(dangMoRongNganKeo ? "▼ Thu nhỏ" : "▲ Mở rộng");
        if (dangMoRongNganKeo && pnlLuoiSanPham.getComponentCount() == 0) {
            napDanhSachPhuTungVaoNganKeo();
        }
        revalidate();
        repaint();
    }

    private void napDanhMucVaoCombo() {
        DefaultComboBoxModel<LuaChonDanhMuc> model = new DefaultComboBoxModel<>();
        model.addElement(new LuaChonDanhMuc(null, "Danh mục"));
        try {
            List<DanhMucSanPham> ds = danhMucBUS.layTatCa();
            for (DanhMucSanPham dm : ds) {
                model.addElement(new LuaChonDanhMuc(dm.getMaDanhMuc(), dm.getTenDanhMuc()));
            }
        } catch (Exception ignored) {
        }
        cboDanhMucSanPham.setModel(model);
    }

    private void napDanhSachPhuTungVaoNganKeo() {
        pnlLuoiSanPham.removeAll();

        List<SanPham> ds;
        try {
            switch (cheDoNganKeoHienTai) {
                case BAN_CHAY -> ds = sanPhamBUS.layDanhSachBanChay(30);
                case SAN_PHAM_MOI -> ds = sanPhamBUS.layDanhSachSanPhamMoi(30);
                case DANH_MUC -> {
                    LuaChonDanhMuc sel = (LuaChonDanhMuc) cboDanhMucSanPham.getSelectedItem();
                    String maDanhMuc = sel != null ? sel.maDanhMuc() : null;
                    if (maDanhMuc != null) {
                        ds = sanPhamBUS.layTheoDanhMuc(maDanhMuc);
                    } else {
                        ds = sanPhamBUS.layTatCa();
                    }
                }
                default -> ds = sanPhamBUS.layTatCa();
            }
        } catch (Exception ex) {
            ds = new ArrayList<>();
        }

        int soLuong = 0;
        for (SanPham sp : ds) {
            if (sp == null || !sp.isTrangThai()) {
                continue;
            }
            pnlLuoiSanPham.add(taoThePhuTungPOS(sp));
            soLuong++;
        }

        if (soLuong == 0) {
            pnlLuoiSanPham.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 60));
            JLabel lblTrong = new JLabel("Không có sản phẩm nào để hiển thị trong mục này");
            lblTrong.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblTrong.setForeground(MAU_CHU_MO);
            pnlLuoiSanPham.add(lblTrong);
        } else {
            // Chia đều sản phẩm thành đúng 2 hàng
            int soCot = Math.max(1, (soLuong + 1) / 2);
            pnlLuoiSanPham.setLayout(new GridLayout(2, soCot, 12, 10));
        }

        pnlLuoiSanPham.revalidate();
        pnlLuoiSanPham.repaint();
    }

    private JPanel taoThePhuTungPOS(SanPham sp) {
        JPanel the = new JPanel(new BorderLayout(0, 4)) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            JPopupMenu mnu = new JPopupMenu();
                            JMenuItem itemXem = new JMenuItem("🔍 Xem chi tiết sản phẩm");
                            itemXem.setFont(new Font("Segoe UI", Font.BOLD, 12));
                            itemXem.addActionListener(ev -> {
                                Window cuaSoCha = SwingUtilities.getWindowAncestor(LeTanManHinhBanHang.this);
                                new QuanLyManHinhQuanLySanPhamXemChiTiet(cuaSoCha, sp, true, sanPhamBUS, danhMucBUS).setVisible(true);
                            });
                            mnu.add(itemXem);
                            mnu.show(e.getComponent(), e.getX(), e.getY());
                        } else {
                            themSanPhamVaoDonHienTai(sp);
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f);
                g2.setColor(hover ? new Color(255, 247, 237) : Color.WHITE);
                g2.fill(rect);
                g2.setColor(hover ? MAU_CAM_CHU_DAO : MAU_VIEN);
                g2.setStroke(new BasicStroke(hover ? 1.5f : 1f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        the.setPreferredSize(new Dimension(126, 162));
        the.setOpaque(false);
        the.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        the.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Hình ảnh sản phẩm
        JLabel lblAnh = new JLabel();
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setPreferredSize(new Dimension(114, 70));
        ImageIcon iconSP = IconUtil.layAnhSanPham(sp.getAnhSanPham(), 96, 64);
        if (iconSP != null) {
            lblAnh.setIcon(iconSP);
        }

        // Tên sản phẩm
        JLabel lblTen = new JLabel("<html><div style='text-align: center; width: 105px;'>"
                + sp.getTenSanPham() + "</div></html>");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTen.setForeground(MAU_CHU_CHINH);
        lblTen.setHorizontalAlignment(SwingConstants.CENTER);

        // Giá bán & Tồn kho
        JPanel pnlGia = new JPanel(new GridLayout(2, 1, 0, 1));
        pnlGia.setOpaque(false);

        JLabel lblGia = new JLabel(dinhDangTien.format(Math.round(sp.getDonGiaBan())) + " đ");
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGia.setForeground(MAU_CAM_CHU_DAO);
        lblGia.setHorizontalAlignment(SwingConstants.CENTER);

        int ton = sp.getSoLuongTon();
        JLabel lblTon = new JLabel("SL: " + ton + " " + (sp.getDonViTinh() != null ? sp.getDonViTinh() : ""));
        lblTon.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTon.setForeground(ton > 0 ? MAU_XANH_LA : MAU_DO);
        lblTon.setHorizontalAlignment(SwingConstants.CENTER);

        pnlGia.add(lblGia);
        pnlGia.add(lblTon);

        the.add(lblAnh, BorderLayout.NORTH);
        the.add(lblTen, BorderLayout.CENTER);
        the.add(pnlGia, BorderLayout.SOUTH);

        return the;
    }

    // =========================================================================
    // XÂY DỰNG PHÂN VÙNG PHẢI: THÔNG TIN THANH TOÁN
    // =========================================================================

    private JPanel taoPhanVungPhai() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN);
        the.setLayout(new BorderLayout(0, 10));
        the.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Tiêu đề: THÔNG TIN THANH TOÁN
        JLabel lblTieuDe = new JLabel("THÔNG TIN THANH TOÁN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        the.add(lblTieuDe, BorderLayout.NORTH);

        // Thân nội dung
        JPanel pnlThan = new JPanel();
        pnlThan.setLayout(new BoxLayout(pnlThan, BoxLayout.Y_AXIS));
        pnlThan.setOpaque(false);

        // 1. Khung Khách hàng
        pnlThan.add(taoKhungKhachHang());
        pnlThan.add(Box.createVerticalStrut(14));

        // 2. Bảng tính tiền & Khuyến mãi & Thuế
        pnlThan.add(taoKhungBangTinhTien());
        pnlThan.add(Box.createVerticalStrut(12));

        // 3. Tiền khách đưa & Tiền thừa
        pnlThan.add(taoKhungTienKhachDua());
        pnlThan.add(Box.createVerticalStrut(12));

        // 4. Phương thức thanh toán
        pnlThan.add(taoKhungPhuongThucThanhToan());
        pnlThan.add(Box.createVerticalStrut(12));

        // 5. Ghi chú đơn hàng
        pnlThan.add(taoKhungGhiChu());

        JScrollPane cuonThan = new JScrollPane(pnlThan);
        cuonThan.setBorder(null);
        cuonThan.getViewport().setBackground(Color.WHITE);
        the.add(cuonThan, BorderLayout.CENTER);

        // Chân trang: Cụm nút THANH TOÁN & IN HÓA ĐƠN + In tạm tính + Hủy đơn
        JPanel pnlFooter = taoFooterThanhToan();
        the.add(pnlFooter, BorderLayout.SOUTH);

        return the;
    }

    private JPanel taoKhungKhachHang() {
        TheBoGoc the = new TheBoGoc(10, new Color(248, 250, 252), MAU_VIEN);
        the.setLayout(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Hàng trên: Label KHÁCH HÀNG + Nút + Thêm mới
        JPanel hangTieuDe = new JPanel(new BorderLayout());
        hangTieuDe.setOpaque(false);

        JLabel lblNhan = new JLabel("KHÁCH HÀNG");
        lblNhan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNhan.setForeground(MAU_CHU_PHU);

        JButton btnThemMoiKH = new JButton("+ Thêm mới");
        btnThemMoiKH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemMoiKH.setForeground(MAU_CAM_CHU_DAO);
        btnThemMoiKH.setFocusPainted(false);
        btnThemMoiKH.setBorderPainted(false);
        btnThemMoiKH.setContentAreaFilled(false);
        btnThemMoiKH.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemMoiKH.addActionListener(e -> {
            String sdtHienTai = txtTimSDTKhach != null ? txtTimSDTKhach.getText().trim() : "";
            moHopThoaiThemKhachHangNhanhVoiSDT(sdtHienTai);
        });

        hangTieuDe.add(lblNhan, BorderLayout.WEST);
        hangTieuDe.add(btnThemMoiKH, BorderLayout.EAST);

        // Khối 2 dòng nhập liệu:
        // Dòng 1: Số điện thoại (thu nhỏ) + Nút kính lúp tra cứu
        JPanel khoiNhap = new JPanel();
        khoiNhap.setLayout(new BoxLayout(khoiNhap, BoxLayout.Y_AXIS));
        khoiNhap.setOpaque(false);

        JPanel hangNhapSDT = new JPanel(new BorderLayout(6, 0));
        hangNhapSDT.setOpaque(false);
        hangNhapSDT.setPreferredSize(new Dimension(0, 34));

        txtTimSDTKhach = new JTextField();
        txtTimSDTKhach.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTimSDTKhach.setForeground(MAU_CHU_CHINH);
        txtTimSDTKhach.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(4, 10, 4, 8)
        ));
        txtTimSDTKhach.putClientProperty("JTextField.placeholderText", "Nhập SĐT khách hàng...");
        txtTimSDTKhach.addActionListener(e -> xuLyTraCuuSDTKhachHang());

        btnTraCuuSDT = new JButton(IconUtil.layIcon("search.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
                g2.setColor(getModel().isRollover() ? new Color(30, 58, 95) : MAU_XANH_DAM);
                g2.fill(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnTraCuuSDT.setPreferredSize(new Dimension(38, 34));
        btnTraCuuSDT.setFocusPainted(false);
        btnTraCuuSDT.setBorderPainted(false);
        btnTraCuuSDT.setContentAreaFilled(false);
        btnTraCuuSDT.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTraCuuSDT.setToolTipText("Tra cứu thông tin khách hàng theo số điện thoại");
        btnTraCuuSDT.addActionListener(e -> xuLyTraCuuSDTKhachHang());

        hangNhapSDT.add(txtTimSDTKhach, BorderLayout.CENTER);
        hangNhapSDT.add(btnTraCuuSDT, BorderLayout.EAST);

        // Dòng 2: Hộp input phía dưới tự động điền tên khách hàng
        JPanel hangTenKH = new JPanel(new BorderLayout(6, 0));
        hangTenKH.setOpaque(false);
        hangTenKH.setPreferredSize(new Dimension(0, 34));
        hangTenKH.setBorder(new EmptyBorder(6, 0, 0, 0));

        txtTenKhachHang = new JTextField("Khách lẻ (Khách vãng lai)");
        txtTenKhachHang.setEditable(false);
        txtTenKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTenKhachHang.setForeground(MAU_CHU_PHU);
        txtTenKhachHang.setBackground(Color.WHITE);
        txtTenKhachHang.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(4, 10, 4, 8)
        ));

        btnHuyChonKhach = new JButton("✕");
        btnHuyChonKhach.setPreferredSize(new Dimension(30, 34));
        btnHuyChonKhach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHuyChonKhach.setForeground(MAU_DO);
        btnHuyChonKhach.setBackground(Color.WHITE);
        btnHuyChonKhach.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        btnHuyChonKhach.setFocusPainted(false);
        btnHuyChonKhach.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuyChonKhach.setToolTipText("Đặt lại thành Khách lẻ");
        btnHuyChonKhach.setVisible(false);
        btnHuyChonKhach.addActionListener(e -> xuLyHuyChonKhachHang());

        hangTenKH.add(txtTenKhachHang, BorderLayout.CENTER);
        hangTenKH.add(btnHuyChonKhach, BorderLayout.EAST);

        khoiNhap.add(hangNhapSDT);
        khoiNhap.add(hangTenKH);

        the.add(hangTieuDe, BorderLayout.NORTH);
        the.add(khoiNhap, BorderLayout.CENTER);
        return the;
    }

    private void hienThiTheKhachHangDaChon(KhachHang kh) {
        if (kh == null || kh.laKhachVangLai()) {
            if (txtTimSDTKhach != null) {
                txtTimSDTKhach.setText("");
            }
            if (txtTenKhachHang != null) {
                txtTenKhachHang.setText("Khách lẻ (Khách vãng lai)");
                txtTenKhachHang.setForeground(MAU_CHU_PHU);
            }
            if (btnHuyChonKhach != null) {
                btnHuyChonKhach.setVisible(false);
            }
        } else {
            if (txtTimSDTKhach != null) {
                txtTimSDTKhach.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
            }
            if (txtTenKhachHang != null) {
                txtTenKhachHang.setText(kh.getHoTen() + " (" + kh.getMaKhachHang() + ")");
                txtTenKhachHang.setForeground(MAU_XANH_DAM);
            }
            if (btnHuyChonKhach != null) {
                btnHuyChonKhach.setVisible(true);
            }
        }
    }

    private JPanel taoKhungBangTinhTien() {
        JPanel pnl = new JPanel(new GridLayout(4, 1, 0, 6));
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, MAU_VIEN),
                new EmptyBorder(10, 0, 10, 0)
        ));

        // 1. Tổng tiền hàng
        lblTongTienHang = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTongTienHang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongTienHang.setForeground(MAU_CHU_CHINH);
        pnl.add(taoDongThongSo("Tổng tiền hàng:", lblTongTienHang));

        // 2. Mã khuyến mãi + Giá trị giảm
        lblTienKhuyenMai = new JLabel("- 0 đ", SwingConstants.RIGHT);
        lblTienKhuyenMai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTienKhuyenMai.setForeground(MAU_DO);
        pnl.add(taoDongThongSo("Mã khuyến mãi:", lblTienKhuyenMai));

        // Dropdown chọn mã khuyến mãi
        cboMaKhuyenMai = new JComboBox<>();
        cboMaKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboMaKhuyenMai.setBackground(Color.WHITE);
        cboMaKhuyenMai.setPreferredSize(new Dimension(0, 32));
        cboMaKhuyenMai.setFocusable(false);
        cboMaKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MaKhuyenMai km) {
                    setText("[" + km.getMaKhuyenMai() + "] Giảm "
                            + (km.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM
                            ? Math.round(km.getGiaTriGiam()) + "%"
                            : dinhDangTien.format(Math.round(km.getGiaTriGiam())) + " đ")
                            + " (Đơn từ " + dinhDangTien.format(Math.round(km.getGiaTriToiThieuApDung())) + " đ)");
                } else {
                    setText("-- Không áp dụng --");
                }
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        cboMaKhuyenMai.addActionListener(e -> xuLyThayDoiKhuyenMai());
        pnl.add(cboMaKhuyenMai);

        // 3. Thuế VAT
        lblThueVAT = new JLabel("0 đ", SwingConstants.RIGHT);
        lblThueVAT.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblThueVAT.setForeground(MAU_CHU_CHINH);
        pnl.add(taoDongThongSo("Thuế VAT:", lblThueVAT));

        return pnl;
    }

    private JPanel taoDongThongSo(String nhan, JComponent compGiaTri) {
        JPanel hang = new JPanel(new BorderLayout());
        hang.setOpaque(false);

        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(MAU_CHU_PHU);

        hang.add(lbl, BorderLayout.WEST);
        hang.add(compGiaTri, BorderLayout.EAST);
        return hang;
    }

    private JPanel taoKhungTienKhachDua() {
        JPanel pnl = new JPanel(new GridLayout(3, 1, 0, 6));
        pnl.setOpaque(false);

        // 1. KHÁCH CẦN TRẢ (Chữ lớn màu cam nổi bật)
        lblKhachCanTra = new JLabel("0 đ", SwingConstants.RIGHT);
        lblKhachCanTra.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblKhachCanTra.setForeground(MAU_CAM_CHU_DAO);

        JPanel hangCanTra = new JPanel(new BorderLayout());
        hangCanTra.setOpaque(false);
        JLabel lblCanTraNhan = new JLabel("KHÁCH CẦN TRẢ:");
        lblCanTraNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCanTraNhan.setForeground(MAU_XANH_DAM);
        hangCanTra.add(lblCanTraNhan, BorderLayout.WEST);
        hangCanTra.add(lblKhachCanTra, BorderLayout.EAST);
        pnl.add(hangCanTra);

        // 2. Tiền khách đưa (VNĐ)
        JPanel hangKhachDua = new JPanel(new BorderLayout(6, 0));
        hangKhachDua.setOpaque(false);

        JLabel lblKhachDuaNhan = new JLabel("Tiền khách đưa (VNĐ):");
        lblKhachDuaNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKhachDuaNhan.setForeground(MAU_CHU_PHU);

        txtTienKhachDua = new JTextField("0");
        txtTienKhachDua.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTienKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtTienKhachDua.setPreferredSize(new Dimension(140, 32));
        txtTienKhachDua.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { capNhatTienThua(); }
            @Override public void removeUpdate(DocumentEvent e) { capNhatTienThua(); }
            @Override public void changedUpdate(DocumentEvent e) { capNhatTienThua(); }
        });

        hangKhachDua.add(lblKhachDuaNhan, BorderLayout.WEST);
        hangKhachDua.add(txtTienKhachDua, BorderLayout.EAST);
        pnl.add(hangKhachDua);

        // 3. Tiền thừa trả khách
        lblTienThuaTraKhach = new JLabel("0 đ", SwingConstants.RIGHT);
        lblTienThuaTraKhach.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTienThuaTraKhach.setForeground(MAU_XANH_LA);

        JPanel hangTienThua = new JPanel(new BorderLayout());
        hangTienThua.setOpaque(false);
        JLabel lblTienThuaNhan = new JLabel("Tiền thừa trả khách:");
        lblTienThuaNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTienThuaNhan.setForeground(MAU_CHU_PHU);
        hangTienThua.add(lblTienThuaNhan, BorderLayout.WEST);
        hangTienThua.add(lblTienThuaTraKhach, BorderLayout.EAST);
        pnl.add(hangTienThua);

        return pnl;
    }

    private JPanel taoKhungPhuongThucThanhToan() {
        JPanel pnl = new JPanel(new BorderLayout(0, 6));
        pnl.setOpaque(false);

        JLabel lblNhan = new JLabel("PHƯƠNG THỨC THANH TOÁN:");
        lblNhan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNhan.setForeground(MAU_CHU_PHU);

        JPanel hangRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        hangRadio.setOpaque(false);

        radTienMat = new JRadioButton("Tiền mặt", true);
        radTienMat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radTienMat.setForeground(MAU_CHU_CHINH);
        radTienMat.setOpaque(false);
        radTienMat.setFocusPainted(false);

        radChuyenKhoan = new JRadioButton("Chuyển khoản (QR)");
        radChuyenKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radChuyenKhoan.setForeground(MAU_CHU_CHINH);
        radChuyenKhoan.setOpaque(false);
        radChuyenKhoan.setFocusPainted(false);

        JLabel lblIconQR = new JLabel(IconUtil.layIcon("qr.png", 15, 15, MAU_CHU_PHU));

        ButtonGroup nhom = new ButtonGroup();
        nhom.add(radTienMat);
        nhom.add(radChuyenKhoan);

        radChuyenKhoan.addActionListener(e -> {
            // Khi chọn chuyển khoản thì tự động khớp tiền khách đưa = tiền cần trả
            DonBanHangTam don = layDonHienTai();
            if (don != null) {
                double canTra = don.tinhTongCanTra();
                txtTienKhachDua.setText(dinhDangTien.format(Math.round(canTra)));
            }
        });

        hangRadio.add(radTienMat);
        hangRadio.add(radChuyenKhoan);
        hangRadio.add(lblIconQR);

        pnl.add(lblNhan, BorderLayout.NORTH);
        pnl.add(hangRadio, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoKhungGhiChu() {
        JPanel pnl = new JPanel(new BorderLayout(0, 6));
        pnl.setOpaque(false);

        JLabel lblNhan = new JLabel("GHI CHÚ ĐƠN HÀNG:");
        lblNhan.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNhan.setForeground(MAU_CHU_PHU);

        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGhiChu.setForeground(MAU_CHU_CHINH);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));

        txtGhiChu.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { capNhatGhiChuDon(); }
            @Override public void removeUpdate(DocumentEvent e) { capNhatGhiChuDon(); }
            @Override public void changedUpdate(DocumentEvent e) { capNhatGhiChuDon(); }
        });

        pnl.add(lblNhan, BorderLayout.NORTH);
        pnl.add(txtGhiChu, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoFooterThanhToan() {
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Nút chính: THANH TOÁN & IN HÓA ĐƠN
        JButton btnThanhToan = new JButton("THANH TOÁN & IN HÓA ĐƠN", IconUtil.layIcon("invoice.png", 16, 16, Color.WHITE)) {
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
        btnThanhToan.setIconTextGap(8);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFocusPainted(false);
        btnThanhToan.setBorderPainted(false);
        btnThanhToan.setContentAreaFilled(false);
        btnThanhToan.setOpaque(false);
        btnThanhToan.setPreferredSize(new Dimension(0, 46));
        btnThanhToan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThanhToan.addActionListener(e -> xuLyThanhToanVaInHoaDon());

        // Hàng 2 nút: In tạm tính & Hủy đơn
        JPanel hangDuoi = new JPanel(new GridLayout(1, 2, 10, 0));
        hangDuoi.setOpaque(false);
        hangDuoi.setPreferredSize(new Dimension(0, 38));

        JButton btnInTamTinh = new JButton("In tạm tính", IconUtil.layIcon("refresh.png", 14, 14, MAU_CHU_CHINH));
        btnInTamTinh.setIconTextGap(6);
        btnInTamTinh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnInTamTinh.setForeground(MAU_CHU_CHINH);
        btnInTamTinh.setBackground(Color.WHITE);
        btnInTamTinh.setFocusPainted(false);
        btnInTamTinh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnInTamTinh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnInTamTinh.addActionListener(e -> xuLyInTamTinh());

        JButton btnHuyDon = new JButton("Hủy đơn", IconUtil.layIcon("trash.png", 14, 14, MAU_DO));
        btnHuyDon.setIconTextGap(6);
        btnHuyDon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHuyDon.setForeground(MAU_DO);
        btnHuyDon.setBackground(new Color(254, 242, 242));
        btnHuyDon.setFocusPainted(false);
        btnHuyDon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnHuyDon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuyDon.addActionListener(e -> xuLyHuyDonHienTai());

        hangDuoi.add(btnInTamTinh);
        hangDuoi.add(btnHuyDon);

        footer.add(btnThanhToan, BorderLayout.NORTH);
        footer.add(hangDuoi, BorderLayout.SOUTH);

        return footer;
    }

    // =========================================================================
    // XỬ LÝ NGHIỆP VỤ POS BÁN HÀNG
    // =========================================================================

    private DonBanHangTam layDonHienTai() {
        if (chiSoDonHienTai >= 0 && chiSoDonHienTai < danhSachDonTam.size()) {
            return danhSachDonTam.get(chiSoDonHienTai);
        }
        return null;
    }

    private void chuyenSangDonTam(int index) {
        if (index < 0 || index >= danhSachDonTam.size()) {
            return;
        }
        chiSoDonHienTai = index;
        capNhatGiaoDienTabs();
        napDuLieuDonLenGiaoDien();
    }

    private void xuLyThemTabMoi() {
        demThuTuTab++;
        DonBanHangTam donMoi = new DonBanHangTam("Hóa đơn " + demThuTuTab);
        danhSachDonTam.add(donMoi);
        chuyenSangDonTam(danhSachDonTam.size() - 1);
    }

    private void xuLyDongTab(int index) {
        if (danhSachDonTam.size() <= 1) {
            // Nếu chỉ còn 1 tab duy nhất, xóa sạch nội dung tab
            xuLyHuyDonHienTai();
            return;
        }

        DonBanHangTam don = danhSachDonTam.get(index);
        if (!don.getDanhSachHang().isEmpty()) {
            int luaChon = JOptionPane.showConfirmDialog(this,
                    "Đơn hàng [" + don.getTenDon() + "] đang có sản phẩm.\nBạn có chắc chắn muốn đóng và hủy bỏ đơn này?",
                    "Xác nhận đóng tab", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (luaChon != JOptionPane.YES_OPTION) {
                return;
            }
        }

        danhSachDonTam.remove(index);
        if (chiSoDonHienTai >= danhSachDonTam.size()) {
            chiSoDonHienTai = danhSachDonTam.size() - 1;
        }
        chuyenSangDonTam(chiSoDonHienTai);
    }

    private void napDuLieuDonLenGiaoDien() {
        DonBanHangTam don = layDonHienTai();
        if (don == null) return;

        dangCapNhatDuLieu = true;
        try {
            // 1. Cập nhật bảng giỏ hàng
            moHinhBangGioHang.setRowCount(0);
            List<DongGioHang> ds = don.getDanhSachHang();
            for (int i = 0; i < ds.size(); i++) {
                DongGioHang dong = ds.get(i);
                SanPham sp = dong.getSanPham();
                moHinhBangGioHang.addRow(new Object[]{
                        i + 1,
                        sp.getMaSanPham(),
                        sp.getTenSanPham(),
                        sp.getDonViTinh(),
                        dinhDangTien.format(Math.round(dong.getDonGia())) + " đ",
                        dong.getSoLuong(),
                        dinhDangTien.format(Math.round(dong.getThanhTien())) + " đ",
                        "✕"
                });
            }

            layoutVungGioHang.show(pnlVungGioHang, ds.isEmpty() ? "RONG" : "CO_HANG");

            // 2. Cập nhật thẻ khách hàng
            hienThiTheKhachHangDaChon(don.getKhachHang());

            // 3. Cập nhật mã khuyến mãi
            napDanhSachKhuyenMaiKhaDung(don);

            // 4. Cập nhật tiền khách đưa & ghi chú
            txtTienKhachDua.setText(dinhDangTien.format(Math.round(don.getTienKhachDua())));
            txtGhiChu.setText(don.getGhiChu() != null ? don.getGhiChu() : "");
            if (don.getPhuongThuc() == PhuongThucThanhToan.CHUYEN_KHOAN) {
                radChuyenKhoan.setSelected(true);
            } else {
                radTienMat.setSelected(true);
            }

            // 5. Cập nhật tổng thanh toán
            capNhatTongThanhToanHienThi();
        } finally {
            dangCapNhatDuLieu = false;
        }
    }

    private void themSanPhamVaoDonHienTai(SanPham sp) {
        if (sp == null) return;
        DonBanHangTam don = layDonHienTai();
        if (don == null) return;

        if (sp.getSoLuongTon() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm [" + sp.getTenSanPham() + "] đã hết hàng trong kho!",
                    "Hết hàng tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }

        don.themSanPham(sp, 1);
        napDuLieuDonLenGiaoDien();
    }

    private void xuLyThayDoiSoLuong(int dongBang) {
        DonBanHangTam don = layDonHienTai();
        if (don == null || dongBang < 0 || dongBang >= don.getDanhSachHang().size()) {
            return;
        }

        DongGioHang dong = don.getDanhSachHang().get(dongBang);
        Object giaTriMoi = moHinhBangGioHang.getValueAt(dongBang, 5);

        try {
            int soLuong = Integer.parseInt(giaTriMoi.toString().trim());
            int ton = dong.getSanPham().getSoLuongTon();

            if (soLuong <= 0) {
                int hoi = JOptionPane.showConfirmDialog(this,
                        "Bạn có muốn xóa sản phẩm [" + dong.getSanPham().getTenSanPham() + "] khỏi đơn hàng?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (hoi == JOptionPane.YES_OPTION) {
                    don.xoaDong(dongBang);
                } else {
                    dong.setSoLuong(1);
                }
            } else if (soLuong > ton) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng nhập (" + soLuong + ") vượt quá tồn kho hiện có (" + ton + ")!",
                        "Cảnh báo tồn kho", JOptionPane.WARNING_MESSAGE);
                dong.setSoLuong(ton);
            } else {
                dong.setSoLuong(soLuong);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ! Vui lòng nhập số nguyên dương.",
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }

        napDuLieuDonLenGiaoDien();
    }

    private void xuLyXoaKhoiGioHang(int dongBang) {
        DonBanHangTam don = layDonHienTai();
        if (don == null || dongBang < 0 || dongBang >= don.getDanhSachHang().size()) {
            return;
        }
        don.xoaDong(dongBang);
        napDuLieuDonLenGiaoDien();
    }

    private void xuLyTimKiemSanPhamLive() {
        String tuKhoa = txtTimKiemSP.getText();
        if (tuKhoa == null || PLACEHOLDER_TIM_SP.equals(tuKhoa) || tuKhoa.trim().isEmpty()) {
            popupGoiYSP.setVisible(false);
            return;
        }

        tuKhoa = tuKhoa.trim().toLowerCase(Locale.ROOT);
        popupGoiYSP.removeAll();

        List<SanPham> ds = sanPhamBUS.layTatCa();
        int dem = 0;
        for (SanPham sp : ds) {
            if (!sp.isTrangThai()) continue;
            String ma = sp.getMaSanPham() != null ? sp.getMaSanPham().toLowerCase(Locale.ROOT) : "";
            String ten = sp.getTenSanPham() != null ? sp.getTenSanPham().toLowerCase(Locale.ROOT) : "";

            if (ma.contains(tuKhoa) || ten.contains(tuKhoa)) {
                JMenuItem item = new JMenuItem(
                        String.format("[%s] %s  |  Giá: %s đ  |  Tồn: %d %s",
                                sp.getMaSanPham(), sp.getTenSanPham(),
                                dinhDangTien.format(Math.round(sp.getDonGiaBan())),
                                sp.getSoLuongTon(), sp.getDonViTinh() != null ? sp.getDonViTinh() : "")
                );
                item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                item.addActionListener(e -> {
                    themSanPhamVaoDonHienTai(sp);
                    txtTimKiemSP.setText("");
                    popupGoiYSP.setVisible(false);
                });
                popupGoiYSP.add(item);
                dem++;
                if (dem >= 8) break; // Giới hạn tối đa 8 gợi ý
            }
        }

        if (dem > 0) {
            popupGoiYSP.show(txtTimKiemSP, 0, txtTimKiemSP.getHeight());
            txtTimKiemSP.requestFocus();
        } else {
            popupGoiYSP.setVisible(false);
        }
    }

    private void xuLyChonSanPhamBangEnter() {
        String tuKhoa = txtTimKiemSP.getText();
        if (tuKhoa == null || PLACEHOLDER_TIM_SP.equals(tuKhoa) || tuKhoa.trim().isEmpty()) {
            return;
        }
        tuKhoa = tuKhoa.trim();

        // 1. Kiểm tra khớp chính xác mã sản phẩm
        SanPham spChinhXac = sanPhamBUS.timTheoMa(tuKhoa);
        if (spChinhXac != null) {
            themSanPhamVaoDonHienTai(spChinhXac);
            txtTimKiemSP.setText("");
            popupGoiYSP.setVisible(false);
            return;
        }

        // 2. Nếu khớp dòng gợi ý đầu tiên
        if (popupGoiYSP.getComponentCount() > 0) {
            Component c = popupGoiYSP.getComponent(0);
            if (c instanceof JMenuItem item) {
                item.doClick();
            }
        }
    }

    public void xuLyTangSoLuong(int dongBang) {
        DonBanHangTam don = layDonHienTai();
        if (don == null || dongBang < 0 || dongBang >= don.getDanhSachHang().size()) return;
        DongGioHang dong = don.getDanhSachHang().get(dongBang);
        int ton = dong.getSanPham().getSoLuongTon();
        if (dong.getSoLuong() + 1 > ton) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng yêu cầu vượt quá tồn kho hiện có (" + ton + " "
                            + (dong.getSanPham().getDonViTinh() != null ? dong.getSanPham().getDonViTinh() : "sản phẩm") + ")!",
                    "Cảnh báo tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }
        dong.setSoLuong(dong.getSoLuong() + 1);
        napDuLieuDonLenGiaoDien();
    }

    public void xuLyGiamSoLuong(int dongBang) {
        DonBanHangTam don = layDonHienTai();
        if (don == null || dongBang < 0 || dongBang >= don.getDanhSachHang().size()) return;
        DongGioHang dong = don.getDanhSachHang().get(dongBang);
        if (dong.getSoLuong() > 1) {
            dong.setSoLuong(dong.getSoLuong() - 1);
            napDuLieuDonLenGiaoDien();
        } else {
            int hoi = JOptionPane.showConfirmDialog(this,
                    "Số lượng về 0. Bạn có muốn xóa sản phẩm [" + dong.getSanPham().getTenSanPham() + "] khỏi đơn hàng?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (hoi == JOptionPane.YES_OPTION) {
                don.xoaDong(dongBang);
                napDuLieuDonLenGiaoDien();
            }
        }
    }

    private void xuLyTraCuuSDTKhachHang() {
        String sdt = txtTimSDTKhach != null ? txtTimSDTKhach.getText().trim() : "";
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số điện thoại khách hàng cần tra cứu!",
                    "Chưa nhập số điện thoại", JOptionPane.WARNING_MESSAGE);
            if (txtTimSDTKhach != null) {
                txtTimSDTKhach.requestFocus();
            }
            return;
        }

        try {
            KhachHang kh = khachHangBUS.timTheoSoDienThoai(sdt);
            DonBanHangTam don = layDonHienTai();

            if (kh != null && !kh.laKhachVangLai()) {
                if (don != null) {
                    don.setKhachHang(kh);
                }
                hienThiTheKhachHangDaChon(kh);
                napDanhSachKhuyenMaiKhaDung(don);
                capNhatTongThanhToanHienThi();

                JOptionPane.showMessageDialog(this,
                        "Đã tìm thấy khách hàng: " + kh.getHoTen()
                                + (kh.getDiaChi() != null && !kh.getDiaChi().isBlank() ? "\nĐịa chỉ: " + kh.getDiaChi() : ""),
                        "Tìm thấy khách hàng", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int chon = JOptionPane.showConfirmDialog(this,
                        "Không tìm thấy khách hàng với số điện thoại [" + sdt + "] trong hệ thống.\n"
                                + "Bạn có muốn thêm khách hàng mới ngay bây giờ không?",
                        "Khách hàng mới",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (chon == JOptionPane.YES_OPTION) {
                    moHopThoaiThemKhachHangNhanhVoiSDT(sdt);
                } else {
                    xuLyHuyChonKhachHang();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tra cứu khách hàng: " + ex.getMessage(),
                    "Lỗi tra cứu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyChonKhachHang() {
        DonBanHangTam don = layDonHienTai();
        if (don != null) {
            don.setKhachHang(null);
        }
        hienThiTheKhachHangDaChon(null);
        napDanhSachKhuyenMaiKhaDung(don);
        capNhatTongThanhToanHienThi();
    }

    private void moHopThoaiThemKhachHangNhanh() {
        String sdtHienTai = txtTimSDTKhach != null ? txtTimSDTKhach.getText().trim() : "";
        moHopThoaiThemKhachHangNhanhVoiSDT(sdtHienTai);
    }

    private void moHopThoaiThemKhachHangNhanhVoiSDT(String sdtMacDinh) {
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        ThemKhachHangNhanhDialog dialog = new ThemKhachHangNhanhDialog(cuaSoCha, sdtMacDinh, khachHangBUS);
        dialog.setVisible(true);

        KhachHang khMoi = dialog.getKhachHangVuaTao();
        if (khMoi != null) {
            DonBanHangTam don = layDonHienTai();
            if (don != null) {
                don.setKhachHang(khMoi);
            }
            hienThiTheKhachHangDaChon(khMoi);
            napDanhSachKhuyenMaiKhaDung(don);
            capNhatTongThanhToanHienThi();
        }
    }

    private void napDanhSachKhuyenMaiKhaDung(DonBanHangTam don) {
        double tongTien = don.tinhTongTienHang();
        List<MaKhuyenMai> dsKhaDung = maKhuyenMaiBUS.layDanhSachKhuyenMaiKhaDung(tongTien);

        DefaultComboBoxModel<MaKhuyenMai> model = new DefaultComboBoxModel<>();
        model.addElement(null); // -- Không áp dụng --

        for (MaKhuyenMai km : dsKhaDung) {
            model.addElement(km);
        }

        cboMaKhuyenMai.setModel(model);
        if (don.getMaKhuyenMai() != null) {
            cboMaKhuyenMai.setSelectedItem(don.getMaKhuyenMai());
        }
    }

    private void xuLyThayDoiKhuyenMai() {
        if (dangCapNhatDuLieu) return;
        DonBanHangTam don = layDonHienTai();
        if (don == null) return;

        MaKhuyenMai km = (MaKhuyenMai) cboMaKhuyenMai.getSelectedItem();
        don.setMaKhuyenMai(km);
        capNhatTongThanhToanHienThi();
    }

    private void capNhatGhiChuDon() {
        DonBanHangTam don = layDonHienTai();
        if (don != null) {
            don.setGhiChu(txtGhiChu.getText());
        }
    }

    private void capNhatTienThua() {
        DonBanHangTam don = layDonHienTai();
        if (don == null) return;

        double canTra = don.tinhTongCanTra();
        String strDua = txtTienKhachDua.getText().replace(".", "").replace(",", "").trim();
        double tienDua = 0.0;
        try {
            if (!strDua.isEmpty()) {
                tienDua = Double.parseDouble(strDua);
            }
        } catch (NumberFormatException ignored) {
        }

        don.setTienKhachDua(tienDua);

        double thua = tienDua - canTra;
        if (thua >= 0) {
            lblTienThuaTraKhach.setText(dinhDangTien.format(Math.round(thua)) + " đ");
            lblTienThuaTraKhach.setForeground(MAU_XANH_LA);
        } else {
            lblTienThuaTraKhach.setText("Còn thiếu: " + dinhDangTien.format(Math.round(Math.abs(thua))) + " đ");
            lblTienThuaTraKhach.setForeground(MAU_DO);
        }
    }

    private void capNhatTongThanhToanHienThi() {
        DonBanHangTam don = layDonHienTai();
        if (don == null) return;

        double tongHang = don.tinhTongTienHang();
        double giam = don.tinhTienKhuyenMai();
        double vat = don.tinhThueVAT();
        double canTra = don.tinhTongCanTra();

        lblTongTienHang.setText(dinhDangTien.format(Math.round(tongHang)) + " đ");
        lblTienKhuyenMai.setText((giam > 0 ? "- " : "") + dinhDangTien.format(Math.round(giam)) + " đ");
        lblThueVAT.setText(dinhDangTien.format(Math.round(vat)) + " đ");
        lblKhachCanTra.setText(dinhDangTien.format(Math.round(canTra)) + " đ");

        capNhatTienThua();
    }

    private void xuLyThanhToanVaInHoaDon() {
        DonBanHangTam don = layDonHienTai();
        if (don == null || don.getDanhSachHang().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng thêm ít nhất một sản phẩm vào đơn hàng trước khi thanh toán!",
                    "Giỏ hàng rỗng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double canTra = don.tinhTongCanTra();
        PhuongThucThanhToan phuongThuc = radChuyenKhoan.isSelected()
                ? PhuongThucThanhToan.CHUYEN_KHOAN
                : PhuongThucThanhToan.TIEN_MAT;

        // Nếu là tiền mặt thì kiểm tra tiền khách đưa
        if (phuongThuc == PhuongThucThanhToan.TIEN_MAT && don.getTienKhachDua() < canTra) {
            int hoi = JOptionPane.showConfirmDialog(this,
                    "Số tiền khách đưa (" + dinhDangTien.format(Math.round(don.getTienKhachDua()))
                            + " đ) chưa đủ thanh toán (" + dinhDangTien.format(Math.round(canTra)) + " đ).\n"
                            + "Bạn có muốn tiếp tục thanh toán không?",
                    "Cảnh báo tiền khách đưa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (hoi != JOptionPane.YES_OPTION) {
                txtTienKhachDua.requestFocus();
                return;
            }
        }

        // Lấy nhân viên lễ tân hiện tại
        NhanVien nvLeTan = layNhanVienLeTanHienTai();
        if (nvLeTan == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy thông tin nhân viên Lễ tân đang đăng nhập để lập hóa đơn!",
                    "Thiếu nhân viên", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Khách hàng (nếu không chọn thì lấy/tạo khách vãng lai)
        KhachHang kh = don.getKhachHang();
        if (kh == null) {
            kh = khachHangBUS.layHoacTaoKhachVangLai();
        }

        LocalDateTime now = LocalDateTime.now();
        String maHD = hoaDonBUS.phatSinhMaHoaDon(LoaiHoaDon.BAN_HANG, kh.getMaKhachHang(), now);

        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHD);
        hd.setLoaiHoaDon(LoaiHoaDon.BAN_HANG);
        hd.setThoiGianLap(now);
        hd.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
        hd.setPhuongThucThanhToan(phuongThuc);
        hd.setThoiGianThanhToan(now);
        hd.setGhiChu(don.getGhiChu());
        hd.setKhachHang(kh);
        hd.setNhanVien(nvLeTan);
        hd.setMaKhuyenMai(don.getMaKhuyenMai());
        hd.setPhieuTiepNhan(null);

        List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
        for (DongGioHang dong : don.getDanhSachHang()) {
            ChiTietHoaDon ct = new ChiTietHoaDon(
                    LoaiHangMuc.SAN_PHAM,
                    dong.getSoLuong(),
                    dong.getDonGia(),
                    hd,
                    null,
                    dong.getSanPham()
            );
            dsChiTiet.add(ct);
        }
        hd.setDanhSachChiTiet(dsChiTiet);

        try {
            if (hoaDonBUS.lapHoaDonBanHang(hd)) {
                JOptionPane.showMessageDialog(this,
                        "Thanh toán và lập hóa đơn bán hàng thành công!\nMã hóa đơn: " + hd.getMaHoaDon(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Mở cửa sổ xem và in hóa đơn chuyên nghiệp
                Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
                QuanLyManHinhQuanLyHoaDonXemChiTiet modalIn =
                        new QuanLyManHinhQuanLyHoaDonXemChiTiet(cuaSoCha, hd, true);
                modalIn.setVisible(true);

                // Làm mới lại tab đơn hàng hiện tại hoặc đóng tab
                if (danhSachDonTam.size() > 1) {
                    danhSachDonTam.remove(chiSoDonHienTai);
                    if (chiSoDonHienTai >= danhSachDonTam.size()) {
                        chiSoDonHienTai = danhSachDonTam.size() - 1;
                    }
                } else {
                    danhSachDonTam.set(0, new DonBanHangTam("Hóa đơn 1"));
                }
                chuyenSangDonTam(chiSoDonHienTai);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể lưu hóa đơn bán hàng. Vui lòng kiểm tra lại kết nối CSDL!",
                        "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lập hóa đơn: " + ex.getMessage(),
                    "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyInTamTinh() {
        DonBanHangTam don = layDonHienTai();
        if (don == null || don.getDanhSachHang().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không thể in phiếu tạm tính vì đơn hàng chưa có sản phẩm nào!",
                    "Chưa có sản phẩm", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhanVien nvLeTan = layNhanVienLeTanHienTai();
        KhachHang kh = don.getKhachHang() != null ? don.getKhachHang() : khachHangBUS.layHoacTaoKhachVangLai();

        LocalDateTime now = LocalDateTime.now();
        String maKhach = (kh != null && kh.getMaKhachHang() != null) ? kh.getMaKhachHang() : "KH000000000";
        String maHD = hoaDonBUS.phatSinhMaHoaDon(LoaiHoaDon.BAN_HANG, maKhach, now);

        HoaDon hdTam = new HoaDon();
        hdTam.setMaHoaDon(maHD);
        hdTam.setLoaiHoaDon(LoaiHoaDon.BAN_HANG);
        hdTam.setThoiGianLap(now);
        hdTam.setTrangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN);
        hdTam.setGhiChu(don.getGhiChu() != null && !don.getGhiChu().isBlank()
                ? don.getGhiChu() : "Đơn in tạm tính chờ thanh toán");
        hdTam.setKhachHang(kh);
        hdTam.setNhanVien(nvLeTan);
        hdTam.setMaKhuyenMai(don.getMaKhuyenMai());

        List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
        for (DongGioHang dong : don.getDanhSachHang()) {
            ChiTietHoaDon ct = new ChiTietHoaDon(
                    LoaiHangMuc.SAN_PHAM,
                    dong.getSoLuong(),
                    dong.getDonGia(),
                    hdTam,
                    null,
                    dong.getSanPham()
            );
            dsChiTiet.add(ct);
        }
        hdTam.setDanhSachChiTiet(dsChiTiet);

        try {
            boolean thanhCong = hoaDonBUS.lapHoaDonBanHang(hdTam);
            if (thanhCong) {
                Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
                QuanLyManHinhQuanLyHoaDonXemChiTiet modalIn =
                        new QuanLyManHinhQuanLyHoaDonXemChiTiet(cuaSoCha, hdTam, false);
                modalIn.setVisible(true);

                JOptionPane.showMessageDialog(this,
                        "Đã in tạm tính & lưu hóa đơn [" + hdTam.getMaHoaDon() + "] vào Danh sách Chờ Thu Tiền!\n"
                        + "Vui lòng chuyển qua màn hình 'Lập hóa đơn thanh toán' để hoàn tất thu tiền.",
                        "Lưu tạm tính thành công", JOptionPane.INFORMATION_MESSAGE);

                don.lamMoi();
                napDuLieuDonLenGiaoDien();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu đơn tạm tính: " + ex.getMessage(),
                    "Lỗi lưu tạm", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyDonHienTai() {
        DonBanHangTam don = layDonHienTai();
        if (don == null) return;

        if (don.getDanhSachHang().isEmpty() && don.getKhachHang() == null) {
            return;
        }

        int hoi = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy và làm mới đơn hàng [" + don.getTenDon() + "] không?",
                "Xác nhận hủy đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (hoi == JOptionPane.YES_OPTION) {
            don.lamMoi();
            napDuLieuDonLenGiaoDien();
        }
    }

    private NhanVien layNhanVienLeTanHienTai() {
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getMaNhanVien() != null) {
            return taiKhoanDangNhap.getMaNhanVien();
        }
        try {
            for (NhanVien nv : nhanVienDAO.layTatCa()) {
                if (nv.getChucVu() == ChucVu.LE_TAN && nv.isTrangThai()) {
                    return nv;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void caiDatPhimTat() {
        // Phím tắt F3: Tìm kiếm sản phẩm
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "TIM_SP");
        getActionMap().put("TIM_SP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiemSP.requestFocus();
                txtTimKiemSP.selectAll();
            }
        });
    }

    // =========================================================================
    // MODAL DIALOG TẠO NHANH HỒ SƠ KHÁCH HÀNG MỚI
    // =========================================================================

    private static class ThemKhachHangNhanhDialog extends JDialog {
        private final KhachHangBUS khachHangBUS;
        private KhachHang khachHangVuaTao = null;

        private JTextField txtMaKH;
        private JTextField txtTenKH;
        private JTextField txtSDT;
        private JComboBox<String> cboGioiTinh;
        private JTextField txtDiaChi;

        ThemKhachHangNhanhDialog(Window cuaSoCha, String sdtMacDinh, KhachHangBUS khachHangBUS) {
            super(cuaSoCha, "Tạo Nhanh Hồ Sơ Khách Hàng", ModalityType.APPLICATION_MODAL);
            this.khachHangBUS = khachHangBUS;

            setSize(480, 420);
            setLocationRelativeTo(cuaSoCha);
            setResizable(false);
            setLayout(new BorderLayout());

            // Header
            JPanel pnlHeader = new JPanel(new BorderLayout());
            pnlHeader.setBackground(MAU_XANH_DAM);
            pnlHeader.setBorder(new EmptyBorder(14, 20, 14, 20));

            JLabel lblTieuDe = new JLabel("THÊM MỚI KHÁCH HÀNG");
            lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblTieuDe.setForeground(Color.WHITE);
            pnlHeader.add(lblTieuDe, BorderLayout.CENTER);
            add(pnlHeader, BorderLayout.NORTH);

            // Form
            JPanel pnlForm = new JPanel(new GridBagLayout());
            pnlForm.setBackground(Color.WHITE);
            pnlForm.setBorder(new EmptyBorder(16, 20, 16, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.weightx = 1.0;

            String maKHMoi = khachHangBUS.phatSinhMaKhachHang();
            txtMaKH = taoOText(false);
            txtMaKH.setText(maKHMoi);
            txtMaKH.setBackground(new Color(241, 245, 249));

            txtTenKH = taoOText(true);
            txtSDT = taoOText(true);
            txtSDT.setText(sdtMacDinh != null ? sdtMacDinh : "");

            cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
            cboGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            cboGioiTinh.setBackground(Color.WHITE);
            cboGioiTinh.setPreferredSize(new Dimension(0, 36));

            txtDiaChi = taoOText(true);

            themTruong(pnlForm, gbc, 0, "Mã khách hàng:", txtMaKH);
            themTruong(pnlForm, gbc, 1, "Họ và tên (*):", txtTenKH);
            themTruong(pnlForm, gbc, 2, "Số điện thoại (*):", txtSDT);
            themTruong(pnlForm, gbc, 3, "Giới tính:", cboGioiTinh);
            themTruong(pnlForm, gbc, 4, "Địa chỉ liên hệ:", txtDiaChi);

            add(pnlForm, BorderLayout.CENTER);

            // Footer
            JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            pnlFooter.setBackground(new Color(248, 250, 252));
            pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

            JButton btnHuy = new JButton("Đóng");
            btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnHuy.setBackground(Color.WHITE);
            btnHuy.setFocusPainted(false);
            btnHuy.addActionListener(e -> dispose());

            JButton btnLuu = new JButton("Lưu Khách Hàng");
            btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnLuu.setForeground(Color.WHITE);
            btnLuu.setBackground(MAU_CAM_CHU_DAO);
            btnLuu.setFocusPainted(false);
            btnLuu.addActionListener(e -> xuLyLuuKhachHang());

            pnlFooter.add(btnHuy);
            pnlFooter.add(btnLuu);
            add(pnlFooter, BorderLayout.SOUTH);
        }

        private void themTruong(JPanel pnl, GridBagConstraints gbc, int y, String nhan, JComponent comp) {
            gbc.gridy = y;
            JPanel cot = new JPanel(new BorderLayout(0, 3));
            cot.setOpaque(false);
            JLabel lbl = new JLabel(nhan);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(MAU_CHU_CHINH);
            cot.add(lbl, BorderLayout.NORTH);
            cot.add(comp, BorderLayout.CENTER);
            pnl.add(cot, gbc);
        }

        private JTextField taoOText(boolean editable) {
            JTextField txt = new JTextField();
            txt.setEditable(editable);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setPreferredSize(new Dimension(0, 36));
            txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                    new EmptyBorder(4, 10, 4, 10)
            ));
            return txt;
        }

        private void xuLyLuuKhachHang() {
            String ten = txtTenKH.getText() != null ? txtTenKH.getText().trim() : "";
            String sdt = txtSDT.getText() != null ? txtSDT.getText().trim() : "";
            String dc = txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "";
            String gt = (String) cboGioiTinh.getSelectedItem();

            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ và tên khách hàng không được để trống!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtTenKH.requestFocus();
                return;
            }
            if (sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtSDT.requestFocus();
                return;
            }

            try {
                KhachHang kh = new KhachHang(txtMaKH.getText(), ten, sdt, dc, gt);
                if (khachHangBUS.themKhachHang(kh)) {
                    khachHangVuaTao = kh;
                    JOptionPane.showMessageDialog(this,
                            "Tạo thành công hồ sơ khách hàng [" + kh.getHoTen() + "]!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể lưu khách hàng vào cơ sở dữ liệu!",
                            "Lỗi lưu trữ", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        }

        public KhachHang getKhachHangVuaTao() {
            return khachHangVuaTao;
        }
    }

    // =========================================================================
    // CÁC LỚP MÔ HÌNH DỮ LIỆU ĐƠN BÁN HÀNG TẠM & GIỎ HÀNG
    // =========================================================================

    public static class DonBanHangTam {
        private final String tenDon;
        private final List<DongGioHang> danhSachHang = new ArrayList<>();
        private KhachHang khachHang = null;
        private MaKhuyenMai maKhuyenMai = null;
        private double tienKhachDua = 0.0;
        private PhuongThucThanhToan phuongThuc = PhuongThucThanhToan.TIEN_MAT;
        private String ghiChu = "";

        public DonBanHangTam(String tenDon) {
            this.tenDon = tenDon;
        }

        public String getTenDon() { return tenDon; }
        public List<DongGioHang> getDanhSachHang() { return danhSachHang; }
        public KhachHang getKhachHang() { return khachHang; }
        public void setKhachHang(KhachHang kh) { this.khachHang = kh; }
        public MaKhuyenMai getMaKhuyenMai() { return maKhuyenMai; }
        public void setMaKhuyenMai(MaKhuyenMai km) { this.maKhuyenMai = km; }
        public double getTienKhachDua() { return tienKhachDua; }
        public void setTienKhachDua(double tien) { this.tienKhachDua = tien; }
        public PhuongThucThanhToan getPhuongThuc() { return phuongThuc; }
        public void setPhuongThuc(PhuongThucThanhToan pt) { this.phuongThuc = pt; }
        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String gc) { this.ghiChu = gc; }

        public void themSanPham(SanPham sp, int slThem) {
            for (DongGioHang dong : danhSachHang) {
                if (dong.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                    int slMoi = Math.min(dong.getSanPham().getSoLuongTon(), dong.getSoLuong() + slThem);
                    dong.setSoLuong(slMoi);
                    return;
                }
            }
            danhSachHang.add(new DongGioHang(sp, slThem));
        }

        public void xoaDong(int index) {
            if (index >= 0 && index < danhSachHang.size()) {
                danhSachHang.remove(index);
            }
        }

        public void lamMoi() {
            danhSachHang.clear();
            khachHang = null;
            maKhuyenMai = null;
            tienKhachDua = 0.0;
            phuongThuc = PhuongThucThanhToan.TIEN_MAT;
            ghiChu = "";
        }

        public double tinhTongTienHang() {
            double tong = 0;
            for (DongGioHang d : danhSachHang) {
                tong += d.getThanhTien();
            }
            return tong;
        }

        public double tinhThueVAT() {
            double thue = 0;
            for (DongGioHang d : danhSachHang) {
                thue += d.getThueVAT();
            }
            return thue;
        }

        public double tinhTienKhuyenMai() {
            double tongHang = tinhTongTienHang();
            if (maKhuyenMai == null || !maKhuyenMai.isTrangThai()
                    || tongHang < maKhuyenMai.getGiaTriToiThieuApDung()) {
                return 0;
            }
            double giam = 0;
            if (maKhuyenMai.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
                giam = tongHang * (maKhuyenMai.getGiaTriGiam() / 100.0);
            } else if (maKhuyenMai.getLoaiGiam() == LoaiGiamGia.SO_TIEN) {
                giam = maKhuyenMai.getGiaTriGiam();
            }
            return Math.min(tongHang, Math.max(0, giam));
        }

        public double tinhTongCanTra() {
            return Math.max(0, tinhTongTienHang() + tinhThueVAT() - tinhTienKhuyenMai());
        }
    }

    public static class DongGioHang {
        private final SanPham sanPham;
        private int soLuong;

        public DongGioHang(SanPham sanPham, int soLuong) {
            this.sanPham = sanPham;
            this.soLuong = soLuong;
        }

        public SanPham getSanPham() { return sanPham; }
        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int sl) { this.soLuong = sl; }
        public double getDonGia() { return sanPham.getDonGiaBan(); }
        public double getThanhTien() { return soLuong * sanPham.getDonGiaBan(); }
        public double getThueVAT() {
            double vat = sanPham.getThueVAT();
            double tyLe = vat > 1 ? vat / 100.0 : vat;
            return getThanhTien() * tyLe;
        }
    }

    private record LuaChonDanhMuc(String maDanhMuc, String tenHienThi) {
        @Override
        public String toString() {
            return tenHienThi;
        }
    }

    // =========================================================================
    // CÁC THÀNH PHẦN GIAO DIỆN TUỲ BIẾN (RENDERER, CELL, THẺ BO GÓC)
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
            RoundRectangle2D hinh = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, gocBo, gocBo);
            g2.setColor(mauNen);
            g2.fill(hinh);
            g2.setColor(mauVien);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(hinh);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BoVeTieuDeBangPOS extends DefaultTableCellRenderer {
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
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(MAU_CHU_CHINH);

            if (column == 4 || column == 6) {
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setBorder(new EmptyBorder(0, 6, 0, 12));
            } else if (column == 0 || column == 3 || column == 5 || column == 7) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new EmptyBorder(0, 4, 0, 4));
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
            }
            return lbl;
        }
    }

    private static class BoVeOGioHang extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(MAU_CHU_CHINH);

            switch (column) {
                case 0 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(MAU_CHU_PHU);
                }
                case 1 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 8, 0, 6));
                }
                case 2 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_CHINH);
                    setBorder(new EmptyBorder(0, 8, 0, 6));
                }
                case 3 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(MAU_CHU_PHU);
                }
                case 4 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setForeground(MAU_CHU_CHINH);
                    setBorder(new EmptyBorder(0, 6, 0, 12));
                }
                case 6 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(MAU_CAM_CHU_DAO);
                    setBorder(new EmptyBorder(0, 6, 0, 12));
                }
            }
            return this;
        }
    }

    private static class BoVeSoLuongEditor extends JPanel implements TableCellRenderer {
        private final JButton btnGiam = new JButton("-");
        private final JLabel lblSoLuong = new JLabel("1", SwingConstants.CENTER);
        private final JButton btnTang = new JButton("+");

        BoVeSoLuongEditor() {
            setLayout(new BorderLayout(3, 0));
            setOpaque(true);
            setBorder(new EmptyBorder(5, 8, 5, 8));

            btnGiam.setPreferredSize(new Dimension(28, 26));
            btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnGiam.setForeground(MAU_CHU_PHU);
            btnGiam.setBackground(new Color(241, 245, 249));
            btnGiam.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
            btnGiam.setFocusPainted(false);
            btnGiam.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            btnTang.setPreferredSize(new Dimension(28, 26));
            btnTang.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnTang.setForeground(MAU_CHU_PHU);
            btnTang.setBackground(new Color(241, 245, 249));
            btnTang.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
            btnTang.setFocusPainted(false);
            btnTang.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            lblSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblSoLuong.setForeground(new Color(15, 41, 66));

            add(btnGiam, BorderLayout.WEST);
            add(lblSoLuong, BorderLayout.CENTER);
            add(btnTang, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            lblSoLuong.setText(value != null ? value.toString() : "1");
            return this;
        }
    }

    private static class BoVeNutXoa extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = new JLabel(IconUtil.layIcon("trash.png", 15, 15, MAU_DO), SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lbl.setToolTipText("Xóa sản phẩm khỏi giỏ hàng");
            return lbl;
        }
    }

    private static class NutTabNganKeo extends JButton {
        private boolean dangChon = false;
        private final String tenIcon;

        NutTabNganKeo(String text, String tenIcon) {
            super(text);
            this.tenIcon = tenIcon;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setIconTextGap(8);
            setBorder(new EmptyBorder(0, 16, 0, 16));
            capNhatGiaoDien();
        }

        NutTabNganKeo(String text, Icon icon) {
            this(text, (String) null);
            setIcon(icon);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(Math.max(d.width + 12, 115), 34);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        void setDangChon(boolean val) {
            this.dangChon = val;
            capNhatGiaoDien();
            repaint();
        }

        boolean isDangChon() {
            return dangChon;
        }

        private void capNhatGiaoDien() {
            setForeground(dangChon ? Color.WHITE : MAU_CHU_PHU);
            if (tenIcon != null) {
                setIcon(IconUtil.layIcon(tenIcon, 14, 14, dangChon ? Color.WHITE : MAU_CHU_PHU));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D rect = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
            if (dangChon) {
                g2.setColor(MAU_CAM_CHU_DAO);
                g2.fill(rect);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(241, 245, 249));
                g2.fill(rect);
                g2.setColor(MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
            } else {
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                g2.setColor(MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LeTanManHinhChinh khungChinh = new LeTanManHinhChinh();
            khungChinh.chuyenManHinh(LeTanManHinhChinh.MAN_TAO_DON_BAN_HANG);
            khungChinh.setVisible(true);
        });
    }
}
