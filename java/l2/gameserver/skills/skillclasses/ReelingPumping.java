/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Fishing;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.WeaponTemplate;

public class ReelingPumping
extends Skill {
    public ReelingPumping(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (!((Player)creature).isFishing()) {
            creature.sendPacket((IStaticPacket)(this.getSkillType() == Skill.SkillType.PUMPING ? Msg.PUMPING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING : Msg.REELING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING));
            creature.sendActionFailed();
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (creature == null || !creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        Fishing fishing = player.getFishing();
        if (fishing == null || !fishing.isInCombat()) {
            return;
        }
        WeaponTemplate weaponTemplate = player.getActiveWeaponItem();
        int n = player.getChargedFishShot() ? 2 : 1;
        int n2 = 0;
        double d = 1.0 + (double)weaponTemplate.getCrystalType().ordinal() * 0.1;
        int n3 = (int)(this.getPower() * d * (double)n);
        if (player.getSkillLevel(1315) < this.getLevel() - 2) {
            int n4;
            player.sendPacket((IStaticPacket)Msg.SINCE_THE_SKILL_LEVEL_OF_REELING_PUMPING_IS_HIGHER_THAN_THE_LEVEL_OF_YOUR_FISHING_MASTERY_A_PENALTY_OF_S1_WILL_BE_APPLIED);
            n2 = 50;
            n3 = n4 = n3 - n2;
        }
        if (n == 2) {
            player.unChargeFishShot();
        }
        fishing.useFishingSkill(n3, n2, this.getSkillType());
    }
}

