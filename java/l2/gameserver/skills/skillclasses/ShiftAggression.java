/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;

public class ShiftAggression
extends Skill {
    public ShiftAggression(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (creature.getPlayer() == null) {
            return;
        }
        for (Creature creature2 : list) {
            if (creature2 == null || !creature2.isPlayer()) continue;
            Player player = (Player)creature2;
            for (NpcInstance npcInstance : World.getAroundNpc(creature, this.getSkillRadius(), this.getSkillRadius())) {
                AggroList.AggroInfo aggroInfo = npcInstance.getAggroList().get(creature);
                if (aggroInfo == null) continue;
                npcInstance.getAggroList().addDamageHate(player, 0, aggroInfo.hate);
                npcInstance.getAggroList().remove(creature, true);
            }
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

