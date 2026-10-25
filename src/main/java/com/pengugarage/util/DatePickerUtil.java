package com.pengugarage.util;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Tiện ích hỗ trợ cấu hình và định dạng cho DatePicker trong ứng dụng Pengu Garage.
 * Chuẩn hóa định dạng hiển thị ngày theo định dạng Việt Nam: dd/MM/yyyy.
 */
public class DatePickerUtil {

    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * Cấu hình DatePicker với định dạng dd/MM/yyyy và xử lý chuyển đổi chuỗi - ngày.
     */
    public static void cauHinhDinhDang(DatePicker datePicker) {
        if (datePicker == null) return;

        datePicker.setPromptText(DATE_PATTERN);
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return DATE_FORMATTER.format(date);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        return LocalDate.parse(string.trim(), DATE_FORMATTER);
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
            }
        });
    }

    /**
     * Chuyển giá trị từ DatePicker thành LocalDateTime vào đầu ngày (00:00:00).
     */
    public static LocalDateTime layDauNgay(DatePicker datePicker) {
        if (datePicker != null && datePicker.getValue() != null) {
            return datePicker.getValue().atStartOfDay();
        }
        return null;
    }

    /**
     * Chuyển giá trị từ DatePicker thành LocalDateTime vào cuối ngày (23:59:59).
     */
    public static LocalDateTime layCuoiNgay(DatePicker datePicker) {
        if (datePicker != null && datePicker.getValue() != null) {
            return datePicker.getValue().atTime(LocalTime.MAX);
        }
        return null;
    }

    /**
     * Gán giá trị LocalDateTime vào DatePicker.
     */
    public static void ganGiaTri(DatePicker datePicker, LocalDateTime ldt) {
        if (datePicker != null) {
            datePicker.setValue(ldt != null ? ldt.toLocalDate() : null);
        }
    }
}
