package enums;

public enum LoaiGiamGia {
    PHAN_TRAM("Phần trăm"),
    SO_TIEN("Số tiền");

    private final String tenHienThi;

    LoaiGiamGia(String tenHienThi) {
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

