package com.pengugarage.controller;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.entity.TaiKhoan;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.service.NhanVienService;
import com.pengugarage.service.TaiKhoanService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller quản lý Nhân Sự và Tài Khoản gara (M01-T07 & M01-T08).
 */
public class NhanVienController {

    @FXML private TextField timKiemField;
    @FXML private ComboBox<String> locChucVuCombo;
    @FXML private ComboBox<String> locTrangThaiCombo;

    @FXML private TableView<NhanVien> nhanVienTable;
    @FXML private TableColumn<NhanVien, String> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colSDT;
    @FXML private TableColumn<NhanVien, String> colDiaChi;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colTrangThai;

    @FXML private Label tongSoLabel;
    @FXML private Label thongBaoLabel;

    @FXML private TextField maNVField;
    @FXML private TextField hoTenField;
    @FXML private TextField sdtField;
    @FXML private TextField diaChiField;
    @FXML private ComboBox<ChucVu> chucVuField;
    @FXML private Button btnKhoaMoKhoa;

    private final NhanVienService nhanVienService = new NhanVienService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final ObservableList<NhanVien> danhSachNhanVien = FXCollections.observableArrayList();
    private NhanVien nhanVienDangChon = null;

    @FXML
    public void initialize() {
        // Cấu hình các cột TableView
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        
        colChucVu.setCellValueFactory(cellData -> {
            ChucVu cv = cellData.getValue().getChucVu();
            String ten = cv != null ? cv.name() : "";
            if (cv == ChucVu.QUAN_LY) ten = "Quản lý";
            else if (cv == ChucVu.KY_THUAT_VIEN) ten = "Kỹ thuật viên";
            else if (cv == ChucVu.LE_TAN) ten = "Lễ tân";
            return new SimpleStringProperty(ten);
        });

        colTrangThai.setCellValueFactory(cellData -> {
            boolean tt = cellData.getValue().isTrangThai();
            return new SimpleStringProperty(tt ? "● Hoạt động" : "✖ Đã khóa");
        });

        // Định dạng màu sắc cột Trạng thái
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Hoạt động")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        nhanVienTable.setItems(danhSachNhanVien);

        // Khởi tạo các ComboBox
        locChucVuCombo.setItems(FXCollections.observableArrayList("Tất cả", "QUAN_LY", "KY_THUAT_VIEN", "LE_TAN"));
        locChucVuCombo.setValue("Tất cả");

        locTrangThaiCombo.setItems(FXCollections.observableArrayList("Tất cả", "Đang hoạt động", "Đã khóa"));
        locTrangThaiCombo.setValue("Tất cả");

        chucVuField.setItems(FXCollections.observableArrayList(ChucVu.values()));
        chucVuField.setValue(ChucVu.KY_THUAT_VIEN);

        // Lắng nghe sự kiện click chọn hàng trong bảng
        nhanVienTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiChiTiet(newVal);
            }
        });

        // Tải dữ liệu ban đầu
        taiDanhSachNhanVien();
    }

    private void taiDanhSachNhanVien() {
        new Thread(() -> {
            List<NhanVien> list = nhanVienService.layTatCa();
            Platform.runLater(() -> {
                danhSachNhanVien.setAll(list);
                tongSoLabel.setText("Tổng số: " + list.size() + " nhân viên");
            });
        }).start();
    }

    private void hienThiChiTiet(NhanVien nv) {
        nhanVienDangChon = nv;
        maNVField.setText(nv.getMaNhanVien());
        hoTenField.setText(nv.getHoTen());
        sdtField.setText(nv.getSoDienThoai());
        diaChiField.setText(nv.getDiaChi() != null ? nv.getDiaChi() : "");
        chucVuField.setValue(nv.getChucVu());

        if (nv.isTrangThai()) {
            btnKhoaMoKhoa.setText("Khóa Tài Khoản");
            btnKhoaMoKhoa.setStyle("-fx-background-color: #dc2626; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
        } else {
            btnKhoaMoKhoa.setText("Mở Khóa Tài Khoản");
            btnKhoaMoKhoa.setStyle("-fx-background-color: #10b981; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void onTimKiemClick() {
        String tuKhoa = timKiemField.getText();
        String cvStr = locChucVuCombo.getValue();
        ChucVu cv = (cvStr == null || cvStr.equals("Tất cả")) ? null : ChucVu.valueOf(cvStr);

        String ttStr = locTrangThaiCombo.getValue();
        Boolean tt = null;
        if ("Đang hoạt động".equals(ttStr)) tt = true;
        else if ("Đã khóa".equals(ttStr)) tt = false;

        Boolean finalTt = tt;
        new Thread(() -> {
            List<NhanVien> ketQua = nhanVienService.timKiem(tuKhoa, cv, finalTt);
            Platform.runLater(() -> {
                danhSachNhanVien.setAll(ketQua);
                tongSoLabel.setText("Tìm thấy: " + ketQua.size() + " nhân viên");
            });
        }).start();
    }

    @FXML
    private void onLamMoiClick() {
        timKiemField.clear();
        locChucVuCombo.setValue("Tất cả");
        locTrangThaiCombo.setValue("Tất cả");
        taiDanhSachNhanVien();
        onXoaTrangClick();
    }

    @FXML
    private void onThemClick() {
        NhanVien nv = new NhanVien();
        nv.setHoTen(hoTenField.getText());
        nv.setSoDienThoai(sdtField.getText());
        nv.setDiaChi(diaChiField.getText());
        nv.setChucVu(chucVuField.getValue());
        nv.setTrangThai(true);

        String loi = nhanVienService.themNhanVien(nv);
        if (loi != null) {
            hienThongBao(loi, true);
        } else {
            hienThongBao("✓ Thêm nhân viên mới thành công (Mã: " + nv.getMaNhanVien() + ")!", false);
            taiDanhSachNhanVien();
            onXoaTrangClick();
        }
    }

    @FXML
    private void onCapNhatClick() {
        if (nhanVienDangChon == null) {
            hienThongBao("Vui lòng chọn nhân viên cần cập nhật từ danh sách!", true);
            return;
        }

        nhanVienDangChon.setHoTen(hoTenField.getText());
        nhanVienDangChon.setSoDienThoai(sdtField.getText());
        nhanVienDangChon.setDiaChi(diaChiField.getText());
        nhanVienDangChon.setChucVu(chucVuField.getValue());

        String loi = nhanVienService.capNhatNhanVien(nhanVienDangChon);
        if (loi != null) {
            hienThongBao(loi, true);
        } else {
            hienThongBao("✓ Cập nhật thông tin nhân viên thành công!", false);
            taiDanhSachNhanVien();
        }
    }

    @FXML
    private void onDoiTrangThaiClick() {
        if (nhanVienDangChon == null) {
            hienThongBao("Vui lòng chọn nhân viên cần đổi trạng thái!", true);
            return;
        }

        boolean trangThaiMoi = !nhanVienDangChon.isTrangThai();
        boolean thanhCong = nhanVienService.doiTrangThai(nhanVienDangChon.getId(), trangThaiMoi);
        if (thanhCong) {
            nhanVienDangChon.setTrangThai(trangThaiMoi);
            hienThiChiTiet(nhanVienDangChon);
            nhanVienTable.refresh();
            String msg = trangThaiMoi ? "✓ Đã mở khóa tài khoản nhân viên!" : "✓ Đã khóa tài khoản nhân viên!";
            hienThongBao(msg, false);
        } else {
            hienThongBao("Không thể đổi trạng thái nhân viên!", true);
        }
    }

    @FXML
    private void onXoaTrangClick() {
        nhanVienDangChon = null;
        nhanVienTable.getSelectionModel().clearSelection();
        maNVField.clear();
        hoTenField.clear();
        sdtField.clear();
        diaChiField.clear();
        chucVuField.setValue(ChucVu.KY_THUAT_VIEN);
        btnKhoaMoKhoa.setText("Khóa / Mở Khóa Nhân Viên");
        thongBaoLabel.setText("");
    }

    /**
     * M01-T08: Tạo tài khoản mới hoặc đổi mật khẩu cho nhân viên đang chọn
     */
    @FXML
    private void onQuanLyTaiKhoanClick() {
        if (nhanVienDangChon == null) {
            hienThongBao("Vui lòng chọn nhân viên để quản lý tài khoản!", true);
            return;
        }

        TaiKhoan tk = taiKhoanService.timTheoNhanVienId(nhanVienDangChon.getId());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Quản Lý Tài Khoản Nhân Viên (M01-T08)");
        dialog.setHeaderText("Nhân viên: " + nhanVienDangChon.getHoTen() + " (" + nhanVienDangChon.getMaNhanVien() + ")");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setStyle("-fx-padding: 20px;");

        TextField usernameInput = new TextField(nhanVienDangChon.getMaNhanVien());
        usernameInput.setEditable(false);
        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Tối thiểu 8 ký tự, hoa, thường, số, ký tự đặc biệt");
        PasswordField passConfirm = new PasswordField();
        passConfirm.setPromptText("Nhập lại mật khẩu");

        grid.add(new Label("Tên đăng nhập:"), 0, 0);
        grid.add(usernameInput, 1, 0);

        if (tk == null) {
            // Tạo tài khoản mới
            grid.add(new Label("Mật khẩu mới:"), 0, 1);
            grid.add(passInput, 1, 1);
            grid.add(new Label("Xác nhận:"), 0, 2);
            grid.add(passConfirm, 1, 2);
        } else {
            // Đổi mật khẩu
            PasswordField oldPass = new PasswordField();
            oldPass.setPromptText("Nhập mật khẩu hiện tại");
            grid.add(new Label("Mật khẩu cũ:"), 0, 1);
            grid.add(oldPass, 1, 1);
            grid.add(new Label("Mật khẩu mới:"), 0, 2);
            grid.add(passInput, 1, 2);
            grid.add(new Label("Xác nhận:"), 0, 3);
            grid.add(passConfirm, 1, 3);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (tk == null) {
                // Tạo mới
                if (!passInput.getText().equals(passConfirm.getText())) {
                    hienThongBao("Xác nhận mật khẩu không trùng khớp!", true);
                    return;
                }
                String err = taiKhoanService.taoTaiKhoan(nhanVienDangChon.getId(), nhanVienDangChon.getMaNhanVien(), passInput.getText());
                if (err != null) {
                    hienThongBao(err, true);
                } else {
                    hienThongBao("✓ Tạo tài khoản thành công cho " + nhanVienDangChon.getMaNhanVien() + "!", false);
                }
            } else {
                // Đổi mật khẩu
                String oldP = ((PasswordField) grid.getChildren().get(3)).getText();
                String err = taiKhoanService.doiMatKhau(tk.getId(), oldP, passInput.getText(), passConfirm.getText());
                if (err != null) {
                    hienThongBao(err, true);
                } else {
                    hienThongBao("✓ Đổi mật khẩu thành công!", false);
                }
            }
        }
    }

    @FXML
    private void onBackToDashboardClick() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengugarage/view/quanly/dashboard-view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) nhanVienTable.getScene().getWindow();
                Scene scene = new Scene(root, 1280, 760);
                stage.setScene(scene);
                stage.setTitle("Pengu Garage Service ERP - Dashboard Quản Lý");
                stage.centerOnScreen();
            } catch (IOException e) {
                hienThongBao("Lỗi mở Dashboard: " + e.getMessage(), true);
            }
        });
    }

    private void hienThongBao(String msg, boolean isError) {
        thongBaoLabel.setText(msg);
        if (isError) {
            thongBaoLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        } else {
            thongBaoLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        }
    }
}
