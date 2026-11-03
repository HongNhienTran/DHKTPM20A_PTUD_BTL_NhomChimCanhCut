package com.pengugarage.dao;

import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Khách Hàng (khach_hang).
 * Cung cấp các thao tác CRUD và tra cứu hồ sơ khách hàng kèm phương tiện.
 */
public class KhachHangDAO {

    /**
     * Lấy toàn bộ danh sách khách hàng trong hệ thống.
     *
     * @return Danh sách KhachHang
     */
    public List<KhachHang> layTatCa() {
        List<KhachHang> dsKhachHang = new ArrayList<>();
        String sql = "SELECT * FROM khach_hang ORDER BY id DESC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dsKhachHang.add(mapResultSetToKhachHang(rs));
            }
        } catch (SQLException ignored) {
        }
        return dsKhachHang;
    }

    /**
     * Tìm khách hàng theo ID định danh.
     *
     * @param id ID khách hàng
     * @return Đối tượng KhachHang hoặc null nếu không tìm thấy
     */
    public KhachHang timTheoId(int id) {
        String sql = "SELECT * FROM khach_hang WHERE id = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Tìm kiếm khách hàng theo số điện thoại (phục vụ chọn khách hàng tại quầy và bán hàng).
     *
     * @param soDienThoai Số điện thoại khách hàng
     * @return KhachHang
     */
    public KhachHang timTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) return null;
        String sql = "SELECT * FROM khach_hang WHERE REPLACE(so_dien_thoai, ' ', '') = REPLACE(?, ' ', '') LIMIT 1";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, soDienThoai.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Tự động sinh mã khách hàng mới KH001, KH002...
     */
    public String sinhMaKhachHangTuDong() {
        String sql = "SELECT MAX(id) FROM khach_hang";
        int nextId = 1;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException ignored) {
        }
        return String.format("KH%03d", nextId);
    }

    /**
     * Tìm kiếm và lọc khách hàng đa tiêu chí theo từ khóa và giới tính.
     *
     * @param tuKhoa   Từ khóa (Mã KH, Tên, Số điện thoại, Địa chỉ)
     * @param gioiTinh Giới tính (Nam, Nữ, hoặc null nếu tất cả)
     * @return Danh sách khách hàng phù hợp
     */
    public List<KhachHang> timKiemVaLoc(String tuKhoa, String gioiTinh) {
        List<KhachHang> dsKetQua = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM khach_hang WHERE 1=1 ");
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append("AND (ho_ten LIKE ? OR so_dien_thoai LIKE ? OR ma_khach_hang LIKE ? OR dia_chi LIKE ?) ");
        }
        if (gioiTinh != null && !gioiTinh.trim().isEmpty() && !gioiTinh.equalsIgnoreCase("Tất cả")) {
            sql.append("AND gioi_tinh = ? ");
        }
        sql.append("ORDER BY id DESC");

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String p = "%" + tuKhoa.trim() + "%";
                ps.setString(paramIndex++, p);
                ps.setString(paramIndex++, p);
                ps.setString(paramIndex++, p);
                ps.setString(paramIndex++, p);
            }
            if (gioiTinh != null && !gioiTinh.trim().isEmpty() && !gioiTinh.equalsIgnoreCase("Tất cả")) {
                ps.setString(paramIndex++, gioiTinh.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsKetQua.add(mapResultSetToKhachHang(rs));
                }
            }
        } catch (SQLException ignored) {
        }
        return dsKetQua;
    }

    /**
     * Lấy danh sách các phương tiện (xe ô tô) do khách hàng này đứng tên sở hữu.
     *
     * @param khachHangId ID khách hàng
     * @return Danh sách PhuongTien
     */
    public List<PhuongTien> layDanhSachXeCuaKhach(int khachHangId) {
        PhuongTienDAO phuongTienDAO = new PhuongTienDAO();
        return phuongTienDAO.layTheoKhachHangId(khachHangId);
    }

    /**
     * Thêm mới một hồ sơ khách hàng vào cơ sở dữ liệu.
     *
     * @param kh Đối tượng KhachHang mới
     * @return true nếu thêm thành công
     */
    public boolean them(KhachHang kh) {
        if (kh == null) return false;
        if (kh.getMaKhachHang() == null || kh.getMaKhachHang().trim().isEmpty()) {
            kh.setMaKhachHang(sinhMaKhachHangTuDong());
        }
        String sql = "INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getDiaChi() != null ? kh.getDiaChi() : "");
            ps.setString(5, kh.getGioiTinh() != null ? kh.getGioiTinh() : "Nam");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        kh.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * Cập nhật thông tin hồ sơ khách hàng.
     *
     * @param kh Đối tượng KhachHang cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean capNhat(KhachHang kh) {
        if (kh == null || kh.getId() <= 0) return false;
        String sql = "UPDATE khach_hang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, gioi_tinh = ? WHERE id = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getDiaChi() != null ? kh.getDiaChi() : "");
            ps.setString(4, kh.getGioiTinh() != null ? kh.getGioiTinh() : "Nam");
            ps.setInt(5, kh.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * Xóa hồ sơ khách hàng theo ID.
     */
    public boolean xoa(int id) {
        String sql = "DELETE FROM khach_hang WHERE id = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
        }
        return false;
    }

    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        KhachHang kh = new KhachHang();
        kh.setId(rs.getInt("id"));
        kh.setMaKhachHang(rs.getString("ma_khach_hang"));
        kh.setHoTen(rs.getString("ho_ten"));
        kh.setSoDienThoai(rs.getString("so_dien_thoai"));
        kh.setDiaChi(rs.getString("dia_chi"));
        kh.setGioiTinh(rs.getString("gioi_tinh"));
        return kh;
    }
}
