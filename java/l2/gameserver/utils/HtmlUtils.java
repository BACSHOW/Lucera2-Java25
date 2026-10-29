/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import org.apache.commons.lang3.StringUtils;

public class HtmlUtils {
    public static final String PREV_BUTTON = "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">";
    public static final String NEXT_BUTTON = "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">";

    public static String htmlResidenceName(int n) {
        return "&%" + n + ";";
    }

    public static String htmlNpcName(int n) {
        return "&@" + n + ";";
    }

    public static String htmlSysString(SysString sysString) {
        return HtmlUtils.htmlSysString(sysString.getId());
    }

    public static String htmlSysString(int n) {
        return "&$" + n + ";";
    }

    public static String htmlItemName(int n) {
        return "&#" + n + ";";
    }

    public static String htmlClassName(int n, Player player) {
        return new CustomMessage(String.format("ClassName.%d", n), player, new Object[0]).toString();
    }

    public static String htmlNpcString(NpcString npcString, Object ... objectArray) {
        return HtmlUtils.htmlNpcString(npcString.getId(), objectArray);
    }

    public static String htmlNpcString(int n, Object ... objectArray) {
        String string = "<fstring";
        if (objectArray.length > 0) {
            for (int i = 0; i < objectArray.length; ++i) {
                string = string + " p" + (i + 1) + "=\"" + String.valueOf(objectArray[i]) + "\"";
            }
        }
        string = string + ">" + n + "</fstring>";
        return string;
    }

    public static String htmlButton(String string, String string2, int n) {
        return HtmlUtils.htmlButton(string, string2, n, 22);
    }

    public static String htmlButton(String string, String string2, int n, int n2) {
        return String.format("<button value=\"%s\" action=\"%s\" back=\"L2UI_CH3.bigbutton2_down\" width=%d height=%d fore=\"L2UI_CH3.bigbutton2\">", string, string2, n, n2);
    }

    public static String sanitizeHtml(String string, int n) {
        return StringUtils.truncate(StringUtils.replaceChars(StringUtils.trimToEmpty(string), "><$\"", ""), n);
    }

    public static String sanitizeHtml(String string) {
        return HtmlUtils.sanitizeHtml(string, 255);
    }
}

