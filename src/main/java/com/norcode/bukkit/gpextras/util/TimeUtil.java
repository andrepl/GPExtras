package com.norcode.bukkit.gpextras.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String millisToString(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours =TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        List<String> parts = new LinkedList<String>();
        if (days > 0) {
            parts.add(days + " days");
        }
        if (hours > 0) {
            parts.add(hours + " hr");
        }
        if (minutes > 0) {
            parts.add(minutes + " min");
        }
        String msg = "";
        for (int i=0;i<parts.size();i++) {
            msg += parts.get(i);
            if (i < parts.size()-1) {
                msg += ", ";
            } else {
                msg += " ";
            }
        }
        return msg;
    }
}