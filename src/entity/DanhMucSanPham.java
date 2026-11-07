package entity;

import java.util.Objects;

public class DanhMucSanPham {
    public static final String REGEX_MA_DANH_MUC = "DM(?!000)\\d{3}";

    private String maDanhMuc;
    private String tenDanhMuc;
    private String moTa;
    private boolean trangThai;

    public DanhMucSanPham() {
    }

    public DanhMucSanPham(String maDanhMuc) {
        setMaDanhMuc(maDanhMuc);
    }

    public DanhMucSanPham(String maDanhMuc, String tenDanhMuc, String moTa, boolean trangThai) {
        setMaDanhMuc(maDanhMuc);
        setTenDanhMuc(tenDanhMuc);
        setMoTa(moTa);
        setTrangThai(trangThai);
    }

    public String getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(String maDanhMuc) {
        if (maDanhMuc == null || !maDanhMuc.matches("^" + REGEX_MA_DANH_MUC + "$")) {
            throw new IllegalArgumentException(
                    "Mã danh mục không hợp lệ (định dạng: DMxxx, chạy từ DM001 đến DM999)");
        }
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        this.tenDanhMuc = tenDanhMuc.trim();
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DanhMucSanPham that)) return false;
        return Objects.equals(maDanhMuc, that.maDanhMuc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maDanhMuc);
    }

    @Override
    public String toString() {
        return "DanhMucSanPham{" +
                "maDanhMuc='" + maDanhMuc + '\'' +
                ", tenDanhMuc='" + tenDanhMuc + '\'' +
                ", moTa='" + moTa + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }
}
