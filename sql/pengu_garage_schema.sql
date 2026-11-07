CREATE DATABASE IF NOT EXISTS pengugarage
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE pengugarage;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS chi_tiet_hoa_don;
DROP TABLE IF EXISTS hoa_don;
DROP TABLE IF EXISTS phieu_phan_cong;
DROP TABLE IF EXISTS chi_tiet_dung_phu_tung;
DROP TABLE IF EXISTS chi_tiet_bao_gia;
DROP TABLE IF EXISTS phieu_bao_gia;
DROP TABLE IF EXISTS phieu_tiep_nhan;
DROP TABLE IF EXISTS phuong_tien;
DROP TABLE IF EXISTS san_pham;
DROP TABLE IF EXISTS danh_muc_san_pham;
DROP TABLE IF EXISTS dich_vu;
DROP TABLE IF EXISTS ma_khuyen_mai;
DROP TABLE IF EXISTS tai_khoan;
DROP TABLE IF EXISTS nhan_vien;
DROP TABLE IF EXISTS khach_hang;
DROP TABLE IF EXISTS hang_xe;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE hang_xe (
    ma_hang_xe VARCHAR(10) NOT NULL,
    ten_hang VARCHAR(100) NOT NULL,
    quoc_gia VARCHAR(100) NOT NULL,
    PRIMARY KEY (ma_hang_xe)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE khach_hang (
    ma_khach_hang VARCHAR(15) NOT NULL,
    ho_ten VARCHAR(150) NOT NULL,
    so_dien_thoai VARCHAR(20) NOT NULL,
    dia_chi VARCHAR(255) DEFAULT NULL,
    gioi_tinh VARCHAR(10) DEFAULT NULL,
    PRIMARY KEY (ma_khach_hang),
    UNIQUE KEY uq_khach_hang_sdt (so_dien_thoai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Hồ sơ mặc định dành cho Khách vãng lai (mã cố định KH000000000; khách đăng ký mới bắt đầu từ KH000000001)
INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, gioi_tinh)
VALUES ('KH000000000', 'Khách vãng lai', '0000000000', NULL, NULL);

CREATE TABLE phuong_tien (
    ma_phuong_tien VARCHAR(30) NOT NULL,
    bien_so_xe VARCHAR(20) NOT NULL,
    mau_xe VARCHAR(100) NOT NULL,
    mau_sac_xe VARCHAR(50) NOT NULL,
    nam_san_xuat INT NOT NULL,
    nhien_lieu VARCHAR(20) NOT NULL,
    ma_hang_xe VARCHAR(10) NOT NULL,
    ma_khach_hang VARCHAR(15) NOT NULL,
    PRIMARY KEY (ma_phuong_tien),
    UNIQUE KEY uq_phuong_tien_bien_so (bien_so_xe),
    CONSTRAINT fk_pt_hang_xe FOREIGN KEY (ma_hang_xe) REFERENCES hang_xe (ma_hang_xe) ON UPDATE CASCADE,
    CONSTRAINT fk_pt_khach_hang FOREIGN KEY (ma_khach_hang) REFERENCES khach_hang (ma_khach_hang) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE nhan_vien (
    ma_nhan_vien VARCHAR(10) NOT NULL,
    ho_ten VARCHAR(150) NOT NULL,
    so_dien_thoai VARCHAR(20) NOT NULL,
    dia_chi VARCHAR(255) DEFAULT NULL,
    chuc_vu VARCHAR(30) NOT NULL,
    trang_thai TINYINT(1) NOT NULL DEFAULT 1,
    chuyen_mon VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (ma_nhan_vien),
    UNIQUE KEY uq_nhan_vien_sdt (so_dien_thoai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tai_khoan (
    ten_dang_nhap VARCHAR(10) NOT NULL,
    mat_khau_ma_hoa VARCHAR(255) NOT NULL,
    trang_thai TINYINT(1) NOT NULL DEFAULT 1,
    ma_nhan_vien VARCHAR(10) NOT NULL,
    PRIMARY KEY (ten_dang_nhap),
    UNIQUE KEY uq_tai_khoan_nv (ma_nhan_vien),
    CONSTRAINT fk_tk_nhan_vien FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien (ma_nhan_vien) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Khởi tạo sẵn 3 nhân viên & 3 tài khoản mẫu tương ứng 3 vai trò (Mật khẩu gốc đăng nhập: 123456)
-- Chuỗi băm SHA-256 của '123456': 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
INSERT INTO nhan_vien (ma_nhan_vien, ho_ten, so_dien_thoai, dia_chi, chuc_vu, trang_thai, chuyen_mon) VALUES
    ('NV000', 'Nguyễn Thành Nam', '0901000000', 'Quận 1, TP. Hồ Chí Minh', 'QUAN_LY', 1, NULL),
    ('NV001', 'Trần Thị Thu Hà', '0901000001', 'Quận Gò Vấp, TP. Hồ Chí Minh', 'LE_TAN', 1, NULL),
    ('NV002', 'Lê Hoàng Phúc', '0901000002', 'TP. Thủ Đức, TP. Hồ Chí Minh', 'KY_THUAT_VIEN', 1, 'CHUYEN_BAO_DUONG');

INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, trang_thai, ma_nhan_vien) VALUES
    ('NV000', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV000'),
    ('NV001', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV001'),
    ('NV002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 1, 'NV002');

CREATE TABLE danh_muc_san_pham (
    ma_danh_muc VARCHAR(10) NOT NULL,
    ten_danh_muc VARCHAR(150) NOT NULL,
    mo_ta TEXT DEFAULT NULL,
    trang_thai TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (ma_danh_muc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO danh_muc_san_pham (ma_danh_muc, ten_danh_muc, mo_ta, trang_thai) VALUES
    ('DM001', 'Dầu nhớt & Phụ gia', 'Dầu động cơ, dầu phanh, dầu hộp số, nước làm mát, nước rửa kính', 1),
    ('DM002', 'Lọc & Hệ thống nạp', 'Lọc nhớt, lọc gió động cơ, lọc gió điều hòa, lọc nhiên liệu', 1),
    ('DM003', 'Hệ thống phanh & Gầm', 'Má phanh, đĩa phanh, giảm xóc, rô-tuyn, cao su gầm', 1),
    ('DM004', 'Hệ thống điện & Ắc quy', 'Bình ắc quy, bu-gi đánh lửa, bóng đèn, cảm biến, cầu chì', 1),
    ('DM005', 'Lốp & Mâm xe', 'Lốp xe các kích cỡ, van lốp, nắp chụp mâm', 1),
    ('DM006', 'Phụ tùng động cơ & Truyền động', 'Dây curoa, bơm nước, gioăng phớt, két nước, ly hợp', 1),
    ('DM007', 'Chăm sóc & Vệ sinh xe', 'Dung dịch rửa xe, xi đánh bóng, chổi gạt mưa, dung dịch khử mùi', 1);

CREATE TABLE san_pham (
    ma_san_pham VARCHAR(20) NOT NULL,
    ten_san_pham VARCHAR(200) NOT NULL,
    don_vi_tinh VARCHAR(50) NOT NULL,
    so_luong_ton INT NOT NULL DEFAULT 0,
    so_luong_ton_toi_thieu INT NOT NULL DEFAULT 0,
    gia_nhap DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    don_gia_ban DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    thue_vat DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    trang_thai TINYINT(1) NOT NULL DEFAULT 1,
    anh_san_pham VARCHAR(500) DEFAULT NULL,
    ma_danh_muc VARCHAR(10) NOT NULL,
    PRIMARY KEY (ma_san_pham),
    CONSTRAINT fk_sp_danh_muc FOREIGN KEY (ma_danh_muc) REFERENCES danh_muc_san_pham (ma_danh_muc) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dich_vu (
    ma_dich_vu VARCHAR(10) NOT NULL,
    ten_dich_vu VARCHAR(200) NOT NULL,
    mo_ta TEXT DEFAULT NULL,
    don_gia DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    thue_vat DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    trang_thai TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (ma_dich_vu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ma_khuyen_mai (
    ma_khuyen_mai VARCHAR(50) NOT NULL,
    loai_giam VARCHAR(20) NOT NULL,
    gia_tri_giam DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    gia_tri_toi_thieu_ap_dung DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    ngay_bat_dau_ap_dung DATETIME NOT NULL,
    ngay_het_han DATETIME NOT NULL,
    so_luong_ton INT DEFAULT NULL,
    trang_thai TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (ma_khuyen_mai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE phieu_tiep_nhan (
    ma_tiep_nhan VARCHAR(50) NOT NULL,
    thoi_gian_tiep_nhan DATETIME NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    so_km_hien_tai DOUBLE NOT NULL DEFAULT 0,
    yeu_cau_khach_hang TEXT NOT NULL,
    tinh_trang_noi_that TEXT DEFAULT NULL,
    tinh_trang_ngoai_that TEXT DEFAULT NULL,
    so_chia_khoa INT NOT NULL DEFAULT 1,
    ghi_chu TEXT DEFAULT NULL,
    ma_phuong_tien VARCHAR(30) NOT NULL,
    ma_nhan_vien VARCHAR(10) NOT NULL,
    PRIMARY KEY (ma_tiep_nhan),
    CONSTRAINT fk_ptn_phuong_tien FOREIGN KEY (ma_phuong_tien) REFERENCES phuong_tien (ma_phuong_tien) ON UPDATE CASCADE,
    CONSTRAINT fk_ptn_nhan_vien FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien (ma_nhan_vien) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE phieu_bao_gia (
    ma_bao_gia VARCHAR(60) NOT NULL,
    thoi_gian_lap DATETIME NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    ly_do_tu_choi TEXT DEFAULT NULL,
    ghi_chu TEXT DEFAULT NULL,
    ma_tiep_nhan VARCHAR(50) NOT NULL,
    ma_nhan_vien VARCHAR(10) NOT NULL,
    PRIMARY KEY (ma_bao_gia),
    CONSTRAINT fk_pbg_tiep_nhan FOREIGN KEY (ma_tiep_nhan) REFERENCES phieu_tiep_nhan (ma_tiep_nhan) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_pbg_nhan_vien FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien (ma_nhan_vien) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chi_tiet_bao_gia (
    ma_bao_gia VARCHAR(60) NOT NULL,
    ma_dich_vu VARCHAR(10) NOT NULL,
    don_gia_dich_vu DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    duoc_chap_nhan TINYINT(1) NOT NULL DEFAULT 0,
    ghi_chu TEXT DEFAULT NULL,
    PRIMARY KEY (ma_bao_gia, ma_dich_vu),
    CONSTRAINT fk_ctbg_bao_gia FOREIGN KEY (ma_bao_gia) REFERENCES phieu_bao_gia (ma_bao_gia) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ctbg_dich_vu FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu (ma_dich_vu) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chi_tiet_dung_phu_tung (
    ma_bao_gia VARCHAR(60) NOT NULL,
    ma_dich_vu VARCHAR(10) NOT NULL,
    ma_san_pham VARCHAR(20) NOT NULL,
    so_luong INT NOT NULL DEFAULT 1,
    don_gia DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    da_tru_kho TINYINT(1) NOT NULL DEFAULT 0,
    ghi_chu TEXT DEFAULT NULL,
    PRIMARY KEY (ma_bao_gia, ma_dich_vu, ma_san_pham),
    CONSTRAINT fk_ctdpt_ctbg FOREIGN KEY (ma_bao_gia, ma_dich_vu) REFERENCES chi_tiet_bao_gia (ma_bao_gia, ma_dich_vu) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ctdpt_san_pham FOREIGN KEY (ma_san_pham) REFERENCES san_pham (ma_san_pham) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE phieu_phan_cong (
    ma_phan_cong VARCHAR(70) NOT NULL,
    thoi_gian_phan_cong DATETIME NOT NULL,
    thoi_gian_bat_dau DATETIME DEFAULT NULL,
    thoi_gian_hoan_thanh DATETIME DEFAULT NULL,
    trang_thai_sua VARCHAR(30) NOT NULL,
    ghi_chu TEXT DEFAULT NULL,
    ma_nhan_vien VARCHAR(10) NOT NULL,
    ma_bao_gia VARCHAR(60) NOT NULL,
    ma_dich_vu VARCHAR(10) NOT NULL,
    PRIMARY KEY (ma_phan_cong),
    UNIQUE KEY uq_ppc_hang_muc (ma_bao_gia, ma_dich_vu),
    CONSTRAINT fk_ppc_nhan_vien FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien (ma_nhan_vien) ON UPDATE CASCADE,
    CONSTRAINT fk_ppc_ctbg FOREIGN KEY (ma_bao_gia, ma_dich_vu) REFERENCES chi_tiet_bao_gia (ma_bao_gia, ma_dich_vu) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE hoa_don (
    ma_hoa_don VARCHAR(50) NOT NULL,
    loai_hoa_don VARCHAR(20) NOT NULL,
    thoi_gian_lap DATETIME NOT NULL,
    trang_thai_thanh_toan VARCHAR(30) NOT NULL,
    phuong_thuc_thanh_toan VARCHAR(30) DEFAULT NULL,
    thoi_gian_thanh_toan DATETIME DEFAULT NULL,
    ghi_chu TEXT DEFAULT NULL,
    ma_khach_hang VARCHAR(15) NOT NULL,
    ma_nhan_vien VARCHAR(10) NOT NULL,
    ma_khuyen_mai VARCHAR(50) DEFAULT NULL,
    ma_tiep_nhan VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (ma_hoa_don),
    CONSTRAINT fk_hd_khach_hang FOREIGN KEY (ma_khach_hang) REFERENCES khach_hang (ma_khach_hang) ON UPDATE CASCADE,
    CONSTRAINT fk_hd_nhan_vien FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien (ma_nhan_vien) ON UPDATE CASCADE,
    CONSTRAINT fk_hd_khuyen_mai FOREIGN KEY (ma_khuyen_mai) REFERENCES ma_khuyen_mai (ma_khuyen_mai) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_hd_tiep_nhan FOREIGN KEY (ma_tiep_nhan) REFERENCES phieu_tiep_nhan (ma_tiep_nhan) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chi_tiet_hoa_don (
    ma_hoa_don VARCHAR(50) NOT NULL,
    loai_hang_muc VARCHAR(20) NOT NULL,
    ma_dich_vu VARCHAR(10) DEFAULT NULL,
    ma_san_pham VARCHAR(20) DEFAULT NULL,
    so_luong INT NOT NULL DEFAULT 1,
    don_gia DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    UNIQUE KEY uq_cthd_dv (ma_hoa_don, ma_dich_vu),
    UNIQUE KEY uq_cthd_sp (ma_hoa_don, ma_san_pham),
    CONSTRAINT fk_cthd_hoa_don FOREIGN KEY (ma_hoa_don) REFERENCES hoa_don (ma_hoa_don) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cthd_dich_vu FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu (ma_dich_vu) ON UPDATE CASCADE,
    CONSTRAINT fk_cthd_san_pham FOREIGN KEY (ma_san_pham) REFERENCES san_pham (ma_san_pham) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

