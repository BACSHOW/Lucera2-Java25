/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.AutoBan;
import l2.gameserver.utils.Log;

public final class AdminFunctions {
    private AdminFunctions() {
    }

    public static boolean kick(String string, String string2) {
        Player player = World.getPlayer(string);
        if (player == null) {
            return false;
        }
        return AdminFunctions.kick(player, string2);
    }

    public static boolean kick(Player player, String string) {
        if (Config.ALLOW_CURSED_WEAPONS && Config.DROP_CURSED_WEAPONS_ON_KICK && player.isCursedWeaponEquipped()) {
            player.setPvpFlag(0);
            CursedWeaponsManager.getInstance().dropPlayer(player);
        }
        player.kick();
        return true;
    }

    public static String banChat(Player player, String string, String string2, int n, String string3) {
        String string4;
        Player player2 = World.getPlayer(string2);
        if (player2 != null) {
            string2 = player2.getName();
        } else if (CharacterDAO.getInstance().getObjectIdByName(string2) == 0) {
            return "Player " + string2 + " not found.";
        }
        if ((string == null || string.isEmpty()) && player != null) {
            string = player.getName();
        }
        if (string3 == null || string3.isEmpty()) {
            string3 = "no arguments";
        }
        String string5 = null;
        if (n == 0) {
            if (player != null && !player.getPlayerAccess().CanUnBanChat) {
                return "You do not have permission to remove the chat ban.";
            }
            if (Config.BANCHAT_ANNOUNCE) {
                string5 = Config.BANCHAT_ANNOUNCE_NICK && string != null && !string.isEmpty() ? string + " chat ban removed from player " + string2 + "." : "From player " + string2 + " unban chat.";
            }
            Log.add(string + " chat ban removed from player " + string2 + ".", "banchat", player);
            string4 = "You remove a chat ban from player " + string2 + ".";
        } else if (n < 0) {
            if (player != null && player.getPlayerAccess().BanChatMaxValue > 0) {
                return "You can ban no more than " + player.getPlayerAccess().BanChatMaxValue + " minutes.";
            }
            if (Config.BANCHAT_ANNOUNCE) {
                string5 = Config.BANCHAT_ANNOUNCE_NICK && string != null && !string.isEmpty() ? string + " banned the chat player " + string2 + " for an indefinite period, the reason: " + string3 + "." : "Banned chat to the player " + string2 + " for an indefinite period, the reason is: " + string3 + ".";
            }
            Log.add(string + " banned the chat player " + string2 + " for an indefinite period, the reason: " + string3 + ".", "banchat", player);
            string4 = "You have banned the chat player " + string2 + " for an indefinite period.";
        } else {
            if (!(player == null || player.getPlayerAccess().CanUnBanChat || player2 != null && player2.getNoChannel() == 0L)) {
                return "You do not have the right to change the time of the ban.";
            }
            if (player != null && player.getPlayerAccess().BanChatMaxValue != -1 && n > player.getPlayerAccess().BanChatMaxValue) {
                return "You can ban no more than " + player.getPlayerAccess().BanChatMaxValue + " minutes.";
            }
            if (Config.BANCHAT_ANNOUNCE) {
                string5 = Config.BANCHAT_ANNOUNCE_NICK && string != null && !string.isEmpty() ? string + " banned the chat player " + string2 + " on " + n + " minutes, the reason: " + string3 + "." : "Banned chat to the player " + string2 + " on " + n + " minutes, reason: " + string3 + ".";
            }
            Log.add(string + " banned the chat player " + string2 + " on " + n + " minutes, the reason: " + string3 + ".", "banchat", player);
            string4 = "You have banned the chat player " + string2 + " on " + n + " minutes.";
        }
        if (player2 != null) {
            AdminFunctions.b(player2, n, string3);
        } else {
            AutoBan.ChatBan(string2, n, string3, string);
        }
        if (string5 != null) {
            if (Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
                Announcements.getInstance().announceToAll(string5);
            } else if (player != null) {
                Announcements.shout(player, string5, ChatType.CRITICAL_ANNOUNCE);
            }
        }
        return string4;
    }

    private static void b(Player player, int n, String string) {
        player.updateNoChannel(n * 60000);
        if (n == 0) {
            player.sendMessage(new CustomMessage("common.ChatUnBanned", player, new Object[0]));
        } else if (n > 0) {
            if (string == null || string.isEmpty()) {
                player.sendMessage(new CustomMessage("common.ChatBanned", player, new Object[0]).addNumber(n));
            } else {
                player.sendMessage(new CustomMessage("common.ChatBannedWithReason", player, new Object[0]).addNumber(n).addString(string));
            }
        } else if (string == null || string.isEmpty()) {
            player.sendMessage(new CustomMessage("common.ChatBannedPermanently", player, new Object[0]));
        } else {
            player.sendMessage(new CustomMessage("common.ChatBannedPermanentlyWithReason", player, new Object[0]).addString(string));
        }
    }
}

