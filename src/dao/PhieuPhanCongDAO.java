package dao;

import util.ConnectDB;
import entity.ChiTietBaoGia;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuPhanCong;
import enums.TrangThaiSua;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhieuPhanCongDAO {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final PhieuBaoGiaDAO phieuBaoGiaDAO = new PhieuBaoGiaDAO();

    public Map<String, Integer> laySoLuongViecDangXuLyCuaTatCaKTV() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT ma_nhan_vien, COUNT(*) AS so_luong FROM phieu_phan_cong " +
                "WHERE trang_thai_sua IN ('CHO_THUC_HIEN', 'DANG_SUA') GROUP BY ma_nhan_vien";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("ma_nhan_vien"), rs.getInt("so_luong"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy thống kê số việc đang xử lý của kỹ thuật viên", e);
        }
        return map;
    }

    public int demSoPhieuDangXuLyCuaNhanVien(String maNhanVien) {
        String sql = "SELECT COUNT(*) FROM phieu_phan_cong WHERE ma_nhan_vien = ? " +
                "AND trang_thai_sua IN ('CHO_THUC_HIEN', 'DANG_SUA')";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm số phiếu đang xử lý của nhân viên: " + maNhanVien, e);
        }
    }

    public String layMaLonNhatTheoTienTo(String tienTo) {
        String sql = "SELECT MAX(ma_phan_cong) FROM phieu_phan_cong WHERE ma_phan_cong LIKE ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã phân công lớn nhất", e);
        }
    }

    /**
     * Giữ lại phương thức cũ để tương thích ngược.
     * Tại màn hình Nhật ký công việc đã thay thế bằng {@link #layNhatKyHoanThanhPhanTrang}.
     */
    public List<PhieuPhanCong> layTatCa() {
        List<PhieuPhanCong> ds = new ArrayList<>();
        String sql = "SELECT ma_phan_cong, thoi_gian_phan_cong, thoi_gian_bat_dau, thoi_gian_hoan_thanh, " +
                "trang_thai_sua, ghi_chu, ma_nhan_vien, ma_bao_gia, ma_dich_vu " +
                "FROM phieu_phan_cong ORDER BY thoi_gian_phan_cong DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách phiếu phân công", e);
        }
        return ds;
    }

    public List<PhieuPhanCong> layNhatKyHoanThanhPhanTrang(String maNhanVien, String tuKhoa,
                                                            LocalDate tuNgay, LocalDate denNgay,
                                                            int offset, int limit) {
        List<PhieuPhanCong> ds = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienNhatKy(maNhanVien, tuKhoa, tuNgay, denNgay, params);
        String sql = "SELECT ppc.ma_phan_cong, ppc.thoi_gian_phan_cong, ppc.thoi_gian_bat_dau, ppc.thoi_gian_hoan_thanh, " +
                "ppc.trang_thai_sua, ppc.ghi_chu, ppc.ma_nhan_vien, ppc.ma_bao_gia, ppc.ma_dich_vu " +
                "FROM phieu_phan_cong ppc " +
                "JOIN phieu_bao_gia pbg ON ppc.ma_bao_gia = pbg.ma_bao_gia " +
                "JOIN phieu_tiep_nhan ptn ON pbg.ma_tiep_nhan = ptn.ma_tiep_nhan " +
                "JOIN phuong_tien pt ON ptn.ma_phuong_tien = pt.ma_phuong_tien " +
                "JOIN dich_vu dv ON ppc.ma_dich_vu = dv.ma_dich_vu " +
                where +
                " ORDER BY COALESCE(ppc.thoi_gian_hoan_thanh, ppc.thoi_gian_phan_cong) DESC, ppc.ma_phan_cong DESC " +
                "LIMIT ? OFFSET ?";
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
            throw new RuntimeException("Lỗi lấy nhật ký công việc phân trang", e);
        }
        return ds;
    }

    public int demNhatKyHoanThanh(String maNhanVien, String tuKhoa, LocalDate tuNgay, LocalDate denNgay) {
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienNhatKy(maNhanVien, tuKhoa, tuNgay, denNgay, params);
        String sql = "SELECT COUNT(*) FROM phieu_phan_cong ppc " +
                "JOIN phieu_bao_gia pbg ON ppc.ma_bao_gia = pbg.ma_bao_gia " +
                "JOIN phieu_tiep_nhan ptn ON pbg.ma_tiep_nhan = ptn.ma_tiep_nhan " +
                "JOIN phuong_tien pt ON ptn.ma_phuong_tien = pt.ma_phuong_tien " +
                "JOIN dich_vu dv ON ppc.ma_dich_vu = dv.ma_dich_vu " + where;
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm nhật ký công việc", e);
        }
    }

    private String xayDungDieuKienNhatKy(String maNhanVien, String tuKhoa, LocalDate tuNgay,
                                         LocalDate denNgay, List<Object> params) {
        StringBuilder where = new StringBuilder("WHERE ppc.trang_thai_sua = 'HOAN_THANH'");
        if (maNhanVien != null && !maNhanVien.trim().isEmpty()) {
            where.append(" AND ppc.ma_nhan_vien = ?");
            params.add(maNhanVien.trim());
        }
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            String pattern = "%" + tuKhoa.trim().toLowerCase() + "%";
            where.append(" AND (LOWER(ppc.ma_phan_cong) LIKE ? OR LOWER(pt.bien_so_xe) LIKE ? ")
                 .append("OR LOWER(pt.mau_xe) LIKE ? OR LOWER(dv.ten_dich_vu) LIKE ?)");
            for (int i = 0; i < 4; i++) {
                params.add(pattern);
            }
        }
        if (tuNgay != null) {
            where.append(" AND DATE(COALESCE(ppc.thoi_gian_hoan_thanh, ppc.thoi_gian_phan_cong)) >= ?");
            params.add(Date.valueOf(tuNgay));
        }
        if (denNgay != null) {
            where.append(" AND DATE(COALESCE(ppc.thoi_gian_hoan_thanh, ppc.thoi_gian_phan_cong)) <= ?");
            params.add(Date.valueOf(denNgay));
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
            } else if (val instanceof Date d) {
                ps.setDate(i + 1, d);
            }
        }
    }

    public List<PhieuPhanCong> timTheoNhanVien(String maNhanVien) {
        List<PhieuPhanCong> ds = new ArrayList<>();
        String sql = "SELECT ma_phan_cong, thoi_gian_phan_cong, thoi_gian_bat_dau, thoi_gian_hoan_thanh, " +
                "trang_thai_sua, ghi_chu, ma_nhan_vien, ma_bao_gia, ma_dich_vu " +
                "FROM phieu_phan_cong WHERE ma_nhan_vien = ? ORDER BY thoi_gian_phan_cong DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu phân công theo nhân viên", e);
        }
        return ds;
    }

    public List<PhieuPhanCong> timTheoPhieuTiepNhan(String maTiepNhan) {
        List<PhieuPhanCong> ds = new ArrayList<>();
        String sql = "SELECT ppc.ma_phan_cong, ppc.thoi_gian_phan_cong, ppc.thoi_gian_bat_dau, " +
                "ppc.thoi_gian_hoan_thanh, ppc.trang_thai_sua, ppc.ghi_chu, " +
                "ppc.ma_nhan_vien, ppc.ma_bao_gia, ppc.ma_dich_vu " +
                "FROM phieu_phan_cong ppc " +
                "JOIN phieu_bao_gia pbg ON ppc.ma_bao_gia = pbg.ma_bao_gia " +
                "WHERE pbg.ma_tiep_nhan = ? ORDER BY ppc.thoi_gian_phan_cong DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu phân công theo phiếu tiếp nhận", e);
        }
        return ds;
    }

    public PhieuPhanCong timTheoChiTietBaoGia(String maBaoGia, String maDichVu) {
        String sql = "SELECT ma_phan_cong, thoi_gian_phan_cong, thoi_gian_bat_dau, thoi_gian_hoan_thanh, " +
                "trang_thai_sua, ghi_chu, ma_nhan_vien, ma_bao_gia, ma_dich_vu " +
                "FROM phieu_phan_cong WHERE ma_bao_gia = ? AND ma_dich_vu = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBaoGia);
            ps.setString(2, maDichVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu phân công theo hạng mục báo giá", e);
        }
        return null;
    }

    public PhieuPhanCong timTheoMa(String maPhanCong) {
        String sql = "SELECT ma_phan_cong, thoi_gian_phan_cong, thoi_gian_bat_dau, thoi_gian_hoan_thanh, " +
                "trang_thai_sua, ghi_chu, ma_nhan_vien, ma_bao_gia, ma_dich_vu " +
                "FROM phieu_phan_cong WHERE ma_phan_cong = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhanCong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu phân công theo mã: " + maPhanCong, e);
        }
        return null;
    }

    public boolean them(PhieuPhanCong ppc) {
        String sql = "INSERT INTO phieu_phan_cong (ma_phan_cong, thoi_gian_phan_cong, thoi_gian_bat_dau, " +
                "thoi_gian_hoan_thanh, trang_thai_sua, ghi_chu, ma_nhan_vien, ma_bao_gia, ma_dich_vu) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ppc.getMaPhanCong());
            ps.setTimestamp(2, Timestamp.valueOf(ppc.getThoiGianPhanCong()));
            if (ppc.getThoiGianBatDau() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(ppc.getThoiGianBatDau()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            if (ppc.getThoiGianHoanThanh() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(ppc.getThoiGianHoanThanh()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.setString(5, ppc.getTrangThaiSua().name());
            ps.setString(6, ppc.getGhiChu());
            ps.setString(7, ppc.getNhanVien().getMaNhanVien());
            ps.setString(8, ppc.getChiTietBaoGia().getPhieuBaoGia().getMaBaoGia());
            ps.setString(9, ppc.getChiTietBaoGia().getDichVu().getMaDichVu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm phiếu phân công", e);
        }
    }

    public boolean capNhat(PhieuPhanCong ppc) {
        String sql = "UPDATE phieu_phan_cong SET thoi_gian_bat_dau = ?, thoi_gian_hoan_thanh = ?, " +
                "trang_thai_sua = ?, ghi_chu = ?, ma_nhan_vien = ? WHERE ma_phan_cong = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (ppc.getThoiGianBatDau() != null) {
                ps.setTimestamp(1, Timestamp.valueOf(ppc.getThoiGianBatDau()));
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }
            if (ppc.getThoiGianHoanThanh() != null) {
                ps.setTimestamp(2, Timestamp.valueOf(ppc.getThoiGianHoanThanh()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }
            ps.setString(3, ppc.getTrangThaiSua().name());
            ps.setString(4, ppc.getGhiChu());
            ps.setString(5, ppc.getNhanVien().getMaNhanVien());
            ps.setString(6, ppc.getMaPhanCong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật phiếu phân công", e);
        }
    }

    public boolean xoa(String maPhanCong) {
        String sql = "DELETE FROM phieu_phan_cong WHERE ma_phan_cong = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhanCong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa phiếu phân công", e);
        }
    }

    private PhieuPhanCong mapRow(ResultSet rs) throws SQLException {
        NhanVien nv = nhanVienDAO.timTheoMa(rs.getString("ma_nhan_vien"));
        PhieuBaoGia pbg = phieuBaoGiaDAO.timTheoMa(rs.getString("ma_bao_gia"));
        String maDichVu = rs.getString("ma_dich_vu");
        ChiTietBaoGia targetCtbg = null;
        if (pbg != null) {
            for (ChiTietBaoGia ct : pbg.getDanhSachChiTiet()) {
                if (ct.getDichVu() != null && maDichVu.equals(ct.getDichVu().getMaDichVu())) {
                    targetCtbg = ct;
                    break;
                }
            }
        }
        Timestamp tsBatDau = rs.getTimestamp("thoi_gian_bat_dau");
        Timestamp tsHoanThanh = rs.getTimestamp("thoi_gian_hoan_thanh");
        return new PhieuPhanCong(
                rs.getString("ma_phan_cong"),
                rs.getTimestamp("thoi_gian_phan_cong").toLocalDateTime(),
                tsBatDau != null ? tsBatDau.toLocalDateTime() : null,
                tsHoanThanh != null ? tsHoanThanh.toLocalDateTime() : null,
                TrangThaiSua.valueOf(rs.getString("trang_thai_sua")),
                rs.getString("ghi_chu"),
                nv,
                targetCtbg
        );
    }
}
