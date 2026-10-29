/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.StatsSet;

public class DefuseTrap
extends Skill {
    public DefuseTrap(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (creature2 == null || !creature2.isTrap()) {
            creature.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            TrapInstance trapInstance;
            if (creature2 == null || !creature2.isTrap() || !((double)(trapInstance = (TrapInstance)creature2).getLevel() <= this.getPower())) continue;
            trapInstance.deleteMe();
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

