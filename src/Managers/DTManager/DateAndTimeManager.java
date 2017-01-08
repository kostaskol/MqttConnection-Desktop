package Managers.DTManager;


import java.time.LocalDateTime;

/**
 * Manager class that manages simple date and time operations
 */
public class DateAndTimeManager {

    public static String getDate() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        String date;
        if (day < 10) {
            date = year + "-" + month + "-0" + day;
        } else {
            date = year + "-" + month + "-" + day;
        }
        return date;
    }

    public static String getTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        return hour + ":" + minute + ":" + second;
    }

    public static int[] dateToParts(String date) {
        int[] parts = new int[3];
        String[] dateParts = date.split("-");
        for (int i = 0; i < 3; i++) {
            parts[i] = Integer.parseInt(dateParts[i]);
        }
        return parts;
    }


    public static int[] timeToParts(String time) {
        int[] parts = new int[3];
        String[] timeParts = time.split(":");
        for (int i = 0; i < timeParts.length; i++) {
            parts[i] = Integer.parseInt(timeParts[i]);
        }

        return parts;
    }

}
