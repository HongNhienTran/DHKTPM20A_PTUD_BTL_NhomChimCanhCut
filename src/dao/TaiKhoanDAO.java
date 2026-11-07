package dao;

import util.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.ChuyenMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    private static final String SELECT_JOIN =
            "SELECT tk.ten_dang_nhap, tk.mat_khau_ma_hoa, tk.trang_thai AS tk_trang_thai, " +
                    "nv.ma_nhan_vien, nv.ho_ten, nv.so_dien_thoai, nv.dia_chi, nv.chuc_vu, " +
                    "nv.trang_thai AS nv_trang_thai, nv.chuyen_mon " +
                    "FROM tai_khoan tk " +
                    "JOIN nhan_vien nv ON tk.ma_nhan_vien = nv.ma_nhan_vien ";

    public TaiKhoan timTheoTenDangNhapVaMatKhau(String tenDangNhap, String matKhauMaHoa) {
        String sql = SELECT_JOIN + "WHERE tk.ten_dang_nhap = ? AND tk.mat_khau_ma_hoa = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhauMaHoa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn tài khoản đăng nhập", e);
        }
        return null;
    }

    public List<TaiKhoan> layTatCa() {
        List<TaiKhoan> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY tk.ten_dang_nhap";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách tài khoản", e);
        }
        return ds;
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        String sql = SELECT_JOIN + "WHERE tk.ten_dang_nhap = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm tài khoản: " + tenDangNhap, e);
        }
        return null;
    }

    public boolean them(Connection con, TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, trang_thai, ma_nhan_vien) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhauMaHoa());
            ps.setBoolean(3, tk.isTrangThai());
            ps.setString(4, tk.getMaNhanVien().getMaNhanVien());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean them(TaiKhoan tk) {
        try (Connection con = ConnectDB.ketNoi()) {
            return them(con, tk);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm tài khoản", e);
        }
    }

    public boolean capNhat(Connection con, TaiKhoan tk) throws SQLException {
        String sql = "UPDATE tai_khoan SET mat_khau_ma_hoa = ?, trang_thai = ? WHERE ten_dang_nhap = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getMatKhauMaHoa());
            ps.setBoolean(2, tk.isTrangThai());
            ps.setString(3, tk.getTenDangNhap());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        try (Connection con = ConnectDB.ketNoi()) {
            return capNhat(con, tk);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật tài khoản", e);
        }
    }

    public boolean xoa(String tenDangNhap) {
        String sql = "DELETE FROM tai_khoan WHERE ten_dang_nhap = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa tài khoản", e);
        }
    }

    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        String cmStr = rs.getString("chuyen_mon");
        ChuyenMon chuyenMon = cmStr != null ? ChuyenMon.valueOf(cmStr) : null;
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getString("ma_nhan_vien"));
        nv.setHoTen(rs.getString("ho_ten"));
        nv.setSoDienThoai(rs.getString("so_dien_thoai"));
        nv.setDiaChi(rs.getString("dia_chi"));
        nv.setChucVu(ChucVu.valueOf(rs.getString("chuc_vu")));
        nv.setTrangThai(rs.getBoolean("nv_trang_thai"));
        nv.setChuyenMon(chuyenMon);

        return new TaiKhoan(
                rs.getString("ten_dang_nhap"),
                rs.getString("mat_khau_ma_hoa"),
                rs.getBoolean("tk_trang_thai"),
                nv
        );
    }
}
