package ui;

import bus.NhanVienBUS;
import bus.PhieuBaoGiaBUS;
import bus.PhieuPhanCongBUS;
import bus.PhieuTiepNhanBUS;
import dao.PhieuPhanCongDAO;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DichVu;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import entity.TaiKhoan;
import enums.ChuyenMon;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiSua;
import util.IconUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/**
 * Modal Báo Giá & Phân Công Sửa Chữa dành cho Lễ tân.
 * Thiết kế giao diện đúng chuẩn theo hình ảnh mẫu Pengu Garage:
 * - Header thông tin xe & khách hàng: "BÁO GIÁ XE: [Biển số] | Khách hàng: [Tên KH]"
 * - Bảng Báo Giá Chi Tiết & Phân Công Nhiệm Vụ Cho Kỹ Thuật Viên (Dịch vụ, Phụ tùng, VAT, KTV phụ trách)
 * - Khung tổng hợp tiền: Tiền dịch vụ & phụ tùng, Thuế VAT (8% và 10%), Tổng tiền dự kiến
 * - Ghi chú cho thợ (nếu có)
 * - Nút: "✕ Khách Từ Chối", "✓ Khách Chốt & Phân Công Thợ", "Đóng"
 */
public class LeTanManHinhHangDoiPhieuPhanCong extends JDialog {

    // Bảng màu chuẩn giao diện Pengu Garage
    private static final Color MAU_NEN_CHINH = new Color(241, 245, 249);
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_CHINH = new Color(30, 41, 59);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CYAN_NUT_CHOT = new Color(56, 189, 248);
    private static final Color MAU_CYAN_NUT_HOVER = new Color(14, 165, 233);
    private static final Color MAU_XANH_DUONG_VAT = new Color(37, 99, 235);
    private static final Color MAU_DO_TU_CHOI = new Color(225, 29, 72);

    private final TaiKhoan taiKhoanDangNhap;
    private final PhieuTiepNhan phieuTiepNhan;
    private final Runnable onComplete;

    private final PhieuBaoGiaBUS phieuBaoGiaBUS = new PhieuBaoGiaBUS();
    private final PhieuPhanCongBUS phieuPhanCongBUS = new PhieuPhanCongBUS();
    private final PhieuPhanCongDAO phieuPhanCongDAO = new PhieuPhanCongDAO();
    private final PhieuTiepNhanBUS phieuTiepNhanBUS = new PhieuTiepNhanBUS();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();

    private PhieuBaoGia phieuBaoGia;
    private List<NhanVien> danhSachKTV = new ArrayList<>();
    private final List<DongBaoGiaItem> danhSachDongBaoGia = new ArrayList<>();

    // Các thành phần UI
    private DefaultTableModel modelBang;
    private JTable bangBaoGia;
    private JLabel lblTienChuaThue;
    private JLabel lblTienVAT;
    private JLabel lblTongTienPhaiTra;
    private JTextField txtGhiChuChoTho;
    private JButton btnKhachTuChoi;
    private JButton btnKhachChot;
    private JButton btnDong;

    /**
     * DTO lưu dữ liệu từng dòng bảng báo giá & phân công
     */
    public static class DongBaoGiaItem {
        private String phanLoai; // "Dịch vụ" hoặc "Phụ tùng"
        private String hangMuc;  // Tên dịch vụ / phụ tùng
        private int soLuong;
        private double donGia;
        private double thueVAT;  // 0.08 hoặc 0.10
        private NhanVien ktvPhanCong;
        private ChiTietBaoGia chiTietBaoGia;
        private ChiTietDungPhuTung chiTietDungPhuTung;

        public DongBaoGiaItem(String phanLoai, String hangMuc, int soLuong, double donGia, double thueVAT,
                              NhanVien ktvPhanCong, ChiTietBaoGia chiTietBaoGia, ChiTietDungPhuTung chiTietDungPhuTung) {
            this.phanLoai = phanLoai;
            this.hangMuc = hangMuc;
            this.soLuong = soLuong;
            this.donGia = donGia;
            // Chuẩn hóa thuế VAT: Nếu giá trị lưu từ DB dạng phần trăm (VD: 8.0, 10.0) thì quy về hệ số thập phân (0.08, 0.10)
            this.thueVAT = (thueVAT > 1.0) ? (thueVAT / 100.0) : thueVAT;
            this.ktvPhanCong = ktvPhanCong;
            this.chiTietBaoGia = chiTietBaoGia;
            this.chiTietDungPhuTung = chiTietDungPhuTung;
        }

        public double tinhThanhTien() {
            return soLuong * donGia;
        }

        public double tinhTienVAT() {
            return tinhThanhTien() * thueVAT;
        }

