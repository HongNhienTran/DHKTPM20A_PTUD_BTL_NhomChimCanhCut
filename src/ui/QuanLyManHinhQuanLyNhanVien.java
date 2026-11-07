package ui;

import bus.NhanVienBUS;
import bus.TaiKhoanBUS;
import entity.NhanVien;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.ChuyenMon;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Giao diện Quản Lý Nhân Sự & Tài Khoản - Phân hệ Quản trị cửa hàng.
 * Thiết kế chuẩn theo hình mẫu Pengu Garage: danh sách nhân viên bên trái và
 * form hồ sơ chi tiết, phân quyền, cấp đổi mật khẩu tài khoản bên phải.
 */
public class QuanLyManHinhQuanLyNhanVien extends JPanel {

    // Bảng màu chuẩn thương hiệu Pengu Garage
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

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo Mã NV, Họ tên hoặc Số điện thoại...";
    private static final String TEXT_MA_NV_TU_SINH = "Hệ thống tự sinh theo chức vụ";

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final TaiKhoanBUS taiKhoanBUS = new TaiKhoanBUS();

    private List<NhanVien> danhSachGoc = new ArrayList<>();
    private final List<NhanVien> danhSachHienThi = new ArrayList<>();

    // Các thành phần bộ lọc phía trên
    private JTextField txtTimKiem;
    private JComboBox<String> cboLocChucVu;
    private JComboBox<String> cboLocTrangThai;
    private BadgeTongSo lblBadgeTongSo;

    // Bảng danh sách nhân sự
    private DefaultTableModel moHinhBang;
    private JTable bangNhanVien;
    private boolean dangCapNhatBoLoc = false;

    // Form chi tiết & thao tác bên phải
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JTextField txtDiaChi;
    private JComboBox<String> cboChucVuForm;
    private JComboBox<String> cboChuyenMonForm;
    private JLabel lblChuyenMonForm;

    private JButton btnThemMoi;
    private JButton btnCapNhat;
    private JButton btnKhoaMoKhoa;
    private JButton btnDoiMatKhau;
    private JButton btnXoaTrang;

    private NhanVien nhanVienDangChon = null;

    public QuanLyManHinhQuanLyNhanVien() {
        setBackground(MAU_NEN_TRANG);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Cụm phía trên: Tiêu đề trang + Thanh tìm kiếm và bộ lọc
        JPanel khuVucTren = new JPanel(new BorderLayout(0, 12));
        khuVucTren.setOpaque(false);
        khuVucTren.add(taoHangTieuDeTrang(), BorderLayout.NORTH);
        khuVucTren.add(taoTheTimKiemVaBoLoc(), BorderLayout.CENTER);

        // Cụm phía dưới: Bảng nhân sự (trái) + Form chi tiết thao tác (phải)
        JPanel khuVucChinh = new JPanel(new BorderLayout(14, 0));
        khuVucChinh.setOpaque(false);
        khuVucChinh.add(taoTheBangDanhSach(), BorderLayout.CENTER);
        khuVucChinh.add(taoTheFormThaoTac(), BorderLayout.EAST);

        add(khuVucTren, BorderLayout.NORTH);
        add(khuVucChinh, BorderLayout.CENTER);

        lamMoiDuLieu();
    }

    private JPanel taoHangTieuDeTrang() {
        JPanel hang = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hang.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Nhân Sự & Tài Khoản");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        hang.add(lblTieuDe);
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

        // 1. Ô tìm kiếm bo tròn dạng pill bên trái
        KhungTimKiemBoTron khungTimKiem = new KhungTimKiemBoTron();
        txtTimKiem = khungTimKiem.getOvanBan();
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

        // 2. Bộ lọc Chức vụ
        cboLocChucVu = taoComboBoLoc(new String[]{"Chức vụ", "Quản lý", "Kỹ thuật viên", "Lễ tân"}, 150);
        cboLocChucVu.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboLocChucVu, gbc);

        // 3. Bộ lọc Trạng thái
        cboLocTrangThai = taoComboBoLoc(new String[]{"Trạng thái", "Hoạt động", "Đã khóa"}, 140);
        cboLocTrangThai.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 2;
        the.add(cboLocTrangThai, gbc);

        // 4. Nút làm mới bộ lọc (icon xoay)
        NutLamMoiBoLoc btnLamMoi = new NutLamMoiBoLoc();
        btnLamMoi.addActionListener(e -> datLaiBoLocVaTaiLai());
        gbc.gridx = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        the.add(btnLamMoi, gbc);

