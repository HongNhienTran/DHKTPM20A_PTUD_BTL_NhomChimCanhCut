package util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.IntConsumer;

/**
 * Thanh điều khiển phân trang dùng chung cho các bảng dữ liệu trong hệ thống.
 */
public class ThanhPhanTrang extends JPanel {
    private static final Color MAU_XANH_DAM = new Color(15, 41, 66);
    private static final Color MAU_CHU_PHU = new Color(100, 116, 139);
    private static final Color MAU_VIEN = new Color(226, 232, 240);

    private final int kichThuocTrang;
    private final String donViDem;
    private final IntConsumer khiChuyenTrang;

    private int trangHienTai = 1;
    private int tongSoTrang = 1;

    private final JLabel lblThongTin;
    private final JLabel lblSoTrang;
    private final JButton btnDau;
    private final JButton btnTruoc;
    private final JButton btnSau;
    private final JButton btnCuoi;

    public ThanhPhanTrang(int kichThuocTrang, IntConsumer khiChuyenTrang) {
        this(kichThuocTrang, "bản ghi", khiChuyenTrang);
    }

    public ThanhPhanTrang(int kichThuocTrang, String donViDem, IntConsumer khiChuyenTrang) {
        this.kichThuocTrang = Math.max(1, kichThuocTrang);
        this.donViDem = (donViDem != null && !donViDem.isBlank()) ? donViDem : "bản ghi";
        this.khiChuyenTrang = khiChuyenTrang;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MAU_VIEN),
                new EmptyBorder(6, 16, 6, 16)
        ));

        lblThongTin = new JLabel("Hiển thị 0 / 0 " + this.donViDem);
        lblThongTin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongTin.setForeground(MAU_CHU_PHU);

        JPanel cumNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cumNut.setOpaque(false);

        btnDau = taoNut("« Đầu", 68);
        btnTruoc = taoNut("‹ Trước", 78);
        lblSoTrang = new JLabel("Trang 1 / 1", SwingConstants.CENTER);
        lblSoTrang.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSoTrang.setForeground(MAU_XANH_DAM);
        lblSoTrang.setPreferredSize(new Dimension(96, 28));
        btnSau = taoNut("Sau ›", 70);
        btnCuoi = taoNut("Cuối »", 72);

        btnDau.addActionListener(e -> chuyenTrang(1));
        btnTruoc.addActionListener(e -> chuyenTrang(trangHienTai - 1));
        btnSau.addActionListener(e -> chuyenTrang(trangHienTai + 1));
        btnCuoi.addActionListener(e -> chuyenTrang(tongSoTrang));

        cumNut.add(btnDau);
        cumNut.add(btnTruoc);
        cumNut.add(lblSoTrang);
        cumNut.add(btnSau);
        cumNut.add(btnCuoi);

        add(lblThongTin, BorderLayout.WEST);
        add(cumNut, BorderLayout.EAST);
        capNhat(0, 0);
    }

    private JButton taoNut(String nhan, int rong) {
        JButton btn = new JButton(nhan);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(MAU_XANH_DAM);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(rong, 28));
        btn.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void chuyenTrang(int trangMoi) {
        if (trangMoi < 1 || trangMoi > tongSoTrang || trangMoi == trangHienTai) {
            return;
        }
        trangHienTai = trangMoi;
        if (khiChuyenTrang != null) {
            khiChuyenTrang.accept(trangHienTai);
        }
    }

    public int getTrangHienTai() {
        return trangHienTai;
    }

    public int getKichThuocTrang() {
        return kichThuocTrang;
    }

    public void datTrang(int trang) {
        this.trangHienTai = Math.max(1, trang);
    }

    public void capNhat(int tongSoBanGhi, int soBanGhiTrenTrang) {
        int tong = Math.max(0, tongSoBanGhi);
        this.tongSoTrang = Math.max(1, (int) Math.ceil((double) tong / kichThuocTrang));
        if (trangHienTai > tongSoTrang) {
            trangHienTai = tongSoTrang;
        }
        if (trangHienTai < 1) {
            trangHienTai = 1;
        }

        if (tong == 0 || soBanGhiTrenTrang <= 0) {
            lblThongTin.setText("Hiển thị 0 / " + tong + " " + donViDem);
        } else {
            int batDau = (trangHienTai - 1) * kichThuocTrang + 1;
            int ketThuc = batDau + soBanGhiTrenTrang - 1;
            lblThongTin.setText(String.format("Hiển thị %d - %d / %d %s",
                    batDau, ketThuc, tong, donViDem));
        }

        lblSoTrang.setText("Trang " + trangHienTai + " / " + tongSoTrang);
        btnDau.setEnabled(trangHienTai > 1);
        btnTruoc.setEnabled(trangHienTai > 1);
        btnSau.setEnabled(trangHienTai < tongSoTrang);
        btnCuoi.setEnabled(trangHienTai < tongSoTrang);
    }

    /**
     * Cố định bảng hiển thị vừa khít số dòng trên 1 trang theo chiều cao khung nhìn thực tế,
     * tránh bị dư khoảng trắng trống bên dưới hoặc phát sinh thanh cuộn dọc không cần thiết.
     */
    public static void caiDatBangCoDinh(JScrollPane cuonBang, JTable bang, int soDongMoiTrang) {
        if (cuonBang == null || bang == null || soDongMoiTrang <= 0) {
            return;
        }
        bang.setFillsViewportHeight(true);
        cuonBang.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int chieuCaoViewport = cuonBang.getViewport().getHeight();
                if (chieuCaoViewport <= 0) {
                    return;
                }
                int chieuCaoDong = Math.max(36, Math.min(54, chieuCaoViewport / soDongMoiTrang));
                if (bang.getRowHeight() != chieuCaoDong) {
                    bang.setRowHeight(chieuCaoDong);
                }
            }
        });
    }
}

