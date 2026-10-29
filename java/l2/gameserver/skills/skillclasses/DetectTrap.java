/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.TrapInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.templates.StatsSet;

public class DetectTrap
extends Skill {
    public DetectTrap(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            TrapInstance trapInstance;
            if (creature2 == null || !creature2.isTrap() || !((double)(trapInstance = (TrapInstance)creature2).getLevel() <= this.getPower())) continue;
            trapInstance.setDetected(true);
            for (Player player : World.getAroundPlayers(trapInstance)) {
                player.sendPacket((IStaticPacket)new NpcInfo(trapInstance, (Creature)player));
            }
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

