# Hướng Dẫn Phát Triển Chức Năng Mới (Ví dụ: Quản Lý Sản Phẩm)

Tài liệu này hướng dẫn chi tiết quy trình từng bước từ Database $\rightarrow$ DAO $\rightarrow$ Service $\rightarrow$ Controller $\rightarrow$ Giao diện FXML (thiết kế bằng Scene Builder) cho hệ thống **Pengu Garage Service ERP**.

---

## 🏛️ 1. Cấu Trúc Giao Diện Phân Theo Vai Trò (Actor Role-Based Architecture)

Hệ thống phân chia giao diện FXML và Controller theo 3 nhóm đối tượng người dùng chính:
- **Quản lý (`com.pengugarage.view.quanly`)**:
  - `dashboard-view.fxml` & `DashboardController.java`
  - Quản lý nhân viên, phụ tùng / sản phẩm, tài chính, báo cáo doanh thu, cấu hình garage.
- **Kỹ thuật viên (`com.pengugarage.view.kythuatvien`)**:
  - `dashboard-view.fxml` & `DashboardKyThuatVienController.java`
  - Tiếp nhận lệnh sửa chữa, cập nhật tiến độ công việc, kiểm tra vật tư/phụ tùng, báo cáo kỹ thuật.
- **Lễ tân & Thu ngân (`com.pengugarage.view.letan`)**:
  - `dashboard-view.fxml` & `DashboardLeTanController.java`
  - Tiếp nhận xe vào gara, tạo phiếu tiếp nhận, lập hóa đơn thanh toán, tra cứu thông tin khách hàng.
- **Dùng chung (`com.pengugarage.view.chung`)**:
  - `splash-view.fxml` & `SplashController.java`
  - `dang-nhap-view.fxml` & `DangNhapController.java`

---

## 🏗️ 2. Sơ Đồ Kiến Trúc Luồng Dữ Liệu

$$\text{Database (MariaDB)} \longrightarrow \text{DAO (Truy vấn SQL)} \longrightarrow \text{Service (Nghiệp vụ)} \longrightarrow \text{Controller (JavaFX)} \longleftrightarrow \text{View FXML (Scene Builder)}$$

---

## 🚀 3. Quy Trình 5 Bước Thực Hiện

### Bước 1: Viết lớp Data Access Object (`SanPhamDAO.java`)
Lớp này chịu trách nhiệm kết nối MariaDB thông qua `CoSoDuLieuUtil` và chuyển đổi kết quả `ResultSet` thành danh sách đối tượng `SanPham`.

Tạo file: `src/main/java/com/pengugarage/dao/SanPhamDAO.java`
```java
package com.pengugarage.dao;

import com.pengugarage.model.entity.SanPham;
import com.pengugarage.util.CoSoDuLieuUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    /**
     * Lấy toàn bộ danh sách sản phẩm từ bảng san_pham
     */
    public List<SanPham> layTatCa() {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM san_pham ORDER BY id DESC";

        try (Connection conn = CoSoDuLieuUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setId(rs.getInt("id"));
                sp.setDanhMucId(rs.getInt("danh_muc_id"));
                sp.setMaSanPham(rs.getString("ma_san_pham"));
                sp.setTenSanPham(rs.getString("ten_san_pham"));
                sp.setDonViTinh(rs.getString("don_vi_tinh"));
                sp.setSoLuongTon(rs.getInt("so_luong_ton"));
                sp.setDonGiaBan(rs.getDouble("don_gia_ban"));
                sp.setTrangThai(rs.getBoolean("trang_thai"));
                ds.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}
```

---

### Bước 2: Viết lớp Service nghiệp vụ (`SanPhamService.java`)
Service chứa các quy tắc logic nghiệp vụ, gọi DAO để lấy dữ liệu hoặc thực hiện kiểm tra hợp lệ trước khi gửi sang giao diện.

Tạo file: `src/main/java/com/pengugarage/service/SanPhamService.java`
```java
package com.pengugarage.service;

import com.pengugarage.dao.SanPhamDAO;
import com.pengugarage.model.entity.SanPham;

import java.util.List;

public class SanPhamService {
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public List<SanPham> layDanhSachSanPham() {
        return sanPhamDAO.layTatCa();
    }
}
```

---

### Bước 3: Thiết kế giao diện bằng Scene Builder (`san-pham-view.fxml`)

