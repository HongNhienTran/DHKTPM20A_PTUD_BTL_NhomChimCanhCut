package com.pengugarage.controller;

import com.pengugarage.dao.NhanVienDAO;
import com.pengugarage.dao.PhieuBaoGiaDAO;
import com.pengugarage.dao.PhieuPhanCongDAO;
import com.pengugarage.model.entity.*;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.model.enums.LoaiHangMuc;
import com.pengugarage.model.enums.TrangThaiBaoGia;
import com.pengugarage.model.enums.TrangThaiPhieuTiepNhan;
import com.pengugarage.model.enums.TrangThaiSua;
import com.pengugarage.service.PhieuTiepNhanService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho Màn hình Hàng Đợi Xe Tại Xưởng (Kanban Live Queue).
 * 
 * Phụ trách: Trần Hồng Nhiên (Module Tiếp nhận xe - M08).
 * Giám sát và điều phối tiến trình các xe đang lưu tại xưởng qua 4 giai đoạn:
 * 1. Chờ kiểm tra -> 2. Kiểm tra & Báo giá -> 3. Đang sửa chữa -> 4. Chờ bàn giao.
 * 
 * Lễ tân đóng vai trò điều phối:
 * - Tiếp nhận xe & chỉ định KTV kiểm tra xe ban đầu.
 * - Xem báo giá chi tiết, đọc cho khách duyệt.
 * - Trực tiếp chọn KTV và phân công sửa chữa khi khách chốt giá.
 */
public class HangDoiXeController implements Initializable {

    // =========================================================================
    // 1. FXML INJECTIONS
    // =========================================================================

    // Top Title & Metric Cards
    @FXML private Label lblTongXeBadge;
    @FXML private Label lblChoKiemTra;
    @FXML private Label lblDangKiemTra;
    @FXML private Label lblDangSuaChua;
    @FXML private Label lblChoGiaoXe;
    @FXML private Button btnTiepNhanMoi;
    @FXML private Button btnLamMoi;

    // Search bar
    @FXML private TextField txtTimKiem;
    @FXML private Button btnXoaLoc;

    // Kanban Column Badges
    @FXML private Label lblBadgeChoKiemTra;
    @FXML private Label lblBadgeDangKiemTra;
    @FXML private Label lblBadgeDangSuaChua;
    @FXML private Label lblBadgeChoGiaoXe;

    // Kanban Column Containers (VBox)
    @FXML private VBox boxChoKiemTra;
    @FXML private VBox boxDangKiemTra;
    @FXML private VBox boxDangSuaChua;
    @FXML private VBox boxChoGiaoXe;

    // =========================================================================
    // 2. SERVICES, DAOS & STATE
    // =========================================================================

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final PhieuBaoGiaDAO phieuBaoGiaDAO = new PhieuBaoGiaDAO();
    private final PhieuPhanCongDAO phieuPhanCongDAO = new PhieuPhanCongDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private final List<PhieuTiepNhan> tatCaXeTaiXuong = new ArrayList<>();

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter fullDateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DecimalFormat tienFmt = new DecimalFormat("#,##0 đ");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lắng nghe tìm kiếm thời gian thực
        if (txtTimKiem != null) {
            txtTimKiem.textProperty().addListener((obs, oldVal, newVal) -> hienThiHangDoi(newVal));
        }

