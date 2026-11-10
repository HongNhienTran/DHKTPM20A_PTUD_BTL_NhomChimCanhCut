package ui;

import bus.PhieuBaoGiaBUS;
import bus.PhieuPhanCongBUS;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DichVu;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import enums.TrangThaiPhieuTiepNhan;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Modal Xem Chi Tiết Phiếu Tiếp Nhận Phương Tiện dành cho Lễ tân.
 * Thiết kế theo chuẩn hệ thống Pengu Garage:
 * - Kích thước rộng rãi (1000x800), giao diện dạng Card hiện đại.
 * - Header thông tin mã tiếp nhận, biển số, trạng thái (Badge pill bo góc).
 * - Khung 1: Thông tin xe & Chủ sở hữu (2 cột thông thoáng).
 * - Khung 2: Thông số bàn giao & Hiện trạng ngoại thất, nội thất.
 * - Khung 3: Yêu cầu của khách hàng & Bảng chi tiết báo giá / phân công KTV (nếu đã có).
 * - Footer: Nút xem tiến độ sửa (nếu xe đang sửa) và nút Đóng.
 */
public class LeTanManHinhTiepNhanXemChiTietTiepNhan extends JDialog {

    private static final Color MAU_NEN_CHINH = new Color(241, 245, 249);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(217, 83, 10);

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuTiepNhan phieuTiepNhan;
    private final Runnable onMoChiTietTienDo;

    private final PhieuBaoGiaBUS phieuBaoGiaBUS = new PhieuBaoGiaBUS();
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();

    public LeTanManHinhTiepNhanXemChiTietTiepNhan() {
        this(null, null, null);
    }

    public LeTanManHinhTiepNhanXemChiTietTiepNhan(Window owner, PhieuTiepNhan ptn) {
        this(owner, ptn, null);
    }

    public LeTanManHinhTiepNhanXemChiTietTiepNhan(Window owner, PhieuTiepNhan ptn, Runnable onMoChiTietTienDo) {
        super(owner, "Chi Tiết Phiếu Tiếp Nhận Phương Tiện", Dialog.ModalityType.APPLICATION_MODAL);
        this.phieuTiepNhan = ptn;
        this.onMoChiTietTienDo = onMoChiTietTienDo;

        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        Dimension manHinh = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int rong = Math.min(1020, manHinh.width - 60);
        int cao = Math.min(820, manHinh.height - 60);
        setSize(rong, cao);
        setMinimumSize(new Dimension(880, 640));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN_CHINH);

        // 1. Header trên cùng
        add(taoHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung cuộn chính
        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Khung 1: Thông tin xe & Khách hàng
        pnlNoiDung.add(taoTheThongTinXeVaKhachHang());
        pnlNoiDung.add(Box.createVerticalStrut(14));

        // Khung 2: Thông số bàn giao & Hiện trạng xe
        pnlNoiDung.add(taoTheThongSoVaHienTrang());
        pnlNoiDung.add(Box.createVerticalStrut(14));

        // Khung 3: Hạng mục yêu cầu & Chi tiết dịch vụ / báo giá
        pnlNoiDung.add(taoTheHangMucVaBaoGia());

        JScrollPane cuonTong = new JScrollPane(pnlNoiDung);
        cuonTong.setBorder(null);
        cuonTong.getViewport().setBackground(MAU_NEN_CHINH);
        cuonTong.getVerticalScrollBar().setUnitIncrement(14);
        add(cuonTong, BorderLayout.CENTER);

        // 3. Footer dưới cùng
        add(taoFooterPanel(), BorderLayout.SOUTH);
    }

