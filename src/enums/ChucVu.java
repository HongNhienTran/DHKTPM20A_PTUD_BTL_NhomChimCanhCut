package enums;

public enum ChucVu {
    QUAN_LY("Quản lý"),
    LE_TAN("Lễ tân"),
    KY_THUAT_VIEN("Kỹ thuật viên");

    private final String tenHienThi;

    ChucVu(String tenHienThi) {
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

