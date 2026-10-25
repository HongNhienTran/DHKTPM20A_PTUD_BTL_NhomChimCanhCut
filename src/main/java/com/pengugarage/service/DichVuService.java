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
        // TODO: Tân thực hiện chức năng nhập thông tin dịch vụ và thêm
        return dichVuDAO.them(dv);
    }

    // =========================================================================
    // TASK M02-T03: Hiển thị thông tin dịch vụ (và cập nhật)
    // Người thực hiện: Tân
    // =========================================================================
    public boolean capNhat(DichVu dv) {
        // TODO: Tân thực hiện chức năng cập nhật thông tin dịch vụ
        return dichVuDAO.capNhat(dv);
    }
}
