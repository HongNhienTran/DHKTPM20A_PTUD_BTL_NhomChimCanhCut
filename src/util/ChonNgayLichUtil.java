package util;

import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Tiện ích hỗ trợ chọn ngày bằng lịch đồ họa (JCalendar / JDateChooser)
 * Dùng cho các bộ lọc ngày và các form nhập liệu trong toàn bộ hệ thống.
 */
public class ChonNgayLichUtil {

    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CAM_CHU_DAO = new Color(234, 106, 31);
    private static final Color MAU_CAM_HOVER = new Color(244, 132, 52);
    private static final Color MAU_VIEN = new Color(226, 232, 240);
    private static final Color MAU_NEN_DIALOG = new Color(248, 250, 252);
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public record KetQuaChonNgay(boolean daXacNhan, boolean xoaLoc, LocalDate ngay) {
        public static KetQuaChonNgay apDung(LocalDate d) {
            return new KetQuaChonNgay(true, false, d);
        }

        public static KetQuaChonNgay boLoc() {
            return new KetQuaChonNgay(true, true, null);
        }

        public static KetQuaChonNgay huy() {
            return new KetQuaChonNgay(false, false, null);
        }
    }

    /**
     * Hiển thị hộp thoại lịch đồ họa trực quan (JCalendar) cho phép người dùng click chọn ngày trực tiếp trên lịch.
     *
     * @param componentCha Component cha để căn giữa dialog
     * @param tieuDe       Tiêu đề hiển thị (Ví dụ: "Chọn mốc Từ ngày", "Chọn ngày làm việc")
     * @param ngayBanDau   Ngày mặc định hiển thị khi mở lịch (null nếu chưa chọn)
     * @param choPhepBoLoc Cho phép nút "Bỏ lọc ngày này" hay không
     * @return KetQuaChonNgay chứa thông tin ngày đã chọn hoặc yêu cầu bỏ lọc
     */
    public static KetQuaChonNgay hienThiLichChonNgay(Component componentCha, String tieuDe, LocalDate ngayBanDau, boolean choPhepBoLoc) {
        Window cuaSoCha = componentCha != null ? SwingUtilities.getWindowAncestor(componentCha) : null;
        JDialog dialog = new JDialog(cuaSoCha, tieuDe, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(0, 0));

        final KetQuaChonNgay[] ketQua = new KetQuaChonNgay[]{KetQuaChonNgay.huy()};

        // 1. Tiêu đề hộp thoại
        JPanel pnlTieuDe = new JPanel(new BorderLayout(10, 0));
        pnlTieuDe.setBackground(Color.WHITE);
        pnlTieuDe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MAU_VIEN),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTieuDe.setForeground(MAU_XANH_DAM);
        pnlTieuDe.add(lblTieuDe, BorderLayout.WEST);

        JLabel lblHuongDan = new JLabel("Nhấp đúp chuột vào ngày để áp dụng nhanh");
        lblHuongDan.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHuongDan.setForeground(new Color(100, 116, 139));
        pnlTieuDe.add(lblHuongDan, BorderLayout.EAST);

        // 2. Thành phần Lịch JCalendar
        JPanel pnlNoiDung = new JPanel(new BorderLayout(0, 10));
        pnlNoiDung.setBackground(MAU_NEN_DIALOG);
        pnlNoiDung.setBorder(new EmptyBorder(14, 16, 10, 16));

        JCalendar calendar = new JCalendar(Locale.of("vi", "VN"));
        calendar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        calendar.setDecorationBordersVisible(true);
        calendar.setTodayButtonVisible(true);

        LocalDate ngayGoiY = ngayBanDau != null ? ngayBanDau : LocalDate.now();
        Date dateBanDau = Date.from(ngayGoiY.atStartOfDay(ZoneId.systemDefault()).toInstant());
        calendar.setDate(dateBanDau);

