package entity;

import enums.TrangThaiBaoGia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuBaoGia {
    public static final String REGEX_MA_BAO_GIA =
            "BG-" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "-(?!000)\\d{3}";

    private String maBaoGia;
    private LocalDateTime thoiGianLap;
    private TrangThaiBaoGia trangThai;
    private String lyDoTuChoi;
    private String ghiChu;

    private PhieuTiepNhan phieuTiepNhan;
    private NhanVien nhanVien;
    private List<ChiTietBaoGia> danhSachChiTiet = new ArrayList<>();

    public PhieuBaoGia() {
    }

    public PhieuBaoGia(String maBaoGia) {
        setMaBaoGia(maBaoGia);
    }

    public PhieuBaoGia(String maBaoGia, LocalDateTime thoiGianLap,
                       TrangThaiBaoGia trangThai, String lyDoTuChoi, String ghiChu,
                       PhieuTiepNhan phieuTiepNhan, NhanVien nhanVien) {
        setPhieuTiepNhan(phieuTiepNhan);
        setNhanVien(nhanVien);
        setMaBaoGia(maBaoGia);
        setThoiGianLap(thoiGianLap);
        setTrangThai(trangThai);
        setLyDoTuChoi(lyDoTuChoi);
        setGhiChu(ghiChu);
    }

    public double tinhTongTienDuKien() {
        double tong = 0;
        for (ChiTietBaoGia ct : danhSachChiTiet) {
            tong += ct.tinhThanhTien();
        }
        return tong;
    }

    public void truKhoTatCaPhuTung() {
        for (ChiTietBaoGia ct : danhSachChiTiet) {
            ct.truKhoPhuTung();
        }
    }

    public void xacNhan() {
        this.trangThai = TrangThaiBaoGia.DA_CHAP_THUAN;
        this.lyDoTuChoi = null;
        for (ChiTietBaoGia ct : danhSachChiTiet) {
            if (!ct.isDuocChapNhan()) {
                ct.hoanKhoPhuTung();
            }
        }
    }

    public void tuChoi() {
        tuChoi(null);
    }

    public void tuChoi(String lyDoTuChoi) {
        this.trangThai = TrangThaiBaoGia.TU_CHOI;
        this.lyDoTuChoi = lyDoTuChoi;
        for (ChiTietBaoGia ct : danhSachChiTiet) {
            ct.hoanKhoPhuTung();
        }
    }

    public String getMaBaoGia() {
        return maBaoGia;
    }

    public void setMaBaoGia(String maBaoGia) {
        if (maBaoGia == null || !maBaoGia.matches("^" + REGEX_MA_BAO_GIA + "$")) {
            throw new IllegalArgumentException(
                    "Mã báo giá không hợp lệ (định dạng: BG-{mã tiếp nhận}-{xxx}, 3 số cuối từ 001 đến 999)");
        }
        if (this.phieuTiepNhan != null && this.phieuTiepNhan.getMaTiepNhan() != null
                && !maBaoGia.startsWith("BG-" + this.phieuTiepNhan.getMaTiepNhan() + "-")) {
            throw new IllegalArgumentException(
                    "Mã báo giá phải chứa đúng mã tiếp nhận (" + this.phieuTiepNhan.getMaTiepNhan() + ")");
        }
        this.maBaoGia = maBaoGia;
    }

    public LocalDateTime getThoiGianLap() {
        return thoiGianLap;
    }

    public void setThoiGianLap(LocalDateTime thoiGianLap) {
        this.thoiGianLap = thoiGianLap;
    }

    public TrangThaiBaoGia getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiBaoGia trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public PhieuTiepNhan getPhieuTiepNhan() {
        return phieuTiepNhan;
    }

    public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) {
        if (phieuTiepNhan != null && phieuTiepNhan.getMaTiepNhan() != null
                && this.maBaoGia != null
                && !this.maBaoGia.startsWith("BG-" + phieuTiepNhan.getMaTiepNhan() + "-")) {
            throw new IllegalArgumentException(
                    "Phiếu tiếp nhận không khớp với mã báo giá hiện tại (" + this.maBaoGia + ")");
        }
        this.phieuTiepNhan = phieuTiepNhan;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public List<ChiTietBaoGia> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietBaoGia> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuBaoGia that)) return false;
        return Objects.equals(maBaoGia, that.maBaoGia);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maBaoGia);
    }

    @Override
    public String toString() {
        return "PhieuBaoGia{" +
                "maBaoGia='" + maBaoGia + '\'' +
                ", thoiGianLap=" + thoiGianLap +
                ", tongTienDuKien=" + tinhTongTienDuKien() +
                ", trangThai=" + trangThai +
                ", phieuTiepNhan=" + (phieuTiepNhan != null ? phieuTiepNhan.getMaTiepNhan() : null) +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNhanVien() : null) +
                '}';
    }
}
