/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class Banking
extends Functions
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"deposit", "withdraw", "adena", "goldbar", "gb"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.SERVICES_BANKING_ENABLED) {
            return false;
        }
        if ((string = string.intern()).equalsIgnoreCase("deposit") || string.equalsIgnoreCase("goldbar") || string.equalsIgnoreCase("gb")) {
            return this.deposit(string, player, string2);
        }
        if (string.equalsIgnoreCase("withdraw") || string.equalsIgnoreCase("adena")) {
            return this.withdraw(string, player, string2);
        }
        return false;
    }

    public boolean deposit(String string, Player player, String string2) {
        if (Banking.getItemCount(player, Config.SERVICES_DEPOSIT_ITEM_ID_NEEDED) < (long)Config.SERVICES_DEPOSIT_ITEM_COUNT_NEEDED) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }
        long l = player.getInventory().getCountOf(Config.SERVICES_DEPOSIT_ITEM_ID_GIVED);
        if (l + (long)Config.SERVICES_DEPOSIT_ITEM_COUNT_GIVED >= Integer.MAX_VALUE) {
            player.sendMessage(new CustomMessage("services.Banking.DepositUnSuccessfully", player, new Object[0]));
            return false;
        }
        Banking.removeItem(player, Config.SERVICES_DEPOSIT_ITEM_ID_NEEDED, Config.SERVICES_DEPOSIT_ITEM_COUNT_NEEDED);
        player.sendMessage(new CustomMessage("services.Banking.DepositSuccessfully", player, new Object[0]));
        Banking.addItem(player, Config.SERVICES_DEPOSIT_ITEM_ID_GIVED, Config.SERVICES_DEPOSIT_ITEM_COUNT_GIVED);
        Log.service("Banking deposit", player, " exchange on " + Config.SERVICES_DEPOSIT_ITEM_ID_NEEDED + " amount " + Config.SERVICES_DEPOSIT_ITEM_COUNT_NEEDED + " and remove " + Config.SERVICES_DEPOSIT_ITEM_ID_NEEDED + " amount " + Config.SERVICES_DEPOSIT_ITEM_COUNT_NEEDED);
        return true;
    }

    public boolean withdraw(String string, Player player, String string2) {
        if (Banking.getItemCount(player, Config.SERVICES_WITHDRAW_ITEM_ID_NEEDED) < (long)Config.SERVICES_WITHDRAW_ITEM_COUNT_NEEDED) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }
        long l = player.getInventory().getCountOf(Config.SERVICES_WITHDRAW_ITEM_ID_GIVED);
        if (l + (long)Config.SERVICES_WITHDRAW_ITEM_COUNT_GIVED >= Integer.MAX_VALUE) {
            player.sendMessage(new CustomMessage("services.Banking.WithdrawUnsuccessfully", player, new Object[0]));
            return false;
        }
        Banking.removeItem(player, Config.SERVICES_WITHDRAW_ITEM_ID_NEEDED, Config.SERVICES_WITHDRAW_ITEM_COUNT_NEEDED);
        player.sendMessage(new CustomMessage("services.Banking.WithdrawSuccessfully", player, new Object[0]));
        Banking.addItem(player, Config.SERVICES_WITHDRAW_ITEM_ID_GIVED, Config.SERVICES_WITHDRAW_ITEM_COUNT_GIVED);
        Log.service("Banking withdraw", player, " exchange on " + Config.SERVICES_WITHDRAW_ITEM_ID_GIVED + " amount " + Config.SERVICES_WITHDRAW_ITEM_COUNT_GIVED + " and remove " + Config.SERVICES_WITHDRAW_ITEM_ID_NEEDED + " amount " + Config.SERVICES_WITHDRAW_ITEM_COUNT_NEEDED);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }
}

