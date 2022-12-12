package ga.justreddy.wiki.daka.utils;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Comparator;
import java.util.Date;

import com.google.gson.Gson;

public class Utils {

    public static String toJson(Object object, Class<?> clazz) {
        return new Gson().toJson(object, clazz);
    }

    public static <T> T fromJson(String string, Class<T> clazz) {
        return new Gson().fromJson(string, clazz);
    }

    public static int compareRoles(Server server, User user1, User user2) {
        Role user1Role = user1.getRoles(server).stream().sorted(Comparator.comparingInt(Role::getPosition).reversed()).findFirst().orElse(user1.getRoles(server).get(0));
        Role user2Role = user2.getRoles(server).stream().sorted(Comparator.comparingInt(Role::getPosition).reversed()).findFirst().orElse(user2.getRoles(server).get(0));
        return user1Role.compareTo(user2Role);
    }

    public static long getDurationMS(String time) {
        long ms = 0;
        if (time.toLowerCase().contains("s"))
            ms = (Long.parseLong(time.replace("s", "")) * 1000) + new Date().getTime();
        if (time.toLowerCase().contains("m") && !time.toLowerCase().contains("o"))
            ms = ((Long.parseLong(time.replace("m", "")) * 1000) * 60) + new Date().getTime();
        if (time.toLowerCase().contains("h"))
            ms = (((Long.parseLong(time.replace("h", "")) * 1000) * 60) * 60) + new Date().getTime();
        if (time.toLowerCase().contains("d"))
            ms = ((((Long.parseLong(time.replace("d", "")) * 1000) * 60) * 60) * 24) + new Date().getTime();
        if (time.toLowerCase().contains("w"))
            ms = (((((Long.parseLong(time.replace("w", "")) * 1000) * 60) * 60) * 24) * 7) + new Date().getTime();
        if (time.toLowerCase().contains("m") && time.toLowerCase().contains("o"))
            ms = (((((Long.parseLong(time.replace("mo", "")) * 1000) * 60) * 60) * 24) * 30) + new Date().getTime();
        if (time.toLowerCase().contains("y"))
            ms = ((((((Long.parseLong(time.replace("y", "")) * 1000) * 60) * 60) * 24) * 7) * 52) + new Date().getTime();

        return ms;
    }

    public static String getDurationString(long time) {
        long current = System.currentTimeMillis();
        long millies = time - current;
        long seconds = 0L;
        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        while (millies > 1000L) {
            millies -= 1000L;
            ++seconds;
        }
        while (seconds > 60L) {
            seconds -= 60L;
            ++minutes;
        }
        while (minutes > 60L) {
            minutes -= 60L;
            ++hours;
        }
        while (hours > 24L) {
            hours -= 24L;
            ++days;
        }

        if (days == 0 && hours == 0 && minutes == 0) {
            return seconds + " seconds";
        }
        if (days == 0 && hours == 0) {
            return minutes + " minutes and " + seconds + " seconds";
        }
        if (days == 0) {
            return hours + " hours, " + minutes + "minutes and " + seconds + " seconds";
        }
        return days + " days, " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds";
    }


}
