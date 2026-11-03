# TÀI LIỆU HƯỚNG DẪN PHÁT TRIỂN & PHÂN CÔNG TASK - PENGU GARAGE SERVICE ERP

> **Dự án**: Hệ Thống Quản Lý Dịch Vụ Gara Ô Tô (Pengu Garage Service ERP)  
> **Công nghệ**: Java 21, JavaFX 21, MariaDB, Maven  
> **Kiến trúc**: MVC kết hợp Layered Architecture (Database $\rightarrow$ DAO $\rightarrow$ Service $\rightarrow$ Controller $\rightarrow$ FXML/Component)

---

## 📋 BẢNG TỔNG HỢP TIẾN ĐỘ & PHÂN CÔNG TASK CHI TIẾT

| Module | Task ID | Tên hàm DAO | Nội dung công việc | Người thực hiện | Trạng thái hiện tại |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **Sản phẩm & Danh mục** | `M02-T01` | `layTatCa()`, `layChiTietSanPhamTheoID()` | Hiển thị danh sách, hiển thị chi tiết sản phẩm, lọc sản phẩm trên giao diện | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |
| **Sản phẩm & Danh mục** | `M02-T02` | `layTatCa()` | Hiển thị danh sách danh mục (lấy từ DB thật) | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |
| **Sản phẩm & Danh mục** | `M02-T03` | `them()`, `themDanhSachSanPham()` | Nhập 1 thông tin sản phẩm và thêm, thêm bằng file import (Excel) | **Phong** | ⏳ Đã dựng khung (Chờ code) |
| **Sản phẩm & Danh mục** | `M02-T04` | `capNhat()` | Hiển thị thông tin sản phẩm và cho phép chỉnh sửa | **Tuấn** | ⏳ Đã dựng khung (Chờ code) |
| **Quản lý Dịch vụ** | `M03-T01` | `layTatCa()`, `layChiTietDichVuTheoID()` | Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |
| **Quản lý Dịch vụ** | `M03-T02` | `them()` | Nhập thông tin dịch vụ và thêm mới qua Modal | **Tân** | ⏳ Đã dựng khung (Chờ code) |
| **Quản lý Dịch vụ** | `M02-T03` | `capNhat()` | Hiển thị thông tin dịch vụ và cho phép chỉnh sửa | **Tân** | ⏳ Đã dựng khung (Chờ code) |
| **Mã Khuyến Mãi** | `M03-T01` | `layTatCa()`, `layChiTietMaKhuyenMaiTheoID()` | Hiển thị danh sách, hiển thị chi tiết, lọc trên giao diện | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |
| **Mã Khuyến Mãi** | `M03-T02` | `them()` | Nhập thông tin mã khuyến mãi và thêm mới qua Modal | **Tuấn** | ⏳ Đã dựng khung (Chờ code) |
| **Tiếp nhận xe & Hàng đợi** | `M08-T01` | `layTatCa()`, `sinhMaTiepNhanTuDong()`, `themPhieuTiepNhan()`, `capNhatPhieuTiepNhan()` | Lập & quản lý phiếu tiếp nhận xe (tự sinh mã TN, chọn KTV kiểm tra, ghi nhận hiện trạng xe, metric cards) | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |
| **Tiếp nhận xe & Hàng đợi** | `M08-T02` | `timTheoSoDienThoai()`, `them()` | Tra cứu & thêm nhanh khách hàng (danh xưng Anh/Chị), thêm mới phương tiện | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |
| **Tiếp nhận xe & Hàng đợi** | `M08-T03` | `layDanhSachPhanCong()`, `layPhieuBaoGia()` | Hàng đợi xe tại xưởng (Kanban Live 4 cột, lễ tân duyệt báo giá & phân công KTV) | **Nhiên** | **✅ ĐÃ HOÀN THÀNH** |

---

## 📌 HƯỚNG DẪN CHI TIẾT DÀNH CHO TỪNG THÀNH VIÊN

---

### 👨‍💻 1. DÀNH CHO PHONG (Task M02-T03: Thêm Sản Phẩm & Import Excel)

#### Nhiệm vụ:
1. Nhập thông tin sản phẩm thủ công và lưu vào database.
2. Nhập danh sách sản phẩm hàng loạt từ file Excel (`.xlsx`, `.xls`).

#### Các vị trí file đã được dựng khung sẵn cần vào code:

##### 1.1. Tầng DAO: [SanPhamDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/SanPhamDAO.java)
- **Hàm `public boolean them(SanPham sp)`**:
  - Viết câu lệnh `INSERT INTO san_pham (ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, don_gia_ban, gia_nhap, thue_vat, so_luong_ton, so_luong_ton_toi_thieu, vi_tri_kho, trang_thai, mo_ta, hinh_anh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`.
  - Trả về `true` nếu thêm thành công, `false` nếu thất bại.
- **Hàm `public boolean themDanhSachSanPham(List<SanPham> danhSach)`**:
  - Dùng `PreparedStatement` kết hợp `conn.setAutoCommit(false)` và `ps.addBatch()` để thêm nhanh theo batch.

##### 1.2. Tầng Service: [SanPhamService.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/service/SanPhamService.java)
- Triển khai logic kiểm tra tính hợp lệ (Mã SP không trùng, Giá bán $\ge$ Giá nhập, Tồn kho $\ge 0$) trước khi gọi `sanPhamDAO.them(sp)` hoặc `sanPhamDAO.themDanhSachSanPham(danhSach)`.

##### 1.3. Tầng Controller & Giao diện:
- **Thêm thủ công 1 sản phẩm**:
  - Mở [SanPhamThemController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/SanPhamThemController.java).
  - Tìm hàm `@FXML public void onLuuClick()`. Lấy dữ liệu từ các trường `tenSanPhamField`, `donViTinhCombo`, `donGiaBanField`, `soLuongTonField`, `hinhAnhView`... $\rightarrow$ tạo đối tượng `SanPham` $\rightarrow$ gọi `sanPhamService.them(sp)`.
- **Thêm sản phẩm bằng file Excel**:
  - Mở [SanPhamController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/SanPhamController.java).
  - Tìm hàm `@FXML public void onNhapFileClick()`. Hệ thống đã có sẵn `FileChooser` chọn file. Phong đọc nội dung file Excel (dùng thư viện Apache POI) $\rightarrow$ chuyển thành `List<SanPham>` $\rightarrow$ gọi `sanPhamService.themDanhSachSanPham(...)`.

---

### 👨‍💻 2. DÀNH CHO TUẤN (Task M02-T04: Cập Nhật Sản Phẩm & Task M03-T02: Thêm Mã Khuyến Mãi)

#### Nhiệm vụ 1: Cập nhật thông tin sản phẩm (Task M02-T04)
- **DAO**: [SanPhamDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/SanPhamDAO.java)
  - Hoàn thiện hàm `public boolean capNhat(SanPham sp)`.
  - Viết câu lệnh `UPDATE san_pham SET ten_san_pham=?, danh_muc_id=?, don_vi_tinh=?, don_gia_ban=?, gia_nhap=?, thue_vat=?, so_luong_ton=?, so_luong_ton_toi_thieu=?, vi_tri_kho=?, trang_thai=?, mo_ta=?, hinh_anh=? WHERE id=?`.
- **Service**: [SanPhamService.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/service/SanPhamService.java)
  - Hoàn thiện hàm `public boolean capNhat(SanPham sp)`.
- **Controller**: [ChiTietSanPhamController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/component/ChiTietSanPhamController.java)
  - Tìm hàm `@FXML public void onLuuThayDoiClick()`.
  - Lấy các giá trị chỉnh sửa từ form chi tiết: `tenSPField`, `giaBanField`, `danhMucCombo`, `dvtField`, `soLuongTonField`, `trangThaiCombo` $\rightarrow$ gán vào `sanPhamHienTai` $\rightarrow$ gọi `sanPhamService.capNhat(...)` $\rightarrow$ hiển thị thông báo thành công.

#### Nhiệm vụ 2: Thêm mới mã khuyến mãi (Task M03-T02)
- **DAO**: [MaKhuyenMaiDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/MaKhuyenMaiDAO.java)
  - Hoàn thiện hàm `public boolean them(MaKhuyenMai km)`.
  - Viết câu lệnh: `INSERT INTO ma_khuyen_mai (ma_khuyen_mai, loai_giam, gia_tri_giam, gia_tri_toi_thieu_ap_dung, ngay_bat_dau_ap_dung, ngay_het_han, so_luong_ton, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)`.
- **Service**: [MaKhuyenMaiService.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/service/MaKhuyenMaiService.java)
  - Hoàn thiện hàm `public boolean them(MaKhuyenMai km)`.
