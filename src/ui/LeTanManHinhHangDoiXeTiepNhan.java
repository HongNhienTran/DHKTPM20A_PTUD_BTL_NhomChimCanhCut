package ui;

import bus.HoaDonBUS;
import bus.NhanVienBUS;
import bus.PhieuBaoGiaBUS;
import bus.PhieuPhanCongBUS;
import bus.PhieuTiepNhanBUS;
import bus.PhuongTienBUS;
import entity.KhachHang;
import entity.PhieuBaoGia;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.TaiKhoan;
import enums.TrangThaiBaoGia;
import enums.TrangThaiPhieuTiepNhan;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Màn hình Hàng Đợi Xe Tại Xưởng dành cho Lễ tân (Kanban Board).
 * Thiết kế giao diện hiện đại theo mẫu chuẩn Pengu Garage:
 * - 4 Thẻ chỉ số tổng quan ở hàng trên.
 * - Thanh tìm kiếm và lọc xe tại xưởng.
 * - Bảng Kanban 4 cột trực quan:
 *   1. Chờ Kiểm Tra
 *   2. Báo Giá
 *   3. Đang Sửa Chữa (với nút "Báo xong ➔" màu cam)
 *   4. Chờ Bàn Giao
 * - Nút "+ Tiếp Nhận Xe" mở modal LeTanManHinhThemTiepNhan.
 */
public class LeTanManHinhHangDoiXeTiepNhan extends JPanel {

