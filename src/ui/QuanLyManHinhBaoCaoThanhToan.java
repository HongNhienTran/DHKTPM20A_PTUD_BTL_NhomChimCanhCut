package ui;

import java.time.LocalDate;
import java.util.List;

public class QuanLyManHinhBaoCaoThanhToan extends QuanLyManHinhBaoCaoChiTiet {
    public QuanLyManHinhBaoCaoThanhToan() {
        super("Báo cáo thanh toán", "Phương thức", "Số hóa đơn", "Tổng tiền");
    }

    @Override
    protected List<Object[]> layDuLieu(LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoThongKeBUS.layBaoCaoThanhToan(tuNgay, denNgay);
    }
}
