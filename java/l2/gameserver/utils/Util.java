/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import l2.commons.util.RandomUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.DeclensionKey;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.tuple.Pair;

public class Util {
    static final String PATTERN = "0.0000000000E00";
    static final DecimalFormat df;
    private static NumberFormat axi;
    private static Pattern axj;

    public static boolean isMatchingRegexp(String string, String string2) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(string2);
        }
        catch (PatternSyntaxException patternSyntaxException) {
            patternSyntaxException.printStackTrace();
        }
        if (pattern == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static String formatDouble(double d, String string, boolean bl) {
        if (Double.isNaN(d)) {
            return string;
        }
        if (bl) {
            return df.format(d);
        }
        if ((double)((long)d) == d) {
            return String.valueOf((long)d);
        }
        return String.valueOf(d);
    }

    public static String formatAdena(long l) {
        return axi.format(l);
    }

    public static String formatTime(int n) {
        if (n == 0) {
            return "now";
        }
        n = Math.abs(n);
        String string = "";
        long l = n / 86400;
        n = (int)((long)n - l * 86400L);
        long l2 = n / 3600;
        n = (int)((long)n - l2 * 3600L);
        long l3 = n / 60;
        n = (int)((long)n - l3 * 60L);
        long l4 = n;
        if (l > 0L) {
            string = string + l + "d ";
        }
        if (l2 > 0L) {
            string = string + l2 + "h ";
        }
        if (l3 > 0L) {
            string = string + l3 + "m ";
        }
        if (l4 > 0L) {
            string = string + l4 + "s";
        }
        return string.trim();
    }

    public static String getCfgDirect() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Auth: ").append(Config.GAME_SERVER_LOGIN_HOST).append('\n');
        stringBuilder.append("Game:\n");
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
                Enumeration<InetAddress> enumeration2 = networkInterface.getInetAddresses();
                while (enumeration2.hasMoreElements()) {
                    InetAddress inetAddress = enumeration2.nextElement();
                    String string = inetAddress.getHostAddress();
                    stringBuilder.append(" ").append(string).append('\n');
                }
            }
        }
        catch (SocketException socketException) {
            return "none";
        }
        return stringBuilder.toString();
    }

    public static long rollDrop(long l, long l2, double d, boolean bl) {
        if (d <= 0.0 || l <= 0L || l2 <= 0L) {
            return 0L;
        }
        int n = 1;
        if (bl) {
            d *= Config.RATE_DROP_ITEMS;
        }
        if (d > 1000000.0) {
            if (d % 1000000.0 == 0.0) {
                n = (int)(d / 1000000.0);
            } else {
                n = (int)Math.ceil(d / 1000000.0);
                d /= (double)n;
            }
        }
        return Rnd.chance(d / 10000.0) ? Rnd.get(l * (long)n, l2 * (long)n) : 0L;
    }

    public static int packInt(int[] nArray, int n) throws Exception {
        int n2 = 32 / n;
        if (nArray.length > n2) {
            throw new Exception("Overflow");
        }
        int n3 = 0;
        int n4 = (int)Math.pow(2.0, n);
        for (int i = 0; i < n2; ++i) {
            int n5;
            n3 <<= n;
            if (nArray.length > i) {
                n5 = nArray[i];
                if (n5 >= n4 || n5 < 0) {
                    throw new Exception("Overload, value is out of range");
                }
            } else {
                n5 = 0;
            }
            n3 += n5;
        }
        return n3;
    }

    public static long packLong(int[] nArray, int n) throws Exception {
        int n2 = 64 / n;
        if (nArray.length > n2) {
            throw new Exception("Overflow");
        }
        long l = 0L;
        int n3 = (int)Math.pow(2.0, n);
        for (int i = 0; i < n2; ++i) {
            int n4;
            l <<= n;
            if (nArray.length > i) {
                n4 = nArray[i];
                if (n4 >= n3 || n4 < 0) {
                    throw new Exception("Overload, value is out of range");
                }
            } else {
                n4 = 0;
            }
            l += (long)n4;
        }
        return l;
    }

    public static int[] unpackInt(int n, int n2) {
        int n3 = 32 / n2;
        int n4 = (int)Math.pow(2.0, n2);
        int[] nArray = new int[n3];
        for (int i = n3; i > 0; --i) {
            int n5 = n;
            nArray[i - 1] = n5 - (n >>= n2) * n4;
        }
        return nArray;
    }

    public static int[] unpackLong(long l, int n) {
        int n2 = 64 / n;
        int n3 = (int)Math.pow(2.0, n);
        int[] nArray = new int[n2];
        for (int i = n2; i > 0; --i) {
            long l2 = l;
            nArray[i - 1] = (int)(l2 - (l >>= n) * (long)n3);
        }
        return nArray;
    }

    public static String joinStrings(String string, String[] stringArray, int n, int n2) {
        return Strings.joinStrings(string, stringArray, n, n2);
    }

    public static String joinStrings(String string, String[] stringArray, int n) {
        return Strings.joinStrings(string, stringArray, n, -1);
    }

    public static boolean isNumber(String string) {
        try {
            Double.parseDouble(string);
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

    public static String dumpObject(Object object, boolean bl, boolean bl2, boolean bl3) {
        Class<?> clazz = object.getClass();
        String string = "[" + (bl ? clazz.getSimpleName() : clazz.getName()) + "\n";
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (clazz = object.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (arrayList.contains(field) || bl3 && Modifier.isStatic(field.getModifiers())) continue;
                arrayList.add(field);
            }
            if (bl2) continue;
        }
        for (Object __raw_field_239 : (Iterable<?>)(Object)(arrayList)) {
           Field field = (Field)__raw_field_239;
            String string2;
            field.setAccessible(true);
            try {
                Object object2 = field.get(object);
                string2 = object2 == null ? "NULL" : object2.toString();
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
                string2 = "<ERROR>";
            }
            String string3 = bl ? field.getType().getSimpleName() : field.getType().toString();
            string = string + String.format("\t%s [%s] = %s;\n", field.getName(), string3, string2);
        }
        string = string + "]\n";
        return string;
    }

    public static HashMap<Integer, String> parseTemplate(String string) {
        Matcher matcher = axj.matcher(string);
        HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
        while (matcher.find()) {
            hashMap.put(Integer.parseInt(matcher.group(1)), matcher.group(2));
            string = string.replace(matcher.group(0), "");
        }
        hashMap.put(0, string);
        return hashMap;
    }

    public static int fibonacci(int n) {
        int n2 = 0;
        int n3 = 1;
        for (int i = 0; i < n; ++i) {
            int n4 = n2;
            n2 = n3;
            n3 = n4 + n3;
        }
        return n2;
    }

    public static double padovan(int n) {
        if (n == 0 || n == 1 || n == 2) {
            return 1.0;
        }
        return Util.padovan(n - 2) + Util.padovan(n - 3);
    }

    public static Player[] GetPlayersFromStoredIds(long[] lArray) {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (long l : lArray) {
            Player player = GameObjectsStorage.getAsPlayer(l);
            if (player == null) continue;
            arrayList.add(player);
        }
        return arrayList.toArray(new Player[arrayList.size()]);
    }

    public static String getNavigationBlock(int n, int n2, int n3, int n4, boolean bl, String string) {
        String string2 = "";
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        String string3 = string;
        String string4 = string;
        for (int i = 1; i <= n; ++i) {
            if (!bl2) {
                if (n2 == 1) {
                    string2 = string2 + "<td width=80 align=left valign=top>&nbsp;</td>";
                } else {
                    string3 = String.format("" + string3 + "", n2 - 1);
                    string2 = string2 + "<td width=80 align=left valign=top><button action=\"bypass -h " + string3 + "\" width=16 height=16 back=\"L2UI_CH3.shortcut_prev_down\" fore=\"L2UI_CH3.shortcut_prev\"></td>";
                }
                bl2 = true;
            }
            if (!bl3 && i == n2) {
                string2 = n3 <= n4 ? string2 + "<td width=50 align=center valign=top>&nbsp;</td>" : string2 + "<td width=50 align=center valign=top>[ " + i + " ]</td>";
                bl3 = true;
            }
            if (bl4 || i != n2) continue;
            if (bl && n >= n2 + 1) {
                string4 = String.format("" + string4 + "", n2 + 1);
                string2 = string2 + "<td width=80 align=right valign=top><button action=\"bypass -h " + string4 + "\" width=16 height=16 back=\"L2UI_CH3.shortcut_next_down\" fore=\"L2UI_CH3.shortcut_next\"></td>";
            } else {
                string2 = string2 + "<td width=80 align=right valign=top>&nbsp;</td>";
            }
            bl4 = true;
        }
        if (string2.equals("")) {
            string2 = "<td width=30 align=center valign=top>&nbsp;</td>";
        }
        return string2;
    }

    public static String declension(long l, DeclensionKey declensionKey) {
        String string = "";
        String string2 = "";
        String string3 = "";
        switch (declensionKey) {
            case DAYS: {
                string = "Day";
                string2 = "Days";
                string3 = "Days";
                break;
            }
            case HOUR: {
                string = "Hour";
                string2 = "Hours";
                string3 = "Hours";
                break;
            }
            case MINUTES: {
                string = "Minute";
                string2 = "Minutes";
                string3 = "Minutes";
                break;
            }
            case PIECE: {
                string = "Piece";
                string2 = "Pieces";
                string3 = "Pieces";
                break;
            }
            case POINT: {
                string = "Point";
                string2 = "Points";
                string3 = "Points";
            }
        }
        if (l > 100L) {
            l %= 100L;
        }
        if (l > 20L) {
            l %= 10L;
        }
        if (l == 1L) {
            return string;
        }
        if (l == 2L || l == 3L || l == 4L) {
            return string2;
        }
        return string3;
    }

    public static String getItemIcon(int n) {
        return ItemHolder.getInstance().getTemplate(n).getIcon();
    }

    public static List<Pair<ItemTemplate, Long>> calcProducts(List<Pair<ItemTemplate, Pair<Long, Double>>> list) {
        LinkedList<Pair<ItemTemplate, Long>> linkedList = new LinkedList<Pair<ItemTemplate, Long>>();
        LinkedList linkedList2 = new LinkedList();
        double d = 0.0;
        for (Pair<ItemTemplate, Pair<Long, Double>> pair : list) {
            double d2 = pair.getValue().getRight();
            Pair<ItemTemplate, Long> pair2 = Pair.of(pair.getKey(), pair.getValue().getLeft());
            if (d2 < 0.0) {
                linkedList.add(pair2);
                continue;
            }
            linkedList2.add(Pair.of(pair2, d2));
            d += d2;
        }
        if (d > 0.0) {
            Collections.sort(linkedList2, RandomUtils.DOUBLE_GROUP_COMPARATOR);
            Pair pair = (Pair)RandomUtils.pickRandomSortedGroup(linkedList2, d);
            if (pair != null) {
                linkedList.add(pair);
            }
        }
        return linkedList;
    }

    static {
        axi = NumberFormat.getIntegerInstance(Locale.FRANCE);
        df = (DecimalFormat)NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.applyPattern(PATTERN);
        df.setPositivePrefix("+");
        axj = Pattern.compile("<!--TEMPLET(\\d+)(.*?)TEMPLET-->", 32);
    }
}

