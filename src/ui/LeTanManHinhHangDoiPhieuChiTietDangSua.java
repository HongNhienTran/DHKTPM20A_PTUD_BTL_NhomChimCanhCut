package ui;

import bus.PhieuPhanCongBUS;
import bus.PhieuTiepNhanBUS;
import entity.KhachHang;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import entity.TaiKhoan;
import enums.TrangThaiSua;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal Xem Chi Tiết Tiến Trình Xe Đang Sửa Tại Xưởng dành cho Lễ tân.
 * Thiết kế giao diện đúng chuẩn theo hình ảnh mẫu Pengu Garage:
 * - Header thông tin: "PHIẾU TIẾP NHẬN: [Mã] | BIỂN SỐ: [Biển số] | CHỦ XE: [Họ tên] ([SĐT])"
 * - Hộp tóm tắt thông tin tiếp nhận: Xe, thời gian vào, KTV kiểm tra, ODO, số chìa khóa, ngoại thất, nội thất, yêu cầu khách
 * - Bảng phân công nhiệm vụ thi công sửa chữa (Mã phân công, Hạng mục, KTV phụ trách, Thời gian, Trạng thái thi công)
 * - Banner thông báo tiến độ thi công (cảnh báo khi còn nhiệm vụ chưa xong)
 * - Nút thao tác: "✓ Báo Xong ➔ Chuyển Thu Ngân", "Đóng"
 */
public class LeTanManHinhHangDoiPhieuChiTietDangSua extends JDialog {

    private static final Color MAU_NEN_CHINH = new Color(241, 245, 249);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_CYAN_NUT_CHOT = new Color(56, 189, 248);
    private static final Color MAU_CYAN_NUT_HOVER = new Color(14, 165, 233);
    private static final Color MAU_XANH_DUONG_LINK = new Color(37, 99, 235);

    // Màu banner cảnh báo tiến độ
    private static final Color MAU_NEN_CANH_BAO = new Color(255, 251, 235);
    private static final Color MAU_VIEN_CANH_BAO = new Color(253, 230, 138);
    private static final Color MAU_CHU_CANH_BAO = new Color(180, 83, 9);

    // Màu banner hoàn thành tiến độ
    private static final Color MAU_NEN_HOAN_THANH = new Color(240, 253, 244);
    private static final Color MAU_VIEN_HOAN_THANH = new Color(187, 247, 208);
    private static final Color MAU_CHU_HOAN_THANH = new Color(21, 128, 61);

    private static final DateTimeFormatter DINH_DANG_GIO_NGAY = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter DINH_DANG_DAY_DU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuTiepNhan phieuTiepNhan;
    private final Runnable onComplete;

    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();

    private final List<DongTienDoItem> danhSachDongTienDo = new ArrayList<>();

    // Các thành phần UI
    private DefaultTableModel modelBang;
    private JTable bangTienDo;
    private JPanel pnlBannerTienDo;
    private JLabel lblIconBanner;
    private JLabel lblNoiDungBanner;
    private JButton btnBaoXongChuyenThuNgan;
    private JButton btnDong;

    /**
     * DTO lưu trữ dữ liệu hiển thị từng nhiệm vụ thi công
     */
    public static class DongTienDoItem {
        private String maPhanCong;
        private String hangMuc;
        private String ktvPhuTrach;
        private String thoiGianPhanCong;
        private TrangThaiSua trangThaiSua;
        private PhieuPhanCong phieuPhanCongGoc;

        public DongTienDoItem(String maPhanCong, String hangMuc, String ktvPhuTrach,
                              String thoiGianPhanCong, TrangThaiSua trangThaiSua, PhieuPhanCong phieuPhanCongGoc) {
            this.maPhanCong = maPhanCong;
            this.hangMuc = hangMuc;
            this.ktvPhuTrach = ktvPhuTrach;
            this.thoiGianPhanCong = thoiGianPhanCong;
            this.trangThaiSua = trangThaiSua;
            this.phieuPhanCongGoc = phieuPhanCongGoc;
        }