        public String getPhanLoai() { return phanLoai; }
        public String getHangMuc() { return hangMuc; }
        public int getSoLuong() { return soLuong; }
        public double getDonGia() { return donGia; }
        public double getThueVAT() { return thueVAT; }
        public NhanVien getKtvPhanCong() { return ktvPhanCong; }
        public void setKtvPhanCong(NhanVien ktvPhanCong) { this.ktvPhanCong = ktvPhanCong; }
        public ChiTietBaoGia getChiTietBaoGia() { return chiTietBaoGia; }
        public ChiTietDungPhuTung getChiTietDungPhuTung() { return chiTietDungPhuTung; }
    }

    /**
     * Item bọc Nhân viên hiển thị trong ComboBox kèm thông tin khối lượng công việc hiện tại
     */
    public static class NhanVienItem {
        private final NhanVien nhanVien;
        private final int soViecDangLam;

        public NhanVienItem(NhanVien nhanVien) {
            this(nhanVien, -1);
        }

        public NhanVienItem(NhanVien nhanVien, int soViecDangLam) {
            this.nhanVien = nhanVien;
            this.soViecDangLam = soViecDangLam;
        }

        public NhanVien getNhanVien() {
            return nhanVien;
        }

        public int getSoViecDangLam() {
            return soViecDangLam;
        }

        @Override
        public String toString() {
            if (nhanVien == null) {
                return "Chưa phân công";
            }
            String cmStr = (nhanVien.getChuyenMon() != null) ? " [" + nhanVien.getChuyenMon().getTenHienThi() + "]" : "";
            String trangThaiKTV = "";
            if (soViecDangLam == 0) {
                trangThaiKTV = " 🟢 (Rảnh tay)";
            } else if (soViecDangLam > 0) {
                trangThaiKTV = " 🟡 (Đang có " + soViecDangLam + " việc)";
            }
            return nhanVien.getHoTen() + " (" + nhanVien.getMaNhanVien() + ")" + cmStr + trangThaiKTV;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NhanVienItem that = (NhanVienItem) o;
            if (nhanVien == null || that.nhanVien == null) return false;
            return nhanVien.getMaNhanVien().equals(that.nhanVien.getMaNhanVien());
        }

        @Override
        public int hashCode() {
            return nhanVien != null ? nhanVien.getMaNhanVien().hashCode() : 0;
        }
    }

    public LeTanManHinhHangDoiPhieuPhanCong() {
        this(null, null, null, null);
    }

    public LeTanManHinhHangDoiPhieuPhanCong(Window owner, TaiKhoan taiKhoanDangNhap, PhieuTiepNhan phieuTiepNhan) {
        this(owner, taiKhoanDangNhap, phieuTiepNhan, null);
    }

    public LeTanManHinhHangDoiPhieuPhanCong(Window owner, TaiKhoan taiKhoanDangNhap, PhieuTiepNhan phieuTiepNhan, Runnable onComplete) {
        super(owner, "Báo Giá & Phân Công Sửa Chữa", Dialog.ModalityType.APPLICATION_MODAL);
        this.taiKhoanDangNhap = taiKhoanDangNhap;
        this.phieuTiepNhan = phieuTiepNhan;
        this.onComplete = onComplete;

        napDanhSachKyThuatVien();
        napDuLieuBaoGia();

        khoiTaoGiaoDien();
        tinhToanTongTien();
    }

    private void napDanhSachKyThuatVien() {
        try {
            danhSachKTV = nhanVienBUS.layKyThuatVienDangLamViec();
        } catch (Exception ignored) {
        }
        if (danhSachKTV == null) {
            danhSachKTV = new ArrayList<>();
        }
    }

    /**
     * Lọc và sắp xếp thông minh danh sách KTV:
     * 1. Chỉ lấy những KTV đúng chuyên môn của dịch vụ đó để lễ tân dễ dàng phân công.
     * 2. Ưu tiên KTV đang rảnh tay (0 việc đang sửa) xếp lên trên cùng.
     * 3. Nếu tất cả đều bận thì xếp theo số lượng việc đang sửa tăng dần (người ít việc nhất xếp trước, có khả năng rảnh sớm nhất).
     */
    private List<NhanVien> layDanhSachKTVPhuHopVaSapXep(ChuyenMon cm) {
        List<NhanVien> ds = (cm != null) ? nhanVienBUS.layKyThuatVienTheoChuyenMon(cm) : new ArrayList<>(danhSachKTV);
        if (ds == null || ds.isEmpty()) {
            ds = new ArrayList<>(danhSachKTV);
        }
        Map<String, Integer> mapSoViec = phieuPhanCongBUS.laySoLuongViecDangXuLyCuaTatCaKTV();
        ds.sort((nv1, nv2) -> {
            int v1 = mapSoViec.getOrDefault(nv1.getMaNhanVien(), 0);
            int v2 = mapSoViec.getOrDefault(nv2.getMaNhanVien(), 0);
            if (v1 != v2) {
                return Integer.compare(v1, v2); // Ưu tiên ít việc nhất (0 việc lên đầu)
            }
            return nv1.getHoTen().compareToIgnoreCase(nv2.getHoTen());
        });
        return ds;
    }

