package enums;

public enum TrangThaiPhieuTiepNhan {
    DANG_KIEM_TRA("Đang kiểm tra"),
    CHO_XAC_NHAN_BAO_GIA("Chờ xác nhận báo giá"),
    DANG_SUA_CHUA("Đang sửa chữa"),
    HOAN_THANH("Hoàn thành"),
    THANH_TOAN("Đã thanh toán"),
    DA_HUY("Đã hủy");

    private final String tenHienThi;

    TrangThaiPhieuTiepNhan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    @Override
    public String toString() {
        return tenHienThi;
    }
}
