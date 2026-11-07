package entity;

import enums.ChucVu;
import enums.ChuyenMon;

import java.util.Objects;

public class NhanVien {
    public static final String REGEX_MA_NHAN_VIEN = "NV(_[A-Z]{2,3}_)?\\d{3}";

    private String maNhanVien;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;
    private ChucVu chucVu;
    private boolean trangThai;
    private ChuyenMon chuyenMon;

    private TaiKhoan taiKhoan;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien) {
        setMaNhanVien(maNhanVien);
    }

    public NhanVien(String maNhanVien, String hoTen, String soDienThoai, String diaChi,
                    ChucVu chucVu, boolean trangThai, ChuyenMon chuyenMon) {
        setMaNhanVien(maNhanVien);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setChucVu(chucVu);
        setTrangThai(trangThai);
        setChuyenMon(chuyenMon);
    }

    public NhanVien(String maNhanVien, String hoTen, String soDienThoai, String diaChi,
                    ChucVu chucVu, boolean trangThai, ChuyenMon chuyenMon, TaiKhoan taiKhoan) {
        this(maNhanVien, hoTen, soDienThoai, diaChi, chucVu, trangThai, chuyenMon);
        setTaiKhoan(taiKhoan);
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || !maNhanVien.matches("^" + REGEX_MA_NHAN_VIEN + "$")) {
            throw new IllegalArgumentException(
                    "Mã nhân viên không hợp lệ (định dạng: NVxxx, chạy từ NV000 đến NV999)");
        }
        if (this.taiKhoan != null && this.taiKhoan.getTenDangNhap() != null
                && !maNhanVien.equals(this.taiKhoan.getTenDangNhap())) {
            throw new IllegalArgumentException("Mã nhân viên phải trùng với tên đăng nhập của tài khoản");
        }
        this.maNhanVien = maNhanVien;
        if (this.taiKhoan != null && this.taiKhoan.getTenDangNhap() == null) {
            this.taiKhoan.setTenDangNhap(maNhanVien);
        }
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên nhân viên không được để trống");
        }
        this.hoTen = hoTen.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        this.soDienThoai = soDienThoai.trim();
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        if (chucVu == null) {
            throw new IllegalArgumentException("Chức vụ nhân viên không được null");
        }
        this.chucVu = chucVu;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
        if (!trangThai && this.taiKhoan != null) {
            this.taiKhoan.setTrangThai(false);
        }
    }

    public ChuyenMon getChuyenMon() {
        return chuyenMon;
    }

    public void setChuyenMon(ChuyenMon chuyenMon) {
        this.chuyenMon = chuyenMon;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        if (taiKhoan != null && this.maNhanVien != null && taiKhoan.getTenDangNhap() != null
                && !this.maNhanVien.equals(taiKhoan.getTenDangNhap())) {
            throw new IllegalArgumentException("Tên đăng nhập của tài khoản phải trùng với mã nhân viên");
        }
        this.taiKhoan = taiKhoan;
        if (taiKhoan != null && taiKhoan.getMaNhanVien() != this) {
            taiKhoan.setMaNhanVien(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhanVien nhanVien)) return false;
        return Objects.equals(maNhanVien, nhanVien.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maNhanVien + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", chucVu=" + chucVu +
                ", trangThai=" + trangThai +
                ", chuyenMon=" + chuyenMon +
                '}';
    }
}
