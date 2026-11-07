package dao;

import util.ConnectDB;
import entity.ChiTietHoaDon;
import entity.DichVu;
import entity.HoaDon;
import entity.SanPham;
import enums.LoaiHangMuc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public List<ChiTietHoaDon> layTheoHoaDon(Connection con, HoaDon hoaDon) throws SQLException {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        String sql = "SELECT loai_hang_muc, ma_dich_vu, ma_san_pham, so_luong, don_gia " +
                "FROM chi_tiet_hoa_don WHERE ma_hoa_don = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hoaDon.getMaHoaDon());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiHangMuc loai = LoaiHangMuc.valueOf(rs.getString("loai_hang_muc"));
                    String maDV = rs.getString("ma_dich_vu");
                    String maSP = rs.getString("ma_san_pham");
                    DichVu dv = maDV != null ? dichVuDAO.timTheoMa(maDV) : null;
                    SanPham sp = maSP != null ? sanPhamDAO.timTheoMa(maSP) : null;
                    ds.add(new ChiTietHoaDon(
                            loai,
                            rs.getInt("so_luong"),
                            rs.getDouble("don_gia"),
                            hoaDon,
                            dv,
                            sp
                    ));
                }
            }
        }
        return ds;
    }

    public List<ChiTietHoaDon> layTheoHoaDon(HoaDon hoaDon) {
        try (Connection con = ConnectDB.ketNoi()) {
            return layTheoHoaDon(con, hoaDon);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách chi tiết hóa đơn", e);
        }
    }

    public boolean them(Connection con, ChiTietHoaDon cthd) throws SQLException {
        String sql = "INSERT INTO chi_tiet_hoa_don " +
                "(ma_hoa_don, loai_hang_muc, ma_dich_vu, ma_san_pham, so_luong, don_gia) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cthd.getHoaDon().getMaHoaDon());
            ps.setString(2, cthd.getLoaiHangMuc().name());
            if (cthd.getDichVu() != null) {
                ps.setString(3, cthd.getDichVu().getMaDichVu());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            if (cthd.getSanPham() != null) {
                ps.setString(4, cthd.getSanPham().getMaSanPham());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.setInt(5, cthd.getSoLuong());
            ps.setDouble(6, cthd.getDonGia());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean them(ChiTietHoaDon cthd) {
        try (Connection con = ConnectDB.ketNoi()) {
            return them(con, cthd);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm chi tiết hóa đơn", e);
        }
    }

    public boolean xoaTheoHoaDon(Connection con, String maHoaDon) throws SQLException {
        String sql = "DELETE FROM chi_tiet_hoa_don WHERE ma_hoa_don = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            return ps.executeUpdate() >= 0;
        }
    }
}

