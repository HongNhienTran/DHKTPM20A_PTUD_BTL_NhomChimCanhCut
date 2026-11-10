package ui;

import java.time.LocalDate;
import java.util.List;

public class QuanLyManHinhBaoCaoKho extends QuanLyManHinhBaoCaoChiTiet {
    public QuanLyManHinhBaoCaoKho() {
        super("Báo cáo kho", "Mã sản phẩm", "Sản phẩm", "Tồn kho", "Tồn tối thiểu", "Giá trị tồn");
    }

    @Override
    protected List<Object[]> layDuLieu(LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoThongKeBUS.layBaoCaoKho();
    }
}
