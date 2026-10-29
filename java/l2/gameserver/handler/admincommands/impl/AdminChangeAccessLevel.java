/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminChangeAccessLevel
implements IAdminCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(AdminChangeAccessLevel.class);

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanGmEdit) {
            return false;
        }
        switch (commands) {
            case admin_changelvl: {
                if (stringArray.length == 2) {
                    int n = Integer.parseInt(stringArray[1]);
                    if (!player.getTarget().isPlayer()) break;
                    ((Player)player.getTarget()).setAccessLevel(n);
                    break;
                }
                if (stringArray.length != 3) break;
                int n = Integer.parseInt(stringArray[2]);
                Player player2 = GameObjectsStorage.getPlayer(stringArray[1]);
                if (player2 == null) break;
                player2.setAccessLevel(n);
                break;
            }
            case admin_moders: {
                AdminChangeAccessLevel.o(player);
                break;
            }
            case admin_moders_add: {
                Object object;
                if (player.getTarget() == null || !player.getTarget().isPlayer()) {
                    player.sendMessage("Incorrect target. Please select a player.");
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                Player player3 = player.getTarget().getPlayer();
                if (Config.gmlist.containsKey(player3.getObjectId())) {
                    player.sendMessage("Error: Moderator " + player3.getName() + " already in server access list.");
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                String string2 = "m" + player3.getObjectId() + ".xml";
                if (!Files.copyFile("config/GMAccess.d/template/moderator.xml", "config/GMAccess.d/" + string2)) {
                    player.sendMessage("Error: Failed to copy access-file.");
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                String string3 = "";
                try {
                    String string4;
                    object = new BufferedReader(new FileReader("config/GMAccess.d/" + string2));
                    while ((string4 = ((BufferedReader)object).readLine()) != null) {
                        string3 = string3 + string4 + "\n";
                    }
                    ((BufferedReader)object).close();
                    string3 = string3.replaceFirst("ObjIdPlayer", "" + player3.getObjectId());
                    Files.writeFile("config/GMAccess.d/" + string2, string3);
                }
                catch (Exception exception) {
                    player.sendMessage("Error: Failed to modify object ID in access-file.");
                    File file = new File("config/GMAccess.d/" + string2);
                    if (file.exists()) {
                        file.delete();
                    }
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                object = new File("config/GMAccess.d/" + string2);
                if (!((File)object).exists()) {
                    player.sendMessage("Error: Failed to read access-file for " + player3.getName());
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                Config.loadGMAccess((File)object);
                player3.setPlayerAccess(Config.gmlist.get(player3.getObjectId()));
                player.sendMessage("Moderator " + player3.getName() + " added.");
                AdminChangeAccessLevel.o(player);
                break;
            }
            case admin_moders_del: {
                if (stringArray.length < 2) {
                    player.sendMessage("Please specify moderator object ID to delete moderator.");
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                int n = Integer.parseInt(stringArray[1]);
                if (!Config.gmlist.containsKey(n)) {
                    player.sendMessage("Error: Moderator with object ID " + n + " not found in server access lits.");
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                Config.gmlist.remove(n);
                Player player4 = GameObjectsStorage.getPlayer(n);
                if (player4 != null) {
                    player4.setPlayerAccess(null);
                }
                String string5 = "m" + n + ".xml";
                File file = new File("config/GMAccess.d/" + string5);
                if (!(file.exists() && file.isFile() && file.delete())) {
                    player.sendMessage("Error: Can't delete access-file: " + string5);
                    AdminChangeAccessLevel.o(player);
                    return false;
                }
                if (player4 != null) {
                    player.sendMessage("Moderator " + player4.getName() + " deleted.");
                } else {
                    player.sendMessage("Moderator with object ID " + n + " deleted.");
                }
                AdminChangeAccessLevel.o(player);
                break;
            }
            case admin_penalty: {
                if (stringArray.length < 2) {
                    player.sendMessage("USAGE: //penalty charName [count] [reason]");
                    return false;
                }
                int n = 1;
                if (stringArray.length > 2) {
                    n = Integer.parseInt(stringArray[2]);
                }
                String string6 = "\u043d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d\u0430";
                if (stringArray.length > 3) {
                    string6 = stringArray[3];
                }
                int n2 = 0;
                Player player5 = GameObjectsStorage.getPlayer(stringArray[1]);
                if (player5 != null && player5.getPlayerAccess().CanBanChat) {
                    n2 = player5.getObjectId();
                    int n3 = 0;
                    String string7 = player5.getVar("penaltyChatCount");
                    if (string7 != null) {
                        n3 = Integer.parseInt(string7);
                    }
                    player5.setVar("penaltyChatCount", "" + (n3 + n), -1L);
                } else {
                    n2 = mysql.simple_get_int("obj_Id", "characters", "`char_name`='" + stringArray[1] + "'");
                    if (n2 > 0) {
                        Integer n4 = (Integer)mysql.get("SELECT `value` FROM character_variables WHERE `obj_id` = " + n2 + " AND `name` = 'penaltyChatCount'");
                        mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (" + n2 + ",'user-var','penaltyChatCount','" + (n4 + n) + "',-1)");
                    }
                }
                if (n2 <= 0) break;
                if (Config.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
                    Announcements.getInstance().announceToAll(player + " \u043e\u0448\u0442\u0440\u0430\u0444\u043e\u0432\u0430\u043b \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440\u0430 " + stringArray[1] + " \u043d\u0430 " + n + ", \u043f\u0440\u0438\u0447\u0438\u043d\u0430: " + string6 + ".");
                    break;
                }
                Announcements.shout(player, player + " \u043e\u0448\u0442\u0440\u0430\u0444\u043e\u0432\u0430\u043b \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440\u0430 " + stringArray[1] + " \u043d\u0430 " + n + ", \u043f\u0440\u0438\u0447\u0438\u043d\u0430: " + string6 + ".", ChatType.CRITICAL_ANNOUNCE);
            }
        }
        return true;
    }

    private static void o(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        String string = "Moderators managment panel.<br>";
        File file = new File("config/GMAccess.d/");
        if (!file.exists() || !file.isDirectory()) {
            string = string + "Error: Can't open permissions folder.";
            npcHtmlMessage.setHtml(string);
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        string = string + "<p align=right>";
        string = string + "<button width=120 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h admin_moders_add\" value=\"Add modrator\">";
        string = string + "</p><br>";
        string = string + "<center><font color=LEVEL>Moderators:</font></center>";
        string = string + "<table width=285>";
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory() || !file2.getName().startsWith("m") || !file2.getName().endsWith(".xml")) continue;
            int n = Integer.parseInt(file2.getName().substring(1, 10));
            String string2 = AdminChangeAccessLevel.s(n);
            boolean bl = false;
            if (string2 == null || string2.isEmpty()) {
                string2 = "" + n;
            } else {
                bl = GameObjectsStorage.getPlayer(string2) != null;
            }
            string = string + "<tr>";
            string = string + "<td width=140>" + string2;
            string = string + (bl ? " <font color=\"33CC66\">(on)</font>" : "");
            string = string + "</td>";
            string = string + "<td width=45><button width=50 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h admin_moders_log " + n + "\" value=\"Logs\"></td>";
            string = string + "<td width=45><button width=20 height=20 back=\"sek.cbui94\" fore=\"sek.cbui94\" action=\"bypass -h admin_moders_del " + n + "\" value=\"X\"></td>";
            string = string + "</tr>";
        }
        string = string + "</table>";
        npcHtmlMessage.setHtml(string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String s(int n) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        String string;
        block4: {
            string = null;
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT `char_name` FROM `characters` WHERE `obj_Id`=\"" + n + "\" LIMIT 1");
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                string = resultSet.getString(1);
            }
            catch (Exception exception) {
                try {
                    _log.warn("SQL Error: " + exception);
                    _log.error("", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return string;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_changelvl,
        admin_moders,
        admin_moders_add,
        admin_moders_del,
        admin_penalty;

    }
}