        return the;
    }

    private JPanel taoTheBangDanhSach() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Header thẻ danh sách
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN_THE),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Nhân Sự Gara");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblBadgeTongSo = new BadgeTongSo("Tổng số: 0 nhân viên");

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblBadgeTongSo, BorderLayout.EAST);

        // Bảng 7 cột (bổ sung cột Chuyên Môn cho Kỹ thuật viên)
        String[] tenCot = {
                "Mã NV",
                "Họ và Tên",
                "Số Điện Thoại",
                "Địa Chỉ",
                "Chức Vụ",
                "Chuyên Môn",
                "Trạng Thái"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangNhanVien = new JTable(moHinhBang);
        bangNhanVien.setRowHeight(41);
        bangNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangNhanVien.setForeground(MAU_CHU_CHINH);
        bangNhanVien.setBackground(Color.WHITE);
        bangNhanVien.setSelectionBackground(MAU_CHON_DONG);
        bangNhanVien.setSelectionForeground(MAU_XANH_DAM);
        bangNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangNhanVien.setShowVerticalLines(false);
        bangNhanVien.setShowHorizontalLines(true);
        bangNhanVien.setGridColor(MAU_KE_BANG);
        bangNhanVien.setIntercellSpacing(new Dimension(0, 1));
        bangNhanVien.setFillsViewportHeight(true);

        // Header bảng
        JTableHeader header = bangNhanVien.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        // Độ rộng các cột
        TableColumnModel cotModel = bangNhanVien.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(105);
        cotModel.getColumn(1).setPreferredWidth(160);
        cotModel.getColumn(2).setPreferredWidth(115);
        cotModel.getColumn(3).setPreferredWidth(135);
        cotModel.getColumn(4).setPreferredWidth(110);
        cotModel.getColumn(5).setPreferredWidth(185);
        cotModel.getColumn(6).setPreferredWidth(115);

        BoVeONhanVien rendererO = new BoVeONhanVien();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            cotModel.getColumn(i).setCellRenderer(rendererO);
        }

        // Bắt sự kiện chọn dòng để đưa dữ liệu lên Form bên phải
        bangNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                capNhatFormKhiChonDong();
            }
        });

        // Menu chuột phải
        JPopupMenu menuNguCanh = taoMenuNguCanhBang();
        bangNhanVien.addMouseListener(new MouseAdapter() {
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
                    int dong = bangNhanVien.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < bangNhanVien.getRowCount()) {
                        bangNhanVien.setRowSelectionInterval(dong, dong);
                        menuNguCanh.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangNhanVien);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoTheFormThaoTac() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setPreferredSize(new Dimension(360, 0));
        the.setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);
        pnlForm.setBorder(new EmptyBorder(18, 18, 18, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        int dong = 0;

        // 1. Trường: MÃ NHÂN VIÊN
        txtMaNV = taoOVanBanForm(false);
        txtMaNV.setText(TEXT_MA_NV_TU_SINH);
        txtMaNV.setBackground(new Color(248, 250, 252));
        txtMaNV.setForeground(MAU_CHU_MO);
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(taoKhoiNhanVaO("MÃ NHÂN VIÊN", txtMaNV), gbc);

        // 2. Trường: HỌ VÀ TÊN (*)
        txtHoTen = taoOVanBanForm(true);
        caiDatPlaceholder(txtHoTen, "Nhập họ và tên đầy đủ");
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(taoKhoiNhanVaO("HỌ VÀ TÊN (*)", txtHoTen), gbc);

        // 3. Trường: SỐ ĐIỆN THOẠI (* 10 số)
        txtSoDienThoai = taoOVanBanForm(true);
        caiDatPlaceholder(txtSoDienThoai, "Ví dụ: 0901234567");
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(taoKhoiNhanVaO("SỐ ĐIỆN THOẠI (* 10 số)", txtSoDienThoai), gbc);

        // 4. Trường: ĐỊA CHỈ
        txtDiaChi = taoOVanBanForm(true);
        caiDatPlaceholder(txtDiaChi, "Địa chỉ cư trú");
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(taoKhoiNhanVaO("ĐỊA CHỈ", txtDiaChi), gbc);

        // 5. Trường: CHỨC VỤ (*)
        cboChucVuForm = new JComboBox<>(new String[]{"Kỹ thuật viên", "Lễ tân", "Quản lý"});
        cboChucVuForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboChucVuForm.setBackground(Color.WHITE);
        cboChucVuForm.setPreferredSize(new Dimension(0, 36));
        cboChucVuForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true),
                new EmptyBorder(0, 8, 0, 8)
        ));
        cboChucVuForm.addActionListener(e -> {
            if (nhanVienDangChon == null) {
                // Tự động gợi ý mã nhân viên theo chức vụ
                ChucVu cv = layChucVuForm();
                txtMaNV.setText(nhanVienBUS.phatSinhMaNhanVienTheoChucVu(cv));
            }
            capNhatTrangThaiChuyenMonForm();
        });
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(taoKhoiNhanVaO("CHỨC VỤ (*)", cboChucVuForm), gbc);

        // 6. Trường: CHUYÊN MÔN (Dành riêng cho Kỹ thuật viên)
        cboChuyenMonForm = new JComboBox<>(new String[]{
                "Chuyên bảo dưỡng",
                "Chuyên sửa hệ thống điện",
                "Chuyên thay thế linh kiện",
                "Chuyên chăm sóc, vệ sinh, rửa xe",
                "Không có"
        });
        cboChuyenMonForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboChuyenMonForm.setBackground(Color.WHITE);
        cboChuyenMonForm.setPreferredSize(new Dimension(0, 36));
        cboChuyenMonForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true),
                new EmptyBorder(0, 8, 0, 8)
        ));
        JPanel khoiChuyenMon = taoKhoiNhanVaO("CHUYÊN MÔN (KỸ THUẬT VIÊN)", cboChuyenMonForm);
        lblChuyenMonForm = (JLabel) khoiChuyenMon.getComponent(0);
        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 16, 0);
        pnlForm.add(khoiChuyenMon, gbc);

        // Hàng 2 nút thao tác chính: + Thêm Mới & Cập Nhật
        JPanel hangNutChinh = new JPanel(new GridLayout(1, 2, 10, 0));
        hangNutChinh.setOpaque(false);
        hangNutChinh.setPreferredSize(new Dimension(0, 38));

        btnThemMoi = new JButton("+ Thêm Mới");
        dinhDangNutThaoTac(btnThemMoi, MAU_CAM_CHU_DAO, MAU_CAM_HOVER, Color.WHITE);
        btnThemMoi.addActionListener(e -> xuLyThemNhanVien());

        btnCapNhat = new JButton("Cập Nhật");
        dinhDangNutThaoTac(btnCapNhat, MAU_XANH_DAM, new Color(26, 58, 92), Color.WHITE);
        btnCapNhat.setEnabled(false);
        btnCapNhat.addActionListener(e -> xuLyCapNhatNhanVien());

        hangNutChinh.add(btnThemMoi);
        hangNutChinh.add(btnCapNhat);

        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(hangNutChinh, gbc);

        // Nút Khóa / Mở Khóa Nhân Viên (màu đỏ nhạt, full width, không icon)
        btnKhoaMoKhoa = new JButton("Khóa / Mở Khóa Tài Khoản");
        btnKhoaMoKhoa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKhoaMoKhoa.setFocusPainted(false);
        btnKhoaMoKhoa.setBackground(new Color(254, 226, 226));
        btnKhoaMoKhoa.setForeground(MAU_DO_CANH_BAO);
        btnKhoaMoKhoa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true),
                new EmptyBorder(9, 10, 9, 10)
        ));
        btnKhoaMoKhoa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKhoaMoKhoa.setPreferredSize(new Dimension(0, 38));
        btnKhoaMoKhoa.setEnabled(false);
        btnKhoaMoKhoa.addActionListener(e -> xuLyKhoaMoKhoaNhanVien());

        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnlForm.add(btnKhoaMoKhoa, gbc);

        // Nút Đổi Mật Khẩu Tài Khoản (màu xanh nhạt, full width, không icon)
        btnDoiMatKhau = new JButton("Đổi Mật Khẩu Tài Khoản");
        btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDoiMatKhau.setFocusPainted(false);
        btnDoiMatKhau.setBackground(new Color(239, 246, 255));
        btnDoiMatKhau.setForeground(new Color(30, 58, 138));
        btnDoiMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(9, 10, 9, 10)
        ));
        btnDoiMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDoiMatKhau.setPreferredSize(new Dimension(0, 38));
        btnDoiMatKhau.setEnabled(false);
        btnDoiMatKhau.addActionListener(e -> moHopThoaiDoiMatKhau());

        gbc.gridy = dong++;
        gbc.insets = new Insets(0, 0, 14, 0);
        pnlForm.add(btnDoiMatKhau, gbc);

        // Link Xóa Trắng Form
        btnXoaTrang = new JButton("Xóa Trắng Form");
        btnXoaTrang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnXoaTrang.setForeground(MAU_CHU_PHU);
        btnXoaTrang.setFocusPainted(false);
        btnXoaTrang.setBorderPainted(false);
        btnXoaTrang.setContentAreaFilled(false);
        btnXoaTrang.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXoaTrang.addActionListener(e -> xoaTrangForm());

        gbc.gridy = dong++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlForm.add(btnXoaTrang, gbc);

        // Filler đẩy nội dung lên trên
        gbc.gridy = dong;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        pnlForm.add(Box.createGlue(), gbc);

        JScrollPane cuonForm = new JScrollPane(pnlForm);
        cuonForm.setBorder(null);
        cuonForm.getViewport().setBackground(Color.WHITE);
        cuonForm.getVerticalScrollBar().setUnitIncrement(12);

        the.add(cuonForm, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoKhoiNhanVaO(String nhan, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(MAU_CHU_CHINH);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JTextField taoOVanBanForm(boolean editable) {
        JTextField txt = new JTextField();
        txt.setEditable(editable);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(MAU_CHU_CHINH);
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txt.setPreferredSize(new Dimension(0, 36));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return txt;
    }

    private void caiDatPlaceholder(JTextField txt, String placeholder) {
        txt.setText(placeholder);
        txt.setForeground(MAU_CHU_MO);
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (placeholder.equals(txt.getText())) {
                    txt.setText("");
                    txt.setForeground(MAU_CHU_CHINH);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(MAU_CHU_MO);
                }
            }
        });
    }

    private void dinhDangNutThaoTac(JButton btn, Color bg, Color bgHover, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bgHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });
    }

    private JPopupMenu taoMenuNguCanhBang() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem mnuSua = new JMenuItem("Chọn xem & chỉnh sửa");
        mnuSua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuSua.addActionListener(e -> capNhatFormKhiChonDong());

        JMenuItem mnuDoiMK = new JMenuItem("Đặt lại mật khẩu...");
        mnuDoiMK.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuDoiMK.addActionListener(e -> moHopThoaiDoiMatKhau());

        JMenuItem mnuKhoa = new JMenuItem("Khóa / Mở khóa nhân viên");
        mnuKhoa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuKhoa.addActionListener(e -> xuLyKhoaMoKhoaNhanVien());

        menu.add(mnuSua);
        menu.add(mnuDoiMK);
        menu.addSeparator();
        menu.add(mnuKhoa);
        return menu;
    }

    /**
     * Nạp lại dữ liệu thực tế từ MariaDB thông qua NhanVienBUS.
     */
    public void lamMoiDuLieu() {
        dangCapNhatBoLoc = true;
        try {
            danhSachGoc = nhanVienBUS.layTatCa();
        } catch (Exception ex) {
            danhSachGoc = new ArrayList<>();
        } finally {
            dangCapNhatBoLoc = false;
        }
        apDungBoLoc();
    }

    private void datLaiBoLocVaTaiLai() {
        dangCapNhatBoLoc = true;
        try {
            txtTimKiem.setText(PLACEHOLDER_TIM_KIEM);
            txtTimKiem.setForeground(MAU_CHU_MO);
            cboLocChucVu.setSelectedIndex(0);
            cboLocTrangThai.setSelectedIndex(0);
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

        int chiSoChucVu = cboLocChucVu.getSelectedIndex();
        int chiSoTrangThai = cboLocTrangThai.getSelectedIndex();

        danhSachHienThi.clear();
        moHinhBang.setRowCount(0);

        for (NhanVien nv : danhSachGoc) {
            // 1. Lọc theo từ khóa (Mã NV, Họ tên, SĐT)
            if (!tuKhoa.isEmpty()) {
                String ma = nv.getMaNhanVien() != null ? nv.getMaNhanVien().toLowerCase(Locale.ROOT) : "";
                String ten = nv.getHoTen() != null ? nv.getHoTen().toLowerCase(Locale.ROOT) : "";
                String sdt = nv.getSoDienThoai() != null ? nv.getSoDienThoai().toLowerCase(Locale.ROOT) : "";
                if (!ma.contains(tuKhoa) && !ten.contains(tuKhoa) && !sdt.contains(tuKhoa)) {
                    continue;
                }
            }

            // 2. Lọc theo Chức vụ
            if (chiSoChucVu == 1 && nv.getChucVu() != ChucVu.QUAN_LY) {
                continue;
            } else if (chiSoChucVu == 2 && nv.getChucVu() != ChucVu.KY_THUAT_VIEN) {
                continue;
            } else if (chiSoChucVu == 3 && nv.getChucVu() != ChucVu.LE_TAN) {
                continue;
            }

            // 3. Lọc theo Trạng thái
            if (chiSoTrangThai == 1 && !nv.isTrangThai()) {
                continue;
            } else if (chiSoTrangThai == 2 && nv.isTrangThai()) {
                continue;
            }

            danhSachHienThi.add(nv);

            // Xử lý hiển thị chuyên môn: Kỹ thuật viên hiển thị chuyên môn cụ thể, các vị trí khác hiển thị "Không có"
            String chuyenMonHienThi;
            if (nv.getChucVu() == ChucVu.KY_THUAT_VIEN) {
                chuyenMonHienThi = nv.getChuyenMon() != null ? nv.getChuyenMon().getTenHienThi() : "Chưa phân công";
            } else {
                chuyenMonHienThi = "Không có";
            }

            moHinhBang.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    nv.getDiaChi() != null ? nv.getDiaChi() : "",
                    nv.getChucVu() != null ? nv.getChucVu().getTenHienThi() : "",
                    chuyenMonHienThi,
                    nv.isTrangThai() ? "• Hoạt động" : "• Đã khóa"
            });
        }

        lblBadgeTongSo.setText("Tổng số: " + danhSachHienThi.size() + " nhân viên");

        // Đồng bộ lại lựa chọn nếu có
        if (nhanVienDangChon != null) {
            int viTri = danhSachHienThi.indexOf(nhanVienDangChon);
            if (viTri >= 0) {
                bangNhanVien.setRowSelectionInterval(viTri, viTri);
            } else {
                xoaTrangForm();
            }
        }
    }

    private void capNhatFormKhiChonDong() {
        int dong = bangNhanVien.getSelectedRow();
        if (dong < 0 || dong >= danhSachHienThi.size()) {
            return;
        }

        nhanVienDangChon = danhSachHienThi.get(dong);
        txtMaNV.setText(nhanVienDangChon.getMaNhanVien());
        txtMaNV.setForeground(MAU_XANH_DAM);

        txtHoTen.setText(nhanVienDangChon.getHoTen() != null ? nhanVienDangChon.getHoTen() : "");
        txtHoTen.setForeground(MAU_CHU_CHINH);

        txtSoDienThoai.setText(nhanVienDangChon.getSoDienThoai() != null ? nhanVienDangChon.getSoDienThoai() : "");
        txtSoDienThoai.setForeground(MAU_CHU_CHINH);

        txtDiaChi.setText(nhanVienDangChon.getDiaChi() != null ? nhanVienDangChon.getDiaChi() : "");
        txtDiaChi.setForeground(MAU_CHU_CHINH);

        if (nhanVienDangChon.getChucVu() != null) {
            switch (nhanVienDangChon.getChucVu()) {
                case KY_THUAT_VIEN -> cboChucVuForm.setSelectedIndex(0);
                case LE_TAN -> cboChucVuForm.setSelectedIndex(1);
                case QUAN_LY -> cboChucVuForm.setSelectedIndex(2);
            }
        }

        if (nhanVienDangChon.getChucVu() == ChucVu.KY_THUAT_VIEN) {
            cboChuyenMonForm.setEnabled(true);
            if (nhanVienDangChon.getChuyenMon() != null) {
                switch (nhanVienDangChon.getChuyenMon()) {
                    case CHUYEN_BAO_DUONG -> cboChuyenMonForm.setSelectedIndex(0);
                    case CHUYEN_SUA_HE_THONG_DIEN -> cboChuyenMonForm.setSelectedIndex(1);
                    case CHUYEN_THAY_THE_LINH_KIEN -> cboChuyenMonForm.setSelectedIndex(2);
                    case CHUYEN_CHAM_SOC_VE_SINH_RUA_XE -> cboChuyenMonForm.setSelectedIndex(3);
                }
            } else {
                cboChuyenMonForm.setSelectedIndex(0);
            }
        } else {
            cboChuyenMonForm.setEnabled(false);
            cboChuyenMonForm.setSelectedItem("Không có");
        }

        btnCapNhat.setEnabled(true);
        btnKhoaMoKhoa.setEnabled(true);
        btnDoiMatKhau.setEnabled(true);

        if (nhanVienDangChon.isTrangThai()) {
            btnKhoaMoKhoa.setText("Khóa Tài Khoản");
            btnKhoaMoKhoa.setBackground(new Color(254, 226, 226));
            btnKhoaMoKhoa.setForeground(MAU_DO_CANH_BAO);
        } else {
            btnKhoaMoKhoa.setText("Mở Khóa Tài Khoản");
            btnKhoaMoKhoa.setBackground(new Color(220, 252, 231));
            btnKhoaMoKhoa.setForeground(new Color(22, 101, 52));
        }
    }

    private void xoaTrangForm() {
        nhanVienDangChon = null;
        bangNhanVien.clearSelection();

        ChucVu cvMacDinh = layChucVuForm();
        txtMaNV.setText(nhanVienBUS.phatSinhMaNhanVienTheoChucVu(cvMacDinh));
        txtMaNV.setForeground(MAU_CHU_MO);

        caiDatPlaceholder(txtHoTen, "Nhập họ và tên đầy đủ");
        caiDatPlaceholder(txtSoDienThoai, "Ví dụ: 0901234567");
        caiDatPlaceholder(txtDiaChi, "Địa chỉ cư trú");
        capNhatTrangThaiChuyenMonForm();

        btnCapNhat.setEnabled(false);
        btnKhoaMoKhoa.setEnabled(false);
        btnDoiMatKhau.setEnabled(false);
        btnKhoaMoKhoa.setText("Khóa / Mở Khóa Tài Khoản");
        btnKhoaMoKhoa.setBackground(new Color(254, 226, 226));
        btnKhoaMoKhoa.setForeground(MAU_DO_CANH_BAO);
    }

    private void capNhatTrangThaiChuyenMonForm() {
        ChucVu cv = layChucVuForm();
        boolean laKTV = (cv == ChucVu.KY_THUAT_VIEN);
        cboChuyenMonForm.setEnabled(laKTV);
        if (!laKTV) {
            cboChuyenMonForm.setSelectedItem("Không có");
        } else if ("Không có".equals(cboChuyenMonForm.getSelectedItem())) {
            cboChuyenMonForm.setSelectedIndex(0);
        }
    }

    private ChuyenMon layChuyenMonForm() {
        if (layChucVuForm() != ChucVu.KY_THUAT_VIEN) {
            return null;
        }
        int idx = cboChuyenMonForm.getSelectedIndex();
        return switch (idx) {
            case 0 -> ChuyenMon.CHUYEN_BAO_DUONG;
            case 1 -> ChuyenMon.CHUYEN_SUA_HE_THONG_DIEN;
            case 2 -> ChuyenMon.CHUYEN_THAY_THE_LINH_KIEN;
            case 3 -> ChuyenMon.CHUYEN_CHAM_SOC_VE_SINH_RUA_XE;
            default -> ChuyenMon.CHUYEN_BAO_DUONG;
        };
    }

    private ChucVu layChucVuForm() {
        int idx = cboChucVuForm.getSelectedIndex();
        return switch (idx) {
            case 1 -> ChucVu.LE_TAN;
            case 2 -> ChucVu.QUAN_LY;
            default -> ChucVu.KY_THUAT_VIEN;
        };
    }

    private void xuLyThemNhanVien() {
        String hoTen = layGiaTriOForm(txtHoTen, "Nhập họ và tên đầy đủ");
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ và tên nhân viên!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }

        String sdt = layGiaTriOForm(txtSoDienThoai, "Ví dụ: 0901234567");
        if (sdt.isEmpty() || !sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 chữ số bắt đầu bằng số 0!",
                    "Số điện thoại không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtSoDienThoai.requestFocus();
            return;
        }

        String diaChi = layGiaTriOForm(txtDiaChi, "Địa chỉ cư trú");
        ChucVu chucVu = layChucVuForm();

        try {
            NhanVien nvMoi = new NhanVien();
            String maDuKien = nhanVienBUS.phatSinhMaNhanVienTheoChucVu(chucVu);
            nvMoi.setMaNhanVien(maDuKien);
            nvMoi.setHoTen(hoTen);
            nvMoi.setSoDienThoai(sdt);
            nvMoi.setDiaChi(diaChi);
            nvMoi.setChucVu(chucVu);
            nvMoi.setTrangThai(true);
            nvMoi.setChuyenMon(chucVu == ChucVu.KY_THUAT_VIEN ? layChuyenMonForm() : null);

            if (nhanVienBUS.themNhanVien(nvMoi)) {
                JOptionPane.showMessageDialog(this,
                        "Thêm nhân viên mới thành công!\n"
                                + "- Mã nhân viên: " + nvMoi.getMaNhanVien() + "\n"
                                + "- Tên tài khoản: " + nvMoi.getMaNhanVien() + "\n"
                                + "- Mật khẩu mặc định: 123456",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                lamMoiDuLieu();
                xoaTrangForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể thêm nhân viên", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhatNhanVien() {
        if (nhanVienDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật từ bảng!",
                    "Chưa chọn nhân sự", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hoTen = layGiaTriOForm(txtHoTen, "Nhập họ và tên đầy đủ");
        if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ và tên không được để trống!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }

        String sdt = layGiaTriOForm(txtSoDienThoai, "Ví dụ: 0901234567");
        if (sdt.isEmpty() || !sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 chữ số bắt đầu bằng số 0!",
                    "Số điện thoại không hợp lệ", JOptionPane.WARNING_MESSAGE);
            txtSoDienThoai.requestFocus();
            return;
        }

        String diaChi = layGiaTriOForm(txtDiaChi, "Địa chỉ cư trú");
        ChucVu chucVu = layChucVuForm();

        try {
            nhanVienDangChon.setHoTen(hoTen);
            nhanVienDangChon.setSoDienThoai(sdt);
            nhanVienDangChon.setDiaChi(diaChi);
            nhanVienDangChon.setChucVu(chucVu);
            nhanVienDangChon.setChuyenMon(chucVu == ChucVu.KY_THUAT_VIEN ? layChuyenMonForm() : null);

            if (nhanVienBUS.capNhatNhanVien(nhanVienDangChon)) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật hồ sơ nhân viên [" + nhanVienDangChon.getMaNhanVien() + "] thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể cập nhật nhân viên", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyKhoaMoKhoaNhanVien() {
        if (nhanVienDangChon == null) {
            return;
        }

        boolean dangHoatDong = nhanVienDangChon.isTrangThai();
        String hanhDong = dangHoatDong ? "KHÓA" : "MỞ KHÓA";

        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn " + hanhDong + " nhân viên ["
                        + nhanVienDangChon.getHoTen() + " - " + nhanVienDangChon.getMaNhanVien() + "]?\n"
                        + (dangHoatDong ? "Nhân viên này và tài khoản đăng nhập tương ứng sẽ không thể thao tác trên hệ thống."
                        : "Tài khoản của nhân viên này sẽ được khôi phục quyền truy cập."),
                "Xác nhận " + hanhDong.toLowerCase() + " nhân sự",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (xacNhan == JOptionPane.YES_OPTION) {
            try {
                boolean trangThaiMoi = !dangHoatDong;
                nhanVienDangChon.setTrangThai(trangThaiMoi);
                nhanVienBUS.capNhatNhanVien(nhanVienDangChon);
                taiKhoanBUS.capNhatTrangThaiTaiKhoan(nhanVienDangChon.getMaNhanVien(), trangThaiMoi);

                JOptionPane.showMessageDialog(this,
                        "Đã " + hanhDong.toLowerCase() + " nhân viên thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                lamMoiDuLieu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Lỗi thao tác", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moHopThoaiDoiMatKhau() {
        if (nhanVienDangChon == null) {
            return;
        }

        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(cuaSoCha, "Đặt Lại Mật Khẩu - " + nhanVienDangChon.getMaNhanVien(),
                JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 290);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField txtUser = new JTextField(nhanVienDangChon.getMaNhanVien());
        txtUser.setEditable(false);
        txtUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtUser.setBackground(new Color(241, 245, 249));

        JPasswordField txtMatKhauMoi = new JPasswordField();
        txtMatKhauMoi.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnDatLaiMacDinh = new JButton("Đặt về mặc định (123456)");
        btnDatLaiMacDinh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDatLaiMacDinh.addActionListener(e -> txtMatKhauMoi.setText("123456"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        pnl.add(new JLabel("Tài khoản:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        pnl.add(txtUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        pnl.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        pnl.add(txtMatKhauMoi, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        pnl.add(btnDatLaiMacDinh, gbc);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(MAU_NEN_TRANG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN_THE));

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());

        JButton btnXacNhan = new JButton("Lưu Mật Khẩu");
        btnXacNhan.setBackground(MAU_CAM_CHU_DAO);
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXacNhan.addActionListener(e -> {
            String pass = new String(txtMatKhauMoi.getPassword()).trim();
            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu không được để trống!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (taiKhoanBUS.datLaiMatKhau(nhanVienDangChon.getMaNhanVien(), pass)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Đã đổi mật khẩu cho tài khoản [" + nhanVienDangChon.getMaNhanVien() + "] thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Lỗi đổi mật khẩu", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnHuy);
        footer.add(btnXacNhan);

        dialog.add(pnl, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String layGiaTriOForm(JTextField txt, String placeholder) {
        String val = txt.getText() != null ? txt.getText().trim() : "";
        if (placeholder.equals(val)) {
            return "";
        }
        return val;
    }

    // --- Các Component hỗ trợ vẽ giao diện ---

    private JComboBox<String> taoComboBoLoc(String[] items, int chieuRong) {
        JComboBox<String> cbo = new JComboBox<>(items);
        cbo.setPreferredSize(new Dimension(chieuRong, 36));
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbo.setForeground(MAU_CHU_CHINH);
        cbo.setBackground(Color.WHITE);
        cbo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN_THE, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));
        cbo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        return cbo;
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

        KhungTimKiemBoTron() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(0, 36));
            setBorder(new EmptyBorder(0, 14, 0, 14));

            JLabel lblIcon = new JLabel(new IconTimKiem(15, MAU_CHU_MO));
            txt = new JTextField(PLACEHOLDER_TIM_KIEM);
            txt.setBorder(null);
            txt.setOpaque(false);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setForeground(MAU_CHU_MO);

            txt.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (PLACEHOLDER_TIM_KIEM.equals(txt.getText())) {
                        txt.setText("");
                        txt.setForeground(MAU_CHU_CHINH);
                    }
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
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

        JTextField getOvanBan() {
            return txt;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, getHeight(), getHeight());
            g2.setColor(Color.WHITE);
            g2.fill(pill);
            g2.setColor(txt.hasFocus() ? MAU_CAM_CHU_DAO : MAU_VIEN_THE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(pill);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NutLamMoiBoLoc extends JButton {
        private boolean hover = false;

        NutLamMoiBoLoc() {
            setPreferredSize(new Dimension(36, 36));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setIcon(new IconLamMoi(15, MAU_CHU_CHINH));

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
            setBorder(new EmptyBorder(5, 12, 5, 12));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D badge = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12f, 12f);
            g2.setColor(MAU_NEN_BADGE_TONG);
            g2.fill(badge);
            g2.setColor(MAU_VIEN_BADGE_TONG);
            g2.draw(badge);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BoVeTieuDeBang extends DefaultTableCellRenderer {
        BoVeTieuDeBang() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(MAU_CHU_CHINH);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 14, 0, 14));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(245, 247, 250),
                    0, getHeight(), new Color(233, 237, 242)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(MAU_VIEN_THE);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BoVeONhanVien extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(0, 14, 0, 14));

            if (isSelected) {
                setBackground(MAU_CHON_DONG);
                setForeground(MAU_XANH_DAM);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                setBackground(Color.WHITE);
                setForeground(MAU_CHU_CHINH);
            }

            switch (column) {
                case 0 -> {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setForeground(isSelected ? MAU_XANH_DAM : MAU_CHU_PHU);
                }
                case 1 -> {
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(MAU_CHU_CHINH);
                }
                case 5 -> {
                    String val = value != null ? value.toString() : "";
                    if ("Không có".equalsIgnoreCase(val)) {
                        setFont(new Font("Segoe UI", Font.ITALIC, 12));
                        setForeground(isSelected ? new Color(148, 163, 184) : MAU_CHU_MO);
                    } else {
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        setForeground(isSelected ? MAU_XANH_DAM : MAU_CHU_CHINH);
                    }
                }
                case 6 -> {
                    String val = value != null ? value.toString() : "";
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (val.contains("Hoạt động")) {
                        setForeground(MAU_XANH_LA);
                    } else {
                        setForeground(MAU_DO_CANH_BAO);
                    }
                }
            }
            return this;
        }
    }

    private static class IconTimKiem implements Icon {
        private final int size;
        private final Color color;

        IconTimKiem(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Icon iconImg = IconUtil.layIcon("search.png", size, size, color);
            if (iconImg != null) {
                iconImg.paintIcon(c, g, x, y);
            }
        }
    }

    private static class IconLamMoi implements Icon {
        private final int size;
        private final Color color;

        IconLamMoi(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Icon iconImg = IconUtil.layIcon("refresh.png", size, size, color);
            if (iconImg != null) {
                iconImg.paintIcon(c, g, x, y);
            }
        }
    }
}
