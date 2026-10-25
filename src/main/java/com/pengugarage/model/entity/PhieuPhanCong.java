package com.pengugarage.model.entity;

import com.pengugarage.model.enums.TrangThaiSua;
import java.time.LocalDateTime;

/**
 * Thực thể Phiếu Phân Công (PhieuPhanCong)
 */
public class PhieuPhanCong {
    private int id;
    private String maPhanCong;
    private int kyThuatVienId;
    private NhanVien kyThuatVien;
    private int chiTietBaoGiaId;
    private ChiTietBaoGia chiTietBaoGia;
    private LocalDateTime thoiGianPhanCong;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianHoanThanh;
    private TrangThaiSua trangThaiSua;
    private String ghiChu;

    public PhieuPhanCong() {
        this.thoiGianPhanCong = LocalDateTime.now();
        this.trangThaiSua = TrangThaiSua.CHO_THUC_HIEN;
    }

    public PhieuPhanCong(int id, String maPhanCong, int kyThuatVienId, int chiTietBaoGiaId, 
                         LocalDateTime thoiGianPhanCong, LocalDateTime thoiGianBatDau, 
                         LocalDateTime thoiGianHoanThanh, TrangThaiSua trangThaiSua, String ghiChu) {
        this.id = id;
        this.maPhanCong = maPhanCong;
        this.kyThuatVienId = kyThuatVienId;
        this.chiTietBaoGiaId = chiTietBaoGiaId;
        this.thoiGianPhanCong = thoiGianPhanCong;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianHoanThanh = thoiGianHoanThanh;
        this.trangThaiSua = trangThaiSua;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public int getKyThuatVienId() {
        return kyThuatVienId;
    }

    public void setKyThuatVienId(int kyThuatVienId) {
        this.kyThuatVienId = kyThuatVienId;
    }

    public NhanVien getKyThuatVien() {
        return kyThuatVien;
    }

    public void setKyThuatVien(NhanVien kyThuatVien) {
        this.kyThuatVien = kyThuatVien;
        if (kyThuatVien != null) {
            this.kyThuatVienId = kyThuatVien.getId();
        }
    }

    public int getChiTietBaoGiaId() {
        return chiTietBaoGiaId;
    }

    public void setChiTietBaoGiaId(int chiTietBaoGiaId) {
        this.chiTietBaoGiaId = chiTietBaoGiaId;
    }

    public ChiTietBaoGia getChiTietBaoGia() {
        return chiTietBaoGia;
    }

    public void setChiTietBaoGia(ChiTietBaoGia chiTietBaoGia) {
        this.chiTietBaoGia = chiTietBaoGia;
        if (chiTietBaoGia != null) {
            this.chiTietBaoGiaId = chiTietBaoGia.getId();
        }
    }

    public LocalDateTime getThoiGianPhanCong() {
        return thoiGianPhanCong;
    }

    public void setThoiGianPhanCong(LocalDateTime thoiGianPhanCong) {
        this.thoiGianPhanCong = thoiGianPhanCong;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianHoanThanh() {
        return thoiGianHoanThanh;
    }

    public void setThoiGianHoanThanh(LocalDateTime thoiGianHoanThanh) {
        this.thoiGianHoanThanh = thoiGianHoanThanh;
    }

    public TrangThaiSua getTrangThaiSua() {
        return trangThaiSua;
    }

    public void setTrangThaiSua(TrangThaiSua trangThaiSua) {
        this.trangThaiSua = trangThaiSua;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return maPhanCong + " [" + trangThaiSua + "]";
    }
}