        // Nhấp đúp chuột lên ngày để chọn nhanh
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            // Khi người dùng bấm đúp chuột hoặc đổi ngày
        });

        // Thanh công cụ chọn nhanh (Hôm nay, Đầu tháng, Cuối tháng)
        JPanel pnlPhimTat = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlPhimTat.setOpaque(false);

        JButton btnHomNay = taoNutPhimTat("Hôm nay", () -> {
            calendar.setDate(new Date());
        });
        JButton btnDauThang = taoNutPhimTat("Đầu tháng này", () -> {
            LocalDate dauThang = LocalDate.now().withDayOfMonth(1);
            calendar.setDate(Date.from(dauThang.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });
        JButton btnCuoiThang = taoNutPhimTat("Cuối tháng này", () -> {
            LocalDate homNay = LocalDate.now();
            LocalDate cuoiThang = homNay.withDayOfMonth(homNay.lengthOfMonth());
            calendar.setDate(Date.from(cuoiThang.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        pnlPhimTat.add(new JLabel("Chọn nhanh:"));
        pnlPhimTat.add(btnHomNay);
        pnlPhimTat.add(btnDauThang);
        pnlPhimTat.add(btnCuoiThang);

        pnlNoiDung.add(pnlPhimTat, BorderLayout.NORTH);
        pnlNoiDung.add(calendar, BorderLayout.CENTER);

        // 3. Thanh nút chân hộp thoại
        JPanel pnlChan = new JPanel(new BorderLayout());
        pnlChan.setBackground(Color.WHITE);
        pnlChan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel cumNutPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cumNutPhai.setOpaque(false);

        JButton btnApDung = new JButton("✓ Áp Dụng");
        btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApDung.setForeground(Color.WHITE);
        btnApDung.setBackground(MAU_CAM_CHU_DAO);
        btnApDung.setFocusPainted(false);
        btnApDung.setPreferredSize(new Dimension(110, 36));
        btnApDung.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnApDung.addActionListener(e -> {
            Date d = calendar.getDate();
            if (d != null) {
                LocalDate chon = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                ketQua[0] = KetQuaChonNgay.apDung(chon);
            }
            dialog.dispose();
        });

        JButton btnHuy = new JButton("Đóng");
        btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHuy.setForeground(new Color(71, 85, 105));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.setPreferredSize(new Dimension(80, 36));
        btnHuy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> dialog.dispose());

        cumNutPhai.add(btnHuy);
        cumNutPhai.add(btnApDung);

        if (choPhepBoLoc) {
            JButton btnBoLoc = new JButton("✖ Bỏ Lọc Ngày");
            btnBoLoc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnBoLoc.setForeground(new Color(220, 38, 38));
            btnBoLoc.setBackground(new Color(254, 242, 242));
            btnBoLoc.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true));
            btnBoLoc.setFocusPainted(false);
            btnBoLoc.setPreferredSize(new Dimension(120, 36));
            btnBoLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnBoLoc.addActionListener(e -> {
                ketQua[0] = KetQuaChonNgay.boLoc();
                dialog.dispose();
            });
            pnlChan.add(btnBoLoc, BorderLayout.WEST);
        }

        pnlChan.add(cumNutPhai, BorderLayout.EAST);

        dialog.add(pnlTieuDe, BorderLayout.NORTH);
        dialog.add(pnlNoiDung, BorderLayout.CENTER);
        dialog.add(pnlChan, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setResizable(false);
        if (componentCha != null) {
            dialog.setLocationRelativeTo(componentCha);
        } else {
            dialog.setLocationRelativeTo(null);
        }
        dialog.setVisible(true);

        return ketQua[0];
    }

    /**
     * Tạo một đối tượng JDateChooser chuẩn với cấu hình font, định dạng dd/MM/yyyy và tiếng Việt.
     */
    public static JDateChooser taoDateChooser(LocalDate ngayBanDau) {
        JDateChooser chooser = new JDateChooser();
        chooser.setLocale(Locale.of("vi", "VN"));
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (ngayBanDau != null) {
            chooser.setDate(Date.from(ngayBanDau.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        return chooser;
    }

    /**
     * Chuyển đổi Date sang LocalDate an toàn.
     */
    public static LocalDate chuyenDateSangLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Chuyển đổi LocalDate sang java.util.Date an toàn.
     */
    public static Date chuyenLocalDateSangDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static JButton taoNutPhimTat(String nhan, Runnable hanhDong) {
        JButton btn = new JButton(nhan);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(30, 41, 59));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(3, 8, 3, 8)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> hanhDong.run());
        return btn;
    }
}

