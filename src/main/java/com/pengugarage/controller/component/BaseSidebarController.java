package com.pengugarage.controller.component;

import com.pengugarage.model.entity.NhanVien;
import com.pengugarage.model.enums.ChucVu;
import com.pengugarage.util.DieuHuongUtil;
import com.pengugarage.util.PhienDangNhapUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller cơ sở cho tất cả các Sidebar trong hệ thống Pengu Garage.
 * Tự động tải thông tin phiên đăng nhập (Họ tên, Avatar chữ cái đầu, chức vụ)
 * và xử lý sự kiện Đăng xuất.
 */
public abstract class BaseSidebarController {

    @FXML
    protected Label userAvatarLabel;

    @FXML
    protected Label userNameLabel;

    @FXML
    protected Label userRoleLabel;

    @FXML
    public void initialize() {
        taiThongTinNguoiDung();
    }

    /**
     * Tự động lấy thông tin người dùng từ phiên đăng nhập hiện tại
     */
    protected void taiThongTinNguoiDung() {
        PhienDangNhapUtil session = PhienDangNhapUtil.getInstance();
        if (session.isLoggedIn()) {
            NhanVien nv = session.getNhanVien();
            if (nv != null) {
                String hoTen = nv.getHoTen();
                if (userNameLabel != null && hoTen != null) {
                    userNameLabel.setText(hoTen);
                }

                if (userAvatarLabel != null && hoTen != null && !hoTen.trim().isEmpty()) {
                    String[] parts = hoTen.trim().split("\\s+");
                    String lastWord = parts[parts.length - 1];
                    userAvatarLabel.setText(lastWord.substring(0, 1).toUpperCase());
                }

                if (userRoleLabel != null && nv.getChucVu() != null) {
                    userRoleLabel.setText(layMoTaChucVu(nv.getChucVu()));
                }
            }
        }
    }

    protected String layMoTaChucVu(ChucVu chucVu) {
        if (chucVu == ChucVu.QUAN_LY) {
            return "Quản lý";
        } else if (chucVu == ChucVu.KY_THUAT_VIEN) {
            return "Kỹ thuật viên";
        } else if (chucVu == ChucVu.LE_TAN) {
            return "Lễ tân & Thu ngân";
        }
        return chucVu.name();
    }

    /**
     * Đăng xuất khỏi hệ thống và quay lại màn hình Đăng nhập
     */
    @FXML
    public void onLogoutClick() {
        PhienDangNhapUtil.getInstance().logout();
        DieuHuongUtil.chuyenManHinh(userNameLabel,
                "/com/pengugarage/view/chung/dang-nhap-view.fxml",
                "Pengu Garage Service ERP - Đăng Nhập Hệ Thống");
    }
}
