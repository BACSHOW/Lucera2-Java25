/*
 * Decompiled with CFR 0.152.
 */
package services.community;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Friend;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageFriends
implements ICommunityBoardHandler,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(ManageFriends.class);

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Manage Friends service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_friendlist_", "_friendblocklist_", "_frienddelete_", "_frienddeleteallconfirm_", "_frienddeleteall_", "_friendblockdelete_", "_friendblockadd_", "_friendblockdeleteallconfirm_", "_friendblockdeleteall_"};
    }

    @Override
    public void onBypassCommand(Player player, String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "_");
        String string2 = stringTokenizer.nextToken();
        String string3 = HtmCache.getInstance().getNotNull(string2.startsWith("friendbloc") ? "scripts/services/community/bbs_block_list.htm" : "scripts/services/community/bbs_friend_list.htm", player);
        player.setSessionVar("add_fav", null);
        if (string2.equals("friendlist")) {
            String string4 = stringTokenizer.nextToken();
            string3 = string3.replace("%friend_list%", ManageFriends.aF(player));
            if (string4.equals("0")) {
                if (player.getSessionVar("selFriends") != null) {
                    player.setSessionVar("selFriends", null);
                }
                string3 = string3.replace("%selected_friend_list%", "");
                string3 = string3.replace("%delete_all_msg%", "");
            } else if (string4.equals("1")) {
                String string5 = stringTokenizer.nextToken();
                String string6 = player.getSessionVar("selFriends");
                if (string6 == null) {
                    string6 = string5 + ";";
                } else if (!string6.contains(string5)) {
                    string6 = string6 + string5 + ";";
                }
                player.setSessionVar("selFriends", string6);
                string3 = string3.replace("%selected_friend_list%", ManageFriends.aG(player));
                string3 = string3.replace("%delete_all_msg%", "");
            } else if (string4.equals("2")) {
                String string7 = stringTokenizer.nextToken();
                String string8 = player.getSessionVar("selFriends");
                if (string8 != null) {
                    string8 = string8.replace(string7 + ";", "");
                    player.setSessionVar("selFriends", string8);
                }
                string3 = string3.replace("%selected_friend_list%", ManageFriends.aG(player));
                string3 = string3.replace("%delete_all_msg%", "");
            }
        } else if (string2.equals("frienddeleteallconfirm")) {
            string3 = string3.replace("%friend_list%", ManageFriends.aF(player));
            string3 = string3.replace("%selected_friend_list%", ManageFriends.aG(player));
            string3 = string3.replace("%delete_all_msg%", "<br>\nAre you sure you want to delete all friends from the friends list? <button value = \"OK\" action=\"bypass _frienddeleteall_\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\">");
        } else if (string2.equals("frienddelete")) {
            String string9 = player.getSessionVar("selFriends");
            if (string9 != null) {
                for (String string10 : string9.split(";")) {
                    if (string10.isEmpty()) continue;
                    player.getFriendList().removeFriend(player.getFriendList().getList().get(Integer.parseInt(string10)).getName());
                }
            }
            player.setSessionVar("selFriends", null);
            string3 = string3.replace("%friend_list%", ManageFriends.aF(player));
            string3 = string3.replace("%selected_friend_list%", "");
            string3 = string3.replace("%delete_all_msg%", "");
        } else if (string2.equals("frienddeleteall")) {
            ArrayList<Friend> arrayList = new ArrayList<Friend>(1);
            arrayList.addAll(player.getFriendList().getList().values());
            for (Friend friend : arrayList) {
                player.getFriendList().removeFriend(friend.getName());
            }
            player.setSessionVar("selFriends", null);
            string3 = string3.replace("%friend_list%", "");
            string3 = string3.replace("%selected_friend_list%", "");
            string3 = string3.replace("%delete_all_msg%", "");
        } else if (string2.equals("friendblocklist")) {
            string3 = string3.replace("%block_list%", ManageFriends.aH(player));
            string3 = string3.replace("%delete_all_msg%", "");
        } else if (string2.equals("friendblockdeleteallconfirm")) {
            string3 = string3.replace("%block_list%", ManageFriends.aH(player));
            string3 = string3.replace("%delete_all_msg%", "<br>\nDo you want to delete all characters from the block list? <button value = \"OK\" action=\"bypass _friendblockdeleteall_\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\" >");
        } else if (string2.equals("friendblockdelete")) {
            String string11 = stringTokenizer.nextToken();
            if (string11 != null && !string11.isEmpty()) {
                int n = Integer.parseInt(string11);
                String string12 = player.getBlockListMap().get(n);
                if (string12 != null) {
                    player.removeFromBlockList(string12);
                }
            }
            string3 = string3.replace("%block_list%", ManageFriends.aH(player));
            string3 = string3.replace("%delete_all_msg%", "");
        } else if (string2.equals("friendblockdeleteall")) {
            ArrayList<String> arrayList = new ArrayList<String>(1);
            arrayList.addAll(player.getBlockList());
            for (String string13 : arrayList) {
                player.removeFromBlockList(string13);
            }
            string3 = string3.replace("%block_list%", "");
            string3 = string3.replace("%delete_all_msg%", "");
        }
        ShowBoard.separateAndSend(string3, player);
    }

    @Override
    public void onWriteCommand(Player player, String string, String string2, String string3, String string4, String string5, String string6) {
        String string7 = HtmCache.getInstance().getNotNull("scripts/services/community/bbs_block_list.htm", player);
        if ("_friendblockadd_".equals(string) && string4 != null && !string4.isEmpty()) {
            player.addToBlockList(string4);
        }
        string7 = string7.replace("%block_list%", ManageFriends.aH(player));
        string7 = string7.replace("%delete_all_msg%", "");
        ShowBoard.separateAndSend(string7, player);
    }

    private static String aF(Player player) {
        StringBuilder stringBuilder = new StringBuilder("");
        Map<Integer, Friend> map = player.getFriendList().getList();
        for (Map.Entry<Integer, Friend> entry : map.entrySet()) {
            stringBuilder.append("<a action=\"bypass _friendlist_1_").append(entry.getKey()).append("\">").append(entry.getValue().getName()).append("</a> (").append(entry.getValue().isOnline() ? "on" : "off").append(") &nbsp;");
        }
        return stringBuilder.toString();
    }

    private static String aG(Player player) {
        String string = player.getSessionVar("selFriends");
        if (string == null) {
            return "";
        }
        String[] stringArray = string.split(";");
        StringBuilder stringBuilder = new StringBuilder("");
        for (String string2 : stringArray) {
            if (string2.isEmpty()) continue;
            stringBuilder.append("<a action=\"bypass _friendlist_2_").append(string2).append("\">").append(player.getFriendList().getList().get(Integer.parseInt(string2)).getName()).append("</a>;");
        }
        return stringBuilder.toString();
    }

    private static String aH(Player player) {
        StringBuilder stringBuilder = new StringBuilder("");
        Map<Integer, String> map = player.getBlockListMap();
        for (Integer n : map.keySet()) {
            stringBuilder.append(map.get(n)).append("&nbsp; <a action=\"bypass _friendblockdelete_").append(n).append("\">Delete</a>&nbsp;&nbsp;");
        }
        return stringBuilder.toString();
    }
}

