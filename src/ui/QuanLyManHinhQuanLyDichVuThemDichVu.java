package ui;

import bus.DichVuBUS;
import entity.DichVu;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
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
import java.awt.Insets;
import java.awt.Window;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Modal Thêm mới hoặc Cập nhật Dịch vụ Kỹ thuật & Sửa chữa dành cho Quản lý.
 * Tách biệt thành class riêng để giữ code của QuanLyManHinhQuanLyDichVu tinh gọn và chuẩn module.
 */
public class QuanLyManHinhQuanLyDichVuThemDichVu extends JDialog {

    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_XAM = new Color(248, 250, 252);

    private final DichVuBUS dichVuBUS;
    private final DichVu dichVuHienTai;
    private final boolean laChinhSua;

    private JTextField txtMaDV;
    private JTextField txtTenDV;
    private JTextArea txtMoTa;
    private JSpinner spnDonGia;
    private JSpinner spnThueVAT;
    private JComboBox<String> cboTrangThai;

    private boolean daLuuThanhCong = false;

    public QuanLyManHinhQuanLyDichVuThemDichVu(Window cuaSoCha) {
        this(cuaSoCha, null, new DichVuBUS());
    }

    public QuanLyManHinhQuanLyDichVuThemDichVu(Window cuaSoCha, DichVuBUS dichVuBUS) {
        this(cuaSoCha, null, dichVuBUS);
    }

    public QuanLyManHinhQuanLyDichVuThemDichVu(Window cuaSoCha, DichVu dichVuHienTai, DichVuBUS dichVuBUS) {
        super(cuaSoCha,
                dichVuHienTai != null ? "Cập Nhật Dịch Vụ Kỹ Thuật" : "Thêm Dịch Vụ Sửa Chữa & Bảo Dưỡng Mới",
                ModalityType.APPLICATION_MODAL);
        this.dichVuHienTai = dichVuHienTai;
        this.laChinhSua = (dichVuHienTai != null);
        this.dichVuBUS = dichVuBUS != null ? dichVuBUS : new DichVuBUS();

        khoiTaoGiaoDien();
        dienDuLieuBanDau();
    }

    public boolean isDaLuuThanhCong() {
        return daLuuThanhCong;
    }

