package ui;

import bus.MaKhuyenMaiBUS;
import entity.MaKhuyenMai;
import enums.LoaiGiamGia;
import util.ChonNgayLichUtil;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuanLyManHinhQuanLyMaKhuyenMai extends JPanel {

    // Bảng màu đồng bộ theo thiết kế mẫu
    private static final Color MAU_NEN_TRANG = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_NGAY = new Color(100, 116, 139);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 132, 52);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);
    private static final Color MAU_NEN_BADGE_TONG = new Color(239, 246, 255);
    private static final Color MAU_VIEN_BADGE_TONG = new Color(219, 234, 254);
    private static final Color MAU_CHU_BADGE_TONG = new Color(30, 58, 138);
    private static final Color MAU_CHON_DONG = new Color(255, 247, 237);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo Mã khuyến mãi...";
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final MaKhuyenMaiBUS maKhuyenMaiBUS = new MaKhuyenMaiBUS();
    private final DecimalFormat dinhDangTien;

    private List<MaKhuyenMai> danhSachGoc = new ArrayList<>();
    private final List<MaKhuyenMai> danhSachHienThi = new ArrayList<>();

    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiGiam;
    private JComboBox<String> cboTrangThai;
    private BadgeTongSo lblBadgeTongSo;
    private DefaultTableModel moHinhBang;
    private JTable bangKhuyenMai;
    private boolean dangCapNhatBoLoc = false;

    public QuanLyManHinhQuanLyMaKhuyenMai() {
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
        setBorder(new EmptyBorder(18, 24, 22, 24));

        // Khu vực phía trên: Tiêu đề + nút Thêm Mã Khuyến Mãi, bên dưới là thanh tìm kiếm & bộ lọc
        JPanel khuVucTren = new JPanel(new BorderLayout(0, 14));
        khuVucTren.setOpaque(false);
        khuVucTren.add(taoHangTieuDeVaNutThem(), BorderLayout.NORTH);
        khuVucTren.add(taoTheTimKiemVaBoLoc(), BorderLayout.CENTER);

        // Khu vực trung tâm: Thẻ bảng Danh Sách Mã Khuyến Mãi Gara
        JPanel theBang = taoTheBangKhuyenMai();

        add(khuVucTren, BorderLayout.NORTH);
        add(theBang, BorderLayout.CENTER);
    }

    private JPanel taoHangTieuDeVaNutThem() {
        JPanel hang = new JPanel(new BorderLayout());
        hang.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Mã Khuyến Mãi & Voucher");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel cumNutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cumNutPhai.setOpaque(false);

        NutCamChuDao btnThemMoi = new NutCamChuDao(
                "Thêm Mã Khuyến Mãi",
                new BieuTuongNut(LoaiIcon.DAU_CONG, 14, Color.WHITE)
        );
        btnThemMoi.addActionListener(e -> moHopThoaiMaKhuyenMai(null));
        cumNutPhai.add(btnThemMoi);

        hang.add(lblTieuDe, BorderLayout.WEST);
        hang.add(cumNutPhai, BorderLayout.EAST);
        return hang;
    }

    private JPanel taoTheTimKiemVaBoLoc() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new GridBagLayout());
        the.setBorder(new EmptyBorder(12, 14, 12, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        // 1. Ô tìm kiếm bo tròn dạng pill chiếm toàn bộ phần rộng co giãn bên trái
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

        // 2. Combobox Loại giảm
        cboLoaiGiam = taoComboBoLoc(new String[]{
                "Loại giảm",
                "Tiền mặt",
                "% Phần trăm"
        }, 148);
        cboLoaiGiam.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboLoaiGiam, gbc);

        // 3. Combobox Trạng thái
        cboTrangThai = taoComboBoLoc(new String[]{
                "Trạng thái",
                "Đang áp dụng",
                "Ngừng áp dụng",
                "Đã hết hạn"
        }, 152);
        cboTrangThai.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 2;
        the.add(cboTrangThai, gbc);

        // 4. Nút làm mới (↻) ở ngoài cùng bên phải
        NutLamMoiVuong btnLamMoi = new NutLamMoiVuong();
        btnLamMoi.setToolTipText("Đặt lại bộ lọc và tải lại dữ liệu");
        btnLamMoi.addActionListener(e -> datLaiBoLocVaTaiLai());
        gbc.gridx = 3;
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

    private JPanel taoTheBangKhuyenMai() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Thanh đầu thẻ: Tiêu đề bên trái + Badge "Tổng số: X mã khuyến mãi" bên phải
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(14, 18, 12, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Mã Khuyến Mãi Gara");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblBadgeTongSo = new BadgeTongSo("Tổng số: 0 mã khuyến mãi");

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblBadgeTongSo, BorderLayout.EAST);

        // Mô hình 8 cột chuẩn theo ảnh mẫu
        String[] tenCot = {
                "Mã KM",
                "Loại Giảm",
                "Mức Giảm",
                "Đơn Tối Thiểu",
                "Ngày Bắt Đầu",
                "Ngày Hết Hạn",
                "Lượt Dùng",
                "Trạng Thái"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangKhuyenMai = new JTable(moHinhBang);
        bangKhuyenMai.setRowHeight(42);
        bangKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangKhuyenMai.setForeground(MAU_CHU_CHINH);
        bangKhuyenMai.setBackground(Color.WHITE);
        bangKhuyenMai.setSelectionBackground(MAU_CHON_DONG);
        bangKhuyenMai.setSelectionForeground(MAU_CHU_CHINH);
        bangKhuyenMai.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangKhuyenMai.setShowVerticalLines(false);
        bangKhuyenMai.setShowHorizontalLines(true);
        bangKhuyenMai.setGridColor(MAU_KE_BANG);
        bangKhuyenMai.setIntercellSpacing(new Dimension(0, 1));
        bangKhuyenMai.setFillsViewportHeight(true);

        JTableHeader header = bangKhuyenMai.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel cotModel = bangKhuyenMai.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(155);
        cotModel.getColumn(1).setPreferredWidth(130);
        cotModel.getColumn(2).setPreferredWidth(135);
        cotModel.getColumn(3).setPreferredWidth(145);
        cotModel.getColumn(4).setPreferredWidth(165);
        cotModel.getColumn(5).setPreferredWidth(165);
        cotModel.getColumn(6).setPreferredWidth(110);
        cotModel.getColumn(7).setPreferredWidth(145);

        BoVeOMaKhuyenMai rendererO = new BoVeOMaKhuyenMai();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            cotModel.getColumn(i).setCellRenderer(rendererO);
        }

        // Nhấp đúp hoặc menu chuột phải để chỉnh sửa / đổi trạng thái / xoá mã khuyến mãi
        JPopupMenu menuNguCanh = taoMenuNguCanhBang();
        bangKhuyenMai.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    MaKhuyenMai km = layMaKhuyenMaiDangChon();
                    if (km != null) {
                        moHopThoaiMaKhuyenMai(km);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                hienThiMenuNeuCan(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                hienThiMenuNeuCan(e);
            }

            private void hienThiMenuNeuCan(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int dong = bangKhuyenMai.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < bangKhuyenMai.getRowCount()) {
                        bangKhuyenMai.setRowSelectionInterval(dong, dong);
                        menuNguCanh.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangKhuyenMai);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    private JPopupMenu taoMenuNguCanhBang() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem mnuSua = new JMenuItem("Chỉnh sửa mã khuyến mãi");
        mnuSua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuSua.addActionListener(e -> {
            MaKhuyenMai km = layMaKhuyenMaiDangChon();
            if (km != null) {
                moHopThoaiMaKhuyenMai(km);
            }
        });

        JMenuItem mnuDoiTrangThai = new JMenuItem("Đổi trạng thái Đang áp dụng / Ngừng áp dụng");
        mnuDoiTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuDoiTrangThai.addActionListener(e -> xuLyDoiTrangThaiNhanh());

        JMenuItem mnuXoa = new JMenuItem("Xóa mã khuyến mãi");
        mnuXoa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuXoa.setForeground(MAU_DO_CANH_BAO);
        mnuXoa.addActionListener(e -> xuLyXoaMaKhuyenMai());

        menu.add(mnuSua);
        menu.add(mnuDoiTrangThai);
        menu.addSeparator();
        menu.add(mnuXoa);
        return menu;
    }

    private MaKhuyenMai layMaKhuyenMaiDangChon() {
        int dong = bangKhuyenMai.getSelectedRow();
        if (dong < 0 || dong >= danhSachHienThi.size()) {
            return null;
        }
        return danhSachHienThi.get(dong);
    }

    /**
     * Tải danh sách mã khuyến mãi thực tế từ CSDL qua MaKhuyenMaiBUS (tuyệt đối không dùng dữ liệu giả).
     */
    public void lamMoiDuLieu() {
        try {
            danhSachGoc = maKhuyenMaiBUS.layTatCa();
        } catch (Exception ex) {
            danhSachGoc = new ArrayList<>();
        }
        apDungBoLoc();
    }

    private void datLaiBoLocVaTaiLai() {
        dangCapNhatBoLoc = true;
        try {
            txtTimKiem.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiem.setForeground(MAU_CHU_MO);
            cboLoaiGiam.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
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

        int chiSoLoaiGiam = cboLoaiGiam.getSelectedIndex();
        int chiSoTrangThai = cboTrangThai.getSelectedIndex();
        LocalDateTime bayGio = LocalDateTime.now();

        danhSachHienThi.clear();
        moHinhBang.setRowCount(0);

        for (MaKhuyenMai km : danhSachGoc) {
            // 1. Lọc theo Mã KM
            if (!tuKhoa.isEmpty()) {
                String ma = km.getMaKhuyenMai() != null ? km.getMaKhuyenMai().toLowerCase(Locale.ROOT) : "";
                if (!ma.contains(tuKhoa)) {
                    continue;
                }
            }

            // 2. Lọc theo Loại giảm
            if (chiSoLoaiGiam == 1 && km.getLoaiGiam() != LoaiGiamGia.SO_TIEN) {
                continue;
            } else if (chiSoLoaiGiam == 2 && km.getLoaiGiam() != LoaiGiamGia.PHAN_TRAM) {
                continue;
            }

            // 3. Xác định trạng thái thực tế & lọc theo Trạng thái
            boolean daHetHan = km.getNgayHetHan() != null && bayGio.isAfter(km.getNgayHetHan());
            boolean dangApDungHieuLuc = km.isTrangThai() && !daHetHan;

            if (chiSoTrangThai == 1 && !dangApDungHieuLuc) {
                continue;
            } else if (chiSoTrangThai == 2 && km.isTrangThai()) {
                continue;
            } else if (chiSoTrangThai == 3 && !daHetHan) {
                continue;
            }

            danhSachHienThi.add(km);

            String tenLoaiGiam = (km.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) ? "% Phần trăm" : "Tiền mặt";
            String mucGiamHienThi;
            if (km.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
                double gt = km.getGiaTriGiam();
                mucGiamHienThi = (gt == Math.floor(gt))
                        ? String.format(Locale.US, "%d%%", (long) gt)
                        : String.format(Locale.US, "%.1f%%", gt);
            } else {
                mucGiamHienThi = dinhDangTien.format(Math.round(km.getGiaTriGiam())) + " đ";
            }

            String donToiThieuHienThi = dinhDangTien.format(Math.round(km.getGiaTriToiThieuApDung())) + " đ";
            String batDauHienThi = km.getNgayBatDauApDung() != null
                    ? km.getNgayBatDauApDung().format(DINH_DANG_NGAY_GIO)
                    : "";
            String hetHanHienThi = km.getNgayHetHan() != null
                    ? km.getNgayHetHan().format(DINH_DANG_NGAY_GIO)
                    : "";
            Object luotDungHienThi = km.getSoLuongTon() != null ? km.getSoLuongTon() : "Không giới hạn";

            String trangThaiHienThi;
            if (!km.isTrangThai()) {
                trangThaiHienThi = "\u2022 Ngừng áp dụng";
            } else if (daHetHan) {
                trangThaiHienThi = "\u2022 Đã hết hạn";
            } else {
                trangThaiHienThi = "\u2022 Đang áp dụng";
            }

            moHinhBang.addRow(new Object[]{
                    km.getMaKhuyenMai(),
                    tenLoaiGiam,
                    mucGiamHienThi,
                    donToiThieuHienThi,
                    batDauHienThi,
                    hetHanHienThi,
                    luotDungHienThi,
                    trangThaiHienThi
            });
        }

        lblBadgeTongSo.setText("Tổng số: " + danhSachHienThi.size() + " mã khuyến mãi");
    }

    private void xuLyDoiTrangThaiNhanh() {
        MaKhuyenMai km = layMaKhuyenMaiDangChon();
        if (km == null) {
            return;
        }
        try {
            km.setTrangThai(!km.isTrangThai());
            if (maKhuyenMaiBUS.capNhatMaKhuyenMai(km)) {
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi cập nhật trạng thái", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoaMaKhuyenMai() {
        MaKhuyenMai km = layMaKhuyenMaiDangChon();
        if (km == null) {
            return;
        }
        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa mã khuyến mãi [" + km.getMaKhuyenMai() + "] không?",
                "Xác nhận xóa mã khuyến mãi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (maKhuyenMaiBUS.xoaMaKhuyenMai(km.getMaKhuyenMai())) {
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa mã khuyến mãi này (có thể đã được áp dụng trong hóa đơn).\n"
                            + "Gợi ý: Bạn có thể chuyển sang trạng thái 'Ngừng áp dụng'.\nChi tiết: " + ex.getMessage(),
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Hộp thoại Thêm mới / Cập nhật Mã Khuyến Mãi & Voucher.
     */
    private void moHopThoaiMaKhuyenMai(MaKhuyenMai kmCanSua) {
        boolean laChinhSua = (kmCanSua != null);
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                cuaSoCha,
                laChinhSua ? "Cập Nhật Mã Khuyến Mãi" : "Thêm Mã Khuyến Mãi & Voucher Mới",
                JDialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(660, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        dialog.setLayout(new BorderLayout());

        // Đầu hộp thoại
        JPanel dauDialog = new JPanel(new BorderLayout());
        dauDialog.setBackground(MAU_XANH_DAM);
        dauDialog.setBorder(new EmptyBorder(15, 22, 15, 22));
        JLabel lblTieuDe = new JLabel(laChinhSua
                ? "CẬP NHẬT MÃ KHUYẾN MÃI (" + kmCanSua.getMaKhuyenMai() + ")"
                : "THÊM MÃ KHUYẾN MÃI & VOUCHER MỚI");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(Color.WHITE);
        dauDialog.add(lblTieuDe, BorderLayout.WEST);

        // Biểu mẫu nhập thông tin
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(18, 22, 14, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtMaKM = taoOForm(!laChinhSua);
        txtMaKM.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if (laChinhSua) {
            txtMaKM.setBackground(new Color(241, 245, 249));
            txtMaKM.setText(kmCanSua.getMaKhuyenMai());
        } else {
            txtMaKM.setToolTipText("Ví dụ: KM_DAUNHOT, KM_HE2026, KM_TRIAN...");
        }

        JComboBox<String> cboChonLoaiGiam = new JComboBox<>(new String[]{"Tiền mặt", "% Phần trăm"});
        cboChonLoaiGiam.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboChonLoaiGiam.setPreferredSize(new Dimension(145, 34));

        JSpinner spnMucGiam = new JSpinner(new SpinnerNumberModel(30_000.0, -1_000_000_000.0, 1_000_000_000.0, 5_000.0));
        JSpinner spnDonToiThieu = new JSpinner(new SpinnerNumberModel(200_000.0, -1_000_000_000.0, 1_000_000_000.0, 50_000.0));
        dinhDangSpinnerSo(spnMucGiam, "#,##0.##");
        dinhDangSpinnerSo(spnDonToiThieu, "#,##0");

        cboChonLoaiGiam.addActionListener(e -> {
            double hienTai = ((Number) spnMucGiam.getValue()).doubleValue();
            if (cboChonLoaiGiam.getSelectedIndex() == 1 && hienTai > 100.0) {
                spnMucGiam.setValue(10.0);
            }
        });

        LocalDate homNay = LocalDate.now();
        LocalDateTime macDinhBatDau = homNay.atStartOfDay();
        LocalDateTime macDinhHetHan = LocalDate.of(homNay.getYear(), 12, 31).atTime(23, 59);
        if (macDinhHetHan.isBefore(macDinhBatDau)) {
            macDinhHetHan = homNay.plusMonths(3).atTime(23, 59);
        }

        JSpinner spnNgayBatDau = taoSpinnerNgayGio(macDinhBatDau);
        JSpinner spnNgayHetHan = taoSpinnerNgayGio(macDinhHetHan);

        JCheckBox chkGioiHanLuot = new JCheckBox("Giới hạn lượt:", true);
        chkGioiHanLuot.setOpaque(false);
        chkGioiHanLuot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkGioiHanLuot.setForeground(MAU_CHU_CHINH);

        JSpinner spnLuotDung = new JSpinner(new SpinnerNumberModel(100, -1_000_000, 1_000_000, 10));
        dinhDangSpinnerSo(spnLuotDung, "#,##0");
        chkGioiHanLuot.addActionListener(e -> spnLuotDung.setEnabled(chkGioiHanLuot.isSelected()));

        JPanel cumLuotDung = new JPanel(new BorderLayout(8, 0));
        cumLuotDung.setOpaque(false);
        cumLuotDung.add(chkGioiHanLuot, BorderLayout.WEST);
        cumLuotDung.add(spnLuotDung, BorderLayout.CENTER);

        JComboBox<String> cboTrangThaiKM = new JComboBox<>(new String[]{"Đang áp dụng", "Ngừng áp dụng"});
        cboTrangThaiKM.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTrangThaiKM.setPreferredSize(new Dimension(160, 34));

        if (laChinhSua) {
            cboChonLoaiGiam.setSelectedIndex(kmCanSua.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM ? 1 : 0);
            spnMucGiam.setValue(kmCanSua.getGiaTriGiam());
            spnDonToiThieu.setValue(kmCanSua.getGiaTriToiThieuApDung());
            if (kmCanSua.getNgayBatDauApDung() != null) {
                spnNgayBatDau.setValue(chuyenSangDate(kmCanSua.getNgayBatDauApDung()));
            }
            if (kmCanSua.getNgayHetHan() != null) {
                spnNgayHetHan.setValue(chuyenSangDate(kmCanSua.getNgayHetHan()));
            }
            if (kmCanSua.getSoLuongTon() != null) {
                chkGioiHanLuot.setSelected(true);
                spnLuotDung.setEnabled(true);
                spnLuotDung.setValue(kmCanSua.getSoLuongTon());
            } else {
                chkGioiHanLuot.setSelected(false);
                spnLuotDung.setEnabled(false);
            }
            cboTrangThaiKM.setSelectedIndex(kmCanSua.isTrangThai() ? 0 : 1);
        }

        themDongForm(form, gbc, 0, "Mã khuyến mãi (*):", txtMaKM);

        JPanel hangLoaiVaMucGiam = taoHangHaiCot("Loại giảm:", cboChonLoaiGiam, "Mức giảm (*):", spnMucGiam);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        form.add(hangLoaiVaMucGiam, gbc);

        gbc.gridwidth = 1;
        themDongForm(form, gbc, 2, "Đơn tối thiểu (VNĐ):", spnDonToiThieu);

        JPanel oNgayBD = taoKhoiNgayGioKemLich(spnNgayBatDau, "Chọn Ngày Bắt Đầu Áp Dụng", dialog);
        JPanel oNgayHH = taoKhoiNgayGioKemLich(spnNgayHetHan, "Chọn Ngày Hết Hạn", dialog);
        JPanel hangThoiGian = taoHangHaiCot("Ngày bắt đầu:", oNgayBD, "Ngày hết hạn:", oNgayHH);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        form.add(hangThoiGian, gbc);

        gbc.gridwidth = 1;
        themDongForm(form, gbc, 4, "Số lượt dùng:", cumLuotDung);
        themDongForm(form, gbc, 5, "Trạng thái:", cboTrangThaiKM);

        // Chân hộp thoại
        JPanel chanDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        chanDialog.setBackground(new Color(248, 250, 252));
        chanDialog.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN_THE));

        NutVienTrang btnHuy = new NutVienTrang("Hủy bỏ");
        btnHuy.addActionListener(e -> dialog.dispose());

        NutCamChuDao btnLuu = new NutCamChuDao(laChinhSua ? "Lưu Cập Nhật" : "Thêm Mã Khuyến Mãi", null);
        btnLuu.addActionListener(e -> {
            try {
                String ma = txtMaKM.getText() != null ? txtMaKM.getText().trim().toUpperCase(Locale.ROOT) : "";
                LoaiGiamGia loai = cboChonLoaiGiam.getSelectedIndex() == 1
                        ? LoaiGiamGia.PHAN_TRAM
                        : LoaiGiamGia.SO_TIEN;
                double mucGiam = ((Number) spnMucGiam.getValue()).doubleValue();
                double donToiThieu = ((Number) spnDonToiThieu.getValue()).doubleValue();
                LocalDateTime ngayBD = LocalDateTime.parse(((JSpinner.DateEditor) spnNgayBatDau.getEditor()).getTextField().getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));// lấy LocalDateTime từ String
                LocalDateTime ngayHH = LocalDateTime.parse(((JSpinner.DateEditor) spnNgayHetHan.getEditor()).getTextField().getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                Integer soLuot = chkGioiHanLuot.isSelected()
                        ? ((Number) spnLuotDung.getValue()).intValue()
                        : null;
                boolean dangApDung = cboTrangThaiKM.getSelectedIndex() == 0;

                MaKhuyenMai duLieuLuu = new MaKhuyenMai(
                        ma, loai, mucGiam, donToiThieu, ngayBD, ngayHH, soLuot, dangApDung
                );
                int check= JOptionPane.showConfirmDialog(this,"Xác nhận thêm?","Xác nhận",JOptionPane.YES_NO_OPTION);
                if (laChinhSua) {
                    if (maKhuyenMaiBUS.capNhatMaKhuyenMai(duLieuLuu)&& check == JOptionPane.YES_OPTION) {
                    if (maKhuyenMaiBUS.capNhatMaKhuyenMai(duLieuLuu)) {
                        lamMoiDuLieu();
                        dialog.dispose();
                    }}
                } else {
                    if (maKhuyenMaiBUS.themMaKhuyenMai(duLieuLuu)&& check == JOptionPane.YES_OPTION) {
                        lamMoiDuLieu();
                        dialog.dispose();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Không thể lưu mã khuyến mãi", JOptionPane.WARNING_MESSAGE);
            }
        });

        chanDialog.add(btnHuy);
        chanDialog.add(btnLuu);

        dialog.add(dauDialog, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(chanDialog, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static Date chuyenSangDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime chuyenSangLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withSecond(0).withNano(0);
    }

    private JSpinner taoSpinnerNgayGio(LocalDateTime giaTriMacDinh) {
        SpinnerDateModel model = new SpinnerDateModel(
                chuyenSangDate(giaTriMacDinh), null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(145, 34));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editor.getTextField().setBorder(new EmptyBorder(4, 6, 4, 6));
        spinner.setEditor(editor);
        return spinner;
    }

    private JPanel taoKhoiNgayGioKemLich(JSpinner spinner, String tieuDe, Window owner) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        p.add(spinner, BorderLayout.CENTER);

        JButton btnLich = new JButton("📅");
        btnLich.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btnLich.setPreferredSize(new Dimension(34, 34));
        btnLich.setFocusPainted(false);
        btnLich.setBackground(Color.WHITE);
        btnLich.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true));
        btnLich.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLich.setToolTipText("Chọn ngày từ lịch");
        btnLich.addActionListener(e -> {
            Date d = (Date) spinner.getValue();
            LocalDateTime ldt = chuyenSangLocalDateTime(d);
            ChonNgayLichUtil.KetQuaChonNgay kq = ChonNgayLichUtil.hienThiLichChonNgay(owner, tieuDe, ldt.toLocalDate(), false);
            if (kq.daXacNhan() && kq.ngay() != null) {
                LocalDateTime moi = kq.ngay().atTime(ldt.toLocalTime());
                spinner.setValue(chuyenSangDate(moi));
            }
        });
        p.add(btnLich, BorderLayout.EAST);
        return p;
    }

    private JTextField taoOForm(boolean choPhepNhap) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(240, 34));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(MAU_CHU_CHINH);
        txt.setEditable(choPhepNhap);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private void dinhDangSpinnerSo(JSpinner spinner, String mauDinhDang) {
        spinner.setPreferredSize(new Dimension(130, 34));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, mauDinhDang);
        editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        editor.getTextField().setBorder(new EmptyBorder(4, 8, 4, 8));
        spinner.setEditor(editor);
    }

    private void themDongForm(JPanel form, GridBagConstraints gbc, int dong, String nhan, Component oNhap) {
        gbc.gridy = dong;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MAU_CHU_PHU);
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(oNhap, gbc);
    }

    private JPanel taoHangHaiCot(String nhan1, Component comp1, String nhan2, Component comp2) {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel l1 = new JLabel(nhan1);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(MAU_CHU_PHU);
        l1.setPreferredSize(new Dimension(122, 28));

        JLabel l2 = new JLabel(nhan2);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(MAU_CHU_PHU);

        c.gridx = 0;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 8);
        pnl.add(l1, c);

        c.gridx = 1;
        c.weightx = 0.5;
        c.insets = new Insets(0, 0, 0, 14);
        pnl.add(comp1, c);

        c.gridx = 2;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 8);
        pnl.add(l2, c);

        c.gridx = 3;
        c.weightx = 0.5;
        c.insets = new Insets(0, 0, 0, 0);
        pnl.add(comp2, c);
        return pnl;
    }

    // =========================================================================
    // CÁC THÀNH PHẦN GIAO DIỆN TUỲ CHỈNH THEO ĐÚNG ẢNH MẪU
    // =========================================================================

    private enum LoaiIcon {
        DAU_CONG,
        KINH_LUP,
        LAM_MOI
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

    private static class KhungTimKiemBoTron extends JPanel {
        private final JTextField txt;
        private boolean dangFocus = false;

        KhungTimKiemBoTron() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(340, 38));
            setBorder(new EmptyBorder(0, 14, 0, 14));

            JLabel lblIcon = new JLabel(new BieuTuongNut(LoaiIcon.KINH_LUP, 14, MAU_CHU_MO));
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

    private static class NutVienTrang extends JButton {
        private boolean hover = false;

        NutVienTrang(String vanBan) {
            super(vanBan);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_CHINH);
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
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 36);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f);
            g2.setColor(hover ? new Color(241, 245, 249) : Color.WHITE);
            g2.fill(nen);
            g2.setColor(new Color(216, 224, 236));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutCamChuDao extends JButton {
        private boolean hover = false;

        NutCamChuDao(String vanBan, Icon icon) {
            super(vanBan, icon);
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
            RoundRectangle2D nen = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f);
            g2.setColor(hover ? MAU_CAM_HOVER : MAU_CAM_CHU_DAO);
            g2.fill(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutLamMoiVuong extends JButton {
        private boolean hover = false;

        NutLamMoiVuong() {
            super(new BieuTuongNut(LoaiIcon.LAM_MOI, 16, MAU_XANH_DAM));
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

    private static class BadgeTongSo extends JLabel {
        BadgeTongSo(String vanBan) {
            super(vanBan);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_BADGE_TONG);
            setBorder(new EmptyBorder(5, 14, 5, 14));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 16f, 16f);
            g2.setColor(MAU_NEN_BADGE_TONG);
            g2.fill(pill);
            g2.setColor(MAU_VIEN_BADGE_TONG);
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

            switch (column) {
                case 0 -> {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setBorder(new EmptyBorder(0, 18, 0, 12));
                }
                case 2, 3 -> {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setBorder(new EmptyBorder(0, 12, 0, 24));
                }
                default -> {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                }
            }
            return lbl;
        }
    }

    /**
     * Bộ vẽ ô dữ liệu theo đúng ảnh mẫu:
     * - Mã KM: in đậm xanh đen (#0F2942)
     * - Loại Giảm: căn giữa ("Tiền mặt" / "% Phần trăm")
     * - Mức Giảm: in đậm màu cam (#EA6A1F), căn phải
     * - Đơn Tối Thiểu: căn phải ("200.000 đ")
     * - Ngày Bắt Đầu & Ngày Hết Hạn: căn giữa ("dd/MM/yyyy HH:mm")
     * - Lượt Dùng: in đậm căn giữa (đỏ nếu = 0)
     * - Trạng Thái: "• Đang áp dụng" xanh lá đậm / "• Ngừng áp dụng" hoặc "• Đã hết hạn" đỏ đậm
     */
    private static class BoVeOMaKhuyenMai extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(MAU_CHU_CHINH);

            switch (column) {
                case 0 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(MAU_XANH_DAM);
                    setBorder(new EmptyBorder(0, 18, 0, 12));
                }
                case 1 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                case 2 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(MAU_CAM_CHU_DAO);
                    setBorder(new EmptyBorder(0, 12, 0, 24));
                }
                case 3 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 12, 0, 24));
                }
                case 4, 5 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(MAU_CHU_NGAY);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                case 6 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    if (value instanceof Number n && n.intValue() == 0) {
                        setForeground(MAU_DO_CANH_BAO);
                    } else {
                        setForeground(MAU_XANH_DAM);
                    }
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                case 7 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    String chuoi = value != null ? value.toString() : "";
                    setForeground(chuoi.contains("Đang áp dụng") ? MAU_XANH_LA : MAU_DO_CANH_BAO);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
            }
            return this;
        }
    }

    private static class BieuTuongNut implements Icon {
        private final LoaiIcon loai;
        private final int kichThuoc;
        private final Color mau;

        BieuTuongNut(LoaiIcon loai, int kichThuoc, Color mau) {
            this.loai = loai;
            this.kichThuoc = kichThuoc;
            this.mau = mau;
        }

        private String layTenFileIcon(LoaiIcon loai) {
            return switch (loai) {
                case DAU_CONG -> "plus.png";
                case KINH_LUP -> "search.png";
                case LAM_MOI -> "refresh.png";
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
     * Cho phép chạy kiểm thử trực tiếp màn hình này mà vẫn giữ nguyên thanh Header của Quản lý.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyManHinhChinh khungChinh = new QuanLyManHinhChinh();
            khungChinh.chuyenManHinh(QuanLyManHinhChinh.MAN_KHUYEN_MAI);
            khungChinh.setVisible(true);
        });
    }
}
