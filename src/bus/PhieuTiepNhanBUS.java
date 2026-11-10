package bus;

import dao.NhanVienDAO;
import dao.PhieuBaoGiaDAO;
import dao.PhieuPhanCongDAO;
import dao.PhieuTiepNhanDAO;
import dao.PhuongTienDAO;
import entity.ChiTietBaoGia;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuPhanCong;
import entity.PhieuTiepNhan;
import entity.PhuongTien;
import enums.ChucVu;
import enums.TrangThaiBaoGia;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiSua;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PhieuTiepNhanBUS {
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("ddMMyy");

    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final PhieuPhanCongDAO phieuPhanCongDAO = new PhieuPhanCongDAO();
    private final PhieuBaoGiaDAO phieuBaoGiaDAO = new PhieuBaoGiaDAO();
    private final PhuongTienDAO phuongTienDAO = new PhuongTienDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public String taoMaTiepNhan(String maPhuongTien, LocalDateTime thoiGianTiepNhan, int soThuTu) {
        if (maPhuongTien == null || !maPhuongTien.matches("^" + PhuongTien.REGEX_MA_PHUONG_TIEN + "$")) {
            throw new IllegalArgumentException("Mã phương tiện không hợp lệ để sinh mã phiếu tiếp nhận");
        }
        if (thoiGianTiepNhan == null) {
            throw new IllegalArgumentException("Thời gian tiếp nhận không được null");
        }
        if (soThuTu < 1 || soThuTu > 99) {
            throw new IllegalArgumentException("Số thứ tự phiếu tiếp nhận trong ngày phải từ 01 đến 99");
        }
        return String.format("TN-%s-%s-%02d",
                maPhuongTien,
                thoiGianTiepNhan.format(DINH_DANG_NGAY),
                soThuTu);
    }

    public String phatSinhMaTiepNhan(String maPhuongTien, LocalDateTime thoiGianTiepNhan) {
        if (maPhuongTien == null || !maPhuongTien.matches("^" + PhuongTien.REGEX_MA_PHUONG_TIEN + "$")) {
            throw new IllegalArgumentException("Mã phương tiện không hợp lệ để sinh mã phiếu tiếp nhận");
        }
        LocalDateTime moc = thoiGianTiepNhan != null ? thoiGianTiepNhan : LocalDateTime.now();
        String tienTo = "TN-" + maPhuongTien + "-" + moc.format(DINH_DANG_NGAY) + "-";
        String maxMa = phieuTiepNhanDAO.layMaLonNhatTheoTienTo(tienTo);
        int next = 1;
        if (maxMa != null && maxMa.startsWith(tienTo)) {
            next = Integer.parseInt(maxMa.substring(tienTo.length())) + 1;
        }
        return taoMaTiepNhan(maPhuongTien, moc, next);
    }

    public List<PhieuTiepNhan> layTatCa() {
        return phieuTiepNhanDAO.layTatCa();
    }

    public List<PhieuTiepNhan> layTheoPhuongTien(String maPhuongTien) {
        if (maPhuongTien == null || maPhuongTien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phương tiện không được để trống");
        }
        return phieuTiepNhanDAO.timTheoPhuongTien(maPhuongTien.trim());
    }

    public List<PhieuTiepNhan> layTheoTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        if (trangThai == null) {
            throw new IllegalArgumentException("Trạng thái cần lọc không được null");
        }
        return phieuTiepNhanDAO.timTheoTrangThai(trangThai);
    }

    public PhieuTiepNhan timTheoMa(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) {
            return null;
        }
        return phieuTiepNhanDAO.timTheoMa(maTiepNhan.trim());
    }

    public boolean lapPhieuTiepNhan(PhieuTiepNhan ptn) {
        if (ptn == null || ptn.getPhuongTien() == null || ptn.getNhanVien() == null) {
            throw new IllegalArgumentException("Phiếu tiếp nhận phải có đầy đủ phương tiện và nhân viên tiếp nhận");
        }
        PhuongTien pt = phuongTienDAO.timTheoMa(ptn.getPhuongTien().getMaPhuongTien());
        if (pt == null) {
            throw new IllegalArgumentException("Phương tiện không tồn tại: "
                    + ptn.getPhuongTien().getMaPhuongTien());
        }
        NhanVien nv = nhanVienDAO.timTheoMa(ptn.getNhanVien().getMaNhanVien());
        if (nv == null || !nv.isTrangThai()) {
            throw new IllegalArgumentException("Nhân viên tiếp nhận không tồn tại hoặc đã ngừng làm việc");
        }
        if (nv.getChucVu() != ChucVu.LE_TAN) {
            throw new IllegalArgumentException("Nhân viên lập phiếu tiếp nhận phải có chức vụ Lễ tân");
        }
        if (ptn.getThoiGianTiepNhan() == null) {
            ptn.setThoiGianTiepNhan(LocalDateTime.now());
        }
        if (ptn.getTrangThai() == null) {
            ptn.setTrangThai(TrangThaiPhieuTiepNhan.DANG_KIEM_TRA);
        }
        if (ptn.getMaTiepNhan() == null || ptn.getMaTiepNhan().trim().isEmpty()) {
            ptn.setMaTiepNhan(phatSinhMaTiepNhan(pt.getMaPhuongTien(), ptn.getThoiGianTiepNhan()));
        } else if (phieuTiepNhanDAO.timTheoMa(ptn.getMaTiepNhan()) != null) {
            throw new IllegalArgumentException("Mã phiếu tiếp nhận đã tồn tại: " + ptn.getMaTiepNhan());
        }
        return phieuTiepNhanDAO.them(ptn);
    }

    public boolean capNhatPhieuTiepNhan(PhieuTiepNhan ptn) {
        if (ptn == null || ptn.getMaTiepNhan() == null) {
            throw new IllegalArgumentException("Phiếu tiếp nhận cần cập nhật không hợp lệ");
        }
        if (phieuTiepNhanDAO.timTheoMa(ptn.getMaTiepNhan()) == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu tiếp nhận: " + ptn.getMaTiepNhan());
        }
        return phieuTiepNhanDAO.capNhat(ptn);
    }

    public boolean chuyenTrangThai(String maTiepNhan, TrangThaiPhieuTiepNhan trangThaiMoi) {
        if (trangThaiMoi == null) {
            throw new IllegalArgumentException("Trạng thái mới không được null");
        }
        PhieuTiepNhan hienTai = phieuTiepNhanDAO.timTheoMa(maTiepNhan);
        if (hienTai == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu tiếp nhận: " + maTiepNhan);
        }
        if (hienTai.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY) {
            throw new IllegalStateException("Phiếu tiếp nhận đã hủy, không thể chuyển trạng thái");
        }
        if (hienTai.getTrangThai() == TrangThaiPhieuTiepNhan.THANH_TOAN) {
            throw new IllegalStateException("Phiếu tiếp nhận đã thanh toán, không thể chuyển trạng thái");
        }
        if (trangThaiMoi == TrangThaiPhieuTiepNhan.HOAN_THANH) {
            if (hienTai.getTrangThai() != TrangThaiPhieuTiepNhan.DANG_SUA_CHUA) {
                throw new IllegalStateException("Chỉ có thể chuyển sang Hoàn thành khi phiếu tiếp nhận đang ở trạng thái Đang sửa chữa");
            }
            List<PhieuPhanCong> dsPhanCong = phieuPhanCongDAO.timTheoPhieuTiepNhan(maTiepNhan);
            if (dsPhanCong != null && !dsPhanCong.isEmpty()) {
                for (PhieuPhanCong pc : dsPhanCong) {
                    if (pc.getTrangThaiSua() != TrangThaiSua.HOAN_THANH) {
                        throw new IllegalStateException("Không thể hoàn thành phiếu tiếp nhận khi còn nhiệm vụ kỹ thuật viên chưa hoàn thành (" + pc.getMaPhanCong() + ")");
                    }
                }
            }
        }
        return phieuTiepNhanDAO.capNhatTrangThai(maTiepNhan, trangThaiMoi);
    }

    public boolean huyPhieuTiepNhan(String maTiepNhan) {
        PhieuTiepNhan hienTai = phieuTiepNhanDAO.timTheoMa(maTiepNhan);
        if (hienTai == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu tiếp nhận: " + maTiepNhan);
        }
        if (hienTai.getTrangThai() == TrangThaiPhieuTiepNhan.DANG_SUA_CHUA
                || hienTai.getTrangThai() == TrangThaiPhieuTiepNhan.HOAN_THANH
                || hienTai.getTrangThai() == TrangThaiPhieuTiepNhan.THANH_TOAN) {
            throw new IllegalStateException("Không thể hủy phiếu tiếp nhận đang sửa chữa, đã hoàn thành hoặc đã thanh toán");
        }
        boolean daHuy = phieuTiepNhanDAO.capNhatTrangThai(maTiepNhan, TrangThaiPhieuTiepNhan.DA_HUY);
        if (daHuy) {
            try {
                PhieuBaoGia pbg = phieuBaoGiaDAO.timMoiNhatTheoTiepNhan(maTiepNhan);
                if (pbg != null && pbg.getTrangThai() != TrangThaiBaoGia.TU_CHOI) {
                    pbg.tuChoi("Phiếu tiếp nhận đã bị hủy");
                    for (ChiTietBaoGia ct : pbg.getDanhSachChiTiet()) {
                        ct.setDuocChapNhan(false);
                    }
                    phieuBaoGiaDAO.capNhat(pbg);
                }
            } catch (Exception ignored) {
            }
        }
        return daHuy;
    }

    public boolean xoaPhieuTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu tiếp nhận cần xóa không được để trống");
        }
        return phieuTiepNhanDAO.xoa(maTiepNhan.trim());
    }
}

