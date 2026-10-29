/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.TamedBeastInstance;
import l2.gameserver.templates.StatsSet;

public class TameControl
extends Skill {
    private final int _type;

    public TameControl(StatsSet statsSet) {
        super(statsSet);
        this._type = statsSet.getInteger("type", 0);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        TamedBeastInstance tamedBeastInstance;
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
        if (!creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        if (player.getTrainedBeast() == null) {
            return;
        }
        if (this._type == 0) {
            for (Creature creature2 : list) {
                if (creature2 == null || !(creature2 instanceof TamedBeastInstance) || player.getTrainedBeast() != creature2) continue;
                player.getTrainedBeast().despawnWithDelay(1000);
            }
        } else if (this._type > 0 && (tamedBeastInstance = player.getTrainedBeast()) != null) {
            switch (this._type) {
                case 1: {
                    tamedBeastInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
                    break;
                }
                case 3: {
                    tamedBeastInstance.buffOwner();
                    break;
                }
                case 4: {
                    tamedBeastInstance.doDespawn();
                }
            }
        }
    }
}