    private void napDuLieuBaoGia() {
        danhSachDongBaoGia.clear();

        if (phieuTiepNhan != null && phieuTiepNhan.getMaTiepNhan() != null) {
            try {
                this.phieuBaoGia = phieuBaoGiaBUS.layBaoGiaMoiNhat(phieuTiepNhan.getMaTiepNhan());
            } catch (Exception ignored) {
            }
        }

        if (this.phieuBaoGia == null && phieuTiepNhan != null) {
            try {
                this.phieuBaoGia = phieuBaoGiaBUS.khoiTaoBaoGiaBanDau(phieuTiepNhan, phieuTiepNhan.getNhanVien(), new ArrayList<>());
            } catch (Exception ignored) {
            }
        }

        if (phieuBaoGia != null && phieuBaoGia.getDanhSachChiTiet() != null && !phieuBaoGia.getDanhSachChiTiet().isEmpty()) {
            for (ChiTietBaoGia ctbg : phieuBaoGia.getDanhSachChiTiet()) {
                DichVu dv = ctbg.getDichVu();
                PhieuPhanCong pcDaCo = null;
                if (dv != null && dv.getMaDichVu() != null) {
                    try {
                        pcDaCo = phieuPhanCongDAO.timTheoChiTietBaoGia(phieuBaoGia.getMaBaoGia(), dv.getMaDichVu());
                    } catch (Exception ignored) {
                    }
                }
                boolean daHoanThanh = (pcDaCo != null && pcDaCo.getTrangThaiSua() == TrangThaiSua.HOAN_THANH);

                String tenDV = (dv != null) ? dv.getTenDichVu() : "Dịch vụ kỹ thuật";
                ChuyenMon cm = (dv != null) ? dv.getChuyenMon() : null;
                double donGiaDV = ctbg.getDonGiaDichVu();
                double thueDV = (dv != null) ? dv.getThueVAT() : 0.08;

                // Tự động gán KTV: Nếu đã có KTV hoàn thành (ví dụ KTV kiểm tra xe), giữ nguyên KTV đó.
                // Ngược lại: Lọc theo chuyên môn dịch vụ và ưu tiên KTV rảnh tay nhất xếp đầu
                List<NhanVien> dsKTVPhuHop = layDanhSachKTVPhuHopVaSapXep(cm);
                NhanVien ktvChiDinh = (pcDaCo != null && pcDaCo.getNhanVien() != null)
                        ? pcDaCo.getNhanVien()
                        : (!dsKTVPhuHop.isEmpty() ? dsKTVPhuHop.get(0) : null);

                String hienThiDV = tenDV + (cm != null ? " [" + cm.getTenHienThi() + "]" : "");
                if (daHoanThanh) {
                    hienThiDV += " (✓ Đã xong kiểm tra)";
                }
                danhSachDongBaoGia.add(new DongBaoGiaItem(
                        "Dịch vụ", hienThiDV, 1, donGiaDV, thueDV, ktvChiDinh, ctbg, null
                ));

                if (ctbg.getDanhSachPhuTung() != null) {
                    for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                        String tenPT = (pt.getSanPham() != null) ? pt.getSanPham().getTenSanPham() : "Phụ tùng thay thế";
                        int sl = pt.getSoLuong();
                        double donGiaPT = pt.getDonGia();
                        double thuePT = (pt.getSanPham() != null) ? pt.getSanPham().getThueVAT() : 0.10;

                        danhSachDongBaoGia.add(new DongBaoGiaItem(
                                "Phụ tùng", "   ↳ " + tenPT, sl, donGiaPT, thuePT, ktvChiDinh, ctbg, pt
                        ));
                    }
                }
            }
        }
    }

    private void khoiTaoGiaoDien() {
        Dimension manHinh = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int rong = Math.min(1180, manHinh.width - 60);
        int cao = Math.min(780, manHinh.height - 60);
        setSize(rong, cao);
        setMinimumSize(new Dimension(1020, 640));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(MAU_NEN_CHINH);

        // 1. Header trên cùng
        add(taoHeaderPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính: Thẻ trắng chứa Bảng chi tiết + Khung tổng tiền + Ghi chú cho thợ
        JPanel pnlNoiDung = new JPanel(new BorderLayout(0, 12));
        pnlNoiDung.setOpaque(false);
        pnlNoiDung.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Thẻ trắng bo góc chứa Bảng báo giá & tổng tiền
        TheBoGocTrang cardBang = new TheBoGocTrang(new BorderLayout(0, 12));
        cardBang.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Tiêu đề bảng: Icon + Text
        JPanel pnlTieuDeBang = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTieuDeBang.setOpaque(false);
        JLabel lblIconBang = new JLabel(IconUtil.layIcon("invoice.png", 18, 18, MAU_XANH_DAM));
        JLabel lblTieuDeBang = new JLabel("Bảng Báo Giá Chi Tiết & Phân Công Nhiệm Vụ Cho Kỹ Thuật Viên:");
        lblTieuDeBang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDeBang.setForeground(MAU_XANH_DAM);
        pnlTieuDeBang.add(lblIconBang);
        pnlTieuDeBang.add(lblTieuDeBang);
        cardBang.add(pnlTieuDeBang, BorderLayout.NORTH);

        // Bảng dữ liệu JTable
        cardBang.add(taoVungCuonBang(), BorderLayout.CENTER);

        // Khung tổng hợp tiền dưới chân bảng
        cardBang.add(taoKhungTongHopTien(), BorderLayout.SOUTH);

        pnlNoiDung.add(cardBang, BorderLayout.CENTER);

        // Hàng ghi chú cho thợ (dưới thẻ trắng)
        pnlNoiDung.add(taoHangGhiChuChoTho(), BorderLayout.SOUTH);

        add(pnlNoiDung, BorderLayout.CENTER);

        // 3. Footer thanh nút thao tác dưới cùng
        add(taoFooterThaoTac(), BorderLayout.SOUTH);
    }

    /**
     * Header hiển thị: "BÁO GIÁ XE: [Biển số] | Khách hàng: [Tên khách hàng]"
     */
    private JPanel taoHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN),
                new EmptyBorder(14, 18, 12, 18)
        ));

        String bienSo = (phieuTiepNhan != null && phieuTiepNhan.getPhuongTien() != null && phieuTiepNhan.getPhuongTien().getBienSoXe() != null)
                ? phieuTiepNhan.getPhuongTien().getBienSoXe()
                : "-";

        String tenKH = "-";
        if (phieuTiepNhan != null && phieuTiepNhan.getPhuongTien() != null && phieuTiepNhan.getPhuongTien().getKhachHang() != null
                && phieuTiepNhan.getPhuongTien().getKhachHang().getHoTen() != null) {
            tenKH = phieuTiepNhan.getPhuongTien().getKhachHang().getHoTen();
        }

        JLabel lblThongTinHeader = new JLabel("BÁO GIÁ XE: " + bienSo + " | Khách hàng: " + tenKH);
        lblThongTinHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblThongTinHeader.setForeground(new Color(30, 41, 59));

        header.add(lblThongTinHeader, BorderLayout.WEST);
        return header;
    }

    /**
     * Tạo vùng JTable danh sách hạng mục báo giá và phân công KTV
     */
    private JScrollPane taoVungCuonBang() {
        String[] tieuDeCot = {
                "Phân Loại", "Hạng Mục / Linh Kiện", "SL", "Đơn Giá", "Thành Tiền", "Thuế VAT", "KTV Thực Hiện Nhiệm Vụ"
        };

        modelBang = new DefaultTableModel(tieuDeCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 6 && row >= 0 && row < danhSachDongBaoGia.size()) {
                    DongBaoGiaItem item = danhSachDongBaoGia.get(row);
                    if ("Dịch vụ".equals(item.getPhanLoai())) {
                        if (item.getHangMuc() != null && item.getHangMuc().contains("✓ Đã xong")) {
                            return false;
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 6) ? NhanVienItem.class : String.class;
            }
        };

        bangBaoGia = new JTable(modelBang) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 6 && row >= 0 && row < danhSachDongBaoGia.size()) {
                    DongBaoGiaItem item = danhSachDongBaoGia.get(row);
                    if ("Dịch vụ".equals(item.getPhanLoai())) {
                        ChuyenMon cm = (item.getChiTietBaoGia() != null && item.getChiTietBaoGia().getDichVu() != null)
                                ? item.getChiTietBaoGia().getDichVu().getChuyenMon()
                                : null;
                        JComboBox<NhanVienItem> cb = new JComboBox<>();
                        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        List<NhanVien> dsKTVPhuHop = layDanhSachKTVPhuHopVaSapXep(cm);
                        Map<String, Integer> mapSoViec = phieuPhanCongBUS.laySoLuongViecDangXuLyCuaTatCaKTV();
                        for (NhanVien nv : dsKTVPhuHop) {
                            int soViec = mapSoViec.getOrDefault(nv.getMaNhanVien(), 0);
                            cb.addItem(new NhanVienItem(nv, soViec));
                        }
                        return new DefaultCellEditor(cb);
                    }
                }
                return super.getCellEditor(row, column);
            }
        };
        bangBaoGia.setRowHeight(38);
        bangBaoGia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bangBaoGia.setForeground(MAU_CHU_CHINH);
        bangBaoGia.setGridColor(new Color(235, 238, 242));
        bangBaoGia.setShowVerticalLines(true);
        bangBaoGia.setShowHorizontalLines(true);
        bangBaoGia.setSelectionBackground(new Color(239, 246, 255));
        bangBaoGia.setSelectionForeground(MAU_CHU_CHINH);
        bangBaoGia.getTableHeader().setReorderingAllowed(false);
        bangBaoGia.getTableHeader().setPreferredSize(new Dimension(0, 36));
        bangBaoGia.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bangBaoGia.getTableHeader().setBackground(new Color(241, 245, 249));
        bangBaoGia.getTableHeader().setForeground(new Color(51, 65, 85));

        // Cài đặt độ rộng cột
        TableColumnModel colModel = bangBaoGia.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(90);  // Phân Loại
        colModel.getColumn(1).setPreferredWidth(280); // Hạng Mục / Linh Kiện
        colModel.getColumn(2).setPreferredWidth(55);  // SL
        colModel.getColumn(3).setPreferredWidth(120); // Đơn Giá
        colModel.getColumn(4).setPreferredWidth(135); // Thành Tiền
        colModel.getColumn(5).setPreferredWidth(85);  // Thuế VAT
        colModel.getColumn(6).setPreferredWidth(280); // KTV Thực Hiện Nhiệm Vụ

        // Định dạng căn lề cho các cột
        DefaultTableCellRenderer canGiua = new DefaultTableCellRenderer();
        canGiua.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer canPhai = new DefaultTableCellRenderer();
        canPhai.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer canPhaiDam = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        };
        canPhaiDam.setHorizontalAlignment(SwingConstants.RIGHT);

        colModel.getColumn(0).setCellRenderer(canGiua);
        colModel.getColumn(2).setCellRenderer(canGiua);
        colModel.getColumn(3).setCellRenderer(canPhai);
        colModel.getColumn(4).setCellRenderer(canPhaiDam);
        colModel.getColumn(5).setCellRenderer(canGiua);

        // Thiết lập ComboBox Editor & Renderer cho cột KTV
        caiDatCotKyThuatVien();

        // Nạp dữ liệu vào bảng
        napDuLieuVaoBang();

        JScrollPane cuon = new JScrollPane(bangBaoGia);
        cuon.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.setPreferredSize(new Dimension(0, 300));
        return cuon;
    }

    private void caiDatCotKyThuatVien() {
        TableColumnModel colModel = bangBaoGia.getColumnModel();

        // Cell Renderer hiển thị icon kỹ thuật viên + tên
        colModel.getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
                cell.setOpaque(true);
                cell.setBackground(isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

                boolean laDichVu = row < danhSachDongBaoGia.size() && "Dịch vụ".equals(danhSachDongBaoGia.get(row).getPhanLoai());
                JLabel lblIcon = new JLabel(IconUtil.layIcon(laDichVu ? "user.png" : "check.png", 14, 14, laDichVu ? MAU_CHU_CHINH : MAU_CHU_PHU));
                String text;
                if (laDichVu) {
                    text = (value != null) ? value.toString() : "Chưa chọn KTV";
                } else {
                    text = (value != null) ? value.toString() + " (Theo KTV dịch vụ)" : "-";
                }
                JLabel lblTen = new JLabel(text);
                lblTen.setFont(new Font("Segoe UI", laDichVu ? Font.BOLD : Font.ITALIC, 12));
                lblTen.setForeground(laDichVu ? MAU_CHU_CHINH : MAU_CHU_PHU);

                cell.add(lblIcon);
                cell.add(lblTen);
                return cell;
            }
        });

        // Lắng nghe khi người dùng chọn KTV khác trong bảng
        modelBang.addTableModelListener(e -> {
            if (e.getColumn() == 6 && e.getFirstRow() >= 0) {
                int row = e.getFirstRow();
                Object val = modelBang.getValueAt(row, 6);
                if (val instanceof NhanVienItem && row < danhSachDongBaoGia.size()) {
                    NhanVien nvChon = ((NhanVienItem) val).getNhanVien();
                    DongBaoGiaItem itemDV = danhSachDongBaoGia.get(row);
                    itemDV.setKtvPhanCong(nvChon);
                    // Tự động đồng bộ KTV này cho tất cả phụ tùng thuộc dịch vụ này
                    if ("Dịch vụ".equals(itemDV.getPhanLoai()) && itemDV.getChiTietBaoGia() != null) {
                        Map<String, Integer> mapSoViec = phieuPhanCongBUS.laySoLuongViecDangXuLyCuaTatCaKTV();
                        int soViecChon = (nvChon != null) ? mapSoViec.getOrDefault(nvChon.getMaNhanVien(), 0) : -1;
                        for (int i = row + 1; i < danhSachDongBaoGia.size(); i++) {
                            DongBaoGiaItem itemSau = danhSachDongBaoGia.get(i);
                            if ("Phụ tùng".equals(itemSau.getPhanLoai())
                                    && itemSau.getChiTietBaoGia() == itemDV.getChiTietBaoGia()) {
                                itemSau.setKtvPhanCong(nvChon);
                                modelBang.setValueAt(new NhanVienItem(nvChon, soViecChon), i, 6);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void napDuLieuVaoBang() {
        modelBang.setRowCount(0);
        Map<String, Integer> mapSoViec = phieuPhanCongBUS.laySoLuongViecDangXuLyCuaTatCaKTV();
        for (DongBaoGiaItem item : danhSachDongBaoGia) {
            int vatPercent = (int) Math.round(item.getThueVAT() * 100);
            int soViec = (item.getKtvPhanCong() != null)
                    ? mapSoViec.getOrDefault(item.getKtvPhanCong().getMaNhanVien(), 0)
                    : -1;
            modelBang.addRow(new Object[]{
                    item.getPhanLoai(),
                    item.getHangMuc(),
                    item.getSoLuong(),
                    dinhDangTien(item.getDonGia()),
                    dinhDangTien(item.tinhThanhTien()),
                    vatPercent + "%",
                    new NhanVienItem(item.getKtvPhanCong(), soViec)
            });
        }
    }

    /**
     * Khung tổng tiền dưới bảng:
     * - Tiền dịch vụ & phụ tùng (chưa thuế):  950.000 đ
     * - Thuế VAT (Dịch vụ 8% | Phụ tùng 10%):  + 88.000 đ
     * - TỔNG TIỀN DỰ KIẾN PHẢI TRẢ (ĐÃ GỒM VAT):  1.038.000 đ
     */
    private JPanel taoKhungTongHopTien() {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        wrap.setOpaque(false);

        JPanel pnlTong = new JPanel(new GridLayout(3, 1, 0, 6));
        pnlTong.setOpaque(false);
        pnlTong.setBorder(new EmptyBorder(8, 0, 0, 6));

        // Dòng 1: Tiền chưa thuế
        JPanel dong1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        dong1.setOpaque(false);
        JLabel lblTieuDeChuaThue = new JLabel("Tiền dịch vụ & phụ tùng (chưa thuế):");
        lblTieuDeChuaThue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTieuDeChuaThue.setForeground(MAU_CHU_PHU);
        lblTienChuaThue = new JLabel("0 đ");
        lblTienChuaThue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTienChuaThue.setForeground(MAU_CHU_CHINH);
        dong1.add(lblTieuDeChuaThue);
        dong1.add(lblTienChuaThue);

        // Dòng 2: Thuế VAT
        JPanel dong2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        dong2.setOpaque(false);
        JLabel lblTieuDeVAT = new JLabel("Thuế VAT (Dịch vụ 8% | Phụ tùng 10%):");
        lblTieuDeVAT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTieuDeVAT.setForeground(MAU_CHU_PHU);
        lblTienVAT = new JLabel("+ 0 đ");
        lblTienVAT.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTienVAT.setForeground(MAU_XANH_DUONG_VAT);
        dong2.add(lblTieuDeVAT);
        dong2.add(lblTienVAT);

        // Dòng 3: Tổng tiền dự kiến phải trả (đã gồm VAT)
        JPanel dong3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        dong3.setOpaque(false);
        JLabel lblTieuDeTong = new JLabel("TỔNG TIỀN DỰ KIẾN PHẢI TRẢ (ĐÃ GỒM VAT):");
        lblTieuDeTong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTieuDeTong.setForeground(MAU_XANH_DAM);
        lblTongTienPhaiTra = new JLabel("0 đ");
        lblTongTienPhaiTra.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTienPhaiTra.setForeground(MAU_CAM_CHU_DAO);
        dong3.add(lblTieuDeTong);
        dong3.add(lblTongTienPhaiTra);

        pnlTong.add(dong1);
        pnlTong.add(dong2);
        pnlTong.add(dong3);

        wrap.add(pnlTong);
        return wrap;
    }

    private void tinhToanTongTien() {
        double tongChuaThue = 0;
        double tongVAT = 0;

        for (DongBaoGiaItem item : danhSachDongBaoGia) {
            tongChuaThue += item.tinhThanhTien();
            tongVAT += item.tinhTienVAT();
        }

        double tongPhaiTra = tongChuaThue + tongVAT;

        lblTienChuaThue.setText(dinhDangTien(tongChuaThue));
        lblTienVAT.setText("+ " + dinhDangTien(tongVAT));
        lblTongTienPhaiTra.setText(dinhDangTien(tongPhaiTra));
    }

    /**
     * Dòng ghi chú cho thợ (nếu có)
     */
    private JPanel taoHangGhiChuChoTho() {
        JPanel hang = new JPanel(new BorderLayout(10, 0));
        hang.setOpaque(false);

        // Nhãn tiêu đề kèm icon
        JPanel pnlNhan = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlNhan.setOpaque(false);
        JLabel lblIcon = new JLabel(IconUtil.layIcon("edit.png", 14, 14, MAU_CHU_CHINH));
        JLabel lblGhiChu = new JLabel("Ghi chú cho thợ (nếu có):");
        lblGhiChu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGhiChu.setForeground(MAU_CHU_CHINH);
        pnlNhan.add(lblIcon);
        pnlNhan.add(lblGhiChu);

        // Ô nhập ghi chú
        String placeholder = "VD: Khách cần xe gấp trước 11h trưa, kiểm tra thêm bình ắc quy...";
        txtGhiChuChoTho = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(MAU_VIEN);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtGhiChuChoTho.setOpaque(false);
        txtGhiChuChoTho.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtGhiChuChoTho.setForeground(MAU_CHU_PHU);
        txtGhiChuChoTho.setBorder(new EmptyBorder(8, 12, 8, 12));
        txtGhiChuChoTho.setPreferredSize(new Dimension(0, 36));

        txtGhiChuChoTho.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtGhiChuChoTho.getText().equals(placeholder)) {
                    txtGhiChuChoTho.setText("");
                    txtGhiChuChoTho.setForeground(MAU_CHU_CHINH);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtGhiChuChoTho.getText().trim().isEmpty()) {
                    txtGhiChuChoTho.setText(placeholder);
                    txtGhiChuChoTho.setForeground(MAU_CHU_PHU);
                }
            }
        });

        hang.add(pnlNhan, BorderLayout.WEST);
        hang.add(txtGhiChuChoTho, BorderLayout.CENTER);
        return hang;
    }

    /**
     * Thanh nút dưới cùng:
     * - Trái: "✕ Khách Từ Chối"
     * - Phải: "✓ Khách Chốt & Phân Công Thợ" + "Đóng"
     */
    private JPanel taoFooterThaoTac() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN),
                new EmptyBorder(12, 18, 12, 18)
        ));

        // Nút bên trái: Khách Từ Chối
        btnKhachTuChoi = new JButton("Khách Từ Chối", IconUtil.layIcon("close.png", 12, 12, new Color(71, 85, 105))) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(254, 242, 242) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(getModel().isRollover() ? new Color(254, 205, 211) : new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnKhachTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKhachTuChoi.setForeground(new Color(71, 85, 105));
        btnKhachTuChoi.setFocusPainted(false);
        btnKhachTuChoi.setBorderPainted(false);
        btnKhachTuChoi.setContentAreaFilled(false);
        btnKhachTuChoi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKhachTuChoi.setPreferredSize(new Dimension(150, 36));
        btnKhachTuChoi.addActionListener(e -> xuLyKhachTuChoi());

        footer.add(btnKhachTuChoi, BorderLayout.WEST);

        // Các nút bên phải: Khách Chốt & Phân Công Thợ + Đóng
        JPanel pnlPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlPhai.setOpaque(false);

        // Nút màu Cyan / Xanh ngọc nổi bật: Khách Chốt & Phân Công Thợ
        btnKhachChot = new JButton("Khách Chốt & Phân Công Thợ", IconUtil.layIcon("check.png", 14, 14, MAU_XANH_DAM)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MAU_CYAN_NUT_HOVER : MAU_CYAN_NUT_CHOT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnKhachChot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKhachChot.setForeground(MAU_XANH_DAM);
        btnKhachChot.setFocusPainted(false);
        btnKhachChot.setBorderPainted(false);
        btnKhachChot.setContentAreaFilled(false);
        btnKhachChot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKhachChot.setPreferredSize(new Dimension(240, 36));
        btnKhachChot.addActionListener(e -> xuLyKhachChotVaPhanCong());

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

        pnlPhai.add(btnKhachChot);
        pnlPhai.add(btnDong);

        footer.add(pnlPhai, BorderLayout.EAST);
        return footer;
    }

    /**
     * Xử lý khi khách chốt báo giá và phân công thợ
     */
    private void xuLyKhachChotVaPhanCong() {
        if (danhSachDongBaoGia.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có hạng mục nào trong bảng báo giá để phân công!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra xem tất cả các dòng dịch vụ đã có KTV được phân công chưa
        for (int i = 0; i < danhSachDongBaoGia.size(); i++) {
            DongBaoGiaItem item = danhSachDongBaoGia.get(i);
            if ("Dịch vụ".equals(item.getPhanLoai()) && item.getKtvPhanCong() == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chỉ định Kỹ thuật viên cho dịch vụ: " + item.getHangMuc(),
                        "Chưa phân công đủ KTV", JOptionPane.WARNING_MESSAGE);
                bangBaoGia.setRowSelectionInterval(i, i);
                return;
            }
        }

        String bienSo = (phieuTiepNhan != null && phieuTiepNhan.getPhuongTien() != null)
                ? phieuTiepNhan.getPhuongTien().getBienSoXe()
                : "phương tiện";

        // Kiểm tra xem tất cả các dịch vụ đã được phân công KTV chưa
        for (DongBaoGiaItem item : danhSachDongBaoGia) {
            if ("Dịch vụ".equals(item.getPhanLoai()) && item.getKtvPhanCong() == null) {
                JOptionPane.showMessageDialog(this,
                        "Hạng mục \"" + item.getHangMuc() + "\" chưa được phân công Kỹ thuật viên!\n" +
                        "Vui lòng chọn KTV thực hiện cho tất cả các dịch vụ trước khi chốt.",
                        "Chưa phân công KTV", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Khách hàng đã đồng ý báo giá và phân công kỹ thuật viên cho " + bienSo + "?\n" +
                        "Xe sẽ được chuyển ngay sang trạng thái [ĐANG SỬA CHỮA].",
                "Xác nhận duyệt & phân công",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (xacNhan != JOptionPane.YES_OPTION) {
            return;
        }

        String placeholder = "VD: Khách cần xe gấp trước 11h trưa, kiểm tra thêm bình ắc quy...";
        String ghiChu = txtGhiChuChoTho.getText().trim();
        if (ghiChu.equals(placeholder)) {
            ghiChu = "";
        }

        boolean daThucHienThanhCong = false;

        // Lưu vào CSDL
        try {
            if (phieuBaoGia != null) {
                // Duyệt báo giá
                for (ChiTietBaoGia ctbg : phieuBaoGia.getDanhSachChiTiet()) {
                    ctbg.setDuocChapNhan(true);
                }
                phieuBaoGiaBUS.duyetBaoGia(phieuBaoGia);

                // Phân công thợ cho từng hạng mục dịch vụ
                for (DongBaoGiaItem item : danhSachDongBaoGia) {
                    if ("Dịch vụ".equals(item.getPhanLoai()) && item.getChiTietBaoGia() != null) {
                        try {
                            if (item.getChiTietBaoGia().getDichVu() != null) {
                                PhieuPhanCong pcDaCo = phieuPhanCongDAO.timTheoChiTietBaoGia(
                                        phieuBaoGia.getMaBaoGia(),
                                        item.getChiTietBaoGia().getDichVu().getMaDichVu()
                                );
                                if (pcDaCo != null) {
                                    continue; // Đã phân công (ví dụ phân công kiểm tra xe đã xong), không tạo lại
                                }
                            }
                            PhieuPhanCong ppc = new PhieuPhanCong();
                            ppc.setNhanVien(item.getKtvPhanCong());
                            ppc.setChiTietBaoGia(item.getChiTietBaoGia());
                            ppc.setGhiChu(ghiChu);
                            ppc.setThoiGianPhanCong(LocalDateTime.now());
                            ppc.setTrangThaiSua(TrangThaiSua.CHO_THUC_HIEN);
                            phieuPhanCongBUS.phanCongHangMuc(ppc);
                        } catch (Exception exPhanCong) {
                            // Nếu đã tồn tại phân công hoặc lỗi nhỏ thì bỏ qua để không chặn luồng
                        }
                    }
                }
                daThucHienThanhCong = true;
            } else if (phieuTiepNhan != null) {
                // Nếu chưa có bảng báo giá chính thức trong DB, cập nhật trực tiếp trạng thái xe sang Đang sửa chữa
                phieuTiepNhanBUS.chuyenTrangThai(phieuTiepNhan.getMaTiepNhan(), TrangThaiPhieuTiepNhan.DANG_SUA_CHUA);
                daThucHienThanhCong = true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi lưu duyệt báo giá & phân công: " + ex.getMessage(),
                    "Lỗi thực hiện", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "✓ Khách hàng đã chốt báo giá thành công!\n" +
                        "Đã phân công nhiệm vụ cho Kỹ thuật viên và chuyển xe sang trạng thái [ĐANG SỬA CHỮA].",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        if (onComplete != null) {
            onComplete.run();
        }
        dispose();
    }

    /**
     * Xử lý khi khách từ chối báo giá
     */
    private void xuLyKhachTuChoi() {
        String lyDo = JOptionPane.showInputDialog(this,
                "Nhập lý do khách từ chối báo giá (nếu có):",
                "Khách từ chối báo giá", JOptionPane.QUESTION_MESSAGE);

        if (lyDo == null) {
            return; // Khách bấm Cancel
        }

        try {
            if (phieuBaoGia != null) {
                phieuBaoGiaBUS.tuChoiBaoGia(phieuBaoGia.getMaBaoGia(),
                        lyDo.trim().isEmpty() ? "Khách từ chối báo giá" : lyDo.trim());
            } else if (phieuTiepNhan != null) {
                new PhieuTiepNhanBUS().huyPhieuTiepNhan(phieuTiepNhan.getMaTiepNhan());
            }
            JOptionPane.showMessageDialog(this,
                    "Đã ghi nhận khách hàng từ chối báo giá.\nPhiếu tiếp nhận đã được chuyển sang trạng thái [ĐÃ HỦY] và loại khỏi hàng đợi.",
                    "Đã ghi nhận", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi cập nhật từ chối báo giá: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (onComplete != null) {
            onComplete.run();
        }
        dispose();
    }

    private String dinhDangTien(double soTien) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien) + " đ";
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
     * Hàm main hỗ trợ kiểm tra và xem trước giao diện độc lập
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LeTanManHinhHangDoiPhieuPhanCong dialog = new LeTanManHinhHangDoiPhieuPhanCong();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }
}
