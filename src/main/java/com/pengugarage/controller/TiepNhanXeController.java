package com.pengugarage.controller;

import com.pengugarage.controller.component.ModalThemPhuongTienController;
import com.pengugarage.dao.KhachHangDAO;
import com.pengugarage.dao.PhuongTienDAO;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhieuTiepNhan;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.model.enums.TrangThaiPhieuTiepNhan;
import com.pengugarage.service.PhieuTiepNhanService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho Màn hình Tiếp Nhận Xe - GIAI ĐOẠN 1.
 * 
 * Phụ trách: Trần Hồng Nhiên (Module Tiếp nhận xe - M08).
 * Quản lý toàn bộ thông tin tiếp nhận ban đầu của phương tiện khi vào gara,
 * tự động sinh mã phiếu theo ngày, ghi nhận tình trạng xe và đưa xe vào hàng đợi xưởng.
 */
public class TiepNhanXeController implements Initializable {

    // =========================================================================
    // 1. FXML INJECTIONS
    // =========================================================================

    // Top Metric Cards & Badges
    @FXML private Label lblTongSoTiepNhan;
    @FXML private Label lblChoKiemTra;
    @FXML private Label lblDangKiemTra;
    @FXML private Label lblDangSuaChua;
    @FXML private Label lblHoanThanh;
    @FXML private Button btnLamMoi;

    // Bộ lọc & Tìm kiếm danh sách
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cboLocTrangThai;
    @FXML private Button btnTimKiem;
    @FXML private Button btnXoaLoc;

    // Bảng danh sách phiếu tiếp nhận
    @FXML private TableView<PhieuTiepNhan> tblPhieuTiepNhan;
    @FXML private TableColumn<PhieuTiepNhan, Integer> colStt;
    @FXML private TableColumn<PhieuTiepNhan, String> colMaTiepNhan;
    @FXML private TableColumn<PhieuTiepNhan, String> colBienSo;
    @FXML private TableColumn<PhieuTiepNhan, String> colMauXe;
    @FXML private TableColumn<PhieuTiepNhan, String> colKhachHang;
    @FXML private TableColumn<PhieuTiepNhan, String> colSoDienThoai;
    @FXML private TableColumn<PhieuTiepNhan, String> colThoiGianVao;
    @FXML private TableColumn<PhieuTiepNhan, TrangThaiPhieuTiepNhan> colTrangThai;
    @FXML private Label lblSoBanGhi;

    // Form tiếp nhận bên phải
    @FXML private Label lblFormTieuDe;
    @FXML private Label lblBadgeMaTiepNhan;
    @FXML private TextField txtBienSoXe;
    @FXML private Button btnTimXe;
    @FXML private Button btnThemPhuongTien;
    @FXML private Label lblThongTinXe;
    @FXML private Label lblThongTinKhach;

    // Các thông số tiếp nhận
    @FXML private TextField txtSoKM;
    @FXML private TextField txtSoChiaKhoa;
    @FXML private ComboBox<com.pengugarage.model.entity.NhanVien> cboKyThuatVien;
    @FXML private TextArea txtYeuCauKhach;
    @FXML private TextArea txtTinhTrangNgoaiThat;
    @FXML private TextArea txtTinhTrangNoiThat;
    @FXML private TextField txtGhiChu;

    // Nút hành động form
    @FXML private Button btnLuuPhieu;
    @FXML private Button btnTiepNhanMoi;
    @FXML private Button btnHuyForm;

    // =========================================================================
    // 2. DATA ACCESS OBJECTS, SERVICES & STATE
    // =========================================================================

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final PhuongTienDAO phuongTienDAO = new PhuongTienDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final com.pengugarage.dao.NhanVienDAO nhanVienDAO = new com.pengugarage.dao.NhanVienDAO();

    private final ObservableList<PhieuTiepNhan> dsPhieuTiepNhan = FXCollections.observableArrayList();
    private final ObservableList<com.pengugarage.model.entity.NhanVien> dsKyThuatVien = FXCollections.observableArrayList();
    private FilteredList<PhieuTiepNhan> dsLocPhieuTiepNhan;