    // Bảng màu giao diện chuẩn Pengu Garage
    private static final Color MAU_NEN_TRANG = new Color(246, 249, 253);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 124, 48);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_XANH_DAM_HOVER = new Color(24, 58, 92);
    private static final Color MAU_XANH_LA = new Color(16, 185, 129);
    private static final Color MAU_XANH_LA_HOVER = new Color(5, 150, 105);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_COT = new Color(248, 250, 252);

    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DINH_DANG_DAY_DU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();
    private final PhieuBaoGiaBUS phieuBaoGiaBUS = new PhieuBaoGiaBUS();
    private final PhuongTienBUS phuongTienBUS = new PhuongTienBUS();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();
    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();

    // Dữ liệu gốc
    private List<PhieuTiepNhan> danhSachGoc = new ArrayList<>();

    // Các thành phần giao diện
    private JLabel lblTongXeTaiXuongPill;
    private JLabel lblChiSoChoKiemTra;
    private JLabel lblChiSoBaoGia;
    private JLabel lblChiSoDangSuaChua;
    private JLabel lblChiSoChoBanGiao;

    private JTextField txtTimKiem;

    // 4 Cột Kanban
    private JPanel pnlCotChoKiemTra;
    private JPanel pnlCotBaoGia;
    private JPanel pnlCotDangSuaChua;
    private JPanel pnlCotChoBanGiao;

    private JLabel lblDemChoKiemTra;
    private JLabel lblDemBaoGia;
    private JLabel lblDemDangSuaChua;
    private JLabel lblDemChoBanGiao;

    public LeTanManHinhHangDoiXeTiepNhan() {
        this(null);
    }

    public LeTanManHinhHangDoiXeTiepNhan(TaiKhoan taiKhoanDangNhap) {
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        khoiTaoGiaoDien();
        taiLaiDuLieu();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN_TRANG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        // Cụm phía trên: Header tiêu đề + 4 Thẻ chỉ số + Thanh tìm kiếm
        JPanel pnlDau = new JPanel();
        pnlDau.setLayout(new BoxLayout(pnlDau, BoxLayout.Y_AXIS));
        pnlDau.setOpaque(false);

        pnlDau.add(taoThanhTieuDe());
        pnlDau.add(Box.createVerticalStrut(14));
        pnlDau.add(taoHangTheChiSo());
        pnlDau.add(Box.createVerticalStrut(12));
        pnlDau.add(taoThanhTimKiem());

        add(pnlDau, BorderLayout.NORTH);

        // Cụm ở giữa: Bảng Kanban 4 cột
        add(taoBangKanban4Cot(), BorderLayout.CENTER);
    }

    private JPanel taoThanhTieuDe() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Tiêu đề lớn bên trái
        JLabel lblTieuDe = new JLabel("Hàng Đợi Xe Tại Xưởng");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_CHU_CHINH);
        header.add(lblTieuDe, BorderLayout.WEST);

        // Cụm nút thao tác bên phải
        JPanel cumPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumPhai.setOpaque(false);

        // Badge pill đếm tổng số xe đang tại xưởng
        lblTongXeTaiXuongPill = new JLabel("Đang tại xưởng: 0 xe", IconUtil.layIcon("car.png", 14, 14, new Color(15, 41, 66)), SwingConstants.LEFT);
        lblTongXeTaiXuongPill.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTongXeTaiXuongPill.setForeground(new Color(15, 41, 66));
        lblTongXeTaiXuongPill.setOpaque(true);
        lblTongXeTaiXuongPill.setBackground(new Color(237, 244, 255));
        lblTongXeTaiXuongPill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 228, 255), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        // Nút + Tiếp Nhận Xe (Màu xanh ngọc / xanh lá)
        JButton btnTiepNhanXe = new JButton("+ Tiếp Nhận Xe", IconUtil.layIcon("plus.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_XANH_LA_HOVER : MAU_XANH_LA);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnTiepNhanXe.setIconTextGap(6);
        btnTiepNhanXe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTiepNhanXe.setForeground(Color.WHITE);
        btnTiepNhanXe.setFocusPainted(false);
        btnTiepNhanXe.setBorderPainted(false);
        btnTiepNhanXe.setContentAreaFilled(false);
        btnTiepNhanXe.setOpaque(false);
        btnTiepNhanXe.setPreferredSize(new Dimension(145, 36));
        btnTiepNhanXe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTiepNhanXe.addActionListener(e -> moModalThemTiepNhan());

        // Nút Cập Nhật (Navy)
        JButton btnCapNhat = new JButton("Cập Nhật", IconUtil.layIcon("refresh.png", 14, 14, Color.WHITE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_XANH_DAM_HOVER : MAU_XANH_DAM);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCapNhat.setIconTextGap(6);
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFocusPainted(false);
        btnCapNhat.setBorderPainted(false);
        btnCapNhat.setContentAreaFilled(false);
        btnCapNhat.setOpaque(false);
        btnCapNhat.setPreferredSize(new Dimension(115, 36));
        btnCapNhat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCapNhat.addActionListener(e -> taiLaiDuLieu());

        cumPhai.add(lblTongXeTaiXuongPill);
        cumPhai.add(btnTiepNhanXe);
        cumPhai.add(btnCapNhat);

        header.add(cumPhai, BorderLayout.EAST);
        return header;
    }

    private JPanel taoHangTheChiSo() {
        JPanel hang = new JPanel(new GridLayout(1, 4, 14, 0));
        hang.setOpaque(false);

        lblChiSoChoKiemTra = new JLabel("0 xe");
        lblChiSoBaoGia = new JLabel("0 xe");
        lblChiSoDangSuaChua = new JLabel("0 xe");
        lblChiSoChoBanGiao = new JLabel("0 xe");

        hang.add(taoTheChiSoNho("history.png", "1. Chờ Kiểm Tra", lblChiSoChoKiemTra, new Color(37, 99, 235)));
        hang.add(taoTheChiSoNho("search.png", "2. Báo Giá", lblChiSoBaoGia, new Color(217, 119, 6)));
        hang.add(taoTheChiSoNho("service.png", "3. Đang Sửa Chữa", lblChiSoDangSuaChua, new Color(234, 88, 12)));
        hang.add(taoTheChiSoNho("save.png", "4. Chờ Bàn Giao", lblChiSoChoBanGiao, new Color(16, 185, 129)));

        return hang;
    }

    private TheBoGoc taoTheChiSoNho(String tenIcon, String tieuDe, JLabel lblGiaTri, Color mauChuDao) {
        TheBoGoc the = new TheBoGoc(10, Color.WHITE, MAU_VIEN);
        the.setLayout(new BorderLayout(12, 0));
        the.setPreferredSize(new Dimension(0, 68));
        the.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Icon bên trái
        JLabel lblIcon = new JLabel(IconUtil.layIcon(tenIcon, 20, 20, mauChuDao));
        lblIcon.setPreferredSize(new Dimension(28, 28));

        // Tiêu đề & giá trị
        JPanel pnlChu = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlChu.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTieuDe.setForeground(MAU_CHU_PHU);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblGiaTri.setForeground(mauChuDao);

        pnlChu.add(lblTieuDe);
        pnlChu.add(lblGiaTri);

        the.add(lblIcon, BorderLayout.WEST);
        the.add(pnlChu, BorderLayout.CENTER);
        return the;
    }

    private TheBoGoc taoThanhTimKiem() {
        TheBoGoc the = new TheBoGoc(10, Color.WHITE, MAU_VIEN);
        the.setLayout(new BorderLayout(10, 0));
        the.setPreferredSize(new Dimension(0, 52));
        the.setBorder(new EmptyBorder(8, 14, 8, 14));

        // Nhãn bên trái
        JLabel lblNhan = new JLabel("Lọc nhanh xe tại xưởng:", IconUtil.layIcon("search.png", 15, 15, MAU_CHU_PHU), SwingConstants.LEFT);
        lblNhan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNhan.setForeground(MAU_CHU_PHU);
        lblNhan.setIconTextGap(6);

        // Input nhập từ khóa
        txtTimKiem = new JTextField();
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        datGoiYNhap(txtTimKiem, "Gõ biển số xe, mã tiếp nhận, tên hoặc số điện thoại khách hàng...");
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                locVaHienThiDuLieu();
            }
        });

        // Nút Đặt lại
        JButton btnDatLai = new JButton("Đặt lại", IconUtil.layIcon("refresh.png", 12, 12, MAU_CHU_PHU)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDatLai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDatLai.setForeground(MAU_CHU_PHU);
        btnDatLai.setFocusPainted(false);
        btnDatLai.setBorderPainted(false);
        btnDatLai.setContentAreaFilled(false);
        btnDatLai.setPreferredSize(new Dimension(90, 36));
        btnDatLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDatLai.setIconTextGap(6);
        btnDatLai.addActionListener(e -> {
            txtTimKiem.setText("Gõ biển số xe, mã tiếp nhận, tên hoặc số điện thoại khách hàng...");
            txtTimKiem.setForeground(MAU_CHU_PHU);
            locVaHienThiDuLieu();
        });

        the.add(lblNhan, BorderLayout.WEST);
        the.add(txtTimKiem, BorderLayout.CENTER);
        the.add(btnDatLai, BorderLayout.EAST);
        return the;
    }

    private JPanel taoBangKanban4Cot() {
        JPanel board = new JPanel(new GridLayout(1, 4, 14, 0));
        board.setOpaque(false);

        lblDemChoKiemTra = taoPillDemSo(new Color(37, 99, 235), new Color(239, 246, 255));
        lblDemBaoGia = taoPillDemSo(new Color(217, 119, 6), new Color(254, 243, 199));
        lblDemDangSuaChua = taoPillDemSo(new Color(234, 88, 12), new Color(255, 237, 213));
        lblDemChoBanGiao = taoPillDemSo(new Color(16, 185, 129), new Color(236, 253, 245));

        pnlCotChoKiemTra = taoDanhSachChuaTheXe();
        pnlCotBaoGia = taoDanhSachChuaTheXe();
        pnlCotDangSuaChua = taoDanhSachChuaTheXe();
        pnlCotChoBanGiao = taoDanhSachChuaTheXe();

        board.add(taoKhungCotKanban("history.png", "1. ĐANG KIỂM TRA", new Color(37, 99, 235), lblDemChoKiemTra, pnlCotChoKiemTra));
        board.add(taoKhungCotKanban("search.png", "2. BÁO GIÁ", new Color(217, 119, 6), lblDemBaoGia, pnlCotBaoGia));
        board.add(taoKhungCotKanban("service.png", "3. ĐANG SỬA CHỮA", new Color(234, 88, 12), lblDemDangSuaChua, pnlCotDangSuaChua));
        board.add(taoKhungCotKanban("save.png", "4. CHỜ BÀN GIAO", new Color(16, 185, 129), lblDemChoBanGiao, pnlCotChoBanGiao));

        return board;
    }

    private TheBoGoc taoKhungCotKanban(String tenIcon, String tieuDe, Color mauChuDao, JLabel lblDem, JPanel pnlNoiDung) {
        TheBoGoc the = new TheBoGoc(12, MAU_NEN_COT, MAU_VIEN);
        the.setLayout(new BorderLayout());

        // Header cột
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel lblTieuDe = new JLabel(tieuDe, IconUtil.layIcon(tenIcon, 15, 15, mauChuDao), SwingConstants.LEFT);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDe.setForeground(mauChuDao);
        lblTieuDe.setIconTextGap(6);

        pnlHeader.add(lblTieuDe, BorderLayout.WEST);
        pnlHeader.add(lblDem, BorderLayout.EAST);

        // Cuộn nội dung thẻ bên trong cột
        JScrollPane cuon = new JScrollPane(pnlNoiDung);
        cuon.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));
        cuon.setOpaque(false);
        cuon.getViewport().setOpaque(false);
        cuon.getVerticalScrollBar().setUnitIncrement(16);
        cuon.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        the.add(pnlHeader, BorderLayout.NORTH);
        the.add(cuon, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoDanhSachChuaTheXe() {
        JPanel pnl = new JPanel();
        pnl.setOpaque(false);
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        return pnl;
    }

    private JLabel taoPillDemSo(Color mauChu, Color mauNen) {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mauNen);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(mauChu);
        lbl.setOpaque(false);
        lbl.setPreferredSize(new Dimension(24, 24));
        return lbl;
    }

    public void taiLaiDuLieu() {
        try {
            danhSachGoc = phieuTiepNhanBUS.layTatCa();
            locVaHienThiDuLieu();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải hàng đợi xe tại xưởng: " + ex.getMessage());
        }
    }

    private void locVaHienThiDuLieu() {
        pnlCotChoKiemTra.removeAll();
        pnlCotBaoGia.removeAll();
        pnlCotDangSuaChua.removeAll();
        pnlCotChoBanGiao.removeAll();

        String tuKhoa = txtTimKiem.getText().trim().toLowerCase();
        if (tuKhoa.startsWith("gõ biển số xe")) {
            tuKhoa = "";
        }

        int demChoKiemTra = 0;
        int demBaoGia = 0;
        int demDangSuaChua = 0;
        int demChoBanGiao = 0;

        for (PhieuTiepNhan ptn : danhSachGoc) {
            TrangThaiPhieuTiepNhan tt = ptn.getTrangThai();
            if (tt == null || tt == TrangThaiPhieuTiepNhan.DA_HUY || tt == TrangThaiPhieuTiepNhan.THANH_TOAN) {
                continue;
            }
            if (tt == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA) {
                try {
                    PhieuBaoGia pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(ptn.getMaTiepNhan());
                    if (pbg != null && pbg.getTrangThai() == TrangThaiBaoGia.TU_CHOI) {
                        continue;
                    }
                } catch (Exception ignored) {
                }
            }

            // Lọc từ khóa
            if (!tuKhoa.isEmpty()) {
                String ma = ptn.getMaTiepNhan() != null ? ptn.getMaTiepNhan().toLowerCase() : "";
                String bienSo = ptn.getPhuongTien() != null && ptn.getPhuongTien().getBienSoXe() != null
                        ? ptn.getPhuongTien().getBienSoXe().toLowerCase() : "";
                String tenXe = ptn.getPhuongTien() != null && ptn.getPhuongTien().getMauXe() != null
                        ? ptn.getPhuongTien().getMauXe().toLowerCase() : "";

                KhachHang kh = ptn.getPhuongTien() != null ? ptn.getPhuongTien().getKhachHang() : null;
                String tenKH = kh != null && kh.getHoTen() != null ? kh.getHoTen().toLowerCase() : "";
                String sdt = kh != null && kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "";

                if (!ma.contains(tuKhoa) && !bienSo.contains(tuKhoa) && !tenXe.contains(tuKhoa)
                        && !tenKH.contains(tuKhoa) && !sdt.contains(tuKhoa)) {
                    continue;
                }
            }

            // Phân loại vào 4 cột
            switch (tt) {
                case DANG_KIEM_TRA -> {
                    pnlCotChoKiemTra.add(taoTheXeKanban(ptn, 1));
                    pnlCotChoKiemTra.add(Box.createVerticalStrut(10));
                    demChoKiemTra++;
                }
                case CHO_XAC_NHAN_BAO_GIA -> {
                    pnlCotBaoGia.add(taoTheXeKanban(ptn, 2));
                    pnlCotBaoGia.add(Box.createVerticalStrut(10));
                    demBaoGia++;
                }
                case DANG_SUA_CHUA -> {
                    pnlCotDangSuaChua.add(taoTheXeKanban(ptn, 3));
                    pnlCotDangSuaChua.add(Box.createVerticalStrut(10));
                    demDangSuaChua++;
                }
                case HOAN_THANH -> {
                    pnlCotChoBanGiao.add(taoTheXeKanban(ptn, 4));
                    pnlCotChoBanGiao.add(Box.createVerticalStrut(10));
                    demChoBanGiao++;
                }
            }
        }

        // Cập nhật số đếm badge cột
        lblDemChoKiemTra.setText(String.valueOf(demChoKiemTra));
        lblDemBaoGia.setText(String.valueOf(demBaoGia));
        lblDemDangSuaChua.setText(String.valueOf(demDangSuaChua));
        lblDemChoBanGiao.setText(String.valueOf(demChoBanGiao));

        // Cập nhật các thẻ chỉ số trên
        lblChiSoChoKiemTra.setText(demChoKiemTra + " xe");
        lblChiSoBaoGia.setText(demBaoGia + " xe");
        lblChiSoDangSuaChua.setText(demDangSuaChua + " xe");
        lblChiSoChoBanGiao.setText(demChoBanGiao + " xe");

        int tongTaiXuong = demChoKiemTra + demBaoGia + demDangSuaChua + demChoBanGiao;
        lblTongXeTaiXuongPill.setText("Đang tại xưởng: " + tongTaiXuong + " xe");

        // Giữ các thẻ xếp từ trên xuống dưới
        pnlCotChoKiemTra.add(Box.createVerticalGlue());
        pnlCotBaoGia.add(Box.createVerticalGlue());
        pnlCotDangSuaChua.add(Box.createVerticalGlue());
        pnlCotChoBanGiao.add(Box.createVerticalGlue());

        pnlCotChoKiemTra.revalidate();
        pnlCotChoKiemTra.repaint();
        pnlCotBaoGia.revalidate();
        pnlCotBaoGia.repaint();
        pnlCotDangSuaChua.revalidate();
        pnlCotDangSuaChua.repaint();
        pnlCotChoBanGiao.revalidate();
        pnlCotChoBanGiao.repaint();
    }

    private JPanel taoTheXeKanban(PhieuTiepNhan ptn, int loaiCot) {
        TheBoGoc the = new TheBoGoc(10, Color.WHITE, MAU_VIEN);
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));
        the.setBorder(new EmptyBorder(12, 14, 12, 14));
        the.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        PhuongTien pt = ptn.getPhuongTien();
        KhachHang kh = pt != null ? pt.getKhachHang() : null;

        // Dòng 1: Biển số xe bên trái + Mã tiếp nhận bên phải
        JPanel hang1 = new JPanel(new BorderLayout());
        hang1.setOpaque(false);

        String bienSo = pt != null && pt.getBienSoXe() != null ? pt.getBienSoXe() : "---";
        JLabel lblBienSo = new JLabel(bienSo);
        lblBienSo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBienSo.setForeground(new Color(15, 23, 42));

        JLabel lblMaTN = new JLabel(ptn.getMaTiepNhan());
        lblMaTN.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMaTN.setForeground(MAU_CHU_PHU);

        hang1.add(lblBienSo, BorderLayout.WEST);
        if (loaiCot == 4) {
            boolean daTT = daThanhToan(ptn);
            JLabel lblTrangThaiTT = new JLabel(daTT ? " Đã TT" : " Chờ TT", IconUtil.layIcon(daTT ? "check.png" : "history.png", 10, 10, daTT ? new Color(21, 128, 61) : new Color(180, 83, 9)), SwingConstants.LEFT);
            lblTrangThaiTT.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblTrangThaiTT.setForeground(daTT ? new Color(21, 128, 61) : new Color(180, 83, 9));
            lblTrangThaiTT.setOpaque(true);
            lblTrangThaiTT.setBackground(daTT ? new Color(220, 252, 231) : new Color(254, 243, 199));
            lblTrangThaiTT.setBorder(BorderFactory.createLineBorder(daTT ? new Color(134, 239, 172) : new Color(252, 211, 77), 1, true));

            JPanel pnlPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            pnlPhai.setOpaque(false);
            pnlPhai.add(lblTrangThaiTT);
            pnlPhai.add(lblMaTN);
            hang1.add(pnlPhai, BorderLayout.EAST);
        } else {
            hang1.add(lblMaTN, BorderLayout.EAST);
        }
        the.add(hang1);
        the.add(Box.createVerticalStrut(6));

        // Dòng 2: Hãng / Mẫu xe & màu sắc
        String mauXe = pt != null ? pt.getMauXe() : "---";
        String hang = pt != null && pt.getHangXe() != null ? pt.getHangXe().getTenHang() + " " : "";
        String mauSac = pt != null && pt.getMauSacXe() != null && !pt.getMauSacXe().isBlank() ? " (" + pt.getMauSacXe() + ")" : "";

        JLabel lblXe = new JLabel(hang + mauXe + mauSac, IconUtil.layIcon("car.png", 13, 13, MAU_CHU_PHU), SwingConstants.LEFT);
        lblXe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblXe.setForeground(MAU_CHU_CHINH);
        lblXe.setIconTextGap(6);
        lblXe.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.add(lblXe);
        the.add(Box.createVerticalStrut(4));

        // Dòng 3: Chủ xe & SĐT
        String tenKH = kh != null ? kh.getHoTen() : "Khách vãng lai";
        String sdt = kh != null && kh.getSoDienThoai() != null ? " - " + kh.getSoDienThoai() : "";

        JLabel lblKhach = new JLabel(tenKH + sdt, IconUtil.layIcon("user.png", 13, 13, MAU_CHU_PHU), SwingConstants.LEFT);
        lblKhach.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKhach.setForeground(MAU_CHU_PHU);
        lblKhach.setIconTextGap(6);
        lblKhach.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.add(lblKhach);
        the.add(Box.createVerticalStrut(4));

        // Dòng 4: Kỹ thuật viên phụ trách
        String ktvText = tríchXuatKTV(ptn);
        JLabel lblKTV = new JLabel(ktvText, IconUtil.layIcon("service.png", 13, 13, new Color(37, 99, 235)), SwingConstants.LEFT);
        lblKTV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKTV.setForeground(new Color(15, 41, 66));
        lblKTV.setIconTextGap(6);
        lblKTV.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.add(lblKTV);
        the.add(Box.createVerticalStrut(4));

        // Dòng 5: Thời gian tiếp nhận vào & thời gian trôi qua
        String gioVao = ptn.getThoiGianTiepNhan() != null ? ptn.getThoiGianTiepNhan().format(DINH_DANG_GIO) : "--:--";
        String troiQua = tinhThoiGianTroiQua(ptn.getThoiGianTiepNhan());
        JLabel lblThoiGian = new JLabel("Vào lúc " + gioVao + " (" + troiQua + ")", IconUtil.layIcon("history.png", 12, 12, new Color(59, 130, 246)), SwingConstants.LEFT);
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblThoiGian.setForeground(new Color(59, 130, 246));
        lblThoiGian.setIconTextGap(6);
        lblThoiGian.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.add(lblThoiGian);
        the.add(Box.createVerticalStrut(4));

        // Dòng 6: Yêu cầu / hỏng hóc
        String yeuCau = ptn.getYeuCauKhachHang() != null ? ptn.getYeuCauKhachHang() : "-";
        if (yeuCau.length() > 40) {
            yeuCau = yeuCau.substring(0, 37) + "...";
        }
        JLabel lblYeuCau = new JLabel(yeuCau, IconUtil.layIcon("edit.png", 12, 12, MAU_CHU_PHU), SwingConstants.LEFT);
        lblYeuCau.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblYeuCau.setForeground(MAU_CHU_PHU);
        lblYeuCau.setIconTextGap(6);
        lblYeuCau.setAlignmentX(Component.LEFT_ALIGNMENT);
        the.add(lblYeuCau);
        the.add(Box.createVerticalStrut(10));

        // Dòng 7: Hàng nút thao tác dưới chân thẻ - sử dụng GridLayout(1, 2, 8, 0) để hai nút chia đều chiều rộng thẻ, không bị co nhỏ hay mất chữ
        JButton btnHanhDong = taoNutHanhDongTheoCot(ptn, loaiCot);
        JPanel hangNut;
        if (btnHanhDong != null) {
            hangNut = new JPanel(new GridLayout(1, 2, 8, 0));
        } else {
            hangNut = new JPanel(new BorderLayout());
        }
        hangNut.setOpaque(false);
        hangNut.setPreferredSize(new Dimension(0, 32));
        hangNut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        // Nút bên trái: Xem chi tiết (nếu cột 3 thì mở phiếu chi tiết tiến độ sửa, các cột khác mở chi tiết tiếp nhận)
        JButton btnChiTiet = new JButton("Xem chi tiết", IconUtil.layIcon("view.png", 13, 13, MAU_CHU_CHINH)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.setColor(MAU_VIEN);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnChiTiet.setForeground(MAU_CHU_CHINH);
        btnChiTiet.setFocusPainted(false);
        btnChiTiet.setBorderPainted(false);
        btnChiTiet.setContentAreaFilled(false);
        btnChiTiet.setMargin(new Insets(0, 2, 0, 2));
        btnChiTiet.setBorder(new EmptyBorder(4, 4, 4, 4));
        btnChiTiet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChiTiet.setIconTextGap(4);

        if (loaiCot == 3) {
            btnChiTiet.setToolTipText("Xem chi tiết tiến độ thi công sửa chữa");
            btnChiTiet.addActionListener(e -> moModalChiTietDangSua(ptn));
        } else {
            btnChiTiet.setToolTipText("Xem chi tiết phiếu tiếp nhận phương tiện");
            btnChiTiet.addActionListener(e -> moChiTietPhieu(ptn));
        }

        hangNut.add(btnChiTiet);
        if (btnHanhDong != null) {
            hangNut.add(btnHanhDong);
        }

        the.add(hangNut);
        return the;
    }

    private JButton taoNutHanhDongTheoCot(PhieuTiepNhan ptn, int loaiCot) {
        String maTN = ptn.getMaTiepNhan();

        if (loaiCot == 1) { // 1. Chờ kiểm tra
            JButton btn = new JButton("Kiểm tra xe ➔", IconUtil.layIcon("history.png", 12, 12, new Color(37, 99, 235))) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(239, 246, 255));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    g2.setColor(new Color(191, 219, 254));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 6, 6));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(new Color(37, 99, 235));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setMargin(new Insets(0, 2, 0, 2));
            btn.setBorder(new EmptyBorder(4, 4, 4, 4));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setIconTextGap(4);
            btn.setToolTipText("Bấm để chuyển trạng thái sang Đang kiểm tra & Báo giá");
            btn.addActionListener(e -> {
                int hoi = JOptionPane.showConfirmDialog(this,
                        "Chuyển phương tiện [" + (ptn.getPhuongTien() != null ? ptn.getPhuongTien().getBienSoXe() : "") + "] sang trạng thái Đang kiểm tra & Báo giá?",
                        "Xác nhận chuyển bước", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (hoi == JOptionPane.YES_OPTION) {
                    chuyenTrangThai(maTN, TrangThaiPhieuTiepNhan.DANG_KIEM_TRA);
                }
            });
            return btn;
        } else if (loaiCot == 2) { // 2. Báo giá
            JButton btn = new JButton("Báo giá ➔", IconUtil.layIcon("search.png", 12, 12, new Color(180, 83, 9))) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(254, 243, 199));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    g2.setColor(new Color(253, 230, 138));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 6, 6));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(new Color(180, 83, 9));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setMargin(new Insets(0, 2, 0, 2));
            btn.setBorder(new EmptyBorder(4, 4, 4, 4));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText("Bấm để xem chi tiết báo giá và phân công thợ");
            btn.addActionListener(e -> moModalBaoGiaPhanCong(ptn));
            return btn;
        } else if (loaiCot == 3) { // 3. Đang sửa chữa (Nút màu cam "Tiến độ ➔")
            JButton btn = new JButton("Tiến độ ➔", IconUtil.layIcon("wrench.png", 12, 12, Color.WHITE)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? MAU_CAM_HOVER : MAU_CAM_CHU_DAO);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setMargin(new Insets(0, 2, 0, 2));
            btn.setBorder(new EmptyBorder(4, 4, 4, 4));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText("Xem chi tiết tiến độ thi công sửa chữa của các kỹ thuật viên");
            btn.addActionListener(e -> moModalChiTietDangSua(ptn));
            return btn;
        } else if (loaiCot == 4) { // 4. Chờ bàn giao
            JButton btn = new JButton("Thanh toán ➔", IconUtil.layIcon("invoice.png", 12, 12, Color.WHITE)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getModel().isRollover() ? MAU_CAM_HOVER : MAU_CAM_CHU_DAO);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setMargin(new Insets(0, 2, 0, 2));
            btn.setBorder(new EmptyBorder(4, 4, 4, 4));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText("Chuyển sang tab Lập hóa đơn để bắt đầu lập và thanh toán");
            btn.addActionListener(e -> xuLyChuyenSangLapHoaDonThanhToan(ptn));
            return btn;
        }
        return null;
    }

    private void xuLyChuyenSangLapHoaDonThanhToan(PhieuTiepNhan ptn) {
        if (ptn == null) return;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof LeTanManHinhChinh main) {
            main.chuyenSangLapHoaDonChoPhieuTiepNhan(ptn);
        }
    }

    private boolean daThanhToan(PhieuTiepNhan ptn) {
        if (ptn == null || ptn.getMaTiepNhan() == null) return false;
        try {
            entity.HoaDon hd = hoaDonBUS.timTheoPhieuTiepNhan(ptn.getMaTiepNhan());
            return hd != null && hd.getTrangThaiThanhToan() == enums.TrangThaiThanhToan.DA_THANH_TOAN;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void chuyenTrangThai(String maTiepNhan, TrangThaiPhieuTiepNhan ttMoi) {
        try {
            boolean ok = phieuTiepNhanBUS.chuyenTrangThai(maTiepNhan, ttMoi);
            if (ok) {
                taiLaiDuLieu();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể chuyển trạng thái phiếu tiếp nhận!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi chuyển trạng thái: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String tríchXuatKTV(PhieuTiepNhan ptn) {
        if (ptn == null) return "KTV phụ trách: Chưa rõ";
        String ghiChu = ptn.getGhiChu();
        if (ghiChu != null && ghiChu.contains("KTV phụ trách:")) {
            int idx = ghiChu.indexOf("KTV phụ trách:");
            String sub = ghiChu.substring(idx + 14).trim();
            int endIdx = sub.indexOf("\n");
            if (endIdx > 0) sub = sub.substring(0, endIdx);
            return "KTV phụ trách: " + sub;
        }
        // Kiểm tra xem đã có phân công KTV thi công sửa chữa hay chưa
        try {
            List<entity.PhieuPhanCong> dsPC = phieuPhanCongBUS.layTheoPhieuTiepNhan(ptn.getMaTiepNhan());
            if (dsPC != null && !dsPC.isEmpty()) {
                Set<String> dsTenKTV = new LinkedHashSet<>();
                for (entity.PhieuPhanCong pc : dsPC) {
                    if (pc.getNhanVien() != null) {
                        dsTenKTV.add(pc.getNhanVien().getHoTen());
                    }
                }
                if (!dsTenKTV.isEmpty()) {
                    return "KTV thực hiện: " + String.join(", ", dsTenKTV);
                }
            }
        } catch (Exception ignored) {
        }
        if (ptn.getNhanVien() != null && ptn.getNhanVien().getChucVu() == enums.ChucVu.KY_THUAT_VIEN) {
            return "KTV kiểm tra: " + ptn.getNhanVien().getHoTen();
        }
        return "KTV phụ trách: Chờ phân công";
    }

    private String tinhThoiGianTroiQua(LocalDateTime thoiGian) {
        if (thoiGian == null) return "";
        Duration d = Duration.between(thoiGian, LocalDateTime.now());
        long phut = Math.max(0, d.toMinutes());
        if (phut < 60) {
            return phut + "p trước";
        }
        long gio = d.toHours();
        long phutDu = phut % 60;
        if (gio < 24) {
            return gio + "h " + phutDu + "p trước";
        }
        long ngay = d.toDays();
        long gioDu = gio % 24;
        return ngay * 24 + gioDu + "h " + phutDu + "p trước";
    }

    private void moModalThemTiepNhan() {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhThemTiepNhan modal = new LeTanManHinhThemTiepNhan(cuaSo, taiKhoanDangNhap, this::taiLaiDuLieu);
        modal.setVisible(true);
    }

    private void moModalBaoGiaPhanCong(PhieuTiepNhan ptn) {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhHangDoiPhieuPhanCong modal = new LeTanManHinhHangDoiPhieuPhanCong(cuaSo, taiKhoanDangNhap, ptn, this::taiLaiDuLieu);
        modal.setVisible(true);
    }

    private void moModalChiTietDangSua(PhieuTiepNhan ptn) {
        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhHangDoiPhieuChiTietDangSua modal = new LeTanManHinhHangDoiPhieuChiTietDangSua(cuaSo, taiKhoanDangNhap, ptn, this::taiLaiDuLieu);
        modal.setVisible(true);
    }

    private void moChiTietPhieu(PhieuTiepNhan ptn) {
        if (ptn == null) return;

        Window cuaSo = SwingUtilities.getWindowAncestor(this);
        LeTanManHinhTiepNhanXemChiTietTiepNhan modal = new LeTanManHinhTiepNhanXemChiTietTiepNhan(
                cuaSo, ptn, () -> moModalChiTietDangSua(ptn)
        );
        modal.setVisible(true);
    }

    private void datGoiYNhap(JTextField field, String prompt) {
        field.setText(prompt);
        field.setForeground(MAU_CHU_PHU);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(prompt)) {
                    field.setText("");
                    field.setForeground(MAU_CHU_CHINH);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(prompt);
                    field.setForeground(MAU_CHU_PHU);
                }
            }
        });
    }

    // Panel bo góc
    private static class TheBoGoc extends JPanel {
        private final int banKinh;
        private final Color mauNen;
        private final Color mauVien;

        TheBoGoc(int banKinh, Color mauNen, Color mauVien) {
            this.banKinh = banKinh;
            this.mauNen = mauNen;
            this.mauVien = mauVien;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D rect = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, banKinh, banKinh);
            g2.setColor(mauNen);
            g2.fill(rect);
            if (mauVien != null) {
                g2.setColor(mauVien);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Pengu Garage - Hàng Đợi Xe Tại Xưởng");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1366, 768);
            f.setLocationRelativeTo(null);
            f.setContentPane(new LeTanManHinhHangDoiXeTiepNhan(null));
            f.setVisible(true);
        });
    }
}
