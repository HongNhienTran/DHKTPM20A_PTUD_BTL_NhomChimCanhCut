package ui;

import bus.DichVuBUS;
import entity.DichVu;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuanLyManHinhQuanLyDichVu extends JPanel {

    // Bảng màu đồng bộ theo thiết kế mẫu
    private static final Color MAU_NEN_TRANG = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
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

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo Mã DV, Tên dịch vụ, Mô tả...";

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private final DecimalFormat dinhDangTien;

    private List<DichVu> danhSachGoc = new ArrayList<>();
    private final List<DichVu> danhSachHienThi = new ArrayList<>();

    private JTextField txtTimKiem;
    private JComboBox<String> cboTrangThai;
    private BadgeTongSo lblBadgeTongSo;
    private DefaultTableModel moHinhBang;
    private JTable bangDichVu;
    private boolean dangCapNhatBoLoc = false;

    public QuanLyManHinhQuanLyDichVu() {
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

        // Khu vực phía trên: Tiêu đề + nút Thêm Dịch Vụ Mới, bên dưới là thanh tìm kiếm & bộ lọc
        JPanel khuVucTren = new JPanel(new BorderLayout(0, 14));
        khuVucTren.setOpaque(false);
        khuVucTren.add(taoHangTieuDeVaNutThem(), BorderLayout.NORTH);
        khuVucTren.add(taoTheTimKiemVaBoLoc(), BorderLayout.CENTER);

        // Khu vực trung tâm: Thẻ bảng Danh Sách Các Dịch Vụ Kỹ Thuật
        JPanel theBang = taoTheBangDichVu();

        add(khuVucTren, BorderLayout.NORTH);
        add(theBang, BorderLayout.CENTER);
    }

    private JPanel taoHangTieuDeVaNutThem() {
        JPanel hang = new JPanel(new BorderLayout());
        hang.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Dịch Vụ Sửa Chữa & Bảo Dưỡng");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel cumNutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cumNutPhai.setOpaque(false);

        NutCamChuDao btnThemMoi = new NutCamChuDao(
                "Thêm Dịch Vụ Mới",
                new BieuTuongNut(LoaiIcon.DAU_CONG, 14, Color.WHITE)
        );
        btnThemMoi.addActionListener(e -> moHopThoaiDichVu(null));
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

        // 2. Combobox Trạng thái
        cboTrangThai = taoComboBoLoc(new String[]{
                "Trạng thái",
                "Đang áp dụng",
                "Ngừng áp dụng"
        }, 156);
        cboTrangThai.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboTrangThai, gbc);

        // 3. Nút làm mới (↻) ở ngoài cùng bên phải
        NutLamMoiVuong btnLamMoi = new NutLamMoiVuong();
        btnLamMoi.setToolTipText("Đặt lại bộ lọc và tải lại dữ liệu");
        btnLamMoi.addActionListener(e -> datLaiBoLocVaTaiLai());
        gbc.gridx = 2;
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

    private JPanel taoTheBangDichVu() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Thanh đầu thẻ: Tiêu đề bên trái + Badge "Tổng số: X dịch vụ" bên phải
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(14, 18, 12, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Các Dịch Vụ Kỹ Thuật");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblBadgeTongSo = new BadgeTongSo("Tổng số: 0 dịch vụ");

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblBadgeTongSo, BorderLayout.EAST);

        // Mô hình 6 cột chuẩn theo ảnh mẫu
        String[] tenCot = {
                "Mã DV",
                "Tên Dịch Vụ",
                "Mô Tả Kỹ Thuật",
                "Đơn Giá",
                "VAT",
                "Trạng Thái"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangDichVu = new JTable(moHinhBang);
        bangDichVu.setRowHeight(41);
        bangDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangDichVu.setForeground(MAU_CHU_CHINH);
        bangDichVu.setBackground(Color.WHITE);
        bangDichVu.setSelectionBackground(MAU_CHON_DONG);
        bangDichVu.setSelectionForeground(MAU_CHU_CHINH);
        bangDichVu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangDichVu.setShowVerticalLines(false);
        bangDichVu.setShowHorizontalLines(true);
        bangDichVu.setGridColor(MAU_KE_BANG);
        bangDichVu.setIntercellSpacing(new Dimension(0, 1));
        bangDichVu.setFillsViewportHeight(true);

        JTableHeader header = bangDichVu.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel cotModel = bangDichVu.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(105);
        cotModel.getColumn(1).setPreferredWidth(250);
        cotModel.getColumn(2).setPreferredWidth(410);
        cotModel.getColumn(3).setPreferredWidth(145);
        cotModel.getColumn(4).setPreferredWidth(95);
        cotModel.getColumn(5).setPreferredWidth(145);

        BoVeODichVu rendererO = new BoVeODichVu();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            cotModel.getColumn(i).setCellRenderer(rendererO);
        }

        // Nhấp đúp hoặc menu chuột phải để chỉnh sửa / đổi trạng thái / xoá dịch vụ
        JPopupMenu menuNguCanh = taoMenuNguCanhBang();
        bangDichVu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    DichVu dv = layDichVuDangChon();
                    if (dv != null) {
                        moHopThoaiDichVu(dv);
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
                    int dong = bangDichVu.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < bangDichVu.getRowCount()) {
                        bangDichVu.setRowSelectionInterval(dong, dong);
                        menuNguCanh.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangDichVu);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    private JPopupMenu taoMenuNguCanhBang() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem mnuSua = new JMenuItem("Chỉnh sửa dịch vụ kỹ thuật");
        mnuSua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuSua.addActionListener(e -> {
            DichVu dv = layDichVuDangChon();
            if (dv != null) {
                moHopThoaiDichVu(dv);
            }
        });

        JMenuItem mnuDoiTrangThai = new JMenuItem("Đổi trạng thái Đang áp dụng / Ngừng áp dụng");
        mnuDoiTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuDoiTrangThai.addActionListener(e -> xuLyDoiTrangThaiNhanh());

        JMenuItem mnuXoa = new JMenuItem("Xóa dịch vụ");
        mnuXoa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuXoa.setForeground(MAU_DO_CANH_BAO);
        mnuXoa.addActionListener(e -> xuLyXoaDichVu());

        menu.add(mnuSua);
        menu.add(mnuDoiTrangThai);
        menu.addSeparator();
        menu.add(mnuXoa);
        return menu;
    }

    private DichVu layDichVuDangChon() {
        int dong = bangDichVu.getSelectedRow();
        if (dong < 0 || dong >= danhSachHienThi.size()) {
            return null;
        }
        return danhSachHienThi.get(dong);
    }

    /**
     * Tải danh sách dịch vụ thực tế từ CSDL qua DichVuBUS (không tạo dữ liệu giả).
     */
    public void lamMoiDuLieu() {
        try {
            danhSachGoc = dichVuBUS.layTatCa();
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
        int chiSoTrangThai = cboTrangThai.getSelectedIndex();

        danhSachHienThi.clear();
        moHinhBang.setRowCount(0);

        for (DichVu dv : danhSachGoc) {
            // 1. Lọc theo từ khoá (Mã DV, Tên dịch vụ, Mô tả)
            if (!tuKhoa.isEmpty()) {
                String ma = dv.getMaDichVu() != null ? dv.getMaDichVu().toLowerCase(Locale.ROOT) : "";
                String ten = dv.getTenDichVu() != null ? dv.getTenDichVu().toLowerCase(Locale.ROOT) : "";
                String moTa = dv.getMoTa() != null ? dv.getMoTa().toLowerCase(Locale.ROOT) : "";
                if (!ma.contains(tuKhoa) && !ten.contains(tuKhoa) && !moTa.contains(tuKhoa)) {
                    continue;
                }
            }

            // 2. Lọc theo Trạng thái
            if (chiSoTrangThai == 1 && !dv.isTrangThai()) {
                continue;
            } else if (chiSoTrangThai == 2 && dv.isTrangThai()) {
                continue;
            }

            danhSachHienThi.add(dv);
            moHinhBang.addRow(new Object[]{
                    dv.getMaDichVu(),
                    dv.getTenDichVu(),
                    dv.getMoTa() != null ? dv.getMoTa() : "",
                    dinhDangTien.format(Math.round(dv.getDonGia())) + " đ",
                    String.format(Locale.US, "%.1f%%", dv.getThueVAT()),
                    dv.isTrangThai() ? "\u2022 Đang áp dụng" : "\u2022 Ngừng áp dụng"
            });
        }

        lblBadgeTongSo.setText("Tổng số: " + danhSachHienThi.size() + " dịch vụ");
    }

    private void xuLyDoiTrangThaiNhanh() {
        DichVu dv = layDichVuDangChon();
        if (dv == null) {
            return;
        }
        try {
            dv.setTrangThai(!dv.isTrangThai());
            if (dichVuBUS.capNhatDichVu(dv)) {
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi cập nhật trạng thái", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoaDichVu() {
        DichVu dv = layDichVuDangChon();
        if (dv == null) {
            return;
        }
        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa dịch vụ [" + dv.getMaDichVu() + " - " + dv.getTenDichVu() + "] không?",
                "Xác nhận xóa dịch vụ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (dichVuBUS.xoaDichVu(dv.getMaDichVu())) {
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa dịch vụ này (có thể đang được tham chiếu trong phiếu báo giá/hóa đơn).\n"
                            + "Gợi ý: Bạn có thể chuyển sang trạng thái 'Ngừng áp dụng'.\nChi tiết: " + ex.getMessage(),
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Hộp thoại Thêm mới / Cập nhật Dịch vụ kỹ thuật.
     */
    private void moHopThoaiDichVu(DichVu dvCanSua) {
        boolean laChinhSua = (dvCanSua != null);
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                cuaSoCha,
                laChinhSua ? "Cập Nhật Dịch Vụ Kỹ Thuật" : "Thêm Dịch Vụ Sửa Chữa & Bảo Dưỡng Mới",
                JDialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(750, 600);
        dialog.setMinimumSize(new Dimension(660, 520));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        dialog.setLayout(new BorderLayout());

        // Đầu hộp thoại
        JPanel dauDialog = new JPanel(new BorderLayout());
        dauDialog.setBackground(MAU_XANH_DAM);
        dauDialog.setBorder(new EmptyBorder(15, 22, 15, 22));
        JLabel lblTieuDe = new JLabel(laChinhSua
                ? "CẬP NHẬT DỊCH VỤ (" + dvCanSua.getMaDichVu() + ")"
                : "THÊM DỊCH VỤ KỸ THUẬT MỚI");
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

        JTextField txtMaDV = taoOForm(false);
        txtMaDV.setBackground(new Color(241, 245, 249));
        try {
            txtMaDV.setText(laChinhSua ? dvCanSua.getMaDichVu() : dichVuBUS.phatSinhMaDichVu());
        } catch (Exception ex) {
            txtMaDV.setText("DV001");
        }

        JTextField txtTenDV = taoOForm(true);

        JTextArea txtMoTa = new JTextArea(4, 24);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMoTa.setForeground(MAU_CHU_CHINH);
        txtMoTa.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane cuonMoTa = new JScrollPane(txtMoTa);
        cuonMoTa.setBorder(BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true));
        cuonMoTa.setPreferredSize(new Dimension(380, 110));

        JSpinner spnDonGia = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 10_000.0));
        JSpinner spnThueVAT = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 100.0, 0.5));
        dinhDangSpinnerSo(spnDonGia, "#,##0");
        dinhDangSpinnerSo(spnThueVAT, "#0.0");

        JComboBox<String> cboTrangThaiDV = new JComboBox<>(new String[]{"Đang áp dụng", "Ngừng áp dụng"});
        cboTrangThaiDV.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTrangThaiDV.setPreferredSize(new Dimension(200, 36));

        if (laChinhSua) {
            txtTenDV.setText(dvCanSua.getTenDichVu());
            txtMoTa.setText(dvCanSua.getMoTa() != null ? dvCanSua.getMoTa() : "");
            spnDonGia.setValue(dvCanSua.getDonGia());
            spnThueVAT.setValue(dvCanSua.getThueVAT());
            cboTrangThaiDV.setSelectedIndex(dvCanSua.isTrangThai() ? 0 : 1);
        }

        themDongForm(form, gbc, 0, "Mã dịch vụ:", txtMaDV);
        themDongForm(form, gbc, 1, "Tên dịch vụ (*):", txtTenDV);
        themDongForm(form, gbc, 2, "Mô tả kỹ thuật:", cuonMoTa);

        JPanel hangGiaVat = taoHangHaiCot("Đơn giá (VNĐ):", spnDonGia, "Thuế VAT (%):", spnThueVAT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        form.add(hangGiaVat, gbc);

        gbc.gridwidth = 1;
        themDongForm(form, gbc, 4, "Trạng thái:", cboTrangThaiDV);

        // Chân hộp thoại
        JPanel chanDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        chanDialog.setBackground(new Color(248, 250, 252));
        chanDialog.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN_THE));

        NutVienTrang btnHuy = new NutVienTrang("Hủy bỏ");
        btnHuy.addActionListener(e -> dialog.dispose());

        NutCamChuDao btnLuu = new NutCamChuDao(laChinhSua ? "Lưu Cập Nhật" : "Thêm Dịch Vụ", null);
        btnLuu.addActionListener(e -> {
            try {
                String ten = txtTenDV.getText() != null ? txtTenDV.getText().trim() : "";
                String moTa = txtMoTa.getText() != null ? txtMoTa.getText().trim() : "";
                double donGia = ((Number) spnDonGia.getValue()).doubleValue();
                double vat = ((Number) spnThueVAT.getValue()).doubleValue();
                boolean dangApDung = cboTrangThaiDV.getSelectedIndex() == 0;

                if (laChinhSua) {
                    dvCanSua.setTenDichVu(ten);
                    dvCanSua.setMoTa(moTa);
                    dvCanSua.setDonGia(donGia);
                    dvCanSua.setThueVAT(vat);
                    dvCanSua.setTrangThai(dangApDung);
                    if (dichVuBUS.capNhatDichVu(dvCanSua)) {
                        lamMoiDuLieu();
                        dialog.dispose();
                    }
                } else {
                    DichVu dvMoi = new DichVu();
                    dvMoi.setTenDichVu(ten);
                    dvMoi.setMoTa(moTa);
                    dvMoi.setDonGia(donGia);
                    dvMoi.setThueVAT(vat);
                    dvMoi.setTrangThai(dangApDung);
                    if (dichVuBUS.themDichVu(dvMoi)) {
                        lamMoiDuLieu();
                        dialog.dispose();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Không thể lưu dịch vụ", JOptionPane.WARNING_MESSAGE);
            }
        });

        chanDialog.add(btnHuy);
        chanDialog.add(btnLuu);

        dialog.add(dauDialog, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(chanDialog, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JTextField taoOForm(boolean choPhepNhap) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(380, 36));
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
        spinner.setPreferredSize(new Dimension(170, 36));
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
        l1.setPreferredSize(new Dimension(108, 28));

        JLabel l2 = new JLabel(nhan2);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(MAU_CHU_PHU);

        c.gridx = 0;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 8);
        pnl.add(l1, c);

        c.gridx = 1;
        c.weightx = 0.55;
        c.insets = new Insets(0, 0, 0, 16);
        pnl.add(comp1, c);

        c.gridx = 2;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 8);
        pnl.add(l2, c);

        c.gridx = 3;
        c.weightx = 0.45;
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
            setPreferredSize(new Dimension(360, 38));
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

            if (column == 3) {
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 26));
            } else if (column == 4 || column == 5) {
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(new EmptyBorder(0, column == 0 ? 18 : 12, 0, 12));
            }
            return lbl;
        }
    }

    /**
     * Bộ vẽ ô dữ liệu theo đúng ảnh mẫu:
     * - Cột Mô Tả Kỹ Thuật: hiển thị tooltip đầy đủ khi di chuột vào dòng mô tả dài
     * - Cột Đơn Giá: in đậm màu cam (#EA6A1F)
     * - Cột VAT: căn giữa định dạng 10.0%
     * - Cột Trạng Thái: "• Đang áp dụng" xanh lá đậm / "• Ngừng áp dụng" đỏ đậm
     */
    private static class BoVeODichVu extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(MAU_CHU_CHINH);
            setToolTipText(null);

            switch (column) {
                case 0 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 18, 0, 12));
                }
                case 1 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_CHINH);
                    setBorder(new EmptyBorder(0, 12, 0, 12));
                }
                case 2 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 12, 0, 12));
                    String moTa = value != null ? value.toString() : "";
                    if (!moTa.isEmpty()) {
                        setToolTipText(moTa);
                    }
                }
                case 3 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(MAU_CAM_CHU_DAO);
                    setBorder(new EmptyBorder(0, 12, 0, 26));
                }
                case 4 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                case 5 -> {
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
     * Cho phép chạy trực tiếp màn hình này mà vẫn giữ nguyên đầy đủ thanh Header của Quản lý.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyManHinhChinh khungChinh = new QuanLyManHinhChinh();
            khungChinh.chuyenManHinh(QuanLyManHinhChinh.MAN_DICH_VU);
            khungChinh.setVisible(true);
        });
    }
}