- **Giao diện Modal & Controller**:
  - Giao diện modal đã được tạo sẵn: [modal-them-ma-khuyen-mai.fxml](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/view/quanly/component/modal-them-ma-khuyen-mai.fxml).
  - Mở [ModalThemMaKhuyenMaiController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/component/ModalThemMaKhuyenMaiController.java).
  - Tìm hàm `@FXML public void onLuuClick()`. Lấy dữ liệu:
    - Mã KM: `maKMField.getText().trim()`
    - Loại giảm: `loaiGiamCombo.getValue()`
    - Giá trị giảm: `Double.parseDouble(giaTriGiamField.getText())`
    - Hóa đơn tối thiểu: `Double.parseDouble(giaTriToiThieuField.getText())`
    - Ngày bắt đầu (Lịch): `DatePickerUtil.layDauNgay(ngayBatDauPicker)`
    - Ngày hết hạn (Lịch): `DatePickerUtil.layCuoiNgay(ngayHetHanPicker)`
    - Số lượng tồn: `Integer.parseInt(soLuongTonField.getText())`
    - Trạng thái: `"Đang áp dụng".equals(trangThaiCombo.getValue())`
    $\rightarrow$ Gọi `maKhuyenMaiService.them(km)`. Nếu thành công, gọi `onDongClick()` để đóng modal.

---

### 👨‍💻 3. DÀNH CHO TÂN (Task M03-T02: Thêm Dịch Vụ & Task M02-T03: Cập Nhật Dịch Vụ)

#### Nhiệm vụ 1: Thêm mới dịch vụ gara qua Modal (Task M03-T02)
- **DAO**: [DichVuDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/DichVuDAO.java)
  - Hoàn thiện hàm `public boolean them(DichVu dv)`.
  - Viết câu lệnh: `INSERT INTO dich_vu (ma_dich_vu, ten_dich_vu, mo_ta, don_gia, thue_vat, trang_thai) VALUES (?, ?, ?, ?, ?, ?)`.
- **Service**: [DichVuService.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/service/DichVuService.java)
  - Hoàn thiện hàm `public boolean them(DichVu dv)`.
- **Giao diện Modal & Controller**:
  - Giao diện modal đã dựng sẵn: [modal-them-dich-vu.fxml](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/view/quanly/component/modal-them-dich-vu.fxml).
  - Mở [ModalThemDichVuController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/component/ModalThemDichVuController.java).
  - Tìm hàm `@FXML public void onLuuClick()`. Lấy dữ liệu:
    - `maDichVuField`, `tenDichVuField`, `donGiaField`, `thueVATField`, `moTaArea`, `trangThaiCombo`
    $\rightarrow$ Tạo đối tượng `DichVu` $\rightarrow$ gọi `dichVuService.them(dv)`. Nếu thành công, gọi `onDongClick()` để đóng modal.

#### Nhiệm vụ 2: Cập nhật thông tin dịch vụ (Task M02-T03)
- **DAO**: [DichVuDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/DichVuDAO.java)
  - Hoàn thiện hàm `public boolean capNhat(DichVu dv)`.
  - Viết câu lệnh: `UPDATE dich_vu SET ten_dich_vu=?, mo_ta=?, don_gia=?, thue_vat=?, trang_thai=? WHERE id=?`.
- **Service**: [DichVuService.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/service/DichVuService.java)
  - Hoàn thiện hàm `public boolean capNhat(DichVu dv)`.
- **Controller**: [ChiTietDichVuController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/component/ChiTietDichVuController.java)
  - Tìm hàm `@FXML public void onLuuThayDoiClick()`.
  - Lấy thông tin từ form chi tiết: `tenDVField`, `donGiaField`, `thueVATField`, `moTaArea`, `trangThaiCombo` $\rightarrow$ gọi `dichVuService.capNhat(...)`.

---

### 👩‍💻 4. DÀNH CHO NHIÊN (Module Tiếp Nhận Xe & Hàng Đợi Xưởng - M08)

#### Nhiệm vụ đã hoàn thiện:

