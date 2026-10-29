/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.instances.BossInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.templates.npc.NpcTemplate;

public class NoblesRewardBossInstance
extends BossInstance {
    public NoblesRewardBossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDeath(Creature creature) {
        super.onDeath(creature);
        if (creature != null && creature.isPlayable()) {
            Player player = creature.getPlayer();
            if (player.isDead() || player.getParty() == null) {
                return;
            }
            Party party = player.getParty();
            for (Player player2 : party) {
                if (player2 == null || player2.isDead() || player2.isNoble() || !player2.isSubClassActive() || player2.getLevel() < 76 || this.getDistance3D(player2) > (double)Config.ALT_PARTY_DISTRIBUTION_RANGE) continue;
                NoblesController.getInstance().addNoble(player2.getPlayer());
                player2.setNoble(true);
                player2.updatePledgeClass();
                player2.updateNobleSkills();
                player2.getPlayer().sendPacket((IStaticPacket)new SkillList(player2.getPlayer()));
                player2.getPlayer().broadcastUserInfo(true);
            }
        }
    }
}

