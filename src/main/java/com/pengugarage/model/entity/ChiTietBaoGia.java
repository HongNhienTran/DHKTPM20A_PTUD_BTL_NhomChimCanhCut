package com.pengugarage.model.entity;

import com.pengugarage.model.enums.LoaiHangMuc;

/**
 * Thực thể Chi Tiết Báo Giá (ChiTietBaoGia)
 */
public class ChiTietBaoGia {
    private int id;
    private int phieuBaoGiaId;
    private PhieuBaoGia phieuBaoGia;
    private LoaiHangMuc loaiHangMuc;
    private Integer dichVuId;
    private DichVu dichVu;
    private Integer sanPhamId;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private double thanhTien;
    private boolean duocChapNhan;
    private String ghiChu;

    public ChiTietBaoGia() {
        this.soLuong = 1;
        this.duocChapNhan = false;
    }

    public ChiTietBaoGia(int id, int phieuBaoGiaId, LoaiHangMuc loaiHangMuc, Integer dichVuId, 
                         Integer sanPhamId, int soLuong, double donGia, double thanhTien, 
                         boolean duocChapNhan, String ghiChu) {
        this.id = id;
        this.phieuBaoGiaId = phieuBaoGiaId;
        this.loaiHangMuc = loaiHangMuc;
        this.dichVuId = dichVuId;
        this.sanPhamId = sanPhamId;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.duocChapNhan = duocChapNhan;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhieuBaoGiaId() {
        return phieuBaoGiaId;
    }

    public void setPhieuBaoGiaId(int phieuBaoGiaId) {
        this.phieuBaoGiaId = phieuBaoGiaId;
    }

    public PhieuBaoGia getPhieuBaoGia() {
        return phieuBaoGia;
    }

    public void setPhieuBaoGia(PhieuBaoGia phieuBaoGia) {
        this.phieuBaoGia = phieuBaoGia;
        if (phieuBaoGia != null) {
            this.phieuBaoGiaId = phieuBaoGia.getId();
        }
    }

    public LoaiHangMuc getLoaiHangMuc() {
        return loaiHangMuc;
    }

    public void setLoaiHangMuc(LoaiHangMuc loaiHangMuc) {
        this.loaiHangMuc = loaiHangMuc;
    }

    public Integer getDichVuId() {
        return dichVuId;
    }

    public void setDichVuId(Integer dichVuId) {
        this.dichVuId = dichVuId;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
        if (dichVu != null) {
            this.dichVuId = dichVu.getId();
            this.loaiHangMuc = LoaiHangMuc.DICH_VU;
            this.donGia = dichVu.getDonGia();
        }
    }

    public Integer getSanPhamId() {
        return sanPhamId;
    }

    public void setSanPhamId(Integer sanPhamId) {
        this.sanPhamId = sanPhamId;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
        if (sanPham != null) {
            this.sanPhamId = sanPham.getId();
            this.loaiHangMuc = LoaiHangMuc.SAN_PHAM;
            this.donGia = sanPham.getDonGiaBan();
        }
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = tinhThanhTien();
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
        this.thanhTien = tinhThanhTien();
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public boolean isDuocChapNhan() {
        return duocChapNhan;
    }

    public void setDuocChapNhan(boolean duocChapNhan) {
        this.duocChapNhan = duocChapNhan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public double tinhThanhTien() {
        this.thanhTien = this.soLuong * this.donGia;
        return this.thanhTien;
    }

    @Override
    public String toString() {
        String tenHangMuc = (loaiHangMuc == LoaiHangMuc.DICH_VU && dichVu != null) ? dichVu.getTenDichVu() :
                (loaiHangMuc == LoaiHangMuc.SAN_PHAM && sanPham != null) ? sanPham.getTenSanPham() : "Hạng mục";
        return tenHangMuc + " x " + soLuong + " = " + thanhTien;
    }
}
