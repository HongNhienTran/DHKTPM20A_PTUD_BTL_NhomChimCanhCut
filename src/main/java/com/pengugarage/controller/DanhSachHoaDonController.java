package com.pengugarage.controller;

import com.pengugarage.dao.HoaDonDAO;
import com.pengugarage.model.entity.ChiTietHoaDon;
import com.pengugarage.model.entity.HoaDon;
import com.pengugarage.model.enums.LoaiHoaDon;
import com.pengugarage.model.enums.TrangThaiThanhToan;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Controller cho Màn hình Quản Lý Danh Sách & Nhật Ký Hóa Đơn (danh-sach-hoa-don-view.fxml).
 * Role: Tiếp nhận & Bán hàng (Lễ tân, Thu ngân) và Quản lý.
 * 
 * Lưu ý: Khung code-base chuẩn bị sẵn các binding FXML, cấu hình bảng, sự kiện lọc;
 * code chi tiết truy vấn DB và xử lý in ấn/xuất báo cáo sẽ được phân công cho thành viên nhóm thực hiện.
 */
public class DanhSachHoaDonController {

    // =========================================================================
    // 1. FXML UI CONTROLS
    // =========================================================================

    @FXML private Button btnXuatExcel;
    @FXML private Button btnLamMoi;

    // Các thẻ thống kê
    @FXML private Label lblTongHoaDon;
    @FXML private Label lblDaThanhToan;
    @FXML private Label lblChoThanhToan;
    @FXML private Label lblTongDoanhThu;

    // Bộ lọc & tìm kiếm
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cboLoaiHoaDon;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
    @FXML private Button btnTimKiem;
    @FXML private Button btnDatLaiBoLoc;

    // Bảng danh sách hóa đơn
    @FXML private Label lblSoBanGhi;
    @FXML private TableView<HoaDon> tblHoaDon;
    @FXML private TableColumn<HoaDon, Integer> colStt;
    @FXML private TableColumn<HoaDon, String> colMaHoaDon;
    @FXML private TableColumn<HoaDon, String> colLoaiHoaDon;
    @FXML private TableColumn<HoaDon, String> colKhachHang;
    @FXML private TableColumn<HoaDon, String> colSdt;
    @FXML private TableColumn<HoaDon, String> colThoiGianLap;
    @FXML private TableColumn<HoaDon, String> colNhanVienLap;
    @FXML private TableColumn<HoaDon, String> colTongTien;
    @FXML private TableColumn<HoaDon, String> colKhuyenMai;
    @FXML private TableColumn<HoaDon, String> colTongThanhToan;
    @FXML private TableColumn<HoaDon, String> colPhuongThuc;
    @FXML private TableColumn<HoaDon, Void> colTrangThai;
    @FXML private TableColumn<HoaDon, Void> colThaoTac;

    // Vùng chi tiết nhanh (Quick Detail Drawer)
    @FXML private VBox boxChiTietHoaDon;
    @FXML private Label lblChiTietMaHD;
    @FXML private Label lblChiTietGhiChu;
    @FXML private TableView<ChiTietHoaDon> tblChiTiet;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colChiTietStt;
    @FXML private TableColumn<ChiTietHoaDon, String> colChiTietLoai;
    @FXML private TableColumn<ChiTietHoaDon, String> colChiTietTenHangMuc;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colChiTietSoLuong;
    @FXML private TableColumn<ChiTietHoaDon, String> colChiTietDonGia;
    @FXML private TableColumn<ChiTietHoaDon, String> colChiTietThue;
    @FXML private TableColumn<ChiTietHoaDon, String> colChiTietThanhTien;

    // =========================================================================
    // 2. DATA PROPERTIES & UTILITIES
    // =========================================================================

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ObservableList<HoaDon> danhSachHoaDon = FXCollections.observableArrayList();
    private final ObservableList<ChiTietHoaDon> danhSachChiTiet = FXCollections.observableArrayList();

