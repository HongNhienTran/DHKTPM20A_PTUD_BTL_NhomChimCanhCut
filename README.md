# DHKTPM20A_PTUD_BTL_NhomChimCanhCut

# 🐧 DANH SÁCH NHÓM "CHIM CÁNH CỤT"

## 📌 Thông tin lớp học phần
* **Môn học:** Phát triển ứng dụng
* **Lớp:** DHKTPM20A
* **Lịch lý thuyết:** Tiết 1 – 3 | **Phòng:** X11.07
* **Lịch thực hành thống nhất:** Thứ tư – Tiết 7 – 9 | **Phòng:** H9.2

---

## 👥 Danh sách thành viên

| STT (DS Lớp) | MSSV | Họ và Tên | Vai trò | Nhóm TH đã đăng ký | Nhóm TH thống nhất |
| :---: | :---: | :--- | :--- | :--- | :--- |
| **44** | `22693161` | **Trần Hồng Nhiên** | 👑 Nhóm trưởng | Thứ tư - Tiết 7-9 (Nhóm 3) | Thứ tư - Tiết 7-9 (Phòng H9.2) |
| **45** | `24682891` | **Nguyễn Bá Gia Phong** | 🎤 Báo cáo viên | Thứ ba - Tiết 1-3 (Nhóm 2) | Thứ tư - Tiết 7-9 (Phòng H9.2) |
| **53** | `24685301` | **Trương Chánh Tân** | 📝 Thư ký | Thứ tư - Tiết 7-9 | Thứ tư - Tiết 7-9 (Phòng H9.2) |
| **66** | `24720851` | **Nguyễn Đỗ Anh Tuấn** | ⏱️ Quản lý thời gian | Thứ tư - Tiết 7-9 | Thứ tư - Tiết 7-9 (Phòng H9.2) |

---

## 📋 Phân công công việc & Vai trò
* **Trần Hồng Nhiên (Nhóm trưởng):** Quản lý tiến độ dự án, phân công nhiệm vụ, tổng hợp tài liệu và đại diện làm việc với giảng viên.
* **Nguyễn Bá Gia Phong (Báo cáo viên):** Chuẩn bị nội dung thuyết trình, slide báo cáo và đại diện nhóm trình bày dự án.
* **Trương Chánh Tân (Thư ký):** Ghi chép biên bản cuộc họp, lưu trữ tài liệu nhóm và tổng hợp tài nguyên dự án.
* **Nguyễn Đỗ Anh Tuấn (Quản lý thời gian):** Theo dõi deadline các milestone, nhắc nhở tiến độ các thành viên và sắp xếp lịch họp nhóm.

---

# Pengu Garage Service ERP

Hệ thống quản lý dịch vụ gara ô tô / xe máy xây dựng bằng **Java 21**, **JavaFX 21**, **FXML**, **CSS** và **JDBC**, sử dụng cơ sở dữ liệu **MariaDB**.

---

## 1. Cấu Trúc Dự Án

