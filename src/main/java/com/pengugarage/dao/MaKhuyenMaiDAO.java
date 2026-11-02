package com.pengugarage.dao;

import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaKhuyenMaiDAO {

    // =========================================================================
    // TASK M03-T01: Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện
    // =========================================================================
    public List<MaKhuyenMai> layTatCa() {
        List<MaKhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM `ma_khuyen_mai` ORDER BY `id` DESC";
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
    // =========================================================================
    public boolean them(MaKhuyenMai km) {
        if (km == null) {
            return false;
        }
        String sql = """
            INSERT INTO `ma_khuyen_mai`
                (ma_khuyen_mai, loai_giam, gia_tri_giam, gia_tri_toi_thieu_ap_dung,
                 ngay_bat_dau_ap_dung, ngay_het_han, so_luong_ton, trang_thai)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getLoaiGiam() != null ? km.getLoaiGiam().name() : LoaiGiamGia.PHAN_TRAM.name());
            ps.setDouble(3, km.getGiaTriGiam());
            ps.setDouble(4, km.getGiaTriToiThieuApDung());

            if (km.getNgayBatDauApDung() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(km.getNgayBatDauApDung()));
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }

            if (km.getNgayHetHan() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(km.getNgayHetHan()));
            } else {
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis() + 30L * 24 * 3600 * 1000));
            }

            if (km.getSoLuongTon() != null) {
                ps.setInt(7, km.getSoLuongTon());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setBoolean(8, km.isTrangThai());

            int m = ps.executeUpdate();
            if (m > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        km.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    // =========================================================================
    // Cập nhật thông tin mã khuyến mãi
    // =========================================================================
    public boolean capNhat(MaKhuyenMai km) {
        if (km == null) {
            return false;
        }
        String sql = """
            UPDATE `ma_khuyen_mai`
            SET ma_khuyen_mai = ?, loai_giam = ?, gia_tri_giam = ?, gia_tri_toi_thieu_ap_dung = ?,
                ngay_bat_dau_ap_dung = ?, ngay_het_han = ?, so_luong_ton = ?, trang_thai = ?
            WHERE (id = ? AND id > 0) OR ma_khuyen_mai = ?
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getLoaiGiam() != null ? km.getLoaiGiam().name() : LoaiGiamGia.PHAN_TRAM.name());
            ps.setDouble(3, km.getGiaTriGiam());
            ps.setDouble(4, km.getGiaTriToiThieuApDung());

            if (km.getNgayBatDauApDung() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(km.getNgayBatDauApDung()));
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }

            if (km.getNgayHetHan() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(km.getNgayHetHan()));
            } else {
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis() + 30L * 24 * 3600 * 1000));
            }

            if (km.getSoLuongTon() != null) {
                ps.setInt(7, km.getSoLuongTon());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setBoolean(8, km.isTrangThai());
            ps.setInt(9, km.getId());
            ps.setString(10, km.getMaKhuyenMai());

            int m = ps.executeUpdate();
            return m > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean kiemTraTrungMa(String maKhuyenMai, int loaiTruId) {
        if (maKhuyenMai == null || maKhuyenMai.isBlank()) {
            return false;
        }
        String sql = "SELECT 1 FROM `ma_khuyen_mai` WHERE ma_khuyen_mai = ? AND id != ? LIMIT 1";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai.trim());
            ps.setInt(2, loaiTruId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean kiemTraTrungMa(String maKhuyenMai) {
        return kiemTraTrungMa(maKhuyenMai, 0);
    }

    private MaKhuyenMai mapResultSetToMaKhuyenMai(ResultSet rs) throws SQLException {
        MaKhuyenMai maKhuyenMai = new MaKhuyenMai();
        maKhuyenMai.setId(rs.getInt("id"));
        maKhuyenMai.setMaKhuyenMai(rs.getString("ma_khuyen_mai"));

        String loaiStr = rs.getString("loai_giam");
        if (loaiStr != null) {
            try {
                maKhuyenMai.setLoaiGiam(LoaiGiamGia.valueOf(loaiStr));
            } catch (IllegalArgumentException e) {
                maKhuyenMai.setLoaiGiam(LoaiGiamGia.PHAN_TRAM);
            }
        }

        maKhuyenMai.setGiaTriGiam(rs.getDouble("gia_tri_giam"));
        maKhuyenMai.setGiaTriToiThieuApDung(rs.getDouble("gia_tri_toi_thieu_ap_dung"));
        maKhuyenMai.setNgayBatDauApDung(rs.getTimestamp("ngay_bat_dau_ap_dung") != null ? rs.getTimestamp("ngay_bat_dau_ap_dung").toLocalDateTime() : null);
        maKhuyenMai.setNgayHetHan(rs.getTimestamp("ngay_het_han") != null ? rs.getTimestamp("ngay_het_han").toLocalDateTime() : null);
        maKhuyenMai.setSoLuongTon(rs.getObject("so_luong_ton") != null ? rs.getInt("so_luong_ton") : null);
        maKhuyenMai.setTrangThai(rs.getBoolean("trang_thai"));
        return maKhuyenMai;
    }
}
