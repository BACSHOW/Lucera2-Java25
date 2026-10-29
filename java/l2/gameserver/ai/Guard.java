/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Guard
extends Fighter {
    public Guard(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean canAttackCharacter(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (this.getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            AggroList.AggroInfo aggroInfo = npcInstance.getAggroList().get(creature);
            return aggroInfo != null && aggroInfo.hate > 0;
        }
        return creature.isMonster() || creature.isPlayable();
    }

    @Override
    public boolean checkAggression(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (this.getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !this.isGlobalAggro()) {
            return false;
        }
        if (creature.isPlayable() && (creature.getKarma() == 0 || npcInstance.getParameter("evilGuard", false) && creature.getPvpFlag() > 0)) {
            return false;
        }
        if (creature.isMonster()) {
            return false;
        }
        return super.checkAggression(creature);
    }

    @Override
    public int getMaxAttackTimeout() {
        return 0;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

