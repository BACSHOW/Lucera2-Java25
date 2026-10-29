/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.AdminFunctions;
import l2.gameserver.utils.Location;

public class AdminMenu
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        StringTokenizer stringTokenizer;
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        if (string.startsWith("admin_teleport_character_to_menu")) {
            String string2;
            Player player2;
            String[] stringArray2 = string.split(" ");
            if (stringArray2.length == 5 && (player2 = World.getPlayer(string2 = stringArray2[1])) != null) {
                this.a(player2, new Location(Integer.parseInt(stringArray2[2]), Integer.parseInt(stringArray2[3]), Integer.parseInt(stringArray2[4])), player);
            }
        } else if (string.startsWith("admin_recall_char_menu")) {
            try {
                String string3 = string.substring(23);
                Player player3 = World.getPlayer(string3);
                this.a(player3, player.getLoc(), player);
            }
            catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
        } else if (string.startsWith("admin_goto_char_menu")) {
            try {
                String string4 = string.substring(21);
                Player player4 = World.getPlayer(string4);
                this.a(player, player4);
            }
            catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
        } else if (string.equals("admin_kill_menu")) {
            Object object;
            GameObject gameObject = player.getTarget();
            StringTokenizer stringTokenizer2 = new StringTokenizer(string);
            if (stringTokenizer2.countTokens() > 1) {
                stringTokenizer2.nextToken();
                object = stringTokenizer2.nextToken();
                Player player5 = World.getPlayer((String)object);
                if (player5 == null) {
                    player.sendMessage("Player " + (String)object + " not found in game.");
                }
                gameObject = player5;
            }
            if (gameObject != null && gameObject.isCreature()) {
                object = (Creature)gameObject;
                ((Creature)object).reduceCurrentHp(((Creature)object).getMaxHp() + 1, player, null, true, true, true, false, false, false, true);
            } else {
                player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            }
        } else if (string.startsWith("admin_kick_menu") && (stringTokenizer = new StringTokenizer(string)).countTokens() > 1) {
            stringTokenizer.nextToken();
            String string5 = stringTokenizer.nextToken();
            if (AdminFunctions.kick(string5, "kick")) {
                player.sendMessage("Player kicked.");
            }
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/charmanage.htm"));
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void a(Player player, Location location, Player player2) {
        if (player != null) {
            player.sendMessage("Admin is teleporting you.");
            player.teleToLocation(location);
        }
    }

    private void a(Player player, GameObject gameObject) {
        if (gameObject == null || !gameObject.isPlayer()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        if (player2.getObjectId() == player.getObjectId()) {
            player.sendMessage("You cannot self teleport.");
        } else {
            player.teleToLocation(player2.getLoc());
            player.sendMessage("You have teleported to character " + player2.getName() + ".");
        }
    }

    private static enum Commands {
        admin_char_manage,
        admin_teleport_character_to_menu,
        admin_recall_char_menu,
        admin_goto_char_menu,
        admin_kick_menu,
        admin_kill_menu,
        admin_ban_menu,
        admin_unban_menu;

    }
}

