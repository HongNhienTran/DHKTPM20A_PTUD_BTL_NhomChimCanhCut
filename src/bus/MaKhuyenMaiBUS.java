package bus;

import dao.MaKhuyenMaiDAO;
import entity.MaKhuyenMai;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaKhuyenMaiBUS {
    private final MaKhuyenMaiDAO maKhuyenMaiDAO = new MaKhuyenMaiDAO();

    public String taoMaKhuyenMai(String tenMaKhuyenMai) {
        if (tenMaKhuyenMai == null || tenMaKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên mã khuyến mãi không được để trống");
        }
        return tenMaKhuyenMai.trim().toUpperCase();
    }

    public List<MaKhuyenMai> layTatCa() {
        return maKhuyenMaiDAO.layTatCa();
    }

    public MaKhuyenMai timTheoMa(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            return null;
        }
        return maKhuyenMaiDAO.timTheoMa(maKhuyenMai.trim());
    }

    public List<MaKhuyenMai> layDanhSachKhuyenMaiKhaDung(double tongTienHang) {
        LocalDateTime now = LocalDateTime.now();
        List<MaKhuyenMai> dsKhaDung = new ArrayList<>();
        for (MaKhuyenMai km : maKhuyenMaiDAO.layTatCa()) {
            if (kiemTraDieuKienApDung(km, tongTienHang, now)) {
                dsKhaDung.add(km);
            }
        }
        return dsKhaDung;
    }

    public boolean kiemTraDieuKienApDung(MaKhuyenMai km, double tongTienHang, LocalDateTime thoiDiem) {
        if (km == null || !km.isTrangThai()) {
            return false;
        }
        LocalDateTime moc = thoiDiem != null ? thoiDiem : LocalDateTime.now();
        if (km.getNgayBatDauApDung() != null && moc.isBefore(km.getNgayBatDauApDung())) {
            return false;
        }
        if (km.getNgayHetHan() != null && moc.isAfter(km.getNgayHetHan())) {
            return false;
        }
        if (km.getSoLuongTon() != null && km.getSoLuongTon() <= 0) {
            return false;
        }
        return tongTienHang >= km.getGiaTriToiThieuApDung();
    }

    public void xacThucApDungKhuyenMai(MaKhuyenMai km, double tongTienHang, LocalDateTime thoiDiem) {
        if (km == null) {
            return;
        }
        LocalDateTime moc = thoiDiem != null ? thoiDiem : LocalDateTime.now();
        if (!km.isTrangThai()) {
            throw new IllegalStateException("Mã khuyến mãi '" + km.getMaKhuyenMai() + "' đang ngừng hoạt động");
        }
        if (km.getNgayBatDauApDung() != null && moc.isBefore(km.getNgayBatDauApDung())) {
            throw new IllegalStateException("Mã khuyến mãi '" + km.getMaKhuyenMai() + "' chưa đến thời gian áp dụng");
        }
        if (km.getNgayHetHan() != null && moc.isAfter(km.getNgayHetHan())) {
            throw new IllegalStateException("Mã khuyến mãi '" + km.getMaKhuyenMai() + "' đã hết hạn");
        }
        if (km.getSoLuongTon() != null && km.getSoLuongTon() <= 0) {
            throw new IllegalStateException("Mã khuyến mãi '" + km.getMaKhuyenMai() + "' đã hết lượt sử dụng");
        }
        if (tongTienHang < km.getGiaTriToiThieuApDung()) {
            throw new IllegalStateException("Tổng tiền hàng chưa đạt mức tối thiểu ("
                    + km.getGiaTriToiThieuApDung() + ") để áp dụng mã '" + km.getMaKhuyenMai() + "'");
        }
    }

    public boolean themMaKhuyenMai(MaKhuyenMai km) {
        if (km == null || km.getMaKhuyenMai() == null) {
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống");
        }
        km.setMaKhuyenMai(taoMaKhuyenMai(km.getMaKhuyenMai()));
        if (maKhuyenMaiDAO.timTheoMa(km.getMaKhuyenMai()) != null) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại: " + km.getMaKhuyenMai());
        }
        return maKhuyenMaiDAO.them(km);
    }

    public boolean capNhatMaKhuyenMai(MaKhuyenMai km) {
        if (km == null || km.getMaKhuyenMai() == null) {
            throw new IllegalArgumentException("Mã khuyến mãi cần cập nhật không hợp lệ");
        }
        if (maKhuyenMaiDAO.timTheoMa(km.getMaKhuyenMai()) == null) {
            throw new IllegalArgumentException("Không tìm thấy mã khuyến mãi: " + km.getMaKhuyenMai());
        }
        return maKhuyenMaiDAO.capNhat(km);
    }

    public boolean xoaMaKhuyenMai(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi cần xóa không được để trống");
        }
        return maKhuyenMaiDAO.xoa(maKhuyenMai.trim());
    }
}

