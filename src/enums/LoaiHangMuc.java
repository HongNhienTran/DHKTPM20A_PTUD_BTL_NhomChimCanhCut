package enums;

public enum LoaiHangMuc {
    DICH_VU("Dịch vụ"),
    SAN_PHAM("Sản phẩm");

    private final String tenHienThi;

    LoaiHangMuc(String tenHienThi) {
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

