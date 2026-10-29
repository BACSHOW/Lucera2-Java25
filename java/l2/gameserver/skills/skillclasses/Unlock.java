/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.StatsSet;

public class Unlock
extends Skill {
    private final int apI;

    public Unlock(StatsSet statsSet) {
        super(statsSet);
        this.apI = statsSet.getInteger("unlockPower", 0) + 100;
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (creature2 == null || creature2 instanceof ChestInstance && creature2.isDead()) {
            creature.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (creature2 instanceof ChestInstance && creature.isPlayer()) {
            return super.checkCondition(creature, creature2, bl, bl2, bl3);
        }
        if (!creature2.isDoor() || this.apI == 0) {
            creature.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        DoorInstance doorInstance = (DoorInstance)creature2;
        if (doorInstance.isOpen()) {
            creature.sendPacket((IStaticPacket)Msg.IT_IS_NOT_LOCKED);
            return false;
        }
        if (!doorInstance.isUnlockable()) {
            creature.sendPacket((IStaticPacket)Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }
        if (doorInstance.getKey() > 0) {
            creature.sendPacket((IStaticPacket)Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }
        if (this.apI - doorInstance.getLevel() * 100 < 0) {
            creature.sendPacket((IStaticPacket)Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            Creature creature3;
            if (creature2 == null) continue;
            if (creature2.isDoor()) {
                creature3 = (DoorInstance)creature2;
                if (!((DoorInstance)creature3).isOpen() && (((DoorInstance)creature3).getKey() > 0 || Rnd.chance(this.apI - ((DoorInstance)creature3).getLevel() * 100))) {
                    ((DoorInstance)creature3).openMe((Player)creature, true);
                    continue;
                }
                creature.sendPacket((IStaticPacket)Msg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
                continue;
            }
            if (!(creature2 instanceof ChestInstance) || (creature3 = (ChestInstance)creature2).isDead()) continue;
            ((ChestInstance)creature3).tryOpen((Player)creature, this);
        }
    }
}

