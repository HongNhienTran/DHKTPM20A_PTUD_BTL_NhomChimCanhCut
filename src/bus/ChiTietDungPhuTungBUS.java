package bus;

import dao.ChiTietDungPhuTungDAO;
import dao.SanPhamDAO;
import entity.ChiTietBaoGia;
import entity.ChiTietDungPhuTung;
import entity.SanPham;

import java.util.List;

public class ChiTietDungPhuTungBUS {
    private final ChiTietDungPhuTungDAO chiTietDungPhuTungDAO = new ChiTietDungPhuTungDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public List<ChiTietDungPhuTung> layTheoChiTietBaoGia(ChiTietBaoGia chiTietBaoGia) {
        if (chiTietBaoGia == null || chiTietBaoGia.getPhieuBaoGia() == null || chiTietBaoGia.getDichVu() == null) {
            throw new IllegalArgumentException("Chi tiết báo giá không hợp lệ");
        }
        return chiTietDungPhuTungDAO.layTheoChiTietBaoGia(chiTietBaoGia);
    }

    public boolean themPhuTungVaoBaoGia(ChiTietDungPhuTung ctdpt) {
        if (ctdpt == null || ctdpt.getChiTietBaoGia() == null || ctdpt.getSanPham() == null) {
            throw new IllegalArgumentException("Chi tiết dùng phụ tùng phải gắn đầy đủ chi tiết báo giá và sản phẩm");
        }
        SanPham spHienTai = sanPhamDAO.timTheoMa(ctdpt.getSanPham().getMaSanPham());
        if (spHienTai == null || !spHienTai.isTrangThai()) {
            throw new IllegalArgumentException("Phụ tùng không tồn tại hoặc ngừng kinh doanh");
        }
        ctdpt.setSanPham(spHienTai);
        ctdpt.truKho();

        boolean daThem = chiTietDungPhuTungDAO.them(ctdpt);
        if (daThem) {
            sanPhamDAO.capNhatSoLuongTon(spHienTai.getMaSanPham(), spHienTai.getSoLuongTon());
        }
        return daThem;
    }

    public boolean xoaPhuTungKhoiBaoGia(ChiTietDungPhuTung ctdpt) {
        if (ctdpt == null || ctdpt.getChiTietBaoGia() == null || ctdpt.getSanPham() == null) {
            throw new IllegalArgumentException("Chi tiết dùng phụ tùng cần xóa không hợp lệ");
        }
        SanPham spHienTai = sanPhamDAO.timTheoMa(ctdpt.getSanPham().getMaSanPham());
        if (spHienTai != null) {
            ctdpt.setSanPham(spHienTai);
            ctdpt.hoanKho();
            sanPhamDAO.capNhatSoLuongTon(spHienTai.getMaSanPham(), spHienTai.getSoLuongTon());
        }
        return chiTietDungPhuTungDAO.xoa(
                ctdpt.getChiTietBaoGia().getPhieuBaoGia().getMaBaoGia(),
                ctdpt.getChiTietBaoGia().getDichVu().getMaDichVu(),
                ctdpt.getSanPham().getMaSanPham()
        );
    }
}

