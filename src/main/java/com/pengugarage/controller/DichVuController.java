package com.pengugarage.controller;

import com.pengugarage.controller.component.ChiTietDichVuController;
import com.pengugarage.model.entity.DichVu;
import com.pengugarage.service.DichVuService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Controller quản lý Dịch Vụ Sửa Chữa Gara (M03-T01).
 * Hỗ trợ hiển thị danh sách, tìm kiếm, lọc trạng thái, tích hợp component chi tiết dạng sổ.
 */
public class DichVuController {

    // Toolbar tìm kiếm & lọc
    @FXML private TextField timKiemField;
    @FXML private ComboBox<String> locTrangThaiCombo;

    // Header & Thông báo
    @FXML private Label thongBaoLabel;
    @FXML private Label tongSoLabel;

    // Bảng danh sách dịch vụ
    @FXML private TableView<DichVu> dichVuTable;
    @FXML private TableColumn<DichVu, String> colMaDV;
    @FXML private TableColumn<DichVu, String> colTenDV;
    @FXML private TableColumn<DichVu, String> colMoTa;
    @FXML private TableColumn<DichVu, String> colDonGia;
    @FXML private TableColumn<DichVu, String> colThueVAT;
    @FXML private TableColumn<DichVu, String> colTrangThai;

    // Component Chi Tiết Dịch Vụ
    @FXML private ChiTietDichVuController chiTietDichVuController;

    private final DichVuService dichVuService = new DichVuService();
    private final ObservableList<DichVu> danhSachDichVu = FXCollections.observableArrayList();
    private FilteredList<DichVu> danhSachLoc;
    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");

    @FXML
    public void initialize() {
        cauHinhBangDichVu();
        khoiTaoBoLoc();
        taiDuLieuTuDatabase();

        // Đăng ký callback cho component chi tiết phía dưới
        if (chiTietDichVuController != null) {
            chiTietDichVuController.setOnDongCallback(() -> dichVuTable.getSelectionModel().clearSelection());
            chiTietDichVuController.setOnLuuCallback(dv -> dichVuTable.refresh());
            chiTietDichVuController.anChiTiet();
        }

        // Lắng nghe chọn dòng trong bảng để hiển thị form chi tiết phía dưới
        dichVuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (chiTietDichVuController != null) {
                if (newVal != null) {
                    chiTietDichVuController.hienThiChiTiet(newVal);
                } else {
                    chiTietDichVuController.anChiTiet();
                }
            }
        });
    }

    private void cauHinhBangDichVu() {
        colMaDV.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        colTenDV.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));

        colDonGia.setCellValueFactory(cellData -> 
            new SimpleStringProperty(tienFormat.format(cellData.getValue().getDonGia()))
        );
        colDonGia.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #f47b31;");

        colThueVAT.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getThueVAT() + "%")
        );
        colThueVAT.setStyle("-fx-alignment: CENTER; -fx-text-fill: #cbd5e1;");

        colTrangThai.setCellValueFactory(cellData -> {
            boolean tt = cellData.getValue().isTrangThai();
            return new SimpleStringProperty(tt ? "● Đang áp dụng" : "✖ Ngừng áp dụng");
        });
        colTrangThai.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Đang áp dụng")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        danhSachLoc = new FilteredList<>(danhSachDichVu, p -> true);
        dichVuTable.setItems(danhSachLoc);
    }

    private void khoiTaoBoLoc() {
        locTrangThaiCombo.setItems(FXCollections.observableArrayList(
                "Tất cả trạng thái",
                "Đang áp dụng",
                "Ngừng áp dụng"
        ));
        locTrangThaiCombo.setValue("Tất cả trạng thái");

        // Tự động lọc tức thì khi thay đổi từ khóa hoặc trạng thái
        timKiemField.textProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
        locTrangThaiCombo.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
    }

    private void taiDuLieuTuDatabase() {
        try {
            List<DichVu> ds = dichVuService.layTatCaDichVu();
            danhSachDichVu.clear();
            if (ds != null && !ds.isEmpty()) {
                danhSachDichVu.addAll(ds);
            }
            dichVuTable.getSelectionModel().clearSelection();
            if (chiTietDichVuController != null) {
                chiTietDichVuController.anChiTiet();
            }
            capNhatTongSo();
        } catch (Exception e) {
            hienThongBao("Lỗi khi tải dữ liệu dịch vụ: " + e.getMessage(), false);
        }
    }

    private void capNhatTongSo() {
        int soLuong = danhSachLoc != null ? danhSachLoc.size() : danhSachDichVu.size();
        tongSoLabel.setText("Tổng số: " + soLuong + " dịch vụ");
    }

    @FXML
    public void onTimKiemClick() {
        String keyword = timKiemField.getText() != null ? timKiemField.getText().trim().toLowerCase() : "";
        String selectedTT = locTrangThaiCombo.getValue();

        danhSachLoc.setPredicate(dv -> {
            // Lọc từ khóa
            if (!keyword.isEmpty()) {
                boolean matchMa = dv.getMaDichVu() != null && dv.getMaDichVu().toLowerCase().contains(keyword);
                boolean matchTen = dv.getTenDichVu() != null && dv.getTenDichVu().toLowerCase().contains(keyword);
                boolean matchMoTa = dv.getMoTa() != null && dv.getMoTa().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen && !matchMoTa) {
                    return false;
                }
            }

            // Lọc trạng thái
            if (selectedTT != null && !selectedTT.equals("Tất cả trạng thái")) {
                if (selectedTT.equals("Đang áp dụng") && !dv.isTrangThai()) {
                    return false;
                } else if (selectedTT.equals("Ngừng áp dụng") && dv.isTrangThai()) {
                    return false;
                }
            }

            return true;
        });

        capNhatTongSo();
        dichVuTable.getSelectionModel().clearSelection();
        if (chiTietDichVuController != null) {
            chiTietDichVuController.anChiTiet();
        }
    }

    @FXML
    public void onLamMoiClick() {
        timKiemField.clear();
        locTrangThaiCombo.setValue("Tất cả trạng thái");
        danhSachLoc.setPredicate(p -> true);
        taiDuLieuTuDatabase();
        hienThongBao("Đã làm mới danh sách dịch vụ!", true);
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin dịch vụ và thêm
    // Người thực hiện: Tân
    // =========================================================================
    @FXML
    public void onThemMoiClick() {
        // Hiển thị modal nhập thông tin dịch vụ mới (Task M03-T02 - Tân)
        DieuHuongUtil.moModal(dichVuTable,
                "/com/pengugarage/view/quanly/component/modal-them-dich-vu.fxml",
                "Pengu Garage - Thêm Dịch Vụ Mới (Task M03-T02 - Tân)");
    }


    @FXML
    public void onBackToDashboardClick() {
        DieuHuongUtil.chuyenManHinh(dichVuTable,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Quản Lý");
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        thongBaoLabel.setText(msg);
        thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #ef4444;");
    }
}
