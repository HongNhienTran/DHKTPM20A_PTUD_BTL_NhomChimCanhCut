package com.pengugarage.dao;

import com.pengugarage.model.entity.SanPham;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPham> layTatCa() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM san_pham ORDER BY id ASC";
        try(Connection conn = CoSoDuLieuUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSanPham(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private SanPham mapResultSetToSanPham(ResultSet rs) throws SQLException {
        SanPham sanPham = new SanPham();
        sanPham.setId(rs.getInt("id"));
        sanPham.setMaSanPham(rs.getString("ma_san_pham"));
        sanPham.setTenSanPham(rs.getString("ten_san_pham"));
        sanPham.setDonViTinh(rs.getString("don_vi_tinh"));
        sanPham.setSoLuongTon(rs.getInt("so_luong_ton"));
        sanPham.setSoLuongTonToiThieu(rs.getInt("so_luong_ton_toi_thieu"));
        sanPham.setGiaNhap(rs.getDouble("gia_nhap"));
        sanPham.setDonGiaBan(rs.getDouble("don_gia_ban"));
        sanPham.setThueVAT(rs.getDouble("thue_vat"));
        sanPham.setTrangThai(rs.getBoolean("trang_thai"));
        sanPham.setDuongDanAnh(rs.getString("duong_dan_anh"));
        return sanPham;
    }
}
