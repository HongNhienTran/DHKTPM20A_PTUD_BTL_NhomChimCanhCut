package ui;

import bus.HoaDonBUS;
import entity.HoaDon;
import entity.KhachHang;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiThanhToan;
import util.ChonNgayLichUtil;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuanLyManHinhQuanLyHoaDon extends JPanel {

    // Bảng màu theo thiết kế ảnh mẫu
    private static final Color MAU_NEN_TRANG = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(26, 58, 92);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_NHAN_NHO = new Color(100, 116, 139);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_XANH_LA_DAM = new Color(16, 185, 129);
    private static final Color MAU_CHON_DONG = new Color(255, 241, 230);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);

    // Màu nền và chữ của pill trạng thái thanh toán trong bảng
    private static final Color MAU_NEN_DA_TT = new Color(220, 252, 231);
    private static final Color MAU_CHU_DA_TT = new Color(21, 128, 61);
    private static final Color MAU_NEN_CHO_TT = new Color(254, 243, 199);
    private static final Color MAU_CHU_CHO_TT = new Color(180, 83, 9);

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo Mã HĐ, Tên khách hàng, SĐT...";
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private final DecimalFormat dinhDangTien;

    private List<HoaDon> danhSachGoc = new ArrayList<>();
    private final List<HoaDon> danhSachHienThi = new ArrayList<>();

    // Thành phần KPI
    private JLabel lblKpiTongHoaDon;
    private JLabel lblKpiDaThanhToan;
    private JLabel lblKpiChoThanhToan;
    private JLabel lblKpiTongThu;

    // Thành phần tìm kiếm & bộ lọc
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiHoaDon;
    private JComboBox<String> cboTrangThai;
    private NutChonNgay btnTuNgay;
    private NutChonNgay btnDenNgay;
    private LocalDate ngayBatDauLoc = null;
    private LocalDate ngayKetThucLoc = null;
    private boolean dangCapNhatBoLoc = false;

    // Thành phần bảng hóa đơn
    private JLabel lblHienThiSoLuong;
    private DefaultTableModel moHinhBang;
    private JTable bangHoaDon;

    public QuanLyManHinhQuanLyHoaDon() {
        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        kyHieu.setGroupingSeparator('.');
        kyHieu.setDecimalSeparator(',');
        this.dinhDangTien = new DecimalFormat("#,##0", kyHieu);

        khoiTaoGiaoDien();
        lamMoiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN_TRANG);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Khu vực phía trên gồm: Tiêu đề + 4 thẻ KPI + Thẻ tìm kiếm & bộ lọc
        JPanel khuVucTren = new JPanel();
        khuVucTren.setOpaque(false);
        khuVucTren.setLayout(new BoxLayout(khuVucTren, BoxLayout.Y_AXIS));

        khuVucTren.add(taoHangTieuDeVaNutLamMoi());
        khuVucTren.add(Box.createVerticalStrut(12));
        khuVucTren.add(taoHangBonTheKpi());
        khuVucTren.add(Box.createVerticalStrut(12));
        khuVucTren.add(taoTheTimKiemVaBoLoc());

        // Khu vực trung tâm: Thẻ bảng Danh Sách Bản Ghi Hóa Đơn
        JPanel theBang = taoTheBangHoaDon();

        add(khuVucTren, BorderLayout.NORTH);
        add(theBang, BorderLayout.CENTER);
    }

    private JPanel taoHangTieuDeVaNutLamMoi() {
        JPanel hang = new JPanel(new BorderLayout());
        hang.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Quản Lý Hóa Đơn & Doanh Thu");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        NutXanhDam btnLamMoiDuLieu = new NutXanhDam(
                "Làm Mới Dữ Liệu",
                new BieuTuongVector(LoaiIcon.LAM_MOI, 14, Color.WHITE)
        );
        btnLamMoiDuLieu.addActionListener(e -> lamMoiDuLieu());

        hang.add(lblTieuDe, BorderLayout.WEST);
        hang.add(btnLamMoiDuLieu, BorderLayout.EAST);
        return hang;
    }

    private JPanel taoHangBonTheKpi() {
        JPanel luoi = new JPanel(new GridLayout(1, 4, 16, 0));
        luoi.setOpaque(false);
        luoi.setPreferredSize(new Dimension(0, 76));

        lblKpiTongHoaDon = taoNhanGiaTriKpi("0", MAU_XANH_DAM);
        lblKpiDaThanhToan = taoNhanGiaTriKpi("0", MAU_XANH_LA_DAM);
        lblKpiChoThanhToan = taoNhanGiaTriKpi("0", MAU_CAM_CHU_DAO);
        lblKpiTongThu = taoNhanGiaTriKpi("0 đ", MAU_XANH_DAM);

        luoi.add(taoMotTheKpi("TỔNG HÓA ĐƠN", lblKpiTongHoaDon,
                LoaiIcon.TAI_LIEU, new Color(239, 246, 255), MAU_XANH_DAM));
        luoi.add(taoMotTheKpi("ĐÃ THANH TOÁN", lblKpiDaThanhToan,
                LoaiIcon.DAU_TICH, new Color(220, 252, 231), MAU_XANH_LA_DAM));
        luoi.add(taoMotTheKpi("CHỜ THANH TOÁN", lblKpiChoThanhToan,
                LoaiIcon.DONG_HO_CAT, new Color(254, 243, 199), MAU_XANH_DAM));
        luoi.add(taoMotTheKpi("TỔNG THU (ĐÃ TT)", lblKpiTongThu,
                LoaiIcon.TUI_TIEN, new Color(241, 245, 249), MAU_XANH_DAM));
        return luoi;
    }

    private JLabel taoNhanGiaTriKpi(String macDinh, Color mauChu) {
        JLabel lbl = new JLabel(macDinh);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(mauChu);
        return lbl;
    }

    private JPanel taoMotTheKpi(String tieuDeNho, JLabel lblGiaTri, LoaiIcon icon, Color nenIcon, Color mauIcon) {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout(12, 0));
        the.setBorder(new EmptyBorder(14, 16, 14, 16));

        HopIconBoGoc hopIcon = new HopIconBoGoc(nenIcon, new BieuTuongVector(icon, 18, mauIcon));

        JPanel cumChu = new JPanel();
        cumChu.setOpaque(false);
        cumChu.setLayout(new BoxLayout(cumChu, BoxLayout.Y_AXIS));

        JLabel lblNhan = new JLabel(tieuDeNho);
        lblNhan.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNhan.setForeground(MAU_CHU_NHAN_NHO);

        cumChu.add(lblNhan);
        cumChu.add(Box.createVerticalStrut(2));
        cumChu.add(lblGiaTri);

        the.add(hopIcon, BorderLayout.WEST);
        the.add(cumChu, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoTheTimKiemVaBoLoc() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new GridBagLayout());
        the.setBorder(new EmptyBorder(11, 14, 11, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        // 1. Ô tìm kiếm bên trái
        KhungTimKiemBoGoc khungTimKiem = new KhungTimKiemBoGoc();
        txtTimKiem = khungTimKiem.getOVanBan();
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                apDungBoLoc();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                apDungBoLoc();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        the.add(khungTimKiem, gbc);

        // 2. Combobox Loại hóa đơn
        cboLoaiHoaDon = taoComboBoLoc(new String[]{
                "Loại hóa đơn", "Bán lẻ", "Dịch vụ"
        }, 145);
        cboLoaiHoaDon.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboLoaiHoaDon, gbc);

        // 3. Combobox Trạng thái
        cboTrangThai = taoComboBoLoc(new String[]{
                "Trạng thái", "Đã thanh toán", "Chờ thanh toán"
        }, 142);
        cboTrangThai.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 2;
        the.add(cboTrangThai, gbc);

        // 4. Nút chọn Từ ngày ▼
        btnTuNgay = new NutChonNgay("Từ ngày");
        btnTuNgay.addActionListener(e -> chonMocNgay(true));
        gbc.gridx = 3;
        the.add(btnTuNgay, gbc);

        // 5. Nút chọn Đến ngày ▼
        btnDenNgay = new NutChonNgay("Đến ngày");
        btnDenNgay.addActionListener(e -> chonMocNgay(false));
        gbc.gridx = 4;
        the.add(btnDenNgay, gbc);

        // 6. Nút vuông đặt lại bộ lọc (↻)
        NutLamMoiVuong btnDatLai = new NutLamMoiVuong();
        btnDatLai.setToolTipText("Đặt lại toàn bộ bộ lọc");
        btnDatLai.addActionListener(e -> datLaiBoLocVaTaiLai());
        gbc.gridx = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        the.add(btnDatLai, gbc);

        return the;
    }

    private <T> JComboBox<T> taoComboBoLoc(T[] mucMacDinh, int chieuRong) {
        JComboBox<T> combo = new JComboBox<>(mucMacDinh);
        combo.setPreferredSize(new Dimension(chieuRong, 38));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(MAU_CHU_CHINH);
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true),
                new EmptyBorder(0, 6, 0, 4)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (!isSelected) {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(MAU_CHU_CHINH);
                }
                return lbl;
            }
        });
        return combo;
    }

    private void chonMocNgay(boolean laTuNgay) {
        LocalDate hienTai = laTuNgay ? ngayBatDauLoc : ngayKetThucLoc;
        String tieuDe = laTuNgay ? "Chọn Mốc Từ Ngày Lọc Hóa Đơn" : "Chọn Mốc Đến Ngày Lọc Hóa Đơn";

        ChonNgayLichUtil.KetQuaChonNgay kq = ChonNgayLichUtil.hienThiLichChonNgay(this, tieuDe, hienTai, true);
        if (!kq.daXacNhan()) {
            return;
        }

        if (kq.xoaLoc()) {
            if (laTuNgay) {
                ngayBatDauLoc = null;
                btnTuNgay.setGiaTriHienThi(null);
            } else {
                ngayKetThucLoc = null;
                btnDenNgay.setGiaTriHienThi(null);
            }
            apDungBoLoc();
        } else if (kq.ngay() != null) {
            LocalDate chon = kq.ngay();
            if (laTuNgay) {
                if (ngayKetThucLoc != null && chon.isAfter(ngayKetThucLoc)) {
                    JOptionPane.showMessageDialog(this,
                            "Mốc 'Từ ngày' không thể lớn hơn mốc 'Đến ngày' (" + ngayKetThucLoc.format(DINH_DANG_NGAY) + ")!",
                            "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                ngayBatDauLoc = chon;
                btnTuNgay.setGiaTriHienThi(chon.format(DINH_DANG_NGAY));
            } else {
                if (ngayBatDauLoc != null && chon.isBefore(ngayBatDauLoc)) {
                    JOptionPane.showMessageDialog(this,
                            "Mốc 'Đến ngày' không thể nhỏ hơn mốc 'Từ ngày' (" + ngayBatDauLoc.format(DINH_DANG_NGAY) + ")!",
                            "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                ngayKetThucLoc = chon;
                btnDenNgay.setGiaTriHienThi(chon.format(DINH_DANG_NGAY));
            }
            apDungBoLoc();
        }
    }

    private JPanel taoTheBangHoaDon() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Thanh đầu thẻ: Tiêu đề bên trái + "Hiển thị X hóa đơn" bên phải
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(13, 18, 11, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Bản Ghi Hóa Đơn");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblHienThiSoLuong = new JLabel("Hiển thị 0 hóa đơn");
        lblHienThiSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHienThiSoLuong.setForeground(MAU_CHU_NHAN_NHO);

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblHienThiSoLuong, BorderLayout.EAST);

        // Mô hình 13 cột chuẩn theo ảnh mẫu
        String[] tenCot = {
                "STT",
                "Mã Hóa Đơn",
                "Phân Loại",
                "Khách Hàng",
                "Số Điện Thoại",
                "Thời Gian Lập",
                "Nhân Viên Lập",
                "Tiền Hàng",
                "Khuyến Mãi",
                "Tổng Thanh Toán",
                "Hình Thức TT",
                "Trạng Thái",
                "Thao Tác"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangHoaDon = new JTable(moHinhBang);
        bangHoaDon.setRowHeight(44);
        bangHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangHoaDon.setForeground(MAU_CHU_CHINH);
        bangHoaDon.setBackground(Color.WHITE);
        bangHoaDon.setSelectionBackground(MAU_CHON_DONG);
        bangHoaDon.setSelectionForeground(MAU_XANH_DAM);
        bangHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangHoaDon.setShowVerticalLines(false);
        bangHoaDon.setShowHorizontalLines(true);
        bangHoaDon.setGridColor(MAU_KE_BANG);
        bangHoaDon.setIntercellSpacing(new Dimension(0, 1));
        bangHoaDon.setFillsViewportHeight(true);

        JTableHeader header = bangHoaDon.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 39));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel cotModel = bangHoaDon.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(42);
        cotModel.getColumn(1).setPreferredWidth(105);
        cotModel.getColumn(2).setPreferredWidth(76);
        cotModel.getColumn(3).setPreferredWidth(120);
        cotModel.getColumn(4).setPreferredWidth(95);
        cotModel.getColumn(5).setPreferredWidth(108);
        cotModel.getColumn(6).setPreferredWidth(112);
        cotModel.getColumn(7).setPreferredWidth(88);
        cotModel.getColumn(8).setPreferredWidth(85);
        cotModel.getColumn(9).setPreferredWidth(105);
        cotModel.getColumn(10).setPreferredWidth(105);
        cotModel.getColumn(11).setPreferredWidth(118);
        cotModel.getColumn(12).setPreferredWidth(72);

        BoVeOHoaDonThuong rendererThuong = new BoVeOHoaDonThuong();
        for (int i = 0; i <= 9; i++) {
            cotModel.getColumn(i).setCellRenderer(rendererThuong);
        }
        cotModel.getColumn(10).setCellRenderer(new BoVeOHinhThucTT());
        cotModel.getColumn(11).setCellRenderer(new BoVeOTrangThai());
        cotModel.getColumn(12).setCellRenderer(new BoVeONutIn());

        // Bấm nút "In" ở cột cuối hoặc nhấp đúp vào bất kỳ dòng nào để mở Modal QuanLyManHinhQuanLyHoaDonXemChiTiet
        bangHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int dong = bangHoaDon.rowAtPoint(e.getPoint());
                int cot = bangHoaDon.columnAtPoint(e.getPoint());
                if (dong < 0 || dong >= danhSachHienThi.size()) {
                    return;
                }
                HoaDon hd = danhSachHienThi.get(dong);
                if (cot == 12 && SwingUtilities.isLeftMouseButton(e)) {
                    moModalChiTietHoaDon(hd, false);
                } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    moModalChiTietHoaDon(hd, false);
                }
            }
        });

        bangHoaDon.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int cot = bangHoaDon.columnAtPoint(e.getPoint());
                bangHoaDon.setCursor(Cursor.getPredefinedCursor(
                        cot == 12 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangHoaDon);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    private void moModalChiTietHoaDon(HoaDon hd, boolean inNgay) {
        if (hd == null) {
            return;
        }
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        QuanLyManHinhQuanLyHoaDonXemChiTiet modal =
                new QuanLyManHinhQuanLyHoaDonXemChiTiet(cuaSoCha, hd, inNgay);
        modal.setVisible(true);
    }

    /**
     * Tải toàn bộ hóa đơn thực tế từ CSDL qua HoaDonBUS và cập nhật 4 thẻ KPI (không dùng dữ liệu giả).
     */
    public void lamMoiDuLieu() {
        try {
            danhSachGoc = hoaDonBUS.layTatCa();
        } catch (Exception ex) {
            danhSachGoc = new ArrayList<>();
        }

        int tongHD = danhSachGoc.size();
        int daTT = 0;
        int choTT = 0;
        double tongDoanhThuDaTT = 0;

        for (HoaDon hd : danhSachGoc) {
            if (hd.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
                daTT++;
                tongDoanhThuDaTT += hd.tinhTongThanhToan();
            } else {
                choTT++;
            }
        }

        lblKpiTongHoaDon.setText(String.valueOf(tongHD));
        lblKpiDaThanhToan.setText(String.valueOf(daTT));
        lblKpiChoThanhToan.setText(String.valueOf(choTT));
        lblKpiTongThu.setText(dinhDangTien.format(Math.round(tongDoanhThuDaTT)) + " đ");

        apDungBoLoc();
    }

    private void datLaiBoLocVaTaiLai() {
        dangCapNhatBoLoc = true;
        try {
            txtTimKiem.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiem.setForeground(MAU_CHU_MO);
            cboLoaiHoaDon.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
            ngayBatDauLoc = null;
            ngayKetThucLoc = null;
            btnTuNgay.setGiaTriHienThi(null);
            btnDenNgay.setGiaTriHienThi(null);
        } finally {
            dangCapNhatBoLoc = false;
        }
        lamMoiDuLieu();
    }

    private void apDungBoLoc() {
        String tuKhoa = txtTimKiem.getText();
        if (tuKhoa == null || PLACEHOLDER_TIM_KIEM.equals(tuKhoa)) {
            tuKhoa = "";
        }
        tuKhoa = tuKhoa.trim().toLowerCase(Locale.ROOT);

        int chiSoLoai = cboLoaiHoaDon.getSelectedIndex();
        int chiSoTrangThai = cboTrangThai.getSelectedIndex();

        danhSachHienThi.clear();
        moHinhBang.setRowCount(0);

        int stt = 1;
        for (HoaDon hd : danhSachGoc) {
            KhachHang kh = hd.getKhachHang();
            boolean laKhachLe = (kh == null || kh.laKhachVangLai());
            String tenKH = laKhachLe ? "Khách lẻ" : (kh.getHoTen() != null ? kh.getHoTen() : "");
            String sdtKH = laKhachLe ? "-" : (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "-");
            String maHD = hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "";

            // 1. Lọc theo từ khoá (Mã HĐ, Tên khách hàng, SĐT)
            if (!tuKhoa.isEmpty()) {
                boolean khop = maHD.toLowerCase(Locale.ROOT).contains(tuKhoa)
                        || tenKH.toLowerCase(Locale.ROOT).contains(tuKhoa)
                        || sdtKH.toLowerCase(Locale.ROOT).contains(tuKhoa);
                if (!khop) {
                    continue;
                }
            }

            // 2. Lọc theo Loại hóa đơn ("Bán lẻ" / "Dịch vụ")
            if (chiSoLoai == 1 && hd.getLoaiHoaDon() != LoaiHoaDon.BAN_HANG) {
                continue;
            } else if (chiSoLoai == 2 && hd.getLoaiHoaDon() != LoaiHoaDon.DICH_VU) {
                continue;
            }

            // 3. Lọc theo Trạng thái
            if (chiSoTrangThai == 1 && hd.getTrangThaiThanhToan() != TrangThaiThanhToan.DA_THANH_TOAN) {
                continue;
            } else if (chiSoTrangThai == 2 && hd.getTrangThaiThanhToan() != TrangThaiThanhToan.CHUA_THANH_TOAN) {
                continue;
            }

            // 4. Lọc theo khoảng Từ ngày - Đến ngày
            if (hd.getThoiGianLap() != null) {
                LocalDate ngayLap = hd.getThoiGianLap().toLocalDate();
                if (ngayBatDauLoc != null && ngayLap.isBefore(ngayBatDauLoc)) {
                    continue;
                }
                if (ngayKetThucLoc != null && ngayLap.isAfter(ngayKetThucLoc)) {
                    continue;
                }
            }

            danhSachHienThi.add(hd);

            String phanLoai = (hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG) ? "Bán lẻ" : "Dịch vụ";
            String thoiGianLap = hd.getThoiGianLap() != null
                    ? hd.getThoiGianLap().format(DINH_DANG_NGAY_GIO) : "-";
            String tenNV = (hd.getNhanVien() != null && hd.getNhanVien().getHoTen() != null)
                    ? hd.getNhanVien().getHoTen() : "-";
            String tienHang = dinhDangTien.format(Math.round(hd.tinhTongTienHang())) + " đ";
            String khuyenMai = dinhDangTien.format(Math.round(hd.tinhTienKhuyenMai())) + " đ";
            String tongThanhToan = dinhDangTien.format(Math.round(hd.tinhTongThanhToan())) + " đ";

            moHinhBang.addRow(new Object[]{
                    stt++,
                    maHD,
                    phanLoai,
                    tenKH,
                    sdtKH,
                    thoiGianLap,
                    tenNV,
                    tienHang,
                    khuyenMai,
                    tongThanhToan,
                    hd.getPhuongThucThanhToan(),
                    hd.getTrangThaiThanhToan(),
                    "In"
            });
        }

        lblHienThiSoLuong.setText("Hiển thị " + danhSachHienThi.size() + " hóa đơn");
    }

    // =========================================================================
    // CÁC COMPONENT ĐỒ HOẠ TUỲ CHỈNH THEO ĐÚNG THIẾT KẾ MẪU
    // =========================================================================

    private enum LoaiIcon {
        LAM_MOI,
        KINH_LUP,
        TAI_LIEU,
        DAU_TICH,
        DONG_HO_CAT,
        TUI_TIEN,
        TIEN_MAT,
        CHUYEN_KHOAN
    }

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

    /**
     * Hộp icon vuông bo góc 42x42 nằm bên trái mỗi thẻ KPI.
     */
    private static class HopIconBoGoc extends JPanel {
        private final Color mauNen;

        HopIconBoGoc(Color mauNen, Icon icon) {
            this.mauNen = mauNen;
            setOpaque(false);
            setPreferredSize(new Dimension(42, 42));
            setLayout(new BorderLayout());
            JLabel lbl = new JLabel(icon, SwingConstants.CENTER);
            add(lbl, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(mauNen);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12f, 12f));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nút "↻ Làm Mới Dữ Liệu" nền xanh đen ở góc trên bên phải.
     */
    private static class NutXanhDam extends JButton {
        private boolean hover = false;

        NutXanhDam(String chu, Icon icon) {
            super(chu, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setIconTextGap(7);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
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
            g2.setColor(hover ? MAU_XANH_DAM_HOVER : MAU_XANH_DAM);
            g2.fill(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class KhungTimKiemBoGoc extends JPanel {
        private final JTextField txt;
        private boolean dangFocus = false;

        KhungTimKiemBoGoc() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(280, 38));
            setBorder(new EmptyBorder(0, 14, 0, 14));

            JLabel lblIcon = new JLabel(new BieuTuongVector(LoaiIcon.KINH_LUP, 14, MAU_CHU_MO));
            txt = new JTextField(PLACEHOLDER_TIM_KIEM);
            txt.setBorder(null);
            txt.setOpaque(false);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setForeground(MAU_CHU_MO);

            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    dangFocus = true;
                    if (PLACEHOLDER_TIM_KIEM.equals(txt.getText())) {
                        txt.setText("");
                        txt.setForeground(MAU_CHU_CHINH);
                    }
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    dangFocus = false;
                    if (txt.getText().trim().isEmpty()) {
                        txt.setText(PLACEHOLDER_TIM_KIEM);
                        txt.setForeground(MAU_CHU_MO);
                    }
                    repaint();
                }
            });

            add(lblIcon, BorderLayout.WEST);
            add(txt, BorderLayout.CENTER);
        }

        JTextField getOVanBan() {
            return txt;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D hinh = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12f, 12f);
            g2.setColor(dangFocus ? Color.WHITE : new Color(248, 250, 252));
            g2.fill(hinh);
            g2.setColor(dangFocus ? MAU_CAM_CHU_DAO : MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(hinh);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nút lọc mốc ngày "Từ ngày ▼" / "Đến ngày ▼" giống hệt bố cục trong ảnh mẫu.
     */
    private static class NutChonNgay extends JButton {
        private final String nhanMacDinh;
        private String giaTriHienThi = null;
        private boolean hover = false;

        NutChonNgay(String nhanMacDinh) {
            this.nhanMacDinh = nhanMacDinh;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setPreferredSize(new Dimension(126, 38));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void setGiaTriHienThi(String giaTri) {
            this.giaTriHienThi = giaTri;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D khungNgoai = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 10f, 10f);
            g2.setColor(hover ? new Color(248, 250, 252) : Color.WHITE);
            g2.fill(khungNgoai);
            g2.setColor(giaTriHienThi != null ? MAU_CAM_CHU_DAO : MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(khungNgoai);

            // Hộp nhãn bên trong bên trái + mũi tên tam giác ▼ bên phải như ảnh mẫu
            RoundRectangle2D hopTrong = new RoundRectangle2D.Float(6f, 6f, w - 34f, h - 12f, 6f, 6f);
            g2.setColor(new Color(248, 250, 252));
            g2.fill(hopTrong);
            g2.setColor(new Color(235, 240, 246));
            g2.draw(hopTrong);

            String chu = giaTriHienThi != null ? giaTriHienThi : nhanMacDinh;
            g2.setFont(new Font("Segoe UI", giaTriHienThi != null ? Font.BOLD : Font.PLAIN, 12));
            g2.setColor(giaTriHienThi != null ? MAU_XANH_DAM : MAU_CHU_MO);
            FontMetrics fm = g2.getFontMetrics();
            int tx = 6 + ((w - 34) - fm.stringWidth(chu)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(chu, Math.max(9, tx), ty);

            // Tam giác đặc ▼ bên phải
            float cx = w - 15f;
            float cy = h / 2f;
            Path2D tamGiac = new Path2D.Float();
            tamGiac.moveTo(cx - 4f, cy - 2.5f);
            tamGiac.lineTo(cx + 4f, cy - 2.5f);
            tamGiac.lineTo(cx, cy + 3.2f);
            tamGiac.closePath();
            g2.setColor(MAU_CHU_CHINH);
            g2.fill(tamGiac);

            g2.dispose();
        }
    }

    private static class NutLamMoiVuong extends JButton {
        private boolean hover = false;

        NutLamMoiVuong() {
            super(new BieuTuongVector(LoaiIcon.LAM_MOI, 16, MAU_XANH_DAM));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setPreferredSize(new Dimension(38, 38));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f);
            g2.setColor(hover ? new Color(226, 232, 240) : new Color(241, 245, 249));
            g2.fill(nen);
            g2.setColor(MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
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
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(MAU_CHU_CHINH);

            if (column == 0 || column == 11 || column == 12) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new EmptyBorder(0, 6, 0, 6));
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 8));
            }
            return lbl;
        }
    }

    /**
     * Bộ vẽ các cột dữ liệu văn bản/số từ cột 0 đến cột 9 (in đậm trên nền cam đào nhạt khi đang chọn).
     */
    private static class BoVeOHoaDonThuong extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 12));
            setForeground(isSelected ? MAU_XANH_DAM : MAU_CHU_PHU);
            setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, column == 0 ? 6 : 10, 0, 8));

            String chuoi = value != null ? value.toString() : "";
            setToolTipText(chuoi.length() > 14 ? chuoi : null);
            return this;
        }
    }

    /**
     * Bộ vẽ cột Hình Thức TT: hiển thị icon tiền mặt xanh lá + "Tiền mặt" / "Chuyển khoản" hoặc "-" nếu chưa TT.
     */
    private static class BoVeOHinhThucTT extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setBorder(new EmptyBorder(0, 10, 0, 8));
            setHorizontalAlignment(SwingConstants.LEFT);

            if (value instanceof PhuongThucThanhToan pt) {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (pt == PhuongThucThanhToan.TIEN_MAT) {
                    setText("Tiền mặt");
                    setForeground(MAU_XANH_LA_DAM);
                    setIcon(new BieuTuongVector(LoaiIcon.TIEN_MAT, 14, MAU_XANH_LA_DAM));
                } else {
                    setText("Chuyển khoản");
                    setForeground(new Color(2, 132, 199));
                    setIcon(new BieuTuongVector(LoaiIcon.CHUYEN_KHOAN, 14, new Color(2, 132, 199)));
                }
                setIconTextGap(5);
            } else {
                setIcon(null);
                setText("-");
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(MAU_CHU_MO);
            }
            return this;
        }
    }

    /**
     * Bộ vẽ cột Trạng Thái dưới dạng pill màu vàng kem ("• CHỜ THANH TOÁN") hoặc xanh bạc hà ("• ĐÃ THANH TOÁN").
     */
    private static class BoVeOTrangThai extends DefaultTableCellRenderer {
        private TrangThaiThanhToan trangThai;
        private boolean chon;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            this.trangThai = (value instanceof TrangThaiThanhToan tt) ? tt : TrangThaiThanhToan.CHUA_THANH_TOAN;
            this.chon = isSelected;
            setText("");
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            g2.setColor(chon ? MAU_CHON_DONG : Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            boolean daTT = (trangThai == TrangThaiThanhToan.DA_THANH_TOAN);
            String nhan = daTT ? "\u2022 ĐÃ THANH TOÁN" : "\u2022 CHỜ THANH TOÁN";

            int pw = Math.min(getWidth() - 10, 108);
            int ph = 24;
            int px = (getWidth() - pw) / 2;
            int py = (getHeight() - ph) / 2;

            g2.setColor(daTT ? MAU_NEN_DA_TT : MAU_NEN_CHO_TT);
            g2.fill(new RoundRectangle2D.Float(px, py, pw, ph, 12f, 12f));

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(daTT ? MAU_CHU_DA_TT : MAU_CHU_CHO_TT);
            FontMetrics fm = g2.getFontMetrics();
            String rutGon = nhan;
            if (fm.stringWidth(rutGon) > pw - 10) {
                rutGon = daTT ? "\u2022 ĐÃ THAN..." : "\u2022 CHỜ THA...";
            }
            int tx = px + (pw - fm.stringWidth(rutGon)) / 2;
            int ty = py + (ph - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(rutGon, tx, ty);

            g2.dispose();
        }
    }

    /**
     * Bộ vẽ nút "In" nền trắng viền xám nhạt nằm trong cột Thao Tác.
     */
    private static class BoVeONutIn extends DefaultTableCellRenderer {
        private boolean chon;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            this.chon = isSelected;
            setText("");
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            g2.setColor(chon ? MAU_CHON_DONG : Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int bw = 42;
            int bh = 26;
            int bx = (getWidth() - bw) / 2;
            int by = (getHeight() - bh) / 2;

            RoundRectangle2D nut = new RoundRectangle2D.Float(bx, by, bw, bh, 7f, 7f);
            g2.setColor(Color.WHITE);
            g2.fill(nut);
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nut);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(MAU_XANH_DAM);
            FontMetrics fm = g2.getFontMetrics();
            int tx = bx + (bw - fm.stringWidth("In")) / 2;
            int ty = by + (bh - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString("In", tx, ty);

            g2.dispose();
        }
    }

    private static class BieuTuongVector implements Icon {
        private final LoaiIcon loai;
        private final int kichThuoc;
        private final Color mau;

        BieuTuongVector(LoaiIcon loai, int kichThuoc, Color mau) {
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
                case LAM_MOI -> "refresh.png";
                case KINH_LUP -> "search.png";
                case TAI_LIEU -> "invoice.png";
                case DAU_TICH -> "save.png";
                case DONG_HO_CAT -> "history.png";
                case TUI_TIEN -> "invoice.png";
                case TIEN_MAT -> "invoice.png";
                case CHUYEN_KHOAN -> "chart.png";
                default -> null;
            };

            if (tenAnh != null) {
                ImageIcon imgIcon = IconUtil.layIcon(tenAnh, kichThuoc, kichThuoc, mau);
                if (imgIcon != null) {
                    imgIcon.paintIcon(c, g, x, y);
                }
            }
        }
    }

    /**
     * Cho phép chạy kiểm thử trực tiếp màn hình này mà vẫn giữ nguyên thanh Header của Quản lý.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyManHinhChinh khungChinh = new QuanLyManHinhChinh();
            khungChinh.chuyenManHinh(QuanLyManHinhChinh.MAN_HOA_DON);
            khungChinh.setVisible(true);
        });
    }
}