        public String getMaPhanCong() { return maPhanCong; }
        public String getHangMuc() { return hangMuc; }
        public String getKtvPhuTrach() { return ktvPhuTrach; }
        public String getThoiGianPhanCong() { return thoiGianPhanCong; }
        public TrangThaiSua getTrangThaiSua() { return trangThaiSua; }
        public void setTrangThaiSua(TrangThaiSua trangThaiSua) { this.trangThaiSua = trangThaiSua; }
        public PhieuPhanCong getPhieuPhanCongGoc() { return phieuPhanCongGoc; }
    }

    public LeTanManHinhHangDoiPhieuChiTietDangSua() {
        this(null, null, null, null);
    }

    public LeTanManHinhHangDoiPhieuChiTietDangSua(Window owner, TaiKhoan taiKhoanDangNhap, PhieuTiepNhan phieuTiepNhan) {
        this(owner, taiKhoanDangNhap, phieuTiepNhan, null);
    }

    public LeTanManHinhHangDoiPhieuChiTietDangSua(Window owner, TaiKhoan taiKhoanDangNhap, PhieuTiepNhan phieuTiepNhan, Runnable onComplete) {
        super(owner, "Chi Tiết Tiến Trình Xe Tại Xưởng", Dialog.ModalityType.APPLICATION_MODAL);
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        this.phieuTiepNhan = phieuTiepNhan;
        this.onComplete = onComplete;

        napDuLieuTienDo();
        khoiTaoGiaoDien();
        capNhatBannerTienDo();
    }

    private void napDuLieuTienDo() {
        danhSachDongTienDo.clear();

        List<PhieuPhanCong> dsPhanCong = null;
        if (phieuTiepNhan != null && phieuTiepNhan.getMaTiepNhan() != null) {
            try {
                dsPhanCong = phieuPhanCongBUS.layTheoPhieuTiepNhan(phieuTiepNhan.getMaTiepNhan());
            } catch (Exception ignored) {
            }
        }

        if (dsPhanCong != null && !dsPhanCong.isEmpty()) {
            for (PhieuPhanCong ppc : dsPhanCong) {
                String hangMuc = "Sửa chữa kỹ thuật";
                if (ppc.getChiTietBaoGia() != null && ppc.getChiTietBaoGia().getDichVu() != null) {
                    hangMuc = ppc.getChiTietBaoGia().getDichVu().getTenDichVu();
                }
                String tenKTV = (ppc.getNhanVien() != null) ? ppc.getNhanVien().getHoTen() : "Chưa chỉ định";
                String thoiGian = (ppc.getThoiGianPhanCong() != null)
                        ? ppc.getThoiGianPhanCong().format(DINH_DANG_GIO_NGAY)
                        : "-";

                danhSachDongTienDo.add(new DongTienDoItem(
                        ppc.getMaPhanCong(),
                        hangMuc,
                        tenKTV,
                        thoiGian,
                        ppc.getTrangThaiSua() != null ? ppc.getTrangThaiSua() : TrangThaiSua.CHO_THUC_HIEN,
                        ppc
                ));
            }
        } else {
            // Dữ liệu mẫu dự phòng đúng theo hình thiết kế
            String tenKTVMacDinh = "Trần Quốc Bảo";
            if (phieuTiepNhan != null && phieuTiepNhan.getNhanVien() != null) {
                tenKTVMacDinh = phieuTiepNhan.getNhanVien().getHoTen();
            }
            danhSachDongTienDo.add(new DongTienDoItem(
                    "PC20261104-010",
                    "Bảo dưỡng định kỳ tổng quát",
                    tenKTVMacDinh,
                    "04/11 13:22",
                    TrangThaiSua.CHO_THUC_HIEN,
                    null
            ));
        }
    }

    private void khoiTaoGiaoDien() {
        Dimension manHinh = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int rong = Math.min(1160, manHinh.width - 60);
        int cao = Math.min(760, manHinh.height - 60);
        setSize(rong, cao);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN_CHINH);

