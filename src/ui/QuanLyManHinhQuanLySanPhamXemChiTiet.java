package ui;

import bus.DanhMucSanPhamBUS;
import bus.SanPhamBUS;
import entity.DanhMucSanPham;
import entity.SanPham;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Modal hiển thị chi tiết và cho phép cập nhật thông tin sản phẩm / phụ tùng
 * Giao diện hiện đại, kích thước nhỏ gọn hơn màn hình chính, bố cục thẻ trực quan,
 * tích hợp tính toán lợi nhuận, cảnh báo tồn kho và chức năng nhập kho nhanh.
 */
public class QuanLyManHinhQuanLySanPhamXemChiTiet extends JDialog {

    // Bảng màu thiết kế đồng bộ với hệ thống Pengu Garage
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
    private static final Color MAU_VANG_CANH_BAO = new Color(217, 119, 6);

    private final SanPham sanPham;
    private final boolean chiXem;
    private final SanPhamBUS sanPhamBUS;
    private final DanhMucSanPhamBUS danhMucBUS;
    private final DecimalFormat dinhDangTien;

    private boolean daCapNhat = false;

    // Các thành phần giao diện nhập liệu
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JComboBox<DanhMucSanPham> cboDanhMuc;
    private JComboBox<String> cboDonViTinh;
    private JComboBox<String> cboTrangThai;

    private JSpinner spnGiaNhap;
    private JSpinner spnDonGiaBan;
    private JSpinner spnThueVAT;

    private JSpinner spnSoLuongTon;
    private JSpinner spnTonToiThieu;

    private JLabel lblChenhLechLoiNhuan;
    private JLabel lblTySuatLoiNhuan;
    private JLabel lblGiaSauThue;
    private JLabel lblCanhBaoTonKho;
    private JLabel lblTrangThaiHeader;
    private JLabel lblAnhMinhHoa;
    private JButton btnChonAnh;
    private String duongDanAnhHienTai;

    public QuanLyManHinhQuanLySanPhamXemChiTiet(Window cuaSoCha, SanPham sanPham) {
        this(cuaSoCha, sanPham, false, new SanPhamBUS(), new DanhMucSanPhamBUS());
    }

    public QuanLyManHinhQuanLySanPhamXemChiTiet(Window cuaSoCha, SanPham sanPham, boolean chiXem) {
        this(cuaSoCha, sanPham, chiXem, new SanPhamBUS(), new DanhMucSanPhamBUS());
    }

    public QuanLyManHinhQuanLySanPhamXemChiTiet(Window cuaSoCha, SanPham sanPham,
                                                SanPhamBUS sanPhamBUS, DanhMucSanPhamBUS danhMucBUS) {
        this(cuaSoCha, sanPham, false, sanPhamBUS, danhMucBUS);
    }

    public QuanLyManHinhQuanLySanPhamXemChiTiet(Window cuaSoCha, SanPham sanPham, boolean chiXem,
                                                SanPhamBUS sanPhamBUS, DanhMucSanPhamBUS danhMucBUS) {
        super(cuaSoCha, (chiXem ? "Chi Tiết Sản Phẩm & Phụ Tùng - " : "Chi Tiết & Cập Nhật Sản Phẩm - ")
                + (sanPham != null ? sanPham.getMaSanPham() : ""), ModalityType.APPLICATION_MODAL);
        this.sanPham = sanPham;
        this.chiXem = chiXem;
        this.sanPhamBUS = sanPhamBUS != null ? sanPhamBUS : new SanPhamBUS();
        this.danhMucBUS = danhMucBUS != null ? danhMucBUS : new DanhMucSanPhamBUS();

        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        kyHieu.setGroupingSeparator('.');
        kyHieu.setDecimalSeparator(',');
        this.dinhDangTien = new DecimalFormat("#,##0", kyHieu);

        khoiTaoGiaoDien();
        napDuLieuVaoForm();
    }

