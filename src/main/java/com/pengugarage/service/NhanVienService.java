package com.pengugarage.service;

import com.pengugarage.dao.NhanVienDAO;
import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.enums.ChucVu;

import java.util.List;

/**
 * Service xử lý các quy tắc nghiệp vụ quản lý nhân viên (M01-T07).
 */
public class NhanVienService {

    private final NhanVienDAO nhanVienDAO;

    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    public NhanVienService(NhanVienDAO nhanVienDAO) {
        this.nhanVienDAO = nhanVienDAO;
    }

    public List<NhanVien> layTatCa() {
        return nhanVienDAO.layTatCa();
    }

    public NhanVien timTheoId(int id) {
        return nhanVienDAO.timTheoId(id);
    }

    public List<NhanVien> timKiem(String tuKhoa, ChucVu chucVu, Boolean trangThai) {
        return nhanVienDAO.timKiem(tuKhoa, chucVu, trangThai);
    }

    /**
     * Sinh mã nhân viên tự động dựa theo chức vụ
     */
    public String sinhMaNhanVienTuDong(ChucVu chucVu) {
        String tienTo;
        switch (chucVu) {
            case QUAN_LY:
                tienTo = "NV_QL_";
                break;
            case KY_THUAT_VIEN:
                tienTo = "NV_KTV_";
                break;
            case LE_TAN:
                tienTo = "NV_LT_";
                break;
            default:
                tienTo = "NV_";
                break;
        }

        int count = nhanVienDAO.demSoLuongTheoChucVu(chucVu) + 1;
        String maMoi;
        do {
            maMoi = String.format("%s%03d", tienTo, count);
            count++;
        } while (nhanVienDAO.timTheoMa(maMoi) != null);

        return maMoi;
    }

    /**
     * Thêm mới nhân viên với kiểm tra nghiệp vụ
     */
    public String themNhanVien(NhanVien nv) {
        String loiValidate = kiemTraHopLe(nv, true);
        if (loiValidate != null) {
            return loiValidate;
        }

        if (nv.getMaNhanVien() == null || nv.getMaNhanVien().trim().isEmpty()) {
            nv.setMaNhanVien(sinhMaNhanVienTuDong(nv.getChucVu()));
        } else if (nhanVienDAO.timTheoMa(nv.getMaNhanVien()) != null) {
            return "Mã nhân viên '" + nv.getMaNhanVien() + "' đã tồn tại trong hệ thống!";
        }

        boolean thanhCong = nhanVienDAO.them(nv);
        return thanhCong ? null : "Lỗi khi lưu nhân viên vào cơ sở dữ liệu!";
    }

    /**
     * Cập nhật thông tin nhân viên
     */
    public String capNhatNhanVien(NhanVien nv) {
        String loiValidate = kiemTraHopLe(nv, false);
        if (loiValidate != null) {
            return loiValidate;
        }

        boolean thanhCong = nhanVienDAO.capNhat(nv);
        return thanhCong ? null : "Lỗi khi cập nhật thông tin nhân viên!";
    }

    /**
     * Đổi trạng thái nhân viên (Khóa / Mở khóa)
     */
    public boolean doiTrangThai(int id, boolean trangThai) {
        return nhanVienDAO.doiTrangThai(id, trangThai);
    }

    /**
     * Kiểm tra tính hợp lệ của thông tin nhân viên
     */
    private String kiemTraHopLe(NhanVien nv, boolean isThemMoi) {
        if (nv == null) {
            return "Thông tin nhân viên không được để trống!";
        }

        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()) {
            return "Họ và tên nhân viên không được để trống!";
        }

        if (nv.getSoDienThoai() == null || !nv.getSoDienThoai().trim().matches("^0[0-9]{9}$")) {
            return "Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 chữ số bắt đầu bằng số 0.";
        }

        // Kiểm tra trùng số điện thoại
        NhanVien existing = nhanVienDAO.timTheoSoDienThoai(nv.getSoDienThoai().trim());
        if (existing != null) {
            if (isThemMoi || existing.getId() != nv.getId()) {
                return "Số điện thoại này đã được sử dụng bởi nhân viên khác (" + existing.getHoTen() + ")!";
            }
        }

        if (nv.getChucVu() == null) {
            return "Vui lòng chọn chức vụ cho nhân viên!";
        }

        return null;
    }
}
