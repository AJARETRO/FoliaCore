package dev.ajaretro.foliaCore.utils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhd])");

    public static long parseTime(String timeString) {
        long totalMillis = 0;
        Matcher matcher = TIME_PATTERN.matcher(timeString);

        if (!matcher.find()) {
            return -1;
        }

        matcher.reset();

        while (matcher.find()) {
            try {
                long value = Long.parseLong(matcher.group(1));
                String unit = matcher.group(2);

                switch (unit) {
                    case "s":
                        totalMillis += TimeUnit.SECONDS.toMillis(value);
                        break;
                    case "m":
                        totalMillis += TimeUnit.MINUTES.toMillis(value);
                        break;
                    case "h":
                        totalMillis += TimeUnit.HOURS.toMillis(value);
                        break;
                    case "d":
                        totalMillis += TimeUnit.DAYS.toMillis(value);
                        break;
                }
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return totalMillis;
    }

    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "Permanent";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");

        String formatted = sb.toString().trim();
        return formatted.isEmpty() ? "0s" : formatted;
    }
}