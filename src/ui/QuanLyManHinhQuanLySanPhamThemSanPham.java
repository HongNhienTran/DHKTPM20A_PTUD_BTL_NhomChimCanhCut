package ui;

import bus.DanhMucSanPhamBUS;
import bus.SanPhamBUS;
import entity.DanhMucSanPham;
import entity.SanPham;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Modal Thêm mới hoặc Cập nhật Sản phẩm & Phụ tùng dành cho Quản lý.
 * Tách biệt thành class riêng để giữ code của QuanLyManHinhQuanLySanPham luôn tinh gọn và dễ bảo trì.
 */
public class QuanLyManHinhQuanLySanPhamThemSanPham extends JDialog {

    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_XAM = new Color(248, 250, 252);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);

    private final SanPhamBUS sanPhamBUS;
    private final DanhMucSanPhamBUS danhMucBUS;
    private final SanPham sanPhamHienTai;
    private final boolean laChinhSua;

    private JComboBox<DanhMucSanPham> cboDanhMuc;
    private DefaultComboBoxModel<DanhMucSanPham> modelDanhMuc;
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JComboBox<String> cboDonViTinh;
    private JSpinner spnGiaNhap;
    private JSpinner spnDonGiaBan;
    private JSpinner spnThueVAT;
    private JSpinner spnSoLuongTon;
    private JSpinner spnTonToiThieu;
    private JComboBox<String> cboTrangThai;

    private JLabel lblPreviewAnh;
    private String duongDanAnhDaChon = null;

    private boolean daLuuThanhCong = false;

    public QuanLyManHinhQuanLySanPhamThemSanPham(Window cuaSoCha) {
        this(cuaSoCha, null, new SanPhamBUS(), new DanhMucSanPhamBUS());
    }

    public QuanLyManHinhQuanLySanPhamThemSanPham(Window cuaSoCha, SanPhamBUS sanPhamBUS, DanhMucSanPhamBUS danhMucBUS) {
        this(cuaSoCha, null, sanPhamBUS, danhMucBUS);
    }

    public QuanLyManHinhQuanLySanPhamThemSanPham(Window cuaSoCha, SanPham sanPhamHienTai,
                                                SanPhamBUS sanPhamBUS, DanhMucSanPhamBUS danhMucBUS) {
        super(cuaSoCha,
                sanPhamHienTai != null ? "Cập Nhật Sản Phẩm & Phụ Tùng" : "Thêm Sản Phẩm & Phụ Tùng Mới",
                ModalityType.APPLICATION_MODAL);
        this.sanPhamHienTai = sanPhamHienTai;
        this.laChinhSua = (sanPhamHienTai != null);
        this.sanPhamBUS = sanPhamBUS != null ? sanPhamBUS : new SanPhamBUS();
        this.danhMucBUS = danhMucBUS != null ? danhMucBUS : new DanhMucSanPhamBUS();

        khoiTaoGiaoDien();
        dienDuLieuBanDau();
    }

    public boolean isDaLuuThanhCong() {
        return daLuuThanhCong;
    }

    private void khoiTaoGiaoDien() {
        setSize(860, 600);
        setMinimumSize(new Dimension(800, 560));
        setResizable(true);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 1. Header hộp thoại
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(MAU_XANH_DAM);
        pnlHeader.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTieuDe = new JLabel(laChinhSua
                ? "CẬP NHẬT SẢN PHẨM & PHỤ TÙNG"
                : "THÊM SẢN PHẨM & PHỤ TÙNG MỚI");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(Color.WHITE);

        ImageIcon iconHeader = IconUtil.layIcon(laChinhSua ? "edit.png" : "plus.png", 18, 18, Color.WHITE);
        if (iconHeader != null) {
            lblTieuDe.setIcon(iconHeader);
            lblTieuDe.setIconTextGap(10);
        }
        pnlHeader.add(lblTieuDe, BorderLayout.WEST);

        // 2. Thân form gồm Khung ảnh (trái) + Form nhập liệu (phải)
        JPanel pnlThan = new JPanel(new BorderLayout(20, 0));
        pnlThan.setBackground(Color.WHITE);
        pnlThan.setBorder(new EmptyBorder(18, 24, 16, 24));

        // Khung ảnh bên trái
        JPanel pnlKhungAnh = new JPanel(new BorderLayout(0, 10));
        pnlKhungAnh.setOpaque(false);
        pnlKhungAnh.setPreferredSize(new Dimension(190, 0));

        lblPreviewAnh = new JLabel();
        lblPreviewAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewAnh.setVerticalAlignment(SwingConstants.CENTER);
        lblPreviewAnh.setBackground(new Color(248, 250, 252));
        lblPreviewAnh.setOpaque(true);
        lblPreviewAnh.setPreferredSize(new Dimension(185, 160));
        lblPreviewAnh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));

        JPanel pnlNutAnh = new JPanel(new GridLayout(2, 1, 0, 6));
        pnlNutAnh.setOpaque(false);

        JButton btnChonAnh = new JButton("Chọn ảnh...", IconUtil.layIcon("part.png", 14, 14, new Color(30, 58, 138)));
        btnChonAnh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnChonAnh.setForeground(new Color(30, 58, 138));
        btnChonAnh.setBackground(new Color(239, 246, 255));
        btnChonAnh.setFocusPainted(false);
        btnChonAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChonAnh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        btnChonAnh.addActionListener(e -> xuLyChonAnhMoi());

        JButton btnXoaAnh = new JButton("Bỏ ảnh", IconUtil.layIcon("close.png", 12, 12, MAU_DO_CANH_BAO));
        btnXoaAnh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnXoaAnh.setForeground(MAU_DO_CANH_BAO);
        btnXoaAnh.setBackground(Color.WHITE);
        btnXoaAnh.setFocusPainted(false);
        btnXoaAnh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXoaAnh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        btnXoaAnh.addActionListener(e -> {
            duongDanAnhDaChon = null;
            capNhatAnhHienThi(null);
        });

        pnlNutAnh.add(btnChonAnh);
        pnlNutAnh.add(btnXoaAnh);

        pnlKhungAnh.add(lblPreviewAnh, BorderLayout.NORTH);
        pnlKhungAnh.add(pnlNutAnh, BorderLayout.CENTER);

        // Form nhập liệu bên phải
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ComboBox Danh mục + Nút thêm danh mục nhanh
        modelDanhMuc = new DefaultComboBoxModel<>();
        try {
            for (DanhMucSanPham dm : danhMucBUS.layTatCa()) {
                modelDanhMuc.addElement(dm);
            }
        } catch (Exception ignored) {
        }

        cboDanhMuc = new JComboBox<>(modelDanhMuc);
        cboDanhMuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDanhMuc.setPreferredSize(new Dimension(240, 36));
        cboDanhMuc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DanhMucSanPham dm) {
                    lbl.setText(dm.getMaDanhMuc() + " - " + dm.getTenDanhMuc());
                } else {
                    lbl.setText("-- Chưa có danh mục --");
                }
                lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
                return lbl;
            }
        });

        txtMaSP = taoOForm(false);
        txtMaSP.setEditable(false);
        txtMaSP.setBackground(new Color(241, 245, 249));

        Runnable capNhatMaTuDong = () -> {
            if (!laChinhSua) {
                DanhMucSanPham dm = (DanhMucSanPham) cboDanhMuc.getSelectedItem();
                if (dm != null) {
                    try {
                        txtMaSP.setText(sanPhamBUS.phatSinhMaSanPham(dm.getMaDanhMuc()));
                    } catch (Exception ex) {
                        txtMaSP.setText("");
                    }
                } else {
                    txtMaSP.setText("Vui lòng tạo danh mục trước");
                }
            }
        };
        cboDanhMuc.addActionListener(e -> capNhatMaTuDong.run());

        JButton btnThemDMNhanh = new JButton("+ Danh mục");
        btnThemDMNhanh.setFocusPainted(false);
        btnThemDMNhanh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemDMNhanh.setBackground(new Color(239, 246, 255));
        btnThemDMNhanh.setForeground(new Color(29, 78, 216));
        btnThemDMNhanh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnThemDMNhanh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemDMNhanh.addActionListener(e -> {
            DanhMucSanPham dmMoi = moHopThoaiThemDanhMucNhanh();
            if (dmMoi != null) {
                modelDanhMuc.addElement(dmMoi);
                cboDanhMuc.setSelectedItem(dmMoi);
                capNhatMaTuDong.run();
            }
        });

        JPanel hangDanhMuc = new JPanel(new BorderLayout(8, 0));
        hangDanhMuc.setOpaque(false);
        hangDanhMuc.add(cboDanhMuc, BorderLayout.CENTER);
        if (!laChinhSua) {
            hangDanhMuc.add(btnThemDMNhanh, BorderLayout.EAST);
        }

        txtTenSP = taoOForm(true);

        cboDonViTinh = new JComboBox<>(new String[]{"Cái", "Bộ", "Bình", "Lít", "Hộp", "Chai", "Cặp", "Lon"});
        cboDonViTinh.setEditable(true);
        cboDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboDonViTinh.setPreferredSize(new Dimension(140, 36));

        spnGiaNhap = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 10_000.0));
        spnDonGiaBan = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 10_000.0));
        spnThueVAT = new JSpinner(new SpinnerNumberModel(8.0, 0.0, 100.0, 1.0));
        spnSoLuongTon = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        spnTonToiThieu = new JSpinner(new SpinnerNumberModel(5, 0, 1_000_000, 1));

        dinhDangSpinnerSo(spnGiaNhap, "#,##0");
        dinhDangSpinnerSo(spnDonGiaBan, "#,##0");
        dinhDangSpinnerSo(spnThueVAT, "#0.##");
        dinhDangSpinnerSo(spnSoLuongTon, "#,##0");
        dinhDangSpinnerSo(spnTonToiThieu, "#,##0");

        cboTrangThai = new JComboBox<>(new String[]{"Đang bán", "Ngừng bán"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTrangThai.setPreferredSize(new Dimension(140, 36));

        // Bố trí các dòng form
        themDongForm(pnlForm, gbc, 0, "Danh mục (*):", hangDanhMuc);
        themDongForm(pnlForm, gbc, 1, "Mã sản phẩm:", txtMaSP);
        themDongForm(pnlForm, gbc, 2, "Tên sản phẩm (*):", txtTenSP);

        JPanel hangDvtVat = taoHangHaiCot("ĐVT (*):", cboDonViTinh, "Thuế VAT (%):", spnThueVAT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        pnlForm.add(hangDvtVat, gbc);

        JPanel hangGia = taoHangHaiCot("Giá nhập (VNĐ):", spnGiaNhap, "Đơn giá bán (*):", spnDonGiaBan);
        gbc.gridy = 4;
        pnlForm.add(hangGia, gbc);

        JPanel hangTon = taoHangHaiCot("Số lượng tồn:", spnSoLuongTon, "Tồn tối thiểu:", spnTonToiThieu);
        gbc.gridy = 5;
        pnlForm.add(hangTon, gbc);

        gbc.gridwidth = 1;
        themDongForm(pnlForm, gbc, 6, "Trạng thái:", cboTrangThai);

        // 3. Chân hộp thoại
        JPanel pnlChan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pnlChan.setBackground(MAU_NEN_XAM);
        pnlChan.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

        JButton btnHuy = new JButton("Hủy bỏ");
        btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHuy.setForeground(MAU_CHU_CHINH);
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuy.setPreferredSize(new Dimension(100, 36));
        btnHuy.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        btnHuy.addActionListener(e -> dispose());

        JButton btnLuu = new JButton(laChinhSua ? "Lưu Cập Nhật" : "Thêm Sản Phẩm");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBackground(MAU_CAM_CHU_DAO);
        btnLuu.setFocusPainted(false);
        btnLuu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLuu.setPreferredSize(new Dimension(laChinhSua ? 130 : 140, 36));
        btnLuu.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        ImageIcon iconSave = IconUtil.layIcon("save.png", 14, 14, Color.WHITE);
        if (iconSave != null) {
            btnLuu.setIcon(iconSave);
            btnLuu.setIconTextGap(8);
        }
        btnLuu.addActionListener(e -> xuLyLuu());

        pnlChan.add(btnHuy);
        pnlChan.add(btnLuu);

        pnlThan.add(pnlForm, BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlThan, BorderLayout.CENTER);
        add(pnlChan, BorderLayout.SOUTH);
    }

    private void dienDuLieuBanDau() {
        if (laChinhSua && sanPhamHienTai != null) {
            if (sanPhamHienTai.getDanhMucSanPham() != null) {
                cboDanhMuc.setSelectedItem(sanPhamHienTai.getDanhMucSanPham());
            }
            cboDanhMuc.setEnabled(false);
            txtMaSP.setText(sanPhamHienTai.getMaSanPham());
            txtTenSP.setText(sanPhamHienTai.getTenSanPham());
            cboDonViTinh.setSelectedItem(sanPhamHienTai.getDonViTinh());
            spnGiaNhap.setValue(sanPhamHienTai.getGiaNhap());
            spnDonGiaBan.setValue(sanPhamHienTai.getDonGiaBan());
            spnThueVAT.setValue(sanPhamHienTai.getThueVAT());
            spnSoLuongTon.setValue(sanPhamHienTai.getSoLuongTon());
            spnTonToiThieu.setValue(sanPhamHienTai.getSoLuongTonToiThieu());
            cboTrangThai.setSelectedIndex(sanPhamHienTai.isTrangThai() ? 0 : 1);
            duongDanAnhDaChon = sanPhamHienTai.getAnhSanPham();
            capNhatAnhHienThi(duongDanAnhDaChon);
        } else {
            capNhatAnhHienThi(null);
            // Tự động phát sinh mã theo danh mục đầu tiên
            DanhMucSanPham dm = (DanhMucSanPham) cboDanhMuc.getSelectedItem();
            if (dm != null) {
                try {
                    txtMaSP.setText(sanPhamBUS.phatSinhMaSanPham(dm.getMaDanhMuc()));
                } catch (Exception ex) {
                    txtMaSP.setText("");
                }
            }
        }
    }

    private void xuLyLuu() {
        try {
            DanhMucSanPham dmChon = (DanhMucSanPham) cboDanhMuc.getSelectedItem();
            if (dmChon == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn hoặc nhấn '+ Danh mục' để tạo danh mục sản phẩm trước!",
                        "Thiếu danh mục", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tenSP = txtTenSP.getText() != null ? txtTenSP.getText().trim() : "";
            if (tenSP.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Tên sản phẩm không được để trống!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtTenSP.requestFocus();
                return;
            }

            Object dvtObj = cboDonViTinh.getEditor().getItem();
            String dvt = dvtObj != null ? dvtObj.toString().trim() : "";
            if (dvt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Đơn vị tính không được để trống!",
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

            if (laChinhSua) {
                sanPhamHienTai.setTenSanPham(tenSP);
                sanPhamHienTai.setDonViTinh(dvt);
                sanPhamHienTai.setGiaNhap(giaNhap);
                sanPhamHienTai.setDonGiaBan(donGiaBan);
                sanPhamHienTai.setThueVAT(vat);
                sanPhamHienTai.setSoLuongTon(ton);
                sanPhamHienTai.setSoLuongTonToiThieu(tonMin);
                sanPhamHienTai.setTrangThai(dangBan);
                sanPhamHienTai.setAnhSanPham(duongDanAnhDaChon);

                if (sanPhamBUS.capNhatSanPham(sanPhamHienTai)) {
                    daLuuThanhCong = true;
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật sản phẩm '" + tenSP + "' thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            } else {
                SanPham spMoi = new SanPham();
                spMoi.setDanhMucSanPham(dmChon);
                spMoi.setTenSanPham(tenSP);
                spMoi.setDonViTinh(dvt);
                spMoi.setGiaNhap(giaNhap);
                spMoi.setDonGiaBan(donGiaBan);
                spMoi.setThueVAT(vat);
                spMoi.setSoLuongTon(ton);
                spMoi.setSoLuongTonToiThieu(tonMin);
                spMoi.setTrangThai(dangBan);
                spMoi.setAnhSanPham(duongDanAnhDaChon);

                if (sanPhamBUS.themSanPham(spMoi)) {
                    daLuuThanhCong = true;
                    JOptionPane.showMessageDialog(this,
                            "Thêm sản phẩm mới '" + tenSP + "' thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể lưu sản phẩm", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void capNhatAnhHienThi(String path) {
        if (lblPreviewAnh == null) return;
        ImageIcon icon = IconUtil.layAnhSanPham(path, 175, 145);
        if (icon != null) {
            lblPreviewAnh.setIcon(icon);
            lblPreviewAnh.setText("");
        } else {
            lblPreviewAnh.setIcon(null);
            lblPreviewAnh.setText("<html><center style='color:#94A3B8;'>Chưa chọn<br>hình ảnh</center></html>");
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
            duongDanAnhDaChon = path;
            capNhatAnhHienThi(duongDanAnhDaChon);
        }
    }

    private DanhMucSanPham moHopThoaiThemDanhMucNhanh() {
        JTextField txtTenDM = taoOForm(true);
        JTextField txtMoTa = taoOForm(true);

        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String maDuKien = "";
        try {
            maDuKien = danhMucBUS.phatSinhMaDanhMuc();
        } catch (Exception ignored) {
        }

        JTextField txtMa = taoOForm(false);
        txtMa.setEditable(false);
        txtMa.setBackground(new Color(241, 245, 249));
        txtMa.setText(maDuKien);

        themDongForm(pnl, gbc, 0, "Mã danh mục:", txtMa);
        themDongForm(pnl, gbc, 1, "Tên danh mục (*):", txtTenDM);
        themDongForm(pnl, gbc, 2, "Mô tả:", txtMoTa);

        int chon = JOptionPane.showConfirmDialog(
                this,
                pnl,
                "Tạo Danh Mục Phụ Tùng Mới",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (chon == JOptionPane.OK_OPTION) {
            try {
                String ten = txtTenDM.getText() != null ? txtTenDM.getText().trim() : "";
                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                DanhMucSanPham dm = new DanhMucSanPham();
                dm.setTenDanhMuc(ten);
                dm.setMoTa(txtMoTa.getText() != null ? txtMoTa.getText().trim() : "");
                dm.setTrangThai(true);
                if (danhMucBUS.themDanhMuc(dm)) {
                    return dm;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Không thể thêm danh mục", JOptionPane.WARNING_MESSAGE);
            }
        }
        return null;
    }

    private JTextField taoOForm(boolean choPhepSua) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(240, 36));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txt.setEditable(choPhepSua);
        return txt;
    }

    private void themDongForm(JPanel form, GridBagConstraints gbc, int hang, String nhan, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = hang;
        gbc.weightx = 0.0;
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MAU_CHU_CHINH);
        lbl.setPreferredSize(new Dimension(120, 26));
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        form.add(comp, gbc);
    }

    private JPanel taoHangHaiCot(String nhan1, Component comp1, String nhan2, Component comp2) {
        JPanel hang = new JPanel(new GridBagLayout());
        hang.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 0, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.weightx = 0.0;
        JLabel lbl1 = new JLabel(nhan1);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl1.setForeground(MAU_CHU_CHINH);
        lbl1.setPreferredSize(new Dimension(120, 26));
        hang.add(lbl1, g);

        g.gridx = 1;
        g.weightx = 0.5;
        hang.add(comp1, g);

        g.gridx = 2;
        g.weightx = 0.0;
        g.insets = new Insets(0, 10, 0, 6);
        JLabel lbl2 = new JLabel(nhan2);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl2.setForeground(MAU_CHU_CHINH);
        lbl2.setPreferredSize(new Dimension(100, 26));
        hang.add(lbl2, g);

        g.gridx = 3;
        g.weightx = 0.5;
        g.insets = new Insets(0, 0, 0, 0);
        hang.add(comp2, g);

        return hang;
    }

    private void dinhDangSpinnerSo(JSpinner spinner, String mauDinhDang) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(120, 36));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        JFormattedTextField tf = editor.getTextField();
        tf.setHorizontalAlignment(JTextField.RIGHT);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat(mauDinhDang, symbols);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(spinner.getValue().getClass());
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);
        tf.setFormatterFactory(new DefaultFormatterFactory(formatter));
    }
}
