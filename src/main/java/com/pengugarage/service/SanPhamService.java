package com.pengugarage.service;

import com.pengugarage.dao.SanPhamDAO;
import com.pengugarage.model.entity.SanPham;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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
        Set<String> maTrongDanhSach = new HashSet<>();
        kiemTraSanPham(sp, maTrongDanhSach);
        return sanPhamDAO.them(sp);
    }

    public boolean themDanhSachSanPham(List<SanPham> danhSach) {
        // TODO: Phong thực hiện chức năng thêm danh sách sản phẩm bằng file import (exel)
        if (danhSach == null || danhSach.isEmpty()) {
            throw new IllegalArgumentException("File Excel không có sản phẩm.");
        }

        Set<String> maTrongDanhSach = new HashSet<>();
        for (SanPham sp : danhSach) {
            kiemTraSanPham(sp, maTrongDanhSach);
        }
        return sanPhamDAO.themDanhSachSanPham(danhSach);
    }
    private void kiemTraSanPham(SanPham sp, Set<String> maTrongDanhSach) {
        if (sp == null) {
            throw new IllegalArgumentException("Thông tin sản phẩm không hợp lệ.");
        }

        String ma = sp.getMaSanPham() == null ? "" : sp.getMaSanPham().trim();
        if (ma.isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        }

        if (!maTrongDanhSach.add(ma.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Mã sản phẩm bị trùng trong danh sách: " + ma);
        }
        if (sanPhamDAO.kiemTraMaSanPhamTonTai(ma)) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + ma);
        }

        if (sp.getTenSanPham() == null || sp.getTenSanPham().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        }
        if (sp.getDanhMucId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn danh mục hợp lệ.");
        }
        if (sp.getDonViTinh() == null || sp.getDonViTinh().isBlank()) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống.");
        }
        if (!Double.isFinite(sp.getGiaNhap()) || !Double.isFinite(sp.getDonGiaBan())
                || sp.getGiaNhap() < 0 || sp.getDonGiaBan() < sp.getGiaNhap()) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn hoặc bằng giá nhập.");
        }
        if (!Double.isFinite(sp.getThueVAT()) || sp.getThueVAT() < 0 || sp.getThueVAT() > 100) {
            throw new IllegalArgumentException("Thuế VAT phải nằm trong khoảng 0–100.");
        }
        if (sp.getSoLuongTon() < 0 || sp.getSoLuongTonToiThieu() < 0) {
            throw new IllegalArgumentException("Số lượng tồn và tồn tối thiểu phải từ 0 trở lên.");
        }

        sp.setMaSanPham(ma);
        sp.setTenSanPham(sp.getTenSanPham().trim());
        sp.setDonViTinh(sp.getDonViTinh().trim());
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
