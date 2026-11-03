package com.pengugarage.dao;

import com.pengugarage.model.entity.ChiTietHoaDon;
import com.pengugarage.model.entity.DichVu;
import com.pengugarage.model.entity.HoaDon;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.model.enums.DonViTinh;
import com.pengugarage.model.enums.LoaiHangMuc;
import com.pengugarage.model.enums.LoaiHoaDon;
import com.pengugarage.model.enums.PhuongThucThanhToan;
import com.pengugarage.model.enums.TrangThaiThanhToan;
import com.pengugarage.util.CoSoDuLieuUtil; //

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Hóa Đơn (HoaDon) và Chi Tiết Hóa Đơn (ChiTietHoaDon).
 * Phục vụ:
 * - Màn hình Danh sách hóa đơn (quản lý toàn bộ)
 * - Màn hình Bán hàng (lưu tạm / in tạm tính, trạng thái CHUA_THANH_TOAN)
 * - Màn hình Thu ngân (xác nhận thanh toán, hủy hóa đơn tạm)
 */
public class HoaDonDAO {

    // =====================================================================
    // SQL DÙNG CHUNG
    // =====================================================================
    private static final String SELECT_HOA_DON =
            "SELECT hd.*, "
                    + "       kh.ma_khach_hang AS kh_ma, kh.ho_ten AS kh_ho_ten, "
                    + "       kh.so_dien_thoai AS kh_sdt, kh.dia_chi AS kh_dia_chi, "
                    + "       nv.ma_nhan_vien AS nv_ma, nv.ho_ten AS nv_ho_ten "
                    + "FROM hoa_don hd "
                    + "LEFT JOIN khach_hang kh ON hd.khach_hang_id = kh.id "
                    + "LEFT JOIN nhan_vien nv ON hd.nhan_vien_lap_id = nv.id "
                    + "LEFT JOIN phieu_tiep_nhan ptn ON hd.phieu_tiep_nhan_id = ptn.id "
                    + "LEFT JOIN phuong_tien pt ON ptn.phuong_tien_id = pt.id ";

    // =====================================================================
    // THÊM HÓA ĐƠN / CHI TIẾT
    // =====================================================================

