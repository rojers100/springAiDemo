package org.example.springaidemo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static LocalDateTime formatDateTime(String dateTime) throws IllegalAccessException {
        if (dateTime == null) {
            return LocalDateTime.now();
        }
        if (dateTime.length() == 10) {
            dateTime = dateTime + " 00:00:00";
        }
        String format = determineFormat(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateTime, formatter);
    }

    private static String determineFormat(String dateTimeString) throws IllegalAccessException {
        if (dateTimeString.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return "yyyy-MM-dd HH:mm:ss";
        } else if (dateTimeString.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
            return "dd-MM-yyyy HH:mm:ss";
        } else if (dateTimeString.matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return "yyyy/MM/dd HH:mm:ss";
        }
        throw new IllegalAccessException("时间格式错误"); // 未知格式
    }

}
