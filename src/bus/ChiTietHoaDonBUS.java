package bus;

import dao.ChiTietHoaDonDAO;
import entity.ChiTietHoaDon;
import entity.DichVu;
import entity.HoaDon;
import entity.SanPham;
import enums.LoaiHangMuc;

import java.util.List;

public class ChiTietHoaDonBUS {
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();

    public List<ChiTietHoaDon> layTheoHoaDon(HoaDon hoaDon) {
        if (hoaDon == null || hoaDon.getMaHoaDon() == null) {
            throw new IllegalArgumentException("Hóa đơn không hợp lệ");
        }
        return chiTietHoaDonDAO.layTheoHoaDon(hoaDon);
    }

    public ChiTietHoaDon taoDongDichVu(HoaDon hoaDon, DichVu dichVu, int soLuong, double donGia) {
        if (dichVu == null) {
            throw new IllegalArgumentException("Dịch vụ trong chi tiết hóa đơn không được null");
        }
        return new ChiTietHoaDon(LoaiHangMuc.DICH_VU, soLuong, donGia, hoaDon, dichVu, null);
    }

    public ChiTietHoaDon taoDongSanPham(HoaDon hoaDon, SanPham sanPham, int soLuong, double donGia) {
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm trong chi tiết hóa đơn không được null");
        }
        return new ChiTietHoaDon(LoaiHangMuc.SAN_PHAM, soLuong, donGia, hoaDon, null, sanPham);
    }
}