    private void khoiTaoGiaoDien() {
        setSize(750, 600);
        setMinimumSize(new Dimension(660, 520));
        setResizable(true);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 1. Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(MAU_XANH_DAM);
        pnlHeader.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTieuDe = new JLabel(laChinhSua
                ? "CẬP NHẬT DỊCH VỤ (" + dichVuHienTai.getMaDichVu() + ")"
                : "THÊM DỊCH VỤ KỸ THUẬT MỚI");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(Color.WHITE);

        ImageIcon icon = IconUtil.layIcon(laChinhSua ? "edit.png" : "plus.png", 18, 18, Color.WHITE);
        if (icon != null) {
            lblTieuDe.setIcon(icon);
            lblTieuDe.setIconTextGap(10);
        }
        pnlHeader.add(lblTieuDe, BorderLayout.WEST);

        // 2. Form nhập liệu
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(18, 24, 16, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaDV = taoOForm(false);
        txtMaDV.setEditable(false);
        txtMaDV.setBackground(new Color(241, 245, 249));

        txtTenDV = taoOForm(true);

        txtMoTa = new JTextArea(4, 24);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMoTa.setForeground(MAU_CHU_CHINH);
        txtMoTa.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane cuonMoTa = new JScrollPane(txtMoTa);
        cuonMoTa.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cuonMoTa.setPreferredSize(new Dimension(380, 110));

        spnDonGia = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 10_000.0));
        spnThueVAT = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 100.0, 0.5));
        dinhDangSpinnerSo(spnDonGia, "#,##0");
        dinhDangSpinnerSo(spnThueVAT, "#0.0");

        cboTrangThai = new JComboBox<>(new String[]{"Đang áp dụng", "Ngừng áp dụng"});
        cboTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboTrangThai.setPreferredSize(new Dimension(200, 36));

        themDongForm(pnlForm, gbc, 0, "Mã dịch vụ:", txtMaDV);
        themDongForm(pnlForm, gbc, 1, "Tên dịch vụ (*):", txtTenDV);
        themDongForm(pnlForm, gbc, 2, "Mô tả kỹ thuật:", cuonMoTa);

        JPanel hangGiaVat = taoHangHaiCot("Đơn giá (VNĐ):", spnDonGia, "Thuế VAT (%):", spnThueVAT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        pnlForm.add(hangGiaVat, gbc);

        gbc.gridwidth = 1;
        themDongForm(pnlForm, gbc, 4, "Trạng thái:", cboTrangThai);

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

        JButton btnLuu = new JButton(laChinhSua ? "Lưu Cập Nhật" : "Thêm Dịch Vụ");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBackground(MAU_CAM_CHU_DAO);
        btnLuu.setFocusPainted(false);
        btnLuu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLuu.setPreferredSize(new Dimension(laChinhSua ? 130 : 130, 36));
        btnLuu.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        ImageIcon iconSave = IconUtil.layIcon("save.png", 14, 14, Color.WHITE);
        if (iconSave != null) {
            btnLuu.setIcon(iconSave);
            btnLuu.setIconTextGap(8);
        }
        btnLuu.addActionListener(e -> xuLyLuu());

        pnlChan.add(btnHuy);
        pnlChan.add(btnLuu);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlForm, BorderLayout.CENTER);
        add(pnlChan, BorderLayout.SOUTH);
    }

    private void dienDuLieuBanDau() {
        if (laChinhSua && dichVuHienTai != null) {
            txtMaDV.setText(dichVuHienTai.getMaDichVu());
            txtTenDV.setText(dichVuHienTai.getTenDichVu());
            txtMoTa.setText(dichVuHienTai.getMoTa() != null ? dichVuHienTai.getMoTa() : "");
            spnDonGia.setValue(dichVuHienTai.getDonGia());
            spnThueVAT.setValue(dichVuHienTai.getThueVAT());
            cboTrangThai.setSelectedIndex(dichVuHienTai.isTrangThai() ? 0 : 1);
        } else {
            try {
                txtMaDV.setText(dichVuBUS.phatSinhMaDichVu());
            } catch (Exception ex) {
                txtMaDV.setText("DV001");
            }
        }
    }

    private void xuLyLuu() {
        try {
            String ten = txtTenDV.getText() != null ? txtTenDV.getText().trim() : "";
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Tên dịch vụ không được để trống!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtTenDV.requestFocus();
                return;
            }

            String moTa = txtMoTa.getText() != null ? txtMoTa.getText().trim() : "";
            double donGia = ((Number) spnDonGia.getValue()).doubleValue();
            double vat = ((Number) spnThueVAT.getValue()).doubleValue();
            boolean dangApDung = cboTrangThai.getSelectedIndex() == 0;

            if (laChinhSua) {
                dichVuHienTai.setTenDichVu(ten);
                dichVuHienTai.setMoTa(moTa);
                dichVuHienTai.setDonGia(donGia);
                dichVuHienTai.setThueVAT(vat);
                dichVuHienTai.setTrangThai(dangApDung);

                if (dichVuBUS.capNhatDichVu(dichVuHienTai)) {
                    daLuuThanhCong = true;
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật dịch vụ '" + ten + "' thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            } else {
                DichVu dvMoi = new DichVu();
                dvMoi.setTenDichVu(ten);
                dvMoi.setMoTa(moTa);
                dvMoi.setDonGia(donGia);
                dvMoi.setThueVAT(vat);
                dvMoi.setTrangThai(dangApDung);

                if (dichVuBUS.themDichVu(dvMoi)) {
                    daLuuThanhCong = true;
                    JOptionPane.showMessageDialog(this,
                            "Thêm dịch vụ mới '" + ten + "' thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không thể lưu dịch vụ", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JTextField taoOForm(boolean choPhepSua) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(380, 36));
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
        lbl.setPreferredSize(new Dimension(110, 26));
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
        lbl1.setPreferredSize(new Dimension(110, 26));
        hang.add(lbl1, g);

        g.gridx = 1;
        g.weightx = 0.55;
        hang.add(comp1, g);

        g.gridx = 2;
        g.weightx = 0.0;
        g.insets = new Insets(0, 10, 0, 6);
        JLabel lbl2 = new JLabel(nhan2);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl2.setForeground(MAU_CHU_CHINH);
        lbl2.setPreferredSize(new Dimension(90, 26));
        hang.add(lbl2, g);

        g.gridx = 3;
        g.weightx = 0.45;
        g.insets = new Insets(0, 0, 0, 0);
        hang.add(comp2, g);

        return hang;
    }

    private void dinhDangSpinnerSo(JSpinner spinner, String mauDinhDang) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(170, 36));
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

