/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;

public class Transformation
extends Skill {
    public final boolean useSummon;
    public final boolean isDisguise;
    public final String transformationName;

    public Transformation(StatsSet statsSet) {
        super(statsSet);
        this.useSummon = statsSet.getBool("useSummon", false);
        this.isDisguise = statsSet.getBool("isDisguise", false);
        this.transformationName = statsSet.getString("transformationName", null);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        Player player = creature2.getPlayer();
        if (player == null || player.getActiveWeaponFlagAttachment() != null) {
            return false;
        }
        if (player.getTransformation() != 0 && this.getId() != 619) {
            creature.sendPacket((IStaticPacket)Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
            return false;
        }
        if (!(this.getId() != 840 && this.getId() != 841 && this.getId() != 842 || player.getX() <= -166168 && player.getZ() > 0 && player.getZ() < 6000 && player.getPet() == null && player.getReflection() == ReflectionManager.DEFAULT)) {
            creature.sendPacket((IStaticPacket)new SystemMessage(113).addSkillName(this._id, this._level));
            return false;
        }
        if (player.isInFlyingTransform() && this.getId() == 619 && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333) {
            creature.sendPacket((IStaticPacket)new SystemMessage(113).addSkillName(this._id, this._level));
            return false;
        }
        if (player.isInWater()) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
            return false;
        }
        if (player.isRiding() || player.getMountType() == 2) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
            return false;
        }
        if (player.getEffectList().getEffectsBySkillId(1411) != null) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
            return false;
        }
        if (player.isInBoat()) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
            return false;
        }
        if (this.useSummon) {
            if (player.getPet() == null || !player.getPet().isSummon() || player.getPet().isDead()) {
                creature.sendPacket((IStaticPacket)Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                return false;
            }
        } else if (player.getPet() != null && player.getPet().isPet() && this.getId() != 619 && !this.isBaseTransformation()) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITOR_PET);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (this.useSummon) {
            if (creature.getPet() == null || !creature.getPet().isSummon() || creature.getPet().isDead()) {
                creature.sendPacket((IStaticPacket)Msg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                return;
            }
            creature.getPet().unSummon();
        }
        if (this.isSummonerTransformation() && creature.getPet() != null && creature.getPet().isSummon()) {
            creature.getPet().unSummon();
        }
        for (Creature creature2 : list) {
            if (creature2 == null || !creature2.isPlayer()) continue;
            this.getEffects(creature, creature2, false, false);
        }
        if (this.isSSPossible() && (!Config.SAVING_SPS || this._skillType != Skill.SkillType.BUFF)) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

