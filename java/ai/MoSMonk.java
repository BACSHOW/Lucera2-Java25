/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class MoSMonk
extends Fighter {
    public MoSMonk(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onIntentionAttack(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (this.getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(20)) {
            Functions.npcSayCustomMessage(this.getActor(), "scripts.ai.MoSMonk.onIntentionAttack", new Object[0]);
        }
        super.onIntentionAttack(creature);
    }

    @Override
    public boolean checkAggression(Creature creature) {
        if (creature.getActiveWeaponInstance() == null) {
            return false;
        }
        return super.checkAggression(creature);
    }
}

