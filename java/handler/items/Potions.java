/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.SimpleItemHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;

public class Potions
extends SimpleItemHandler {
    private static final int[] lS = new int[]{7906, 7907, 7908, 7909, 7910, 7911};

    @Override
    public int[] getItemIds() {
        return lS;
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
        int n = itemInstance.getItemId();
        if (player.isOlyParticipant()) {
            player.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(n));
            return false;
        }
        if (!Potions.useItem(player, itemInstance, 1L)) {
            return false;
        }
        switch (n) {
            case 7906: {
                player.broadcastPacket(new MagicSkillUse(player, player, 2248, 1, 0, 0L));
                player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2248, 1));
                break;
            }
            case 7907: {
                player.broadcastPacket(new MagicSkillUse(player, player, 2249, 1, 0, 0L));
                player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2249, 1));
                break;
            }
            case 7908: {
                player.broadcastPacket(new MagicSkillUse(player, player, 2250, 1, 0, 0L));
                player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2250, 1));
                break;
            }
            case 7909: {
                player.broadcastPacket(new MagicSkillUse(player, player, 2251, 1, 0, 0L));
                player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2251, 1));
                break;
            }
            case 7910: {
                player.broadcastPacket(new MagicSkillUse(player, player, 2252, 1, 0, 0L));
                player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2252, 1));
                break;
            }
            case 7911: {
                player.broadcastPacket(new MagicSkillUse(player, player, 2253, 1, 0, 0L));
                player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(2253, 1));
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
}

