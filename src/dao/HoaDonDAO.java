package dao;

import util.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.MaKhuyenMai;
import entity.NhanVien;
import entity.PhieuTiepNhan;
import enums.LoaiHangMuc;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiThanhToan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final MaKhuyenMaiDAO maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    /**
     * Giữ lại phương thức cũ để tương thích ngược và phục vụ báo cáo tổng hợp.
     * Tại màn hình danh sách hóa đơn đã thay thế bằng {@link #layDanhSachPhanTrang}.
     */
    public List<HoaDon> layTatCa() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT ma_hoa_don, loai_hoa_don, thoi_gian_lap, trang_thai_thanh_toan, " +
                "phuong_thuc_thanh_toan, thoi_gian_thanh_toan, ghi_chu, " +
                "ma_khach_hang, ma_nhan_vien, ma_khuyen_mai, ma_tiep_nhan " +
                "FROM hoa_don ORDER BY thoi_gian_lap DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(con, rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách hóa đơn", e);
        }
        return ds;
    }

    public List<HoaDon> layDanhSachPhanTrang(String tuKhoa, LoaiHoaDon loai, TrangThaiThanhToan trangThai,
                                             LocalDate tuNgay, LocalDate denNgay, int offset, int limit) {
        List<HoaDon> ds = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienLoc(tuKhoa, loai, trangThai, tuNgay, denNgay, params);
        String sql = "SELECT hd.ma_hoa_don, hd.loai_hoa_don, hd.thoi_gian_lap, hd.trang_thai_thanh_toan, " +
                "hd.phuong_thuc_thanh_toan, hd.thoi_gian_thanh_toan, hd.ghi_chu, " +
                "hd.ma_khach_hang, hd.ma_nhan_vien, hd.ma_khuyen_mai, hd.ma_tiep_nhan " +
                "FROM hoa_don hd LEFT JOIN khach_hang kh ON hd.ma_khach_hang = kh.ma_khach_hang " +
                where +
                " ORDER BY hd.thoi_gian_lap DESC, hd.ma_hoa_don DESC LIMIT ? OFFSET ?";
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(con, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách hóa đơn phân trang", e);
        }
        return ds;
    }

    public int demTheoDieuKien(String tuKhoa, LoaiHoaDon loai, TrangThaiThanhToan trangThai,
                               LocalDate tuNgay, LocalDate denNgay) {
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienLoc(tuKhoa, loai, trangThai, tuNgay, denNgay, params);
        String sql = "SELECT COUNT(*) FROM hoa_don hd " +
                "LEFT JOIN khach_hang kh ON hd.ma_khach_hang = kh.ma_khach_hang " + where;
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm số lượng hóa đơn", e);
        }
    }

    public double[] layThongKeKpi() {
        String sql = """
                SELECT
                    COUNT(*) AS tong_hd,
                    SUM(CASE WHEN hd.trang_thai_thanh_toan = 'DA_THANH_TOAN' THEN 1 ELSE 0 END) AS da_tt,
                    SUM(CASE WHEN hd.trang_thai_thanh_toan <> 'DA_THANH_TOAN' THEN 1 ELSE 0 END) AS cho_tt,
                    COALESCE(SUM(CASE
                        WHEN hd.trang_thai_thanh_toan = 'DA_THANH_TOAN' THEN
                            GREATEST(0,
                                COALESCE(ct.tien_hang, 0) + COALESCE(ct.thue, 0) -
                                CASE
                                    WHEN km.ma_khuyen_mai IS NOT NULL AND km.trang_thai = 1
                                         AND COALESCE(ct.tien_hang, 0) >= km.gia_tri_toi_thieu_ap_dung THEN
                                        LEAST(COALESCE(ct.tien_hang, 0),
                                            GREATEST(0,
                                                CASE
                                                    WHEN km.loai_giam = 'PHAN_TRAM' THEN COALESCE(ct.tien_hang, 0) * (km.gia_tri_giam / 100.0)
                                                    WHEN km.loai_giam = 'SO_TIEN' THEN km.gia_tri_giam
                                                    ELSE 0
                                                END
                                            )
                                        )
                                    ELSE 0
                                END
                            )
                        ELSE 0
                    END), 0) AS tong_thu
                FROM hoa_don hd
                LEFT JOIN ma_khuyen_mai km ON hd.ma_khuyen_mai = km.ma_khuyen_mai
                LEFT JOIN (
                    SELECT cthd.ma_hoa_don,
                           SUM(cthd.so_luong * cthd.don_gia) AS tien_hang,
                           SUM(cthd.so_luong * cthd.don_gia *
                               CASE
                                   WHEN COALESCE(dv.thue_vat, sp.thue_vat, 0) > 1
                                       THEN COALESCE(dv.thue_vat, sp.thue_vat, 0) / 100.0
                                   ELSE COALESCE(dv.thue_vat, sp.thue_vat, 0)
                               END
                           ) AS thue
                    FROM chi_tiet_hoa_don cthd
                    LEFT JOIN dich_vu dv ON cthd.loai_hang_muc = 'DICH_VU' AND cthd.ma_dich_vu = dv.ma_dich_vu
                    LEFT JOIN san_pham sp ON cthd.loai_hang_muc = 'SAN_PHAM' AND cthd.ma_san_pham = sp.ma_san_pham
                    GROUP BY cthd.ma_hoa_don
                ) ct ON hd.ma_hoa_don = ct.ma_hoa_don
                """;
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new double[]{
                        rs.getDouble("tong_hd"),
                        rs.getDouble("da_tt"),
                        rs.getDouble("cho_tt"),
                        rs.getDouble("tong_thu")
                };
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thống kê KPI hóa đơn", e);
        }
        return new double[]{0, 0, 0, 0};
    }

    private String xayDungDieuKienLoc(String tuKhoa, LoaiHoaDon loai, TrangThaiThanhToan trangThai,
                                      LocalDate tuNgay, LocalDate denNgay, List<Object> params) {
        StringBuilder where = new StringBuilder("WHERE 1=1");
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            String pattern = "%" + tuKhoa.trim().toLowerCase() + "%";
            where.append(" AND (LOWER(hd.ma_hoa_don) LIKE ? OR LOWER(COALESCE(kh.ho_ten, '')) LIKE ? ")
                 .append("OR LOWER(COALESCE(kh.so_dien_thoai, '')) LIKE ? ")
                 .append("OR (hd.ma_khach_hang = 'KH000000000' AND LOWER('Khách lẻ') LIKE ?))");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (loai != null) {
            where.append(" AND hd.loai_hoa_don = ?");
            params.add(loai.name());
        }
        if (trangThai != null) {
            where.append(" AND hd.trang_thai_thanh_toan = ?");
            params.add(trangThai.name());
        }
        if (tuNgay != null) {
            where.append(" AND DATE(hd.thoi_gian_lap) >= ?");
            params.add(Date.valueOf(tuNgay));
        }
        if (denNgay != null) {
            where.append(" AND DATE(hd.thoi_gian_lap) <= ?");
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

    public List<HoaDon> timTheoKhachHang(String maKhachHang) {
        List<HoaDon> ds = new ArrayList<>();
        String sql = "SELECT ma_hoa_don, loai_hoa_don, thoi_gian_lap, trang_thai_thanh_toan, " +
                "phuong_thuc_thanh_toan, thoi_gian_thanh_toan, ghi_chu, " +
                "ma_khach_hang, ma_nhan_vien, ma_khuyen_mai, ma_tiep_nhan " +
                "FROM hoa_don WHERE ma_khach_hang = ? ORDER BY thoi_gian_lap DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(con, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm hóa đơn theo khách hàng", e);
        }
        return ds;
    }

    public HoaDon timTheoPhieuTiepNhan(String maTiepNhan) {
        String sql = "SELECT ma_hoa_don, loai_hoa_don, thoi_gian_lap, trang_thai_thanh_toan, " +
                "phuong_thuc_thanh_toan, thoi_gian_thanh_toan, ghi_chu, " +
                "ma_khach_hang, ma_nhan_vien, ma_khuyen_mai, ma_tiep_nhan " +
                "FROM hoa_don WHERE ma_tiep_nhan = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(con, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm hóa đơn theo phiếu tiếp nhận: " + maTiepNhan, e);
        }
        return null;
    }

    public HoaDon timTheoMa(String maHoaDon) {
        String sql = "SELECT ma_hoa_don, loai_hoa_don, thoi_gian_lap, trang_thai_thanh_toan, " +
                "phuong_thuc_thanh_toan, thoi_gian_thanh_toan, ghi_chu, " +
                "ma_khach_hang, ma_nhan_vien, ma_khuyen_mai, ma_tiep_nhan " +
                "FROM hoa_don WHERE ma_hoa_don = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(con, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm hóa đơn theo mã: " + maHoaDon, e);
        }
        return null;
    }

    public boolean them(HoaDon hd) {
        return them(hd, false);
    }

    public boolean them(HoaDon hd, boolean capNhatTonKhoSanPham) {
        String sql = "INSERT INTO hoa_don (ma_hoa_don, loai_hoa_don, thoi_gian_lap, trang_thai_thanh_toan, " +
                "phuong_thuc_thanh_toan, thoi_gian_thanh_toan, ghi_chu, " +
                "ma_khach_hang, ma_nhan_vien, ma_khuyen_mai, ma_tiep_nhan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, hd.getMaHoaDon());
                ps.setString(2, hd.getLoaiHoaDon().name());
                ps.setTimestamp(3, Timestamp.valueOf(hd.getThoiGianLap()));
                ps.setString(4, hd.getTrangThaiThanhToan().name());
                if (hd.getPhuongThucThanhToan() != null) {
                    ps.setString(5, hd.getPhuongThucThanhToan().name());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                if (hd.getThoiGianThanhToan() != null) {
                    ps.setTimestamp(6, Timestamp.valueOf(hd.getThoiGianThanhToan()));
                } else {
                    ps.setNull(6, Types.TIMESTAMP);
                }
                ps.setString(7, hd.getGhiChu());
                ps.setString(8, hd.getKhachHang().getMaKhachHang());
                ps.setString(9, hd.getNhanVien().getMaNhanVien());
                if (hd.getMaKhuyenMai() != null) {
                    ps.setString(10, hd.getMaKhuyenMai().getMaKhuyenMai());
                } else {
                    ps.setNull(10, Types.VARCHAR);
                }
                if (hd.getPhieuTiepNhan() != null) {
                    ps.setString(11, hd.getPhieuTiepNhan().getMaTiepNhan());
                } else {
                    ps.setNull(11, Types.VARCHAR);
                }
                ps.executeUpdate();

                for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                    if (capNhatTonKhoSanPham
                            && ct.getLoaiHangMuc() == LoaiHangMuc.SAN_PHAM
                            && ct.getSanPham() != null) {
                        boolean truThanhCong = sanPhamDAO.truSoLuongTon(
                                con,
                                ct.getSanPham().getMaSanPham(),
                                ct.getSoLuong()
                        );
                        if (!truThanhCong) {
                            throw new SQLException(
                                    "Sản phẩm " + ct.getSanPham().getTenSanPham()
                                            + " không đủ số lượng tồn kho!"
                            );
                        }
                    }
                    chiTietHoaDonDAO.them(con, ct);
                }

                if (hd.getMaKhuyenMai() != null && hd.getMaKhuyenMai().getSoLuongTon() != null) {
                    maKhuyenMaiDAO.capNhatSoLuongTon(
                            con,
                            hd.getMaKhuyenMai().getMaKhuyenMai(),
                            hd.getMaKhuyenMai().getSoLuongTon()
                    );
                }

                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm hóa đơn", e);
        }
    }

    public boolean capNhatThanhToan(HoaDon hd) {
        String sql = "UPDATE hoa_don SET trang_thai_thanh_toan = ?, phuong_thuc_thanh_toan = ?, " +
                "thoi_gian_thanh_toan = ?, ghi_chu = ? WHERE ma_hoa_don = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hd.getTrangThaiThanhToan().name());
            if (hd.getPhuongThucThanhToan() != null) {
                ps.setString(2, hd.getPhuongThucThanhToan().name());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            if (hd.getThoiGianThanhToan() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(hd.getThoiGianThanhToan()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setString(4, hd.getGhiChu());
            ps.setString(5, hd.getMaHoaDon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật thanh toán hóa đơn", e);
        }
    }

    public boolean xoa(String maHoaDon) {
        String sqlCT = "DELETE FROM chi_tiet_hoa_don WHERE ma_hoa_don = ?";
        String sqlHD = "DELETE FROM hoa_don WHERE ma_hoa_don = ?";
        try (Connection con = ConnectDB.ketNoi()) {
            con.setAutoCommit(false);
            try (PreparedStatement psCT = con.prepareStatement(sqlCT);
                 PreparedStatement psHD = con.prepareStatement(sqlHD)) {
                psCT.setString(1, maHoaDon);
                psCT.executeUpdate();

                psHD.setString(1, maHoaDon);
                int ketQua = psHD.executeUpdate();
                con.commit();
                return ketQua > 0;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa hóa đơn: " + maHoaDon, e);
        }
    }

    private HoaDon mapRow(Connection con, ResultSet rs) throws SQLException {
        KhachHang kh = khachHangDAO.timTheoMa(rs.getString("ma_khach_hang"));
        NhanVien nv = nhanVienDAO.timTheoMa(rs.getString("ma_nhan_vien"));
        String maKM = rs.getString("ma_khuyen_mai");
        MaKhuyenMai km = maKM != null ? maKhuyenMaiDAO.timTheoMa(maKM) : null;
        String maTN = rs.getString("ma_tiep_nhan");
        PhieuTiepNhan ptn = maTN != null ? phieuTiepNhanDAO.timTheoMa(maTN) : null;
        String ptStr = rs.getString("phuong_thuc_thanh_toan");
        Timestamp tsTT = rs.getTimestamp("thoi_gian_thanh_toan");

        HoaDon hd = new HoaDon(
                rs.getString("ma_hoa_don"),
                LoaiHoaDon.valueOf(rs.getString("loai_hoa_don")),
                rs.getTimestamp("thoi_gian_lap").toLocalDateTime(),
                TrangThaiThanhToan.valueOf(rs.getString("trang_thai_thanh_toan")),
                ptStr != null ? PhuongThucThanhToan.valueOf(ptStr) : null,
                tsTT != null ? tsTT.toLocalDateTime() : null,
                rs.getString("ghi_chu"),
                kh,
                nv,
                km,
                ptn
        );
        hd.setDanhSachChiTiet(chiTietHoaDonDAO.layTheoHoaDon(con, hd));
        return hd;
    }
}
