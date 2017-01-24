package de.fynder.jtransfer.util;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class DateUtility {
    private static final Pattern diffPattern = Pattern.compile("[\\-\\+]?\\d+ [a-z]+");

    private static Calendar c = Calendar.getInstance();



    static Date strToDate(String date, Date referenceDate) {
        c.setTime(referenceDate);
        if(date.equals("now")) {
            return c.getTime();
        }

        if (diffPattern.matcher(date).matches()) {
            String[] parts = date.split(" ");
            int amount = Integer.parseInt(parts[0]);

            if(parts[1].indexOf("day") == 0) {
                c.add(Calendar.DAY_OF_YEAR, amount);
            } else if(parts[1].indexOf("week") == 0) {
                c.add(Calendar.DAY_OF_YEAR, amount * 7);
            } else if(parts[1].indexOf("month") == 0) {
                c.add(Calendar.MONTH, amount);
            } else if(parts[1].indexOf("year") == 0) {
                c.add(Calendar.YEAR, amount);
            } else if(parts[1].indexOf("sec") == 0) {
                c.add(Calendar.SECOND, amount);
            } else if(parts[1].indexOf("min") == 0) {
                c.add(Calendar.MINUTE, amount);
            } else if(parts[1].indexOf("hour") == 0) {
                c.add(Calendar.HOUR, amount);
            }

            return c.getTime();
        }

        String[] supportedDateFormats = new String[] {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd"
        };

        for(String f : supportedDateFormats) {
            try {
                SimpleDateFormat simpledateformat = new SimpleDateFormat(f);
                return simpledateformat.parse(date);
            } catch(Exception ignored) {

            }
        }

        try {
            LocalDateTime local = LocalDateTime.parse(date);
            c.setTime(Date.from(local.atZone(ZoneId.systemDefault()).toInstant()));
            return c.getTime();
        } catch(Exception ignored) {

        }

        return null;
    }

    public static Date strToDate(String date) {
        return strToDate(date, new Date());
    }
}
