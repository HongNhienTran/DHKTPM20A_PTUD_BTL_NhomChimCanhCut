package com.pengugarage.controller;

import com.pengugarage.controller.component.ChiTietSanPhamController;
import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.service.DanhMucService;
import com.pengugarage.service.SanPhamService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Controller quản lý Sản Phẩm & Phụ Tùng gara (M02-T01).
 * Hỗ trợ tra cứu danh sách, bộ lọc đa tiêu chí, tích hợp component xem chi tiết sản phẩm.
 */
public class SanPhamController {

    // Toolbar tìm kiếm & lọc
    @FXML private TextField timKiemField;
    @FXML private ComboBox<String> locDanhMucCombo;
    @FXML private ComboBox<String> locTonKhoCombo;
    @FXML private ComboBox<String> locTrangThaiCombo;

    // Header & Thông báo
    @FXML private Label thongBaoLabel;
    @FXML private Label tongSoLabel;

    // Bảng danh sách sản phẩm
    @FXML private TableView<SanPham> sanPhamTable;
    @FXML private TableColumn<SanPham, String> colMaSP;
    @FXML private TableColumn<SanPham, String> colTenSP;
    @FXML private TableColumn<SanPham, String> colDanhMuc;
    @FXML private TableColumn<SanPham, String> colDVT;
    @FXML private TableColumn<SanPham, String> colGiaBan;
    @FXML private TableColumn<SanPham, String> colTonKho;
    @FXML private TableColumn<SanPham, String> colTrangThai;

    // Component Chi Tiết Sản Phẩm (Tự động inject từ fx:include fx:id="chiTietSanPham")
    @FXML private ChiTietSanPhamController chiTietSanPhamController;

    private final SanPhamService sanPhamService = new SanPhamService();
    private final DanhMucService danhMucService = new DanhMucService();

    private final ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();
    private FilteredList<SanPham> danhSachLoc;
    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");
    private boolean dangCapNhatBoLoc = false;

