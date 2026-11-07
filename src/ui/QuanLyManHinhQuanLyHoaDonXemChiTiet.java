package ui;

import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.PhieuTiepNhan;
import enums.LoaiHangMuc;
import enums.LoaiHoaDon;
import enums.TrangThaiThanhToan;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Modal riêng hiển thị chi tiết và hỗ trợ in chứng từ Hóa đơn (tách biệt khỏi màn hình danh sách).
 */
public class QuanLyManHinhQuanLyHoaDonXemChiTiet extends JDialog {

    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_NHAT = new Color(248, 250, 252);
    private static final Color MAU_XANH_LA = new Color(21, 128, 61);
    private static final Color MAU_VANG_CAM = new Color(180, 83, 9);

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final HoaDon hoaDon;
    private final DecimalFormat dinhDangTien;
    private JPanel vungBanIn;

    public QuanLyManHinhQuanLyHoaDonXemChiTiet(Window cuaSoCha, HoaDon hoaDon, boolean goiInNgay) {
        super(cuaSoCha, "Chi Tiết Hóa Đơn - " + (hoaDon != null ? hoaDon.getMaHoaDon() : ""), ModalityType.APPLICATION_MODAL);
        this.hoaDon = hoaDon;

        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        kyHieu.setGroupingSeparator('.');
        kyHieu.setDecimalSeparator(',');
        this.dinhDangTien = new DecimalFormat("#,##0", kyHieu);

        khoiTaoGiaoDien();
        if (goiInNgay) {
            javax.swing.SwingUtilities.invokeLater(this::xuLyInHoaDon);
        }
    }

    private void khoiTaoGiaoDien() {
        setSize(960, 780);
        setMinimumSize(new Dimension(880, 700));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // 1. Đầu Modal
        JPanel dauModal = new JPanel(new BorderLayout());
        dauModal.setBackground(MAU_XANH_DAM);
        dauModal.setBorder(new EmptyBorder(15, 22, 15, 22));

        JLabel lblTieuDe = new JLabel("CHI TIẾT CHỨNG TỪ HÓA ĐƠN");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(Color.WHITE);

        boolean daTT = hoaDon.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN;
        JLabel lblTrangThai = new JLabel(daTT ? "ĐÃ THANH TOÁN" : "CHỜ THANH TOÁN");
        lblTrangThai.setOpaque(true);
        lblTrangThai.setBackground(daTT ? new Color(220, 252, 231) : new Color(254, 243, 199));
        lblTrangThai.setForeground(daTT ? MAU_XANH_LA : MAU_VANG_CAM);
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTrangThai.setBorder(new EmptyBorder(4, 12, 4, 12));

        dauModal.add(lblTieuDe, BorderLayout.WEST);
        dauModal.add(lblTrangThai, BorderLayout.EAST);

        // 2. Nội dung phiếu in ở giữa
        vungBanIn = new JPanel(new BorderLayout(0, 14));
        vungBanIn.setBackground(Color.WHITE);
        vungBanIn.setBorder(new EmptyBorder(20, 24, 20, 24));

        vungBanIn.add(taoKhoiThongTinChung(), BorderLayout.NORTH);
        vungBanIn.add(taoBangHangMucChiTiet(), BorderLayout.CENTER);
        vungBanIn.add(taoKhoiTongKetTien(), BorderLayout.SOUTH);

        JScrollPane cuon = new JScrollPane(vungBanIn);
        cuon.setBorder(null);
        cuon.getViewport().setBackground(Color.WHITE);

        // 3. Chân Modal
        JPanel chanModal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        chanModal.setBackground(MAU_NEN_NHAT);
        chanModal.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

        JButton btnIn = taoNutHanhDong("In Hóa Đơn", MAU_CAM_CHU_DAO, Color.WHITE);
        btnIn.addActionListener(e -> xuLyInHoaDon());

        JButton btnDong = taoNutHanhDong("Đóng", MAU_XANH_DAM, Color.WHITE);
        btnDong.addActionListener(e -> dispose());

        chanModal.add(btnIn);
        chanModal.add(btnDong);

        add(dauModal, BorderLayout.NORTH);
        add(cuon, BorderLayout.CENTER);
        add(chanModal, BorderLayout.SOUTH);
    }