    public boolean isDaCapNhat() {
        return daCapNhat;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    private void khoiTaoGiaoDien() {
        // Tăng kích thước modal rộng rãi (1060 x 840) để xem trọn vẹn thông tin sản phẩm, giá bán, tồn kho mà không cần cuộn
        setSize(1060, 840);
        setMinimumSize(new Dimension(940, 740));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 1. Header sắc xanh navy thương hiệu
        JPanel header = taoHeaderModal();
        add(header, BorderLayout.NORTH);

        // 2. Thân nội dung chính dạng thẻ cuộn
        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setBackground(MAU_NEN_FORM);
        noiDung.setBorder(new EmptyBorder(16, 20, 16, 20));

        noiDung.add(taoTheThongTinCoBan());
        noiDung.add(Box.createVerticalStrut(14));
        noiDung.add(taoTheGiaVaThue());
        noiDung.add(Box.createVerticalStrut(14));
        noiDung.add(taoTheTonKhoVaCanhBao());

        JScrollPane cuonNoiDung = new JScrollPane(noiDung);
        cuonNoiDung.setBorder(null);
        cuonNoiDung.getVerticalScrollBar().setUnitIncrement(14);
        cuonNoiDung.getViewport().setBackground(MAU_NEN_FORM);
        add(cuonNoiDung, BorderLayout.CENTER);

        // 3. Footer các nút thao tác
        JPanel footer = taoFooterModal();
        add(footer, BorderLayout.SOUTH);
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
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        // Cụm trái: Tiêu đề + Mã sản phẩm
        JPanel cumTrai = new JPanel();
        cumTrai.setOpaque(false);
        cumTrai.setLayout(new BoxLayout(cumTrai, BoxLayout.Y_AXIS));

        JLabel lblTieuDe = new JLabel(chiXem ? "CHI TIẾT SẢN PHẨM & PHỤ TÙNG" : "CHI TIẾT & CẬP NHẬT SẢN PHẨM");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTieuDe.setForeground(Color.WHITE);

        String maHienThi = sanPham != null && sanPham.getMaSanPham() != null
                ? sanPham.getMaSanPham()
                : "Mã SP chưa xác định";
        String tenDMDangChon = sanPham != null && sanPham.getDanhMucSanPham() != null
                ? sanPham.getDanhMucSanPham().getTenDanhMuc()
                : "Phụ tùng tổng hợp";

        JLabel lblPhu = new JLabel("Mã: " + maHienThi + "  |  Nhóm: " + tenDMDangChon);
        lblPhu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhu.setForeground(new Color(203, 213, 225));
        lblPhu.setBorder(new EmptyBorder(3, 0, 0, 0));

        cumTrai.add(lblTieuDe);
        cumTrai.add(lblPhu);

        // Cụm phải: Huy hiệu trạng thái kinh doanh
        lblTrangThaiHeader = new JLabel();
        lblTrangThaiHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTrangThaiHeader.setOpaque(true);
        lblTrangThaiHeader.setBorder(new EmptyBorder(6, 14, 6, 14));
        capNhatBadgeTrangThaiHeader(sanPham != null && sanPham.isTrangThai());

        header.add(cumTrai, BorderLayout.WEST);
        header.add(lblTrangThaiHeader, BorderLayout.EAST);
        return header;
    }

    private void capNhatBadgeTrangThaiHeader(boolean dangBan) {
        if (dangBan) {
            lblTrangThaiHeader.setText("• ĐANG KINH DOANH");
            lblTrangThaiHeader.setBackground(new Color(220, 252, 231));
            lblTrangThaiHeader.setForeground(new Color(22, 101, 52));
        } else {
            lblTrangThaiHeader.setText("• NGỪNG KINH DOANH");
            lblTrangThaiHeader.setBackground(new Color(241, 245, 249));
            lblTrangThaiHeader.setForeground(new Color(100, 116, 139));
        }
    }

    /**
     * Thẻ 1: Thông tin cơ bản và phân loại sản phẩm (tích hợp khung xem & đổi ảnh sản phẩm)
     */
    private JPanel taoTheThongTinCoBan() {
        JPanel the = taoTheKhungBoGoc("1. THÔNG TIN CƠ BẢN & PHÂN LOẠI");
        JPanel noiDung = new JPanel(new BorderLayout(18, 0));
        noiDung.setOpaque(false);

        // --- Cột trái: Khung xem & chọn ảnh minh họa sản phẩm ---
        JPanel pnlKhungAnh = new JPanel(new BorderLayout(0, 8));
        pnlKhungAnh.setOpaque(false);
        pnlKhungAnh.setPreferredSize(new Dimension(190, 190));

        lblAnhMinhHoa = new JLabel();
        lblAnhMinhHoa.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhMinhHoa.setVerticalAlignment(SwingConstants.CENTER);
        lblAnhMinhHoa.setBackground(new Color(248, 250, 252));
        lblAnhMinhHoa.setOpaque(true);
        lblAnhMinhHoa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));
        pnlKhungAnh.add(lblAnhMinhHoa, BorderLayout.CENTER);

