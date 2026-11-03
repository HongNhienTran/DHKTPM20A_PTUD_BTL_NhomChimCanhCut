package com.pengugarage.controller.component;

import com.pengugarage.dao.HangXeDAO;
import com.pengugarage.dao.KhachHangDAO;
import com.pengugarage.dao.PhuongTienDAO;
import com.pengugarage.model.entity.HangXe;
import com.pengugarage.model.entity.KhachHang;
import com.pengugarage.model.entity.PhuongTien;
import com.pengugarage.model.enums.NhienLieu;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller cho Modal Thêm Mới Phương Tiện & Khách Hàng.
 * Cho phép tra cứu khách hàng theo SĐT:
 * - Nếu đã có: hiển thị thông tin kèm danh xưng Anh/Chị và thêm xe cho khách hàng đó.
 * - Nếu chưa có: nhập thông tin khách hàng mới (Họ tên, SĐT, Giới tính, Địa chỉ) và lưu đồng thời.
 */
public class ModalThemPhuongTienController {

    @FXML private VBox rootPane;

    // Khối 1: Khách hàng
    @FXML private Label lblTrangThaiKhach;
    @FXML private TextField txtTimSdtKhach;
    @FXML private Button btnTimKhach;
    @FXML private Button btnDoiKhach;

    @FXML private VBox boxKhachDaTonTai;
    @FXML private Label lblTenKhachTimThay;
    @FXML private Label lblMaKhachTimThay;
    @FXML private Label lblChiTietKhachTimThay;

    @FXML private VBox boxNhapKhachMoi;
    @FXML private TextField txtHoTenKhach;
    @FXML private RadioButton radNam;
    @FXML private RadioButton radNu;
    @FXML private ToggleGroup grpGioiTinh;
    @FXML private TextField txtSdtKhach;
    @FXML private TextField txtDiaChiKhach;

    // Khối 2: Phương tiện
    @FXML private TextField txtBienSo;
    @FXML private ComboBox<HangXe> cboHangXe;
    @FXML private TextField txtMauXe;
    @FXML private TextField txtMauSacXe;
    @FXML private TextField txtNamSanXuat;
    @FXML private ComboBox<NhienLieu> cboNhienLieu;

    @FXML private Label lblThongBao;
    @FXML private Button btnLuu;

    // DAOs
    private final HangXeDAO hangXeDAO = new HangXeDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final PhuongTienDAO phuongTienDAO = new PhuongTienDAO();

    // Dữ liệu nội bộ
    private KhachHang khachHangHienTai = null;
    private PhuongTien phuongTienVuaTao = null;

