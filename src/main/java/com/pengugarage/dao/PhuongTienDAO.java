package com.pengugarage.dao;

import com.pengugarage.model.entity.*;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Phương Tiện (PhuongTien).
 * Chịu trách nhiệm truy vấn CRUD phương tiện và lịch sử sửa chữa xe.
 * 
 * Phụ trách: Tuấn (Module Quản lý phương tiện).
 */
public class PhuongTienDAO {

    /**
     * Lấy toàn bộ danh sách phương tiện trong gara (kèm thông tin Hãng xe và Khách hàng).
     *
     * @return Danh sách PhuongTien
     */
    public List<PhuongTien> layTatCa() {
        List<PhuongTien> dsXe = new ArrayList<>();
        // TODO: [M07-T01 - Tuấn] Viết SQL SELECT * FROM PhuongTien JOIN HangXe JOIN KhachHang
        return dsXe;
    }

    /**
     * Lấy thông tin chi tiết một phương tiện theo ID.
     *
     * @param id ID phương tiện
     * @return Đối tượng PhuongTien hoặc null
     */
    public PhuongTien timTheoId(int id) {
        // TODO: [M07-T01 - Tuấn] Viết SQL SELECT * FROM PhuongTien WHERE id = ?
        return null;
    }

    /**
     * Tìm phương tiện theo Biển số xe (kèm thông tin Hãng xe và Khách hàng).
     * Phục vụ tra cứu phương tiện khi tiếp nhận xe.
     *
     * @param bienSo Biển số xe cần tìm
     * @return Đối tượng PhuongTien hoặc null
     */
    public PhuongTien timTheoBienSo(String bienSo) {
        if (bienSo == null || bienSo.trim().isEmpty()) return null;
        String sql = """
            SELECT pt.*,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh
            FROM phuong_tien pt
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            WHERE REPLACE(REPLACE(pt.bien_so_xe, '-', ''), '.', '') = REPLACE(REPLACE(?, '-', ''), '.', '')
               OR pt.bien_so_xe LIKE ?
            LIMIT 1
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo.trim());
            ps.setString(2, "%" + bienSo.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhuongTien pt = new PhuongTien();
                    pt.setId(rs.getInt("id"));
                    pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                    pt.setBienSoXe(rs.getString("bien_so_xe"));
                    pt.setMauXe(rs.getString("mau_xe"));
                    pt.setMauSacXe(rs.getString("mau_sac_xe"));
                    pt.setNamSanXuat(rs.getInt("nam_san_xuat"));

                    HangXe hx = new HangXe();
                    hx.setId(rs.getInt("hx_id"));
                    hx.setTenHang(rs.getString("ten_hang"));
                    pt.setHangXe(hx);

                    KhachHang kh = new KhachHang();
                    kh.setId(rs.getInt("kh_id"));
                    kh.setMaKhachHang(rs.getString("ma_khach_hang"));
                    kh.setHoTen(rs.getString("ho_ten"));
                    kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                    try {
                        kh.setGioiTinh(rs.getString("gioi_tinh"));
                    } catch (SQLException ignored) {}
                    pt.setKhachHang(kh);

                    return pt;
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Tự động sinh mã phương tiện PT001, PT002...
     */
    public String sinhMaPhuongTienTuDong() {
        String sql = "SELECT MAX(id) FROM phuong_tien";
        int nextId = 1;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
        } catch (SQLException ignored) {
        }
        return String.format("PT%03d", nextId);
    }

    /**
     * Lấy danh sách các xe thuộc sở hữu của một khách hàng cụ thể.
     *
     * @param khachHangId ID của khách hàng
     * @return Danh sách xe của khách
     */
    public List<PhuongTien> layTheoKhachHangId(int khachHangId) {
        List<PhuongTien> dsXe = new ArrayList<>();
        String sql = """
            SELECT pt.*, hx.id AS hx_id, hx.ten_hang
            FROM phuong_tien pt
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            WHERE pt.khach_hang_id = ?
            ORDER BY pt.id DESC
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, khachHangId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhuongTien pt = new PhuongTien();
                    pt.setId(rs.getInt("id"));
                    pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                    pt.setBienSoXe(rs.getString("bien_so_xe"));
                    pt.setMauXe(rs.getString("mau_xe"));
                    pt.setMauSacXe(rs.getString("mau_sac_xe"));
                    pt.setNamSanXuat(rs.getInt("nam_san_xuat"));

                    HangXe hx = new HangXe();
                    hx.setId(rs.getInt("hx_id"));
                    hx.setTenHang(rs.getString("ten_hang"));
                    pt.setHangXe(hx);

                    dsXe.add(pt);
                }
            }
        } catch (SQLException ignored) {
        }
        return dsXe;
    }

    /**
     * Thêm mới một phương tiện vào cơ sở dữ liệu.
     *
     * @param pt Đối tượng PhuongTien
     * @return true nếu thêm thành công
     */
    public boolean them(PhuongTien pt) {
        if (pt == null) return false;
        if (pt.getMaPhuongTien() == null || pt.getMaPhuongTien().trim().isEmpty()) {
            pt.setMaPhuongTien(sinhMaPhuongTienTuDong());
        }
        String sql = """
            INSERT INTO phuong_tien (ma_phuong_tien, hang_xe_id, khach_hang_id, bien_so_xe, mau_xe, mau_sac_xe, nam_san_xuat, nhien_lieu, so_khung, so_may)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pt.getMaPhuongTien());
            ps.setInt(2, pt.getHangXe() != null ? pt.getHangXe().getId() : (pt.getHangXeId() > 0 ? pt.getHangXeId() : 1));
            ps.setInt(3, pt.getKhachHang() != null ? pt.getKhachHang().getId() : (pt.getKhachHangId() > 0 ? pt.getKhachHangId() : 1));
            ps.setString(4, pt.getBienSoXe() != null ? pt.getBienSoXe().toUpperCase().trim() : "");
            ps.setString(5, pt.getMauXe() != null ? pt.getMauXe().trim() : "");
            ps.setString(6, pt.getMauSacXe() != null ? pt.getMauSacXe().trim() : "Trắng");
            ps.setInt(7, pt.getNamSanXuat() > 0 ? pt.getNamSanXuat() : 2022);
            ps.setString(8, pt.getNhienLieu() != null ? pt.getNhienLieu().name() : "XANG");
            ps.setString(9, pt.getSoKhung() != null ? pt.getSoKhung().trim() : "");
            ps.setString(10, pt.getSoMay() != null ? pt.getSoMay().trim() : "");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pt.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * Cập nhật thông tin phương tiện.
     *
     * @param pt Đối tượng PhuongTien cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean capNhat(PhuongTien pt) {
        // TODO: [M07-T03 - Tuấn] Viết SQL UPDATE PhuongTien SET hang_xe_id = ?, khach_hang_id = ?,
        // bien_so_xe = ?, mau_xe = ?, mau_sac_xe = ?, nam_san_xuat = ?, nhien_lieu = ?, so_khung = ?, so_may = ? WHERE id = ?
        return false;
    }

    /**
     * Lấy toàn bộ lịch sử sửa chữa / bảo dưỡng của một xe ô tô theo ID phương tiện.
     * Truy vấn thông tin các phiếu tiếp nhận, tình trạng xe, cố vấn, ngày ra vào xưởng
     * và các dịch vụ/phụ tùng đã thay thế.
     *
     * @param phuongTienId ID của phương tiện cần xem lịch sử
     * @return Danh sách các phiếu tiếp nhận / lần bảo dưỡng của xe
     */
    public List<PhieuTiepNhan> layLichSuSuaChua(int phuongTienId) {
        List<PhieuTiepNhan> dsLichSu = new ArrayList<>();
        // TODO: [M07-T04 - Tuấn] Viết SQL SELECT * FROM PhieuTiepNhan WHERE phuong_tien_id = ? ORDER BY thoi_gian_vao DESC
        return dsLichSu;
    }

    /**
     * Lấy chi tiết các hạng mục phụ tùng và dịch vụ trong một lần sửa chữa cụ thể.
     *
     * @param phieuTiepNhanId ID phiếu tiếp nhận
     * @return Danh sách ChiTietHoaDon
     */
    public List<ChiTietHoaDon> layChiTietHaoMucSuaChua(int phieuTiepNhanId) {
        List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
        // TODO: [M07-T04 - Tuấn] Viết SQL JOIN HoaDon và ChiTietHoaDon WHERE phieu_tiep_nhan_id = ?
        return dsChiTiet;
    }

    /*Tìm phương tiện theo biển số xe (biển số xe là duy nhất trong hệ thống).
     *
     * @param bienSoXe Biển số xe cần tìm
     * @return Đối tượng PhuongTien hoặc null nếu không tìm thấy
     */
    //Nhiên (module tiếp nhận)
    public PhuongTien timTheoBienSoXe(String bienSo) {
        if (bienSo == null || bienSo.trim().isEmpty()) {
            return null;
        }
        String sql ="SELECT pt.*, \n" +
                "               hx.id AS hx_id, hx.ten_hang, \n" +
                "               kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh\n" +
                "        FROM phuong_tien pt\n" +
                "        LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id\n" +
                "        LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id\n" +
                "        WHERE REPLACE(REPLACE(pt.bien_so_xe, '-', ''), '.', '') = REPLACE(REPLACE(?, '-', ''), '.', '')\n" +
                "           OR pt.bien_so_xe LIKE ?\n" +
                "        LIMIT 1";
        try(Connection conn = CoSoDuLieuUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo);
            ps.setString(2, "%" + bienSo + "%");

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    PhuongTien pt = new PhuongTien();
                    pt.setId(rs.getInt("id"));
                    pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                    pt.setBienSoXe(rs.getString("bien_so_xe"));
                    pt.setMauXe(rs.getString("mau_xe"));
                    pt.setMauSacXe(rs.getString("mau_sac_xe"));
                    pt.setNamSanXuat(rs.getInt("nam_san_xuat"));

                    // Gán Hãng xe
                    HangXe hx = new HangXe();
                    hx.setId(rs.getInt("hx_id"));
                    hx.setTenHang(rs.getString("ten_hang"));
                    pt.setHangXe(hx);

                    // Gán Khách hàng
                    KhachHang kh = new KhachHang();
                    kh.setId(rs.getInt("kh_id"));
                    kh.setMaKhachHang(rs.getString("ma_khach_hang"));
                    kh.setHoTen(rs.getString("ho_ten"));
                    kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                    try {
                        kh.setGioiTinh(rs.getString("gioi_tinh"));
                    } catch (SQLException ignored) {}
                    pt.setKhachHang(kh);

                    return pt;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}

