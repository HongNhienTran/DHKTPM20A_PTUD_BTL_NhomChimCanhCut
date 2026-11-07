package entity;

import enums.LoaiHangMuc;

import java.util.Objects;

public class ChiTietHoaDon {
    private LoaiHangMuc loaiHangMuc;
    private int soLuong;
    private double donGia;

    private HoaDon hoaDon;
    private DichVu dichVu;
    private SanPham sanPham;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(LoaiHangMuc loaiHangMuc, int soLuong, double donGia,
                         HoaDon hoaDon, DichVu dichVu, SanPham sanPham) {
        setLoaiHangMuc(loaiHangMuc);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setHoaDon(hoaDon);
        setDichVu(dichVu);
        setSanPham(sanPham);
    }

    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public double tinhThue() {
        double thueSuat = 0;
        if (loaiHangMuc == LoaiHangMuc.DICH_VU && dichVu != null) {
            thueSuat = dichVu.getThueVAT();
        } else if (loaiHangMuc == LoaiHangMuc.SAN_PHAM && sanPham != null) {
            thueSuat = sanPham.getThueVAT();
        }
        double tyLe = thueSuat > 1 ? thueSuat / 100.0 : thueSuat;
        return tinhThanhTien() * tyLe;
    }

    public double tinhThanhTienSauThue() {
        return tinhThanhTien() + tinhThue();
    }

    public LoaiHangMuc getLoaiHangMuc() {
        return loaiHangMuc;
    }

    public void setLoaiHangMuc(LoaiHangMuc loaiHangMuc) {
        if (loaiHangMuc == null) {
            throw new IllegalArgumentException("Loại hạng mục trong chi tiết hóa đơn không được null");
        }
        this.loaiHangMuc = loaiHangMuc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng trong chi tiết hóa đơn phải lớn hơn 0");
        }
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia < 0) {
            throw new IllegalArgumentException("Đơn giá trong chi tiết hóa đơn không được âm");
        }
        this.donGia = donGia;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDon that)) return false;
        return Objects.equals(hoaDon, that.hoaDon) &&
                loaiHangMuc == that.loaiHangMuc &&
                Objects.equals(dichVu, that.dichVu) &&
                Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, loaiHangMuc, dichVu, sanPham);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "hoaDon=" + (hoaDon != null ? hoaDon.getMaHoaDon() : null) +
                ", loaiHangMuc=" + loaiHangMuc +
                ", dichVu=" + (dichVu != null ? dichVu.getMaDichVu() : null) +
                ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : null) +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTienSauThue=" + tinhThanhTienSauThue() +
                '}';
    }
}
