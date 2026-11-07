package bus;

import dao.HangXeDAO;
import dao.KhachHangDAO;
import dao.PhieuTiepNhanDAO;
import dao.PhuongTienDAO;
import entity.KhachHang;
import entity.PhuongTien;

import java.util.List;

public class PhuongTienBUS {
    private final PhuongTienDAO phuongTienDAO = new PhuongTienDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final HangXeDAO hangXeDAO = new HangXeDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();

    public String taoMaPhuongTien(String maKhachHang, int soThuTu) {
        if (maKhachHang == null || !maKhachHang.matches("^" + KhachHang.REGEX_MA_KHACH_HANG + "$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ để sinh mã phương tiện");
        }
        if (soThuTu < 1 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự phương tiện của khách hàng phải từ 001 đến 999");
        }
        return String.format("PT-%s-%03d", maKhachHang, soThuTu);
    }

    public String phatSinhMaPhuongTien(String maKhachHang) {
        if (maKhachHang == null || !maKhachHang.matches("^" + KhachHang.REGEX_MA_KHACH_HANG + "$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ để sinh mã phương tiện");
        }
        String tienTo = "PT-" + maKhachHang + "-";
        String maxMa = phuongTienDAO.layMaLonNhatTheoTienTo(tienTo);
        int next = 1;
        if (maxMa != null && maxMa.startsWith(tienTo)) {
            next = Integer.parseInt(maxMa.substring(tienTo.length())) + 1;
        }
        return taoMaPhuongTien(maKhachHang, next);
    }

    public List<PhuongTien> layTatCa() {
        return phuongTienDAO.layTatCa();
    }

    public List<PhuongTien> layTheoKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống");
        }
        return phuongTienDAO.timTheoKhachHang(maKhachHang.trim());
    }

    public PhuongTien timTheoMa(String maPhuongTien) {
        if (maPhuongTien == null || maPhuongTien.trim().isEmpty()) {
            return null;
        }
        return phuongTienDAO.timTheoMa(maPhuongTien.trim());
    }

    public PhuongTien layChiTietPhuongTien(String maPhuongTien) {
        PhuongTien pt = timTheoMa(maPhuongTien);
        if (pt != null) {
            pt.setLichSuDungDichVu(phieuTiepNhanDAO.timTheoPhuongTien(pt.getMaPhuongTien()));
        }
        return pt;
    }

    public PhuongTien timTheoBienSo(String bienSoXe) {
        if (bienSoXe == null || bienSoXe.trim().isEmpty()) {
            return null;
        }
        return phuongTienDAO.timTheoBienSo(bienSoXe.trim());
    }

    public boolean themPhuongTien(PhuongTien pt) {
        if (pt == null || pt.getKhachHang() == null || pt.getHangXe() == null) {
            throw new IllegalArgumentException("Phương tiện phải gắn đầy đủ khách hàng và hãng xe");
        }
        if (khachHangDAO.timTheoMa(pt.getKhachHang().getMaKhachHang()) == null) {
            throw new IllegalArgumentException("Khách hàng không tồn tại: " + pt.getKhachHang().getMaKhachHang());
        }
        if (hangXeDAO.timTheoMa(pt.getHangXe().getMaHangXe()) == null) {
            throw new IllegalArgumentException("Hãng xe không tồn tại: " + pt.getHangXe().getMaHangXe());
        }
        PhuongTien trungBienSo = phuongTienDAO.timTheoBienSo(pt.getBienSoXe());
        if (trungBienSo != null) {
            throw new IllegalArgumentException("Biển số xe đã tồn tại trong hệ thống: " + pt.getBienSoXe());
        }
        if (pt.getMaPhuongTien() == null || pt.getMaPhuongTien().trim().isEmpty()) {
            pt.setMaPhuongTien(phatSinhMaPhuongTien(pt.getKhachHang().getMaKhachHang()));
        } else if (phuongTienDAO.timTheoMa(pt.getMaPhuongTien()) != null) {
            throw new IllegalArgumentException("Mã phương tiện đã tồn tại: " + pt.getMaPhuongTien());
        }
        return phuongTienDAO.them(pt);
    }

    public boolean capNhatPhuongTien(PhuongTien pt) {
        if (pt == null || pt.getMaPhuongTien() == null) {
            throw new IllegalArgumentException("Phương tiện cần cập nhật không hợp lệ");
        }
        if (phuongTienDAO.timTheoMa(pt.getMaPhuongTien()) == null) {
            throw new IllegalArgumentException("Không tìm thấy phương tiện có mã: " + pt.getMaPhuongTien());
        }
        PhuongTien trungBienSo = phuongTienDAO.timTheoBienSo(pt.getBienSoXe());
        if (trungBienSo != null && !trungBienSo.getMaPhuongTien().equals(pt.getMaPhuongTien())) {
            throw new IllegalArgumentException("Biển số xe đã thuộc phương tiện khác ("
                    + trungBienSo.getMaPhuongTien() + ")");
        }
        return phuongTienDAO.capNhat(pt);
    }

    public boolean xoaPhuongTien(String maPhuongTien) {
        if (maPhuongTien == null || maPhuongTien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phương tiện cần xóa không được để trống");
        }
        return phuongTienDAO.xoa(maPhuongTien.trim());
    }
}

