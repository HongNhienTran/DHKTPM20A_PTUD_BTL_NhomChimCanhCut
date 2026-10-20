package com.pengugarage.util;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.entity.TaiKhoan;
import com.pengugarage.model.enums.ChucVu;

/**
 * Tiện ích quản lý phiên đăng nhập của người dùng hiện tại (Session Management).
 */
public class PhienDangNhapUtil {

    private static PhienDangNhapUtil instance;

    private TaiKhoan taiKhoan;
    private NhanVien nhanVien;

    private PhienDangNhapUtil() {
    }

    public static synchronized PhienDangNhapUtil getInstance() {
        if (instance == null) {
            instance = new PhienDangNhapUtil();
        }
        return instance;
    }

    public void login(TaiKhoan taiKhoan, NhanVien nhanVien) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = nhanVien;
    }

    public void logout() {
        this.taiKhoan = null;
        this.nhanVien = null;
    }

    public boolean isLoggedIn() {
        return taiKhoan != null && nhanVien != null;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public ChucVu getChucVu() {
        return nhanVien != null ? nhanVien.getChucVu() : null;
    }

    public String getHoTen() {
        return nhanVien != null ? nhanVien.getHoTen() : "";
    }

    public boolean isQuanLy() {
        return getChucVu() == ChucVu.QUAN_LY;
    }

    public boolean isKyThuatVien() {
        return getChucVu() == ChucVu.KY_THUAT_VIEN;
    }

    public boolean isLeTan() {
        return getChucVu() == ChucVu.LE_TAN;
    }
}
