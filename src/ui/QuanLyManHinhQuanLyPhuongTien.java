package ui;

import bus.HangXeBUS;
import bus.KhachHangBUS;
import bus.PhieuTiepNhanBUS;
import bus.PhuongTienBUS;
import entity.HangXe;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import enums.TrangThaiPhieuTiepNhan;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class QuanLyManHinhQuanLyPhuongTien extends JPanel {

    // Bảng màu đồng bộ theo ảnh thiết kế mẫu
    private static final Color MAU_NEN_TRANG = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_NHAN_NHO = new Color(100, 116, 139);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 132, 52);
    private static final Color MAU_XANH_LICH_SU_NEN = new Color(239, 246, 255);
    private static final Color MAU_XANH_LICH_SU_VIEN = new Color(219, 234, 254);
    private static final Color MAU_CHU_BADGE_TONG = new Color(30, 58, 138);
    private static final Color MAU_CHON_DONG_BANG = new Color(235, 242, 250);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo Biển số xe, Dòng xe, Tên chủ xe, SĐT...";

    private final PhuongTienBUS phuongTienBUS = new PhuongTienBUS();
    private final HangXeBUS hangXeBUS = new HangXeBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();

    private List<PhuongTien> danhSachGoc = new ArrayList<>();
    private final List<PhuongTien> danhSachHienThi = new ArrayList<>();
    private PhuongTien phuongTienDangChon = null;

    // Thành phần KPI & Bộ lọc
    private JLabel lblKpiTongPhuongTien;
    private JLabel lblKpiDangPhucVu;
    private JTextField txtTimKiem;
    private JComboBox<LuaChonHangXe> cboLocHangXe;
    private JComboBox<String> cboLocNhienLieu;
    private JComboBox<String> cboLocNamSX;
    private boolean dangCapNhatBoLoc = false;

    // Bảng danh sách phương tiện
    private BadgeVienXanh lblBadgeTongSo;
    private DefaultTableModel moHinhBang;
    private JTable bangPhuongTien;
    private boolean dangDongBoBang = false;

    public QuanLyManHinhQuanLyPhuongTien() {
        khoiTaoGiaoDien();
        lamMoiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN_TRANG);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Khu vực phía trên gồm: Tiêu đề trang + 2 thẻ KPI + Thẻ tìm kiếm & bộ lọc
        JPanel khuVucTren = new JPanel();
        khuVucTren.setOpaque(false);
        khuVucTren.setLayout(new BoxLayout(khuVucTren, BoxLayout.Y_AXIS));

        JPanel hangTieuDe = taoHangTieuDe();
        JPanel hangKpi = taoHangTheKpi();
        JPanel theBoLoc = taoTheTimKiemVaBoLoc();

        khuVucTren.add(hangTieuDe);
        khuVucTren.add(Box.createVerticalStrut(12));
        khuVucTren.add(hangKpi);
        khuVucTren.add(Box.createVerticalStrut(12));
        khuVucTren.add(theBoLoc);

        // Bảng danh sách phương tiện chiếm toàn bộ chiều rộng
        add(khuVucTren, BorderLayout.NORTH);
        add(taoTheBangPhuongTien(), BorderLayout.CENTER);
    }

    private JPanel taoHangTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Quản Lý Phương Tiện");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel cumNutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumNutPhai.setOpaque(false);

        NutCamChuDao btnThemMoi = new NutCamChuDao("+ Đăng Ký Phương Tiện Mới");
        btnThemMoi.setPreferredSize(new Dimension(200, 38));
        btnThemMoi.addActionListener(e -> moHopThoaiChiTietPhuongTien(null));

        cumNutPhai.add(btnThemMoi);

        panel.add(lblTieuDe, BorderLayout.WEST);
        panel.add(cumNutPhai, BorderLayout.EAST);
        return panel;
    }

    private JPanel taoHangTheKpi() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 74));

        lblKpiTongPhuongTien = new JLabel("0 xe");
        lblKpiTongPhuongTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblKpiTongPhuongTien.setForeground(MAU_XANH_DAM);

        lblKpiDangPhucVu = new JLabel("0 xe");
        lblKpiDangPhucVu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblKpiDangPhucVu.setForeground(MAU_CAM_CHU_DAO);

        panel.add(taoMotTheKpi("TỔNG SỐ PHƯƠNG TIỆN", lblKpiTongPhuongTien, LoaiIcon.O_TO, MAU_XANH_DAM));
        panel.add(taoMotTheKpi("ĐANG PHỤC VỤ TẠI XƯỞNG", lblKpiDangPhucVu, LoaiIcon.DONG_HO_CAT, MAU_XANH_DAM));
        return panel;
    }

    private JPanel taoMotTheKpi(String nhanTren, JLabel lblGiaTri, LoaiIcon loaiIcon, Color mauIcon) {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout(14, 0));
        the.setBorder(new EmptyBorder(13, 18, 13, 18));

        JLabel lblIcon = new JLabel(new BieuTuongVector(loaiIcon, 24, mauIcon));
        lblIcon.setVerticalAlignment(SwingConstants.CENTER);

        JPanel cumChu = new JPanel();
        cumChu.setOpaque(false);
        cumChu.setLayout(new BoxLayout(cumChu, BoxLayout.Y_AXIS));

        JLabel lblTieuDeNho = new JLabel(nhanTren);
        lblTieuDeNho.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTieuDeNho.setForeground(MAU_CHU_NHAN_NHO);

        cumChu.add(lblTieuDeNho);
        cumChu.add(Box.createVerticalStrut(2));
        cumChu.add(lblGiaTri);

        the.add(lblIcon, BorderLayout.WEST);
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

        // 1. Ô tìm kiếm dạng pill bo tròn
        KhungTimKiemBoTron khungTimKiem = new KhungTimKiemBoTron();
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

        // 2. Bộ lọc Hãng xe
        cboLocHangXe = taoComboBoLoc(new LuaChonHangXe[]{new LuaChonHangXe(null, "Hãng xe")}, 142);
        cboLocHangXe.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboLocHangXe, gbc);

        // 3. Bộ lọc Nhiên liệu
        cboLocNhienLieu = taoComboBoLoc(new String[]{
                "Nhiên liệu", "Xăng", "Dầu", "Điện", "Hybrid"
        }, 136);
        cboLocNhienLieu.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 2;
        the.add(cboLocNhienLieu, gbc);

        // 4. Bộ lọc Năm sản xuất
        cboLocNamSX = taoComboBoLoc(new String[]{"Năm sản xuất"}, 142);
        cboLocNamSX.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 3;
        the.add(cboLocNamSX, gbc);

        // 5. Nút làm mới (↻)
        NutLamMoiVuong btnLamMoi = new NutLamMoiVuong();
        btnLamMoi.setToolTipText("Đặt lại bộ lọc và tải lại dữ liệu");
        btnLamMoi.addActionListener(e -> datLaiBoLocVaTaiLai());
        gbc.gridx = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        the.add(btnLamMoi, gbc);

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

    private JPanel taoTheBangPhuongTien() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(13, 18, 11, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Phương Tiện Đã Đăng Ký");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblBadgeTongSo = new BadgeVienXanh("Tổng số: 0 phương tiện", MAU_CHU_BADGE_TONG);

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblBadgeTongSo, BorderLayout.EAST);

        String[] tenCot = {
                "Mã PT",
                "Biển Số Xe",
                "Hãng Xe",
                "Dòng / Mẫu Xe",
                "Màu Sắc",
                "Năm SX",
                "Nhiên Liệu",
                "Chủ Xe / Khách Hàng",
                "Số Điện Thoại"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangPhuongTien = new JTable(moHinhBang);
        bangPhuongTien.setRowHeight(40);
        bangPhuongTien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangPhuongTien.setForeground(MAU_CHU_CHINH);
        bangPhuongTien.setBackground(Color.WHITE);
        bangPhuongTien.setSelectionBackground(MAU_CHON_DONG_BANG);
        bangPhuongTien.setSelectionForeground(MAU_XANH_DAM);
        bangPhuongTien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangPhuongTien.setShowVerticalLines(false);
        bangPhuongTien.setShowHorizontalLines(true);
        bangPhuongTien.setGridColor(MAU_KE_BANG);
        bangPhuongTien.setIntercellSpacing(new Dimension(0, 1));
        bangPhuongTien.setFillsViewportHeight(true);

        JTableHeader header = bangPhuongTien.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel cotModel = bangPhuongTien.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(92);
        cotModel.getColumn(1).setPreferredWidth(105);
        cotModel.getColumn(2).setPreferredWidth(88);
        cotModel.getColumn(3).setPreferredWidth(120);
        cotModel.getColumn(4).setPreferredWidth(72);
        cotModel.getColumn(5).setPreferredWidth(68);
        cotModel.getColumn(6).setPreferredWidth(82);
        cotModel.getColumn(7).setPreferredWidth(155);
        cotModel.getColumn(8).setPreferredWidth(108);

        BoVeOPhuongTien rendererO = new BoVeOPhuongTien();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            cotModel.getColumn(i).setCellRenderer(rendererO);
        }

        bangPhuongTien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !dangDongBoBang) {
                int dong = bangPhuongTien.getSelectedRow();
                if (dong >= 0 && dong < danhSachHienThi.size()) {
                    phuongTienDangChon = danhSachHienThi.get(dong);
                } else {
                    phuongTienDangChon = null;
                }
            }
        });

        // Menu chuột phải hỗ trợ xem chi tiết, xem lịch sử hoặc xoá phương tiện
        JPopupMenu menuNguCanh = new JPopupMenu();
        JMenuItem mnuChiTiet = new JMenuItem("Xem & Cập nhật chi tiết xe");
        mnuChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mnuChiTiet.addActionListener(e -> {
            int dong = bangPhuongTien.getSelectedRow();
            if (dong >= 0 && dong < danhSachHienThi.size()) {
                moHopThoaiChiTietPhuongTien(danhSachHienThi.get(dong));
            }
        });

        JMenuItem mnuLichSu = new JMenuItem("Xem lịch sử tiếp nhận & sửa chữa");
        mnuLichSu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuLichSu.addActionListener(e -> moHopThoaiLichSuTiepNhan());

        JMenuItem mnuXoaXe = new JMenuItem("Xóa phương tiện khỏi hệ thống");
        mnuXoaXe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuXoaXe.setForeground(MAU_DO_CANH_BAO);
        mnuXoaXe.addActionListener(e -> xuLyXoaPhuongTienDangChon());

        menuNguCanh.add(mnuChiTiet);
        menuNguCanh.add(mnuLichSu);
        menuNguCanh.addSeparator();
        menuNguCanh.add(mnuXoaXe);

        bangPhuongTien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int dong = bangPhuongTien.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < danhSachHienThi.size()) {
                        moHopThoaiChiTietPhuongTien(danhSachHienThi.get(dong));
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                hienMenuNeuCan(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                hienMenuNeuCan(e);
            }

            private void hienMenuNeuCan(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int dong = bangPhuongTien.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < bangPhuongTien.getRowCount()) {
                        bangPhuongTien.setRowSelectionInterval(dong, dong);
                        menuNguCanh.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangPhuongTien);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    // =========================================================================
    // NGHIỆP VỤ NẠP DỮ LIỆU & LỌC DANH SÁCH PHƯƠNG TIỆN
    // =========================================================================

    /**
     * Nạp dữ liệu thực tế từ CSDL thông qua các lớp BUS (không sinh dữ liệu giả).
     */
    public void lamMoiDuLieu() {
        String maDangChonCu = phuongTienDangChon != null ? phuongTienDangChon.getMaPhuongTien() : null;
        dangCapNhatBoLoc = true;
        try {
            // 1. Nạp danh sách hãng xe cho bộ lọc & form bên phải
            List<HangXe> dsHang = hangXeBUS.layTatCa();

            LuaChonHangXe hangDangLocCu = (LuaChonHangXe) cboLocHangXe.getSelectedItem();
            String maHangLocCu = hangDangLocCu != null ? hangDangLocCu.maHangXe() : null;
            DefaultComboBoxModel<LuaChonHangXe> modelLocHang = new DefaultComboBoxModel<>();
            modelLocHang.addElement(new LuaChonHangXe(null, "Hãng xe"));
            LuaChonHangXe mucHangChonLai = modelLocHang.getElementAt(0);
            for (HangXe hx : dsHang) {
                LuaChonHangXe item = new LuaChonHangXe(hx.getMaHangXe(), hx.getTenHang());
                modelLocHang.addElement(item);
                if (maHangLocCu != null && maHangLocCu.equals(hx.getMaHangXe())) {
                    mucHangChonLai = item;
                }
            }
            cboLocHangXe.setModel(modelLocHang);
            cboLocHangXe.setSelectedItem(mucHangChonLai);

            // 2. Nạp danh sách phương tiện gốc
            danhSachGoc = phuongTienBUS.layTatCa();
            danhSachGoc.sort(Comparator.comparing(PhuongTien::getMaPhuongTien).reversed());

            // 3. Cập nhật combobox lọc Năm sản xuất theo dữ liệu thực tế
            String namCu = (String) cboLocNamSX.getSelectedItem();
            DefaultComboBoxModel<String> modelNam = new DefaultComboBoxModel<>();
            modelNam.addElement("Năm sản xuất");
            Set<Integer> tapNam = new TreeSet<>(Comparator.reverseOrder());
            for (PhuongTien pt : danhSachGoc) {
                tapNam.add(pt.getNamSanXuat());
            }
            for (Integer nam : tapNam) {
                modelNam.addElement(String.valueOf(nam));
            }
            cboLocNamSX.setModel(modelNam);
            if (namCu != null && modelNam.getIndexOf(namCu) >= 0) {
                cboLocNamSX.setSelectedItem(namCu);
            }

            // 4. Tính toán 2 chỉ số KPI thực tế
            lblKpiTongPhuongTien.setText(danhSachGoc.size() + " xe");
            Set<String> xeDangTaiXuong = new HashSet<>();
            for (PhieuTiepNhan ptn : phieuTiepNhanBUS.layTatCa()) {
                TrangThaiPhieuTiepNhan tt = ptn.getTrangThai();
                if (tt != null
                        && tt != TrangThaiPhieuTiepNhan.HOAN_THANH
                        && tt != TrangThaiPhieuTiepNhan.THANH_TOAN
                        && tt != TrangThaiPhieuTiepNhan.DA_HUY
                        && ptn.getPhuongTien() != null
                        && ptn.getPhuongTien().getMaPhuongTien() != null) {
                    xeDangTaiXuong.add(ptn.getPhuongTien().getMaPhuongTien());
                }
            }
            lblKpiDangPhucVu.setText(xeDangTaiXuong.size() + " xe");

        } catch (Exception ex) {
            danhSachGoc = new ArrayList<>();
            lblKpiTongPhuongTien.setText("0 xe");
            lblKpiDangPhucVu.setText("0 xe");
        } finally {
            dangCapNhatBoLoc = false;
        }

        apDungBoLoc();

        // Khôi phục dòng đang chọn (hoặc chọn dòng đầu tiên nếu có dữ liệu)
        if (!danhSachHienThi.isEmpty()) {
            int viTriChon = 0;
            if (maDangChonCu != null) {
                for (int i = 0; i < danhSachHienThi.size(); i++) {
                    if (maDangChonCu.equals(danhSachHienThi.get(i).getMaPhuongTien())) {
                        viTriChon = i;
                        break;
                    }
                }
            }
            bangPhuongTien.setRowSelectionInterval(viTriChon, viTriChon);
            phuongTienDangChon = danhSachHienThi.get(viTriChon);
        } else {
            phuongTienDangChon = null;
        }
    }

    private void datLaiBoLocVaTaiLai() {
        dangCapNhatBoLoc = true;
        try {
            txtTimKiem.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiem.setForeground(MAU_CHU_MO);
            if (cboLocHangXe.getItemCount() > 0) {
                cboLocHangXe.setSelectedIndex(0);
            }
            cboLocNhienLieu.setSelectedIndex(0);
            if (cboLocNamSX.getItemCount() > 0) {
                cboLocNamSX.setSelectedIndex(0);
            }
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

        LuaChonHangXe locHang = (LuaChonHangXe) cboLocHangXe.getSelectedItem();
        String maHangLoc = locHang != null ? locHang.maHangXe() : null;
        int chiSoNhienLieu = cboLocNhienLieu.getSelectedIndex();
        String namLoc = (String) cboLocNamSX.getSelectedItem();

        dangDongBoBang = true;
        try {
            danhSachHienThi.clear();
            moHinhBang.setRowCount(0);

            for (PhuongTien pt : danhSachGoc) {
                String maPT = pt.getMaPhuongTien() != null ? pt.getMaPhuongTien() : "";
                String bienSo = pt.getBienSoXe() != null ? pt.getBienSoXe() : "";
                String tenHang = (pt.getHangXe() != null && pt.getHangXe().getTenHang() != null)
                        ? pt.getHangXe().getTenHang() : "";
                String mauXe = pt.getMauXe() != null ? pt.getMauXe() : "";
                String mauSac = pt.getMauSacXe() != null ? pt.getMauSacXe() : "";
                String tenChu = (pt.getKhachHang() != null && pt.getKhachHang().getHoTen() != null)
                        ? pt.getKhachHang().getHoTen() : "";
                String sdtChu = (pt.getKhachHang() != null && pt.getKhachHang().getSoDienThoai() != null)
                        ? pt.getKhachHang().getSoDienThoai() : "";

                if (!tuKhoa.isEmpty()) {
                    boolean khop = maPT.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || bienSo.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || mauXe.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || tenHang.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || tenChu.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || sdtChu.toLowerCase(Locale.ROOT).contains(tuKhoa);
                    if (!khop) {
                        continue;
                    }
                }

                if (maHangLoc != null) {
                    String maHangPT = pt.getHangXe() != null ? pt.getHangXe().getMaHangXe() : null;
                    if (!maHangLoc.equals(maHangPT)) {
                        continue;
                    }
                }

                if (chiSoNhienLieu > 0 && pt.getNhienLieu() != null) {
                    String tenNLChon = (String) cboLocNhienLieu.getSelectedItem();
                    if (!pt.getNhienLieu().getTenHienThi().equalsIgnoreCase(tenNLChon)) {
                        continue;
                    }
                }

                if (namLoc != null && !"Năm sản xuất".equals(namLoc)) {
                    if (!namLoc.equals(String.valueOf(pt.getNamSanXuat()))) {
                        continue;
                    }
                }

                danhSachHienThi.add(pt);
                moHinhBang.addRow(new Object[]{
                        maPT,
                        bienSo,
                        tenHang,
                        mauXe,
                        mauSac,
                        pt.getNamSanXuat(),
                        pt.getNhienLieu() != null ? pt.getNhienLieu().getTenHienThi() : "",
                        tenChu,
                        sdtChu
                });
            }
            lblBadgeTongSo.setText("Tổng số: " + danhSachHienThi.size() + " phương tiện");
        } finally {
            dangDongBoBang = false;
        }
    }

    private void xuLyXoaPhuongTienDangChon() {
        if (phuongTienDangChon == null) {
            return;
        }
        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa phương tiện [" + phuongTienDangChon.getBienSoXe()
                        + " - " + phuongTienDangChon.getMaPhuongTien() + "] không?",
                "Xác nhận xóa phương tiện",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (phuongTienBUS.xoaPhuongTien(phuongTienDangChon.getMaPhuongTien())) {
                phuongTienDangChon = null;
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa phương tiện đã phát sinh lịch sử tiếp nhận sửa chữa.\nChi tiết: " + ex.getMessage(),
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Mở modal Hồ Sơ Chi Tiết & Lịch Sử Tiếp Nhận Sửa Chữa (tách riêng thành class độc lập).
     */
    public void moHopThoaiChiTietPhuongTien(PhuongTien pt) {
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        QuanLyManHinhQuanLyPhuongTienXemChiTiet modal =
                new QuanLyManHinhQuanLyPhuongTienXemChiTiet(cuaSoCha, pt, phuongTienBUS, phieuTiepNhanBUS, hangXeBUS, khachHangBUS);
        modal.setVisible(true);
        if (modal.isDaCapNhat()) {
            lamMoiDuLieu();
        }
    }

    private void moHopThoaiLichSuTiepNhan() {
        if (phuongTienDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một phương tiện trong danh sách để xem lịch sử tiếp nhận & sửa chữa!",
                    "Chưa chọn phương tiện", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        moHopThoaiChiTietPhuongTien(phuongTienDangChon);
    }

    // =========================================================================
    // CÁC COMPONENT ĐỒ HOẠ TUỲ CHỈNH THEO ĐÚNG THIẾT KẾ MẪU
    // =========================================================================

    private record LuaChonHangXe(String maHangXe, String tenHienThi) {
        @Override
        public String toString() {
            return tenHienThi;
        }
    }

    private enum LoaiIcon {
        O_TO,
        DONG_HO_CAT,
        KINH_LUP,
        LAM_MOI,
        NGUOI_DUNG,
        LICH_SU
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

    private static class NutVienTrang extends JButton {
        private boolean hover = false;

        NutVienTrang(String vanBan, Icon icon) {
            super(vanBan, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_CHINH);
            setIconTextGap(7);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 15, 8, 15));

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
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 9f, 9f);
            g2.setColor(hover ? new Color(241, 245, 249) : Color.WHITE);
            g2.fill(nen);
            g2.setColor(hover ? MAU_CAM_CHU_DAO : MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class KhungTimKiemBoTron extends JPanel {
        private final JTextField txt;
        private boolean dangFocus = false;

        KhungTimKiemBoTron() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(320, 38));
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
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 20f, 20f);
            g2.setColor(dangFocus ? Color.WHITE : new Color(248, 250, 252));
            g2.fill(pill);
            g2.setColor(dangFocus ? MAU_CAM_CHU_DAO : MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(pill);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutCamChuDao extends JButton {
        private boolean hover = false;

        NutCamChuDao(String vanBan) {
            super(vanBan);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 14, 8, 14));

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
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 9f, 9f);
            g2.setColor(hover ? MAU_CAM_HOVER : MAU_CAM_CHU_DAO);
            g2.fill(nen);
            g2.dispose();
            super.paintComponent(g);
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

    private static class BadgeVienXanh extends JLabel {
        BadgeVienXanh(String vanBan, Color mauChu) {
            super(vanBan);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(mauChu);
            setBorder(new EmptyBorder(4, 12, 4, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 14f, 14f);
            g2.setColor(MAU_XANH_LICH_SU_NEN);
            g2.fill(pill);
            g2.setColor(MAU_XANH_LICH_SU_VIEN);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(pill);
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
            lbl.setHorizontalAlignment(column == 5 ? SwingConstants.CENTER : SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(0, column == 0 ? 14 : 10, 0, 10));
            return lbl;
        }
    }

    /**
     * Khi dòng được chọn: tô nền xanh nhạt (#EBF2FA) và in đậm toàn bộ chữ trên dòng đó giống hệt ảnh mẫu.
     */
    private static class BoVeOPhuongTien extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected ? MAU_CHON_DONG_BANG : Color.WHITE);
            setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 12));
            setForeground(isSelected ? MAU_XANH_DAM : MAU_CHU_PHU);
            setHorizontalAlignment(column == 5 ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, column == 0 ? 14 : 10, 0, 10));

            String chuoi = value != null ? value.toString() : "";
            setToolTipText(chuoi.length() > 16 ? chuoi : null);
            return this;
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

        private String layTenFileIcon(LoaiIcon loai) {
            return switch (loai) {
                case O_TO -> "car.png";
                case KINH_LUP -> "search.png";
                case LAM_MOI -> "refresh.png";
                case NGUOI_DUNG -> "user.png";
                case LICH_SU, DONG_HO_CAT -> "history.png";
            };
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
            Icon iconImg = IconUtil.layIcon(layTenFileIcon(loai), kichThuoc, kichThuoc, mau);
            if (iconImg != null) {
                iconImg.paintIcon(c, g, x, y);
            }
        }
    }

    /**
     * Cho phép chạy trực tiếp màn hình này mà vẫn giữ nguyên đầy đủ thanh Header của Quản lý.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyManHinhChinh khungChinh = new QuanLyManHinhChinh();
            khungChinh.chuyenManHinh(QuanLyManHinhChinh.MAN_PHUONG_TIEN);
            khungChinh.setVisible(true);
        });
    }
}
