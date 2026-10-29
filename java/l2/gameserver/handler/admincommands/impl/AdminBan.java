/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import l2.commons.geometry.Point2D;
import l2.commons.geometry.Point3D;
import l2.commons.net.nio.impl.MMOClient;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import l2.gameserver.network.l2.CGModule;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.AutoBan;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

public class AdminBan
implements IAdminCommandHandler {
    private static final String yh = "jailed";

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Object object;
        long l;
        long l2;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Commands commands = (Commands)enum_;
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        if (player.getPlayerAccess().CanTradeBanUnban) {
            switch (commands) {
                case admin_trade_ban: {
                    return this.a(stringTokenizer, player);
                }
                case admin_trade_unban: {
                    return this.b(stringTokenizer, player);
                }
            }
        }
        if (player.getPlayerAccess().CanJail) {
            switch (commands) {
                case admin_jail: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = stringTokenizer.nextToken();
                        object3 = stringTokenizer.nextToken();
                        object2 = World.getPlayer((String)object5);
                        if (object2 != null) {
                            l2 = System.currentTimeMillis();
                            l = TimeUnit.MINUTES.toMillis(Long.parseLong((String)object4));
                            object = ((GameObject)object2).getReflection() == ReflectionManager.DEFAULT ? new Location(((GameObject)object2).getX(), ((GameObject)object2).getY(), ((GameObject)object2).getZ()) : new Location(((GameObject)object2).getX(), ((GameObject)object2).getY(), ((GameObject)object2).getZ(), ((GameObject)object2).getReflectionId());
                            ((Player)object2).setVar(yh, String.format("%d;%s", l2 + l, ((Location)object).toXYZHString()), -1L);
                            ((Player)object2).startUnjailTask((Player)object2, (int)l);
                            if (player.isInStoreMode()) {
                                player.setPrivateStoreType(0);
                            }
                            ((Creature)object2).teleToLocation(Location.findPointToStay((GameObject)object2, Config.SERVICE_JAIL_COORDINATES, 50, 200), ReflectionManager.JAIL);
                            ((Player)object2).sitDown(null);
                            ((Creature)object2).block();
                            ((Player)object2).sendMessage("You moved to jail, time to escape - " + TimeUnit.MILLISECONDS.toMinutes(l) + " minutes, reason - " + (String)object3 + " .");
                            player.sendMessage("You jailed " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //jail char_name period reason");
                    }
                    break;
                }
                case admin_unjail: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = World.getPlayer((String)object5);
                        if (object4 != null && (object3 = ((Player)object4).getVar(yh)) != null && !((String)object3).isEmpty()) {
                            object2 = Location.parseLoc(((String)object3).substring(((String)object3).indexOf(59) + 1));
                            Reflection reflection = null;
                            if (((Location)object2).getH() != 0) {
                                reflection = ReflectionManager.getInstance().get(((Location)object2).getH());
                            }
                            ((Creature)object4).unblock();
                            ((Player)object4).standUp();
                            if (reflection != null) {
                                if (reflection.isCollapseStarted()) {
                                    ((Player)object4).teleToClosestTown();
                                } else {
                                    ((Creature)object4).teleToLocation(((Point2D)object2).getX(), ((Point2D)object2).getY(), ((Point3D)object2).getZ(), reflection);
                                }
                            } else {
                                ((Creature)object4).teleToLocation(((Point2D)object2).getX(), ((Point2D)object2).getY(), ((Point3D)object2).getZ(), ReflectionManager.DEFAULT);
                            }
                            ((Player)object4).stopUnjailTask();
                            ((Player)object4).unsetVar(yh);
                            player.sendMessage("You unjailed " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                        break;
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //unjail char_name");
                    }
                }
            }
        }
        if (player.getPlayerAccess().CanBan) {
            switch (commands) {
                case admin_ban: {
                    this.c(stringTokenizer, player);
                    break;
                }
                case admin_unban: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        if (AutoBan.Banned((String)object5, 0, 0, "", player.getName())) {
                            player.sendMessage("You unban for " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //unban char_name");
                    }
                    break;
                }
                case admin_accban: {
                    Player player2;
                    stringTokenizer.nextToken();
                    int n = 0;
                    int n2 = 0;
                    object3 = stringTokenizer.nextToken();
                    if (stringTokenizer.hasMoreTokens()) {
                        n2 = (int)(System.currentTimeMillis() / 1000L) + Integer.parseInt(stringTokenizer.nextToken()) * 60;
                    } else {
                        n = -100;
                    }
                    AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel((String)object3, n, n2));
                    object2 = AuthServerCommunication.getInstance().getAuthedClient((String)object3);
                    if (object2 == null || (player2 = ((GameClient)object2).getActiveChar()) == null) break;
                    player2.kick();
                    player.sendMessage("Player " + player2.getName() + " kicked.");
                    break;
                }
                case admin_accunban: {
                    stringTokenizer.nextToken();
                    object5 = stringTokenizer.nextToken();
                    AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel((String)object5, 0, 0));
                    break;
                }
                case admin_trade_ban: {
                    return this.a(stringTokenizer, player);
                }
                case admin_trade_unban: {
                    return this.b(stringTokenizer, player);
                }
                case admin_hwidban: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = World.getPlayer((String)object5);
                        object3 = null;
                        object2 = stringTokenizer.nextToken();
                        GameClient gameClient = null;
                        if (object4 != null) {
                            gameClient = ((Player)object4).getNetConnection();
                            if (gameClient != null && gameClient.isConnected()) {
                                object3 = ((Player)object4).getNetConnection().getHwid();
                            }
                        } else {
                            object3 = object5;
                        }
                        if (object3 != null && !((String)object3).isEmpty()) {
                            CGModule.getInstance().addHWIDBan(gameClient, (String)object3, (String)object2);
                            LinkedList<Player> linkedList = new LinkedList<Player>();
                            for (Player player3 : GameObjectsStorage.getAllPlayersForIterate()) {
                                if (player3 == null || (object = player3.getNetConnection()) == null || !((MMOClient)object).isConnected() || !((String)object3).equals(((GameClient)object).getHwid())) continue;
                                linkedList.add(player3);
                            }
                            for (Player player3 : linkedList) {
                                player3.kick();
                                player.sendMessage("Player " + player3.getName() + " kicked.");
                            }
                            player.sendMessage("You baned hwid \"" + (String)object3 + "\".");
                            break;
                        }
                        player.sendMessage("HWID or player \"" + (String)object3 + "\" not found.");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //hwidban [char_name|hwid] comment");
                    }
                    break;
                }
                case admin_chatban: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = stringTokenizer.nextToken();
                        object3 = "admin_chatban " + (String)object5 + " " + (String)object4 + " ";
                        object2 = string.substring(((String)object3).length(), string.length());
                        if (AutoBan.ChatBan((String)object5, Integer.parseInt((String)object4), (String)object2, player.getName())) {
                            player.sendMessage("You ban chat for " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //chatban char_name period reason");
                    }
                    break;
                }
                case admin_nospam: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = stringTokenizer.nextToken();
                        if (AutoBan.noSpam((String)object5, Integer.parseInt((String)object4))) {
                            player.sendMessage("You ban spam chat for " + (String)object5 + " " + (String)object4 + " min");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //nospam char_name period");
                    }
                    break;
                }
                case admin_unblockspam: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        if (AutoBan.noSpam((String)object5, 0)) {
                            player.sendMessage("You unban chat spammer for " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //unblockspam char_name");
                    }
                    break;
                }
                case admin_chatunban: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        if (AutoBan.ChatUnBan((String)object5, player.getName())) {
                            player.sendMessage("You unban chat for " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //chatunban char_name");
                    }
                    break;
                }
                case admin_jail: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = stringTokenizer.nextToken();
                        object3 = stringTokenizer.nextToken();
                        object2 = World.getPlayer((String)object5);
                        if (object2 != null) {
                            l2 = System.currentTimeMillis();
                            l = TimeUnit.MINUTES.toMillis(Long.parseLong((String)object4));
                            object = ((GameObject)object2).getReflection() == ReflectionManager.DEFAULT ? new Location(((GameObject)object2).getX(), ((GameObject)object2).getY(), ((GameObject)object2).getZ()) : new Location(((GameObject)object2).getX(), ((GameObject)object2).getY(), ((GameObject)object2).getZ(), ((GameObject)object2).getReflectionId());
                            ((Player)object2).setVar(yh, String.format("%d;%s", l2 + l, ((Location)object).toXYZHString()), -1L);
                            ((Player)object2).startUnjailTask((Player)object2, (int)l);
                            if (player.isInStoreMode()) {
                                player.setPrivateStoreType(0);
                            }
                            ((Creature)object2).teleToLocation(Location.findPointToStay((GameObject)object2, Config.SERVICE_JAIL_COORDINATES, 50, 200), ReflectionManager.JAIL);
                            ((Player)object2).sitDown(null);
                            ((Creature)object2).block();
                            ((Player)object2).sendMessage("You moved to jail, time to escape - " + TimeUnit.MILLISECONDS.toMinutes(l) + " minutes, reason - " + (String)object3 + " .");
                            player.sendMessage("You jailed " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //jail char_name period reason");
                    }
                    break;
                }
                case admin_unjail: {
                    try {
                        stringTokenizer.nextToken();
                        object5 = stringTokenizer.nextToken();
                        object4 = World.getPlayer((String)object5);
                        if (object4 != null && (object3 = ((Player)object4).getVar(yh)) != null && !((String)object3).isEmpty()) {
                            object2 = Location.parseLoc(((String)object3).substring(((String)object3).indexOf(59) + 1));
                            Reflection reflection = null;
                            if (((Location)object2).getH() != 0) {
                                reflection = ReflectionManager.getInstance().get(((Location)object2).getH());
                            }
                            ((Creature)object4).unblock();
                            ((Player)object4).standUp();
                            if (reflection != null) {
                                if (reflection.isCollapseStarted()) {
                                    ((Player)object4).teleToClosestTown();
                                } else {
                                    ((Creature)object4).teleToLocation(((Point2D)object2).getX(), ((Point2D)object2).getY(), ((Point3D)object2).getZ(), reflection);
                                }
                            } else {
                                ((Creature)object4).teleToLocation(((Point2D)object2).getX(), ((Point2D)object2).getY(), ((Point3D)object2).getZ(), ReflectionManager.DEFAULT);
                            }
                            ((Player)object4).stopUnjailTask();
                            ((Player)object4).unsetVar(yh);
                            player.sendMessage("You unjailed " + (String)object5 + ".");
                            break;
                        }
                        player.sendMessage("Can't find char " + (String)object5 + ".");
                    }
                    catch (Exception exception) {
                        player.sendMessage("Command syntax: //unjail char_name");
                    }
                    break;
                }
                case admin_cban: {
                    player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/cban.htm"));
                    break;
                }
                case admin_permaban: {
                    if (player.getTarget() == null || !player.getTarget().isPlayer()) {
                        Functions.sendDebugMessage(player, "Target should be set and be a player instance");
                        return false;
                    }
                    object5 = player.getTarget().getPlayer();
                    object4 = ((Player)object5).getAccountName();
                    AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel((String)object4, -100, 0));
                    if (((Player)object5).isInOfflineMode()) {
                        ((Player)object5).setOfflineMode(false);
                    }
                    ((Player)object5).kick();
                    Functions.sendDebugMessage(player, "Player account " + (String)object4 + " is banned, player " + ((Creature)object5).getName() + " kicked.");
                }
            }
        }
        return true;
    }

    private boolean a(StringTokenizer stringTokenizer, Player player) {
        if (player.getTarget() == null || !player.getTarget().isPlayer()) {
            return false;
        }
        stringTokenizer.nextToken();
        Player player2 = (Player)player.getTarget();
        long l = -1L;
        long l2 = -1L;
        if (stringTokenizer.hasMoreTokens()) {
            l = Long.parseLong(stringTokenizer.nextToken());
            l2 = l * 24L * 60L * 60L * 1000L + System.currentTimeMillis();
        }
        player2.setVar("tradeBan", String.valueOf(l2), -1L);
        String string = player.getName() + " \u0437\u0430\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0443 " + player2.getName() + (l == -1L ? " \u043d\u0430 \u0431\u0435\u0441\u0441\u0440\u043e\u0447\u043d\u044b\u0439 \u043f\u0435\u0440\u0438\u043e\u0434." : " \u043d\u0430 " + l + " \u0434\u043d\u0435\u0439.");
        Log.add(player2.getName() + ":" + l + AdminBan.d(player2, player2.getPrivateStoreType()), "tradeBan", player);
        if (player2.isInOfflineMode()) {
            player2.setOfflineMode(false);
            player2.kick();
        } else if (player2.isInStoreMode()) {
            player2.setPrivateStoreType(0);
            player2.standUp();
            player2.broadcastCharInfo();
            player2.getBuyList().clear();
        }
        if (Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
            Announcements.getInstance().announceToAll(string);
        } else {
            Announcements.shout(player, string, ChatType.CRITICAL_ANNOUNCE);
        }
        return true;
    }

    private static String d(Player player, int n) {
        switch (n) {
            case 3: {
                List<TradeItem> list = player.getBuyList();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                String string = ":buy:";
                for (TradeItem tradeItem : list) {
                    string = string + tradeItem.getItemId() + ";" + tradeItem.getCount() + ";" + tradeItem.getOwnersPrice() + ":";
                }
                return string;
            }
            case 1: 
            case 8: {
                List<TradeItem> list = player.getSellList();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                String string = ":sell:";
                for (TradeItem tradeItem : list) {
                    string = string + tradeItem.getItemId() + ";" + tradeItem.getCount() + ";" + tradeItem.getOwnersPrice() + ":";
                }
                return string;
            }
            case 5: {
                List<ManufactureItem> list = player.getCreateList();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                String string = ":mf:";
                for (ManufactureItem manufactureItem : list) {
                    string = string + manufactureItem.getRecipeId() + ";" + manufactureItem.getCost() + ":";
                }
                return string;
            }
        }
        return "";
    }

    private boolean b(StringTokenizer stringTokenizer, Player player) {
        if (player.getTarget() == null || !player.getTarget().isPlayer()) {
            return false;
        }
        Player player2 = (Player)player.getTarget();
        player2.unsetVar("tradeBan");
        if (Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
            Announcements.getInstance().announceToAll(player.getName() + " \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0443 " + player2.getName() + ".");
        } else {
            Announcements.shout(player, player.getName() + " \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0443 " + player2.getName() + ".", ChatType.CRITICAL_ANNOUNCE);
        }
        Log.add(player + " \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u043b \u0442\u043e\u0440\u0433\u043e\u0432\u043b\u044e \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0443 " + player2 + ".", "tradeBan", player);
        return true;
    }

    private boolean c(StringTokenizer stringTokenizer, Player player) {
        try {
            Player player2;
            stringTokenizer.nextToken();
            String string = stringTokenizer.nextToken();
            int n = 0;
            String string2 = "";
            if (stringTokenizer.hasMoreTokens()) {
                n = Integer.parseInt(stringTokenizer.nextToken());
            }
            if (stringTokenizer.hasMoreTokens()) {
                string2 = "admin_ban " + string + " " + n + " ";
                while (stringTokenizer.hasMoreTokens()) {
                    string2 = string2 + stringTokenizer.nextToken() + " ";
                }
                string2.trim();
            }
            if ((player2 = World.getPlayer(string)) != null) {
                player2.sendMessage(new CustomMessage("admincommandhandlers.YoureBannedByGM", player2, new Object[0]));
                player2.setAccessLevel(-100);
                AutoBan.Banned(player2, n, string2, player.getName());
                player2.kick();
                player.sendMessage("You banned " + player2.getName());
            } else if (AutoBan.Banned(string, -100, n, string2, player.getName())) {
                player.sendMessage("You banned " + string);
            } else {
                player.sendMessage("Can't find char: " + string);
            }
        }
        catch (Exception exception) {
            player.sendMessage("Command syntax: //ban char_name days reason");
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_ban,
        admin_unban,
        admin_hwidban,
        admin_cban,
        admin_chatban,
        admin_nospam,
        admin_unblockspam,
        admin_chatunban,
        admin_accban,
        admin_accunban,
        admin_trade_ban,
        admin_trade_unban,
        admin_jail,
        admin_unjail,
        admin_permaban;

    }
}

