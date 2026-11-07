package bus;

import dao.DanhMucSanPhamDAO;
import dao.SanPhamDAO;
import entity.DanhMucSanPham;
import entity.SanPham;

import java.util.ArrayList;
import java.util.List;

public class SanPhamBUS {
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final DanhMucSanPhamDAO danhMucDAO = new DanhMucSanPhamDAO();

    public String taoMaSanPham(String maDanhMuc, int soThuTu) {
        if (maDanhMuc == null || !maDanhMuc.matches("^" + DanhMucSanPham.REGEX_MA_DANH_MUC + "$")) {
            throw new IllegalArgumentException("Mã danh mục không hợp lệ để sinh mã sản phẩm");
        }
        if (soThuTu < 1 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự sản phẩm trong danh mục phải từ 001 đến 999");
        }
        return String.format("SP-%s-%03d", maDanhMuc, soThuTu);
    }

    public String phatSinhMaSanPham(String maDanhMuc) {
        if (maDanhMuc == null || !maDanhMuc.matches("^" + DanhMucSanPham.REGEX_MA_DANH_MUC + "$")) {
            throw new IllegalArgumentException("Mã danh mục không hợp lệ để sinh mã sản phẩm");
        }
        String tienTo = "SP-" + maDanhMuc + "-";
        String maxMa = sanPhamDAO.layMaLonNhatTheoTienTo(tienTo);
        int next = 1;
        if (maxMa != null && maxMa.startsWith(tienTo)) {
            next = Integer.parseInt(maxMa.substring(tienTo.length())) + 1;
        }
        return taoMaSanPham(maDanhMuc, next);
    }

    public List<SanPham> layTatCa() {
        return sanPhamDAO.layTatCa();
    }

    public List<SanPham> layTheoDanhMuc(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã danh mục không được để trống");
        }
        return sanPhamDAO.timTheoDanhMuc(maDanhMuc.trim());
    }

    public List<SanPham> layDanhSachBanChay(int gioiHan) {
        if (gioiHan <= 0) {
            gioiHan = 30;
        }
        return sanPhamDAO.layDanhSachBanChay(gioiHan);
    }

    public List<SanPham> layDanhSachSanPhamMoi(int gioiHan) {
        if (gioiHan <= 0) {
            gioiHan = 30;
        }
        return sanPhamDAO.layDanhSachSanPhamMoi(gioiHan);
    }

    public List<SanPham> layDanhSachCanNhapThem() {
        List<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : sanPhamDAO.layTatCa()) {
            if (sp.isTrangThai() && sp.getSoLuongTon() <= sp.getSoLuongTonToiThieu()) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    public SanPham timTheoMa(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        return sanPhamDAO.timTheoMa(maSanPham.trim());
    }

    public boolean themSanPham(SanPham sp) {
        if (sp == null || sp.getDanhMucSanPham() == null) {
            throw new IllegalArgumentException("Sản phẩm phải thuộc một danh mục hợp lệ");
        }
        DanhMucSanPham dm = danhMucDAO.timTheoMa(sp.getDanhMucSanPham().getMaDanhMuc());
        if (dm == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục sản phẩm: "
                    + sp.getDanhMucSanPham().getMaDanhMuc());
        }
        if (sp.getMaSanPham() == null || sp.getMaSanPham().trim().isEmpty()) {
            sp.setMaSanPham(phatSinhMaSanPham(dm.getMaDanhMuc()));
        } else if (sanPhamDAO.timTheoMa(sp.getMaSanPham()) != null) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + sp.getMaSanPham());
        }
        return sanPhamDAO.them(sp);
    }

    public boolean capNhatSanPham(SanPham sp) {
        if (sp == null || sp.getMaSanPham() == null) {
            throw new IllegalArgumentException("Sản phẩm cần cập nhật không hợp lệ");
        }
        if (sanPhamDAO.timTheoMa(sp.getMaSanPham()) == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + sp.getMaSanPham());
        }
        return sanPhamDAO.capNhat(sp);
    }

    public boolean nhapThemHang(String maSanPham, int soLuongNhap) {
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Số lượng nhập kho phải lớn hơn 0");
        }
        SanPham sp = sanPhamDAO.timTheoMa(maSanPham);
        if (sp == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + maSanPham);
        }
        int tonMoi = sp.getSoLuongTon() + soLuongNhap;
        sp.setSoLuongTon(tonMoi);
        return sanPhamDAO.capNhatSoLuongTon(maSanPham, tonMoi);
    }

    public boolean xoaSanPham(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm cần xóa không được để trống");
        }
        return sanPhamDAO.xoa(maSanPham.trim());
    }
}

