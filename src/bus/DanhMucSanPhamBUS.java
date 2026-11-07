package bus;

import dao.DanhMucSanPhamDAO;
import entity.DanhMucSanPham;

import java.util.ArrayList;
import java.util.List;

public class DanhMucSanPhamBUS {
    private final DanhMucSanPhamDAO danhMucDAO = new DanhMucSanPhamDAO();

    public String taoMaDanhMuc(int soThuTu) {
        if (soThuTu < 1 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự danh mục phải nằm trong khoảng 001 - 999");
        }
        return String.format("DM%03d", soThuTu);
    }

    public String phatSinhMaDanhMuc() {
        String maxMa = danhMucDAO.layMaLonNhat();
        int next = 1;
        if (maxMa != null && maxMa.startsWith("DM")) {
            next = Integer.parseInt(maxMa.substring(2)) + 1;
        }
        return taoMaDanhMuc(next);
    }

    public List<DanhMucSanPham> layTatCa() {
        return danhMucDAO.layTatCa();
    }

    public List<DanhMucSanPham> layDanhSachDangHoatDong() {
        List<DanhMucSanPham> ketQua = new ArrayList<>();
        for (DanhMucSanPham dm : layTatCa()) {
            if (dm.isTrangThai()) {
                ketQua.add(dm);
            }
        }
        return ketQua;
    }

    public DanhMucSanPham timTheoMa(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            return null;
        }
        return danhMucDAO.timTheoMa(maDanhMuc.trim());
    }

    public boolean themDanhMuc(DanhMucSanPham dm) {
        if (dm == null) {
            throw new IllegalArgumentException("Danh mục sản phẩm không được null");
        }
        if (dm.getMaDanhMuc() == null || dm.getMaDanhMuc().trim().isEmpty()) {
            dm.setMaDanhMuc(phatSinhMaDanhMuc());
        } else if (danhMucDAO.timTheoMa(dm.getMaDanhMuc()) != null) {
            throw new IllegalArgumentException("Mã danh mục đã tồn tại: " + dm.getMaDanhMuc());
        }
        return danhMucDAO.them(dm);
    }

    public boolean capNhatDanhMuc(DanhMucSanPham dm) {
        if (dm == null || dm.getMaDanhMuc() == null) {
            throw new IllegalArgumentException("Danh mục sản phẩm cần cập nhật không hợp lệ");
        }
        if (danhMucDAO.timTheoMa(dm.getMaDanhMuc()) == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục có mã: " + dm.getMaDanhMuc());
        }
        return danhMucDAO.capNhat(dm);
    }

    public boolean xoaDanhMuc(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục cần xóa không được để trống");
        }
        return danhMucDAO.xoa(maDanhMuc.trim());
    }
}

