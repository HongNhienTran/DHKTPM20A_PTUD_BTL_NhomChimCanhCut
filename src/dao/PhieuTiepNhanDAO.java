package dao;

import util.ConnectDB;
import entity.HangXe;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import enums.ChucVu;
import enums.ChuyenMon;
import enums.NhienLieu;
import enums.TrangThaiPhieuTiepNhan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhieuTiepNhanDAO {

    private static final String SELECT_JOIN =
            "SELECT ptn.ma_tiep_nhan, ptn.thoi_gian_tiep_nhan, ptn.trang_thai, ptn.so_km_hien_tai, " +
                    "ptn.yeu_cau_khach_hang, ptn.tinh_trang_noi_that, ptn.tinh_trang_ngoai_that, " +
                    "ptn.so_chia_khoa, ptn.ghi_chu, " +
                    "pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat, pt.nhien_lieu, " +
                    "hx.ma_hang_xe, hx.ten_hang, hx.quoc_gia, " +
                    "kh.ma_khach_hang, kh.ho_ten AS kh_ho_ten, kh.so_dien_thoai AS kh_sdt, " +
                    "kh.dia_chi AS kh_dia_chi, kh.gioi_tinh, " +
                    "nv.ma_nhan_vien, nv.ho_ten AS nv_ho_ten, nv.so_dien_thoai AS nv_sdt, " +
                    "nv.dia_chi AS nv_dia_chi, nv.chuc_vu, nv.trang_thai AS nv_trang_thai, nv.chuyen_mon " +
                    "FROM phieu_tiep_nhan ptn " +
                    "JOIN phuong_tien pt ON ptn.ma_phuong_tien = pt.ma_phuong_tien " +
                    "JOIN hang_xe hx ON pt.ma_hang_xe = hx.ma_hang_xe " +
                    "JOIN khach_hang kh ON pt.ma_khach_hang = kh.ma_khach_hang " +
                    "JOIN nhan_vien nv ON ptn.ma_nhan_vien = nv.ma_nhan_vien ";

    public String layMaLonNhatTheoTienTo(String tienTo) {
        String sql = "SELECT MAX(ma_tiep_nhan) FROM phieu_tiep_nhan WHERE ma_tiep_nhan LIKE ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã phiếu tiếp nhận lớn nhất", e);
        }
    }

    public List<PhieuTiepNhan> layTatCa() {
        List<PhieuTiepNhan> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY ptn.thoi_gian_tiep_nhan DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách phiếu tiếp nhận", e);
        }
        return ds;
    }

    public List<PhieuTiepNhan> timTheoPhuongTien(String maPhuongTien) {
        List<PhieuTiepNhan> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE ptn.ma_phuong_tien = ? ORDER BY ptn.thoi_gian_tiep_nhan DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhuongTien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu tiếp nhận theo phương tiện", e);
        }
        return ds;
    }

    public List<PhieuTiepNhan> timTheoTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        List<PhieuTiepNhan> ds = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE ptn.trang_thai = ? ORDER BY ptn.thoi_gian_tiep_nhan DESC";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu tiếp nhận theo trạng thái", e);
        }
        return ds;
    }

    public PhieuTiepNhan timTheoMa(String maTiepNhan) {
        String sql = SELECT_JOIN + "WHERE ptn.ma_tiep_nhan = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phiếu tiếp nhận theo mã: " + maTiepNhan, e);
        }
        return null;
    }

    public boolean them(PhieuTiepNhan ptn) {
        String sql = "INSERT INTO phieu_tiep_nhan (ma_tiep_nhan, thoi_gian_tiep_nhan, trang_thai, " +
                "so_km_hien_tai, yeu_cau_khach_hang, tinh_trang_noi_that, tinh_trang_ngoai_that, " +
                "so_chia_khoa, ghi_chu, ma_phuong_tien, ma_nhan_vien) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ptn.getMaTiepNhan());
            ps.setTimestamp(2, Timestamp.valueOf(ptn.getThoiGianTiepNhan()));
            ps.setString(3, ptn.getTrangThai().name());
            ps.setDouble(4, ptn.getSoKMHienTai());
            ps.setString(5, ptn.getYeuCauKhachHang());
            ps.setString(6, ptn.getTinhTrangNoiThat());
            ps.setString(7, ptn.getTinhTrangNgoaiThat());
            ps.setInt(8, ptn.getSoChiaKhoa());
            ps.setString(9, ptn.getGhiChu());
            ps.setString(10, ptn.getPhuongTien().getMaPhuongTien());
            ps.setString(11, ptn.getNhanVien().getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm phiếu tiếp nhận", e);
        }
    }

    public boolean capNhat(PhieuTiepNhan ptn) {
        String sql = "UPDATE phieu_tiep_nhan SET thoi_gian_tiep_nhan = ?, trang_thai = ?, " +
                "so_km_hien_tai = ?, yeu_cau_khach_hang = ?, tinh_trang_noi_that = ?, " +
                "tinh_trang_ngoai_that = ?, so_chia_khoa = ?, ghi_chu = ?, " +
                "ma_phuong_tien = ?, ma_nhan_vien = ? WHERE ma_tiep_nhan = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(ptn.getThoiGianTiepNhan()));
            ps.setString(2, ptn.getTrangThai().name());
            ps.setDouble(3, ptn.getSoKMHienTai());
            ps.setString(4, ptn.getYeuCauKhachHang());
            ps.setString(5, ptn.getTinhTrangNoiThat());
            ps.setString(6, ptn.getTinhTrangNgoaiThat());
            ps.setInt(7, ptn.getSoChiaKhoa());
            ps.setString(8, ptn.getGhiChu());
            ps.setString(9, ptn.getPhuongTien().getMaPhuongTien());
            ps.setString(10, ptn.getNhanVien().getMaNhanVien());
            ps.setString(11, ptn.getMaTiepNhan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật phiếu tiếp nhận", e);
        }
    }

    public boolean capNhatTrangThai(String maTiepNhan, TrangThaiPhieuTiepNhan trangThai) {
        String sql = "UPDATE phieu_tiep_nhan SET trang_thai = ? WHERE ma_tiep_nhan = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai.name());
            ps.setString(2, maTiepNhan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật trạng thái phiếu tiếp nhận", e);
        }
    }

    public boolean xoa(String maTiepNhan) {
        String sql = "DELETE FROM phieu_tiep_nhan WHERE ma_tiep_nhan = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTiepNhan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa phiếu tiếp nhận", e);
        }
    }

    private PhieuTiepNhan mapRow(ResultSet rs) throws SQLException {
        HangXe hx = new HangXe(
                rs.getString("ma_hang_xe"),
                rs.getString("ten_hang"),
                rs.getString("quoc_gia")
        );
        KhachHang kh = new KhachHang(
                rs.getString("ma_khach_hang"),
                rs.getString("kh_ho_ten"),
                rs.getString("kh_sdt"),
                rs.getString("kh_dia_chi"),
                rs.getString("gioi_tinh")
        );
        PhuongTien pt = new PhuongTien(
                rs.getString("ma_phuong_tien"),
                rs.getString("bien_so_xe"),
                rs.getString("mau_xe"),
                rs.getString("mau_sac_xe"),
                rs.getInt("nam_san_xuat"),
                NhienLieu.valueOf(rs.getString("nhien_lieu")),
                hx,
                kh
        );
        String cmStr = rs.getString("chuyen_mon");
        NhanVien nv = new NhanVien(
                rs.getString("ma_nhan_vien"),
                rs.getString("nv_ho_ten"),
                rs.getString("nv_sdt"),
                rs.getString("nv_dia_chi"),
                ChucVu.valueOf(rs.getString("chuc_vu")),
                rs.getBoolean("nv_trang_thai"),
                cmStr != null ? ChuyenMon.valueOf(cmStr) : null
        );
        return new PhieuTiepNhan(
                rs.getString("ma_tiep_nhan"),
                rs.getTimestamp("thoi_gian_tiep_nhan").toLocalDateTime(),
                TrangThaiPhieuTiepNhan.valueOf(rs.getString("trang_thai")),
                rs.getDouble("so_km_hien_tai"),
                rs.getString("yeu_cau_khach_hang"),
                rs.getString("tinh_trang_noi_that"),
                rs.getString("tinh_trang_ngoai_that"),
                rs.getInt("so_chia_khoa"),
                rs.getString("ghi_chu"),
                pt,
                nv
        );
    }
}
