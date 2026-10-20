package com.pengugarage.service;

import com.pengugarage.dao.TaiKhoanDAO;
import com.pengugarage.model.entity.TaiKhoan;
import com.pengugarage.util.MatKhauUtil;

import java.util.List;

/**
 * Service xử lý các quy tắc nghiệp vụ quản lý tài khoản người dùng (M01-T08).
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public TaiKhoanService(TaiKhoanDAO taiKhoanDAO) {
        this.taiKhoanDAO = taiKhoanDAO;
    }

    public List<TaiKhoan> layTatCa() {
        return taiKhoanDAO.layTatCa();
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        return taiKhoanDAO.findByTenDangNhap(tenDangNhap);
    }

    public TaiKhoan timTheoNhanVienId(int nhanVienId) {
        return taiKhoanDAO.timTheoNhanVienId(nhanVienId);
    }

    /**
     * Tạo tài khoản mới cho nhân viên.
     * Mật khẩu phải đáp ứng tiêu chuẩn mạnh (hoa, thường, số, ký tự đặc biệt, >= 8 ký tự).
     */
    public String taoTaiKhoan(int nhanVienId, String tenDangNhap, String matKhauRaw) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return "Tên đăng nhập không được để trống!";
        }

        if (taiKhoanDAO.findByTenDangNhap(tenDangNhap.trim()) != null) {
            return "Tên đăng nhập '" + tenDangNhap + "' đã tồn tại!";
        }

        if (taiKhoanDAO.timTheoNhanVienId(nhanVienId) != null) {
            return "Nhân viên này đã có tài khoản hệ thống!";
        }

        String loiMatKhau = kiemTraMatKhauManh(matKhauRaw);
        if (loiMatKhau != null) {
            return loiMatKhau;
        }

        // Băm mật khẩu bằng thuật toán SHA-256
        String hash = MatKhauUtil.maHoa(matKhauRaw);
        TaiKhoan tk = new TaiKhoan(nhanVienId, tenDangNhap.trim(), hash);

        boolean thanhCong = taiKhoanDAO.them(tk);
        return thanhCong ? null : "Lỗi khi lưu tài khoản vào cơ sở dữ liệu!";
    }

    /**
     * Đổi mật khẩu tài khoản
     */
    public String doiMatKhau(int taiKhoanId, String matKhauCu, String matKhauMoi, String xacNhanMatKhau) {
        if (matKhauCu == null || matKhauCu.trim().isEmpty()) {
            return "Vui lòng nhập mật khẩu hiện tại!";
        }

        if (matKhauMoi == null || !matKhauMoi.equals(xacNhanMatKhau)) {
            return "Mật khẩu mới và xác nhận mật khẩu không trùng khớp!";
        }

        String loiMatKhau = kiemTraMatKhauManh(matKhauMoi);
        if (loiMatKhau != null) {
            return loiMatKhau;
        }

        List<TaiKhoan> list = taiKhoanDAO.layTatCa();
        TaiKhoan target = null;
        for (TaiKhoan tk : list) {
            if (tk.getId() == taiKhoanId) {
                target = tk;
                break;
            }
        }

        if (target == null) {
            return "Tài khoản không tồn tại!";
        }

        if (!MatKhauUtil.verifyPassword(matKhauCu, target.getMatKhauMaHoa())) {
            return "Mật khẩu hiện tại không chính xác!";
        }

        String hashMoi = MatKhauUtil.maHoa(matKhauMoi);
        boolean thanhCong = taiKhoanDAO.doiMatKhau(taiKhoanId, hashMoi);
        return thanhCong ? null : "Lỗi khi cập nhật mật khẩu vào CSDL!";
    }

    /**
     * Kiểm tra tiêu chuẩn mật khẩu mạnh:
     * - Tối thiểu 8 ký tự
     * - Ít nhất 1 chữ in hoa
     * - Ít nhất 1 chữ thường
     * - Ít nhất 1 chữ số
     * - Ít nhất 1 ký tự đặc biệt (@, #, $, %, !, ^, &, *, ...)
     */
    public String kiemTraMatKhauManh(String password) {
        if (password == null || password.length() < 8) {
            return "Mật khẩu phải có độ dài tối thiểu 8 ký tự!";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái in hoa (A-Z)!";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái in thường (a-z)!";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Mật khẩu phải chứa ít nhất 1 chữ số (0-9)!";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (@, !, #, $...)!";
        }
        return null;
    }
}
