package ui;

import entity.ChiTietDungPhuTung;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Modal Xem Chi Tiết Nghiệm Thu Ca Sửa dành cho Kỹ thuật viên.
 * Thiết kế giao diện đúng chuẩn theo hình ảnh mẫu Pengu Garage:
 * - Header: Biểu tượng hóa đơn / checklist + "CHI TIẾT NGHIỆM THU CA SỬA"
 * - Thẻ tóm tắt xe & mã phân công:
 *   + Biển số xe in lớn, badge "ĐÃ HOÀN TẤT" màu xanh lá
 *   + Dòng xe: 🚗 Vios E CVT
 *   + Mã phân công: PC20261104-009
 * - Hiện tượng ban đầu của xe
 * - Kết luận chẩn đoán kỹ thuật
 * - Ghi chú hoàn thành / Bàn giao
 * - Bảng phụ tùng / vật tư đã sử dụng (Tên vật tư, Số lượng, Trạng thái "Đã sử dụng")
 * - Nút "Đóng"
 */
public class KyThuatVienManHinhNhatKyCongViecXemChiTiet extends JDialog {

    private static final Color MAU_NEN_CHINH = new Color(246, 249, 253);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_XANH_LA_NEN = new Color(220, 252, 231);
    private static final Color MAU_XANH_LA_VIEN = new Color(134, 239, 172);
    private static final Color MAU_XANH_LA_CHU = new Color(21, 128, 61);
    private static final Color MAU_XANH_DUONG_LINK = new Color(37, 99, 235);

    private final PhieuPhanCong phieuPhanCong;

    public KyThuatVienManHinhNhatKyCongViecXemChiTiet() {
        this(null, null);
    }