1. **Khởi tạo file FXML sườn cơ bản**:
   Tạo file `src/main/resources/com/pengugarage/view/quanly/san-pham-view.fxml` với nội dung:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <?import javafx.scene.layout.VBox?>

   <VBox xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
         fx:controller="com.pengugarage.controller.SanPhamController"
         stylesheets="@../../css/style.css" styleClass="dark-root"
         prefHeight="760.0" prefWidth="1280.0">
   </VBox>
   ```

2. **Mở file bằng Scene Builder**:
   * Nhấp chuột phải vào file `san-pham-view.fxml` trong IntelliJ $\rightarrow$ chọn **Open In SceneBuilder** (hoặc mở ứng dụng Scene Builder $\rightarrow$ `File` $\rightarrow$ `Open`).

3. **Kéo thả các Component**:
   * **Thanh Header trên cùng**: Kéo `HBox` thả vào `VBox`. Bên trong `HBox`, thêm một `Button` (Nút "← Dashboard") và một `Label` (Tiêu đề "Quản Lý Sản Phẩm & Phụ Tùng").
   * **Bảng danh sách TableView**: Kéo `TableView` thả vào giữa `VBox`.
     * Trong `TableView`, kéo thả các `TableColumn` đại diện cho các cột:
       * Mã sản phẩm
       * Tên sản phẩm
       * Đơn vị tính
       * Số lượng tồn
       * Đơn giá bán

4. **Đặt `fx:id` và sự kiện trong Scene Builder**:
   * Chọn `TableView` $\rightarrow$ tại bảng thuộc tính bên phải, mở mục **Code**:
     * Đặt **`fx:id`** là: `sanPhamTable`.
   * Chọn từng cột `TableColumn` $\rightarrow$ đặt `fx:id` tương ứng:
     * Cột Mã SP: `colMaSP`
     * Cột Tên SP: `colTenSP`
     * Cột ĐVT: `colDonViTinh`
     * Cột Tồn kho: `colSoLuongTon`
     * Cột Đơn giá: `colDonGiaBan`
   * Chọn nút "← Dashboard" $\rightarrow$ tại mục **Code** $\rightarrow$ **On Action** gõ: `#onBackToDashboardClick`.

5. Nhấn **`Ctrl + S`** để lưu file trong Scene Builder.

---

### Bước 4: Viết Controller điều khiển giao diện (`SanPhamController.java`)
Controller ánh xạ các phần tử FXML qua `@FXML`, cấu hình các cột TableView và tải danh sách sản phẩm lên màn hình.

Tạo file: `src/main/java/com/pengugarage/controller/SanPhamController.java`
```java
package com.pengugarage.controller;

import com.pengugarage.model.entity.SanPham;
import com.pengugarage.service.SanPhamService;
import com.pengugarage.util.DieuHuongUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.DecimalFormat;
import java.util.List;

public class SanPhamController {

    @FXML
    private TableView<SanPham> sanPhamTable;

    @FXML
    private TableColumn<SanPham, String> colMaSP;

    @FXML
    private TableColumn<SanPham, String> colTenSP;

    @FXML
    private TableColumn<SanPham, String> colDonViTinh;

    @FXML
    private TableColumn<SanPham, String> colSoLuongTon;

    @FXML
    private TableColumn<SanPham, String> colDonGiaBan;

    private final SanPhamService sanPhamService = new SanPhamService();

    @FXML
    public void initialize() {
        cauHinhCacCotTable();
        taiDanhSachSanPham();
    }

    private void cauHinhCacCotTable() {
        colMaSP.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMaSanPham()));
        colTenSP.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenSanPham()));
        colDonViTinh.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDonViTinh()));
        colSoLuongTon.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getSoLuongTon())));
        
        DecimalFormat df = new DecimalFormat("#,### VNĐ");
        colDonGiaBan.setCellValueFactory(cell -> new SimpleStringProperty(df.format(cell.getValue().getDonGiaBan())));
    }

    private void taiDanhSachSanPham() {
        List<SanPham> list = sanPhamService.layDanhSachSanPham();
        sanPhamTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void onBackToDashboardClick() {
        DieuHuongUtil.chuyenManHinh(sanPhamTable,
                "/com/pengugarage/view/quanly/dashboard-view.fxml",
                "Pengu Garage Service ERP - Dashboard Quản Lý");
    }
}
```

---

### Bước 5: Nối sự kiện từ Dashboard sang màn hình Sản phẩm
1. Mở file [DashboardController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/DashboardController.java), thêm hàm mở màn hình sản phẩm:
   ```java
   @FXML
   private void onQuanLySanPhamClick() {
       DieuHuongUtil.chuyenManHinh(userNameLabel,
               "/com/pengugarage/view/quanly/san-pham-view.fxml",
               "Pengu Garage Service ERP - Quản Lý Sản Phẩm & Phụ Tùng");
   }
   ```
2. Trong [dashboard-view.fxml](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/view/quanly/dashboard-view.fxml), tìm đến Button `Quản lý sản phẩm` và gắn thêm thuộc tính `onAction`:
   ```xml
   <Button text="Quản lý sản phẩm" onAction="#onQuanLySanPhamClick" ... />
   ```

---

## 💡 Quy Tắc Quan Trọng Khi Phát Triển

1. **Luôn dùng `DieuHuongUtil.chuyenManHinh`**:
   - Đảm bảo tất cả màn hình khi chuyển tiếp luôn giữ nguyên trạng thái **Toàn màn hình (Maximized)** và giữ ổn định kích thước cửa sổ.
2. **Kế thừa bảng màu trong `style.css`**:
   - Gắn `styleClass="dark-root"` cho thẻ gốc của FXML để giao diện đồng bộ chủ đề Dark sang trọng với toàn hệ thống (`#090d16` và card `#111827`).
3. **Phân quyền và phiên làm việc**:
   - Lấy thông tin tài khoản đăng nhập hiện tại bằng `PhienDangNhapUtil.getInstance().getNhanVienHienTai()`.
   - Kiểm tra vai trò bằng `PhienDangNhapUtil.getInstance().getChucVu()`.
