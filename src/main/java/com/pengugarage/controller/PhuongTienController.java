package com.pengugarage.controller;

import com.pengugarage.dao.HangXeDAO;
import com.pengugarage.dao.PhuongTienDAO;
import com.pengugarage.model.entity.HangXe;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.model.enums.NhienLieu;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho Giao diện Quản Lý Phương Tiện (phuong-tien-view.fxml) phía Quản Lý.
 * 
 * Lưu ý: Theo yêu cầu, các hàm nghiệp vụ (thêm, sửa, xóa, tìm kiếm, xuất file...)
 * được để trống để người dùng tự do bổ sung code sau. Giao diện được khởi tạo sẵn sàng hiển thị.
 */
public class PhuongTienController {

    // =========================================================================
    // 1. FXML CONTROLS
    // =========================================================================

    // Top action & Notification
    @FXML private Label thongBaoLabel;
    @FXML private Label tongSoLabel;

    // KPI Cards
    @FXML private Label lblTongSoXeKpi;
    @FXML private Label lblXeDangSuaKpi;
    @FXML private Label lblHangXePhoBienKpi;
    @FXML private Label lblKhachHangKpi;

    // Toolbar Tìm kiếm & Lọc
    @FXML private TextField timKiemField;
    @FXML private ComboBox<String> locHangXeCombo;
    @FXML private ComboBox<String> locNhienLieuCombo;
    @FXML private ComboBox<String> locNamSxCombo;

    // TableView Phương Tiện
    @FXML private TableView<PhuongTien> phuongTienTable;
    @FXML private TableColumn<PhuongTien, String> colMaPT;
    @FXML private TableColumn<PhuongTien, String> colBienSo;
    @FXML private TableColumn<PhuongTien, String> colHangXe;
    @FXML private TableColumn<PhuongTien, String> colMauXe;
    @FXML private TableColumn<PhuongTien, String> colMauSac;
    @FXML private TableColumn<PhuongTien, Integer> colNamSX;
    @FXML private TableColumn<PhuongTien, String> colNhienLieu;
    @FXML private TableColumn<PhuongTien, String> colChuXe;
    @FXML private TableColumn<PhuongTien, String> colSDT;

    // Form Chi Tiết Phương Tiện (Bên Phải)
    @FXML private Label lblTrangThaiForm;
    @FXML private Label lblBadgeBienSo;
    @FXML private TextField maPTField;
    @FXML private TextField bienSoField;
    @FXML private ComboBox<HangXe> hangXeCombo;
    @FXML private TextField mauXeField;
    @FXML private TextField mauSacField;
    @FXML private TextField namSxField;
    @FXML private ComboBox<NhienLieu> nhienLieuCombo;

    // Thông tin Khách hàng (Chủ sở hữu)
    @FXML private TextField chuXeField;
    @FXML private TextField sdtChuXeField;
    @FXML private TextField diaChiChuXeField;

    // DAOs
    private final HangXeDAO hangXeDAO = new HangXeDAO();
    private final PhuongTienDAO phuongTienDAO = new PhuongTienDAO();

    private final ObservableList<PhuongTien> danhSachPhuongTien = FXCollections.observableArrayList();

    // =========================================================================
    // 2. KHỞI TẠO GIAO DIỆN (INITIALIZE)
    // =========================================================================

    @FXML
    public void initialize() {
        khoiTaoBangPhuongTien();
        khoiTaoCacCombobox();
        taiDuLieuHienThiGiaoDien();
        langNgheChonDongBang();
    }

    private void khoiTaoBangPhuongTien() {
        colMaPT.setCellValueFactory(new PropertyValueFactory<>("maPhuongTien"));
        colBienSo.setCellValueFactory(new PropertyValueFactory<>("bienSoXe"));
        colMauXe.setCellValueFactory(new PropertyValueFactory<>("mauXe"));
        colMauSac.setCellValueFactory(new PropertyValueFactory<>("mauSacXe"));
        colNamSX.setCellValueFactory(new PropertyValueFactory<>("namSanXuat"));

        colHangXe.setCellValueFactory(cellData -> {
            PhuongTien pt = cellData.getValue();
            if (pt != null && pt.getHangXe() != null) {
                return new SimpleStringProperty(pt.getHangXe().getTenHang());
            }
            return new SimpleStringProperty("-");
        });

        colNhienLieu.setCellValueFactory(cellData -> {
            PhuongTien pt = cellData.getValue();
            if (pt != null && pt.getNhienLieu() != null) {
                return new SimpleStringProperty(pt.getNhienLieu().name());
            }
            return new SimpleStringProperty("-");
        });

        colChuXe.setCellValueFactory(cellData -> {
            PhuongTien pt = cellData.getValue();
            if (pt != null && pt.getKhachHang() != null) {
                return new SimpleStringProperty(pt.getKhachHang().getDanhXungVaHoTen());
            }
            return new SimpleStringProperty("-");
        });

        colSDT.setCellValueFactory(cellData -> {
            PhuongTien pt = cellData.getValue();
            if (pt != null && pt.getKhachHang() != null && pt.getKhachHang().getSoDienThoai() != null) {
                return new SimpleStringProperty(pt.getKhachHang().getSoDienThoai());
            }
            return new SimpleStringProperty("-");
        });

        phuongTienTable.setItems(danhSachPhuongTien);
    }