```
pengugarage/
├── pom.xml                                      # Quản lý thư viện và plugin build Maven
├── README.md                                    # Tài liệu thông tin nhóm và dự án
├── .gitignore                                   # Cấu hình loại trừ file nhạy cảm và build artifact
├── .env.example                                 # Cấu hình mẫu biến môi trường
├── database/                                    # Thư mục chứa các script SQL MariaDB
│   ├── 01_create_database.sql                   # Khởi tạo CSDL, charset utf8mb4, collation tiếng Việt
│   ├── 02_create_tables.sql                     # Tạo cấu trúc 16 bảng dữ liệu theo chuẩn InnoDB
│   ├── 03_create_constraints.sql                # Tạo khóa ngoại, ràng buộc CHECK và các chỉ mục
│   ├── 04_insert_sample_data.sql                # Dữ liệu mẫu hoàn chỉnh (54 sản phẩm, 18 dịch vụ, 7 NV...)
│   └── danh_sach_tai_khoan.md                   # Danh sách tài khoản đăng nhập & mật khẩu phục vụ test
├── uploads/                                     # Thư mục lưu trữ media
│   └── products/                                # Lưu ảnh sản phẩm người dùng tải lên
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── pengugarage/
        │           ├── App.java                 # Điểm khởi chạy chính ứng dụng JavaFX (Launcher)
        │           ├── controller/              # Bộ điều khiển giao diện JavaFX
        │           │   ├── DangNhapController.java
        │           │   ├── DashboardController.java
        │           │   ├── ManHinhChaoController.java
        │           │   ├── NhanVienController.java
        │           │   └── MainController.java
        │           ├── model/
        │           │   ├── entity/              # 16 Java Entity Model ánh xạ CSDL
        │           │   └── enums/               # 10 Enum định nghĩa theo chuẩn UML
        │           ├── dao/                     # Tầng Data Access Object (JDBC)
        │           ├── service/                 # Tầng xử lý nghiệp vụ (Business Service)
        │           └── util/
        │               ├── CoSoDuLieuUtil.java  # Quản lý kết nối MariaDB qua .env
        │               ├── DieuHuongUtil.java   # Tiện ích điều hướng duy trì full màn hình
        │               ├── MatKhauUtil.java     # Mã hóa mật khẩu bảo mật chuẩn SHA-256
        │               └── PhienDangNhapUtil.java # Quản lý phiên đăng nhập người dùng
        └── resources/
            └── com/
                └── pengugarage/
                    ├── config.properties.example # File mẫu cấu hình JDBC
                    ├── view/
                    │   ├── chung/               # Màn hình dùng chung (Splash, Đăng nhập)
                    │   │   ├── splash-view.fxml
                    │   │   ├── dang-nhap-view.fxml
                    │   │   └── main-view.fxml
                    │   ├── quanly/              # Màn hình cho Quản lý (Dashboard, Nhân viên)
                    │   │   ├── dashboard-view.fxml
                    │   │   └── nhan-vien-view.fxml
                    │   ├── kythuatvien/         # Màn hình cho Kỹ thuật viên (Sửa chữa, Phụ tùng)
                    │   └── letan/               # Màn hình cho Lễ tân (Tiếp nhận xe, Thu tiền)
                    ├── css/
                    │   └── style.css            # Bảng màu Dark Automotive và kiểu dáng giao diện
                    └── images/                  # Thư mục chứa logo thương hiệu và hình ảnh
```

---

## 2. Yêu Cầu Môi Trường

* **Java Development Kit (JDK)**: JDK 21 trở lên.
* **Apache Maven**: Phiên bản 3.8.x trở lên.
* **MariaDB Server**: Phiên bản 10.6 LTS trở lên (khuyên dùng MariaDB 10.11 LTS hoặc 12.x để hỗ trợ đầy đủ `CHECK CONSTRAINT` và `InnoDB`).

---

## 3. Hướng Dẫn Chạy Script SQL Theo Thứ Tự

Để đảm bảo không gặp lỗi phụ thuộc ràng buộc khóa ngoại (Foreign Key dependencies), hãy thực thi các file SQL trong thư mục `database/` theo đúng thứ tự sau:

### Cách 1: Chạy qua dòng lệnh (MariaDB CLI)

Mở PowerShell hoặc Command Prompt tại thư mục dự án và chạy:

```powershell
# Bước 1: Tạo Database và thiết lập charset utf8mb4
& "mariadb.exe" -u root -p < database/01_create_database.sql

# Bước 2: Tạo cấu trúc 16 bảng dữ liệu
& "mariadb.exe" -u root -p pengu_garage < database/02_create_tables.sql

# Bước 3: Tạo toàn bộ Khóa ngoại, CHECK constraint và Chỉ mục (Index)
& "mariadb.exe" -u root -p pengu_garage < database/03_create_constraints.sql

# Bước 4: Nạp dữ liệu mẫu kiểm thử
& "mariadb.exe" -u root -p pengu_garage < database/04_insert_sample_data.sql
```

