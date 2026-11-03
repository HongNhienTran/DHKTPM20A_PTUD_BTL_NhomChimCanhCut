package com.pengugarage.dao;

import com.pengugarage.model.entity.HangXe;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.entity.PhieuTiepNhan;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.model.enums.TrangThaiPhieuTiepNhan;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Thực thể Phiếu Tiếp Nhận (PhieuTiepNhan).
 * Phụ trách lưu trữ, tra cứu và xử lý tiếp nhận xe vào gara.
 * 
 * Phụ trách: Trần Hồng Nhiên (Module Tiếp nhận xe - M08).
 */
public class PhieuTiepNhanDAO {

    /**
     * Lấy toàn bộ danh sách phiếu tiếp nhận trong hệ thống.
     * Sắp xếp theo thời gian tiếp nhận mới nhất lên đầu.
     *
     * @return Danh sách PhieuTiepNhan
     */
    public List<PhieuTiepNhan> layTatCa() {
        List<PhieuTiepNhan> ds = new ArrayList<>();
        String sql = """
            SELECT ptn.*,
                   pt.id AS pt_id, pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh,
                   nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten AS nv_ho_ten
            FROM phieu_tiep_nhan ptn
            LEFT JOIN phuong_tien pt ON ptn.phuong_tien_id = pt.id
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            LEFT JOIN nhan_vien nv ON ptn.nhan_vien_tiep_nhan_id = nv.id
            ORDER BY ptn.thoi_gian_tiep_nhan DESC
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapResultSetToPhieu(rs));
            }
        } catch (SQLException ignored) {
        }
        return ds;
    }

    /**
     * Tìm phiếu tiếp nhận theo ID định danh.
     *
     * @param id ID phiếu tiếp nhận
     * @return Đối tượng PhieuTiepNhan hoặc null
     */
    public PhieuTiepNhan timTheoId(int id) {
        String sql = """
            SELECT ptn.*,
                   pt.id AS pt_id, pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh,
                   nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten AS nv_ho_ten
            FROM phieu_tiep_nhan ptn
            LEFT JOIN phuong_tien pt ON ptn.phuong_tien_id = pt.id
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            LEFT JOIN nhan_vien nv ON ptn.nhan_vien_tiep_nhan_id = nv.id
            WHERE ptn.id = ?
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieu(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Tìm phiếu tiếp nhận theo mã phiếu (vd: TN20261003-001).
     *
     * @param maTiepNhan Mã phiếu tiếp nhận
     * @return Đối tượng PhieuTiepNhan hoặc null
     */
    public PhieuTiepNhan timTheoMa(String maTiepNhan) {
        if (maTiepNhan == null || maTiepNhan.trim().isEmpty()) return null;
        String sql = """
            SELECT ptn.*,
                   pt.id AS pt_id, pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh,
                   nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten AS nv_ho_ten
            FROM phieu_tiep_nhan ptn
            LEFT JOIN phuong_tien pt ON ptn.phuong_tien_id = pt.id
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            LEFT JOIN nhan_vien nv ON ptn.nhan_vien_tiep_nhan_id = nv.id
            WHERE ptn.ma_tiep_nhan = ?
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maTiepNhan.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieu(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Lấy danh sách phiếu tiếp nhận theo trạng thái xử lý.
     * Phục vụ quản lý hàng đợi và theo dõi tiến độ xe tại xưởng.
     *
     * @param trangThai Trạng thái phiếu tiếp nhận
     * @return Danh sách phiếu tiếp nhận phù hợp
     */
    public List<PhieuTiepNhan> layTheoTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        List<PhieuTiepNhan> ds = new ArrayList<>();
        if (trangThai == null) return layTatCa();

        String sql = """
            SELECT ptn.*,
                   pt.id AS pt_id, pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh,
                   nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten AS nv_ho_ten
            FROM phieu_tiep_nhan ptn
            LEFT JOIN phuong_tien pt ON ptn.phuong_tien_id = pt.id
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            LEFT JOIN nhan_vien nv ON ptn.nhan_vien_tiep_nhan_id = nv.id
            WHERE ptn.trang_thai = ?
            ORDER BY ptn.thoi_gian_tiep_nhan ASC
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapResultSetToPhieu(rs));
                }
            }
        } catch (SQLException ignored) {
        }
        return ds;
    }

    /**
     * Lấy danh sách các xe đang lưu tại xưởng (chưa hủy và sắp xếp theo thứ tự thời gian vào).
     * Phục vụ hiển thị hàng đợi xe tại xưởng theo thời gian thực (Kanban / Queue).
     *
     * @return Danh sách xe trong hàng đợi
     */
    public List<PhieuTiepNhan> layDanhSachHangDoiXeTaiXuong() {
        List<PhieuTiepNhan> ds = new ArrayList<>();
        String sql = """
            SELECT ptn.*,
                   pt.id AS pt_id, pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, pt.nam_san_xuat,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.gioi_tinh,
                   nv.id AS nv_id, nv.ma_nhan_vien, nv.ho_ten AS nv_ho_ten
            FROM phieu_tiep_nhan ptn
            LEFT JOIN phuong_tien pt ON ptn.phuong_tien_id = pt.id
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            LEFT JOIN nhan_vien nv ON ptn.nhan_vien_tiep_nhan_id = nv.id
            WHERE ptn.trang_thai != 'DA_HUY'
            ORDER BY ptn.thoi_gian_tiep_nhan ASC
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapResultSetToPhieu(rs));
            }
        } catch (SQLException ignored) {
        }
        return ds;
    }

    /**
     * Lấy mã phiếu tiếp nhận cuối cùng được tạo trong một ngày cụ thể.
     * Dùng để tính toán số thứ tự tự tăng cho hàm sinh mã tự động.
     *
     * @param ngay Ngày cần lấy mã cuối
     * @return Mã tiếp nhận dạng TNyyyyMMdd-XXX hoặc null nếu chưa có phiếu nào
     */
    public String layMaTiepNhanCuoiCungTrongNgay(LocalDate ngay) {
        if (ngay == null) ngay = LocalDate.now();
        String tienTo = "TN" + ngay.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-%";
        String sql = "SELECT ma_tiep_nhan FROM phieu_tiep_nhan WHERE ma_tiep_nhan LIKE ? ORDER BY id DESC LIMIT 1";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tienTo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ma_tiep_nhan");
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Lập phiếu tiếp nhận xe mới vào xưởng.
     *
     * @param ptn Đối tượng PhieuTiepNhan cần thêm
     * @return true nếu thêm thành công
     */
    public boolean them(PhieuTiepNhan ptn) {
        if (ptn == null) return false;
        String sql = """
            INSERT INTO phieu_tiep_nhan (ma_tiep_nhan, phuong_tien_id, nhan_vien_tiep_nhan_id,
                                        thoi_gian_tiep_nhan, trang_thai, so_km_hien_tai,
                                        yeu_cau_khach_hang, tinh_trang_noi_that, tinh_trang_ngoai_that,
                                        so_chia_khoa, ghi_chu)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ptn.getMaTiepNhan());
            ps.setInt(2, ptn.getPhuongTienId());
            ps.setInt(3, ptn.getNhanVienTiepNhanId() > 0 ? ptn.getNhanVienTiepNhanId() : 1);

            LocalDateTime tg = ptn.getThoiGianTiepNhan() != null ? ptn.getThoiGianTiepNhan() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(tg));
            ps.setString(5, ptn.getTrangThai() != null ? ptn.getTrangThai().name() : TrangThaiPhieuTiepNhan.CHO_TIEP_NHAN.name());
            ps.setDouble(6, ptn.getSoKMHienTai());
            ps.setString(7, ptn.getYeuCauKhachHang());
            ps.setString(8, ptn.getTinhTrangNoiThat());
            ps.setString(9, ptn.getTinhTrangNgoaiThat());
            ps.setInt(10, ptn.getSoChiaKhoa());
            ps.setString(11, ptn.getGhiChu());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        ptn.setId(gk.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * Cập nhật thông tin phiếu tiếp nhận xe (tình trạng, yêu cầu, số KM, ghi chú).
     *
     * @param ptn Đối tượng PhieuTiepNhan cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean capNhat(PhieuTiepNhan ptn) {
        if (ptn == null || ptn.getId() <= 0) return false;
        String sql = """
            UPDATE phieu_tiep_nhan
            SET so_km_hien_tai = ?, yeu_cau_khach_hang = ?,
                tinh_trang_noi_that = ?, tinh_trang_ngoai_that = ?,
                so_chia_khoa = ?, ghi_chu = ?
            WHERE id = ?
            """;

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, ptn.getSoKMHienTai());
            ps.setString(2, ptn.getYeuCauKhachHang());
            ps.setString(3, ptn.getTinhTrangNoiThat());
            ps.setString(4, ptn.getTinhTrangNgoaiThat());
            ps.setInt(5, ptn.getSoChiaKhoa());
            ps.setString(6, ptn.getGhiChu());
            ps.setInt(7, ptn.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * Cập nhật trạng thái phiếu tiếp nhận xe (CHO_TIEP_NHAN -> DA_TIEP_NHAN -> DANG_KIEM_TRA -> DANG_SUA_CHUA -> HOAN_THANH / DA_HUY).
     *
     * @param id        ID phiếu tiếp nhận
     * @param trangThai Trạng thái mới
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatTrangThai(int id, TrangThaiPhieuTiepNhan trangThai) {
        if (id <= 0 || trangThai == null) return false;
        String sql = "UPDATE phieu_tiep_nhan SET trang_thai = ? WHERE id = ?";
        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) {
        }
        return false;
    }

    /**
     * Chuyển đổi dòng dữ liệu từ ResultSet sang đối tượng PhieuTiepNhan kèm theo
     * thông tin Phương Tiện, Hãng Xe, Khách Hàng và Nhân Viên Tiếp Nhận.
     */
    public PhieuTiepNhan mapResultSetToPhieu(ResultSet rs) throws SQLException {
        PhieuTiepNhan ptn = new PhieuTiepNhan();
        ptn.setId(rs.getInt("id"));
        ptn.setMaTiepNhan(rs.getString("ma_tiep_nhan"));
        ptn.setPhuongTienId(rs.getInt("phuong_tien_id"));
        ptn.setNhanVienTiepNhanId(rs.getInt("nhan_vien_tiep_nhan_id"));

        Timestamp ts = rs.getTimestamp("thoi_gian_tiep_nhan");
        if (ts != null) {
            ptn.setThoiGianTiepNhan(ts.toLocalDateTime());
        }

        try {
            ptn.setTrangThai(TrangThaiPhieuTiepNhan.valueOf(rs.getString("trang_thai")));
        } catch (Exception e) {
            ptn.setTrangThai(TrangThaiPhieuTiepNhan.CHO_TIEP_NHAN);
        }

        ptn.setSoKMHienTai(rs.getDouble("so_km_hien_tai"));
        ptn.setYeuCauKhachHang(rs.getString("yeu_cau_khach_hang"));
        ptn.setTinhTrangNoiThat(rs.getString("tinh_trang_noi_that"));
        ptn.setTinhTrangNgoaiThat(rs.getString("tinh_trang_ngoai_that"));
        ptn.setSoChiaKhoa(rs.getInt("so_chia_khoa"));
        ptn.setGhiChu(rs.getString("ghi_chu"));

        // Ánh xạ Phương Tiện
        try {
            int ptId = rs.getInt("pt_id");
            if (ptId > 0) {
                PhuongTien pt = new PhuongTien();
                pt.setId(ptId);
                pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                pt.setBienSoXe(rs.getString("bien_so_xe"));
                pt.setMauXe(rs.getString("mau_xe"));
                pt.setMauSacXe(rs.getString("mau_sac_xe"));
                pt.setNamSanXuat(rs.getInt("nam_san_xuat"));

                try {
                    int hxId = rs.getInt("hx_id");
                    if (hxId > 0) {
                        HangXe hx = new HangXe();
                        hx.setId(hxId);
                        hx.setTenHang(rs.getString("ten_hang"));
                        pt.setHangXe(hx);
                    }
                } catch (SQLException ignored) {}

                try {
                    int khId = rs.getInt("kh_id");
                    if (khId > 0) {
                        KhachHang kh = new KhachHang();
                        kh.setId(khId);
                        kh.setMaKhachHang(rs.getString("ma_khach_hang"));
                        kh.setHoTen(rs.getString("ho_ten"));
                        kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                        try {
                            kh.setGioiTinh(rs.getString("gioi_tinh"));
                        } catch (SQLException ignored) {}
                        pt.setKhachHang(kh);
                    }
                } catch (SQLException ignored) {}

                ptn.setPhuongTien(pt);
            }
        } catch (SQLException ignored) {}

        // Ánh xạ Nhân Viên Tiếp Nhận
        try {
            int nvId = rs.getInt("nv_id");
            if (nvId > 0) {
                NhanVien nv = new NhanVien();
                nv.setId(nvId);
                nv.setMaNhanVien(rs.getString("ma_nhan_vien"));
                nv.setHoTen(rs.getString("nv_ho_ten"));
                ptn.setNhanVienTiepNhan(nv);
            }
        } catch (SQLException ignored) {}

        return ptn;
    }
}
