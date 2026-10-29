/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.items.IRefineryHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.ExPutCommissionResultForVariationMake;

public final class RequestRefine
extends L2GameClientPacket {
    private static final int aby = 2130;
    private static final int abz = 2131;
    private static final int abA = 2132;
    private int abB;
    private int abC;
    private int abD;
    private long abE;

    @Override
    protected void readImpl() {
        this.abB = this.readD();
        this.abC = this.readD();
        this.abD = this.readD();
        this.abE = this.readD();
    }

    @Override
    protected void runImpl() {
        if (this.abE <= 0L) {
            return;
        }
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByObjectId(this.abB);
        ItemInstance itemInstance2 = player.getInventory().getItemByObjectId(this.abC);
        ItemInstance itemInstance3 = player.getInventory().getItemByObjectId(this.abD);
        IRefineryHandler iRefineryHandler = player.getRefineryHandler();
        if (itemInstance == null || itemInstance2 == null || itemInstance3 == null || iRefineryHandler == null) {
            player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ExPutCommissionResultForVariationMake.FAIL_PACKET);
            return;
        }
        iRefineryHandler.onRequestRefine(player, itemInstance, itemInstance2, itemInstance3, this.abE);
    }
}

