/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import ai.residences.clanhall.MatchTrief;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class MatchTriefInstance
extends CTBBossInstance {
    private long ayJ;

    public MatchTriefInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        if (this.ayJ > System.currentTimeMillis()) {
            d = 10000.0;
            if (Rnd.chance(10)) {
                ((MatchTrief)this.getAI()).hold();
            }
        } else if (this.getCurrentHpPercents() > 50.0) {
            d = creature.isPlayer() ? d / (double)this.getMaxHp() / 0.05 * 100.0 : d / (double)this.getMaxHp() / 0.05 * 10.0;
        } else if (this.getCurrentHpPercents() > 30.0) {
            if (Rnd.chance(90)) {
                d = creature.isPlayer() ? d / (double)this.getMaxHp() / 0.05 * 100.0 : d / (double)this.getMaxHp() / 0.05 * 10.0;
            } else {
                this.ayJ = System.currentTimeMillis() + 5000L;
            }
        } else {
            this.ayJ = System.currentTimeMillis() + 5000L;
        }
        super.reduceCurrentHp(d, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }
}

