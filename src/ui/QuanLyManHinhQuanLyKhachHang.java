package ui;

import bus.KhachHangBUS;
import bus.PhuongTienBUS;
import entity.KhachHang;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import java.util.List;
import java.util.Locale;

public class QuanLyManHinhQuanLyKhachHang extends JPanel {

    // Bảng màu đồng bộ theo ảnh thiết kế mẫu
    private static final Color MAU_NEN_TRANG = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_VIEN_INPUT = new Color(209, 218, 230);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(26, 58, 92);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_NHAN_NHO = new Color(100, 116, 139);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_XANH_LUU = new Color(16, 158, 101);
    private static final Color MAU_XANH_LUU_HOVER = new Color(13, 138, 88);
    private static final Color MAU_NEN_BADGE = new Color(239, 246, 255);
    private static final Color MAU_VIEN_BADGE = new Color(219, 234, 254);
    private static final Color MAU_CHU_BADGE = new Color(30, 58, 138);
    private static final Color MAU_CHON_DONG = new Color(255, 241, 230);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo Mã KH, Họ tên, Số điện thoại, Địa chỉ...";

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final PhuongTienBUS phuongTienBUS = new PhuongTienBUS();

    private List<KhachHang> danhSachGoc = new ArrayList<>();
    private final List<KhachHang> danhSachHienThi = new ArrayList<>();
    private KhachHang khachHangDangChon = null;

    // Thành phần đầu trang & bộ lọc
    private BadgeVienXanh lblBadgeTongDauTrang;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLocGioiTinh;
    private boolean dangCapNhatBoLoc = false;

    // Thành phần bảng danh sách bên trái
    private JLabel lblHienThiSoKhachHang;
    private DefaultTableModel moHinhBangKH;
    private JTable bangKhachHang;
    private boolean dangDongBoBang = false;

    // Thành phần hồ sơ khách hàng bên phải
    private JLabel lblTenKhachHangTieuDe;
    private JTextField txtMaKhachHang;
    private JComboBox<String> cboGioiTinhForm;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JTextField txtDiaChi;
    private NutVienTrang btnLamMoiForm;
    private NutCapNhat btnCapNhat;
    private NutCamThem btnThemKhachHang;

    public QuanLyManHinhQuanLyKhachHang() {
        khoiTaoGiaoDien();
        lamMoiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN_TRANG);
        setBorder(new EmptyBorder(16, 24, 20, 24));

        // Khu vực phía trên: Hàng tiêu đề + Thanh tìm kiếm & bộ lọc giới tính
        JPanel khuVucTren = new JPanel(new BorderLayout(0, 12));
        khuVucTren.setOpaque(false);
        khuVucTren.add(taoHangTieuDe(), BorderLayout.NORTH);
        khuVucTren.add(taoTheTimKiemVaBoLoc(), BorderLayout.CENTER);

        // Khu vực trung tâm chia 2 cột: Trái là bảng hồ sơ KH, Phải là form chi tiết KH
        JPanel khuVucChinh = new JPanel(new BorderLayout(16, 0));
        khuVucChinh.setOpaque(false);
        khuVucChinh.add(taoTheBangKhachHang(), BorderLayout.CENTER);
        khuVucChinh.add(taoTheChiTietKhachHang(), BorderLayout.EAST);

