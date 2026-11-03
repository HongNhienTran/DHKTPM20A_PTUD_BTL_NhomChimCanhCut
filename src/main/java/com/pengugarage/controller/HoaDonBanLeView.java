package com.pengugarage.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình HÓA ĐƠN BÁN HÀNG (dạng phiếu giấy) + chức năng in.
 * Bố cục theo mẫu: Khách hàng / Điện thoại / Địa chỉ, bảng 12 dòng,
 * Tổng cộng, Thành tiền bằng chữ, Người bán hàng.
 */
public class HoaDonBanLeView {

    // ===== SỬA THÔNG TIN GARA Ở ĐÂY =====
    private static final String TEN_GARA = "PENGU GARAGE";
    private static final String DIA_CHI_GARA = "Địa chỉ: Số 12 Nguyễn Văn Bảo, Phường Hạnh Thông, TP.HCM";
    private static final String SDT_GARA = "Hotline: 0862.632.799";
    private static final String DUONG_DAN_LOGO = "/com/pengugarage/images/logo_pengu-garage.png";

    private static final double CHIEU_RONG = 560;
    private static final int SO_DONG_TOI_THIEU = 12;
    private static final double[] RONG_COT = {36, 160, 66, 66, 90, 106}; // tổng vẫn 524
    private static final String FONT = "-fx-font-family: 'Times New Roman'; ";
    private static final String VIEN_NET_DUT =
            "-fx-border-style: dashed; -fx-border-color: #444; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 2 0;";

    // =========================================================================
    // DỮ LIỆU TRUYỀN VÀO
    // =========================================================================
    public static class Dong {
        public final String ten;
        public final String donViTinh;
        public final int soLuong;
        public final double donGia;
        public final double thanhTien;

        public Dong(String ten, String donViTinh, int soLuong, double donGia, double thanhTien) {
            this.ten = ten;
            this.donViTinh = donViTinh;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thanhTien = thanhTien;
        }
    }

    public static class DuLieu {
        public String maHoaDon = "";
        public LocalDateTime thoiGian = LocalDateTime.now();
        public String tenKhach = "Khách lẻ";
        public String soDienThoai = "";
        public String diaChi = "";
        public final List<Dong> dong = new ArrayList<>();
        public double tongTienHang;
        public double tienKhuyenMai;
        public String maKhuyenMai = "";
        public double khachCanTra;
        public double tienKhachDua;
        public double tienThua;
        public boolean tienMat = true;
    }

    // =========================================================================
    // HIỂN THỊ
    // =========================================================================
    public static void hienThi(Window owner, DuLieu dl) {
        VBox hoaDon = taoHoaDon(dl);

        StackPane nen = new StackPane(hoaDon);
        nen.setPadding(new Insets(16));
        nen.setStyle("-fx-background-color: #E5E7EB;");

        ScrollPane scroll = new ScrollPane(nen);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #E5E7EB;");

        Stage stage = new Stage();
        if (owner != null) stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Hóa đơn bán hàng - " + dl.maHoaDon);

        Button btnIn = new Button("🖨  In hóa đơn");
        btnIn.setStyle("-fx-background-color: #F47B31; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22;");
        btnIn.setOnAction(e -> in(hoaDon, stage));

        Button btnDong = new Button("Đóng");
        btnDong.setStyle("-fx-padding: 8 22;");
        btnDong.setOnAction(e -> stage.close());

        HBox thanhNut = new HBox(10, btnIn, btnDong);
        thanhNut.setAlignment(Pos.CENTER_RIGHT);
        thanhNut.setPadding(new Insets(10, 16, 10, 16));

        BorderPane root = new BorderPane();
        root.setCenter(scroll);
        root.setBottom(thanhNut);

        stage.setScene(new Scene(root, 650, 780));
        stage.showAndWait();
    }

