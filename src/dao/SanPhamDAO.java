package dao;

import util.ConnectDB;
import entity.DanhMucSanPham;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public String layMaLonNhatTheoTienTo(String tienTo) {
        String sql = "SELECT MAX(ma_san_pham) FROM san_pham WHERE ma_san_pham LIKE ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã sản phẩm lớn nhất", e);
        }
    }

    public List<SanPham> layTatCa() {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "ORDER BY sp.ma_san_pham";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm", e);
        }
        return ds;
    }

    public List<SanPham> timTheoDanhMuc(String maDanhMuc) {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.ma_danh_muc = ? ORDER BY sp.ma_san_pham";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDanhMuc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm sản phẩm theo danh mục", e);
        }
        return ds;
    }

    public List<SanPham> layDanhSachBanChay(int gioiHan) {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "LEFT JOIN ( " +
                "    SELECT ma_san_pham, SUM(so_luong) AS tong_ban " +
                "    FROM chi_tiet_hoa_don " +
                "    WHERE ma_san_pham IS NOT NULL " +
                "    GROUP BY ma_san_pham " +
                ") ban ON sp.ma_san_pham = ban.ma_san_pham " +
                "WHERE sp.trang_thai = 1 " +
                "ORDER BY COALESCE(ban.tong_ban, 0) DESC, sp.so_luong_ton ASC " +
                "LIMIT ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, gioiHan <= 0 ? 30 : gioiHan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm bán chạy", e);
        }
        return ds;
    }

    public List<SanPham> layDanhSachSanPhamMoi(int gioiHan) {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.trang_thai = 1 " +
                "ORDER BY sp.ma_san_pham DESC " +
                "LIMIT ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, gioiHan <= 0 ? 30 : gioiHan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm mới", e);
        }
        return ds;
    }

    public SanPham timTheoMa(String maSanPham) {
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm sản phẩm theo mã: " + maSanPham, e);
        }
        return null;
    }

    public boolean them(SanPham sp) {
        String sql = "INSERT INTO san_pham (ma_san_pham, ten_san_pham, don_vi_tinh, so_luong_ton, " +
                "so_luong_ton_toi_thieu, gia_nhap, don_gia_ban, thue_vat, trang_thai, anh_san_pham, ma_danh_muc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSanPham());
            ps.setString(2, sp.getTenSanPham());
            ps.setString(3, sp.getDonViTinh());
            ps.setInt(4, sp.getSoLuongTon());
            ps.setInt(5, sp.getSoLuongTonToiThieu());
            ps.setDouble(6, sp.getGiaNhap());
            ps.setDouble(7, sp.getDonGiaBan());
            ps.setDouble(8, sp.getThueVAT());
            ps.setBoolean(9, sp.isTrangThai());
            ps.setString(10, sp.getAnhSanPham());
            ps.setString(11, sp.getDanhMucSanPham().getMaDanhMuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm sản phẩm", e);
        }
    }

    public boolean capNhat(SanPham sp) {
        String sql = "UPDATE san_pham SET ten_san_pham = ?, don_vi_tinh = ?, so_luong_ton = ?, " +
                "so_luong_ton_toi_thieu = ?, gia_nhap = ?, don_gia_ban = ?, thue_vat = ?, " +
                "trang_thai = ?, anh_san_pham = ?, ma_danh_muc = ? WHERE ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setString(2, sp.getDonViTinh());
            ps.setInt(3, sp.getSoLuongTon());
            ps.setInt(4, sp.getSoLuongTonToiThieu());
            ps.setDouble(5, sp.getGiaNhap());
            ps.setDouble(6, sp.getDonGiaBan());
            ps.setDouble(7, sp.getThueVAT());
            ps.setBoolean(8, sp.isTrangThai());
            ps.setString(9, sp.getAnhSanPham());
            ps.setString(10, sp.getDanhMucSanPham().getMaDanhMuc());
            ps.setString(11, sp.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật sản phẩm", e);
        }
    }

    public boolean capNhatSoLuongTon(Connection con, String maSanPham, int soLuongTonMoi) throws SQLException {
        String sql = "UPDATE san_pham SET so_luong_ton = ? WHERE ma_san_pham = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuongTonMoi);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatSoLuongTon(String maSanPham, int soLuongTonMoi) {
        try (Connection con = ConnectDB.ketNoi()) {
            return capNhatSoLuongTon(con, maSanPham, soLuongTonMoi);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật tồn kho sản phẩm", e);
        }
    }

    public boolean xoa(String maSanPham) {
        String sql = "DELETE FROM san_pham WHERE ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa sản phẩm", e);
        }
    }

    private SanPham mapRow(ResultSet rs) throws SQLException {
        DanhMucSanPham dm = new DanhMucSanPham(
                rs.getString("ma_danh_muc"),
                rs.getString("ten_danh_muc"),
                rs.getString("mo_ta"),
                rs.getBoolean("dm_trang_thai")
        );
        return new SanPham(
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
    }
}
