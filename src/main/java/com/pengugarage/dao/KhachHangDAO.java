package com.pengugarage.dao;

import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        // TODO: [M05-T01 - Phong] Viết SQL SELECT * FROM KhachHang ORDER BY id DESC
        String sql ="SELECT * FROM Khach_hang ORDER BY id DESC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                KhachHang kh = new KhachHang();
                kh.setId(rs.getInt("id"));
                kh.setMaKhachHang(rs.getString("ma_khach_hang"));
                kh.setHoTen(rs.getString("ho_ten"));
                kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                kh.setDiaChi(rs.getString("dia_chi"));
                kh.setGioiTinh(rs.getString("gioi_tinh"));
                dsKhachHang.add(kh);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách khách hàng" + e.getMessage()  );
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
        List<PhuongTien> dsXe = new ArrayList<>();
        // TODO: [M05-T02 - Phong] Viết SQL SELECT * FROM PhuongTien WHERE khach_hang_id = ?
        String sql = "SELECT * FROM phuong_tien WHERE khach_hang_id = ?";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, khachHangId); // Truyền ID khách hàng vào câu SQL

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PhuongTien pt = new PhuongTien();
                    pt.setId(rs.getInt("id"));
                    // Mapping các trường cơ bản
                    pt.setBienSoXe(rs.getString("bien_so_xe"));
                    pt.setMauSacXe(rs.getString("mau_sac_xe"));
                    pt.setNamSanXuat(rs.getInt("nam_san_xuat"));

                    /* Lưu ý: Nếu entity PhuongTien của bạn có liên kết đối tượng HangXe (pt.setHangXe(...))
                       hoặc các trường khác, bạn cần map thêm tương ứng với cấu trúc bảng ở đây */

                    dsXe.add(pt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách xe của khách hàng: " + e.getMessage());
        }
        return dsXe;
    }

    /**
     * Thêm mới một hồ sơ khách hàng vào cơ sở dữ liệu.
     *
     * @param kh Đối tượng KhachHang mới
     * @return true nếu thêm thành công
     */
    public boolean them(KhachHang kh) {
        if (kh == null) {
            return false;
        }

        if (kh.getMaKhachHang() == null || kh.getMaKhachHang().trim().isEmpty()) {
            kh.setMaKhachHang(sinhMaKhachHangTuDong());
        }

        String sql = "INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getGioiTinh());

            if (ps.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    kh.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm khách hàng", e);
        }
    }



    /**
     * Cập nhật thông tin hồ sơ khách hàng.
     *
     * @param kh Đối tượng KhachHang cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean capNhat(KhachHang kh) {

        // TODO: [M05-T04 - Phong] Viết SQL UPDATE KhachHang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, gioi_tinh = ? WHERE id = ?
        String sql = "UPDATE khach_hang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, gioi_tinh = ? WHERE id = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3,  kh.getDiaChi() != null ? kh.getDiaChi() : "");
            ps.setString(4, kh.getGioiTinh() != null ? kh.getGioiTinh() : "Nam");
            ps.setInt(5, kh.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật khách hàng: " + e.getMessage());
        }
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
