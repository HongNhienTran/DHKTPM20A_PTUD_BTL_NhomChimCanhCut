package com.pengugarage.dao;

import com.pengugarage.model.entity.DichVu;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Dịch Vụ Kỹ Thuật (dich_vu)
 */
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
            throw new RuntimeException("Lỗi khi lấy danh sách dịch vụ: " + e.getMessage(), e);
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
            throw new RuntimeException("Lỗi khi lấy chi tiết dịch vụ theo ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    public DichVu layChiTietDichVuTheoMa(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM `dich_vu` WHERE `ma_dich_vu` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDichVu.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDichVu(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm dịch vụ theo mã: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean tonTaiMaDichVu(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM `dich_vu` WHERE `ma_dich_vu` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDichVu.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra mã dịch vụ: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean kiemTraTrungMa(String maDichVu, int loaiTruId) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM `dich_vu` WHERE `ma_dich_vu` = ? AND `id` != ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDichVu.trim());
            ps.setInt(2, loaiTruId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra trùng mã dịch vụ: " + e.getMessage(), e);
        }
        return false;
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    public boolean them(DichVu dv) {
        if (dv == null) {
            return false;
        }
        String sql = "INSERT INTO `dich_vu` " +
                "(`ma_dich_vu`, `ten_dich_vu`, `mo_ta`, `don_gia`, `thue_vat`, `trang_thai`) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dv.getMaDichVu());
            ps.setString(2, dv.getTenDichVu());
            ps.setString(3, dv.getMoTa());
            ps.setDouble(4, dv.getDonGia());
            ps.setDouble(5, dv.getThueVAT());
            ps.setBoolean(6, dv.isTrangThai());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        dv.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm dịch vụ: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    public boolean capNhat(DichVu dv) {
        if (dv == null) {
            return false;
        }
        String sql = "UPDATE `dich_vu` SET " +
                "`ten_dich_vu` = ?, " +
                "`mo_ta` = ?, " +
                "`don_gia` = ?, " +
                "`thue_vat` = ?, " +
                "`trang_thai` = ? " +
                "WHERE (`id` = ? AND `id` > 0) OR (`ma_dich_vu` = ?)";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setString(2, dv.getMoTa());
            ps.setDouble(3, dv.getDonGia());
            ps.setDouble(4, dv.getThueVAT());
            ps.setBoolean(5, dv.isTrangThai());
            ps.setInt(6, dv.getId());
            ps.setString(7, dv.getMaDichVu());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật dịch vụ: " + e.getMessage(), e);
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
}
