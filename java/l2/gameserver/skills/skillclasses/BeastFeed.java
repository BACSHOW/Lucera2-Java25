/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.FeedableBeastInstance;
import l2.gameserver.templates.StatsSet;

public class BeastFeed
extends Skill {
    public BeastFeed(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (!(creature2 instanceof FeedableBeastInstance)) continue;
            ((FeedableBeastInstance)creature2).onSkillUse((Player)creature, this._id);
        }
    }
}

