package com.pengugarage.model.entity;

/**
 * Thực thể Tài Khoản (TaiKhoan) - Quan hệ 1-1 với NhanVien
 */
public class TaiKhoan {
    private int id;
    private int nhanVienId;
    private NhanVien nhanVien;
    private String tenDangNhap;
    private String matKhauMaHoa;

    public TaiKhoan() {
    }

    public TaiKhoan(int id, int nhanVienId, String tenDangNhap, String matKhauMaHoa) {
        this.id = id;
        this.nhanVienId = nhanVienId;
        this.tenDangNhap = tenDangNhap;
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public TaiKhoan(int nhanVienId, String tenDangNhap, String matKhauMaHoa) {
        this.nhanVienId = nhanVienId;
        this.tenDangNhap = tenDangNhap;
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNhanVienId() {
        return nhanVienId;
    }

    public void setNhanVienId(int nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        if (nhanVien != null) {
            this.nhanVienId = nhanVien.getId();
        }
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauMaHoa() {
        return matKhauMaHoa;
    }

    public void setMatKhauMaHoa(String matKhauMaHoa) {
        this.matKhauMaHoa = matKhauMaHoa;
    }

    @Override
    public String toString() {
        return tenDangNhap;
    }
}