        // Tải danh sách xe ban đầu
        taiDanhSachHangDoi();
    }

    // =========================================================================
    // 3. TẢI DỮ LIỆU & RENDER KANBAN BOARD
    // =========================================================================

    public void taiDanhSachHangDoi() {
        new Thread(() -> {
            try {
                List<PhieuTiepNhan> list = phieuTiepNhanService.layDanhSachHangDoiXeTaiXuong();
                Platform.runLater(() -> {
                    tatCaXeTaiXuong.clear();
                    if (list != null) {
                        tatCaXeTaiXuong.addAll(list);
                    }
                    String keyword = txtTimKiem != null ? txtTimKiem.getText() : "";
                    hienThiHangDoi(keyword);
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiHangDoi(""));
            }
        }).start();
    }

    private void hienThiHangDoi(String keyword) {
        // Xóa trắng 4 cột
        if (boxChoKiemTra != null) boxChoKiemTra.getChildren().clear();
        if (boxDangKiemTra != null) boxDangKiemTra.getChildren().clear();
        if (boxDangSuaChua != null) boxDangSuaChua.getChildren().clear();
        if (boxChoGiaoXe != null) boxChoGiaoXe.getChildren().clear();

        int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
        String kw = keyword != null ? keyword.trim().toLowerCase() : "";

        for (PhieuTiepNhan p : tatCaXeTaiXuong) {
            // Lọc theo từ khóa
            if (!kw.isEmpty()) {
                String ma = p.getMaTiepNhan() != null ? p.getMaTiepNhan().toLowerCase() : "";
                String bienSo = (p.getPhuongTien() != null && p.getPhuongTien().getBienSoXe() != null)
                        ? p.getPhuongTien().getBienSoXe().toLowerCase() : "";
                String tenKhach = (p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null
                        && p.getPhuongTien().getKhachHang().getHoTen() != null)
                        ? p.getPhuongTien().getKhachHang().getHoTen().toLowerCase() : "";
                String sdt = (p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null
                        && p.getPhuongTien().getKhachHang().getSoDienThoai() != null)
                        ? p.getPhuongTien().getKhachHang().getSoDienThoai().toLowerCase() : "";

                if (!ma.contains(kw) && !bienSo.contains(kw) && !tenKhach.contains(kw) && !sdt.contains(kw)) {
                    continue;
                }
            }

            VBox card = taoTheXe(p);

            TrangThaiPhieuTiepNhan tt = p.getTrangThai();
            if (tt == null) tt = TrangThaiPhieuTiepNhan.CHO_TIEP_NHAN;

            switch (tt) {
                case CHO_TIEP_NHAN, DA_TIEP_NHAN -> {
                    if (boxChoKiemTra != null) boxChoKiemTra.getChildren().add(card);
                    c1++;
                }
                case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA -> {
                    if (boxDangKiemTra != null) boxDangKiemTra.getChildren().add(card);
                    c2++;
                }
                case DANG_SUA_CHUA -> {
                    if (boxDangSuaChua != null) boxDangSuaChua.getChildren().add(card);
                    c3++;
                }
                case HOAN_THANH -> {
                    if (boxChoGiaoXe != null) boxChoGiaoXe.getChildren().add(card);
                    c4++;
                }
                default -> {}
            }
        }

        // Cập nhật nhãn và số liệu
        capNhatSoLieu(c1, c2, c3, c4);
    }

    private void capNhatSoLieu(int c1, int c2, int c3, int c4) {
        int tong = c1 + c2 + c3 + c4;
        if (lblTongXeBadge != null) lblTongXeBadge.setText("🚗 Đang tại xưởng: " + tong + " xe");

        if (lblChoKiemTra != null) lblChoKiemTra.setText(c1 + " xe");
        if (lblBadgeChoKiemTra != null) lblBadgeChoKiemTra.setText(String.valueOf(c1));

        if (lblDangKiemTra != null) lblDangKiemTra.setText(c2 + " xe");
        if (lblBadgeDangKiemTra != null) lblBadgeDangKiemTra.setText(String.valueOf(c2));

        if (lblDangSuaChua != null) lblDangSuaChua.setText(c3 + " xe");
        if (lblBadgeDangSuaChua != null) lblBadgeDangSuaChua.setText(String.valueOf(c3));

        if (lblChoGiaoXe != null) lblChoGiaoXe.setText(c4 + " xe");
        if (lblBadgeChoGiaoXe != null) lblBadgeChoGiaoXe.setText(String.valueOf(c4));
    }

    // =========================================================================
    // 4. TẠO CARD THẺ XE (VEHICLE QUEUE CARD)
    // =========================================================================

    private VBox taoTheXe(PhieuTiepNhan p) {
        VBox card = new VBox(7);
        card.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-background-radius: 8px; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-radius: 8px; " +
                "-fx-padding: 10px 12px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 4, 0, 0, 1);"
        );

        // 1. Header Card: Biển số xe & Mã tiếp nhận
        HBox topBox = new HBox(8);
        topBox.setAlignment(Pos.CENTER_LEFT);

        String bienSo = (p.getPhuongTien() != null && p.getPhuongTien().getBienSoXe() != null)
                ? p.getPhuongTien().getBienSoXe() : "Chưa có biển số";
        Label lblBienSo = new Label(bienSo);
        lblBienSo.setStyle("-fx-text-fill: #102A46; -fx-font-weight: 900; -fx-font-size: 15px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblMa = new Label(p.getMaTiepNhan() != null ? p.getMaTiepNhan() : "TN-" + p.getId());
        lblMa.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 3px 8px; -fx-background-radius: 6px; -fx-font-size: 11.5px; -fx-font-weight: bold;");

        topBox.getChildren().addAll(lblBienSo, spacer, lblMa);

        // 2. Thông tin dòng xe & hãng xe
        String mauXe = "-";
        if (p.getPhuongTien() != null) {
            PhuongTien pt = p.getPhuongTien();
            String hang = pt.getHangXe() != null ? pt.getHangXe().getTenHang() : "";
            String mau = pt.getMauXe() != null ? pt.getMauXe() : "";
            mauXe = (hang + " " + mau).trim();
            if (pt.getMauSacXe() != null && !pt.getMauSacXe().isBlank()) {
                mauXe += " (" + pt.getMauSacXe() + ")";
            }
        }
        Label lblMauXe = new Label("🚗 " + mauXe);
        lblMauXe.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px; -fx-font-weight: 700;");

        // 3. Thông tin chủ xe & Số điện thoại
        String tenKhach = "Khách vãng lai";
        String sdt = "";
        if (p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null) {
            tenKhach = p.getPhuongTien().getKhachHang().getDanhXungVaHoTen();
            sdt = p.getPhuongTien().getKhachHang().getSoDienThoai();
        }
        Label lblKhach = new Label("👤 " + tenKhach + (sdt != null && !sdt.isBlank() ? " | 📞 " + sdt : ""));
        lblKhach.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12.5px;");

        // 4. Kỹ thuật viên phụ trách kiểm tra xe (do Lễ tân chỉ định lúc tiếp nhận)
        String tenKTV = (p.getNhanVienTiepNhan() != null && p.getNhanVienTiepNhan().getHoTen() != null)
                ? p.getNhanVienTiepNhan().getHoTen() : "Chưa chỉ định";
        Label lblKTV = new Label("👨‍🔧 KTV kiểm tra: " + tenKTV);
        lblKTV.setStyle("-fx-text-fill: #1e40af; -fx-font-size: 12.5px; -fx-font-weight: 700;");

        // 5. Thời gian xe vào xưởng & Thời gian đã lưu lại
        String thoiGianLuu = tinhThoiGianLuu(p.getThoiGianTiepNhan());
        Label lblThoiGian = new Label("🕒 " + thoiGianLuu);
        lblThoiGian.setStyle("-fx-text-fill: #0284c7; -fx-font-size: 12.5px; -fx-font-weight: 600;");

        // 6. Yêu cầu / Hiện tượng hỏng hóc
        String yeuCau = p.getYeuCauKhachHang() != null ? p.getYeuCauKhachHang() : "Không có ghi chú yêu cầu";
        Label lblYeuCau = new Label("📝 " + yeuCau);
        lblYeuCau.setWrapText(true);
        lblYeuCau.setStyle("-fx-text-fill: #475569; -fx-font-size: 12.5px; -fx-background-color: #f8fafc; -fx-padding: 5px 8px; -fx-background-radius: 6px;");

        // 7. Nút Thao Tác (Chi Tiết & Chuyển Bước / Xem Báo Giá & Phân Công)
        HBox actionBox = new HBox(6);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnChiTiet = new Button("👁️ Chi Tiết");
        btnChiTiet.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
        btnChiTiet.setOnAction(e -> hienThiModalChiTiet(p));

        Button btnChuyenBuoc = new Button(getButtonChuyenBuocText(p.getTrangThai()));
        btnChuyenBuoc.setStyle(getButtonChuyenBuocStyle(p.getTrangThai()));
        btnChuyenBuoc.setOnAction(e -> xuLyChuyenBuoc(p));

        Region actSpacer = new Region();
        HBox.setHgrow(actSpacer, Priority.ALWAYS);

        actionBox.getChildren().addAll(btnChiTiet, actSpacer, btnChuyenBuoc);

        card.getChildren().addAll(topBox, lblMauXe, lblKhach, lblKTV, lblThoiGian, lblYeuCau, actionBox);
        return card;
    }

    private String tinhThoiGianLuu(LocalDateTime tgVao) {
        if (tgVao == null) return "Vừa vào xưởng";
        Duration d = Duration.between(tgVao, LocalDateTime.now());
        long phut = d.toMinutes();
        if (phut < 1) return "Vào lúc " + tgVao.format(timeFmt) + " (Vừa vào)";
        if (phut < 60) return "Vào lúc " + tgVao.format(timeFmt) + " (" + phut + " phút trước)";
        long gio = d.toHours();
        long phutDu = phut % 60;
        return "Vào lúc " + tgVao.format(timeFmt) + " (" + gio + "h " + phutDu + "p trước)";
    }

    private String getButtonChuyenBuocText(TrangThaiPhieuTiepNhan tt) {
        if (tt == null) return "Kiểm tra xe ➔";
        return switch (tt) {
            case CHO_TIEP_NHAN, DA_TIEP_NHAN -> "Kiểm tra xe ➔";
            case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA -> "📄 Xem Báo Giá & Phân Công";
            case DANG_SUA_CHUA -> "Báo xong ➔";
            case HOAN_THANH -> "Thu ngân 🧾";
            default -> "Chi tiết";
        };
    }

    private String getButtonChuyenBuocStyle(TrangThaiPhieuTiepNhan tt) {
        if (tt == null) return "-fx-background-color: #2563eb; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 12px; -fx-background-radius: 6px; -fx-cursor: hand;";
        return switch (tt) {
            case CHO_TIEP_NHAN, DA_TIEP_NHAN ->
                    "-fx-background-color: #2563eb; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 12px; -fx-background-radius: 6px; -fx-cursor: hand;";
            case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA ->
                    "-fx-background-color: #ea580c; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 12px; -fx-background-radius: 6px; -fx-cursor: hand;";
            case DANG_SUA_CHUA ->
                    "-fx-background-color: #ea580c; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 12px; -fx-background-radius: 6px; -fx-cursor: hand;";
            case HOAN_THANH ->
                    "-fx-background-color: #059669; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 12px; -fx-background-radius: 6px; -fx-cursor: hand;";
            default ->
                    "-fx-background-color: #475569; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5px 12px; -fx-background-radius: 6px; -fx-cursor: hand;";
        };
    }

    // =========================================================================
    // 5. EVENT HANDLERS & XỬ LÝ CHUYỂN BƯỚC / MODAL BÁO GIÁ & PHÂN CÔNG
    // =========================================================================

    private void xuLyChuyenBuoc(PhieuTiepNhan p) {
        if (p == null) return;

        TrangThaiPhieuTiepNhan hienTai = p.getTrangThai();
        if (hienTai == null) hienTai = TrangThaiPhieuTiepNhan.CHO_TIEP_NHAN;

        switch (hienTai) {
            case CHO_TIEP_NHAN, DA_TIEP_NHAN -> {
                // KTV bắt đầu kiểm tra xe
                boolean ok = phieuTiepNhanService.capNhatTrangThai(p.getId(), TrangThaiPhieuTiepNhan.DANG_KIEM_TRA);
                if (ok) {
                    p.setTrangThai(TrangThaiPhieuTiepNhan.DANG_KIEM_TRA);
                    taiDanhSachHangDoi();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Không thể cập nhật tiến trình xe!").showAndWait();
                }
            }
            case DANG_KIEM_TRA, CHO_XAC_NHAN_BAO_GIA -> {
                // Lễ tân mở Popup: Xem Báo Giá chi tiết, đọc cho khách duyệt và Phân Công Thợ
                hienThiModalBaoGiaVaPhanCong(p);
            }
            case DANG_SUA_CHUA -> {
                // Báo xong sửa chữa
                boolean ok = phieuTiepNhanService.capNhatTrangThai(p.getId(), TrangThaiPhieuTiepNhan.HOAN_THANH);
                if (ok) {
                    p.setTrangThai(TrangThaiPhieuTiepNhan.HOAN_THANH);
                    taiDanhSachHangDoi();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Không thể cập nhật tiến trình xe!").showAndWait();
                }
            }
            case HOAN_THANH -> {
                // Xe đã xong -> Chuyển sang thu ngân / lập hóa đơn
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xe đã hoàn thành");
                alert.setHeaderText("Bàn giao xe & Lập hóa đơn");
                alert.setContentText("Xe " + (p.getPhuongTien() != null ? p.getPhuongTien().getBienSoXe() : "") +
                        " đã sửa xong. Bạn có muốn chuyển sang màn hình Lập Hóa Đơn & Thu Ngân không?");
                alert.showAndWait().ifPresent(res -> {
                    if (res == ButtonType.OK) {
                        DieuHuongUtil.chuyenManHinh(boxChoGiaoXe,
                                "/com/pengugarage/view/letan/lap-hoa-don-view.fxml",
                                "Pengu Garage Service ERP - Lập Hóa Đơn & Thu Ngân");
                    }
                });
            }
            default -> {}
        }
    }

    /**
     * MODAL XEM BÁO GIÁ & PHÂN CÔNG KỸ THUẬT VIÊN (DO LỄ TÂN ĐIỀU PHỐI)
     * - Hiển thị chi tiết bảng báo giá KTV vừa kê (tiền công + phụ tùng).
     * - Lễ tân đọc báo giá cho khách duyệt.
     * - Khi khách đồng ý, Lễ tân chọn KTV đang có mặt tại gara để phân công sửa.
     * - Nhấn Xác nhận -> Tự động tạo PhieuPhanCong, xe nhảy sang Cột 3 "Đang sửa chữa".
     */
    private void hienThiModalBaoGiaVaPhanCong(PhieuTiepNhan p) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Báo Giá & Phân Công Sửa Chữa");
        dialog.setHeaderText("BÁO GIÁ XE: " + (p.getPhuongTien() != null ? p.getPhuongTien().getBienSoXe() : "") +
                " | Khách hàng: " + (p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null ? p.getPhuongTien().getKhachHang().getDanhXungVaHoTen() : "Khách lẻ"));

        ButtonType btnChotPhanCong = new ButtonType("✅ Khách Chốt & Phân Công Thợ", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnTuChoi = new ButtonType("❌ Khách Từ Chối", ButtonBar.ButtonData.OTHER);
        ButtonType btnDong = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(btnChotPhanCong, btnTuChoi, btnDong);

        VBox contentBox = new VBox(12);
        contentBox.setPrefWidth(620);
        contentBox.setPadding(new Insets(10, 10, 10, 10));

        // Lấy dữ liệu báo giá từ DB
        PhieuBaoGia pbg = phieuBaoGiaDAO.layTheoPhieuTiepNhanId(p.getId());

        // 1. Khu vực hiển thị bảng chi tiết báo giá
        VBox boxBangBaoGia = new VBox(6);
        boxBangBaoGia.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10px; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px;");

        Label lblTieuDeBaoGia = new Label("📋 Bảng Báo Giá Chi Tiết (Kỹ thuật viên đã kê):");
        lblTieuDeBaoGia.setStyle("-fx-font-weight: bold; -fx-text-fill: #102A46; -fx-font-size: 13.5px;");
        boxBangBaoGia.getChildren().add(lblTieuDeBaoGia);

        double tongTien = 0;
        if (pbg != null && pbg.getDanhSachChiTiet() != null && !pbg.getDanhSachChiTiet().isEmpty()) {
            TableView<ChiTietBaoGia> tableChiTiet = new TableView<>();
            tableChiTiet.setPrefHeight(160);

            TableColumn<ChiTietBaoGia, String> colLoai = new TableColumn<>("Phân Loại");
            colLoai.setPrefWidth(90);
            colLoai.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLoaiHangMuc() == LoaiHangMuc.DICH_VU ? "Dịch vụ" : "Phụ tùng"));

            TableColumn<ChiTietBaoGia, String> colTen = new TableColumn<>("Hạng Mục / Linh Kiện");
            colTen.setPrefWidth(220);
            colTen.setCellValueFactory(cell -> {
                ChiTietBaoGia ct = cell.getValue();
                if (ct.getLoaiHangMuc() == LoaiHangMuc.DICH_VU && ct.getDichVu() != null) {
                    return new SimpleStringProperty(ct.getDichVu().getTenDichVu());
                } else if (ct.getSanPham() != null) {
                    return new SimpleStringProperty(ct.getSanPham().getTenSanPham());
                }
                return new SimpleStringProperty(ct.getGhiChu() != null ? ct.getGhiChu() : "Hạng mục sửa chữa");
            });

            TableColumn<ChiTietBaoGia, String> colSL = new TableColumn<>("SL");
            colSL.setPrefWidth(45);
            colSL.setStyle("-fx-alignment: CENTER;");
            colSL.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getSoLuong())));

            TableColumn<ChiTietBaoGia, String> colDonGia = new TableColumn<>("Đơn Giá");
            colDonGia.setPrefWidth(110);
            colDonGia.setStyle("-fx-alignment: CENTER_RIGHT;");
            colDonGia.setCellValueFactory(cell -> new SimpleStringProperty(tienFmt.format(cell.getValue().getDonGia())));

            TableColumn<ChiTietBaoGia, String> colThanhTien = new TableColumn<>("Thành Tiền");
            colThanhTien.setPrefWidth(120);
            colThanhTien.setStyle("-fx-alignment: CENTER_RIGHT; -fx-font-weight: bold;");
            colThanhTien.setCellValueFactory(cell -> new SimpleStringProperty(tienFmt.format(cell.getValue().getThanhTien())));

            tableChiTiet.getColumns().addAll(colLoai, colTen, colSL, colDonGia, colThanhTien);
            tableChiTiet.setItems(FXCollections.observableArrayList(pbg.getDanhSachChiTiet()));

            tongTien = pbg.getTongTienDuKien();
            boxBangBaoGia.getChildren().add(tableChiTiet);

        } else {
            Label lblChuaCo = new Label("⚠️ Kỹ thuật viên chưa hoàn tất bảng kê chi tiết trên hệ thống.\n(Lễ tân vẫn có thể tư vấn trực tiếp và phân công thợ thi công ngay bên dưới).");
            lblChuaCo.setStyle("-fx-text-fill: #ea580c; -fx-font-size: 12.5px; -fx-font-style: italic;");
            boxBangBaoGia.getChildren().add(lblChuaCo);
        }

        // Dòng tổng tiền dự kiến
        HBox boxTongTien = new HBox(8);
        boxTongTien.setAlignment(Pos.CENTER_RIGHT);
        Label lblTextTong = new Label("TỔNG TIỀN DỰ KIẾN:");
        lblTextTong.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #102A46;");
        Label lblValTong = new Label(tienFmt.format(tongTien));
        lblValTong.setStyle("-fx-font-weight: 900; -fx-font-size: 18px; -fx-text-fill: #ea580c;");
        boxTongTien.getChildren().addAll(lblTextTong, lblValTong);
        boxBangBaoGia.getChildren().add(boxTongTien);

        // 2. Khu vực Lễ tân Phân công Kỹ thuật viên sửa chữa
        VBox boxPhanCong = new VBox(8);
        boxPhanCong.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 12px; -fx-background-radius: 8px; -fx-border-color: #cbd5e1; -fx-border-radius: 8px;");

        Label lblTieuDePC = new Label("👨‍🔧 PHÂN CÔNG KỸ THUẬT VIÊN SỬA CHỮA (Lễ tân chỉ định):");
        lblTieuDePC.setStyle("-fx-font-weight: 800; -fx-text-fill: #1e3a8a; -fx-font-size: 13.5px;");

        ComboBox<NhanVien> cboKTVPhanCong = new ComboBox<>();
        cboKTVPhanCong.setMaxWidth(Double.MAX_VALUE);
        cboKTVPhanCong.setPromptText("-- Chọn thợ kỹ thuật viên thi công chính --");

        List<NhanVien> dsKTV = nhanVienDAO.layDanhSachTheoChucVu(ChucVu.KY_THUAT_VIEN);
        cboKTVPhanCong.setItems(FXCollections.observableArrayList(dsKTV));

        cboKTVPhanCong.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "👨‍🔧 " + item.getHoTen() + " (" + item.getMaNhanVien() + ")");
            }
        });
        cboKTVPhanCong.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "👨‍🔧 " + item.getHoTen() + " (" + item.getMaNhanVien() + ")");
            }
        });

        // Mặc định chọn KTV đã kiểm tra xe ban đầu nếu có
        if (p.getNhanVienTiepNhanId() > 0) {
            for (NhanVien nv : dsKTV) {
                if (nv.getId() == p.getNhanVienTiepNhanId()) {
                    cboKTVPhanCong.setValue(nv);
                    break;
                }
            }
        }
        if (cboKTVPhanCong.getValue() == null && !dsKTV.isEmpty()) {
            cboKTVPhanCong.getSelectionModel().selectFirst();
        }

        TextField txtGhiChuPhanCong = new TextField();
        txtGhiChuPhanCong.setPromptText("Ghi chú cho thợ (VD: Khách cần xe gấp trước 11h trưa, kiểm tra thêm bình ắc quy)...");
        txtGhiChuPhanCong.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6px; -fx-padding: 7px 12px; -fx-font-size: 13.5px;");

        boxPhanCong.getChildren().addAll(lblTieuDePC, cboKTVPhanCong, txtGhiChuPhanCong);

        contentBox.getChildren().addAll(boxBangBaoGia, boxPhanCong);
        dialog.getDialogPane().setContent(contentBox);

        // Xử lý khi nhấn nút
        dialog.showAndWait().ifPresent(res -> {
            if (res == btnChotPhanCong) {
                // Khách đồng ý chốt giá -> Tiến hành phân công thợ
                NhanVien ktvChon = cboKTVPhanCong.getValue();
                if (ktvChon == null) {
                    new Alert(Alert.AlertType.WARNING, "Vui lòng chọn Kỹ thuật viên để phân công sửa chữa!").showAndWait();
                    return;
                }

                // 1. Tạo Phiếu Phân Công vào DB
                PhieuPhanCong ppc = new PhieuPhanCong();
                ppc.setMaPhanCong("PC-" + (p.getMaTiepNhan() != null ? p.getMaTiepNhan() : "TN" + p.getId()));
                ppc.setKyThuatVienId(ktvChon.getId());
                ppc.setThoiGianPhanCong(LocalDateTime.now());
                ppc.setTrangThaiSua(TrangThaiSua.CHO_THUC_HIEN);
                ppc.setGhiChu(txtGhiChuPhanCong.getText() != null ? txtGhiChuPhanCong.getText().trim() : "");
                phieuPhanCongDAO.them(ppc);

                // 2. Cập nhật Báo giá sang ĐÃ CHẤP THUẬN
                if (pbg != null) {
                    phieuBaoGiaDAO.capNhatTrangThai(pbg.getId(), TrangThaiBaoGia.DA_CHAP_THUAN, null);
                }

                // 3. Cập nhật Phiếu Tiếp Nhận sang ĐANG SỬA CHỮA
                phieuTiepNhanService.capNhatTrangThai(p.getId(), TrangThaiPhieuTiepNhan.DANG_SUA_CHUA);
                p.setTrangThai(TrangThaiPhieuTiepNhan.DANG_SUA_CHUA);

                Alert okAlert = new Alert(Alert.AlertType.INFORMATION);
                okAlert.setTitle("Phân công thành công");
                okAlert.setHeaderText("ĐÃ CHỐT BÁO GIÁ VÀ PHÂN CÔNG THỢ THI CÔNG!");
                okAlert.setContentText(
                        "• Kỹ thuật viên nhận việc: " + ktvChon.getHoTen() + " (" + ktvChon.getMaNhanVien() + ")\n" +
                        "• Xe " + (p.getPhuongTien() != null ? p.getPhuongTien().getBienSoXe() : "") + " đã được chuyển sang khoang ĐANG SỬA CHỮA."
                );
                okAlert.showAndWait();

                // Tải lại bảng Kanban
                taiDanhSachHangDoi();

            } else if (res == btnTuChoi) {
                // Khách từ chối báo giá
                TextInputDialog inputLyDo = new TextInputDialog("Chi phí vượt dự kiến của khách");
                inputLyDo.setTitle("Khách từ chối báo giá");
                inputLyDo.setHeaderText("Ghi nhận lý do khách hàng từ chối:");
                inputLyDo.setContentText("Lý do:");
                inputLyDo.showAndWait().ifPresent(lyDo -> {
                    if (pbg != null) {
                        phieuBaoGiaDAO.capNhatTrangThai(pbg.getId(), TrangThaiBaoGia.TU_CHOI, lyDo);
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đã ghi nhận khách từ chối báo giá: " + lyDo);
                    alert.showAndWait();
                    taiDanhSachHangDoi();
                });
            }
        });
    }

    private void hienThiModalChiTiet(PhieuTiepNhan p) {
        if (p == null) return;

        String bienSo = p.getPhuongTien() != null ? p.getPhuongTien().getBienSoXe() : "-";
        String chuXe = (p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null)
                ? p.getPhuongTien().getKhachHang().getHoTen() : "Khách lẻ";
        String sdt = (p.getPhuongTien() != null && p.getPhuongTien().getKhachHang() != null)
                ? p.getPhuongTien().getKhachHang().getSoDienThoai() : "-";
        String ktv = (p.getNhanVienTiepNhan() != null && p.getNhanVienTiepNhan().getHoTen() != null)
                ? p.getNhanVienTiepNhan().getHoTen() : "Chưa chỉ định";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết xe tại xưởng");
        alert.setHeaderText("PHIẾU TIẾP NHẬN: " + p.getMaTiepNhan() + " | Biển số: " + bienSo);
        alert.setContentText(
                "• Chủ xe: " + chuXe + " (📞 " + sdt + ")\n" +
                "• KTV phụ trách kiểm tra: " + ktv + "\n" +
                "• Thời gian vào: " + (p.getThoiGianTiepNhan() != null ? p.getThoiGianTiepNhan().format(fullDateFmt) : "-") + "\n" +
                "• Số KM ODO: " + (long) p.getSoKMHienTai() + " km | Số chìa khóa: " + p.getSoChiaKhoa() + "\n" +
                "• Yêu cầu: " + (p.getYeuCauKhachHang() != null ? p.getYeuCauKhachHang() : "-") + "\n" +
                "• Ngoại thất: " + (p.getTinhTrangNgoaiThat() != null ? p.getTinhTrangNgoaiThat() : "-") + "\n" +
                "• Nội thất: " + (p.getTinhTrangNoiThat() != null ? p.getTinhTrangNoiThat() : "-") + "\n" +
                "• Ghi chú: " + (p.getGhiChu() != null ? p.getGhiChu() : "-")
        );
        alert.showAndWait();
    }

    @FXML
    public void onTiepNhanMoiClick(ActionEvent event) {
        DieuHuongUtil.chuyenManHinh(boxChoKiemTra,
                "/com/pengugarage/view/letan/tiep-nhan-xe-view.fxml",
                "Pengu Garage Service ERP - Tiếp Nhận Xe");
    }

    @FXML
    public void onLamMoiClick(ActionEvent event) {
        taiDanhSachHangDoi();
    }

    @FXML
    public void onXoaLocClick(ActionEvent event) {
        if (txtTimKiem != null) txtTimKiem.clear();
        taiDanhSachHangDoi();
    }
}
