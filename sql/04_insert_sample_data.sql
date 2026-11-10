-- =====================================================================
-- HỆ THỐNG QUẢN LÝ GARA Ô TÔ / XE MÁY PENGU GARAGE SERVICE ERP
-- Script: 04_insert_sample_data.sql
-- Dữ liệu mẫu hoàn chỉnh chuẩn hóa theo schema thực thể OOP Pengu Garage
-- Nhân viên giữ 3 người chuẩn: Quản lý (NV000), Lễ tân (NV001), Kỹ thuật viên (NV002)
-- Mật khẩu đăng nhập mặc định: 123456 (băm SHA-256)
-- Đường dẫn ảnh sản phẩm lưu theo thư mục img/products/
-- =====================================================================

USE `pengugarage`;

SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu cũ trước khi nạp để tránh trùng khóa chính
DELETE FROM `chi_tiet_hoa_don`;
DELETE FROM `hoa_don`;
DELETE FROM `phieu_phan_cong`;
DELETE FROM `chi_tiet_dung_phu_tung`;
DELETE FROM `chi_tiet_bao_gia`;
DELETE FROM `phieu_bao_gia`;
DELETE FROM `phieu_tiep_nhan`;
DELETE FROM `phuong_tien`;
DELETE FROM `san_pham`;
DELETE FROM `danh_muc_san_pham`;
DELETE FROM `dich_vu`;
DELETE FROM `ma_khuyen_mai`;
DELETE FROM `tai_khoan`;
DELETE FROM `nhan_vien`;
DELETE FROM `khach_hang`;
DELETE FROM `hang_xe`;

-- 1. Danh mục Hãng Xe (Mã theo chuẩn HX(?!000)\d{3})
INSERT INTO `hang_xe` (`ma_hang_xe`, `ten_hang`, `quoc_gia`) VALUES
('HX001', 'Toyota', 'Nhật Bản'),
('HX002', 'Honda', 'Nhật Bản'),
('HX003', 'Hyundai', 'Hàn Quốc'),
('HX004', 'Ford', 'Mỹ'),
('HX005', 'Kia', 'Hàn Quốc'),
('HX006', 'Mazda', 'Nhật Bản'),
('HX007', 'Mitsubishi', 'Nhật Bản'),
('HX008', 'VinFast', 'Việt Nam');

-- 2. Danh mục Khách Hàng (Mã theo chuẩn KH\d{9})
INSERT INTO `khach_hang` (`ma_khach_hang`, `ho_ten`, `so_dien_thoai`, `dia_chi`, `gioi_tinh`) VALUES
('KH000000000', 'Khách vãng lai', '0000000000', 'Tại xưởng', 'Khác'),
('KH000000001', 'Nguyễn Văn An', '0912345678', '123 Nguyễn Văn Cừ, Quận 5, TP.HCM', 'Nam'),
('KH000000002', 'Trần Thị Mai', '0987654321', '456 Lê Văn Sỹ, Quận 3, TP.HCM', 'Nữ'),
('KH000000003', 'Lê Hoàng Long', '0909123456', '789 Quang Trung, Gò Vấp, TP.HCM', 'Nam'),
('KH000000004', 'Phạm Minh Tuấn', '0938111222', '12 Đại lộ Bình Dương, Thủ Dầu Một', 'Nam'),
('KH000000005', 'Đỗ Quỳnh Trang', '0977888999', '34 Đồng Khởi, Biên Hòa, Đồng Nai', 'Nữ');

