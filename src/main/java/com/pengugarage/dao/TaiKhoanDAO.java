package com.pengugarage.dao;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.entity.TaiKhoan;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object cho bảng `tai_khoan` liên kết với bảng `nhan_vien`.
 */
public class TaiKhoanDAO {

    private static final Logger LOGGER = Logger.getLogger(TaiKhoanDAO.class.getName());

    /**
     * Tìm tài khoản kèm thông tin nhân viên theo tên đăng nhập.
     *
     * @param tenDangNhap Tên đăng nhập (chính là mã nhân viên)
     * @return TaiKhoan nếu tìm thấy, null nếu không tìm thấy
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT tk.id AS tk_id, tk.nhan_vien_id, tk.ten_dang_nhap, tk.mat_khau_maHoa, " +
                     "       nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten, nv.so_dien_thoai, " +
                     "       nv.dia_chi, nv.chuc_vu, nv.trang_thai " +
                     "FROM `tai_khoan` tk " +
                     "JOIN `nhan_vien` nv ON tk.nhan_vien_id = nv.id " +
                     "WHERE tk.ten_dang_nhap = ?";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan taiKhoan = new TaiKhoan();
                    taiKhoan.setId(rs.getInt("tk_id"));
                    taiKhoan.setNhanVienId(rs.getInt("nhan_vien_id"));
                    taiKhoan.setTenDangNhap(rs.getString("ten_dang_nhap"));
                    taiKhoan.setMatKhauMaHoa(rs.getString("mat_khau_maHoa"));

                    NhanVien nv = new NhanVien();
                    nv.setId(rs.getInt("nv_id"));
                    nv.setMaNhanVien(rs.getString("ma_nhan_vien"));
                    nv.setHoTen(rs.getString("ho_ten"));
                    nv.setSoDienThoai(rs.getString("so_dien_thoai"));
                    nv.setDiaChi(rs.getString("dia_chi"));
                    
                    try {
                        nv.setChucVu(ChucVu.valueOf(rs.getString("chuc_vu")));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warning("Không thể parse ChucVu: " + rs.getString("chuc_vu"));
                    }
                    nv.setTrangThai(rs.getBoolean("trang_thai"));

                    taiKhoan.setNhanVien(nv);
                    return taiKhoan;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn tài khoản theo tên đăng nhập: " + tenDangNhap, e);
        }

        return null;
    }

    /**
     * Lấy toàn bộ danh sách tài khoản hệ thống kèm thông tin nhân viên
     */
    public List<TaiKhoan> layTatCa() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT tk.id AS tk_id, tk.nhan_vien_id, tk.ten_dang_nhap, tk.mat_khau_maHoa, " +
                     "       nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten, nv.so_dien_thoai, " +
                     "       nv.dia_chi, nv.chuc_vu, nv.trang_thai " +
                     "FROM `tai_khoan` tk " +
                     "JOIN `nhan_vien` nv ON tk.nhan_vien_id = nv.id " +
                     "ORDER BY tk.id ASC";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setId(rs.getInt("tk_id"));
                tk.setNhanVienId(rs.getInt("nhan_vien_id"));
                tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
                tk.setMatKhauMaHoa(rs.getString("mat_khau_maHoa"));

                NhanVien nv = new NhanVien();
                nv.setId(rs.getInt("nv_id"));
                nv.setMaNhanVien(rs.getString("ma_nhan_vien"));
                nv.setHoTen(rs.getString("ho_ten"));
                nv.setSoDienThoai(rs.getString("so_dien_thoai"));
                nv.setDiaChi(rs.getString("dia_chi"));
                try {
                    nv.setChucVu(ChucVu.valueOf(rs.getString("chuc_vu")));
                } catch (Exception e) {
                    nv.setChucVu(ChucVu.KY_THUAT_VIEN);
                }
                nv.setTrangThai(rs.getBoolean("trang_thai"));

                tk.setNhanVien(nv);
                list.add(tk);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách tài khoản", e);
        }
        return list;
    }

    /**
     * Tìm tài khoản theo ID nhân viên
     */
    public TaiKhoan timTheoNhanVienId(int nhanVienId) {
        String sql = "SELECT * FROM `tai_khoan` WHERE `nhan_vien_id` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nhanVienId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = new TaiKhoan();
                    tk.setId(rs.getInt("id"));
                    tk.setNhanVienId(rs.getInt("nhan_vien_id"));
                    tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
                    tk.setMatKhauMaHoa(rs.getString("mat_khau_maHoa"));
                    return tk;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm tài khoản theo nhân viên id: " + nhanVienId, e);
        }
        return null;
    }

    /**
     * Thêm mới tài khoản nhân viên
     */
    public boolean them(TaiKhoan tk) {
        String sql = "INSERT INTO `tai_khoan` (`nhan_vien_id`, `ten_dang_nhap`, `mat_khau_maHoa`) VALUES (?, ?, ?)";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, tk.getNhanVienId());
            ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhauMaHoa());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tk.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tạo tài khoản mới", e);
        }
        return false;
    }

    /**
     * Cập nhật mật khẩu tài khoản
     */
    public boolean doiMatKhau(int taiKhoanId, String matKhauMoiMaHoa) {
        String sql = "UPDATE `tai_khoan` SET `mat_khau_maHoa` = ? WHERE `id` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matKhauMoiMaHoa);
            ps.setInt(2, taiKhoanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đổi mật khẩu tài khoản: " + taiKhoanId, e);
        }
        return false;
    }
}
