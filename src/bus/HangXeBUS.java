package bus;

import dao.HangXeDAO;
import entity.HangXe;

import java.util.List;

public class HangXeBUS {
    private final HangXeDAO hangXeDAO = new HangXeDAO();

    public String taoMaHangXe(int soThuTu) {
        if (soThuTu < 1 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự hãng xe phải nằm trong khoảng 001 - 999");
        }
        return String.format("HX%03d", soThuTu);
    }

    public String phatSinhMaHangXe() {
        String maxMa = hangXeDAO.layMaLonNhat();
        int next = 1;
        if (maxMa != null && maxMa.startsWith("HX")) {
            next = Integer.parseInt(maxMa.substring(2)) + 1;
        }
        return taoMaHangXe(next);
    }

    public List<HangXe> layTatCa() {
        return hangXeDAO.layTatCa();
    }

    public HangXe timTheoMa(String maHangXe) {
        if (maHangXe == null || maHangXe.trim().isEmpty()) {
            return null;
        }
        return hangXeDAO.timTheoMa(maHangXe.trim());
    }

    public boolean themHangXe(HangXe hangXe) {
        if (hangXe == null) {
            throw new IllegalArgumentException("Thông tin hãng xe không được null");
        }
        if (hangXe.getMaHangXe() == null || hangXe.getMaHangXe().trim().isEmpty()) {
            hangXe.setMaHangXe(phatSinhMaHangXe());
        } else if (hangXeDAO.timTheoMa(hangXe.getMaHangXe()) != null) {
            throw new IllegalArgumentException("Mã hãng xe đã tồn tại: " + hangXe.getMaHangXe());
        }
        return hangXeDAO.them(hangXe);
    }

    public boolean capNhatHangXe(HangXe hangXe) {
        if (hangXe == null || hangXe.getMaHangXe() == null) {
            throw new IllegalArgumentException("Hãng xe cần cập nhật không hợp lệ");
        }
        if (hangXeDAO.timTheoMa(hangXe.getMaHangXe()) == null) {
            throw new IllegalArgumentException("Không tìm thấy hãng xe có mã: " + hangXe.getMaHangXe());
        }
        return hangXeDAO.capNhat(hangXe);
    }

    public boolean xoaHangXe(String maHangXe) {
        if (maHangXe == null || maHangXe.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hãng xe cần xóa không được để trống");
        }
        return hangXeDAO.xoa(maHangXe.trim());
    }
}

