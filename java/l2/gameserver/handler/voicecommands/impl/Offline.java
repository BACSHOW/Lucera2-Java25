/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;

public class Offline
extends Functions
implements IVoicedCommandHandler {
    private String[] zF = new String[]{"offline"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.SERVICES_OFFLINE_TRADE_ALLOW) {
            return false;
        }
        if (player.isOlyParticipant() || ParticipantPool.getInstance().isRegistred(player) || player.getKarma() > 0) {
            Offline.show(new CustomMessage("trade.OfflineNoTradeOlyRegOrKarma", player, new Object[0]), player);
            return false;
        }
        if (player.getLevel() < Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL) {
            Offline.show(new CustomMessage("voicedcommandhandlers.Offline.LowLevel", player, new Object[0]).addNumber(Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL), player);
            return false;
        }
        if (!player.isInZone(Zone.ZoneType.offshore) && Config.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE) {
            Offline.show(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore", player, new Object[0]), player);
            return false;
        }
        if (!player.isInStoreMode()) {
            Offline.show(new CustomMessage("voicedcommandhandlers.Offline.IncorrectUse", player, new Object[0]), player);
            return false;
        }
        if (player.getNoChannelRemained() > 0L) {
            Offline.show(new CustomMessage("voicedcommandhandlers.Offline.BanChat", player, new Object[0]), player);
            return false;
        }
        if (player.isActionBlocked("open_private_store")) {
            Offline.show(new CustomMessage("trade.OfflineNoTradeZone", player, new Object[0]), player);
            return false;
        }
        if (Config.SERVICES_OFFLINE_TRADE_PRICE > 0 && Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM > 0) {
            if (Offline.getItemCount(player, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM) < (long)Config.SERVICES_OFFLINE_TRADE_PRICE) {
                Offline.show(new CustomMessage("voicedcommandhandlers.Offline.NotEnough", player, new Object[0]).addItemName(Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM).addNumber(Config.SERVICES_OFFLINE_TRADE_PRICE), player);
                return false;
            }
            Offline.removeItem(player, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM, Config.SERVICES_OFFLINE_TRADE_PRICE);
            Log.service("Offline", player, " bought offline trade time for " + Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM + " amount " + Config.SERVICES_OFFLINE_TRADE_PRICE);
        }
        player.offline();
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }
}

