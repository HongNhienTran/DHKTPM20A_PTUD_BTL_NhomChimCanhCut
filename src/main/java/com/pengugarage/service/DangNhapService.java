package com.pengugarage.service;

import com.pengugarage.dao.TaiKhoanDAO;
import com.pengugarage.model.entity.TaiKhoan;
import com.pengugarage.util.MatKhauUtil;
import com.pengugarage.util.PhienDangNhapUtil;

/**
 * Service xử lý nghiệp vụ xác thực đăng nhập và phiên làm việc của nhân viên.
 */
public class DangNhapService {

    private final TaiKhoanDAO taiKhoanDAO;

    public DangNhapService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public DangNhapService(TaiKhoanDAO taiKhoanDAO) {
        this.taiKhoanDAO = taiKhoanDAO;
    }

    /**
     * Đối tượng chứa kết quả đăng nhập.
     */
    public static class KetQuaDangNhap {
        private final boolean thanhCong;
        private final String thongBao;
        private final TaiKhoan taiKhoan;

        public KetQuaDangNhap(boolean thanhCong, String thongBao, TaiKhoan taiKhoan) {
            this.thanhCong = thanhCong;
            this.thongBao = thongBao;
            this.taiKhoan = taiKhoan;
        }

        public boolean isThanhCong() {
            return thanhCong;
        }

        public String getThongBao() {
            return thongBao;
        }

        public TaiKhoan getTaiKhoan() {
            return taiKhoan;
        }
    }

    /**
     * Xác thực thông tin đăng nhập từ cơ sở dữ liệu.
     *
     * @param tenDangNhap Tên đăng nhập / Mã nhân viên
     * @param matKhau Mật khẩu nguyên bản người dùng nhập
     * @return KetQuaDangNhap
     */
    public KetQuaDangNhap dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return new KetQuaDangNhap(false, "Vui lòng nhập tên đăng nhập hoặc mã nhân viên", null);
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            return new KetQuaDangNhap(false, "Vui lòng nhập mật khẩu tài khoản", null);
        }

        String username = tenDangNhap.trim();
        TaiKhoan taiKhoan = taiKhoanDAO.findByTenDangNhap(username);

        if (taiKhoan == null) {
            return new KetQuaDangNhap(false, "Tài khoản không tồn tại trong hệ thống", null);
        }

        if (taiKhoan.getNhanVien() != null && !taiKhoan.getNhanVien().isTrangThai()) {
            return new KetQuaDangNhap(false, "Tài khoản nhân viên này đã bị ngừng kích hoạt", null);
        }

        boolean passwordMatches = MatKhauUtil.verifyPassword(matKhau, taiKhoan.getMatKhauMaHoa());
        if (!passwordMatches) {
            return new KetQuaDangNhap(false, "Mật khẩu không chính xác. Vui lòng thử lại!", null);
        }

        // Đăng nhập thành công, lưu vào phiên làm việc
        PhienDangNhapUtil.getInstance().login(taiKhoan, taiKhoan.getNhanVien());

        return new KetQuaDangNhap(true, "Đăng nhập thành công", taiKhoan);
    }

    /**
     * Đăng xuất phiên làm việc hiện tại
     */
    public void dangXuat() {
        PhienDangNhapUtil.getInstance().logout();
    }
}
