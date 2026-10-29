/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.clanhall.MatchBerserkerInstance;

public class MatchLeaderInstance
extends MatchBerserkerInstance {
    public MatchLeaderInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        d = creature.isPlayer() ? d / (double)this.getMaxHp() / 0.05 * 100.0 : d / (double)this.getMaxHp() / 0.05 * 10.0;
        super.reduceCurrentHp(d, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }
}

