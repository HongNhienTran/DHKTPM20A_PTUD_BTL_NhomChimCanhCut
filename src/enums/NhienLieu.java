package enums;

public enum NhienLieu {
    XANG("Xăng"),
    DAU("Dầu"),
    DIEN("Điện"),
    HYBRID("Hybrid");

    private final String tenHienThi;

    NhienLieu(String tenHienThi) {
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