    private final DecimalFormat dfTien = new DecimalFormat("#,##0 'đ'");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        khoiTaoBoLoc();
        cauHinhBangHoaDon();
        cauHinhBangChiTiet();
        langNgheChonDongHoaDon();
        taiDuLieuHoaDon();
    }

    /**
     * Khởi tạo các tùy chọn cho ComboBox lọc
     */
    private void khoiTaoBoLoc() {
        if (cboLoaiHoaDon != null) {
            cboLoaiHoaDon.setItems(FXCollections.observableArrayList(
                    "Tất cả loại HĐ",
                    "Bán hàng phụ tùng",
                    "Dịch vụ sửa chữa"
            ));
            cboLoaiHoaDon.getSelectionModel().selectFirst();
        }

        if (cboTrangThai != null) {
            cboTrangThai.setItems(FXCollections.observableArrayList(
                    "Tất cả trạng thái",
                    "Đã thanh toán",
                    "Chưa thanh toán"
            ));
            cboTrangThai.getSelectionModel().selectFirst();
        }
    }

    /**
     * Thiết lập cấu hình các cột cho Bảng Hóa Đơn
     */
    private void cauHinhBangHoaDon() {
        if (tblHoaDon == null) return;

        tblHoaDon.setItems(danhSachHoaDon);

        if (colStt != null) {
            colStt.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() < 0) {
                        setText(null);
                    } else {
                        setText(String.valueOf(getIndex() + 1));
                    }
                }
            });
        }

        if (colMaHoaDon != null) {
            colMaHoaDon.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        }

        if (colLoaiHoaDon != null) {
            colLoaiHoaDon.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null && hd.getLoaiHoaDon() != null) {
                    return new SimpleStringProperty(hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG ? "🛒 Bán lẻ" : "🔧 Dịch vụ");
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colKhachHang != null) {
            colKhachHang.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null) {
                    if (hd.getKhachHang() != null) {
                        return new SimpleStringProperty(hd.getKhachHang().getHoTen());
                    } else if (hd.getTenKhachLe() != null && !hd.getTenKhachLe().isEmpty()) {
                        return new SimpleStringProperty(hd.getTenKhachLe());
                    }
                }
                return new SimpleStringProperty("Khách lẻ");
            });
        }

        if (colSdt != null) {
            colSdt.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null) {
                    if (hd.getKhachHang() != null && hd.getKhachHang().getSoDienThoai() != null) {
                        return new SimpleStringProperty(hd.getKhachHang().getSoDienThoai());
                    } else if (hd.getSdtKhachLe() != null) {
                        return new SimpleStringProperty(hd.getSdtKhachLe());
                    }
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colThoiGianLap != null) {
            colThoiGianLap.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null && hd.getThoiGianLap() != null) {
                    return new SimpleStringProperty(hd.getThoiGianLap().format(dtf));
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colNhanVienLap != null) {
            colNhanVienLap.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null && hd.getNhanVienLap() != null) {
                    return new SimpleStringProperty(hd.getNhanVienLap().getHoTen());
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colTongTien != null) {
            colTongTien.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                return new SimpleStringProperty(hd != null ? dfTien.format(hd.getTongTienHang()) : "0 đ");
            });
        }

        if (colKhuyenMai != null) {
            colKhuyenMai.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                return new SimpleStringProperty(hd != null ? dfTien.format(hd.getTienKhuyenMai()) : "0 đ");
            });
        }

        if (colTongThanhToan != null) {
            colTongThanhToan.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                return new SimpleStringProperty(hd != null ? dfTien.format(hd.getTongThanhToan()) : "0 đ");
            });
        }

        if (colPhuongThuc != null) {
            colPhuongThuc.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null && hd.getPhuongThucThanhToan() != null) {
                    return new SimpleStringProperty(hd.getPhuongThucThanhToan().name());
                }
                return new SimpleStringProperty("-");
            });
        }

        // Cột Trạng Thái: Hiển thị Badge màu (Xanh lá: Đã thanh toán, Cam: Chưa thanh toán)
        if (colTrangThai != null) {
            colTrangThai.setCellFactory(col -> new TableCell<>() {
                private final Label badge = new Label();
                {
                    badge.setStyle("-fx-padding: 3px 10px; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11.5px;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                    } else {
                        HoaDon hd = getTableView().getItems().get(getIndex());
                        if (hd != null && hd.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
                            badge.setText("● ĐÃ THANH TOÁN");
                            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-padding: 3px 10px; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11.5px;");
                        } else {
                            badge.setText("● CHỜ THANH TOÁN");
                            badge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #b45309; -fx-padding: 3px 10px; -fx-background-radius: 12px; -fx-font-weight: bold; -fx-font-size: 11.5px;");
                        }
                        setGraphic(badge);
                    }
                }
            });
        }

        // Cột Thao Tác: Xem chi tiết & In hóa đơn
        if (colThaoTac != null) {
            colThaoTac.setCellFactory(col -> new TableCell<>() {
                private final Button btnXem = new Button("👁️");
                private final Button btnIn = new Button("🖨️");
                private final HBox box = new HBox(6, btnXem, btnIn);

                {
                    btnXem.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #102a46; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 4px 8px;");
                    btnIn.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #f47b31; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 4px 8px;");

                    btnXem.setTooltip(new Tooltip("Xem chi tiết hóa đơn"));
                    btnIn.setTooltip(new Tooltip("In lại hóa đơn"));

                    btnXem.setOnAction(e -> {
                        HoaDon hd = getTableView().getItems().get(getIndex());
                        onXemChiTietHoaDon(hd);
                    });

                    btnIn.setOnAction(e -> {
                        HoaDon hd = getTableView().getItems().get(getIndex());
                        onInHoaDon(hd);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                    } else {
                        setGraphic(box);
                    }
                }
            });
        }
    }

    /**
     * Cấu hình bảng hiển thị chi tiết các hạng mục (mặt hàng/dịch vụ) của hóa đơn được chọn
     */
    private void cauHinhBangChiTiet() {
        if (tblChiTiet == null) return;

        tblChiTiet.setItems(danhSachChiTiet);

        if (colChiTietStt != null) {
            colChiTietStt.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getIndex() < 0) {
                        setText(null);
                    } else {
                        setText(String.valueOf(getIndex() + 1));
                    }
                }
            });
        }

        if (colChiTietLoai != null) {
            colChiTietLoai.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                return new SimpleStringProperty(ct != null && ct.getLoaiHangMuc() != null ? ct.getLoaiHangMuc().name() : "-");
            });
        }

        if (colChiTietTenHangMuc != null) {
            colChiTietTenHangMuc.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                if (ct != null) {
                    if (ct.getSanPham() != null) return new SimpleStringProperty(ct.getSanPham().getTenSanPham());
                    if (ct.getDichVu() != null) return new SimpleStringProperty(ct.getDichVu().getTenDichVu());
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colChiTietSoLuong != null) {
            colChiTietSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        }

        if (colChiTietDonGia != null) {
            colChiTietDonGia.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                return new SimpleStringProperty(ct != null ? dfTien.format(ct.getDonGia()) : "0 đ");
            });
        }

        if (colChiTietThue != null) {
            colChiTietThue.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                return new SimpleStringProperty(ct != null ? ct.getThueVAT() + "%" : "10%");
            });
        }

        if (colChiTietThanhTien != null) {
            colChiTietThanhTien.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                return new SimpleStringProperty(ct != null ? dfTien.format(ct.getThanhTienSauThue()) : "0 đ");
            });
        }
    }

    /**
     * Lắng nghe sự kiện chọn dòng trên bảng hóa đơn để tải danh sách chi tiết mặt hàng
     */
    private void langNgheChonDongHoaDon() {
        if (tblHoaDon == null) return;

        tblHoaDon.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (lblChiTietMaHD != null) {
                    lblChiTietMaHD.setText(newVal.getMaHoaDon() + " (" + (newVal.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG ? "Bán lẻ" : "Dịch vụ") + ")");
                }
                if (lblChiTietGhiChu != null) {
                    lblChiTietGhiChu.setText("Ghi chú: " + (newVal.getGhiChu() != null && !newVal.getGhiChu().isEmpty() ? newVal.getGhiChu() : "Không có"));
                }
                // TODO: [Phân công thành viên] Gọi hoaDonDAO.layChiTietHoaDon(newVal.getId()) để nạp vào danhSachChiTiet
            }
        });
    }

    // =========================================================================
    // 3. EVENT HANDLERS & KHUNG PHÂN CÔNG THÀNH VIÊN
    // =========================================================================

    /**
     * Tải dữ liệu toàn bộ hóa đơn từ cơ sở dữ liệu
     */
    public void taiDuLieuHoaDon() {
        // TODO: [Phân công thành viên] 
        // 1. Gọi hoaDonDAO.layTatCaHoaDon()
        // 2. Cập nhật danhSachHoaDon
        // 3. Tính toán các thống kê: lblTongHoaDon, lblDaThanhToan, lblChoThanhToan, lblTongDoanhThu
        // 4. Cập nhật lblSoBanGhi.setText("Hiển thị: " + danhSachHoaDon.size() + " hóa đơn")
    }

    @FXML
    public void onTimKiemClick(ActionEvent event) {
        // TODO: [Phân công thành viên]
        // Lấy các giá trị: txtTimKiem, cboLoaiHoaDon, cboTrangThai, dpTuNgay, dpDenNgay
        // Gọi hoaDonDAO.timKiemVaLocHoaDon(...) và hiển thị kết quả lên tblHoaDon
    }

    @FXML
    public void onDatLaiBoLocClick(ActionEvent event) {
        // TODO: [Phân công thành viên]
        // Xóa trắng ô tìm kiếm, đưa các ComboBox về mặc định và DatePicker về null, sau đó gọi taiDuLieuHoaDon()
        if (txtTimKiem != null) txtTimKiem.clear();
        if (cboLoaiHoaDon != null) cboLoaiHoaDon.getSelectionModel().selectFirst();
        if (cboTrangThai != null) cboTrangThai.getSelectionModel().selectFirst();
        if (dpTuNgay != null) dpTuNgay.setValue(null);
        if (dpDenNgay != null) dpDenNgay.setValue(null);
        taiDuLieuHoaDon();
    }

    @FXML
    public void onLamMoiClick(ActionEvent event) {
        taiDuLieuHoaDon();
    }

    @FXML
    public void onXuatExcelClick(ActionEvent event) {
        // TODO: [Phân công thành viên]
        // Xuất danh sách hóa đơn hiện tại trong tblHoaDon ra file Excel (.xlsx) qua Apache POI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xuất báo cáo");
        alert.setHeaderText("Chức năng xuất báo cáo hóa đơn");
        alert.setContentText("Tính năng xuất Excel hóa đơn sẽ được hoàn thiện bởi thành viên nhóm!");
        alert.showAndWait();
    }

    private void onXemChiTietHoaDon(HoaDon hd) {
        // TODO: [Phân công thành viên]
        // Mở cửa sổ popup / modal hiển thị toàn bộ hóa đơn kèm thông tin khách hàng, chi tiết dịch vụ, thanh toán
    }

    private void onInHoaDon(HoaDon hd) {
        // TODO: [Phân công thành viên]
        // Kết nối mẫu in hóa đơn / JasperReport hoặc in trực tiếp ra máy in hóa đơn (Bill printer)
    }
}

