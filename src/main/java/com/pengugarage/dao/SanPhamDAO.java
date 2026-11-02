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
    public boolean them(SanPham sp) {
        // TODO: Phong thực hiện chức năng nhập 1 thông tin sản phẩm và thêm
        String sql = "Update `san_pham` set ten_san_pham =?,don_vi_tinh=?,so_luong_ton=?,so_luong_ton_toi_thieu=?,gia_nhap=?,don_gia_ban=?,thue_vat=?,trang_thai=?,duong_dan_anh=?,danh_muc_id=? WHERE ma_san_pham = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);)
              {
                  ps.setString(1, sp.getTenSanPham());
                  ps.setString(2, sp.getDonViTinh());
                  ps.setInt(3, sp.getSoLuongTon());
                  ps.setInt(4,sp.getSoLuongTonToiThieu());
                  ps.setDouble(5,sp.getGiaNhap());
                  ps.setDouble(6,sp.getDonGiaBan());
                  ps.setDouble(7,sp.getThueVAT());
                  ps.setBoolean(8,sp.isTrangThai());
                  ps.setString(9,sp.getDuongDanAnh());
                  ps.setInt(10,sp.getDanhMucId());
                  ps.setString(11,sp.getMaSanPham());
                  int n = ps.executeUpdate();
                  if(n>0){
                      return true;
                  }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean themDanhSachSanPham(List<SanPham> danhSach) {
        // TODO: Phong thực hiện chức năng thêm danh sách sản phẩm bằng file import (exel)
        return false;
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
