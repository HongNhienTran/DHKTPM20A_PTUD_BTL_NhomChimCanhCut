package com.pengugarage.controller;

import com.pengugarage.dao.HoaDonDAO;
import com.pengugarage.model.entity.ChiTietHoaDon;
import com.pengugarage.model.entity.HoaDon;
import com.pengugarage.model.enums.LoaiHoaDon;
import com.pengugarage.model.enums.PhuongThucThanhToan;
import com.pengugarage.model.enums.TrangThaiThanhToan;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * Controller cho Màn hình Lập Hóa Đơn & Thu Ngân (lap-hoa-don-view.fxml).
 * Quản lý các hóa đơn in tạm chờ thanh toán có trạng thái CHUA_THANH_TOAN.
 *
 * Lưu ý: Khung code-base chuẩn bị sẵn cấu trúc giao diện, ràng buộc trường dữ liệu
 * và các phương thức xử lý sự kiện kèm ghi chú TODO để phân công thành viên thực hiện code nghiệp vụ sau.
 */
public class LapHoaDonController {

    // =========================================================================
    // 1. FXML UI CONTROLS
    // =========================================================================

    // Header & Badges
    @FXML private Label lblSoDonChoTT;
    @FXML private Label lblTongTienChoThu;

    // Cột Trái (Danh Sách Đơn Chờ Thanh Toán)
    @FXML private TextField txtTimKiemDonCho;
    @FXML private ComboBox<String> cboNguonDon;
    @FXML private Button btnLamMoiDanhSach;
    @FXML private TableView<HoaDon> tblDonChoThanhToan;
    @FXML private TableColumn<HoaDon, Integer> colChoStt;
    @FXML private TableColumn<HoaDon, String> colChoMaHD;
    @FXML private TableColumn<HoaDon, String> colChoLoai;
    @FXML private TableColumn<HoaDon, String> colChoKhachHang;
    @FXML private TableColumn<HoaDon, String> colChoGioLap;
    @FXML private TableColumn<HoaDon, String> colChoTongTien;

    // Cột Phải (Chi Tiết Hóa Đơn & Quầy Thu Ngân)
    @FXML private Label lblMaHoaDonHienTai;
    @FXML private Label lblBadgeTrangThai;
    @FXML private Label lblTenKhach;
    @FXML private Label lblSdtKhach;
    @FXML private Label lblBienSo;
    @FXML private Label lblNhanVienLap;

    // Bảng chi tiết mặt hàng trong đơn
    @FXML private TableView<ChiTietHoaDon> tblChiTietThuNgan;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colMucStt;
    @FXML private TableColumn<ChiTietHoaDon, String> colMucTen;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colMucSL;
    @FXML private TableColumn<ChiTietHoaDon, String> colMucDonGia;
    @FXML private TableColumn<ChiTietHoaDon, String> colMucThanhTien;

    // Tóm tắt tài chính
    @FXML private Label lblTongTienHang;
    @FXML private Label lblTienGiamGia;
    @FXML private Label lblTienThue;
    @FXML private Label lblTongCanThu;

    // Phương thức thanh toán & Tiền mặt
    @FXML private RadioButton radTienMat;
    @FXML private RadioButton radChuyenKhoan;
    @FXML private ToggleGroup grpPhuongThucTT;
    @FXML private HBox boxTienMat;
    @FXML private TextField txtTienKhachDua;
    @FXML private Label lblTienThua;
    @FXML private TextField txtGhiChuThanhToan;

    // Nút hành động
    @FXML private Button btnHuyDon;
    @FXML private Button btnInTamTinh;
    @FXML private Button btnXacNhanThanhToan;

    // =========================================================================
    // 2. DATA PROPERTIES & UTILITIES
    // =========================================================================

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ObservableList<HoaDon> dsDonChoThanhToan = FXCollections.observableArrayList();
    private final ObservableList<ChiTietHoaDon> dsChiTietThuNgan = FXCollections.observableArrayList();
    private HoaDon hoaDonDangChon = null;

