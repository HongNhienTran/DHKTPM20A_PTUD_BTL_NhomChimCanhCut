package entity;

import java.util.Objects;

public class SanPham {
    public static final String REGEX_MA_SAN_PHAM =
            "SP-" + DanhMucSanPham.REGEX_MA_DANH_MUC + "-(?!000)\\d{3}";

    private String maSanPham;
    private String tenSanPham;
    private String donViTinh;
    private int soLuongTon;
    private int soLuongTonToiThieu;
    private double giaNhap;
    private double donGiaBan;
    private double thueVAT;
    private boolean trangThai;
    private String anhSanPham;

    private DanhMucSanPham danhMucSanPham;

    public SanPham() {
    }

    public SanPham(String maSanPham) {
        setMaSanPham(maSanPham);
    }

    public SanPham(String maSanPham, String tenSanPham, String donViTinh, int soLuongTon,
                   int soLuongTonToiThieu, double giaNhap, double donGiaBan, double thueVAT,
                   boolean trangThai, String anhSanPham, DanhMucSanPham danhMucSanPham) {
        setDanhMucSanPham(danhMucSanPham);
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setDonViTinh(donViTinh);
        setSoLuongTon(soLuongTon);
        setSoLuongTonToiThieu(soLuongTonToiThieu);
        setGiaNhap(giaNhap);
        setDonGiaBan(donGiaBan);
        setThueVAT(thueVAT);
        setTrangThai(trangThai);
        setAnhSanPham(anhSanPham);
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null || !maSanPham.matches("^" + REGEX_MA_SAN_PHAM + "$")) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không hợp lệ (định dạng: SP-{mã danh mục}-{xxx}, 3 số cuối từ 001 đến 999)");
        }
        if (this.danhMucSanPham != null && this.danhMucSanPham.getMaDanhMuc() != null
                && !maSanPham.startsWith("SP-" + this.danhMucSanPham.getMaDanhMuc() + "-")) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm phải chứa đúng mã danh mục (" + this.danhMucSanPham.getMaDanhMuc() + ")");
        }
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        this.tenSanPham = tenSanPham.trim();
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        if (donViTinh == null || donViTinh.trim().isEmpty()) {
            throw new IllegalArgumentException("Đơn vị tính không được để trống");
        }
        this.donViTinh = donViTinh.trim();
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm");
        } else if(soLuongTon < this.soLuongTonToiThieu) {
            throw new IllegalArgumentException("Số lượng tồn không được nhỏ hơn số lượng tối thiểu");
        }
        this.soLuongTon = soLuongTon;
    }
    public int getSoLuongTonToiThieu() {
        return soLuongTonToiThieu;
    }

    public void setSoLuongTonToiThieu(int soLuongTonToiThieu) {
        if (soLuongTonToiThieu < 0) {
            throw new IllegalArgumentException("Số lượng tồn tối thiểu không được âm");
        }
        this.soLuongTonToiThieu = soLuongTonToiThieu;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap < 0) {
            throw new IllegalArgumentException("Giá nhập không được âm");
        }
        this.giaNhap = giaNhap;
    }

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        if (donGiaBan < 0) {
            throw new IllegalArgumentException("Đơn giá bán không được âm");
        }
        this.donGiaBan = donGiaBan;
    }

    public double getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(double thueVAT) {
        if (thueVAT < 0 || thueVAT > 100) {
            throw new IllegalArgumentException("Thuế VAT phải nằm trong khoảng 0 đến 100");
        }
        this.thueVAT = thueVAT;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }

    public DanhMucSanPham getDanhMucSanPham() {
        return danhMucSanPham;
    }

    public void setDanhMucSanPham(DanhMucSanPham danhMucSanPham) {
        if (danhMucSanPham != null && danhMucSanPham.getMaDanhMuc() != null
                && this.maSanPham != null
                && !this.maSanPham.startsWith("SP-" + danhMucSanPham.getMaDanhMuc() + "-")) {
            throw new IllegalArgumentException(
                    "Danh mục sản phẩm không khớp với mã sản phẩm hiện tại (" + this.maSanPham + ")");
        }
        this.danhMucSanPham = danhMucSanPham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanPham sanPham)) return false;
        return Objects.equals(maSanPham, sanPham.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maSanPham);
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSanPham='" + maSanPham + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", donViTinh='" + donViTinh + '\'' +
                ", soLuongTon=" + soLuongTon +
                ", donGiaBan=" + donGiaBan +
                ", thueVAT=" + thueVAT +
                ", trangThai=" + trangThai +
                ", danhMuc=" + (danhMucSanPham != null ? danhMucSanPham.getMaDanhMuc() : null) +
                '}';
    }
}
