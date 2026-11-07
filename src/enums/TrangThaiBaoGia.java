package enums;

public enum TrangThaiBaoGia {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DA_CHAP_THUAN("Đã chấp thuận"),
    TU_CHOI("Từ chối");

    private final String tenHienThi;

    TrangThaiBaoGia(String tenHienThi) {
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
