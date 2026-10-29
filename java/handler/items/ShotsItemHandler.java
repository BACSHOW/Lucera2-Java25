/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import java.util.List;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.templates.item.ItemTemplate;

public abstract class ShotsItemHandler
extends ScriptItemHandler {
    protected void broadcastShotSkill(Creature creature, int n, int n2) {
        MagicSkillUse magicSkillUse = new MagicSkillUse(creature, creature, n, n2, 0, 0L);
        creature.sendPacket((IStaticPacket)magicSkillUse);
        if (!creature.isVisible()) {
            return;
        }
        List<Player> list = World.getAroundPlayers(creature);
        for (int i = 0; i < list.size(); ++i) {
            Player player = list.get(i);
            if (player == null || player.isNoShotsAnim()) continue;
            player.sendPacket((IStaticPacket)magicSkillUse);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        int[] nArray = this.getItemIds();
        int n = nArray.length;
        for (int i = 0; i < n; ++i) {
            Integer n2 = nArray[i];
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n2);
            if (itemTemplate == null) continue;
            itemTemplate.setIsShotItem(true);
        }
    }
}

