package ui;

import java.time.LocalDate;
import java.util.List;

public class QuanLyManHinhBaoCaoNhanVien extends QuanLyManHinhBaoCaoChiTiet {
    public QuanLyManHinhBaoCaoNhanVien() {
        super("Báo cáo nhân viên kỹ thuật", "Mã nhân viên", "Nhân viên",
                "Được phân công", "Hoàn thành", "Tỷ lệ hoàn thành");
    }

    @Override
    protected List<Object[]> layDuLieu(LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoThongKeBUS.layBaoCaoNhanVien(tuNgay, denNgay);
    }
}
