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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final MaKhuyenMaiDAO maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

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
