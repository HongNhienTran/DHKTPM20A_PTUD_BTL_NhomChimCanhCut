package com.pengugarage.model.entity;

/**
 * Thực thể Hãng Xe (HangXe)
 */
public class HangXe {
    private int id;
    private String maHangXe;
    private String tenHang;
    private String quocGia;

    public HangXe() {
    }

    public HangXe(int id, String maHangXe, String tenHang, String quocGia) {
        this.id = id;
        this.maHangXe = maHangXe;
        this.tenHang = tenHang;
        this.quocGia = quocGia;
    }

    public HangXe(String maHangXe, String tenHang, String quocGia) {
        this.maHangXe = maHangXe;
        this.tenHang = tenHang;
        this.quocGia = quocGia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaHangXe() {
        return maHangXe;
    }

    public void setMaHangXe(String maHangXe) {
        this.maHangXe = maHangXe;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public String getQuocGia() {
        return quocGia;
    }

    public void setQuocGia(String quocGia) {
        this.quocGia = quocGia;
    }

    @Override
    public String toString() {
        return tenHang;
    }
}
