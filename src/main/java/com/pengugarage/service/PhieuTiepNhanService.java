package com.pengugarage.service;

import com.pengugarage.dao.PhieuTiepNhanDAO;
import com.pengugarage.model.entity.PhieuTiepNhan;
import com.pengugarage.model.enums.TrangThaiPhieuTiepNhan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service xử lý các nghiệp vụ cho Phiếu Tiếp Nhận (PhieuTiepNhan).
 * Quản lý quy trình tiếp nhận xe, tự động sinh mã phiếu theo ngày,
 * và điều phối trạng thái xe tại xưởng.
 * 
 * Phụ trách: Trần Hồng Nhiên (Module Tiếp nhận xe - M08).
 */
public class PhieuTiepNhanService {

    private final PhieuTiepNhanDAO phieuTiepNhanDAO;

    public PhieuTiepNhanService() {
        this.phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    }

    public PhieuTiepNhanService(PhieuTiepNhanDAO phieuTiepNhanDAO) {
        this.phieuTiepNhanDAO = phieuTiepNhanDAO;
    }

    // =========================================================================
    // HÀM TỰ SINH MÃ PHIẾU TIẾP NHẬN: TN{ngày tiếp nhận}-{3 ký số thứ tự}
    // Cú pháp mẫu: TN20261003-001, TN20261003-002, TN20261003-003,...
    // =========================================================================

    /**
     * Tự động sinh mã tiếp nhận cho ngày hôm nay.
     * Cú pháp: TN{yyyyMMdd}-{XXX} (VD: TN20261103-001).
     *
     * @return Mã tiếp nhận tự sinh
     */
    public String sinhMaTiepNhanTuDong() {
        return sinhMaTiepNhan(LocalDate.now());
    }

    /**
     * Tự động sinh mã tiếp nhận theo một ngày cụ thể.
     * Cú pháp: TN{ngày tiếp nhận}-{3 ký số, thể hiện số thứ tự của phiếu trong ngày}.
     * Ví dụ: TN20261003-003.
     *
     * @param ngay Ngày tiếp nhận xe
     * @return Mã tiếp nhận tự sinh
     */
    public String sinhMaTiepNhan(LocalDate ngay) {
        if (ngay == null) {
            ngay = LocalDate.now();
        }

        // Tiền tố: TN + yyyyMMdd + "-" (VD: TN20261103-)
        String prefix = "TN" + ngay.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";

        // Lấy mã phiếu tiếp nhận cuối cùng được tạo trong ngày
        String maCuoi = phieuTiepNhanDAO.layMaTiepNhanCuoiCungTrongNgay(ngay);

        int soThuTuMoi = 1;
        if (maCuoi != null && maCuoi.trim().startsWith(prefix)) {
            try {
                // Tách 3 ký số sau dấu gạch ngang
                String phanSo = maCuoi.trim().substring(prefix.length()).trim();
                int soHienTai = Integer.parseInt(phanSo);
                soThuTuMoi = soHienTai + 1;
            } catch (Exception e) {
                soThuTuMoi = 1;
            }
        }

        // Định dạng 3 ký số: 001, 002, ..., 999
        return String.format("%s%03d", prefix, soThuTuMoi);
    }

    // =========================================================================
    // CÁC HÀM NGHIỆP VỤ TIẾP NHẬN & TRUY VẤN
    // =========================================================================

    /**
     * Lấy toàn bộ danh sách phiếu tiếp nhận.
     */
    public List<PhieuTiepNhan> layTatCa() {
        return phieuTiepNhanDAO.layTatCa();
    }

    /**
     * Lấy danh sách các xe đang trong hàng đợi xưởng (chưa hủy, theo thời gian vào).
     */
    public List<PhieuTiepNhan> layDanhSachHangDoiXeTaiXuong() {
        return phieuTiepNhanDAO.layDanhSachHangDoiXeTaiXuong();
    }

    /**
     * Lấy danh sách phiếu tiếp nhận theo trạng thái.
     */
    public List<PhieuTiepNhan> layTheoTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        return phieuTiepNhanDAO.layTheoTrangThai(trangThai);
    }

    /**
     * Tìm phiếu tiếp nhận theo ID.
     */
    public PhieuTiepNhan timTheoId(int id) {
        if (id <= 0) return null;
        return phieuTiepNhanDAO.timTheoId(id);
    }

    /**
     * Tìm phiếu tiếp nhận theo Mã.
     */
    public PhieuTiepNhan timTheoMa(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) return null;
        return phieuTiepNhanDAO.timTheoMa(maTiepNhan.trim());
    }

    /**
     * Lập phiếu tiếp nhận mới vào xưởng.
     * Tự động sinh mã nếu đối tượng chưa có mã tiếp nhận.
     */
    public boolean themPhieuTiepNhan(PhieuTiepNhan ptn) {
        if (ptn == null) {
            throw new IllegalArgumentException("Thông tin phiếu tiếp nhận không được để trống!");
        }
        if (ptn.getPhuongTienId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn một phương tiện hợp lệ!");
        }

        // Tự động sinh mã nếu chưa có
        if (ptn.getMaTiepNhan() == null || ptn.getMaTiepNhan().trim().isEmpty()) {
            ptn.setMaTiepNhan(sinhMaTiepNhanTuDong());
        }

        return phieuTiepNhanDAO.them(ptn);
    }

    /**
     * Cập nhật thông tin phiếu tiếp nhận.
     */
    public boolean capNhatPhieuTiepNhan(PhieuTiepNhan ptn) {
        if (ptn == null || ptn.getId() <= 0) {
            throw new IllegalArgumentException("Phiếu tiếp nhận không hợp lệ để cập nhật!");
        }
        return phieuTiepNhanDAO.capNhat(ptn);
    }

    /**
     * Cập nhật chuyển trạng thái tiến độ của phiếu tiếp nhận tại xưởng.
     */
    public boolean capNhatTrangThai(int id, TrangThaiPhieuTiepNhan trangThai) {
        if (id <= 0 || trangThai == null) {
            throw new IllegalArgumentException("Dữ liệu cập nhật trạng thái không hợp lệ!");
        }
        return phieuTiepNhanDAO.capNhatTrangThai(id, trangThai);
    }
}

