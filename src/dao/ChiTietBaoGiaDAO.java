package dao;

import util.ConnectDB;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DichVu;
import entity.PhieuBaoGia;
import enums.ChuyenMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBaoGiaDAO {
    private final ChiTietDungPhuTungDAO chiTietDungPhuTungDAO = new ChiTietDungPhuTungDAO();

    public List<ChiTietBaoGia> layTheoPhieuBaoGia(Connection con, PhieuBaoGia phieuBaoGia) throws SQLException {
        List<ChiTietBaoGia> ds = new ArrayList<>();
        String sql = "SELECT ctbg.don_gia_dich_vu, ctbg.duoc_chap_nhan, ctbg.ghi_chu, " +
                "dv.ma_dich_vu, dv.ten_dich_vu, dv.mo_ta, dv.don_gia AS dv_don_gia, " +
                "dv.thue_vat, dv.co_phu_tung, dv.chuyen_mon, dv.trang_thai AS dv_trang_thai " +
                "FROM chi_tiet_bao_gia ctbg " +
                "JOIN dich_vu dv ON ctbg.ma_dich_vu = dv.ma_dich_vu " +
                "WHERE ctbg.ma_bao_gia = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phieuBaoGia.getMaBaoGia());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChuyenMon cm = null;
                    String cmStr = rs.getString("chuyen_mon");
                    if (cmStr != null && !cmStr.isBlank()) {
                        try {
                            cm = ChuyenMon.valueOf(cmStr);
                        } catch (Exception ignored) {
                        }
                    }
                    DichVu dv = new DichVu(
                            rs.getString("ma_dich_vu"),
                            rs.getString("ten_dich_vu"),
                            rs.getString("mo_ta"),
                            rs.getDouble("dv_don_gia"),
                            rs.getDouble("thue_vat"),
                            rs.getBoolean("co_phu_tung"),
                            cm,
                            rs.getBoolean("dv_trang_thai")
                    );
                    ChiTietBaoGia ctbg = new ChiTietBaoGia(
                            rs.getDouble("don_gia_dich_vu"),
                            rs.getBoolean("duoc_chap_nhan"),
                            rs.getString("ghi_chu"),
                            phieuBaoGia,
                            dv
                    );
                    ctbg.setDanhSachPhuTung(chiTietDungPhuTungDAO.layTheoChiTietBaoGia(con, ctbg));
                    ds.add(ctbg);
                }
            }
        }
        return ds;
    }

    public List<ChiTietBaoGia> layTheoPhieuBaoGia(PhieuBaoGia phieuBaoGia) {
        try (Connection con = ConnectDB.ketNoi()) {
            return layTheoPhieuBaoGia(con, phieuBaoGia);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách chi tiết báo giá", e);
        }
    }

    public boolean them(Connection con, ChiTietBaoGia ctbg) throws SQLException {
        String sql = "INSERT INTO chi_tiet_bao_gia (ma_bao_gia, ma_dich_vu, don_gia_dich_vu, duoc_chap_nhan, ghi_chu) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ctbg.getPhieuBaoGia().getMaBaoGia());
            ps.setString(2, ctbg.getDichVu().getMaDichVu());
            ps.setDouble(3, ctbg.getDonGiaDichVu());
            ps.setBoolean(4, ctbg.isDuocChapNhan());
            ps.setString(5, ctbg.getGhiChu());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                    pt.setChiTietBaoGia(ctbg);
                    chiTietDungPhuTungDAO.them(con, pt);
                }
            }
            return ok;
        }
    }

    public boolean them(ChiTietBaoGia ctbg) {
        try (Connection con = ConnectDB.ketNoi()) {
            return them(con, ctbg);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm chi tiết báo giá", e);
        }
    }

    public boolean capNhat(Connection con, ChiTietBaoGia ctbg) throws SQLException {
        String sql = "UPDATE chi_tiet_bao_gia SET don_gia_dich_vu = ?, duoc_chap_nhan = ?, ghi_chu = ? " +
                "WHERE ma_bao_gia = ? AND ma_dich_vu = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, ctbg.getDonGiaDichVu());
            ps.setBoolean(2, ctbg.isDuocChapNhan());
            ps.setString(3, ctbg.getGhiChu());
            ps.setString(4, ctbg.getPhieuBaoGia().getMaBaoGia());
            ps.setString(5, ctbg.getDichVu().getMaDichVu());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhat(ChiTietBaoGia ctbg) {
        try (Connection con = ConnectDB.ketNoi()) {
            return capNhat(con, ctbg);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật chi tiết báo giá", e);
        }
    }

    public boolean xoa(String maBaoGia, String maDichVu) {
        String sql = "DELETE FROM chi_tiet_bao_gia WHERE ma_bao_gia = ? AND ma_dich_vu = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maBaoGia);
            ps.setString(2, maDichVu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa chi tiết báo giá", e);
        }
    }
}

