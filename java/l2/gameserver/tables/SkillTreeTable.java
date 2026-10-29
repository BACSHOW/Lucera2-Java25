/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.tables;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.EnchantSkillLearn;
import l2.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillTreeTable {
    public static final int NORMAL_ENCHANT_COST_MULTIPLIER = 1;
    public static final int NORMAL_ENCHANT_BOOK = 6622;
    private static final Logger _log = LoggerFactory.getLogger(SkillTreeTable.class);
    private static SkillTreeTable aqV;
    public static Map<Integer, List<EnchantSkillLearn>> _enchant;

    public static SkillTreeTable getInstance() {
        if (aqV == null) {
            aqV = new SkillTreeTable();
        }
        return aqV;
    }

    private SkillTreeTable() {
        _log.info("SkillTreeTable: Loaded " + _enchant.size() + " enchanted skills.");
    }

    public static void checkSkill(Player player, Skill skill) {
        SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, player.getClassId(), skill.getId(), SkillTreeTable.e(skill), AcquireType.NORMAL);
        if (skillLearn == null) {
            return;
        }
        if (skillLearn.getMinLevel() >= player.getLevel() + Config.ALT_REMOVE_SKILLS_ON_DELEVEL) {
            player.removeSkill(skill, true);
            for (int i = skill.getBaseLevel(); i != 0; --i) {
                Skill skill2;
                SkillLearn skillLearn2 = SkillAcquireHolder.getInstance().getSkillLearn(player, player.getClassId(), skill.getId(), i, AcquireType.NORMAL);
                if (skillLearn2 == null || skillLearn2.getMinLevel() > player.getLevel() + Config.ALT_REMOVE_SKILLS_ON_DELEVEL || (skill2 = SkillTable.getInstance().getInfo(skill.getId(), i)) == null) continue;
                player.addSkill(skill2, true);
                break;
            }
        }
    }

    private static int e(Skill skill) {
        return skill.getDisplayLevel() > 100 ? skill.getBaseLevel() : skill.getLevel();
    }

    public static int isEnchantable(Skill skill) {
        List<EnchantSkillLearn> list = _enchant.get(skill.getId());
        if (list == null) {
            return 0;
        }
        for (EnchantSkillLearn enchantSkillLearn : list) {
            if (enchantSkillLearn.getBaseLevel() > skill.getLevel()) continue;
            return 1;
        }
        return 0;
    }

    public static void unload() {
        if (aqV != null) {
            aqV = null;
        }
        _enchant.clear();
    }

    static {
        _enchant = new ConcurrentHashMap<Integer, List<EnchantSkillLearn>>();
    }
}

