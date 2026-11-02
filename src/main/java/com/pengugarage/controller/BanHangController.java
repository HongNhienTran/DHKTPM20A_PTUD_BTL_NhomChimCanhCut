package com.pengugarage.controller;

import com.pengugarage.dao.DanhMucDAO;
import com.pengugarage.dao.KhachHangDAO;
import com.pengugarage.dao.MaKhuyenMaiDAO;
import com.pengugarage.dao.SanPhamDAO;
import com.pengugarage.model.entity.DanhMucSanPham;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.MaKhuyenMai;
import com.pengugarage.model.entity.SanPham;
import com.pengugarage.model.enums.DonViTinh;
import com.pengugarage.model.enums.LoaiGiamGia;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Controller cho Màn hình Bán Hàng & Phụ Tùng (POS Bán Lẻ)
 * Thiết kế giao diện Light Modern UI đồng bộ với hệ thống Pengu Garage:
 * - Brand Navy #102A46 / #173B61
 * - Action Orange #F47B31
 * - Canvas xám nhạt #F4F7FA, Surface trắng #FFFFFF
 * - Menu sản phẩm ở dưới đáy có thể kéo mở / thu gọn linh hoạt từ phím mũi tên.
 */
public class BanHangController {

    // =========================================================================
    // FXML INJECTIONS
    // =========================================================================
    @FXML private TextField txtTimKiemSanPham;
    @FXML private HBox boxTabsHoaDon;
    @FXML private Button btnThemHoaDon;

    // Giỏ hàng
    @FXML private TableView<ChiTietBanHang> tableGioHang;
    @FXML private TableColumn<ChiTietBanHang, Integer> colSTT;
    @FXML private TableColumn<ChiTietBanHang, String> colMaSP;
    @FXML private TableColumn<ChiTietBanHang, String> colTenSP;
    @FXML private TableColumn<ChiTietBanHang, String> colDVT;
    @FXML private TableColumn<ChiTietBanHang, String> colDonGia;
    @FXML private TableColumn<ChiTietBanHang, Void> colSoLuong;
    @FXML private TableColumn<ChiTietBanHang, String> colThanhTien;
    @FXML private TableColumn<ChiTietBanHang, Void> colThaoTac;

    // Ngăn kéo Menu sản phẩm ở dưới đáy
    @FXML private VBox drawerSanPham;
    @FXML private ComboBox<DanhMucSanPham> cboDanhMucSanPham;
    @FXML private Button btnToggleDrawer;
    @FXML private ScrollPane scrollSanPham;
    @FXML private FlowPane flowSanPham;

    // Cột thanh toán bên phải: Khách hàng
    @FXML private TextField txtTimKiemKhachHang;
    @FXML private Button btnTimKhachHang;
    @FXML private Label lblTenKhachHang;
    @FXML private Label lblSdtKhachHang;
    @FXML private Button btnXoaChonKhachHang;
    @FXML private Button btnThemKhachHang;

    @FXML private Label lblTongTienHang;
    @FXML private ComboBox<MaKhuyenMai> cboKhuyenMai;
    @FXML private Label lblTienKhuyenMai;
    @FXML private Label lblKhachCanTra;
    @FXML private TextField txtTienKhachDua;
    @FXML private Label lblTienThua;
    @FXML private RadioButton rbTienMat;
    @FXML private RadioButton rbChuyenKhoan;
    @FXML private TextArea txtGhiChu;
    @FXML private Button btnThanhToan;
    @FXML private Button btnInTamTinh;
    @FXML private Button btnHuyDon;

    // =========================================================================
    // SERVICES & STATE
    // =========================================================================
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final MaKhuyenMaiDAO maKhuyenMaiDAO = new MaKhuyenMaiDAO();
    private final DecimalFormat tienFormat = new DecimalFormat("#,### đ");

    private final List<SanPham> tatCaSanPham = new ArrayList<>();
    private final List<DanhMucSanPham> tatCaDanhMuc = new ArrayList<>();
    private final ObservableList<MaKhuyenMai> danhSachKhuyenMai = FXCollections.observableArrayList();

    // Quản lý đa tab hóa đơn
    private final List<DonBanHangTab> danhSachDonHang = new ArrayList<>();
    private DonBanHangTab donHangHienTai;
    private int demHoaDon = 1;
    private boolean isDrawerExpanded = true;
    private Integer danhMucDangChonId = null; // null = Tất cả

