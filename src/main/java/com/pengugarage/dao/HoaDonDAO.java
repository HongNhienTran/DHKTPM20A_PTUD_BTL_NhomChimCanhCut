package com.pengugarage.dao;

import com.pengugarage.model.entity.ChiTietHoaDon;
import com.pengugarage.model.entity.HoaDon;
import com.pengugarage.model.enums.LoaiHoaDon;
import com.pengugarage.model.enums.PhuongThucThanhToan;
import com.pengugarage.model.enums.TrangThaiThanhToan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Hóa Đơn (HoaDon) và Chi Tiết Hóa Đơn (ChiTietHoaDon).
 * Cung cấp các thao tác truy vấn, lọc dữ liệu hóa đơn phục vụ:
 * - Màn hình Danh sách hóa đơn (quản lý toàn bộ)
 * - Màn hình Lập hóa đơn / Thu ngân (hóa đơn in tạm trạng thái CHUA_THANH_TOAN)
 *
 * Lưu ý: Khung method chuẩn bị sẵn, thành viên nhóm sẽ bổ sung câu lệnh SQL chi tiết.
 */
public class HoaDonDAO {

    /**
     * Thêm mới một hóa đơn vào cơ sở dữ liệu (lưu tạm trạng thái CHUA_THANH_TOAN hoặc tạo mới).
     *
     * @param hd Đối tượng HoaDon
     * @return ID tự sinh của hóa đơn vừa tạo (hoặc -1 nếu thất bại)
     */
    public int themHoaDon(HoaDon hd) {
        // TODO: [M06-T04 - Tân] Viết SQL INSERT INTO HoaDon (...) và trả về generated key (ID)
        return -1;
    }

    /**
     * Thêm mới một dòng chi tiết vào hóa đơn (sản phẩm/phụ tùng).
     *
     * @param ct Đối tượng ChiTietHoaDon
     * @return true nếu thêm thành công
     */
    public boolean themChiTietHoaDon(ChiTietHoaDon ct) {
        // TODO: [M06-T04 - Tân] Viết SQL INSERT INTO ChiTietHoaDon (...)
        return false;
    }

    /**
     * Lấy toàn bộ danh sách hóa đơn (cả Bán lẻ và Dịch vụ, mọi trạng thái thanh toán)
     * sắp xếp theo thời gian lập giảm dần (mới nhất lên đầu).
     *
     * @return Danh sách hóa đơn
     */
    public List<HoaDon> layTatCaHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        // TODO: [Phân công thành viên] Viết câu lệnh SQL SELECT toàn bộ hóa đơn từ bảng HoaDon
        // JOIN với bảng KhachHang, NhanVien, PhieuTiepNhan để lấy đầy đủ thông tin hiển thị
        return dsHoaDon;
    }

    /**
     * Lấy danh sách hóa đơn theo trạng thái thanh toán cụ thể.
     * Dùng cho màn hình Thu ngân để lấy các hóa đơn in tạm có trạng thái CHUA_THANH_TOAN.
     *
     * @param trangThai Trạng thái thanh toán (CHUA_THANH_TOAN / DA_THANH_TOAN)
     * @return Danh sách hóa đơn tương ứng
     */
    public List<HoaDon> layHoaDonTheoTrangThai(TrangThaiThanhToan trangThai) {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        // TODO: [Phân công thành viên] Viết câu lệnh SQL SELECT hóa đơn WHERE trang_thai_thanh_toan = ?
        // ORDER BY thoi_gian_lap DESC
        return dsHoaDon;
    }

    /**
     * Tìm kiếm và lọc hóa đơn đa tiêu chí: từ khóa (mã HĐ, tên/SĐT khách hàng, biển số xe),
     * loại hóa đơn, trạng thái thanh toán và khoảng thời gian lập.
     *
     * @param tuKhoa        Từ khóa tìm kiếm (mã HĐ, tên khách, SĐT, biển số...)
     * @param loaiHoaDon    Loại hóa đơn (BAN_HANG, DICH_VU hoặc null nếu chọn tất cả)
     * @param trangThai     Trạng thái thanh toán (null nếu chọn tất cả)
     * @param tuNgay        Ngày bắt đầu (null nếu không giới hạn)
     * @param denNgay       Ngày kết thúc (null nếu không giới hạn)
     * @return Danh sách hóa đơn thỏa mãn điều kiện lọc
     */
    public List<HoaDon> timKiemVaLocHoaDon(String tuKhoa, LoaiHoaDon loaiHoaDon, 
                                          TrangThaiThanhToan trangThai, LocalDate tuNgay, LocalDate denNgay) {
        List<HoaDon> dsKetQua = new ArrayList<>();
        // TODO: [Phân công thành viên] Xây dựng câu lệnh PreparedStatement động dựa trên các tham số truyền vào
        return dsKetQua;
    }

    /**
     * Lấy danh sách chi tiết các mặt hàng / dịch vụ của một hóa đơn theo ID hóa đơn.
     *
     * @param hoaDonId ID của hóa đơn cần lấy chi tiết
     * @return Danh sách ChiTietHoaDon
     */
    public List<ChiTietHoaDon> layChiTietHoaDon(int hoaDonId) {
        List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
        // TODO: [Phân công thành viên] Viết SQL SELECT * FROM ChiTietHoaDon WHERE hoa_don_id = ?
        // JOIN bảng SanPham và DichVu để lấy tên sản phẩm, đơn vị tính, quy cách
        return dsChiTiet;
    }

    /**
     * Cập nhật trạng thái thanh toán của hóa đơn thành DA_THANH_TOAN,
     * lưu phương thức thanh toán, thời gian thanh toán và ghi chú.
     *
     * @param hoaDonId           ID hóa đơn
     * @param phuongThucThanhToan Phương thức thanh toán (TIEN_MAT, CHUYEN_KHOAN,...)
     * @param ghiChu             Ghi chú thu tiền / mã giao dịch
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean xacNhanThanhToan(int hoaDonId, PhuongThucThanhToan phuongThucThanhToan, String ghiChu) {
        // TODO: [Phân công thành viên] Viết UPDATE HoaDon SET trang_thai_thanh_toan = 'DA_THANH_TOAN',
        // phuong_thuc_thanh_toan = ?, thoi_gian_thanh_toan = NOW(), ghi_chu = ? WHERE id = ?
        return false;
    }

    /**
     * Hủy hóa đơn (đối với hóa đơn in tạm hoặc khách hủy giao dịch trước khi thanh toán).
     *
     * @param hoaDonId ID hóa đơn cần hủy
     * @param lyDoHuy  Lý do hủy đơn
     * @return true nếu thành công
     */
    public boolean huyHoaDon(int hoaDonId, String lyDoHuy) {
        // TODO: [Phân công thành viên] Xử lý nghiệp vụ hủy đơn (cập nhật trạng thái hoặc xóa chi tiết và hóa đơn)
        return false;
    }
}

