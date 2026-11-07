package dao;

import util.ConnectDB;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DanhMucSanPham;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDungPhuTungDAO {

    public List<ChiTietDungPhuTung> layTheoChiTietBaoGia(Connection con, ChiTietBaoGia chiTietBaoGia)
            throws SQLException {
        List<ChiTietDungPhuTung> ds = new ArrayList<>();
        String sql = "SELECT ctdpt.so_luong, ctdpt.don_gia, ctdpt.da_tru_kho, ctdpt.ghi_chu, " +
                "sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM chi_tiet_dung_phu_tung ctdpt " +
                "JOIN san_pham sp ON ctdpt.ma_san_pham = sp.ma_san_pham " +
                "JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE ctdpt.ma_bao_gia = ? AND ctdpt.ma_dich_vu = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, chiTietBaoGia.getPhieuBaoGia().getMaBaoGia());
            ps.setString(2, chiTietBaoGia.getDichVu().getMaDichVu());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DanhMucSanPham dm = new DanhMucSanPham(
                            rs.getString("ma_danh_muc"),
                            rs.getString("ten_danh_muc"),
                            rs.getString("mo_ta"),
                            rs.getBoolean("dm_trang_thai")
                    );
                    SanPham sp = new SanPham(
                            rs.getString("ma_san_pham"),
                            rs.getString("ten_san_pham"),
                            rs.getString("don_vi_tinh"),
                            rs.getInt("so_luong_ton"),
                            rs.getInt("so_luong_ton_toi_thieu"),
                            rs.getDouble("gia_nhap"),
                            rs.getDouble("don_gia_ban"),
                            rs.getDouble("thue_vat"),
                            rs.getBoolean("sp_trang_thai"),
                            rs.getString("anh_san_pham"),
                            dm
                    );
                    ds.add(new ChiTietDungPhuTung(
                            rs.getInt("so_luong"),
                            rs.getDouble("don_gia"),
                            rs.getBoolean("da_tru_kho"),
                            rs.getString("ghi_chu"),
                            chiTietBaoGia,
                            sp
                    ));
                }
            }
        }
        return ds;
    }

    public List<ChiTietDungPhuTung> layTheoChiTietBaoGia(ChiTietBaoGia chiTietBaoGia) {
        try (Connection con = ConnectDB.ketNoi()) {
            return layTheoChiTietBaoGia(con, chiTietBaoGia);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách phụ tùng của chi tiết báo giá", e);
        }
    }

    public boolean them(Connection con, ChiTietDungPhuTung pt) throws SQLException {
        String sql = "INSERT INTO chi_tiet_dung_phu_tung " +
                "(ma_bao_gia, ma_dich_vu, ma_san_pham, so_luong, don_gia, da_tru_kho, ghi_chu) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pt.getChiTietBaoGia().getPhieuBaoGia().getMaBaoGia());
            ps.setString(2, pt.getChiTietBaoGia().getDichVu().getMaDichVu());
            ps.setString(3, pt.getSanPham().getMaSanPham());
            ps.setInt(4, pt.getSoLuong());
            ps.setDouble(5, pt.getDonGia());
            ps.setBoolean(6, pt.isDaTruKho());
            ps.setString(7, pt.getGhiChu());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean them(ChiTietDungPhuTung pt) {
        try (Connection con = ConnectDB.ketNoi()) {
            return them(con, pt);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm chi tiết dùng phụ tùng", e);
        }
    }

    public boolean capNhat(Connection con, ChiTietDungPhuTung pt) throws SQLException {
        String sql = "UPDATE chi_tiet_dung_phu_tung SET so_luong = ?, don_gia = ?, da_tru_kho = ?, ghi_chu = ? " +
                "WHERE ma_bao_gia = ? AND ma_dich_vu = ? AND ma_san_pham = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pt.getSoLuong());
            ps.setDouble(2, pt.getDonGia());
            ps.setBoolean(3, pt.isDaTruKho());
            ps.setString(4, pt.getGhiChu());
            ps.setString(5, pt.getChiTietBaoGia().getPhieuBaoGia().getMaBaoGia());
            ps.setString(6, pt.getChiTietBaoGia().getDichVu().getMaDichVu());
            ps.setString(7, pt.getSanPham().getMaSanPham());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhat(ChiTietDungPhuTung pt) {
        try (Connection con = ConnectDB.ketNoi()) {
            return capNhat(con, pt);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật chi tiết dùng phụ tùng", e);
        }
    }

    public boolean xoa(String maBaoGia, String maDichVu, String maSanPham) {
        String sql = "DELETE FROM chi_tiet_dung_phu_tung WHERE ma_bao_gia = ? AND ma_dich_vu = ? AND ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBaoGia);
            ps.setString(2, maDichVu);
            ps.setString(3, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa chi tiết dùng phụ tùng", e);
        }
    }
}

