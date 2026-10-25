package com.pengugarage.model.entity;

/**
 * Thực thể Danh Mục Sản Phẩm (DanhMucSanPham)
 */
public class DanhMucSanPham {
    private int id;
    private String maDanhMuc;
    private String tenDanhMuc;
    private String moTa;
    private boolean trangThai;

    public DanhMucSanPham() {
        this.trangThai = true;
    }

    public DanhMucSanPham(int id, String maDanhMuc, String tenDanhMuc, String moTa, boolean trangThai) {
        this.id = id;
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public DanhMucSanPham(String maDanhMuc, String tenDanhMuc, String moTa) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
        this.trangThai = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(String maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenDanhMuc;
    }
}
