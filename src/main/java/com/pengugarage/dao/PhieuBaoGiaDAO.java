package com.pengugarage.dao;

import com.pengugarage.model.entity.ChiTietBaoGia;
import com.pengugarage.model.entity.DichVu;
import com.pengugarage.model.entity.PhieuBaoGia;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.model.enums.LoaiHangMuc;
import com.pengugarage.model.enums.TrangThaiBaoGia;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Phiếu Báo Giá (PhieuBaoGia) và Chi Tiết Báo Giá (ChiTietBaoGia).
 */
public class PhieuBaoGiaDAO {

    /**
     * Lấy phiếu báo giá mới nhất theo ID phiếu tiếp nhận (kèm đầy đủ danh sách chi tiết báo giá).
     *
     * @param phieuTiepNhanId ID phiếu tiếp nhận
     * @return PhieuBaoGia hoặc null nếu chưa có báo giá
     */
    public PhieuBaoGia layTheoPhieuTiepNhanId(int phieuTiepNhanId) {
        String sql = "SELECT * FROM `phieu_bao_gia` WHERE `phieu_tiep_nhan_id` = ? ORDER BY `id` DESC LIMIT 1";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phieuTiepNhanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhieuBaoGia pbg = mapResultSetToBaoGia(rs);
                    // Lấy kèm danh sách chi tiết
                    pbg.setDanhSachChiTiet(layChiTietTheoBaoGiaId(pbg.getId()));
                    return pbg;
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Lấy toàn bộ danh sách chi tiết của một phiếu báo giá (gồm Dịch vụ sửa chữa và Phụ tùng).
     *
     * @param phieuBaoGiaId ID phiếu báo giá
     * @return Danh sách ChiTietBaoGia
     */
    public List<ChiTietBaoGia> layChiTietTheoBaoGiaId(int phieuBaoGiaId) {
        List<ChiTietBaoGia> list = new ArrayList<>();
        String sql = """
            SELECT ct.*,
                   dv.id AS dv_id, dv.ma_dich_vu, dv.ten_dich_vu, dv.gia_dich_vu,
                   sp.id AS sp_id, sp.ma_san_pham, sp.ten_san_pham, sp.don_gia_ban, sp.don_vi_tinh
            FROM `chi_tiet_bao_gia` ct
            LEFT JOIN `dich_vu` dv ON ct.dich_vu_id = dv.id
            LEFT JOIN `san_pham` sp ON ct.san_pham_id = sp.id
            WHERE ct.phieu_bao_gia_id = ?
            ORDER BY ct.id ASC
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, phieuBaoGiaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToChiTiet(rs));
                }
            }
        } catch (SQLException ignored) {
        }
        return list;
    }

    /**
     * Cập nhật trạng thái phiếu báo giá (DA_CHAP_THUAN / TU_CHOI).
     *
     * @param phieuBaoGiaId ID phiếu báo giá
     * @param trangThai     Trạng thái mới
     * @param lyDoTuChoi    Lý do nếu khách từ chối (hoặc null)
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatTrangThai(int phieuBaoGiaId, TrangThaiBaoGia trangThai, String lyDoTuChoi) {
        String sql = """
            UPDATE `phieu_bao_gia`
            SET `trang_thai` = ?, `thoi_gian_xac_nhan` = ?, `ly_do_tu_choi` = ?
            WHERE `id` = ?
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai.name());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, lyDoTuChoi);
            ps.setInt(4, phieuBaoGiaId);

            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
        }
        return false;
    }

    private PhieuBaoGia mapResultSetToBaoGia(ResultSet rs) throws SQLException {
        PhieuBaoGia pbg = new PhieuBaoGia();
        pbg.setId(rs.getInt("id"));
        pbg.setMaBaoGia(rs.getString("ma_bao_gia"));
        pbg.setPhieuTiepNhanId(rs.getInt("phieu_tiep_nhan_id"));
        pbg.setNhanVienLapId(rs.getInt("nhan_vien_lap_id"));

        Timestamp ts = rs.getTimestamp("thoi_gian_lap");
        if (ts != null) pbg.setThoiGianLap(ts.toLocalDateTime());

        pbg.setPhienBan(rs.getInt("phien_ban"));
        pbg.setTongTienDuKien(rs.getDouble("tong_tien_du_kien"));

        try {
            pbg.setTrangThai(TrangThaiBaoGia.valueOf(rs.getString("trang_thai")));
        } catch (Exception e) {
            pbg.setTrangThai(TrangThaiBaoGia.CHO_XAC_NHAN);
        }

        pbg.setLyDoTuChoi(rs.getString("ly_do_tu_choi"));
        pbg.setGhiChu(rs.getString("ghi_chu"));
        return pbg;
    }

    private ChiTietBaoGia mapResultSetToChiTiet(ResultSet rs) throws SQLException {
        ChiTietBaoGia ct = new ChiTietBaoGia();
        ct.setId(rs.getInt("id"));
        ct.setPhieuBaoGiaId(rs.getInt("phieu_bao_gia_id"));

        try {
            ct.setLoaiHangMuc(LoaiHangMuc.valueOf(rs.getString("loai_hang_muc")));
        } catch (Exception e) {
            ct.setLoaiHangMuc(LoaiHangMuc.DICH_VU);
        }

        int dvId = rs.getInt("dich_vu_id");
        if (dvId > 0) {
            ct.setDichVuId(dvId);
            DichVu dv = new DichVu();
            dv.setId(dvId);
            dv.setMaDichVu(rs.getString("ma_dich_vu"));
            dv.setTenDichVu(rs.getString("ten_dich_vu"));
            try {
                dv.setDonGia(rs.getDouble("gia_dich_vu"));
            } catch (SQLException e) {
                dv.setDonGia(rs.getDouble("don_gia"));
            }
            ct.setDichVu(dv);
        }

        int spId = rs.getInt("san_pham_id");
        if (spId > 0) {
            ct.setSanPhamId(spId);
            SanPham sp = new SanPham();
            sp.setId(spId);
            sp.setMaSanPham(rs.getString("ma_san_pham"));
            sp.setTenSanPham(rs.getString("ten_san_pham"));
            sp.setDonGiaBan(rs.getDouble("don_gia_ban"));
            ct.setSanPham(sp);
        }

        ct.setSoLuong(rs.getInt("so_luong"));
        ct.setDonGia(rs.getDouble("don_gia"));
        ct.setThanhTien(rs.getDouble("thanh_tien"));
        ct.setDuocChapNhan(rs.getBoolean("duoc_chap_nhan"));
        ct.setGhiChu(rs.getString("ghi_chu"));
        return ct;
    }
}