    private PhuongTien phuongTienDangChon = null;
    private PhieuTiepNhan phieuTiepNhanDangChon = null;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =========================================================================
    // 3. KHỞI TẠO (INITIALIZE)
    // =========================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caiDatComboBoxLoc();
        caiDatComboBoxKyThuatVien();
        cauHinhBangPhieuTiepNhan();
        langNgheChonPhieuTiepNhan();
        taiDanhSachPhieuTiepNhan();
        lamMoiForm();
    }

    private void caiDatComboBoxKyThuatVien() {
        if (cboKyThuatVien == null) return;
        List<com.pengugarage.model.entity.NhanVien> list = nhanVienDAO.layDanhSachTheoChucVu(com.pengugarage.model.enums.ChucVu.KY_THUAT_VIEN);
        dsKyThuatVien.setAll(list);
        cboKyThuatVien.setItems(dsKyThuatVien);

        cboKyThuatVien.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(com.pengugarage.model.entity.NhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "👨‍🔧 " + item.getHoTen() + " (" + item.getMaNhanVien() + ")");
            }
        });
        cboKyThuatVien.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(com.pengugarage.model.entity.NhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "👨‍🔧 " + item.getHoTen() + " (" + item.getMaNhanVien() + ")");
            }
        });

        if (!dsKyThuatVien.isEmpty()) {
            cboKyThuatVien.getSelectionModel().selectFirst();
        }
    }

    private void caiDatComboBoxLoc() {
        if (cboLocTrangThai == null) return;
        ObservableList<String> items = FXCollections.observableArrayList(
                "Tất cả trạng thái",
                "Chờ tiếp nhận",
                "Chờ kiểm tra",
                "Đang kiểm tra",
                "Chờ xác nhận báo giá",
                "Đang sửa chữa",
                "Hoàn thành",
                "Đã hủy"
        );
        cboLocTrangThai.setItems(items);
        cboLocTrangThai.getSelectionModel().selectFirst();
        cboLocTrangThai.setOnAction(e -> onLocThayDoi());
    }

    private void cauHinhBangPhieuTiepNhan() {
        if (tblPhieuTiepNhan == null) return;

        dsLocPhieuTiepNhan = new FilteredList<>(dsPhieuTiepNhan, p -> true);
        tblPhieuTiepNhan.setItems(dsLocPhieuTiepNhan);

        // STT
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

        // Mã phiếu
        if (colMaTiepNhan != null) {
            colMaTiepNhan.setCellValueFactory(new PropertyValueFactory<>("maTiepNhan"));
        }

        // Biển số xe
        if (colBienSo != null) {
            colBienSo.setCellValueFactory(cell -> {
                PhieuTiepNhan p = cell.getValue();
                if (p != null && p.getPhuongTien() != null) {
                    return new SimpleStringProperty(p.getPhuongTien().getBienSoXe());
                }
                return new SimpleStringProperty("-");
            });
        }

        // Hãng / Mẫu xe
        if (colMauXe != null) {
            colMauXe.setCellValueFactory(cell -> {
                PhieuTiepNhan p = cell.getValue();
                if (p != null && p.getPhuongTien() != null) {
                    PhuongTien pt = p.getPhuongTien();
                    String hang = pt.getHangXe() != null ? pt.getHangXe().getTenHang() : "";
                    String mau = pt.getMauXe() != null ? pt.getMauXe() : "";
                    return new SimpleStringProperty((hang + " " + mau).trim());
                }
                return new SimpleStringProperty("-");
            });
        }

        // Tên khách hàng
        if (colKhachHang != null) {
            colKhachHang.setCellValueFactory(cell -> {
                PhieuTiepNhan p = cell.getValue();
                if (p != null && p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null) {
                    return new SimpleStringProperty(p.getPhuongTien().getKhachHang().getDanhXungVaHoTen());
                }
                return new SimpleStringProperty("Khách vãng lai");
            });
        }

        // Số điện thoại
        if (colSoDienThoai != null) {
            colSoDienThoai.setCellValueFactory(cell -> {
                PhieuTiepNhan p = cell.getValue();
                if (p != null && p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null) {
                    return new SimpleStringProperty(p.getPhuongTien().getKhachHang().getSoDienThoai());
                }
                return new SimpleStringProperty("-");
            });
        }

        // Thời gian tiếp nhận vào xưởng
        if (colThoiGianVao != null) {
            colThoiGianVao.setCellValueFactory(cell -> {
                PhieuTiepNhan p = cell.getValue();
                if (p != null && p.getThoiGianTiepNhan() != null) {
                    return new SimpleStringProperty(p.getThoiGianTiepNhan().format(dtf));
                }
                return new SimpleStringProperty("-");
            });
        }

        // Badge màu sắc cho Trạng thái tiếp nhận
        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
            colTrangThai.setCellFactory(col -> new TableCell<>() {
                private final Label badge = new Label();
                private final HBox container = new HBox(badge);
                {
                    container.setStyle("-fx-alignment: CENTER;");
                    badge.setStyle("-fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                }

                @Override
                protected void updateItem(TrangThaiPhieuTiepNhan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        badge.setText(formatTrangThai(item));
                        badge.setStyle(getStyleTrangThai(item));
                        setGraphic(container);
                    }
                }
            });
        }
    }

    private void langNgheChonPhieuTiepNhan() {
        if (tblPhieuTiepNhan == null) return;

        tblPhieuTiepNhan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            phieuTiepNhanDangChon = newVal;
            hienThiChiTietPhieu(newVal);
        });
    }

    private void hienThiChiTietPhieu(PhieuTiepNhan p) {
        if (p == null) {
            lamMoiForm();
            return;
        }

        if (lblFormTieuDe != null) lblFormTieuDe.setText("Chi Tiết Phiếu Tiếp Nhận");
        if (lblBadgeMaTiepNhan != null) lblBadgeMaTiepNhan.setText(p.getMaTiepNhan() != null ? p.getMaTiepNhan() : "TN-" + p.getId());

        if (p.getPhuongTien() != null) {
            phuongTienDangChon = p.getPhuongTien();
            if (txtBienSoXe != null) txtBienSoXe.setText(phuongTienDangChon.getBienSoXe());
            capNhatThongTinXeVaKhach(phuongTienDangChon);
        }

        if (txtSoKM != null) txtSoKM.setText(String.valueOf((long) p.getSoKMHienTai()));
        if (txtSoChiaKhoa != null) txtSoChiaKhoa.setText(String.valueOf(p.getSoChiaKhoa()));

        if (cboKyThuatVien != null) {
            for (com.pengugarage.model.entity.NhanVien nv : dsKyThuatVien) {
                if (nv.getId() == p.getNhanVienTiepNhanId()) {
                    cboKyThuatVien.setValue(nv);
                    break;
                }
            }
        }

        if (txtYeuCauKhach != null) txtYeuCauKhach.setText(p.getYeuCauKhachHang() != null ? p.getYeuCauKhachHang() : "");
        if (txtTinhTrangNgoaiThat != null) txtTinhTrangNgoaiThat.setText(p.getTinhTrangNgoaiThat() != null ? p.getTinhTrangNgoaiThat() : "");
        if (txtTinhTrangNoiThat != null) txtTinhTrangNoiThat.setText(p.getTinhTrangNoiThat() != null ? p.getTinhTrangNoiThat() : "");
        if (txtGhiChu != null) txtGhiChu.setText(p.getGhiChu() != null ? p.getGhiChu() : "");
    }

    private void lamMoiForm() {
        phieuTiepNhanDangChon = null;
        phuongTienDangChon = null;
        if (lblFormTieuDe != null) lblFormTieuDe.setText("Phiếu Tiếp Nhận Xe");

        // Tự động sinh mã hiển thị sẵn
        String maDuKien = phieuTiepNhanService.sinhMaTiepNhanTuDong();
        if (lblBadgeMaTiepNhan != null) lblBadgeMaTiepNhan.setText(maDuKien);

        if (txtBienSoXe != null) txtBienSoXe.clear();
        if (lblThongTinXe != null) lblThongTinXe.setText("Phương tiện: Chưa chọn xe");
        if (lblThongTinKhach != null) lblThongTinKhach.setText("Chủ xe: -");
        if (txtSoKM != null) txtSoKM.clear();
        if (txtSoChiaKhoa != null) txtSoChiaKhoa.setText("1");
        if (cboKyThuatVien != null && !dsKyThuatVien.isEmpty()) {
            cboKyThuatVien.getSelectionModel().selectFirst();
        }
        if (txtYeuCauKhach != null) txtYeuCauKhach.clear();
        if (txtTinhTrangNgoaiThat != null) txtTinhTrangNgoaiThat.clear();
        if (txtTinhTrangNoiThat != null) txtTinhTrangNoiThat.clear();
        if (txtGhiChu != null) txtGhiChu.clear();
    }

    private void capNhatThongTinXeVaKhach(PhuongTien pt) {
        if (pt == null) {
            if (lblThongTinXe != null) lblThongTinXe.setText("Phương tiện: Không tìm thấy xe");
            if (lblThongTinKhach != null) lblThongTinKhach.setText("Chủ xe: -");
            return;
        }

        String hang = pt.getHangXe() != null ? pt.getHangXe().getTenHang() : "";
        String mau = pt.getMauXe() != null ? pt.getMauXe() : "";
        String mauSac = pt.getMauSacXe() != null ? " - Màu: " + pt.getMauSacXe() : "";
        if (lblThongTinXe != null) {
            lblThongTinXe.setText("🚗 " + pt.getBienSoXe() + " | " + hang + " " + mau + mauSac);
        }

        KhachHang kh = pt.getKhachHang();
        if (lblThongTinKhach != null) {
            if (kh != null) {
                lblThongTinKhach.setText("👤 Chủ xe: " + kh.getDanhXungVaHoTen() + " (📞 " + kh.getSoDienThoai() + ")");
            } else {
                lblThongTinKhach.setText("👤 Chủ xe: Khách lẻ / Chưa liên kết hồ sơ");
            }
        }
    }

    // =========================================================================
    // 4. EVENT HANDLERS & NHIỆM VỤ THÀNH VIÊN (TRẦN HỒNG NHIÊN)
    // =========================================================================

    /**
     * Tải danh sách toàn bộ phiếu tiếp nhận và cập nhật các thẻ thống kê Metric Cards
     */
    public void taiDanhSachPhieuTiepNhan() {
        // [M08-T01 - Nhiên]
        List<PhieuTiepNhan> list = phieuTiepNhanService.layTatCa();
        dsPhieuTiepNhan.setAll(list);
        capNhatMetricCards();
        if (lblSoBanGhi != null) {
            lblSoBanGhi.setText("Hiển thị " + dsPhieuTiepNhan.size() + " phiếu tiếp nhận");
        }
    }

    /**
     * Tra cứu phương tiện theo Biển số xe
     */
    @FXML
    public void onTimXeClick(ActionEvent event) {
        String bienSo = txtBienSoXe != null ? txtBienSoXe.getText().trim() : "";
        if (bienSo.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập biển số xe cần tra cứu!");
            alert.showAndWait();
            return;
        }

        // [M08-T02 - Nhiên] Gọi phuongTienDAO tìm theo biển số xe
        PhuongTien pt = phuongTienDAO.timTheoBienSo(bienSo);
        if (pt != null) {
            this.phuongTienDangChon = pt;
            capNhatThongTinXeVaKhach(pt);
        } else {
            this.phuongTienDangChon = null;
            capNhatThongTinXeVaKhach(null);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Không tìm thấy xe");
            alert.setHeaderText("Biển số xe: " + bienSo + " chưa có trong hệ thống.");
            alert.setContentText("Bạn có muốn tạo mới thông tin phương tiện và chủ xe ngay bây giờ không?");
            ButtonType btnTaoMoi = new ButtonType("➕ Tạo Mới Xe", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnHuy = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnTaoMoi, btnHuy);
            alert.showAndWait().ifPresent(res -> {
                if (res == btnTaoMoi) {
                    moModalThemPhuongTien(bienSo);
                }
            });
        }
    }

    /**
     * Mở modal thêm mới phương tiện và hồ sơ khách hàng
     */
    @FXML
    public void onThemPhuongTienClick(ActionEvent event) {
        String bienSo = txtBienSoXe != null ? txtBienSoXe.getText().trim() : "";
        moModalThemPhuongTien(bienSo);
    }

    private void moModalThemPhuongTien(String bienSoMacDinh) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengugarage/view/letan/component/modal-them-phuong-tien.fxml"));
            Parent root = loader.load();
            ModalThemPhuongTienController modalCtrl = loader.getController();
            if (modalCtrl != null && bienSoMacDinh != null && !bienSoMacDinh.isBlank()) {
                modalCtrl.setBienSoKhoiTao(bienSoMacDinh);
            }

            Stage stage = new Stage();
            stage.setTitle("Pengu Garage - Thêm Mới Phương Tiện & Khách Hàng");
            stage.initModality(Modality.APPLICATION_MODAL);
            if (txtBienSoXe != null && txtBienSoXe.getScene() != null && txtBienSoXe.getScene().getWindow() != null) {
                stage.initOwner(txtBienSoXe.getScene().getWindow());
            }
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (modalCtrl != null && modalCtrl.getPhuongTienVuaTao() != null) {
                PhuongTien ptMoi = modalCtrl.getPhuongTienVuaTao();
                this.phuongTienDangChon = ptMoi;
                if (txtBienSoXe != null) txtBienSoXe.setText(ptMoi.getBienSoXe());
                capNhatThongTinXeVaKhach(ptMoi);
                if (txtSoKM != null) txtSoKM.requestFocus();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Lỗi khi mở cửa sổ thêm mới phương tiện: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Lọc và tìm kiếm phiếu tiếp nhận trên giao diện
     */
    @FXML
    public void onTimKiemClick(ActionEvent event) {
        onLocThayDoi();
    }

    @FXML
    public void onXoaLocClick(ActionEvent event) {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (cboLocTrangThai != null) cboLocTrangThai.getSelectionModel().selectFirst();
        onLocThayDoi();
    }

    private void onLocThayDoi() {
        if (dsLocPhieuTiepNhan == null) return;

        String keyword = txtTimKiem != null && txtTimKiem.getText() != null ? txtTimKiem.getText().trim().toLowerCase() : "";
        String statusFilter = cboLocTrangThai != null && cboLocTrangThai.getValue() != null ? cboLocTrangThai.getValue() : "Tất cả trạng thái";

        dsLocPhieuTiepNhan.setPredicate(ptn -> {
            // Lọc theo từ khóa
            boolean matchKeyword = true;
            if (!keyword.isEmpty()) {
                String ma = ptn.getMaTiepNhan() != null ? ptn.getMaTiepNhan().toLowerCase() : "";
                String bienSo = (ptn.getPhuongTien() != null && ptn.getPhuongTien().getBienSoXe() != null)
                        ? ptn.getPhuongTien().getBienSoXe().toLowerCase() : "";
                String tenKhach = (ptn.getPhuongTien() != null && ptn.getPhuongTien().getKhachHang() != null
                        && ptn.getPhuongTien().getKhachHang().getHoTen() != null)
                        ? ptn.getPhuongTien().getKhachHang().getHoTen().toLowerCase() : "";
                matchKeyword = ma.contains(keyword) || bienSo.contains(keyword) || tenKhach.contains(keyword);
            }

            // Lọc theo trạng thái
            boolean matchStatus = true;
            if (!"Tất cả trạng thái".equals(statusFilter)) {
                matchStatus = formatTrangThai(ptn.getTrangThai()).equalsIgnoreCase(statusFilter);
            }

            return matchKeyword && matchStatus;
        });

        if (lblSoBanGhi != null) {
            lblSoBanGhi.setText("Hiển thị " + dsLocPhieuTiepNhan.size() + " phiếu tiếp nhận");
        }
    }

    /**
     * Tạo phiếu tiếp nhận xe mới hoặc Cập nhật phiếu tiếp nhận
     */
    @FXML
    public void onLuuPhieuClick(ActionEvent event) {
        // [M08-T02 / M08-T03 - Nhiên]
        // 1. Validate Form
        if (phuongTienDangChon == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng tra cứu và chọn một phương tiện trước khi lập phiếu!").showAndWait();
            if (txtBienSoXe != null) txtBienSoXe.requestFocus();
            return;
        }

        String strKM = txtSoKM != null && txtSoKM.getText() != null ? txtSoKM.getText().trim() : "";
        if (strKM.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập số KM / ODO hiện tại của xe!").showAndWait();
            if (txtSoKM != null) txtSoKM.requestFocus();
            return;
        }

        double soKM = 0;
        try {
            soKM = Double.parseDouble(strKM);
            if (soKM < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Số KM phải là một số hợp lệ lớn hơn hoặc bằng 0!").showAndWait();
            if (txtSoKM != null) txtSoKM.requestFocus();
            return;
        }

        String yeuCau = txtYeuCauKhach != null && txtYeuCauKhach.getText() != null ? txtYeuCauKhach.getText().trim() : "";
        if (yeuCau.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng ghi nhận yêu cầu bảo dưỡng/hỏng hóc của khách hàng!").showAndWait();
            if (txtYeuCauKhach != null) txtYeuCauKhach.requestFocus();
            return;
        }

        int soChiaKhoa = 1;
        try {
            if (txtSoChiaKhoa != null) {
                soChiaKhoa = Integer.parseInt(txtSoChiaKhoa.getText().trim());
            }
        } catch (Exception ignored) {}

        com.pengugarage.model.entity.NhanVien ktvPhuTrach = cboKyThuatVien != null ? cboKyThuatVien.getValue() : null;
        if (ktvPhuTrach == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Kỹ thuật viên phụ trách kiểm tra chiếc xe này!").showAndWait();
            if (cboKyThuatVien != null) cboKyThuatVien.requestFocus();
            return;
        }

        // 2. Nhánh Thêm Mới hoặc Cập Nhật
        if (phieuTiepNhanDangChon == null) {
            // --- THÊM MỚI PHIẾU TIẾP NHẬN [M08-T02] ---
            PhieuTiepNhan ptn = new PhieuTiepNhan();
            // Tự sinh mã theo cú pháp: TN{ngày tiếp nhận}-{3 ký số} (VD: TN20261003-003)
            String maTN = phieuTiepNhanService.sinhMaTiepNhanTuDong();
            ptn.setMaTiepNhan(maTN);
            ptn.setPhuongTienId(phuongTienDangChon.getId());
            ptn.setNhanVienTiepNhanId(ktvPhuTrach.getId());
            ptn.setNhanVienTiepNhan(ktvPhuTrach);
            ptn.setThoiGianTiepNhan(LocalDateTime.now());
            ptn.setTrangThai(TrangThaiPhieuTiepNhan.CHO_TIEP_NHAN);
            ptn.setSoKMHienTai(soKM);
            ptn.setSoChiaKhoa(soChiaKhoa);
            ptn.setYeuCauKhachHang(yeuCau);
            ptn.setTinhTrangNgoaiThat(txtTinhTrangNgoaiThat != null && txtTinhTrangNgoaiThat.getText() != null ? txtTinhTrangNgoaiThat.getText().trim() : "");
            ptn.setTinhTrangNoiThat(txtTinhTrangNoiThat != null && txtTinhTrangNoiThat.getText() != null ? txtTinhTrangNoiThat.getText().trim() : "");
            ptn.setGhiChu(txtGhiChu != null && txtGhiChu.getText() != null ? txtGhiChu.getText().trim() : "");

            boolean thanhCong = phieuTiepNhanService.themPhieuTiepNhan(ptn);
            if (thanhCong) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Tiếp nhận thành công");
                alert.setHeaderText("Đã lập phiếu tiếp nhận xe vào gara!");
                alert.setContentText("Mã phiếu: " + maTN + "\nBiển số xe: " + phuongTienDangChon.getBienSoXe() + 
                                     "\nKTV kiểm tra xe: " + ktvPhuTrach.getHoTen() + " (" + ktvPhuTrach.getMaNhanVien() + ")" +
                                     "\nTrạng thái: Chờ tiếp nhận");
                alert.showAndWait();

                lamMoiForm();
                taiDanhSachPhieuTiepNhan();
            } else {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu phiếu tiếp nhận vào CSDL! Vui lòng thử lại.").showAndWait();
            }

        } else {
            // --- CẬP NHẬT PHIẾU TIẾP NHẬN ĐANG CHỌN [M08-T03] ---
            phieuTiepNhanDangChon.setNhanVienTiepNhanId(ktvPhuTrach.getId());
            phieuTiepNhanDangChon.setNhanVienTiepNhan(ktvPhuTrach);
            phieuTiepNhanDangChon.setSoKMHienTai(soKM);
            phieuTiepNhanDangChon.setSoChiaKhoa(soChiaKhoa);
            phieuTiepNhanDangChon.setYeuCauKhachHang(yeuCau);
            phieuTiepNhanDangChon.setTinhTrangNgoaiThat(txtTinhTrangNgoaiThat != null && txtTinhTrangNgoaiThat.getText() != null ? txtTinhTrangNgoaiThat.getText().trim() : "");
            phieuTiepNhanDangChon.setTinhTrangNoiThat(txtTinhTrangNoiThat != null && txtTinhTrangNoiThat.getText() != null ? txtTinhTrangNoiThat.getText().trim() : "");
            phieuTiepNhanDangChon.setGhiChu(txtGhiChu != null && txtGhiChu.getText() != null ? txtGhiChu.getText().trim() : "");

            boolean thanhCong = phieuTiepNhanService.capNhatPhieuTiepNhan(phieuTiepNhanDangChon);
            if (thanhCong) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Cập nhật thành công");
                alert.setHeaderText("Đã cập nhật thông tin phiếu tiếp nhận: " + phieuTiepNhanDangChon.getMaTiepNhan());
                alert.setContentText("Thông tin hiện trạng xe và yêu cầu dịch vụ đã được ghi nhận.");
                alert.showAndWait();

                taiDanhSachPhieuTiepNhan();
            } else {
                new Alert(Alert.AlertType.ERROR, "Lỗi khi cập nhật phiếu tiếp nhận vào CSDL!").showAndWait();
            }
        }
    }

    @FXML
    public void onTiepNhanMoiClick(ActionEvent event) {
        if (tblPhieuTiepNhan != null) tblPhieuTiepNhan.getSelectionModel().clearSelection();
        lamMoiForm();
        if (txtBienSoXe != null) txtBienSoXe.requestFocus();
    }

    @FXML
    public void onHuyFormClick(ActionEvent event) {
        lamMoiForm();
    }

    @FXML
    public void onLamMoiClick(ActionEvent event) {
        taiDanhSachPhieuTiepNhan();
    }

    private void capNhatMetricCards() {
        long choKiemTra = 0;
        long dangKiemTra = 0;
        long dangSua = 0;
        long hoanThanh = 0;

        for (PhieuTiepNhan p : dsPhieuTiepNhan) {
            if (p.getTrangThai() == null) continue;
            switch (p.getTrangThai()) {
                case CHO_TIEP_NHAN, DA_TIEP_NHAN -> choKiemTra++;
                case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA -> dangKiemTra++;
                case DANG_SUA_CHUA -> dangSua++;
                case HOAN_THANH -> hoanThanh++;
                default -> {}
            }
        }

        if (lblTongSoTiepNhan != null) lblTongSoTiepNhan.setText("🚗 Tiếp nhận hôm nay: " + dsPhieuTiepNhan.size() + " xe");
        if (lblChoKiemTra != null) lblChoKiemTra.setText(choKiemTra + " xe");
        if (lblDangKiemTra != null) lblDangKiemTra.setText(dangKiemTra + " xe");
        if (lblDangSuaChua != null) lblDangSuaChua.setText(dangSua + " xe");
        if (lblHoanThanh != null) lblHoanThanh.setText(hoanThanh + " xe");
    }

    // =========================================================================
    // 5. HELPER METHODS FOR STATUS PILLS
    // =========================================================================

    private static String formatTrangThai(TrangThaiPhieuTiepNhan tt) {
        if (tt == null) return "Chưa xác định";
        return switch (tt) {
            case CHO_TIEP_NHAN -> "Chờ tiếp nhận";
            case DA_TIEP_NHAN -> "Chờ kiểm tra";
            case DANG_KIEM_TRA -> "Đang kiểm tra";
            case CHO_XAC_NHAN_BAO_GIA -> "Chờ xác nhận báo giá";
            case DANG_SUA_CHUA -> "Đang sửa chữa";
            case HOAN_THANH -> "Hoàn thành";
            case DA_HUY -> "Đã hủy";
        };
    }

    private static String getStyleTrangThai(TrangThaiPhieuTiepNhan tt) {
        if (tt == null) return "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;";
        return switch (tt) {
            case CHO_TIEP_NHAN, DA_TIEP_NHAN ->
                    "-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;";
            case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA ->
                    "-fx-background-color: #fff7ed; -fx-text-fill: #ea580c; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;";
            case DANG_SUA_CHUA ->
                    "-fx-background-color: #f5f3ff; -fx-text-fill: #7c3aed; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;";
            case HOAN_THANH ->
                    "-fx-background-color: #f0fdf4; -fx-text-fill: #059669; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;";
            case DA_HUY ->
                    "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-padding: 3px 8px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;";
        };
    }
}
