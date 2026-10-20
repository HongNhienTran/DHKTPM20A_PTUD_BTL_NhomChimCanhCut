package com.pengugarage.model.entity;

/**
 * Thực thể Chi Tiết Dùng Phụ Tùng (ChiTietDungPhuTung)
 */
public class ChiTietDungPhuTung {
    private int id;
    private int phieuPhanCongId;
    private PhieuPhanCong phieuPhanCong;
    private int sanPhamId;
    private SanPham sanPham;
    private int soLuong;
    private boolean daTruKho;
    private String ghiChu;

    public ChiTietDungPhuTung() {
        this.soLuong = 1;
        this.daTruKho = false;
    }

    public ChiTietDungPhuTung(int id, int phieuPhanCongId, int sanPhamId, int soLuong, boolean daTruKho, String ghiChu) {
        this.id = id;
        this.phieuPhanCongId = phieuPhanCongId;
        this.sanPhamId = sanPhamId;
        this.soLuong = soLuong;
        this.daTruKho = daTruKho;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhieuPhanCongId() {
        return phieuPhanCongId;
    }

    public void setPhieuPhanCongId(int phieuPhanCongId) {
        this.phieuPhanCongId = phieuPhanCongId;
    }

    public PhieuPhanCong getPhieuPhanCong() {
        return phieuPhanCong;
    }

    public void setPhieuPhanCong(PhieuPhanCong phieuPhanCong) {
        this.phieuPhanCong = phieuPhanCong;
        if (phieuPhanCong != null) {
            this.phieuPhanCongId = phieuPhanCong.getId();
        }
    }

    public int getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(int sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
        if (sanPham != null) {
            this.sanPhamId = sanPham.getId();
        }
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
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

    @Override
    public String toString() {
        String spName = (sanPham != null) ? sanPham.getTenSanPham() : "Phụ tùng";
        return spName + " x " + soLuong + (daTruKho ? " (Đã trừ kho)" : " (Chưa trừ kho)");
    }
}
