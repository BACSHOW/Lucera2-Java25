/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ShotsItemHandler;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;

public class BeastShot
extends ShotsItemHandler {
    private static final int[] iv = new int[]{6645, 6646, 6647};

    private boolean a(Player player, ItemInstance itemInstance, int n) {
        if (Config.ALT_CONSUME_BEAST_SHOTS && (!player.hasBonus() || Config.ALT_PA_CONSUME_BEAST_SHOTS) && !player.getInventory().destroyItem(itemInstance, n)) {
            player.sendPacket((IStaticPacket)Msg.YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PET_SERVITOR);
            return false;
        }
        return true;
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        Summon summon;
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        boolean bl2 = false;
        if (player.getAutoSoulShot().contains(itemInstance.getItemId())) {
            bl2 = true;
        }
        if ((summon = player.getPet()) == null) {
            if (!bl2) {
                player.sendPacket((IStaticPacket)Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            }
            return false;
        }
        if (summon.isDead()) {
            if (!bl2) {
                player.sendPacket((IStaticPacket)Msg.WHEN_PET_OR_SERVITOR_IS_DEAD_SOULSHOTS_OR_SPIRITSHOTS_FOR_PET_OR_SERVITOR_ARE_NOT_AVAILABLE);
            }
            return false;
        }
        int n = 0;
        switch (itemInstance.getItemId()) {
            case 6645: {
                if (summon.getChargedSoulShot() || !this.a(player, itemInstance, summon.getSoulshotConsumeCount())) {
                    return false;
                }
                summon.chargeSoulShot();
                n = 2033;
                break;
            }
            case 6646: {
                if (summon.getChargedSpiritShot() > 0 || !this.a(player, itemInstance, summon.getSpiritshotConsumeCount())) {
                    return false;
                }
                summon.chargeSpiritShot(1);
                n = 2008;
                break;
            }
            case 6647: {
                if (summon.getChargedSpiritShot() > 1 || !this.a(player, itemInstance, summon.getSpiritshotConsumeCount())) {
                    return false;
                }
                summon.chargeSpiritShot(2);
                n = 2009;
            }
        }
        this.broadcastShotSkill(summon, n, 1);
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}

