package dao;

import util.ConnectDB;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuTiepNhan;
import enums.TrangThaiBaoGia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhieuBaoGiaDAO {
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final ChiTietBaoGiaDAO chiTietBaoGiaDAO = new ChiTietBaoGiaDAO();
    private final ChiTietDungPhuTungDAO chiTietDungPhuTungDAO = new ChiTietDungPhuTungDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public String layMaLonNhatTheoTienTo(String tienTo) {
        String sql = "SELECT MAX(ma_bao_gia) FROM phieu_bao_gia WHERE ma_bao_gia LIKE ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã báo giá lớn nhất", e);
        }
    }

    public List<PhieuBaoGia> layTatCa() {
        List<PhieuBaoGia> ds = new ArrayList<>();
        String sql = "SELECT ma_bao_gia, thoi_gian_lap, trang_thai, ly_do_tu_choi, ghi_chu, " +
                "ma_tiep_nhan, ma_nhan_vien FROM phieu_bao_gia ORDER BY thoi_gian_lap DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(con, rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách phiếu báo giá", e);
        }
        return ds;
    }

    public List<PhieuBaoGia> timTheoPhieuTiepNhan(String maTiepNhan) {
        List<PhieuBaoGia> ds = new ArrayList<>();
        String sql = "SELECT ma_bao_gia, thoi_gian_lap, trang_thai, ly_do_tu_choi, ghi_chu, " +
                "ma_tiep_nhan, ma_nhan_vien FROM phieu_bao_gia " +
                "WHERE ma_tiep_nhan = ? ORDER BY thoi_gian_lap DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(con, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu báo giá theo mã tiếp nhận", e);
        }
        return ds;
    }

    public PhieuBaoGia timMoiNhatTheoTiepNhan(String maTiepNhan) {
        String sql = "SELECT ma_bao_gia, thoi_gian_lap, trang_thai, ly_do_tu_choi, ghi_chu, " +
                "ma_tiep_nhan, ma_nhan_vien FROM phieu_bao_gia " +
                "WHERE TRIM(ma_tiep_nhan) = TRIM(?) ORDER BY thoi_gian_lap DESC, ma_bao_gia DESC LIMIT 1";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(con, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy phiếu báo giá gần nhất", e);
        }
        return null;
    }

    public PhieuBaoGia timTheoMa(String maBaoGia) {
        String sql = "SELECT ma_bao_gia, thoi_gian_lap, trang_thai, ly_do_tu_choi, ghi_chu, " +
                "ma_tiep_nhan, ma_nhan_vien FROM phieu_bao_gia WHERE ma_bao_gia = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBaoGia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(con, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu báo giá theo mã: " + maBaoGia, e);
        }
        return null;
    }

    public boolean them(PhieuBaoGia pbg) {
        String sql = "INSERT INTO phieu_bao_gia (ma_bao_gia, thoi_gian_lap, trang_thai, ly_do_tu_choi, " +
                "ghi_chu, ma_tiep_nhan, ma_nhan_vien) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pbg.getMaBaoGia());
                ps.setTimestamp(2, Timestamp.valueOf(pbg.getThoiGianLap()));
                ps.setString(3, pbg.getTrangThai().name());
                ps.setString(4, pbg.getLyDoTuChoi());
                ps.setString(5, pbg.getGhiChu());
                ps.setString(6, pbg.getPhieuTiepNhan().getMaTiepNhan());
                ps.setString(7, pbg.getNhanVien().getMaNhanVien());
                ps.executeUpdate();

                for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
                    chiTietBaoGiaDAO.them(con, ctbg);
                    for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                        sanPhamDAO.capNhatSoLuongTon(con, pt.getSanPham().getMaSanPham(), pt.getSanPham().getSoLuongTon());
                    }
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
            throw new RuntimeException("Lỗi thêm phiếu báo giá", e);
        }
    }

    public boolean capNhat(PhieuBaoGia pbg) {
        String sql = "UPDATE phieu_bao_gia SET trang_thai = ?, ly_do_tu_choi = ?, ghi_chu = ?, ma_nhan_vien = ? WHERE ma_bao_gia = ?";
        try (Connection con = ConnectDB.ketNoi()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pbg.getTrangThai().name());
                ps.setString(2, pbg.getLyDoTuChoi());
                ps.setString(3, pbg.getGhiChu());
                ps.setString(4, pbg.getNhanVien() != null ? pbg.getNhanVien().getMaNhanVien() : null);
                ps.setString(5, pbg.getMaBaoGia());
                int rows = ps.executeUpdate();

                if (rows == 0) {
                    // Nếu phiếu báo giá chưa tồn tại, thực hiện INSERT mới
                    String sqlInsert = "INSERT INTO phieu_bao_gia (ma_bao_gia, thoi_gian_lap, trang_thai, ly_do_tu_choi, " +
                            "ghi_chu, ma_tiep_nhan, ma_nhan_vien) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement psIns = con.prepareStatement(sqlInsert)) {
                        psIns.setString(1, pbg.getMaBaoGia());
                        psIns.setTimestamp(2, Timestamp.valueOf(pbg.getThoiGianLap() != null ? pbg.getThoiGianLap() : java.time.LocalDateTime.now()));
                        psIns.setString(3, pbg.getTrangThai().name());
                        psIns.setString(4, pbg.getLyDoTuChoi());
                        psIns.setString(5, pbg.getGhiChu());
                        psIns.setString(6, pbg.getPhieuTiepNhan().getMaTiepNhan());
                        psIns.setString(7, pbg.getNhanVien() != null ? pbg.getNhanVien().getMaNhanVien() : null);
                        psIns.executeUpdate();
                    }
                }

                // Cập nhật hoặc thêm mới từng chi tiết dịch vụ và phụ tùng đề xuất
                if (pbg.getDanhSachChiTiet() != null) {
                    for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
                        ctbg.setPhieuBaoGia(pbg);
                        boolean daCapNhat = chiTietBaoGiaDAO.capNhat(con, ctbg);
                        if (!daCapNhat) {
                            chiTietBaoGiaDAO.them(con, ctbg);
                        } else {
                            if (ctbg.getDanhSachPhuTung() != null) {
                                for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                                    pt.setChiTietBaoGia(ctbg);
                                    boolean ptDaCapNhat = chiTietDungPhuTungDAO.capNhat(con, pt);
                                    if (!ptDaCapNhat) {
                                        chiTietDungPhuTungDAO.them(con, pt);
                                    }
                                }
                            }
                        }
                    }
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
            throw new RuntimeException("Lỗi cập nhật phiếu báo giá", e);
        }
    }

    public boolean xoa(String maBaoGia) {
        String sql = "DELETE FROM phieu_bao_gia WHERE ma_bao_gia = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBaoGia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa phiếu báo giá", e);
        }
    }

    private PhieuBaoGia mapRow(Connection con, ResultSet rs) throws SQLException {
        PhieuTiepNhan ptn = phieuTiepNhanDAO.timTheoMa(rs.getString("ma_tiep_nhan"));
        NhanVien nv = nhanVienDAO.timTheoMa(rs.getString("ma_nhan_vien"));
        PhieuBaoGia pbg = new PhieuBaoGia(
                rs.getString("ma_bao_gia"),
                rs.getTimestamp("thoi_gian_lap").toLocalDateTime(),
                TrangThaiBaoGia.valueOf(rs.getString("trang_thai")),
                rs.getString("ly_do_tu_choi"),
                rs.getString("ghi_chu"),
                ptn,
                nv
        );
        pbg.setDanhSachChiTiet(chiTietBaoGiaDAO.layTheoPhieuBaoGia(con, pbg));
        return pbg;
    }
}
