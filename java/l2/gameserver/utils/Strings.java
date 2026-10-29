/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import l2.gameserver.Config;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strings {
    private static final Logger _log = LoggerFactory.getLogger(Strings.class);
    private static String[] axe;
    private static String[] axf;
    private static String[] axg;

    public static String stripSlashes(String string) {
        if (string == null) {
            return "";
        }
        string = string.replace("\\'", "'");
        string = string.replace("\\\\", "\\");
        return string;
    }

    public static Boolean parseBoolean(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Number) {
            return ((Number)object).intValue() > 0;
        }
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object instanceof Double) {
            return Math.abs((Double)object) < 1.0E-5;
        }
        return !String.valueOf(object).isEmpty();
    }

    public static void reload() {
        try {
            String[] stringArray;
            int n;
            String[] stringArray2 = FileUtils.readFileToString(new File(Config.DATAPACK_ROOT, "data/translit.txt")).split("\n");
            axe = new String[stringArray2.length * 2];
            for (n = 0; n < stringArray2.length; ++n) {
                stringArray = stringArray2[n].split(" +");
                Strings.axe[n * 2] = stringArray[0];
                Strings.axe[n * 2 + 1] = stringArray[1];
            }
            stringArray2 = FileUtils.readFileToString(new File(Config.DATAPACK_ROOT, "data/translit_back.txt")).split("\n");
            axf = new String[stringArray2.length * 2];
            for (n = 0; n < stringArray2.length; ++n) {
                stringArray = stringArray2[n].split(" +");
                Strings.axf[n * 2] = stringArray[0];
                Strings.axf[n * 2 + 1] = stringArray[1];
            }
            stringArray2 = FileUtils.readFileToString(new File(Config.DATAPACK_ROOT, "data/transcode.txt")).split("\n");
            axg = new String[stringArray2.length * 2];
            for (n = 0; n < stringArray2.length; ++n) {
                stringArray = stringArray2[n].split(" +");
                Strings.axg[n * 2] = stringArray[0];
                Strings.axg[n * 2 + 1] = stringArray[1];
            }
        }
        catch (IOException iOException) {
            _log.error("", iOException);
        }
        _log.info("Loaded " + (axe.length + axe.length + axg.length) + " translit entries.");
    }

    public static String translit(String string) {
        for (int i = 0; i < axe.length; i += 2) {
            string = string.replace(axe[i], axe[i + 1]);
        }
        return string;
    }

    public static String fromTranslit(String string, int n) {
        block3: {
            block2: {
                if (n != 1) break block2;
                for (int i = 0; i < axf.length; i += 2) {
                    string = string.replace(axf[i], axf[i + 1]);
                }
                break block3;
            }
            if (n != 2) break block3;
            for (int i = 0; i < axg.length; i += 2) {
                string = string.replace(axg[i], axg[i + 1]);
            }
        }
        return string;
    }

    public static String replace(String string, String string2, int n, String string3) {
        return Pattern.compile(string2, n).matcher(string).replaceAll(string3);
    }

    public static boolean matches(String string, String string2, int n) {
        return Pattern.compile(string2, n).matcher(string).matches();
    }

    public static String bbParse(String string) {
        if (string == null) {
            return null;
        }
        string = string.replace("\r", "");
        string = string.replaceAll("(\\s|\"|'|\\(|^|\n)\\*(.*?)\\*(\\s|\"|'|\\)|\\?|\\.|!|:|;|,|$|\n)", "$1<font color=\"LEVEL\">$2</font>$3");
        string = string.replaceAll("(\\s|\"|'|\\(|^|\n)\\$(.*?)\\$(\\s|\"|'|\\)|\\?|\\.|!|:|;|,|$|\n)", "$1<font color=\"00FFFF\">$2</font>$3");
        string = Strings.replace(string, "^!(.*?)$", 8, "<font color=\"FFFFFF\">$1</font>\n\n");
        string = string.replaceAll("%%\\s*\n", "<br1>");
        string = string.replaceAll("\n\n+", "<br>");
        string = Strings.replace(string, "\\[([^\\]\\|]*?)\\|([^\\]]*?)\\]", 32, "<a action=\"bypass -h $1\">$2</a>");
        string = string.replaceAll(" @", "\" msg=\"");
        return string;
    }

    public static String joinStrings(String string, String[] stringArray, int n, int n2) {
        String string2 = "";
        if (n < 0 && (n += stringArray.length) < 0) {
            return string2;
        }
        while (n < stringArray.length && n2 != 0) {
            if (!string2.isEmpty() && string != null && !string.isEmpty()) {
                string2 = string2 + string;
            }
            string2 = string2 + stringArray[n++];
            --n2;
        }
        return string2;
    }

    public static String joinStrings(String string, String[] stringArray, int n) {
        return Strings.joinStrings(string, stringArray, n, -1);
    }

    public static String joinStrings(String string, String[] stringArray) {
        return Strings.joinStrings(string, stringArray, 0);
    }

    public static String stripToSingleLine(String string) {
        if (string.isEmpty()) {
            return string;
        }
        int n = (string = string.replaceAll("\\\\n", "\n")).indexOf("\n");
        if (n > -1) {
            string = string.substring(0, n);
        }
        return string;
    }
}

