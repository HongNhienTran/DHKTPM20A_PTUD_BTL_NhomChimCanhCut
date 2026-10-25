package com.pengugarage.model.entity;

/**
 * Thực thể Khách Hàng (KhachHang)
 */
public class KhachHang {
    private int id;
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;

    public KhachHang() {
    }

    public KhachHang(int id, String maKhachHang, String hoTen, String soDienThoai, String diaChi, String gioiTinh) {
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.gioiTinh = gioiTinh;
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, String diaChi, String gioiTinh) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.gioiTinh = gioiTinh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
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

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void xemDSPhuongTien() {
        // Nghiệp vụ xem danh sách xe của khách hàng
    }

    public void xemLSMuaHang() {
        // Nghiệp vụ xem lịch sử mua sắm / sửa chữa của khách hàng
    }

    @Override
    public String toString() {
        return hoTen + " (" + soDienThoai + ")";
    }
}
