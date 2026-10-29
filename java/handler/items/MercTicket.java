/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import java.util.List;
import l2.gameserver.dao.CastleHiredGuardDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Privilege;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.PositionUtils;
import org.napile.primitive.sets.impl.HashIntSet;

public class MercTicket
extends ScriptItemHandler {
    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        return false;
    }

    @Override
    public void dropItem(Player player, ItemInstance itemInstance, long l, Location location) {
        if (!player.hasPrivilege(Privilege.CS_FS_MERCENARIES) || player.getClan().getCastle() == 0) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES, ActionFail.STATIC);
            return;
        }
        Castle castle = player.getCastle();
        MerchantGuard merchantGuard = castle.getMerchantGuard(itemInstance.getItemId());
        if (merchantGuard == null || !castle.checkIfInZone(location, ReflectionManager.DEFAULT) || player.isActionBlocked("drop_merchant_guard")) {
            player.sendPacket(SystemMsg.YOU_CANNOT_POSITION_MERCENARIES_HERE, ActionFail.STATIC);
            return;
        }
        if (((SiegeEvent)castle.getSiegeEvent()).isInProgress() || !merchantGuard.isValidSSQPeriod()) {
            player.sendPacket(SystemMsg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS, ActionFail.STATIC);
            return;
        }
        int n = 0;
        for (ItemInstance itemInstance2 : castle.getSpawnMerchantTickets()) {
            if (PositionUtils.getDistance(itemInstance2.getLoc(), location) < 200.0) {
                player.sendPacket(SystemMsg.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT, ActionFail.STATIC);
                return;
            }
            if (itemInstance2.getItemId() != merchantGuard.getItemId()) continue;
            ++n;
        }
        if (n >= merchantGuard.getMax()) {
            player.sendPacket(SystemMsg.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE, ActionFail.STATIC);
            return;
        }
        itemInstance = player.getInventory().removeItemByObjectId(itemInstance.getObjectId(), 1L);
        if (itemInstance == null) {
            player.sendActionFailed();
            return;
        }
        Log.LogItem(player, Log.ItemLog.Drop, itemInstance);
        itemInstance.dropToTheGround((Playable)player, location);
        player.disableDrop(1000);
        player.sendChanges();
        itemInstance.delete();
        castle.getSpawnMerchantTickets().add(itemInstance);
        CastleHiredGuardDAO.getInstance().insert(castle, itemInstance.getItemId(), itemInstance.getLoc());
    }

    @Override
    public boolean pickupItem(Playable playable, ItemInstance itemInstance) {
        if (!playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        if (!player.hasPrivilege(Privilege.CS_FS_MERCENARIES) || player.getClan().getCastle() == 0) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING);
            return false;
        }
        Castle castle = player.getCastle();
        if (!castle.getSpawnMerchantTickets().contains(itemInstance)) {
            player.sendPacket((IStaticPacket)SystemMsg.THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING);
            return false;
        }
        if (((SiegeEvent)castle.getSiegeEvent()).isInProgress()) {
            player.sendPacket(SystemMsg.A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS, ActionFail.STATIC);
            return false;
        }
        castle.getSpawnMerchantTickets().remove(itemInstance);
        CastleHiredGuardDAO.getInstance().delete(castle, itemInstance);
        return true;
    }

    @Override
    public final int[] getItemIds() {
        HashIntSet hashIntSet = new HashIntSet(100);
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : list) {
            hashIntSet.addAll(castle.getMerchantGuards().keySet());
        }
        return hashIntSet.toArray();
    }
}

