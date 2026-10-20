package com.pengugarage.model.entity;

import com.pengugarage.model.enums.NhienLieu;

/**
 * Thực thể Phương Tiện (PhuongTien)
 */
public class PhuongTien {
    private int id;
    private String maPhuongTien;
    private int hangXeId;
    private HangXe hangXe;
    private int khachHangId;
    private KhachHang khachHang;
    private String bienSoXe;
    private String mauXe;
    private String mauSacXe;
    private int namSanXuat;
    private NhienLieu nhienLieu;
    private String soKhung;
    private String soMay;

    public PhuongTien() {
    }

    public PhuongTien(int id, String maPhuongTien, int hangXeId, int khachHangId, String bienSoXe, 
                      String mauXe, String mauSacXe, int namSanXuat, NhienLieu nhienLieu, 
                      String soKhung, String soMay) {
        this.id = id;
        this.maPhuongTien = maPhuongTien;
        this.hangXeId = hangXeId;
        this.khachHangId = khachHangId;
        this.bienSoXe = bienSoXe;
        this.mauXe = mauXe;
        this.mauSacXe = mauSacXe;
        this.namSanXuat = namSanXuat;
        this.nhienLieu = nhienLieu;
        this.soKhung = soKhung;
        this.soMay = soMay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(String maPhuongTien) {
        this.maPhuongTien = maPhuongTien;
    }

    public int getHangXeId() {
        return hangXeId;
    }

    public void setHangXeId(int hangXeId) {
        this.hangXeId = hangXeId;
    }

    public HangXe getHangXe() {
        return hangXe;
    }

    public void setHangXe(HangXe hangXe) {
        this.hangXe = hangXe;
        if (hangXe != null) {
            this.hangXeId = hangXe.getId();
        }
    }

    public int getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(int khachHangId) {
        this.khachHangId = khachHangId;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
        if (khachHang != null) {
            this.khachHangId = khachHang.getId();
        }
    }

    public String getBienSoXe() {
        return bienSoXe;
    }

    public void setBienSoXe(String bienSoXe) {
        this.bienSoXe = bienSoXe;
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        this.mauXe = mauXe;
    }

    public String getMauSacXe() {
        return mauSacXe;
    }

    public void setMauSacXe(String mauSacXe) {
        this.mauSacXe = mauSacXe;
    }

    public int getNamSanXuat() {
        return namSanXuat;
    }

    public void setNamSanXuat(int namSanXuat) {
        this.namSanXuat = namSanXuat;
    }

    public NhienLieu getNhienLieu() {
        return nhienLieu;
    }

    public void setNhienLieu(NhienLieu nhienLieu) {
        this.nhienLieu = nhienLieu;
    }

    public String getSoKhung() {
        return soKhung;
    }

    public void setSoKhung(String soKhung) {
        this.soKhung = soKhung;
    }

    public String getSoMay() {
        return soMay;
    }

    public void setSoMay(String soMay) {
        this.soMay = soMay;
    }

    public void xemLSDungDichVu() {
        // Nghiệp vụ tra cứu lịch sử bảo dưỡng / sửa chữa của xe
    }

    @Override
    public String toString() {
        return bienSoXe + " - " + mauXe;
    }
}
