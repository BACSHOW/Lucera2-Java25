/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.text.PrintfFormat;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;
import npc.model.OrfenInstance;

public class Orfen
extends Fighter {
    public static final PrintfFormat[] MsgOnRecall = new PrintfFormat[]{new PrintfFormat("%s. Stop kidding yourself about your own powerlessness!"), new PrintfFormat("%s. I'll make you feel what true fear is!"), new PrintfFormat("You're really stupid to have challenged me. %s! Get ready!"), new PrintfFormat("%s. Do you think that's going to work?!")};
    public final Skill[] _paralyze = this.getActor().getTemplate().getDebuffSkills();

    public Orfen(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        if (super.thinkActive()) {
            return true;
        }
        OrfenInstance orfenInstance = this.getActor();
        if (orfenInstance.isTeleported() && orfenInstance.getCurrentHpPercents() > 95.0) {
            orfenInstance.setTeleported(false);
            return true;
        }
        return false;
    }

    @Override
    protected boolean createNewTask() {
        return this.defaultNewTask();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        Skill skill;
        super.onEvtAttacked(creature, n);
        OrfenInstance orfenInstance = this.getActor();
        if (orfenInstance.isCastingNow()) {
            return;
        }
        double d = orfenInstance.getDistance(creature);
        if (Config.ORFEN_USE_TELEPORT && d > 300.0 && d < 1000.0 && this._damSkills.length > 0 && Rnd.chance(10)) {
            Functions.npcSay(orfenInstance, MsgOnRecall[Rnd.get(MsgOnRecall.length - 1)].sprintf(creature.getName()));
            this.a(creature, Location.findFrontPosition(orfenInstance, creature, 0, 50));
            Skill skill2 = this._damSkills[Rnd.get(this._damSkills.length)];
            if (this.canUseSkill(skill2, creature, -1.0)) {
                this.addTaskAttack(creature, skill2, 1000000);
            }
        } else if (this._paralyze.length > 0 && Rnd.chance(20) && this.canUseSkill(skill = this._paralyze[Rnd.get(this._paralyze.length)], creature, -1.0)) {
            this.addTaskAttack(creature, skill, 1000000);
        }
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature creature) {
        super.onEvtSeeSpell(skill, creature);
        OrfenInstance orfenInstance = this.getActor();
        if (orfenInstance.isCastingNow()) {
            return;
        }
        double d = orfenInstance.getDistance(creature);
        if (Config.ORFEN_USE_TELEPORT && this._damSkills.length > 0 && skill.getEffectPoint() > 0 && d < 1000.0 && Rnd.chance(20)) {
            Functions.npcSay(orfenInstance, MsgOnRecall[Rnd.get(MsgOnRecall.length)].sprintf(creature.getName()));
            this.a(creature, Location.findFrontPosition(orfenInstance, creature, 0, 50));
            Skill skill2 = this._damSkills[Rnd.get(this._damSkills.length)];
            if (this.canUseSkill(skill2, creature, -1.0)) {
                this.addTaskAttack(creature, skill2, 1000000);
            }
        }
    }

    @Override
    public OrfenInstance getActor() {
        return (OrfenInstance)super.getActor();
    }

    private void a(Creature creature, Location location) {
        creature.teleToLocation(location);
    }
}

