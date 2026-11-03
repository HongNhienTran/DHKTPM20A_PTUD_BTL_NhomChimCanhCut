package com.pengugarage.dao;

import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Khách Hàng (khach_hang).
 * Cung cấp các thao tác CRUD và tra cứu hồ sơ khách hàng kèm phương tiện.
 *
 * Lưu ý: Khung code-base chuẩn bị sẵn, thành viên nhóm sẽ bổ sung câu lệnh SQL chi tiết.
 */
public class KhachHangDAO {

    /**
     * Lấy toàn bộ danh sách khách hàng trong hệ thống.
     *
     * @return Danh sách KhachHang
     */
    public List<KhachHang> layTatCa() {
        List<KhachHang> dsKhachHang = new ArrayList<>();
        // TODO: [M05-T01 - Phong] Viết SQL SELECT * FROM KhachHang ORDER BY id DESC
        String sql ="SELECT * FROM Khach_hang ORDER BY id DESC";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                KhachHang kh = new KhachHang();
                kh.setId(rs.getInt("id"));
                kh.setMaKhachHang(rs.getString("ma_khach_hang"));
                kh.setHoTen(rs.getString("ho_ten"));
                kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                kh.setDiaChi(rs.getString("dia_chi"));
                kh.setGioiTinh(rs.getString("gioi_tinh"));
                dsKhachHang.add(kh);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách khách hàng" + e.getMessage()  );
        }

        return dsKhachHang;
    }

    /**
     * Tìm khách hàng theo ID định danh.
     *
     * @param id ID khách hàng
     * @return Đối tượng KhachHang hoặc null nếu không tìm thấy
     */
    public KhachHang timTheoId(int id) {
        // TODO: [M05-T01 - Phong] Viết SQL SELECT * FROM KhachHang WHERE id = ?
        return null;
    }

    /**
     * Tìm kiếm khách hàng theo số điện thoại (phục vụ chọn khách hàng tại quầy và bán hàng).
     *
     * @param soDienThoai Số điện thoại khách hàng
     * @return KhachHang
     */
    public KhachHang timTheoSoDienThoai(String soDienThoai) {
        // TODO: [M05-T01 - Phong] Viết SQL SELECT * FROM KhachHang WHERE so_dien_thoai = ?
        return null;
    }

    /**
     * Tìm kiếm và lọc khách hàng đa tiêu chí theo từ khóa và giới tính.
     *
     * @param tuKhoa   Từ khóa (Mã KH, Tên, Số điện thoại, Địa chỉ)
     * @param gioiTinh Giới tính (Nam, Nữ, hoặc null nếu tất cả)
     * @return Danh sách khách hàng phù hợp
     */
    public List<KhachHang> timKiemVaLoc(String tuKhoa, String gioiTinh) {
        List<KhachHang> dsKetQua = new ArrayList<>();
        // TODO: [M05-T01 - Phong] Viết SQL SELECT * FROM KhachHang WHERE (ho_ten LIKE ? OR so_dien_thoai LIKE ? ...)
        return dsKetQua;
    }

    /**
     * Lấy danh sách các phương tiện (xe ô tô) do khách hàng này đứng tên sở hữu.
     *
     * @param khachHangId ID khách hàng
     * @return Danh sách PhuongTien
     */
    public List<PhuongTien> layDanhSachXeCuaKhach(int khachHangId) {
        List<PhuongTien> dsXe = new ArrayList<>();
        // TODO: [M05-T02 - Phong] Viết SQL SELECT * FROM PhuongTien WHERE khach_hang_id = ?
        return dsXe;
    }

    /**
     * Thêm mới một hồ sơ khách hàng vào cơ sở dữ liệu.
     *
     * @param kh Đối tượng KhachHang mới
     * @return true nếu thêm thành công
     */
    public boolean them(KhachHang kh) {
        // TODO: [M05-T03 - Phong] Viết SQL INSERT INTO KhachHang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh) VALUES (...)
        return false;
    }

    /**
     * Cập nhật thông tin hồ sơ khách hàng.
     *
     * @param kh Đối tượng KhachHang cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean capNhat(KhachHang kh) {
        // TODO: [M05-T04 - Phong] Viết SQL UPDATE KhachHang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, gioi_tinh = ? WHERE id = ?
        return false;
    }

    /**
     * Xóa hồ sơ khách hàng theo ID (kiểm tra ràng buộc hóa đơn và lịch sử tiếp nhận trước khi xóa).
     *
     * @param id ID khách hàng
     * @return true nếu xóa thành công
     */
    public boolean xoa(int id) {
        // TODO: [Phân công thành viên] Kiểm tra ràng buộc khóa ngoại và thực hiện DELETE FROM KhachHang WHERE id = ?
        return false;
    }
}