    public KyThuatVienManHinhNhatKyCongViecXemChiTiet(Window owner, PhieuPhanCong ppc) {
        super(owner, "Chi Tiết Nghiệm Thu Ca Sửa", Dialog.ModalityType.APPLICATION_MODAL);
        this.phieuPhanCong = ppc;

        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setSize(520, 680);
        setMinimumSize(new Dimension(480, 600));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN_CHINH);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN));

        JLabel lblIconHeader = new JLabel(IconUtil.layIcon("invoice.png", 18, 18, MAU_XANH_DAM));
        JLabel lblTieuDeHeader = new JLabel("CHI TIẾT NGHIỆM THU CA SỬA");
        lblTieuDeHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTieuDeHeader.setForeground(MAU_XANH_DAM);
        header.add(lblIconHeader);
        header.add(lblTieuDeHeader);
        add(header, BorderLayout.NORTH);

        // Nội dung chính
        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setBorder(new EmptyBorder(14, 18, 14, 18));

        // 1. Thẻ thông tin xe & mã phân công
        pnlNoiDung.add(taoTheThongTinXe());
        pnlNoiDung.add(Box.createVerticalStrut(12));

        // 2. Các ô thông tin chi tiết: Hiện tượng, Chẩn đoán, Ghi chú hoàn thành
        pnlNoiDung.add(taoCumThongTinChiTiet());
        pnlNoiDung.add(Box.createVerticalStrut(12));

        // 3. Bảng phụ tùng / vật tư đã sử dụng
        pnlNoiDung.add(taoBangVatTuDaSuDung());

        JScrollPane cuonTong = new JScrollPane(pnlNoiDung);
        cuonTong.setBorder(null);
        cuonTong.getViewport().setBackground(MAU_NEN_CHINH);
        cuonTong.getVerticalScrollBar().setUnitIncrement(12);
        add(cuonTong, BorderLayout.CENTER);

        // Footer chứa nút Đóng
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

        JButton btnDong = new JButton("Đóng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDong.setForeground(new Color(71, 85, 105));
        btnDong.setFocusPainted(false);
        btnDong.setBorderPainted(false);
        btnDong.setContentAreaFilled(false);
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.setPreferredSize(new Dimension(90, 34));
        btnDong.addActionListener(e -> dispose());

        footer.add(btnDong);
        add(footer, BorderLayout.SOUTH);
    }

    /**
     * Thẻ thông tin xe, biển số, mã phân công & badge ĐÃ HOÀN TẤT
     */
    private JPanel taoTheThongTinXe() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout());
        the.setBorder(new EmptyBorder(12, 14, 12, 14));

        String bienSo = "-";
        String mauXe = "-";
        String maPC = "-";

        if (phieuPhanCong != null) {
            maPC = phieuPhanCong.getMaPhanCong() != null ? phieuPhanCong.getMaPhanCong() : "-";
            PhieuTiepNhan ptn = phieuPhanCong.layPhieuTiepNhan();
            if (ptn != null && ptn.getPhuongTien() != null) {
                PhuongTien pt = ptn.getPhuongTien();
                bienSo = pt.getBienSoXe() != null ? pt.getBienSoXe() : "-";
                mauXe = pt.getMauXe() != null ? pt.getMauXe() : "-";
            }
        }

        // Dòng trên: Biển số in đậm + Badge ĐÃ HOÀN TẤT
        JPanel hang1 = new JPanel(new BorderLayout());
        hang1.setOpaque(false);

        JLabel lblBienSo = new JLabel(bienSo);
        lblBienSo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBienSo.setForeground(MAU_XANH_DAM);

        JLabel lblBadge = new JLabel("ĐÃ HOÀN TẤT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 14, 14);
                g2.setColor(MAU_XANH_LA_NEN);
                g2.fill(rect);
                g2.setColor(MAU_XANH_LA_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(MAU_XANH_LA_CHU);
        lblBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
        lblBadge.setOpaque(false);

        hang1.add(lblBienSo, BorderLayout.WEST);
        hang1.add(lblBadge, BorderLayout.EAST);

        // Dòng dưới: 🚗 Dòng xe | Mã phân công
        JPanel hang2 = new JPanel();
        hang2.setLayout(new BoxLayout(hang2, BoxLayout.Y_AXIS));
        hang2.setOpaque(false);
        hang2.setBorder(new EmptyBorder(4, 0, 0, 0));

        JLabel lblDongXe = new JLabel(mauXe, IconUtil.layIcon("car.png", 14, 14, MAU_CHU_CHINH), SwingConstants.LEFT);
        lblDongXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDongXe.setForeground(MAU_CHU_CHINH);

        JLabel lblMaPC = new JLabel("Mã phân công: " + maPC);
        lblMaPC.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMaPC.setForeground(MAU_XANH_DUONG_LINK);

        hang2.add(lblDongXe);
        hang2.add(Box.createVerticalStrut(2));
        hang2.add(lblMaPC);

        the.add(hang1, BorderLayout.NORTH);
        the.add(hang2, BorderLayout.CENTER);
        return the;
    }

    /**
     * Cụm thông tin chi tiết:
     * - Hiện tượng ban đầu
     * - Kết luận chẩn đoán kỹ thuật
     * - Ghi chú hoàn thành / Bàn giao
     */
    private JPanel taoCumThongTinChiTiet() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout());
        the.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);

        String hienTuong = "-";
        String ketLuan = "-";
        String ghiChu = "-";

        if (phieuPhanCong != null) {
            PhieuTiepNhan ptn = phieuPhanCong.layPhieuTiepNhan();
            if (ptn != null && ptn.getYeuCauKhachHang() != null && !ptn.getYeuCauKhachHang().isBlank()) {
                hienTuong = ptn.getYeuCauKhachHang();
            }
            if (phieuPhanCong.getChiTietBaoGia() != null && phieuPhanCong.getChiTietBaoGia().getPhieuBaoGia() != null
                    && phieuPhanCong.getChiTietBaoGia().getPhieuBaoGia().getGhiChu() != null
                    && !phieuPhanCong.getChiTietBaoGia().getPhieuBaoGia().getGhiChu().isBlank()) {
                ketLuan = phieuPhanCong.getChiTietBaoGia().getPhieuBaoGia().getGhiChu();
            }
            if (phieuPhanCong.getGhiChu() != null && !phieuPhanCong.getGhiChu().isBlank()) {
                ghiChu = phieuPhanCong.getGhiChu();
            }
        }

        pnl.add(taoODoanVan("Hiện tượng ban đầu:", hienTuong, 42));
        pnl.add(Box.createVerticalStrut(8));
        pnl.add(taoODoanVan("Kết luận chẩn đoán kỹ thuật:", ketLuan, 54));
        pnl.add(Box.createVerticalStrut(8));
        pnl.add(taoODoanVan("Ghi chú hoàn thành / Bàn giao:", ghiChu, 54));

        the.add(pnl, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoODoanVan(String tieuDe, String noiDung, int chieuCao) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(tieuDe);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(MAU_CHU_CHINH);

        JTextArea txt = new JTextArea(noiDung);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setForeground(MAU_CHU_CHINH);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setBackground(new Color(248, 250, 252));
        txt.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane cuon = new JScrollPane(txt);
        cuon.setPreferredSize(new Dimension(0, chieuCao));
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));

        p.add(lbl, BorderLayout.NORTH);
        p.add(cuon, BorderLayout.CENTER);
        return p;
    }

    /**
     * Bảng danh sách phụ tùng / vật tư đã sử dụng
     */
    private JPanel taoBangVatTuDaSuDung() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel pnlTieuDe = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlTieuDe.setOpaque(false);
        JLabel lblIcon = new JLabel(IconUtil.layIcon("part.png", 14, 14, MAU_CHU_CHINH));
        JLabel lblTieuDe = new JLabel("Phụ tùng / Vật tư đã sử dụng:");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(MAU_CHU_CHINH);
        pnlTieuDe.add(lblIcon);
        pnlTieuDe.add(lblTieuDe);
        the.add(pnlTieuDe, BorderLayout.NORTH);

        String[] tieuDeCot = {"Tên vật tư", "Số lượng", "Trạng thái"};
        DefaultTableModel modelBang = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bangVatTu = new JTable(modelBang);
        bangVatTu.setRowHeight(34);
        bangVatTu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangVatTu.setForeground(MAU_CHU_CHINH);
        bangVatTu.setGridColor(new Color(235, 238, 242));
        bangVatTu.getTableHeader().setReorderingAllowed(false);
        bangVatTu.getTableHeader().setPreferredSize(new Dimension(0, 30));
        bangVatTu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        bangVatTu.getTableHeader().setBackground(new Color(241, 245, 249));
        bangVatTu.getTableHeader().setForeground(new Color(51, 65, 85));

        TableColumnModel colModel = bangVatTu.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(210);
        colModel.getColumn(1).setPreferredWidth(80);
        colModel.getColumn(2).setPreferredWidth(95);

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        colModel.getColumn(1).setCellRenderer(canGiua);

        // Badge Đã sử dụng
        colModel.getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setForeground(MAU_XANH_LA_CHU);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                return lbl;
            }
        });

        // Nạp danh sách phụ tùng
        List<ChiTietDungPhuTung> dsPhuTung = null;
        if (phieuPhanCong != null && phieuPhanCong.getChiTietBaoGia() != null) {
            dsPhuTung = phieuPhanCong.getChiTietBaoGia().getDanhSachPhuTung();
        }

        if (dsPhuTung != null && !dsPhuTung.isEmpty()) {
            for (ChiTietDungPhuTung pt : dsPhuTung) {
                String ten = (pt.getSanPham() != null) ? pt.getSanPham().getTenSanPham() : "Vật tư kỹ thuật";
                String dvt = (pt.getSanPham() != null && pt.getSanPham().getDonViTinh() != null) ? pt.getSanPham().getDonViTinh() : "Cái";
                modelBang.addRow(new Object[]{ten, pt.getSoLuong() + " " + dvt, "Đã sử dụng"});
            }
        } else {
            modelBang.addRow(new Object[]{"(Không có vật tư / phụ tùng thay thế cho hạng mục này)", "-", "-"});
        }

        JScrollPane cuon = new JScrollPane(bangVatTu);
        cuon.setPreferredSize(new Dimension(0, 110));
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuon.getViewport().setBackground(Color.WHITE);

        the.add(cuon, BorderLayout.CENTER);
        return the;
    }

    /**
     * Thẻ nền trắng bo góc nhẹ hỗ trợ đổ bóng viền
     */
    private static class TheBoGocTrang extends JPanel {
        public TheBoGocTrang(BorderLayout layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D rect = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 10, 10);
            g2.setColor(Color.WHITE);
            g2.fill(rect);
            g2.setColor(MAU_VIEN);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(rect);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KyThuatVienManHinhNhatKyCongViecXemChiTiet dialog = new KyThuatVienManHinhNhatKyCongViecXemChiTiet();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
