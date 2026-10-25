package com.pengugarage.service;

import com.pengugarage.dao.DanhMucDAO;
import com.pengugarage.model.entity.DanhMucSanPham;

import java.util.List;

public class DanhMucService {
    private final DanhMucDAO danhMucDAO ;

    public DanhMucService() {
        this.danhMucDAO = new DanhMucDAO();
    }

    public List<DanhMucSanPham> layTatCa() {
        return danhMucDAO.layTatCa();
    }
}
