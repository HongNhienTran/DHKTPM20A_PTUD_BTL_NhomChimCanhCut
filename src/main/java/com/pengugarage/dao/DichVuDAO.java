package com.pengugarage.dao;

import com.pengugarage.model.entity.DichVu;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    public List<DichVu> layTatCa(){
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM `dich_vu` ORDER BY `id` ASC";
        try(Connection conn = CoSoDuLieuUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                list.add(mapResultSetToDichVu(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private DichVu mapResultSetToDichVu(ResultSet rs) throws SQLException {
        DichVu dichVu = new DichVu();
        dichVu.setId(rs.getInt("id"));
        dichVu.setMaDichVu(rs.getString("ma_dich_vu"));
        dichVu.setTenDichVu(rs.getString("ten_dich_vu"));
        dichVu.setMoTa(rs.getString("mo_ta"));
        dichVu.setDonGia(rs.getDouble("don_gia"));
        dichVu.setThueVAT(rs.getDouble("thue_vat"));
        dichVu.setTrangThai(rs.getBoolean("trang_thai"));
        return dichVu;
    }
}
