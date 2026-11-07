package dao;

import util.ConnectDB;
import entity.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    /**
     * Lấy mã khách hàng đăng ký lớn nhất (bỏ qua mã mặc định KH000000000 của khách vãng lai).
     */
    public String layMaLonNhat() {
        String sql = "SELECT MAX(ma_khach_hang) FROM khach_hang WHERE ma_khach_hang <> ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, KhachHang.MA_KHACH_VANG_LAI);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã khách hàng lớn nhất", e);
        }
    }

    public KhachHang timKhachVangLai() {
        return timTheoMa(KhachHang.MA_KHACH_VANG_LAI);
    }

    public List<KhachHang> layTatCa() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh FROM khach_hang ORDER BY ma_khach_hang";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách khách hàng", e);
        }
        return ds;
    }

    public List<KhachHang> layDanhSachKhachDangKy() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh " +
                "FROM khach_hang WHERE ma_khach_hang <> ? ORDER BY ma_khach_hang";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, KhachHang.MA_KHACH_VANG_LAI);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách khách hàng đăng ký", e);
        }
        return ds;
    }

    public KhachHang timTheoMa(String maKhachHang) {
        String sql = "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh FROM khach_hang WHERE ma_khach_hang = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm khách hàng theo mã: " + maKhachHang, e);
        }
        return null;
    }

    public KhachHang timTheoSoDienThoai(String soDienThoai) {
        String sql = "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh FROM khach_hang WHERE so_dien_thoai = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soDienThoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm khách hàng theo số điện thoại: " + soDienThoai, e);
        }
        return null;
    }

    public boolean them(KhachHang kh) {
        String sql = "INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getGioiTinh());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm khách hàng", e);
        }
    }

    public boolean capNhat(KhachHang kh) {
        String sql = "UPDATE khach_hang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, gioi_tinh = ? WHERE ma_khach_hang = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getDiaChi());
            ps.setString(4, kh.getGioiTinh());
            ps.setString(5, kh.getMaKhachHang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật khách hàng", e);
        }
    }

    public boolean xoa(String maKhachHang) {
        String sql = "DELETE FROM khach_hang WHERE ma_khach_hang = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa khách hàng", e);
        }
    }

    private KhachHang mapRow(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("ma_khach_hang"),
                rs.getString("ho_ten"),
                rs.getString("so_dien_thoai"),
                rs.getString("dia_chi"),
                rs.getString("gioi_tinh")
        );
    }
}
