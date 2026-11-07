package bus;

import dao.ChiTietBaoGiaDAO;
import dao.PhieuBaoGiaDAO;
import dao.SanPhamDAO;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.PhieuBaoGia;
import enums.TrangThaiBaoGia;

import java.util.List;

public class ChiTietBaoGiaBUS {
    private final ChiTietBaoGiaDAO chiTietBaoGiaDAO = new ChiTietBaoGiaDAO();
    private final PhieuBaoGiaDAO phieuBaoGiaDAO = new PhieuBaoGiaDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public List<ChiTietBaoGia> layTheoPhieuBaoGia(PhieuBaoGia phieuBaoGia) {
        if (phieuBaoGia == null || phieuBaoGia.getMaBaoGia() == null) {
            throw new IllegalArgumentException("Phiếu báo giá không hợp lệ");
        }
        return chiTietBaoGiaDAO.layTheoPhieuBaoGia(phieuBaoGia);
    }

    public boolean themChiTietBaoGia(ChiTietBaoGia ctbg) {
        if (ctbg == null || ctbg.getPhieuBaoGia() == null || ctbg.getDichVu() == null) {
            throw new IllegalArgumentException("Chi tiết báo giá phải gắn đầy đủ phiếu báo giá và dịch vụ");
        }
        PhieuBaoGia pbg = phieuBaoGiaDAO.timTheoMa(ctbg.getPhieuBaoGia().getMaBaoGia());
        if (pbg == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu báo giá: "
                    + ctbg.getPhieuBaoGia().getMaBaoGia());
        }
        if (pbg.getTrangThai() != TrangThaiBaoGia.CHO_XAC_NHAN) {
            throw new IllegalStateException("Chỉ được thêm hạng mục khi báo giá đang ở trạng thái Chờ xác nhận");
        }
        for (ChiTietBaoGia daCo : pbg.getDanhSachChiTiet()) {
            if (daCo.getDichVu().getMaDichVu().equals(ctbg.getDichVu().getMaDichVu())) {
                throw new IllegalArgumentException("Dịch vụ " + ctbg.getDichVu().getMaDichVu()
                        + " đã có trong báo giá " + pbg.getMaBaoGia());
            }
        }

        ctbg.truKhoPhuTung();
        boolean daThem = chiTietBaoGiaDAO.them(ctbg);
        if (daThem) {
            for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
                sanPhamDAO.capNhatSoLuongTon(pt.getSanPham().getMaSanPham(), pt.getSanPham().getSoLuongTon());
            }
        }
        return daThem;
    }

    public boolean capNhatChiTietBaoGia(ChiTietBaoGia ctbg) {
        if (ctbg == null || ctbg.getPhieuBaoGia() == null || ctbg.getDichVu() == null) {
            throw new IllegalArgumentException("Chi tiết báo giá cần cập nhật không hợp lệ");
        }
        return chiTietBaoGiaDAO.capNhat(ctbg);
    }

    public boolean xoaChiTietBaoGia(ChiTietBaoGia ctbg) {
        if (ctbg == null || ctbg.getPhieuBaoGia() == null || ctbg.getDichVu() == null) {
            throw new IllegalArgumentException("Chi tiết báo giá cần xóa không hợp lệ");
        }
        ctbg.hoanKhoPhuTung();
        for (ChiTietDungPhuTung pt : ctbg.getDanhSachPhuTung()) {
            sanPhamDAO.capNhatSoLuongTon(pt.getSanPham().getMaSanPham(), pt.getSanPham().getSoLuongTon());
        }
        return chiTietBaoGiaDAO.xoa(
                ctbg.getPhieuBaoGia().getMaBaoGia(),
                ctbg.getDichVu().getMaDichVu()
        );
    }
}

