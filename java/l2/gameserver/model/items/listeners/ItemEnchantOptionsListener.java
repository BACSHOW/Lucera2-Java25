/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.listeners;

import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.templates.OptionDataTemplate;

public final class ItemEnchantOptionsListener
implements OnEquipListener {
    private static final ItemEnchantOptionsListener XT = new ItemEnchantOptionsListener();

    public static ItemEnchantOptionsListener getInstance() {
        return XT;
    }

    @Override
    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable()) {
            return;
        }
        Player player = playable.getPlayer();
        boolean bl = false;
        for (int n2 : itemInstance.getEnchantOptions()) {
            OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(n2);
            if (optionDataTemplate == null) continue;
            player.addStatFuncs(optionDataTemplate.getStatFuncs(optionDataTemplate));
            for (Skill skill : optionDataTemplate.getSkills()) {
                player.addSkill(skill, false);
                bl = true;
            }
            for (TriggerInfo triggerInfo : optionDataTemplate.getTriggerList()) {
                player.addTrigger(triggerInfo);
            }
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
        player.sendChanges();
    }

    @Override
    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable()) {
            return;
        }
        Player player = playable.getPlayer();
        boolean bl = false;
        for (int n2 : itemInstance.getEnchantOptions()) {
            OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(n2);
            if (optionDataTemplate == null) continue;
            player.removeStatsOwner(optionDataTemplate);
            for (Skill skill : optionDataTemplate.getSkills()) {
                player.removeSkill(skill, false);
                bl = true;
            }
            for (TriggerInfo triggerInfo : optionDataTemplate.getTriggerList()) {
                player.removeTrigger(triggerInfo);
            }
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
        player.sendChanges();
    }
}

