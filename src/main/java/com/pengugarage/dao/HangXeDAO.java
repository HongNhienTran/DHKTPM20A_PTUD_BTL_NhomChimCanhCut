package com.pengugarage.dao;

import com.pengugarage.model.entity.HangXe;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Hãng Xe (hang_xe).
 */
public class HangXeDAO {

    public List<HangXe> layTatCa() {
        List<HangXe> ds = new ArrayList<>();
        String sql = "SELECT * FROM hang_xe ORDER BY ten_hang ASC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HangXe hx = new HangXe();
                hx.setId(rs.getInt("id"));
                hx.setMaHangXe(rs.getString("ma_hang_xe"));
                hx.setTenHang(rs.getString("ten_hang"));
                hx.setQuocGia(rs.getString("quoc_gia"));
                ds.add(hx);
            }
        } catch (SQLException ignored) {
        }
        return ds;
    }

    public HangXe timTheoId(int id) {
        String sql = "SELECT * FROM hang_xe WHERE id = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HangXe hx = new HangXe();
                    hx.setId(rs.getInt("id"));
                    hx.setMaHangXe(rs.getString("ma_hang_xe"));
                    hx.setTenHang(rs.getString("ten_hang"));
                    hx.setQuocGia(rs.getString("quoc_gia"));
                    return hx;
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }
}

