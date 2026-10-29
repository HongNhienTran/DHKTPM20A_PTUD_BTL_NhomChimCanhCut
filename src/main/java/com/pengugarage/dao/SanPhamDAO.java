package com.pengugarage.dao;

import com.pengugarage.model.entity.DanhMucSanPham;
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
        String sql = """
            SELECT sp.*, 
                   dm.id AS dm_id, dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta AS dm_mo_ta, dm.trang_thai AS dm_trang_thai
            FROM san_pham sp
            LEFT JOIN danh_muc_san_pham dm ON sp.danh_muc_id = dm.id
            ORDER BY sp.id ASC
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
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

    /**
     * Lấy danh sách các đơn vị tính thực tế hiện có trong cơ sở dữ liệu
     */
    public List<String> layDanhSachDonViTinh() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT `don_vi_tinh` FROM `san_pham` WHERE `don_vi_tinh` IS NOT NULL AND TRIM(`don_vi_tinh`) != '' ORDER BY `don_vi_tinh` ASC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("don_vi_tinh").trim());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn danh sách đơn vị tính: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // TASK M02-T01: Hiển thị danh sách, hiển thị chi tiết sản phẩm, lọc sản phẩm trên giao diện
    // Người thực hiện: Nhiên
    // =========================================================================
    public SanPham layChiTietSanPhamTheoID(int id) {
        String sql = """
            SELECT sp.*, 
                   dm.id AS dm_id, dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta AS dm_mo_ta, dm.trang_thai AS dm_trang_thai
            FROM san_pham sp
            LEFT JOIN danh_muc_san_pham dm ON sp.danh_muc_id = dm.id
            WHERE sp.id = ?
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSanPham(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public SanPham layChiTietSanPhamTheoId(int id) {
        return layChiTietSanPhamTheoID(id);
    }

    // =========================================================================
    // TASK M02-T03: Nhập 1 thông tin sản phẩm và thêm, thêm bằng file import (exel)
    // Người thực hiện: Phong
    // =========================================================================
    public boolean kiemTraMaSanPhamTonTai(String maSanPham) {
        String sql = "SELECT 1 FROM san_pham WHERE ma_san_pham = ? LIMIT 1";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra mã sản phẩm: " + maSanPham, e);
        }
    }
    public boolean them(SanPham sp) {
        // TODO: Phong thực hiện chức năng nhập 1 thông tin sản phẩm và thêm
        String sql = """
        INSERT INTO san_pham
            (danh_muc_id, ma_san_pham, ten_san_pham, don_vi_tinh,
             so_luong_ton, so_luong_ton_toi_thieu, gia_nhap,
             don_gia_ban, thue_vat, trang_thai, duong_dan_anh)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ganThamSoThemSanPham(ps, sp);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm sản phẩm: " + sp.getMaSanPham(), e);
        }
    }

    public boolean themDanhSachSanPham(List<SanPham> danhSach) {
        // TODO: Phong thực hiện chức năng thêm danh sách sản phẩm bằng file import (exel)
        if (danhSach == null || danhSach.isEmpty()) {
            return false;
        }

        String sql = """
        INSERT INTO san_pham
            (danh_muc_id, ma_san_pham, ten_san_pham, don_vi_tinh,
             so_luong_ton, so_luong_ton_toi_thieu, gia_nhap,
             don_gia_ban, thue_vat, trang_thai, duong_dan_anh)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = CoSoDuLieuUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (SanPham sp : danhSach) {
                    ganThamSoThemSanPham(ps, sp);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi import danh sách sản phẩm", e);
        }
    }
    private void ganThamSoThemSanPham(PreparedStatement ps, SanPham sp) throws SQLException {
        ps.setInt(1, sp.getDanhMucId());
        ps.setString(2, sp.getMaSanPham());
        ps.setString(3, sp.getTenSanPham());
        ps.setString(4, sp.getDonViTinh());
        ps.setInt(5, sp.getSoLuongTon());
        ps.setInt(6, sp.getSoLuongTonToiThieu());
        ps.setDouble(7, sp.getGiaNhap());
        ps.setDouble(8, sp.getDonGiaBan());
        ps.setDouble(9, sp.getThueVAT());
        ps.setBoolean(10, sp.isTrangThai());
        ps.setString(11, sp.getDuongDanAnh());
    }

    // =========================================================================
    // TASK M02-T04: Hiển thị thông tin sản phẩm và cho phép chỉnh sửa
    // Người thực hiện: Tuấn
    // =========================================================================
    public boolean capNhat(SanPham sp) {
        // TODO: Tuấn thực hiện chức năng cập nhật thông tin sản phẩm
        return false;
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

        int danhMucId = rs.getInt("danh_muc_id");
        sanPham.setDanhMucId(danhMucId);

        // Lấy thông tin đối tượng Danh mục sản phẩm từ LEFT JOIN
        try {
            String tenDanhMuc = rs.getString("ten_danh_muc");
            if (tenDanhMuc != null) {
                DanhMucSanPham dm = new DanhMucSanPham();
                dm.setId(rs.getInt("dm_id"));
                dm.setMaDanhMuc(rs.getString("ma_danh_muc"));
                dm.setTenDanhMuc(tenDanhMuc);
                dm.setMoTa(rs.getString("dm_mo_ta"));
                dm.setTrangThai(rs.getBoolean("dm_trang_thai"));
                sanPham.setDanhMuc(dm);
            }
        } catch (SQLException ignored) {
            // Bỏ qua nếu câu query không có cột join
        }

        return sanPham;
    }
}
