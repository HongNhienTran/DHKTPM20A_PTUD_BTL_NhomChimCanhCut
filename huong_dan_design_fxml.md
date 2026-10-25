# Cẩm Nang Thiết Kế Giao Diện FXML Với Scene Builder
### Dự án: Pengu Garage Service ERP

Tài liệu này quy chuẩn toàn bộ kiến trúc layout, quy tắc kéo thả trong **JavaFX Scene Builder**, hệ thống CSS Tokens và các mẹo responsive để toàn bộ thành viên trong nhóm thiết kế giao diện đồng bộ, chuyên nghiệp và chuẩn công nghiệp.

---

## 📑 Mục Lục
1. [Tổng Quan & Cấu Trúc Thư Mục View](#1-tổng-quan--cấu-trúc-thư-mục-view)
2. [Khởi Tạo File FXML Chuẩn Mẫu (Template)](#2-khởi-tạo-file-fxml-chuẩn-mẫu-template)
3. [Quy Tắc Layout Responsive (Chống Vỡ Giao Diện)](#3-quy-tắc-layout-responsive-chống-vỡ-giao-diện)
4. [Các Bước Thao Tác Trong Scene Builder](#4-các-bước-thao-tác-trong-scene-builder)
5. [Bảng Tra Cứu CSS Tokens & Style Classes](#5-bảng-tra-cứu-css-tokens--style-classes)
6. [Mẫu Bố Cục Tiêu Chuẩn 1 Màn Hình Nghiệp Vụ](#6-mẫu-bố-cục-tiêu-chuẩn-1-màn-hình-nghiệp-vụ)
7. [Các Lỗi Thường Gặp & Cách Khắc Phục](#7-các-lỗi-thường-gặp--cách-khắc-phục)

---

## 1. Tổng Quan & Cấu Trúc Thư Mục View

Tất cả các file `.fxml` phải được đặt trong thư mục `src/main/resources/com/pengugarage/view/` và phân loại đúng theo quyền truy cập của người dùng:

```
src/main/resources/com/pengugarage/
├── css/
│   └── style.css                     # Bảng style chung của toàn hệ thống
├── images/                           # Logo, icon, ảnh minh họa
└── view/
    ├── chung/                        # Màn hình dùng chung (Splash, Đăng nhập)
    │   ├── splash-view.fxml
    │   └── dang-nhap-view.fxml
    ├── quanly/                       # Màn hình dành riêng cho Quản lý
    │   ├── component/
    │   │   └── sidebar-quanly.fxml   # Component Sidebar tái sử dụng của Quản lý
    │   ├── dashboard-view.fxml
    │   ├── nhan-vien-view.fxml
    │   └── san-pham-view.fxml        (ví dụ)
    ├── kythuatvien/                  # Màn hình cho Kỹ thuật viên
    │   ├── component/
    │   │   └── sidebar-kythuatvien.fxml # Component Sidebar tái sử dụng của KTV
    │   ├── dashboard-view.fxml
    │   └── sua-chua-view.fxml        (ví dụ)
    └── letan/                        # Màn hình cho Lễ tân & Thu ngân
        ├── component/
        │   └── sidebar-letan.fxml    # Component Sidebar tái sử dụng của Lễ tân
        ├── dashboard-view.fxml
        └── tiep-nhan-view.fxml       (ví dụ)
```

> ⚠️ **Lưu ý đường dẫn CSS**: Khi file FXML nằm trong thư mục con (ví dụ `quanly/`), đường dẫn CSS luôn là:  
> `stylesheets="@../../css/style.css"`

---

## 2. Khởi Tạo File FXML Chuẩn Mẫu (Template)

Dự án phân biệt 2 loại màn hình chính:

### 🌟 Dạng 1: Màn hình CÓ Sidebar (Dashboard, Quản lý Nhân sự, Sản phẩm, Hóa đơn...)
Khung ngoài cùng là **`HBox`**, bên trái nhúng **Component Sidebar** bằng thẻ `<fx:include>`, bên phải là `VBox` chứa nội dung chính co giãn:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.VBox?>

<HBox xmlns="http://javafx.com/javafx/21"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.pengugarage.controller.TenController"
      stylesheets="@../../css/style.css"
      styleClass="dark-root"
      prefHeight="760.0" prefWidth="1280.0">

    <!-- Nhúng Sidebar của Actor tương ứng -->
    <fx:include source="component/sidebar-quanly.fxml" />

    <!-- Khối nội dung nghiệp vụ bên phải (tự co giãn chiếm hết khoảng trống) -->
    <VBox HBox.hgrow="ALWAYS" spacing="18.0" style="-fx-background-color: #0b131f;">
        <padding>
            <Insets top="24.0" right="28.0" bottom="24.0" left="28.0"/>
        </padding>

        <!-- Kéo thả các thành phần giao diện của bạn vào đây -->

    </VBox>
</HBox>
```

### 📄 Dạng 2: Màn hình KHÔNG CÓ Sidebar (Đăng nhập, Splash, Cửa sổ Pop-up, In phiếu...)
Khung ngoài cùng là **`VBox`** trực tiếp, không nhúng Sidebar:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.layout.VBox?>

<VBox xmlns="http://javafx.com/javafx/21"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.pengugarage.controller.TenDialogController"
      stylesheets="@../../css/style.css"
      styleClass="dark-root"
      spacing="18.0"
      prefHeight="500.0" prefWidth="700.0">

    <padding>
        <Insets top="20.0" right="24.0" bottom="20.0" left="24.0"/>
    </padding>

    <!-- Nội dung dialog/màn hình độc lập -->

</VBox>

---

## 3. Quy Tắc Layout Responsive (Chống Vỡ Giao Diện)

Hệ thống Pengu Garage luôn chạy ở chế độ **Toàn màn hình (Maximized)** thông qua `DieuHuongUtil`. Để giao diện hiển thị đẹp từ màn hình Laptop 14" (1366x768) đến màn hình PC 2K/4K:

### ❌ KHÔNG ĐƯỢC:
- **Không dùng `AnchorPane` làm khung chính** rồi kéo thả điều khiển bằng tọa độ tĩnh `LayoutX`, `LayoutY`. Khi phóng to cửa sổ, các ô sẽ bị lệch hoặc dính vào góc trái.
- Không đặt kích thước cứng `prefWidth` / `prefHeight` cho `TableView` hoặc các vùng nội dung chính.

### ✅ BẮT BUỘC DÙNG:
1. **Container phân cấp linh hoạt**:
   - `VBox`: Xếp các khối từ trên xuống dưới (Header $\rightarrow$ Toolbar tìm kiếm $\rightarrow$ Khu vực nội dung $\rightarrow$ Footer).
   - `HBox`: Xếp các phần tử theo hàng ngang (Nút bấm, ô nhập, chia 2 cột Bảng và Form).
2. **Co giãn theo chiều ngang (`HBox.hgrow="ALWAYS"`)**:
   - Gắn cho ô tìm kiếm `TextField` để ô này tự kéo dài chiếm trọn khoảng trống của thanh công cụ.
   - Gắn cho khung chứa `TableView` để bảng chiếm phần lớn màn hình.
3. **Co giãn theo chiều dọc (`VBox.vgrow="ALWAYS"`)**:
   - Gắn trực tiếp cho thẻ `TableView` để bảng tự kéo dài xuống sát đáy màn hình.
4. **Khoảng đệm lò xo (`Region HBox.hgrow="ALWAYS"` / Spacer)**:
   - Đặt một thẻ `Region` rỗng giữa tiêu đề bên trái và các nút/nhãn bên phải. Nó sẽ đóng vai trò như lò xo đẩy 2 cụm dạt về 2 mép màn hình.
5. **Cột bảng tự co giãn (`CONSTRAINED_RESIZE_POLICY`)**:
   - Trong `TableView`, thiết lập `columnResizePolicy` là `CONSTRAINED_RESIZE_POLICY`. Các cột sẽ tự tính toán để lấp đầy 100% bề rộng bảng, không tạo ra khoảng trống màu xám thừa thãi.

---

## 4. Các Bước Thao Tác Trong Scene Builder

### Bước 1: Mở file FXML
- Trong IntelliJ: Chuột phải vào file `.fxml` $\rightarrow$ chọn **Open In SceneBuilder**.
- Hoặc mở Scene Builder độc lập $\rightarrow$ `File` $\rightarrow$ `Open...` $\rightarrow$ chọn file.

### Bước 2: Kiểm tra Controller Class
- Nhìn góc dưới bên trái màn hình Scene Builder, mở mục **Controller**:
  - Ô **Controller class**: Đảm bảo đã điền đúng `com.pengugarage.controller.TenController`.

### Bước 3: Đặt `fx:id` cho các Control cần lập trình
- Chọn phần tử cần bắt sự kiện hoặc gán dữ liệu (ví dụ: `TableView`, `TextField`, `Label`, `ComboBox`).
- Nhìn sang bảng bên phải $\rightarrow$ chọn tab **Code**:
  - Mục **`fx:id`**: Nhập tên biến theo chuẩn `camelCase` (ví dụ: `nhanVienTable`, `timKiemField`, `thongBaoLabel`).
  - *Biến này phải trùng khớp với biến `@FXML` trong file Controller Java*.

### Bước 4: Gắn sự kiện Click chuột (`onAction`)
- Chọn `Button` cần xử lý $\rightarrow$ mở tab **Code** bên phải.
- Tìm mục **On Action**: Nhập tên hàm có dấu `#` phía trước (ví dụ: `#onThemMoiClick`, `#onBackToDashboardClick`).

### Bước 5: Thêm Style Class
- Chọn phần tử $\rightarrow$ mở tab **Properties** bên phải.
- Tìm mục **Style Class**: Bấm dấu `+` và nhập tên class CSS có sẵn trong `style.css` (ví dụ: `dark-text-field`, `action-button`, `card-panel`).

### Bước 6: Xem trước giao diện thực tế (Preview)
- Nhấn tổ hợp phím **`Ctrl + P`** (hoặc menu `Preview` $\rightarrow$ `Show Preview in Window`).
- Thử kéo giãn cửa sổ preview để kiểm tra xem bảng và các nút có co giãn mượt mà không.
- Nhấn **`Ctrl + S`** để lưu.

---

## 5. Bảng Tra Cứu CSS Tokens & Style Classes

Tất cả class dưới đây đã được định nghĩa sẵn trong [`src/main/resources/com/pengugarage/css/style.css`](file:///d:/HK9/PTUD/PROJ/pengugarage/src/main/resources/com/pengugarage/css/style.css):

### 🎨 Bảng Màu Nhận Diện (Brand Colors)
| Màu sắc | Mã HEX | Mục đích sử dụng |
| :--- | :--- | :--- |
| **Dark Root** | `#0b131f` | Nền toàn bộ màn hình Dashboard / Form |
| **Card / Panel** | `#142235` | Khung chứa nội dung, form chi tiết, bảng |
| **Border Dark** | `#1e354d` | Viền khung card, viền table |
| **Primary Blue** | `#173B61` | Nút chức năng thông thường, màu thương hiệu chính |
| **Orange Accent** | `#F47B31` | Nút hành động nổi bật (CTA), Lưu, Đăng nhập, Highlight |
| **Text Trắng** | `#FFFFFF` | Tiêu đề chính, chữ trên nền tối |
| **Text Muted** | `#8c9eb2` | Mô tả phụ, nhãn phụ, placeholder |

### 🔲 Các Lớp Style (CSS Classes) Thường Dùng
| Style Class | Áp dụng cho | Hiệu ứng |
| :--- | :--- | :--- |
| `dark-root` | Thẻ gốc `VBox` | Nền tối `#0b131f`, font Segoe UI đồng bộ |
| `dark-card` / `card-panel` | `VBox`, `HBox` | Bo góc 10px-12px, nền `#142235`, viền nhẹ |
| `dark-text-field` | `TextField`, `PasswordField` | Ô nhập nền tối, chữ trắng, focus viền xanh sáng |
| `dark-field-label` | `Label` trước ô nhập | Chữ in hoa nhỏ gọn, màu xám xanh dịu mắt |
| `login-submit-button` / `action-button` | `Button` quan trọng | Nền cam `#f47b31`, hover sáng, con trỏ bàn tay |
| `primary-button` | `Button` chính | Nền xanh navy đậm `#173B61`, viền bo góc 8px |
| `secondary-button` | `Button` phụ, nút Hủy | Nền trong suốt hoặc viền sáng mỏng |
| `badge-success` | `Label` trạng thái | Nền xanh lá nhạt, chữ xanh lá đậm (Hoạt động, Đã thanh toán) |
| `badge-warning` | `Label` trạng thái | Nền vàng nhạt, chữ nâu vàng (Đang xử lý, Chờ duyệt) |
| `badge-danger` | `Label` trạng thái | Nền đỏ hồng nhạt, chữ đỏ đậm (Đã khóa, Hủy, Quá hạn) |

---

## 6. Mẫu Bố Cục Tiêu Chuẩn 1 Màn Hình Nghiệp Vụ

Dưới đây là cấu trúc layout chuẩn mẫu (Split View: Bảng dữ liệu 70% bên trái + Form chi tiết 30% bên phải) được áp dụng tại màn hình `nhan-vien-view.fxml`:

```
┌────────────────────────────────────────────────────────────────────────┐
│  [← Dashboard]   Tiêu Đề Màn Hình (Quản Lý Phụ Tùng)     [Thông báo]   │  <- Header Bar
├────────────────────────────────────────────────────────────────────────┤
│  🔍 [Ô tìm kiếm.......................] [Lọc loại ▼] [Tìm] [Làm Mới]   │  <- Toolbar (HBox)
├───────────────────────────────────────────┬────────────────────────────┤
│  DANH SÁCH SẢN PHẨM (TableView)           │  CHI TIẾT & THAO TÁC (Form)│
│  ┌──────────┬──────────────┬───────────┐  │  Mã sản phẩm: [SP001     ] │
│  │ Mã SP    │ Tên Phụ Tùng │ Đơn Giá   │  │  Tên sản phẩm: [Lọc dầu  ] │
│  ├──────────┼──────────────┼───────────┤  │  Đơn vị tính: [Cái       ] │
│  │ SP001    │ Nhớt Motul   │ 280,000   │  │  Đơn giá:    [280000     ] │
│  │ SP002    │ Lọc gió K&N  │ 450,000   │  │  Số lượng:   [45         ] │
│  │ ...      │ ...          │ ...       │  │                            │
│  │          │              │           │  │  [➕ Thêm Mới]  [💾 Lưu]    │
│  │          │              │           │  │  [🔒 Khóa]     [🔄 Hủy]    │
│  └──────────┴──────────────┴───────────┘  │                            │
└───────────────────────────────────────────┴────────────────────────────┘
```

### Mã FXML Cốt Lõi:
```xml
<!-- 1. Thanh Header -->
<HBox alignment="CENTER_LEFT" spacing="16.0">
    <Button text="← Dashboard" onAction="#onBackToDashboardClick" styleClass="secondary-button" />
    <VBox spacing="2.0">
        <Label text="Tiêu Đề Nghiệp Vụ" style="-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: 800;" />
        <Label text="Mô tả tóm tắt tính năng màn hình" style="-fx-text-fill: #8c9eb2; -fx-font-size: 12px;" />
    </VBox>
    <Region HBox.hgrow="ALWAYS" />
    <Label fx:id="thongBaoLabel" text="" />
</HBox>

<!-- 2. Thanh tìm kiếm -->
<HBox alignment="CENTER_LEFT" spacing="12.0" style="-fx-background-color: #142235; -fx-padding: 12px; -fx-background-radius: 8px;">
    <TextField fx:id="timKiemField" promptText="Nhập từ khóa tìm kiếm..." HBox.hgrow="ALWAYS" styleClass="dark-text-field" />
    <Button text="Tìm Kiếm" onAction="#onTimKiemClick" styleClass="action-button" />
    <Button text="Làm Mới" onAction="#onLamMoiClick" styleClass="secondary-button" />
</HBox>

<!-- 3. Khu vực nội dung chia 2 cột -->
<HBox spacing="18.0" VBox.vgrow="ALWAYS">
    <!-- Cột trái: TableView tự co giãn -->
    <VBox HBox.hgrow="ALWAYS" spacing="10.0" style="-fx-background-color: #142235; -fx-padding: 16px; -fx-background-radius: 10px;">
        <TableView fx:id="mainTable" VBox.vgrow="ALWAYS">
            <columnResizePolicy>
                <TableView fx:constant="CONSTRAINED_RESIZE_POLICY" />
            </columnResizePolicy>
        </TableView>
    </VBox>

    <!-- Cột phải: Form nhập liệu cố định độ rộng 380px -->
    <VBox prefWidth="380.0" spacing="14.0" style="-fx-background-color: #142235; -fx-padding: 18px; -fx-background-radius: 10px;">
        <!-- Các trường TextField và Nút Lưu -->
    </VBox>
</HBox>
```

---

## 7. Các Lỗi Thường Gặp & Cách Khắc Phục

| Lỗi | Nguyên Nhân | Cách Khắc Phục |
| :--- | :--- | :--- |
| **`LoadException` khi chạy ứng dụng** | Sai tên hoặc đường dẫn Controller trong file FXML | Kiểm tra lại thuộc tính `fx:controller="com.pengugarage.controller.TenDung"`. |
| **`NullPointerException` khi gọi biến FXML** | Tên biến trong Java không trùng với `fx:id` trong Scene Builder | Kiểm tra ký tự hoa/thường giữa Java `@FXML private TextField tenBien;` và `fx:id="tenBien"`. |
| **Sự kiện click nút không chạy** | Quên dấu `#` trước tên hàm `onAction` trong Scene Builder | Trong Scene Builder, sửa `onAction` thành `#tenHam` (phải có ký tự thăng `#`). |
| **Lỗi XML parsing: `The entity name must immediately follow the '&'`** | Dùng trực tiếp dấu `&` trong chuỗi XML | Thay ký tự `&` bằng `&amp;` (ví dụ: `Quản lý &amp; Báo cáo`). |
| **Bảng TableView không giãn khi phóng to cửa sổ** | Thiếu `VBox.vgrow="ALWAYS"` cho TableView hoặc `HBox.hgrow="ALWAYS"` cho VBox cha | Chọn TableView trong Scene Builder $\rightarrow$ tab **Layout** $\rightarrow$ đặt **Vgrow** thành `ALWAYS`. |
| **Cột TableView bị thừa khoảng xám ở cuối** | Chưa đặt chính sách co giãn cột | Chọn TableView $\rightarrow$ tab **Properties** $\rightarrow$ mục **Column Resize Policy** chọn `constrained-resize`. |
| **Font chữ hoặc màu sắc không ăn theo CSS** | Đường dẫn `@../../css/style.css` bị lệch tầng thư mục | Kiểm tra lại số lượng `../` tùy theo cấp sâu của thư mục chứa file FXML. |

---

*Tài liệu được ban hành để hỗ trợ chuẩn hóa thiết kế UI cho toàn bộ thành viên nhóm DHKTPM20A - Nhóm Chim Cánh Cụt.*
