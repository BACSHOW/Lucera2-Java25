/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;
import l2.gameserver.templates.StatsSet;

public class Craft
extends Skill {
    private final boolean apd;

    public Craft(StatsSet statsSet) {
        super(statsSet);
        this.apd = statsSet.getBool("isDwarven");
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        Player player = (Player)creature;
        if (player.isInStoreMode() || player.isProcessingRequest() || player.isInDuel()) {
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        creature.sendPacket((IStaticPacket)new RecipeBookItemList((Player)creature, this.apd));
    }
}

