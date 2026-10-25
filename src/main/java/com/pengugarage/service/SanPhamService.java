package com.pengugarage.service;

import com.pengugarage.dao.SanPhamDAO;
import com.pengugarage.model.entity.SanPham;

import java.util.List;

public class SanPhamService {
    private final SanPhamDAO sanPhamDAO;

    public SanPhamService() {
        this.sanPhamDAO = new SanPhamDAO();
    }

    // =========================================================================
    // TASK M02-T01: Hiển thị danh sách, hiển thị chi tiết sản phẩm, lọc sản phẩm trên giao diện
    // Người thực hiện: Nhiên
    // =========================================================================
    public List<SanPham> layTatCaSanPham() {
        return sanPhamDAO.layTatCa();
    }

    public SanPham layChiTietSanPhamTheoID(int id) {
        return sanPhamDAO.layChiTietSanPhamTheoID(id);
    }

    public SanPham layChiTietSanPhamTheoId(int id) {
        return layChiTietSanPhamTheoID(id);
    }

    public List<String> layDanhSachDonViTinh() {
        return sanPhamDAO.layDanhSachDonViTinh();
    }

    // =========================================================================
    // TASK M02-T03: Nhập 1 thông tin sản phẩm và thêm, thêm bằng file import (exel)
    // Người thực hiện: Phong
    // =========================================================================
    public boolean them(SanPham sp) {
        // TODO: Phong thực hiện chức năng nhập 1 thông tin sản phẩm và thêm
        return sanPhamDAO.them(sp);
    }

    public boolean themDanhSachSanPham(List<SanPham> danhSach) {
        // TODO: Phong thực hiện chức năng thêm danh sách sản phẩm bằng file import (exel)
        return sanPhamDAO.themDanhSachSanPham(danhSach);
    }

    // =========================================================================
    // TASK M02-T04: Hiển thị thông tin sản phẩm và cho phép chỉnh sửa
    // Người thực hiện: Tuấn
    // =========================================================================
    public boolean capNhat(SanPham sp) {
        // TODO: Tuấn thực hiện chức năng cập nhật thông tin sản phẩm
        return sanPhamDAO.capNhat(sp);
    }

    public boolean capNhatSanPham(SanPham sp) {
        return capNhat(sp);
    }
}
