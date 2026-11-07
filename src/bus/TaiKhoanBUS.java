package bus;

import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import util.MaHoaMatKhauUtil;

import java.util.List;

public class TaiKhoanBUS {
    public static final String MAT_KHAU_KHOI_TAO_MAC_DINH = "123456";

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public String phatSinhTenDangNhap(NhanVien nhanVien) {
        if (nhanVien == null || nhanVien.getMaNhanVien() == null) {
            throw new IllegalArgumentException("Nhân viên phải có mã hợp lệ để lấy làm tên đăng nhập");
        }
        return nhanVien.getMaNhanVien();
    }

    public String maHoaMatKhau(String matKhauGoc) {
        if (matKhauGoc == null || matKhauGoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        String trimmed = matKhauGoc.trim();
        return MaHoaMatKhauUtil.laChuoiDaMaHoa(trimmed)
                ? trimmed
                : MaHoaMatKhauUtil.maHoa(trimmed);
    }
    
    public TaiKhoan khoiTaoTaiKhoanChoNhanVien(NhanVien nhanVien, String matKhauBanDau) {
        String tenDangNhap = phatSinhTenDangNhap(nhanVien);
        String matKhauGoc = (matKhauBanDau != null && !matKhauBanDau.trim().isEmpty())
                ? matKhauBanDau
                : MAT_KHAU_KHOI_TAO_MAC_DINH;
        String matKhauDaMaHoa = maHoaMatKhau(matKhauGoc);
        return new TaiKhoan(
                tenDangNhap,
                matKhauDaMaHoa,
                nhanVien.isTrangThai(),
                nhanVien
        );
    }

    public TaiKhoan khoiTaoTaiKhoanChoNhanVien(NhanVien nhanVien) {
        return khoiTaoTaiKhoanChoNhanVien(nhanVien, MAT_KHAU_KHOI_TAO_MAC_DINH);
    }

    public TaiKhoan dangNhap(String tenDangNhap, String matKhauGoc) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (matKhauGoc == null || matKhauGoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        String matKhauDaMaHoa = maHoaMatKhau(matKhauGoc);
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhapVaMatKhau(tenDangNhap.trim(), matKhauDaMaHoa);
        if (tk == null) {
            return null;
        }
        if (!tk.isTrangThai()) {
            throw new IllegalStateException("Tài khoản đã bị khóa");
        }
        if (tk.getMaNhanVien() != null && !tk.getMaNhanVien().isTrangThai()) {
            throw new IllegalStateException("Nhân viên sở hữu tài khoản đã ngừng làm việc");
        }
        return tk;
    }

    public List<TaiKhoan> layTatCa() {
        return taiKhoanDAO.layTatCa();
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return null;
        }
        return taiKhoanDAO.timTheoTenDangNhap(tenDangNhap.trim());
    }

    public boolean doiMatKhau(String tenDangNhap, String matKhauCuGoc, String matKhauMoiGoc) {
        if (matKhauMoiGoc == null || matKhauMoiGoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        String matKhauCuMaHoa = maHoaMatKhau(matKhauCuGoc);
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhapVaMatKhau(tenDangNhap.trim(), matKhauCuMaHoa);
        if (tk == null) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu hiện tại không đúng");
        }
        tk.setMatKhauMaHoa(maHoaMatKhau(matKhauMoiGoc));
        return taiKhoanDAO.capNhat(tk);
    }

    public boolean datLaiMatKhau(String tenDangNhap, String matKhauMoiGoc) {
        if (matKhauMoiGoc == null || matKhauMoiGoc.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu đặt lại không được để trống");
        }
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(tenDangNhap.trim());
        if (tk == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản: " + tenDangNhap);
        }
        tk.setMatKhauMaHoa(maHoaMatKhau(matKhauMoiGoc));
        return taiKhoanDAO.capNhat(tk);
    }

    public boolean datLaiMatKhauMacDinh(String tenDangNhap) {
        return datLaiMatKhau(tenDangNhap, MAT_KHAU_KHOI_TAO_MAC_DINH);
    }

    public boolean capNhatTrangThaiTaiKhoan(String tenDangNhap, boolean trangThaiMoi) {
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(tenDangNhap.trim());
        if (tk == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản: " + tenDangNhap);
        }
        tk.setTrangThai(trangThaiMoi);
        return taiKhoanDAO.capNhat(tk);
    }
}
