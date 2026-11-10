package entity;

import enums.ChucVu;

import java.util.Objects;

public class TaiKhoan {
    public static final String REGEX_TEN_DANG_NHAP = NhanVien.REGEX_MA_NHAN_VIEN;

    private String tenDangNhap;
    private String matKhauMaHoa;
    private boolean trangThai;
    private NhanVien maNhanVien;

    public TaiKhoan() {
    }

    public TaiKhoan(String tenDangNhap) {
        setTenDangNhap(tenDangNhap);
    }

    public TaiKhoan(String matKhauMaHoa, boolean trangThai, NhanVien maNhanVien) {
        setMaNhanVien(maNhanVien);
        if (maNhanVien != null && maNhanVien.getMaNhanVien() != null) {
            setTenDangNhap(maNhanVien.getMaNhanVien());
        }
        setMatKhauMaHoa(matKhauMaHoa);
        setTrangThai(trangThai);
    }

    public TaiKhoan(String tenDangNhap, String matKhauMaHoa, boolean trangThai, NhanVien maNhanVien) {
        setMaNhanVien(maNhanVien);
        setTenDangNhap(tenDangNhap);
        setMatKhauMaHoa(matKhauMaHoa);
        setTrangThai(trangThai);
    }

    public ChucVu getQuyen() {
        return maNhanVien != null ? maNhanVien.getChucVu() : null;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || !tenDangNhap.matches("^" + REGEX_TEN_DANG_NHAP + "$")) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập phải là mã nhân viên hợp lệ (định dạng: NVxxx, từ NV000 đến NV999)");
        }
        if (this.maNhanVien != null && this.maNhanVien.getMaNhanVien() != null
                && !tenDangNhap.equals(this.maNhanVien.getMaNhanVien())) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập phải trùng khớp với mã nhân viên (" + this.maNhanVien.getMaNhanVien() + ")");
        }
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauMaHoa() {
        return matKhauMaHoa;
    }

    public void setMatKhauMaHoa(String matKhauMaHoa) {
        if (matKhauMaHoa == null || matKhauMaHoa.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mã hóa không được để trống");
        }
        this.matKhauMaHoa = matKhauMaHoa.trim();
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        if (trangThai && this.maNhanVien != null && !this.maNhanVien.isTrangThai()) {
            throw new IllegalStateException("Không thể kích hoạt tài khoản của nhân viên đã ngừng làm việc");
        }
        this.trangThai = trangThai;
    }

    public NhanVien getMaNhanVien() {
        return maNhanVien;
    }

    public NhanVien getNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(NhanVien maNhanVien) {
        if (maNhanVien != null && maNhanVien.getMaNhanVien() != null
                && this.tenDangNhap != null
                && !this.tenDangNhap.equals(maNhanVien.getMaNhanVien())) {
            throw new IllegalArgumentException(
                    "Nhân viên được gán phải có mã trùng với tên đăng nhập (" + this.tenDangNhap + ")");
        }
        this.maNhanVien = maNhanVien;
        if (this.tenDangNhap == null && maNhanVien != null && maNhanVien.getMaNhanVien() != null) {
            this.tenDangNhap = maNhanVien.getMaNhanVien();
        }
        if (maNhanVien != null && maNhanVien.getTaiKhoan() != this) {
            maNhanVien.setTaiKhoan(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiKhoan taiKhoan)) return false;
        return Objects.equals(tenDangNhap, taiKhoan.tenDangNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tenDangNhap);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "tenDangNhap='" + tenDangNhap + '\'' +
                ", quyen=" + getQuyen() +
                ", trangThai=" + trangThai +
                ", maNhanVien=" + (maNhanVien != null ? maNhanVien.getMaNhanVien() : null) +
                '}';
    }
}
