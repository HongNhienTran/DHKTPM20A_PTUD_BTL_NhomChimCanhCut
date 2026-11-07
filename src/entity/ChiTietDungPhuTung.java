package entity;

import java.util.Objects;

public class ChiTietDungPhuTung {
    private int soLuong;
    private double donGia;
    private boolean daTruKho;
    private String ghiChu;

    private ChiTietBaoGia chiTietBaoGia;
    private SanPham sanPham;

    public ChiTietDungPhuTung() {
    }

    public ChiTietDungPhuTung(int soLuong, double donGia, boolean daTruKho, String ghiChu,
                              ChiTietBaoGia chiTietBaoGia, SanPham sanPham) {
        setSoLuong(soLuong);
        setDonGia(donGia);
        setDaTruKho(daTruKho);
        setGhiChu(ghiChu);
        setChiTietBaoGia(chiTietBaoGia);
        setSanPham(sanPham);
    }

    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public void truKho() {
        if (!daTruKho && sanPham != null) {
            if (sanPham.getSoLuongTon() < soLuong) {
                throw new IllegalStateException(
                        "Sản phẩm '" + sanPham.getTenSanPham() + "' không đủ tồn kho (còn "
                                + sanPham.getSoLuongTon() + ", cần " + soLuong + ")");
            }
            sanPham.setSoLuongTon(sanPham.getSoLuongTon() - soLuong);
            this.daTruKho = true;
        }
    }

    public void hoanKho() {
        if (daTruKho && sanPham != null) {
            sanPham.setSoLuongTon(sanPham.getSoLuongTon() + soLuong);
            this.daTruKho = false;
        }
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phụ tùng phải lớn hơn 0");
        }
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia < 0) {
            throw new IllegalArgumentException("Đơn giá phụ tùng không được âm");
        }
        this.donGia = donGia;
    }

    public boolean isDaTruKho() {
        return daTruKho;
    }

    public void setDaTruKho(boolean daTruKho) {
        this.daTruKho = daTruKho;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public ChiTietBaoGia getChiTietBaoGia() {
        return chiTietBaoGia;
    }

    public void setChiTietBaoGia(ChiTietBaoGia chiTietBaoGia) {
        this.chiTietBaoGia = chiTietBaoGia;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm phụ tùng không được null");
        }
        this.sanPham = sanPham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietDungPhuTung that)) return false;
        return Objects.equals(chiTietBaoGia, that.chiTietBaoGia) &&
                Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiTietBaoGia, sanPham);
    }

    @Override
    public String toString() {
        return "ChiTietDungPhuTung{" +
                "sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : null) +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + tinhThanhTien() +
                ", daTruKho=" + daTruKho +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}
