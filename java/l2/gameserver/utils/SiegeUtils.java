/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.tables.SkillTable;

public class SiegeUtils {
    public static void addSiegeSkills(Player player) {
        for (SkillLearn skillLearn : SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.CLAN_LEADER)) {
            Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill == null || (skill.getId() == 326 || skill.getId() == 327) && !player.isNoble() || player.getSkillLevel(skill.getId()) >= skill.getLevel()) continue;
            player.addSkill(skill, false);
        }
    }

    public static void removeSiegeSkills(Player player) {
        for (SkillLearn skillLearn : SkillAcquireHolder.getInstance().getAllSkillLearn(player, player.getClassId(), AcquireType.CLAN_LEADER)) {
            Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill == null) continue;
            player.removeSkill(skill, false);
        }
    }
}