    // =========================================================================
    // IN
    // =========================================================================
    private static void in(Node hoaDon, Window owner) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            canhBao("Không tìm thấy máy in nào trên máy tính này.");
            return;
        }
        if (!job.showPrintDialog(owner)) {
            return; // người dùng bấm Hủy
        }

        PageLayout trang = job.getJobSettings().getPageLayout();
        double rong = hoaDon.getLayoutBounds().getWidth();
        double cao = hoaDon.getLayoutBounds().getHeight();
        double tyLe = Math.min(trang.getPrintableWidth() / rong, trang.getPrintableHeight() / cao);

        Scale scale = new Scale(tyLe, tyLe);
        hoaDon.getTransforms().add(scale);
        boolean ok = job.printPage(hoaDon);
        hoaDon.getTransforms().remove(scale);

        if (ok) {
            job.endJob();
        } else {
            canhBao("In thất bại. Vui lòng kiểm tra máy in rồi thử lại.");
        }
    }

    private static void canhBao(String noiDung) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("In hóa đơn");
        a.setHeaderText(null);
        a.setContentText(noiDung);
        a.showAndWait();
    }

    // =========================================================================
    // DỰNG GIAO DIỆN PHIẾU
    // =========================================================================
    private static VBox taoHoaDon(DuLieu dl) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(18));
        root.setPrefWidth(CHIEU_RONG);
        root.setMinWidth(CHIEU_RONG);
        root.setMaxWidth(CHIEU_RONG);
        root.setStyle("-fx-background-color: white; " + FONT + "-fx-font-size: 13px;");
        root.getChildren().addAll(
                taoDauTrang(),
                taoTieuDe(dl),
                taoThongTinKhach(dl),
                taoBang(dl),
                taoChanTrang(dl)
        );
        return root;
    }

    private static Node taoDauTrang() {
        HBox h = new HBox(14);
        h.setAlignment(Pos.CENTER);

        Image logo = taiLogo();
        if (logo != null) {
            ImageView iv = new ImageView(logo);
            iv.setFitWidth(110);
            iv.setFitHeight(60);
            iv.setPreserveRatio(true);
            h.getChildren().add(iv);
        }

        Label ten = new Label(TEN_GARA);
        ten.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        VBox c = new VBox(2, ten, new Label(DIA_CHI_GARA), new Label(SDT_GARA));
        c.setAlignment(Pos.CENTER);
        h.getChildren().add(c);
        return h;
    }

    private static Image taiLogo() {
        try (InputStream in = HoaDonBanLeView.class.getResourceAsStream(DUONG_DAN_LOGO)) {
            if (in != null) {
                Image img = new Image(in);
                return img.isError() ? null : img;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Node taoTieuDe(DuLieu dl) {
        Label tieuDe = new Label("HÓA ĐƠN BÁN HÀNG");
        tieuDe.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        String ngay = dl.thoiGian.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Label phu = new Label("Số: " + dl.maHoaDon + "     Ngày: " + ngay);
        phu.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

        VBox v = new VBox(2, tieuDe, phu);
        v.setAlignment(Pos.CENTER);
        return v;
    }

    private static Node taoThongTinKhach(DuLieu dl) {
        Label khach = dongNetDut("Khách hàng:", dl.tenKhach);
        HBox.setHgrow(khach, Priority.ALWAYS);

        Label sdt = dongNetDut("Điện thoại:", dl.soDienThoai);
        sdt.setMinWidth(190);
        sdt.setPrefWidth(190);

        HBox hang1 = new HBox(10, khach, sdt);
        Label diaChi = dongNetDut("Địa chỉ:", dl.diaChi);
        return new VBox(6, hang1, diaChi);
    }

    private static Label dongNetDut(String nhan, String giaTri) {
        Label l = new Label(nhan + " " + (giaTri == null ? "" : giaTri));
        l.setMaxWidth(Double.MAX_VALUE);
        l.setStyle(VIEN_NET_DUT);
        return l;
    }

    private static Node taoBang(DuLieu dl) {
        GridPane g = new GridPane();
        g.setStyle("-fx-border-color: #222; -fx-border-width: 1 0 0 1;");
        for (double w : RONG_COT) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(w);
            cc.setPrefWidth(w);
            cc.setMaxWidth(w);
            g.getColumnConstraints().add(cc);
        }

        // Tiêu đề cột
        String[] tieuDe = {"SỐ\nTT", "TÊN HÀNG VÀ QUY CÁCH", "ĐƠN VỊ\nTÍNH", "SỐ\nLƯỢNG", "GIÁ\nĐƠN VỊ", "THÀNH TIỀN"};
        for (int c = 0; c < tieuDe.length; c++) {
            o(g, c, 0, 1, tieuDe[c], Pos.CENTER, true);
        }

        // Các dòng hàng (tối thiểu 12 dòng như phiếu giấy)
        int soHang = Math.max(SO_DONG_TOI_THIEU, dl.dong.size());
        for (int i = 0; i < soHang; i++) {
            int hang = i + 1;
            Dong d = i < dl.dong.size() ? dl.dong.get(i) : null;
            o(g, 0, hang, 1, String.valueOf(i + 1), Pos.CENTER, false);
            o(g, 1, hang, 1, d != null ? d.ten : "", Pos.CENTER_LEFT, false);
            o(g, 2, hang, 1, d != null ? d.donViTinh : "", Pos.CENTER, false);
            o(g, 3, hang, 1, d != null ? String.valueOf(d.soLuong) : "", Pos.CENTER, false);
            o(g, 4, hang, 1, d != null ? tien(d.donGia) : "", Pos.CENTER_RIGHT, false);
            o(g, 5, hang, 1, d != null ? tien(d.thanhTien) : "", Pos.CENTER_RIGHT, false);
        }

        // Các dòng tổng kết
        int hang = soHang + 1;
        tongKet(g, hang++, "TỔNG CỘNG:", tien(dl.tongTienHang), true);
        if (dl.tienKhuyenMai > 0) {
            String nhan = "Khuyến mãi" + (dl.maKhuyenMai == null || dl.maKhuyenMai.isEmpty()
                    ? "" : " (" + dl.maKhuyenMai + ")") + ":";
            tongKet(g, hang++, nhan, "- " + tien(dl.tienKhuyenMai), false);
        }
        tongKet(g, hang++, "KHÁCH CẦN TRẢ:", tien(dl.khachCanTra), true);
        if (dl.tienMat) {
            tongKet(g, hang++, "Tiền khách đưa:", tien(dl.tienKhachDua), false);
            tongKet(g, hang, "Tiền thừa trả khách:", tien(dl.tienThua), false);
        } else {
            tongKet(g, hang, "Hình thức thanh toán:", "Chuyển khoản", false);
        }
        return g;
    }

    private static void tongKet(GridPane g, int hang, String nhan, String giaTri, boolean dam) {
        o(g, 0, hang, 5, nhan, Pos.CENTER_RIGHT, dam);
        o(g, 5, hang, 1, giaTri, Pos.CENTER_RIGHT, dam);
    }

    /** Tạo 1 ô của bảng, có viền phải + viền dưới. */
    private static void o(GridPane g, int cot, int hang, int span, String text, Pos pos, boolean dam) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setMaxHeight(Double.MAX_VALUE);
        l.setMinHeight(26);
        l.setWrapText(true);
        l.setAlignment(pos);
        l.setTextAlignment(pos == Pos.CENTER ? TextAlignment.CENTER
                : pos == Pos.CENTER_RIGHT ? TextAlignment.RIGHT : TextAlignment.LEFT);
        l.setStyle("-fx-border-color: #222; -fx-border-width: 0 1 1 0; -fx-padding: 3 6 3 6;"
                + (dam ? " -fx-font-weight: bold;" : ""));
        g.add(l, cot, hang, span, 1);
    }

    private static Node taoChanTrang(DuLieu dl) {
        Label bangChu = new Label("Thành tiền (viết bằng chữ): " + docSoThanhChu(Math.round(dl.khachCanTra)));
        bangChu.setWrapText(true);
        bangChu.setMaxWidth(Double.MAX_VALUE);
        bangChu.setStyle(VIEN_NET_DUT);

        LocalDateTime t = dl.thoiGian;
        Label ngay = new Label(String.format("Ngày %02d tháng %02d năm %d",
                t.getDayOfMonth(), t.getMonthValue(), t.getYear()));
        ngay.setStyle("-fx-font-style: italic;");

        Label nguoiBan = new Label("Người bán hàng");
        nguoiBan.setStyle("-fx-font-weight: bold;");

        Label ky = new Label("(Ký, ghi rõ họ tên)");
        ky.setStyle("-fx-font-size: 11px; -fx-font-style: italic;");

        Region khoangKy = new Region();
        khoangKy.setMinHeight(48);

        VBox chuKy = new VBox(2, ngay, nguoiBan, ky, khoangKy);
        chuKy.setAlignment(Pos.CENTER);
        HBox hangChuKy = new HBox(chuKy);
        hangChuKy.setAlignment(Pos.CENTER_RIGHT);

        Label camOn = new Label("Cám ơn quý khách!");
        camOn.setStyle("-fx-font-weight: bold; -fx-font-style: italic;");
        HBox hangCamOn = new HBox(camOn);
        hangCamOn.setAlignment(Pos.CENTER);

        return new VBox(8, bangChu, hangChuKy, hangCamOn);
    }

    // =========================================================================
    // TIỆN ÍCH
    // =========================================================================
    private static String tien(double v) {
        return new DecimalFormat("#,##0").format(v);
    }

    private static final String[] CHU_SO =
            {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] BAC =
            {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};

    /** Đọc số tiền thành chữ tiếng Việt. Ví dụ 205000 -> "Hai trăm lẻ năm nghìn đồng". */
    static String docSoThanhChu(long so) {
        if (so <= 0) return "Không đồng";

        List<Integer> nhom = new ArrayList<>();
        long t = so;
        while (t > 0) {
            nhom.add((int) (t % 1000));
            t /= 1000;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = nhom.size() - 1; i >= 0; i--) {
            int n = nhom.get(i);
            if (n == 0) continue;
            boolean khongPhaiNhomDau = i < nhom.size() - 1;
            sb.append(docNhomBaSo(n, khongPhaiNhomDau)).append(' ')
                    .append(i < BAC.length ? BAC[i] : "").append(' ');
        }

        String kq = sb.toString().replaceAll("\\s+", " ").trim();
        return Character.toUpperCase(kq.charAt(0)) + kq.substring(1) + " đồng";
    }

    private static String docNhomBaSo(int n, boolean docHangTramNeuThieu) {
        int tram = n / 100;
        int chuc = (n % 100) / 10;
        int donVi = n % 10;
        StringBuilder sb = new StringBuilder();

        if (tram > 0 || docHangTramNeuThieu) {
            sb.append(CHU_SO[tram]).append(" trăm");
        }
        if (chuc > 1) {
            sb.append(' ').append(CHU_SO[chuc]).append(" mươi");
            if (donVi == 1) sb.append(" mốt");
            else if (donVi == 4) sb.append(" tư");
            else if (donVi == 5) sb.append(" lăm");
            else if (donVi > 0) sb.append(' ').append(CHU_SO[donVi]);
        } else if (chuc == 1) {
            sb.append(" mười");
            if (donVi == 5) sb.append(" lăm");
            else if (donVi > 0) sb.append(' ').append(CHU_SO[donVi]);
        } else if (donVi > 0) {
            if (tram > 0 || docHangTramNeuThieu) sb.append(" lẻ");
            sb.append(' ').append(CHU_SO[donVi]);
        }
        return sb.toString().trim();
    }
}
