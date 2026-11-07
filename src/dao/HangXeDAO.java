package dao;

import util.ConnectDB;
import entity.HangXe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HangXeDAO {

    public String layMaLonNhat() {
        String sql = "SELECT MAX(ma_hang_xe) FROM hang_xe";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã hãng xe lớn nhất", e);
        }
    }

    public List<HangXe> layTatCa() {
        List<HangXe> ds = new ArrayList<>();
        String sql = "SELECT ma_hang_xe, ten_hang, quoc_gia FROM hang_xe ORDER BY ma_hang_xe";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách hãng xe", e);
        }
        return ds;
    }

    public HangXe timTheoMa(String maHangXe) {
        String sql = "SELECT ma_hang_xe, ten_hang, quoc_gia FROM hang_xe WHERE ma_hang_xe = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHangXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm hãng xe theo mã: " + maHangXe, e);
        }
        return null;
    }

    public boolean them(HangXe hangXe) {
        String sql = "INSERT INTO hang_xe (ma_hang_xe, ten_hang, quoc_gia) VALUES (?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hangXe.getMaHangXe());
            ps.setString(2, hangXe.getTenHang());
            ps.setString(3, hangXe.getQuocGia());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm hãng xe", e);
        }
    }

    public boolean capNhat(HangXe hangXe) {
        String sql = "UPDATE hang_xe SET ten_hang = ?, quoc_gia = ? WHERE ma_hang_xe = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hangXe.getTenHang());
            ps.setString(2, hangXe.getQuocGia());
            ps.setString(3, hangXe.getMaHangXe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật hãng xe", e);
        }
    }

    public boolean xoa(String maHangXe) {
        String sql = "DELETE FROM hang_xe WHERE ma_hang_xe = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHangXe);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa hãng xe", e);
        }
    }

    private HangXe mapRow(ResultSet rs) throws SQLException {
        return new HangXe(
                rs.getString("ma_hang_xe"),
                rs.getString("ten_hang"),
                rs.getString("quoc_gia")
        );
    }
}
