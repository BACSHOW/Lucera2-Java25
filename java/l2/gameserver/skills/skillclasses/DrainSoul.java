/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.StatsSet;

public class DrainSoul
extends Skill {
    public DrainSoul(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (creature2 == null || !creature2.isMonster() || creature2.isDead()) {
            creature.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayer() || creature.isDead()) {
            return;
        }
        for (Creature creature2 : list) {
            if (creature2 == null || creature2.isDead() || !creature2.isMonster()) {
                return;
            }
            MonsterInstance monsterInstance = (MonsterInstance)creature2;
            if (monsterInstance.getTemplate().getAbsorbInfo().isEmpty()) continue;
            monsterInstance.addAbsorber(creature.getPlayer());
        }
    }
}

