/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import l2.gameserver.utils.DeclensionKey;
import l2.gameserver.utils.Util;

public class TimeUtils {
    private static final SimpleDateFormat Pu = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private static final SimpleDateFormat axh = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    public static String toSimpleFormat(Calendar calendar) {
        return Pu.format(calendar.getTime());
    }

    public static String toSimpleFormat(long l) {
        return Pu.format(l);
    }

    public static String toHeroRecordFormat(long l) {
        return axh.format(l);
    }

    public static String formatTime(int n, boolean bl) {
        int n2 = n / 86400;
        int n3 = (n - n2 * 24 * 3600) / 3600;
        int n4 = (n - n2 * 24 * 3600 - n3 * 3600) / 60;
        String string = n2 >= 1 ? (n3 < 1 || bl ? n2 + " " + Util.declension(n2, DeclensionKey.DAYS) : n2 + " " + Util.declension(n2, DeclensionKey.DAYS) + " " + n3 + " " + Util.declension(n3, DeclensionKey.HOUR)) : (n3 >= 1 ? (n4 < 1 || bl ? n3 + " " + Util.declension(n3, DeclensionKey.HOUR) : n3 + " " + Util.declension(n3, DeclensionKey.HOUR) + " " + n4 + " " + Util.declension(n4, DeclensionKey.MINUTES)) : n4 + " " + Util.declension(n4, DeclensionKey.MINUTES));
        return string;
    }

    public static long parse(String string) throws ParseException {
        return Pu.parse(string).getTime();
    }
}

