package bus;

import dao.HoaDonDAO;
import dao.PhieuTiepNhanDAO;
import dao.PhieuPhanCongDAO;
import dao.SanPhamDAO;
import entity.HoaDon;
import entity.PhieuTiepNhan;
import entity.SanPham;
import enums.LoaiHoaDon;
import enums.LoaiHangMuc;
import enums.TrangThaiSua;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiThanhToan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaoCaoThongKeBUS {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final PhieuPhanCongDAO phieuPhanCongDAO = new PhieuPhanCongDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public Map<String, Double> layKpiDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> ketQua = new LinkedHashMap<>();
        ketQua.put("tong", 0d);
        ketQua.put("banHang", 0d);
        ketQua.put("dichVu", 0d);
        for (HoaDon hoaDon : hoaDonDAO.layTatCa()) {
            if (!laHoaDonDaThanhToanTrongKhoang(hoaDon, tuNgay, denNgay)) {
                continue;
            }
            double tong = hoaDon.tinhTongThanhToan();
            String khoa = hoaDon.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG ? "banHang" : "dichVu";
            ketQua.put(khoa, ketQua.get(khoa) + tong);
            ketQua.put("tong", ketQua.get("tong") + tong);
        }
        return ketQua;
    }

    public int demHoaDonDaThanhToan(LocalDate tuNgay, LocalDate denNgay) {
        int ketQua = 0;
        for (HoaDon hoaDon : hoaDonDAO.layTatCa()) {
            if (laHoaDonDaThanhToanTrongKhoang(hoaDon, tuNgay, denNgay)) {
                ketQua++;
            }
        }
        return ketQua;
    }

    public int demPhieuSua(LocalDate tuNgay, LocalDate denNgay) {
        int ketQua = 0;
        for (PhieuTiepNhan phieu : phieuTiepNhanDAO.layTatCa()) {
            if (namTrongKhoang(phieu.getThoiGianTiepNhan().toLocalDate(), tuNgay, denNgay)) {
                ketQua++;
            }
        }
        return ketQua;
    }

    public int demTonKhoThap() {
        int ketQua = 0;
        for (SanPham sanPham : sanPhamDAO.layTatCa()) {
            if (sanPham.isTrangThai() && sanPham.getSoLuongTon() <= sanPham.getSoLuongTonToiThieu()) {
                ketQua++;
            }
        }
        return ketQua;
    }

    public Map<String, List<Double>> layDoanhThu7Ngay(LocalDate denNgay) {
        Map<String, List<Double>> ketQua = new LinkedHashMap<>();
        List<Double> banHang = new ArrayList<>();
        List<Double> dichVu = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate ngay = denNgay.minusDays(i);
            Map<String, Double> kpiNgay = layKpiDoanhThu(ngay, ngay);
            banHang.add(kpiNgay.get("banHang"));
            dichVu.add(kpiNgay.get("dichVu"));
        }
        ketQua.put("Bán hàng", banHang);
        ketQua.put("Dịch vụ", dichVu);
        return ketQua;
    }

    public Map<String, Double> layCoCauDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> kpi = layKpiDoanhThu(tuNgay, denNgay);
        Map<String, Double> ketQua = new LinkedHashMap<>();
        ketQua.put("Bán hàng", kpi.get("banHang"));
        ketQua.put("Dịch vụ", kpi.get("dichVu"));
        return ketQua;
    }

    public Map<String, Double> layTrangThaiSua(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> ketQua = new LinkedHashMap<>();
        for (TrangThaiPhieuTiepNhan trangThai : TrangThaiPhieuTiepNhan.values()) {
            ketQua.put(trangThai.getTenHienThi(), 0d);
        }
        for (PhieuTiepNhan phieu : phieuTiepNhanDAO.layTatCa()) {
            if (namTrongKhoang(phieu.getThoiGianTiepNhan().toLocalDate(), tuNgay, denNgay)) {
                String ten = phieu.getTrangThai().getTenHienThi();
                ketQua.put(ten, ketQua.get(ten) + 1);
            }
        }
        return ketQua;
    }

    public Map<String, List<Double>> layTonKhoCanhBao(int gioiHan) {
        List<SanPham> danhSach = new ArrayList<>();
        for (SanPham sanPham : sanPhamDAO.layTatCa()) {
            if (sanPham.isTrangThai() && sanPham.getSoLuongTon() <= sanPham.getSoLuongTonToiThieu()) {
                danhSach.add(sanPham);
            }
        }
        danhSach.sort(Comparator.comparingInt(SanPham::getSoLuongTon));
        Map<String, List<Double>> ketQua = new LinkedHashMap<>();
        for (SanPham sanPham : danhSach.subList(0, Math.min(gioiHan, danhSach.size()))) {
            List<Double> ton = new ArrayList<>();
            ton.add((double) sanPham.getSoLuongTon());
            ton.add((double) sanPham.getSoLuongTonToiThieu());
            ketQua.put(sanPham.getTenSanPham(), ton);
        }
        return ketQua;
    }

    public List<Object[]> layBaoCaoBanHang(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Object[]> tongHop = new LinkedHashMap<>();
        for (HoaDon hoaDon : hoaDonDAO.layTatCa()) {
            if (!laHoaDonDaThanhToanTrongKhoang(hoaDon, tuNgay, denNgay)
                    || hoaDon.getLoaiHoaDon() != LoaiHoaDon.BAN_HANG) {
                continue;
            }
            hoaDon.getDanhSachChiTiet().stream()
                    .filter(ct -> ct.getLoaiHangMuc() == LoaiHangMuc.SAN_PHAM && ct.getSanPham() != null)
                    .forEach(ct -> {
                        String ma = ct.getSanPham().getMaSanPham();
                        Object[] dong = tongHop.computeIfAbsent(ma,
                                key -> new Object[]{ma, ct.getSanPham().getTenSanPham(), 0, 0d});
                        dong[2] = (Integer) dong[2] + ct.getSoLuong();
                        dong[3] = (Double) dong[3] + ct.tinhThanhTienSauThue();
                    });
        }
        return new ArrayList<>(tongHop.values());
    }

    public List<Object[]> layBaoCaoDichVu(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Object[]> tongHop = new LinkedHashMap<>();
        for (HoaDon hoaDon : hoaDonDAO.layTatCa()) {
            if (!laHoaDonDaThanhToanTrongKhoang(hoaDon, tuNgay, denNgay)
                    || hoaDon.getLoaiHoaDon() != LoaiHoaDon.DICH_VU) {
                continue;
            }
            hoaDon.getDanhSachChiTiet().stream()
                    .filter(ct -> ct.getLoaiHangMuc() == LoaiHangMuc.DICH_VU && ct.getDichVu() != null)
                    .forEach(ct -> {
                        String ma = ct.getDichVu().getMaDichVu();
                        Object[] dong = tongHop.computeIfAbsent(ma,
                                key -> new Object[]{ma, ct.getDichVu().getTenDichVu(), 0, 0d});
                        dong[2] = (Integer) dong[2] + ct.getSoLuong();
                        dong[3] = (Double) dong[3] + ct.tinhThanhTienSauThue();
                    });
        }
        return new ArrayList<>(tongHop.values());
    }

    public List<Object[]> layBaoCaoKho() {
        List<Object[]> ketQua = new ArrayList<>();
        for (SanPham sanPham : sanPhamDAO.layTatCa()) {
            if (sanPham.isTrangThai()) {
                ketQua.add(new Object[]{
                        sanPham.getMaSanPham(),
                        sanPham.getTenSanPham(),
                        sanPham.getSoLuongTon(),
                        sanPham.getSoLuongTonToiThieu(),
                        sanPham.getSoLuongTon() * sanPham.getGiaNhap()
                });
            }
        }
        return ketQua;
    }

    public List<Object[]> layBaoCaoNhanVien(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Object[]> tongHop = new LinkedHashMap<>();
        for (entity.PhieuPhanCong phieu : phieuPhanCongDAO.layTatCa()) {
            if (phieu.getThoiGianPhanCong() == null
                    || !namTrongKhoang(phieu.getThoiGianPhanCong().toLocalDate(), tuNgay, denNgay)
                    || phieu.getNhanVien() == null) {
                continue;
            }
            String ma = phieu.getNhanVien().getMaNhanVien();
            String ten = phieu.getNhanVien().getHoTen();
            Object[] dong = tongHop.computeIfAbsent(ma,
                    key -> new Object[]{ma, ten, 0, 0, 0d});
            dong[2] = (Integer) dong[2] + 1;
            if (phieu.getTrangThaiSua() == TrangThaiSua.HOAN_THANH) {
                dong[3] = (Integer) dong[3] + 1;
            }
        }
        for (Object[] dong : tongHop.values()) {
            int tong = (Integer) dong[2];
            int hoanThanh = (Integer) dong[3];
            dong[4] = tong == 0 ? 0d : (double) hoanThanh / tong * 100;
        }
        return new ArrayList<>(tongHop.values());
    }

    public List<Object[]> layBaoCaoThanhToan(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Object[]> tongHop = new LinkedHashMap<>();
        for (HoaDon hoaDon : hoaDonDAO.layTatCa()) {
            if (!laHoaDonDaThanhToanTrongKhoang(hoaDon, tuNgay, denNgay)
                    || hoaDon.getPhuongThucThanhToan() == null) {
                continue;
            }
            String ten = hoaDon.getPhuongThucThanhToan().getTenHienThi();
            Object[] dong = tongHop.computeIfAbsent(ten, key -> new Object[]{ten, 0, 0d});
            dong[1] = (Integer) dong[1] + 1;
            dong[2] = (Double) dong[2] + hoaDon.tinhTongThanhToan();
        }
        return new ArrayList<>(tongHop.values());
    }

    private boolean laHoaDonDaThanhToanTrongKhoang(HoaDon hoaDon, LocalDate tuNgay, LocalDate denNgay) {
        LocalDate ngayThanhToan = hoaDon.getThoiGianThanhToan() != null
                ? hoaDon.getThoiGianThanhToan().toLocalDate()
                : hoaDon.getThoiGianLap().toLocalDate();
        return hoaDon.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN
                && namTrongKhoang(ngayThanhToan, tuNgay, denNgay);
    }

    private boolean namTrongKhoang(LocalDate ngay, LocalDate tuNgay, LocalDate denNgay) {
        return !ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay);
    }
}
