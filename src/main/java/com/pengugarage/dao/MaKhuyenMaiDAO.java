package com.pengugarage.dao;

import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaKhuyenMaiDAO {

    public List<MaKhuyenMai> layTatCa() {
        List<MaKhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM `ma_khuyen_mai` ORDER BY `id` ASC";
        try(Connection conn = CoSoDuLieuUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMaKhuyenMai(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private MaKhuyenMai mapResultSetToMaKhuyenMai(ResultSet rs) throws SQLException {
        MaKhuyenMai maKhuyenMai = new MaKhuyenMai();
        maKhuyenMai.setId(rs.getInt("id"));
        maKhuyenMai.setMaKhuyenMai(rs.getString("ma_khuyen_mai"));

        maKhuyenMai.setLoaiGiam(rs.getString("loai_giam") != null ?
                LoaiGiamGia.valueOf(rs.getString("loai_giam")) : null);

        maKhuyenMai.setGiaTriGiam(rs.getDouble("gia_tri_giam"));
        maKhuyenMai.setGiaTriToiThieuApDung(rs.getDouble("gia_tri_toi_thieu_ap_dung"));
        maKhuyenMai.setNgayBatDauApDung(rs.getTimestamp("ngay_bat_dau_ap_dung") != null ? rs.getTimestamp("ngay_bat_dau_ap_dung").toLocalDateTime() : null);
        maKhuyenMai.setNgayHetHan(rs.getTimestamp("ngay_het_han") != null ? rs.getTimestamp("ngay_het_han").toLocalDateTime() : null);
        maKhuyenMai.setSoLuongTon(rs.getObject("so_luong_ton") != null ? rs.getInt("so_luong_ton") : null);
        maKhuyenMai.setTrangThai(rs.getBoolean("trang_thai"));
        return maKhuyenMai;
    }
}
