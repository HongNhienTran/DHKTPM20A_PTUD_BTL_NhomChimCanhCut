package enums;

public enum TrangThaiSua {
    KIEM_TRA("Kiểm tra"),
    CHO_THUC_HIEN("Chờ thực hiện"),
    DANG_SUA("Đang sửa"),
    HOAN_THANH("Hoàn thành");

    private final String tenHienThi;

    TrangThaiSua(String tenHienThi) {
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

