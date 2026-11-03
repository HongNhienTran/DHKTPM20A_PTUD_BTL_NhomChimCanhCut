package com.pengugarage.controller;

import com.pengugarage.dao.DanhMucDAO;
import com.pengugarage.dao.SanPhamDAO;
import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.model.enums.DonViTinh;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Controller cho màn hình Tra Cứu Sản Phẩm & Phụ Tùng (Role Tiếp nhận & Bán hàng).
 * Cung cấp khả năng tìm kiếm, xem danh sách và thông tin chi tiết sản phẩm.
 * Lưu ý nghiệp vụ bảo mật:
 * - Chế độ CHỈ XEM (Read-only), không cho phép thêm/sửa/xóa.
 * - Tuyệt đối KHÔNG hiển thị giá nhập kho (giaNhap).
 */
public class TraCuuSanPhamController {

    @FXML private Label tongSoLabel;
    @FXML private TextField timKiemField;
    @FXML private ComboBox<DanhMucSanPham> locDanhMucCombo;
    @FXML private ComboBox<String> locTonKhoCombo;
    @FXML private ComboBox<String> locTrangThaiCombo;

    @FXML private TableView<SanPham> sanPhamTable;
    @FXML private TableColumn<SanPham, String> colMaSP;
    @FXML private TableColumn<SanPham, String> colTenSP;
    @FXML private TableColumn<SanPham, String> colDanhMuc;
    @FXML private TableColumn<SanPham, String> colDVT;
    @FXML private TableColumn<SanPham, String> colGiaBan;
    @FXML private TableColumn<SanPham, String> colTonKho;
    @FXML private TableColumn<SanPham, String> colTrangThai;

    // Khung xem chi tiết
    @FXML private VBox paneChiTiet;
    @FXML private Label lblDetailMaSP;
    @FXML private Label lblDetailTenSP;
    @FXML private Label lblDetailDanhMuc;
    @FXML private Label lblDetailDVT;
    @FXML private Label lblDetailTrangThai;
    @FXML private Label lblDetailGiaBan;
    @FXML private Label lblDetailTonKho;
    @FXML private Label lblDetailTonToiThieu;
    @FXML private Label lblDetailVAT;
    @FXML private ImageView detailImageView;
    @FXML private Label detailPlaceholderLabel;

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();

    private final ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();
    private FilteredList<SanPham> danhSachLoc;
    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");
    private boolean dangCapNhatBoLoc = false;