    @FXML
    public void initialize() {
        cauHinhBangSanPham();
        khoiTaoBoLoc();
        taiDuLieuTuDatabase();

        // Đăng ký callback cho nút Đóng và nút Lưu trong component chi tiết
        if (chiTietSanPhamController != null) {
            chiTietSanPhamController.setOnDongCallback(() -> sanPhamTable.getSelectionModel().clearSelection());
            chiTietSanPhamController.setOnLuuCallback(sp -> sanPhamTable.refresh());
            chiTietSanPhamController.anChiTiet();
        }

        // Lắng nghe chọn dòng trong bảng để sổ chi tiết phía dưới ra
        sanPhamTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (chiTietSanPhamController != null) {
                if (newVal != null) {
                    chiTietSanPhamController.hienThiChiTiet(newVal);
                } else {
                    chiTietSanPhamController.anChiTiet();
                }
            }
        });
    }

    /**
     * Cấu hình các cột trong TableView sản phẩm
     */
    private void cauHinhBangSanPham() {
        colMaSP.setCellValueFactory(new PropertyValueFactory<>("maSanPham"));
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));

        colDanhMuc.setCellValueFactory(cellData -> {
            DanhMucSanPham dm = cellData.getValue().getDanhMuc();
            return new SimpleStringProperty(dm != null ? dm.getTenDanhMuc() : "Chưa phân loại");
        });

        colDVT.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));

        colGiaBan.setCellValueFactory(cellData -> 
            new SimpleStringProperty(tienFormat.format(cellData.getValue().getDonGiaBan()))
        );
        colGiaBan.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #f47b31;");

        colTonKho.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getSoLuongTon()))
        );
        colTonKho.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    SanPham sp = getTableView().getItems().get(getIndex());
                    if (sp.getSoLuongTon() <= 0) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #ef4444; -fx-font-weight: bold;"); // Hết hàng
                    } else if (sp.getSoLuongTon() <= sp.getSoLuongTonToiThieu()) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #f59e0b; -fx-font-weight: bold;"); // Tồn ít
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #10b981; -fx-font-weight: bold;"); // Ổn định
                    }
                }
            }
        });

        colTrangThai.setCellValueFactory(cellData -> {
            boolean tt = cellData.getValue().isTrangThai();
            return new SimpleStringProperty(tt ? "● Đang bán" : "✖ Ngừng bán");
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
                    if (item.contains("Đang bán")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        danhSachLoc = new FilteredList<>(danhSachSanPham, p -> true);
        sanPhamTable.setItems(danhSachLoc);
    }

    /**
     * Khởi tạo các ComboBox bộ lọc và gắn listener tự động lọc tức thì
     */
    private void khoiTaoBoLoc() {
        napBoLocTuDuLieuThat();

        // Lắng nghe sự kiện thay đổi của các bộ lọc để tự động lọc kết quả tức thì
        timKiemField.textProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
        locDanhMucCombo.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
        locTonKhoCombo.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
        locTrangThaiCombo.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
    }

    /**
     * Nạp dữ liệu thật từ CSDL vào các ComboBox bộ lọc (không fix cứng danh sách thủ công)
     */
    private void napBoLocTuDuLieuThat() {
        dangCapNhatBoLoc = true;
        try {
            String currentDM = locDanhMucCombo.getValue();
            String currentTon = locTonKhoCombo.getValue();
            String currentTT = locTrangThaiCombo.getValue();

            // 1. Lọc danh mục: Lấy toàn bộ danh mục thực tế từ Database
            Set<String> setDanhMuc = new LinkedHashSet<>();
            try {
                List<DanhMucSanPham> dsDM = danhMucService.layTatCa();
                if (dsDM != null) {
                    for (DanhMucSanPham dm : dsDM) {
                        if (dm.getTenDanhMuc() != null && !dm.getTenDanhMuc().isBlank()) {
                            setDanhMuc.add(dm.getTenDanhMuc().trim());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải danh mục từ CSDL: " + e.getMessage());
            }

            // Bổ sung các danh mục thực tế đang có trong danh sách sản phẩm (nếu có danh mục đặc thù)
            for (SanPham sp : danhSachSanPham) {
                if (sp.getDanhMuc() != null && sp.getDanhMuc().getTenDanhMuc() != null) {
                    setDanhMuc.add(sp.getDanhMuc().getTenDanhMuc().trim());
                }
            }

            locDanhMucCombo.getItems().clear();
            locDanhMucCombo.getItems().add("Tất cả danh mục");
            locDanhMucCombo.getItems().addAll(setDanhMuc);
            locDanhMucCombo.setValue((currentDM != null && locDanhMucCombo.getItems().contains(currentDM)) ? currentDM : "Tất cả danh mục");

            // 2. Lọc tồn kho
            locTonKhoCombo.getItems().clear();
            locTonKhoCombo.getItems().addAll(
                    "Tất cả tồn kho",
                    "Còn hàng (> 0)",
                    "Hết hàng (= 0)",
                    "Cảnh báo tồn ít (<= Tối thiểu)"
            );
            locTonKhoCombo.setValue((currentTon != null && locTonKhoCombo.getItems().contains(currentTon)) ? currentTon : "Tất cả tồn kho");

            // 3. Lọc trạng thái
            locTrangThaiCombo.getItems().clear();
            locTrangThaiCombo.getItems().addAll(
                    "Tất cả trạng thái",
                    "Đang kinh doanh",
                    "Ngừng kinh doanh"
            );
            locTrangThaiCombo.setValue((currentTT != null && locTrangThaiCombo.getItems().contains(currentTT)) ? currentTT : "Tất cả trạng thái");
        } finally {
            dangCapNhatBoLoc = false;
        }
    }

    /**
     * Tải dữ liệu từ database thông qua SanPhamService
     */
    private void taiDuLieuTuDatabase() {
        try {
            List<SanPham> ds = sanPhamService.layTatCaSanPham();
            danhSachSanPham.clear();
            if (ds != null && !ds.isEmpty()) {
                danhSachSanPham.addAll(ds);
            }
            sanPhamTable.getSelectionModel().clearSelection();
            if (chiTietSanPhamController != null) {
                chiTietSanPhamController.anChiTiet();
            }

            // Đồng bộ lại danh mục từ dữ liệu vừa tải
            napBoLocTuDuLieuThat();
            onTimKiemClick();
        } catch (Exception e) {
            hienThongBao("Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage(), false);
        }
    }

    /**
     * Cập nhật nhãn tổng số sản phẩm hiển thị
     */
    private void capNhatTongSo() {
        int soLuong = danhSachLoc != null ? danhSachLoc.size() : danhSachSanPham.size();
        tongSoLabel.setText("Tổng số: " + soLuong + " sản phẩm");
    }

    /**
     * Sự kiện nút Tìm Kiếm: lọc đa tiêu chí (từ khóa, danh mục, tồn kho, trạng thái)
     */
    @FXML
    public void onTimKiemClick() {
        if (dangCapNhatBoLoc || danhSachLoc == null) {
            return;
        }
        String keyword = timKiemField.getText() != null ? timKiemField.getText().trim().toLowerCase() : "";
        String selectedDM = locDanhMucCombo.getValue();
        String selectedTon = locTonKhoCombo.getValue();
        String selectedTT = locTrangThaiCombo.getValue();

        danhSachLoc.setPredicate(sp -> {
            // 1. Lọc từ khóa (Mã SP, Tên SP, ĐVT)
            if (!keyword.isEmpty()) {
                boolean matchMa = sp.getMaSanPham() != null && sp.getMaSanPham().toLowerCase().contains(keyword);
                boolean matchTen = sp.getTenSanPham() != null && sp.getTenSanPham().toLowerCase().contains(keyword);
                boolean matchDVT = sp.getDonViTinh() != null && sp.getDonViTinh().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen && !matchDVT) {
                    return false;
                }
            }

            // 2. Lọc theo danh mục
            if (selectedDM != null && !selectedDM.equals("Tất cả danh mục")) {
                if (sp.getDanhMuc() == null || !selectedDM.equalsIgnoreCase(sp.getDanhMuc().getTenDanhMuc())) {
                    return false;
                }
            }

            // 3. Lọc theo tồn kho
            if (selectedTon != null && !selectedTon.equals("Tất cả tồn kho")) {
                if (selectedTon.contains("Còn hàng") && sp.getSoLuongTon() <= 0) {
                    return false;
                } else if (selectedTon.contains("Hết hàng") && sp.getSoLuongTon() > 0) {
                    return false;
                } else if (selectedTon.contains("Cảnh báo tồn ít") && sp.getSoLuongTon() > sp.getSoLuongTonToiThieu()) {
                    return false;
                }
            }

            // 4. Lọc theo trạng thái kinh doanh
            if (selectedTT != null && !selectedTT.equals("Tất cả trạng thái")) {
                if (selectedTT.equals("Đang kinh doanh") && !sp.isTrangThai()) {
                    return false;
                } else if (selectedTT.equals("Ngừng kinh doanh") && sp.isTrangThai()) {
                    return false;
                }
            }

            return true;
        });

        capNhatTongSo();
        sanPhamTable.getSelectionModel().clearSelection();
        if (chiTietSanPhamController != null) {
            chiTietSanPhamController.anChiTiet();
        }
    }

    /**
     * Sự kiện nút Làm Mới: xóa bộ lọc và tải lại từ CSDL
     */
    @FXML
    public void onLamMoiClick() {
        timKiemField.clear();
        locDanhMucCombo.setValue("Tất cả danh mục");
        locTonKhoCombo.setValue("Tất cả tồn kho");
        locTrangThaiCombo.setValue("Tất cả trạng thái");
        danhSachLoc.setPredicate(p -> true);
        taiDuLieuTuDatabase();
        hienThongBao("Đã làm mới danh sách sản phẩm!", true);
    }

    /**
     * Sự kiện nút + Thêm Sản Phẩm (Thủ công): chuyển hướng sang san-pham-them.fxml
     */
    @FXML
    public void onThemThuCongClick() {
        DieuHuongUtil.chuyenManHinh(sanPhamTable,
                "/com/pengugarage/view/quanly/san-pham-them.fxml",
                "Pengu Garage Service ERP - Thêm Sản Phẩm Mới");
    }

    /**
     * =========================================================================
     * TASK M02-T03: Thêm sản phẩm bằng file import (Excel)
     * Người thực hiện: Phong
     * =========================================================================
     * Hiển thị cửa sổ chọn file Excel từ máy tính người dùng
     */
    @FXML
    public void onNhapFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file danh sách sản phẩm Excel");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("Tất cả các file (*.*)", "*.*")
        );
        Stage stage = (sanPhamTable != null && sanPhamTable.getScene() != null)
                ? (Stage) sanPhamTable.getScene().getWindow() : null;
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            // TODO: Phong thực hiện đọc dữ liệu từ file Excel và gọi SanPhamService.themDanhSachSanPham() (Task M02-T03)
            hienThongBao("Đã chọn file: " + file.getName() + " - Chức năng import Excel do Phong thực hiện (Task M02-T03)", true);
        }
    }

    @FXML
    public void onThemMoiClick() {
        onThemThuCongClick();
    }


    /**
     * Sự kiện quay lại Dashboard Quản lý
     */
    @FXML
    public void onBackToDashboardClick() {
        DieuHuongUtil.chuyenManHinh(sanPhamTable,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Quản Lý");
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        thongBaoLabel.setText(msg);
        thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #ef4444;");
    }
}