    /**
     * Header phía trên: Tiêu đề biển số + Mã phiếu + Badge trạng thái + Thời gian tiếp nhận
     */
    private JPanel taoHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN),
                new EmptyBorder(14, 20, 14, 20)
        ));

        // Bên trái: Icon + Biển số + Mã tiếp nhận
        JPanel pnlTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTrai.setOpaque(false);

        JLabel lblIcon = new JLabel(IconUtil.layIcon("car.png", 22, 22, MAU_XANH_DAM));
        pnlTrai.add(lblIcon);

        PhuongTien pt = (phieuTiepNhan != null) ? phieuTiepNhan.getPhuongTien() : null;
        String bienSo = (pt != null && pt.getBienSoXe() != null) ? pt.getBienSoXe() : "Chưa có biển số";
        JLabel lblTieuDe = new JLabel("PHIẾU TIẾP NHẬN: " + bienSo);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        pnlTrai.add(lblTieuDe);

        String maTN = (phieuTiepNhan != null && phieuTiepNhan.getMaTiepNhan() != null) ? phieuTiepNhan.getMaTiepNhan() : "-";
        JLabel lblMaTN = new JLabel("(" + maTN + ")");
        lblMaTN.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMaTN.setForeground(MAU_CHU_PHU);
        pnlTrai.add(lblMaTN);

        header.add(pnlTrai, BorderLayout.WEST);

        // Bên phải: Badge trạng thái + Thời gian tiếp nhận
        JPanel pnlPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlPhai.setOpaque(false);

        String thoiGian = (phieuTiepNhan != null && phieuTiepNhan.getThoiGianTiepNhan() != null)
                ? phieuTiepNhan.getThoiGianTiepNhan().format(DINH_DANG_NGAY_GIO)
                : "-";
        JLabel lblThoiGian = new JLabel("Tiếp nhận lúc: " + thoiGian);
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThoiGian.setForeground(MAU_CHU_PHU);
        pnlPhai.add(lblThoiGian);

        TrangThaiPhieuTiepNhan tt = (phieuTiepNhan != null) ? phieuTiepNhan.getTrangThai() : null;
        pnlPhai.add(taoBadgeTrangThai(tt));

        header.add(pnlPhai, BorderLayout.EAST);
        return header;
    }

    /**
     * Khung 1: Thông tin phương tiện & Thông tin chủ xe
     */
    private JPanel taoTheThongTinXeVaKhachHang() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 10));
        the.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Tiêu đề thẻ
        the.add(taoTieuDeThe("car.png", "1. Thông Tin Phương Tiện & Chủ Sở Hữu"), BorderLayout.NORTH);

        // Nội dung 2 cột: Cột trái Thông tin xe, Cột phải Thông tin chủ xe & Nhân viên tiếp nhận
        JPanel luoi = new JPanel(new GridLayout(1, 2, 20, 0));
        luoi.setOpaque(false);

        PhuongTien pt = (phieuTiepNhan != null) ? phieuTiepNhan.getPhuongTien() : null;
        KhachHang kh = (pt != null) ? pt.getKhachHang() : null;
        NhanVien nvLeTan = (phieuTiepNhan != null) ? phieuTiepNhan.getNhanVien() : null;

        // Cột trái: Thông tin xe
        JPanel cotTrai = new JPanel();
        cotTrai.setLayout(new BoxLayout(cotTrai, BoxLayout.Y_AXIS));
        cotTrai.setOpaque(false);
        cotTrai.add(taoDongThongTin("Biển số xe:", pt != null ? pt.getBienSoXe() : "-"));
        cotTrai.add(Box.createVerticalStrut(6));
        String mauVaHang = (pt != null)
                ? pt.getMauXe() + (pt.getHangXe() != null ? " (" + pt.getHangXe().getTenHang() + ")" : "")
                : "-";
        cotTrai.add(taoDongThongTin("Mẫu xe / Hãng:", mauVaHang));
        cotTrai.add(Box.createVerticalStrut(6));
        cotTrai.add(taoDongThongTin("Màu sắc xe:", (pt != null && pt.getMauSacXe() != null) ? pt.getMauSacXe() : "-"));
        cotTrai.add(Box.createVerticalStrut(6));
        cotTrai.add(taoDongThongTin("Năm sản xuất:", (pt != null && pt.getNamSanXuat() > 0) ? String.valueOf(pt.getNamSanXuat()) : "-"));
        cotTrai.add(Box.createVerticalStrut(6));
        String loaiNhienLieu = (pt != null && pt.getNhienLieu() != null) ? pt.getNhienLieu().getTenHienThi() : "-";
        cotTrai.add(taoDongThongTin("Loại nhiên liệu:", loaiNhienLieu));

        // Cột phải: Thông tin chủ xe & Nhân viên tiếp nhận
        JPanel cotPhai = new JPanel();
        cotPhai.setLayout(new BoxLayout(cotPhai, BoxLayout.Y_AXIS));
        cotPhai.setOpaque(false);
        cotPhai.add(taoDongThongTin("Chủ sở hữu:", kh != null ? kh.getHoTen() : "-"));
        cotPhai.add(Box.createVerticalStrut(6));
        cotPhai.add(taoDongThongTin("Số điện thoại:", kh != null ? kh.getSoDienThoai() : "-"));
        cotPhai.add(Box.createVerticalStrut(6));
        cotPhai.add(taoDongThongTin("Địa chỉ:", (kh != null && kh.getDiaChi() != null) ? kh.getDiaChi() : "-"));
        cotPhai.add(Box.createVerticalStrut(6));
        String tenLeTan = (nvLeTan != null) ? nvLeTan.getHoTen() + " (" + nvLeTan.getMaNhanVien() + ")" : "-";
        cotPhai.add(taoDongThongTin("Nhân viên tiếp nhận:", tenLeTan));

        luoi.add(cotTrai);
        luoi.add(cotPhai);
        the.add(luoi, BorderLayout.CENTER);
        return the;
    }

    /**
     * Khung 2: Thông số bàn giao & Hiện trạng ngoại thất, nội thất xe
     */
    private JPanel taoTheThongSoVaHienTrang() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 10));
        the.setBorder(new EmptyBorder(14, 16, 14, 16));

        the.add(taoTieuDeThe("settings.png", "2. Thông Số Bàn Giao Xe & Hiện Trạng Tiếp Nhận"), BorderLayout.NORTH);

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setOpaque(false);

        // Hàng thông số ngắn: ODO, Chìa khóa & KTV phụ trách kiểm tra
        JPanel hangThongSo = new JPanel(new GridLayout(1, 3, 16, 0));
        hangThongSo.setOpaque(false);
        String odo = (phieuTiepNhan != null) ? String.format("%,.0f km", phieuTiepNhan.getSoKMHienTai()) : "0 km";
        String chiaKhoa = (phieuTiepNhan != null) ? phieuTiepNhan.getSoChiaKhoa() + " chìa" : "1 chìa";
        String ktv = layTenKTVKiemTra(phieuTiepNhan);
        hangThongSo.add(taoDongThongTin("Số KM hiện tại (ODO):", odo));
        hangThongSo.add(taoDongThongTin("Số chìa khóa bàn giao:", chiaKhoa));
        hangThongSo.add(taoDongThongTin("KTV kiểm tra xe:", ktv));
        pnlNoiDung.add(hangThongSo);
        pnlNoiDung.add(Box.createVerticalStrut(10));

        // Hàng 2 ô nội dung dài: Hiện trạng ngoại thất & Hiện trạng nội thất
        JPanel hangHienTrang = new JPanel(new GridLayout(1, 2, 20, 0));
        hangHienTrang.setOpaque(false);

        String ngoaiThat = (phieuTiepNhan != null && phieuTiepNhan.getTinhTrangNgoaiThat() != null && !phieuTiepNhan.getTinhTrangNgoaiThat().isBlank())
                ? phieuTiepNhan.getTinhTrangNgoaiThat()
                : "Không ghi nhận trầy xước ngoại thất.";
        hangHienTrang.add(taoHopVanBanChiTiet("Tình trạng ngoại thất (Vỏ, đèn, gương, kính):", ngoaiThat));

        String noiThat = (phieuTiepNhan != null && phieuTiepNhan.getTinhTrangNoiThat() != null && !phieuTiepNhan.getTinhTrangNoiThat().isBlank())
                ? phieuTiepNhan.getTinhTrangNoiThat()
                : "Không có đồ đạc quý giá để lại trong xe.";
        hangHienTrang.add(taoHopVanBanChiTiet("Tình trạng nội thất & Đồ đạc trên xe:", noiThat));

        pnlNoiDung.add(hangHienTrang);

        // Ghi chú tiếp nhận (nếu có)
        if (phieuTiepNhan != null && phieuTiepNhan.getGhiChu() != null && !phieuTiepNhan.getGhiChu().isBlank()) {
            pnlNoiDung.add(Box.createVerticalStrut(8));
            pnlNoiDung.add(taoDongThongTin("Ghi chú tiếp nhận:", phieuTiepNhan.getGhiChu()));
        }

        the.add(pnlNoiDung, BorderLayout.CENTER);
        return the;
    }

    /**
     * Khung 3: Yêu cầu của khách hàng & Bảng chi tiết báo giá / phân công thợ
     */
    private JPanel taoTheHangMucVaBaoGia() {
        TheBoGocTrang the = new TheBoGocTrang(new BorderLayout(0, 10));
        the.setBorder(new EmptyBorder(14, 16, 14, 16));

        the.add(taoTieuDeThe("invoice.png", "3. Yêu Cầu Của Khách Hàng & Các Hạng Mục Dịch Vụ Tiếp Nhận"), BorderLayout.NORTH);

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setOpaque(false);

        // Ô hiển thị yêu cầu của khách hàng
        String yeuCau = (phieuTiepNhan != null && phieuTiepNhan.getYeuCauKhachHang() != null && !phieuTiepNhan.getYeuCauKhachHang().isBlank())
                ? phieuTiepNhan.getYeuCauKhachHang()
                : "(Khách hàng chưa mô tả hiện tượng hỏng hóc)";
        pnlNoiDung.add(taoHopVanBanChiTiet("Hiện tượng hỏng hóc / Yêu cầu bảo dưỡng của khách:", yeuCau));
        pnlNoiDung.add(Box.createVerticalStrut(10));

        // Bảng các dịch vụ & phụ tùng đã lập trong báo giá (nếu có)
        PhieuBaoGia pbg = null;
        if (phieuTiepNhan != null && phieuTiepNhan.getMaTiepNhan() != null) {
            try {
                pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(phieuTiepNhan.getMaTiepNhan());
            } catch (Exception ignored) {
            }
        }

        if (pbg != null && pbg.getDanhSachChiTiet() != null && !pbg.getDanhSachChiTiet().isEmpty()) {
            JLabel lblBang = new JLabel("Danh sách hạng mục công thợ & vật tư báo giá:");
            lblBang.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblBang.setForeground(MAU_XANH_DAM);
            pnlNoiDung.add(lblBang);
            pnlNoiDung.add(Box.createVerticalStrut(6));

            pnlNoiDung.add(taoBangChiTietBaoGia(pbg));
        } else {
            // Chưa có báo giá chính thức
            JPanel pnlThongBaoChuaCo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            pnlThongBaoChuaCo.setBackground(new Color(248, 250, 252));
            pnlThongBaoChuaCo.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
            JLabel lblIconInfo = new JLabel(IconUtil.layIcon("alert.png", 14, 14, MAU_CHU_PHU));
            JLabel lblChuaCo = new JLabel("Phiếu tiếp nhận đang chờ Kỹ thuật viên kiểm tra hoặc chưa lập bảng báo giá chi tiết.");
            lblChuaCo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblChuaCo.setForeground(MAU_CHU_PHU);
            pnlThongBaoChuaCo.add(lblIconInfo);
            pnlThongBaoChuaCo.add(lblChuaCo);
            pnlNoiDung.add(pnlThongBaoChuaCo);
        }

        the.add(pnlNoiDung, BorderLayout.CENTER);
        return the;
    }

    /**
     * Bảng danh sách dịch vụ và phụ tùng trong báo giá
     */
    private JPanel taoBangChiTietBaoGia(PhieuBaoGia pbg) {
        String[] tieuDe = {"Phân Loại", "Hạng Mục / Linh Kiện", "SL", "Đơn Giá", "Thành Tiền", "Thuế VAT"};
        DefaultTableModel model = new DefaultTableModel(tieuDe, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        double tongChuaThue = 0;
        double tongVAT = 0;

        for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
            DichVu dv = ctbg.getDichVu();
            String tenDV = (dv != null) ? dv.getTenDichVu() : "Dịch vụ kỹ thuật";
            String chuyenMonStr = (dv != null && dv.getChuyenMon() != null) ? " [" + dv.getChuyenMon().getTenHienThi() + "]" : "";
            double donGiaDV = ctbg.getDonGiaDichVu();
            double vatRateDV = (dv != null) ? dv.getThueVAT() : 8.0;
            if (vatRateDV > 1.0) vatRateDV /= 100.0;

            double ttDV = donGiaDV * 1;
            double vatDV = ttDV * vatRateDV;
            tongChuaThue += ttDV;
            tongVAT += vatDV;

            model.addRow(new Object[]{
                    "Dịch vụ",
                    tenDV + chuyenMonStr,
                    1,
                    dinhDangTien(donGiaDV),
                    dinhDangTien(ttDV),
                    Math.round(vatRateDV * 100) + "%"
            });

            if (ctbg.getDanhSachPhuTung() != null) {
                for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                    String tenPT = (pt.getSanPham() != null) ? pt.getSanPham().getTenSanPham() : "Phụ tùng thay thế";
                    int sl = pt.getSoLuong();
                    double donGiaPT = pt.getDonGia();
                    double vatRatePT = (pt.getSanPham() != null) ? pt.getSanPham().getThueVAT() : 10.0;
                    if (vatRatePT > 1.0) vatRatePT /= 100.0;

                    double ttPT = sl * donGiaPT;
                    double vatPT = ttPT * vatRatePT;
                    tongChuaThue += ttPT;
                    tongVAT += vatPT;

                    model.addRow(new Object[]{
                            "Phụ tùng",
                            "   ↳ " + tenPT,
                            sl,
                            dinhDangTien(donGiaPT),
                            dinhDangTien(ttPT),
                            Math.round(vatRatePT * 100) + "%"
                    });
                }
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(MAU_CHU_CHINH);
        table.setGridColor(new Color(235, 238, 242));
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(51, 65, 85));

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(90);
        cm.getColumn(1).setPreferredWidth(360);
        cm.getColumn(2).setPreferredWidth(50);
        cm.getColumn(3).setPreferredWidth(120);
        cm.getColumn(4).setPreferredWidth(130);
        cm.getColumn(5).setPreferredWidth(80);

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);

        cm.getColumn(0).setCellRenderer(canGiua);
        cm.getColumn(2).setCellRenderer(canGiua);
        cm.getColumn(3).setCellRenderer(canPhai);
        cm.getColumn(4).setCellRenderer(canPhai);
        cm.getColumn(5).setCellRenderer(canGiua);

        JScrollPane cuonBang = new JScrollPane(table);
        cuonBang.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuonBang.setPreferredSize(new Dimension(0, Math.min(200, 38 + model.getRowCount() * 34)));

        // Khung tổng kết tiền bên dưới bảng
        JPanel pnlWrap = new JPanel(new BorderLayout(0, 6));
        pnlWrap.setOpaque(false);
        pnlWrap.add(cuonBang, BorderLayout.CENTER);

        JPanel pnlTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        pnlTongTien.setOpaque(false);

        JLabel lblChuaThue = new JLabel("Tiền chưa thuế: " + dinhDangTien(tongChuaThue));
        lblChuaThue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblChuaThue.setForeground(MAU_CHU_PHU);

        JLabel lblVat = new JLabel("| Thuế VAT: + " + dinhDangTien(tongVAT));
        lblVat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVat.setForeground(new Color(37, 99, 235));

        JLabel lblTong = new JLabel("| TỔNG CỘNG: " + dinhDangTien(tongChuaThue + tongVAT));
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTong.setForeground(MAU_CAM_CHU_DAO);

        pnlTongTien.add(lblChuaThue);
        pnlTongTien.add(lblVat);
        pnlTongTien.add(lblTong);

        pnlWrap.add(pnlTongTien, BorderLayout.SOUTH);
        return pnlWrap;
    }

    /**
     * Footer dưới cùng với các nút hành động
     */
    private JPanel taoFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN),
                new EmptyBorder(10, 20, 10, 20)
        ));

        // Bên trái: Mã phiếu tiếp nhận
        JLabel lblGhiChuCuoi = new JLabel("Pengu Garage System - Quản lý tiếp nhận dịch vụ");
        lblGhiChuCuoi.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblGhiChuCuoi.setForeground(new Color(148, 163, 184));
        footer.add(lblGhiChuCuoi, BorderLayout.WEST);

        // Bên phải: Nút tiến độ (nếu có) + Nút Đóng
        JPanel pnlNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlNut.setOpaque(false);

        if (onMoChiTietTienDo != null && phieuTiepNhan != null && phieuTiepNhan.getTrangThai() == TrangThaiPhieuTiepNhan.DANG_SUA_CHUA) {
            JButton btnTienDo = new JButton("Chi Tiết Tiến Độ Sửa ➔", IconUtil.layIcon("wrench.png", 14, 14, Color.WHITE));
            btnTienDo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnTienDo.setForeground(Color.WHITE);
            btnTienDo.setBackground(MAU_CAM_CHU_DAO);
            btnTienDo.setFocusPainted(false);
            btnTienDo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnTienDo.setPreferredSize(new Dimension(190, 36));
            btnTienDo.addActionListener(e -> {
                dispose();
                onMoChiTietTienDo.run();
            });
            pnlNut.add(btnTienDo);
        } else if (phieuTiepNhan != null && phieuTiepNhan.getTrangThai() == TrangThaiPhieuTiepNhan.HOAN_THANH) {
            JButton btnThanhToan = new JButton("Lập Hóa Đơn & Thanh Toán ➔", IconUtil.layIcon("invoice.png", 14, 14, Color.WHITE));
            btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnThanhToan.setForeground(Color.WHITE);
            btnThanhToan.setBackground(MAU_CAM_CHU_DAO);
            btnThanhToan.setFocusPainted(false);
            btnThanhToan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnThanhToan.setPreferredSize(new Dimension(225, 36));
            btnThanhToan.addActionListener(e -> {
                dispose();
                Window w = getOwner();
                if (w instanceof LeTanManHinhChinh main) {
                    main.chuyenSangLapHoaDonChoPhieuTiepNhan(phieuTiepNhan);
                }
            });
            pnlNut.add(btnThanhToan);
        } else if (phieuTiepNhan != null && phieuTiepNhan.getTrangThai() == TrangThaiPhieuTiepNhan.THANH_TOAN) {
            JButton btnXemHD = new JButton("Xem Hóa Đơn ➔", IconUtil.layIcon("invoice.png", 14, 14, Color.WHITE));
            btnXemHD.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnXemHD.setForeground(Color.WHITE);
            btnXemHD.setBackground(new Color(79, 70, 229));
            btnXemHD.setFocusPainted(false);
            btnXemHD.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnXemHD.setPreferredSize(new Dimension(160, 36));
            btnXemHD.addActionListener(e -> {
                dispose();
                Window w = getOwner();
                if (w instanceof LeTanManHinhChinh main) {
                    main.chuyenSangLapHoaDonChoPhieuTiepNhan(phieuTiepNhan);
                }
            });
            pnlNut.add(btnXemHD);
        }

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDong.setForeground(Color.WHITE);
        btnDong.setBackground(MAU_XANH_DAM);
        btnDong.setFocusPainted(false);
        btnDong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDong.setPreferredSize(new Dimension(100, 36));
        btnDong.addActionListener(e -> dispose());
        pnlNut.add(btnDong);

        footer.add(pnlNut, BorderLayout.EAST);
        return footer;
    }

    /**
     * Tạo dòng thông tin 2 phần: [Nhãn bên trái] + [Giá trị bên phải]
     */
    private JPanel taoDongThongTin(String nhan, String giaTri) {
        JPanel pnl = new JPanel(new BorderLayout(10, 0));
        pnl.setOpaque(false);

        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNhan.setForeground(MAU_CHU_PHU);
        lblNhan.setPreferredSize(new Dimension(150, 20));

        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGiaTri.setForeground(MAU_CHU_CHINH);

        pnl.add(lblNhan, BorderLayout.WEST);
        pnl.add(lblGiaTri, BorderLayout.CENTER);
        return pnl;
    }

    /**
     * Tạo hộp hiển thị văn bản chi tiết (dành cho mô tả hiện trạng, hỏng hóc)
     */
    private JPanel taoHopVanBanChiTiet(String tieuDe, String noiDung) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTieuDe.setForeground(MAU_CHU_PHU);
        pnl.add(lblTieuDe, BorderLayout.NORTH);

        JTextArea txt = new JTextArea(noiDung);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setForeground(MAU_CHU_CHINH);
        txt.setBackground(new Color(248, 250, 252));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        pnl.add(txt, BorderLayout.CENTER);
        return pnl;
    }

    /**
     * Tiêu đề của từng card
     */
    private JPanel taoTieuDeThe(String iconName, String tieuDe) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel lblIcon = new JLabel(IconUtil.layIcon(iconName, 16, 16, MAU_XANH_DAM));
        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDe.setForeground(MAU_XANH_DAM);

        pnl.add(lblIcon);
        pnl.add(lblTieuDe);
        return pnl;
    }

    /**
     * Tạo badge bo góc hiển thị trạng thái của phiếu tiếp nhận
     */
    private JPanel taoBadgeTrangThai(TrangThaiPhieuTiepNhan tt) {
        String text = (tt != null) ? tt.getTenHienThi() : "Chưa xác định";
        Color mauNen;
        Color mauChu;
        Color mauVien;

        if (tt == TrangThaiPhieuTiepNhan.DANG_KIEM_TRA) {
            mauNen = new Color(254, 243, 199);
            mauChu = new Color(180, 83, 9);
            mauVien = new Color(252, 211, 77);
        } else if (tt == TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA) {
            mauNen = new Color(224, 242, 254);
            mauChu = new Color(3, 105, 161);
            mauVien = new Color(125, 211, 252);
        } else if (tt == TrangThaiPhieuTiepNhan.DANG_SUA_CHUA) {
            mauNen = new Color(255, 237, 213);
            mauChu = new Color(234, 88, 12);
            mauVien = new Color(254, 215, 170);
        } else if (tt == TrangThaiPhieuTiepNhan.HOAN_THANH) {
            mauNen = new Color(220, 252, 231);
            mauChu = new Color(21, 128, 61);
            mauVien = new Color(134, 239, 172);
        } else if (tt == TrangThaiPhieuTiepNhan.THANH_TOAN) {
            mauNen = new Color(238, 242, 255);
            mauChu = new Color(79, 70, 229);
            mauVien = new Color(199, 210, 254);
        } else {
            mauNen = new Color(241, 245, 249);
            mauChu = new Color(100, 116, 139);
            mauVien = new Color(203, 213, 225);
        }

        final Color finalNen = mauNen;
        final Color finalVien = mauVien;
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(finalNen);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(finalVien);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(mauChu);
        badge.add(lbl);
        return badge;
    }

    private String layTenKTVKiemTra(PhieuTiepNhan ptn) {
        if (ptn == null) return "-";
        try {
            PhieuBaoGia pbg = phieuBaoGiaBUS.layBaoGiaMoiNhat(ptn.getMaTiepNhan());
            if (pbg != null && pbg.getNhanVien() != null && pbg.getNhanVien().getChucVu() == enums.ChucVu.KY_THUAT_VIEN) {
                return pbg.getNhanVien().getHoTen() + " (" + pbg.getNhanVien().getMaNhanVien() + ")";
            }
        } catch (Exception ignored) {
        }
        String gc = ptn.getGhiChu();
        if (gc != null && gc.contains("[KTV kiểm tra:")) {
            int start = gc.indexOf("[KTV kiểm tra:") + "[KTV kiểm tra:".length();
            int end = gc.indexOf("]", start);
            if (end > start) {
                return gc.substring(start, end).trim();
            }
        }
        return "Chưa gán";
    }

    private String dinhDangTien(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien) + " đ";
    }

    /**
     * Thẻ nền trắng bo góc nhẹ viền nhạt
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
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 12, 12);
            g2.setColor(Color.WHITE);
            g2.fill(rect);
            g2.setColor(MAU_VIEN);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(rect);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
