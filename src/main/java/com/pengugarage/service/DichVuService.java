package com.pengugarage.service;

import com.pengugarage.dao.DichVuDAO;
import com.pengugarage.model.entity.DichVu;

import java.util.List;

public class DichVuService {
    private final DichVuDAO dichVuDAO;

    public DichVuService() {
        this.dichVuDAO = new DichVuDAO();
    }

    public List<DichVu> layTatCaDichVu() {
        return dichVuDAO.layTatCa();
    }
}
