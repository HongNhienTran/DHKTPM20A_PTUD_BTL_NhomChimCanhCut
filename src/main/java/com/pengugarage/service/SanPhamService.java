package com.pengugarage.service;

import com.pengugarage.dao.SanPhamDAO;
import com.pengugarage.model.entity.SanPham;

import java.util.List;

public class SanPhamService {
    private final SanPhamDAO sanPhamDAO;

    public SanPhamService() {
        this.sanPhamDAO = new SanPhamDAO();
    }

    public List<SanPham> layTatCaSanPham() {
        return sanPhamDAO.layTatCa();
    }
}
