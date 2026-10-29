/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.WarehouseInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class ArenaManagerInstance
extends WarehouseInstance {
    private static final int axt = 4380;

    public ArenaManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ArenaManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (!player.isInPeaceZone() || player.isCursedWeaponEquipped()) {
            return;
        }
        if (string.startsWith("CPRecovery")) {
            if (Functions.getItemCount(player, 57) >= 100L) {
                Functions.removeItem(player, 57, 100L);
                this.doCast(SkillTable.getInstance().getInfo(4380, 1), player, true);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}

