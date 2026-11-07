package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KhachHang {
    public static final String REGEX_MA_KHACH_HANG = "KH\\d{9}";
    public static final String MA_KHACH_VANG_LAI = "KH000000000";
    public static final String TEN_KHACH_VANG_LAI = "Khách vãng lai";
    public static final String SDT_KHACH_VANG_LAI = "0000000000";

    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;

    private List<PhuongTien> danhSachPhuongTien = new ArrayList<>();
    private List<HoaDon> lichSuThanhToan = new ArrayList<>();

    public KhachHang() {
    }

    public KhachHang(String maKhachHang) {
        setMaKhachHang(maKhachHang);
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String diaChi, String gioiTinh) {
        setMaKhachHang(maKhachHang);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setGioiTinh(gioiTinh);
    }

    public static KhachHang taoKhachVangLaiMacDinh() {
        return new KhachHang(MA_KHACH_VANG_LAI, TEN_KHACH_VANG_LAI, SDT_KHACH_VANG_LAI, null, null);
    }

    public boolean laKhachVangLai() {
        return MA_KHACH_VANG_LAI.equals(this.maKhachHang);
    }

    public List<PhuongTien> xemDSPhuongTien() {
        return danhSachPhuongTien;
    }

    public void setDanhSachPhuongTien(List<PhuongTien> danhSachPhuongTien) {
        this.danhSachPhuongTien = danhSachPhuongTien != null ? danhSachPhuongTien : new ArrayList<>();
    }

    public List<HoaDon> xemLSThanhToan() {
        return lichSuThanhToan;
    }

    public void setLichSuThanhToan(List<HoaDon> lichSuThanhToan) {
        this.lichSuThanhToan = lichSuThanhToan != null ? lichSuThanhToan : new ArrayList<>();
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || !maKhachHang.matches("^" + REGEX_MA_KHACH_HANG + "$")) {
            throw new IllegalArgumentException(
                    "Mã khách hàng không hợp lệ (định dạng: KHxxxxxxxxx, KH000000000 cho khách vãng lai, KH000000001 - KH999999999 cho khách đăng ký)");
        }
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            if (laKhachVangLai()) {
                this.hoTen = TEN_KHACH_VANG_LAI;
                return;
            }
            throw new IllegalArgumentException("Họ tên khách hàng không được để trống");
        }
        this.hoTen = hoTen.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            if (laKhachVangLai()) {
                this.soDienThoai = SDT_KHACH_VANG_LAI;
                return;
            }
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống");
        }
        this.soDienThoai = soDienThoai.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhachHang khachHang)) return false;
        return Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maKhachHang);
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                '}';
    }
}
