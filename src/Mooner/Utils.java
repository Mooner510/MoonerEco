package Mooner;

import org.bukkit.ChatColor;

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

    public static String chat (String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String getCommaNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }
}
