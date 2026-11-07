package entity;

import java.util.Objects;

public class DichVu {
    public static final String REGEX_MA_DICH_VU = "DV(?!000)\\d{3}";

    private String maDichVu;
    private String tenDichVu;
    private String moTa;
    private double donGia;
    private double thueVAT;
    private boolean trangThai;

    public DichVu() {
    }

    public DichVu(String maDichVu) {
        setMaDichVu(maDichVu);
    }

    public DichVu(String maDichVu, String tenDichVu, String moTa, double donGia, double thueVAT, boolean trangThai) {
        setMaDichVu(maDichVu);
        setTenDichVu(tenDichVu);
        setMoTa(moTa);
        setDonGia(donGia);
        setThueVAT(thueVAT);
        setTrangThai(trangThai);
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        if (maDichVu == null || !maDichVu.matches("^" + REGEX_MA_DICH_VU + "$")) {
            throw new IllegalArgumentException(
                    "Mã dịch vụ không hợp lệ (định dạng: DVxxx, chạy từ DV001 đến DV999)");
        }
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống");
        }
        this.tenDichVu = tenDichVu.trim();
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia < 0) {
            throw new IllegalArgumentException("Đơn giá dịch vụ không được âm");
        }
        this.donGia = donGia;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DichVu dichVu)) return false;
        return Objects.equals(maDichVu, dichVu.maDichVu);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maDichVu);
    }

    @Override
    public String toString() {
        return "DichVu{" +
                "maDichVu='" + maDichVu + '\'' +
                ", tenDichVu='" + tenDichVu + '\'' +
                ", moTa='" + moTa + '\'' +
                ", donGia=" + donGia +
                ", thueVAT=" + thueVAT +
                ", trangThai=" + trangThai +
                '}';
    }
}