##### 4.1. Lập & Quản Lý Phiếu Tiếp Nhận Xe (Task M08-T01)
- **Giao diện**: [tiep-nhan-xe-view.fxml](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/view/letan/tiep-nhan-xe-view.fxml)
- **Controller**: [TiepNhanXeController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/TiepNhanXeController.java)
- **DAO & Service**: [PhieuTiepNhanDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/PhieuTiepNhanDAO.java), [PhieuTiepNhanService.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/service/PhieuTiepNhanService.java)
- **Nghiệp vụ thực hiện**:
  - Tự động sinh mã tiếp nhận chuẩn quy tắc theo ngày: `TNyyyyMMdd-XXX` (VD: `TN20261103-001`).
  - Lễ tân chọn Kỹ thuật viên phụ trách kiểm tra xe ban đầu ngay khi tạo phiếu.
  - Ghi nhận đầy đủ hiện trạng xe: số KM (ODO), số lượng chìa khóa, yêu cầu dịch vụ của khách, tình trạng nội ngoại thất và ghi chú.
  - Cập nhật số liệu các thẻ thống kê Metric Cards trực tiếp theo thời gian thực.
  - Thiết kế nút hành động duy nhất tối giản màu cam thương hiệu: **"➕ Tạo Tiếp Nhận"**.

##### 4.2. Modal Tra Cứu & Thêm Nhanh Phương Tiện / Khách Hàng (Task M08-T02)
- **Giao diện**: [modal-them-phuong-tien.fxml](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/view/letan/component/modal-them-phuong-tien.fxml)
- **Controller**: [ModalThemPhuongTienController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/component/ModalThemPhuongTienController.java)
- **DAO**: [KhachHangDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/KhachHangDAO.java), [PhuongTienDAO.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/dao/PhuongTienDAO.java)
- **Nghiệp vụ thực hiện**:
  - Tra cứu khách hàng trước theo SĐT: nếu đã tồn tại thì tự động hiển thị thông tin và gán xe cho khách hàng đó; nếu khách mới thì hiển thị form nhập đồng thời cả khách và xe.
  - Tự động chèn danh xưng theo giới tính: **Anh** (Nam) hoặc **Chị** (Nữ) trước họ tên khách hàng.
  - Tự động viết hoa biển số xe và kiểm tra tính hợp lệ.

##### 4.3. Hàng Đợi Xe Tại Xưởng - Kanban Live 4 Giai Đoạn (Task M08-T03)
- **Giao diện**: [hang-doi-xe-view.fxml](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/view/letan/hang-doi-xe-view.fxml)
- **Controller**: [HangDoiXeController.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/controller/HangDoiXeController.java)
- **Nghiệp vụ thực hiện**:
  - Quản lý quy trình 4 giai đoạn chuẩn xưởng: **1. Chờ kiểm tra** ➔ **2. Kiểm tra & Báo giá** ➔ **3. Đang sửa chữa** (màu cam đồng bộ) ➔ **4. Chờ bàn giao**.
  - Hiển thị badge số lượng phương tiện trực tiếp trên từng trạng thái và liên kết click từ Dashboard nhảy sang hàng đợi xe.
  - Lễ tân xem chi tiết phiếu báo giá để đọc cho khách duyệt, chọn KTV đang có mặt tại xưởng để phân công rồi xác nhận chuyển sang tab "Đang sửa chữa".

## 🛠️ CÁC TIỆN ÍCH DỰ ÁN HỖ TRỢ SẴN CẦN LƯU Ý

1. **Mở Modal Popup**:
   ```java
   DieuHuongUtil.moModal(tableHoacButton, "/com/pengugarage/view/quanly/component/modal-ten.fxml", "Tiêu đề Popup");
   ```
2. **Chọn ngày bằng Lịch (DatePicker)**:
   - Sử dụng [DatePickerUtil.java](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/java/com/pengugarage/util/DatePickerUtil.java):
     ```java
     // Cấu hình hiển thị chuẩn dd/MM/yyyy:
     DatePickerUtil.cauHinhDinhDang(myDatePicker);

     // Lấy giá trị LocalDateTime:
     LocalDateTime start = DatePickerUtil.layDauNgay(myDatePicker);

     // Gán giá trị LocalDateTime lên DatePicker:
     DatePickerUtil.ganGiaTri(myDatePicker, ldt);
     ```
3. **Kết nối Database**:
   - Sử dụng `CoSoDuLieuUtil.getConnection()`. Luôn bọc trong khối `try-with-resources` để tự động đóng kết nối.

---

## ⚙️ CÁCH TEST VÀ CHẠY DỰ ÁN

1. **Biên dịch dự án**:
   ```powershell
   mvn clean compile
   ```
2. **Khởi chạy ứng dụng**:
   ```powershell
   mvn javafx:run
   ```
   *(Hoặc chạy trực tiếp file `com.pengugarage.App` trong IntelliJ IDEA)*
