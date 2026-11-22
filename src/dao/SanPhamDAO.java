package dao;

import util.ConnectDB;
import entity.DanhMucSanPham;
import entity.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SanPhamDAO {

    public String layMaLonNhatTheoTienTo(String tienTo) {
        String sql = "SELECT MAX(ma_san_pham) FROM san_pham WHERE ma_san_pham LIKE ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tienTo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn mã sản phẩm lớn nhất", e);
        }
    }

    /**
     * Giữ lại phương thức cũ để tương thích ngược và phục vụ báo cáo/kiểm tra.
     * Tại các màn hình quản lý và tra cứu sản phẩm đã thay thế bằng {@link #layDanhSachQuanLyPhanTrang}.
     */
    public List<SanPham> layTatCa() {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "ORDER BY sp.ma_san_pham";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm", e);
        }
        return ds;
    }

    public List<SanPham> layDanhSachQuanLyPhanTrang(String tuKhoa, String maDanhMuc,
                                                     int cheDoTonKho, Boolean trangThai,
                                                     int offset, int limit) {
        List<SanPham> ds = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienLoc(tuKhoa, maDanhMuc, cheDoTonKho, trangThai, params);
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                where +
                " ORDER BY sp.ma_san_pham ASC LIMIT ? OFFSET ?";
        params.add(Math.max(1, limit));
        params.add(Math.max(0, offset));

        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm phân trang", e);
        }
        return ds;
    }

    public int demTheoDieuKien(String tuKhoa, String maDanhMuc, int cheDoTonKho, Boolean trangThai) {
        List<Object> params = new ArrayList<>();
        String where = xayDungDieuKienLoc(tuKhoa, maDanhMuc, cheDoTonKho, trangThai, params);
        String sql = "SELECT COUNT(*) FROM san_pham sp " +
                "JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " + where;
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganThamSo(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm số lượng sản phẩm", e);
        }
    }

    private String xayDungDieuKienLoc(String tuKhoa, String maDanhMuc, int cheDoTonKho,
                                      Boolean trangThai, List<Object> params) {
        StringBuilder where = new StringBuilder("WHERE 1=1");
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            String pattern = "%" + tuKhoa.trim().toLowerCase() + "%";
            where.append(" AND (LOWER(sp.ma_san_pham) LIKE ? OR LOWER(sp.ten_san_pham) LIKE ? ")
                 .append("OR LOWER(COALESCE(sp.don_vi_tinh, '')) LIKE ?)");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (maDanhMuc != null && !maDanhMuc.isBlank()) {
            where.append(" AND sp.ma_danh_muc = ?");
            params.add(maDanhMuc.trim());
        }
        if (cheDoTonKho == 1) {
            where.append(" AND sp.so_luong_ton > 0");
        } else if (cheDoTonKho == 2) {
            where.append(" AND sp.so_luong_ton > 0 AND sp.so_luong_ton <= GREATEST(5, sp.so_luong_ton_toi_thieu)");
        } else if (cheDoTonKho == 3) {
            where.append(" AND sp.so_luong_ton = 0");
        }
        if (trangThai != null) {
            where.append(" AND sp.trang_thai = ?");
            params.add(trangThai ? 1 : 0);
        }
        return where.toString();
    }

    private void ganThamSo(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object val = params.get(i);
            if (val instanceof String s) {
                ps.setString(i + 1, s);
            } else if (val instanceof Integer n) {
                ps.setInt(i + 1, n);
            }
        }
    }

    public List<SanPham> timTheoDanhMuc(String maDanhMuc) {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.ma_danh_muc = ? ORDER BY sp.ma_san_pham";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maDanhMuc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm sản phẩm theo danh mục", e);
        }
        return ds;
    }

    public List<SanPham> layDanhSachBanChay(int gioiHan) {
        return layDanhSachPhanTrang("BAN_CHAY", null, 0, gioiHan <= 0 ? 30 : gioiHan);
    }

    public List<SanPham> layDanhSachSanPhamMoi(int gioiHan) {
        return layDanhSachPhanTrang("SAN_PHAM_MOI", null, 0, gioiHan <= 0 ? 30 : gioiHan);
    }

    public List<SanPham> layDanhSachPhanTrang(String cheDo, String maDanhMuc, int offset, int limit) {
        return layDanhSachPhanTrang(cheDo, maDanhMuc, null, offset, limit);
    }

    public List<SanPham> layDanhSachPhanTrang(String cheDo, String maDanhMuc, String tuKhoa,
                                              int offset, int limit) {
        List<SanPham> ds = new ArrayList<>();
        String baseSelect = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc ";

        boolean locDanhMuc = "DANH_MUC".equals(cheDo) && maDanhMuc != null && !maDanhMuc.isBlank();
        List<String> tokens = tachTuKhoaTimKiem(tuKhoa);
        String dieuKienTuKhoa = taoDieuKienTuKhoaTuongDoi(tokens);

        StringBuilder sql = new StringBuilder(baseSelect);
        if ("BAN_CHAY".equals(cheDo)) {
            sql.append("LEFT JOIN ( " +
                    "    SELECT ma_san_pham, SUM(so_luong) AS tong_ban " +
                    "    FROM chi_tiet_hoa_don " +
                    "    WHERE ma_san_pham IS NOT NULL " +
                    "    GROUP BY ma_san_pham " +
                    ") ban ON sp.ma_san_pham = ban.ma_san_pham ");
        }
        sql.append("WHERE sp.trang_thai = 1 ");
        if (locDanhMuc) {
            sql.append("AND sp.ma_danh_muc = ? ");
        }
        sql.append(dieuKienTuKhoa);
        if ("BAN_CHAY".equals(cheDo)) {
            sql.append("ORDER BY COALESCE(ban.tong_ban, 0) DESC, sp.so_luong_ton ASC, sp.ma_san_pham ASC ");
        } else if ("SAN_PHAM_MOI".equals(cheDo)) {
            sql.append("ORDER BY sp.ma_san_pham DESC ");
        } else {
            sql.append("ORDER BY sp.ma_san_pham ASC ");
        }
        sql.append("LIMIT ? OFFSET ?");

        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (locDanhMuc) {
                ps.setString(idx++, maDanhMuc.trim());
            }
            idx = ganThamSoTuKhoaTuongDoi(ps, idx, tokens);
            ps.setInt(idx++, Math.max(1, limit));
            ps.setInt(idx, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm phân trang", e);
        }
        return ds;
    }

    public int demTongSanPhamDangKinhDoanh(String maDanhMuc) {
        return demTongSanPhamDangKinhDoanh(maDanhMuc, null);
    }

    public int demTongSanPhamDangKinhDoanh(String maDanhMuc, String tuKhoa) {
        boolean locDanhMuc = maDanhMuc != null && !maDanhMuc.isBlank();
        List<String> tokens = tachTuKhoaTimKiem(tuKhoa);
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM san_pham sp " +
                "JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.trang_thai = 1 ");
        if (locDanhMuc) {
            sql.append("AND sp.ma_danh_muc = ? ");
        }
        sql.append(taoDieuKienTuKhoaTuongDoi(tokens));

        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (locDanhMuc) {
                ps.setString(idx++, maDanhMuc.trim());
            }
            ganThamSoTuKhoaTuongDoi(ps, idx, tokens);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đếm tổng số sản phẩm", e);
        }
    }

    public List<SanPham> timKiemNhanh(String tuKhoa, int gioiHan) {
        List<SanPham> ds = new ArrayList<>();
        List<String> tokens = tachTuKhoaTimKiem(tuKhoa);
        if (tokens.isEmpty()) {
            return ds;
        }
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.trang_thai = 1 " + taoDieuKienTuKhoaTuongDoi(tokens) +
                "ORDER BY CASE WHEN LOWER(sp.ma_san_pham) = ? THEN 0 " +
                "WHEN REPLACE(LOWER(sp.ten_san_pham), 'đ', 'd') LIKE ? THEN 1 ELSE 2 END, " +
                "sp.ma_san_pham ASC LIMIT ?";
        String tuKhoaChuan = chuanHoaKhongDau(tuKhoa);
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = ganThamSoTuKhoaTuongDoi(ps, 1, tokens);
            ps.setString(idx++, tuKhoa.trim().toLowerCase(Locale.ROOT));
            ps.setString(idx++, tuKhoaChuan + "%");
            ps.setInt(idx, Math.max(1, gioiHan));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm kiếm nhanh sản phẩm", e);
        }
        return ds;
    }

    private static List<String> tachTuKhoaTimKiem(String tuKhoa) {
        List<String> tokens = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.isBlank()) {
            return tokens;
        }
        for (String phan : tuKhoa.trim().split("\\s+")) {
            String daChuan = chuanHoaKhongDau(phan);
            if (!daChuan.isEmpty()) {
                tokens.add(daChuan);
            }
        }
        return tokens;
    }

    private static String chuanHoaKhongDau(String input) {
        if (input == null) {
            return "";
        }
        String lower = input.trim().toLowerCase(Locale.ROOT).replace('đ', 'd');
        String nfd = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}+", "");
    }

    private static String taoDieuKienTuKhoaTuongDoi(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            sb.append("AND REPLACE(LOWER(CONCAT_WS(' ', sp.ma_san_pham, sp.ten_san_pham, dm.ten_danh_muc, sp.don_vi_tinh)), 'đ', 'd') LIKE ? ");
        }
        return sb.toString();
    }

    private static int ganThamSoTuKhoaTuongDoi(PreparedStatement ps, int startIndex, List<String> tokens) throws SQLException {
        int idx = startIndex;
        if (tokens != null) {
            for (String token : tokens) {
                ps.setString(idx++, "%" + token + "%");
            }
        }
        return idx;
    }

    public SanPham timTheoMa(String maSanPham) {
        String sql = "SELECT sp.ma_san_pham, sp.ten_san_pham, sp.don_vi_tinh, sp.so_luong_ton, " +
                "sp.so_luong_ton_toi_thieu, sp.gia_nhap, sp.don_gia_ban, sp.thue_vat, " +
                "sp.trang_thai AS sp_trang_thai, sp.anh_san_pham, " +
                "dm.ma_danh_muc, dm.ten_danh_muc, dm.mo_ta, dm.trang_thai AS dm_trang_thai " +
                "FROM san_pham sp JOIN danh_muc_san_pham dm ON sp.ma_danh_muc = dm.ma_danh_muc " +
                "WHERE sp.ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm sản phẩm theo mã: " + maSanPham, e);
        }
        return null;
    }

    public boolean them(SanPham sp) {
        String sql = "INSERT INTO san_pham (ma_san_pham, ten_san_pham, don_vi_tinh, so_luong_ton, " +
                "so_luong_ton_toi_thieu, gia_nhap, don_gia_ban, thue_vat, trang_thai, anh_san_pham, ma_danh_muc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSanPham());
            ps.setString(2, sp.getTenSanPham());
            ps.setString(3, sp.getDonViTinh());
            ps.setInt(4, sp.getSoLuongTon());
            ps.setInt(5, sp.getSoLuongTonToiThieu());
            ps.setDouble(6, sp.getGiaNhap());
            ps.setDouble(7, sp.getDonGiaBan());
            ps.setDouble(8, sp.getThueVAT());
            ps.setBoolean(9, sp.isTrangThai());
            ps.setString(10, sp.getAnhSanPham());
            ps.setString(11, sp.getDanhMucSanPham().getMaDanhMuc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm sản phẩm", e);
        }
    }

    public boolean capNhat(SanPham sp) {
        String sql = "UPDATE san_pham SET ten_san_pham = ?, don_vi_tinh = ?, so_luong_ton = ?, " +
                "so_luong_ton_toi_thieu = ?, gia_nhap = ?, don_gia_ban = ?, thue_vat = ?, " +
                "trang_thai = ?, anh_san_pham = ?, ma_danh_muc = ? WHERE ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setString(2, sp.getDonViTinh());
            ps.setInt(3, sp.getSoLuongTon());
            ps.setInt(4, sp.getSoLuongTonToiThieu());
            ps.setDouble(5, sp.getGiaNhap());
            ps.setDouble(6, sp.getDonGiaBan());
            ps.setDouble(7, sp.getThueVAT());
            ps.setBoolean(8, sp.isTrangThai());
            ps.setString(9, sp.getAnhSanPham());
            ps.setString(10, sp.getDanhMucSanPham().getMaDanhMuc());
            ps.setString(11, sp.getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật sản phẩm", e);
        }
    }

    public boolean capNhatSoLuongTon(Connection con, String maSanPham, int soLuongTonMoi) throws SQLException {
        String sql = "UPDATE san_pham SET so_luong_ton = ? WHERE ma_san_pham = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuongTonMoi);
            ps.setString(2, maSanPham);
            return ps.executeUpdate() > 0;
        }
    }
    public boolean truSoLuongTon(Connection con, String maSanPham, int soLuongBan)
            throws SQLException {
        if (soLuongBan <= 0) {
            throw new IllegalArgumentException("Số lượng bán phải lớn hơn 0");
        }
        String sql = """
        UPDATE san_pham
        SET so_luong_ton = so_luong_ton - ?
        WHERE ma_san_pham = ?
          AND so_luong_ton >= ?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, soLuongBan);
            ps.setString(2, maSanPham);
            ps.setInt(3, soLuongBan);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatSoLuongTon(String maSanPham, int soLuongTonMoi) {
        try (Connection con = ConnectDB.ketNoi()) {
            return capNhatSoLuongTon(con, maSanPham, soLuongTonMoi);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật tồn kho sản phẩm", e);
        }
    }

    public boolean xoa(String maSanPham) {
        String sql = "DELETE FROM san_pham WHERE ma_san_pham = ?";
        try (Connection con = ConnectDB.ketNoi();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa sản phẩm", e);
        }
    }

    private SanPham mapRow(ResultSet rs) throws SQLException {
        DanhMucSanPham dm = new DanhMucSanPham(
                rs.getString("ma_danh_muc"),
                rs.getString("ten_danh_muc"),
                rs.getString("mo_ta"),
                rs.getBoolean("dm_trang_thai")
        );
        return new SanPham(
                rs.getString("ma_san_pham"),
                rs.getString("ten_san_pham"),
                rs.getString("don_vi_tinh"),
                rs.getInt("so_luong_ton"),
                rs.getInt("so_luong_ton_toi_thieu"),
                rs.getDouble("gia_nhap"),
                rs.getDouble("don_gia_ban"),
                rs.getDouble("thue_vat"),
                rs.getBoolean("sp_trang_thai"),
                rs.getString("anh_san_pham"),
                dm
        );
    }
}
