package entity;

import enums.LoaiGiamGia;

import java.time.LocalDateTime;
import java.util.Objects;

public class MaKhuyenMai {
    private String maKhuyenMai;
    private LoaiGiamGia loaiGiam;
    private double giaTriGiam;
    private double giaTriToiThieuApDung;
    private LocalDateTime ngayBatDauApDung;
    private LocalDateTime ngayHetHan;
    private Integer soLuongTon;
    private boolean trangThai;

    public MaKhuyenMai() {
    }

    public MaKhuyenMai(String maKhuyenMai) {
        setMaKhuyenMai(maKhuyenMai);
    }

    public MaKhuyenMai(String maKhuyenMai, LoaiGiamGia loaiGiam, double giaTriGiam,
                       double giaTriToiThieuApDung, LocalDateTime ngayBatDauApDung,
                       LocalDateTime ngayHetHan, Integer soLuongTon, boolean trangThai) {
        setMaKhuyenMai(maKhuyenMai);
        setLoaiGiam(loaiGiam);
        setGiaTriGiam(giaTriGiam);
        setGiaTriToiThieuApDung(giaTriToiThieuApDung);
        setNgayBatDauApDung(ngayBatDauApDung);
        setNgayHetHan(ngayHetHan);
        setSoLuongTon(soLuongTon);
        setTrangThai(trangThai);
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi (tên mã khuyến mãi) không được để trống");
        }
        this.maKhuyenMai = maKhuyenMai.trim();
    }

    public LoaiGiamGia getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(LoaiGiamGia loaiGiam) {
        if (loaiGiam == null) {
            throw new IllegalArgumentException("Loại giảm giá không được null");
        }
        if (loaiGiam == LoaiGiamGia.PHAN_TRAM && this.giaTriGiam > 100) {
            throw new IllegalArgumentException("Giá trị giảm theo phần trăm không được vượt quá 100%");
        }
        this.loaiGiam = loaiGiam;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(double giaTriGiam) {
        if (giaTriGiam < 0) {
            throw new IllegalArgumentException("Giá trị giảm không được âm");
        }
        if (this.loaiGiam == LoaiGiamGia.PHAN_TRAM && giaTriGiam > 100) {
            throw new IllegalArgumentException("Giá trị giảm theo phần trăm không được vượt quá 100%");
        }
        this.giaTriGiam = giaTriGiam;
    }

    public double getGiaTriToiThieuApDung() {
        return giaTriToiThieuApDung;
    }

    public void setGiaTriToiThieuApDung(double giaTriToiThieuApDung) {
        if (giaTriToiThieuApDung < 0) {
            throw new IllegalArgumentException("Giá trị tối thiểu áp dụng không được âm");
        }
        this.giaTriToiThieuApDung = giaTriToiThieuApDung;
    }

    public LocalDateTime getNgayBatDauApDung() {
        return ngayBatDauApDung;
    }

    public void setNgayBatDauApDung(LocalDateTime ngayBatDauApDung) {
        if (ngayBatDauApDung != null && this.ngayHetHan != null
                && this.ngayHetHan.isBefore(ngayBatDauApDung)) {
            throw new IllegalArgumentException("Ngày bắt đầu áp dụng không được sau ngày hết hạn");
        }
        this.ngayBatDauApDung = ngayBatDauApDung;
    }

    public LocalDateTime getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDateTime ngayHetHan) {
        if (ngayHetHan != null && this.ngayBatDauApDung != null
                && ngayHetHan.isBefore(this.ngayBatDauApDung)) {
            throw new IllegalArgumentException("Ngày hết hạn không được trước ngày bắt đầu áp dụng");
        }
        this.ngayHetHan = ngayHetHan;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        if (soLuongTon != null && soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn mã khuyến mãi không được âm");
        }
        this.soLuongTon = soLuongTon;
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
        if (!(o instanceof MaKhuyenMai that)) return false;
        return Objects.equals(maKhuyenMai, that.maKhuyenMai);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maKhuyenMai);
    }

    @Override
    public String toString() {
        return "MaKhuyenMai{" +
                "maKhuyenMai='" + maKhuyenMai + '\'' +
                ", loaiGiam=" + loaiGiam +
                ", giaTriGiam=" + giaTriGiam +
                ", giaTriToiThieuApDung=" + giaTriToiThieuApDung +
                ", ngayBatDauApDung=" + ngayBatDauApDung +
                ", ngayHetHan=" + ngayHetHan +
                ", soLuongTon=" + soLuongTon +
                ", trangThai=" + trangThai +
                '}';
    }
}