    private final DecimalFormat dfTien = new DecimalFormat("#,##0 'đ'");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    public void initialize() {
        khoiTaoBoLoc();
        cauHinhBangDonCho();
        cauHinhBangChiTiet();
        langNgheChonDonCho();
        langNghePhuongThucThanhToan();
        langNgheTienKhachDua();
        taiDanhSachDonChoThanhToan();
    }

    /**
     * Khởi tạo các ComboBox bộ lọc
     */
    private void khoiTaoBoLoc() {
        if (cboNguonDon != null) {
            cboNguonDon.setItems(FXCollections.observableArrayList(
                    "Tất cả nguồn đơn",
                    "Bán hàng phụ tùng",
                    "Dịch vụ sửa chữa"
            ));
            cboNguonDon.getSelectionModel().selectFirst();
        }
    }

    /**
     * Cấu hình bảng danh sách hóa đơn in tạm (CHƯA THANH TOÁN)
     */
    private void cauHinhBangDonCho() {
        if (tblDonChoThanhToan == null) return;

        tblDonChoThanhToan.setItems(dsDonChoThanhToan);

        if (colChoStt != null) {
            colChoStt.setCellFactory(col -> new TableCell<>() {
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

        if (colChoMaHD != null) {
            colChoMaHD.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        }

        if (colChoLoai != null) {
            colChoLoai.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null && hd.getLoaiHoaDon() != null) {
                    return new SimpleStringProperty(hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG ? "🛒 Bán lẻ" : "🔧 Dịch vụ");
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colChoKhachHang != null) {
            colChoKhachHang.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null) {
                    if (hd.getKhachHang() != null) return new SimpleStringProperty(hd.getKhachHang().getHoTen());
                    if (hd.getTenKhachLe() != null) return new SimpleStringProperty(hd.getTenKhachLe());
                }
                return new SimpleStringProperty("Khách lẻ");
            });
        }

        if (colChoGioLap != null) {
            colChoGioLap.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                if (hd != null && hd.getThoiGianLap() != null) {
                    return new SimpleStringProperty(hd.getThoiGianLap().format(dtf));
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colChoTongTien != null) {
            colChoTongTien.setCellValueFactory(cell -> {
                HoaDon hd = cell.getValue();
                return new SimpleStringProperty(hd != null ? dfTien.format(hd.getTongThanhToan()) : "0 đ");
            });
            colChoTongTien.setStyle("-fx-font-weight: bold; -fx-text-fill: #f47b31;");
        }
    }

    /**
     * Cấu hình bảng hiển thị chi tiết các hạng mục thanh toán bên quầy thu ngân
     */
    private void cauHinhBangChiTiet() {
        if (tblChiTietThuNgan == null) return;

        tblChiTietThuNgan.setItems(dsChiTietThuNgan);

        if (colMucStt != null) {
            colMucStt.setCellFactory(col -> new TableCell<>() {
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

        if (colMucTen != null) {
            colMucTen.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                if (ct != null) {
                    if (ct.getSanPham() != null) return new SimpleStringProperty(ct.getSanPham().getTenSanPham());
                    if (ct.getDichVu() != null) return new SimpleStringProperty(ct.getDichVu().getTenDichVu());
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colMucSL != null) {
            colMucSL.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        }

        if (colMucDonGia != null) {
            colMucDonGia.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                return new SimpleStringProperty(ct != null ? dfTien.format(ct.getDonGia()) : "0 đ");
            });
        }

        if (colMucThanhTien != null) {
            colMucThanhTien.setCellValueFactory(cell -> {
                ChiTietHoaDon ct = cell.getValue();
                return new SimpleStringProperty(ct != null ? dfTien.format(ct.getThanhTienSauThue()) : "0 đ");
            });
        }
    }

    /**
     * Lắng nghe sự kiện chọn một hóa đơn từ bảng danh sách chờ
     */
    private void langNgheChonDonCho() {
        if (tblDonChoThanhToan == null) return;

        tblDonChoThanhToan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            hoaDonDangChon = newVal;
            hienThiChiTietHoaDon(newVal);
        });
    }

    /**
     * Hiển thị thông tin hóa đơn được chọn lên quầy thu ngân bên phải
     */
    private void hienThiChiTietHoaDon(HoaDon hd) {
        if (hd == null) {
            if (lblMaHoaDonHienTai != null) lblMaHoaDonHienTai.setText("HÓA ĐƠN: (Chưa chọn)");
            if (lblTenKhach != null) lblTenKhach.setText("-");
            if (lblSdtKhach != null) lblSdtKhach.setText("-");
            if (lblBienSo != null) lblBienSo.setText("-");
            if (lblNhanVienLap != null) lblNhanVienLap.setText("-");
            if (lblTongTienHang != null) lblTongTienHang.setText("0 đ");
            if (lblTienGiamGia != null) lblTienGiamGia.setText("- 0 đ");
            if (lblTienThue != null) lblTienThue.setText("0 đ");
            if (lblTongCanThu != null) lblTongCanThu.setText("0 đ");
            dsChiTietThuNgan.clear();
            return;
        }

        if (lblMaHoaDonHienTai != null) {
            lblMaHoaDonHienTai.setText("HÓA ĐƠN: " + hd.getMaHoaDon());
        }

        if (lblTenKhach != null) {
            String ten = (hd.getKhachHang() != null) ? hd.getKhachHang().getHoTen() : (hd.getTenKhachLe() != null ? hd.getTenKhachLe() : "Khách lẻ");
            lblTenKhach.setText(ten);
        }

        if (lblSdtKhach != null) {
            String sdt = (hd.getKhachHang() != null) ? hd.getKhachHang().getSoDienThoai() : (hd.getSdtKhachLe() != null ? hd.getSdtKhachLe() : "-");
            lblSdtKhach.setText(sdt);
        }

        if (lblBienSo != null) {
            lblBienSo.setText("-"); // TODO: [Phân công thành viên] Lấy biển số xe qua PhieuTiepNhan nếu là đơn dịch vụ
        }

        if (lblNhanVienLap != null) {
            lblNhanVienLap.setText(hd.getNhanVienLap() != null ? hd.getNhanVienLap().getHoTen() : "-");
        }

        if (lblTongTienHang != null) lblTongTienHang.setText(dfTien.format(hd.getTongTienHang()));
        if (lblTienGiamGia != null) lblTienGiamGia.setText("- " + dfTien.format(hd.getTienKhuyenMai()));
        if (lblTienThue != null) lblTienThue.setText(dfTien.format(hd.getTienThue()));
        if (lblTongCanThu != null) lblTongCanThu.setText(dfTien.format(hd.getTongThanhToan()));

        // TODO: [Phân công thành viên] Gọi hoaDonDAO.layChiTietHoaDon(hd.getId()) để nạp vào dsChiTietThuNgan
    }

    /**
     * Lắng nghe chuyển đổi phương thức thanh toán Tiền mặt / Chuyển khoản
     */
    private void langNghePhuongThucThanhToan() {
        if (radTienMat != null && boxTienMat != null) {
            radTienMat.selectedProperty().addListener((obs, oldVal, isTienMat) -> {
                boxTienMat.setVisible(isTienMat);
                boxTienMat.setManaged(isTienMat);
            });
        }
    }

    /**
     * Lắng nghe nhập tiền khách đưa để tự động tính tiền thừa trả lại
     */
    private void langNgheTienKhachDua() {
        if (txtTienKhachDua != null) {
            txtTienKhachDua.textProperty().addListener((obs, oldVal, newVal) -> {
                tinhTienThuaTraKhach();
            });
        }
    }

    private void tinhTienThuaTraKhach() {
        if (hoaDonDangChon == null || txtTienKhachDua == null || lblTienThua == null) return;
        try {
            String cleanText = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
            if (cleanText.isEmpty()) {
                lblTienThua.setText("0 đ");
                return;
            }
            double tienKhachDua = Double.parseDouble(cleanText);
            double tongCanThu = hoaDonDangChon.getTongThanhToan();
            double tienThua = tienKhachDua - tongCanThu;
            if (tienThua >= 0) {
                lblTienThua.setText(dfTien.format(tienThua));
                lblTienThua.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #059669; -fx-padding: 6px 10px; -fx-background-radius: 6px; -fx-border-color: #cbd5e1; -fx-border-radius: 6px; -fx-font-weight: 900; -fx-font-size: 13px;");
            } else {
                lblTienThua.setText("Thiếu " + dfTien.format(Math.abs(tienThua)));
                lblTienThua.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-padding: 6px 10px; -fx-background-radius: 6px; -fx-border-color: #fecaca; -fx-border-radius: 6px; -fx-font-weight: 900; -fx-font-size: 13px;");
            }
        } catch (NumberFormatException e) {
            lblTienThua.setText("0 đ");
        }
    }

    // =========================================================================
    // 3. EVENT HANDLERS & KHUNG PHÂN CÔNG THÀNH VIÊN
    // =========================================================================

    /**
     * Tải danh sách hóa đơn in tạm (CHƯA THANH TOÁN) từ cơ sở dữ liệu
     */
    public void taiDanhSachDonChoThanhToan() {
        // TODO: [Phân công thành viên]
        // 1. Gọi hoaDonDAO.layHoaDonTheoTrangThai(TrangThaiThanhToan.CHUA_THANH_TOAN)
        // 2. Cập nhật dsDonChoThanhToan
        // 3. Cập nhật lblSoDonChoTT: "⏳ " + dsDonChoThanhToan.size() + " đơn chờ thanh toán"
        // 4. Tính tổng tiền chờ thu và gán vào lblTongTienChoThu
    }

    @FXML
    public void onLamMoiDanhSachClick(ActionEvent event) {
        taiDanhSachDonChoThanhToan();
    }

    @FXML
    public void onHuyDonClick(ActionEvent event) {
        if (hoaDonDangChon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một hóa đơn in tạm cần hủy!");
            alert.showAndWait();
            return;
        }

        // TODO: [Phân công thành viên]
        // Hiển thị dialog xác nhận hủy đơn, gọi hoaDonDAO.huyHoaDon(...) và làm mới danh sách
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                "Bạn có chắc muốn hủy hóa đơn in tạm: " + hoaDonDangChon.getMaHoaDon() + "?");
        alert.showAndWait();
    }

