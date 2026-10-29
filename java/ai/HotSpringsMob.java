/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;

public class HotSpringsMob
extends Mystic {
    public HotSpringsMob(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature != null && Rnd.chance(Config.ALT_HOT_SPIRIT_CHANCE_DEBUFF)) {
            int n2 = 0;
            switch (npcInstance.getNpcId()) {
                case 21317: 
                case 21322: {
                    n2 = Rnd.nextBoolean() ? 4553 : 4554;
                    break;
                }
                case 21316: 
                case 21319: {
                    n2 = Rnd.nextBoolean() ? 4552 : 4554;
                    break;
                }
                case 21314: 
                case 21321: {
                    n2 = Rnd.nextBoolean() ? 4551 : 4554;
                }
            }
            List<Effect> list = creature.getEffectList().getEffectsBySkillId(n2);
            if (list != null) {
                int n3 = list.get(0).getSkill().getLevel();
                if (n3 < 10) {
                    list.get(0).exit();
                    Skill skill = SkillTable.getInstance().getInfo(n2, n3 + 1);
                    skill.getEffects(npcInstance, creature, false, false);
                }
            } else {
                Skill skill = SkillTable.getInstance().getInfo(n2, 1);
                if (skill != null) {
                    skill.getEffects(npcInstance, creature, false, false);
                } else {
                    System.out.println("Skill " + n2 + " is null, fix it.");
                }
            }
        }
        super.onEvtAttacked(creature, n);
    }
}

