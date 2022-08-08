package org.mooner.moonereco;

import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class Utils {
    private static final long aMinute = 60;
    private static final long aHour = 60 * 60;
    private static final long aDay = aHour * 24;
    private static final long aMonth = aDay * 30;
    private static final long aYear = aMonth * 24;

    public static String timeAgo(long beforeTimestamp, long nowTimestamp) {
        long limitTimestamp = nowTimestamp - beforeTimestamp;
        if (limitTimestamp >= aYear) {
            return limitTimestamp / aYear + " years";
        }
        if (limitTimestamp >= aMonth) {
            return limitTimestamp / aMonth + " months";
        }
        if (limitTimestamp >= aDay) {
            return limitTimestamp / aDay + " days";
        }
        if (limitTimestamp >= aHour) {
            return limitTimestamp / aHour + " hours";
        }
        if (limitTimestamp >= aMinute) {
            return limitTimestamp / aMinute + " mins";
        }
        return limitTimestamp + " secs";
    }

    public static String getCommaNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String getCommaNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String parseIfInt(double value, boolean comma) {
        if(comma) {
            if ((long) value == value) {
                return getCommaNumber((int) Math.floor(value));
            }
            return getCommaNumber(value);
        } else {
            if ((long) value == value) {
                return (long) Math.floor(value) + "";
            }
            return BigDecimal.valueOf(value).toPlainString();
        }
    }

    public static String parseString(double value) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(0, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, int amount) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, boolean comma) {
        BigDecimal b;
        try {
            b = BigDecimal.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
            return value + "";
        }
        b = b.setScale(0, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }

    public static String parseString(double value, int amount, boolean comma) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }

    public static String chat(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