    private JPanel taoKhoiThongTinChung() {
        JPanel khoi = new JPanel();
        khoi.setOpaque(false);
        khoi.setLayout(new BoxLayout(khoi, BoxLayout.Y_AXIS));

        JPanel dongThuongHieu = new JPanel(new BorderLayout());
        dongThuongHieu.setOpaque(false);
        JLabel lblGara = new JLabel("PENGU GARAGE - SERVICE ERP");
        lblGara.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGara.setForeground(MAU_XANH_DAM);

        JLabel lblMaHD = new JLabel("Mã HĐ: " + hoaDon.getMaHoaDon());
        lblMaHD.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMaHD.setForeground(MAU_CAM_CHU_DAO);

        dongThuongHieu.add(lblGara, BorderLayout.WEST);
        dongThuongHieu.add(lblMaHD, BorderLayout.EAST);

        JPanel luoiThongTin = new JPanel(new GridLayout(0, 2, 18, 6));
        luoiThongTin.setBackground(MAU_NEN_NHAT);
        luoiThongTin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        KhachHang kh = hoaDon.getKhachHang();
        boolean khachLe = (kh == null || kh.laKhachVangLai());
        String tenKH = khachLe ? "Khách lẻ" : kh.getHoTen();
        String sdtKH = khachLe ? "-" : kh.getSoDienThoai();
        String phanLoai = hoaDon.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG ? "Bán lẻ phụ tùng" : "Dịch vụ sửa chữa";
        String tenNV = (hoaDon.getNhanVien() != null && hoaDon.getNhanVien().getHoTen() != null)
                ? hoaDon.getNhanVien().getHoTen() : "-";
        String ngayLap = hoaDon.getThoiGianLap() != null
                ? hoaDon.getThoiGianLap().format(DINH_DANG_NGAY_GIO) : "-";
        String hinhThucTT = hoaDon.getPhuongThucThanhToan() != null
                ? hoaDon.getPhuongThucThanhToan().getTenHienThi() : "-";

        luoiThongTin.add(taoDongCapGiaTri("Khách hàng:", tenKH));
        luoiThongTin.add(taoDongCapGiaTri("Phân loại:", phanLoai));
        luoiThongTin.add(taoDongCapGiaTri("Số điện thoại:", sdtKH));
        luoiThongTin.add(taoDongCapGiaTri("Thời gian lập:", ngayLap));
        luoiThongTin.add(taoDongCapGiaTri("Nhân viên lập:", tenNV));
        luoiThongTin.add(taoDongCapGiaTri("Hình thức TT:", hinhThucTT));

        PhieuTiepNhan ptn = hoaDon.getPhieuTiepNhan();
        if (ptn != null) {
            luoiThongTin.add(taoDongCapGiaTri("Phiếu tiếp nhận:", ptn.getMaTiepNhan()));
            String bienSo = (ptn.getPhuongTien() != null && ptn.getPhuongTien().getBienSoXe() != null)
                    ? ptn.getPhuongTien().getBienSoXe() : "-";
            luoiThongTin.add(taoDongCapGiaTri("Biển số xe:", bienSo));
        }

        khoi.add(dongThuongHieu);
        khoi.add(Box.createVerticalStrut(10));
        khoi.add(luoiThongTin);
        return khoi;
    }

