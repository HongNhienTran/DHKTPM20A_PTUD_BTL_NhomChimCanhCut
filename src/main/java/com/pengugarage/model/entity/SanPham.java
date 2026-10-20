package com.pengugarage.model.entity;

/**
 * Thực thể Sản Phẩm / Phụ Tùng (SanPham)
 */
public class SanPham {
    private int id;
    private int danhMucId;
    private DanhMucSanPham danhMuc;
    private String maSanPham;
    private String tenSanPham;
    private String donViTinh;
    private int soLuongTon;
    private int soLuongTonToiThieu;
    private double giaNhap;
    private double donGiaBan;
    private double thueVAT;
    private boolean trangThai;
    private String anhSanPham;

    public SanPham() {
        this.thueVAT = 10.0;
        this.trangThai = true;
    }

    public SanPham(int id, int danhMucId, String maSanPham, String tenSanPham, String donViTinh, 
                   int soLuongTon, int soLuongTonToiThieu, double giaNhap, double donGiaBan, 
                   double thueVAT, boolean trangThai, String anhSanPham) {
        this.id = id;
        this.danhMucId = danhMucId;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.soLuongTonToiThieu = soLuongTonToiThieu;
        this.giaNhap = giaNhap;
        this.donGiaBan = donGiaBan;
        this.thueVAT = thueVAT;
        this.trangThai = trangThai;
        this.anhSanPham = anhSanPham;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDanhMucId() {
        return danhMucId;
    }

    public void setDanhMucId(int danhMucId) {
        this.danhMucId = danhMucId;
    }

    public DanhMucSanPham getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMucSanPham danhMuc) {
        this.danhMuc = danhMuc;
        if (danhMuc != null) {
            this.danhMucId = danhMuc.getId();
        }
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getSoLuongTonToiThieu() {
        return soLuongTonToiThieu;
    }

    public void setSoLuongTonToiThieu(int soLuongTonToiThieu) {
        this.soLuongTonToiThieu = soLuongTonToiThieu;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        this.donGiaBan = donGiaBan;
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

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }

    // Alias hỗ trợ tương thích với quy định duongDanAnh
    public String getDuongDanAnh() {
        return anhSanPham;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.anhSanPham = duongDanAnh;
    }

    @Override
    public String toString() {
        return tenSanPham + " (" + maSanPham + ")";
    }
}
