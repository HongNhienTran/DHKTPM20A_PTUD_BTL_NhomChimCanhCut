package entity;

import enums.TrangThaiSua;

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuPhanCong {
    public static final String REGEX_MA_PHAN_CONG =
            "PC-" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "-" + NhanVien.REGEX_MA_NHAN_VIEN + "-(?!00)\\d{2}";

    private String maPhanCong;
    private LocalDateTime thoiGianPhanCong;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianHoanThanh;
    private TrangThaiSua trangThaiSua;
    private String ghiChu;

    private NhanVien nhanVien;
    private ChiTietBaoGia chiTietBaoGia;

    public PhieuPhanCong() {
    }

    public PhieuPhanCong(String maPhanCong) {
        setMaPhanCong(maPhanCong);
    }

    public PhieuPhanCong(String maPhanCong, LocalDateTime thoiGianPhanCong,
                         LocalDateTime thoiGianBatDau, LocalDateTime thoiGianHoanThanh,
                         TrangThaiSua trangThaiSua, String ghiChu,
                         NhanVien nhanVien, ChiTietBaoGia chiTietBaoGia) {
        setNhanVien(nhanVien);
        setChiTietBaoGia(chiTietBaoGia);
        setMaPhanCong(maPhanCong);
        setThoiGianPhanCong(thoiGianPhanCong);
        setThoiGianBatDau(thoiGianBatDau);
        setThoiGianHoanThanh(thoiGianHoanThanh);
        setTrangThaiSua(trangThaiSua);
        setGhiChu(ghiChu);
    }

    public PhieuTiepNhan layPhieuTiepNhan() {
        if (chiTietBaoGia == null || chiTietBaoGia.getPhieuBaoGia() == null) {
            return null;
        }
        return chiTietBaoGia.getPhieuBaoGia().getPhieuTiepNhan();
    }

    private void kiemTraKhopMaPhanCong(String maPC, NhanVien nv, ChiTietBaoGia ctbg) {
        if (maPC == null) {
            return;
        }
        PhieuTiepNhan ptn = (ctbg != null && ctbg.getPhieuBaoGia() != null)
                ? ctbg.getPhieuBaoGia().getPhieuTiepNhan()
                : null;
        if (ptn != null && ptn.getMaTiepNhan() != null
                && !maPC.startsWith("PC-" + ptn.getMaTiepNhan() + "-")) {
            throw new IllegalArgumentException(
                    "Mã phân công phải chứa đúng mã phiếu tiếp nhận (" + ptn.getMaTiepNhan() + ")");
        }
        if (nv != null && nv.getMaNhanVien() != null
                && !maPC.matches("^PC-" + PhieuTiepNhan.REGEX_MA_TIEP_NHAN + "-" + nv.getMaNhanVien() + "-(?!00)\\d{2}$")) {
            throw new IllegalArgumentException(
                    "Mã phân công phải chứa đúng mã nhân viên (" + nv.getMaNhanVien() + ")");
        }
    }

    public String getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(String maPhanCong) {
        if (maPhanCong == null || !maPhanCong.matches("^" + REGEX_MA_PHAN_CONG + "$")) {
            throw new IllegalArgumentException(
                    "Mã phân công không hợp lệ (định dạng: PC-{mã phiếu tiếp nhận}-{mã nhân viên}-{xx từ 01 đến 99})");
        }
        kiemTraKhopMaPhanCong(maPhanCong, this.nhanVien, this.chiTietBaoGia);
        this.maPhanCong = maPhanCong;
    }

    public LocalDateTime getThoiGianPhanCong() {
        return thoiGianPhanCong;
    }

    public void setThoiGianPhanCong(LocalDateTime thoiGianPhanCong) {
        this.thoiGianPhanCong = thoiGianPhanCong;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        if (thoiGianBatDau != null && this.thoiGianHoanThanh != null
                && this.thoiGianHoanThanh.isBefore(thoiGianBatDau)) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được sau thời gian hoàn thành");
        }
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianHoanThanh() {
        return thoiGianHoanThanh;
    }

    public void setThoiGianHoanThanh(LocalDateTime thoiGianHoanThanh) {
        if (thoiGianHoanThanh != null && this.thoiGianBatDau != null
                && thoiGianHoanThanh.isBefore(this.thoiGianBatDau)) {
            throw new IllegalArgumentException("Thời gian hoàn thành không được trước thời gian bắt đầu");
        }
        this.thoiGianHoanThanh = thoiGianHoanThanh;
    }

    public TrangThaiSua getTrangThaiSua() {
        return trangThaiSua;
    }

    public void setTrangThaiSua(TrangThaiSua trangThaiSua) {
        this.trangThaiSua = trangThaiSua;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        kiemTraKhopMaPhanCong(this.maPhanCong, nhanVien, this.chiTietBaoGia);
        this.nhanVien = nhanVien;
    }

    public ChiTietBaoGia getChiTietBaoGia() {
        return chiTietBaoGia;
    }

    public void setChiTietBaoGia(ChiTietBaoGia chiTietBaoGia) {
        kiemTraKhopMaPhanCong(this.maPhanCong, this.nhanVien, chiTietBaoGia);
        this.chiTietBaoGia = chiTietBaoGia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuPhanCong that)) return false;
        return Objects.equals(maPhanCong, that.maPhanCong);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maPhanCong);
    }

    @Override
    public String toString() {
        return "PhieuPhanCong{" +
                "maPhanCong='" + maPhanCong + '\'' +
                ", thoiGianPhanCong=" + thoiGianPhanCong +
                ", thoiGianBatDau=" + thoiGianBatDau +
                ", thoiGianHoanThanh=" + thoiGianHoanThanh +
                ", trangThaiSua=" + trangThaiSua +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNhanVien() : null) +
                '}';
    }
}
