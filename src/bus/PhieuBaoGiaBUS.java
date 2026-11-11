package bus;

import dao.DichVuDAO;
import dao.NhanVienDAO;
import dao.PhieuBaoGiaDAO;
import dao.PhieuTiepNhanDAO;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.DichVu;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuTiepNhan;
import enums.ChucVu;
import enums.TrangThaiBaoGia;
import enums.TrangThaiPhieuTiepNhan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhieuBaoGiaBUS {
    private final PhieuBaoGiaDAO phieuBaoGiaDAO = new PhieuBaoGiaDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    public String taoMaBaoGia(String maTiepNhan, int soThuTu) {
        if (maTiepNhan == null || !maTiepNhan.matches("^" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "$")) {
            throw new IllegalArgumentException("Mã tiếp nhận không hợp lệ để sinh mã báo giá");
        }
        if (soThuTu < 1 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự phiếu báo giá phải từ 001 đến 999");
        }
        return String.format("BG-%s-%03d", maTiepNhan, soThuTu);
    }

    public String phatSinhMaBaoGia(String maTiepNhan) {
        if (maTiepNhan == null || !maTiepNhan.matches("^" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "$")) {
            throw new IllegalArgumentException("Mã tiếp nhận không hợp lệ để sinh mã báo giá");
        }
        String tienTo = "BG-" + maTiepNhan + "-";
        String maxMa = phieuBaoGiaDAO.layMaLonNhatTheoTienTo(tienTo);
        int next = 1;
        if (maxMa != null && maxMa.startsWith(tienTo)) {
            next = Integer.parseInt(maxMa.substring(tienTo.length())) + 1;
        }
        return taoMaBaoGia(maTiepNhan, next);
    }

    public List<PhieuBaoGia> layTatCa() {
        return phieuBaoGiaDAO.layTatCa();
    }

    public List<PhieuBaoGia> layTheoPhieuTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tiếp nhận không được để trống");
        }
        return phieuBaoGiaDAO.timTheoPhieuTiepNhan(maTiepNhan.trim());
    }

    public PhieuBaoGia layBaoGiaMoiNhat(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) {
            return null;
        }
        return phieuBaoGiaDAO.timMoiNhatTheoTiepNhan(maTiepNhan.trim());
    }

    public PhieuBaoGia timTheoMa(String maBaoGia) {
        if (maBaoGia == null || maBaoGia.trim().isEmpty()) {
            return null;
        }
        return phieuBaoGiaDAO.timTheoMa(maBaoGia.trim());
    }

    public boolean lapPhieuBaoGia(PhieuBaoGia pbg) {
        if (pbg == null || pbg.getPhieuTiepNhan() == null || pbg.getNhanVien() == null) {
            throw new IllegalArgumentException("Phiếu báo giá phải gắn với phiếu tiếp nhận và kỹ thuật viên");
        }
        if (pbg.getDanhSachChiTiet() == null || pbg.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Phiếu báo giá phải có ít nhất 1 hạng mục dịch vụ");
        }
        PhieuTiepNhan ptn = phieuTiepNhanDAO.timTheoMa(pbg.getPhieuTiepNhan().getMaTiepNhan());
        if (ptn == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu tiếp nhận: "
                    + pbg.getPhieuTiepNhan().getMaTiepNhan());
        }
        if (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY
                || ptn.getTrangThai() == TrangThaiPhieuTiepNhan.HOAN_THANH
                || ptn.getTrangThai() == TrangThaiPhieuTiepNhan.THANH_TOAN) {
            throw new IllegalStateException("Không thể lập báo giá cho phiếu tiếp nhận đã hủy, đã hoàn thành hoặc đã thanh toán");
        }
        NhanVien nv = nhanVienDAO.timTheoMa(pbg.getNhanVien().getMaNhanVien());
        if (nv == null || !nv.isTrangThai() || (nv.getChucVu() != ChucVu.KY_THUAT_VIEN && nv.getChucVu() != ChucVu.LE_TAN && nv.getChucVu() != ChucVu.QUAN_LY)) {
            throw new IllegalArgumentException("Nhân viên lập phiếu báo giá phải là Kỹ thuật viên hoặc Lễ tân đang làm việc");
        }

        if (pbg.getThoiGianLap() == null) {
            pbg.setThoiGianLap(LocalDateTime.now());
        }
        if (pbg.getTrangThai() == null) {
            pbg.setTrangThai(TrangThaiBaoGia.CHO_XAC_NHAN);
        }
        if (pbg.getMaBaoGia() == null || pbg.getMaBaoGia().trim().isEmpty()) {
            pbg.setMaBaoGia(phatSinhMaBaoGia(ptn.getMaTiepNhan()));
        } else if (phieuBaoGiaDAO.timTheoMa(pbg.getMaBaoGia()) != null) {
            throw new IllegalArgumentException("Mã báo giá đã tồn tại: " + pbg.getMaBaoGia());
        }

        for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
            ctbg.setPhieuBaoGia(pbg);
            for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                pt.setChiTietBaoGia(ctbg);
            }
        }

        pbg.truKhoTatCaPhuTung();

        boolean daLuu = phieuBaoGiaDAO.them(pbg);
        if (daLuu && pbg.getTrangThai() == TrangThaiBaoGia.CHO_XAC_NHAN) {
            phieuTiepNhanDAO.capNhatTrangThai(ptn.getMaTiepNhan(), TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA);
        }
        return daLuu;
    }

    public PhieuBaoGia khoiTaoBaoGiaBanDau(PhieuTiepNhan ptn, NhanVien nvLap, List<DichVu> dsDichVu) {
        if (ptn == null) {
            return null;
        }
        PhieuBaoGia pbg = new PhieuBaoGia();
        pbg.setPhieuTiepNhan(ptn);
        pbg.setNhanVien(nvLap != null ? nvLap : ptn.getNhanVien());
        pbg.setThoiGianLap(LocalDateTime.now());
        pbg.setTrangThai(TrangThaiBaoGia.CHO_XAC_NHAN);
        pbg.setMaBaoGia(phatSinhMaBaoGia(ptn.getMaTiepNhan()));
        pbg.setGhiChu("Báo giá dịch vụ khách yêu cầu ban đầu");

        List<ChiTietBaoGia> dsCT = new ArrayList<>();
        boolean coDV000 = false;
        if (dsDichVu != null) {
            for (DichVu dv : dsDichVu) {
                if ("DV000".equalsIgnoreCase(dv.getMaDichVu())) {
                    coDV000 = true;
                }
                ChiTietBaoGia ct = new ChiTietBaoGia(dv.getDonGia(), false, "", pbg, dv);
                dsCT.add(ct);
            }
        }

        // Nếu xe đang ở trạng thái DANG_KIEM_TRA và chưa có DV000, thêm DV000 vào đầu bảng báo giá
        if (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.DANG_KIEM_TRA && !coDV000) {
            try {
                DichVu dv000 = dichVuDAO.timTheoMa("DV000");
                if (dv000 != null) {
                    ChiTietBaoGia ctKiemTra = new ChiTietBaoGia(dv000.getDonGia(), false, "Khảo sát và chẩn đoán tổng quát", pbg, dv000);
                    dsCT.add(0, ctKiemTra);
                }
            } catch (Exception ignored) {
            }
        }

        pbg.setDanhSachChiTiet(dsCT);
        phieuBaoGiaDAO.them(pbg);
        return pbg;
    }

    public boolean capNhatBaoGiaKTV(PhieuBaoGia pbg) {
        return guiKetQuaKiemTraVaBaoGia(pbg, pbg != null ? pbg.getGhiChu() : null);
    }

    /**
     * Kỹ thuật viên gửi kết quả kiểm tra & bảng báo giá đề xuất lên cho Lễ tân:
     * 1. Cập nhật / Lưu phiếu báo giá cùng danh sách dịch vụ và phụ tùng đề xuất.
     * 2. Hoàn thành nhiệm vụ phân công kiểm tra xe (TrangThaiSua.KIEM_TRA -> HOAN_THANH).
     * 3. Chuyển trạng thái phiếu tiếp nhận sang CHO_XAC_NHAN_BAO_GIA (để Lễ tân báo giá cho khách).
     * Tuyệt đối không chuyển sang HOAN_THANH hay THANH_TOAN.
     */
    public boolean guiKetQuaKiemTraVaBaoGia(PhieuBaoGia pbg, String ketLuanKiemTra) {
        if (pbg == null || pbg.getMaBaoGia() == null) {
            throw new IllegalArgumentException("Phiếu báo giá không hợp lệ");
        }
        if (pbg.getPhieuTiepNhan() == null || pbg.getPhieuTiepNhan().getMaTiepNhan() == null) {
            throw new IllegalArgumentException("Phiếu báo giá phải gắn với phiếu tiếp nhận hợp lệ");
        }

        String maTiepNhan = pbg.getPhieuTiepNhan().getMaTiepNhan();
        PhieuTiepNhan ptn = phieuTiepNhanDAO.timTheoMa(maTiepNhan);
        if (ptn == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu tiếp nhận: " + maTiepNhan);
        }
        if (ptn.getTrangThai() == TrangThaiPhieuTiepNhan.DA_HUY
                || ptn.getTrangThai() == TrangThaiPhieuTiepNhan.HOAN_THANH
                || ptn.getTrangThai() == TrangThaiPhieuTiepNhan.THANH_TOAN) {
            throw new IllegalStateException("Không thể gửi báo giá cho phiếu tiếp nhận đã kết thúc");
        }

        for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
            ctbg.setPhieuBaoGia(pbg);
            for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                pt.setChiTietBaoGia(ctbg);
            }
        }

        pbg.setTrangThai(TrangThaiBaoGia.CHO_XAC_NHAN);
        if (ketLuanKiemTra != null && !ketLuanKiemTra.trim().isEmpty()) {
            pbg.setGhiChu(ketLuanKiemTra.trim());
        }

        boolean ok;
        if (phieuBaoGiaDAO.timTheoMa(pbg.getMaBaoGia()) == null) {
            ok = phieuBaoGiaDAO.them(pbg);
        } else {
            ok = phieuBaoGiaDAO.capNhat(pbg);
        }

        if (!ok) {
            return false;
        }

        // Hoàn tất nhiệm vụ kiểm tra xe (nếu có)
        new PhieuPhanCongBUS().hoanThanhKiemTraXe(maTiepNhan, ketLuanKiemTra);

        // Chuyển trạng thái phiếu tiếp nhận sang Chờ xác nhận báo giá
        phieuTiepNhanDAO.capNhatTrangThai(maTiepNhan, TrangThaiPhieuTiepNhan.CHO_XAC_NHAN_BAO_GIA);

        return true;
    }

    public boolean duyetBaoGia(String maBaoGia, List<String> dsMaDichVuChapNhan) {
        PhieuBaoGia pbg = phieuBaoGiaDAO.timTheoMa(maBaoGia);
        if (pbg == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu báo giá: " + maBaoGia);
        }
        if (pbg.getTrangThai() != TrangThaiBaoGia.CHO_XAC_NHAN) {
            throw new IllegalStateException("Chỉ có thể duyệt phiếu báo giá đang ở trạng thái Chờ xác nhận");
        }
        if (dsMaDichVuChapNhan == null || dsMaDichVuChapNhan.isEmpty()) {
            throw new IllegalArgumentException("Phải có ít nhất 1 hạng mục dịch vụ được khách chấp thuận khi duyệt báo giá");
        }

        Set<String> tapChapNhan = new HashSet<>(dsMaDichVuChapNhan);
        boolean coHangMucDuocDuyet = false;
        for (ChiTietBaoGia ct : pbg.getDanhSachChiTiet()) {
            boolean chapNhan = tapChapNhan.contains(ct.getDichVu().getMaDichVu());
            ct.setDuocChapNhan(chapNhan);
            if (chapNhan) {
                coHangMucDuocDuyet = true;
            }
        }
        if (!coHangMucDuocDuyet) {
            throw new IllegalArgumentException("Không có mã dịch vụ nào khớp với chi tiết báo giá");
        }

        // Nghiệp vụ: Duyệt báo giá và hoàn kho các phụ tùng thuộc hạng mục không được chấp nhận
        pbg.xacNhan();

        boolean daCapNhat = phieuBaoGiaDAO.capNhat(pbg);
        if (daCapNhat && pbg.getPhieuTiepNhan() != null) {
            phieuTiepNhanDAO.capNhatTrangThai(
                    pbg.getPhieuTiepNhan().getMaTiepNhan(),
                    TrangThaiPhieuTiepNhan.DANG_SUA_CHUA
            );
        }
        return daCapNhat;
    }

    public boolean duyetBaoGia(PhieuBaoGia pbg) {
        if (pbg == null || pbg.getMaBaoGia() == null) {
            throw new IllegalArgumentException("Phiếu báo giá không hợp lệ");
        }
        boolean coHangMucDuocDuyet = false;
        for (ChiTietBaoGia ct : pbg.getDanhSachChiTiet()) {
            if (ct.isDuocChapNhan()) {
                coHangMucDuocDuyet = true;
                break;
            }
        }
        if (!coHangMucDuocDuyet) {
            throw new IllegalArgumentException("Phải có ít nhất 1 hạng mục được chấp thuận khi duyệt báo giá");
        }
        pbg.xacNhan();
        boolean daCapNhat = phieuBaoGiaDAO.capNhat(pbg);
        if (daCapNhat && pbg.getPhieuTiepNhan() != null) {
            phieuTiepNhanDAO.capNhatTrangThai(
                    pbg.getPhieuTiepNhan().getMaTiepNhan(),
                    TrangThaiPhieuTiepNhan.DANG_SUA_CHUA
            );
        }
        return daCapNhat;
    }

    public boolean tuChoiBaoGia(String maBaoGia, String lyDoTuChoi) {
        PhieuBaoGia pbg = phieuBaoGiaDAO.timTheoMa(maBaoGia);
        if (pbg == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu báo giá: " + maBaoGia);
        }
        if (pbg.getTrangThai() == TrangThaiBaoGia.TU_CHOI) {
            throw new IllegalStateException("Phiếu báo giá này đã bị từ chối trước đó");
        }

        // Nghiệp vụ: Từ chối báo giá và hoàn trả toàn bộ phụ tùng đã tạm trừ về kho
        pbg.tuChoi(lyDoTuChoi);
        for (ChiTietBaoGia ct : pbg.getDanhSachChiTiet()) {
            ct.setDuocChapNhan(false);
        }
        boolean daCapNhat = phieuBaoGiaDAO.capNhat(pbg);
        if (daCapNhat && pbg.getPhieuTiepNhan() != null) {
            phieuTiepNhanDAO.capNhatTrangThai(
                    pbg.getPhieuTiepNhan().getMaTiepNhan(),
                    TrangThaiPhieuTiepNhan.DA_HUY
            );
        }
        return daCapNhat;
    }

    public boolean xoaPhieuBaoGia(String maBaoGia) {
        PhieuBaoGia pbg = phieuBaoGiaDAO.timTheoMa(maBaoGia);
        if (pbg == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu báo giá: " + maBaoGia);
        }
        pbg.tuChoi("Xóa báo giá - hoàn kho");
        phieuBaoGiaDAO.capNhat(pbg);
        boolean daXoa = phieuBaoGiaDAO.xoa(maBaoGia);
        if (daXoa && pbg.getPhieuTiepNhan() != null) {
            phieuTiepNhanDAO.capNhatTrangThai(
                    pbg.getPhieuTiepNhan().getMaTiepNhan(),
                    TrangThaiPhieuTiepNhan.DA_HUY
            );
        }
        return daXoa;
    }
}

