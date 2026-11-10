package ui;

import java.time.LocalDate;
import java.util.List;

public class QuanLyManHinhBaoCaoDichVu extends QuanLyManHinhBaoCaoChiTiet {
    public QuanLyManHinhBaoCaoDichVu() {
        super("Báo cáo dịch vụ sửa chữa", "Mã dịch vụ", "Dịch vụ", "Số lượt", "Doanh thu");
    }

    @Override
    protected List<Object[]> layDuLieu(LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoThongKeBUS.layBaoCaoDichVu(tuNgay, denNgay);
    }
}
