package ui;

import bus.BaoCaoThongKeBUS;
import util.ChonNgayLichUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;

abstract class QuanLyManHinhBaoCaoChiTiet extends JPanel {
    protected static final Color MAU_NEN = new Color(248, 250, 252);
    protected static final Color MAU_XANH = new Color(15, 41, 66);
    protected final BaoCaoThongKeBUS baoCaoThongKeBUS = new BaoCaoThongKeBUS();
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private final JButton btnTuNgay = new JButton();
    private final JButton btnDenNgay = new JButton();
    private final DefaultTableModel moHinhBang;

    protected QuanLyManHinhBaoCaoChiTiet(String tieuDe, String... cot) {
        moHinhBang = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setLayout(new BorderLayout(0, 12));
        setBackground(MAU_NEN);
        setBorder(new EmptyBorder(18, 24, 20, 24));

        JPanel dauTrang = new JPanel(new BorderLayout());
        dauTrang.setOpaque(false);
        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTieuDe.setForeground(MAU_XANH);
        dauTrang.add(lblTieuDe, BorderLayout.WEST);
        dauTrang.add(taoBoLoc(), BorderLayout.EAST);

        JTable bang = new JTable(moHinhBang);
        bang.setRowHeight(28);
        bang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        add(dauTrang, BorderLayout.NORTH);
        add(new JScrollPane(bang), BorderLayout.CENTER);
        datNgayMacDinh();
    }

    private JPanel taoBoLoc() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("Từ"));
        panel.add(btnTuNgay);
        panel.add(new JLabel("đến"));
        panel.add(btnDenNgay);
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> lamMoiDuLieu());
        panel.add(btnLamMoi);
        btnTuNgay.addActionListener(e -> chonNgay(true));
        btnDenNgay.addActionListener(e -> chonNgay(false));
        return panel;
    }

    private void datNgayMacDinh() {
        LocalDate den = LocalDate.now();
        tuNgay = den.withDayOfMonth(1);
        denNgay = den;
        capNhatNutNgay();
    }

    private void chonNgay(boolean laTuNgay) {
        LocalDate hienTai = laTuNgay ? tuNgay : denNgay;
        ChonNgayLichUtil.KetQuaChonNgay ketQua = ChonNgayLichUtil.hienThiLichChonNgay(
                this, laTuNgay ? "Chọn ngày bắt đầu" : "Chọn ngày kết thúc", hienTai, false);
        if (!ketQua.daXacNhan() || ketQua.ngay() == null) {
            return;
        }
        if (laTuNgay) {
            tuNgay = ketQua.ngay();
            if (denNgay.isBefore(tuNgay)) {
                denNgay = tuNgay;
            }
        } else {
            denNgay = ketQua.ngay();
            if (tuNgay.isAfter(denNgay)) {
                tuNgay = denNgay;
            }
        }
        capNhatNutNgay();
        lamMoiDuLieu();
    }

    private void capNhatNutNgay() {
        btnTuNgay.setText(tuNgay.toString());
        btnDenNgay.setText(denNgay.toString());
    }

    protected LocalDate getTuNgay() { return tuNgay; }

    protected LocalDate getDenNgay() { return denNgay; }

    protected void lamMoiDuLieu() {
        moHinhBang.setRowCount(0);
        for (Object[] dong : layDuLieu(getTuNgay(), getDenNgay())) {
            moHinhBang.addRow(dong);
        }
    }

    protected abstract List<Object[]> layDuLieu(LocalDate tuNgay, LocalDate denNgay);
}
