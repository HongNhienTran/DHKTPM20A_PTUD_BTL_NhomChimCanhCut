package com.pengugarage.controller;

import com.pengugarage.dao.KhachHangDAO;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhuongTien;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * Controller cho Màn hình Quản Lý Hồ Sơ Khách Hàng (ho-so-khach-hang-view.fxml).
 * Role: Tiếp nhận & Bán hàng (Lễ tân, Thu ngân, Cố vấn dịch vụ).
 *
 * Lưu ý: Khung code-base chuẩn bị sẵn cấu trúc giao diện, ràng buộc trường dữ liệu
 * và các phương thức xử lý sự kiện kèm ghi chú TODO để phân công thành viên thực hiện code nghiệp vụ sau.
 */
public class HoSoKhachHangController {

    // =========================================================================
    // 1. FXML UI CONTROLS
    // =========================================================================

    // Top Bar & Stats
    @FXML private Label lblTongSoKhachHang;
    @FXML private Button btnThemMoi;
    @FXML private Button btnLamMoi;

    // Search & Filter
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cboLocGioiTinh;
    @FXML private Button btnTimKiem;
    @FXML private Button btnXoaLoc;

    // Bảng Khách Hàng
    @FXML private Label lblSoBanGhi;
    @FXML private TableView<KhachHang> tblKhachHang;
    @FXML private TableColumn<KhachHang, Integer> colStt;
    @FXML private TableColumn<KhachHang, String> colMaKH;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colSoDienThoai;
    @FXML private TableColumn<KhachHang, String> colGioiTinh;
    @FXML private TableColumn<KhachHang, String> colDiaChi;
    @FXML private TableColumn<KhachHang, Void> colThaoTac;

    // Chi Tiết & Form Khách Hàng
    @FXML private Label lblChiTietTieuDe;
    @FXML private Label lblBadgeMaKH;
    @FXML private TextField txtFormMaKH;
    @FXML private ComboBox<String> cboFormGioiTinh;
    @FXML private TextField txtFormHoTen;
    @FXML private TextField txtFormSoDienThoai;
    @FXML private TextField txtFormDiaChi;
    @FXML private Button btnHuyForm;
    @FXML private Button btnLuuForm;

    // Khối Phương Tiện Của Khách Hàng
    @FXML private Button btnThemXe;
    @FXML private TableView<PhuongTien> tblXeKhachHang;
    @FXML private TableColumn<PhuongTien, Integer> colXeStt;
    @FXML private TableColumn<PhuongTien, String> colXeBienSo;
    @FXML private TableColumn<PhuongTien, String> colXeHangMau;
    @FXML private TableColumn<PhuongTien, String> colXeMauSac;
    @FXML private TableColumn<PhuongTien, Integer> colXeNamSX;

    // Tóm tắt hoạt động gần nhất
    @FXML private Label lblLanDichVuGanNhat;
    @FXML private Label lblTongChiTieu;

    // =========================================================================
    // 2. DATA PROPERTIES & UTILITIES
    // =========================================================================

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final ObservableList<KhachHang> dsKhachHang = FXCollections.observableArrayList();
    private final ObservableList<PhuongTien> dsXeKhachHang = FXCollections.observableArrayList();
    private KhachHang khachHangDangChon = null;

    @FXML
    public void initialize() {
        khoiTaoBoLocVaForm();
        cauHinhBangKhachHang();
        cauHinhBangXe();
        langNgheChonKhachHang();
        taiDanhSachKhachHang();
    }

    /**
     * Khởi tạo giá trị cho các ComboBox
     */
    private void khoiTaoBoLocVaForm() {
        if (cboLocGioiTinh != null) {
            cboLocGioiTinh.setItems(FXCollections.observableArrayList("Tất cả giới tính", "Nam", "Nữ"));
            cboLocGioiTinh.getSelectionModel().selectFirst();
        }

        if (cboFormGioiTinh != null) {
            cboFormGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
            cboFormGioiTinh.getSelectionModel().selectFirst();
        }
    }

