package entity;

import enums.TrangThaiPhieuTiepNhan;

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuTiepNhan {
    public static final String REGEX_MA_TIEP_NHAN =
            "TN-" + PhuongTien.REGEX_MA_PHUONG_TIEN + "-\\d{6}-(?!00)\\d{2}";

    private String maTiepNhan;
    private LocalDateTime thoiGianTiepNhan;
    private TrangThaiPhieuTiepNhan trangThai;
    private double soKMHienTai;
    private String yeuCauKhachHang;
    private String tinhTrangNoiThat;
    private String tinhTrangNgoaiThat;
    private int soChiaKhoa;
    private String ghiChu;

    private PhuongTien phuongTien;
    private NhanVien nhanVien;

    public PhieuTiepNhan() {
    }

    public PhieuTiepNhan(String maTiepNhan) {
        setMaTiepNhan(maTiepNhan);
    }

    public PhieuTiepNhan(String maTiepNhan, LocalDateTime thoiGianTiepNhan,
                         TrangThaiPhieuTiepNhan trangThai, double soKMHienTai,
                         String yeuCauKhachHang, String tinhTrangNoiThat,
                         String tinhTrangNgoaiThat, int soChiaKhoa, String ghiChu,
                         PhuongTien phuongTien, NhanVien nhanVien) {
        setPhuongTien(phuongTien);
        setNhanVien(nhanVien);
        setMaTiepNhan(maTiepNhan);
        setThoiGianTiepNhan(thoiGianTiepNhan);
        setTrangThai(trangThai);
        setSoKMHienTai(soKMHienTai);
        setYeuCauKhachHang(yeuCauKhachHang);
        setTinhTrangNoiThat(tinhTrangNoiThat);
        setTinhTrangNgoaiThat(tinhTrangNgoaiThat);
        setSoChiaKhoa(soChiaKhoa);
        setGhiChu(ghiChu);
    }

    public String getMaTiepNhan() {
        return maTiepNhan;
    }

    public void setMaTiepNhan(String maTiepNhan) {
        if (maTiepNhan == null || !maTiepNhan.matches("^" + REGEX_MA_TIEP_NHAN + "$")) {
            throw new IllegalArgumentException(
                    "Mã phiếu tiếp nhận không hợp lệ (định dạng: TN-{mã phương tiện}-{xxxxxx ngày}-{xx từ 01 đến 99})");
        }
        if (this.phuongTien != null && this.phuongTien.getMaPhuongTien() != null
                && !maTiepNhan.startsWith("TN-" + this.phuongTien.getMaPhuongTien() + "-")) {
            throw new IllegalArgumentException(
                    "Mã phiếu tiếp nhận phải chứa đúng mã phương tiện (" + this.phuongTien.getMaPhuongTien() + ")");
        }
        this.maTiepNhan = maTiepNhan;
    }

    public LocalDateTime getThoiGianTiepNhan() {
        return thoiGianTiepNhan;
    }

    public void setThoiGianTiepNhan(LocalDateTime thoiGianTiepNhan) {
        this.thoiGianTiepNhan = thoiGianTiepNhan;
    }

    public TrangThaiPhieuTiepNhan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        this.trangThai = trangThai;
    }

    public double getSoKMHienTai() {
        return soKMHienTai;
    }

    public void setSoKMHienTai(double soKMHienTai) {
        if (soKMHienTai < 0) {
            throw new IllegalArgumentException("Số KM hiện tại không được âm");
        }
        this.soKMHienTai = soKMHienTai;
    }

    public String getYeuCauKhachHang() {
        return yeuCauKhachHang;
    }

    public void setYeuCauKhachHang(String yeuCauKhachHang) {
        if (yeuCauKhachHang == null || yeuCauKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Yêu cầu khách hàng không được để trống");
        }
        this.yeuCauKhachHang = yeuCauKhachHang.trim();
    }

    public String getTinhTrangNoiThat() {
        return tinhTrangNoiThat;
    }

    public void setTinhTrangNoiThat(String tinhTrangNoiThat) {
        this.tinhTrangNoiThat = tinhTrangNoiThat;
    }

    public String getTinhTrangNgoaiThat() {
        return tinhTrangNgoaiThat;
    }

    public void setTinhTrangNgoaiThat(String tinhTrangNgoaiThat) {
        this.tinhTrangNgoaiThat = tinhTrangNgoaiThat;
    }

    public int getSoChiaKhoa() {
        return soChiaKhoa;
    }

    public void setSoChiaKhoa(int soChiaKhoa) {
        if (soChiaKhoa < 0) {
            throw new IllegalArgumentException("Số chìa khóa không được âm");
        }
        this.soChiaKhoa = soChiaKhoa;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public PhuongTien getPhuongTien() {
        return phuongTien;
    }

    public void setPhuongTien(PhuongTien phuongTien) {
        if (phuongTien != null && phuongTien.getMaPhuongTien() != null
                && this.maTiepNhan != null
                && !this.maTiepNhan.startsWith("TN-" + phuongTien.getMaPhuongTien() + "-")) {
            throw new IllegalArgumentException(
                    "Phương tiện không khớp với mã phiếu tiếp nhận hiện tại (" + this.maTiepNhan + ")");
        }
        this.phuongTien = phuongTien;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuTiepNhan that)) return false;
        return Objects.equals(maTiepNhan, that.maTiepNhan);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maTiepNhan);
    }

    @Override
    public String toString() {
        return "PhieuTiepNhan{" +
                "maTiepNhan='" + maTiepNhan + '\'' +
                ", thoiGianTiepNhan=" + thoiGianTiepNhan +
                ", trangThai=" + trangThai +
                ", soKMHienTai=" + soKMHienTai +
                ", phuongTien=" + (phuongTien != null ? phuongTien.getMaPhuongTien() : null) +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNhanVien() : null) +
                '}';
    }
}
