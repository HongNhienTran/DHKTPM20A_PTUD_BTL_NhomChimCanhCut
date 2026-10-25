package com.pengugarage.service;

import com.pengugarage.dao.MaKhuyenMaiDAO;
import com.pengugarage.model.entity.MaKhuyenMai;

import java.util.List;

public class MaKhuyenMaiService {
    private final MaKhuyenMaiDAO maKhuyenMaiDAO;

    public MaKhuyenMaiService() {
        this.maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    }

    // =========================================================================
    // TASK M03-T01: Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện
    // Người thực hiện: Nhiên
    // =========================================================================
    public List<MaKhuyenMai> layTatCaMaKhuyenMai() {
        return maKhuyenMaiDAO.layTatCa();
    }

    public MaKhuyenMai layChiTietMaKhuyenMaiTheoID(int id) {
        return maKhuyenMaiDAO.layChiTietMaKhuyenMaiTheoID(id);
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin mã khuyến mãi và thêm
    // Người thực hiện: Tuấn
    // =========================================================================
    public boolean them(MaKhuyenMai km) {
        // TODO: Tuấn thực hiện chức năng nhập thông tin mã khuyến mãi và thêm (Task M03-T02)
        return maKhuyenMaiDAO.them(km);
    }
}

