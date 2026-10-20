package com.pengugarage.model.entity;

import com.pengugarage.model.enums.TrangThaiBaoGia;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Phiếu Báo Giá (PhieuBaoGia)
 */
public class PhieuBaoGia {
    private int id;
    private String maBaoGia;
    private int phieuTiepNhanId;
    private PhieuTiepNhan phieuTiepNhan;
    private int nhanVienLapId;
    private NhanVien nhanVienLap;
    private LocalDateTime thoiGianLap;
    private int phienBan;
    private double tongTienDuKien;
    private TrangThaiBaoGia trangThai;
    private LocalDateTime ngayHetHieuLuc;
    private LocalDateTime thoiGianXacNhan;
    private String lyDoTuChoi;
    private String ghiChu;
    private List<ChiTietBaoGia> danhSachChiTiet = new ArrayList<>();

    public PhieuBaoGia() {
        this.thoiGianLap = LocalDateTime.now();
        this.phienBan = 1;
        this.trangThai = TrangThaiBaoGia.NHAP;
    }

    public PhieuBaoGia(int id, String maBaoGia, int phieuTiepNhanId, int nhanVienLapId, 
                       LocalDateTime thoiGianLap, int phienBan, double tongTienDuKien, 
                       TrangThaiBaoGia trangThai, LocalDateTime ngayHetHieuLuc, 
                       LocalDateTime thoiGianXacNhan, String lyDoTuChoi, String ghiChu) {
        this.id = id;
        this.maBaoGia = maBaoGia;
        this.phieuTiepNhanId = phieuTiepNhanId;
        this.nhanVienLapId = nhanVienLapId;
        this.thoiGianLap = thoiGianLap;
        this.phienBan = phienBan;
        this.tongTienDuKien = tongTienDuKien;
        this.trangThai = trangThai;
        this.ngayHetHieuLuc = ngayHetHieuLuc;
        this.thoiGianXacNhan = thoiGianXacNhan;
        this.lyDoTuChoi = lyDoTuChoi;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaBaoGia() {
        return maBaoGia;
    }

    public void setMaBaoGia(String maBaoGia) {
        this.maBaoGia = maBaoGia;
    }

    public int getPhieuTiepNhanId() {
        return phieuTiepNhanId;
    }

    public void setPhieuTiepNhanId(int phieuTiepNhanId) {
        this.phieuTiepNhanId = phieuTiepNhanId;
    }

    public PhieuTiepNhan getPhieuTiepNhan() {
        return phieuTiepNhan;
    }

    public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) {
        this.phieuTiepNhan = phieuTiepNhan;
        if (phieuTiepNhan != null) {
            this.phieuTiepNhanId = phieuTiepNhan.getId();
        }
    }

    public int getNhanVienLapId() {
        return nhanVienLapId;
    }

    public void setNhanVienLapId(int nhanVienLapId) {
        this.nhanVienLapId = nhanVienLapId;
    }

    public NhanVien getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(NhanVien nhanVienLap) {
        this.nhanVienLap = nhanVienLap;
        if (nhanVienLap != null) {
            this.nhanVienLapId = nhanVienLap.getId();
        }
    }

    public LocalDateTime getThoiGianLap() {
        return thoiGianLap;
    }

    public void setThoiGianLap(LocalDateTime thoiGianLap) {
        this.thoiGianLap = thoiGianLap;
    }

    public int getPhienBan() {
        return phienBan;
    }

    public void setPhienBan(int phienBan) {
        this.phienBan = phienBan;
    }

    public double getTongTienDuKien() {
        return tongTienDuKien;
    }

    public void setTongTienDuKien(double tongTienDuKien) {
        this.tongTienDuKien = tongTienDuKien;
    }

    public TrangThaiBaoGia getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiBaoGia trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayHetHieuLuc() {
        return ngayHetHieuLuc;
    }

    public void setNgayHetHieuLuc(LocalDateTime ngayHetHieuLuc) {
        this.ngayHetHieuLuc = ngayHetHieuLuc;
    }

    public LocalDateTime getThoiGianXacNhan() {
        return thoiGianXacNhan;
    }

    public void setThoiGianXacNhan(LocalDateTime thoiGianXacNhan) {
        this.thoiGianXacNhan = thoiGianXacNhan;
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<ChiTietBaoGia> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietBaoGia> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public double tinhTongTienDuKien() {
        double total = 0.0;
        if (danhSachChiTiet != null) {
            for (ChiTietBaoGia ct : danhSachChiTiet) {
                total += ct.tinhThanhTien();
            }
        }
        this.tongTienDuKien = total;
        return total;
    }

    public void xacNhan() {
        this.trangThai = TrangThaiBaoGia.DA_CHAP_THUAN;
        this.thoiGianXacNhan = LocalDateTime.now();
    }

    public void tuChoi(String lyDo) {
        this.trangThai = TrangThaiBaoGia.TU_CHOI;
        this.lyDoTuChoi = lyDo;
    }

    @Override
    public String toString() {
        return maBaoGia + " - v" + phienBan + " [" + trangThai + "]";
    }
}
