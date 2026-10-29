/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import events.SavingSnowman.SavingSnowman;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class ThomasInstance
extends MonsterInstance {
    public ThomasInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        d = 10.0;
        if (creature.getActiveWeaponInstance() != null) {
            switch (creature.getActiveWeaponInstance().getItemId()) {
                case 4202: 
                case 5133: 
                case 5817: 
                case 7058: 
                case 8350: {
                    d = 100.0;
                    break;
                }
                default: {
                    d = 10.0;
                }
            }
        }
        super.reduceCurrentHp(d, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }

    @Override
    protected void onDeath(Creature creature) {
        Creature creature2 = this.getAggroList().getTopDamager();
        if (creature2 == null) {
            creature2 = creature;
        }
        SavingSnowman.freeSnowman(creature2);
        super.onDeath(creature);
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}

