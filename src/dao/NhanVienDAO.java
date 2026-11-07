package dao;

import util.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.ChuyenMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private static final String SELECT_JOIN =
            "SELECT nv.ma_nhan_vien, nv.ho_ten, nv.so_dien_thoai, nv.dia_chi, " +
                    "nv.chuc_vu, nv.trang_thai AS nv_trang_thai, nv.chuyen_mon, " +
                    "tk.ten_dang_nhap, tk.mat_khau_ma_hoa, tk.trang_thai AS tk_trang_thai " +
                    "FROM nhan_vien nv " +
                    "LEFT JOIN tai_khoan tk ON nv.ma_nhan_vien = tk.ma_nhan_vien ";

    public String layMaLonNhat() {
        String sql = "SELECT MAX(ma_nhan_vien) FROM nhan_vien";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã nhân viên lớn nhất", e);
        }
    }

    public List<NhanVien> layTatCa() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY nv.ma_nhan_vien";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách nhân viên", e);
        }
        return ds;
    }

    public List<NhanVien> timTheoChucVu(ChucVu chucVu) {
        List<NhanVien> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE nv.chuc_vu = ? ORDER BY nv.ma_nhan_vien";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, chucVu.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm nhân viên theo chức vụ", e);
        }
        return ds;
    }

    public NhanVien timTheoMa(String maNhanVien) {
        String sql = SELECT_JOIN + "WHERE nv.ma_nhan_vien = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm nhân viên theo mã: " + maNhanVien, e);
        }
        return null;
    }

    public NhanVien timTheoSoDienThoai(String soDienThoai) {
        String sql = SELECT_JOIN + "WHERE nv.so_dien_thoai = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soDienThoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm nhân viên theo số điện thoại: " + soDienThoai, e);
        }
        return null;
    }

    public boolean them(NhanVien nv) {
        String sql = "INSERT INTO nhan_vien (ma_nhan_vien, ho_ten, so_dien_thoai, dia_chi, chuc_vu, trang_thai, chuyen_mon) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nv.getMaNhanVien());
                ps.setString(2, nv.getHoTen());
                ps.setString(3, nv.getSoDienThoai());
                ps.setString(4, nv.getDiaChi());
                ps.setString(5, nv.getChucVu().name());
                ps.setBoolean(6, nv.isTrangThai());
                ps.setString(7, nv.getChuyenMon() != null ? nv.getChuyenMon().name() : null);
                ps.executeUpdate();

                if (nv.getTaiKhoan() != null) {
                    taiKhoanDAO.them(con, nv.getTaiKhoan());
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
            throw new RuntimeException("Lỗi thêm nhân viên kèm tài khoản", e);
        }
    }

    public boolean capNhat(NhanVien nv) {
        String sql = "UPDATE nhan_vien SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, chuc_vu = ?, " +
                "trang_thai = ?, chuyen_mon = ? WHERE ma_nhan_vien = ?";
        try (Connection con = ConnectDB.ketNoi()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nv.getHoTen());
                ps.setString(2, nv.getSoDienThoai());
                ps.setString(3, nv.getDiaChi());
                ps.setString(4, nv.getChucVu().name());
                ps.setBoolean(5, nv.isTrangThai());
                ps.setString(6, nv.getChuyenMon() != null ? nv.getChuyenMon().name() : null);
                ps.setString(7, nv.getMaNhanVien());
                int affected = ps.executeUpdate();

                if (affected > 0 && nv.getTaiKhoan() != null) {
                    taiKhoanDAO.capNhat(con, nv.getTaiKhoan());
                }

                con.commit();
                return affected > 0;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật nhân viên", e);
        }
    }

    public boolean xoa(String maNhanVien) {
        String sql = "DELETE FROM nhan_vien WHERE ma_nhan_vien = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa nhân viên", e);
        }
    }

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        String cmStr = rs.getString("chuyen_mon");
        ChuyenMon chuyenMon = cmStr != null ? ChuyenMon.valueOf(cmStr) : null;

        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getString("ma_nhan_vien"));
        nv.setHoTen(rs.getString("ho_ten"));
        nv.setSoDienThoai(rs.getString("so_dien_thoai"));
        nv.setDiaChi(rs.getString("dia_chi"));
        nv.setChucVu(ChucVu.valueOf(rs.getString("chuc_vu")));
        nv.setTrangThai(rs.getBoolean("nv_trang_thai"));
        nv.setChuyenMon(chuyenMon);

        String tenDN = rs.getString("ten_dang_nhap");
        if (tenDN != null) {
            new TaiKhoan(
                    tenDN,
                    rs.getString("mat_khau_ma_hoa"),
                    rs.getBoolean("tk_trang_thai"),
                    nv
            );
        }
        return nv;
    }
}
