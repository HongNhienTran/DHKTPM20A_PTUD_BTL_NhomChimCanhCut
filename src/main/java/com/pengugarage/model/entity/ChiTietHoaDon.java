package com.pengugarage.model.entity;

import com.pengugarage.model.enums.LoaiHangMuc;

/**
 * Thực thể Chi Tiết Hóa Đơn (ChiTietHoaDon) - Lưu snapshot giá và thuế
 */
public class ChiTietHoaDon {
    private int id;
    private int hoaDonId;
    private HoaDon hoaDon;
    private LoaiHangMuc loaiHangMuc;
    private Integer dichVuId;
    private DichVu dichVu;
    private Integer sanPhamId;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private double thueVAT;
    private double tienThue;
    private double thanhTien;
    private double thanhTienSauThue;
    private String ghiChu;

    public ChiTietHoaDon() {
        this.soLuong = 1;
        this.thueVAT = 10.0;
    }

    public ChiTietHoaDon(int id, int hoaDonId, LoaiHangMuc loaiHangMuc, Integer dichVuId, 
                         Integer sanPhamId, int soLuong, double donGia, double thueVAT, 
                         double tienThue, double thanhTien, double thanhTienSauThue, String ghiChu) {
        this.id = id;
        this.hoaDonId = hoaDonId;
        this.loaiHangMuc = loaiHangMuc;
        this.dichVuId = dichVuId;
        this.sanPhamId = sanPhamId;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thueVAT = thueVAT;
        this.tienThue = tienThue;
        this.thanhTien = thanhTien;
        this.thanhTienSauThue = thanhTienSauThue;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(int hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        if (hoaDon != null) {
            this.hoaDonId = hoaDon.getId();
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
            this.thueVAT = dichVu.getThueVAT();
            tinhThanhTien();
            tinhThue();
            tinhThanhTienSauThue();
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
            this.thueVAT = sanPham.getThueVAT();
            tinhThanhTien();
            tinhThue();
            tinhThanhTienSauThue();
        }
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        tinhThanhTien();
        tinhThue();
        tinhThanhTienSauThue();
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
        tinhThanhTien();
        tinhThue();
        tinhThanhTienSauThue();
    }

    public double getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(double thueVAT) {
        this.thueVAT = thueVAT;
        tinhThue();
        tinhThanhTienSauThue();
    }

    public double getTienThue() {
        return tienThue;
    }

    public void setTienThue(double tienThue) {
        this.tienThue = tienThue;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public double getThanhTienSauThue() {
        return thanhTienSauThue;
    }

    public void setThanhTienSauThue(double thanhTienSauThue) {
        this.thanhTienSauThue = thanhTienSauThue;
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

    public double tinhThue() {
        this.tienThue = Math.round(this.thanhTien * (this.thueVAT / 100.0) * 100.0) / 100.0;
        return this.tienThue;
    }

    public double tinhThanhTienSauThue() {
        this.thanhTienSauThue = this.thanhTien + this.tienThue;
        return this.thanhTienSauThue;
    }

    @Override
    public String toString() {
        String tenHangMuc = (loaiHangMuc == LoaiHangMuc.DICH_VU && dichVu != null) ? dichVu.getTenDichVu() :
                (loaiHangMuc == LoaiHangMuc.SAN_PHAM && sanPham != null) ? sanPham.getTenSanPham() : "Hạng mục";
        return tenHangMuc + " x " + soLuong + " = " + thanhTienSauThue + " đ (đã gồm VAT)";
    }
}
