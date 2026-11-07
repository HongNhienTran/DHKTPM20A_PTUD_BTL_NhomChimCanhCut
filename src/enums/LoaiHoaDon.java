package enums;

public enum LoaiHoaDon {
    BAN_HANG("Bán hàng"),
    DICH_VU("Dịch vụ");

    private final String tenHienThi;

    LoaiHoaDon(String tenHienThi) {
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

