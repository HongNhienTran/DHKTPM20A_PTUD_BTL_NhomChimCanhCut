package com.pengugarage.dao;

import com.pengugarage.model.entity.*;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.pengugarage.model.enums.NhienLieu;
import com.pengugarage.model.enums.TrangThaiPhieuTiepNhan;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.*;
import java.time.LocalDateTime;
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
        String sql = "SELECT * FROM `phuong_tien` ORDER BY `id` DESC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhuongTien pt = new PhuongTien();
                pt.setId(rs.getInt("id"));
                pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                HangXe hx= new HangXe();
                hx.setId(rs.getInt("hang_xe_id"));
                pt.setHangXe(hx);
                pt.setBienSoXe(rs.getString("bien_so_xe"));
                pt.setMauXe(rs.getString("mau_xe"));
                pt.setMauSacXe(rs.getString("mau_sac_xe"));
                pt.setNamSanXuat(rs.getInt("nam_san_xuat"));
                String nl = rs.getString("nhien_lieu");
                if(nl.equals(NhienLieu.XANG.toString())) {
                    pt.setNhienLieu(NhienLieu.XANG);
                }
                else if(nl.equals(NhienLieu.DAU.toString())) {
                    pt.setNhienLieu(NhienLieu.DAU);
                }
                else if(nl.equals(NhienLieu.DIEN.toString())) {
                    pt.setNhienLieu(NhienLieu.DIEN);
                }
                else {pt.setNhienLieu(NhienLieu.HYBRID);}
                dsXe.add(pt);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn danh sách phương tiện: " + e.getMessage(), e);
        }
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
        String sql = "SELECT * FROM `phuong_tien` where 'id' = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhuongTien pt = new PhuongTien();
                    pt.setId(rs.getInt("id"));
                    pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                    KhachHang kh= new KhachHang();
                    kh.setId(rs.getInt("khach_hang_id"));
                    pt.setKhachHang(kh);
                    pt.setKhachHangId(rs.getInt("khach_hang_id"));
                    HangXe hx= new HangXe();
                    hx.setId(rs.getInt("hang_xe_id"));
                    pt.setHangXe(hx);
                    pt.setBienSoXe(rs.getString("bien_so_xe"));
                    pt.setMauXe(rs.getString("mau_xe"));
                    pt.setMauSacXe(rs.getString("mau_sac_xe"));
                    pt.setNamSanXuat(rs.getInt("nam_san_xuat"));
                    String nl = rs.getString("nhien_lieu");
                    if(nl.equals(NhienLieu.XANG.toString())) {
                        pt.setNhienLieu(NhienLieu.XANG);
                    }
                    else if(nl.equals(NhienLieu.DAU.toString())) {
                        pt.setNhienLieu(NhienLieu.DAU);
                    }
                    else if(nl.equals(NhienLieu.DIEN.toString())) {
                        pt.setNhienLieu(NhienLieu.DIEN);
                    }
                    else {pt.setNhienLieu(NhienLieu.HYBRID);}
                    return pt;
                }
                rs.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn phương tiện: " + e.getMessage(), e);
        }
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
        // TODO: [M07-T01 - Tuấn] Viết SQL SELECT * FROM PhuongTien WHERE khach_hang_id = ?
          String sql = "SELECT * FROM `phuong_tien` where 'khach_hang_id' = ?";
          try (Connection conn = CoSoDuLieuUtil.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, khachHangId);
              try (ResultSet rs = ps.executeQuery()) {
                  if (rs.next())
                  {
                      PhuongTien pt = new PhuongTien();
                      pt.setId(rs.getInt("id"));
                      pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                      KhachHang kh= new KhachHang();
                      kh.setId(rs.getInt("khach_hang_id"));
                      pt.setKhachHang(kh);
                      pt.setKhachHangId(rs.getInt("khach_hang_id"));
                      HangXe hx= new HangXe(); hx.setId(rs.getInt("hang_xe_id"));
                      pt.setHangXe(hx);
                      pt.setBienSoXe(rs.getString("bien_so_xe"));
                      pt.setMauXe(rs.getString("mau_xe"));
                      pt.setMauSacXe(rs.getString("mau_sac_xe"));
                      pt.setNamSanXuat(rs.getInt("nam_san_xuat"));
                      String nl = rs.getString("nhien_lieu");
                      if(nl.equals(NhienLieu.XANG.toString())) { pt.setNhienLieu(NhienLieu.XANG); }
                      else if(nl.equals(NhienLieu.DAU.toString())) { pt.setNhienLieu(NhienLieu.DAU); }
                      else if(nl.equals(NhienLieu.DIEN.toString())) { pt.setNhienLieu(NhienLieu.DIEN); }
                      else {pt.setNhienLieu(NhienLieu.HYBRID);} dsXe.add(pt); }
                  rs.close(); } }
          catch (SQLException e) { throw new RuntimeException("Lỗi truy vấn phương tiện: " + e.getMessage(), e); }
          return dsXe;
    }

    /**
     * Thêm mới một phương tiện vào cơ sở dữ liệu.
     *
     * @param pt Đối tượng PhuongTien
     * @return true nếu thêm thành công
     */
    public boolean them(PhuongTien pt) {
        // TODO: [M07-T02 - Tuấn] Viết SQL INSERT INTO PhuongTien (ma_phuong_tien, hang_xe_id, khach_hang_id,
        String sql = "INSERT INTO 'phuong_tien' (ma_phuong_tien, hang_xe_id, khach_hang_id,bien_so_xe,mau_xe,mau_sac_xe,nam_san_xuat,nhien_lieu,so_khung,so_may) values(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pt.getMaPhuongTien());
            ps.setInt(2, pt.getHangXeId());
            ps.setInt(3, pt.getKhachHangId());
            ps.setString(4, pt.getBienSoXe());
            ps.setString(5, pt.getMauXe());
            ps.setString(6, pt.getMauSacXe());
            ps.setInt(7, pt.getNamSanXuat());
            ps.setString(8,pt.getNhienLieu().toString());
            ps.setString(9,pt.getSoKhung());
            ps.setString(10,pt.getSoMay());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
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
        String sql = "Update 'phuong_tien' set ma_phuong_tien= ?,hang_xe_id= ?,khach_hang_id=?,bien_so_xe=?,mau_xe=?,mau_sac_xe=?,nam_san_xuat=?,nhien_lieu=?,so_khung=?,so_may=? where id=?";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pt.getMaPhuongTien());
            ps.setInt(2, pt.getHangXeId());
            ps.setInt(3, pt.getKhachHangId());
            ps.setString(4, pt.getBienSoXe());
            ps.setString(5, pt.getMauXe());
            ps.setString(6, pt.getMauSacXe());
            ps.setInt(7, pt.getNamSanXuat());
            ps.setString(8,pt.getNhienLieu().toString());
            ps.setString(9,pt.getSoKhung());
            ps.setString(10,pt.getSoMay());
            ps.setInt(11,pt.getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
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
        String sql = "SELECT * FROM `phieu_tiep_nhan` where phuong_tien_id = ? ORDER BY `thoi_gian_tiep_nhan` DESC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, phuongTienId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                {
                    PhieuTiepNhan ptn = new PhieuTiepNhan();
                    ptn.setId(rs.getInt("id"));
                    ptn.setMaTiepNhan(rs.getString("ma_tiep_nhan"));
                    PhuongTien pt = new PhuongTien();
                    pt.setId(rs.getInt("phuong_tien_id"));
                    ptn.setPhuongTien(pt);
                    ptn.setPhuongTienId(pt.getId());
                    ptn.setThoiGianTiepNhan(rs.getDate("thoi_gian_tiep_nhan").toLocalDate().atTime(rs.getTime("thoi_gian_tiep_nhan").toLocalTime()));
                    ptn.setTrangThai(TrangThaiPhieuTiepNhan.valueOf(rs.getString("thoi_gian_tiep_nhan")));
                    ptn.setSoKMHienTai(rs.getDouble("so_km_hien_tai"));
                    ptn.setYeuCauKhachHang(rs.getString("yeu_cau_khach_hang"));
                    ptn.setTinhTrangNoiThat(rs.getString("tinh_trang_noi_that"));
                    ptn.setTinhTrangNgoaiThat(rs.getString("tinh_trang_ngoai_that"));
                    ptn.setSoChiaKhoa(rs.getInt("so_chia_khoa"));
                    ptn.setGhiChu(rs.getString("ghi_chu"));
                    dsLichSu.add(ptn);
                }
                rs.close(); }
        }
        catch (SQLException e) { throw new RuntimeException("Lỗi truy vấn phiếu tiếp nhận: " + e.getMessage(), e); }
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

