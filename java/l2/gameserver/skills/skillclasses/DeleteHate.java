/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.StatsSet;

public class DeleteHate
extends Skill {
    public DeleteHate(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isRaid()) continue;
            if (this.getActivateRate() > 0) {
                if (creature.isPlayer() && ((Player)creature).isGM()) {
                    creature.sendMessage(new CustomMessage("l2p.gameserver.skills.Formulas.Chance", (Player)creature, new Object[0]).addString(this.getName()).addNumber(this.getActivateRate()));
                }
                if (!Rnd.chance(this.getActivateRate())) {
                    return;
                }
            }
            if (creature2.isNpc()) {
                NpcInstance npcInstance = (NpcInstance)creature2;
                npcInstance.getAggroList().clear(false);
                if (npcInstance.hasAI()) {
                    npcInstance.getAI().getTargetList().clear();
                }
                npcInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
            this.getEffects(creature, creature2, false, false);
        }
    }
}

