package com.pengugarage.dao;

import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DanhMucDAO {

    public List<DanhMucSanPham> layTatCa() {
        List<DanhMucSanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM `danh_muc_san_pham` ORDER BY `id` ASC";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDanhMuc(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private DanhMucSanPham mapResultSetToDanhMuc(ResultSet rs) throws SQLException {
        DanhMucSanPham danhMuc = new DanhMucSanPham();
        danhMuc.setId(rs.getInt("id"));
        danhMuc.setMaDanhMuc(rs.getString("ma_danh_muc"));
        danhMuc.setTenDanhMuc(rs.getString("ten_danh_muc"));
        danhMuc.setMoTa(rs.getString("mo_ta"));
        danhMuc.setTrangThai(rs.getBoolean("trang_thai"));
        return danhMuc;
    }
}
