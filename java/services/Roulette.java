/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.GameStats;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;

public class Roulette
extends Functions {
    private static final String bXN = "red";
    private static final String bXO = "black";
    private static final String bXP = "first";
    private static final String bXQ = "second";
    private static final String bXR = "third";
    private static final String bXS = "even";
    private static final String bXT = "odd";
    private static final String bXU = "low";
    private static final String bXV = "high";
    private static final String bXW = "zero";
    private static final String[][] bXX = new String[][]{{"0", "zero", "zero", "zero", "zero", "zero"}, {"1", "red", "first", "first", "odd", "low"}, {"2", "black", "first", "second", "even", "low"}, {"3", "red", "first", "third", "odd", "low"}, {"4", "black", "first", "first", "even", "low"}, {"5", "red", "first", "second", "odd", "low"}, {"6", "black", "first", "third", "even", "low"}, {"7", "red", "first", "first", "odd", "low"}, {"8", "black", "first", "second", "even", "low"}, {"9", "red", "first", "third", "odd", "low"}, {"10", "black", "first", "first", "even", "low"}, {"11", "black", "first", "second", "odd", "low"}, {"12", "red", "first", "third", "even", "low"}, {"13", "black", "second", "first", "odd", "low"}, {"14", "red", "second", "second", "even", "low"}, {"15", "black", "second", "third", "odd", "low"}, {"16", "red", "second", "first", "even", "low"}, {"17", "black", "second", "second", "odd", "low"}, {"18", "red", "second", "third", "even", "low"}, {"19", "red", "second", "first", "odd", "high"}, {"20", "black", "second", "second", "even", "high"}, {"21", "red", "second", "third", "odd", "high"}, {"22", "black", "second", "first", "even", "high"}, {"23", "red", "second", "second", "odd", "high"}, {"24", "black", "second", "third", "even", "high"}, {"25", "red", "third", "first", "odd", "high"}, {"26", "black", "third", "second", "even", "high"}, {"27", "red", "third", "third", "odd", "high"}, {"28", "black", "third", "first", "even", "high"}, {"29", "black", "third", "second", "odd", "high"}, {"30", "red", "third", "third", "even", "high"}, {"31", "black", "third", "first", "odd", "high"}, {"32", "red", "third", "second", "even", "high"}, {"33", "black", "third", "third", "odd", "high"}, {"34", "red", "third", "first", "even", "high"}, {"35", "black", "third", "second", "odd", "high"}, {"36", "red", "third", "third", "even", "high"}};

    public void dialog() {
        Player player = this.getSelf();
        this.show(HtmCache.getInstance().getNotNull("scripts/services/roulette.htm", player).replaceFirst("%min%", Util.formatAdena(Config.SERVICES_ROULETTE_MIN_BET)).replaceFirst("%max%", Util.formatAdena(Config.SERVICES_ROULETTE_MAX_BET)), player);
    }

    public void play(String[] stringArray) {
        GameType gameType;
        Player player = this.getSelf();
        long l = 0L;
        String string = "";
        try {
            if (stringArray.length != 3) {
                throw new NumberFormatException();
            }
            gameType = GameType.valueOf(stringArray[0]);
            string = stringArray[1].trim();
            l = Long.parseLong(stringArray[2]);
            if (gameType == GameType.StraightUp && (string.length() > 2 || Integer.parseInt(string) < 0 || Integer.parseInt(string) > 36)) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException numberFormatException) {
            this.show("Invalid value input!<br><a action=\"bypass -h scripts_services.Roulette:dialog\">Back</a>", player);
            return;
        }
        if (l < (long)Config.SERVICES_ROULETTE_MIN_BET) {
            this.show("Too small bet!<br><a action=\"bypass -h scripts_services.Roulette:dialog\">Back</a>", player);
            return;
        }
        if (l > (long)Config.SERVICES_ROULETTE_MAX_BET) {
            this.show("Too large bet!<br><a action=\"bypass -h scripts_services.Roulette:dialog\">Back</a>", player);
            return;
        }
        if (ItemFunctions.getItemCount(player, Config.SERVICES_ROULETTE_ITEM_ID) < l) {
            if (Config.SERVICES_ROULETTE_ITEM_ID == 57) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
            this.show("You do not have enough items!<br><a action=\"bypass -h scripts_services.Roulette:dialog\">Back</a>", player);
            return;
        }
        String[] stringArray2 = bXX[Rnd.get(bXX.length)];
        int n = Roulette.a(string, stringArray2, gameType);
        String string2 = HtmCache.getInstance().getNotNull("scripts/services/rouletteresult.htm", player);
        if (n == 0) {
            Roulette.removeItem(player, Config.SERVICES_ROULETTE_ITEM_ID, l);
            Log.service("Roulette", player, "lost the bet and removed from inventory " + Config.SERVICES_ROULETTE_ITEM_ID + " amount " + l);
            GameStats.addRoulette(l);
            string2 = string2.replace("%result%", "<font color=\"FF0000\">Fail!</font>");
        } else {
            Roulette.addItem(player, Config.SERVICES_ROULETTE_ITEM_ID, l * (long)n);
            GameStats.addRoulette(-1L * l * (long)n);
            Log.service("Roulette", player, "received an award " + Config.SERVICES_ROULETTE_ITEM_ID + " amount " + l * (long)n);
            string2 = string2.replace("%result%", "<font color=\"00FF00\">Success!</font>");
        }
        if (player.isGM()) {
            player.sendMessage("Roulette balance: " + Util.formatAdena(GameStats.getRouletteSum()));
        }
        string2 = string2.replace("%bettype%", new CustomMessage("Roulette." + gameType.toString(), player, new Object[0]).toString());
        string2 = string2.replace("%betnumber%", gameType == GameType.StraightUp ? string : new CustomMessage("Roulette." + string, player, new Object[0]).toString());
        string2 = string2.replace("%number%", stringArray2[0]);
        string2 = string2.replace("%color%", new CustomMessage("Roulette." + stringArray2[1], player, new Object[0]).toString());
        string2 = string2.replace("%evenness%", new CustomMessage("Roulette." + stringArray2[4], player, new Object[0]).toString());
        string2 = string2.replace("%column%", new CustomMessage("Roulette." + stringArray2[3], player, new Object[0]).toString());
        string2 = string2.replace("%dozen%", new CustomMessage("Roulette." + stringArray2[2], player, new Object[0]).toString());
        string2 = string2.replace("%highness%", new CustomMessage("Roulette." + stringArray2[5], player, new Object[0]).toString());
        string2 = string2.replace("%param%", stringArray[0] + " " + stringArray[1] + " " + stringArray[2]);
        this.show(string2, player);
    }

    private static final int a(String string, String[] stringArray, GameType gameType) {
        switch (gameType) {
            case StraightUp: {
                if (string.equals(stringArray[0])) {
                    return 35;
                }
                return 0;
            }
            case ColumnBet: {
                if (string.equals(stringArray[3])) {
                    return 2;
                }
                return 0;
            }
            case DozenBet: {
                if (string.equals(stringArray[2])) {
                    return 2;
                }
                return 0;
            }
            case RedOrBlack: {
                if (string.equals(stringArray[1])) {
                    return 1;
                }
                return 0;
            }
            case EvenOrOdd: {
                if (string.equals(stringArray[4])) {
                    return 1;
                }
                return 0;
            }
            case LowOrHigh: {
                if (string.equals(stringArray[5])) {
                    return 1;
                }
                return 0;
            }
        }
        return 0;
    }

    public String DialogAppend_30990(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30991(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30992(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30993(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30994(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String getHtmlAppends(Integer n) {
        Player player = this.getSelf();
        if (Config.SERVICES_ALLOW_ROULETTE) {
            return "<br><a action=\"bypass -h scripts_services.Roulette:dialog\">" + new CustomMessage("Roulette.dialog", player, new Object[0]).toString() + "</a>";
        }
        return "";
    }

    private static enum GameType {
        StraightUp,
        ColumnBet,
        DozenBet,
        RedOrBlack,
        EvenOrOdd,
        LowOrHigh;

    }
}

