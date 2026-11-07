package util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Tiện ích tải và co giãn icon hình ảnh từ thư mục img/.
 * Hỗ trợ bộ nhớ đệm (caching), đổi màu theo chủ đề (recolor/tinting),
 * và co giãn mượt mà chất lượng cao (bicubic/bilinear).
 */
public class IconUtil {

    private static final Map<String, BufferedImage> BO_NHO_DEM_ANH_GOC = new HashMap<>();
    private static final Map<String, ImageIcon> BO_NHO_DEM_ICON_DA_XU_LY = new HashMap<>();

    /**
     * Lấy ImageIcon từ file trong thư mục img/ với kích thước mong muốn.
     * Ví dụ: IconUtil.layIcon("search.png", 16, 16);
     */
    public static ImageIcon layIcon(String tenFile, int rong, int cao) {
        return layIcon(tenFile, rong, cao, null);
    }

    /**
     * Lấy ImageIcon và tô màu tùy chọn (tinting theo mã màu của nút bấm).
     * Ví dụ: IconUtil.layIcon("plus.png", 14, 14, Color.WHITE);
     */
    public static ImageIcon layIcon(String tenFile, int rong, int cao, Color mauTuyChinh) {
        if (tenFile == null || tenFile.trim().isEmpty()) {
            return null;
        }

        String tenChuan = tenFile.endsWith(".png") ? tenFile : tenFile + ".png";
        String khoaDem = tenChuan + "_" + rong + "x" + cao + "_" + (mauTuyChinh != null ? mauTuyChinh.getRGB() : "orig");

        ImageIcon daLuu = BO_NHO_DEM_ICON_DA_XU_LY.get(khoaDem);
        if (daLuu != null) {
            return daLuu;
        }

        BufferedImage anhGoc = docAnhTuImg(tenChuan);
        if (anhGoc == null) {
            return null;
        }

        BufferedImage anhDaXuLy = coGiaVaToMau(anhGoc, rong, cao, mauTuyChinh);
        ImageIcon icon = new ImageIcon(anhDaXuLy);
        BO_NHO_DEM_ICON_DA_XU_LY.put(khoaDem, icon);
        return icon;
    }

    /**
     * Đọc BufferedImage gốc từ thư mục img/ (có lưu cache ảnh gốc).
     */
    public static BufferedImage docAnhTuImg(String tenFile) {
        String tenChuan = tenFile.endsWith(".png") ? tenFile : tenFile + ".png";
        if (BO_NHO_DEM_ANH_GOC.containsKey(tenChuan)) {
            return BO_NHO_DEM_ANH_GOC.get(tenChuan);
        }

        File[] cacDuongDan = new File[]{
                new File("img", tenChuan),
                new File(System.getProperty("user.dir"), "img/" + tenChuan),
                new File("../img", tenChuan)
        };

        for (File f : cacDuongDan) {
            if (f.exists() && f.isFile()) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        BO_NHO_DEM_ANH_GOC.put(tenChuan, img);
                        return img;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        try {
            java.net.URL url = IconUtil.class.getResource("/img/" + tenChuan);
            if (url == null) {
                url = IconUtil.class.getResource("/" + tenChuan);
            }
            if (url != null) {
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    BO_NHO_DEM_ANH_GOC.put(tenChuan, img);
                    return img;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static BufferedImage coGiaVaToMau(BufferedImage goc, int rong, int cao, Color mau) {
        BufferedImage anhMoi = new BufferedImage(rong, cao, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = anhMoi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ ảnh thu nhỏ mượt mà
        g2.drawImage(goc, 0, 0, rong, cao, null);
        g2.dispose();

        // Nếu có yêu cầu đổi màu (tinting), giữ nguyên kênh Alpha và tô theo màu mới
        if (mau != null) {
            int rgbMoi = mau.getRGB() & 0x00FFFFFF;
            for (int y = 0; y < cao; y++) {
                for (int x = 0; x < rong; x++) {
                    int pixel = anhMoi.getRGB(x, y);
                    int alpha = (pixel >> 24) & 0xFF;
                    if (alpha > 0) {
                        anhMoi.setRGB(x, y, (alpha << 24) | rgbMoi);
                    }
                }
            }
        }
        return anhMoi;
    }

    /**
     * Tải và co giãn ảnh sản phẩm từ đường dẫn lưu trong CSDL (img/products/..., uploads/...).
     * Giữ nguyên tỷ lệ khung hình (aspect ratio) để hình ảnh không bị méo.
     * Nếu không tìm thấy file, tự động trả về icon phụ tùng mặc định.
     */
    public static ImageIcon layAnhSanPham(String duongDan, int rong, int cao) {
        if (duongDan != null && !duongDan.trim().isEmpty()) {
            File file = new File(duongDan.trim());
            if (!file.exists()) {
                String alt = duongDan.replace("uploads/", "img/").replace("uploads\\", "img\\");
                file = new File(alt);
            }
            if (!file.exists()) {
                file = new File("img/products/" + new File(duongDan).getName());
            }
            if (file.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                    int origW = icon.getIconWidth();
                    int origH = icon.getIconHeight();
                    if (origW > 0 && origH > 0) {
                        double tiLe = Math.min((double) rong / origW, (double) cao / origH);
                        int newW = Math.max(1, (int) Math.round(origW * tiLe));
                        int newH = Math.max(1, (int) Math.round(origH * tiLe));
                        Image img = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                        return new ImageIcon(img);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return layIcon("part.png", Math.min(rong, 64), Math.min(cao, 64));
    }
}

