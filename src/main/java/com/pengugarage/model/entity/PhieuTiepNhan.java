package com.pengugarage.model.entity;

import com.pengugarage.model.enums.TrangThaiPhieuTiepNhan;
import java.time.LocalDateTime;

/**
 * Thực thể Phiếu Tiếp Nhận (PhieuTiepNhan)
 */
public class PhieuTiepNhan {
    private int id;
    private String maTiepNhan;
    private int phuongTienId;
    private PhuongTien phuongTien;
    private int nhanVienTiepNhanId;
    private NhanVien nhanVienTiepNhan;
    private LocalDateTime thoiGianTiepNhan;
    private TrangThaiPhieuTiepNhan trangThai;
    private double soKMHienTai;
    private String yeuCauKhachHang;
    private String tinhTrangNoiThat;
    private String tinhTrangNgoaiThat;
    private int soChiaKhoa;
    private String ghiChu;

    public PhieuTiepNhan() {
        this.thoiGianTiepNhan = LocalDateTime.now();
        this.trangThai = TrangThaiPhieuTiepNhan.CHO_TIEP_NHAN;
        this.soChiaKhoa = 1;
    }

    public PhieuTiepNhan(int id, String maTiepNhan, int phuongTienId, int nhanVienTiepNhanId, 
                        LocalDateTime thoiGianTiepNhan, TrangThaiPhieuTiepNhan trangThai, 
                        double soKMHienTai, String yeuCauKhachHang, String tinhTrangNoiThat, 
                        String tinhTrangNgoaiThat, int soChiaKhoa, String ghiChu) {
        this.id = id;
        this.maTiepNhan = maTiepNhan;
        this.phuongTienId = phuongTienId;
        this.nhanVienTiepNhanId = nhanVienTiepNhanId;
        this.thoiGianTiepNhan = thoiGianTiepNhan;
        this.trangThai = trangThai;
        this.soKMHienTai = soKMHienTai;
        this.yeuCauKhachHang = yeuCauKhachHang;
        this.tinhTrangNoiThat = tinhTrangNoiThat;
        this.tinhTrangNgoaiThat = tinhTrangNgoaiThat;
        this.soChiaKhoa = soChiaKhoa;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaTiepNhan() {
        return maTiepNhan;
    }

    public void setMaTiepNhan(String maTiepNhan) {
        this.maTiepNhan = maTiepNhan;
    }

    public int getPhuongTienId() {
        return phuongTienId;
    }

    public void setPhuongTienId(int phuongTienId) {
        this.phuongTienId = phuongTienId;
    }

    public PhuongTien getPhuongTien() {
        return phuongTien;
    }

    public void setPhuongTien(PhuongTien phuongTien) {
        this.phuongTien = phuongTien;
        if (phuongTien != null) {
            this.phuongTienId = phuongTien.getId();
        }
    }

    public int getNhanVienTiepNhanId() {
        return nhanVienTiepNhanId;
    }

    public void setNhanVienTiepNhanId(int nhanVienTiepNhanId) {
        this.nhanVienTiepNhanId = nhanVienTiepNhanId;
    }

    public NhanVien getNhanVienTiepNhan() {
        return nhanVienTiepNhan;
    }

    public void setNhanVienTiepNhan(NhanVien nhanVienTiepNhan) {
        this.nhanVienTiepNhan = nhanVienTiepNhan;
        if (nhanVienTiepNhan != null) {
            this.nhanVienTiepNhanId = nhanVienTiepNhan.getId();
        }
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
        this.soKMHienTai = soKMHienTai;
    }

    public String getYeuCauKhachHang() {
        return yeuCauKhachHang;
    }

    public void setYeuCauKhachHang(String yeuCauKhachHang) {
        this.yeuCauKhachHang = yeuCauKhachHang;
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
        this.soChiaKhoa = soChiaKhoa;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return maTiepNhan + " [" + trangThai + "]";
    }
}
