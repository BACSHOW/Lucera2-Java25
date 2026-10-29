/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.util.List;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;

public class BypassManager {
    public static final String SIMPLE_MRSL = "_mrsl";
    public static final String SIMPLE_DIARY = "_diary";
    public static final String SIMPLE_MATCH = "_match";
    public static final String SIMPLE_MANOR_MENU_SELECT = "manor_menu_select";
    public static final String SIMPLE_OLYMPIAD = "_olympiad";
    public static final String SIMPLE_DISPEL = "_dispel";
    public static final String SIMPLE_DISPEL_CUSTOM_PATCH = "interface?dispell=";
    public static final String SIMPLE_AUTOLOOT_ON = "interface?autoloot_on";
    public static final String SIMPLE_AUTOLOOT_OFF = "interface?autoloot_off";
    public static final String SIMPLE_EXP_ON = "interface?exp_on";
    public static final String SIMPLE_EXP_OFF = "interface?exp_off";
    public static final String SIMPLE_DONATE_URL = "interface?donate_ru";
    public static final String SIMPLE_VK_URL = "interface?vk";
    public static final String SIMPLE_FACEBOOK_URL = "interface?facebook";
    private static final String[] zY = new String[]{"_mrsl", "_diary", "_match", "manor_menu_select", "_olympiad", "_dispel", "interface?dispell=", "interface?autoloot_on", "interface?autoloot_off", "interface?exp_on", "interface?exp_off", "interface?donate_ru", "interface?vk", "interface?facebook"};
    private static final String[] zZ = new String[]{"_bbshome", "_bbsgetfav", "_bbslink", "_bbsloc", "_bbsclan", "_bbsmemo", "_maillist_0_1_0_", "_friendlist_0_", "_bbsaddfav"};

    private static boolean b(String string, boolean bl) {
        String[] stringArray;
        for (String string2 : stringArray = bl ? zZ : zY) {
            if (!string.startsWith(string2)) continue;
            return true;
        }
        return false;
    }

    public static BypassType getBypassType(String string, Player player) {
        switch (string.charAt(0)) {
            case '0': {
                return BypassType.ENCODED;
            }
            case '1': {
                return BypassType.ENCODED_BBS;
            }
        }
        if (BypassManager.b(string, false)) {
            return BypassType.SIMPLE;
        }
        if (BypassManager.b(string, true) && CommunityBoardManager.getInstance().getCommunityHandler(string, player) != null) {
            return BypassType.SIMPLE_BBS;
        }
        return BypassType.SIMPLE_DIRECT;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String encode(String string, List<String> list, boolean bl) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] cArray = string.toCharArray();
        int n = 0;
        int n2 = 0;
        while (n2 + 7 < cArray.length) {
            int n3;
            int n4 = 0;
            int n5 = 0;
            if (cArray[n2] == '\"' && Character.toLowerCase(cArray[n2 + 1]) == 'b' && Character.toLowerCase(cArray[n2 + 2]) == 'y' && Character.toLowerCase(cArray[n2 + 3]) == 'p' && Character.toLowerCase(cArray[n2 + 4]) == 'a' && Character.toLowerCase(cArray[n2 + 5]) == 's' && Character.toLowerCase(cArray[n2 + 6]) == 's' && Character.isWhitespace(cArray[n2 + 7])) {
                n3 = 8;
                while (n3 + n2 < cArray.length && cArray[n3 + n2] != '\"') {
                    ++n3;
                }
                if (n3 + n2 == cArray.length) {
                    n4 = 0;
                    n5 = 0;
                } else {
                    n4 = n2 + 1;
                    n5 = n3 - 1;
                }
            }
            if (n5 > 0) {
                boolean bl2;
                String string2;
                boolean bl3;
                for (n3 = 7; n3 < n5 && Character.isWhitespace(cArray[n4 + n3]); ++n3) {
                }
                boolean bl4 = bl3 = cArray[n4 + n3] == '-' && (cArray[n4 + n3 + 1] == 'h' || cArray[n4 + n3 + 1] == 'H');
                if (bl3) {
                    n3 += 2;
                    while (n3 < n5 && (cArray[n4 + n3] == ' ' || cArray[n4 + n3] == '\t')) {
                        ++n3;
                    }
                }
                String string3 = string2 = new String(cArray, n4 + n3, n5 - n3);
                String string4 = "";
                int n6 = string2.indexOf(" $");
                boolean bl5 = bl2 = n6 >= 0;
                if (bl2) {
                    string3 = string2.substring(0, n6);
                    string4 = string2.substring(n6);
                }
                stringBuffer.append(cArray, n, n4 - n);
                n = n4 + n5;
                stringBuffer.append("bypass ");
                if (bl3) {
                    stringBuffer.append("-h ");
                }
                stringBuffer.append(bl ? (char)'1' : '0');
                List<String> list2 = list;
                synchronized (list2) {
                    stringBuffer.append(Integer.toHexString(list.size()));
                    stringBuffer.append(string4);
                    list.add(string3);
                }
            }
            ++n2;
        }
        stringBuffer.append(cArray, n, cArray.length - n);
        return stringBuffer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DecodedBypass decode(String string, List<String> list, boolean bl, GameClient gameClient) {
        List<String> list2 = list;
        synchronized (list2) {
            String string2;
            String[] stringArray = StringUtils.split(string, ' ');
            int n = Integer.parseInt(stringArray[0].substring(1), 16);
            try {
                string2 = list.get(n);
            }
            catch (Exception exception) {
                string2 = null;
            }
            if (string2 == null) {
                Log.add("Can't decode bypass (bypass not exists): " + (bl ? "[bbs] " : "") + string + " / Client: " + gameClient.toString() + " / Npc: " + (gameClient.getActiveChar() == null || gameClient.getActiveChar().getLastNpc() == null ? "null" : gameClient.getActiveChar().getLastNpc().getName()), "debug_bypass");
                return null;
            }
            DecodedBypass decodedBypass = new DecodedBypass(string2, bl, !bl ? BypassType.ENCODED : BypassType.ENCODED_BBS);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < stringArray.length; ++i) {
                stringBuilder.append(' ').append(stringArray[i]);
            }
            decodedBypass.bypass = decodedBypass.bypass + stringBuilder.toString();
            decodedBypass.trim();
            return decodedBypass;
        }
    }

    public static class DecodedBypass {
        public String bypass;
        public boolean bbs;
        public BypassType bypassType;

        public DecodedBypass(String string, boolean bl, BypassType bypassType) {
            this.bypass = string;
            this.bbs = bl;
            this.bypassType = bypassType;
        }

        public DecodedBypass trim() {
            this.bypass = this.bypass.trim();
            return this;
        }
    }

    public static enum BypassType {
        ENCODED,
        ENCODED_BBS,
        SIMPLE,
        SIMPLE_BBS,
        SIMPLE_DIRECT;

    }
}

