/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import ai.residences.SiegeGuardFighter;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.PositionUtils;

public class LidiaVonHellmann
extends SiegeGuardFighter {
    private static final Skill DRAIN_SKILL = SkillTable.getInstance().getInfo(4999, 1);
    private static final Skill DAMAGE_SKILL = SkillTable.getInstance().getInfo(4998, 1);

    public LidiaVonHellmann(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        Functions.npcShoutCustomMessage(this.getActor(), "clanhall.LidiaVonHellmann.HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE__HUMPH__THE_BITTER_GRUDGES_OF_THE_DEAD", new Object[0]);
    }

    @Override
    public void onEvtDead(Creature creature) {
        super.onEvtDead(creature);
        Functions.npcShoutCustomMessage(this.getActor(), "clanhall.LidiaVonHellmann.GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED", new Object[0]);
    }

    @Override
    public void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        super.onEvtAttacked(creature, n);
        if (Rnd.chance(0.22)) {
            this.addTaskCast(creature, DRAIN_SKILL);
        } else if (npcInstance.getCurrentHpPercents() < 20.0 && Rnd.chance(0.22)) {
            this.addTaskCast(creature, DRAIN_SKILL);
        }
        if (PositionUtils.calculateDistance(npcInstance, creature, false) > 300.0 && Rnd.chance(0.13)) {
            this.addTaskCast(creature, DAMAGE_SKILL);
        }
    }
}

