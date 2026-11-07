package entity;

import enums.NhienLieu;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhuongTien {
    public static final String REGEX_MA_PHUONG_TIEN =
            "PT-" + KhachHang.REGEX_MA_KHACH_HANG + "-(?!000)\\d{3}";

    private String maPhuongTien;
    private String bienSoXe;
    private String mauXe;
    private String mauSacXe;
    private int namSanXuat;
    private NhienLieu nhienLieu;

    private HangXe hangXe;
    private KhachHang khachHang;
    private List<PhieuTiepNhan> lichSuDungDichVu = new ArrayList<>();

    public PhuongTien() {
    }

    public PhuongTien(String maPhuongTien) {
        setMaPhuongTien(maPhuongTien);
    }

    public PhuongTien(String maPhuongTien, String bienSoXe, String mauXe, String mauSacXe,
                      int namSanXuat, NhienLieu nhienLieu,
                      HangXe hangXe, KhachHang khachHang) {
        setKhachHang(khachHang);
        setHangXe(hangXe);
        setMaPhuongTien(maPhuongTien);
        setBienSoXe(bienSoXe);
        setMauXe(mauXe);
        setMauSacXe(mauSacXe);
        setNamSanXuat(namSanXuat);
        setNhienLieu(nhienLieu);
    }

    public List<PhieuTiepNhan> xemLSDungDichVu() {
        return lichSuDungDichVu;
    }

    public void setLichSuDungDichVu(List<PhieuTiepNhan> lichSuDungDichVu) {
        this.lichSuDungDichVu = lichSuDungDichVu != null ? lichSuDungDichVu : new ArrayList<>();
    }

    public String getMaPhuongTien() {
        return maPhuongTien;
    }

    public void setMaPhuongTien(String maPhuongTien) {
        if (maPhuongTien == null || !maPhuongTien.matches("^" + REGEX_MA_PHUONG_TIEN + "$")) {
            throw new IllegalArgumentException(
                    "Mã phương tiện không hợp lệ (định dạng: PT-{mã khách hàng}-{xxx}, 3 số cuối từ 001 đến 999)");
        }
        if (this.khachHang != null && this.khachHang.getMaKhachHang() != null
                && !maPhuongTien.startsWith("PT-" + this.khachHang.getMaKhachHang() + "-")) {
            throw new IllegalArgumentException(
                    "Mã phương tiện phải chứa đúng mã khách hàng (" + this.khachHang.getMaKhachHang() + ")");
        }
        this.maPhuongTien = maPhuongTien;
    }

    public String getBienSoXe() {
        return bienSoXe;
    }

    public void setBienSoXe(String bienSoXe) {
        if (bienSoXe == null || bienSoXe.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống");
        }
        this.bienSoXe = bienSoXe.trim();
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        if (mauXe == null || mauXe.trim().isEmpty()) {
            throw new IllegalArgumentException("Mẫu xe không được để trống");
        }
        this.mauXe = mauXe.trim();
    }

    public String getMauSacXe() {
        return mauSacXe;
    }

    public void setMauSacXe(String mauSacXe) {
        if (mauSacXe == null || mauSacXe.trim().isEmpty()) {
            throw new IllegalArgumentException("Màu sắc xe không được để trống");
        }
        this.mauSacXe = mauSacXe.trim();
    }

    public int getNamSanXuat() {
        return namSanXuat;
    }

    public void setNamSanXuat(int namSanXuat) {
        int namHienTai = Year.now().getValue();
        if (namSanXuat < 1900 || namSanXuat > namHienTai + 1) {
            throw new IllegalArgumentException("Năm sản xuất không hợp lệ (" + namSanXuat + ")");
        }
        this.namSanXuat = namSanXuat;
    }

    public NhienLieu getNhienLieu() {
        return nhienLieu;
    }

    public void setNhienLieu(NhienLieu nhienLieu) {
        if (nhienLieu == null) {
            throw new IllegalArgumentException("Nhiên liệu không được null");
        }
        this.nhienLieu = nhienLieu;
    }

    public HangXe getHangXe() {
        return hangXe;
    }

    public void setHangXe(HangXe hangXe) {
        this.hangXe = hangXe;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        if (khachHang != null && khachHang.getMaKhachHang() != null
                && this.maPhuongTien != null
                && !this.maPhuongTien.startsWith("PT-" + khachHang.getMaKhachHang() + "-")) {
            throw new IllegalArgumentException(
                    "Khách hàng không khớp với mã phương tiện hiện tại (" + this.maPhuongTien + ")");
        }
        this.khachHang = khachHang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhuongTien that)) return false;
        return Objects.equals(maPhuongTien, that.maPhuongTien);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maPhuongTien);
    }

    @Override
    public String toString() {
        return "PhuongTien{" +
                "maPhuongTien='" + maPhuongTien + '\'' +
                ", bienSoXe='" + bienSoXe + '\'' +
                ", mauXe='" + mauXe + '\'' +
                ", mauSacXe='" + mauSacXe + '\'' +
                ", namSanXuat=" + namSanXuat +
                ", nhienLieu=" + nhienLieu +
                ", hangXe=" + (hangXe != null ? hangXe.getMaHangXe() : null) +
                ", khachHang=" + (khachHang != null ? khachHang.getMaKhachHang() : null) +
                '}';
    }
}
