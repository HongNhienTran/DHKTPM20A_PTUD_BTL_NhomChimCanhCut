package bus;

import dao.NhanVienDAO;
import dao.PhieuPhanCongDAO;
import dao.PhieuTiepNhanDAO;
import entity.ChiTietBaoGia;
import entity.NhanVien;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import enums.ChucVu;
import enums.TrangThaiBaoGia;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiSua;

import java.time.LocalDateTime;
import java.util.List;

public class PhieuPhanCongBUS {
    private final PhieuPhanCongDAO phieuPhanCongDAO = new PhieuPhanCongDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();

    public String taoMaPhanCong(String maTiepNhan, String maNhanVien, int soThuTu) {
        if (maTiepNhan == null || !maTiepNhan.matches("^" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "$")) {
            throw new IllegalArgumentException("Mã phiếu tiếp nhận không hợp lệ để sinh mã phân công");
        }
        if (maNhanVien == null || !maNhanVien.matches("^" + NhanVien.REGEX_MA_NHAN_VIEN + "$")) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ để sinh mã phân công");
        }
        if (soThuTu < 1 || soThuTu > 99) {
            throw new IllegalArgumentException("Số thứ tự phân công phải từ 01 đến 99");
        }
        return String.format("PC-%s-%s-%02d", maTiepNhan, maNhanVien, soThuTu);
    }

    public String phatSinhMaPhanCong(String maTiepNhan, String maNhanVien) {
        if (maTiepNhan == null || !maTiepNhan.matches("^" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "$")) {
            throw new IllegalArgumentException("Mã phiếu tiếp nhận không hợp lệ để sinh mã phân công");
        }
        if (maNhanVien == null || !maNhanVien.matches("^" + NhanVien.REGEX_MA_NHAN_VIEN + "$")) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ để sinh mã phân công");
        }
        String tienTo = "PC-" + maTiepNhan + "-" + maNhanVien + "-";
        String maxMa = phieuPhanCongDAO.layMaLonNhatTheoTienTo(tienTo);
        int next = 1;
        if (maxMa != null && maxMa.startsWith(tienTo)) {
            next = Integer.parseInt(maxMa.substring(tienTo.length())) + 1;
        }
        return taoMaPhanCong(maTiepNhan, maNhanVien, next);
    }

    public List<PhieuPhanCong> layTatCa() {
        return phieuPhanCongDAO.layTatCa();
    }

    public List<PhieuPhanCong> layTheoNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        return phieuPhanCongDAO.timTheoNhanVien(maNhanVien.trim());
    }

    public List<PhieuPhanCong> layTheoPhieuTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu tiếp nhận không được để trống");
        }
        return phieuPhanCongDAO.timTheoPhieuTiepNhan(maTiepNhan.trim());
    }

    public PhieuPhanCong timTheoMa(String maPhanCong) {
        if (maPhanCong == null || maPhanCong.trim().isEmpty()) {
            return null;
        }
        return phieuPhanCongDAO.timTheoMa(maPhanCong.trim());
    }

    public boolean phanCongHangMuc(PhieuPhanCong ppc) {
        if (ppc == null || ppc.getNhanVien() == null || ppc.getChiTietBaoGia() == null) {
            throw new IllegalArgumentException("Phiếu phân công phải gắn đầy đủ kỹ thuật viên và hạng mục báo giá");
        }
        ChiTietBaoGia ctbg = ppc.getChiTietBaoGia();
        if (ctbg.getPhieuBaoGia() == null || ctbg.getDichVu() == null) {
            throw new IllegalArgumentException("Hạng mục báo giá không hợp lệ");
        }
        if (ctbg.getPhieuBaoGia().getTrangThai() != TrangThaiBaoGia.DA_CHAP_THUAN || !ctbg.isDuocChapNhan()) {
            throw new IllegalStateException("Chỉ được phân công hạng mục thuộc báo giá đã chấp thuận và duocChapNhan = true");
        }

        PhieuTiepNhan ptn = ppc.layPhieuTiepNhan();
        if (ptn == null || ptn.getMaTiepNhan() == null) {
            throw new IllegalArgumentException("Không truy xuất được phiếu tiếp nhận từ hạng mục báo giá");
        }

        NhanVien ktv = nhanVienDAO.timTheoMa(ppc.getNhanVien().getMaNhanVien());
        if (ktv == null || !ktv.isTrangThai() || ktv.getChucVu() != ChucVu.KY_THUAT_VIEN) {
            throw new IllegalArgumentException("Nhân viên được phân công phải là Kỹ thuật viên đang làm việc");
        }

        PhieuPhanCong daPhanCong = phieuPhanCongDAO.timTheoChiTietBaoGia(
                ctbg.getPhieuBaoGia().getMaBaoGia(),
                ctbg.getDichVu().getMaDichVu()
        );
        if (daPhanCong != null) {
            throw new IllegalStateException("Hạng mục dịch vụ này đã được phân công trong phiếu "
                    + daPhanCong.getMaPhanCong());
        }

        if (ppc.getThoiGianPhanCong() == null) {
            ppc.setThoiGianPhanCong(LocalDateTime.now());
        }
        if (ppc.getTrangThaiSua() == null) {
            ppc.setTrangThaiSua(TrangThaiSua.CHO_THUC_HIEN);
        }
        if (ppc.getMaPhanCong() == null || ppc.getMaPhanCong().trim().isEmpty()) {
            ppc.setMaPhanCong(phatSinhMaPhanCong(ptn.getMaTiepNhan(), ktv.getMaNhanVien()));
        } else if (phieuPhanCongDAO.timTheoMa(ppc.getMaPhanCong()) != null) {
            throw new IllegalArgumentException("Mã phân công đã tồn tại: " + ppc.getMaPhanCong());
        }

        return phieuPhanCongDAO.them(ppc);
    }

    public boolean batDauSua(String maPhanCong) {
        PhieuPhanCong ppc = phieuPhanCongDAO.timTheoMa(maPhanCong);
        if (ppc == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu phân công: " + maPhanCong);
        }
        if (ppc.getTrangThaiSua() == TrangThaiSua.HOAN_THANH) {
            throw new IllegalStateException("Phiếu phân công đã hoàn thành");
        }
        ppc.setThoiGianBatDau(LocalDateTime.now());
        ppc.setTrangThaiSua(TrangThaiSua.DANG_SUA);
        boolean daCapNhat = phieuPhanCongDAO.capNhat(ppc);
        PhieuTiepNhan ptn = ppc.layPhieuTiepNhan();
        if (daCapNhat && ptn != null) {
            phieuTiepNhanDAO.capNhatTrangThai(ptn.getMaTiepNhan(), TrangThaiPhieuTiepNhan.DANG_SUA_CHUA);
        }
        return daCapNhat;
    }

    public boolean hoanThanhSua(String maPhanCong, String ghiChuHoanThanh) {
        PhieuPhanCong ppc = phieuPhanCongDAO.timTheoMa(maPhanCong);
        if (ppc == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu phân công: " + maPhanCong);
        }
        LocalDateTime now = LocalDateTime.now();
        if (ppc.getThoiGianBatDau() == null) {
            ppc.setThoiGianBatDau(now);
        }
        ppc.setThoiGianHoanThanh(now);
        ppc.setTrangThaiSua(TrangThaiSua.HOAN_THANH);
        if (ghiChuHoanThanh != null && !ghiChuHoanThanh.trim().isEmpty()) {
            ppc.setGhiChu(ghiChuHoanThanh.trim());
        }
        boolean daCapNhat = phieuPhanCongDAO.capNhat(ppc);

        PhieuTiepNhan ptn = ppc.layPhieuTiepNhan();
        if (daCapNhat && ptn != null) {
            List<PhieuPhanCong> dsPhanCong = phieuPhanCongDAO.timTheoPhieuTiepNhan(ptn.getMaTiepNhan());
            boolean tatCaHoanThanh = !dsPhanCong.isEmpty();
            for (PhieuPhanCong item : dsPhanCong) {
                if (item.getTrangThaiSua() != TrangThaiSua.HOAN_THANH) {
                    tatCaHoanThanh = false;
                    break;
                }
            }
            if (tatCaHoanThanh) {
                phieuTiepNhanDAO.capNhatTrangThai(ptn.getMaTiepNhan(), TrangThaiPhieuTiepNhan.HOAN_THANH);
            }
        }
        return daCapNhat;
    }

    public boolean xoaPhieuPhanCong(String maPhanCong) {
        if (maPhanCong == null || maPhanCong.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phân công cần xóa không được để trống");
        }
        return phieuPhanCongDAO.xoa(maPhanCong.trim());
    }
}