        if (!chiXem) {
            btnChonAnh = new JButton("Chọn ảnh mới...", IconUtil.layIcon("part.png", 14, 14, new Color(30, 58, 138)));
            btnChonAnh.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnChonAnh.setForeground(new Color(30, 58, 138));
            btnChonAnh.setBackground(new Color(239, 246, 255));
            btnChonAnh.setFocusPainted(false);
            btnChonAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnChonAnh.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));
            btnChonAnh.addActionListener(e -> xuLyChonAnhMoi());
            pnlKhungAnh.add(btnChonAnh, BorderLayout.SOUTH);
        }

        // --- Cột phải: Các trường thông tin cơ bản ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Mã sản phẩm (Chỉ đọc)
        txtMaSP = taoOVanBan(false);
        txtMaSP.setBackground(new Color(241, 245, 249));
        txtMaSP.setEditable(false);
        txtMaSP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtMaSP.setForeground(MAU_XANH_DAM);

        // Danh mục sản phẩm
        cboDanhMuc = new JComboBox<>();
        cboDanhMuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDanhMuc.setBackground(Color.WHITE);
        cboDanhMuc.setPreferredSize(new Dimension(0, 36));
        cboDanhMuc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DanhMucSanPham dm) {
                    setText("[" + dm.getMaDanhMuc() + "] " + dm.getTenDanhMuc());
                }
                setBorder(new EmptyBorder(5, 8, 5, 8));
                return this;
            }
        });
        napDanhMucVaoCombo();

        // Tên sản phẩm
        txtTenSP = taoOVanBan(true);
        txtTenSP.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Đơn vị tính
        cboDonViTinh = new JComboBox<>(new String[]{
                "Cái", "Bộ", "Bình", "Lít", "Hộp", "Chai", "Cặp", "Lon", "Cuộn", "Mét", "Kg", "Chiếc"
        });
        cboDonViTinh.setEditable(true);
        cboDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDonViTinh.setBackground(Color.WHITE);
        cboDonViTinh.setPreferredSize(new Dimension(0, 36));

        // Trạng thái kinh doanh
        cboTrangThai = new JComboBox<>(new String[]{"Đang bán", "Ngừng bán"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTrangThai.setBackground(Color.WHITE);
        cboTrangThai.setPreferredSize(new Dimension(0, 36));
        cboTrangThai.addActionListener(e -> {
            boolean dangBan = cboTrangThai.getSelectedIndex() == 0;
            capNhatBadgeTrangThaiHeader(dangBan);
        });

        // Bố cục lưới 2 cột
        themTruongForm(pnlForm, gbc, 0, 0, "Mã sản phẩm:", txtMaSP);
        themTruongForm(pnlForm, gbc, 1, 0, "Danh mục (*):", cboDanhMuc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JPanel pnlTen = new JPanel(new BorderLayout(0, 4));
        pnlTen.setOpaque(false);
        JLabel lblTen = new JLabel("Tên sản phẩm & phụ tùng (*):");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTen.setForeground(MAU_CHU_CHINH);
        pnlTen.add(lblTen, BorderLayout.NORTH);
        pnlTen.add(txtTenSP, BorderLayout.CENTER);
        pnlForm.add(pnlTen, gbc);

        gbc.gridwidth = 1;
        themTruongForm(pnlForm, gbc, 0, 2, "Đơn vị tính (ĐVT) (*):", cboDonViTinh);
        themTruongForm(pnlForm, gbc, 1, 2, "Trạng thái kinh doanh:", cboTrangThai);

        noiDung.add(pnlKhungAnh, BorderLayout.WEST);
        noiDung.add(pnlForm, BorderLayout.CENTER);

        the.add(noiDung, BorderLayout.CENTER);
        return the;
    }

    /**
     * Thẻ 2: Thiết lập giá nhập, đơn giá bán và thuế VAT kèm tính toán tỷ suất lợi nhuận
     */
    private JPanel taoTheGiaVaThue() {
        JPanel the = taoTheKhungBoGoc("2. THIẾT LẬP GIÁ BÁN, GIÁ NHẬP & THUẾ VAT");
        JPanel noiDung = new JPanel(new BorderLayout(14, 10));
        noiDung.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        spnGiaNhap = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 2_000_000_000.0, 10_000.0));
        spnDonGiaBan = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 2_000_000_000.0, 10_000.0));
        spnThueVAT = new JSpinner(new SpinnerNumberModel(8.0, 0.0, 100.0, 1.0));

        dinhDangSpinnerTien(spnGiaNhap);
        dinhDangSpinnerTien(spnDonGiaBan);
        dinhDangSpinnerPhanTram(spnThueVAT);

        ChangeListener langNgheGia = (ChangeEvent e) -> capNhatTinhToanLoiNhuanVaGia();
        spnGiaNhap.addChangeListener(langNgheGia);
        spnDonGiaBan.addChangeListener(langNgheGia);
        spnThueVAT.addChangeListener(langNgheGia);

        themTruongForm(pnlInput, gbc, 0, 0, "Giá vốn nhập kho (VNĐ):", spnGiaNhap);
        themTruongForm(pnlInput, gbc, 1, 0, "Đơn giá bán niêm yết (VNĐ) (*):", spnDonGiaBan);
        themTruongForm(pnlInput, gbc, 2, 0, "Thuế VAT (%):", spnThueVAT);

        // Khung hiển thị tự động phân tích tỷ suất sinh lời
        JPanel pnlThongKeGia = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        pnlThongKeGia.setBackground(new Color(241, 245, 249));
        pnlThongKeGia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        lblChenhLechLoiNhuan = new JLabel("Lợi nhuận gộp/sp: 0 đ");
        lblChenhLechLoiNhuan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblChenhLechLoiNhuan.setForeground(MAU_CHU_CHINH);

        lblTySuatLoiNhuan = new JLabel("Markup: 0.0%");
        lblTySuatLoiNhuan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTySuatLoiNhuan.setForeground(MAU_XANH_LA);

        lblGiaSauThue = new JLabel("Đơn giá gồm VAT: 0 đ");
        lblGiaSauThue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGiaSauThue.setForeground(MAU_XANH_DAM);

        pnlThongKeGia.add(lblChenhLechLoiNhuan);
        pnlThongKeGia.add(new JLabel("•"));
        pnlThongKeGia.add(lblTySuatLoiNhuan);
        pnlThongKeGia.add(new JLabel("•"));
        pnlThongKeGia.add(lblGiaSauThue);

        noiDung.add(pnlInput, BorderLayout.NORTH);
        noiDung.add(pnlThongKeGia, BorderLayout.CENTER);

        the.add(noiDung, BorderLayout.CENTER);
        return the;
    }

    /**
     * Thẻ 3: Quản lý số lượng tồn kho, ngưỡng cảnh báo và nhập kho nhanh
     */
    private JPanel taoTheTonKhoVaCanhBao() {
        JPanel the = taoTheKhungBoGoc("3. TỒN KHO & CẢNH BÁO AN TOÀN");
        JPanel noiDung = new JPanel(new BorderLayout(14, 10));
        noiDung.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        spnSoLuongTon = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        spnTonToiThieu = new JSpinner(new SpinnerNumberModel(5, 0, 1_000_000, 1));

        dinhDangSpinnerSoLuong(spnSoLuongTon);
        dinhDangSpinnerSoLuong(spnTonToiThieu);

        ChangeListener langNgheTon = (ChangeEvent e) -> capNhatCanhBaoTonKho();
        spnSoLuongTon.addChangeListener(langNgheTon);
        spnTonToiThieu.addChangeListener(langNgheTon);

        themTruongForm(pnlInput, gbc, 0, 0, "Số lượng tồn hiện tại:", spnSoLuongTon);
        themTruongForm(pnlInput, gbc, 1, 0, "Ngưỡng tồn an toàn tối thiểu:", spnTonToiThieu);

        // Khung cảnh báo và nút nhập kho nhanh
        JPanel pnlCanhBaoVaNut = new JPanel(new BorderLayout(10, 0));
        pnlCanhBaoVaNut.setOpaque(false);
        pnlCanhBaoVaNut.setBorder(new EmptyBorder(4, 6, 4, 6));

        lblCanhBaoTonKho = new JLabel();
        lblCanhBaoTonKho.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCanhBaoTonKho.setOpaque(true);
        lblCanhBaoTonKho.setBorder(new EmptyBorder(8, 14, 8, 14));

        pnlCanhBaoVaNut.add(lblCanhBaoTonKho, BorderLayout.CENTER);

        if (!chiXem) {
            JButton btnNhapThemNhanh = new JButton("+ Nhập Hàng Bổ Sung...");
            btnNhapThemNhanh.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnNhapThemNhanh.setFocusPainted(false);
            btnNhapThemNhanh.setBackground(new Color(239, 246, 255));
            btnNhapThemNhanh.setForeground(new Color(30, 58, 138));
            btnNhapThemNhanh.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                    new EmptyBorder(8, 14, 8, 14)
            ));
            btnNhapThemNhanh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnNhapThemNhanh.addActionListener(e -> xuLyNhapThemHangNhanh());
            pnlCanhBaoVaNut.add(btnNhapThemNhanh, BorderLayout.EAST);
        }

        noiDung.add(pnlInput, BorderLayout.NORTH);
        noiDung.add(pnlCanhBaoVaNut, BorderLayout.CENTER);

        the.add(noiDung, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoFooterModal() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

        // Nút Đóng
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDong.setFocusPainted(false);
        btnDong.setBackground(Color.WHITE);
        btnDong.setForeground(MAU_CHU_CHINH);
        btnDong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.addActionListener(e -> dispose());

        if (chiXem) {
            footer.add(btnDong);
            return footer;
        }

        // Nút Khôi phục dữ liệu ban đầu
        JButton btnKhoiPhuc = new JButton("Khôi phục ban đầu");
        btnKhoiPhuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnKhoiPhuc.setFocusPainted(false);
        btnKhoiPhuc.setBackground(Color.WHITE);
        btnKhoiPhuc.setForeground(MAU_CHU_PHU);
        btnKhoiPhuc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnKhoiPhuc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKhoiPhuc.addActionListener(e -> napDuLieuVaoForm());

        // Nút Lưu cập nhật (Chủ đạo màu cam Pengu)
        JButton btnLuu = new JButton("Lưu Thay Đổi", IconUtil.layIcon("save.png", 14, 14, Color.WHITE)) {
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

        footer.add(btnKhoiPhuc);
        footer.add(btnDong);
        footer.add(btnLuu);
        return footer;
    }

    private void napDanhMucVaoCombo() {
        DefaultComboBoxModel<DanhMucSanPham> model = new DefaultComboBoxModel<>();
        try {
            List<DanhMucSanPham> ds = danhMucBUS.layTatCa();
            for (DanhMucSanPham dm : ds) {
                model.addElement(dm);
            }
        } catch (Exception ignored) {
        }
        cboDanhMuc.setModel(model);
    }

    private void napDuLieuVaoForm() {
        if (sanPham == null) {
            return;
        }

        txtMaSP.setText(sanPham.getMaSanPham() != null ? sanPham.getMaSanPham() : "");
        txtTenSP.setText(sanPham.getTenSanPham() != null ? sanPham.getTenSanPham() : "");

        duongDanAnhHienTai = sanPham.getAnhSanPham();
        capNhatAnhHienThi(duongDanAnhHienTai);

        if (sanPham.getDanhMucSanPham() != null) {
            for (int i = 0; i < cboDanhMuc.getItemCount(); i++) {
                DanhMucSanPham dm = cboDanhMuc.getItemAt(i);
                if (dm != null && dm.getMaDanhMuc() != null
                        && dm.getMaDanhMuc().equals(sanPham.getDanhMucSanPham().getMaDanhMuc())) {
                    cboDanhMuc.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Khóa chuyển danh mục nếu mã sản phẩm đã được sinh theo cấu trúc SP-{maDanhMuc}-{xxx}
        cboDanhMuc.setEnabled(false);

        if (sanPham.getDonViTinh() != null) {
            cboDonViTinh.setSelectedItem(sanPham.getDonViTinh());
        }

        cboTrangThai.setSelectedIndex(sanPham.isTrangThai() ? 0 : 1);
        capNhatBadgeTrangThaiHeader(sanPham.isTrangThai());

        spnGiaNhap.setValue(sanPham.getGiaNhap());
        spnDonGiaBan.setValue(sanPham.getDonGiaBan());
        spnThueVAT.setValue(sanPham.getThueVAT());

        spnSoLuongTon.setValue(sanPham.getSoLuongTon());
        spnTonToiThieu.setValue(sanPham.getSoLuongTonToiThieu());

        if (chiXem) {
            txtTenSP.setEditable(false);
            txtTenSP.setBackground(new Color(241, 245, 249));
            cboDonViTinh.setEnabled(false);
            cboTrangThai.setEnabled(false);
            spnGiaNhap.setEnabled(false);
            spnDonGiaBan.setEnabled(false);
            spnThueVAT.setEnabled(false);
            spnSoLuongTon.setEnabled(false);
            spnTonToiThieu.setEnabled(false);
        }

        capNhatTinhToanLoiNhuanVaGia();
        capNhatCanhBaoTonKho();
    }

    private void capNhatTinhToanLoiNhuanVaGia() {
        double giaNhap = ((Number) spnGiaNhap.getValue()).doubleValue();
        double donGiaBan = ((Number) spnDonGiaBan.getValue()).doubleValue();
        double vat = ((Number) spnThueVAT.getValue()).doubleValue();

        double chenhLech = donGiaBan - giaNhap;
        lblChenhLechLoiNhuan.setText("Lợi nhuận gộp/sp: "
                + (chenhLech >= 0 ? "+" : "") + dinhDangTien.format(Math.round(chenhLech)) + " đ");
        lblChenhLechLoiNhuan.setForeground(chenhLech >= 0 ? new Color(22, 101, 52) : MAU_DO_CANH_BAO);

        double markup = giaNhap > 0 ? (chenhLech / giaNhap) * 100.0 : 0.0;
        lblTySuatLoiNhuan.setText(String.format(Locale.US, "Markup: %+.1f%%", markup));
        lblTySuatLoiNhuan.setForeground(markup >= 0 ? new Color(22, 101, 52) : MAU_DO_CANH_BAO);

        double sauThue = donGiaBan * (1.0 + vat / 100.0);
        lblGiaSauThue.setText("Đơn giá gồm VAT: " + dinhDangTien.format(Math.round(sauThue)) + " đ");
    }

    private void capNhatCanhBaoTonKho() {
        int ton = ((Number) spnSoLuongTon.getValue()).intValue();
        int tonMin = ((Number) spnTonToiThieu.getValue()).intValue();

        if (ton <= 0) {
            lblCanhBaoTonKho.setText("⛔ HẾT HÀNG TRONG KHO (Tồn: 0)");
            lblCanhBaoTonKho.setBackground(new Color(254, 226, 226));
            lblCanhBaoTonKho.setForeground(new Color(153, 27, 27));
        } else if (ton <= tonMin) {
            lblCanhBaoTonKho.setText("⚠️ CẢNH BÁO: TỒN KHO DƯỚI MỨC AN TOÀN (Tồn: " + ton + " ≤ Ngưỡng: " + tonMin + ")");
            lblCanhBaoTonKho.setBackground(new Color(254, 243, 199));
            lblCanhBaoTonKho.setForeground(new Color(146, 64, 14));
        } else {
            lblCanhBaoTonKho.setText("✓ TỒN KHO AN TOÀN (Tồn hiện tại: " + ton + " "
                    + (cboDonViTinh.getSelectedItem() != null ? cboDonViTinh.getSelectedItem() : "") + ")");
            lblCanhBaoTonKho.setBackground(new Color(220, 252, 231));
            lblCanhBaoTonKho.setForeground(new Color(22, 101, 52));
        }
    }

    private void xuLyNhapThemHangNhanh() {
        if (sanPham == null) {
            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Nhập số lượng phụ tùng bổ sung thêm vào kho:\n" +
                        "Sản phẩm: " + sanPham.getTenSanPham() + " (" + sanPham.getMaSanPham() + ")",
                "Nhập Hàng Bổ Sung Nhanh",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int soLuongThem = Integer.parseInt(input.trim());
            if (soLuongThem <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập thêm phải lớn hơn 0!",
                        "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (sanPhamBUS.nhapThemHang(sanPham.getMaSanPham(), soLuongThem)) {
                int tonMoi = sanPham.getSoLuongTon();
                spnSoLuongTon.setValue(tonMoi);
                capNhatCanhBaoTonKho();
                daCapNhat = true;
                JOptionPane.showMessageDialog(this,
                        "Đã nhập thêm thành công " + soLuongThem + " sản phẩm vào kho!\n"
                                + "Tổng số lượng tồn mới: " + tonMoi,
                        "Nhập kho thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!",
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi nhập kho", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyLuuCapNhat() {
        if (sanPham == null) {
            dispose();
            return;
        }

        String tenSP = txtTenSP.getText() != null ? txtTenSP.getText().trim() : "";
        if (tenSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTenSP.requestFocus();
            return;
        }

        Object dvtObj = cboDonViTinh.getEditor().getItem();
        String dvt = dvtObj != null ? dvtObj.toString().trim() : "";
        if (dvt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn vị tính không được để trống!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            cboDonViTinh.requestFocus();
            return;
        }

        double giaNhap = ((Number) spnGiaNhap.getValue()).doubleValue();
        double donGiaBan = ((Number) spnDonGiaBan.getValue()).doubleValue();
        double vat = ((Number) spnThueVAT.getValue()).doubleValue();
        int ton = ((Number) spnSoLuongTon.getValue()).intValue();
        int tonMin = ((Number) spnTonToiThieu.getValue()).intValue();
        boolean dangBan = cboTrangThai.getSelectedIndex() == 0;

        try {
            sanPham.setTenSanPham(tenSP);
            sanPham.setDonViTinh(dvt);
            sanPham.setGiaNhap(giaNhap);
            sanPham.setDonGiaBan(donGiaBan);
            sanPham.setThueVAT(vat);
            sanPham.setSoLuongTon(ton);
            sanPham.setSoLuongTonToiThieu(tonMin);
            sanPham.setTrangThai(dangBan);
            sanPham.setAnhSanPham(duongDanAnhHienTai);

            if (sanPhamBUS.capNhatSanPham(sanPham)) {
                daCapNhat = true;
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin sản phẩm [" + sanPham.getMaSanPham() + "] thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể cập nhật sản phẩm. Vui lòng kiểm tra lại kết nối CSDL!",
                        "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi kiểm tra dữ liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void capNhatAnhHienThi(String path) {
        if (lblAnhMinhHoa == null) return;
        ImageIcon icon = IconUtil.layAnhSanPham(path, 175, 145);
        if (icon != null) {
            lblAnhMinhHoa.setIcon(icon);
            lblAnhMinhHoa.setText("");
        } else {
            lblAnhMinhHoa.setIcon(null);
            lblAnhMinhHoa.setText("<html><center style='color:#94A3B8;'>Chưa có<br>hình ảnh</center></html>");
        }
    }

    private void xuLyChonAnhMoi() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn hình ảnh sản phẩm");
        chooser.setFileFilter(new FileNameExtensionFilter("Tập tin hình ảnh (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
        File thuMucImg = new File("img/products");
        if (thuMucImg.exists()) {
            chooser.setCurrentDirectory(thuMucImg);
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String path = f.getAbsolutePath();
            String root = new File("").getAbsolutePath();
            if (path.startsWith(root)) {
                path = path.substring(root.length());
                if (path.startsWith(File.separator)) {
                    path = path.substring(1);
                }
                path = path.replace('\\', '/');
            }
            duongDanAnhHienTai = path;
            capNhatAnhHienThi(duongDanAnhHienTai);
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

    private void dinhDangSpinnerTien(JSpinner spn) {
        spn.setPreferredSize(new Dimension(0, 36));
        spn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spn, "#,##0 đ");
        editor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        editor.getTextField().setFont(new Font("Segoe UI", Font.BOLD, 13));
        editor.getTextField().setForeground(MAU_CHU_CHINH);
        spn.setEditor(editor);
        spn.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
    }

    private void dinhDangSpinnerPhanTram(JSpinner spn) {
        spn.setPreferredSize(new Dimension(0, 36));
        spn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spn, "#0.## '%'");
        editor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        editor.getTextField().setFont(new Font("Segoe UI", Font.BOLD, 13));
        spn.setEditor(editor);
        spn.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
    }

    private void dinhDangSpinnerSoLuong(JSpinner spn) {
        spn.setPreferredSize(new Dimension(0, 36));
        spn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spn, "#,##0");
        editor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        editor.getTextField().setFont(new Font("Segoe UI", Font.BOLD, 13));
        spn.setEditor(editor);
        spn.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
    }
}