    @FXML
    public void initialize() {
        // Tải danh sách Hãng xe
        List<HangXe> dsHangXe = hangXeDAO.layTatCa();
        if (cboHangXe != null) {
            cboHangXe.setItems(FXCollections.observableArrayList(dsHangXe));
            if (!dsHangXe.isEmpty()) {
                cboHangXe.getSelectionModel().selectFirst();
            }
        }

        // Tải danh mục Nhiên liệu
        if (cboNhienLieu != null) {
            cboNhienLieu.setItems(FXCollections.observableArrayList(NhienLieu.values()));
            cboNhienLieu.getSelectionModel().select(NhienLieu.XANG);
        }

        // Auto uppercase biển số xe
        if (txtBienSo != null) {
            txtBienSo.textProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && !newV.equals(newV.toUpperCase())) {
                    txtBienSo.setText(newV.toUpperCase());
                }
            });
        }

        // Nhấn Enter ở ô tìm SĐT
        if (txtTimSdtKhach != null) {
            txtTimSdtKhach.setOnAction(e -> onTimKhachClick());
        }
    }

    /**
     * Điền sẵn biển số xe từ màn hình gọi nếu người dùng đã gõ trước đó
     */
    public void setBienSoKhoiTao(String bienSo) {
        if (bienSo != null && !bienSo.trim().isEmpty() && txtBienSo != null) {
            txtBienSo.setText(bienSo.trim().toUpperCase());
        }
    }

    /**
     * Lấy phương tiện vừa được tạo thành công
     */
    public PhuongTien getPhuongTienVuaTao() {
        return phuongTienVuaTao;
    }

    /**
     * Xử lý tra cứu khách hàng theo Số điện thoại
     */
    @FXML
    public void onTimKhachClick() {
        String sdt = txtTimSdtKhach != null ? txtTimSdtKhach.getText().trim() : "";
        if (sdt.isEmpty()) {
            hienThongBao("Vui lòng nhập số điện thoại để tra cứu khách hàng!", false);
            if (txtTimSdtKhach != null) txtTimSdtKhach.requestFocus();
            return;
        }

        KhachHang kh = khachHangDAO.timTheoSoDienThoai(sdt);
        if (kh != null) {
            // Khách hàng đã tồn tại
            this.khachHangHienTai = kh;
            if (boxKhachDaTonTai != null) {
                boxKhachDaTonTai.setVisible(true);
                boxKhachDaTonTai.setManaged(true);
            }
            if (boxNhapKhachMoi != null) {
                boxNhapKhachMoi.setVisible(false);
                boxNhapKhachMoi.setManaged(false);
            }
            if (btnDoiKhach != null) {
                btnDoiKhach.setVisible(true);
                btnDoiKhach.setManaged(true);
            }

            if (lblTrangThaiKhach != null) {
                lblTrangThaiKhach.setText("✓ Đã có hồ sơ khách hàng");
                lblTrangThaiKhach.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-padding: 3px 10px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
            }
            if (lblTenKhachTimThay != null) {
                lblTenKhachTimThay.setText(kh.getDanhXungVaHoTen());
            }
            if (lblMaKhachTimThay != null) {
                lblMaKhachTimThay.setText(kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "");
            }
            if (lblChiTietKhachTimThay != null) {
                String dc = kh.getDiaChi() != null && !kh.getDiaChi().isBlank() ? kh.getDiaChi() : "Chưa cập nhật địa chỉ";
                lblChiTietKhachTimThay.setText("📞 " + kh.getSoDienThoai() + " • 🏠 " + dc + " • Giới tính: " + (kh.getGioiTinh() != null ? kh.getGioiTinh() : "Nam"));
            }
            hienThongBao("Đã tìm thấy hồ sơ của " + kh.getDanhXungVaHoTen() + "! Xe sẽ được gắn với khách hàng này.", true);
        } else {
            // Khách hàng mới chưa có trong hệ thống
            this.khachHangHienTai = null;
            if (boxKhachDaTonTai != null) {
                boxKhachDaTonTai.setVisible(false);
                boxKhachDaTonTai.setManaged(false);
            }
            if (boxNhapKhachMoi != null) {
                boxNhapKhachMoi.setVisible(true);
                boxNhapKhachMoi.setManaged(true);
            }
            if (btnDoiKhach != null) {
                btnDoiKhach.setVisible(false);
                btnDoiKhach.setManaged(false);
            }

            if (lblTrangThaiKhach != null) {
                lblTrangThaiKhach.setText("✨ Khách hàng mới");
                lblTrangThaiKhach.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-padding: 3px 10px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
            }

            if (txtSdtKhach != null) {
                txtSdtKhach.setText(sdt);
            }
            if (txtHoTenKhach != null) {
                txtHoTenKhach.requestFocus();
            }
            hienThongBao("SĐT " + sdt + " chưa có hồ sơ. Vui lòng nhập thông tin để tạo khách hàng mới!", false);
        }
    }

    /**
     * Nút chọn khách hàng khác để tra cứu lại
     */
    @FXML
    public void onDoiKhachClick() {
        this.khachHangHienTai = null;
        if (boxKhachDaTonTai != null) {
            boxKhachDaTonTai.setVisible(false);
            boxKhachDaTonTai.setManaged(false);
        }
        if (boxNhapKhachMoi != null) {
            boxNhapKhachMoi.setVisible(true);
            boxNhapKhachMoi.setManaged(true);
        }
        if (btnDoiKhach != null) {
            btnDoiKhach.setVisible(false);
            btnDoiKhach.setManaged(false);
        }
        if (lblTrangThaiKhach != null) {
            lblTrangThaiKhach.setText("Tra cứu số điện thoại");
            lblTrangThaiKhach.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-padding: 3px 10px; -fx-background-radius: 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
        if (txtTimSdtKhach != null) {
            txtTimSdtKhach.clear();
            txtTimSdtKhach.requestFocus();
        }
    }

    /**
     * Lưu thông tin Phương tiện & Khách hàng
     */
    @FXML
    public void onLuuClick() {
        // ================= 1. XỬ LÝ KHÁCH HÀNG =================
        KhachHang kh = this.khachHangHienTai;
        if (kh == null) {
            // Cần tạo khách hàng mới từ form
            String hoTen = txtHoTenKhach != null ? txtHoTenKhach.getText().trim() : "";
            String sdt = txtSdtKhach != null ? txtSdtKhach.getText().trim() : "";
            String diaChi = txtDiaChiKhach != null ? txtDiaChiKhach.getText().trim() : "";
            String gioiTinh = (radNu != null && radNu.isSelected()) ? "Nữ" : "Nam";

            if (hoTen.isEmpty()) {
                hienThongBao("Vui lòng nhập họ và tên khách hàng!", false);
                if (txtHoTenKhach != null) txtHoTenKhach.requestFocus();
                return;
            }
            if (sdt.isEmpty() || !sdt.matches("^0[0-9]{9,10}$")) {
                hienThongBao("Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 số (bắt đầu bằng số 0).", false);
                if (txtSdtKhach != null) txtSdtKhach.requestFocus();
                return;
            }

            // Kiểm tra xem SĐT có vừa được người khác thêm chưa
            KhachHang daCo = khachHangDAO.timTheoSoDienThoai(sdt);
            if (daCo != null) {
                kh = daCo;
            } else {
                kh = new KhachHang();
                kh.setHoTen(hoTen);
                kh.setSoDienThoai(sdt);
                kh.setDiaChi(diaChi);
                kh.setGioiTinh(gioiTinh);

                boolean taoKhOk = khachHangDAO.them(kh);
                if (!taoKhOk) {
                    hienThongBao("Không thể tạo hồ sơ khách hàng mới vào CSDL! Vui lòng thử lại.", false);
                    return;
                }
            }
        }

        // ================= 2. XỬ LÝ PHƯƠNG TIỆN =================
        String bienSo = txtBienSo != null ? txtBienSo.getText().trim().toUpperCase() : "";
        HangXe hangXe = cboHangXe != null ? cboHangXe.getValue() : null;
        String mauXe = txtMauXe != null ? txtMauXe.getText().trim() : "";
        String mauSac = txtMauSacXe != null ? txtMauSacXe.getText().trim() : "";
        String namSxStr = txtNamSanXuat != null ? txtNamSanXuat.getText().trim() : "2022";
        NhienLieu nhienLieu = cboNhienLieu != null && cboNhienLieu.getValue() != null ? cboNhienLieu.getValue() : NhienLieu.XANG;

        if (bienSo.isEmpty()) {
            hienThongBao("Vui lòng nhập biển số xe!", false);
            if (txtBienSo != null) txtBienSo.requestFocus();
            return;
        }

        // Kiểm tra trùng biển số xe
        PhuongTien xeDaCo = phuongTienDAO.timTheoBienSo(bienSo);
        if (xeDaCo != null) {
            hienThongBao("Biển số xe " + bienSo + " đã tồn tại trong hệ thống (thuộc chủ xe: " + 
                    (xeDaCo.getKhachHang() != null ? xeDaCo.getKhachHang().getDanhXungVaHoTen() : "Khác") + ")!", false);
            return;
        }

        if (hangXe == null) {
            hienThongBao("Vui lòng chọn hãng xe!", false);
            if (cboHangXe != null) cboHangXe.requestFocus();
            return;
        }

        if (mauXe.isEmpty()) {
            hienThongBao("Vui lòng nhập dòng/mẫu xe (VD: Vios 1.5G, City RS...)!", false);
            if (txtMauXe != null) txtMauXe.requestFocus();
            return;
        }

        int namSX = 2022;
        try {
            namSX = Integer.parseInt(namSxStr);
            if (namSX < 1980 || namSX > 2030) {
                hienThongBao("Năm sản xuất xe phải từ 1980 đến 2030!", false);
                return;
            }
        } catch (NumberFormatException e) {
            hienThongBao("Năm sản xuất phải là số nguyên hợp lệ!", false);
            return;
        }

        // Tạo Entity PhuongTien
        PhuongTien pt = new PhuongTien();
        pt.setBienSoXe(bienSo);
        pt.setHangXe(hangXe);
        pt.setKhachHang(kh);
        pt.setMauXe(mauXe);
        pt.setMauSacXe(mauSac.isEmpty() ? "Trắng" : mauSac);
        pt.setNamSanXuat(namSX);
        pt.setNhienLieu(nhienLieu);

        boolean taoXeOk = phuongTienDAO.them(pt);
        if (taoXeOk) {
            this.phuongTienVuaTao = pt;
            onDongClick();
        } else {
            hienThongBao("Không thể lưu thông tin xe vào CSDL! Vui lòng kiểm tra lại kết nối.", false);
        }
    }

    @FXML
    public void onDongClick() {
        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            ((Stage) rootPane.getScene().getWindow()).close();
        }
    }

    private void hienThongBao(String msg, boolean thanhCong) {
        if (lblThongBao != null) {
            lblThongBao.setText(msg);
            lblThongBao.setVisible(true);
            lblThongBao.setManaged(true);
            lblThongBao.setStyle(thanhCong ? "-fx-text-fill: #059669; -fx-font-size: 12px; -fx-font-weight: bold;"
                                           : "-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }
}

