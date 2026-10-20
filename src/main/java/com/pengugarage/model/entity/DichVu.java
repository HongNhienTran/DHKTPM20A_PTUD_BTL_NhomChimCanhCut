package com.pengugarage.model.entity;

/**
 * Thực thể Dịch Vụ Kỹ Thuật (DichVu)
 */
public class DichVu {
    private int id;
    private String maDichVu;
    private String tenDichVu;
    private String moTa;
    private double donGia;
    private double thueVAT;
    private boolean trangThai;

    public DichVu() {
        this.thueVAT = 10.0;
        this.trangThai = true;
    }

    public DichVu(int id, String maDichVu, String tenDichVu, String moTa, double donGia, double thueVAT, boolean trangThai) {
        this.id = id;
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.donGia = donGia;
        this.thueVAT = thueVAT;
        this.trangThai = trangThai;
    }

    public DichVu(String maDichVu, String tenDichVu, String moTa, double donGia, double thueVAT) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.donGia = donGia;
        this.thueVAT = thueVAT;
        this.trangThai = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(double thueVAT) {
        this.thueVAT = thueVAT;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenDichVu;
    }
}
