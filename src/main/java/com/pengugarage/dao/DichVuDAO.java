package com.pengugarage.dao;

import com.pengugarage.model.entity.DichVu;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    // =========================================================================
    // TASK M03-T01: Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện
    // Người thực hiện: Nhiên
    // =========================================================================
    public List<DichVu> layTatCa() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM `dich_vu` ORDER BY `id` ASC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDichVu(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public DichVu layChiTietDichVuTheoID(int id) {
        String sql = "SELECT * FROM `dich_vu` WHERE `id` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDichVu(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    public boolean them(DichVu dv) {
        // TODO: Tân thực hiện chức năng nhập thông tin dịch vụ và thêm
        return false;
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    public boolean capNhat(DichVu dv) {
        // TODO: Tân thực hiện chức năng cập nhật thông tin dịch vụ
        return false;
    }

    private DichVu mapResultSetToDichVu(ResultSet rs) throws SQLException {
        DichVu dichVu = new DichVu();
        dichVu.setId(rs.getInt("id"));
        dichVu.setMaDichVu(rs.getString("ma_dich_vu"));
        dichVu.setTenDichVu(rs.getString("ten_dich_vu"));
        dichVu.setMoTa(rs.getString("mo_ta"));
        dichVu.setDonGia(rs.getDouble("don_gia"));
        dichVu.setThueVAT(rs.getDouble("thue_vat"));
        dichVu.setTrangThai(rs.getBoolean("trang_thai"));
        return dichVu;
    }
}
