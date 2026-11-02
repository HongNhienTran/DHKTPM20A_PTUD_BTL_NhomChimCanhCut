package com.pengugarage.service;

import com.pengugarage.dao.MaKhuyenMaiDAO;
import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;

import java.util.List;

public class MaKhuyenMaiService {
    private final MaKhuyenMaiDAO maKhuyenMaiDAO;

    public MaKhuyenMaiService() {
        this.maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    }

    public List<MaKhuyenMai> layTatCaMaKhuyenMai() {
        return maKhuyenMaiDAO.layTatCa();
    }

    public MaKhuyenMai layChiTietMaKhuyenMaiTheoID(int id) {
        return maKhuyenMaiDAO.layChiTietMaKhuyenMaiTheoID(id);
    }

    public boolean kiemTraTrungMa(String maKM) {
        return maKhuyenMaiDAO.kiemTraTrungMa(maKM);
    }

    public boolean kiemTraTrungMa(String maKM, int loaiTruId) {
        return maKhuyenMaiDAO.kiemTraTrungMa(maKM, loaiTruId);
    }

    public boolean them(MaKhuyenMai km) {
        kiemTraMaKhuyenMai(km, true);
        return maKhuyenMaiDAO.them(km);
    }

    public boolean capNhat(MaKhuyenMai km) {
        kiemTraMaKhuyenMai(km, false);
        return maKhuyenMaiDAO.capNhat(km);
    }

    private void kiemTraMaKhuyenMai(MaKhuyenMai km, boolean isThemMoi) {
        if (km == null) {
            throw new IllegalArgumentException("Thông tin mã khuyến mãi không hợp lệ.");
        }

        String ma = km.getMaKhuyenMai() == null ? "" : km.getMaKhuyenMai().trim();
        if (ma.isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống.");
        }

        int id = km.getId();
        if (maKhuyenMaiDAO.kiemTraTrungMa(ma, isThemMoi ? 0 : id)) {
            throw new IllegalArgumentException("Mã khuyến mãi '" + ma + "' đã tồn tại trong hệ thống.");
        }

        if (km.getLoaiGiam() == null) {
            throw new IllegalArgumentException("Vui lòng chọn loại giảm giá.");
        }

        if (km.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
            if (km.getGiaTriGiam() <= 0 || km.getGiaTriGiam() > 100) {
                throw new IllegalArgumentException("Mức giảm theo phần trăm phải lớn hơn 0% và không vượt quá 100%.");
            }
        } else {
            if (km.getGiaTriGiam() <= 0) {
                throw new IllegalArgumentException("Mức giảm tiền mặt phải lớn hơn 0 VNĐ.");
            }
            if (km.getGiaTriToiThieuApDung() > 0 && km.getGiaTriGiam() > km.getGiaTriToiThieuApDung()) {
                throw new IllegalArgumentException("Mức giảm không thể lớn hơn giá trị đơn hàng tối thiểu.");
            }
        }

        if (km.getGiaTriToiThieuApDung() < 0) {
            throw new IllegalArgumentException("Giá trị đơn hàng tối thiểu không được âm.");
        }

        if (km.getNgayBatDauApDung() != null && km.getNgayHetHan() != null) {
            if (km.getNgayHetHan().isBefore(km.getNgayBatDauApDung())) {
                throw new IllegalArgumentException("Ngày hết hạn phải sau ngày bắt đầu áp dụng.");
            }
        }

        if (km.getSoLuongTon() != null && km.getSoLuongTon() < 0) {
            throw new IllegalArgumentException("Số lượng phát hành không được âm.");
        }

        km.setMaKhuyenMai(ma.toUpperCase());
    }
}
