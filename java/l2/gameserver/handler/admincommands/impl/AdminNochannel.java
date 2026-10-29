/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.AdminFunctions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;

public class AdminNochannel
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        String string2;
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanBanChat) {
            return false;
        }
        int n = 0;
        int n2 = 0;
        int n3 = player.getPlayerAccess().BanChatCountPerDay;
        if (n3 > 0) {
            String string3 = player.getVar("banChatCount");
            if (string3 != null) {
                n = Integer.parseInt(string3);
            }
            if ((string2 = player.getVar("penaltyChatCount")) != null) {
                n2 = Integer.parseInt(string2);
            }
            long l = 0L;
            String string4 = player.getVar("LastBanChatDayTime");
            if (string4 != null) {
                l = Long.parseLong(string4);
            }
            if (l != 0L) {
                if (System.currentTimeMillis() - l < 86400000L) {
                    if (n >= n3) {
                        player.sendMessage("\u0412 \u0441\u0443\u0442\u043a\u0438, \u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u044b\u0434\u0430\u0442\u044c \u043d\u0435 \u0431\u043e\u043b\u0435\u0435 " + n + " \u0431\u0430\u043d\u043e\u0432 \u0447\u0430\u0442\u0430.");
                        return false;
                    }
                } else {
                    int n4 = n / 10;
                    n4 = Math.max(1, n4);
                    n4 = 1;
                    if (player.getPlayerAccess().BanChatBonusId > 0 && player.getPlayerAccess().BanChatBonusCount > 0) {
                        int n5 = player.getPlayerAccess().BanChatBonusCount * n4;
                        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(player.getPlayerAccess().BanChatBonusId);
                        player.sendMessage("\u0411\u043e\u043d\u0443\u0441 \u0437\u0430 \u043c\u043e\u0434\u0435\u0440\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435: " + n5 + " " + itemTemplate.getName());
                        if (n2 > 0) {
                            player.sendMessage("\u0428\u0442\u0440\u0430\u0444 \u0437\u0430 \u043d\u0430\u0440\u0443\u0448\u0435\u043d\u0438\u044f: " + n2 + " " + itemTemplate.getName());
                            player.setVar("penaltyChatCount", "" + Math.max(0, n2 - n5), -1L);
                            n5 -= n2;
                        }
                        if (n5 > 0) {
                            ItemFunctions.addItem((Playable)player, player.getPlayerAccess().BanChatBonusId, (long)n5, true);
                        }
                    }
                    player.setVar("LastBanChatDayTime", "" + System.currentTimeMillis(), -1L);
                    player.setVar("banChatCount", "0", -1L);
                    n = 0;
                }
            } else {
                player.setVar("LastBanChatDayTime", "" + System.currentTimeMillis(), -1L);
            }
        }
        switch (commands) {
            case admin_nochannel: 
            case admin_nc: {
                if (stringArray.length < 2) {
                    player.sendMessage("USAGE: //nochannel charName [period] [reason]");
                    return false;
                }
                int n6 = 30;
                if (stringArray.length > 2) {
                    try {
                        n6 = Integer.parseInt(stringArray[2]);
                    }
                    catch (Exception exception) {
                        n6 = 30;
                    }
                }
                string2 = AdminFunctions.banChat(player, null, stringArray[1], n6, stringArray.length > 3 ? Util.joinStrings(" ", stringArray, 3) : null);
                player.sendMessage(string2);
                if (n3 <= -1 || !string2.startsWith("\u0412\u044b \u0437\u0430\u0431\u0430\u043d\u0438\u043b\u0438 \u0447\u0430\u0442")) break;
                player.setVar("banChatCount", "" + ++n, -1L);
                player.sendMessage("\u0423 \u0432\u0430\u0441 \u043e\u0441\u0442\u0430\u043b\u043e\u0441\u044c " + (n3 - n) + " \u0431\u0430\u043d\u043e\u0432 \u0447\u0430\u0442\u0430.");
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_nochannel,
        admin_nc;

    }
}

