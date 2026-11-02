package com.pengugarage.service;

import com.pengugarage.dao.DichVuDAO;
import com.pengugarage.model.entity.DichVu;

import java.util.List;

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
        return dichVuDAO.layChiTietDichVuTheoID(id);
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    public boolean them(DichVu dv) {
        if (dv == null) {
            throw new IllegalArgumentException(
                    "Thông tin dịch vụ không được để trống."
            );}
        if (dv.getMaDichVu() == null || dv.getMaDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã dịch vụ không được để trống."
            );}
        if (dv.getTenDichVu() == null || dv.getTenDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên dịch vụ không được để trống."
            );}
        if (dv.getDonGia() < 0) {
            throw new IllegalArgumentException(
                    "Đơn giá không được âm."
            );}
        if (dichVuDAO.tonTaiMaDichVu(dv.getMaDichVu().trim())) {
            throw new IllegalArgumentException(
                    "Mã dịch vụ đã tồn tại."
            );}
        return dichVuDAO.them(dv);
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    public boolean capNhat(DichVu dv) {
        if (dv == null) {
            throw new IllegalArgumentException(
                    "Thông tin dịch vụ không được để trống."
            );}
        if (dv.getTenDichVu() == null
                || dv.getTenDichVu().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên dịch vụ không được để trống."
            );}
        if (!Double.isFinite(dv.getDonGia()) || dv.getDonGia() < 0) {
            throw new IllegalArgumentException(
                    "Đơn giá phải là số không âm."
            );}
        if (!Double.isFinite(dv.getThueVAT())
                || dv.getThueVAT() < 0
                || dv.getThueVAT() > 100) {
            throw new IllegalArgumentException(
                    "Thuế VAT phải từ 0 đến 100%."
            );}
        DichVu dichVuHienTai =
                dichVuDAO.layChiTietDichVuTheoID(dv.getId());

        if (dichVuHienTai == null) {
            throw new IllegalArgumentException(
                    "Dịch vụ cần cập nhật không tồn tại."
            );}
        dv.setTenDichVu(dv.getTenDichVu().trim());
        return dichVuDAO.capNhat(dv);}
}
