package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import entity.TaiKhoan;
import enums.ChucVu;
import enums.ChuyenMon;

import java.util.ArrayList;
import java.util.List;

public class NhanVienBUS {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanBUS taiKhoanBUS = new TaiKhoanBUS();

    public String taoMaNhanVien(int soThuTu) {
        if (soThuTu < 0 || soThuTu > 999) {
            throw new IllegalArgumentException("Số thứ tự nhân viên phải nằm trong khoảng 000 - 999");
        }
        return String.format("NV%03d", soThuTu);
    }

    public String phatSinhMaNhanVien() {
        return phatSinhMaNhanVienTheoChucVu(ChucVu.KY_THUAT_VIEN);
    }

    public String phatSinhMaNhanVienTheoChucVu(ChucVu chucVu) {
        String tienTo = switch (chucVu) {
            case QUAN_LY -> "NV_QL_";
            case LE_TAN -> "NV_LT_";
            case KY_THUAT_VIEN -> "NV_KTV_";
            default -> "NV_";
        };
        int max = 0;
        for (NhanVien nv : nhanVienDAO.layTatCa()) {
            String ma = nv.getMaNhanVien();
            if (ma != null) {
                if (ma.startsWith(tienTo)) {
                    try {
                        int num = Integer.parseInt(ma.substring(tienTo.length()));
                        if (num > max) max = num;
                    } catch (Exception ignored) {}
                } else if (ma.matches("^NV\\d{3}$")) {
                    try {
                        int num = Integer.parseInt(ma.substring(2));
                        if (num > max) max = num;
                    } catch (Exception ignored) {}
                }
            }
        }
        return String.format("%s%03d", tienTo, max + 1);
    }

    public List<NhanVien> layTatCa() {
        return nhanVienDAO.layTatCa();
    }

    public List<NhanVien> layTheoChucVu(ChucVu chucVu) {
        if (chucVu == null) {
            throw new IllegalArgumentException("Chức vụ cần lọc không được null");
        }
        return nhanVienDAO.timTheoChucVu(chucVu);
    }

    public List<NhanVien> layKyThuatVienDangLamViec() {
        List<NhanVien> ketQua = new ArrayList<>();
        for (NhanVien nv : nhanVienDAO.timTheoChucVu(ChucVu.KY_THUAT_VIEN)) {
            if (nv.isTrangThai()) {
                ketQua.add(nv);
            }
        }
        return ketQua;
    }

    public List<NhanVien> layKyThuatVienTheoChuyenMon(ChuyenMon chuyenMon) {
        if (chuyenMon == null) {
            return layKyThuatVienDangLamViec();
        }
        return nhanVienDAO.timKyThuatVienTheoChuyenMon(chuyenMon);
    }

    public NhanVien timTheoMa(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return null;
        }
        return nhanVienDAO.timTheoMa(maNhanVien.trim());
    }

    public boolean themNhanVien(NhanVien nv) {
        return themNhanVien(nv, null);
    }

    public boolean themNhanVien(NhanVien nv, String matKhauBanDau) {
        if (nv == null) {
            throw new IllegalArgumentException("Thông tin nhân viên không được null");
        }
        if (nv.getChucVu() == ChucVu.KY_THUAT_VIEN && nv.getChuyenMon() == null) {
            throw new IllegalArgumentException("Kỹ thuật viên bắt buộc phải có chuyên môn");
        }
        NhanVien trungSDT = nhanVienDAO.timTheoSoDienThoai(nv.getSoDienThoai());
        if (trungSDT != null) {
            throw new IllegalArgumentException("Số điện thoại đã thuộc về nhân viên " + trungSDT.getMaNhanVien());
        }
        if (nv.getMaNhanVien() == null || nv.getMaNhanVien().trim().isEmpty()) {
            nv.setMaNhanVien(phatSinhMaNhanVien());
        } else if (nhanVienDAO.timTheoMa(nv.getMaNhanVien()) != null) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + nv.getMaNhanVien());
        }

        TaiKhoan tk = nv.getTaiKhoan();
        if (tk == null) {
            taiKhoanBUS.khoiTaoTaiKhoanChoNhanVien(nv, matKhauBanDau);
        } else {
            tk.setMaNhanVien(nv);
            tk.setTenDangNhap(nv.getMaNhanVien());
            String matKhauNguon = (matKhauBanDau != null && !matKhauBanDau.trim().isEmpty())
                    ? matKhauBanDau
                    : tk.getMatKhauMaHoa();
            tk.setMatKhauMaHoa(taiKhoanBUS.maHoaMatKhau(matKhauNguon));
            if (!nv.isTrangThai()) {
                tk.setTrangThai(false);
            }
        }

        return nhanVienDAO.them(nv);
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        if (nv == null || nv.getMaNhanVien() == null) {
            throw new IllegalArgumentException("Nhân viên cần cập nhật không hợp lệ");
        }
        NhanVien hienTai = nhanVienDAO.timTheoMa(nv.getMaNhanVien());
        if (hienTai == null) {
            throw new IllegalArgumentException("Không tìm thấy nhân viên có mã: " + nv.getMaNhanVien());
        }
        if (nv.getChucVu() == ChucVu.KY_THUAT_VIEN && nv.getChuyenMon() == null) {
            throw new IllegalArgumentException("Kỹ thuật viên bắt buộc phải có chuyên môn");
        }
        NhanVien trungSDT = nhanVienDAO.timTheoSoDienThoai(nv.getSoDienThoai());
        if (trungSDT != null && !trungSDT.getMaNhanVien().equals(nv.getMaNhanVien())) {
            throw new IllegalArgumentException("Số điện thoại đã thuộc về nhân viên khác ("
                    + trungSDT.getMaNhanVien() + ")");
        }
        if (nv.getTaiKhoan() == null && hienTai.getTaiKhoan() != null) {
            nv.setTaiKhoan(hienTai.getTaiKhoan());
        } else if (nv.getTaiKhoan() != null) {
            nv.getTaiKhoan().setMatKhauMaHoa(taiKhoanBUS.maHoaMatKhau(nv.getTaiKhoan().getMatKhauMaHoa()));
        }
        if (!nv.isTrangThai() && nv.getTaiKhoan() != null) {
            nv.getTaiKhoan().setTrangThai(false);
        }
        return nhanVienDAO.capNhat(nv);
    }

    public boolean xoaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên cần xóa không được để trống");
        }
        return nhanVienDAO.xoa(maNhanVien.trim());
    }
}
