package ui;

import bus.DanhMucSanPhamBUS;
import bus.SanPhamBUS;
import entity.DanhMucSanPham;
import entity.SanPham;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuanLyManHinhQuanLySanPham extends JPanel {

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

    private static final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo Mã SP, Tên sản phẩm, ĐVT...";

    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private final DanhMucSanPhamBUS danhMucBUS = new DanhMucSanPhamBUS();
    private final DecimalFormat dinhDangTien;

    private List<SanPham> danhSachGoc = new ArrayList<>();
    private final List<SanPham> danhSachHienThi = new ArrayList<>();

    private JTextField txtTimKiem;
    private JComboBox<LuaChonDanhMuc> cboDanhMuc;
    private JComboBox<String> cboTonKho;
    private JComboBox<String> cboTrangThai;
    private BadgeTongSo lblBadgeTongSo;
    private DefaultTableModel moHinhBang;
    private JTable bangSanPham;
    private boolean dangCapNhatBoLoc = false;

    public QuanLyManHinhQuanLySanPham() {
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

        // Phần đầu gồm: Hàng tiêu đề + nút chức năng, bên dưới là thẻ tìm kiếm & bộ lọc
        JPanel khuVucTren = new JPanel(new BorderLayout(0, 14));
        khuVucTren.setOpaque(false);
        khuVucTren.add(taoHangTieuDeVaNutChucNang(), BorderLayout.NORTH);
        khuVucTren.add(taoTheTimKiemVaBoLoc(), BorderLayout.CENTER);

        // Phần chính: Thẻ bảng danh sách phụ tùng & vật tư
        JPanel theBangDanhSach = taoTheBangSanPham();

        add(khuVucTren, BorderLayout.NORTH);
        add(theBangDanhSach, BorderLayout.CENTER);
    }

    private JPanel taoHangTieuDeVaNutChucNang() {
        JPanel hang = new JPanel(new BorderLayout());
        hang.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Sản Phẩm & Phụ Tùng");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JPanel cumNutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumNutPhai.setOpaque(false);

        NutVienTrang btnNhapExcel = new NutVienTrang(
                "Nhập từ file Excel",
                new BieuTuongNut(LoaiIcon.NHAP_EXCEL, 15, MAU_CHU_CHINH)
        );
        btnNhapExcel.addActionListener(e -> xuLyNhapTuFileExcel());

        NutCamChuDao btnThemMoi = new NutCamChuDao(
                "Thêm Sản Phẩm Mới",
                new BieuTuongNut(LoaiIcon.DAU_CONG, 14, Color.WHITE)
        );
        btnThemMoi.addActionListener(e -> moHopThoaiSanPham(null));

        cumNutPhai.add(btnNhapExcel);
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

        // 1. Ô tìm kiếm bo tròn dạng pill bên trái (chiếm phần rộng co giãn)
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

        // 2. Bộ lọc Danh mục
        cboDanhMuc = taoComboBoLoc(new LuaChonDanhMuc[]{new LuaChonDanhMuc(null, "Danh mục")}, 180);
        cboDanhMuc.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 0;
        the.add(cboDanhMuc, gbc);

        // 3. Bộ lọc Tồn kho
        cboTonKho = taoComboBoLoc(new String[]{
                "Tồn kho",
                "Còn hàng (> 0)",
                "Sắp hết hàng",
                "Hết hàng (= 0)"
        }, 138);
        cboTonKho.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 2;
        the.add(cboTonKho, gbc);

        // 4. Bộ lọc Trạng thái
        cboTrangThai = taoComboBoLoc(new String[]{
                "Trạng thái",
                "Đang bán",
                "Ngừng bán"
        }, 134);
        cboTrangThai.addActionListener(e -> {
            if (!dangCapNhatBoLoc) {
                apDungBoLoc();
            }
        });
        gbc.gridx = 3;
        the.add(cboTrangThai, gbc);

        // 5. Nút làm mới (↻) vuông góc phải
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

    private JPanel taoTheBangSanPham() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Đầu thẻ: Tiêu đề bên trái + Badge "Tổng số: X sản phẩm" bên phải
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(14, 18, 12, 18)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Phụ Tùng & Vật Tư");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblBadgeTongSo = new BadgeTongSo("Tổng số: 0 sản phẩm");

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblBadgeTongSo, BorderLayout.EAST);

        // Mô hình bảng 7 cột theo đúng thiết kế ảnh mẫu
        String[] tenCot = {
                "Mã SP",
                "Tên Sản Phẩm",
                "Danh Mục",
                "ĐVT",
                "Đơn Giá Bán",
                "Tồn Kho",
                "Trạng Thái"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangSanPham = new JTable(moHinhBang);
        bangSanPham.setRowHeight(41);
        bangSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangSanPham.setForeground(MAU_CHU_CHINH);
        bangSanPham.setBackground(Color.WHITE);
        bangSanPham.setSelectionBackground(MAU_CHON_DONG);
        bangSanPham.setSelectionForeground(MAU_CHU_CHINH);
        bangSanPham.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangSanPham.setShowVerticalLines(false);
        bangSanPham.setShowHorizontalLines(true);
        bangSanPham.setGridColor(MAU_KE_BANG);
        bangSanPham.setIntercellSpacing(new Dimension(0, 1));
        bangSanPham.setFillsViewportHeight(true);

        // Tuỳ chỉnh thanh tiêu đề cột (nền chuyển sắc xám bạc nhẹ giống ảnh mẫu)
        JTableHeader header = bangSanPham.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        // Độ rộng & bộ vẽ từng cột
        TableColumnModel cotModel = bangSanPham.getColumnModel();
        cotModel.getColumn(0).setPreferredWidth(140);
        cotModel.getColumn(1).setPreferredWidth(270);
        cotModel.getColumn(2).setPreferredWidth(200);
        cotModel.getColumn(3).setPreferredWidth(95);
        cotModel.getColumn(4).setPreferredWidth(155);
        cotModel.getColumn(5).setPreferredWidth(115);
        cotModel.getColumn(6).setPreferredWidth(145);

        BoVeOSanPham rendererO = new BoVeOSanPham();
        for (int i = 0; i < cotModel.getColumnCount(); i++) {
            cotModel.getColumn(i).setCellRenderer(rendererO);
        }

        // Menu chuột phải & nhấp đúp để chỉnh sửa / nhập kho nhanh
        JPopupMenu menuNguCanh = taoMenuNguCanhBang();
        bangSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    SanPham sp = laySanPhamDangChon();
                    if (sp != null) {
                        moModalChiTietSanPham(sp);
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
                    int dong = bangSanPham.rowAtPoint(e.getPoint());
                    if (dong >= 0 && dong < bangSanPham.getRowCount()) {
                        bangSanPham.setRowSelectionInterval(dong, dong);
                        menuNguCanh.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane cuonBang = new JScrollPane(bangSanPham);
        cuonBang.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        cuonBang.getViewport().setBackground(Color.WHITE);

        the.add(dauThe, BorderLayout.NORTH);
        the.add(cuonBang, BorderLayout.CENTER);
        return the;
    }

    private JPopupMenu taoMenuNguCanhBang() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem mnuChiTiet = new JMenuItem("Xem chi tiết & Cập nhật sản phẩm");
        mnuChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mnuChiTiet.addActionListener(e -> {
            SanPham sp = laySanPhamDangChon();
            if (sp != null) {
                moModalChiTietSanPham(sp);
            }
        });

        JMenuItem mnuNhapKho = new JMenuItem("Nhập thêm tồn kho...");
        mnuNhapKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuNhapKho.addActionListener(e -> xuLyNhapThemTonKhoNhanh());

        JMenuItem mnuDoiTrangThai = new JMenuItem("Đổi trạng thái Đang bán / Ngừng bán");
        mnuDoiTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mnuDoiTrangThai.addActionListener(e -> xuLyDoiTrangThaiNhanh());

        menu.add(mnuChiTiet);
        menu.add(mnuNhapKho);
        menu.addSeparator();
        menu.add(mnuDoiTrangThai);
        return menu;
    }

    private SanPham laySanPhamDangChon() {
        int dong = bangSanPham.getSelectedRow();
        if (dong < 0 || dong >= danhSachHienThi.size()) {
            return null;
        }
        return danhSachHienThi.get(dong);
    }

    /**
     * Nạp lại toàn bộ danh mục và sản phẩm thực tế từ cơ sở dữ liệu thông qua BUS.
     * Tuyệt đối không chèn bất kỳ dữ liệu giả (mockup) nào.
     */
    public void lamMoiDuLieu() {
        dangCapNhatBoLoc = true;
        try {
            LuaChonDanhMuc dangChonTruocDo = (LuaChonDanhMuc) cboDanhMuc.getSelectedItem();
            String maDanhMucCu = dangChonTruocDo != null ? dangChonTruocDo.maDanhMuc() : null;

            DefaultComboBoxModel<LuaChonDanhMuc> modelDM = new DefaultComboBoxModel<>();
            modelDM.addElement(new LuaChonDanhMuc(null, "Danh mục"));

            List<DanhMucSanPham> dsDanhMuc = danhMucBUS.layTatCa();
            LuaChonDanhMuc mucCanChonLai = modelDM.getElementAt(0);
            for (DanhMucSanPham dm : dsDanhMuc) {
                LuaChonDanhMuc item = new LuaChonDanhMuc(dm.getMaDanhMuc(), dm.getTenDanhMuc());
                modelDM.addElement(item);
                if (maDanhMucCu != null && maDanhMucCu.equals(dm.getMaDanhMuc())) {
                    mucCanChonLai = item;
                }
            }
            cboDanhMuc.setModel(modelDM);
            cboDanhMuc.setSelectedItem(mucCanChonLai);

            danhSachGoc = sanPhamBUS.layTatCa();
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
            if (cboDanhMuc.getItemCount() > 0) {
                cboDanhMuc.setSelectedIndex(0);
            }
            cboTonKho.setSelectedIndex(0);
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

        LuaChonDanhMuc luaChonDM = (LuaChonDanhMuc) cboDanhMuc.getSelectedItem();
        String maDanhMucLoc = luaChonDM != null ? luaChonDM.maDanhMuc() : null;
        int chiSoTonKho = cboTonKho.getSelectedIndex();
        int chiSoTrangThai = cboTrangThai.getSelectedIndex();

        danhSachHienThi.clear();
        moHinhBang.setRowCount(0);

        for (SanPham sp : danhSachGoc) {
            // 1. Lọc theo từ khoá (Mã SP, Tên sản phẩm, ĐVT)
            if (!tuKhoa.isEmpty()) {
                String ma = sp.getMaSanPham() != null ? sp.getMaSanPham().toLowerCase(Locale.ROOT) : "";
                String ten = sp.getTenSanPham() != null ? sp.getTenSanPham().toLowerCase(Locale.ROOT) : "";
                String dvt = sp.getDonViTinh() != null ? sp.getDonViTinh().toLowerCase(Locale.ROOT) : "";
                if (!ma.contains(tuKhoa) && !ten.contains(tuKhoa) && !dvt.contains(tuKhoa)) {
                    continue;
                }
            }

            // 2. Lọc theo Danh mục
            if (maDanhMucLoc != null) {
                String maDM = sp.getDanhMucSanPham() != null ? sp.getDanhMucSanPham().getMaDanhMuc() : null;
                if (!maDanhMucLoc.equals(maDM)) {
                    continue;
                }
            }

            // 3. Lọc theo Tồn kho
            int ton = sp.getSoLuongTon();
            if (chiSoTonKho == 1 && ton <= 0) {
                continue;
            } else if (chiSoTonKho == 2 && !(ton > 0 && ton <= Math.max(5, sp.getSoLuongTonToiThieu()))) {
                continue;
            } else if (chiSoTonKho == 3 && ton != 0) {
                continue;
            }

            // 4. Lọc theo Trạng thái
            if (chiSoTrangThai == 1 && !sp.isTrangThai()) {
                continue;
            } else if (chiSoTrangThai == 2 && sp.isTrangThai()) {
                continue;
            }

            danhSachHienThi.add(sp);
            String tenDanhMuc = (sp.getDanhMucSanPham() != null && sp.getDanhMucSanPham().getTenDanhMuc() != null)
                    ? sp.getDanhMucSanPham().getTenDanhMuc()
                    : "";

            moHinhBang.addRow(new Object[]{
                    sp.getMaSanPham(),
                    sp.getTenSanPham(),
                    tenDanhMuc,
                    sp.getDonViTinh(),
                    dinhDangTien.format(Math.round(sp.getDonGiaBan())) + " đ",
                    ton,
                    sp.isTrangThai() ? "\u2022 Đang bán" : "\u2022 Ngừng bán"
            });
        }

        lblBadgeTongSo.setText("Tổng số: " + danhSachHienThi.size() + " sản phẩm");
    }

    private void xuLyNhapThemTonKhoNhanh() {
        SanPham sp = laySanPhamDangChon();
        if (sp == null) {
            return;
        }
        String nhap = JOptionPane.showInputDialog(
                this,
                "Nhập số lượng hàng bổ sung vào kho cho [" + sp.getTenSanPham() + "]:",
                "Nhập thêm tồn kho",
                JOptionPane.PLAIN_MESSAGE
        );
        if (nhap == null || nhap.trim().isEmpty()) {
            return;
        }
        try {
            int soLuong = Integer.parseInt(nhap.trim());
            if (sanPhamBUS.nhapThemHang(sp.getMaSanPham(), soLuong)) {
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this, "Đã cập nhật số lượng tồn kho thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng nhập phải là số nguyên dương!",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi cập nhật tồn kho", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyDoiTrangThaiNhanh() {
        SanPham sp = laySanPhamDangChon();
        if (sp == null) {
            return;
        }
        try {
            sp.setTrangThai(!sp.isTrangThai());
            if (sanPhamBUS.capNhatSanPham(sp)) {
                lamMoiDuLieu();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Lỗi đổi trạng thái", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở modal xem chi tiết và cập nhật sản phẩm chuyên biệt (kích thước chuẩn, bố cục thẻ trực quan).
     */
    public void moModalChiTietSanPham(SanPham sp) {
        if (sp == null) {
            return;
        }
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        QuanLyManHinhQuanLySanPhamXemChiTiet modal =
                new QuanLyManHinhQuanLySanPhamXemChiTiet(cuaSoCha, sp, sanPhamBUS, danhMucBUS);
        modal.setVisible(true);
        if (modal.isDaCapNhat()) {
            lamMoiDuLieu();
        }
    }

    /**
     * Hộp thoại Thêm mới / Cập nhật Sản phẩm & Phụ tùng.
     * Đã được chuẩn hóa và tách biệt sang class QuanLyManHinhQuanLySanPhamThemSanPham.
     */
    private void moHopThoaiSanPham(SanPham spCanSua) {
        Window cuaSoCha = SwingUtilities.getWindowAncestor(this);
        QuanLyManHinhQuanLySanPhamThemSanPham modal = new QuanLyManHinhQuanLySanPhamThemSanPham(
                cuaSoCha, spCanSua, sanPhamBUS, danhMucBUS
        );
        modal.setVisible(true);
        if (modal.isDaLuuThanhCong()) {
            lamMoiDuLieu();
        }
    }

    /**
     * Hỗ trợ nhập danh sách sản phẩm từ file Excel lưu dạng CSV UTF-8 (hoặc tải file mẫu để nhập nhanh).
     */
    private void xuLyNhapTuFileExcel() {
        String[] luaChon = {"Chọn file dữ liệu (.csv)", "Tải file mẫu Excel (.csv)", "Đóng"};
        int kq = JOptionPane.showOptionDialog(
                this,
                """
                        Hỗ trợ nhập hàng loạt sản phẩm & phụ tùng từ Excel (lưu dưới định dạng CSV UTF-8):
                        Cấu trúc cột: Tên danh mục | Tên sản phẩm | ĐVT | Giá nhập | Đơn giá bán | Tồn kho | Trạng thái
                        (Hệ thống sẽ tự động tạo Danh mục mới nếu chưa tồn tại và phát sinh Mã SP chuẩn).
                        """,
                "Nhập Sản Phẩm & Phụ Tùng từ Excel",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                luaChon,
                luaChon[0]
        );

        if (kq == 1) {
            xuatFileMauExcelCsv();
            return;
        }
        if (kq != 0) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file Excel CSV (.csv)");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel CSV Files (*.csv)", "csv", "txt"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        int soThanhCong = 0;
        int soBoQua = 0;

        Map<String, DanhMucSanPham> mapDanhMucTheoTen = new HashMap<>();
        for (DanhMucSanPham dm : danhMucBUS.layTatCa()) {
            mapDanhMucTheoTen.put(dm.getTenDanhMuc().trim().toLowerCase(Locale.ROOT), dm);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            boolean laDongDau = true;
            while ((dong = br.readLine()) != null) {
                dong = dong.replace("\uFEFF", "").trim();
                if (dong.isEmpty()) {
                    continue;
                }
                String phanTach = dong.contains(";") ? ";" : ",";
                String[] cot = dong.split(phanTach, -1);

                // Bỏ qua dòng tiêu đề nếu cột đơn giá không phải là số
                if (laDongDau) {
                    laDongDau = false;
                    if (cot.length >= 5 && !laChuoiSo(cot[4])) {
                        continue;
                    }
                }

                if (cot.length < 5) {
                    soBoQua++;
                    continue;
                }

                try {
                    String tenDM = lamSachO(cot[0]);
                    String tenSP = lamSachO(cot[1]);
                    String dvt = lamSachO(cot[2]);
                    double giaNhap = docSoThuc(cot[3], 0);
                    double donGiaBan = docSoThuc(cot[4], 0);
                    int tonKho = cot.length >= 6 ? (int) Math.round(docSoThuc(cot[5], 0)) : 0;
                    boolean trangThai = cot.length < 7 || !lamSachO(cot[6]).toLowerCase(Locale.ROOT).contains("ngừng");

                    if (tenDM.isEmpty() || tenSP.isEmpty() || dvt.isEmpty()) {
                        soBoQua++;
                        continue;
                    }

                    String khoaDM = tenDM.toLowerCase(Locale.ROOT);
                    DanhMucSanPham dm = mapDanhMucTheoTen.get(khoaDM);
                    if (dm == null) {
                        dm = new DanhMucSanPham();
                        dm.setTenDanhMuc(tenDM);
                        dm.setMoTa("Nhập từ file Excel");
                        dm.setTrangThai(true);
                        danhMucBUS.themDanhMuc(dm);
                        mapDanhMucTheoTen.put(khoaDM, dm);
                    }

                    SanPham sp = new SanPham();
                    sp.setDanhMucSanPham(dm);
                    sp.setTenSanPham(tenSP);
                    sp.setDonViTinh(dvt);
                    sp.setGiaNhap(giaNhap);
                    sp.setDonGiaBan(donGiaBan);
                    sp.setThueVAT(8.0);
                    sp.setSoLuongTon(Math.max(0, tonKho));
                    sp.setSoLuongTonToiThieu(5);
                    sp.setTrangThai(trangThai);
                    if (sanPhamBUS.themSanPham(sp)) {
                        soThanhCong++;
                    } else {
                        soBoQua++;
                    }
                } catch (Exception exDong) {
                    soBoQua++;
                }
            }

            lamMoiDuLieu();
            JOptionPane.showMessageDialog(
                    this,
                    "Hoàn tất nhập dữ liệu!\n- Đã thêm thành công: " + soThanhCong + " sản phẩm\n- Dòng bỏ qua/lỗi: " + soBoQua,
                    "Kết quả nhập file",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể đọc file: " + ex.getMessage(),
                    "Lỗi đọc file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuatFileMauExcelCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file mẫu nhập sản phẩm (.csv)");
        chooser.setSelectedFile(new File("mau_nhap_san_pham_pengu_garage.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File fileDich = chooser.getSelectedFile();
        if (!fileDich.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            fileDich = new File(fileDich.getParentFile(), fileDich.getName() + ".csv");
        }
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileDich), StandardCharsets.UTF_8))) {
            bw.write('\uFEFF'); // BOM để Microsoft Excel mở tiếng Việt đúng chuẩn UTF-8
            bw.write("Tên danh mục,Tên sản phẩm,ĐVT,Giá nhập,Đơn giá bán,Tồn kho,Trạng thái");
            bw.newLine();
            JOptionPane.showMessageDialog(this,
                    "Đã xuất file mẫu thành công tại:\n" + fileDich.getAbsolutePath(),
                    "Xuất file mẫu", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi ghi file mẫu: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String lamSachO(String s) {
        if (s == null) {
            return "";
        }
        String kq = s.trim();
        if (kq.length() >= 2 && kq.startsWith("\"") && kq.endsWith("\"")) {
            kq = kq.substring(1, kq.length() - 1).trim();
        }
        return kq;
    }

    private static boolean laChuoiSo(String s) {
        String sach = lamSachO(s).replace(".", "").replace(",", "").replace("đ", "").trim();
        if (sach.isEmpty()) {
            return false;
        }
        for (int i = 0; i < sach.length(); i++) {
            char c = sach.charAt(i);
            if (!Character.isDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

    private static double docSoThuc(String s, double macDinh) {
        String sach = lamSachO(s).replace(".", "").replace("đ", "").replace(" ", "").replace(",", ".");
        if (sach.isEmpty()) {
            return macDinh;
        }
        try {
            return Double.parseDouble(sach);
        } catch (NumberFormatException ex) {
            return macDinh;
        }
    }

    // =========================================================================
    // CÁC THÀNH PHẦN GIAO DIỆN TUỲ CHỈNH THEO ĐÚNG ẢNH MẪU
    // =========================================================================

    private record LuaChonDanhMuc(String maDanhMuc, String tenHienThi) {
        @Override
        public String toString() {
            return tenHienThi;
        }
    }

    private enum LoaiIcon {
        NHAP_EXCEL,
        DAU_CONG,
        KINH_LUP,
        LAM_MOI
    }

    /**
     * Thẻ nền trắng bo góc mềm với viền mảnh tinh tế.
     */
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
     * Ô tìm kiếm bo tròn dạng pill với icon kính lúp bên trái.
     */
    private static class KhungTimKiemBoTron extends JPanel {
        private final JTextField txt;
        private boolean dangFocus = false;

        KhungTimKiemBoTron() {
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(320, 38));
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

        JTextField getOvanBan() {
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

    /**
     * Nút nền trắng viền xám nhạt ("↓ Nhập từ file Excel").
     */
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

    /**
     * Nút màu cam chủ đạo ("+ Thêm Sản Phẩm Mới").
     */
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

    /**
     * Nút vuông làm mới (↻) ở góc phải thanh lọc.
     */
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

    /**
     * Nhãn pill xanh nhạt hiển thị "Tổng số: X sản phẩm" ở góc phải thẻ bảng.
     */
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

    /**
     * Bộ vẽ tiêu đề cột của bảng danh sách sản phẩm với nền chuyển sắc xám bạc tinh tế như ảnh mẫu.
     */
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

            if (column == 4) {
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 26));
            } else if (column == 5 || column == 6) {
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
     * Bộ vẽ ô dữ liệu trong bảng:
     * - Cột Đơn Giá Bán: in đậm màu cam (#EA6A1F)
     * - Cột Tồn Kho: in đậm xanh lá (#10B981) nếu > 0, in đậm đỏ (#DC2626) nếu = 0
     * - Cột Trạng Thái: "• Đang bán" xanh lá đậm hoặc "• Ngừng bán" đỏ đậm
     */
    private static class BoVeOSanPham extends DefaultTableCellRenderer {
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
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 18, 0, 12));
                }
                case 1 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_CHINH);
                    setBorder(new EmptyBorder(0, 12, 0, 12));
                }
                case 2, 3 -> {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(MAU_CHU_PHU);
                    setBorder(new EmptyBorder(0, 12, 0, 12));
                }
                case 4 -> {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(MAU_CAM_CHU_DAO);
                    setBorder(new EmptyBorder(0, 12, 0, 26));
                }
                case 5 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    int ton = 0;
                    if (value instanceof Number n) {
                        ton = n.intValue();
                    }
                    setForeground(ton > 0 ? MAU_XANH_LA : MAU_DO_CANH_BAO);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                case 6 -> {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    String chuoi = value != null ? value.toString() : "";
                    setForeground(chuoi.contains("Đang bán") ? MAU_XANH_LA : MAU_DO_CANH_BAO);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                }
            }
            return this;
        }
    }

    /**
     * Bộ vẽ vector sắc nét cho các icon nút bấm (tránh lỗi font ký tự đặc biệt trên Windows).
     */
    private static class BieuTuongNut implements Icon {
        private final LoaiIcon loai;
        private final int kichThuoc;
        private final Color mau;

        BieuTuongNut(LoaiIcon loai, int kichThuoc, Color mau) {
            this.loai = loai;
            this.kichThuoc = kichThuoc;
            this.mau = mau;
        }

        private String layTenFileAnh(LoaiIcon loai) {
            return switch (loai) {
                case NHAP_EXCEL -> "excel.png";
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
            Icon iconImg = IconUtil.layIcon(layTenFileAnh(loai), kichThuoc, kichThuoc, mau);
            if (iconImg != null) {
                iconImg.paintIcon(c, g, x, y);
            }
        }
    }

    /**
     * Cho phép chạy kiểm thử trực tiếp file này mà vẫn hiển thị đầy đủ thanh Header của Quản lý.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyManHinhChinh khungChinh = new QuanLyManHinhChinh();
            khungChinh.chuyenManHinh(QuanLyManHinhChinh.MAN_SAN_PHAM);
            khungChinh.setVisible(true);
        });
    }
}
