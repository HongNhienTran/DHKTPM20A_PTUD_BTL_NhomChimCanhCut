package ui;

import bus.BaoCaoThongKeBUS;
import util.ChonNgayLichUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuanLyManHinhBaoCaoThongKe extends JPanel {
    private static final Color MAU_NEN = new Color(248, 250, 252);
    private static final Color MAU_THE = Color.WHITE;
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_XANH = new Color(15, 41, 66);
    private static final Color MAU_CAM = new Color(234, 106, 31);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JComboBox<String> cboKhoangThoiGian =
            new JComboBox<>(new String[]{"Hôm nay", "7 ngày qua", "Tháng này", "Năm nay", "Tùy chọn"});
    private final BaoCaoThongKeBUS baoCaoThongKeBUS = new BaoCaoThongKeBUS();
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private final JButton btnTuNgay = new JButton();
    private final JButton btnDenNgay = new JButton();
    private final JLabel lblTongDoanhThu = new JLabel("—");
    private final JLabel lblDoanhThuBanHang = new JLabel("—");
    private final JLabel lblDoanhThuDichVu = new JLabel("—");
    private final JLabel lblSoHoaDon = new JLabel("—");
    private final JLabel lblSoPhieuSua = new JLabel("—");
    private final JLabel lblTonKhoThap = new JLabel("—");
    private final BieuDoPanel bieuDoDoanhThu = new BieuDoPanel("Doanh thu 7 ngày gần nhất");
    private final BieuDoPanel bieuDoCoCau = new BieuDoPanel("Cơ cấu doanh thu");
    private final BieuDoPanel bieuDoSuaChua = new BieuDoPanel("Trạng thái sửa chữa");
    private final BieuDoPanel bieuDoTonKho = new BieuDoPanel("Tồn kho cảnh báo");

    public QuanLyManHinhBaoCaoThongKe() {
        khoiTaoGiaoDien();
        datKhoangThoiGianMacDinh();
    }

    private void khoiTaoGiaoDien() {
        setLayout(new BorderLayout(0, 14));
        setBackground(MAU_NEN);
        setBorder(new EmptyBorder(18, 24, 20, 24));

        JPanel dauTrang = new JPanel();
        dauTrang.setOpaque(false);
        dauTrang.setLayout(new BoxLayout(dauTrang, BoxLayout.Y_AXIS));
        dauTrang.add(taoTieuDe());
        dauTrang.add(Box.createVerticalStrut(12));
        dauTrang.add(taoBoLoc());
        dauTrang.add(Box.createVerticalStrut(12));
        dauTrang.add(taoNhomBaoCaoChiTiet());
        dauTrang.add(Box.createVerticalStrut(12));
        dauTrang.add(taoKpi());

        JPanel luoiBieuDo = new JPanel(new GridLayout(2, 2, 14, 14));
        luoiBieuDo.setOpaque(false);
        luoiBieuDo.add(bieuDoDoanhThu);
        luoiBieuDo.add(bieuDoCoCau);
        luoiBieuDo.add(bieuDoSuaChua);
        luoiBieuDo.add(bieuDoTonKho);

        add(dauTrang, BorderLayout.NORTH);
        add(new JScrollPane(luoiBieuDo), BorderLayout.CENTER);
        cboKhoangThoiGian.addActionListener(e -> capNhatTheoKhoangThoiGian());
    }

    private JPanel taoTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel tieuDe = new JLabel("Báo cáo & Thống kê");
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tieuDe.setForeground(MAU_XANH);
        JButton lamMoi = new JButton("Làm mới");
        lamMoi.addActionListener(e -> lamMoiDuLieu());
        panel.add(tieuDe, BorderLayout.WEST);
        panel.add(lamMoi, BorderLayout.EAST);
        return panel;
    }

    private JPanel taoNhomBaoCaoChiTiet() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.add(taoNutBaoCao("Bán hàng", QuanLyManHinhBaoCaoBanHang.class));
        panel.add(taoNutBaoCao("Dịch vụ", QuanLyManHinhBaoCaoDichVu.class));
        panel.add(taoNutBaoCao("Kho", QuanLyManHinhBaoCaoKho.class));
        panel.add(taoNutBaoCao("Nhân viên", QuanLyManHinhBaoCaoNhanVien.class));
        panel.add(taoNutBaoCao("Thanh toán", QuanLyManHinhBaoCaoThanhToan.class));
        return panel;
    }

    private JButton taoNutBaoCao(String nhan, Class<? extends JPanel> lopManHinh) {
        JButton nut = new JButton(nhan);
        nut.addActionListener(e -> moBaoCaoChiTiet(lopManHinh));
        return nut;
    }

    private void moBaoCaoChiTiet(Class<? extends JPanel> lopManHinh) {
        try {
            JPanel manHinh = lopManHinh.getDeclaredConstructor().newInstance();
            JDialog hopThoai = new JDialog((Window) getTopLevelAncestor(), "Báo cáo chi tiết", JDialog.ModalityType.APPLICATION_MODAL);
            hopThoai.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            hopThoai.setContentPane(manHinh);
            hopThoai.setSize(960, 600);
            hopThoai.setLocationRelativeTo(this);
            if (manHinh instanceof QuanLyManHinhBaoCaoChiTiet) {
                ((QuanLyManHinhBaoCaoChiTiet) manHinh).lamMoiDuLieu();
            }
            hopThoai.setVisible(true);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Không thể mở màn hình báo cáo chi tiết", ex);
        }
    }

    private JPanel taoBoLoc() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBackground(MAU_THE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN),
                new EmptyBorder(3, 10, 3, 10)
        ));
        panel.add(new JLabel("Khoảng thời gian:"));
        panel.add(cboKhoangThoiGian);
        panel.add(new JLabel("Từ"));
        panel.add(btnTuNgay);
        panel.add(new JLabel("đến"));
        panel.add(btnDenNgay);
        btnTuNgay.addActionListener(e -> chonNgay(true));
        btnDenNgay.addActionListener(e -> chonNgay(false));
        return panel;
    }

    private JPanel taoKpi() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 12, 12));
        panel.setOpaque(false);
        panel.add(taoTheKpi("Tổng doanh thu", lblTongDoanhThu, "Bán hàng + dịch vụ"));
        panel.add(taoTheKpi("Doanh thu bán hàng", lblDoanhThuBanHang, "Hóa đơn bán phụ tùng"));
        panel.add(taoTheKpi("Doanh thu dịch vụ", lblDoanhThuDichVu, "Hóa đơn sửa chữa"));
        panel.add(taoTheKpi("Hóa đơn đã thanh toán", lblSoHoaDon, "Trong khoảng chọn"));
        panel.add(taoTheKpi("Phiếu sửa chữa", lblSoPhieuSua, "Theo trạng thái xử lý"));
        panel.add(taoTheKpi("Tồn kho thấp", lblTonKhoThap, "Không dùng dữ liệu giả"));
        return panel;
    }

    private JPanel taoTheKpi(String tieuDe, JLabel giaTri, String moTa) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(MAU_THE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAU_VIEN),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel nhan = new JLabel(tieuDe);
        nhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nhan.setForeground(MAU_CHU_PHU);
        giaTri.setFont(new Font("Segoe UI", Font.BOLD, 22));
        giaTri.setForeground(MAU_XANH);
        JLabel ghiChu = new JLabel(moTa);
        ghiChu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ghiChu.setForeground(MAU_CHU_PHU);
        panel.add(nhan, BorderLayout.NORTH);
        panel.add(giaTri, BorderLayout.CENTER);
        panel.add(ghiChu, BorderLayout.SOUTH);
        return panel;
    }

    private void datKhoangThoiGianMacDinh() {
        cboKhoangThoiGian.setSelectedItem("7 ngày qua");
        capNhatTheoKhoangThoiGian();
    }

    private void capNhatTheoKhoangThoiGian() {
        LocalDate den = LocalDate.now();
        LocalDate tu = den.minusDays(6);
        String luaChon = String.valueOf(cboKhoangThoiGian.getSelectedItem());
        if ("Hôm nay".equals(luaChon)) {
            tu = den;
        } else if ("Tháng này".equals(luaChon)) {
            tu = den.withDayOfMonth(1);
        } else if ("Năm nay".equals(luaChon)) {
            tu = den.withDayOfYear(1);
        }
        if (!"Tùy chọn".equals(luaChon)) {
            tuNgay = tu;
            denNgay = den;
            capNhatNutNgay();
        }
    }

    private void chonNgay(boolean laTuNgay) {
        LocalDate hienTai = laTuNgay ? tuNgay : denNgay;
        ChonNgayLichUtil.KetQuaChonNgay ketQua = ChonNgayLichUtil.hienThiLichChonNgay(
                this, laTuNgay ? "Chọn ngày bắt đầu" : "Chọn ngày kết thúc", hienTai, false);
        if (!ketQua.daXacNhan() || ketQua.ngay() == null) {
            return;
        }
        cboKhoangThoiGian.setSelectedItem("Tùy chọn");
        if (laTuNgay) {
            tuNgay = ketQua.ngay();
            if (denNgay.isBefore(tuNgay)) {
                denNgay = tuNgay;
            }
        } else {
            denNgay = ketQua.ngay();
            if (tuNgay.isAfter(denNgay)) {
                tuNgay = denNgay;
            }
        }
        capNhatNutNgay();
        lamMoiDuLieu();
    }

    private void capNhatNutNgay() {
        btnTuNgay.setText(tuNgay.format(DINH_DANG_NGAY));
        btnDenNgay.setText(denNgay.format(DINH_DANG_NGAY));
    }

    public void lamMoiDuLieu() {
        capNhatTheoKhoangThoiGian();
        LocalDate tuNgay = getTuNgay();
        LocalDate denNgay = getDenNgay();
        Map<String, Double> kpi = baoCaoThongKeBUS.layKpiDoanhThu(tuNgay, denNgay);
        capNhatKpi(
                kpi.get("tong"),
                kpi.get("banHang"),
                kpi.get("dichVu"),
                baoCaoThongKeBUS.demHoaDonDaThanhToan(tuNgay, denNgay),
                baoCaoThongKeBUS.demPhieuSua(tuNgay, denNgay),
                baoCaoThongKeBUS.demTonKhoThap()
        );
        capNhatBieuDoDoanhThu(baoCaoThongKeBUS.layDoanhThu7Ngay(denNgay));
        capNhatBieuDoCoCau(baoCaoThongKeBUS.layCoCauDoanhThu(tuNgay, denNgay));
        capNhatBieuDoSuaChua(baoCaoThongKeBUS.layTrangThaiSua(tuNgay, denNgay));
        capNhatBieuDoTonKho(baoCaoThongKeBUS.layTonKhoCanhBao(5));
    }

    public LocalDate getTuNgay() {
        return tuNgay;
    }

    public LocalDate getDenNgay() {
        return denNgay;
    }

    public void capNhatKpi(double tongDoanhThu, double doanhThuBanHang,
                           double doanhThuDichVu, int soHoaDon,
                           int soPhieuSua, int tonKhoThap) {
        lblTongDoanhThu.setText(dinhDangTien(tongDoanhThu));
        lblDoanhThuBanHang.setText(dinhDangTien(doanhThuBanHang));
        lblDoanhThuDichVu.setText(dinhDangTien(doanhThuDichVu));
        lblSoHoaDon.setText(String.valueOf(soHoaDon));
        lblSoPhieuSua.setText(String.valueOf(soPhieuSua));
        lblTonKhoThap.setText(String.valueOf(tonKhoThap));
    }

    public void capNhatBieuDoDoanhThu(Map<String, List<Double>> duLieu) {
        bieuDoDoanhThu.capNhatDuLieu(duLieu, false);
    }

    public void capNhatBieuDoCoCau(Map<String, Double> duLieu) {
        bieuDoCoCau.capNhatDuLieu(duLieu, true);
    }

    public void capNhatBieuDoSuaChua(Map<String, Double> duLieu) {
        bieuDoSuaChua.capNhatDuLieu(duLieu, true);
    }

    public void capNhatBieuDoTonKho(Map<String, List<Double>> duLieu) {
        bieuDoTonKho.capNhatDuLieu(duLieu, false);
    }

    private String dinhDangTien(double giaTri) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(giaTri);
    }

    private static class BieuDoPanel extends JPanel {
        private final String tieuDe;
        private Map<String, List<Double>> duLieuChuoi = new LinkedHashMap<>();
        private boolean bieuDoTron;

        private BieuDoPanel(String tieuDe) {
            this.tieuDe = tieuDe;
            setBackground(MAU_THE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MAU_VIEN),
                    new EmptyBorder(12, 14, 14, 14)
            ));
            setPreferredSize(new Dimension(380, 240));
        }

        private void capNhatDuLieu(Map<String, ? extends Object> duLieu, boolean bieuDoTron) {
            this.bieuDoTron = bieuDoTron;
            this.duLieuChuoi = new LinkedHashMap<>();
            for (Map.Entry<String, ? extends Object> entry : duLieu.entrySet()) {
                Object giaTri = entry.getValue();
                if (giaTri instanceof List) {
                    List<Double> danhSach = new ArrayList<>();
                    for (Object item : (List<?>) giaTri) {
                        danhSach.add(((Number) item).doubleValue());
                    }
                    this.duLieuChuoi.put(entry.getKey(), danhSach);
                } else if (giaTri instanceof Number) {
                    this.duLieuChuoi.put(entry.getKey(),
                            Collections.singletonList(((Number) giaTri).doubleValue()));
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.setColor(MAU_XANH);
            g.drawString(tieuDe, 14, 23);
            if (duLieuChuoi.isEmpty()) {
                g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g.setColor(MAU_CHU_PHU);
                g.drawString("Chưa có dữ liệu. BUS sẽ cập nhật sau.", 14, 58);
                g.dispose();
                return;
            }
            if (bieuDoTron) {
                veBieuDoTron(g);
            } else {
                veBieuDoCot(g);
            }
            g.dispose();
        }

        private void veBieuDoCot(Graphics2D g) {
            int left = 42;
            int top = 48;
            int bottom = getHeight() - 34;
            int right = getWidth() - 18;
            double max = 1;
            for (List<Double> values : duLieuChuoi.values()) {
                for (Double value : values) {
                    max = Math.max(max, value);
                }
            }
            g.setColor(new Color(203, 213, 225));
            g.drawLine(left, bottom, right, bottom);
            int soCot = duLieuChuoi.values().iterator().next().size();
            int rongCot = Math.max(12, (right - left) / Math.max(1, soCot * duLieuChuoi.size() + 1));
            int index = 0;
            for (List<Double> values : duLieuChuoi.values()) {
                g.setColor(index == 0 ? MAU_CAM : new Color(37, 99, 235));
                for (int i = 0; i < values.size(); i++) {
                    int x = left + (i * duLieuChuoi.size() + index + 1) * rongCot;
                    int h = (int) ((bottom - top) * values.get(i) / max);
                    g.fillRect(x, bottom - h, Math.max(8, rongCot - 3), h);
                }
                index++;
            }
        }

        private void veBieuDoTron(Graphics2D g) {
            int tong = 0;
            for (List<Double> values : duLieuChuoi.values()) {
                tong += values.get(0);
            }
            int start = 0;
            int size = Math.min(getHeight() - 68, 150);
            int x = 20;
            int y = 42;
            int index = 0;
            Color[] mau = {MAU_CAM, new Color(37, 99, 235), new Color(16, 185, 129), new Color(168, 85, 247)};
            for (List<Double> values : duLieuChuoi.values()) {
                int goc = tong == 0 ? 0 : (int) Math.round(values.get(0) * 360 / tong);
                g.setColor(mau[index++ % mau.length]);
                g.fillArc(x, y, size, size, start, goc);
                start += goc;
            }
        }
    }
}
