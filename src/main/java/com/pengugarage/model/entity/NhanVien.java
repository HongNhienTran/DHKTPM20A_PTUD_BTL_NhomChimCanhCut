package com.pengugarage.model.entity;

import com.pengugarage.model.enums.ChucVu;

/**
 * Thực thể Nhân Viên (NhanVien)
 */
public class NhanVien {
    private int id;
    private String maNhanVien;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private ChucVu chucVu;
    private boolean trangThai;

    public NhanVien() {
        this.trangThai = true;
    }

    public NhanVien(int id, String maNhanVien, String hoTen, String soDienThoai, String diaChi, ChucVu chucVu, boolean trangThai) {
        this.id = id;
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }

    public NhanVien(String maNhanVien, String hoTen, String soDienThoai, String diaChi, ChucVu chucVu) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.trangThai = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return hoTen + " [" + chucVu + "]";
    }
}