### Cách 2: Chạy qua công cụ đồ họa (DBeaver / HeidiSQL / Navicat)
1. Mở công cụ quản lý CSDL kết nối tới MariaDB `localhost:3306`.
2. Mở file `database/01_create_database.sql` và nhấn **Execute**.
3. Chọn database `pengu_garage`.
4. Mở file `database/02_create_tables.sql` và nhấn **Execute**.
5. Mở file `database/03_create_constraints.sql` và nhấn **Execute**.
6. Mở file `database/04_insert_sample_data.sql` và nhấn **Execute**.

---

## 4. Cấu Hình Kết Nối Cơ Sở Dữ Liệu & Bảo Mật

Hệ thống hỗ trợ nạp cấu hình linh hoạt, tuyệt đối **không ghi cứng mật khẩu vào mã nguồn**:

### Cách 1: Sử dụng file `.env` tại thư mục gốc (Khuyến nghị)
Hệ thống sử dụng file `.env` tại thư mục gốc (và đã được cấu hình trong `.gitignore` để **không bao giờ bị đẩy lên Git**). Bạn mở file `.env` và điền cấu hình:

```env
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=
UPLOAD_PRODUCT_DIR=
```

> **Chính sách bảo mật Git:**
> - File `.env` chứa mật khẩu thật đã được thêm vào `.gitignore` $\rightarrow$ Tuyệt đối **không được xóa** khỏi `.gitignore`.
> - Khi chia sẻ mã nguồn hoặc đưa lên GitHub/GitLab, chỉ commit file mẫu [.env.example](file:///.env.example).
> - Thư mục `database/` chứa tài liệu mật khẩu tài khoản cũng được bảo vệ trong `.gitignore`.

---

## 5. Lệnh Biên Dịch & Chạy Ứng Dụng

### Khởi chạy từ IDE (IntelliJ IDEA):
Mở file `com.pengugarage.App.java` và nhấn nút **Run** (màu xanh).

### Khởi chạy bằng Maven:
```powershell
# 1. Biên dịch dự án
mvn clean compile

# 2. Khởi chạy ứng dụng JavaFX
mvn javafx:run
```

---

## 6. Danh Sách Nhiệm Vụ Đã Triển Khai

| Mã Task | Tên Nhiệm Vụ | Người Thực Hiện | Trạng Thái | Mô Tả |
| :---: | :--- | :--- | :---: | :--- |
| **M01-T01** | Khởi tạo project base | Trần Hồng Nhiên | **Hoàn thành** | Maven, JavaFX 21, FXML, CSS Dark Theme, JDBC |
| **M01-T02** | Kết nối database | Trần Hồng Nhiên | **Hoàn thành** | Đọc cấu hình từ `.env`, quản lý đóng/mở Connection an toàn |
| **M01-T03** | Đăng nhập hệ thống | Trần Hồng Nhiên | **Hoàn thành** | Xác thực tên đăng nhập, mật khẩu băm chuẩn SHA-256 |
| **M01-T04** | Đăng xuất hệ thống | Trần Hồng Nhiên | **Hoàn thành** | Xóa phiên làm việc trong `PhienDangNhapUtil`, trở về màn hình đăng nhập |
| **M01-T05** | Phân quyền người dùng | Trần Hồng Nhiên | **Hoàn thành** | Phân định chức năng Quản lý, Kỹ thuật viên, Lễ tân |
| **M01-T06** | Điều hướng giao diện | Trần Hồng Nhiên | **Hoàn thành** | Màn hình Splash khởi động, Dashboard quản trị tự động full màn hình |
| **M01-T07** | Quản lý nhân viên | Trần Hồng Nhiên | **Hoàn thành** | Thêm, sửa, tìm kiếm theo tên/SĐT, khóa/mở trạng thái nhân viên |
| **M01-T08** | Quản lý tài khoản | Trần Hồng Nhiên | **Hoàn thành** | Cấp tài khoản liên kết nhân viên, đổi mật khẩu bảo mật SHA-256 |
