package com.pengugarage.service;

import com.pengugarage.dao.MaKhuyenMaiDAO;
import com.pengugarage.model.entity.MaKhuyenMai;

import java.util.List;

public class MaKhuyenMaiService {
    private final MaKhuyenMaiDAO maKhuyenMaiDAO;

    public MaKhuyenMaiService() {
        this.maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    }

    public List<MaKhuyenMai> layTatCaMaKhuyenMai() {
        return maKhuyenMaiDAO.layTatCa();
    }
}