    private void khoiTaoCacCombobox() {
        // Hãng xe
        List<HangXe> dsHangXe = hangXeDAO.layTatCa();
        if (hangXeCombo != null) {
            hangXeCombo.setItems(FXCollections.observableArrayList(dsHangXe));
        }

        // Lọc Hãng xe
        if (locHangXeCombo != null) {
            ObservableList<String> dsTenHang = FXCollections.observableArrayList("Tất cả hãng xe");
            for (HangXe hx : dsHangXe) {
                dsTenHang.add(hx.getTenHang());
            }
            locHangXeCombo.setItems(dsTenHang);
            locHangXeCombo.getSelectionModel().selectFirst();
        }

        // Nhiên liệu
        if (nhienLieuCombo != null) {
            nhienLieuCombo.setItems(FXCollections.observableArrayList(NhienLieu.values()));
        }

        // Lọc Nhiên liệu
        if (locNhienLieuCombo != null) {
            locNhienLieuCombo.setItems(FXCollections.observableArrayList(
                    "Tất cả nhiên liệu", "Xăng", "Dầu Diesel", "Điện", "Hybrid"
            ));
            locNhienLieuCombo.getSelectionModel().selectFirst();
        }

        // Lọc Năm sản xuất
        if (locNamSxCombo != null) {
            locNamSxCombo.setItems(FXCollections.observableArrayList(
                    "Tất cả các năm", "2024", "2023", "2022", "2021", "2020", "Trước 2020"
            ));
            locNamSxCombo.getSelectionModel().selectFirst();
        }
    }

