package dao;

import util.ConnectDB;
import entity.DanhMucSanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DanhMucSanPhamDAO {

    public String layMaLonNhat() {
        String sql = "SELECT MAX(ma_danh_muc) FROM danh_muc_san_pham";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã danh mục lớn nhất", e);
        }
    }

    public List<DanhMucSanPham> layTatCa() {
        List<DanhMucSanPham> ds = new ArrayList<>();
        String sql = "SELECT ma_danh_muc, ten_danh_muc, mo_ta, trang_thai FROM danh_muc_san_pham ORDER BY ma_danh_muc";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách danh mục sản phẩm", e);
        }
        return ds;
    }

    public DanhMucSanPham timTheoMa(String maDanhMuc) {
        String sql = "SELECT ma_danh_muc, ten_danh_muc, mo_ta, trang_thai FROM danh_muc_san_pham WHERE ma_danh_muc = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDanhMuc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm danh mục theo mã: " + maDanhMuc, e);
        }
        return null;
    }

    public boolean them(DanhMucSanPham dm) {
        String sql = "INSERT INTO danh_muc_san_pham (ma_danh_muc, ten_danh_muc, mo_ta, trang_thai) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dm.getMaDanhMuc());
            ps.setString(2, dm.getTenDanhMuc());
            ps.setString(3, dm.getMoTa());
            ps.setBoolean(4, dm.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm danh mục sản phẩm", e);
        }
    }

    public boolean capNhat(DanhMucSanPham dm) {
        String sql = "UPDATE danh_muc_san_pham SET ten_danh_muc = ?, mo_ta = ?, trang_thai = ? WHERE ma_danh_muc = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dm.getTenDanhMuc());
            ps.setString(2, dm.getMoTa());
            ps.setBoolean(3, dm.isTrangThai());
            ps.setString(4, dm.getMaDanhMuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật danh mục sản phẩm", e);
        }
    }

    public boolean xoa(String maDanhMuc) {
        String sql = "DELETE FROM danh_muc_san_pham WHERE ma_danh_muc = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDanhMuc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa danh mục sản phẩm", e);
        }
    }

    private DanhMucSanPham mapRow(ResultSet rs) throws SQLException {
        return new DanhMucSanPham(
                rs.getString("ma_danh_muc"),
                rs.getString("ten_danh_muc"),
                rs.getString("mo_ta"),
                rs.getBoolean("trang_thai")
        );
    }
}
