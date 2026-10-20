package com.pengugarage.util;

import com.pengugarage.model.enums.ChucVu;

/**
 * Tiện ích kiểm tra và phân định quyền truy cập theo chức vụ nhân viên (Authorization).
 */
public class PhanQuyenUtil {

    private PhanQuyenUtil() {
    }

    /**
     * Kiểm tra người dùng hiện tại có quyền Quản lý hay không
     */
    public static boolean coQuyenQuanLy() {
        return PhienDangNhapUtil.getInstance().isQuanLy();
    }

    /**
     * Kiểm tra người dùng có quyền quản lý nhân sự & tài khoản không (chỉ Quản lý)
     */
    public static boolean coQuyenQuanLyNhanSu() {
        return coQuyenQuanLy();
    }

    /**
     * Kiểm tra người dùng có quyền quản lý phụ tùng / kho không (Quản lý và Kỹ thuật viên)
     */
    public static boolean coQuyenQuanLyPhuTung() {
        ChucVu cv = PhienDangNhapUtil.getInstance().getChucVu();
        return cv == ChucVu.QUAN_LY || cv == ChucVu.KY_THUAT_VIEN;
    }

    /**
     * Kiểm tra người dùng có quyền tiếp nhận xe & lập hóa đơn không (Quản lý và Lễ tân)
     */
    public static boolean coQuyenThuNganTiepNhan() {
        ChucVu cv = PhienDangNhapUtil.getInstance().getChucVu();
        return cv == ChucVu.QUAN_LY || cv == ChucVu.LE_TAN;
    }

    /**
     * Kiểm tra người dùng có quyền xem báo cáo thống kê doanh thu không (chỉ Quản lý)
     */
    public static boolean coQuyenXemBaoCao() {
        return coQuyenQuanLy();
    }
}
