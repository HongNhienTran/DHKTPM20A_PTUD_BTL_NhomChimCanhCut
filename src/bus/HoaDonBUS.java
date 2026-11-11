package bus;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.MaKhuyenMaiDAO;
import dao.NhanVienDAO;
import dao.PhieuBaoGiaDAO;
import dao.PhieuTiepNhanDAO;
import dao.SanPhamDAO;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.MaKhuyenMai;
import entity.NhanVien;
import entity.PhieuBaoGia;
import entity.PhieuTiepNhan;
import entity.SanPham;
import enums.ChucVu;
import enums.LoaiHangMuc;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiBaoGia;
import enums.TrangThaiPhieuTiepNhan;
import enums.TrangThaiThanhToan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDonBUS {
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("ddMMyy");
    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HHmmss");

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final MaKhuyenMaiDAO maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    private final PhieuTiepNhanDAO phieuTiepNhanDAO = new PhieuTiepNhanDAO();
    private final PhieuBaoGiaDAO phieuBaoGiaDAO = new PhieuBaoGiaDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final MaKhuyenMaiBUS maKhuyenMaiBUS = new MaKhuyenMaiBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    public String taoMaHoaDon(LoaiHoaDon loaiHoaDon, String maKhachHang, LocalDateTime thoiGianMua) {
        if (loaiHoaDon == null) {
            throw new IllegalArgumentException("Loại hóa đơn không được null");
        }
        if (maKhachHang == null || !maKhachHang.matches("^" + KhachHang.REGEX_MA_KHACH_HANG + "$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ để sinh mã hóa đơn");
        }
        if (thoiGianMua == null) {
            throw new IllegalArgumentException("Thời gian mua không được null");
        }
        String kyHieuLoai = (loaiHoaDon == LoaiHoaDon.BAN_HANG) ? "BH" : "DV";
        return String.format("HD-%s-%s-%s-%s",
                kyHieuLoai,
                maKhachHang,
                thoiGianMua.format(DINH_DANG_NGAY),
                thoiGianMua.format(DINH_DANG_GIO));
    }

    public String phatSinhMaHoaDon(LoaiHoaDon loaiHoaDon, String maKhachHang, LocalDateTime thoiGianMua) {
        LocalDateTime moc = thoiGianMua != null ? thoiGianMua : LocalDateTime.now();
        String maUngVien = taoMaHoaDon(loaiHoaDon, maKhachHang, moc);
        while (hoaDonDAO.timTheoMa(maUngVien) != null) {
            moc = moc.plusSeconds(1);
            maUngVien = taoMaHoaDon(loaiHoaDon, maKhachHang, moc);
        }
        return maUngVien;
    }

    public List<HoaDon> layTatCa() {
        return hoaDonDAO.layTatCa();
    }

    public List<HoaDon> layTheoKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống");
        }
        return hoaDonDAO.timTheoKhachHang(maKhachHang.trim());
    }

    public HoaDon timTheoPhieuTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) {
            return null;
        }
        return hoaDonDAO.timTheoPhieuTiepNhan(maTiepNhan.trim());
    }

    public HoaDon timTheoMa(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            return null;
        }
        return hoaDonDAO.timTheoMa(maHoaDon.trim());
    }

    public boolean lapHoaDonBanHang(HoaDon hd) {
        if (hd == null) {
            throw new IllegalArgumentException("Hóa đơn không được null");
        }
        hd.setLoaiHoaDon(LoaiHoaDon.BAN_HANG);
        hd.setPhieuTiepNhan(null);
        // Nếu khách mua lẻ không cung cấp hồ sơ khách hàng hoặc chọn khách vãng lai -> mặc định gán KH000000000
        if (hd.getKhachHang() == null || hd.getKhachHang().laKhachVangLai()) {
            hd.setKhachHang(khachHangBUS.layHoacTaoKhachVangLai());
        }
        xacThucThongTinChungHoaDon(hd);

        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            if (ct.getLoaiHangMuc() != LoaiHangMuc.SAN_PHAM || ct.getSanPham() == null) {
                throw new IllegalArgumentException("Hóa đơn bán hàng lẻ chỉ được chứa hạng mục sản phẩm");
            }
            SanPham spKho = sanPhamDAO.timTheoMa(ct.getSanPham().getMaSanPham());
            if (spKho == null || !spKho.isTrangThai()) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại hoặc ngừng kinh doanh: "
                        + ct.getSanPham().getMaSanPham());
            }

            if (ct.getSoLuong() <= 0) {
                throw new IllegalArgumentException(
                        "Số lượng bán phải lớn hơn 0: " + spKho.getTenSanPham()
                );
            }

            if (spKho.getSoLuongTon() < ct.getSoLuong()) {
                throw new IllegalStateException(
                        "Sản phẩm '" + spKho.getTenSanPham()
                                + "' không đủ tồn kho (còn " + spKho.getSoLuongTon()
                                + ", cần " + ct.getSoLuong() + ")"
                );
            }
            ct.setSanPham(spKho);
            ct.setHoaDon(hd);
        }

        xuLyMaKhuyenMaiKhiLapHoaDon(hd);
        return hoaDonDAO.them(hd, true);
    }

    public HoaDon taoHoaDonDichVuTuPhieuTiepNhan(String maTiepNhan, NhanVien nhanVienLeTan,
                                                 String maKhuyenMai, String ghiChu) {
        PhieuTiepNhan ptn = phieuTiepNhanDAO.timTheoMa(maTiepNhan);
        if (ptn == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu tiếp nhận: " + maTiepNhan);
        }
        if (hoaDonDAO.timTheoPhieuTiepNhan(maTiepNhan) != null) {
            throw new IllegalStateException("Phiếu tiếp nhận " + maTiepNhan + " đã được xuất hóa đơn trước đó");
        }

        PhieuBaoGia pbg = phieuBaoGiaDAO.timMoiNhatTheoTiepNhan(maTiepNhan);
        if (pbg == null) {
            throw new IllegalStateException("Phiếu tiếp nhận " + maTiepNhan + " chưa có báo giá để lập hóa đơn");
        }
        if (pbg.getTrangThai() != TrangThaiBaoGia.DA_CHAP_THUAN) {
            for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
                ctbg.setDuocChapNhan(true);
            }
            pbg.setTrangThai(TrangThaiBaoGia.DA_CHAP_THUAN);
            phieuBaoGiaDAO.capNhat(pbg);
        }

        KhachHang kh = ptn.getPhuongTien().getKhachHang();
        MaKhuyenMai km = (maKhuyenMai != null && !maKhuyenMai.trim().isEmpty())
                ? maKhuyenMaiDAO.timTheoMa(maKhuyenMai.trim())
                : null;

        LocalDateTime now = LocalDateTime.now();
        String maHD = phatSinhMaHoaDon(LoaiHoaDon.DICH_VU, kh.getMaKhachHang(), now);
        HoaDon hd = new HoaDon(
                maHD,
                LoaiHoaDon.DICH_VU,
                now,
                TrangThaiThanhToan.CHUA_THANH_TOAN,
                null,
                null,
                ghiChu,
                kh,
                nhanVienLeTan,
                km,
                ptn
        );

        List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
        Map<String, ChiTietHoaDon> gopPhuTung = new LinkedHashMap<>();

        for (ChiTietBaoGia ctbg : pbg.getDanhSachChiTiet()) {
            if (!ctbg.isDuocChapNhan()) {
                continue;
            }
            dsChiTiet.add(new ChiTietHoaDon(
                    LoaiHangMuc.DICH_VU,
                    1,
                    ctbg.getDonGiaDichVu(),
                    hd,
                    ctbg.getDichVu(),
                    null
            ));
            for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                String maSP = pt.getSanPham().getMaSanPham();
                ChiTietHoaDon dongSP = gopPhuTung.get(maSP);
                if (dongSP == null) {
                    gopPhuTung.put(maSP, new ChiTietHoaDon(
                            LoaiHangMuc.SAN_PHAM,
                            pt.getSoLuong(),
                            pt.getDonGia(),
                            hd,
                            null,
                            pt.getSanPham()
                    ));
                } else {
                    dongSP.setSoLuong(dongSP.getSoLuong() + pt.getSoLuong());
                }
            }
        }
        dsChiTiet.addAll(gopPhuTung.values());
        hd.setDanhSachChiTiet(dsChiTiet);

        lapHoaDonDichVu(hd);
        return hd;
    }

    public boolean lapHoaDonDichVu(HoaDon hd) {
        if (hd == null || hd.getPhieuTiepNhan() == null) {
            throw new IllegalArgumentException("Hóa đơn dịch vụ bắt buộc phải gắn với phiếu tiếp nhận");
        }
        hd.setLoaiHoaDon(LoaiHoaDon.DICH_VU);
        xacThucThongTinChungHoaDon(hd);

        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            ct.setHoaDon(hd);
        }

        // Không trừ kho lần 2 vì phụ tùng trong báo giá đã được giữ/trừ kho từ lúc tạo báo giá
        xuLyMaKhuyenMaiKhiLapHoaDon(hd);
        return hoaDonDAO.them(hd, false);
    }

    public boolean thanhToanHoaDon(String maHoaDon, PhuongThucThanhToan phuongThuc, String ghiChu) {
        if (phuongThuc == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không được null");
        }
        HoaDon hd = hoaDonDAO.timTheoMa(maHoaDon);
        if (hd == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn: " + maHoaDon);
        }
        if (hd.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
            throw new IllegalStateException("Hóa đơn này đã được thanh toán trước đó");
        }

        hd.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
        hd.setPhuongThucThanhToan(phuongThuc);
        hd.setThoiGianThanhToan(LocalDateTime.now());
        if (ghiChu != null && !ghiChu.trim().isEmpty()) {
            hd.setGhiChu(ghiChu.trim());
        }

        boolean daThanhToan = hoaDonDAO.capNhatThanhToan(hd);
        if (daThanhToan && hd.getPhieuTiepNhan() != null) {
            phieuTiepNhanDAO.capNhatTrangThai(
                    hd.getPhieuTiepNhan().getMaTiepNhan(),
                    TrangThaiPhieuTiepNhan.THANH_TOAN
            );
        }
        return daThanhToan;
    }

    public boolean xoaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn cần xóa không được để trống");
        }
        return hoaDonDAO.xoa(maHoaDon.trim());
    }

    private void xacThucThongTinChungHoaDon(HoaDon hd) {
        if (hd.getKhachHang() == null || hd.getNhanVien() == null) {
            throw new IllegalArgumentException("Hóa đơn phải có đầy đủ khách hàng và nhân viên lập");
        }
        if (hd.getDanhSachChiTiet() == null || hd.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất 1 chi tiết hạng mục");
        }
        if (khachHangDAO.timTheoMa(hd.getKhachHang().getMaKhachHang()) == null) {
            throw new IllegalArgumentException("Khách hàng không tồn tại: " + hd.getKhachHang().getMaKhachHang());
        }
        NhanVien nv = nhanVienDAO.timTheoMa(hd.getNhanVien().getMaNhanVien());
        if (nv == null || !nv.isTrangThai() || nv.getChucVu() != ChucVu.LE_TAN) {
            throw new IllegalArgumentException("Nhân viên lập hóa đơn phải là Lễ tân đang làm việc");
        }
        if (hd.getThoiGianLap() == null) {
            hd.setThoiGianLap(LocalDateTime.now());
        }
        if (hd.getTrangThaiThanhToan() == null) {
            hd.setTrangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN);
        }
        if (hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) {
            hd.setMaHoaDon(phatSinhMaHoaDon(
                    hd.getLoaiHoaDon(),
                    hd.getKhachHang().getMaKhachHang(),
                    hd.getThoiGianLap()
            ));
        } else if (hoaDonDAO.timTheoMa(hd.getMaHoaDon()) != null) {
            throw new IllegalArgumentException("Mã hóa đơn đã tồn tại: " + hd.getMaHoaDon());
        }
    }

    private void xuLyMaKhuyenMaiKhiLapHoaDon(HoaDon hd) {
        if (hd.getMaKhuyenMai() == null) {
            return;
        }
        MaKhuyenMai km = maKhuyenMaiDAO.timTheoMa(hd.getMaKhuyenMai().getMaKhuyenMai());
        if (km == null) {
            throw new IllegalArgumentException("Mã khuyến mãi không tồn tại: "
                    + hd.getMaKhuyenMai().getMaKhuyenMai());
        }
        maKhuyenMaiBUS.xacThucApDungKhuyenMai(km, hd.tinhTongTienHang(), hd.getThoiGianLap());
        if (km.getSoLuongTon() != null) {
            km.setSoLuongTon(km.getSoLuongTon() - 1);
        }
        hd.setMaKhuyenMai(km);
    }
}

