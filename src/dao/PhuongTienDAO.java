package dao;

import util.ConnectDB;
import entity.HangXe;
import entity.KhachHang;
import entity.PhuongTien;
import enums.NhienLieu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhuongTienDAO {

    private static final String SELECT_JOIN =
            "SELECT pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat, pt.nhien_lieu, " +
                    "hx.ma_hang_xe, hx.ten_hang, hx.quoc_gia, " +
                    "kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.dia_chi, kh.gioi_tinh " +
                    "FROM phuong_tien pt " +
                    "JOIN hang_xe hx ON pt.ma_hang_xe = hx.ma_hang_xe " +
                    "JOIN khach_hang kh ON pt.ma_khach_hang = kh.ma_khach_hang ";

    public String layMaLonNhatTheoTienTo(String tienTo) {
        String sql = "SELECT MAX(ma_phuong_tien) FROM phuong_tien WHERE ma_phuong_tien LIKE ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã phương tiện lớn nhất", e);
        }
    }

    public List<PhuongTien> layTatCa() {
        List<PhuongTien> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY pt.ma_phuong_tien";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách phương tiện", e);
        }
        return ds;
    }

    public List<PhuongTien> timTheoKhachHang(String maKhachHang) {
        List<PhuongTien> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE pt.ma_khach_hang = ? ORDER BY pt.ma_phuong_tien";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phương tiện theo khách hàng", e);
        }
        return ds;
    }

    public PhuongTien timTheoMa(String maPhuongTien) {
        String sql = SELECT_JOIN + "WHERE pt.ma_phuong_tien = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhuongTien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phương tiện theo mã: " + maPhuongTien, e);
        }
        return null;
    }

    public PhuongTien timTheoBienSo(String bienSoXe) {
        String sql = SELECT_JOIN + "WHERE pt.bien_so_xe = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bienSoXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phương tiện theo biển số: " + bienSoXe, e);
        }
        return null;
    }

    public boolean them(PhuongTien pt) {
        String sql = "INSERT INTO phuong_tien (ma_phuong_tien, bien_so_xe, mau_xe, mau_sac_xe, " +
                "nam_san_xuat, nhien_lieu, ma_hang_xe, ma_khach_hang) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pt.getMaPhuongTien());
            ps.setString(2, pt.getBienSoXe());
            ps.setString(3, pt.getMauXe());
            ps.setString(4, pt.getMauSacXe());
            ps.setInt(5, pt.getNamSanXuat());
            ps.setString(6, pt.getNhienLieu().name());
            ps.setString(7, pt.getHangXe().getMaHangXe());
            ps.setString(8, pt.getKhachHang().getMaKhachHang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm phương tiện", e);
        }
    }

    public boolean capNhat(PhuongTien pt) {
        String sql = "UPDATE phuong_tien SET bien_so_xe = ?, mau_xe = ?, mau_sac_xe = ?, " +
                "nam_san_xuat = ?, nhien_lieu = ?, ma_hang_xe = ?, ma_khach_hang = ? " +
                "WHERE ma_phuong_tien = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pt.getBienSoXe());
            ps.setString(2, pt.getMauXe());
            ps.setString(3, pt.getMauSacXe());
            ps.setInt(4, pt.getNamSanXuat());
            ps.setString(5, pt.getNhienLieu().name());
            ps.setString(6, pt.getHangXe().getMaHangXe());
            ps.setString(7, pt.getKhachHang().getMaKhachHang());
            ps.setString(8, pt.getMaPhuongTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật phương tiện", e);
        }
    }

    public boolean xoa(String maPhuongTien) {
        String sql = "DELETE FROM phuong_tien WHERE ma_phuong_tien = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhuongTien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa phương tiện", e);
        }
    }

    private PhuongTien mapRow(ResultSet rs) throws SQLException {
        HangXe hx = new HangXe(
                rs.getString("ma_hang_xe"),
                rs.getString("ten_hang"),
                rs.getString("quoc_gia")
        );
        KhachHang kh = new KhachHang(
                rs.getString("ma_khach_hang"),
                rs.getString("ho_ten"),
                rs.getString("so_dien_thoai"),
                rs.getString("dia_chi"),
                rs.getString("gioi_tinh")
        );
        return new PhuongTien(
                rs.getString("ma_phuong_tien"),
                rs.getString("bien_so_xe"),
                rs.getString("mau_xe"),
                rs.getString("mau_sac_xe"),
                rs.getInt("nam_san_xuat"),
                NhienLieu.valueOf(rs.getString("nhien_lieu")),
                hx,
                kh
        );
    }
}