    /**
     * Cấu hình bảng danh sách khách hàng
     */
    private void cauHinhBangKhachHang() {
        if (tblKhachHang == null) return;

        tblKhachHang.setItems(dsKhachHang);

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

        if (colMaKH != null) {
            colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        }

        if (colHoTen != null) {
            colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        }

        if (colSoDienThoai != null) {
            colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        }

        if (colGioiTinh != null) {
            colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        }

        if (colDiaChi != null) {
            colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        }

        // Cột Thao Tác (Nút Sửa & Nút Xóa)
        if (colThaoTac != null) {
            colThaoTac.setCellFactory(col -> new TableCell<>() {
                private final Button btnSua = new Button("✏️");
                private final Button btnXoa = new Button("🗑️");
                private final HBox box = new HBox(6, btnSua, btnXoa);

                {
                    btnSua.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #102a46; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 4px 8px;");
                    btnXoa.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 4px 8px;");

                    btnSua.setTooltip(new Tooltip("Chỉnh sửa thông tin khách hàng"));
                    btnXoa.setTooltip(new Tooltip("Xóa hồ sơ khách hàng"));

                    btnSua.setOnAction(e -> {
                        KhachHang kh = getTableView().getItems().get(getIndex());
                        onSuaKhachHang(kh);
                    });

                    btnXoa.setOnAction(e -> {
                        KhachHang kh = getTableView().getItems().get(getIndex());
                        onXoaKhachHang(kh);
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
     * Cấu hình bảng hiển thị xe của khách hàng
     */
    private void cauHinhBangXe() {
        if (tblXeKhachHang == null) return;

        tblXeKhachHang.setItems(dsXeKhachHang);

        if (colXeStt != null) {
            colXeStt.setCellFactory(col -> new TableCell<>() {
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

        if (colXeBienSo != null) {
            colXeBienSo.setCellValueFactory(new PropertyValueFactory<>("bienSoXe"));
            colXeBienSo.setStyle("-fx-font-weight: bold; -fx-text-fill: #102a46;");
        }

        if (colXeHangMau != null) {
            colXeHangMau.setCellValueFactory(cell -> {
                PhuongTien pt = cell.getValue();
                if (pt != null) {
                    String hang = pt.getHangXe() != null ? pt.getHangXe().getTenHang() : "";
                    String mau = pt.getMauXe() != null ? pt.getMauXe() : "";
                    return new SimpleStringProperty((hang + " " + mau).trim());
                }
                return new SimpleStringProperty("-");
            });
        }

        if (colXeMauSac != null) {
            colXeMauSac.setCellValueFactory(new PropertyValueFactory<>("mauSacXe"));
        }

        if (colXeNamSX != null) {
            colXeNamSX.setCellValueFactory(new PropertyValueFactory<>("namSanXuat"));
        }
    }

    /**
     * Lắng nghe sự kiện chọn khách hàng trên bảng
     */
    private void langNgheChonKhachHang() {
        if (tblKhachHang == null) return;

        tblKhachHang.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            khachHangDangChon = newVal;
            hienThiChiTietKhachHang(newVal);
        });
    }

    /**
     * Đổ dữ liệu khách hàng được chọn sang Form chi tiết bên phải
     */
    private void hienThiChiTietKhachHang(KhachHang kh) {
        if (kh == null) {
            lamMoiForm();
            return;
        }

        if (lblChiTietTieuDe != null) lblChiTietTieuDe.setText(kh.getHoTen());
        if (lblBadgeMaKH != null) lblBadgeMaKH.setText(kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "KH" + kh.getId());
        if (txtFormMaKH != null) txtFormMaKH.setText(kh.getMaKhachHang());
        if (txtFormHoTen != null) txtFormHoTen.setText(kh.getHoTen());
        if (txtFormSoDienThoai != null) txtFormSoDienThoai.setText(kh.getSoDienThoai());
        if (txtFormDiaChi != null) txtFormDiaChi.setText(kh.getDiaChi());
        if (cboFormGioiTinh != null && kh.getGioiTinh() != null) {
            cboFormGioiTinh.getSelectionModel().select(kh.getGioiTinh());
        }

        // TODO: [M05-T02 - Phong] Gọi khachHangDAO.layDanhSachXeCuaKhach(kh.getId()) và nạp vào dsXeKhachHang
    }

    private void lamMoiForm() {
        khachHangDangChon = null;
        if (lblChiTietTieuDe != null) lblChiTietTieuDe.setText("Thông Tin Khách Hàng");
        if (lblBadgeMaKH != null) lblBadgeMaKH.setText("Tạo mới");
        if (txtFormMaKH != null) txtFormMaKH.clear();
        if (txtFormHoTen != null) txtFormHoTen.clear();
        if (txtFormSoDienThoai != null) txtFormSoDienThoai.clear();
        if (txtFormDiaChi != null) txtFormDiaChi.clear();
        if (cboFormGioiTinh != null) cboFormGioiTinh.getSelectionModel().selectFirst();
        dsXeKhachHang.clear();
        if (lblLanDichVuGanNhat != null) lblLanDichVuGanNhat.setText("Chưa có dữ liệu");
        if (lblTongChiTieu != null) lblTongChiTieu.setText("0 đ");
    }

    // =========================================================================
    // 3. EVENT HANDLERS & KHUNG PHÂN CÔNG THÀNH VIÊN
    // =========================================================================

    /**
     * Tải danh sách toàn bộ khách hàng từ DB và thiết lập tìm kiếm lọc trên giao diện
     */
    public void taiDanhSachKhachHang() {
        // TODO: [M05-T01 - Phong]
        // 1. Gọi khachHangDAO.layTatCa()
        // 2. Cập nhật dsKhachHang và thiết lập FilteredList để tìm kiếm, lọc theo tên, SĐT, giới tính trên giao diện
        // 3. Cập nhật lblTongSoKhachHang và lblSoBanGhi
    }

    @FXML
    public void onTimKiemClick(ActionEvent event) {
        // TODO: [M05-T01 - Phong] Lọc dữ liệu trên giao diện qua FilteredList
    }

    @FXML
    public void onXoaLocClick(ActionEvent event) {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (cboLocGioiTinh != null) cboLocGioiTinh.getSelectionModel().selectFirst();
        taiDanhSachKhachHang();
    }

    @FXML
    public void onThemMoiClick(ActionEvent event) {
        if (tblKhachHang != null) tblKhachHang.getSelectionModel().clearSelection();
        lamMoiForm();
        if (txtFormHoTen != null) txtFormHoTen.requestFocus();
    }

    @FXML
    public void onLamMoiClick(ActionEvent event) {
        taiDanhSachKhachHang();
    }

    @FXML
    public void onLuuFormClick(ActionEvent event) {
        // TODO: [M05-T03 / M05-T04 - Phong]
        // 1. Validate form: Họ tên không rỗng, SĐT hợp lệ (10 chữ số)
        // 2. Nếu khachHangDangChon == null: Thêm mới khachHangDAO.them(new KhachHang(...)) [M05-T03]
        // 3. Nếu khachHangDangChon != null: Cập nhật khachHangDAO.capNhat(...) [M05-T04]
        // 4. Thông báo kết quả và làm mới danh sách
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lưu hồ sơ");
        alert.setHeaderText("Lưu thông tin khách hàng");
        alert.setContentText("Tính năng lưu/cập nhật hồ sơ khách hàng sẽ được hoàn thiện bởi thành viên nhóm!");
        alert.showAndWait();
    }

    @FXML
    public void onHuyFormClick(ActionEvent event) {
        lamMoiForm();
    }

    @FXML
    public void onThemXeClick(ActionEvent event) {
        if (khachHangDangChon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn hoặc lưu khách hàng trước khi thêm xe!");
            alert.showAndWait();
            return;
        }

        // TODO: [Phân công thành viên]
        // Mở modal/dialog thêm phương tiện mới cho khách hàng: biển số xe, hãng xe, mẫu xe, năm sản xuất
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Mở form thêm xe mới cho khách: " + khachHangDangChon.getHoTen());
        alert.showAndWait();
    }

    private void onSuaKhachHang(KhachHang kh) {
        if (kh != null) {
            khachHangDangChon = kh;
            hienThiChiTietKhachHang(kh);
        }
    }

    private void onXoaKhachHang(KhachHang kh) {
        if (kh == null) return;
        // TODO: [Phân công thành viên]
        // Kiểm tra ràng buộc và gọi khachHangDAO.xoa(kh.getId())
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa hồ sơ khách hàng: " + kh.getHoTen() + "?");
        alert.showAndWait();
    }
}
