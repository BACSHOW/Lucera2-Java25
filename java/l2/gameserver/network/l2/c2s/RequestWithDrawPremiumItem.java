/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.PremiumItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExGetPremiumItemList;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public final class RequestWithDrawPremiumItem
extends L2GameClientPacket {
    private int acY;
    private int CT;
    private long acZ;

    @Override
    protected void readImpl() {
        this.acY = this.readD();
        this.CT = this.readD();
        this.acZ = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this.acZ <= 0L) {
            return;
        }
        if (player.getObjectId() != this.CT) {
            return;
        }
        if (player.getPremiumItemList().isEmpty()) {
            return;
        }
        if (player.getWeightPenalty() >= 3 || (double)player.getInventoryLimit() * 0.8 <= (double)player.getInventory().getSize()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT);
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
            return;
        }
        PremiumItem premiumItem = player.getPremiumItemList().get(this.acY);
        if (premiumItem == null) {
            return;
        }
        boolean bl = ItemHolder.getInstance().getTemplate(premiumItem.getItemId()).isStackable();
        if (premiumItem.getCount() < this.acZ) {
            return;
        }
        if (!bl) {
            int n = 0;
            while ((long)n < this.acZ) {
                this.b(player, premiumItem.getItemId(), 1L);
                ++n;
            }
        } else {
            this.b(player, premiumItem.getItemId(), this.acZ);
        }
        if (this.acZ < premiumItem.getCount()) {
            player.getPremiumItemList().get(this.acY).updateCount(premiumItem.getCount() - this.acZ);
            player.updatePremiumItem(this.acY, premiumItem.getCount() - this.acZ);
        } else {
            player.getPremiumItemList().remove(this.acY);
            player.deletePremiumItem(this.acY);
        }
        if (player.getPremiumItemList().isEmpty()) {
            player.sendPacket((IStaticPacket)Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
        } else {
            player.sendPacket((IStaticPacket)new ExGetPremiumItemList(player));
        }
    }

    private void b(Player player, int n, long l) {
        player.getInventory().addItem(n, l);
        player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(n, l, 0));
    }
}

