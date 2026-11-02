package com.pengugarage.dao;

import com.pengugarage.model.entity.ChiTietHoaDon;
import com.pengugarage.model.entity.PhieuTiepNhan;
import com.pengugarage.model.entity.PhuongTien;

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
     * Lấy danh sách các xe thuộc sở hữu của một khách hàng cụ thể.
     *
     * @param khachHangId ID của khách hàng
     * @return Danh sách xe của khách
     */
    public List<PhuongTien> layTheoKhachHangId(int khachHangId) {
        List<PhuongTien> dsXe = new ArrayList<>();
        // TODO: [M07-T01 - Tuấn] Viết SQL SELECT * FROM PhuongTien WHERE khach_hang_id = ?
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
        // bien_so_xe, mau_xe, mau_sac_xe, nam_san_xuat, nhien_lieu, so_khung, so_may) VALUES (...)
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
}