-- 3. Danh mục Nhân Viên
INSERT INTO `nhan_vien` (`ma_nhan_vien`, `ho_ten`, `so_dien_thoai`, `dia_chi`, `chuc_vu`, `trang_thai`, `chuyen_mon`) VALUES
('NV000', 'Nguyễn Thành Nam', '0901000000', 'Quận 1, TP. Hồ Chí Minh', 'QUAN_LY', 1, NULL),
('NV001', 'Trần Thị Thu Hà', '0901000001', 'Quận Gò Vấp, TP. Hồ Chí Minh', 'LE_TAN', 1, NULL),
('NV002', 'Lê Hoàng Phúc', '0901000002', 'TP. Thủ Đức, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_BAO_DUONG'),
('NV003', 'Trần Quốc Bảo', '0901000003', 'Quận Bình Thạnh, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_SUA_HE_THONG_DIEN'),
('NV004', 'Lê Hoàng Nam', '0901000004', 'Quận 7, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_THAY_THE_LINH_KIEN'),
('NV005', 'Nguyễn Văn Toàn', '0901000005', 'Quận Tân Bình, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_CHAM_SOC_VE_SINH_RUA_XE'),
('NV006', 'Hoàng Văn Khang', '0901000006', 'Quận 12, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_BAO_DUONG'),
('NV007', 'Vũ Đình Trọng', '0901000007', 'Quận Tân Phú, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_SUA_HE_THONG_DIEN'),
('NV008', 'Đặng Quốc Hùng', '0901000008', 'Quận 8, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_THAY_THE_LINH_KIEN'),
('NV009', 'Bùi Minh Trí', '0901000009', 'Quận Phú Nhuận, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_CHAM_SOC_VE_SINH_RUA_XE'),
('NV010', 'Phạm Thu Trang', '0901000010', 'Quận 3, TP. Hồ Chí Minh', 'LE_TAN', 1, NULL);

