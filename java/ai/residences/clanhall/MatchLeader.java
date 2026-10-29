/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import ai.residences.clanhall.MatchFighter;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;

public class MatchLeader
extends MatchFighter {
    public static final Skill ATTACK_SKILL = SkillTable.getInstance().getInfo(4077, 6);

    public MatchLeader(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public void onEvtAttacked(Creature creature, int n) {
        super.onEvtAttacked(creature, n);
        if (Rnd.chance(10)) {
            this.addTaskCast(creature, ATTACK_SKILL);
        }
    }
}