    /**
     * Thêm mới một hóa đơn (tự mở và đóng kết nối).
     *
     * @return ID tự sinh của hóa đơn vừa tạo (hoặc -1 nếu thất bại)
     */
    public int themHoaDon(HoaDon hd) {
        try (Connection con = CoSoDuLieuUtil.getConnection()) {
            return themHoaDon(con, hd);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Phiên bản dùng chung connection để chạy trong transaction. */
    public int themHoaDon(Connection con, HoaDon hd) throws SQLException {
        String sql = "INSERT INTO hoa_don (ma_hoa_don, loai_hoa_don, khach_hang_id, phieu_tiep_nhan_id, "
                + "nhan_vien_lap_id, ma_khuyen_mai_id, thoi_gian_lap, tong_tien_hang, tien_khuyen_mai, "
                + "tien_thue, tong_thanh_toan, trang_thai_thanh_toan, phuong_thuc_thanh_toan, "
                + "thoi_gian_thanh_toan, ten_khach_le, sdt_khach_le, ghi_chu) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, hd.getMaHoaDon());
            ps.setString(2, hd.getLoaiHoaDon().name());
            ps.setObject(3, hd.getKhachHangId(), Types.INTEGER);
            ps.setObject(4, hd.getPhieuTiepNhanId(), Types.INTEGER);
            ps.setInt(5, hd.getNhanVienLapId());
            ps.setObject(6, hd.getMaKhuyenMaiId(), Types.INTEGER);
            ps.setTimestamp(7, Timestamp.valueOf(hd.getThoiGianLap()));
            ps.setDouble(8, hd.getTongTienHang());
            ps.setDouble(9, hd.getTienKhuyenMai());
            ps.setDouble(10, hd.getTienThue());
            ps.setDouble(11, hd.getTongThanhToan());
            ps.setString(12, hd.getTrangThaiThanhToan().name());
            ps.setString(13, hd.getPhuongThucThanhToan() != null
                    ? hd.getPhuongThucThanhToan().name() : null);
            ps.setTimestamp(14, hd.getThoiGianThanhToan() != null
                    ? Timestamp.valueOf(hd.getThoiGianThanhToan()) : null);
            ps.setString(15, hd.getTenKhachLe());
            ps.setString(16, hd.getSdtKhachLe());
            ps.setString(17, hd.getGhiChu());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /**
     * Thêm một dòng chi tiết vào hóa đơn (tự mở và đóng kết nối).
     *
     * @return true nếu thêm thành công
     */
    public boolean themChiTietHoaDon(ChiTietHoaDon ct) {
        try (Connection con = CoSoDuLieuUtil.getConnection()) {
            return themChiTietHoaDon(con, ct);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Phiên bản dùng chung connection để chạy trong transaction. */
    public boolean themChiTietHoaDon(Connection con, ChiTietHoaDon ct) throws SQLException {
        String sql = "INSERT INTO chi_tiet_hoa_don (hoa_don_id, loai_hang_muc, dich_vu_id, san_pham_id, "
                + "so_luong, don_gia, thue_vat, tien_thue, thanh_tien, thanh_tien_sau_thue, ghi_chu) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ct.getHoaDonId());
            ps.setString(2, ct.getLoaiHangMuc().name());
            ps.setObject(3, ct.getDichVuId(), Types.INTEGER);
            ps.setObject(4, ct.getSanPhamId(), Types.INTEGER);
            ps.setInt(5, ct.getSoLuong());
            ps.setDouble(6, ct.getDonGia());
            ps.setDouble(7, ct.getThueVAT());
            ps.setDouble(8, ct.getTienThue());
            ps.setDouble(9, ct.getThanhTien());
            ps.setDouble(10, ct.getThanhTienSauThue());
            ps.setString(11, ct.getGhiChu());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lưu hóa đơn kèm toàn bộ chi tiết trong MỘT transaction
     * (dùng cho "In tạm tính" và cho thanh toán trực tiếp).
     * Lỗi ở bất kỳ bước nào thì rollback, không để lại hóa đơn thiếu chi tiết.
     *
     * @return ID hóa đơn, hoặc -1 nếu thất bại
     */
    public int luuHoaDonKemChiTiet(HoaDon hd) {
        Connection con = null;
        try {
            con = CoSoDuLieuUtil.getConnection();
            con.setAutoCommit(false);

            int id = themHoaDon(con, hd);
            if (id <= 0) {
                throw new SQLException("Không lấy được ID hóa đơn vừa tạo");
            }
            hd.setId(id);

            for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                ct.setHoaDonId(id);
                if (!themChiTietHoaDon(con, ct)) {
                    throw new SQLException("Không thể thêm chi tiết hóa đơn");
                }
            }
            con.commit();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackQuietly(con);
            return -1;
        } finally {
            closeQuietly(con);
        }
    }

    // =====================================================================
    // TRUY VẤN
    // =====================================================================

    /** Lấy toàn bộ hóa đơn, mới nhất lên đầu. */
    public List<HoaDon> layTatCaHoaDon() {
        String sql = SELECT_HOA_DON + "ORDER BY hd.thoi_gian_lap DESC, hd.id DESC";
        List<HoaDon> ds = new ArrayList<>();
        try (Connection con = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy hóa đơn theo trạng thái thanh toán (màn hình Thu ngân dùng CHUA_THANH_TOAN). */
    public List<HoaDon> layHoaDonTheoTrangThai(TrangThaiThanhToan trangThai) {
        String sql = SELECT_HOA_DON
                + "WHERE hd.trang_thai_thanh_toan = ? "
                + "ORDER BY hd.thoi_gian_lap DESC, hd.id DESC";
        List<HoaDon> ds = new ArrayList<>();
        try (Connection con = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy 1 hóa đơn theo ID, kèm danh sách chi tiết. Trả về null nếu không có. */
    public HoaDon layHoaDonTheoId(int id) {
        String sql = SELECT_HOA_DON + "WHERE hd.id = ?";
        try (Connection con = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDon hd = mapHoaDon(rs);
                    hd.setDanhSachChiTiet(layChiTietHoaDon(con, id));
                    return hd;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tìm kiếm và lọc đa tiêu chí.
     * Từ khóa khớp: mã HĐ, tên/SĐT khách hàng, tên/SĐT khách lẻ, biển số xe.
     * Mọi tham số null/rỗng nghĩa là không lọc theo tiêu chí đó.
     */
    public List<HoaDon> timKiemVaLocHoaDon(String tuKhoa, LoaiHoaDon loaiHoaDon,
                                           TrangThaiThanhToan trangThai,
                                           LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder sql = new StringBuilder(SELECT_HOA_DON).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            String like = "%" + tuKhoa.trim() + "%";
            sql.append("AND (hd.ma_hoa_don LIKE ? OR kh.ho_ten LIKE ? OR kh.so_dien_thoai LIKE ? "
                    + "OR hd.ten_khach_le LIKE ? OR hd.sdt_khach_le LIKE ? OR pt.bien_so_xe LIKE ?) ");
            for (int i = 0; i < 6; i++) {
                params.add(like);
            }
        }
        if (loaiHoaDon != null) {
            sql.append("AND hd.loai_hoa_don = ? ");
            params.add(loaiHoaDon.name());
        }
        if (trangThai != null) {
            sql.append("AND hd.trang_thai_thanh_toan = ? ");
            params.add(trangThai.name());
        }
        if (tuNgay != null) {
            sql.append("AND hd.thoi_gian_lap >= ? ");
            params.add(Timestamp.valueOf(tuNgay.atStartOfDay()));
        }
        if (denNgay != null) {
            // Nhỏ hơn 0h ngày hôm sau để bao trọn cả ngày kết thúc
            sql.append("AND hd.thoi_gian_lap < ? ");
            params.add(Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
        }
        sql.append("ORDER BY hd.thoi_gian_lap DESC, hd.id DESC");

        List<HoaDon> ds = new ArrayList<>();
        try (Connection con = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lấy chi tiết (kèm tên sản phẩm / dịch vụ, đơn vị tính) của một hóa đơn. */
    public List<ChiTietHoaDon> layChiTietHoaDon(int hoaDonId) {
        try (Connection con = CoSoDuLieuUtil.getConnection()) {
            return layChiTietHoaDon(con, hoaDonId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<ChiTietHoaDon> layChiTietHoaDon(Connection con, int hoaDonId) throws SQLException {
        String sql = "SELECT ct.*, "
                + "       sp.ma_san_pham AS sp_ma, sp.ten_san_pham AS sp_ten, sp.don_vi_tinh AS sp_dvt, "
                + "       dv.ma_dich_vu AS dv_ma, dv.ten_dich_vu AS dv_ten "
                + "FROM chi_tiet_hoa_don ct "
                + "LEFT JOIN san_pham sp ON ct.san_pham_id = sp.id "
                + "LEFT JOIN dich_vu dv ON ct.dich_vu_id = dv.id "
                + "WHERE ct.hoa_don_id = ? "
                + "ORDER BY ct.id";
        List<ChiTietHoaDon> ds = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapChiTiet(rs));
                }
            }
        }
        return ds;
    }

    // =====================================================================
    // THANH TOÁN / HỦY
    // =====================================================================

    /**
     * Xác nhận thanh toán hóa đơn tạm.
     * Chỉ cập nhật khi hóa đơn đang CHUA_THANH_TOAN, tránh thu tiền 2 lần.
     * Không đụng tới tồn kho vì kho đã trừ từ lúc thêm vào giỏ hàng.
     */
    public boolean xacNhanThanhToan(int hoaDonId, PhuongThucThanhToan phuongThucThanhToan, String ghiChu) {
        String sql = "UPDATE hoa_don SET trang_thai_thanh_toan = ?, phuong_thuc_thanh_toan = ?, "
                + "thoi_gian_thanh_toan = NOW(), ghi_chu = ? "
                + "WHERE id = ? AND trang_thai_thanh_toan = ?";
        try (Connection con = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, TrangThaiThanhToan.DA_THANH_TOAN.name());
            ps.setString(2, phuongThucThanhToan != null ? phuongThucThanhToan.name() : null);
            ps.setString(3, ghiChu);
            ps.setInt(4, hoaDonId);
            ps.setString(5, TrangThaiThanhToan.CHUA_THANH_TOAN.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hủy hóa đơn chưa thanh toán, trong một transaction:
     *  1. Khóa dòng hóa đơn và kiểm tra còn CHUA_THANH_TOAN
     *  2. Hoàn lại tồn kho (chỉ với hóa đơn BAN_HANG, vì kho đã trừ khi thêm vào giỏ)
     *  3. Xóa chi tiết rồi xóa hóa đơn
     *
     * Lưu ý: bảng hoa_don không có cột lưu lý do hủy nên lyDoHuy chỉ được ghi log.
     */
    public boolean huyHoaDon(int hoaDonId, String lyDoHuy) {
        Connection con = null;
        try {
            con = CoSoDuLieuUtil.getConnection();
            con.setAutoCommit(false);

            String loai;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT loai_hoa_don, trang_thai_thanh_toan FROM hoa_don WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, hoaDonId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()
                            || !TrangThaiThanhToan.CHUA_THANH_TOAN.name()
                            .equals(rs.getString("trang_thai_thanh_toan"))) {
                        con.rollback();
                        return false; // không tồn tại hoặc đã thanh toán
                    }
                    loai = rs.getString("loai_hoa_don");
                }
            }

            if (LoaiHoaDon.BAN_HANG.name().equals(loai)) {
                String hoanKho = "UPDATE san_pham sp "
                        + "JOIN (SELECT san_pham_id, SUM(so_luong) AS sl FROM chi_tiet_hoa_don "
                        + "      WHERE hoa_don_id = ? AND san_pham_id IS NOT NULL "
                        + "      GROUP BY san_pham_id) t ON sp.id = t.san_pham_id "
                        + "SET sp.so_luong_ton = sp.so_luong_ton + t.sl";
                try (PreparedStatement ps = con.prepareStatement(hoanKho)) {
                    ps.setInt(1, hoaDonId);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM chi_tiet_hoa_don WHERE hoa_don_id = ?")) {
                ps.setInt(1, hoaDonId);
                ps.executeUpdate();
            }
            int daXoa;
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM hoa_don WHERE id = ?")) {
                ps.setInt(1, hoaDonId);
                daXoa = ps.executeUpdate();
            }
            if (daXoa == 0) {
                con.rollback();
                return false;
            }

            con.commit();
            System.out.println("Đã hủy hóa đơn ID=" + hoaDonId
                    + (lyDoHuy != null && !lyDoHuy.isBlank() ? " - Lý do: " + lyDoHuy : ""));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            rollbackQuietly(con);
            return false;
        } finally {
            closeQuietly(con);
        }
    }

    // =====================================================================
    // MAPPER & TIỆN ÍCH
    // =====================================================================

    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon(
                rs.getInt("id"),
                rs.getString("ma_hoa_don"),
                parseEnum(LoaiHoaDon.class, rs.getString("loai_hoa_don")),
                getNullableInt(rs, "khach_hang_id"),
                getNullableInt(rs, "phieu_tiep_nhan_id"),
                rs.getInt("nhan_vien_lap_id"),
                getNullableInt(rs, "ma_khuyen_mai_id"),
                toLocalDateTime(rs.getTimestamp("thoi_gian_lap")),
                rs.getDouble("tong_tien_hang"),
                rs.getDouble("tien_khuyen_mai"),
                rs.getDouble("tien_thue"),
                rs.getDouble("tong_thanh_toan"),
                parseEnum(TrangThaiThanhToan.class, rs.getString("trang_thai_thanh_toan")),
                parseEnum(PhuongThucThanhToan.class, rs.getString("phuong_thuc_thanh_toan")),
                toLocalDateTime(rs.getTimestamp("thoi_gian_thanh_toan")),
                rs.getString("ten_khach_le"),
                rs.getString("sdt_khach_le"),
                rs.getString("ghi_chu"));

        if (hd.getKhachHangId() != null) {
            KhachHang kh = new KhachHang();
            kh.setId(hd.getKhachHangId());
            kh.setMaKhachHang(rs.getString("kh_ma"));
            kh.setHoTen(rs.getString("kh_ho_ten"));
            kh.setSoDienThoai(rs.getString("kh_sdt"));
            kh.setDiaChi(rs.getString("kh_dia_chi"));
            hd.setKhachHang(kh);
        }

        NhanVien nv = new NhanVien();
        nv.setId(hd.getNhanVienLapId());
        nv.setMaNhanVien(rs.getString("nv_ma"));
        nv.setHoTen(rs.getString("nv_ho_ten"));
        hd.setNhanVienLap(nv);

        return hd;
    }

    private ChiTietHoaDon mapChiTiet(ResultSet rs) throws SQLException {
        double donGia = rs.getDouble("don_gia");
        double thueVat = rs.getDouble("thue_vat");
        double tienThue = rs.getDouble("tien_thue");
        double thanhTien = rs.getDouble("thanh_tien");
        double sauThue = rs.getDouble("thanh_tien_sau_thue");

        ChiTietHoaDon ct = new ChiTietHoaDon(
                rs.getInt("id"),
                rs.getInt("hoa_don_id"),
                parseEnum(LoaiHangMuc.class, rs.getString("loai_hang_muc")),
                getNullableInt(rs, "dich_vu_id"),
                getNullableInt(rs, "san_pham_id"),
                rs.getInt("so_luong"),
                donGia, thueVat, tienThue, thanhTien, sauThue,
                rs.getString("ghi_chu"));

        // Gắn thông tin hiển thị (tên, đơn vị tính)
        if (ct.getSanPhamId() != null) {
            SanPham sp = new SanPham();
            sp.setId(ct.getSanPhamId());
            sp.setMaSanPham(rs.getString("sp_ma"));
            sp.setTenSanPham(rs.getString("sp_ten"));
            sp.setDonViTinh(parseEnum(DonViTinh.class, rs.getString("sp_dvt")));
            ct.setSanPham(sp);
        } else if (ct.getDichVuId() != null) {
            DichVu dv = new DichVu();
            dv.setId(ct.getDichVuId());
            dv.setMaDichVu(rs.getString("dv_ma"));
            dv.setTenDichVu(rs.getString("dv_ten"));
            ct.setDichVu(dv);
        }

        // setSanPham()/setDichVu() ghi đè giá bằng dữ liệu hiện tại của sản phẩm,
        // nên phải khôi phục lại snapshot đã lưu trong hóa đơn.
        ct.setLoaiHangMuc(parseEnum(LoaiHangMuc.class, rs.getString("loai_hang_muc")));
        ct.setDonGia(donGia);
        ct.setThueVAT(thueVat);
        ct.setTienThue(tienThue);
        ct.setThanhTien(thanhTien);
        ct.setThanhTienSauThue(sauThue);
        return ct;
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int v = rs.getInt(column);
        return rs.wasNull() ? null : v;
    }

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private static void rollbackQuietly(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private static void closeQuietly(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}