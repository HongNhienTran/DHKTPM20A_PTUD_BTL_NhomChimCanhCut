package com.pengugarage.dao;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho thực thể Nhân Viên (`nhan_vien`).
 */
public class NhanVienDAO {

    /**
     * Lấy toàn bộ danh sách nhân viên
     */
    public List<NhanVien> layTatCa() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM `nhan_vien` ORDER BY `id` ASC";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        } catch (SQLException e) {
            return list;
        }
        return list;
    }

    /**
     * Tìm nhân viên theo ID
     */
    public NhanVien timTheoId(int id) {
        String sql = "SELECT * FROM `nhan_vien` WHERE `id` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * Tìm nhân viên theo mã nhân viên
     */
    public NhanVien timTheoMa(String maNhanVien) {
        String sql = "SELECT * FROM `nhan_vien` WHERE `ma_nhan_vien` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * Tìm nhân viên theo số điện thoại
     */
    public NhanVien timTheoSoDienThoai(String soDienThoai) {
        String sql = "SELECT * FROM `nhan_vien` WHERE `so_dien_thoai` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, soDienThoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * Thêm mới nhân viên
     */
    public boolean them(NhanVien nv) {
        String sql = "INSERT INTO `nhan_vien` (`ma_nhan_vien`, `ho_ten`, `so_dien_thoai`, `dia_chi`, `chuc_vu`, `trang_thai`) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getSoDienThoai());
            ps.setString(4, nv.getDiaChi());
            ps.setString(5, nv.getChucVu().name());
            ps.setBoolean(6, nv.isTrangThai());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        nv.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    /**
     * Cập nhật thông tin nhân viên
     */
    public boolean capNhat(NhanVien nv) {
        String sql = "UPDATE `nhan_vien` SET `ho_ten` = ?, `so_dien_thoai` = ?, `dia_chi` = ?, `chuc_vu` = ?, `trang_thai` = ? " +
                     "WHERE `id` = ?";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getSoDienThoai());
            ps.setString(3, nv.getDiaChi());
            ps.setString(4, nv.getChucVu().name());
            ps.setBoolean(5, nv.isTrangThai());
            ps.setInt(6, nv.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Cập nhật trạng thái khóa / mở khóa nhân viên
     */
    public boolean doiTrangThai(int id, boolean trangThai) {
        String sql = "UPDATE `nhan_vien` SET `trang_thai` = ? WHERE `id` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, trangThai);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Đếm số lượng nhân viên theo chức vụ để sinh mã tự động
     */
    public int demSoLuongTheoChucVu(ChucVu chucVu) {
        String sql = "SELECT COUNT(*) FROM `nhan_vien` WHERE `chuc_vu` = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, chucVu.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setId(rs.getInt("id"));
        nv.setMaNhanVien(rs.getString("ma_nhan_vien"));
        nv.setHoTen(rs.getString("ho_ten"));
        nv.setSoDienThoai(rs.getString("so_dien_thoai"));
        nv.setDiaChi(rs.getString("dia_chi"));
        try {
            nv.setChucVu(ChucVu.valueOf(rs.getString("chuc_vu")));
        } catch (Exception e) {
            nv.setChucVu(ChucVu.KY_THUAT_VIEN);
        }
        nv.setTrangThai(rs.getBoolean("trang_thai"));
        return nv;
    }
}