        add(khuVucTren, BorderLayout.NORTH);
        add(khuVucChinh, BorderLayout.CENTER);
    }

    private JPanel taoHangTieuDe() {
        JPanel hang = new JPanel(new BorderLayout());
        hang.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Quản Lý Khách Hàng");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumPhai.setOpaque(false);

        lblBadgeTongDauTrang = new BadgeVienXanh("0", MAU_XANH_DAM);

        cumPhai.add(lblBadgeTongDauTrang);

        hang.add(lblTieuDe, BorderLayout.WEST);
        hang.add(cumPhai, BorderLayout.EAST);
        return hang;
    }

    private JPanel taoTheTimKiemVaBoLoc() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new GridBagLayout());
        the.setBorder(new EmptyBorder(11, 14, 11, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);

        // 1. Ô tìm kiếm rộng co giãn bên trái
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

        // 2. Combobox lọc Giới tính
        cboLocGioiTinh = taoComboBoLoc(new String[]{
                "Giới tính", "Nam", "Nữ", "Khác"
        }, 148);
        cboLocGioiTinh.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboLocGioiTinh, gbc);

        // 3. Nút vuông đặt lại bộ lọc (↻)
        NutLamMoiVuong btnDatLai = new NutLamMoiVuong();
        btnDatLai.setToolTipText("Đặt lại bộ lọc và tải lại dữ liệu");
        btnDatLai.addActionListener(e -> datLaiBoLocVaTaiLai());
        gbc.gridx = 2;
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

    private JPanel taoTheBangKhachHang() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Thanh đầu thẻ: "Danh Sách Hồ Sơ Khách Hàng" + "Hiển thị X khách hàng" + Nút "Xem Phương Tiện Sở Hữu"
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(10, 18, 10, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Hồ Sơ Khách Hàng");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        JPanel cumPhaiBang = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        cumPhaiBang.setOpaque(false);

        lblHienThiSoKhachHang = new JLabel("Hiển thị 0 khách hàng");
        lblHienThiSoKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHienThiSoKhachHang.setForeground(MAU_CHU_NHAN_NHO);

        cumPhaiBang.add(lblHienThiSoKhachHang);

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(cumPhaiBang, BorderLayout.EAST);

        // Mô hình 6 cột chuẩn theo ảnh mẫu
        String[] tenCot = {
                "STT",
                "Mã KH",
                "Họ Và Tên",
                "Số Điện Thoại",
                "Giới Tính",
                "Địa Chỉ"
        };

        moHinhBangKH = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangKhachHang = new JTable(moHinhBangKH);
        bangKhachHang.setRowHeight(41);
        bangKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangKhachHang.setForeground(MAU_CHU_CHINH);
        bangKhachHang.setBackground(Color.WHITE);
        bangKhachHang.setSelectionBackground(MAU_CHON_DONG);
        bangKhachHang.setSelectionForeground(MAU_XANH_DAM);
        bangKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangKhachHang.setShowVerticalLines(false);
        bangKhachHang.setShowHorizontalLines(true);
        bangKhachHang.setGridColor(MAU_KE_BANG);
        bangKhachHang.setIntercellSpacing(new Dimension(0, 1));
        bangKhachHang.setFillsViewportHeight(true);

        JTableHeader header = bangKhachHang.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 39));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel cotModel = bangKhachHang.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(48);
        cotModel.getColumn(1).setPreferredWidth(115);
        cotModel.getColumn(2).setPreferredWidth(175);
        cotModel.getColumn(3).setPreferredWidth(125);
        cotModel.getColumn(4).setPreferredWidth(80);
        cotModel.getColumn(5).setPreferredWidth(250);

        BoVeOKhachHang rendererO = new BoVeOKhachHang();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            cotModel.getColumn(i).setCellRenderer(rendererO);
        }

        bangKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !dangDongBoBang) {
                int dong = bangKhachHang.getSelectedRow();
                if (dong >= 0 && dong < danhSachHienThi.size()) {
                    hienThiChiTietKhachHang(danhSachHienThi.get(dong));
                }
            }
        });

        // Menu chuột phải hỗ trợ xem danh sách xe hoặc xóa nhanh hồ sơ khách hàng
        JPopupMenu menuNguCanh = new JPopupMenu();

        JMenuItem mnuXemXe = new JMenuItem("Xem danh sách phương tiện sở hữu");
        mnuXemXe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mnuXemXe.addActionListener(e -> moModalDanhSachPhuongTienSoHuu());

        JMenuItem mnuLamMoiForm = new JMenuItem("Làm mới form (Thêm khách hàng mới)");
        mnuLamMoiForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuLamMoiForm.addActionListener(e -> lamMoiForm());

        JMenuItem mnuXoaKH = new JMenuItem("Xóa hồ sơ khách hàng");
        mnuXoaKH.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuXoaKH.setForeground(MAU_DO_CANH_BAO);
        mnuXoaKH.addActionListener(e -> xuLyXoaKhachHangDangChon());

        menuNguCanh.add(mnuXemXe);
        menuNguCanh.addSeparator();
        menuNguCanh.add(mnuLamMoiForm);
        menuNguCanh.addSeparator();
        menuNguCanh.add(mnuXoaKH);

        bangKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    moModalDanhSachPhuongTienSoHuu();
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
                    int dong = bangKhachHang.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < bangKhachHang.getRowCount()) {
                        bangKhachHang.setRowSelectionInterval(dong, dong);
                        menuNguCanh.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangKhachHang);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    /**
     * Cột bên phải: Form thông tin chi tiết khách hàng.
     */
    private JPanel taoTheChiTietKhachHang() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout(0, 10));
        the.setPreferredSize(new Dimension(415, 0));
        the.setBorder(new EmptyBorder(16, 18, 18, 18));

        JPanel khoiForm = new JPanel(new GridBagLayout());
        khoiForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        int dong = 0;

        // Hàng đầu thẻ bên phải: Tên khách hàng bên trái (đã bỏ tag mã KH ở góc phải theo yêu cầu)
        JPanel dauForm = new JPanel(new BorderLayout(8, 0));
        dauForm.setOpaque(false);

        lblTenKhachHangTieuDe = new JLabel("Thêm Khách Hàng Mới");
        lblTenKhachHangTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTenKhachHangTieuDe.setForeground(MAU_XANH_DAM);

        dauForm.add(lblTenKhachHangTieuDe, BorderLayout.WEST);

        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 14, 0);
        khoiForm.add(dauForm, gbc);

        // Dòng 1: Mã khách hàng (bên trái) + Giới tính (bên phải)
        txtMaKhachHang = taoOForm(false);
        txtMaKhachHang.setBackground(new Color(248, 250, 252));
        txtMaKhachHang.setForeground(MAU_CHU_MO);

        cboGioiTinhForm = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cboGioiTinhForm.setPreferredSize(new Dimension(115, 34));
        cboGioiTinhForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboGioiTinhForm.setBackground(Color.WHITE);
        cboGioiTinhForm.setBorder(BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true));

        JPanel hangMaVaGioiTinh = new JPanel(new GridBagLayout());
        hangMaVaGioiTinh.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel cotMa = taoKhoiNhanVaO("Mã khách hàng:", txtMaKhachHang);
        JPanel cotGT = taoKhoiNhanVaO("Giới tính:", cboGioiTinhForm);

        c.gridx = 0;
        c.weightx = 0.66;
        c.insets = new Insets(0, 0, 0, 10);
        hangMaVaGioiTinh.add(cotMa, c);

        c.gridx = 1;
        c.weightx = 0.34;
        c.insets = new Insets(0, 0, 0, 0);
        hangMaVaGioiTinh.add(cotGT, c);

        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        khoiForm.add(hangMaVaGioiTinh, gbc);

        // Dòng 2: Họ và tên (*)
        txtHoTen = taoOForm(true);
        txtHoTen.getDocument().addDocumentListener(new DocumentListener() {
            private void dongBoTieuDe() {
                if (khachHangDangChon == null) {
                    String t = txtHoTen.getText() != null ? txtHoTen.getText().trim() : "";
                    lblTenKhachHangTieuDe.setText(t.isEmpty() ? "Thêm Khách Hàng Mới" : t);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                dongBoTieuDe();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                dongBoTieuDe();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                dongBoTieuDe();
            }
        });
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        khoiForm.add(taoKhoiNhanVaO("Họ và tên (*):", txtHoTen), gbc);

        // Dòng 3: Số điện thoại (*)
        txtSoDienThoai = taoOForm(true);
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        khoiForm.add(taoKhoiNhanVaO("Số điện thoại (*):", txtSoDienThoai), gbc);

        // Dòng 4: Địa chỉ
        txtDiaChi = taoOForm(true);
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 14, 0);
        khoiForm.add(taoKhoiNhanVaO("Địa chỉ:", txtDiaChi), gbc);

        // Dòng 5: Cụm nút "Làm mới form", "Cập nhật" & "Thêm" (màu cam)
        JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hangNut.setOpaque(false);

        btnLamMoiForm = new NutVienTrang("Làm mới form");
        btnLamMoiForm.addActionListener(e -> lamMoiForm());

        btnCapNhat = new NutCapNhat(
                "Cập nhật",
                IconUtil.layIcon("save.png", 14, 14, Color.WHITE)
        );
        btnCapNhat.addActionListener(e -> xuLyCapNhatKhachHang());

        btnThemKhachHang = new NutCamThem(
                "Thêm",
                IconUtil.layIcon("plus.png", 14, 14, Color.WHITE)
        );
        btnThemKhachHang.addActionListener(e -> xuLyThemKhachHang());

        hangNut.add(btnLamMoiForm);
        hangNut.add(btnCapNhat);
        hangNut.add(btnThemKhachHang);

        gbc.gridy = dong++;
        gbc.insets = new Insets(4, 0, 10, 0);
        khoiForm.add(hangNut, gbc);

        // Dòng 6: Nút "Xem Phương Tiện Sở Hữu" đặt ở phía dưới form hiển thị thông tin khách hàng
        NutXanhDam btnXemPhuongTienSoHuu = new NutXanhDam(
                "Xem Phương Tiện Sở Hữu",
                IconUtil.layIcon("car.png", 16, 16, Color.WHITE)
        );
        btnXemPhuongTienSoHuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXemPhuongTienSoHuu.setPreferredSize(new Dimension(0, 38));
        btnXemPhuongTienSoHuu.addActionListener(e -> moModalDanhSachPhuongTienSoHuu());

        JPanel hangNutXemXe = new JPanel(new BorderLayout());
        hangNutXemXe.setOpaque(false);
        hangNutXemXe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN_THE),
                new EmptyBorder(14, 0, 4, 0)
        ));
        hangNutXemXe.add(btnXemPhuongTienSoHuu, BorderLayout.CENTER);

        gbc.gridy = dong;
        gbc.insets = new Insets(8, 0, 4, 0);
        khoiForm.add(hangNutXemXe, gbc);

        the.add(khoiForm, BorderLayout.NORTH);
        return the;
    }

    private JPanel taoKhoiNhanVaO(String nhan, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(MAU_CHU_NHAN_NHO);
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JTextField taoOForm(boolean choPhepNhap) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(0, 34));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(MAU_CHU_CHINH);
        txt.setEditable(choPhepNhap);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_INPUT, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    // =========================================================================
    // NGHIỆP VỤ TẢI DỮ LIỆU, LỌC, LƯU HỒ SƠ & HIỂN THỊ PHƯƠNG TIỆN SỞ HỮU
    // =========================================================================

    /**
     * Tải danh sách khách hàng đăng ký từ CSDL qua KhachHangBUS (không dùng dữ liệu giả).
     */
    public void lamMoiDuLieu() {
        String maDangChonCu = khachHangDangChon != null ? khachHangDangChon.getMaKhachHang() : null;
        try {
            danhSachGoc = khachHangBUS.layDanhSachKhachDangKy();
            danhSachGoc.sort(Comparator.comparing(KhachHang::getMaKhachHang).reversed());
        } catch (Exception ex) {
            danhSachGoc = new ArrayList<>();
        }

        lblBadgeTongDauTrang.setText(String.valueOf(danhSachGoc.size()));
        apDungBoLoc();

        if (!danhSachHienThi.isEmpty()) {
            int viTri = 0;
            if (maDangChonCu != null) {
                for (int i = 0; i < danhSachHienThi.size(); i++) {
                    if (maDangChonCu.equals(danhSachHienThi.get(i).getMaKhachHang())) {
                        viTri = i;
                        break;
                    }
                }
            }
            bangKhachHang.setRowSelectionInterval(viTri, viTri);
            hienThiChiTietKhachHang(danhSachHienThi.get(viTri));
        } else {
            lamMoiForm();
        }
    }

    private void datLaiBoLocVaTaiLai() {
        dangCapNhatBoLoc = true;
        try {
            txtTimKiem.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiem.setForeground(MAU_CHU_MO);
            cboLocGioiTinh.setSelectedIndex(0);
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
        int chiSoGioiTinh = cboLocGioiTinh.getSelectedIndex();
        String gioiTinhLoc = (String) cboLocGioiTinh.getSelectedItem();

        dangDongBoBang = true;
        try {
            danhSachHienThi.clear();
            moHinhBangKH.setRowCount(0);

            int stt = 1;
            for (KhachHang kh : danhSachGoc) {
                String ma = kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "";
                String ten = kh.getHoTen() != null ? kh.getHoTen() : "";
                String sdt = kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "";
                String gt = kh.getGioiTinh() != null ? kh.getGioiTinh() : "";
                String dc = kh.getDiaChi() != null ? kh.getDiaChi() : "";

                if (!tuKhoa.isEmpty()) {
                    boolean khop = ma.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || ten.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || sdt.toLowerCase(Locale.ROOT).contains(tuKhoa)
                            || dc.toLowerCase(Locale.ROOT).contains(tuKhoa);
                    if (!khop) {
                        continue;
                    }
                }

                if (chiSoGioiTinh > 0 && gioiTinhLoc != null) {
                    if (!gt.equalsIgnoreCase(gioiTinhLoc)) {
                        continue;
                    }
                }

                danhSachHienThi.add(kh);
                moHinhBangKH.addRow(new Object[]{
                        stt++,
                        ma,
                        ten,
                        sdt,
                        gt.isEmpty() ? "-" : gt,
                        dc
                });
            }
            lblHienThiSoKhachHang.setText("Hiển thị " + danhSachHienThi.size() + " khách hàng");
        } finally {
            dangDongBoBang = false;
        }
    }

    private void hienThiChiTietKhachHang(KhachHang kh) {
        if (kh == null) {
            lamMoiForm();
            return;
        }
        this.khachHangDangChon = kh;
        lblTenKhachHangTieuDe.setText(kh.getHoTen() != null ? kh.getHoTen() : "Hồ Sơ Khách Hàng");

        txtMaKhachHang.setText(kh.getMaKhachHang());
        txtHoTen.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
        txtSoDienThoai.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
        txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");

        String gt = kh.getGioiTinh();
        if ("Nữ".equalsIgnoreCase(gt)) {
            cboGioiTinhForm.setSelectedItem("Nữ");
        } else if ("Khác".equalsIgnoreCase(gt)) {
            cboGioiTinhForm.setSelectedItem("Khác");
        } else {
            cboGioiTinhForm.setSelectedItem("Nam");
        }

        // Bấm xem chi tiết: nút thêm màu cam mờ xuống, nút cập nhật sáng rõ
        if (btnThemKhachHang != null) {
            btnThemKhachHang.setEnabled(false);
        }
        if (btnCapNhat != null) {
            btnCapNhat.setEnabled(true);
        }
    }

    private void moModalDanhSachPhuongTienSoHuu() {
        if (khachHangDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một khách hàng trong danh sách để xem danh sách phương tiện sở hữu!",
                    "Chưa chọn khách hàng", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien modal =
                new QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien(cuaSoCha, khachHangDangChon, khachHangBUS, phuongTienBUS);
        modal.setVisible(true);
        if (modal.isCoThayDoi()) {
            lamMoiDuLieu();
        }
    }

    private void lamMoiForm() {
        dangDongBoBang = true;
        try {
            bangKhachHang.clearSelection();
        } finally {
            dangDongBoBang = false;
        }
        this.khachHangDangChon = null;

        String maDuKien = "KH000000001";
        try {
            maDuKien = khachHangBUS.phatSinhMaKhachHang();
        } catch (Exception ignored) {
        }

        lblTenKhachHangTieuDe.setText("Thêm Khách Hàng Mới");
        txtMaKhachHang.setText(maDuKien);
        cboGioiTinhForm.setSelectedIndex(0);
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtDiaChi.setText("");

        // Form trống: nút thêm màu cam hiển thị rõ để có thể điền thông tin và thêm, nút cập nhật mờ xuống
        if (btnThemKhachHang != null) {
            btnThemKhachHang.setEnabled(true);
        }
        if (btnCapNhat != null) {
            btnCapNhat.setEnabled(false);
        }

        txtHoTen.requestFocus();
    }

    private void xuLyThemKhachHang() {
        try {
            String hoTen = txtHoTen.getText() != null ? txtHoTen.getText().trim() : "";
            String sdt = txtSoDienThoai.getText() != null ? txtSoDienThoai.getText().trim() : "";
            String diaChi = txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "";
            String gioiTinh = (String) cboGioiTinhForm.getSelectedItem();

            if (khachHangDangChon == null) {
                KhachHang khMoi = new KhachHang();
                khMoi.setHoTen(hoTen);
                khMoi.setSoDienThoai(sdt);
                khMoi.setDiaChi(diaChi);
                khMoi.setGioiTinh(gioiTinh);

                if (khachHangBUS.themKhachHang(khMoi)) {
                    lamMoiDuLieu();
                    lamMoiForm();
                    JOptionPane.showMessageDialog(this,
                            "Đã thêm hồ sơ khách hàng [" + khMoi.getHoTen() + "] thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                khachHangDangChon.setHoTen(hoTen);
                khachHangDangChon.setSoDienThoai(sdt);
                khachHangDangChon.setDiaChi(diaChi);
                khachHangDangChon.setGioiTinh(gioiTinh);

                if (khachHangBUS.capNhatKhachHang(khachHangDangChon)) {
                    lamMoiDuLieu();
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật thông tin khách hàng [" + hoTen + "] thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            if (hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtHoTen.requestFocus();
                return;
            }
            if (sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtSoDienThoai.requestFocus();
                return;
            }

            KhachHang khMoi = new KhachHang();
            khMoi.setHoTen(hoTen);
            khMoi.setSoDienThoai(sdt);
            khMoi.setDiaChi(diaChi);
            khMoi.setGioiTinh(gioiTinh);

            if (khachHangBUS.themKhachHang(khMoi)) {
                this.khachHangDangChon = khMoi;
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this,
                        "Đã thêm hồ sơ khách hàng [" + khMoi.getHoTen() + "] thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể thêm khách hàng", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void xuLyCapNhatKhachHang() {
        if (khachHangDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật từ danh sách!", "Chưa chọn khách hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String hoTen = txtHoTen.getText() != null ? txtHoTen.getText().trim() : "";
            String sdt = txtSoDienThoai.getText() != null ? txtSoDienThoai.getText().trim() : "";
            String diaChi = txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "";
            String gioiTinh = (String) cboGioiTinhForm.getSelectedItem();

            if (hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ và tên không được để trống!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtHoTen.requestFocus();
                return;
            }
            if (sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtSoDienThoai.requestFocus();
                return;
            }

            khachHangDangChon.setHoTen(hoTen);
            khachHangDangChon.setSoDienThoai(sdt);
            khachHangDangChon.setDiaChi(diaChi);
            khachHangDangChon.setGioiTinh(gioiTinh);

            if (khachHangBUS.capNhatKhachHang(khachHangDangChon)) {
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this,
                        "Đã cập nhật thông tin khách hàng [" + hoTen + "] thành công!",
                        "Cập nhật thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể cập nhật khách hàng", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void xuLyXoaKhachHangDangChon() {
        if (khachHangDangChon == null) {
            return;
        }
        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa hồ sơ khách hàng [" + khachHangDangChon.getHoTen()
                        + " - " + khachHangDangChon.getMaKhachHang() + "] không?",
                "Xác nhận xóa khách hàng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (khachHangBUS.xoaKhachHang(khachHangDangChon.getMaKhachHang())) {
                khachHangDangChon = null;
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa khách hàng đã có phương tiện hoặc hóa đơn lưu trữ.\nChi tiết: " + ex.getMessage(),
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =========================================================================
    // CÁC COMPONENT ĐỒ HOẠ TUỲ CHỈNH THEO ĐÚNG THIẾT KẾ MẪU
    // =========================================================================

    private enum LoaiIcon {
        LAM_MOI,
        KINH_LUP,
        LUU_DIA,
        O_TO
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

    private static class KhungTimKiemBoGoc extends JPanel {
        private final JTextField txt;
        private boolean dangFocus = false;

        KhungTimKiemBoGoc() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(340, 38));
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

    private static class NutCamChuDao extends JButton {
        private boolean hover = false;

        NutCamChuDao(String chu, Icon icon) {
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
            g2.setColor(hover ? new Color(249, 115, 22) : MAU_CAM_CHU_DAO);
            g2.fill(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10f, 10f));
            g2.dispose();
            super.paintComponent(g);
        }
    }

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
            if (isPreferredSizeSet()) {
                return super.getPreferredSize();
            }
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 38);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? MAU_XANH_DAM_HOVER : MAU_XANH_DAM);
            g2.fill(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 16f, 16f));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutVienTrang extends JButton {
        private boolean hover = false;

        NutVienTrang(String chu) {
            this(chu, null);
        }

        NutVienTrang(String chu, Icon icon) {
            super(chu, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_PHU);
            setIconTextGap(7);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));

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
            return new Dimension(d.width, 33);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D nen = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f);
            g2.setColor(hover ? new Color(241, 245, 249) : new Color(248, 250, 252));
            g2.fill(nen);
            g2.setColor(MAU_VIEN_INPUT);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(nen);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nút "Cập Nhật" (xanh ngọc).
     * Khi xem chi tiết: sáng rõ để lưu sửa đổi.
     * Khi form trống: mờ xuống (disabled).
     */
    private static class NutCapNhat extends JButton {
        private boolean hover = false;

        NutCapNhat(String chu, Icon icon) {
            super(chu, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setIconTextGap(6);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        hover = true;
                        repaint();
                    }
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
            return new Dimension(d.width, 33);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isEnabled()) {
                g2.setColor(hover ? MAU_XANH_LUU_HOVER : MAU_XANH_LUU);
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                g2.setColor(new Color(226, 232, 240));
                setForeground(new Color(148, 163, 184));
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            g2.fill(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Nút màu cam "Thêm" khách hàng.
     * Khi xem chi tiết (chọn dòng): nút mờ xuống (disabled).
     * Khi form trống (thêm mới): nút màu cam hiển thị rõ ràng (enabled).
     */
    private static class NutCamThem extends JButton {
        private boolean hover = false;

        NutCamThem(String chu, Icon icon) {
            super(chu, icon);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setIconTextGap(6);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 16, 7, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        hover = true;
                        repaint();
                    }
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
            return new Dimension(d.width, 33);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isEnabled()) {
                g2.setColor(hover ? new Color(244, 124, 48) : MAU_CAM_CHU_DAO);
                setForeground(Color.WHITE);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                // Mờ xuống khi xem chi tiết khách hàng
                g2.setColor(new Color(253, 186, 116, 130));
                setForeground(new Color(255, 255, 255, 160));
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            g2.fill(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8f, 8f));
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
            super(vanBan, SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(mauChu);
            setBorder(new EmptyBorder(5, 13, 5, 13));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 16f, 16f);
            g2.setColor(MAU_NEN_BADGE);
            g2.fill(pill);
            g2.setColor(MAU_VIEN_BADGE);
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
            lbl.setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(0, column == 0 ? 6 : 10, 0, 8));
            return lbl;
        }
    }

    /**
     * Khi dòng khách hàng được chọn: tô nền cam đào nhạt (#FFF1E6) và in đậm toàn bộ chữ như ảnh mẫu.
     */
    private static class BoVeOKhachHang extends DefaultTableCellRenderer {
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
            setToolTipText(chuoi.length() > 24 ? chuoi : null);
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
                case LAM_MOI -> "refresh.png";
                case KINH_LUP -> "search.png";
                case LUU_DIA -> "save.png";
                case O_TO -> "car.png";
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
            khungChinh.chuyenManHinh(QuanLyManHinhChinh.MAN_KHACH_HANG);
            khungChinh.setVisible(true);
        });
    }
}
