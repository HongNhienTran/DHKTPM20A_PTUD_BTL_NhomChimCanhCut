package bus;

import dao.DichVuDAO;
import entity.DichVu;

import java.util.ArrayList;
import java.util.List;

public class DichVuBUS {
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    public String taoMaDichVu(int soThuTu) {
        if (soThuTu < 1 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự dịch vụ phải nằm trong khoảng 001 - 999");
        }
        return String.format("DV%03d", soThuTu);
    }

    public String phatSinhMaDichVu() {
        String maxMa = dichVuDAO.layMaLonNhat();
        int next = 1;
        if (maxMa != null && maxMa.startsWith("DV")) {
            next = Integer.parseInt(maxMa.substring(2)) + 1;
        }
        return taoMaDichVu(next);
    }

    public List<DichVu> layTatCa() {
        return dichVuDAO.layTatCa();
    }

    public List<DichVu> layDanhSachDangKinhDoanh() {
        List<DichVu> ketQua = new ArrayList<>();
        for (DichVu dv : dichVuDAO.layTatCa()) {
            if (dv.isTrangThai()) {
                ketQua.add(dv);
            }
        }
        return ketQua;
    }

    public DichVu timTheoMa(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            return null;
        }
        return dichVuDAO.timTheoMa(maDichVu.trim());
    }

    public boolean themDichVu(DichVu dv) {
        if (dv == null) {
            throw new IllegalArgumentException("Thông tin dịch vụ không được null");
        }
        if (dv.getMaDichVu() == null || dv.getMaDichVu().trim().isEmpty()) {
            dv.setMaDichVu(phatSinhMaDichVu());
        } else if (dichVuDAO.timTheoMa(dv.getMaDichVu()) != null) {
            throw new IllegalArgumentException("Mã dịch vụ đã tồn tại: " + dv.getMaDichVu());
        }
        return dichVuDAO.them(dv);
    }

    public boolean capNhatDichVu(DichVu dv) {
        if (dv == null || dv.getMaDichVu() == null) {
            throw new IllegalArgumentException("Dịch vụ cần cập nhật không hợp lệ");
        }
        if (dichVuDAO.timTheoMa(dv.getMaDichVu()) == null) {
            throw new IllegalArgumentException("Không tìm thấy dịch vụ có mã: " + dv.getMaDichVu());
        }
        return dichVuDAO.capNhat(dv);
    }

    public boolean xoaDichVu(String maDichVu) {
        if (maDichVu == null || maDichVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã dịch vụ cần xóa không được để trống");
        }
        return dichVuDAO.xoa(maDichVu.trim());
    }
}

