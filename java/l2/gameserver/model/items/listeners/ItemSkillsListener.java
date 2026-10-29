/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.stats.Formulas;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;

public final class ItemSkillsListener
implements OnEquipListener {
    private static final ItemSkillsListener XU = new ItemSkillsListener();

    public static ItemSkillsListener getInstance() {
        return XU;
    }

    @Override
    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        Player player = (Player)playable;
        Skill[] skillArray = null;
        Skill skill = null;
        ItemTemplate itemTemplate = itemInstance.getTemplate();
        skillArray = itemTemplate.getAttachedSkills();
        skill = itemTemplate.getEnchant4Skill();
        player.removeTriggers(itemTemplate);
        if (skillArray != null && skillArray.length > 0) {
            for (Skill skill2 : skillArray) {
                if (skill2.getId() >= 26046 && skill2.getId() <= 26048) {
                    int n2 = player.getSkillLevel(skill2.getId());
                    int n3 = n2 - 1;
                    if (n3 > 0) {
                        player.addSkill(SkillTable.getInstance().getInfo(skill2.getId(), n3), false);
                        continue;
                    }
                    player.removeSkillById(skill2.getId());
                    continue;
                }
                player.removeSkill(skill2, false);
            }
        }
        if (skill != null) {
            player.removeSkill(skill, false);
        }
        if (skillArray.length > 0 || skill != null) {
            player.sendPacket((IStaticPacket)new SkillList(player));
            player.updateStats();
        }
    }

    @Override
    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        Player player = (Player)playable;
        Skill[] skillArray = null;
        Skill skill = null;
        ItemTemplate itemTemplate = itemInstance.getTemplate();
        skillArray = itemTemplate.getAttachedSkills();
        if (itemInstance.getEnchantLevel() >= 4) {
            skill = itemTemplate.getEnchant4Skill();
        }
        if (itemTemplate.getType2() == 0 && player.getGradePenalty() > 0) {
            return;
        }
        player.addTriggers(itemTemplate);
        boolean bl = false;
        if (skillArray.length > 0) {
            for (Skill skill2 : skillArray) {
                if (skill2.getId() >= 26046 && skill2.getId() <= 26048) {
                    int n2;
                    int n3 = n2 = player.getSkillLevel(skill2.getId());
                    if (n2 > 0) {
                        if (SkillTable.getInstance().getInfo(skill2.getId(), n2 + 1) != null) {
                            n3 = n2 + 1;
                        }
                    } else {
                        n3 = 1;
                    }
                    if (n3 == n2) continue;
                    player.addSkill(SkillTable.getInstance().getInfo(skill2.getId(), n3), false);
                    continue;
                }
                if (player.getSkillLevel(skill2.getId()) >= skill2.getLevel()) continue;
                player.addSkill(skill2, false);
                if (!skill2.isActive()) continue;
                long l = itemTemplate.getEquipReuseDelay();
                if (l < 0L) {
                    l = Math.min(Formulas.calcSkillReuseDelay(player, skill2), 30000L);
                }
                if (l <= 0L || player.isSkillDisabled(skill2)) continue;
                player.disableSkill(skill2, l);
                bl = true;
            }
        }
        if (skill != null) {
            player.addSkill(skill, false);
        }
        if (skillArray.length > 0 || skill != null) {
            player.sendPacket((IStaticPacket)new SkillList(player));
            player.updateStats();
            if (bl) {
                player.sendPacket((IStaticPacket)new SkillCoolTime(player));
            }
        }
    }
}

