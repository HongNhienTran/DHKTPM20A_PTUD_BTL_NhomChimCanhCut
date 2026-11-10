package ui;

import java.time.LocalDate;
import java.util.List;

public class QuanLyManHinhBaoCaoBanHang extends QuanLyManHinhBaoCaoChiTiet {
    public QuanLyManHinhBaoCaoBanHang() {
        super("Báo cáo bán hàng", "Mã sản phẩm", "Sản phẩm", "Số lượng bán", "Doanh thu");
    }

    @Override
    protected List<Object[]> layDuLieu(LocalDate tuNgay, LocalDate denNgay) {
        return baoCaoThongKeBUS.layBaoCaoBanHang(tuNgay, denNgay);
    }
}
