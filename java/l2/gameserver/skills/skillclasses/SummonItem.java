/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;

public class SummonItem
extends Skill {
    private final int _itemId;
    private final int apB;
    private final int apC;
    private final long apD;
    private final long apE;

    public SummonItem(StatsSet statsSet) {
        super(statsSet);
        this._itemId = statsSet.getInteger("SummonItemId", 0);
        this.apB = statsSet.getInteger("SummonMinId", 0);
        this.apC = statsSet.getInteger("SummonMaxId", this.apB);
        this.apD = statsSet.getLong("SummonMinCount");
        this.apE = statsSet.getLong("SummonMaxCount", this.apD);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayable()) {
            return;
        }
        for (Creature creature2 : list) {
            if (creature2 == null) continue;
            int n = this.apB > 0 ? Rnd.get(this.apB, this.apC) : this._itemId;
            long l = Rnd.get(this.apD, this.apE);
            ItemFunctions.addItem((Playable)creature, n, l, true);
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
        }
    }
}

