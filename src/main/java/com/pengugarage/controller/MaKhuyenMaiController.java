package com.pengugarage.controller;

import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.enums.LoaiGiamGia;
import com.pengugarage.service.MaKhuyenMaiService;
import com.pengugarage.util.DatePickerUtil;
import com.pengugarage.util.DieuHuongUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller quản lý Mã Khuyến Mãi Gara (M03-T01).
 * Hỗ trợ hiển thị danh sách, bộ lọc tìm kiếm, xem chi tiết và cập nhật thông tin khuyến mãi.
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
    @FXML private TableView<MaKhuyenMai> khuyenMaiTable;
    @FXML private TableColumn<MaKhuyenMai, String> colMaKM;
    @FXML private TableColumn<MaKhuyenMai, String> colLoaiGiam;
    @FXML private TableColumn<MaKhuyenMai, String> colGiaTriGiam;
    @FXML private TableColumn<MaKhuyenMai, String> colDonHangToiThieu;
    @FXML private TableColumn<MaKhuyenMai, String> colNgayBatDau;
    @FXML private TableColumn<MaKhuyenMai, String> colNgayHetHan;
    @FXML private TableColumn<MaKhuyenMai, String> colSoLuong;
    @FXML private TableColumn<MaKhuyenMai, String> colTrangThai;

    // Khung Chi Tiết Mã Khuyến Mãi
    @FXML private VBox chiTietPanel;
    @FXML private Label detailMaKMLabel;
    @FXML private Label thongBaoLuuLabel;
    @FXML private TextField maKMField;
    @FXML private ComboBox<String> loaiGiamCombo;
    @FXML private TextField giaTriGiamField;
    @FXML private TextField giaTriToiThieuField;
    @FXML private DatePicker ngayBatDauPicker;
    @FXML private DatePicker ngayHetHanPicker;
    @FXML private TextField soLuongTonField;
    @FXML private ComboBox<String> trangThaiCombo;

    // Các nhãn hiển thị lỗi chữ đỏ ngay dưới từng ô input
    @FXML private Label errMaKM;
    @FXML private Label errGiaTriGiam;
    @FXML private Label errGiaTriToiThieu;
    @FXML private Label errNgayBatDau;
    @FXML private Label errNgayHetHan;
    @FXML private Label errSoLuongTon;

    private final MaKhuyenMaiService maKhuyenMaiService = new MaKhuyenMaiService();
    private final ObservableList<MaKhuyenMai> danhSachMaKM = FXCollections.observableArrayList();
    private FilteredList<MaKhuyenMai> danhSachLoc;
    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");
    private final DateTimeFormatter ngayGioFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private MaKhuyenMai maKMDangChon = null;

    @FXML
    public void initialize() {
        cauHinhBangKhuyenMai();
        khoiTaoBoLoc();
        taiDuLieuTuDatabase();
        anChiTiet();

        khuyenMaiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiChiTiet(newVal);
            } else {
                anChiTiet();
            }
        });
    }

    private void cauHinhBangKhuyenMai() {
        colMaKM.setCellValueFactory(new PropertyValueFactory<>("maKhuyenMai"));
        colMaKM.setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-LEFT;");

        colLoaiGiam.setCellValueFactory(cellData -> {
            LoaiGiamGia lg = cellData.getValue().getLoaiGiam();
            String loai = (lg == LoaiGiamGia.PHAN_TRAM) ? "% Phần trăm" : "Tiền mặt";
            return new SimpleStringProperty(loai);
        });
        colLoaiGiam.setStyle("-fx-alignment: CENTER;");

        colGiaTriGiam.setCellValueFactory(cellData -> {
            MaKhuyenMai km = cellData.getValue();
            if (km.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
                return new SimpleStringProperty(String.format("%.0f%%", km.getGiaTriGiam()));
            } else {
                return new SimpleStringProperty(tienFormat.format(km.getGiaTriGiam()));
            }
        });
        colGiaTriGiam.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #f47b31;");

        colDonHangToiThieu.setCellValueFactory(cellData ->
                new SimpleStringProperty(tienFormat.format(cellData.getValue().getGiaTriToiThieuApDung()))
        );
        colDonHangToiThieu.setStyle("-fx-alignment: CENTER-RIGHT;");

        colNgayBatDau.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getNgayBatDauApDung();
            return new SimpleStringProperty(dt != null ? dt.format(ngayGioFormat) : "---");
        });
        colNgayBatDau.setStyle("-fx-alignment: CENTER; -fx-text-fill: #475569;");

        colNgayHetHan.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getNgayHetHan();
            return new SimpleStringProperty(dt != null ? dt.format(ngayGioFormat) : "---");
        });
        colNgayHetHan.setStyle("-fx-alignment: CENTER; -fx-text-fill: #475569;");

        colSoLuong.setCellValueFactory(cellData -> {
            Integer sl = cellData.getValue().getSoLuongTon();
            return new SimpleStringProperty(sl != null ? String.valueOf(sl) : "Không giới hạn");
        });
        colSoLuong.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        colTrangThai.setCellValueFactory(cellData -> {
            boolean tt = cellData.getValue().isTrangThai();
            return new SimpleStringProperty(tt ? "● Đang áp dụng" : "✖ Hết hạn / Khóa");
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
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        danhSachLoc = new FilteredList<>(danhSachMaKM, p -> true);
        khuyenMaiTable.setItems(danhSachLoc);
    }

    private void khoiTaoBoLoc() {
        locLoaiGiamCombo.setItems(FXCollections.observableArrayList(
                "Tất cả loại giảm",
                "Phần trăm (%)",
                "Tiền mặt (VNĐ)"
        ));
        locLoaiGiamCombo.setValue("Tất cả loại giảm");

        locTrangThaiCombo.setItems(FXCollections.observableArrayList(
                "Tất cả trạng thái",
                "Đang áp dụng",
                "Hết hạn / Khóa"
        ));
        locTrangThaiCombo.setValue("Tất cả trạng thái");

        loaiGiamCombo.setItems(FXCollections.observableArrayList("Theo Phần Trăm (%)", "Theo Tiền Mặt (VNĐ)"));
        trangThaiCombo.setItems(FXCollections.observableArrayList("Đang áp dụng", "Ngừng áp dụng"));

        DatePickerUtil.cauHinhDinhDang(ngayBatDauPicker);
        DatePickerUtil.cauHinhDinhDang(ngayHetHanPicker);

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
            khuyenMaiTable.getSelectionModel().clearSelection();
            anChiTiet();
            capNhatTongSo();
        } catch (Exception e) {
            hienThongBao("Lỗi khi tải dữ liệu mã khuyến mãi: " + e.getMessage(), false);
        }
    }

    private void capNhatTongSo() {
        int soLuong = danhSachLoc != null ? danhSachLoc.size() : danhSachMaKM.size();
        tongSoLabel.setText("Tổng số: " + soLuong + " mã khuyến mãi");
    }

    private void xoaTatCaLoi() {
        datLoiInput(maKMField, errMaKM, null);
        datLoiInput(giaTriGiamField, errGiaTriGiam, null);
        datLoiInput(giaTriToiThieuField, errGiaTriToiThieu, null);
        datLoiInput(ngayBatDauPicker, errNgayBatDau, null);
        datLoiInput(ngayHetHanPicker, errNgayHetHan, null);
        datLoiInput(soLuongTonField, errSoLuongTon, null);
        if (thongBaoLuuLabel != null) {
            thongBaoLuuLabel.setText("");
        }
    }

    private void datLoiInput(Control control, Label errLabel, String loiMsg) {
        if (errLabel != null) {
            if (loiMsg != null && !loiMsg.isEmpty()) {
                errLabel.setText("⚠ " + loiMsg);
                errLabel.setVisible(true);
                errLabel.setManaged(true);
            } else {
                errLabel.setText("");
                errLabel.setVisible(false);
                errLabel.setManaged(false);
            }
        }
        if (control != null) {
            control.getStyleClass().remove("input-error");
            if (loiMsg != null && !loiMsg.isEmpty()) {
                control.getStyleClass().add("input-error");
            }
        }
    }

    private void hienThiChiTiet(MaKhuyenMai chiTiet) {
        this.maKMDangChon = chiTiet;
        chiTietPanel.setVisible(true);
        chiTietPanel.setManaged(true);
        xoaTatCaLoi();

        detailMaKMLabel.setText(chiTiet.getMaKhuyenMai() != null ? chiTiet.getMaKhuyenMai() : "---");
        maKMField.setText(chiTiet.getMaKhuyenMai() != null ? chiTiet.getMaKhuyenMai() : "");

        if (chiTiet.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
            loaiGiamCombo.setValue("Theo Phần Trăm (%)");
            giaTriGiamField.setText(String.format("%.0f", chiTiet.getGiaTriGiam()));
        } else {
            loaiGiamCombo.setValue("Theo Tiền Mặt (VNĐ)");
            giaTriGiamField.setText(String.format("%.0f", chiTiet.getGiaTriGiam()));
        }

        giaTriToiThieuField.setText(String.format("%.0f", chiTiet.getGiaTriToiThieuApDung()));
        DatePickerUtil.ganGiaTri(ngayBatDauPicker, chiTiet.getNgayBatDauApDung());
        DatePickerUtil.ganGiaTri(ngayHetHanPicker, chiTiet.getNgayHetHan());
        soLuongTonField.setText(chiTiet.getSoLuongTon() != null ? String.valueOf(chiTiet.getSoLuongTon()) : "0");
        trangThaiCombo.setValue(chiTiet.isTrangThai() ? "Đang áp dụng" : "Ngừng áp dụng");
    }

    private void anChiTiet() {
        this.maKMDangChon = null;
        chiTietPanel.setVisible(false);
        chiTietPanel.setManaged(false);
        xoaTatCaLoi();
    }

    @FXML
    public void onDongChiTietClick() {
        khuyenMaiTable.getSelectionModel().clearSelection();
        anChiTiet();
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

            if (selectedLoai != null && !selectedLoai.equals("Tất cả loại giảm")) {
                if (selectedLoai.contains("Phần trăm") && km.getLoaiGiam() != LoaiGiamGia.PHAN_TRAM) {
                    return false;
                } else if (selectedLoai.contains("Tiền mặt") && km.getLoaiGiam() != LoaiGiamGia.SO_TIEN) {
                    return false;
                }
            }

            if (selectedTT != null && !selectedTT.equals("Tất cả trạng thái")) {
                if (selectedTT.equals("Đang áp dụng") && !km.isTrangThai()) {
                    return false;
                } else if (selectedTT.contains("Hết hạn") && km.isTrangThai()) {
                    return false;
                }
            }

            return true;
        });

        capNhatTongSo();
        khuyenMaiTable.getSelectionModel().clearSelection();
        anChiTiet();
    }

    @FXML
    public void onLamMoiClick() {
        timKiemField.clear();
        locLoaiGiamCombo.setValue("Tất cả loại giảm");
        locTrangThaiCombo.setValue("Tất cả trạng thái");
        danhSachLoc.setPredicate(p -> true);
        taiDuLieuTuDatabase();
        hienThongBao("Đã làm mới danh sách mã khuyến mãi!", true);
    }

    @FXML
    public void onThemMoiClick() {
        DieuHuongUtil.moModal(khuyenMaiTable,
                "/com/pengugarage/view/quanly/component/modal-them-ma-khuyen-mai.fxml",
                "Pengu Garage - Thêm Mã Khuyến Mãi Mới");
        taiDuLieuTuDatabase();
    }

    @FXML
    public void onLuuThayDoiClick() {
        if (maKMDangChon == null) {
            hienThongBaoLuu("Vui lòng chọn một mã khuyến mãi từ danh sách để cập nhật!", false);
            return;
        }

        xoaTatCaLoi();

        try {
            // 1. Kiểm tra Mã KM
            String maKM = maKMField.getText() != null ? maKMField.getText().trim().toUpperCase() : "";
            if (maKM.isEmpty()) {
                datLoiInput(maKMField, errMaKM, "Mã khuyến mãi không được để trống!");
                maKMField.requestFocus();
                return;
            }
            if (!maKM.matches("^[A-Za-z0-9_\\-]{3,30}$")) {
                datLoiInput(maKMField, errMaKM, "Mã từ 3-30 ký tự (chữ cái, chữ số, gạch nối)!");
                maKMField.requestFocus();
                return;
            }
            if (maKhuyenMaiService.kiemTraTrungMa(maKM, maKMDangChon.getId())) {
                datLoiInput(maKMField, errMaKM, "Mã '" + maKM + "' đã được sử dụng bởi khuyến mãi khác!");
                maKMField.requestFocus();
                return;
            }

            // 2. Kiểm tra Loại giảm và Giá trị giảm
            String loaiGiamStr = loaiGiamCombo.getValue();
            LoaiGiamGia loaiGiam = (loaiGiamStr != null && loaiGiamStr.contains("Tiền"))
                    ? LoaiGiamGia.SO_TIEN : LoaiGiamGia.PHAN_TRAM;

            String giaTriGiamTxt = giaTriGiamField.getText() != null ? giaTriGiamField.getText().trim().replace(",", "") : "";
            if (giaTriGiamTxt.isEmpty()) {
                datLoiInput(giaTriGiamField, errGiaTriGiam, "Vui lòng nhập giá trị giảm!");
                giaTriGiamField.requestFocus();
                return;
            }

            double giaTriGiam;
            try {
                giaTriGiam = Double.parseDouble(giaTriGiamTxt);
            } catch (NumberFormatException e) {
                datLoiInput(giaTriGiamField, errGiaTriGiam, "Giá trị giảm phải là số hợp lệ!");
                giaTriGiamField.requestFocus();
                return;
            }

            if (loaiGiam == LoaiGiamGia.PHAN_TRAM) {
                if (giaTriGiam <= 0 || giaTriGiam > 100) {
                    datLoiInput(giaTriGiamField, errGiaTriGiam, "Giảm theo % phải từ 1 đến 100%!");
                    giaTriGiamField.requestFocus();
                    return;
                }
            } else {
                if (giaTriGiam <= 0) {
                    datLoiInput(giaTriGiamField, errGiaTriGiam, "Số tiền giảm phải lớn hơn 0!");
                    giaTriGiamField.requestFocus();
                    return;
                }
            }

            // 3. Đơn hàng tối thiểu
            double giaTriToiThieu = 0;
            String toiThieuTxt = giaTriToiThieuField.getText() != null ? giaTriToiThieuField.getText().trim().replace(",", "") : "";
            if (!toiThieuTxt.isEmpty()) {
                try {
                    giaTriToiThieu = Double.parseDouble(toiThieuTxt);
                    if (giaTriToiThieu < 0) {
                        datLoiInput(giaTriToiThieuField, errGiaTriToiThieu, "Đơn hàng tối thiểu không được âm!");
                        giaTriToiThieuField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    datLoiInput(giaTriToiThieuField, errGiaTriToiThieu, "Giá trị tối thiểu phải là số!");
                    giaTriToiThieuField.requestFocus();
                    return;
                }
            }

            if (loaiGiam == LoaiGiamGia.SO_TIEN && giaTriToiThieu > 0 && giaTriGiam > giaTriToiThieu) {
                datLoiInput(giaTriGiamField, errGiaTriGiam, "Tiền giảm không được vượt quá đơn tối thiểu!");
                giaTriGiamField.requestFocus();
                return;
            }

            // 4. Ngày áp dụng
            LocalDate startLocalDate = ngayBatDauPicker.getValue();
            LocalDate endLocalDate = ngayHetHanPicker.getValue();
            if (startLocalDate == null) {
                datLoiInput(ngayBatDauPicker, errNgayBatDau, "Vui lòng chọn ngày bắt đầu áp dụng!");
                ngayBatDauPicker.requestFocus();
                return;
            }
            if (endLocalDate == null) {
                datLoiInput(ngayHetHanPicker, errNgayHetHan, "Vui lòng chọn ngày hết hạn!");
                ngayHetHanPicker.requestFocus();
                return;
            }
            if (endLocalDate.isBefore(startLocalDate)) {
                datLoiInput(ngayHetHanPicker, errNgayHetHan, "Ngày hết hạn không thể trước ngày bắt đầu!");
                ngayHetHanPicker.requestFocus();
                return;
            }

            LocalDateTime ngayBatDau = startLocalDate.atStartOfDay();
            LocalDateTime ngayHetHan = endLocalDate.atTime(23, 59, 59);

            // 5. Số lượng tồn
            int soLuong = 100;
            String soLuongTxt = soLuongTonField.getText() != null ? soLuongTonField.getText().trim() : "";
            if (!soLuongTxt.isEmpty()) {
                try {
                    soLuong = Integer.parseInt(soLuongTxt);
                    if (soLuong < 0) {
                        datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng không được nhỏ hơn 0!");
                        soLuongTonField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    datLoiInput(soLuongTonField, errSoLuongTon, "Số lượng phải là số nguyên!");
                    soLuongTonField.requestFocus();
                    return;
                }
            }

            boolean trangThai = "Đang áp dụng".equals(trangThaiCombo.getValue());

            // Gán dữ liệu vào đối tượng đang chọn
            maKMDangChon.setMaKhuyenMai(maKM);
            maKMDangChon.setLoaiGiam(loaiGiam);
            maKMDangChon.setGiaTriGiam(giaTriGiam);
            maKMDangChon.setGiaTriToiThieuApDung(giaTriToiThieu);
            maKMDangChon.setNgayBatDauApDung(ngayBatDau);
            maKMDangChon.setNgayHetHan(ngayHetHan);
            maKMDangChon.setSoLuongTon(soLuong);
            maKMDangChon.setTrangThai(trangThai);

            boolean capNhatOk = maKhuyenMaiService.capNhat(maKMDangChon);
            if (capNhatOk) {
                khuyenMaiTable.refresh();
                onTimKiemClick();
                capNhatTongSo();
                hienThongBaoLuu("Cập nhật mã khuyến mãi '" + maKM + "' thành công!", true);
                hienThongBao("Cập nhật mã '" + maKM + "' thành công!", true);
            } else {
                hienThongBaoLuu("Cập nhật mã khuyến mãi thất bại!", false);
            }

        } catch (IllegalArgumentException e) {
            hienThongBaoLuu(e.getMessage(), false);
        } catch (Exception e) {
            hienThongBaoLuu("Lỗi khi cập nhật: " + e.getMessage(), false);
        }
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        thongBaoLabel.setText(msg);
        thongBaoLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }

    private void hienThongBaoLuu(String msg, boolean thanhCong) {
        thongBaoLuuLabel.setText(msg);
        thongBaoLuuLabel.setStyle(thanhCong ? "-fx-text-fill: #10b981; -fx-font-weight: bold;" : "-fx-text-fill: #ef4444; -fx-font-weight: bold;");
    }
}
