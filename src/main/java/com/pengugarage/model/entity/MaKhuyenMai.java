package com.pengugarage.model.entity;

import com.pengugarage.model.enums.LoaiGiamGia;
import java.time.LocalDateTime;

/**
 * Thực thể Mã Khuyến Mãi (MaKhuyenMai)
 */
public class MaKhuyenMai {
    private int id;
    private String maKhuyenMai;
    private LoaiGiamGia loaiGiam;
    private double giaTriGiam;
    private double giaTriToiThieuApDung;
    private LocalDateTime ngayBatDauApDung;
    private LocalDateTime ngayHetHan;
    private Integer soLuongTon;
    private boolean trangThai;

    public MaKhuyenMai() {
        this.trangThai = true;
    }

    public MaKhuyenMai(int id, String maKhuyenMai, LoaiGiamGia loaiGiam, double giaTriGiam, 
                       double giaTriToiThieuApDung, LocalDateTime ngayBatDauApDung, 
                       LocalDateTime ngayHetHan, Integer soLuongTon, boolean trangThai) {
        this.id = id;
        this.maKhuyenMai = maKhuyenMai;
        this.loaiGiam = loaiGiam;
        this.giaTriGiam = giaTriGiam;
        this.giaTriToiThieuApDung = giaTriToiThieuApDung;
        this.ngayBatDauApDung = ngayBatDauApDung;
        this.ngayHetHan = ngayHetHan;
        this.soLuongTon = soLuongTon;
        this.trangThai = trangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public LoaiGiamGia getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(LoaiGiamGia loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(double giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public double getGiaTriToiThieuApDung() {
        return giaTriToiThieuApDung;
    }

    public void setGiaTriToiThieuApDung(double giaTriToiThieuApDung) {
        this.giaTriToiThieuApDung = giaTriToiThieuApDung;
    }

    public LocalDateTime getNgayBatDauApDung() {
        return ngayBatDauApDung;
    }

    public void setNgayBatDauApDung(LocalDateTime ngayBatDauApDung) {
        this.ngayBatDauApDung = ngayBatDauApDung;
    }

    public LocalDateTime getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDateTime ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maKhuyenMai + " (" + loaiGiam + ": " + giaTriGiam + ")";
    }
}
