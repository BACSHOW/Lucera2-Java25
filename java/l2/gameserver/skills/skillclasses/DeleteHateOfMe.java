/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

public class DeleteHateOfMe
extends Skill {
    public DeleteHateOfMe(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null) continue;
            if (creature.isPlayer() && ((Player)creature).isGM()) {
                creature.sendMessage(new CustomMessage("l2p.gameserver.skills.Formulas.Chance", (Player)creature, new Object[0]).addString(this.getName()).addNumber(this.getActivateRate()));
            }
            if (creature2.isNpc() && Formulas.calcSkillSuccess(creature, creature2, this, this.getActivateRate())) {
                NpcInstance npcInstance = (NpcInstance)creature2;
                npcInstance.getAggroList().remove(creature, true);
                npcInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
            this.getEffects(creature, creature2, true, false);
        }
    }
}

