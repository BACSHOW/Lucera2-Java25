/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Dice;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RollingDice
extends ScriptItemHandler {
    private static final int[] iv = new int[]{4625, 4626, 4627, 4628};

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        int n = itemInstance.getItemId();
        if (player.isOlyParticipant()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
            return false;
        }
        if (player.isSitting()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            return false;
        }
        int n2 = Rnd.get(1, 6);
        if (n2 == 0) {
            player.sendPacket((IStaticPacket)Msg.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIMETRY_AGAIN_LATER);
            return false;
        }
        player.broadcastPacket(new Dice(player.getObjectId(), n, n2, player.getX() - 30, player.getY() - 30, player.getZ()), new SystemMessage(834).addString(player.getName()).addNumber(n2));
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}

