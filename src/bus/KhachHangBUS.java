package bus;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.PhuongTienDAO;
import entity.KhachHang;

import java.util.List;

public class KhachHangBUS {
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final PhuongTienDAO phuongTienDAO = new PhuongTienDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public String taoMaKhachHang(long soThuTu) {
        if (soThuTu < 1L || soThuTu > 999_999_999L) {
            throw new IllegalArgumentException(
                    "Số thứ tự khách hàng thêm mới phải từ 000000001 đến 999999999 (KH000000000 dành riêng cho khách vãng lai)");
        }
        return String.format("KH%09d", soThuTu);
    }

    public String phatSinhMaKhachHang() {
        String maxMa = khachHangDAO.layMaLonNhat();
        long next = 1L;
        if (maxMa != null && maxMa.startsWith("KH")) {
            long current = Long.parseLong(maxMa.substring(2));
            next = Math.max(1L, current + 1L);
        }
        return taoMaKhachHang(next);
    }

    public KhachHang layHoacTaoKhachVangLai() {
        KhachHang khVangLai = khachHangDAO.timKhachVangLai();
        if (khVangLai == null) {
            khVangLai = KhachHang.taoKhachVangLaiMacDinh();
            khachHangDAO.them(khVangLai);
        }
        return khVangLai;
    }

    public List<KhachHang> layTatCa() {
        return khachHangDAO.layTatCa();
    }

    public List<KhachHang> layDanhSachKhachDangKy() {
        return khachHangDAO.layDanhSachKhachDangKy();
    }

    public KhachHang timTheoMa(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return null;
        }
        return khachHangDAO.timTheoMa(maKhachHang.trim());
    }

    public KhachHang layChiTietKhachHang(String maKhachHang) {
        KhachHang kh = timTheoMa(maKhachHang);
        if (kh != null) {
            kh.setDanhSachPhuongTien(phuongTienDAO.timTheoKhachHang(kh.getMaKhachHang()));
            kh.setLichSuThanhToan(hoaDonDAO.timTheoKhachHang(kh.getMaKhachHang()));
        }
        return kh;
    }

    public KhachHang timTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        return khachHangDAO.timTheoSoDienThoai(soDienThoai.trim());
    }

    public boolean themKhachHang(KhachHang kh) {
        if (kh == null) {
            throw new IllegalArgumentException("Thông tin khách hàng không được null");
        }
        if (KhachHang.MA_KHACH_VANG_LAI.equals(kh.getMaKhachHang())) {
            throw new IllegalArgumentException(
                    "Mã " + KhachHang.MA_KHACH_VANG_LAI + " mặc định dành riêng cho khách vãng lai; khách thêm mới bắt đầu từ KH000000001");
        }
        if (KhachHang.SDT_KHACH_VANG_LAI.equals(kh.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại này được dành riêng cho hồ sơ khách vãng lai");
        }
        KhachHang trungSDT = khachHangDAO.timTheoSoDienThoai(kh.getSoDienThoai());
        if (trungSDT != null) {
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký cho khách hàng "
                    + trungSDT.getMaKhachHang());
        }
        if (kh.getMaKhachHang() == null || kh.getMaKhachHang().trim().isEmpty()) {
            kh.setMaKhachHang(phatSinhMaKhachHang());
        } else if (khachHangDAO.timTheoMa(kh.getMaKhachHang()) != null) {
            throw new IllegalArgumentException("Mã khách hàng đã tồn tại: " + kh.getMaKhachHang());
        }
        return khachHangDAO.them(kh);
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        if (kh == null || kh.getMaKhachHang() == null) {
            throw new IllegalArgumentException("Khách hàng cần cập nhật không hợp lệ");
        }
        if (kh.laKhachVangLai()) {
            throw new IllegalStateException("Không thể chỉnh sửa hồ sơ mặc định của Khách vãng lai ("
                    + KhachHang.MA_KHACH_VANG_LAI + ")");
        }
        if (khachHangDAO.timTheoMa(kh.getMaKhachHang()) == null) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng có mã: " + kh.getMaKhachHang());
        }
        KhachHang trungSDT = khachHangDAO.timTheoSoDienThoai(kh.getSoDienThoai());
        if (trungSDT != null && !trungSDT.getMaKhachHang().equals(kh.getMaKhachHang())) {
            throw new IllegalArgumentException("Số điện thoại đã thuộc về khách hàng khác ("
                    + trungSDT.getMaKhachHang() + ")");
        }
        return khachHangDAO.capNhat(kh);
    }

    public boolean xoaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng cần xóa không được để trống");
        }
        String ma = maKhachHang.trim();
        if (KhachHang.MA_KHACH_VANG_LAI.equals(ma)) {
            throw new IllegalStateException("Không được xóa hồ sơ mặc định của Khách vãng lai ("
                    + KhachHang.MA_KHACH_VANG_LAI + ")");
        }
        return khachHangDAO.xoa(ma);
    }
}
