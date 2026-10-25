package com.pengugarage.model.entity;

import com.pengugarage.model.enums.LoaiHoaDon;
import com.pengugarage.model.enums.PhuongThucThanhToan;
import com.pengugarage.model.enums.TrangThaiThanhToan;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Hóa Đơn (HoaDon) - Dùng chung cho BAN_HANG và DICH_VU
 */
public class HoaDon {
    private int id;
    private String maHoaDon;
    private LoaiHoaDon loaiHoaDon;
    private Integer khachHangId;
    private KhachHang khachHang;
    private Integer phieuTiepNhanId;
    private PhieuTiepNhan phieuTiepNhan;
    private int nhanVienLapId;
    private NhanVien nhanVienLap;
    private Integer maKhuyenMaiId;
    private MaKhuyenMai maKhuyenMai;
    private LocalDateTime thoiGianLap;
    private double tongTienHang;
    private double tienKhuyenMai;
    private double tienThue;
    private double tongThanhToan;
    private TrangThaiThanhToan trangThaiThanhToan;
    private PhuongThucThanhToan phuongThucThanhToan;
    private LocalDateTime thoiGianThanhToan;
    private String tenKhachLe;
    private String sdtKhachLe;
    private String ghiChu;
    private List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

    public HoaDon() {
        this.thoiGianLap = LocalDateTime.now();
        this.trangThaiThanhToan = TrangThaiThanhToan.CHUA_THANH_TOAN;
    }

    public HoaDon(int id, String maHoaDon, LoaiHoaDon loaiHoaDon, Integer khachHangId, 
                  Integer phieuTiepNhanId, int nhanVienLapId, Integer maKhuyenMaiId, 
                  LocalDateTime thoiGianLap, double tongTienHang, double tienKhuyenMai, 
                  double tienThue, double tongThanhToan, TrangThaiThanhToan trangThaiThanhToan, 
                  PhuongThucThanhToan phuongThucThanhToan, LocalDateTime thoiGianThanhToan, 
                  String tenKhachLe, String sdtKhachLe, String ghiChu) {
        this.id = id;
        this.maHoaDon = maHoaDon;
        this.loaiHoaDon = loaiHoaDon;
        this.khachHangId = khachHangId;
        this.phieuTiepNhanId = phieuTiepNhanId;
        this.nhanVienLapId = nhanVienLapId;
        this.maKhuyenMaiId = maKhuyenMaiId;
        this.thoiGianLap = thoiGianLap;
        this.tongTienHang = tongTienHang;
        this.tienKhuyenMai = tienKhuyenMai;
        this.tienThue = tienThue;
        this.tongThanhToan = tongThanhToan;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.thoiGianThanhToan = thoiGianThanhToan;
        this.tenKhachLe = tenKhachLe;
        this.sdtKhachLe = sdtKhachLe;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LoaiHoaDon getLoaiHoaDon() {
        return loaiHoaDon;
    }

    public void setLoaiHoaDon(LoaiHoaDon loaiHoaDon) {
        this.loaiHoaDon = loaiHoaDon;
    }

    public Integer getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(Integer khachHangId) {
        this.khachHangId = khachHangId;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
        if (khachHang != null) {
            this.khachHangId = khachHang.getId();
        }
    }

    public Integer getPhieuTiepNhanId() {
        return phieuTiepNhanId;
    }

    public void setPhieuTiepNhanId(Integer phieuTiepNhanId) {
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

    public Integer getMaKhuyenMaiId() {
        return maKhuyenMaiId;
    }

    public void setMaKhuyenMaiId(Integer maKhuyenMaiId) {
        this.maKhuyenMaiId = maKhuyenMaiId;
    }

    public MaKhuyenMai getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(MaKhuyenMai maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
        if (maKhuyenMai != null) {
            this.maKhuyenMaiId = maKhuyenMai.getId();
        }
    }

    public LocalDateTime getThoiGianLap() {
        return thoiGianLap;
    }

    public void setThoiGianLap(LocalDateTime thoiGianLap) {
        this.thoiGianLap = thoiGianLap;
    }

    public double getTongTienHang() {
        return tongTienHang;
    }

    public void setTongTienHang(double tongTienHang) {
        this.tongTienHang = tongTienHang;
    }

    public double getTienKhuyenMai() {
        return tienKhuyenMai;
    }

    public void setTienKhuyenMai(double tienKhuyenMai) {
        this.tienKhuyenMai = tienKhuyenMai;
    }

    public double getTienThue() {
        return tienThue;
    }

    public void setTienThue(double tienThue) {
        this.tienThue = tienThue;
    }

    public double getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(double tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public TrangThaiThanhToan getTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(TrangThaiThanhToan trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public String getTenKhachLe() {
        return tenKhachLe;
    }

    public void setTenKhachLe(String tenKhachLe) {
        this.tenKhachLe = tenKhachLe;
    }

    public String getSdtKhachLe() {
        return sdtKhachLe;
    }

    public void setSdtKhachLe(String sdtKhachLe) {
        this.sdtKhachLe = sdtKhachLe;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public double tinhTongTien() {
        double tongHang = 0.0;
        double thue = 0.0;
        if (danhSachChiTiet != null) {
            for (ChiTietHoaDon ct : danhSachChiTiet) {
                tongHang += ct.tinhThanhTien();
                thue += ct.tinhThue();
            }
        }
        this.tongTienHang = tongHang;
        this.tienThue = thue;
        this.tienKhuyenMai = tinhTienKhuyenMai();
        this.tongThanhToan = Math.max(0.0, this.tongTienHang + this.tienThue - this.tienKhuyenMai);
        return this.tongThanhToan;
    }

    public double tinhThue() {
        double thue = 0.0;
        if (danhSachChiTiet != null) {
            for (ChiTietHoaDon ct : danhSachChiTiet) {
                thue += ct.tinhThue();
            }
        }
        this.tienThue = thue;
        return thue;
    }

    public double tinhTienKhuyenMai() {
        if (maKhuyenMai == null || !maKhuyenMai.isTrangThai()) {
            return 0.0;
        }
        if (this.tongTienHang < maKhuyenMai.getGiaTriToiThieuApDung()) {
            return 0.0;
        }
        double discount = 0.0;
        switch (maKhuyenMai.getLoaiGiam()) {
            case PHAN_TRAM:
                discount = (this.tongTienHang * maKhuyenMai.getGiaTriGiam()) / 100.0;
                break;
            case SO_TIEN:
                discount = maKhuyenMai.getGiaTriGiam();
                break;
        }
        this.tienKhuyenMai = Math.min(discount, this.tongTienHang);
        return this.tienKhuyenMai;
    }

    @Override
    public String toString() {
        return maHoaDon + " (" + loaiHoaDon + ": " + tongThanhToan + " đ)";
    }
}