        // 1. Header trên cùng
        add(taoHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính: Hộp thông số xe + Bảng phân công tiến độ + Banner cảnh báo
        JPanel pnlNoiDung = new JPanel(new BorderLayout(0, 12));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Hộp thông số tiếp nhận trên đầu
        pnlNoiDung.add(taoHopThongSoTiepNhan(), BorderLayout.NORTH);

        // Thẻ trắng bo góc chứa Bảng danh sách nhiệm vụ phân công
        TheBoGocTrang cardBang = new TheBoGocTrang(new BorderLayout(0, 10));
        cardBang.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Tiêu đề bảng: Icon + Text
        JPanel pnlTieuDeBang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTieuDeBang.setOpaque(false);
        JLabel lblIconBang = new JLabel(IconUtil.layIcon("service.png", 18, 18, MAU_XANH_DAM));
        JLabel lblTieuDeBang = new JLabel("Bảng Phân Công Nhiệm Vụ Thi Công Sửa Chữa (Chi Tiết Từng Hạng Mục):");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);
        pnlTieuDeBang.add(lblIconBang);
        pnlTieuDeBang.add(lblTieuDeBang);
        cardBang.add(pnlTieuDeBang, BorderLayout.NORTH);

        // Bảng dữ liệu JTable
        cardBang.add(taoVungCuonBang(), BorderLayout.CENTER);

        // Banner tiến độ thi công (đặt dưới chân card)
        pnlBannerTienDo = taoBannerTienDo();
        cardBang.add(pnlBannerTienDo, BorderLayout.SOUTH);

        pnlNoiDung.add(cardBang, BorderLayout.CENTER);

        add(pnlNoiDung, BorderLayout.CENTER);

