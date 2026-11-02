package com.pengugarage.service;

import com.pengugarage.dao.DichVuDAO;
import com.pengugarage.model.entity.DichVu;

import java.util.List;

/**
 * Service xử lý nghiệp vụ Quản Lý Dịch Vụ Sửa Chữa (DichVu)
 */
public class DichVuService {

    private final DichVuDAO dichVuDAO;

    public DichVuService() {
        this.dichVuDAO = new DichVuDAO();
    }

    // =========================================================================
    // TASK M03-T01: Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện
    // Người thực hiện: Nhiên
    // =========================================================================
    public List<DichVu> layTatCaDichVu() {
        return dichVuDAO.layTatCa();
    }

    public DichVu layChiTietDichVuTheoID(int id) {
        if (id <= 0) {
            return null;
        }
        return dichVuDAO.layChiTietDichVuTheoID(id);
    }

    public DichVu layChiTietDichVuTheoMa(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return null;
        }
        return dichVuDAO.layChiTietDichVuTheoMa(maDichVu.trim());
    }

    public boolean kiemTraTonTaiMa(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return false;
        }
        return dichVuDAO.tonTaiMaDichVu(maDichVu.trim());
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    public boolean them(DichVu dv) {
        if (dv == null) {
            throw new IllegalArgumentException("Thông tin dịch vụ không được để trống.");
        }

        if (dv.getMaDichVu() == null || dv.getMaDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã dịch vụ không được để trống.");
        }
        dv.setMaDichVu(dv.getMaDichVu().trim().toUpperCase());

        if (dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống.");
        }
        dv.setTenDichVu(dv.getTenDichVu().trim());

        if (!Double.isFinite(dv.getDonGia()) || dv.getDonGia() < 0) {
            throw new IllegalArgumentException("Đơn giá dịch vụ phải là số không âm.");
        }

        if (!Double.isFinite(dv.getThueVAT()) || dv.getThueVAT() < 0 || dv.getThueVAT() > 100) {
            throw new IllegalArgumentException("Thuế VAT phải từ 0% đến 100%.");
        }

        if (dichVuDAO.tonTaiMaDichVu(dv.getMaDichVu())) {
            throw new IllegalArgumentException("Mã dịch vụ '" + dv.getMaDichVu() + "' đã tồn tại.");
        }

        return dichVuDAO.them(dv);
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    public boolean capNhat(DichVu dv) {
        if (dv == null) {
            throw new IllegalArgumentException("Thông tin dịch vụ không được để trống.");
        }

        if (dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống.");
        }
        dv.setTenDichVu(dv.getTenDichVu().trim());

        if (!Double.isFinite(dv.getDonGia()) || dv.getDonGia() < 0) {
            throw new IllegalArgumentException("Đơn giá dịch vụ phải là số không âm.");
        }

        if (!Double.isFinite(dv.getThueVAT()) || dv.getThueVAT() < 0 || dv.getThueVAT() > 100) {
            throw new IllegalArgumentException("Thuế VAT phải từ 0% đến 100%.");
        }

        DichVu dichVuHienTai = null;
        if (dv.getId() > 0) {
            dichVuHienTai = dichVuDAO.layChiTietDichVuTheoID(dv.getId());
        }
        if (dichVuHienTai == null && dv.getMaDichVu() != null && !dv.getMaDichVu().trim().isEmpty()) {
            dichVuHienTai = dichVuDAO.layChiTietDichVuTheoMa(dv.getMaDichVu());
            if (dichVuHienTai != null) {
                dv.setId(dichVuHienTai.getId());
            }
        }

        if (dichVuHienTai == null) {
            throw new IllegalArgumentException("Dịch vụ cần cập nhật không tồn tại trong hệ thống.");
        }

        return dichVuDAO.capNhat(dv);
    }
}
