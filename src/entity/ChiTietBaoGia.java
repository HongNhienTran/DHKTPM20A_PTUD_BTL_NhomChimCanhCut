package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChiTietBaoGia {
    private double donGiaDichVu;
    private boolean duocChapNhan;
    private String ghiChu;

    private PhieuBaoGia phieuBaoGia;
    private DichVu dichVu;
    private List<ChiTietDungPhuTung> danhSachPhuTung = new ArrayList<>();

    public ChiTietBaoGia() {
    }

    public ChiTietBaoGia(double donGiaDichVu, boolean duocChapNhan, String ghiChu,
                         PhieuBaoGia phieuBaoGia, DichVu dichVu) {
        setDonGiaDichVu(donGiaDichVu);
        setDuocChapNhan(duocChapNhan);
        setGhiChu(ghiChu);
        setPhieuBaoGia(phieuBaoGia);
        setDichVu(dichVu);
    }

    public double tinhTienPhuTung() {
        double tongPhuTung = 0;
        for (ChiTietDungPhuTung pt : danhSachPhuTung) {
            tongPhuTung += pt.tinhThanhTien();
        }
        return tongPhuTung;
    }

    public double tinhThanhTien() {
        return donGiaDichVu + tinhTienPhuTung();
    }

    public void truKhoPhuTung() {
        for (ChiTietDungPhuTung pt : danhSachPhuTung) {
            pt.truKho();
        }
    }

    public void hoanKhoPhuTung() {
        for (ChiTietDungPhuTung pt : danhSachPhuTung) {
            pt.hoanKho();
        }
    }

    public double getDonGiaDichVu() {
        return donGiaDichVu;
    }

    public void setDonGiaDichVu(double donGiaDichVu) {
        if (donGiaDichVu < 0) {
            throw new IllegalArgumentException("Đơn giá dịch vụ trong báo giá không được âm");
        }
        this.donGiaDichVu = donGiaDichVu;
    }

    public boolean isDuocChapNhan() {
        return duocChapNhan;
    }

    public void setDuocChapNhan(boolean duocChapNhan) {
        this.duocChapNhan = duocChapNhan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public PhieuBaoGia getPhieuBaoGia() {
        return phieuBaoGia;
    }

    public void setPhieuBaoGia(PhieuBaoGia phieuBaoGia) {
        this.phieuBaoGia = phieuBaoGia;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        if (dichVu == null) {
            throw new IllegalArgumentException("Dịch vụ trong chi tiết báo giá không được null");
        }
        this.dichVu = dichVu;
    }

    public List<ChiTietDungPhuTung> getDanhSachPhuTung() {
        return danhSachPhuTung;
    }

    public void setDanhSachPhuTung(List<ChiTietDungPhuTung> danhSachPhuTung) {
        this.danhSachPhuTung = danhSachPhuTung != null ? danhSachPhuTung : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietBaoGia that)) return false;
        return Objects.equals(phieuBaoGia, that.phieuBaoGia) &&
                Objects.equals(dichVu, that.dichVu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuBaoGia, dichVu);
    }

    @Override
    public String toString() {
        return "ChiTietBaoGia{" +
                "phieuBaoGia=" + (phieuBaoGia != null ? phieuBaoGia.getMaBaoGia() : null) +
                ", dichVu=" + (dichVu != null ? dichVu.getMaDichVu() : null) +
                ", donGiaDichVu=" + donGiaDichVu +
                ", soLoaiPhuTung=" + danhSachPhuTung.size() +
                ", thanhTien=" + tinhThanhTien() +
                ", duocChapNhan=" + duocChapNhan +
                '}';
    }
}
