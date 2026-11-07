package entity;

import enums.LoaiGiamGia;
import enums.LoaiHoaDon;
import enums.PhuongThucThanhToan;
import enums.TrangThaiThanhToan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoaDon {
    public static final String REGEX_MA_HOA_DON =
            "HD-(BH|DV)-" + KhachHang.REGEX_MA_KHACH_HANG + "-\\d{6}-\\d{6}";

    private String maHoaDon;
    private LoaiHoaDon loaiHoaDon;
    private LocalDateTime thoiGianLap;
    private TrangThaiThanhToan trangThaiThanhToan;
    private PhuongThucThanhToan phuongThucThanhToan;
    private LocalDateTime thoiGianThanhToan;
    private String ghiChu;

    private KhachHang khachHang;
    private NhanVien nhanVien;
    private MaKhuyenMai maKhuyenMai;
    private PhieuTiepNhan phieuTiepNhan;
    private List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

    public HoaDon() {
    }

    public HoaDon(String maHoaDon) {
        setMaHoaDon(maHoaDon);
    }

    public HoaDon(String maHoaDon, LoaiHoaDon loaiHoaDon, LocalDateTime thoiGianLap,
                  TrangThaiThanhToan trangThaiThanhToan, PhuongThucThanhToan phuongThucThanhToan,
                  LocalDateTime thoiGianThanhToan, String ghiChu, KhachHang khachHang,
                  NhanVien nhanVien, MaKhuyenMai maKhuyenMai, PhieuTiepNhan phieuTiepNhan) {
        setLoaiHoaDon(loaiHoaDon);
        setKhachHang(khachHang);
        setNhanVien(nhanVien);
        setMaHoaDon(maHoaDon);
        setThoiGianLap(thoiGianLap);
        setTrangThaiThanhToan(trangThaiThanhToan);
        setPhuongThucThanhToan(phuongThucThanhToan);
        setThoiGianThanhToan(thoiGianThanhToan);
        setGhiChu(ghiChu);
        setMaKhuyenMai(maKhuyenMai);
        setPhieuTiepNhan(phieuTiepNhan);
    }

    private void kiemTraKhopMaHoaDon(String maHD, LoaiHoaDon loai, KhachHang kh) {
        if (maHD == null) {
            return;
        }
        if (loai != null) {
            String tienToLoai = (loai == LoaiHoaDon.BAN_HANG) ? "HD-BH-" : "HD-DV-";
            if (!maHD.startsWith(tienToLoai)) {
                throw new IllegalArgumentException(
                        "Mã hóa đơn phải bắt đầu bằng '" + tienToLoai + "' tương ứng với loại hóa đơn " + loai);
            }
        }
        if (kh != null && kh.getMaKhachHang() != null
                && !maHD.matches("^HD-(BH|DV)-" + kh.getMaKhachHang() + "-\\d{6}-\\d{6}$")) {
            throw new IllegalArgumentException(
                    "Mã hóa đơn phải chứa đúng mã khách hàng (" + kh.getMaKhachHang() + ")");
        }
    }

    public double tinhTongTienHang() {
        double tienHang = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            tienHang += ct.tinhThanhTien();
        }
        return tienHang;
    }

    public double tinhThue() {
        double thue = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            thue += ct.tinhThue();
        }
        return thue;
    }

    public double tinhTienKhuyenMai() {
        double tienHang = tinhTongTienHang();
        if (maKhuyenMai == null || !maKhuyenMai.isTrangThai()
                || tienHang < maKhuyenMai.getGiaTriToiThieuApDung()) {
            return 0;
        }

        double giam = 0;
        if (maKhuyenMai.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
            giam = tienHang * (maKhuyenMai.getGiaTriGiam() / 100.0);
        } else if (maKhuyenMai.getLoaiGiam() == LoaiGiamGia.SO_TIEN) {
            giam = maKhuyenMai.getGiaTriGiam();
        }
        return Math.min(tienHang, Math.max(0, giam));
    }

    public double tinhTongThanhToan() {
        return Math.max(0, tinhTongTienHang() + tinhThue() - tinhTienKhuyenMai());
    }

    public double tinhTongTien() {
        return tinhTongThanhToan();
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || !maHoaDon.matches("^" + REGEX_MA_HOA_DON + "$")) {
            throw new IllegalArgumentException(
                    "Mã hóa đơn không hợp lệ (định dạng: HD-BH/DV-{mã KH}-{xxxxxx ngày mua}-{xxxxxx giờ mua})");
        }
        kiemTraKhopMaHoaDon(maHoaDon, this.loaiHoaDon, this.khachHang);
        this.maHoaDon = maHoaDon;
    }

    public LoaiHoaDon getLoaiHoaDon() {
        return loaiHoaDon;
    }

    public void setLoaiHoaDon(LoaiHoaDon loaiHoaDon) {
        kiemTraKhopMaHoaDon(this.maHoaDon, loaiHoaDon, this.khachHang);
        this.loaiHoaDon = loaiHoaDon;
    }

    public LocalDateTime getThoiGianLap() {
        return thoiGianLap;
    }

    public void setThoiGianLap(LocalDateTime thoiGianLap) {
        this.thoiGianLap = thoiGianLap;
    }

    public TrangThaiThanhToan getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(TrangThaiThanhToan trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        kiemTraKhopMaHoaDon(this.maHoaDon, this.loaiHoaDon, khachHang);
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public MaKhuyenMai getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(MaKhuyenMai maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public PhieuTiepNhan getPhieuTiepNhan() {
        return phieuTiepNhan;
    }

    public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) {
        if (phieuTiepNhan != null && this.loaiHoaDon == LoaiHoaDon.BAN_HANG) {
            throw new IllegalArgumentException("Hóa đơn bán hàng lẻ (BAN_HANG) không gắn phiếu tiếp nhận");
        }
        this.phieuTiepNhan = phieuTiepNhan;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoaDon hoaDon)) return false;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", loaiHoaDon=" + loaiHoaDon +
                ", thoiGianLap=" + thoiGianLap +
                ", tongThanhToan=" + tinhTongThanhToan() +
                ", trangThaiThanhToan=" + trangThaiThanhToan +
                ", khachHang=" + (khachHang != null ? khachHang.getMaKhachHang() : null) +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNhanVien() : null) +
                '}';
    }
}
