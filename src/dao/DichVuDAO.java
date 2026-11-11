package dao;

import util.ConnectDB;
import entity.DichVu;
import enums.ChuyenMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    public String layMaLonNhat() {
        String sql = "SELECT MAX(ma_dich_vu) FROM dich_vu";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã dịch vụ lớn nhất", e);
        }
    }

    public List<DichVu> layTatCa() {
        List<DichVu> ds = new ArrayList<>();
        String sql = "SELECT ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, co_phu_tung, chuyen_mon, trang_thai FROM dich_vu ORDER BY ma_dich_vu";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách dịch vụ", e);
        }
        return ds;
    }

    public List<DichVu> layDanhSachHoatDong() {
        List<DichVu> ds = new ArrayList<>();
        String sql = "SELECT ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, co_phu_tung, chuyen_mon, trang_thai FROM dich_vu WHERE trang_thai = 1 ORDER BY ma_dich_vu";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách dịch vụ đang hoạt động", e);
        }
        return ds;
    }

    public List<DichVu> layTheoChuyenMon(ChuyenMon cm) {
        List<DichVu> ds = new ArrayList<>();
        if (cm == null) return ds;
        String sql = "SELECT ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, co_phu_tung, chuyen_mon, trang_thai FROM dich_vu WHERE chuyen_mon = ? AND trang_thai = 1 ORDER BY ma_dich_vu";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cm.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách dịch vụ theo chuyên môn: " + cm, e);
        }
        return ds;
    }

    public List<DichVu> layTheoCoPhuTung(boolean coPhuTung) {
        List<DichVu> ds = new ArrayList<>();
        String sql = "SELECT ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, co_phu_tung, chuyen_mon, trang_thai FROM dich_vu WHERE co_phu_tung = ? AND trang_thai = 1 ORDER BY ma_dich_vu";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, coPhuTung ? 1 : 0);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách dịch vụ theo cờ phụ tùng", e);
        }
        return ds;
    }

    public DichVu timTheoMa(String maDichVu) {
        String sql = "SELECT ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, co_phu_tung, chuyen_mon, trang_thai FROM dich_vu WHERE ma_dich_vu = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm dịch vụ theo mã: " + maDichVu, e);
        }
        return null;
    }

    public boolean them(DichVu dv) {
        String sql = "INSERT INTO dich_vu (ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, co_phu_tung, chuyen_mon, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dv.getMaDichVu());
            ps.setString(2, dv.getTenDichVu());
            ps.setString(3, dv.getMoTa());
            ps.setDouble(4, dv.getDonGia());
            ps.setDouble(5, dv.getThueVAT());
            ps.setInt(6, dv.isCoPhuTung() ? 1 : 0);
            ps.setString(7, dv.getChuyenMon() != null ? dv.getChuyenMon().name() : null);
            ps.setBoolean(8, dv.isTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm dịch vụ", e);
        }
    }

    public boolean capNhat(DichVu dv) {
        String sql = "UPDATE dich_vu SET ten_dich_vu = ?, mo_ta = ?, don_gia = ?, thue_vat = ?, co_phu_tung = ?, chuyen_mon = ?, trang_thai = ? WHERE ma_dich_vu = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setString(2, dv.getMoTa());
            ps.setDouble(3, dv.getDonGia());
            ps.setDouble(4, dv.getThueVAT());
            ps.setInt(5, dv.isCoPhuTung() ? 1 : 0);
            ps.setString(6, dv.getChuyenMon() != null ? dv.getChuyenMon().name() : null);
            ps.setBoolean(7, dv.isTrangThai());
            ps.setString(8, dv.getMaDichVu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật dịch vụ", e);
        }
    }

    public boolean xoa(String maDichVu) {
        String sql = "DELETE FROM dich_vu WHERE ma_dich_vu = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa dịch vụ", e);
        }
    }

    private DichVu mapRow(ResultSet rs) throws SQLException {
        String cmStr = rs.getString("chuyen_mon");
        ChuyenMon cm = null;
        if (cmStr != null && !cmStr.trim().isEmpty()) {
            try {
                cm = ChuyenMon.valueOf(cmStr.trim());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return new DichVu(
                rs.getString("ma_dich_vu"),
                rs.getString("ten_dich_vu"),
                rs.getString("mo_ta"),
                rs.getDouble("don_gia"),
                rs.getDouble("thue_vat"),
                rs.getInt("co_phu_tung") == 1,
                cm,
                rs.getBoolean("trang_thai")
        );
    }
}