    // =========================================================================
    // INITIALIZATION
    // =========================================================================
    @FXML
    public void initialize() {
        cauHinhBangGioHang();
        cauHinhTimKiemVaPhimTat();
        cauHinhThanhToan();
        
        // Khởi tạo tab hóa đơn đầu tiên
        themHoaDonMoi();

        // Tải dữ liệu thật từ CSDL bất đồng bộ
        new Thread(() -> {
            taiDanhSachDanhMuc();
            taiDanhSachSanPham();
            taiDanhSachKhachHang();
            taiDanhSachMaKhuyenMai();
        }).start();
    }

    private void cauHinhBangGioHang() {
        colSTT.setCellValueFactory(cellData -> new SimpleIntegerProperty(
                tableGioHang.getItems().indexOf(cellData.getValue()) + 1).asObject());
        colMaSP.setCellValueFactory(new PropertyValueFactory<>("maSanPham"));
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));
        colDVT.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        
        colDonGia.setCellValueFactory(cellData -> 
                new SimpleStringProperty(tienFormat.format(cellData.getValue().getDonGia())));
        
        colThanhTien.setCellValueFactory(cellData -> 
                new SimpleStringProperty(tienFormat.format(cellData.getValue().getThanhTien())));

        // Cột Số lượng (+/- buttons)
        colSoLuong.setCellFactory(param -> new TableCell<>() {
            private final Button btnGiam = new Button("-");
            private final Label lblQty = new Label("1");
            private final Button btnTang = new Button("+");
            private final HBox container = new HBox(4, btnGiam, lblQty, btnTang);

            {
                container.setAlignment(Pos.CENTER);
                btnGiam.getStyleClass().add("pos-qty-btn");
                btnTang.getStyleClass().add("pos-qty-btn");
                lblQty.setStyle("-fx-font-weight: 800; -fx-min-width: 24px; -fx-alignment: center;");

                btnGiam.setOnAction(e -> {
                    ChiTietBanHang item = getTableView().getItems().get(getIndex());
                    if (item.getSoLuong() > 1) {
                        item.setSoLuong(item.getSoLuong() - 1);
                        tableGioHang.refresh();
                        capNhatThongKeTien();
                    } else {
                        xoaKhoiGioHang(item);
                    }
                });

                btnTang.setOnAction(e -> {
                    ChiTietBanHang item = getTableView().getItems().get(getIndex());
                    if (item.getSoLuong() < item.getSanPham().getSoLuongTon()) {
                        item.setSoLuong(item.getSoLuong() + 1);
                        tableGioHang.refresh();
                        capNhatThongKeTien();
                    } else {
                        thongBao("Cảnh báo kho", "Sản phẩm \"" + item.getTenSanPham() + "\" chỉ còn tồn kho " + item.getSanPham().getSoLuongTon() + " " + item.getDonViTinh());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    ChiTietBanHang ct = getTableView().getItems().get(getIndex());
                    lblQty.setText(String.valueOf(ct.getSoLuong()));
                    setGraphic(container);
                }
            }
        });

        // Cột Thao tác (Xóa sản phẩm)
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnXoa = new Button("🗑");
            {
                btnXoa.getStyleClass().add("pos-delete-item-btn");
                btnXoa.setTooltip(new Tooltip("Xóa khỏi giỏ hàng"));
                btnXoa.setOnAction(e -> {
                    ChiTietBanHang item = getTableView().getItems().get(getIndex());
                    xoaKhoiGioHang(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnXoa);
            }
        });
    }

    private void cauHinhTimKiemVaPhimTat() {
        if (txtTimKiemSanPham != null) {
            txtTimKiemSanPham.setOnAction(e -> {
                String keyword = txtTimKiemSanPham.getText();
                if (keyword != null && !keyword.trim().isEmpty()) {
                    xuLyTimKiemVaThemNhanh(keyword.trim());
                }
            });

            // Lắng nghe gõ phím để lọc danh sách sản phẩm bên dưới
            txtTimKiemSanPham.textProperty().addListener((obs, oldVal, newVal) -> {
                locVaHienThiSanPham();
            });
        }
    }

    private void cauHinhThanhToan() {
        // Toggle phương thức thanh toán
        ToggleGroup groupPayment = new ToggleGroup();
        if (rbTienMat != null) rbTienMat.setToggleGroup(groupPayment);
        if (rbChuyenKhoan != null) rbChuyenKhoan.setToggleGroup(groupPayment);

        // Cấu hình ComboBox Mã khuyến mãi
        if (cboKhuyenMai != null) {
            cboKhuyenMai.setItems(danhSachKhuyenMai);
            cboKhuyenMai.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(MaKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else if (item.getId() == 0) {
                        setText("-- Không áp dụng mã khuyến mãi --");
                    } else {
                        String giam = item.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM
                                ? (int) item.getGiaTriGiam() + "%"
                                : tienFormat.format(item.getGiaTriGiam());
                        setText(item.getMaKhuyenMai() + " (Giảm " + giam + " • Đơn từ " + tienFormat.format(item.getGiaTriToiThieuApDung()) + ")");
                    }
                }
            });

            cboKhuyenMai.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(MaKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getId() == 0) {
                        setText("-- Không áp dụng --");
                    } else {
                        String giam = item.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM
                                ? (int) item.getGiaTriGiam() + "%"
                                : tienFormat.format(item.getGiaTriGiam());
                        setText(item.getMaKhuyenMai() + " (-" + giam + ")");
                    }
                }
            });

            cboKhuyenMai.setOnAction(e -> {
                if (donHangHienTai != null) {
                    donHangHienTai.maKhuyenMai = cboKhuyenMai.getValue();
                }
                capNhatThongKeTien();
            });
        }

        // Tiền khách đưa thay đổi
        if (txtTienKhachDua != null) {
            txtTienKhachDua.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    try {
                        String clean = newVal.replaceAll("[^0-9]", "");
                        if (!clean.isEmpty()) {
                            double tienDua = Double.parseDouble(clean);
                            if (donHangHienTai != null) donHangHienTai.tienKhachDua = tienDua;
                        }
                    } catch (NumberFormatException ignored) {}
                } else {
                    if (donHangHienTai != null) donHangHienTai.tienKhachDua = 0;
                }
                capNhatThongKeTien();
            });
        }

        // Khách hàng: Tìm kiếm số điện thoại
        if (txtTimKiemKhachHang != null) {
            txtTimKiemKhachHang.setOnAction(e -> onTimKhachHangClick());
        }
    }

    // =========================================================================
    // QUẢN LÝ ĐA HÓA ĐƠN (INVOICE TABS)
    // =========================================================================
    @FXML
    public void onThemHoaDonClick() {
        themHoaDonMoi();
    }

    private void themHoaDonMoi() {
        demHoaDon++;
        DonBanHangTab donHang = new DonBanHangTab("Hóa đơn " + (danhSachDonHang.size() + 1));
        danhSachDonHang.add(donHang);
        chonHoaDon(donHang);
        veLaiTabsHoaDon();
    }

    private void chonHoaDon(DonBanHangTab donHang) {
        this.donHangHienTai = donHang;
        tableGioHang.setItems(donHang.danhSachItem);

        // Khôi phục thông tin khách hàng của đơn hiện tại
        capNhatThongTinKhachHang(donHang.khachHang);
        if (txtTimKiemKhachHang != null) {
            txtTimKiemKhachHang.setText(donHang.khachHang != null && donHang.khachHang.getSoDienThoai() != null
                    ? donHang.khachHang.getSoDienThoai() : "");
        }

        // Khôi phục mã khuyến mãi của đơn hiện tại
        if (cboKhuyenMai != null) {
            if (donHang.maKhuyenMai != null) {
                cboKhuyenMai.setValue(donHang.maKhuyenMai);
            } else if (!danhSachKhuyenMai.isEmpty()) {
                cboKhuyenMai.setValue(danhSachKhuyenMai.get(0));
            }
        }
        if (txtTienKhachDua != null) {
            txtTienKhachDua.setText(donHang.tienKhachDua > 0 ? String.format("%.0f", donHang.tienKhachDua) : "");
        }
        if (txtGhiChu != null) {
            txtGhiChu.setText(donHang.ghiChu != null ? donHang.ghiChu : "");
        }

        capNhatThongKeTien();
        veLaiTabsHoaDon();
    }

    private void dongHoaDon(DonBanHangTab donHang) {
        if (danhSachDonHang.size() <= 1) {
            // Nếu chỉ còn 1 hóa đơn thì chỉ xóa sạch giỏ hàng chứ không đóng tab
            donHang.danhSachItem.clear();
            donHang.maKhuyenMai = null;
            donHang.tienKhachDua = 0;
            donHang.ghiChu = "";
            chonHoaDon(donHang);
            return;
        }

        danhSachDonHang.remove(donHang);
        chonHoaDon(danhSachDonHang.get(danhSachDonHang.size() - 1));
        veLaiTabsHoaDon();
    }

    private void veLaiTabsHoaDon() {
        if (boxTabsHoaDon == null) return;
        boxTabsHoaDon.getChildren().clear();

        for (DonBanHangTab dh : danhSachDonHang) {
            HBox tabBox = new HBox(6);
            tabBox.setAlignment(Pos.CENTER_LEFT);
            tabBox.getStyleClass().add("pos-tab-btn");
            if (dh == donHangHienTai) {
                tabBox.getStyleClass().add("pos-tab-btn-active");
            }

            Label lblName = new Label(dh.tenTab);
            lblName.setStyle("-fx-font-weight: 800;");

            Button btnClose = new Button("✕");
            btnClose.setStyle("-fx-background-color: transparent; -fx-padding: 0 2px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: #94a3b8;");
            btnClose.setOnAction(e -> {
                e.consume();
                dongHoaDon(dh);
            });

            tabBox.getChildren().addAll(lblName, btnClose);
            tabBox.setOnMouseClicked(e -> chonHoaDon(dh));

            boxTabsHoaDon.getChildren().add(tabBox);
        }
    }

    // =========================================================================
    // NGĂN KÉO SẢN PHẨM Ở DƯỚI ĐÁY (COLLAPSIBLE DRAWER & MŨI TÊN KÉO)
    // =========================================================================
    @FXML
    public void onToggleDrawerClick() {
        isDrawerExpanded = !isDrawerExpanded;
        if (scrollSanPham != null) {
            scrollSanPham.setVisible(isDrawerExpanded);
            scrollSanPham.setManaged(isDrawerExpanded);
        }
        if (btnToggleDrawer != null) {
            btnToggleDrawer.setText(isDrawerExpanded ? "▼ Thu nhỏ" : "▲ Mở rộng");
            btnToggleDrawer.setTooltip(new Tooltip(isDrawerExpanded ? "Thu nhỏ menu sản phẩm" : "Kéo mở menu sản phẩm"));
        }
    }

    private void taiDanhSachDanhMuc() {
        try {
            List<DanhMucSanPham> list = danhMucDAO.layTatCa();
            Platform.runLater(() -> {
                tatCaDanhMuc.clear();
                if (list != null) {
                    tatCaDanhMuc.addAll(list);
                }
                caiDatComboBoxDanhMuc();
            });
        } catch (Exception ignored) {}
    }

    private void caiDatComboBoxDanhMuc() {
        if (cboDanhMucSanPham == null) return;

        ObservableList<DanhMucSanPham> items = FXCollections.observableArrayList();
        DanhMucSanPham dmTatCa = new DanhMucSanPham();
        dmTatCa.setId(0);
        dmTatCa.setTenDanhMuc("Tất cả danh mục");
        items.add(dmTatCa);
        items.addAll(tatCaDanhMuc);

        cboDanhMucSanPham.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(DanhMucSanPham item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenDanhMuc());
            }
        });
        cboDanhMucSanPham.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DanhMucSanPham item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenDanhMuc());
            }
        });

        cboDanhMucSanPham.setItems(items);
        cboDanhMucSanPham.setValue(dmTatCa);

        cboDanhMucSanPham.setOnAction(e -> {
            DanhMucSanPham selected = cboDanhMucSanPham.getValue();
            if (selected == null || selected.getId() == 0) {
                danhMucDangChonId = null;
            } else {
                danhMucDangChonId = selected.getId();
            }
            locVaHienThiSanPham();
        });
    }

    private void taiDanhSachSanPham() {
        try {
            List<SanPham> list = sanPhamDAO.layTatCa();
            Platform.runLater(() -> {
                tatCaSanPham.clear();
                tatCaSanPham.addAll(list);
                locVaHienThiSanPham();
            });
        } catch (Exception ignored) {}
    }

    private void locVaHienThiSanPham() {
        if (flowSanPham == null) return;
        flowSanPham.getChildren().clear();

        String keyword = txtTimKiemSanPham != null ? txtTimKiemSanPham.getText() : "";
        String kwLower = keyword != null ? keyword.trim().toLowerCase() : "";

        for (SanPham sp : tatCaSanPham) {
            // Lọc theo danh mục
            if (danhMucDangChonId != null && sp.getDanhMucId() != danhMucDangChonId) {
                continue;
            }
            // Lọc theo từ khóa tìm kiếm
            if (!kwLower.isEmpty()) {
                boolean matchMa = sp.getMaSanPham() != null && sp.getMaSanPham().toLowerCase().contains(kwLower);
                boolean matchTen = sp.getTenSanPham() != null && sp.getTenSanPham().toLowerCase().contains(kwLower);
                if (!matchMa && !matchTen) {
                    continue;
                }
            }

            VBox card = taoTheSanPham(sp);
            flowSanPham.getChildren().add(card);
        }
    }

    private VBox taoTheSanPham(SanPham sp) {
        VBox card = new VBox(6);
        card.getStyleClass().add("pos-product-card");
        card.setPrefWidth(155);
        card.setMaxWidth(160);
        card.setAlignment(Pos.CENTER);

        // Icon ảnh đại diện
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(135, 75);
        imgBox.getStyleClass().add("pos-card-img-placeholder");
        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 28px;");
        imgBox.getChildren().add(icon);

        // Tên sản phẩm
        Label lblTen = new Label(sp.getTenSanPham());
        lblTen.getStyleClass().add("pos-card-title");
        lblTen.setWrapText(true);
        lblTen.setMaxHeight(38);
        lblTen.setAlignment(Pos.CENTER);

        // Giá bán
        Label lblGia = new Label(tienFormat.format(sp.getDonGiaBan()));
        lblGia.getStyleClass().add("pos-card-price");

        // Số lượng tồn
        String dvt = formatDonViTinh(sp.getDonViTinh());
        Label lblTon = new Label("SL: " + sp.getSoLuongTon() + " " + dvt);
        lblTon.getStyleClass().add("pos-card-stock");

        card.getChildren().addAll(imgBox, lblTen, lblGia, lblTon);

        // Click vào thẻ sản phẩm -> Thêm vào giỏ hàng
        card.setOnMouseClicked(e -> themVaoGioHang(sp, 1));

        return card;
    }

    // =========================================================================
    // TODO: [M06-T01 - Tân] NGHIỆP VỤ CHỌN SẢN PHẨM & QUẢN LÝ GIỎ HÀNG
    // =========================================================================
    private void themVaoGioHang(SanPham sp, int soLuong) {
        if (donHangHienTai == null || sp == null) return;

        if (sp.getSoLuongTon() <= 0) {
            thongBao("Hết hàng", "Sản phẩm \"" + sp.getTenSanPham() + "\" hiện đã hết hàng trong kho!");
            return;
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        for (ChiTietBanHang item : donHangHienTai.danhSachItem) {
            if (item.getSanPham().getId() == sp.getId()) {
                if (item.getSoLuong() + soLuong <= sp.getSoLuongTon()) {
                    item.setSoLuong(item.getSoLuong() + soLuong);
                    tableGioHang.refresh();
                    capNhatThongKeTien();
                } else {
                    thongBao("Cảnh báo số lượng", "Số lượng yêu cầu vượt quá tồn kho hiện tại (" + sp.getSoLuongTon() + ")!");
                }
                return;
            }
        }

        // Nếu chưa có thì tạo mới chi tiết
        ChiTietBanHang moi = new ChiTietBanHang(sp, soLuong);
        donHangHienTai.danhSachItem.add(moi);
        capNhatThongKeTien();
    }

    private void xoaKhoiGioHang(ChiTietBanHang item) {
        if (donHangHienTai != null && item != null) {
            donHangHienTai.danhSachItem.remove(item);
            capNhatThongKeTien();
        }
    }

    private void xuLyTimKiemVaThemNhanh(String keyword) {
        String kw = keyword.toLowerCase();
        SanPham match = null;

        // Ưu tiên trùng mã chính xác (quét barcode)
        for (SanPham sp : tatCaSanPham) {
            if (sp.getMaSanPham() != null && sp.getMaSanPham().equalsIgnoreCase(kw)) {
                match = sp;
                break;
            }
        }

        // Tìm gần đúng
        if (match == null) {
            for (SanPham sp : tatCaSanPham) {
                if ((sp.getMaSanPham() != null && sp.getMaSanPham().toLowerCase().contains(kw)) ||
                    (sp.getTenSanPham() != null && sp.getTenSanPham().toLowerCase().contains(kw))) {
                    match = sp;
                    break;
                }
            }
        }

        if (match != null) {
            themVaoGioHang(match, 1);
            if (txtTimKiemSanPham != null) {
                txtTimKiemSanPham.clear();
            }
        } else {
            thongBao("Không tìm thấy", "Không tìm thấy sản phẩm phù hợp với từ khóa: \"" + keyword + "\"");
        }
    }

    private void capNhatThongKeTien() {
        if (donHangHienTai == null) return;

        double tongTien = 0;
        for (ChiTietBanHang item : donHangHienTai.danhSachItem) {
            tongTien += item.getThanhTien();
        }

        // Tính tiền khuyến mãi theo mã khuyến mãi đã chọn
        double tienKm = 0;
        if (donHangHienTai.maKhuyenMai != null && donHangHienTai.maKhuyenMai.getId() > 0) {
            MaKhuyenMai mkm = donHangHienTai.maKhuyenMai;
            // Kiểm tra điều kiện đơn hàng đạt giá trị tối thiểu
            if (tongTien >= mkm.getGiaTriToiThieuApDung()) {
                if (mkm.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
                    tienKm = tongTien * (mkm.getGiaTriGiam() / 100.0);
                } else {
                    tienKm = Math.min(tongTien, mkm.getGiaTriGiam());
                }
            } else {
                tienKm = 0; // Chưa đạt giá trị tối thiểu
            }
        }

        double khachCanTra = Math.max(0, tongTien - tienKm);
        double tienThua = Math.max(0, donHangHienTai.tienKhachDua - khachCanTra);

        if (lblTongTienHang != null) lblTongTienHang.setText(tienFormat.format(tongTien));
        if (lblTienKhuyenMai != null) lblTienKhuyenMai.setText("- " + tienFormat.format(tienKm));
        if (lblKhachCanTra != null) lblKhachCanTra.setText(tienFormat.format(khachCanTra));
        if (lblTienThua != null) lblTienThua.setText(tienFormat.format(tienThua));

        // Cập nhật/đánh giá mã khuyến mãi theo điều kiện giá trị tối thiểu áp dụng
        capNhatDanhSachKhuyenMaiTheoDieuKien(tongTien);
    }

    // =========================================================================
    // TODO: [M06-T02 - Tân] CHỌN MÃ KHUYẾN MÃI THEO ĐIỀU KIỆN ĐƠN HÀNG
    // Lấy danh sách mã khuyến mãi từ DB lên ComboBox theo điều kiện `gia_tri_toi_thieu_ap_dung`
    // Điều kiện: gia_tri_toi_thieu_ap_dung <= tongTienHang
    // =========================================================================
    public void capNhatDanhSachKhuyenMaiTheoDieuKien(double tongTienHang) {
        // TODO: [M06-T02 - Tân] Khi người dùng thêm/bớt giỏ hàng thay đổi tổng tiền:
        // Gọi maKhuyenMaiDAO.layDanhSachKhuyenMaiTheoGiaTriToiThieu(tongTienHang)
        // để cập nhật lại danh sách trên ComboBox chỉ hiển thị các mã thỏa mãn gia_tri_toi_thieu_ap_dung.
    }

    private void taiDanhSachMaKhuyenMai() {
        try {
            List<MaKhuyenMai> listTuDB = maKhuyenMaiDAO.layTatCa();
            List<MaKhuyenMai> list = new ArrayList<>();
            MaKhuyenMai macDinh = new MaKhuyenMai();
            macDinh.setId(0);
            macDinh.setMaKhuyenMai("-- Không áp dụng mã --");
            list.add(macDinh);

            if (listTuDB != null) {
                for (MaKhuyenMai km : listTuDB) {
                    if (km.isTrangThai()) {
                        list.add(km);
                    }
                }
            }

            Platform.runLater(() -> {
                danhSachKhuyenMai.setAll(list);
                if (cboKhuyenMai != null && (cboKhuyenMai.getValue() == null || cboKhuyenMai.getValue().getId() == 0)) {
                    cboKhuyenMai.setValue(macDinh);
                }
            });
        } catch (Exception ignored) {}
    }

    // =========================================================================
    // KHÁCH HÀNG & THANH TOÁN
    // =========================================================================
    private void taiDanhSachKhachHang() {
        try {
            List<KhachHang> list = khachHangDAO.layTatCa();
            // Khởi tạo trạng thái ban đầu mặc định là Khách lẻ
            Platform.runLater(() -> capNhatThongTinKhachHang(null));
        } catch (Exception ignored) {}
    }

    private void capNhatThongTinKhachHang(KhachHang kh) {
        if (kh == null || kh.getId() == 0) {
            if (lblTenKhachHang != null) lblTenKhachHang.setText("Khách lẻ (Vãng lai)");
            if (lblSdtKhachHang != null) lblSdtKhachHang.setText("Mua trực tiếp tại quầy");
        } else {
            if (lblTenKhachHang != null) lblTenKhachHang.setText(kh.getHoTen());
            if (lblSdtKhachHang != null) lblSdtKhachHang.setText("📞 " + (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "Chưa có SĐT"));
        }
    }

    @FXML
    public void onTimKhachHangClick() {
        if (txtTimKiemKhachHang == null) return;
        String sdt = txtTimKiemKhachHang.getText() != null ? txtTimKiemKhachHang.getText().trim() : "";
        if (sdt.isEmpty()) {
            thongBao("Tìm khách hàng", "Vui lòng nhập số điện thoại khách hàng cần tìm.");
            return;
        }

        KhachHang kh = khachHangDAO.timTheoSoDienThoai(sdt);
        if (kh != null) {
            if (donHangHienTai != null) {
                donHangHienTai.khachHang = kh;
            }
            capNhatThongTinKhachHang(kh);
        } else {
            thongBao("Không tìm thấy", "Không tìm thấy khách hàng với số điện thoại: \"" + sdt + "\".\nBạn có thể thêm mới hoặc thanh toán dưới dạng Khách lẻ.");
        }
    }

    @FXML
    public void onXoaChonKhachHangClick() {
        if (donHangHienTai != null) {
            donHangHienTai.khachHang = null;
        }
        if (txtTimKiemKhachHang != null) {
            txtTimKiemKhachHang.clear();
        }
        capNhatThongTinKhachHang(null);
    }

    @FXML
    public void onThemKhachHangClick() {
        thongBao("Thêm khách hàng mới", "Chức năng thêm nhanh thông tin khách hàng từ quầy bán hàng đang được cập nhật.");
    }

    // =========================================================================
    // TODO: [M06-T03 - Tân] CHỌN PHƯƠNG THỨC THANH TOÁN TIỀN MẶT & TÍNH TIỀN THỪA
    // =========================================================================
    @FXML
    public void onThanhToanClick() {
        if (donHangHienTai == null || donHangHienTai.danhSachItem.isEmpty()) {
            thongBao("Giỏ hàng rỗng", "Vui lòng chọn ít nhất 1 sản phẩm vào giỏ hàng trước khi thanh toán!");
            return;
        }

        double tongTien = 0;
        for (ChiTietBanHang item : donHangHienTai.danhSachItem) {
            tongTien += item.getThanhTien();
        }

        // Tính tiền khuyến mãi theo mã đã chọn
        double tienKm = 0;
        String thongTinKM = "Không áp dụng";
        if (donHangHienTai.maKhuyenMai != null && donHangHienTai.maKhuyenMai.getId() > 0) {
            MaKhuyenMai mkm = donHangHienTai.maKhuyenMai;
            if (tongTien >= mkm.getGiaTriToiThieuApDung()) {
                if (mkm.getLoaiGiam() == LoaiGiamGia.PHAN_TRAM) {
                    tienKm = tongTien * (mkm.getGiaTriGiam() / 100.0);
                    thongTinKM = mkm.getMaKhuyenMai() + " (-" + (int) mkm.getGiaTriGiam() + "% = -" + tienFormat.format(tienKm) + ")";
                } else {
                    tienKm = Math.min(tongTien, mkm.getGiaTriGiam());
                    thongTinKM = mkm.getMaKhuyenMai() + " (-" + tienFormat.format(tienKm) + ")";
                }
            } else {
                thongTinKM = mkm.getMaKhuyenMai() + " (Chưa đạt đơn tối thiểu " + tienFormat.format(mkm.getGiaTriToiThieuApDung()) + ")";
            }
        }

        double khachCanTra = Math.max(0, tongTien - tienKm);
        String pttt = (rbChuyenKhoan != null && rbChuyenKhoan.isSelected()) ? "Chuyển khoản (QR)" : "Tiền mặt";

        // Nếu là tiền mặt và khách đưa chưa đủ
        if ("Tiền mặt".equals(pttt) && donHangHienTai.tienKhachDua < khachCanTra) {
            thongBao("Chưa đủ tiền", "Số tiền khách đưa (" + tienFormat.format(donHangHienTai.tienKhachDua) + 
                    ") chưa đủ để thanh toán (" + tienFormat.format(khachCanTra) + ")!");
            if (txtTienKhachDua != null) txtTienKhachDua.requestFocus();
            return;
        }

        // Tạo mã hóa đơn ngẫu nhiên
        String maHD = "HD_LE_" + System.currentTimeMillis() % 1000000;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanh toán thành công");
        alert.setHeaderText("ĐÃ HOÀN TẤT THANH TOÁN " + donHangHienTai.tenTab);
        alert.setContentText(
                "Mã hóa đơn: " + maHD + "\n" +
                "Khách hàng: " + (donHangHienTai.khachHang != null ? donHangHienTai.khachHang.getHoTen() : "Khách lẻ") + "\n" +
                "Tổng tiền hàng: " + tienFormat.format(tongTien) + "\n" +
                "Mã khuyến mãi: " + thongTinKM + "\n" +
                "Thực thu: " + tienFormat.format(khachCanTra) + "\n" +
                "Phương thức: " + pttt + "\n\n" +
                "Hóa đơn đã được lập thành công và sẵn sàng để in biên nhận."
        );
        alert.showAndWait();

        // Đóng tab hoặc làm sạch đơn vừa thanh toán
        dongHoaDon(donHangHienTai);
    }

    // =========================================================================
    // TODO: [M06-T04 - Tân] CHỌN LƯU TẠM HÓA ĐƠN (IN TẠM TÍNH - TRẠNG THÁI CHUA_THANH_TOAN)
    // Lưu vào bảng HoaDon (trang_thai = CHUA_THANH_TOAN) và ChiTietHoaDon qua HoaDonDAO
    // =========================================================================
    @FXML
    public void onInTamTinhClick() {
        if (donHangHienTai == null || donHangHienTai.danhSachItem.isEmpty()) {
            thongBao("Thông báo", "Giỏ hàng đang trống, chưa có thông tin để in tạm tính.");
            return;
        }
        // TODO: [M06-T04 - Tân] Gọi hoaDonDAO.themHoaDon(...) và hoaDonDAO.themChiTietHoaDon(...)
        thongBao("In tạm tính", "Lệnh in phiếu tạm tính cho " + donHangHienTai.tenTab + " đã được gửi tới máy in.");
    }

    @FXML
    public void onHuyDonClick() {
        if (donHangHienTai != null) {
            donHangHienTai.danhSachItem.clear();
            donHangHienTai.maKhuyenMai = null;
            donHangHienTai.tienKhachDua = 0;
            donHangHienTai.ghiChu = "";
            if (cboKhuyenMai != null && !danhSachKhuyenMai.isEmpty()) {
                cboKhuyenMai.setValue(danhSachKhuyenMai.get(0));
            }
            capNhatThongKeTien();
        }
    }

    private void thongBao(String tieuDe, String noiDung) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(tieuDe);
        a.setHeaderText(null);
        a.setContentText(noiDung);
        a.showAndWait();
    }

    private static String formatDonViTinh(DonViTinh dvt) {
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

    // =========================================================================
    // HELPER CLASSES
    // =========================================================================
    public static class ChiTietBanHang {
        private final SanPham sanPham;
        private int soLuong;

        public ChiTietBanHang(SanPham sanPham, int soLuong) {
            this.sanPham = sanPham;
            this.soLuong = soLuong;
        }

        public SanPham getSanPham() { return sanPham; }
        public String getMaSanPham() { return sanPham.getMaSanPham(); }
        public String getTenSanPham() { return sanPham.getTenSanPham(); }
        public String getDonViTinh() { 
            return formatDonViTinh(sanPham.getDonViTinh()); 
        }
        public double getDonGia() { return sanPham.getDonGiaBan(); }
        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
        public double getThanhTien() { return sanPham.getDonGiaBan() * soLuong; }
    }

    public static class DonBanHangTab {
        public String tenTab;
        public ObservableList<ChiTietBanHang> danhSachItem = FXCollections.observableArrayList();
        public KhachHang khachHang;
        public MaKhuyenMai maKhuyenMai;
        public double tienKhachDua = 0;
        public String ghiChu = "";

        public DonBanHangTab(String tenTab) {
            this.tenTab = tenTab;
        }
    }
}
