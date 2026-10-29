/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.templates.StatsSet;

public class NegateStats
extends Skill {
    private final List<Stats> apk;
    private final boolean apl;
    private final int aoC;

    public NegateStats(StatsSet statsSet) {
        super(statsSet);
        String[] stringArray = statsSet.getString("negateStats", "").split(" ");
        this.apk = new ArrayList<Stats>(stringArray.length);
        for (String string : stringArray) {
            if (string.isEmpty()) continue;
            this.apk.add(Stats.valueOfXml(string));
        }
        this.apl = statsSet.getBool("negateDebuffs", false);
        this.aoC = statsSet.getInteger("negateCount", 0);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null) continue;
            if (!this.apl && !Formulas.calcSkillSuccess(creature, creature2, this, this.getActivateRate())) {
                creature.sendPacket((IStaticPacket)new SystemMessage(139).addString(creature2.getName()).addSkillName(this.getId(), this.getLevel()));
                continue;
            }
            int n = 0;
            List<Effect> list2 = creature2.getEffectList().getAllEffects();
            block1: for (Stats stats : this.apk) {
                for (Effect effect : list2) {
                    Skill skill = effect.getSkill();
                    if (!skill.isOffensive() && skill.getMagicLevel() > this.getMagicLevel() && Rnd.chance(skill.getMagicLevel() - this.getMagicLevel())) {
                        ++n;
                        continue;
                    }
                    if (skill.isOffensive() == this.apl && this.a(effect, stats) && skill.isCancelable()) {
                        creature2.sendPacket((IStaticPacket)new SystemMessage(749).addSkillName(effect.getSkill().getId(), effect.getSkill().getDisplayLevel()));
                        effect.exit();
                        ++n;
                    }
                    if (this.aoC <= 0 || n < this.aoC) continue;
                    continue block1;
                }
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }

    private boolean a(Effect effect, Stats stats) {
        for (FuncTemplate funcTemplate : effect.getTemplate().getAttachedFuncs()) {
            if (funcTemplate._stat != stats) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isOffensive() {
        return !this.apl;
    }

    public List<Stats> getNegateStats() {
        return this.apk;
    }
}