        // 3. Footer thanh nút thao tác dưới cùng
        add(taoFooterThaoTac(), BorderLayout.SOUTH);
    }

    /**
     * Header hiển thị: "PHIẾU TIẾP NHẬN: [Mã] | BIỂN SỐ: [Biển số] | CHỦ XE: [Họ tên] ([SĐT])"
     */
    private JPanel taoHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN),
                new EmptyBorder(14, 18, 12, 18)
        ));

        String maTN = (phieuTiepNhan != null && phieuTiepNhan.getMaTiepNhan() != null)
                ? phieuTiepNhan.getMaTiepNhan()
                : "-";

        String bienSo = (phieuTiepNhan != null && phieuTiepNhan.getPhuongTien() != null && phieuTiepNhan.getPhuongTien().getBienSoXe() != null)
                ? phieuTiepNhan.getPhuongTien().getBienSoXe()
                : "-";

        String chuXe = "-";
        if (phieuTiepNhan != null && phieuTiepNhan.getPhuongTien() != null && phieuTiepNhan.getPhuongTien().getKhachHang() != null) {
            KhachHang kh = phieuTiepNhan.getPhuongTien().getKhachHang();
            chuXe = (kh.getHoTen() != null ? kh.getHoTen() : "-") + (kh.getSoDienThoai() != null ? " (" + kh.getSoDienThoai() + ")" : "");
        }

        JLabel lblThongTinHeader = new JLabel("PHIẾU TIẾP NHẬN: " + maTN + " | BIỂN SỐ: " + bienSo + " | CHỦ XE: " + chuXe);
        lblThongTinHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblThongTinHeader.setForeground(new Color(30, 41, 59));

        header.add(lblThongTinHeader, BorderLayout.WEST);
        return header;
    }

    /**
     * Hộp tóm tắt thông số tiếp nhận xe (3 dòng):
     * Dòng 1: Xe | Vào lúc | KTV kiểm tra
     * Dòng 2: ODO | Chìa khóa | Ngoại thất | Nội thất
     * Dòng 3: Yêu cầu khách
     */
    private JPanel taoHopThongSoTiepNhan() {
        TheBoGocTrang pnl = new TheBoGocTrang(new BorderLayout());
        pnl.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel pnlNoiDung = new JPanel();
        pnlNoiDung.setLayout(new BoxLayout(pnlNoiDung, BoxLayout.Y_AXIS));
        pnlNoiDung.setOpaque(false);

        PhuongTien pt = (phieuTiepNhan != null) ? phieuTiepNhan.getPhuongTien() : null;
        String tenXe = (pt != null && pt.getMauXe() != null) ? pt.getMauXe() : "-";
        String thoiGianVao = (phieuTiepNhan != null && phieuTiepNhan.getThoiGianTiepNhan() != null)
                ? phieuTiepNhan.getThoiGianTiepNhan().format(DINH_DANG_DAY_DU)
                : "-";
        String tenKTV = (phieuTiepNhan != null && phieuTiepNhan.getNhanVien() != null && phieuTiepNhan.getNhanVien().getHoTen() != null)
                ? phieuTiepNhan.getNhanVien().getHoTen()
                : "-";

        double odo = (phieuTiepNhan != null) ? phieuTiepNhan.getSoKMHienTai() : 0;
        int soChiaKhoa = (phieuTiepNhan != null) ? phieuTiepNhan.getSoChiaKhoa() : 0;
        String ngoaiThat = (phieuTiepNhan != null && phieuTiepNhan.getTinhTrangNgoaiThat() != null && !phieuTiepNhan.getTinhTrangNgoaiThat().isBlank())
                ? phieuTiepNhan.getTinhTrangNgoaiThat() : "-";
        String noiThat = (phieuTiepNhan != null && phieuTiepNhan.getTinhTrangNoiThat() != null && !phieuTiepNhan.getTinhTrangNoiThat().isBlank())
                ? phieuTiepNhan.getTinhTrangNoiThat() : "-";
        String yeuCau = (phieuTiepNhan != null && phieuTiepNhan.getYeuCauKhachHang() != null && !phieuTiepNhan.getYeuCauKhachHang().isBlank())
                ? phieuTiepNhan.getYeuCauKhachHang() : "-";

        // Dòng 1: Xe | Vào lúc | KTV kiểm tra
        JPanel d1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        d1.setOpaque(false);
        d1.add(taoItemThongSo("car.png", "Xe: ", tenXe, true));
        d1.add(taoNhanPhanCach());
        d1.add(taoItemThongSo("history.png", "Vào lúc: ", thoiGianVao, true));
        d1.add(taoNhanPhanCach());
        d1.add(taoItemThongSo("user.png", "KTV kiểm tra: ", tenKTV, true));

        // Dòng 2: ODO | Chìa khóa | Ngoại thất | Nội thất
        JPanel d2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        d2.setOpaque(false);
        d2.add(taoItemThongSo("chart.png", "ODO: ", String.format("%,.0f km", odo), false));
        d2.add(taoNhanPhanCach());
        d2.add(taoItemThongSo("key.png", "Chìa khóa: ", String.valueOf(soChiaKhoa), false));
        d2.add(taoNhanPhanCach());
        d2.add(taoItemThongSo("search.png", "Ngoại thất: ", ngoaiThat, false));
        d2.add(taoNhanPhanCach());
        d2.add(taoItemThongSo("part.png", "Nội thất: ", noiThat, false));

        // Dòng 3: Yêu cầu khách
        JPanel d3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        d3.setOpaque(false);
        JLabel lblIconYeuCau = new JLabel(IconUtil.layIcon("edit.png", 13, 13, MAU_XANH_DUONG_LINK));
        JLabel lblYeuCauTieuDe = new JLabel("Yêu cầu khách: ");
        lblYeuCauTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblYeuCauTieuDe.setForeground(MAU_XANH_DUONG_LINK);
        JLabel lblYeuCauGiaTri = new JLabel(yeuCau);
        lblYeuCauGiaTri.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblYeuCauGiaTri.setForeground(MAU_XANH_DUONG_LINK);
        d3.add(lblIconYeuCau);
        d3.add(lblYeuCauTieuDe);
        d3.add(lblYeuCauGiaTri);

        pnlNoiDung.add(d1);
        pnlNoiDung.add(Box.createVerticalStrut(6));
        pnlNoiDung.add(d2);
        pnlNoiDung.add(Box.createVerticalStrut(6));
        pnlNoiDung.add(d3);

        pnl.add(pnlNoiDung, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoItemThongSo(String iconName, String label, String value, boolean dam) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        p.add(new JLabel(IconUtil.layIcon(iconName, 13, 13, MAU_CHU_CHINH)));
        JLabel lblNhan = new JLabel(label);
        lblNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNhan.setForeground(MAU_CHU_PHU);
        JLabel lblVal = new JLabel(value != null && !value.isBlank() ? value : "-");
        lblVal.setFont(new Font("Segoe UI", dam ? Font.BOLD : Font.PLAIN, 12));
        lblVal.setForeground(MAU_CHU_CHINH);
        p.add(lblNhan);
        p.add(lblVal);
        return p;
    }

    private JLabel taoNhanPhanCach() {
        JLabel lbl = new JLabel("|");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(203, 213, 225));
        return lbl;
    }

    /**
     * Tạo JTable hiển thị danh sách nhiệm vụ phân công thi công
     */
    private JScrollPane taoVungCuonBang() {
        String[] tieuDeCot = {
                "Mã Phân Công", "Hạng Mục / Linh Kiện", "KTV Phụ Trách", "Thời Gian Phân Công", "Trạng Thái Thi Công"
        };

        modelBang = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chế độ xem theo dõi tiến độ: Không cho phép lễ tân tự ý sửa trạng thái của KTV
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 4) ? TrangThaiSua.class : String.class;
            }
        };

        bangTienDo = new JTable(modelBang);
        bangTienDo.setRowHeight(38);
        bangTienDo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangTienDo.setForeground(MAU_CHU_CHINH);
        bangTienDo.setGridColor(new Color(235, 238, 242));
        bangTienDo.setShowVerticalLines(true);
        bangTienDo.setShowHorizontalLines(true);
        bangTienDo.setSelectionBackground(new Color(239, 246, 255));
        bangTienDo.setSelectionForeground(MAU_CHU_CHINH);
        bangTienDo.getTableHeader().setReorderingAllowed(false);
        bangTienDo.getTableHeader().setPreferredSize(new Dimension(0, 36));
        bangTienDo.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bangTienDo.getTableHeader().setBackground(new Color(241, 245, 249));
        bangTienDo.getTableHeader().setForeground(new Color(51, 65, 85));

        TableColumnModel colModel = bangTienDo.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(140); // Mã Phân Công
        colModel.getColumn(1).setPreferredWidth(320); // Hạng Mục / Linh Kiện
        colModel.getColumn(2).setPreferredWidth(220); // KTV Phụ Trách
        colModel.getColumn(3).setPreferredWidth(160); // Thời Gian Phân Công
        colModel.getColumn(4).setPreferredWidth(190); // Trạng Thái Thi Công

        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);

        colModel.getColumn(0).setCellRenderer(canGiua);
        colModel.getColumn(3).setCellRenderer(canGiua);

        // Thiết lập ComboBox cho cột Trạng Thái Thi Công
        caiDatCotTrangThai();

        // Nạp dữ liệu
        napDuLieuVaoBang();

        JScrollPane cuon = new JScrollPane(bangTienDo);
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.setPreferredSize(new Dimension(0, 280));
        return cuon;
    }

    private void caiDatCotTrangThai() {
        JComboBox<TrangThaiSua> cbTrangThai = new JComboBox<>(TrangThaiSua.values());
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTrangThai.setBackground(Color.WHITE);

        TableColumnModel colModel = bangTienDo.getColumnModel();
        colModel.getColumn(4).setCellEditor(new DefaultCellEditor(cbTrangThai));

        // Renderer hiển thị màu badge nhẹ hoặc dropdown text
        colModel.getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof TrangThaiSua tt) {
                    setText(tt.getTenHienThi());
                    if (tt == TrangThaiSua.HOAN_THANH) {
                        setForeground(new Color(16, 185, 129));
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (tt == TrangThaiSua.DANG_SUA) {
                        setForeground(new Color(234, 106, 31));
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        setForeground(MAU_CHU_CHINH);
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    }
                }
                return c;
            }
        });
    }

    private void napDuLieuVaoBang() {
        modelBang.setRowCount(0);
        for (DongTienDoItem item : danhSachDongTienDo) {
            modelBang.addRow(new Object[]{
                    item.getMaPhanCong(),
                    item.getHangMuc(),
                    item.getKtvPhuTrach(),
                    item.getThoiGianPhanCong(),
                    item.getTrangThaiSua()
            });
        }
    }

    /**
     * Banner thông báo tiến độ thi công:
     * - Khi chưa xong: "⚠️ TIẾN ĐỘ THI CÔNG: X/Y nhiệm vụ hoàn thành. Vẫn còn Z nhiệm vụ chưa xong -> CHƯA THỂ BÁO XONG."
     * - Khi đã xong: "✓ TIẾN ĐỘ THI CÔNG: X/X nhiệm vụ hoàn thành. ĐÃ HOÀN THÀNH TẤT CẢ NHIỆM VỤ -> CÓ THỂ BÁO XONG."
     */
    private JPanel taoBannerTienDo() {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8);
                g2.setColor(getBackground());
                g2.fill(rect);
                g2.setColor(kiemTraTatCaHoanThanh() ? MAU_VIEN_HOAN_THANH : MAU_VIEN_CANH_BAO);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(rect);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(4, 10, 4, 10));

        lblIconBanner = new JLabel(IconUtil.layIcon("alert.png", 16, 16, MAU_CHU_CANH_BAO));

        lblNoiDungBanner = new JLabel("");
        lblNoiDungBanner.setFont(new Font("Segoe UI", Font.BOLD, 12));

        banner.add(lblIconBanner);
        banner.add(lblNoiDungBanner);
        return banner;
    }

    private boolean kiemTraTatCaHoanThanh() {
        if (danhSachDongTienDo.isEmpty()) return false;
        for (DongTienDoItem item : danhSachDongTienDo) {
            if (item.getTrangThaiSua() != TrangThaiSua.HOAN_THANH) {
                return false;
            }
        }
        return true;
    }

    private void capNhatBannerTienDo() {
        int tong = danhSachDongTienDo.size();
        int daXong = 0;
        for (DongTienDoItem item : danhSachDongTienDo) {
            if (item.getTrangThaiSua() == TrangThaiSua.HOAN_THANH) {
                daXong++;
            }
        }

        int chuaXong = tong - daXong;
        boolean tatCaXong = (daXong == tong && tong > 0);

        if (tatCaXong) {
            pnlBannerTienDo.setBackground(MAU_NEN_HOAN_THANH);
            lblIconBanner.setText("");
            lblIconBanner.setIcon(IconUtil.layIcon("check.png", 16, 16, MAU_CHU_HOAN_THANH));
            lblNoiDungBanner.setForeground(MAU_CHU_HOAN_THANH);
            lblNoiDungBanner.setText("TIẾN ĐỘ THI CÔNG: " + daXong + "/" + tong + " nhiệm vụ hoàn thành. TẤT CẢ KỸ THUẬT VIÊN ĐÃ XÁC NHẬN HOÀN TẤT CA SỬA.");
            if (btnBaoXongChuyenThuNgan != null) {
                btnBaoXongChuyenThuNgan.setEnabled(true);
                btnBaoXongChuyenThuNgan.setText("Chuyển Sang Thu Ngân ➔");
                btnBaoXongChuyenThuNgan.setToolTipText("Chuyển sang màn hình Thu Ngân để bắt đầu lập hóa đơn");
                btnBaoXongChuyenThuNgan.repaint();
            }
        } else {
            pnlBannerTienDo.setBackground(MAU_NEN_CANH_BAO);
            lblIconBanner.setText("");
            lblIconBanner.setIcon(IconUtil.layIcon("alert.png", 16, 16, MAU_CHU_CANH_BAO));
            lblNoiDungBanner.setForeground(MAU_CHU_CANH_BAO);
            lblNoiDungBanner.setText("TIẾN ĐỘ THI CÔNG: " + daXong + "/" + tong + " nhiệm vụ hoàn thành. Vẫn còn " + chuaXong + " ca đang thực hiện. Đang chờ KTV xác nhận hoàn tất.");
            if (btnBaoXongChuyenThuNgan != null) {
                btnBaoXongChuyenThuNgan.setEnabled(false);
                btnBaoXongChuyenThuNgan.setText("Đang Thi Công (" + daXong + "/" + tong + " ca xong)");
                btnBaoXongChuyenThuNgan.setToolTipText("Chờ tất cả kỹ thuật viên xác nhận hoàn thành nhiệm vụ trước khi chuyển Thu Ngân");
                btnBaoXongChuyenThuNgan.repaint();
            }
        }
        pnlBannerTienDo.repaint();
    }

    /**
     * Footer thanh thao tác:
     * - "Chuyển Sang Thu Ngân ➔" (Chỉ khả dụng khi KTV đã hoàn thành 100% nhiệm vụ)
     * - "Đóng"
     */
    private JPanel taoFooterThaoTac() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN));

        // Nút trạng thái thi công / chuyển thu ngân
        btnBaoXongChuyenThuNgan = new JButton("Đang Thi Công") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color mauNen = isEnabled()
                        ? (getModel().isRollover() ? MAU_CYAN_NUT_HOVER : MAU_CYAN_NUT_CHOT)
                        : new Color(241, 245, 249);
                g2.setColor(mauNen);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                setForeground(isEnabled() ? MAU_XANH_DAM : new Color(148, 163, 184));
                super.paintComponent(g);
            }
        };
        btnBaoXongChuyenThuNgan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBaoXongChuyenThuNgan.setFocusPainted(false);
        btnBaoXongChuyenThuNgan.setBorderPainted(false);
        btnBaoXongChuyenThuNgan.setContentAreaFilled(false);
        btnBaoXongChuyenThuNgan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBaoXongChuyenThuNgan.setPreferredSize(new Dimension(250, 36));
        btnBaoXongChuyenThuNgan.addActionListener(e -> xuLyBaoXongChuyenThuNgan());

        // Nút Đóng
        btnDong = new JButton("Đóng") {
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
        btnDong.setPreferredSize(new Dimension(90, 36));
        btnDong.addActionListener(e -> dispose());

        footer.add(btnBaoXongChuyenThuNgan);
        footer.add(btnDong);
        return footer;
    }

    /**
     * Xử lý chuyển sang giao diện Thu Ngân khi tất cả KTV đã hoàn thành nhiệm vụ
     */
    private void xuLyBaoXongChuyenThuNgan() {
        boolean tatCaXong = kiemTraTatCaHoanThanh();

        if (!tatCaXong) {
            JOptionPane.showMessageDialog(this,
                    "Không thể hoàn thành phiếu tiếp nhận vì còn nhiệm vụ kỹ thuật viên chưa hoàn thành!\n" +
                    "Kỹ thuật viên phụ trách phải là người trực tiếp xác nhận hoàn tất ca sửa trên hệ thống.",
                    "Chưa hoàn tất sửa chữa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int hoi = JOptionPane.showConfirmDialog(this,
                "Tất cả nhiệm vụ thi công đã được kỹ thuật viên hoàn thành!\n" +
                        "Chuyển sang màn hình Thu Ngân để bắt đầu lập hóa đơn và thanh toán?",
                "Chuyển sang Thu Ngân",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (hoi != JOptionPane.YES_OPTION) {
            return;
        }

        if (onComplete != null) {
            onComplete.run();
        }
        dispose();

        Window w = getOwner();
        if (w instanceof LeTanManHinhChinh main) {
            main.chuyenManHinh(LeTanManHinhChinh.MAN_LAP_HOA_DON);
        }
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

    /**
     * Hàm main hỗ trợ kiểm tra xem trước giao diện độc lập
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LeTanManHinhHangDoiPhieuChiTietDangSua dialog = new LeTanManHinhHangDoiPhieuChiTietDangSua();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
