package com.pengugarage.dao;

import com.pengugarage.model.entity.PhieuPhanCong;
import com.pengugarage.model.enums.TrangThaiSua;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object cho Thực thể Phiếu Phân Công (PhieuPhanCong).
 * Quản lý việc phân chia việc cho Kỹ thuật viên thi công sửa chữa.
 */
public class PhieuPhanCongDAO {

    /**
     * Thêm mới một phiếu phân công cho Kỹ thuật viên.
     *
     * @param ppc Đối tượng PhieuPhanCong
     * @return true nếu thêm thành công
     */
    public boolean them(PhieuPhanCong ppc) {
        String sql = """
            INSERT INTO `phieu_phan_cong`
            (`ma_phan_cong`, `ky_thuat_vien_id`, `chi_tiet_bao_gia_id`, `thoi_gian_phan_cong`, `trang_thai_sua`, `ghi_chu`)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ppc.getMaPhanCong());
            ps.setInt(2, ppc.getKyThuatVienId());
            ps.setInt(3, ppc.getChiTietBaoGiaId());

            LocalDateTime tg = ppc.getThoiGianPhanCong() != null ? ppc.getThoiGianPhanCong() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(tg));
            ps.setString(5, ppc.getTrangThaiSua() != null ? ppc.getTrangThaiSua().name() : TrangThaiSua.CHO_THUC_HIEN.name());
            ps.setString(6, ppc.getGhiChu());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        ppc.setId(gk.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }
}

