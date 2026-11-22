package dao;

import util.ConnectDB;
import entity.MaKhuyenMai;
import enums.LoaiGiamGia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MaKhuyenMaiDAO {

    /**
     * Giữ lại phương thức cũ để tương thích ngược.
     * Tại màn hình Quản lý mã khuyến mãi đã thay thế bằng {@link #layDanhSachPhanTrang}.
     */
    public List<MaKhuyenMai> layTatCa() {
        List<MaKhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT ma_khuyen_mai, loai_giam, gia_tri_giam, gia_tri_toi_thieu_ap_dung, " +
                "ngay_bat_dau_ap_dung, ngay_het_han, so_luong_ton, trang_thai " +
                "FROM ma_khuyen_mai ORDER BY ngay_bat_dau_ap_dung DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách mã khuyến mãi", e);
        }
        return ds;
    }

    public List<MaKhuyenMai> layDanhSachPhanTrang(String tuKhoa, LoaiGiamGia loaiGiam,
                                                   int chiSoTrangThai, int offset, int limit) {
        List<MaKhuyenMai> ds = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienLoc(tuKhoa, loaiGiam, chiSoTrangThai, params);
        String sql = "SELECT ma_khuyen_mai, loai_giam, gia_tri_giam, gia_tri_toi_thieu_ap_dung, " +
                "ngay_bat_dau_ap_dung, ngay_het_han, so_luong_ton, trang_thai " +
                "FROM ma_khuyen_mai " + where +
                " ORDER BY ngay_bat_dau_ap_dung DESC, ma_khuyen_mai ASC LIMIT ? OFFSET ?";
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách mã khuyến mãi phân trang", e);
        }
        return ds;
    }

    public int demTheoDieuKien(String tuKhoa, LoaiGiamGia loaiGiam, int chiSoTrangThai) {
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienLoc(tuKhoa, loaiGiam, chiSoTrangThai, params);
        String sql = "SELECT COUNT(*) FROM ma_khuyen_mai " + where;
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm số lượng mã khuyến mãi", e);
        }
    }

    private String xayDungDieuKienLoc(String tuKhoa, LoaiGiamGia loaiGiam, int chiSoTrangThai, List<Object> params) {
        StringBuilder where = new StringBuilder("WHERE 1=1");
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            where.append(" AND LOWER(ma_khuyen_mai) LIKE ?");
            params.add("%" + tuKhoa.trim().toLowerCase() + "%");
        }
        if (loaiGiam != null) {
            where.append(" AND loai_giam = ?");
            params.add(loaiGiam.name());
        }
        if (chiSoTrangThai == 1) {
            where.append(" AND trang_thai = 1 AND (ngay_het_han IS NULL OR ngay_het_han >= NOW())");
        } else if (chiSoTrangThai == 2) {
            where.append(" AND trang_thai = 0");
        } else if (chiSoTrangThai == 3) {
            where.append(" AND ngay_het_han < NOW()");
        }
        return where.toString();
    }

    private void ganThamSo(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object val = params.get(i);
            if (val instanceof String s) {
                ps.setString(i + 1, s);
            } else if (val instanceof Integer n) {
                ps.setInt(i + 1, n);
            }
        }
    }

    public MaKhuyenMai timTheoMa(String maKhuyenMai) {
        String sql = "SELECT ma_khuyen_mai, loai_giam, gia_tri_giam, gia_tri_toi_thieu_ap_dung, " +
                "ngay_bat_dau_ap_dung, ngay_het_han, so_luong_ton, trang_thai " +
                "FROM ma_khuyen_mai WHERE ma_khuyen_mai = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm mã khuyến mãi: " + maKhuyenMai, e);
        }
        return null;
    }

    public boolean them(MaKhuyenMai km) {
        String sql = "INSERT INTO ma_khuyen_mai (ma_khuyen_mai, loai_giam, gia_tri_giam, " +
                "gia_tri_toi_thieu_ap_dung, ngay_bat_dau_ap_dung, ngay_het_han, so_luong_ton, trang_thai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getLoaiGiam().name());
            ps.setDouble(3, km.getGiaTriGiam());
            ps.setDouble(4, km.getGiaTriToiThieuApDung());
            ps.setTimestamp(5, Timestamp.valueOf(km.getNgayBatDauApDung()));
            ps.setTimestamp(6, Timestamp.valueOf(km.getNgayHetHan()));
            if (km.getSoLuongTon() != null) {
                ps.setInt(7, km.getSoLuongTon());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setBoolean(8, km.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm mã khuyến mãi", e);
        }
    }

    public boolean capNhat(MaKhuyenMai km) {
        String sql = "UPDATE ma_khuyen_mai SET loai_giam = ?, gia_tri_giam = ?, " +
                "gia_tri_toi_thieu_ap_dung = ?, ngay_bat_dau_ap_dung = ?, ngay_het_han = ?, " +
                "so_luong_ton = ?, trang_thai = ? WHERE ma_khuyen_mai = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, km.getLoaiGiam().name());
            ps.setDouble(2, km.getGiaTriGiam());
            ps.setDouble(3, km.getGiaTriToiThieuApDung());
            ps.setTimestamp(4, Timestamp.valueOf(km.getNgayBatDauApDung()));
            ps.setTimestamp(5, Timestamp.valueOf(km.getNgayHetHan()));
            if (km.getSoLuongTon() != null) {
                ps.setInt(6, km.getSoLuongTon());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setBoolean(7, km.isTrangThai());
            ps.setString(8, km.getMaKhuyenMai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật mã khuyến mãi", e);
        }
    }

    public boolean capNhatSoLuongTon(Connection con, String maKhuyenMai, Integer soLuongTonMoi) throws SQLException {
        String sql = "UPDATE ma_khuyen_mai SET so_luong_ton = ? WHERE ma_khuyen_mai = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (soLuongTonMoi != null) {
                ps.setInt(1, soLuongTonMoi);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, maKhuyenMai);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean xoa(String maKhuyenMai) {
        String sql = "DELETE FROM ma_khuyen_mai WHERE ma_khuyen_mai = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhuyenMai);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa mã khuyến mãi", e);
        }
    }

    private MaKhuyenMai mapRow(ResultSet rs) throws SQLException {
        int sl = rs.getInt("so_luong_ton");
        Integer soLuongTon = rs.wasNull() ? null : sl;
        return new MaKhuyenMai(
                rs.getString("ma_khuyen_mai"),
                LoaiGiamGia.valueOf(rs.getString("loai_giam")),
                rs.getDouble("gia_tri_giam"),
                rs.getDouble("gia_tri_toi_thieu_ap_dung"),
                rs.getTimestamp("ngay_bat_dau_ap_dung").toLocalDateTime(),
                rs.getTimestamp("ngay_het_han").toLocalDateTime(),
                soLuongTon,
                rs.getBoolean("trang_thai")
        );
    }
}
