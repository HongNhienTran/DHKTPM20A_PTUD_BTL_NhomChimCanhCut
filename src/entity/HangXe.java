package entity;

import java.util.Objects;

public class HangXe {
    public static final String REGEX_MA_HANG_XE = "HX(?!000)\\d{3}";

    private String maHangXe;
    private String tenHang;
    private String quocGia;

    public HangXe() {
    }

    public HangXe(String maHangXe) {
        setMaHangXe(maHangXe);
    }

    public HangXe(String maHangXe, String tenHang, String quocGia) {
        setMaHangXe(maHangXe);
        setTenHang(tenHang);
        setQuocGia(quocGia);
    }

    public String getMaHangXe() {
        return maHangXe;
    }

    public void setMaHangXe(String maHangXe) {
        if (maHangXe == null || !maHangXe.matches("^" + REGEX_MA_HANG_XE + "$")) {
            throw new IllegalArgumentException(
                    "Mã hãng xe không hợp lệ (định dạng: HXxxx, chạy từ HX001 đến HX999)");
        }
        this.maHangXe = maHangXe;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        if (tenHang == null || tenHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên hãng xe không được để trống");
        }
        this.tenHang = tenHang.trim();
    }

    public String getQuocGia() {
        return quocGia;
    }

    public void setQuocGia(String quocGia) {
        if (quocGia == null || quocGia.trim().isEmpty()) {
            throw new IllegalArgumentException("Quốc gia không được để trống");
        }
        this.quocGia = quocGia.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HangXe hangXe)) return false;
        return Objects.equals(maHangXe, hangXe.maHangXe);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maHangXe);
    }

    @Override
    public String toString() {
        return "HangXe{" +
                "maHangXe='" + maHangXe + '\'' +
                ", tenHang='" + tenHang + '\'' +
                ", quocGia='" + quocGia + '\'' +
                '}';
    }
}
