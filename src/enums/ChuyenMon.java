package enums;

public enum ChuyenMon {
    CHUYEN_BAO_DUONG("Chuyên bảo dưỡng"),
    CHUYEN_SUA_HE_THONG_DIEN("Chuyên sửa hệ thống điện"),
    CHUYEN_THAY_THE_LINH_KIEN("Chuyên thay thế linh kiện"),
    CHUYEN_CHAM_SOC_VE_SINH_RUA_XE("Chuyên chăm sóc, vệ sinh, rửa xe");

    private final String tenHienThi;

    ChuyenMon(String tenHienThi) {
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