    @FXML
    public void initialize() {
        cauHinhBangSanPham();
        khoiTaoBoLoc();
        taiDuLieuTuDatabase();

        // Lắng nghe chọn dòng để hiển thị khung chi tiết
        sanPhamTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiChiTiet(newVal);
            } else {
                anChiTiet();
            }
        });
    }

    private void cauHinhBangSanPham() {
        colMaSP.setCellValueFactory(new PropertyValueFactory<>("maSanPham"));
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));

        colDanhMuc.setCellValueFactory(cellData -> {
            DanhMucSanPham dm = cellData.getValue().getDanhMuc();
            return new SimpleStringProperty(dm != null ? dm.getTenDanhMuc() : "Chưa phân loại");
        });

        colDVT.setCellValueFactory(cellData -> {
            DonViTinh dvt = cellData.getValue().getDonViTinh();
            return new SimpleStringProperty(layTenHienThiDVT(dvt));
        });

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
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else if (sp.getSoLuongTon() <= sp.getSoLuongTonToiThieu()) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });

        colTrangThai.setCellValueFactory(cellData -> {
            boolean tt = cellData.getValue().isTrangThai();
            return new SimpleStringProperty(tt ? "● Đang kinh doanh" : "✖ Ngừng kinh doanh");
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
                    if (item.contains("Đang")) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #94a3b8;");
                    }
                }
            }
        });
    }

    private void khoiTaoBoLoc() {
        danhSachLoc = new FilteredList<>(danhSachSanPham, p -> true);

        // ComboBox Danh mục
        locDanhMucCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(DanhMucSanPham item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenDanhMuc());
            }
        });
        locDanhMucCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DanhMucSanPham item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenDanhMuc());
            }
        });

        // ComboBox Tồn kho
        locTonKhoCombo.setItems(FXCollections.observableArrayList(
                "Tất cả tồn kho", "Còn hàng", "Sắp hết hàng", "Hết hàng"
        ));
        locTonKhoCombo.setValue("Tất cả tồn kho");

        // ComboBox Trạng thái
        locTrangThaiCombo.setItems(FXCollections.observableArrayList(
                "Tất cả trạng thái", "Đang kinh doanh", "Ngừng kinh doanh"
        ));
        locTrangThaiCombo.setValue("Tất cả trạng thái");

        // Gắn listener lọc
        timKiemField.textProperty().addListener((obs, oldVal, newVal) -> apDungBoLoc());
        locDanhMucCombo.valueProperty().addListener((obs, oldVal, newVal) -> apDungBoLoc());
        locTonKhoCombo.valueProperty().addListener((obs, oldVal, newVal) -> apDungBoLoc());
        locTrangThaiCombo.valueProperty().addListener((obs, oldVal, newVal) -> apDungBoLoc());

        SortedList<SanPham> danhSachSapXep = new SortedList<>(danhSachLoc);
        danhSachSapXep.comparatorProperty().bind(sanPhamTable.comparatorProperty());
        sanPhamTable.setItems(danhSachSapXep);
    }

    private void apDungBoLoc() {
        if (dangCapNhatBoLoc) return;

        String tuKhoa = timKiemField.getText() != null ? timKiemField.getText().trim().toLowerCase() : "";
        DanhMucSanPham dmChon = locDanhMucCombo.getValue();
        Integer danhMucIdChon = (dmChon != null && dmChon.getId() > 0) ? dmChon.getId() : null;

        String tonKhoChon = locTonKhoCombo.getValue();
        String trangThaiChon = locTrangThaiCombo.getValue();

        danhSachLoc.setPredicate(sp -> {
            // Lọc từ khóa (Mã SP, Tên SP, ĐVT)
            if (!tuKhoa.isEmpty()) {
                boolean matchMa = sp.getMaSanPham() != null && sp.getMaSanPham().toLowerCase().contains(tuKhoa);
                boolean matchTen = sp.getTenSanPham() != null && sp.getTenSanPham().toLowerCase().contains(tuKhoa);
                String tenDvt = layTenHienThiDVT(sp.getDonViTinh()).toLowerCase();
                boolean matchDvt = tenDvt.contains(tuKhoa);

                if (!matchMa && !matchTen && !matchDvt) {
                    return false;
                }
            }

            // Lọc theo Danh mục
            if (danhMucIdChon != null && sp.getDanhMucId() != danhMucIdChon) {
                return false;
            }

            // Lọc theo Mức tồn kho
            if (tonKhoChon != null && !"Tất cả tồn kho".equals(tonKhoChon)) {
                if ("Còn hàng".equals(tonKhoChon) && sp.getSoLuongTon() <= 0) {
                    return false;
                }
                if ("Sắp hết hàng".equals(tonKhoChon) && (sp.getSoLuongTon() <= 0 || sp.getSoLuongTon() > sp.getSoLuongTonToiThieu())) {
                    return false;
                }
                if ("Hết hàng".equals(tonKhoChon) && sp.getSoLuongTon() > 0) {
                    return false;
                }
            }

            // Lọc theo Trạng thái kinh doanh
            if (trangThaiChon != null && !"Tất cả trạng thái".equals(trangThaiChon)) {
                if ("Đang kinh doanh".equals(trangThaiChon) && !sp.isTrangThai()) {
                    return false;
                }
                if ("Ngừng kinh doanh".equals(trangThaiChon) && sp.isTrangThai()) {
                    return false;
                }
            }

            return true;
        });

        capNhatTongSoLabel();
    }

    private void taiDuLieuTuDatabase() {
        new Thread(() -> {
            try {
                List<DanhMucSanPham> dsDanhMuc = danhMucDAO.layTatCa();
                List<SanPham> dsSanPham = sanPhamDAO.layTatCa();

                Platform.runLater(() -> {
                    dangCapNhatBoLoc = true;

                    // Danh mục
                    ObservableList<DanhMucSanPham> dmItems = FXCollections.observableArrayList();
                    DanhMucSanPham dmTatCa = new DanhMucSanPham();
                    dmTatCa.setId(0);
                    dmTatCa.setTenDanhMuc("Tất cả danh mục");
                    dmItems.add(dmTatCa);
                    if (dsDanhMuc != null) {
                        dmItems.addAll(dsDanhMuc);
                    }
                    locDanhMucCombo.setItems(dmItems);
                    locDanhMucCombo.setValue(dmTatCa);

                    // Sản phẩm
                    danhSachSanPham.clear();
                    if (dsSanPham != null) {
                        danhSachSanPham.addAll(dsSanPham);
                    }

                    dangCapNhatBoLoc = false;
                    apDungBoLoc();
                    anChiTiet();
                });
            } catch (Exception ignored) {}
        }).start();
    }

    private void hienThiChiTiet(SanPham sp) {
        if (sp == null || paneChiTiet == null) return;

        lblDetailMaSP.setText(sp.getMaSanPham() != null ? sp.getMaSanPham() : "---");
        lblDetailTenSP.setText(sp.getTenSanPham() != null ? sp.getTenSanPham() : "---");

        DanhMucSanPham dm = sp.getDanhMuc();
        lblDetailDanhMuc.setText(dm != null ? dm.getTenDanhMuc() : "Chưa phân loại");

        lblDetailDVT.setText(layTenHienThiDVT(sp.getDonViTinh()));

        if (sp.isTrangThai()) {
            lblDetailTrangThai.setText("● Đang kinh doanh");
            lblDetailTrangThai.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: 700;");
        } else {
            lblDetailTrangThai.setText("✖ Ngừng kinh doanh");
            lblDetailTrangThai.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: 700;");
        }

        lblDetailGiaBan.setText(tienFormat.format(sp.getDonGiaBan()));
        lblDetailTonKho.setText(sp.getSoLuongTon() + " " + layTenHienThiDVT(sp.getDonViTinh()));
        lblDetailTonToiThieu.setText(sp.getSoLuongTonToiThieu() + " " + layTenHienThiDVT(sp.getDonViTinh()));
        lblDetailVAT.setText(String.format("%.1f %%", sp.getThueVAT()));

        // Tải ảnh đại diện (nếu có)
        hienThiHinhAnh(sp.getAnhSanPham());

        paneChiTiet.setVisible(true);
        paneChiTiet.setManaged(true);
    }

    private void hienThiHinhAnh(String duongDanAnh) {
        if (detailImageView == null || detailPlaceholderLabel == null) return;

        if (duongDanAnh != null && !duongDanAnh.trim().isEmpty()) {
            try {
                File fileAnh = new File(duongDanAnh);
                if (fileAnh.exists()) {
                    Image image = new Image(fileAnh.toURI().toString());
                    detailImageView.setImage(image);
                    detailImageView.setVisible(true);
                    detailPlaceholderLabel.setVisible(false);
                    return;
                }
            } catch (Exception ignored) {}
        }

        detailImageView.setImage(null);
        detailImageView.setVisible(false);
        detailPlaceholderLabel.setVisible(true);
    }

    @FXML
    public void onDongChiTietClick() {
        if (sanPhamTable != null) {
            sanPhamTable.getSelectionModel().clearSelection();
        }
        anChiTiet();
    }

    private void anChiTiet() {
        if (paneChiTiet != null) {
            paneChiTiet.setVisible(false);
            paneChiTiet.setManaged(false);
        }
    }

    @FXML
    public void onLamMoiClick() {
        dangCapNhatBoLoc = true;
        if (timKiemField != null) timKiemField.clear();
        if (locDanhMucCombo != null && !locDanhMucCombo.getItems().isEmpty()) {
            locDanhMucCombo.setValue(locDanhMucCombo.getItems().get(0));
        }
        if (locTonKhoCombo != null) locTonKhoCombo.setValue("Tất cả tồn kho");
        if (locTrangThaiCombo != null) locTrangThaiCombo.setValue("Tất cả trạng thái");
        dangCapNhatBoLoc = false;

        taiDuLieuTuDatabase();
    }

    private void capNhatTongSoLabel() {
        if (tongSoLabel != null) {
            int tong = danhSachLoc != null ? danhSachLoc.size() : 0;
            tongSoLabel.setText("Tổng số: " + tong + " sản phẩm");
        }
    }

    private static String layTenHienThiDVT(DonViTinh dvt) {
        if (dvt == null) return "Cái";
        return switch (dvt) {
            case CAI -> "Cái";
            case BO -> "Bộ";
            case CHAI -> "Chai";
            case CAN -> "Can";
            case BINH -> "Bình";
            case LIT -> "Lít";
            case HOP -> "Hộp";
            case CUON -> "Cuộn";
            case TUYP -> "Tuýp";
            case MIENG -> "Miếng";
            case BICH -> "Bịch";
            case THUNG -> "Thùng";
            case KG -> "Kg";
            case MET -> "Mét";
            case CHIEC -> "Chiếc";
        };
    }
}