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
        String sql = "INSERT INTO `dich_vu` " +
                "(`ma_dich_vu`, `ten_dich_vu`, `mo_ta`, `don_gia`, `thue_vat`, `trang_thai`) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getMaDichVu());
            ps.setString(2, dv.getTenDichVu());
            ps.setString(3, dv.getMoTa());
            ps.setDouble(4, dv.getDonGia());
            ps.setDouble(5, dv.getThueVAT());
            ps.setBoolean(6, dv.isTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    public boolean capNhat(DichVu dv) {
        String sql = "UPDATE `dich_vu` SET " +
                "`ten_dich_vu` = ?, " +
                "`mo_ta` = ?, " +
                "`don_gia` = ?, " +
                "`thue_vat` = ?, " +
                "`trang_thai` = ? " +
                "WHERE `id` = ?";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setString(2, dv.getMoTa());
            ps.setDouble(3, dv.getDonGia());
            ps.setDouble(4, dv.getThueVAT());
            ps.setBoolean(5, dv.isTrangThai());
            ps.setInt(6, dv.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public boolean tonTaiMaDichVu(String maDichVu) {
        String sql = "SELECT COUNT(*) FROM dich_vu WHERE ma_dich_vu = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