    private void taiDuLieuHienThiGiaoDien() {
        danhSachPhuongTien.clear();

        // Tải danh sách xe từ CSDL hiển thị lên bảng
        String sql = """
            SELECT pt.id, pt.ma_phuong_tien, pt.bien_so_xe, pt.mau_xe, pt.mau_sac_xe, 
                   pt.nam_san_xuat, pt.nhien_lieu,
                   hx.id AS hx_id, hx.ten_hang,
                   kh.id AS kh_id, kh.ma_khach_hang, kh.ho_ten, kh.so_dien_thoai, kh.dia_chi, kh.gioi_tinh
            FROM phuong_tien pt
            LEFT JOIN hang_xe hx ON pt.hang_xe_id = hx.id
            LEFT JOIN khach_hang kh ON pt.khach_hang_id = kh.id
            ORDER BY pt.id ASC
        """;

        try (Connection conn = com.pengugarage.util.CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhuongTien pt = new PhuongTien();
                pt.setId(rs.getInt("id"));
                pt.setMaPhuongTien(rs.getString("ma_phuong_tien"));
                pt.setBienSoXe(rs.getString("bien_so_xe"));
                pt.setMauXe(rs.getString("mau_xe"));
                pt.setMauSacXe(rs.getString("mau_sac_xe"));
                pt.setNamSanXuat(rs.getInt("nam_san_xuat"));

                String nhienLieuStr = rs.getString("nhien_lieu");
                if (nhienLieuStr != null) {
                    try {
                        pt.setNhienLieu(NhienLieu.valueOf(nhienLieuStr.toUpperCase()));
                    } catch (IllegalArgumentException ignored) {
                        pt.setNhienLieu(NhienLieu.XANG);
                    }
                }

                if (rs.getObject("hx_id") != null) {
                    HangXe hx = new HangXe();
                    hx.setId(rs.getInt("hx_id"));
                    hx.setTenHang(rs.getString("ten_hang"));
                    pt.setHangXe(hx);
                }

                if (rs.getObject("kh_id") != null) {
                    KhachHang kh = new KhachHang();
                    kh.setId(rs.getInt("kh_id"));
                    kh.setMaKhachHang(rs.getString("ma_khach_hang"));
                    kh.setHoTen(rs.getString("ho_ten"));
                    kh.setSoDienThoai(rs.getString("so_dien_thoai"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    kh.setGioiTinh(rs.getString("gioi_tinh"));
                    pt.setKhachHang(kh);
                }

                danhSachPhuongTien.add(pt);
            }
        } catch (Exception e) {
            // Không làm gián đoạn hiển thị UI nếu có ngoại lệ kết nối
        }

        // Cập nhật tổng số lượng
        int tong = danhSachPhuongTien.size();
        if (tongSoLabel != null) {
            tongSoLabel.setText("Tổng số: " + tong + " phương tiện");
        }
        if (lblTongSoXeKpi != null) {
            lblTongSoXeKpi.setText(tong + " xe");
        }

        // Tự động chọn dòng đầu tiên nếu có dữ liệu
        if (!danhSachPhuongTien.isEmpty()) {
            phuongTienTable.getSelectionModel().selectFirst();
            hienThiChiTietXe(danhSachPhuongTien.get(0));
        }
    }

    private void langNgheChonDongBang() {
        phuongTienTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, pt) -> {
            if (pt != null) {
                hienThiChiTietXe(pt);
            }
        });
    }

    private void hienThiChiTietXe(PhuongTien pt) {
        if (pt == null) return;

        if (lblBadgeBienSo != null) {
            lblBadgeBienSo.setText(pt.getBienSoXe() != null ? pt.getBienSoXe() : "CHƯA CHỌN");
        }
        if (maPTField != null) maPTField.setText(pt.getMaPhuongTien());
        if (bienSoField != null) bienSoField.setText(pt.getBienSoXe());
        if (mauXeField != null) mauXeField.setText(pt.getMauXe());
        if (mauSacField != null) mauSacField.setText(pt.getMauSacXe());
        if (namSxField != null) namSxField.setText(String.valueOf(pt.getNamSanXuat()));

        if (nhienLieuCombo != null && pt.getNhienLieu() != null) {
            nhienLieuCombo.getSelectionModel().select(pt.getNhienLieu());
        }

        if (hangXeCombo != null && pt.getHangXe() != null) {
            for (HangXe hx : hangXeCombo.getItems()) {
                if (hx.getId() == pt.getHangXe().getId()) {
                    hangXeCombo.getSelectionModel().select(hx);
                    break;
                }
            }
        }

        if (pt.getKhachHang() != null) {
            if (chuXeField != null) chuXeField.setText(pt.getKhachHang().getDanhXungVaHoTen());
            if (sdtChuXeField != null) sdtChuXeField.setText(pt.getKhachHang().getSoDienThoai());
            if (diaChiChuXeField != null) diaChiChuXeField.setText(pt.getKhachHang().getDiaChi());
        } else {
            if (chuXeField != null) chuXeField.clear();
            if (sdtChuXeField != null) sdtChuXeField.clear();
            if (diaChiChuXeField != null) diaChiChuXeField.clear();
        }

        if (lblTrangThaiForm != null) {
            lblTrangThaiForm.setText("Đang xem chi tiết");
        }
    }

    // =========================================================================
    // 3. CÁC HÀM XỬ LÝ SỰ KIỆN (ĐỂ TRỐNG THEO YÊU CẦU ĐỂ BỔ SUNG LOGIC SAU)
    // =========================================================================

    @FXML
    public void onThemMoiClick() {
        // TODO: Hàm thêm mới phương tiện (để trống theo yêu cầu)
    }

    @FXML
    public void onCapNhatClick() {
        // TODO: Hàm cập nhật thông tin phương tiện (để trống theo yêu cầu)
    }

    @FXML
    public void onXoaClick() {
        // TODO: Hàm xóa hồ sơ phương tiện (để trống theo yêu cầu)
    }

    @FXML
    public void onXoaTrangClick() {
        // TODO: Hàm xóa trắng form nhập liệu (để trống theo yêu cầu)
    }

    @FXML
    public void onXemLichSuClick() {
        // TODO: Hàm xem lịch sử tiếp nhận và sửa chữa của xe (để trống theo yêu cầu)
    }

    @FXML
    public void onTimKiemClick() {
        // TODO: Hàm tìm kiếm và lọc phương tiện (để trống theo yêu cầu)
    }

    @FXML
    public void onLamMoiClick() {
        // TODO: Hàm làm mới danh sách phương tiện (để trống theo yêu cầu)
    }

    @FXML
    public void onXuatExcelClick() {
        // TODO: Hàm xuất danh sách phương tiện ra Excel (để trống theo yêu cầu)
    }
}

