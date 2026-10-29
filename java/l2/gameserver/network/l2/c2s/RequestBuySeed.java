/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.ManorManagerInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.SeedProduction;
import l2.gameserver.utils.Log;

public class RequestBuySeed
extends L2GameClientPacket {
    private int _count;
    private int Gh;
    private int[] abp;
    private long[] abq;

    @Override
    protected void readImpl() {
        this.Gh = this.readD();
        this._count = this.readD();
        if (this._count * 8 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.abq = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.abq[i] = this.readD();
            if (this.abq[i] >= 1L) continue;
            this._count = 0;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        long l;
        int n;
        int n2;
        ManorManagerInstance manorManagerInstance;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isInTrade()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && !player.isGM()) {
            player.sendActionFailed();
            return;
        }
        GameObject gameObject = player.getTarget();
        ManorManagerInstance manorManagerInstance2 = manorManagerInstance = gameObject != null && gameObject instanceof ManorManagerInstance ? (ManorManagerInstance)gameObject : null;
        if (!(player.isGM() || manorManagerInstance != null && manorManagerInstance.isInActingRange(player))) {
            player.sendActionFailed();
            return;
        }
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this.Gh);
        if (castle == null) {
            return;
        }
        long l2 = 0L;
        int n3 = 0;
        long l3 = 0L;
        try {
            for (n2 = 0; n2 < this._count; ++n2) {
                n = this.abp[n2];
                l = this.abq[n2];
                long l4 = 0L;
                long l5 = 0L;
                SeedProduction seedProduction = castle.getSeed(n, 0);
                l4 = seedProduction.getPrice();
                l5 = seedProduction.getCanProduce();
                if (l4 < 1L) {
                    return;
                }
                if (l5 < l) {
                    return;
                }
                l2 = SafeMath.addAndCheck(l2, SafeMath.mulAndCheck(l, l4));
                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
                if (itemTemplate == null) {
                    return;
                }
                l3 = SafeMath.addAndCheck(l3, SafeMath.mulAndCheck(l, (long)itemTemplate.getWeight()));
                if (itemTemplate.isStackable() && player.getInventory().getItemByItemId(n) != null) continue;
                ++n3;
            }
        }
        catch (ArithmeticException arithmeticException) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        player.getInventory().writeLock();
        try {
            if (!player.getInventory().validateWeight(l3)) {
                this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!player.getInventory().validateCapacity(n3)) {
                this.sendPacket((L2GameServerPacket)Msg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            if (!player.reduceAdena(l2, true)) {
                this.sendPacket((L2GameServerPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            castle.addToTreasuryNoTax(l2, false, true);
            for (n2 = 0; n2 < this._count; ++n2) {
                n = this.abp[n2];
                l = this.abq[n2];
                SeedProduction seedProduction = castle.getSeed(n, 0);
                seedProduction.setCanProduce(seedProduction.getCanProduce() - l);
                castle.updateSeed(seedProduction.getId(), seedProduction.getCanProduce(), 0);
                player.getInventory().addItem(n, l);
                player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(n, l, 0));
                Log.LogItem(player, Log.ItemLog.CastleManorSeedBuy, n, l);
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.sendChanges();
    }
}

