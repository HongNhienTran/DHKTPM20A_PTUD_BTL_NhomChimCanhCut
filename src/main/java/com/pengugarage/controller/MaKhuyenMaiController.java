package com.pengugarage.controller;

import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;
import com.pengugarage.service.MaKhuyenMaiService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller quản lý Mã Khuyến Mãi Gara (M03-T01).
 * Hỗ trợ hiển thị danh sách, tra cứu, lọc loại giảm/trạng thái và form chỉnh sửa chi tiết.
 */
public class MaKhuyenMaiController {

    // Toolbar tìm kiếm & lọc
    @FXML private TextField timKiemField;
    @FXML private ComboBox<String> locLoaiGiamCombo;
    @FXML private ComboBox<String> locTrangThaiCombo;

    // Header & Thông báo
    @FXML private Label thongBaoLabel;
    @FXML private Label tongSoLabel;

    // Bảng danh sách mã khuyến mãi
    @FXML private TableView<MaKhuyenMai> maKhuyenMaiTable;
    @FXML private TableColumn<MaKhuyenMai, String> colMaKM;
    @FXML private TableColumn<MaKhuyenMai, String> colLoaiGiam;
    @FXML private TableColumn<MaKhuyenMai, String> colGiaTriGiam;
    @FXML private TableColumn<MaKhuyenMai, String> colGiaTriToiThieu;
    @FXML private TableColumn<MaKhuyenMai, String> colNgayBatDau;
    @FXML private TableColumn<MaKhuyenMai, String> colNgayHetHan;
    @FXML private TableColumn<MaKhuyenMai, String> colSoLuongTon;
    @FXML private TableColumn<MaKhuyenMai, String> colTrangThai;

    // Khung Xem & Chỉnh Sửa Chi Tiết (Sổ ra phía dưới)
    @FXML private VBox chiTietPane;
    @FXML private Label detailMaKMLabel;
    @FXML private Label thongBaoLuuLabel;
    @FXML private TextField maKMField;
    @FXML private ComboBox<LoaiGiamGia> loaiGiamCombo;
    @FXML private TextField giaTriGiamField;
    @FXML private TextField giaTriToiThieuField;
    @FXML private DatePicker ngayBatDauPicker;
    @FXML private DatePicker ngayHetHanPicker;
    @FXML private TextField soLuongTonField;
    @FXML private ComboBox<String> trangThaiCombo;


    private final MaKhuyenMaiService maKhuyenMaiService = new MaKhuyenMaiService();
    private final ObservableList<MaKhuyenMai> danhSachMaKM = FXCollections.observableArrayList();
    private FilteredList<MaKhuyenMai> danhSachLoc;

    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");
    private final DateTimeFormatter dtfHienThi = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter dtfNhapLieu = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private MaKhuyenMai maKMDangChon;

