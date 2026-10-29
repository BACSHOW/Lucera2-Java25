/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import java.util.ArrayList;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SymbolInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EffectSymbol
extends Effect {
    private static final Logger _log = LoggerFactory.getLogger(EffectSymbol.class);
    private NpcInstance aoQ = null;

    public EffectSymbol(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this.getSkill().getTargetType() != Skill.SkillTargetType.TARGET_SELF) {
            _log.error("Symbol skill with target != self, id = " + this.getSkill().getId());
            return false;
        }
        Skill skill = this.getSkill().getFirstAddedSkill();
        if (skill == null) {
            _log.error("Not implemented symbol skill, id = " + this.getSkill().getId());
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        Skill skill = this.getSkill().getFirstAddedSkill();
        skill.setMagicType(this.getSkill().getMagicType());
        Location location = this._effected.getLoc();
        if (this._effected.isPlayer() && ((Player)this._effected).getGroundSkillLoc() != null) {
            location = ((Player)this._effected).getGroundSkillLoc();
            ((Player)this._effected).setGroundSkillLoc(null);
        }
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(this._skill.getSymbolId());
        this.aoQ = this.getTemplate()._count <= 1 ? new SymbolInstance(IdFactory.getInstance().getNextId(), npcTemplate, this._effected, skill) : new NpcInstance(IdFactory.getInstance().getNextId(), npcTemplate);
        this.aoQ.setLevel(this._effected.getLevel());
        this.aoQ.setReflection(this._effected.getReflection());
        this.aoQ.spawnMe(location);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (this.aoQ != null && this.aoQ.isVisible()) {
            this.aoQ.deleteMe();
        }
        this.aoQ = null;
    }

    @Override
    public boolean onActionTime() {
        if (this.getTemplate()._count <= 1) {
            return false;
        }
        Creature creature = this.getEffector();
        Skill skill = this.getSkill().getFirstAddedSkill();
        NpcInstance npcInstance = this.aoQ;
        double d = this.getSkill().getMpConsume();
        if (creature == null || skill == null || npcInstance == null) {
            return false;
        }
        if (d > creature.getCurrentMp()) {
            creature.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
            return false;
        }
        creature.reduceCurrentMp(d, creature);
        for (Creature creature2 : World.getAroundCharacters(npcInstance, this.getSkill().getSkillRadius(), 200)) {
            if (creature2.isDoor() || creature2.getEffectList().getEffectsBySkill(skill) != null || skill.checkTarget(creature, creature2, creature2, false, false) != null || skill.isOffensive() && !GeoEngine.canSeeTarget(npcInstance, creature2, false)) continue;
            ArrayList<Creature> arrayList = new ArrayList<Creature>(1);
            arrayList.add(creature2);
            creature.callSkill(skill, arrayList, true);
            creature.broadcastPacket(new MagicSkillLaunched((Creature)npcInstance, this.getSkill().getDisplayId(), this.getSkill().getDisplayLevel(), creature2));
        }
        return true;
    }
}

