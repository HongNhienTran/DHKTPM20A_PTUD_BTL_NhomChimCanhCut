package ui;

import bus.HangXeBUS;
import bus.KhachHangBUS;
import bus.PhieuTiepNhanBUS;
import bus.PhuongTienBUS;
import entity.KhachHang;
import entity.PhuongTien;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal hiển thị danh sách toàn bộ phương tiện thuộc quyền sở hữu của khách hàng.
 * Tách biệt hoàn toàn khỏi form nhập liệu bên phải của Quản Lý Khách Hàng để hiển thị bảng rộng rãi,
 * đầy đủ thông tin kỹ thuật của xe, đồng thời hỗ trợ nút "Thêm Phương Tiện Mới" trực tiếp.
 */
public class QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien extends JDialog {

    private static final Color MAU_NEN_TRANG = new Color(248, 250, 252);
    private static final Color MAU_THE_TRANG = Color.WHITE;
    private static final Color MAU_VIEN_THE = new Color(226, 232, 240);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(71, 85, 105);
    private static final Color MAU_CHU_MO = new Color(148, 163, 184);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 132, 52);
    private static final Color MAU_CHON_DONG = new Color(235, 242, 250);
    private static final Color MAU_KE_BANG = new Color(241, 245, 249);
    private static final Color MAU_DO_CANH_BAO = new Color(220, 38, 38);

    private final KhachHang khachHang;
    private final KhachHangBUS khachHangBUS;
    private final PhuongTienBUS phuongTienBUS;
    private final HangXeBUS hangXeBUS;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS;

    private List<PhuongTien> danhSachPhuongTien = new ArrayList<>();
    private DefaultTableModel moHinhBang;
    private JTable bangPhuongTien;
    private BadgeVienXanh lblBadgeTongSo;
    private boolean coThayDoi = false;

    public QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien(Window cuaSoCha, KhachHang khachHang) {
        this(cuaSoCha, khachHang, new KhachHangBUS(), new PhuongTienBUS(), new HangXeBUS(), new PhieuTiepNhanBUS());
    }

    public QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien(Window cuaSoCha, KhachHang khachHang,
                                                             KhachHangBUS khachHangBUS, PhuongTienBUS phuongTienBUS) {
        this(cuaSoCha, khachHang, khachHangBUS, phuongTienBUS, new HangXeBUS(), new PhieuTiepNhanBUS());
    }

    public QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien(Window cuaSoCha, KhachHang khachHang,
                                                             KhachHangBUS khachHangBUS, PhuongTienBUS phuongTienBUS,
                                                             HangXeBUS hangXeBUS, PhieuTiepNhanBUS phieuTiepNhanBUS) {
        super(cuaSoCha, "Danh Sách Phương Tiện Sở Hữu - " + (khachHang != null ? khachHang.getHoTen() : ""),
                ModalityType.APPLICATION_MODAL);
        this.khachHang = khachHang;
        this.khachHangBUS = khachHangBUS != null ? khachHangBUS : new KhachHangBUS();
        this.phuongTienBUS = phuongTienBUS != null ? phuongTienBUS : new PhuongTienBUS();
        this.hangXeBUS = hangXeBUS != null ? hangXeBUS : new HangXeBUS();
        this.phieuTiepNhanBUS = phieuTiepNhanBUS != null ? phieuTiepNhanBUS : new PhieuTiepNhanBUS();

        khoiTaoGiaoDien();
        taiDanhSachPhuongTien();
    }

    public boolean isCoThayDoi() {
        return coThayDoi;
    }

    private void khoiTaoGiaoDien() {
        setSize(1140, 720);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(0, 14));
        contentPane.setBackground(MAU_NEN_TRANG);
        contentPane.setBorder(new EmptyBorder(18, 22, 18, 22));
        setContentPane(contentPane);

        // 1. Phần đỉnh: Tiêu đề + Thẻ tóm tắt thông tin khách hàng + Cụm nút thao tác
        contentPane.add(taoKhuVucDauTrang(), BorderLayout.NORTH);

        // 2. Phần giữa: Thẻ bảng danh sách phương tiện sở hữu
        contentPane.add(taoTheBangPhuongTien(), BorderLayout.CENTER);

        // 3. Phần chân: Gợi ý thao tác & nút Đóng
        contentPane.add(taoThanhChanTrang(), BorderLayout.SOUTH);
    }

    private JPanel taoKhuVucDauTrang() {
        JPanel khuVucDau = new JPanel(new BorderLayout(0, 12));
        khuVucDau.setOpaque(false);

        // Hàng trên cùng: Tiêu đề + Nút thêm phương tiện & Đóng
        JPanel hangTieuDe = new JPanel(new BorderLayout());
        hangTieuDe.setOpaque(false);

        JPanel cumTieuDeTrai = new JPanel();
        cumTieuDeTrai.setLayout(new BoxLayout(cumTieuDeTrai, BoxLayout.Y_AXIS));
        cumTieuDeTrai.setOpaque(false);

        JLabel lblTieuDe = new JLabel("DANH SÁCH PHƯƠNG TIỆN SỞ HỮU");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        JLabel lblPhu = new JLabel("Quản lý và theo dõi các phương tiện cơ giới thuộc quyền sở hữu của khách hàng");
        lblPhu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhu.setForeground(MAU_CHU_PHU);

        cumTieuDeTrai.add(lblTieuDe);
        cumTieuDeTrai.add(Box.createVerticalStrut(2));
        cumTieuDeTrai.add(lblPhu);

        // Cụm nút phải
        JPanel cumNutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cumNutPhai.setOpaque(false);

        NutCamChuDao btnThemXe = new NutCamChuDao("+ Thêm Phương Tiện Mới");
        btnThemXe.setPreferredSize(new Dimension(190, 38));
        btnThemXe.addActionListener(e -> xuLyThemPhuongTienMoi());

        cumNutPhai.add(btnThemXe);

        hangTieuDe.add(cumTieuDeTrai, BorderLayout.WEST);
        hangTieuDe.add(cumNutPhai, BorderLayout.EAST);

        // Hàng tóm tắt hồ sơ khách hàng
        TheBoGoc theKhachHang = new TheBoGoc(12, MAU_THE_TRANG, MAU_VIEN_THE);
        theKhachHang.setLayout(new GridLayout(1, 4, 16, 0));
        theKhachHang.setBorder(new EmptyBorder(10, 16, 10, 16));

        String maKH = khachHang != null && khachHang.getMaKhachHang() != null ? khachHang.getMaKhachHang() : "---";
        String hoTen = khachHang != null && khachHang.getHoTen() != null ? khachHang.getHoTen() : "---";
        String sdt = khachHang != null && khachHang.getSoDienThoai() != null ? khachHang.getSoDienThoai() : "---";
        String diaChi = khachHang != null && khachHang.getDiaChi() != null ? khachHang.getDiaChi() : "---";

        theKhachHang.add(taoItemThongTin("MÃ KHÁCH HÀNG", maKH, MAU_CAM_CHU_DAO));
        theKhachHang.add(taoItemThongTin("HỌ VÀ TÊN CHỦ SỞ HỮU", hoTen, MAU_XANH_DAM));
        theKhachHang.add(taoItemThongTin("SỐ ĐIỆN THOẠI", sdt, MAU_CHU_CHINH));
        theKhachHang.add(taoItemThongTin("ĐỊA CHỈ", diaChi, MAU_CHU_PHU));

        khuVucDau.add(hangTieuDe, BorderLayout.NORTH);
        khuVucDau.add(theKhachHang, BorderLayout.CENTER);
        return khuVucDau;
    }

    private JPanel taoItemThongTin(String nhanNho, String giaTri, Color mauGiaTri) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lblNhan = new JLabel(nhanNho);
        lblNhan.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblNhan.setForeground(MAU_CHU_MO);

        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGiaTri.setForeground(mauGiaTri);
        lblGiaTri.setToolTipText(giaTri);

        p.add(lblNhan);
        p.add(Box.createVerticalStrut(3));
        p.add(lblGiaTri);
        return p;
    }

    private JPanel taoTheBangPhuongTien() {
        TheBoGoc the = new TheBoGoc(14, MAU_THE_TRANG, MAU_VIEN_THE);
        the.setLayout(new BorderLayout());

        // Đầu thẻ: Tiêu đề + Badge tổng số
        JPanel dauThe = new JPanel(new BorderLayout());
        dauThe.setOpaque(false);
        dauThe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_KE_BANG),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTieuDeBang = new JLabel("Danh Sách Xe Đã Đăng Ký");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);

        lblBadgeTongSo = new BadgeVienXanh("Tổng số: 0 phương tiện", new Color(30, 58, 138));

        dauThe.add(lblTieuDeBang, BorderLayout.WEST);
        dauThe.add(lblBadgeTongSo, BorderLayout.EAST);

        // Mô hình bảng 8 cột
        String[] tenCot = {
                "STT",
                "Mã PT",
                "Biển Số Xe",
                "Hãng Xe",
                "Dòng / Mẫu Xe",
                "Màu Sắc",
                "Năm SX",
                "Nhiên Liệu"
        };

        moHinhBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bangPhuongTien = new JTable(moHinhBang);
        bangPhuongTien.setRowHeight(38);
        bangPhuongTien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bangPhuongTien.setForeground(MAU_CHU_CHINH);
        bangPhuongTien.setBackground(Color.WHITE);
        bangPhuongTien.setSelectionBackground(MAU_CHON_DONG);
        bangPhuongTien.setSelectionForeground(MAU_XANH_DAM);
        bangPhuongTien.setShowVerticalLines(false);
        bangPhuongTien.setShowHorizontalLines(true);
        bangPhuongTien.setGridColor(MAU_KE_BANG);
        bangPhuongTien.setFillsViewportHeight(true);

        JTableHeader header = bangPhuongTien.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new BoVeTieuDeBang());

        TableColumnModel colModel = bangPhuongTien.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(45);
        colModel.getColumn(1).setPreferredWidth(95);
        colModel.getColumn(2).setPreferredWidth(120);
        colModel.getColumn(3).setPreferredWidth(110);
        colModel.getColumn(4).setPreferredWidth(150);
        colModel.getColumn(5).setPreferredWidth(95);
        colModel.getColumn(6).setPreferredWidth(75);
        colModel.getColumn(7).setPreferredWidth(100);

        BoVeOPhuongTien renderer = new BoVeOPhuongTien();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(renderer);
        }

        // Menu ngữ cảnh chuột phải & thao tác double click
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnuXemChiTiet = new JMenuItem("Xem & Cập nhật chi tiết xe");
        mnuXemChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mnuXemChiTiet.addActionListener(e -> xuLyXemChiTietXeDangChon());

        JMenuItem mnuXoaXe = new JMenuItem("Xóa phương tiện khỏi khách hàng");
        mnuXoaXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mnuXoaXe.setForeground(MAU_DO_CANH_BAO);
        mnuXoaXe.addActionListener(e -> xuLyXoaXeDangChon());

        popupMenu.add(mnuXemChiTiet);
        popupMenu.addSeparator();
        popupMenu.add(mnuXoaXe);

        bangPhuongTien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    xuLyXemChiTietXeDangChon();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                hienPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                hienPopup(e);
            }

            private void hienPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int r = bangPhuongTien.rowAtPoint(e.getPoint());
                    if (r >= 0 && r < bangPhuongTien.getRowCount()) {
                        bangPhuongTien.setRowSelectionInterval(r, r);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
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

    private JPanel taoThanhChanTrang() {
        JPanel chanTrang = new JPanel(new BorderLayout());
        chanTrang.setOpaque(false);

        JLabel lblHuongDan = new JLabel(
                "Mẹo: Nhấp đúp vào dòng phương tiện hoặc nhấp chuột phải để xem chi tiết & lịch sử sửa chữa.",
                IconUtil.layIcon("help.png", 14, 14, MAU_CHU_MO),
                SwingConstants.LEFT
        );
        lblHuongDan.setIconTextGap(6);
        lblHuongDan.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHuongDan.setForeground(MAU_CHU_MO);

        chanTrang.add(lblHuongDan, BorderLayout.WEST);
        return chanTrang;
    }

    public void taiDanhSachPhuongTien() {
        moHinhBang.setRowCount(0);
        if (khachHang == null || khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().isBlank()) {
            lblBadgeTongSo.setText("Tổng số: 0 phương tiện");
            return;
        }

        try {
            danhSachPhuongTien = phuongTienBUS.layTheoKhachHang(khachHang.getMaKhachHang());
            int stt = 1;
            for (PhuongTien pt : danhSachPhuongTien) {
                String hang = (pt.getHangXe() != null && pt.getHangXe().getTenHang() != null)
                        ? pt.getHangXe().getTenHang() : "";
                String mauXe = pt.getMauXe() != null ? pt.getMauXe() : "";
                String mauSac = pt.getMauSacXe() != null ? pt.getMauSacXe() : "";
                String nhienLieu = pt.getNhienLieu() != null ? pt.getNhienLieu().getTenHienThi() : "";

                moHinhBang.addRow(new Object[]{
                        stt++,
                        pt.getMaPhuongTien(),
                        pt.getBienSoXe(),
                        hang,
                        mauXe,
                        mauSac,
                        pt.getNamSanXuat(),
                        nhienLieu
                });
            }
            lblBadgeTongSo.setText("Tổng số: " + danhSachPhuongTien.size() + " phương tiện");
        } catch (Exception ex) {
            danhSachPhuongTien = new ArrayList<>();
            lblBadgeTongSo.setText("Tổng số: 0 phương tiện");
        }
    }

    private void xuLyThemPhuongTienMoi() {
        if (khachHang == null || khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng lưu thông tin khách hàng trước khi thêm phương tiện sở hữu!",
                    "Chưa xác định khách hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        QuanLyManHinhQuanLyPhuongTienXemChiTiet modal = new QuanLyManHinhQuanLyPhuongTienXemChiTiet(
                this,
                null,
                khachHang,
                phuongTienBUS,
                phieuTiepNhanBUS,
                hangXeBUS,
                khachHangBUS
        );
        modal.setVisible(true);

        if (modal.isDaCapNhat()) {
            coThayDoi = true;
            taiDanhSachPhuongTien();
        }
    }

    private void xuLyXemChiTietXeDangChon() {
        int dong = bangPhuongTien.getSelectedRow();
        if (dong < 0 || dong >= danhSachPhuongTien.size()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một phương tiện từ bảng để xem chi tiết!",
                    "Chưa chọn xe", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PhuongTien pt = danhSachPhuongTien.get(dong);
        QuanLyManHinhQuanLyPhuongTienXemChiTiet modal = new QuanLyManHinhQuanLyPhuongTienXemChiTiet(
                this,
                pt,
                khachHang,
                phuongTienBUS,
                phieuTiepNhanBUS,
                hangXeBUS,
                khachHangBUS
        );
        modal.setVisible(true);

        if (modal.isDaCapNhat()) {
            coThayDoi = true;
            taiDanhSachPhuongTien();
        }
    }

    private void xuLyXoaXeDangChon() {
        int dong = bangPhuongTien.getSelectedRow();
        if (dong < 0 || dong >= danhSachPhuongTien.size()) {
            return;
        }

        PhuongTien pt = danhSachPhuongTien.get(dong);
        int xacNhan = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa phương tiện [" + pt.getBienSoXe() + "] khỏi khách hàng này không?",
                "Xác nhận xóa xe",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (phuongTienBUS.xoaPhuongTien(pt.getMaPhuongTien())) {
                coThayDoi = true;
                taiDanhSachPhuongTien();
                JOptionPane.showMessageDialog(this,
                        "Đã xóa phương tiện [" + pt.getBienSoXe() + "] thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa phương tiện đã có phát sinh tiếp nhận sửa chữa.\nChi tiết: " + ex.getMessage(),
                    "Không thể xóa", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================================
    // CÁC COMPONENT ĐỒ HOẠ THEO DESIGN SYSTEM
    // =========================================================================

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
            g2.setColor(hover ? new Color(241, 245, 249) : Color.WHITE);
            g2.fill(nen);
            g2.setColor(hover ? MAU_CAM_CHU_DAO : MAU_VIEN_THE);
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
            g2.setColor(new Color(239, 246, 255));
            g2.fill(pill);
            g2.setColor(new Color(219, 234, 254));
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
            lbl.setHorizontalAlignment((column == 0 || column == 6) ? SwingConstants.CENTER : SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(0, column == 0 ? 10 : 8, 0, 8));
            return lbl;
        }
    }

    private static class BoVeOPhuongTien extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected ? MAU_CHON_DONG : Color.WHITE);
            setFont(new Font("Segoe UI", column == 2 ? Font.BOLD : Font.PLAIN, 12));
            setForeground(column == 2 ? MAU_XANH_DAM : MAU_CHU_PHU);
            setHorizontalAlignment((column == 0 || column == 6) ? SwingConstants.CENTER : SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, column == 0 ? 10 : 8, 0, 8));

            String chuoi = value != null ? value.toString() : "";
            setToolTipText(chuoi.length() > 20 ? chuoi : null);
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KhachHangBUS bus = new KhachHangBUS();
            List<KhachHang> ds = bus.layTatCa();
            KhachHang kh = !ds.isEmpty() ? ds.getFirst() : null;
            QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien dlg =
                    new QuanLyManHinhQuanLyKhachHangXemDanhSachPhuongTien(null, kh);
            dlg.setVisible(true);
        });
    }
}
