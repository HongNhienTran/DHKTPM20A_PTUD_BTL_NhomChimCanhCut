package bus;

import dao.DichVuDAO;
import entity.DichVu;
import enums.ChuyenMon;

import java.util.List;

public class DichVuBUS {
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    public String taoMaDichVu(int soThuTu) {
        if (soThuTu < 0 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự dịch vụ phải nằm trong khoảng 000 - 999");
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
        return dichVuDAO.layDanhSachHoatDong();
    }

    public List<DichVu> layTheoChuyenMon(ChuyenMon cm) {
        return dichVuDAO.layTheoChuyenMon(cm);
    }

    public List<DichVu> layTheoCoPhuTung(boolean coPhuTung) {
        return dichVuDAO.layTheoCoPhuTung(coPhuTung);
    }

    /**
     * Kiểm tra xem danh sách dịch vụ khách chọn có cần KTV kiểm tra xe hay không.
     * Cần KTV kiểm tra nếu:
     * - Danh sách rỗng (khách chưa chọn dịch vụ nào cụ thể, cần KTV khám).
     * - Hoặc có ít nhất 1 dịch vụ yêu cầu phụ tùng (coPhuTung == true).
     * - Hoặc có dịch vụ "Kiểm tra & chẩn đoán tổng quát" (DV000).
     * Trả về false (Bỏ qua bước KTV kiểm tra) nếu 100% dịch vụ KHÔNG cần phụ tùng và không có DV000.
     */
    public boolean canKiemTraKTV(List<DichVu> dsDichVu) {
        if (dsDichVu == null || dsDichVu.isEmpty()) {
            return true;
        }
        for (DichVu dv : dsDichVu) {
            if ("DV000".equalsIgnoreCase(dv.getMaDichVu())) {
                return true;
            }
            if (dv.isCoPhuTung()) {
                return true;
            }
        }
        return false;
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
        if (dv.getChuyenMon() == null) {
            throw new IllegalArgumentException("Dịch vụ bắt buộc phải gắn với chuyên môn phụ trách");
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
        if (dv.getChuyenMon() == null) {
            throw new IllegalArgumentException("Dịch vụ bắt buộc phải gắn với chuyên môn phụ trách");
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