    private JPanel taoDongCapGiaTri(String nhan, String giaTri) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false);
        JLabel l1 = new JLabel(nhan);
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l1.setForeground(MAU_CHU_PHU);
        l1.setPreferredSize(new Dimension(105, 20));

        JLabel l2 = new JLabel(giaTri != null ? giaTri : "-");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l2.setForeground(MAU_CHU_CHINH);

        p.add(l1, BorderLayout.WEST);
        p.add(l2, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane taoBangHangMucChiTiet() {
        String[] cot = {"STT", "Loại", "Tên Hạng Mục / Phụ Tùng", "SL", "Đơn Giá", "Thuế VAT", "Thành Tiền"};
        DefaultTableModel model = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int stt = 1;
        for (ChiTietHoaDon ct : hoaDon.getDanhSachChiTiet()) {
            boolean laDichVu = ct.getLoaiHangMuc() == LoaiHangMuc.DICH_VU;
            String loai = laDichVu ? "Dịch vụ" : "Phụ tùng";
            String ten = laDichVu
                    ? (ct.getDichVu() != null ? ct.getDichVu().getTenDichVu() : "")
                    : (ct.getSanPham() != null ? ct.getSanPham().getTenSanPham() : "");

            model.addRow(new Object[]{
                    stt++,
                    loai,
                    ten,
                    ct.getSoLuong(),
                    dinhDangTien.format(Math.round(ct.getDonGia())) + " đ",
                    dinhDangTien.format(Math.round(ct.tinhThue())) + " đ",
                    dinhDangTien.format(Math.round(ct.tinhThanhTienSauThue())) + " đ"
            });
        }

        JTable bang = new JTable(model);
        bang.setRowHeight(34);
        bang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bang.getTableHeader().setBackground(MAU_NEN_NHAT);
        bang.getTableHeader().setPreferredSize(new Dimension(0, 34));
        bang.setGridColor(new Color(241, 245, 249));
        bang.setShowVerticalLines(false);
        bang.setFillsViewportHeight(true);

        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);
        bang.getColumnModel().getColumn(4).setCellRenderer(canPhai);
        bang.getColumnModel().getColumn(5).setCellRenderer(canPhai);
        bang.getColumnModel().getColumn(6).setCellRenderer(canPhai);

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        bang.getColumnModel().getColumn(0).setCellRenderer(canGiua);
        bang.getColumnModel().getColumn(3).setCellRenderer(canGiua);

        bang.getColumnModel().getColumn(0).setPreferredWidth(42);
        bang.getColumnModel().getColumn(1).setPreferredWidth(75);
        bang.getColumnModel().getColumn(2).setPreferredWidth(230);
        bang.getColumnModel().getColumn(3).setPreferredWidth(45);

        JScrollPane cuon = new JScrollPane(bang);
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        cuon.setPreferredSize(new Dimension(0, 250));
        return cuon;
    }

    private JPanel taoKhoiTongKetTien() {
        JPanel baoNgoai = new JPanel(new BorderLayout());
        baoNgoai.setOpaque(false);

        JPanel cotPhai = new JPanel(new GridLayout(4, 2, 18, 6));
        cotPhai.setOpaque(false);
        cotPhai.setBorder(new EmptyBorder(6, 0, 0, 4));

        themDongTong(cotPhai, "Tổng tiền hàng (trước thuế):",
                dinhDangTien.format(Math.round(hoaDon.tinhTongTienHang())) + " đ", false);
        themDongTong(cotPhai, "Tổng thuế VAT:",
                dinhDangTien.format(Math.round(hoaDon.tinhThue())) + " đ", false);
        String nhanKM = hoaDon.getMaKhuyenMai() != null
                ? "Khuyến mãi (" + hoaDon.getMaKhuyenMai().getMaKhuyenMai() + "):"
                : "Khuyến mãi:";
        themDongTong(cotPhai, nhanKM,
                "-" + dinhDangTien.format(Math.round(hoaDon.tinhTienKhuyenMai())) + " đ", false);
        themDongTong(cotPhai, "TỔNG THANH TOÁN:",
                dinhDangTien.format(Math.round(hoaDon.tinhTongThanhToan())) + " đ", true);

        baoNgoai.add(cotPhai, BorderLayout.EAST);
        return baoNgoai;
    }

    private void themDongTong(JPanel pnl, String nhan, String giaTri, boolean noiBat) {
        JLabel l1 = new JLabel(nhan, SwingConstants.RIGHT);
        l1.setFont(new Font("Segoe UI", noiBat ? Font.BOLD : Font.PLAIN, noiBat ? 14 : 12));
        l1.setForeground(noiBat ? MAU_XANH_DAM : MAU_CHU_PHU);

        JLabel l2 = new JLabel(giaTri, SwingConstants.RIGHT);
        l2.setFont(new Font("Segoe UI", Font.BOLD, noiBat ? 15 : 13));
        l2.setForeground(noiBat ? MAU_CAM_CHU_DAO : MAU_CHU_CHINH);

        pnl.add(l1);
        pnl.add(l2);
    }

    private JButton taoNutHanhDong(String chu, Color mauNen, Color mauChu) {
        JButton btn = new JButton(chu) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mauNen);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 9f, 9f));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(mauChu);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void xuLyInHoaDon() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("HoaDon_" + hoaDon.getMaHoaDon());
        job.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double tyLeRong = pageFormat.getImageableWidth() / Math.max(1, vungBanIn.getWidth());
            double tyLe = Math.min(1.0, tyLeRong);
            g2.scale(tyLe, tyLe);
            g2.setStroke(new BasicStroke(1f));
            vungBanIn.printAll(g2);
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                        "Không thể in hóa đơn: " + ex.getMessage(),
                        "Lỗi máy in", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