    @FXML
    public void initialize() {
        cauHinhBangMaKM();
        khoiTaoBoLoc();
        anChiTiet();
        taiDuLieuTuDatabase();

        // Lắng nghe chọn dòng trong bảng
        maKhuyenMaiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiChiTiet(newVal);
            } else {
                anChiTiet();
            }
        });
    }

    private void cauHinhBangMaKM() {
        colMaKM.setCellValueFactory(new PropertyValueFactory<>("maKhuyenMai"));

        colLoaiGiam.setCellValueFactory(cellData -> {
            LoaiGiamGia lg = cellData.getValue().getLoaiGiam();
            return new SimpleStringProperty(lg == LoaiGiamGia.PHAN_TRAM ? "Phần trăm (%)" : "Tiền mặt (VNĐ)");
        });

        colGiaTriGiam.setCellValueFactory(cellData -> {
            MaKhuyenMai km = cellData.getValue();
            if (km.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
                return new SimpleStringProperty(String.format("%.0f%%", km.getGiaTriGiam()));
            } else {
                return new SimpleStringProperty(tienFormat.format(km.getGiaTriGiam()));
            }
        });
        colGiaTriGiam.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #f47b31;");

        colGiaTriToiThieu.setCellValueFactory(cellData -> 
            new SimpleStringProperty(tienFormat.format(cellData.getValue().getGiaTriToiThieuApDung()))
        );
        colGiaTriToiThieu.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #cbd5e1;");

        colNgayBatDau.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getNgayBatDauApDung();
            return new SimpleStringProperty(dt != null ? dt.format(dtfHienThi) : "---");
        });
        colNgayBatDau.setStyle("-fx-alignment: CENTER; -fx-text-fill: #94a3b8;");

        colNgayHetHan.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getNgayHetHan();
            return new SimpleStringProperty(dt != null ? dt.format(dtfHienThi) : "Không giới hạn");
        });
        colNgayHetHan.setStyle("-fx-alignment: CENTER; -fx-text-fill: #94a3b8;");

        colSoLuongTon.setCellValueFactory(cellData -> {
            Integer sl = cellData.getValue().getSoLuongTon();
            return new SimpleStringProperty(sl != null ? String.valueOf(sl) : "∞");
        });
        colSoLuongTon.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #10b981;");

        colTrangThai.setCellValueFactory(cellData -> {
            MaKhuyenMai km = cellData.getValue();
            boolean hetHan = km.getNgayHetHan() != null && km.getNgayHetHan().isBefore(LocalDateTime.now());
            if (hetHan) {
                return new SimpleStringProperty("⏳ Hết hạn");
            } else if (km.isTrangThai()) {
                return new SimpleStringProperty("● Đang áp dụng");
            } else {
                return new SimpleStringProperty("✖ Ngừng áp dụng");
            }
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
                    } else if (item.contains("Hết hạn")) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        danhSachLoc = new FilteredList<>(danhSachMaKM, p -> true);
        maKhuyenMaiTable.setItems(danhSachLoc);
    }

    private void khoiTaoBoLoc() {
        locLoaiGiamCombo.setItems(FXCollections.observableArrayList("Tất cả loại", "Phần trăm (%)", "Tiền mặt (VNĐ)"));
        locLoaiGiamCombo.setValue("Tất cả loại");

        locTrangThaiCombo.setItems(FXCollections.observableArrayList("Tất cả trạng thái", "Đang áp dụng", "Ngừng áp dụng", "Đã hết hạn"));
        locTrangThaiCombo.setValue("Tất cả trạng thái");

        loaiGiamCombo.setItems(FXCollections.observableArrayList(LoaiGiamGia.values()));
        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));

        // Cấu hình DatePicker lịch hiển thị chuẩn dd/MM/yyyy
        com.pengugarage.util.DatePickerUtil.cauHinhDinhDang(ngayBatDauPicker);
        com.pengugarage.util.DatePickerUtil.cauHinhDinhDang(ngayHetHanPicker);

        // Tự động lọc tức thì khi thay đổi từ khóa hoặc bộ lọc
        timKiemField.textProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
        locLoaiGiamCombo.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
        locTrangThaiCombo.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiemClick());
    }

    private void taiDuLieuTuDatabase() {
        try {
            List<MaKhuyenMai> ds = maKhuyenMaiService.layTatCaMaKhuyenMai();
            danhSachMaKM.clear();
            if (ds != null && !ds.isEmpty()) {
                danhSachMaKM.addAll(ds);
            }
            maKhuyenMaiTable.getSelectionModel().clearSelection();
            anChiTiet();
            capNhatTongSo();
        } catch (Exception e) {
            hienThongBao("Lỗi khi tải dữ liệu mã khuyến mãi: " + e.getMessage(), false);
        }
    }

    private void capNhatTongSo() {
        int soLuong = danhSachLoc != null ? danhSachLoc.size() : danhSachMaKM.size();
        tongSoLabel.setText("Tổng số: " + soLuong + " mã");
    }

    private void hienThiChiTiet(MaKhuyenMai km) {
        if (km == null) {
            anChiTiet();
            return;
        }

        // TASK M03-T01: Hiển thị chi tiết mã khuyến mãi từ Database (Nhiên)
        MaKhuyenMai chiTiet = maKhuyenMaiService.layChiTietMaKhuyenMaiTheoID(km.getId());
        if (chiTiet == null) {
            chiTiet = km;
        }

        this.maKMDangChon = chiTiet;
        chiTietPane.setVisible(true);
        chiTietPane.setManaged(true);
        thongBaoLuuLabel.setText("");

        detailMaKMLabel.setText(chiTiet.getMaKhuyenMai() != null ? chiTiet.getMaKhuyenMai() : "---");
        maKMField.setText(chiTiet.getMaKhuyenMai() != null ? chiTiet.getMaKhuyenMai() : "");
        loaiGiamCombo.setValue(chiTiet.getLoaiGiam() != null ? chiTiet.getLoaiGiam() : LoaiGiamGia.PHAN_TRAM);
        giaTriGiamField.setText(String.format("%.0f", chiTiet.getGiaTriGiam()));
        giaTriToiThieuField.setText(String.format("%.0f", chiTiet.getGiaTriToiThieuApDung()));
        
        // Gán giá trị ngày vào DatePicker lịch
        com.pengugarage.util.DatePickerUtil.ganGiaTri(ngayBatDauPicker, chiTiet.getNgayBatDauApDung());
        com.pengugarage.util.DatePickerUtil.ganGiaTri(ngayHetHanPicker, chiTiet.getNgayHetHan());

        soLuongTonField.setText(chiTiet.getSoLuongTon() != null ? String.valueOf(chiTiet.getSoLuongTon()) : "0");
        trangThaiCombo.setValue(chiTiet.isTrangThai() ? "Đang áp dụng" : "Ngừng áp dụng");
    }


    private void anChiTiet() {
        if (chiTietPane != null) {
            chiTietPane.setVisible(false);
            chiTietPane.setManaged(false);
        }
    }

    @FXML
    public void onDongChiTietClick() {
        anChiTiet();
        maKhuyenMaiTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void onLuuThayDoiClick() {
        // Lưu ý: Trong bảng phân công không có task cập nhật mã khuyến mãi cho Nhiên
        // (Nhiên chỉ phụ trách M03-T01: danh sách, chi tiết, lọc trên giao diện).
        thongBaoLuuLabel.setText("Lưu ý: Chức năng cập nhật mã khuyến mãi không thuộc task phân công cho Nhiên.");
        thongBaoLuuLabel.setStyle("-fx-text-fill: #f59e0b;");
    }

    @FXML
    public void onTimKiemClick() {
        String keyword = timKiemField.getText() != null ? timKiemField.getText().trim().toLowerCase() : "";
        String selectedLoai = locLoaiGiamCombo.getValue();
        String selectedTT = locTrangThaiCombo.getValue();

        danhSachLoc.setPredicate(km -> {
            if (!keyword.isEmpty()) {
                if (km.getMaKhuyenMai() == null || !km.getMaKhuyenMai().toLowerCase().contains(keyword)) {
                    return false;
                }
            }

            if (selectedLoai != null && !selectedLoai.equals("Tất cả loại")) {
                if (selectedLoai.contains("Phần trăm") && km.getLoaiGiam() != LoaiGiamGia.PHAN_TRAM) return false;
                if (selectedLoai.contains("Tiền mặt") && km.getLoaiGiam() != LoaiGiamGia.SO_TIEN) return false;
            }

            if (selectedTT != null && !selectedTT.equals("Tất cả trạng thái")) {
                boolean hetHan = km.getNgayHetHan() != null && km.getNgayHetHan().isBefore(LocalDateTime.now());
                if (selectedTT.equals("Đã hết hạn") && !hetHan) return false;
                if (selectedTT.equals("Đang áp dụng") && (!km.isTrangThai() || hetHan)) return false;
                if (selectedTT.equals("Ngừng áp dụng") && (km.isTrangThai() || hetHan)) return false;
            }

            return true;
        });

        capNhatTongSo();
        maKhuyenMaiTable.getSelectionModel().clearSelection();
        anChiTiet();
    }

    @FXML
    public void onLamMoiClick() {
        timKiemField.clear();
        locLoaiGiamCombo.setValue("Tất cả loại");
        locTrangThaiCombo.setValue("Tất cả trạng thái");
        danhSachLoc.setPredicate(p -> true);
        taiDuLieuTuDatabase();
        hienThongBao("Đã làm mới danh sách khuyến mãi!", true);
    }

    // =========================================================================
    // TASK M03-T02: Nhập thông tin mã khuyến mãi và thêm
    // Người thực hiện: Tuấn
    // =========================================================================
    @FXML
    public void onThemMoiClick() {
        // Hiển thị modal nhập thông tin mã khuyến mãi mới (Task M03-T02 - Tuấn)
        DieuHuongUtil.moModal(maKhuyenMaiTable,
                "/com/pengugarage/view/quanly/component/modal-them-ma-khuyen-mai.fxml",
                "Pengu Garage - Thêm Mã Khuyến Mãi Mới (Task M03-T02 - Tuấn)");
    }



    @FXML
    public void onBackToDashboardClick() {
        DieuHuongUtil.chuyenManHinh(maKhuyenMaiTable,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Quản Lý");
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        thongBaoLabel.setText(msg);
        thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981;" : "-fx-text-fill: #ef4444;");
    }
}