    @FXML
    public void onInTamTinhClick(ActionEvent event) {
        if (hoaDonDangChon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một hóa đơn để in phiếu tạm tính!");
            alert.showAndWait();
            return;
        }

        // TODO: [Phân công thành viên]
        // Kết nối mẫu in tạm tính (chưa có giá trị thuế VAT chính thức hoặc ghi chú 'BẢN IN TẠM')
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "In phiếu tạm tính cho đơn " + hoaDonDangChon.getMaHoaDon());
        alert.showAndWait();
    }

    @FXML
    public void onXacNhanThanhToanClick(ActionEvent event) {
        if (hoaDonDangChon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn một hóa đơn để xác nhận thu tiền!");
            alert.showAndWait();
            return;
        }

        // TODO: [Phân công thành viên]
        // 1. Kiểm tra số tiền khách đưa nếu là tiền mặt
        // 2. Xác định phương thức thanh toán: TIEN_MAT hoặc CHUYEN_KHOAN
        // 3. Gọi hoaDonDAO.xacNhanThanhToan(hoaDonDangChon.getId(), phuongThuc, ghiChu)
        // 4. Xuất hóa đơn VAT / Phiếu thu chính thức
        // 5. Tải lại danh sách đơn chờ thanh toán và reset khung chi tiết
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xác nhận thanh toán");
        alert.setHeaderText("Thu tiền & Hoàn tất hóa đơn: " + hoaDonDangChon.getMaHoaDon());
        alert.setContentText("Tính năng xác nhận thanh toán và in hóa đơn chính thức sẽ được hoàn thiện bởi thành viên nhóm!");
        alert.showAndWait();
    }
}

