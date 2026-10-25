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

    // =========================================================================
    // TASK M03-T01: Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện
    // Người thực hiện: Nhiên
    // =========================================================================
    public List<MaKhuyenMai> layTatCa() {
        List<MaKhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM `ma_khuyen_mai` ORDER BY `id` ASC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMaKhuyenMai(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn danh sách mã khuyến mãi: " + e.getMessage(), e);
        }
        return list;
    }

    public MaKhuyenMai layChiTietMaKhuyenMaiTheoID(int id) {
        String sql = "SELECT * FROM `ma_khuyen_mai` WHERE `id` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMaKhuyenMai(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn chi tiết mã khuyến mãi: " + e.getMessage(), e);
        }
        return null;
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin mã khuyến mãi và thêm
    // Người thực hiện: Tuấn
    // =========================================================================
    public boolean them(MaKhuyenMai km) {
        // TODO: Tuấn thực hiện chức năng nhập thông tin mã khuyến mãi và thêm
        return false;
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