-- 4. Danh mục Tài Khoản Nhân Viên (Tên đăng nhập là mã nhân viên, mật khẩu '123456' băm SHA-256)
INSERT INTO `tai_khoan` (`ten_dang_nhap`, `mat_khau_ma_hoa`, `trang_thai`, `ma_nhan_vien`) VALUES
('NV000', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV000'),
('NV001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV001'),
('NV002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV002'),
('NV003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV003'),
('NV004', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV004'),
('NV005', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV005'),
('NV006', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV006'),
('NV007', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV007'),
('NV008', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV008'),
('NV009', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV009'),
('NV010', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV010');

-- 5. Danh mục Phương Tiện (Mã PT-KHxxxxxxxxx-yyy, khóa ngoại ma_hang_xe, ma_khach_hang)
INSERT INTO `phuong_tien` (`ma_phuong_tien`, `bien_so_xe`, `mau_xe`, `mau_sac_xe`, `nam_san_xuat`, `nhien_lieu`, `ma_hang_xe`, `ma_khach_hang`) VALUES
('PT-KH000000001-001', '51F-123.45', 'Vios E CVT', 'Trắng', 2021, 'XANG', 'HX001', 'KH000000001'),
('PT-KH000000002-001', '51G-678.90', 'City RS', 'Đỏ', 2022, 'XANG', 'HX002', 'KH000000002'),
('PT-KH000000003-001', '60A-456.78', 'Accent 1.4 AT', 'Đen', 2020, 'XANG', 'HX003', 'KH000000003'),
('PT-KH000000004-001', '61K-890.12', 'VF8 Plus', 'Xanh', 2023, 'DIEN', 'HX008', 'KH000000004'),
('PT-KH000000005-001', '43B-345.67', 'Ranger XLS', 'Bạc', 2019, 'DAU', 'HX004', 'KH000000005');

-- 6. Danh Mục Sản Phẩm (Mã DMxxx từ DM001 đến DM007)
INSERT INTO `danh_muc_san_pham` (`ma_danh_muc`, `ten_danh_muc`, `mo_ta`, `trang_thai`) VALUES
('DM001', 'Phụ tùng động cơ', 'Bao gồm lọc dầu, lọc gió động cơ, bugi đánh lửa', 1),
('DM002', 'Phụ tùng phanh & gầm', 'Bao gồm má phanh, giảm xóc, rotuyn lái', 1),
('DM003', 'Dầu nhớt & dung dịch', 'Bao gồm dầu động cơ, nước làm mát, dầu phanh', 1),
('DM004', 'Điện & ắc quy', 'Bao gồm bình ắc quy, bóng đèn chiếu sáng, cầu chì & relay', 1),
('DM005', 'Lốp & phụ tùng bánh xe', 'Bao gồm lốp ô tô, van lốp, vật tư vá lốp', 1),
('DM006', 'Chăm sóc & vật tư gara', 'Bao gồm dung dịch vệ sinh, vật tư sửa chữa, gạt mưa', 1),
('DM007', 'Phụ tùng khác & Đồ chơi xe', 'Các phụ kiện và phụ tùng bổ trợ khác', 1);

-- 7. Danh mục Sản Phẩm (Đầy đủ 54 sản phẩm theo chuẩn SP-DMxxx-yyy, đường dẫn ảnh img/products/)
INSERT INTO `san_pham` (`ma_san_pham`, `ten_san_pham`, `don_vi_tinh`, `so_luong_ton`, `so_luong_ton_toi_thieu`, `gia_nhap`, `don_gia_ban`, `thue_vat`, `trang_thai`, `anh_san_pham`, `ma_danh_muc`) VALUES
('SP-DM001-001', 'Lọc dầu Toyota Vios', 'Cái', 20, 5, 80000.00, 110000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP001.jpg', 'DM001'),
('SP-DM001-002', 'Lọc dầu Honda City', 'Cái', 15, 5, 90000.00, 125000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP002.jpg', 'DM001'),
('SP-DM001-003', 'Lọc dầu Hyundai Accent', 'Cái', 18, 5, 85000.00, 120000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP003.jpg', 'DM001'),
('SP-DM001-004', 'Lọc gió Toyota Vios', 'Cái', 15, 4, 100000.00, 140000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP004.jpg', 'DM001'),
('SP-DM001-005', 'Lọc gió Honda City', 'Cái', 12, 4, 110000.00, 150000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP005.jpg', 'DM001'),
('SP-DM001-006', 'Lọc gió Hyundai Accent', 'Cái', 16, 4, 95000.00, 135000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP006.jpg', 'DM001'),
('SP-DM001-007', 'Bugi NGK tiêu chuẩn', 'Cái', 30, 8, 70000.00, 100000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP007.jpg', 'DM001'),
('SP-DM001-008', 'Bugi Denso tiêu chuẩn', 'Cái', 25, 8, 75000.00, 105000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP008.jpg', 'DM001'),
('SP-DM001-009', 'Bugi Iridium NGK', 'Cái', 10, 3, 180000.00, 240000.00, 10.00, 1, 'img/products/AnhSanPham_DM01_SP009.jpg', 'DM001'),
('SP-DM002-001', 'Má phanh trước Toyota Vios', 'Bộ', 8, 3, 450000.00, 600000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP001.jpg', 'DM002'),
('SP-DM002-002', 'Má phanh trước Honda City', 'Bộ', 6, 2, 500000.00, 680000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP002.jpg', 'DM002'),
('SP-DM002-003', 'Má phanh trước Hyundai Accent', 'Bộ', 7, 3, 420000.00, 570000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP003.jpg', 'DM002'),
('SP-DM002-004', 'Giảm xóc trước Toyota Vios', 'Cái', 4, 2, 850000.00, 1100000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP004.jpg', 'DM002'),
('SP-DM002-005', 'Giảm xóc sau Honda City', 'Cái', 4, 2, 750000.00, 980000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP005.jpg', 'DM002'),
('SP-DM002-006', 'Giảm xóc trước Hyundai Accent', 'Cái', 3, 2, 800000.00, 1050000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP006.jpg', 'DM002'),
('SP-DM002-007', 'Rotuyn lái Toyota Vios', 'Cái', 8, 3, 180000.00, 250000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP007.jpg', 'DM002'),
('SP-DM002-008', 'Rotuyn lái Honda City', 'Cái', 6, 2, 200000.00, 280000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP008.jpg', 'DM002'),
('SP-DM002-009', 'Rotuyn lái Hyundai Accent', 'Cái', 7, 3, 190000.00, 260000.00, 10.00, 1, 'img/products/AnhSanPham_DM02_SP009.jpg', 'DM002'),
('SP-DM003-001', 'Castrol Magnatec 5W-30 1L', 'Chai', 24, 6, 160000.00, 210000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP001.jpg', 'DM003'),
('SP-DM003-002', 'Mobil Super 3000 5W-30 1L', 'Chai', 20, 5, 180000.00, 235000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP002.jpg', 'DM003'),
('SP-DM003-003', 'Shell Helix HX8 5W-40 1L', 'Chai', 18, 5, 190000.00, 250000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP003.jpg', 'DM003'),
('SP-DM003-004', 'Nước làm mát xanh 1L', 'Chai', 20, 5, 70000.00, 100000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP004.jpg', 'DM003'),
('SP-DM003-005', 'Nước làm mát đỏ 1L', 'Chai', 18, 5, 80000.00, 115000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP005.jpg', 'DM003'),
('SP-DM003-006', 'Nước làm mát đậm đặc 1L', 'Chai', 10, 3, 120000.00, 165000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP006.jpg', 'DM003'),
('SP-DM003-007', 'Dầu phanh DOT3 500ml', 'Chai', 20, 5, 70000.00, 100000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP007.jpg', 'DM003'),
('SP-DM003-008', 'Dầu phanh DOT4 500ml', 'Chai', 18, 5, 90000.00, 125000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP008.jpg', 'DM003'),
('SP-DM003-009', 'Dầu phanh DOT4 1L', 'Chai', 10, 3, 160000.00, 220000.00, 10.00, 1, 'img/products/AnhSanPham_DM03_SP009.jpg', 'DM003'),
('SP-DM004-001', 'Ắc quy GS 12V 45Ah', 'Bình', 5, 2, 950000.00, 1200000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP001.jpg', 'DM004'),
('SP-DM004-002', 'Ắc quy GS 12V 60Ah', 'Bình', 4, 2, 1250000.00, 1550000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP002.jpg', 'DM004'),
('SP-DM004-003', 'Ắc quy Đồng Nai 12V 45Ah', 'Bình', 5, 2, 850000.00, 1080000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP003.jpg', 'DM004'),
('SP-DM004-004', 'Bóng halogen H4 12V', 'Cái', 15, 4, 70000.00, 100000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP004.jpg', 'DM004'),
('SP-DM004-005', 'Bóng halogen H7 12V', 'Cái', 12, 4, 75000.00, 110000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP005.jpg', 'DM004'),
('SP-DM004-006', 'Bóng LED H4', 'Cái', 6, 2, 350000.00, 480000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP006.jpg', 'DM004'),
('SP-DM004-007', 'Cầu chì 15A', 'Cái', 50, 10, 5000.00, 10000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP007.jpg', 'DM004'),
('SP-DM004-008', 'Cầu chì 20A', 'Cái', 50, 10, 5000.00, 10000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP008.jpg', 'DM004'),
('SP-DM004-009', 'Relay 12V', 'Cái', 10, 3, 50000.00, 80000.00, 10.00, 1, 'img/products/AnhSanPham_DM04_SP009.jpg', 'DM004'),
('SP-DM005-001', 'Michelin 205/55R16', 'Cái', 4, 2, 1650000.00, 1950000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP001.jpg', 'DM005'),
('SP-DM005-002', 'Bridgestone 205/55R16', 'Cái', 4, 2, 1450000.00, 1750000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP002.jpg', 'DM005'),
('SP-DM005-003', 'Michelin 215/60R17', 'Cái', 2, 1, 2200000.00, 2600000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP003.jpg', 'DM005'),
('SP-DM005-004', 'Van lốp cao su', 'Cái', 30, 8, 10000.00, 20000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP004.jpg', 'DM005'),
('SP-DM005-005', 'Van lốp kim loại', 'Cái', 15, 4, 25000.00, 45000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP005.jpg', 'DM005'),
('SP-DM005-006', 'Nắp van lốp', 'Cái', 20, 5, 10000.00, 20000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP006.jpg', 'DM005'),
('SP-DM005-007', 'Miếng vá lốp không săm', 'Miếng', 50, 10, 5000.00, 10000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP007.jpg', 'DM005'),
('SP-DM005-008', 'Keo vá lốp', 'Chai', 10, 3, 40000.00, 60000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP008.jpg', 'DM005'),
('SP-DM005-009', 'Bộ vá lốp không săm', 'Bộ', 5, 2, 80000.00, 120000.00, 10.00, 1, 'img/products/AnhSanPham_DM05_SP009.jpg', 'DM005'),
('SP-DM006-001', 'Dung dịch vệ sinh nội thất 500ml', 'Chai', 10, 3, 80000.00, 120000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP001.jpg', 'DM006'),
('SP-DM006-002', 'Dung dịch vệ sinh kính 500ml', 'Chai', 12, 3, 45000.00, 70000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP002.jpg', 'DM006'),
('SP-DM006-003', 'Dung dịch vệ sinh mâm xe 500ml', 'Chai', 8, 2, 90000.00, 130000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP003.jpg', 'DM006'),
('SP-DM006-004', 'Keo làm kín gioăng', 'Tuýp', 10, 3, 60000.00, 90000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP004.jpg', 'DM006'),
('SP-DM006-005', 'Băng keo điện', 'Cuộn', 20, 5, 10000.00, 20000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP005.jpg', 'DM006'),
('SP-DM006-006', 'Dây rút nhựa 200mm', 'Bịch', 15, 4, 15000.00, 25000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP006.jpg', 'DM006'),
('SP-DM006-007', 'Gạt mưa 24 inch', 'Cái', 10, 3, 100000.00, 145000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP007.jpg', 'DM006'),
('SP-DM006-008', 'Gạt mưa 18 inch', 'Cái', 10, 3, 85000.00, 125000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP008.jpg', 'DM006'),
('SP-DM006-009', 'Bộ gạt mưa trước', 'Bộ', 8, 2, 180000.00, 250000.00, 10.00, 1, 'img/products/AnhSanPham_DM06_SP009.jpg', 'DM006');

-- 8. Danh mục Dịch Vụ Kỹ Thuật (19 dịch vụ chuẩn DV\d{3}, có phân định co_phu_tung và chuyen_mon)
INSERT INTO `dich_vu` (`ma_dich_vu`, `ten_dich_vu`, `mo_ta`, `don_gia`, `thue_vat`, `co_phu_tung`, `chuyen_mon`, `trang_thai`) VALUES
('DV000', 'Kiểm tra & chẩn đoán tổng quát', 'Khảo sát tình trạng xe, quét lỗi động cơ/hộp số, chẩn đoán hư hỏng gầm máy', 0.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV001', 'Thay dầu động cơ', 'Nhóm: Bảo dưỡng. Vật tư: Dầu nhớt & dung dịch. Áp dụng: Dầu động cơ', 80000.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV002', 'Thay lọc dầu động cơ', 'Nhóm: Bảo dưỡng. Vật tư: Phụ tùng động cơ. Áp dụng: Lọc dầu', 50000.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV003', 'Thay lọc gió động cơ', 'Nhóm: Bảo dưỡng. Vật tư: Phụ tùng động cơ. Áp dụng: Lọc gió động cơ', 40000.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV004', 'Thay bugi', 'Nhóm: Bảo dưỡng. Vật tư: Phụ tùng động cơ. Áp dụng: Bugi', 60000.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV005', 'Bảo dưỡng định kỳ tổng quát', 'Nhóm: Bảo dưỡng. Vật tư: Phụ tùng động cơ; Dầu nhớt & dung dịch. Áp dụng: Lọc dầu; Lọc gió động cơ; Bugi; Dầu động cơ', 350000.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV006', 'Thay má phanh', 'Nhóm: Hệ thống phanh. Vật tư: Phụ tùng phanh & gầm. Áp dụng: Má phanh', 150000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV007', 'Thay dầu phanh', 'Nhóm: Hệ thống phanh. Vật tư: Dầu nhớt & dung dịch. Áp dụng: Dầu phanh', 100000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV008', 'Thay giảm xóc', 'Nhóm: Hệ thống gầm. Vật tư: Phụ tùng phanh & gầm. Áp dụng: Giảm xóc', 250000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV009', 'Thay rotuyn lái', 'Nhóm: Hệ thống lái. Vật tư: Phụ tùng phanh & gầm. Áp dụng: Rotuyn lái', 180000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV010', 'Bổ sung/thay nước làm mát', 'Nhóm: Hệ thống làm mát. Vật tư: Dầu nhớt & dung dịch. Áp dụng: Nước làm mát', 70000.00, 10.00, 1, 'CHUYEN_BAO_DUONG', 1),
('DV011', 'Thay ắc quy', 'Nhóm: Hệ thống điện. Vật tư: Điện & ắc quy. Áp dụng: Ắc quy', 50000.00, 10.00, 1, 'CHUYEN_SUA_HE_THONG_DIEN', 1),
('DV012', 'Thay bóng đèn', 'Nhóm: Hệ thống điện. Vật tư: Điện & ắc quy. Áp dụng: Bóng đèn', 40000.00, 10.00, 1, 'CHUYEN_SUA_HE_THONG_DIEN', 1),
('DV013', 'Kiểm tra/thay cầu chì, relay', 'Nhóm: Hệ thống điện. Vật tư: Điện & ắc quy. Áp dụng: Cầu chì & relay', 50000.00, 10.00, 1, 'CHUYEN_SUA_HE_THONG_DIEN', 1),
('DV014', 'Thay lốp ô tô', 'Nhóm: Lốp xe. Vật tư: Lốp & phụ tùng bánh xe. Áp dụng: Lốp ô tô', 80000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV015', 'Vá lốp không săm', 'Nhóm: Lốp xe. Thuần công vá lốp, không dùng phụ tùng thay mới', 40000.00, 10.00, 0, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV016', 'Thay van lốp', 'Nhóm: Lốp xe. Vật tư: Lốp & phụ tùng bánh xe. Áp dụng: Van lốp', 30000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1),
('DV017', 'Vệ sinh nội thất, kính, mâm xe', 'Nhóm: Chăm sóc xe. Thuần công chăm sóc vệ sinh xưởng, không dùng phụ tùng', 200000.00, 10.00, 0, 'CHUYEN_CHAM_SOC_VE_SINH_RUA_XE', 1),
('DV018', 'Thay gạt mưa', 'Nhóm: Hệ thống gạt mưa. Vật tư: Chăm sóc & vật tư gara. Áp dụng: Gạt mưa', 30000.00, 10.00, 1, 'CHUYEN_THAY_THE_LINH_KIEN', 1);

-- 9. Danh mục Mã Khuyến Mãi
INSERT INTO `ma_khuyen_mai` (`ma_khuyen_mai`, `loai_giam`, `gia_tri_giam`, `gia_tri_toi_thieu_ap_dung`, `ngay_bat_dau_ap_dung`, `ngay_het_han`, `so_luong_ton`, `trang_thai`) VALUES
('KM_CHAOMUNG', 'PHAN_TRAM', 10.00, 200000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 100, 1),
('KM_TRIAN', 'SO_TIEN', 50000.00, 300000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 50, 1),
('KM_HE2026', 'PHAN_TRAM', 15.00, 500000.00, '2026-05-01 00:00:00', '2026-08-31 23:59:59', 80, 1),
('KM_DAUNHOT', 'SO_TIEN', 30000.00, 200000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 200, 1);

-- 10. Phiếu Tiếp Nhận Mẫu (Chuẩn mã: TN-{mã phương tiện}-{ddMMyy}-{xx}, nhân viên tiếp nhận: NV001 - Lễ tân)
INSERT INTO `phieu_tiep_nhan` (`ma_tiep_nhan`, `thoi_gian_tiep_nhan`, `trang_thai`, `so_km_hien_tai`, `yeu_cau_khach_hang`, `tinh_trang_noi_that`, `tinh_trang_ngoai_that`, `so_chia_khoa`, `ghi_chu`, `ma_phuong_tien`, `ma_nhan_vien`) VALUES
('TN-PT-KH000000001-001-151026-01', '2026-10-15 08:30:00', 'THANH_TOAN', 25300.00, 'Bảo dưỡng 25.000 km, thay dầu máy và lọc dầu', 'Nội thất sạch sẽ, ghế da nguyên bản', 'Xước nhẹ cản trước bên phụ', 1, 'Khách hẹn lấy xe lúc 11h30', 'PT-KH000000001-001', 'NV001'),
('TN-PT-KH000000002-001-161026-01', '2026-10-16 09:15:00', 'DANG_SUA_CHUA', 18450.00, 'Phanh có tiếng kêu kẹt kẹt khi dừng, kiểm tra lọc gió', 'Bình thường', 'Không trầy xước', 1, 'Khách ngồi chờ tại phòng chờ gara', 'PT-KH000000002-001', 'NV001'),
('TN-PT-KH000000003-001-171026-01', '2026-10-17 14:00:00', 'CHO_XAC_NHAN_BAO_GIA', 42100.00, 'Thay ắc quy và kiểm tra hệ thống làm mát', 'Bình thường', 'Vết móp nhỏ cửa sau bên tài', 2, 'Cần gọi báo giá trước khi làm', 'PT-KH000000003-001', 'NV001');

-- 11. Phiếu Báo Giá Mẫu (Chuẩn mã: BG-{mã tiếp nhận}-{xxx}, nhân viên lập: NV001)
INSERT INTO `phieu_bao_gia` (`ma_bao_gia`, `thoi_gian_lap`, `trang_thai`, `ly_do_tu_choi`, `ghi_chu`, `ma_tiep_nhan`, `ma_nhan_vien`) VALUES
('BG-TN-PT-KH000000001-001-151026-01-001', '2026-10-15 08:45:00', 'DA_CHAP_THUAN', NULL, 'Khách đồng ý toàn bộ hạng mục', 'TN-PT-KH000000001-001-151026-01', 'NV001'),
('BG-TN-PT-KH000000002-001-161026-01-001', '2026-10-16 09:30:00', 'DA_CHAP_THUAN', NULL, 'Đã xác nhận trực tiếp với khách', 'TN-PT-KH000000002-001-161026-01', 'NV001'),
('BG-TN-PT-KH000000003-001-171026-01-001', '2026-10-17 14:15:00', 'CHO_XAC_NHAN', NULL, 'Báo giá thay ắc quy GS 60Ah và bổ sung nước làm mát', 'TN-PT-KH000000003-001-171026-01', 'NV001');

-- 12. Chi Tiết Báo Giá Mẫu (Các dịch vụ được báo giá)
INSERT INTO `chi_tiet_bao_gia` (`ma_bao_gia`, `ma_dich_vu`, `don_gia_dich_vu`, `duoc_chap_nhan`, `ghi_chu`) VALUES
('BG-TN-PT-KH000000001-001-151026-01-001', 'DV001', 80000.00, 1, 'Công thay dầu động cơ'),
('BG-TN-PT-KH000000001-001-151026-01-001', 'DV002', 50000.00, 1, 'Công thay lọc dầu động cơ'),
('BG-TN-PT-KH000000002-001-161026-01-001', 'DV006', 150000.00, 1, 'Công thay má phanh trước'),
('BG-TN-PT-KH000000002-001-161026-01-001', 'DV003', 40000.00, 1, 'Công thay lọc gió động cơ'),
('BG-TN-PT-KH000000003-001-171026-01-001', 'DV011', 50000.00, 1, 'Công thay ắc quy'),
('BG-TN-PT-KH000000003-001-171026-01-001', 'DV010', 70000.00, 1, 'Công bổ sung nước làm mát');

-- 13. Chi Tiết Dùng Phụ Tùng Thực Tế (Phụ tùng gắn với từng dịch vụ trong báo giá)
INSERT INTO `chi_tiet_dung_phu_tung` (`ma_bao_gia`, `ma_dich_vu`, `ma_san_pham`, `so_luong`, `don_gia`, `da_tru_kho`, `ghi_chu`) VALUES
('BG-TN-PT-KH000000001-001-151026-01-001', 'DV001', 'SP-DM003-001', 4, 210000.00, 1, '4 chai dầu Castrol Magnatec 5W-30 1L'),
('BG-TN-PT-KH000000001-001-151026-01-001', 'DV002', 'SP-DM001-001', 1, 110000.00, 1, '1 lọc dầu Toyota Vios'),
('BG-TN-PT-KH000000002-001-161026-01-001', 'DV006', 'SP-DM002-002', 1, 680000.00, 1, '1 bộ má phanh trước Honda City'),
('BG-TN-PT-KH000000002-001-161026-01-001', 'DV003', 'SP-DM001-005', 1, 150000.00, 1, '1 lọc gió Honda City'),
('BG-TN-PT-KH000000003-001-171026-01-001', 'DV011', 'SP-DM004-002', 1, 1550000.00, 1, '1 bình Ắc quy GS 12V 60Ah'),
('BG-TN-PT-KH000000003-001-171026-01-001', 'DV010', 'SP-DM003-004', 2, 100000.00, 1, '2 chai Nước làm mát xanh 1L');

-- 14. Phiếu Phân Công Kỹ Thuật Viên (Chuẩn mã: PC-{mã tiếp nhận}-{mã NV}-{xx}, Kỹ thuật viên: NV002 - Lê Hoàng Phúc)
INSERT INTO `phieu_phan_cong` (`ma_phan_cong`, `thoi_gian_phan_cong`, `thoi_gian_bat_dau`, `thoi_gian_hoan_thanh`, `trang_thai_sua`, `ghi_chu`, `ma_nhan_vien`, `ma_bao_gia`, `ma_dich_vu`) VALUES
('PC-TN-PT-KH000000001-001-151026-01-NV002-01', '2026-10-15 09:05:00', '2026-10-15 09:10:00', '2026-10-15 09:40:00', 'HOAN_THANH', 'Đã thay dầu động cơ xong', 'NV002', 'BG-TN-PT-KH000000001-001-151026-01-001', 'DV001'),
('PC-TN-PT-KH000000001-001-151026-01-NV002-02', '2026-10-15 09:05:00', '2026-10-15 09:40:00', '2026-10-15 10:00:00', 'HOAN_THANH', 'Đã thay lọc dầu xong', 'NV002', 'BG-TN-PT-KH000000001-001-151026-01-001', 'DV002'),
('PC-TN-PT-KH000000002-001-161026-01-NV002-01', '2026-10-16 09:45:00', '2026-10-16 10:00:00', NULL, 'DANG_SUA', 'Đang tháo cụm má phanh cũ', 'NV002', 'BG-TN-PT-KH000000002-001-161026-01-001', 'DV006');

-- 15. Hóa Đơn Mẫu (Chuẩn mã: HD-(BH|DV)-{mã KH}-{ddMMyy}-{HHmmss})
INSERT INTO `hoa_don` (`ma_hoa_don`, `loai_hoa_don`, `thoi_gian_lap`, `trang_thai_thanh_toan`, `phuong_thuc_thanh_toan`, `thoi_gian_thanh_toan`, `ghi_chu`, `ma_khach_hang`, `ma_nhan_vien`, `ma_khuyen_mai`, `ma_tiep_nhan`) VALUES
('HD-DV-KH000000001-151026-103000', 'DICH_VU', '2026-10-15 10:30:00', 'DA_THANH_TOAN', 'CHUYEN_KHOAN', '2026-10-15 10:35:00', 'Khách chuyển khoản Vietcombank', 'KH000000001', 'NV001', 'KM_DAUNHOT', 'TN-PT-KH000000001-001-151026-01'),
('HD-BH-KH000000000-161026-152000', 'BAN_HANG', '2026-10-16 15:20:00', 'DA_THANH_TOAN', 'TIEN_MAT', '2026-10-16 15:22:00', 'Mua lẻ mang về, thanh toán tiền mặt', 'KH000000000', 'NV001', NULL, NULL);

-- 16. Chi Tiết Hóa Đơn Mẫu
INSERT INTO `chi_tiet_hoa_don` (`ma_hoa_don`, `loai_hang_muc`, `ma_dich_vu`, `ma_san_pham`, `so_luong`, `don_gia`) VALUES
('HD-DV-KH000000001-151026-103000', 'DICH_VU', 'DV001', NULL, 1, 80000.00),
('HD-DV-KH000000001-151026-103000', 'DICH_VU', 'DV002', NULL, 1, 50000.00),
('HD-DV-KH000000001-151026-103000', 'SAN_PHAM', NULL, 'SP-DM001-001', 1, 110000.00),
('HD-DV-KH000000001-151026-103000', 'SAN_PHAM', NULL, 'SP-DM003-001', 4, 210000.00),
('HD-BH-KH000000000-161026-152000', 'SAN_PHAM', NULL, 'SP-DM003-005', 2, 115000.00),
('HD-BH-KH000000000-161026-152000', 'SAN_PHAM', NULL, 'SP-DM006-008', 1, 125000.00);

SET FOREIGN_KEY_CHECKS = 1;
