/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;

public class Services
extends Functions
implements IVoicedCommandHandler {
    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_SERVICES_COMMAND) {
            return false;
        }
        if ((string = string.intern()).startsWith("autoloot") && string2 != null && string2.length() > 1) {
            this.autoLoot(player, string2.startsWith("on"), string2.startsWith("adena"));
            return true;
        }
        if (string.startsWith("xpfreez")) {
            if (string2.startsWith("on")) {
                if (Config.XP_FREEZ_ONLY_FOR_PREMIUM && !player.hasBonus()) {
                    player.sendMessage(new CustomMessage("usercommandhandlers.XpFreezFroPAOnly", player, new Object[0]));
                    return false;
                }
                player.setVar("NoExp", "1", -1L);
                player.sendMessage(new CustomMessage("usercommandhandlers.ExpFreezed", player, new Object[0]));
                return true;
            }
            if (string2.startsWith("off")) {
                player.unsetVar("NoExp");
                player.sendMessage(new CustomMessage("usercommandhandlers.ExpNormal", player, new Object[0]));
                return true;
            }
        } else {
            if (string.startsWith(Config.ALT_CHANGE_LANG_NAME_2) && Config.ALT_ALLOW_LANG_COMMAND) {
                player.setVar("lang@", "ru", -1L);
                player.sendMessage(new CustomMessage("usercommandhandlers.LangRu", player, new Object[0]));
                return true;
            }
            if (string.startsWith(Config.ALT_CHANGE_LANG_NAME_1) && Config.ALT_ALLOW_LANG_COMMAND) {
                player.setVar("lang@", "en", -1L);
                player.sendMessage(new CustomMessage("usercommandhandlers.LangEn", player, new Object[0]));
                return true;
            }
        }
        return false;
    }

    public void autoLoot(Player player, boolean bl, boolean bl2) {
        if (bl && !bl2) {
            if (Config.AUTO_LOOT_ONLY_FOR_PREMIUM && !player.hasBonus()) {
                player.sendMessage(new CustomMessage("usercommandhandlers.AutolootForPAOnly", player, new Object[0]));
                return;
            }
            player.setAutoLoot(bl);
            if (Config.AUTO_LOOT_HERBS) {
                player.setAutoLootHerbs(bl);
            }
            player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAll", player, new Object[0]));
        } else if (bl2) {
            player.setAutoLoot(false);
            if (Config.AUTO_LOOT_ONLY_FOR_PREMIUM && !player.hasBonus()) {
                player.sendMessage(new CustomMessage("usercommandhandlers.AutolootForPAOnly", player, new Object[0]));
                return;
            }
            player.setAutoLootAdena(bl2);
            player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAdena", player, new Object[0]));
        } else {
            player.setAutoLootAdena(false);
            player.setAutoLoot(false);
            player.setAutoLootHerbs(false);
            player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootOff", player, new Object[0]));
        }
    }

    @Override
    public String[] getVoicedCommandList() {
        return new String[]{"autoloot", "xpfreez", Config.ALT_CHANGE_LANG_NAME_1, Config.ALT_CHANGE_LANG_NAME_2};
    }
}

